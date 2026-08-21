package com.fulfilment.application.monolith.warehouses.domain.validation;

public class WarehouseValidationException extends RuntimeException {

    public WarehouseValidationException(String message) {
        super(message);
    }
}