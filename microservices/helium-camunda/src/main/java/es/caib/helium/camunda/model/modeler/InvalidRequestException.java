package es.caib.helium.camunda.model.modeler;

import javax.ws.rs.core.Response;

public class InvalidRequestException extends RestException {
    private static final long serialVersionUID = 1L;

    public InvalidRequestException(Response.Status status, String message) {
        super(status, message);
    }

    public InvalidRequestException(Response.Status status, Exception cause, String message) {
        super(status, cause, message);
    }
}
