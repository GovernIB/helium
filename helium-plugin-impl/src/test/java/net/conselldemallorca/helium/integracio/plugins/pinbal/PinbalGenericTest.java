/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.pinbal;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import net.conselldemallorca.helium.v3.core.api.dto.ScspRespostaPinbal;

/**
 * Test del client genèric del recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PinbalGenericTest {

	private static final String URL_BASE = "https://proves.caib.es/pinbalapi";
	private static final String USUARI = "$ripea_pinbal";
	private static final String CONTRASENYA = "ripea_pinbal";
//	private static final String URL_BASE = "http://localhost:8080/pinbal";
//	private static final String USUARI = "user";
//	private static final String CONTRASENYA = "passwd";
	private static final String SERVEI_SCSP = "SVDDELSEXCDIWS01";
	private static final String ENTITAT_CIF = "S0711001H";
	private static final String CODIGO_PROCEDIMIENTO = "CODSVDR_GBA_20121107";
//	private static final String PETICION_SCSP_ID = "PBL0000000001292";
	private static final String PETICION_SCSP_ID = "PINBAL00000000000000263447";
	//private static final String PETICION_SCSP_ID = "PINBAL00000000000000263714";
	private static final boolean ENABLE_LOGGING = true;
	private static final boolean IS_JBOSS = true;

//	private ClientGeneric client = new ClientGeneric(URL_BASE, USUARI, CONTRASENYA);
	
	PinbalPlugin pinbalPlugin = new PinbalPlugin();

	@Before
	public void setUp() throws Exception {
		System.setProperty("app.pinbal.plugin.url", "https://proves.caib.es/pinbalapi");
		System.setProperty("app.pinbal.plugin.username", "$ripea_pinbal");
		System.setProperty("app.pinbal.plugin.password", "ripea_pinbal");
		System.setProperty("app.pinbal.plugin.isJBoss", "true");
	}

	@Test
	public void peticionSincrona() throws Exception {
	
		
		Titular titular = new Titular (
				"12345678Z",
				Titular.ScspTipusDocumentacion.DNI.toString(),
				"Antoni Garau Jaume",
				"Antoni",
				"Garau",
				"Jaume");
		
		Funcionari funcionari = new Funcionari(
				"Funcionari CAIB",
				"00000000T",
				"pseudònim funcionari");
		
		String xmlDadesEspecifiques="<?xml version=\"1.0\" encoding=\"UTF-8\"?><DatosEspecificos><Consulta/></DatosEspecificos>";
		
		DadesConsultaPinbal dadesConsulta = new DadesConsultaPinbal(
				titular,
				funcionari,
				xmlDadesEspecifiques,
				SERVEI_SCSP,
				"psigna_doc",
				"finalitat proves Test",
				"SI", 
				null,
				CODIGO_PROCEDIMIENTO, 
				ENTITAT_CIF,
				null,
				null); // Any naixement
		
		ScspRespostaPinbal respuestaPlugin = pinbalPlugin.peticionSincronaClientPinbalGeneric(dadesConsulta);
		assertNotNull(respuestaPlugin);
		System.out.println("-> peticionSincrona(" + SERVEI_SCSP + ") = " + objectToJsonString(respuestaPlugin));
	}

//	@Test
//	public void getRespuesta() throws IOException {
//		ScspRespuesta respuesta = client.getRespuesta(PETICION_SCSP_ID);
//		assertNotNull(respuesta);
//		System.out.println("-> getRespuesta(" + PETICION_SCSP_ID + ") = " + objectToJsonString(respuesta));
//	}

//	@Test
	public void getJustificante() throws Exception {
		ScspJustificante justificante = (ScspJustificante) pinbalPlugin.getJustificantPinbal(PETICION_SCSP_ID);
//		ScspJustificante justificante = client.getJustificante(PETICION_SCSP_ID);
		assertNotNull(justificante);
		System.out.println("-> getJustificante");
		System.out.println("\tnom: " + justificante.getNom());
		System.out.println("\tcontentType: " + justificante.getContentType());
		System.out.println("\tcontingut: " + justificante.getContingut());
	}

	private String objectToJsonString(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		//mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		//mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setSerializationInclusion(Include.NON_NULL);
		return mapper.writeValueAsString(obj);
	}

}
