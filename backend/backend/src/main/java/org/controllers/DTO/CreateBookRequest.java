package org.controllers.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class CreateBookRequest {
    @NotNull(message = "Book id should be specified")
    @Positive(message = "Book id should be positive")
    private Integer id;

    @NotBlank(message = "Title should be specified")
    private String title;

    @NotBlank(message = "Author should be specified")
    private String author;

    @NotBlank(message = "Genre should be specified")
    private String genre;

    @NotBlank(message = "Description should be specified")
    private String description;
}
