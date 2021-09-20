package es.caib.helium.dada.exception;

public class DocumentException extends Exception {

    public DocumentException() {
        super();
    }

    public DocumentException(Throwable cause) {
        super(cause);
    }

    public DocumentException(String message) {
        super(message);
    }

    public DocumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
