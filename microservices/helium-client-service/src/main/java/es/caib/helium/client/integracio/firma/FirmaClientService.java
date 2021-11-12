package es.caib.helium.client.integracio.firma;

import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.firma.model.FirmaPost;
import es.caib.helium.client.integracio.firma.model.FirmaResposta;

@Service
public interface FirmaClientService {

	FirmaResposta firmar(FirmaPost firma, Long entornId);
}
