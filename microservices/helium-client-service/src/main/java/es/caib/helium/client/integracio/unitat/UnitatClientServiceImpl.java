package es.caib.helium.client.integracio.unitat;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.unitat.model.UnitatOrganica;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnitatClientServiceImpl implements UnitatClientService {
	
	private final String missatgeLog = "Cridant Integracio Service - Unitat - ";
	
	private UnitatFeignClient clientUnitat;

	@Override
	public UnitatOrganica consultaUnitat(String codi, Long entornId) {

		log.debug(missatgeLog + " consulta unitat amb codi " + codi + " per l'entornId " + entornId);
		var responseEntity = clientUnitat.consultaUnitat(codi, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<UnitatOrganica> consultaArbre(String codi, Long entornId) {
		
		log.debug(missatgeLog + " consulta unitat amb codi " + codi + " per l'entornId " + entornId);
		var responseEntity = clientUnitat.consultaArbre(codi, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}
}
