package org.controllers;

import jakarta.validation.Valid;
import org.controllers.DTO.GetTitleFilterSettingsResponse;
import org.controllers.DTO.UpdateTitleFilterSettingsRequest;
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

    @GetMapping("/title-filter")
    public GetTitleFilterSettingsResponse getTitleFilterSettings(Authentication authentication) {
        final var settings = settingsService.getTitleFilterSettings(authentication.getName());

        return new GetTitleFilterSettingsResponse(settings.getTitle());
    }

    @PutMapping("/title-filter")
    public GetTitleFilterSettingsResponse updateTitleFilterSettings(
            @RequestBody @Valid final UpdateTitleFilterSettingsRequest request,
            Authentication authentication
    ) {
        final var settings = settingsService.updateTitleFilterSettings(
                authentication.getName(),
                request.getTitle()
        );

        return new GetTitleFilterSettingsResponse(settings.getTitle());
    }
}
