package es.caib.helium.camunda.service;

import java.util.Date;

public interface TimerService {

    public void suspendTimer(String timerId, Date dueDate);
    public void resumeTimer(String timerId, Date dueDate);

}
