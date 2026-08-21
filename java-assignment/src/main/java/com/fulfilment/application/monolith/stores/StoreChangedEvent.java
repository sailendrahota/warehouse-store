package com.fulfilment.application.monolith.stores;

public record StoreChangedEvent(
        Store store,
        ChangeType type) {

    public enum ChangeType {
        CREATED,
        UPDATED
    }
}
