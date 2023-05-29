package net.conselldemallorca.helium.integracio.plugins.portasignatures;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.fundaciobit.apisib.apifirmaasyncsimple.v2.ApiFirmaAsyncSimple;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleAnnex;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleDocumentTypeInformation;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleFile;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignature;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureBlock;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureRequestInfo;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignatureRequestWithSignBlockList;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSignedFile;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.beans.FirmaAsyncSimpleSigner;
import org.fundaciobit.apisib.apifirmaasyncsimple.v2.jersey.ApiFirmaAsyncSimpleJersey;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.ApiFlowTemplateSimple;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleEditFlowTemplateRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleFlowTemplate;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleFlowTemplateList;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleFlowTemplateRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleGetTransactionIdRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleKeyValue;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleStartTransactionRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.beans.FlowTemplateSimpleViewFlowTemplateRequest;
import org.fundaciobit.apisib.apiflowtemplatesimple.v1.jersey.ApiFlowTemplateSimpleJersey;

import es.caib.portafib.ws.api.v1.CarrecWs;
import es.caib.portafib.ws.api.v1.UsuariPersonaBean;
import es.caib.portafib.ws.api.v1.WsI18NException;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.core.util.OpenOfficeUtils;
import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;

/**
 * Implementació del plugin de portasignatures per l'API REST Simple del PortaFIB.
 * Data: 31/01/2022
 * Després de la implementació per passarela WS SOAP es crea el plugin
 * per a les peticions per API REST i callback per API REST.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PortafirmesPluginPortafibFluxSimple implements PortafirmesPluginFluxSimple {


	@Override
	public PortafirmesIniciFluxResposta iniciarFluxDeFirma(String idioma, boolean isPlantilla, String nom,
			String descripcio, boolean descripcioVisible, String urlReturn) throws SistemaExternException {
		PortafirmesIniciFluxResposta transaccioResponse = new PortafirmesIniciFluxResposta();
		try {
			String idTransaccio = getTransaction(
					idioma,
					isPlantilla,
					nom,
					descripcio,
					descripcioVisible);

			String urlRedireccio = startTransaction(
					idTransaccio,
					urlReturn + idTransaccio);
			transaccioResponse.setIdTransaccio(idTransaccio);
			transaccioResponse.setUrlRedireccio(urlRedireccio);

		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error iniciant la transacció: " + ex.getCause(),
					ex);
		}

		return transaccioResponse;
	}
	
	private String startTransaction(
			String idTransaccio,
			String urlReturn) throws SistemaExternException {
		String urlRedireccio = null;
		try {
			FlowTemplateSimpleStartTransactionRequest transactionRequest = new FlowTemplateSimpleStartTransactionRequest(
					idTransaccio,
					urlReturn);

			urlRedireccio = getFluxDeFirmaClient().startTransaction(transactionRequest);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut iniciar la transacció (" +
					"portafib=" + getBaseUrl() + ", " +			
					"transactionId=" + idTransaccio + ", " +
					"returnUrl=" + urlReturn + ")",
					ex);
		}
		return urlRedireccio;
	}

	@Override
	public PortafirmesFluxResposta recuperarFluxDeFirmaByIdTransaccio(String idTransaccio)
			throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PortafirmesFluxResposta> recuperarPlantillesDisponibles(String idioma) throws SistemaExternException {
		List<PortafirmesFluxResposta> plantilles = new ArrayList<PortafirmesFluxResposta>();
		try {
			FlowTemplateSimpleFlowTemplateList resposta = getFluxDeFirmaClient().getAllFlowTemplates("ca");
			
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
		return plantilles;
	}

	@Override
	public PortafirmesFluxInfo recuperarFluxDeFirmaByIdPlantilla(String plantillaFluxId, String idioma)
			throws SistemaExternException {
		PortafirmesFluxInfo info = null;
		try {
			FlowTemplateSimpleFlowTemplateRequest request = new FlowTemplateSimpleFlowTemplateRequest(idioma, plantillaFluxId);

			FlowTemplateSimpleFlowTemplate result = getFluxDeFirmaClient().getFlowInfoByFlowTemplateID(request);
			
			if (result != null) {
				info = new PortafirmesFluxInfo();
				info.setNom(result.getName());
				info.setDescripcio(result.getDescription());
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error recuperant el detall del flux de firmes",
					ex);
		}
		return info;
	}

	@Override
	public String recuperarUrlViewEditPlantilla(String idPlantilla, String idioma, String urlReturn, boolean edicio)
			throws SistemaExternException {
		String urlPlantilla;
		try {
			if (!edicio) {
				FlowTemplateSimpleViewFlowTemplateRequest request = new FlowTemplateSimpleViewFlowTemplateRequest(idioma, idPlantilla);
				urlPlantilla = getFluxDeFirmaClient().getUrlToViewFlowTemplate(request);
			} else {
				FlowTemplateSimpleEditFlowTemplateRequest request = new FlowTemplateSimpleEditFlowTemplateRequest(idioma, idPlantilla, urlReturn);
				urlPlantilla = getFluxDeFirmaClient().getUrlToEditFlowTemplate(request);
			}
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'ha pogut recuperar la url per visualitzar el flux de firmes",
					ex);
		}
		return urlPlantilla;
	}

	@Override
	public boolean esborrarPlantillaFirma(String idioma, String plantillaFluxId) throws SistemaExternException {
		boolean esborrat = false;
		try {
			FlowTemplateSimpleFlowTemplateRequest request = new FlowTemplateSimpleFlowTemplateRequest(idioma, plantillaFluxId);
			esborrat = getFluxDeFirmaClient().deleteFlowTemplate(request);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Hi ha hagut un problema esborrant el flux de firmes",
					ex);
		}
		return esborrat;
	}

	@Override
	public void tancarTransaccioFlux(String idTransaccio) throws SistemaExternException {
		try {
			closeTransaction(idTransaccio);
		} catch (Exception ex) {
			throw new SistemaExternException(
					"S'ha produït un error tancant la transacció",
					ex);
		}
	}

	@Override
	public List<PortafirmesCarrec> recuperarCarrecs() throws SistemaExternException {
//		List<PortafirmesCarrec> carrecs = new ArrayList<PortafirmesCarrec>();
//		try {
//			List<CarrecWs> carrecsWs = getUsuariEntitatWs().getCarrecsOfMyEntitat();
//			if (carrecsWs != null) {
//				for (CarrecWs carrecWs : carrecsWs) {
//					PortafirmesCarrec carrec = new PortafirmesCarrec();
//					carrec.setCarrecId(carrecWs.getCarrecID());
//					carrec.setCarrecName(carrecWs.getCarrecName());
//					carrec.setEntitatId(carrecWs.getEntitatID());
//					carrec.setUsuariPersonaId(carrecWs.getUsuariPersonaID());
//					if (carrecMostrarPersona()) {
//						UsuariPersonaBean usuariPersona = getUsuariEntitatWs().getUsuariPersona(carrecWs.getUsuariPersonaID());
//						if (usuariPersona != null) {
//							carrec.setUsuariPersonaNif(usuariPersona.getNif());
//							carrec.setUsuariPersonaEmail(usuariPersona.getEmail());
//							carrec.setUsuariPersonaNom(usuariPersona.getNom());
//						} else {
//							throw new SistemaExternException("No s'ha trobat cap usuari persona amb id " + carrecWs.getUsuariPersonaID() + " relacionat amb aquest càrrec");
//						}
//					}
//					carrecs.add(carrec);
//				}
//			}
//			return carrecs;
//		} catch (Exception ex) {
//			throw new SistemaExternException("Hi ha hagut un problema recuperant els càrrecs per l'usuari aplicació " + getUsername(), ex);
//		}
		return null;
	}

	@Override
	public PortafirmesCarrec recuperarCarrec(String carrecId) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PortafirmesFluxResposta> recuperarPlantillesPerFiltre(String idioma, String descripcio)
			throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String recuperarUrlViewEstatFluxDeFirmes(long portafirmesId, String idioma) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
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
					"portafib=" + getBaseUrl() + ", " +					
					"nom=" + nom + ", " +
					"descripcio=" + descripcio + ")",
					ex);
		}
		return transactionId;
	}
	
	private void closeTransaction(
			String transactionID) throws SistemaExternException {
		try {
			getFluxDeFirmaClient().closeTransaction(transactionID);
		} catch (Exception ex) {
			throw new SistemaExternException("", ex);
		}
	}
	
	private ApiFlowTemplateSimple getFluxDeFirmaClient() throws MalformedURLException {
		String apiRestUrl = getBaseUrl();
		ApiFlowTemplateSimple api = new ApiFlowTemplateSimpleJersey(
				apiRestUrl,
				getUserName(),
				getPassword());
		return api;
	}
	
	private String getBaseUrl() {
//		return "https://dev.caib.es/portafib/common/rest/apiflowtemplatesimple/v1/";
		return (String)GlobalProperties.getInstance().getProperty(
				"app.portafirmes.plugin.flux.firma.url");
	}

	private String getUserName() {
		return GlobalProperties.getInstance().getProperty("app.portafirmes.plugin.flux.firma.usuari");
	}
	private String getPassword() {
		return GlobalProperties.getInstance().getProperty("app.portafirmes.plugin.flux.firma.password");
	}
	

}
