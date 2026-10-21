package org.controllers.DTO;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateTitleFilterSettingsRequest {
    @Size(max = 100, message = "Title should be shorter than 100 characters")
    private String title;
}
