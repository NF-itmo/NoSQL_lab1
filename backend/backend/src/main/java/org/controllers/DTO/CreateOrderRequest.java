package org.controllers.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class CreateOrderRequest {
    @NotNull(message = "Book id should be specified")
    @Positive(message = "Book id should be positive")
    private Integer bookId;
}
