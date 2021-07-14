package es.caib.helium.client.integracio.validacio;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.arxiu.model.ArxiuFirma;
import es.caib.helium.client.integracio.arxiu.model.ArxiuFirmaDetall;
import es.caib.helium.client.model.RespostaValidacioSignatura;
import es.caib.helium.client.integracio.validacio.model.VerificacioFirma;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ValidacioClientClientServiceImpl implements ValidacioClientService {

	private final String missatgeLog = "Cridant Integracio Service - Validacio - ";
	
	private ValidacioFeignClient validacioUnitat;

	@Override
	public RespostaValidacioSignatura verificacio(VerificacioFirma verificacio) {
		
		log.debug(missatgeLog + " verificacio firma  documentStoreId " + verificacio.getDocumentStoreId() + " per l'entorn " + verificacio.getEntornId());
		var responseEntity = validacioUnitat.verificacio(verificacio);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<ArxiuFirma> validacioFirmes(VerificacioFirma validacio) {
		
		log.debug(missatgeLog + " validacio firmes documentStoreId " + validacio.getDocumentStoreId() + " per l'entornId " + validacio.getEntornId());
		var responseEntity = validacioUnitat.validacioFirmes(validacio);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}

	@Override
	public List<ArxiuFirmaDetall> validacioDetalls(VerificacioFirma validacio) {
		
		log.debug(missatgeLog + " validacio detalls documentStoreId " + validacio.getDocumentStoreId() + " per l'entornId " + validacio.getEntornId());
		var responseEntity = validacioUnitat.validacioDetalls(validacio);
	 	var resultat = Objects.requireNonNull(responseEntity.getBody());
    	return resultat;
	}
}
