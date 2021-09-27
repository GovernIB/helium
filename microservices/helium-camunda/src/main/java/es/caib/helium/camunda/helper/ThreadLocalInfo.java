package es.caib.helium.camunda.helper;

public class ThreadLocalInfo {

    public static ThreadLocal<Boolean> startExpedientThreadLocal = new ThreadLocal<>();
    public static ThreadLocal<Boolean> releaseTaskThreadLocal = new ThreadLocal<>();


    public static Boolean getStartExpedient() {
        return (startExpedientThreadLocal.get() != null && startExpedientThreadLocal.get());
    }
    public static void setStartExpedient(Boolean startExpedient) {
        startExpedientThreadLocal.set(startExpedient);
    }

    public static Boolean getReleaseTaskThreadLocal() {
        return (releaseTaskThreadLocal.get() != null && releaseTaskThreadLocal.get());
    }
    public static void setReleaseTaskThreadLocal(Boolean release) {
        releaseTaskThreadLocal.set(release);
    }
}
