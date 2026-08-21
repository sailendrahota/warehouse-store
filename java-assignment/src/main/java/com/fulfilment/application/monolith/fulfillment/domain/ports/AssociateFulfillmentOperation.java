package com.fulfilment.application.monolith.fulfillment.domain.ports;

import com.fulfilment.application.monolith.fulfillment.domain.model.Fulfillment;

import java.util.List;

public interface AssociateFulfillmentOperation {

    Fulfillment associate(Fulfillment fulfillment);
    List<Fulfillment> getAll();
}
