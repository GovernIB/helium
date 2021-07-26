package es.caib.helium.client.integracio.registre;

import es.caib.helium.client.integracio.registre.model.RegistreAssentament;
import es.caib.helium.client.integracio.registre.model.RespostaAnotacioRegistre;
import es.caib.helium.client.integracio.registre.model.RespostaConsultaRegistre;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;


@Service
@Slf4j
@RequiredArgsConstructor
public class RegistreClientServiceImpl implements RegistreClientService {
	
	private final String missatgeLog = "Cridant Integracio Service - Registre - ";
	
	private RegistreFeignClient registreClient;

	@Override
	public Date getDataJustificant(String numeroRegistre) {

		log.debug(missatgeLog + " obtinguent la data del justificant amb numero de registre " + numeroRegistre);
		var responseEntity = registreClient.getDataJustificant(numeroRegistre);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public RespostaAnotacioRegistre crearRegistreSortida(RegistreAssentament registre, Long entornId) {
		
		log.debug(missatgeLog + " creant registre de sortida amb identificador " + registre.getIdentificador() + " per l'entorn " + entornId);
		var responseEntity = registreClient.crearRegistreSortida(registre, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public RespostaConsultaRegistre getRegistreOficinaNom(String numeroRegistre, String usuariCodi, String entitatCodi, Long entornId) {
		
		log.debug(missatgeLog + " obtinguent el nom de l'oficina del registre numero" + numeroRegistre 
				+ " amb codi d'usuari" + usuariCodi + " i entitatCodi " + entitatCodi + " per l'entorn " + entornId);
		var responseEntity = registreClient.getRegistreOficinaNom(numeroRegistre, usuariCodi, entitatCodi, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}
}
