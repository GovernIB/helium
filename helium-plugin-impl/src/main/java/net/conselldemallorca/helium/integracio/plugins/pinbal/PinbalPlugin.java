/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.pinbal;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import es.caib.pinbal.client.recobriment.ClientGeneric;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspFuncionario;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.ScspSolicitante.ScspConsentimiento;
import es.caib.pinbal.client.recobriment.model.ScspTitular;
import es.caib.pinbal.client.recobriment.model.ScspTitular.ScspTipoDocumentacion;
import es.caib.pinbal.client.recobriment.model.Solicitud;
import es.caib.pinbal.client.recobriment.svdccaacpasws01.ClientSvdccaacpasws01;
import es.caib.pinbal.client.recobriment.svdccaacpasws01.ClientSvdccaacpasws01.SolicitudSvdccaacpasws01;
import es.caib.pinbal.client.recobriment.svddgpciws02.ClientSvddgpciws02;
import es.caib.pinbal.client.recobriment.svddgpciws02.ClientSvddgpciws02.SolicitudSvddgpciws02;
import es.caib.pinbal.client.recobriment.svddgpviws02.ClientSvddgpviws02;
import es.caib.pinbal.client.recobriment.svddgpviws02.ClientSvddgpviws02.SolicitudSvddgpviws02;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalEstatEnum;
import net.conselldemallorca.helium.v3.core.api.dto.ScspAtributos;
import net.conselldemallorca.helium.v3.core.api.dto.ScspConfirmacioPeticioPinbal;
import net.conselldemallorca.helium.v3.core.api.dto.ScspEstado;
import net.conselldemallorca.helium.v3.core.api.dto.ScspJustificant;
import net.conselldemallorca.helium.v3.core.api.dto.ScspRespostaPinbal;


