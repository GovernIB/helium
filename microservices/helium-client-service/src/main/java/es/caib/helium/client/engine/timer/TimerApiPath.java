package es.caib.helium.client.engine.timer;

public class TimerApiPath {
		
	public static final String API_PATH = "/api/v1/timers";

	public static final String SUSPEND_TIMER = API_PATH + "/{timerId}/suspend";

	public static final String RESUME_TIMER = API_PATH + "/{timerId}/resume";
}
