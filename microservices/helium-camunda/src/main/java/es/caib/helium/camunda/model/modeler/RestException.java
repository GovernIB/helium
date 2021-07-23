package es.caib.helium.camunda.model.modeler;

import javax.ws.rs.core.Response;

public class RestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private Response.Status status;

    public RestException(String message) {
        super(message);
    }

    public RestException(Response.Status status, String message) {
        super(message);
        this.status = status;
    }

    public RestException(Response.Status status, Exception cause) {
        super(cause);
        this.status = status;
    }

    public RestException(Response.Status status, Exception cause, String message) {
        super(message, cause);
        this.status = status;
    }

    public Response.Status getStatus() {
        return this.status;
    }
}
