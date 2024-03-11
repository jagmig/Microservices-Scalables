package com.jagmi.inventoryservice.model.dtos;

public record BaseResponse(String[] errorMessages) {
    public boolean hasError() {
        return errorMessages!= null && errorMessages.length > 0;
    }
}
