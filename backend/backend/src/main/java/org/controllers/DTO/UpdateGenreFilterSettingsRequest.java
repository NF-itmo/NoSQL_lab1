package org.controllers.DTO;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateGenreFilterSettingsRequest {
    @Size(max = 100, message = "Genre should be shorter than 100 characters")
    private String genre;
}
