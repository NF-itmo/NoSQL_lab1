package org.services;

import org.models.TitleFilterSettings;
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

    public TitleFilterSettings getTitleFilterSettings(String username) {
        final var cachedSettings = settingsCacheRepository.getByUsername(username).join();

        if (cachedSettings.isPresent()) {
            return cachedSettings.get();
        }

        final TitleFilterSettings settings = userSettingsRepository.getByUsername(username)
                .map(value -> new TitleFilterSettings(value.getTitleFilter()))
                .orElseGet(() -> new TitleFilterSettings(null));

        if (settings.getTitle() != null) {
            settingsCacheRepository.put(username, settings).join();
        }

        return settings;
    }

    public TitleFilterSettings updateTitleFilterSettings(String username, String title) {
        final String normalizedTitle = title == null ? "" : title;

        userSettingsRepository.save(UserSettings.builder()
                .username(username)
                .titleFilter(normalizedTitle)
                .build());

        final TitleFilterSettings settings = new TitleFilterSettings(normalizedTitle);
        settingsCacheRepository.put(username, settings).join();

        return settings;
    }
}
