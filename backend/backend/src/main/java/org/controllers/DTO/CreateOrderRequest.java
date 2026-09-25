package org.controllers.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateOrderRequest {
    private Integer bookId;
}
