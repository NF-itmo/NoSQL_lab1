package org.controllers.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateOrderRequest {
    @NotBlank(message = "Book id should be specified")
    private String bookId;
}
