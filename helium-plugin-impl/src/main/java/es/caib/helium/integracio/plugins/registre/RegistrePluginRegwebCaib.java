package es.caib.helium.integracio.plugins.registre;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import es.caib.helium.commons.utils.GlobalProperties;
import es.caib.regweb3.ws.api.v3.RegistroEntradaWs;


/**
 * Implementació del plugin de registre per a la interficie ejb del
 * registre de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class RegistrePluginRegwebCaib extends RegWeb3Utils implements RegistrePlugin {

	private static final String SEPARADOR_ENTITAT = "-";
	private static final String SEPARADOR_NUMERO = "/";

	@Override
	public RespostaAnotacioRegistre registrarEntrada(RegistreEntrada registreEntrada) throws RegistrePluginException {
		RegistroEntradaWs re = new RegistroEntradaWs();
		re.setOficina(registreEntrada.getDadesOficina().getOficinaCodi());
//		re.setAnexos(registreEntrada.getDocuments().stream().map((dr) -> {
//			dr.getArxiuContingut();
//		}));
		re.setAplicacion("Helium");
		re.setCodigoAsunto(registreEntrada.getDadesAssumpte().getAssumpte());
		re.setCodigoUsuario(registreEntrada.getDadesInteressat().getNif());
		re.setContactoUsuario(registreEntrada.getDadesInteressat().getEmail());
//		re.setDocFisica();
//		re.setExpone();
//		re.setExtracto(extracto);
//		re.setFecha();
//		re.setIdioma(idioma);
//		re.setInteresados(interesados);
//		re.setLibro(libro);
//		re.setNumExpediente(numExpediente);
//		re.setNumTransporte(numTransporte);
//		re.setNumero(numero);
//		re.setNumeroRegistroFormateado(numeroRegistroFormateado);
//		re.setObservaciones(observaciones);
//		re.setOficina(oficina);
//		re.setRefExterna(refExterna);
//		re.setSolicita(solicita);
//		re.setTipoAsunto(tipoAsunto);
//		re.setTipoTransporte(tipoTransporte);
//		re.setVersion(version);
//		getRegistroEntradaApi().altaRegistroEntrada()
		return null;
	}



	@Override
	public RespostaConsulta consultarEntrada(String organCodi, String oficinaCodi, String numeroRegistre)
			throws RegistrePluginException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public RespostaAnotacioRegistre registrarSortida(RegistreSortida registreSortida) throws RegistrePluginException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public RespostaConsulta consultarSortida(String organCodi, String oficinaCodi, String numeroRegistre)
			throws RegistrePluginException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public RespostaAnotacioRegistre registrarNotificacio(RegistreNotificacio registreNotificacio)
			throws RegistrePluginException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public RespostaJustificantRecepcio obtenirJustificantRecepcio(String numeroRegistre)
			throws RegistrePluginException {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String obtenirNomOficina(String oficinaCodi) throws RegistrePluginException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static final Log logger = LogFactory.getLog(RegistrePluginRegwebCaib.class);
}
