package es.caib.helium.client.integracio.persones;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.persones.model.Persona;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonaClientServiceImpl implements PersonaClientService {

	private final String missatgeLog = "Cridant Integracio Service - Persona - ";
	
	private PersonaFeignClient personaClient;

	@Override
	public List<Persona> findAll(Long entornId) {

		//TODO Metode no implementat al microservei. Veure des d'on es crida
		return null;
	}

	@Override
	public List<Persona> getPersones(String textSearch, Long entornId) {
		
		log.debug(missatgeLog + " buscant persona amb el filtre " + textSearch + " per l'entorn " + entornId);
		var responseEntity = personaClient.getPersones(textSearch, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public Persona getPersonaByCodi(String codi, Long entornId) {
		
		log.debug(missatgeLog + " buscant persona amb codi " + codi + " per l'entorn " + entornId);
		var responseEntity = personaClient.getPersonaByCodi(codi, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<String> getPersonaRolsByCodi(String codi, Long entornId) {
		
		log.debug(missatgeLog + " buscant rols de la persona amb codi " + codi + " per l'entorn " + entornId);
		var responseEntity = personaClient.getPersonaRolsByCodi(codi, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}
}
