package es.caib.helium.api.controller.tramitacio.v1;

import es.caib.helium.client.tramitacio.v1.TramitacioErrorResponse;
import es.caib.helium.client.tramitacio.v1.TramitacioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class TramitacioExceptionHandler {

	@ExceptionHandler(TramitacioException.class)
	public ResponseEntity<TramitacioErrorResponse> handleAll(Exception ex, HttpServletRequest req) {
		TramitacioErrorResponse response = new TramitacioErrorResponse();
		response.setTimestamp(Instant.now().toString());
		response.setStatus(500);
		response.setError("Internal Server Error");
		response.setMessage(ex.getMessage());
		response.setPath(req.getRequestURL().toString());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

}
