package es.caib.helium.client.integracio.validacio;

import java.util.List;

import es.caib.helium.client.integracio.arxiu.model.ArxiuFirma;
import es.caib.helium.client.integracio.arxiu.model.ArxiuFirmaDetall;
import es.caib.helium.client.model.RespostaValidacioSignatura;
import es.caib.helium.client.integracio.validacio.model.VerificacioFirma;

public interface ValidacioClientService {

	public RespostaValidacioSignatura verificacio(VerificacioFirma verificacio);
	
	
	public List<ArxiuFirma> validacioFirmes(VerificacioFirma validacio);
	
	public List<ArxiuFirmaDetall> validacioDetalls(VerificacioFirma validacio);
}
