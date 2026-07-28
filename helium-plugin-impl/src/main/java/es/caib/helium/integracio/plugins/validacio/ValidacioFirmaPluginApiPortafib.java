/**
 *
 */
package es.caib.helium.integracio.plugins.validacio;

import es.caib.comanda.model.server.monitoring.EstatSalut;
import es.caib.comanda.model.server.monitoring.IntegracioPeticions;
import es.caib.helium.commons.dto.ArxiuFirmaDetallDto;
import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.commons.utils.GlobalProperties;
import es.caib.helium.logic.intf.helper.ArxiuConversions;
import es.caib.portafib.apiinterna.client.signature.v1.api.SignatureValidationV1Api;
import es.caib.portafib.apiinterna.client.signature.v1.model.CertificateInformation;
import es.caib.portafib.apiinterna.client.signature.v1.model.Document;
import es.caib.portafib.apiinterna.client.signature.v1.model.SignatureDetailInfo;
import es.caib.portafib.apiinterna.client.signature.v1.model.SignatureRequestedInformation;
import es.caib.portafib.apiinterna.client.signature.v1.model.TimeStampInfo;
import es.caib.portafib.apiinterna.client.signature.v1.model.ValidateSignatureRequest;
import es.caib.portafib.apiinterna.client.signature.v1.model.ValidateSignatureResponse;
import es.caib.portafib.apiinterna.client.signature.v1.services.ApiClient;
import es.caib.helium.integracio.plugins.AbstractSalutPlugin;
import io.micrometer.core.instrument.MeterRegistry;
import org.fundaciobit.pluginsib.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin;

import java.util.Date;
import java.util.Properties;

