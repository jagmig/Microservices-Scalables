package com.jagmi.inventoryservice.controllers;

import com.jagmi.inventoryservice.model.dtos.BaseResponse;
import com.jagmi.inventoryservice.model.dtos.OrderItemsRequest;
import com.jagmi.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{sku}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean isInStock(@PathVariable String sku){

        return inventoryService.isInStock(sku);
    }

    @PostMapping("/in-stock")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse areInStock(@RequestBody List<OrderItemsRequest> orderItems){

        return inventoryService.areInStock(orderItems);
    }
}
