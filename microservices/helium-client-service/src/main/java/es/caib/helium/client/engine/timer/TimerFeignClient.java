package es.caib.helium.client.engine.timer;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface TimerFeignClient {

	@RequestMapping(method = RequestMethod.POST, value = TimerApiPath.SUSPEND_TIMER)
	public ResponseEntity<Void> suspendTimer(
            @PathVariable("timerId") String timerId,
            @RequestBody Date dueDate);
	
	@RequestMapping(method = RequestMethod.POST, value = TimerApiPath.RESUME_TIMER)
	public ResponseEntity<Void> resumeTimer(
            @PathVariable("timerId") String timerId,
            @RequestBody Date dueDate);
}
