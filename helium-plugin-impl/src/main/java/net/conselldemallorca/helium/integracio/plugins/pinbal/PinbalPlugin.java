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
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

import es.caib.pinbal.client.recobriment.ClientGeneric;
import es.caib.pinbal.client.recobriment.model.ScspFuncionario;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.ScspTitular;
import es.caib.pinbal.client.recobriment.model.ScspSolicitante.ScspConsentimiento;
import es.caib.pinbal.client.recobriment.model.ScspTitular.ScspTipoDocumentacion;
import es.caib.pinbal.client.recobriment.model.Solicitud;
import es.caib.pinbal.client.recobriment.svdccaacpasws01.ClientSvdccaacpasws01;
import es.caib.pinbal.client.recobriment.svdccaacpasws01.ClientSvdccaacpasws01.SolicitudSvdccaacpasws01;
import es.caib.pinbal.client.recobriment.svddgpciws02.ClientSvddgpciws02;
import es.caib.pinbal.client.recobriment.svddgpciws02.ClientSvddgpciws02.SolicitudSvddgpciws02;
import es.caib.pinbal.client.recobriment.svddgpviws02.ClientSvddgpviws02;
import es.caib.pinbal.client.recobriment.svddgpviws02.ClientSvddgpviws02.SolicitudSvddgpviws02;
import net.conselldemallorca.helium.core.util.GlobalProperties;
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

	private ClientGeneric clientGeneric;
	private ClientSvddgpciws02 clientSvddgpciws02;
	private ClientSvddgpviws02 clientSvddgpviws02;
	private ClientSvdccaacpasws01 clientSvdccaacpasws01;
	
	public PinbalPlugin() {	
		}
	
	@Override
	public ScspRespostaPinbal peticionSincronaClientPinbalGeneric(DadesConsultaPinbal dadesConsultaPinbal) throws UniformInterfaceException, ClientHandlerException, IOException {
		
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
		clientGeneric = this.getClientGeneric();

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
		ScspRespuesta respuesta;
	
		try {
			respuesta = clientGeneric.peticionSincrona(dadesConsultaPinbal.getServeiCodi(), Arrays.asList(solicitud));
			assertNotNull(respuesta);
		} catch (IOException e) {
			throw new IOException(
					"No s'ha pogut obtenir la solicitud síncrona del servei genèric de PINBAL",
					e);
		}
		logger.debug("-> peticionSincrona = " + objectToJsonString(respuesta));
	
		ScspRespostaPinbal resposta= new ScspRespostaPinbal();
		
		if(respuesta!=null && respuesta.getAtributos()!=null) {
			resposta = convertirFromPinbalIgetJustificant(respuesta, dadesConsultaPinbal.getDocumentCodi());
		}
			
		return resposta;
	}
	

	/**Servei de CONSULTA DE DADES D'IDENTITAT**/
	@Override
	public Object peticionSincronaClientPinbalSvddgpciws02(DadesConsultaPinbal dadesConsultaPinbal) throws UniformInterfaceException, ClientHandlerException, IOException {
		
		SolicitudSvddgpciws02 solicitud = new SolicitudSvddgpciws02();

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
		
		/**El nom complert es pot posar en el cas de CONSULTA de dades d'identitat**/
		if(dadesConsultaPinbal.getTitular()!=null && dadesConsultaPinbal.getTitular().getNombreCompleto()!=null) {
			scspTitular.setNombreCompleto(dadesConsultaPinbal.getTitular().getNombreCompleto());
			solicitud.setTitular(scspTitular);
		}

		clientSvddgpciws02 = this.getClientSvddgpciws02();
		
		if (ENABLE_LOGGING) {
			clientSvddgpciws02.enableLogginFilter();
		}

		ScspRespuesta respuesta;
		
		try {
			respuesta = clientSvddgpciws02.peticionSincrona(Arrays.asList(solicitud));
			assertNotNull(respuesta);
		} catch (IOException e) {
			throw new IOException(
					"No s'ha pogut obtenir la solicitud síncrona del servei específic SVDDGPCIWS02 de Consulta de dades d'identitat",
					e);
		}
		logger.debug("-> peticionSincrona = " + objectToJsonString(respuesta));
		
		ScspRespostaPinbal resposta= new ScspRespostaPinbal();
		
		if(respuesta!=null && respuesta.getAtributos()!=null) {
			resposta = convertirFromPinbalIgetJustificant(respuesta, dadesConsultaPinbal.getDocumentCodi());
		}
		return resposta;
	}
	
	
	/**Servei de VERIRICACIÓ DE DADES D'IDENTITAT**/
	@Override
	public Object peticionSincronaClientPinbalSvddgpviws02(DadesConsultaPinbal dadesConsultaPinbal)
			throws UniformInterfaceException, ClientHandlerException, IOException {
		SolicitudSvddgpviws02 solicitud = new SolicitudSvddgpviws02();

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

		clientSvddgpviws02 = this.getClientSvddgpviws02();
		
		if (ENABLE_LOGGING) {
			clientSvddgpviws02.enableLogginFilter();
		}

		ScspRespuesta respuesta;
		
		try {
			respuesta = clientSvddgpviws02.peticionSincrona(Arrays.asList(solicitud));
			assertNotNull(respuesta);
		} catch (IOException e) {
			throw new IOException(
					"No s'ha pogut obtenir la solicitud síncrona del servei específic SVDDGPCIWS02 de Verificació de dades d'identitat",
					e);
		}
		logger.debug("-> peticionSincrona = " + objectToJsonString(respuesta));
		
		ScspRespostaPinbal resposta= new ScspRespostaPinbal();
		
		if(respuesta!=null && respuesta.getAtributos()!=null) {
			resposta = convertirFromPinbalIgetJustificant(respuesta, dadesConsultaPinbal.getDocumentCodi());
		}
		return resposta;
	}
	
	
	/**Servei d'OBTENCIÓ DE DADES TRIBUTÀRIES**/
	@Override
	public Object peticionSincronaClientPinbalSvdccaacpasws01(DadesConsultaPinbal dadesConsultaPinbal)
			throws UniformInterfaceException, ClientHandlerException, IOException {
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
		this.clientSvdccaacpasws01 = this.getClientSvdccaacpasws01();
		
		if (ENABLE_LOGGING) {
			clientSvdccaacpasws01.enableLogginFilter();
		}

		ScspRespuesta respuesta;
		
		try {
			respuesta = clientSvdccaacpasws01.peticionSincrona(Arrays.asList(solicitud));
			assertNotNull(respuesta);
		} catch (IOException e) {
			throw new IOException(
					"No s'ha pogut obtenir la solicitud síncrona del servei específic SVDCCAACPASWS01 de Consulta de dades tributàries",
					e);
		}
		logger.debug("-> peticionSincrona = " + objectToJsonString(respuesta));
		
		ScspRespostaPinbal resposta= new ScspRespostaPinbal();
		
		if(respuesta!=null && respuesta.getAtributos()!=null) {
			resposta = convertirFromPinbalIgetJustificant(respuesta, dadesConsultaPinbal.getDocumentCodi());
		}
		return resposta;
	}

	

	@Override
	public Object getRespuestaPinbal(String peticioId) throws IOException {
		clientGeneric=this.getClientGeneric();
		clientGeneric.enableLogginFilter();
		ScspRespuesta respuesta;
		try {
			respuesta = clientGeneric.getRespuesta(peticioId);
			assertNotNull(respuesta);
		} catch (IOException e) {
			throw new IOException(
					"No s'ha pogut obtenir la resposta (" +
					"petició=" + peticioId + ")",
					e);
		}
		logger.debug("-> getRespuesta(" + peticioId + ") = " + objectToJsonString(respuesta));
		return respuesta;
	}
	
	@Override
	public Object getJustificantPinbal(String peticioId) throws IOException  {
		clientGeneric=this.getClientGeneric();
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
	public ScspJustificant getJustificantPinbal(String idPeticio, String documentCodi) throws IOException {
		
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
		if (clientGeneric == null) {
			clientGeneric =new ClientGeneric(getUrl(),  getUsername(), getPassword(), isJBoss(), null, null);
		}
		return clientGeneric;
	}
	
	private ClientSvddgpciws02 getClientSvddgpciws02() {
		if (clientSvddgpciws02 == null) {
			clientSvddgpciws02 =new ClientSvddgpciws02(getUrl(), getUsername(), getPassword(), isJBoss(), null, null);
		}
		return clientSvddgpciws02;
	}
	
	private ClientSvddgpviws02 getClientSvddgpviws02() {
		if (clientSvddgpviws02 == null) {
			clientSvddgpciws02 =new ClientSvddgpciws02(getUrl(), getUsername(), getPassword(), isJBoss(), null, null);
		}
		return clientSvddgpviws02;
	}
	
	private ClientSvdccaacpasws01 getClientSvdccaacpasws01() {
		if (clientSvdccaacpasws01 == null) {
			clientSvdccaacpasws01 =new ClientSvdccaacpasws01(getUrl(), getUsername(), getPassword(), isJBoss(), null, null);
		}
		return clientSvdccaacpasws01;
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
	private ScspRespostaPinbal convertirFromPinbalIgetJustificant(ScspRespuesta scspRespuesta, String documentCodi) throws IOException {
		ScspRespostaPinbal resposta = new ScspRespostaPinbal();
		resposta.setIdPeticion(scspRespuesta.getAtributos().getIdPeticion());
		resposta.setJustificant(this.getJustificantPinbal(resposta.getIdPeticion(), documentCodi));
		resposta.setEstat(scspRespuesta.getAtributos().getEstado().toString());
		return resposta;	
	}


}
