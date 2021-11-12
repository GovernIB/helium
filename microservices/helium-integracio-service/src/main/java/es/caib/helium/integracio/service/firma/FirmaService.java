package es.caib.helium.integracio.service.firma;

import es.caib.helium.integracio.domini.firma.FirmaPost;
import es.caib.helium.integracio.excepcions.firma.FirmaException;
import org.springframework.stereotype.Service;

@Service
public interface FirmaService {

	FirmaResposta firmar(FirmaPost firma, Long entornId) throws FirmaException;
}