/**
 * Implementació del plugin de validació de firmes emprant el client del portafirmes
 * per comunicar-se amb Afirma.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidacioFirmaPluginApiPortafib extends AfirmaCxfValidateSignaturePlugin implements ValidacioSignaturaPlugin {

	public ValidacioFirmaPluginApiPortafib(String prefix, Properties properties) {
		super(prefix, properties);
	}

	@Override
	public ValidaSignaturaResposta validaSignatura(
			String documentNom,
			String documentMime,
			byte[] documentContingut,
			byte[] firmaContingut
			) throws SistemaExternException {
		long start = System.currentTimeMillis();
		ValidateSignatureRequest validateRequest = new ValidateSignatureRequest();
		ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();

        Document document = null;
		if (documentContingut != null) {
            document = new Document();
            document.setName(documentNom);
            document.setMime(documentMime);
            document.setData(documentContingut);
		}

		Document firma = null;
		if (firmaContingut != null) {
			firma = new Document();
			firma.setData(firmaContingut);
			firma.setMime("application/octet-stream");
			firma.setName("firma");
	        validateRequest.setSignatureDocument(firma);
			validateRequest.setDetachedDocument(document);
		} else {
	        validateRequest.setSignatureDocument(document);
			validateRequest.setDetachedDocument(firma);
		}

		SignatureRequestedInformation sri = new SignatureRequestedInformation();
		sri.setReturnSignatureTypeFormatProfile(true);
		sri.setReturnCertificateInfo(true);
		sri.setReturnValidationChecks(false);
		sri.setValidateCertificateRevocation(false);
		sri.setReturnCertificates(false);
		sri.setReturnTimeStampInfo(true);
		validateRequest.setSignatureRequestedInformation(sri);

		ApiClient apiClient;
		try {
			apiClient = getApiClient();
			SignatureValidationV1Api api = getApi(apiClient);
	        ValidateSignatureResponse response = api.validateSignature(getLanguageUI(), validateRequest);

	        // Completa la resposta
			resposta.setStatus(response.getValidationStatus().getStatus());
			resposta.setErrMsg(response.getValidationStatus().getErrorMsg());
			resposta.setErrException(new RuntimeException(response.getValidationStatus().getErrorException()));

			if (response.getSignatureDetailInfo() != null) {
				for (SignatureDetailInfo signatureInfo: response.getSignatureDetailInfo()) {
					ArxiuFirmaDetallDto detall = new ArxiuFirmaDetallDto();
					TimeStampInfo timeStampInfo = signatureInfo.getTimeStampInfo();
					if (timeStampInfo != null) {
						detall.setData(Date.from(timeStampInfo.getCreationTime().toInstant()));
					} else if (signatureInfo.getSignDate() != null){
						detall.setData(Date.from(signatureInfo.getSignDate().toInstant()));
					}
					CertificateInformation certificateInformation = signatureInfo.getCertificateInfo();
					if (certificateInformation != null) {
						detall.setResponsableNif(certificateInformation.getEuropeanOrganizationAdministrationID());
						detall.setResponsableNom(certificateInformation.getFullName());
						detall.setEmissorCertificat(certificateInformation.getIssuerOrganization());
					}
					resposta.getFirmaDetalls().add(detall);
				}
				resposta.setPerfil(ArxiuConversions.toPerfilFirmaArxiu(response.getSignProfile()));
				resposta.setTipus(ArxiuConversions.toFirmaTipusPortafib(
						response.getSignType(),
						response.getSignMode()));
			}
//			salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
		} catch (Exception e) {
//			salutPluginComponent.incrementarOperacioError();
			e.printStackTrace();
			throw new SistemaExternException(e);
		}
		return resposta;
	}

    public SignatureValidationV1Api getApi(ApiClient client) throws Exception {
        SignatureValidationV1Api api = new SignatureValidationV1Api(client);
        return api;
    }

	protected ApiClient getApiClient() throws Exception {

        // Properties prop = getConfigProperties();
        String languageUI = null; // = getLanguageUI(prop);

        String basePath = this.getPropertyEndpoint(); // = getRequiredProperty(prop, "basePath");

        String username = this.getPropertyUsername(); // = getRequiredProperty(prop, "username");

        String password = this.getPropertyPassword(); // = getRequiredProperty(prop, "password");

        ApiClient apiClient = getApiClient(basePath, username, password, languageUI);

        return apiClient;
    }

    protected String getLanguageUI() throws Exception {
        Properties prop; // = getConfigProperties();
        return null; // getRequiredProperty(prop, "languageUI");
    }

    protected String getLanguageUI(Properties prop) throws Exception {
        return null; // getRequiredProperty(prop, "languageUI");
    }

    protected ApiClient getApiClient(String basePath, String username, String password, String languageUI) {
        ApiClient client = new ApiClient();
        client.setBasePath(basePath);
        client.setUsername(username);
        client.setPassword(password);

        client.setDebugging(true);

        client.addDefaultHeader("Accept-Language", languageUI);
        return client;
    }

	@Override
	public String getUsuariIntegracio() {
		return this.getPropertyUsername();
	}

	private String getPropertyEndpoint() {
		return GlobalProperties.getInstance().getProperty(
				"es.caib.helium.pluginsib.validatesignature.afirmacxf.endpoint");
	}

	private String getPropertyUsername() {
		return GlobalProperties.getInstance().getProperty(
				"es.caib.helium.pluginsib.validatesignature.afirmacxf.authorization.username");
	}

	private String getPropertyPassword() {
		return GlobalProperties.getInstance().getProperty(
				"es.caib.helium.pluginsib.validatesignature.afirmacxf.authorization.password");
	}

    // Mètodes de SALUT
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private AbstractSalutPlugin salutPluginComponent = new AbstractSalutPlugin();
    public void init(MeterRegistry registry, String codiPlugin) {
    	String uniqueId = codiPlugin + "-" + System.identityHashCode(this);
        salutPluginComponent.init(registry, uniqueId);
    }

    @Override
	public boolean teConfiguracioEspecifica() {
		return salutPluginComponent.teConfiguracioEspecifica();
	}

	@Override
	public EstatSalut getEstatPlugin() {
		return salutPluginComponent.getEstatPlugin();
	}

	@Override
	public IntegracioPeticions getPeticionsPlugin() {
		IntegracioPeticions peticions = salutPluginComponent.getPeticionsPlugin();
		peticions.setEndpoint(getPropertyEndpoint());
		return peticions;
	}

}
