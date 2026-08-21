package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.model.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.AssociateFulfillmentOperation;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.fulfillment.domain.validation.FulfillmentValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class AssociateFulfillmentUseCase implements AssociateFulfillmentOperation {

    private final FulfillmentStore fulfillmentStore;
    private final FulfillmentValidator validator;

    public AssociateFulfillmentUseCase(FulfillmentStore fulfillmentStore, FulfillmentValidator validator) {
        this.fulfillmentStore = fulfillmentStore;
        this.validator = validator;
    }

    @Override
    @Transactional
    public Fulfillment associate(Fulfillment fulfillment) {

        validator.validate(fulfillment);

        fulfillmentStore.create(fulfillment);

        return fulfillment;
    }

    @Override
    public List<Fulfillment> getAll() {
        return fulfillmentStore.getAll();
    }
}