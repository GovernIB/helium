package es.caib.helium.client.integracio.persones;

import es.caib.helium.client.integracio.persones.model.Persona;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonaClientImpl implements PersonaClient {

	private final String missatgeLog = "Cridant Integracio Service - Persona - ";
	
	private final PersonaFeignClient personaClient;

	@Override
	public List<Persona> findAll(Long entornId) {

		//TODO Metode no implementat al microservei. Veure des d'on es crida
		return null;
	}

	@Override
	public List<Persona> getPersones(String textSearch, Long entornId) {
		
		log.debug(missatgeLog + " buscant persona amb el filtre " + textSearch + " per l'entorn " + entornId);
		return personaClient.getPersones(textSearch, entornId).getBody();
	}

	@Override
	public Persona getPersonaByCodi(String codi, Long entornId) {
		
		log.debug(missatgeLog + " buscant persona amb codi " + codi + " per l'entorn " + entornId);
		return personaClient.getPersonaByCodi(codi, entornId).getBody();
	}

	@Override
	public List<String> getPersonaRolsByCodi(String codi, Long entornId) {
		
		log.debug(missatgeLog + " buscant rols de la persona amb codi " + codi + " per l'entorn " + entornId);
		return personaClient.getPersonaRolsByCodi(codi, entornId).getBody();
	}

	@Override
	public List<String> getPersonesCodiByRol(String rol, Long entornId) {

		log.debug(missatgeLog + " buscant codi de persones amb rol " + rol + " per l'entorn " + entornId);
		return personaClient.getPersonesCodiByRol(rol, entornId).getBody();
	}

	@Override
	public List<Persona> getPersonesByCodi(List<String> codis, Long entornId) {

		log.debug(missatgeLog + " buscant rols de la persona amb codis " + codis + " per l'entorn " + entornId);
		return personaClient.getPersonesByCodi(codis, entornId).getBody();
	}
}
