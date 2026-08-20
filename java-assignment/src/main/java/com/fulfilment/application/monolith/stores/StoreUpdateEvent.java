package com.fulfilment.application.monolith.stores;

public record StoreUpdateEvent(
        Store store,
        ChangeType type) {

    public enum ChangeType {
        CREATED,
        UPDATED
    }
}
