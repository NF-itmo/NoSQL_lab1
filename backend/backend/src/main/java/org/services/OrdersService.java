package org.services;

import lombok.extern.slf4j.Slf4j;
import org.exceptions.ConflictException;
import org.exceptions.NotFoundException;
import org.models.Order;
import org.repository.OrderConfirmationRepository;
import org.repository.OrdersRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final OrderConfirmationRepository orderConfirmationRepository;

    public OrdersService(
            OrdersRepository ordersRepository,
            OrderConfirmationRepository orderConfirmationRepository
    ) {
        this.ordersRepository = ordersRepository;
        this.orderConfirmationRepository = orderConfirmationRepository;
    }

    public Order getById(Integer orderId) {
        return ordersRepository.getById(orderId).join().orElseThrow(
                () -> {
                    log.info("Order not found {}", orderId);
                    return new NotFoundException("Order not found");
                }
        );
    }

    public List<Order> getAll() {
        return ordersRepository.getAll().join();
    }

    public void cancel(Integer orderId) {
        ordersRepository.deleteById(orderId).join();
    }

    public void confirm(Integer orderId) {
        final Order order = getById(orderId);
        final boolean confirmed = orderConfirmationRepository.confirm(orderId, order.getBookId()).join();

        if (!confirmed) {
            throw new ConflictException("Book is unavailable or order has expired");
        }
    }

    public void create(Integer bookId) {
        ordersRepository.createOrderByBookId(
                bookId
        ).join();
    }
}
