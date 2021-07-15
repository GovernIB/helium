package es.caib.helium.client.engine.timer;

import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public interface TimerClient {

	public void suspendTimer(String timerId, Date dueDate);
	
	public void resumeTimer(String timerId, Date dueDate);
}
