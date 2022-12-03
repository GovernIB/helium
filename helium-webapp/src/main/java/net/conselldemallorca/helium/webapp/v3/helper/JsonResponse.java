package net.conselldemallorca.helium.webapp.v3.helper;

import lombok.Data;

@Data
public class JsonResponse {
    private Object data;
    private boolean error;
    private String errorMsg;

    private boolean warning;
    private String warningMsg;

    public JsonResponse(Object data) {
        super();
        this.data = data;
    }

    public JsonResponse(
            boolean error,
            String errorMsg) {
        this.error = error;
        this.errorMsg = errorMsg;
    }

    public JsonResponse(
            String  warningMsg,
            boolean warning) {
        this.warning = warning;
        this.warningMsg = warningMsg;
    }
}
