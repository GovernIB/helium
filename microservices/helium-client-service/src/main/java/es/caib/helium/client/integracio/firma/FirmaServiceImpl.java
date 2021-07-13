package es.caib.helium.client.integracio.firma;

import java.util.Objects;

import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.firma.model.FirmaPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FirmaServiceImpl implements FirmaService {

	private final String missatgeLog = "Cridant Integracio Service - Firma - ";
	
	private FirmaFeignClient firmaClient;
	
	@Override
	public byte[] firmar(FirmaPost firma, Long entornId) {
		
		log.debug(missatgeLog + " firmant l'arxiu " + firma.getArxiuNom() + " per l'entorn " + entornId);
		var responseEntity = firmaClient.firmar(firma, entornId);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

}
