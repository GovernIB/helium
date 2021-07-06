package es.caib.helium.client.domini.consultaTest;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.helium.client.domini.consultaTest.model.FilaResultat;

@Service
public interface ConsultaTestService {

	public List<FilaResultat> dominisRestTest(@RequestParam Map<String,String> allParams);
}
