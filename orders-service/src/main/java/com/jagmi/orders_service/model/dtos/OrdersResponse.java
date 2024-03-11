package com.jagmi.orders_service.model.dtos;

import java.util.List;

public record OrdersResponse(Long id, String orderNumber, List<OrderItemsResponse> orderItems) {
}
