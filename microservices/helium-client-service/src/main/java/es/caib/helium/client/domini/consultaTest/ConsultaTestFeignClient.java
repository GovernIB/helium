package es.caib.helium.client.domini.consultaTest;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.domini.consultaTest.model.FilaResultat;

public interface ConsultaTestFeignClient {

	@RequestMapping(method = RequestMethod.GET, value = ConsultaTestApiPath.DOMINIS_REST_TEST)
	public ResponseEntity<List<FilaResultat>> dominisRestTest(@RequestParam Map<String,String> allParams);
}
