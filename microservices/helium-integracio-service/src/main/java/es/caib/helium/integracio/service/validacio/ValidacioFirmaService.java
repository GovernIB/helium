package es.caib.helium.integracio.service.validacio;

import java.util.List;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.arxiu.ArxiuFirma;
import es.caib.helium.integracio.domini.arxiu.ArxiuFirmaDetall;
import es.caib.helium.integracio.domini.validacio.RespostaValidacioSignatura;
import es.caib.helium.integracio.domini.validacio.VerificacioFirma;
import es.caib.helium.integracio.excepcions.validacio.ValidacioFirmaException;

@Service
public interface ValidacioFirmaService {
	
	public RespostaValidacioSignatura verificarFirma(VerificacioFirma verificacio) throws ValidacioFirmaException;
	public List<ArxiuFirma> validarFirma(VerificacioFirma validar) throws ValidacioFirmaException;
	public List<ArxiuFirmaDetall> validarSignaturaObtenirDetalls(VerificacioFirma validacio) throws ValidacioFirmaException;
}
