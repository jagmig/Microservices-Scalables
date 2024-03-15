package com.jagmi.orders_service.services;

import com.jagmi.orders_service.entities.Order;
import com.jagmi.orders_service.entities.OrderItems;
import com.jagmi.orders_service.events.OrderEvent;
import com.jagmi.orders_service.model.dtos.*;
import com.jagmi.orders_service.model.enums.OrderStatus;
import com.jagmi.orders_service.repositories.OrderRepository;
import com.jagmi.orders_service.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrdersResponse placeOrder(OrderRequest orderRequest) {

        //Antes de guardar los items debemos revisar que tengan stock o inventario
        BaseResponse baseResult = this.webClient.build()
                .post()
                .uri("lb://inventory-service/api/inventory/in-stock")
                .bodyValue(orderRequest.getOrdersItems())
                .retrieve()
                .bodyToMono(BaseResponse.class)
                .block();

        if (baseResult != null && !baseResult.hasError()) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setOrderItems(orderRequest.getOrdersItems().stream()
                    .map(orderItemRequest -> mapOrderItemRequest(orderItemRequest, order))
                    .toList());
           var savedOrder = this.orderRepository.save(order);
           //TODO: Send message to order topic
           this.kafkaTemplate.send("orders-topic", JsonUtils.toJson(
                   new OrderEvent(savedOrder.getOrderNumber(), savedOrder.getOrderItems().size(), OrderStatus.PLACED)
           ));
           return mapToOrderResponse(savedOrder);
        }else{
            throw new IllegalArgumentException("Some of the products are not in stock");
        }
    }

    public List<OrdersResponse> getAllOrders() {
        List<Order> orders = this.orderRepository.findAll();

        return orders.stream().map(this::mapToOrderResponse).toList();
    }

    private OrdersResponse mapToOrderResponse(Order order) {
        return new OrdersResponse(order.getId(), order.getOrderNumber(),
        order.getOrderItems().stream().map(this::mapToOrderItemRequest).toList());
    }

    private OrderItemsResponse mapToOrderItemRequest(OrderItems orderItems) {

        return new OrderItemsResponse(orderItems.getId(), orderItems.getSku(), orderItems.getPrice(), orderItems.getQuantity());
    }


    private OrderItems mapOrderItemRequest(OrderItemsRequest orderItemRequest, Order order) {
        return OrderItems.builder()
                .id(orderItemRequest.getId())
                .sku(orderItemRequest.getSku())
                .price(orderItemRequest.getPrice())
                .quantity(orderItemRequest.getQuantity())
                .order(order)
                .build();
    }
}
