package org.controllers;

import jakarta.validation.Valid;
import org.controllers.DTO.GetGenreFilterSettingsResponse;
import org.controllers.DTO.UpdateGenreFilterSettingsRequest;
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

    @GetMapping("/genre-filter")
    public GetGenreFilterSettingsResponse getGenreFilterSettings() {
        throw new RuntimeException("Not implemented");
    }

    @PutMapping("/genre-filter")
    public GetGenreFilterSettingsResponse updateGenreFilterSettings(
            @RequestBody @Valid final UpdateGenreFilterSettingsRequest request
    ) {
        throw new RuntimeException("Not implemented");
    }
}
