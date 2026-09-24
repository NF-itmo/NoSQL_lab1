package org.controllers.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateGenreFilterSettingsRequest {
    @NotBlank(message = "Genre should be specified")
    private String genre;
}
