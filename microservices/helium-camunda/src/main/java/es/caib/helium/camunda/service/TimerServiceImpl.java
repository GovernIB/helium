package es.caib.helium.camunda.service;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TimerServiceImpl implements TimerService {

    private final RuntimeService runtimeService;
    private final ManagementService managementService;

    @Override
    public void suspendTimer(String timerId, Date dueDate) {
        managementService.setJobDuedate(timerId, dueDate);
        managementService.suspendJobById(timerId);
    }

    @Override
    public void resumeTimer(String timerId, Date dueDate) {
        managementService.setJobDuedate(timerId, dueDate);
        managementService.activateJobById(timerId);
    }
}