/**
 * Implementació de del plugin d'enviament de notificacions
 * emprant NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PinbalPlugin implements PinbalPluginInterface {

	private static final Log logger = LogFactory.getLog(PinbalPlugin.class);
	private static final boolean ENABLE_LOGGING = true;
	
	public PinbalPlugin() {	
		}
	
	@Override
	public Object peticioClientPinbalGeneric(DadesConsultaPinbal dadesConsultaPinbal) throws Exception {
		Solicitud solicitud = new Solicitud();
		
		if (this.validarDadesObligatories(dadesConsultaPinbal)) {
			solicitud.setFinalidad(dadesConsultaPinbal.getFinalitat());
			solicitud.setConsentimiento(compararConsentiment(dadesConsultaPinbal.getConsentiment()));		
		}
		//En el cas de client genèric hem de validar que ens passin un codi del Servei
		if(dadesConsultaPinbal.getServeiCodi()==null || ("").equals(dadesConsultaPinbal.getServeiCodi())) {
			logger.error("Error al obtenir la petició, el Codi del Servei és obligatori.");
			throw new IOException(
					"Error al obtenir la petició, el Codi del Servei és obligatori.");
		}
		ClientGeneric clientGeneric = this.getClientGeneric();

		solicitud.setIdentificadorSolicitante(dadesConsultaPinbal.getEntitat_CIF());
		solicitud.setCodigoProcedimiento(dadesConsultaPinbal.getCodiProcediment());
		solicitud.setUnidadTramitadora(dadesConsultaPinbal.getUnitatTramitadora());
		
		ScspFuncionario funcionario = new ScspFuncionario();		
		ScspTitular scspTitular = new ScspTitular();
		
		this.dadesTitularIFuncionari(funcionario, scspTitular, dadesConsultaPinbal);
		solicitud.setFuncionario(funcionario);
		solicitud.setTitular(scspTitular);
	
		if(dadesConsultaPinbal.getXmlDadesEspecifiques()!=null) {
			solicitud.setDatosEspecificos(dadesConsultaPinbal.getXmlDadesEspecifiques());
		}
		//El nom complert no cal segons per quin servei, per exemple en el de VERIFICACIÓ peta si se li passa, però va bé en el de CONSULTA 
		if(!"SVDDGPVIWS02".equals(dadesConsultaPinbal.getServeiCodi()) && dadesConsultaPinbal.getTitular()!=null && dadesConsultaPinbal.getTitular().getNombreCompleto()!=null) {
			scspTitular.setNombreCompleto(dadesConsultaPinbal.getTitular().getNombreCompleto());
			solicitud.setTitular(scspTitular);
		}

		if (ENABLE_LOGGING) {
			clientGeneric.enableLogginFilter();
		}
		
		try {
			if(dadesConsultaPinbal.isAsincrona()){
				ScspConfirmacionPeticion confirmacioPeticio = clientGeneric.peticionAsincrona(dadesConsultaPinbal.getServeiCodi(), Arrays.asList(solicitud));
				assertNotNull(confirmacioPeticio);	
				logger.debug("-> peticioAsincrona = " + objectToJsonString(confirmacioPeticio));
				ScspConfirmacioPeticioPinbal respostaAsincrona = new ScspConfirmacioPeticioPinbal();
				if (confirmacioPeticio!=null) {
					respostaAsincrona = convertirFromPinbalAsincrona(confirmacioPeticio);
				}
				return respostaAsincrona;
			}else {
				ScspRespuesta respuesta = clientGeneric.peticionSincrona(dadesConsultaPinbal.getServeiCodi(), Arrays.asList(solicitud));
				assertNotNull(respuesta);
				logger.debug("-> peticionSincrona = " + objectToJsonString(respuesta));
				ScspRespostaPinbal resposta= new ScspRespostaPinbal();
				if(respuesta!=null && respuesta.getAtributos()!=null) {
					resposta = convertirFromPinbalIgetJustificant(respuesta, dadesConsultaPinbal.getDocumentCodi());
				}
				return resposta;
			}
		} catch (IOException e) {
			String errMsg = "No s'ha pogut obtenir la solicitud " +
					(dadesConsultaPinbal.isAsincrona() ? "asíncrona " : "síncrona ") + 
					"del servei genèric de PINBAL";
			throw new IOException(
					errMsg,
					e);
		}
	}


	/**Servei de CONSULTA DE DADES D'IDENTITAT**/
	@Override
	public Object peticioClientPinbalSvddgpciws02(DadesConsultaPinbal dadesConsultaPinbal) throws Exception {
		
		SolicitudSvddgpciws02 solicitud = new SolicitudSvddgpciws02();

		if (this.validarDadesObligatories(dadesConsultaPinbal)) {
			solicitud.setFinalidad(dadesConsultaPinbal.getFinalitat());
			solicitud.setConsentimiento(compararConsentiment(dadesConsultaPinbal.getConsentiment()));		
		}
		solicitud.setAnioNacimiento(dadesConsultaPinbal.getAnyNaixement());
		solicitud.setIdentificadorSolicitante(dadesConsultaPinbal.getEntitat_CIF());
		solicitud.setCodigoProcedimiento(dadesConsultaPinbal.getCodiProcediment());
		solicitud.setUnidadTramitadora(dadesConsultaPinbal.getUnitatTramitadora());
		
		ScspFuncionario funcionario = new ScspFuncionario();		
		ScspTitular scspTitular = new ScspTitular();
		
		this.dadesTitularIFuncionari(funcionario, scspTitular, dadesConsultaPinbal);
		solicitud.setFuncionario(funcionario);
		solicitud.setTitular(scspTitular);
		
		/**El nom complert es pot posar en el cas de CONSULTA de dades d'identitat**/
		if(dadesConsultaPinbal.getTitular()!=null && dadesConsultaPinbal.getTitular().getNombreCompleto()!=null) {
			scspTitular.setNombreCompleto(dadesConsultaPinbal.getTitular().getNombreCompleto());
			solicitud.setTitular(scspTitular);
		}

		ClientSvddgpciws02 clientSvddgpciws02 = this.getClientSvddgpciws02();
		
		if (ENABLE_LOGGING) {
			clientSvddgpciws02.enableLogginFilter();
		}

		try {
			if(dadesConsultaPinbal.isAsincrona()) {
				ScspConfirmacionPeticion confirmacioPeticio = clientSvddgpciws02.peticionAsincrona(Arrays.asList(solicitud));
				assertNotNull(confirmacioPeticio);	
				logger.debug("-> peticioAsincrona = " + objectToJsonString(confirmacioPeticio));
				return confirmacioPeticio;
			}else {
				ScspRespuesta respuesta = clientSvddgpciws02.peticionSincrona(Arrays.asList(solicitud));
				assertNotNull(respuesta);
				logger.debug("-> peticionSincrona = " + objectToJsonString(respuesta));
				ScspRespostaPinbal resposta= new ScspRespostaPinbal();
				if(respuesta!=null && respuesta.getAtributos()!=null) {
					resposta = convertirFromPinbalIgetJustificant(respuesta, dadesConsultaPinbal.getDocumentCodi());
				}
				return resposta;
			}
		
		} catch (IOException e) {
			String errMsg = "No s'ha pogut obtenir la solicitud " +
					(dadesConsultaPinbal.isAsincrona() ? "asíncrona " : "síncrona ") + 
					"del servei específic SVDDGPCIWS02 de Consulta de dades d'identitat";
			throw new IOException(
					errMsg,
					e);
		}
		
	}
	
	
	/**Servei de VERIRICACIÓ DE DADES D'IDENTITAT**/
	@Override
	public Object peticioClientPinbalSvddgpviws02(DadesConsultaPinbal dadesConsultaPinbal) throws Exception {
		
		SolicitudSvddgpviws02 solicitud = new SolicitudSvddgpviws02();

		if (this.validarDadesObligatories(dadesConsultaPinbal)) {
			solicitud.setFinalidad(dadesConsultaPinbal.getFinalitat());
			solicitud.setConsentimiento(compararConsentiment(dadesConsultaPinbal.getConsentiment()));		
		}
//		solicitud.setAnioNacimiento(dadesConsultaPinbal.getAnyNaixement());
		solicitud.setIdentificadorSolicitante(dadesConsultaPinbal.getEntitat_CIF());
		solicitud.setCodigoProcedimiento(dadesConsultaPinbal.getCodiProcediment());
		solicitud.setUnidadTramitadora(dadesConsultaPinbal.getUnitatTramitadora());

		ScspFuncionario funcionario = new ScspFuncionario();		
		ScspTitular scspTitular = new ScspTitular();
		
		this.dadesTitularIFuncionari(funcionario, scspTitular, dadesConsultaPinbal);
		solicitud.setFuncionario(funcionario);
		solicitud.setTitular(scspTitular);

		ClientSvddgpviws02 clientSvddgpviws02 = this.getClientSvddgpviws02();
		
		if (ENABLE_LOGGING) {
			clientSvddgpviws02.enableLogginFilter();
		}
		
		try {
			if(dadesConsultaPinbal.isAsincrona()) {
				ScspConfirmacionPeticion confirmacioPeticio = clientSvddgpviws02.peticionAsincrona(Arrays.asList(solicitud));
				assertNotNull(confirmacioPeticio);	
				logger.debug("-> peticioAsincrona = " + objectToJsonString(confirmacioPeticio));
				return confirmacioPeticio;
			} else {
				ScspRespuesta respuesta = clientSvddgpviws02.peticionSincrona(Arrays.asList(solicitud));
				assertNotNull(respuesta);
				logger.debug("-> peticionSincrona = " + objectToJsonString(respuesta));
				ScspRespostaPinbal resposta= new ScspRespostaPinbal();
				if(respuesta!=null && respuesta.getAtributos()!=null) {
					resposta = convertirFromPinbalIgetJustificant(respuesta, dadesConsultaPinbal.getDocumentCodi());
				}
				return resposta;
			}
		} catch (IOException e) {
			String errMsg = "No s'ha pogut obtenir la solicitud " +
					(dadesConsultaPinbal.isAsincrona() ? "asíncrona " : "síncrona ") + 
					"del servei específic SVDDGPVIWS02 de Verificació de dades d'identitat";
			throw new IOException(
					errMsg,
					e);
		}
		
	}
	
	
	/**Servei d'OBTENCIÓ DE DADES TRIBUTÀRIES**/
	@Override
	public Object peticioClientPinbalSvdccaacpasws01(DadesConsultaPinbal dadesConsultaPinbal)
			throws Exception {
		SolicitudSvdccaacpasws01 solicitud = new SolicitudSvdccaacpasws01();

		if (this.validarDadesObligatories(dadesConsultaPinbal)) {
			solicitud.setFinalidad(dadesConsultaPinbal.getFinalitat());
			solicitud.setConsentimiento(compararConsentiment(dadesConsultaPinbal.getConsentiment()));		
		}
		
		solicitud.setIdentificadorSolicitante(dadesConsultaPinbal.getEntitat_CIF());
		solicitud.setCodigoProcedimiento(dadesConsultaPinbal.getCodiProcediment());
		solicitud.setUnidadTramitadora(dadesConsultaPinbal.getUnitatTramitadora());

		ScspFuncionario funcionario = new ScspFuncionario();		
		ScspTitular scspTitular = new ScspTitular();
		
		this.dadesTitularIFuncionari(funcionario, scspTitular, dadesConsultaPinbal);
		solicitud.setFuncionario(funcionario);
		solicitud.setTitular(scspTitular);
		/**El nom complert es pot posar en el cas d'OBTENCIÓ DE DADES TRIBUTÀRIES**/
		if(dadesConsultaPinbal.getTitular()!=null && dadesConsultaPinbal.getTitular().getNombreCompleto()!=null) {
			scspTitular.setNombreCompleto(dadesConsultaPinbal.getTitular().getNombreCompleto());
			solicitud.setTitular(scspTitular);
		}
		ClientSvdccaacpasws01 clientSvdccaacpasws01 = this.getClientSvdccaacpasws01();
		
		if (ENABLE_LOGGING) {
			clientSvdccaacpasws01.enableLogginFilter();
		}

		try {
			if(dadesConsultaPinbal.isAsincrona()) {
				ScspConfirmacionPeticion confirmacioPeticio = clientSvdccaacpasws01.peticionAsincrona(Arrays.asList(solicitud));
				assertNotNull(confirmacioPeticio);	
				logger.debug("-> peticioAsincrona = " + objectToJsonString(confirmacioPeticio));
				return confirmacioPeticio;
			} else {
				ScspRespuesta respuesta = clientSvdccaacpasws01.peticionSincrona(Arrays.asList(solicitud));
				assertNotNull(respuesta);
				logger.debug("-> peticionSincrona = " + objectToJsonString(respuesta));
				ScspRespostaPinbal resposta= new ScspRespostaPinbal();
				if(respuesta!=null && respuesta.getAtributos()!=null) {
					resposta = convertirFromPinbalIgetJustificant(respuesta, dadesConsultaPinbal.getDocumentCodi());
				}
				return resposta;
			}
		} catch (IOException e) {
			String errMsg = "No s'ha pogut obtenir la solicitud " +
					(dadesConsultaPinbal.isAsincrona() ? "asíncrona " : "síncrona ") + 
					"del servei específic SVDCCAACPASWS01 de Consulta de dades tributàries";
			throw new IOException(
					errMsg,
					e);
		}
	}

	

	@Override
	public ScspRespostaPinbal getRespuestaPinbal(String peticioId) throws Exception {
		
		ClientGeneric clientGeneric=this.getClientGeneric();
		clientGeneric.enableLogginFilter();
		ScspRespuesta respuesta = clientGeneric.getRespuesta(peticioId);
		
		if (respuesta==null) throw new Exception("La resposta clientGeneric.getRespuesta ha resultat nula.");
		
		ScspRespostaPinbal resultat = new ScspRespostaPinbal();
		if ("0003".equals(respuesta.getAtributos().getEstado().getCodigoEstado())) {
			resultat.setEstatAsincron(PeticioPinbalEstatEnum.TRAMITADA);
			resultat.setJustificant(getJustificantPinbal(peticioId, null));
		} else {
			resultat.setEstatAsincron(PeticioPinbalEstatEnum.PENDENT);
		}

		logger.debug("-> getRespuesta(" + peticioId + ") = " + objectToJsonString(respuesta));
		return resultat;
	}
	
	@Override
	public Object getJustificantPinbal(String peticioId) throws Exception  {
		ClientGeneric clientGeneric=this.getClientGeneric();
		clientGeneric.enableLogginFilter();
		ScspJustificante justificante;
		try {
			justificante = clientGeneric.getJustificante(peticioId);
			assertNotNull(justificante);
		} catch (IOException e) {
			throw new IOException(
					"No s'ha pogut obtenir el justificant (" +
					"petició=" + peticioId + ")",
					e);
		}
		logger.debug("-> getJustificante");
		logger.debug("\tnom: " + justificante.getNom());
		logger.debug("\tcontentType: " + justificante.getContentType());
		logger.debug("\tcontingut: " + justificante.getContingut());
		return justificante;
		
	}
	
	/**Obté les dades del justificant Pinbal i les converteix a Helium**/
	public ScspJustificant getJustificantPinbal(String idPeticio, String documentCodi) throws Exception {
		
		ScspJustificante justificante = new ScspJustificante();
		ScspJustificant justificant = new ScspJustificant();
		try {
			justificante = (ScspJustificante) this.getJustificantPinbal(idPeticio);
		} catch (IOException e) {
			throw new IOException(
					"No s'ha pogut obtenir el justificant de la solicitud: " + idPeticio,
					e);
		}
		justificant.setContentType(justificante.getContentType());
		justificant.setContingut(justificante.getContingut());
		justificant.setIdPeticion(idPeticio);
		justificant.setNom(justificante.getNom());
		justificant.setDocumentCodi(documentCodi);
		return justificant;
	
}
	private ClientGeneric getClientGeneric() {
		return new ClientGeneric(getUrl(),  getUsername(), getPassword(), isJBoss(), null, null);
	}
	
	private ClientSvddgpciws02 getClientSvddgpciws02() {
		return new ClientSvddgpciws02(getUrl(), getUsername(), getPassword(), isJBoss(), null, null);
	}
	
	private ClientSvddgpviws02 getClientSvddgpviws02() {
		return new ClientSvddgpviws02(getUrl(), getUsername(), getPassword(), isJBoss(), null, null);
	}
	
	private ClientSvdccaacpasws01 getClientSvdccaacpasws01() {
		return new ClientSvdccaacpasws01(getUrl(), getUsername(), getPassword(), isJBoss(), null, null);
	}
	
	
	private String objectToJsonString(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper.writeValueAsString(obj);
	}
	
	private String getUrl() {
		return GlobalProperties.getInstance().getProperty("app.pinbal.plugin.url");
	}
	private String getUsername() {
		return GlobalProperties.getInstance().getProperty("app.pinbal.plugin.username");
	}
	private String getPassword() {
		return GlobalProperties.getInstance().getProperty("app.pinbal.plugin.password");		
	}
	private boolean isJBoss() {
		if("false".equals(GlobalProperties.getInstance().getProperty("app.pinbal.plugin.isJBoss")))
			return false;
		else 
			return true;
	}

	/**Compara els Tipus de Documentació amb els de Pinbal**/
	private ScspTipoDocumentacion compararTipusDocumentacio(String tipusDocumentacio) {
		for (ScspTitular.ScspTipoDocumentacion tipo : ScspTitular.ScspTipoDocumentacion.values()) { 
		    if(tipo.toString().equals(tipusDocumentacio)){
		    	return tipo;
		    }
		}
		return null;
	}
	
	/**Compara les dades de Consentiment amb les dades de consentiment de Pinbal**/
	private ScspConsentimiento compararConsentiment(String consentiment) {
			if(consentiment.equals(DadesConsultaPinbal.consentiment.SI.toString()))
				return ScspConsentimiento.Si;
			else if (consentiment.equals(DadesConsultaPinbal.consentiment.LLEI.toString()))
				return ScspConsentimiento.Ley;
		return null;
	}

	
	/**Valida les dades obligatòries per fer la consulta a Pinbal**/
	private boolean validarDadesObligatories(DadesConsultaPinbal dadesConsultaPinbal) throws IOException {
		if (dadesConsultaPinbal.getDocumentCodi()==null) {
			logger.error("Error al obtenir la petició, el codi del document és obligatori.");
			throw new IOException(
					"Error al obtenir la petició, el codi del document és obligatori.");
		}	
		if (dadesConsultaPinbal.getFinalitat()==null) {
			logger.error("Error al obtenir la petició, la finalitat és obligatòria.");
			throw new IOException(
					"Error al obtenir la petició, la finalitat és obligatòria.");
		}	
		if (dadesConsultaPinbal.getConsentiment()==null) {
			logger.error("Error al obtenir la petició, el consentiment és obligatori.");
			throw new IOException(
					"Error al obtenir la petició, el consentiment és obligatori.");
		}
		if(dadesConsultaPinbal.getTitular()==null && dadesConsultaPinbal.getInteressatCodi()==null) {
			logger.error("Error al obtenir la petició, les dades del Titular o el codi de l'Interessat són obligatòries.");
			throw new IOException(
					"Error al obtenir la petició, les dades del Titular són obligatòries.");
		} else if (dadesConsultaPinbal.getTitular().getTipusDocumentacion()==null) {
			logger.error("Error al obtenir la petició, el Tipus de documentació del Titular és obligatori.");
			throw new IOException(
					"Error al obtenir la petició, el Tipus de documentació del Titular és obligatori.");
			}
		return true;
	}
	
	/**Converteix les dades de titular i funcionari a les requerides per Pinbal**/
	private void dadesTitularIFuncionari(ScspFuncionario funcionario, ScspTitular scspTitular, DadesConsultaPinbal dadesConsultaPinbal) {	
		if(dadesConsultaPinbal.getFuncionari()!=null) {
			funcionario.setNifFuncionario(dadesConsultaPinbal.getFuncionari().getNifFuncionario());
			String nomCompletFuncionari = dadesConsultaPinbal.getFuncionari().getNombreCompletoFuncionario();
			funcionario.setNombreCompletoFuncionario(nomCompletFuncionari!=null && !nomCompletFuncionari.isEmpty()  ? dadesConsultaPinbal.getFuncionari().getNombreCompletoFuncionario() : dadesConsultaPinbal.getFuncionari().getSeudonimo());
			funcionario.setSeudonimo(dadesConsultaPinbal.getFuncionari().getSeudonimo());	
		} 
		//les dades del titular ja han estat validades anteriorment
		scspTitular.setTipoDocumentacion(compararTipusDocumentacio(dadesConsultaPinbal.getTitular().getTipusDocumentacion()));
		scspTitular.setDocumentacion(dadesConsultaPinbal.getTitular().getDocumentacion());
		scspTitular.setApellido1(dadesConsultaPinbal.getTitular().getApellido1());
		scspTitular.setApellido2(dadesConsultaPinbal.getTitular().getApellido2());
		/**El nombreCompleto no funciona per tots els serveis, per això ho deixarem segons el servei i les dades que introdueixi l'usuari**/
		//scspTitular.setNombreCompleto(dadesConsultaPinbal.getTitular().getNombreCompleto());
		scspTitular.setNombre(dadesConsultaPinbal.getTitular().getNombre());		
	}
	
	/**Retorna la resposta de pinbal amb el justificant en format Helium**/
	private ScspRespostaPinbal convertirFromPinbalIgetJustificant(ScspRespuesta scspRespuesta, String documentCodi) throws Exception {
		ScspRespostaPinbal resposta = new ScspRespostaPinbal();
		resposta.setIdPeticion(scspRespuesta.getAtributos().getIdPeticion());
		resposta.setJustificant(this.getJustificantPinbal(resposta.getIdPeticion(), documentCodi));
		//resposta.setEstat(scspRespuesta.getAtributos().getEstado().toString());
		return resposta;	
	}

	private ScspConfirmacioPeticioPinbal convertirFromPinbalAsincrona(ScspConfirmacionPeticion scspRespuesta) throws Exception {
		ScspConfirmacioPeticioPinbal resposta = new ScspConfirmacioPeticioPinbal();
		ScspAtributos atributos = new ScspAtributos();
		if (scspRespuesta.getAtributos()!=null) {
			atributos.setIdPeticion(scspRespuesta.getAtributos().getIdPeticion());
			atributos.setCodigoCertificado(scspRespuesta.getAtributos().getCodigoCertificado());
			atributos.setNumElementos(scspRespuesta.getAtributos().getNumElementos());
			atributos.setTimeStamp(scspRespuesta.getAtributos().getTimeStamp());
			ScspEstado estado = new ScspEstado();
			if (scspRespuesta.getAtributos().getEstado()!=null) {
				estado.setTiempoEstimadoRespuesta(scspRespuesta.getAtributos().getEstado().getTiempoEstimadoRespuesta());
				estado.setCodigoEstado(scspRespuesta.getAtributos().getEstado().getCodigoEstado());
				estado.setLiteralError(scspRespuesta.getAtributos().getEstado().getLiteralError());
				estado.setCodigoEstadoSecundario(scspRespuesta.getAtributos().getEstado().getCodigoEstadoSecundario());
				estado.setLiteralErrorSec(scspRespuesta.getAtributos().getEstado().getLiteralErrorSec());
			}
			atributos.setEstado(estado);
		}
		resposta.setAtributos(atributos);
		return resposta;
	}


}
