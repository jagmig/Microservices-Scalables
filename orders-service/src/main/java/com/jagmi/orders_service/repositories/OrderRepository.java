package com.jagmi.orders_service.repositories;

import com.jagmi.orders_service.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
