package es.caib.helium.client.integracio.validacio;

import es.caib.helium.client.integracio.arxiu.model.ArxiuFirma;
import es.caib.helium.client.integracio.arxiu.model.ArxiuFirmaDetall;
import es.caib.helium.client.integracio.validacio.model.VerificacioFirma;
import es.caib.helium.client.model.RespostaValidacioSignatura;

import java.util.List;

public interface ValidacioClientService {

	RespostaValidacioSignatura verificacio(VerificacioFirma verificacio);
	
	
	List<ArxiuFirma> validacioFirmes(VerificacioFirma validacio);
	
	List<ArxiuFirmaDetall> validacioDetalls(VerificacioFirma validacio);
}
