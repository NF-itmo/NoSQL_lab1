package org.controllers;

import jakarta.validation.Valid;
import org.controllers.DTO.CreateOrderRequest;
import org.controllers.DTO.GetOrderResponse;
import org.models.Order;
import org.services.OrdersService;
import org.springframework.http.ResponseEntity;
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
    private final OrdersService ordersService;

    public OrdersController(
            OrdersService ordersService
    ) {
        this.ordersService = ordersService;
    }

    @GetMapping
    public List<GetOrderResponse> getOrders() {
        return ordersService.getAll().stream()
                .map(order -> new GetOrderResponse(
                        order.getId(),
                        order.getBookId(),
                        order.getExpiresAt()
                ))
                .toList();
    }

    @PostMapping
    public ResponseEntity<GetOrderResponse> createOrder(
            @RequestBody @Valid final CreateOrderRequest request
    ) {
        final Order order = ordersService.create(request.getBookId());

        return ResponseEntity.ok(new GetOrderResponse(
                order.getId(),
                order.getBookId(),
                order.getExpiresAt()
        ));
    }

    @GetMapping("/{orderId}")
    public GetOrderResponse getOrder(
            @PathVariable Integer orderId
    ) {
        final Order order = ordersService.getById(orderId);

        return new GetOrderResponse(
                order.getId(),
                order.getBookId(),
                order.getExpiresAt()
        );
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<Void> confirmOrder(
            @PathVariable Integer orderId
    ) {
        ordersService.confirm(orderId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable String orderId
    ) {
        ordersService.cancel(Integer.parseInt(orderId));

        return ResponseEntity.noContent().build();
    }
}
