package com.fulfilment.application.monolith.warehouses.adapters.restapi.exceptionMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fulfilment.application.monolith.fulfillment.domain.validation.FulfillmentValidationException;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseValidationException;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Inject
    ObjectMapper objectMapper;

    @Override
    public Response toResponse(Exception exception) {

        int code = 500;

        if (exception instanceof WarehouseValidationException ||
                exception instanceof FulfillmentValidationException) {
            code = 400;
        } else if (exception instanceof WebApplicationException webException) {
            code = webException.getResponse().getStatus();
        }

        ObjectNode json = objectMapper.createObjectNode();
        json.put("exceptionType", exception.getClass().getName());
        json.put("code", code);

        if (exception.getMessage() != null) {
            json.put("error", exception.getMessage());
        }

        return Response.status(code)
                .entity(json)
                .build();
    }
}
