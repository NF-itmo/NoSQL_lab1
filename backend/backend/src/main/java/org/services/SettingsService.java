package org.services;

import org.models.GenreFilterSettings;
import org.models.UserSettings;
import org.repository.SettingsCacheRepository;
import org.repository.UserSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    private final UserSettingsRepository userSettingsRepository;
    private final SettingsCacheRepository settingsCacheRepository;

    public SettingsService(
            UserSettingsRepository userSettingsRepository,
            SettingsCacheRepository settingsCacheRepository
    ) {
        this.userSettingsRepository = userSettingsRepository;
        this.settingsCacheRepository = settingsCacheRepository;
    }

    public GenreFilterSettings getGenreFilterSettings(String username) {
        final var cachedSettings = settingsCacheRepository.getByUsername(username).join();

        if (cachedSettings.isPresent()) {
            return cachedSettings.get();
        }

        final GenreFilterSettings settings = userSettingsRepository.getByUsername(username)
                .map(value -> new GenreFilterSettings(value.getGenre()))
                .orElseGet(() -> new GenreFilterSettings(null));

        if (settings.getGenre() != null) {
            settingsCacheRepository.put(username, settings).join();
        }

        return settings;
    }

    public GenreFilterSettings updateGenreFilterSettings(String username, String genre) {
        final String normalizedGenre = genre == null ? "" : genre;

        userSettingsRepository.save(UserSettings.builder()
                .username(username)
                .genre(normalizedGenre)
                .build());

        final GenreFilterSettings settings = new GenreFilterSettings(normalizedGenre);
        settingsCacheRepository.put(username, settings).join();

        return settings;
    }
}
