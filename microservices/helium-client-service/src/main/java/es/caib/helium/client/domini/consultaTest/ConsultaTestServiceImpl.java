package es.caib.helium.client.domini.consultaTest;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import es.caib.helium.client.domini.consultaTest.model.FilaResultat;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsultaTestServiceImpl implements ConsultaTestService {
	
	private final String missatgeLog = "Cridant Domini Service - ConsultaTest - ";

	private ConsultaTestFeignClient consultaTestClient;
	
	@Override
	public List<FilaResultat> dominisRestTest(Map<String, String> allParams) {
		
		log.debug(missatgeLog + " domini rest test ");
		var responseEntity = consultaTestClient.dominisRestTest(allParams);
		var resultat = Objects.requireNonNull(responseEntity.getBody());
		return resultat;
	}
	
}
