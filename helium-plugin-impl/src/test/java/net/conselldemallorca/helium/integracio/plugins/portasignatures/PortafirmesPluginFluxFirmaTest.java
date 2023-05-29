/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.ApiFlowTemplateSimple;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleFlowTemplateList;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleGetTransactionIdRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleKeyValue;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleStartTransactionRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleViewFlowTemplateRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.jersey.ApiFlowTemplateSimpleJersey;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.conselldemallorca.helium.core.helper.PluginHelper;
import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;
import net.conselldemallorca.helium.integracio.plugins.firmaweb.FirmaWebPluginPortafibRest;


/**
 * Test de la implementació de l'API de l'arxiu que utilitza
 * l'API REST de l'arxiu de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortafirmesPluginFluxFirmaTest {

	@Autowired
	PluginHelper pluginHelper;
	
	private static final String BASE_URL = "https://dev.caib.es/portafib/common/rest/apiflowtemplatesimple/v1/";//"https://dev.caib.es/portafib";
	private static final String USERNAME = "$ripea_portafib";
	private static final String PASSWORD = "ripea_portafib";

	private static final String classe = "org.fundaciobit.apisib.apifirmasimple.v1.ApiFlowTemplateSimple";
	private static final String descripcioCurta ="Plugin de flux de firma";
	private static final String name ="Prova des de API REST - " + System.currentTimeMillis();
	private static final String idioma = "ca";

	@BeforeClass
	public static void setUp() throws IOException {
		System.setProperty(
				"app.portasignatures.plugin.portafib.base.url",
				BASE_URL);
		System.setProperty(
				"app.portasignatures.plugin.portafib.username",
				USERNAME);
		System.setProperty(
				"app.portasignatures.plugin.portafib.password",
				PASSWORD);
		System.setProperty(
				"app.portasignatures.plugin.portafib.perfil",
				"");
		
	}
	PortafirmesPluginPortafibFluxSimple plugin;
	

//	@Test
//	public void iniciarFluxDeFirma() throws SistemaExternException {
//		ApiFlowTemplateSimple api;
//		api = new ApiFlowTemplateSimpleJersey(BASE_URL, USERNAME,PASSWORD);
//		try {
//			api.getAvailableLanguages(BASE_URL);
//			String languageUI = "ca";
//		    boolean saveOnServer = true;
//		   
//		    final boolean visibleDescription = false;
//
//		    FlowTemplateSimpleGetTransactionIdRequest transactionRequest;
//		    transactionRequest = new FlowTemplateSimpleGetTransactionIdRequest(languageUI,
//		    					saveOnServer, name, descripcioCurta, visibleDescription);
//		      // Enviam informació bàsica
//		      String transactionID = api.getTransactionID(transactionRequest);
//
//		      System.out.println("TransactionID = |" + transactionID + "|");
////			  String url = "http://10.35.3.231:8080/helium/v3/expedientTipus/20050";
//			  int port = 1989 + (int) (Math.random() * 100.0);
//			  final String returnUrl = "http://localhost:" + port + "/returnurl/" + transactionID;
//		      String urlRedireccio = startTransaction(
//		    		  api,
//		    		  transactionID,
//		    		  returnUrl);
//		      
//		      PortafirmesIniciFluxResposta transaccioResponse = new PortafirmesIniciFluxResposta();
//				transaccioResponse.setIdTransaccio(transactionID);
//				transaccioResponse.setUrlRedireccio(urlRedireccio);
//				System.out.println("returnUrl = " + returnUrl + "");
//				System.out.println("-> iniciarFluxDeFirma = " + objectToJsonString(transaccioResponse));
//
//		} catch (Exception ex) {
//			System.out.println("-> iniciarFluxDeFirma = " + ex.getCause());
//			
//		}
//	}
	
	
	@Test
	public void recuperarPlantillesDisponibles() throws SistemaExternException {
		List<PortafirmesFluxResposta> plantilles = new ArrayList<PortafirmesFluxResposta>();
		try {
			FlowTemplateSimpleFlowTemplateList resposta = getFluxDeFirmaClient().getAllFlowTemplates(idioma);
			System.out.println("resposta = " + resposta);
			for (FlowTemplateSimpleKeyValue flowTemplate : resposta.getList()) {
				PortafirmesFluxResposta plantilla = new PortafirmesFluxResposta();
				plantilla.setFluxId(flowTemplate.getKey());
				plantilla.setNom(flowTemplate.getValue());
				plantilles.add(plantilla);
				System.out.println("plantilla = " + flowTemplate.getKey() +"_"+ flowTemplate.getValue());
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut recuperar les plantilles per l'usuari aplicació actual",
					ex);
		}
		
	}
	
	
	private String startTransaction(
			ApiFlowTemplateSimple api, String idTransaccio,
			String urlReturn) throws SistemaExternException {
		String urlRedireccio = null;
		try {
			FlowTemplateSimpleStartTransactionRequest transactionRequest = new FlowTemplateSimpleStartTransactionRequest(
					idTransaccio, 
					urlReturn);

			urlRedireccio =api.startTransaction(transactionRequest);// getFluxDeFirmaClient().startTransaction(transactionRequest);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut iniciar la transacció (" +
					"portafib=" + BASE_URL + ", " +			
					"transactionId=" + idTransaccio + ", " +
					"returnUrl=" + urlReturn + ")",
					ex);
		}
		return urlRedireccio;
	}
	
	
	private String getTransaction(
			String idioma,
			boolean isPlantilla,
			String nom,
			String descripcio,
			boolean descripcioVisible) throws SistemaExternException {
		String transactionId = null;
		try {
			FlowTemplateSimpleGetTransactionIdRequest transactionRequest = new FlowTemplateSimpleGetTransactionIdRequest(
					idioma,
					isPlantilla,
					nom,
					descripcio,
					descripcioVisible);

			transactionId = getFluxDeFirmaClient().getTransactionID(transactionRequest);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut recuperar el id de la transacció (" +
					"portafib=" + BASE_URL + ", " +					
					"nom=" + nom + ", " +
					"descripcio=" + descripcio + ")",
					ex);
		}
		return transactionId;
	}
	
	private ApiFlowTemplateSimple getFluxDeFirmaClient() throws MalformedURLException {
		String apiRestUrl = BASE_URL; // "/common/rest/apiflowtemplatesimple/v1";
		ApiFlowTemplateSimple api = new ApiFlowTemplateSimpleJersey(
				apiRestUrl,
				USERNAME,
				PASSWORD);
		return api;
	}
	
	
	
	
	private String objectToJsonString(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper.writeValueAsString(obj);
	}

}
