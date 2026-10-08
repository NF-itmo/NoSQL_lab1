package org.controllers;

import jakarta.validation.Valid;
import org.controllers.DTO.GetGenreFilterSettingsResponse;
import org.controllers.DTO.UpdateGenreFilterSettingsRequest;
import org.services.SettingsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/settings")
@PreAuthorize("isAuthenticated()")
public class SettingsController {
    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("/genre-filter")
    public GetGenreFilterSettingsResponse getGenreFilterSettings(Authentication authentication) {
        final var settings = settingsService.getGenreFilterSettings(authentication.getName());

        return new GetGenreFilterSettingsResponse(settings.getGenre());
    }

    @PutMapping("/genre-filter")
    public GetGenreFilterSettingsResponse updateGenreFilterSettings(
            @RequestBody @Valid final UpdateGenreFilterSettingsRequest request,
            Authentication authentication
    ) {
        final var settings = settingsService.updateGenreFilterSettings(
                authentication.getName(),
                request.getGenre()
        );

        return new GetGenreFilterSettingsResponse(settings.getGenre());
    }
}
