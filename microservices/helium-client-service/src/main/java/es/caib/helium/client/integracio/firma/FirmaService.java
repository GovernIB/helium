package es.caib.helium.client.integracio.firma;

import org.springframework.stereotype.Service;

import es.caib.helium.client.integracio.firma.model.FirmaPost;

@Service
public interface FirmaService {

	public byte[] firmar(FirmaPost firma, Long entornId);
}
