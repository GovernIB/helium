package es.caib.helium.integracio.service.firma;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.firma.FirmaPost;
import es.caib.helium.integracio.excepcions.firma.FirmaException;

@Service
public interface FirmaService {

	public byte[] firmar(FirmaPost firma, Long entornId) throws FirmaException;
}
