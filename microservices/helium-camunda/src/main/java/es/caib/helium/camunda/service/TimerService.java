package es.caib.helium.camunda.service;

import java.util.Date;

public interface TimerService {

    public void suspendTimer(long timerId, Date dueDate);
    public void resumeTimer(long timerId, Date dueDate);

}
