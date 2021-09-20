package es.caib.helium.camunda.helper;

public class ThreadLocalInfo {

    public static ThreadLocal<Long> expedientIdThreadLocal = new ThreadLocal<>();
    public static ThreadLocal<Boolean> startExpedientThreadLocal = new ThreadLocal<>();

    public static Long getExpedientId() {
        return expedientIdThreadLocal.get();
    }
    public static void setExpedientId(Long expedientId) {
        expedientIdThreadLocal.set(expedientId);
    }

    public static Boolean getStartExpedient() {
        return startExpedientThreadLocal.get();
    }
    public static void setStartExpedient(Boolean startExpedient) {
        startExpedientThreadLocal.set(startExpedient);
    }
}
