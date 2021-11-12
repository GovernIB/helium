package es.caib.helium.client.integracio.firma;

import java.util.Objects;

import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.firma.model.FirmaPost;
import es.caib.helium.client.integracio.firma.model.FirmaResposta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FirmaClientServiceImpl implements FirmaClientService {

	private final String missatgeLog = "Cridant Integracio Service - Firma - ";
	
	private FirmaFeignClient firmaClient;
	
	@Override
	public FirmaResposta firmar(FirmaPost firma, Long entornId) {
		
		log.debug(missatgeLog + " firmant l'arxiu " + firma.getNom() + " per l'entorn " + entornId);
		var responseEntity = firmaClient.firmar(firma, entornId);
	 	FirmaResposta resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

}
