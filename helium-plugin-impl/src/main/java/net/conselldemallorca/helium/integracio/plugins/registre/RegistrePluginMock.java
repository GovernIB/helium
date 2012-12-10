package net.conselldemallorca.helium.integracio.plugins.registre;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.caib.regweb.logic.helper.ParametrosRegistroEntrada;
import es.caib.regweb.logic.helper.ParametrosRegistroSalida;
import es.caib.regweb.logic.interfaces.RegistroEntradaFacade;
import es.caib.regweb.logic.interfaces.RegistroSalidaFacade;

/**
 * Implementació Mock del plugin de registre.
 * 
 * @author Tomeu Domenge <tomeud@limit.es>
 */
public class RegistrePluginMock implements RegistrePlugin{
	private static final String SEPARADOR_ENTITAT = "-";
	private static final String SEPARADOR_NUMERO = "/";

	public RespostaAnotacioRegistre registrarEntrada(
			RegistreEntrada registreEntrada) throws RegistrePluginException {
		try {
			Date ara = new Date();
			RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
			resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
			resposta.setNumero(
					"0" +
					SEPARADOR_NUMERO +
					"2012");
			resposta.setData(ara);
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al registrar l'entrada", ex);
			throw new RegistrePluginException("Error al registrar l'entrada", ex);
		}
	}
	
	public RespostaAnotacioRegistre registrarSortida(
			RegistreSortida registreSortida) throws RegistrePluginException {
		try {
			Date ara = new Date();
			RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
			resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
			resposta.setNumero(
					"0" +
					SEPARADOR_NUMERO +
					"2012");
			resposta.setData(ara);
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al registrar la sortida", ex);
			throw new RegistrePluginException("Error al registrar la sortida", ex);
		}	
	}

	public RespostaConsulta consultarEntrada(String organCodi,
			String oficinaCodi, String numeroRegistre)
			throws RegistrePluginException {
		// TODO Auto-generated method stub  NOT IMPLEMENTED
		return null;
	}

	public RespostaConsulta consultarSortida(String organCodi,
			String oficinaCodi, String numeroRegistre)
			throws RegistrePluginException {
		// TODO Auto-generated method stub  NOT IMPLEMENTED
		return null;
	}

	public RespostaAnotacioRegistre registrarNotificacio(
			RegistreNotificacio registreNotificacio)
			throws RegistrePluginException {
		// TODO Auto-generated method stub  NOT IMPLEMENTED
		return null;
	}

	public RespostaJustificantRecepcio obtenirJustificantRecepcio(
			String numeroRegistre) throws RegistrePluginException {
		// TODO Auto-generated method stub  NOT IMPLEMENTED
		return null;
	}

	public String obtenirNomOficina(String oficinaCodi)
			throws RegistrePluginException {
		// TODO Auto-generated method stub  NOT IMPLEMENTED
		return null;
	}
	
	private String convertirIdioma(String iso6391) {
		if ("es".equalsIgnoreCase(iso6391)) {
			return "1";
		} else if ("ca".equalsIgnoreCase(iso6391)) {
			return "2";
		} else if ("eu".equalsIgnoreCase(iso6391)) {
			return "4";
		} else if ("gl".equalsIgnoreCase(iso6391)) {
			return "5";
		} else if ("as".equalsIgnoreCase(iso6391)) {
			return "6";
		} else if ("de".equalsIgnoreCase(iso6391)) {
			return "C";
		} else if ("en".equalsIgnoreCase(iso6391)) {
			return "A";
		} else if ("fr".equalsIgnoreCase(iso6391)) {
			return "B";
		} else if ("it".equalsIgnoreCase(iso6391)) {
			return "E";
		} else if ("pt".equalsIgnoreCase(iso6391)) {
			return "F";
		}
		return "2";
	}
	
	private static final Log logger = LogFactory.getLog(RegistrePluginRegwebLogic.class);
}
