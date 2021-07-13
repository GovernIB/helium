package es.caib.helium.client.engine.timerController;

import java.util.Date;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TimerControllerServiceImpl implements TimerControllerService {

	private final String missatgeLog = "Cridant Engine Service - TimeController - ";

	private TimerControllerFeignClient timeControllerClient;

	@Override
	public void suspendTimer(String timerId, Date dueDate) {
		
		log.debug(missatgeLog + " suspend timer timerId " + timerId + " dueDate " + dueDate);
		timeControllerClient.suspendTimer(timerId, dueDate);
	}

	@Override
	public void resumeTimer(String timerId, Date dueDate) {

		log.debug(missatgeLog + " resume timer timerId " + timerId + " dueDate " + dueDate);
		timeControllerClient.resumeTimer(timerId, dueDate);
	}
}
