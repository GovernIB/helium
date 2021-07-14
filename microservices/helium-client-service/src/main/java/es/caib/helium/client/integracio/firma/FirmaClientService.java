package es.caib.helium.client.integracio.firma;

import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.firma.model.FirmaPost;

@Service
public interface FirmaClientService {

	byte[] firmar(FirmaPost firma, Long entornId);
}
