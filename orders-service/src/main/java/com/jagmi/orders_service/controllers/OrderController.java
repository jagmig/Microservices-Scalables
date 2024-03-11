package com.jagmi.orders_service.controllers;

import com.jagmi.orders_service.model.dtos.OrderRequest;
import com.jagmi.orders_service.model.dtos.OrdersResponse;
import com.jagmi.orders_service.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder(@RequestBody OrderRequest orderRequest) {

        this.orderService.placeOrder(orderRequest);
        return "Order created";
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrdersResponse> getOrders() {
        return this.orderService.getAllOrders();
    }
}
