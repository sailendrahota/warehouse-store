package com.fulfilment.application.monolith.fulfillment.adapters.restapi;

import com.fulfilment.application.monolith.fulfillment.domain.model.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.AssociateFulfillmentOperation;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
public class FulfillmentResourceImpl implements FulfillmentResource {

    private final AssociateFulfillmentOperation
            associateFulfillmentOperation;

    public FulfillmentResourceImpl(
            AssociateFulfillmentOperation associateFulfillmentOperation) {
        this.associateFulfillmentOperation =
                associateFulfillmentOperation;
    }

    @Override
    public Response create(Fulfillment request) {

        Fulfillment fulfillment =
                associateFulfillmentOperation.associate(request);

        return Response
                .status(Response.Status.CREATED)
                .entity(fulfillment)
                .build();
    }

    @Override
    public List<Fulfillment> getAll() {
        return associateFulfillmentOperation.getAll();
    }
}