package org.controllers;

import jakarta.validation.Valid;
import org.controllers.DTO.ConfirmOrderResponse;
import org.controllers.DTO.CreateOrderRequest;
import org.controllers.DTO.GetOrderResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@PreAuthorize("isAuthenticated()")
public class OrdersController {
    @GetMapping
    public List<GetOrderResponse> getOrders() {
        throw new RuntimeException("Not implemented");
    }

    @PostMapping
    public GetOrderResponse createOrder(
            @RequestBody @Valid final CreateOrderRequest request
    ) {
        throw new RuntimeException("Not implemented");
    }

    @GetMapping("/{orderId}")
    public GetOrderResponse getOrder(
            @PathVariable String orderId
    ) {
        throw new RuntimeException("Not implemented");
    }

    @PostMapping("/{orderId}/confirm")
    public ConfirmOrderResponse confirmOrder(
            @PathVariable String orderId
    ) {
        throw new RuntimeException("Not implemented");
    }

    @PostMapping("/{orderId}/cancel")
    public ConfirmOrderResponse cancelOrder(
            @PathVariable String orderId
    ) {
        throw new RuntimeException("Not implemented");
    }
}
