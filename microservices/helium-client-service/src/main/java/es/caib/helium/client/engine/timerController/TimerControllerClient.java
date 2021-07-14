package es.caib.helium.client.engine.timerController;

import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public interface TimerControllerClient {

	public void suspendTimer(String timerId, Date dueDate);
	
	public void resumeTimer(String timerId, Date dueDate);
}
