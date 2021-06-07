package es.caib.helium.integracio.service.validacio;

import es.caib.helium.integracio.domini.validacio.RespostaValidacioSignatura;
import es.caib.helium.integracio.excepcions.validacio.ValidacioFirmaException;

public interface SignaturaPlugin {

	public RespostaValidacioSignatura verificarSignatura( 
			byte[] document,
			byte[] signatura,
			boolean obtenirDadesCertificat) throws ValidacioFirmaException;
}
