package es.caib.helium.integracio.service.firma;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.fundaciobit.apisib.apifirmasimple.v1.ApiFirmaEnServidorSimple;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleAvailableProfile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleCommonInfo;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFileInfoSignature;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignDocumentRequest;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignatureResult;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignedFileInfo;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleStatus;
import org.fundaciobit.apisib.apifirmasimple.v1.jersey.ApiFirmaEnServidorSimpleJersey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.helium.integracio.domini.firma.FirmaPost;
import es.caib.helium.integracio.excepcions.firma.FirmaException;
import es.caib.helium.integracio.service.monitor.MonitorIntegracionsService;
import es.caib.helium.jms.domini.Parametre;
import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.enums.TipusAccio;
import es.caib.helium.jms.events.IntegracioEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class FirmaServicePortaFibImpl implements FirmaService {
	
	private static final String PROPERTIES_BASE = "app.plugin.firma.portafib.";

	@Autowired
	private Environment env;

	@Setter
	private ApiFirmaEnServidorSimpleJersey plugin;
	@Setter
	private String username;
	@Setter
	private String location;
	@Setter
	private String email;
	@Autowired
	protected MonitorIntegracionsService monitor;
	
	@Override
	public FirmaResposta firmar(FirmaPost firma, Long entornId) throws FirmaException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("expedientIdentificador", firma.getExpedientIdentificador()));
		parametres.add(new Parametre("expedientNumero", firma.getExpedientNumero()));
		parametres.add(new Parametre("expedientTipusId", firma.getExpedientTipusId() + ""));
		parametres.add(new Parametre("expedientTipusCodi", firma.getExpedientTipusCodi()));
		parametres.add(new Parametre("expedientTipusNom", firma.getExpedientTipusNom()));
		parametres.add(new Parametre("documentCodi", firma.getCodiDocument()));
		parametres.add(new Parametre("firmaId", firma.getId()));
		parametres.add(new Parametre("arxiuNom", firma.getNom()));
		parametres.add(new Parametre("arxiuTamany", firma.getTamany() + ""));

		var t0 = System.currentTimeMillis();
		var descripcio = "Firmant el fitxer " + firma.getNom();

		FirmaResposta resposta = new FirmaResposta();
		
		FirmaSimpleFile fileToSign = new FirmaSimpleFile(firma.getNom(), firma.getMime(), firma.getContingut());

		FirmaSimpleSignatureResult result;
		try {
			
//			getAvailableProfiles(api);
			String perfil = getPropertyApiPerfil();
			result = internalSignDocument(
					firma.getId(),
					plugin,
					perfil,
					fileToSign,
					firma.getMotiu(),
					firma.getTipusDocumental());
			
			resposta.setContingut(result.getSignedFile().getData());
			if (result.getSignedFile() != null) {
				resposta.setNom(result.getSignedFile().getNom());
				resposta.setMime(result.getSignedFile().getMime());
			}
			if (result.getSignedFileInfo() != null) {
				resposta.setTipusFirma(result.getSignedFileInfo().getSignType());
				resposta.setTipusFirmaEni(result.getSignedFileInfo().getEniTipoFirma());
				resposta.setPerfilFirmaEni(result.getSignedFileInfo().getEniPerfilFirma());
			}

			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.FIRMA_SERV)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Fitxer firmat correctament");
			
			return resposta;
		} catch (Exception ex) {
			var error = "Error firmant l'arxiu";
			log.error(error, ex);
			
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.FIRMA_SERV) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			
			throw new FirmaException(error, ex);
		}
	}
	
	protected FirmaSimpleSignatureResult internalSignDocument(
			String id, 
			ApiFirmaEnServidorSimple api,
			final String perfil,
			FirmaSimpleFile fileToSign,
			String motiu,
			String tipusDocumental) throws Exception, FileNotFoundException, IOException {
		String signID = id;
		String name = fileToSign.getNom();
		String reason = motiu;
		String location = getLocationProperty();

		int signNumber = 1;
		String languageSign = "ca";
		Long tipusDocumentalID = tipusDocumental != null ? Long.valueOf(tipusDocumental.substring(2)) : null;

		FirmaSimpleFileInfoSignature fileInfoSignature = new FirmaSimpleFileInfoSignature(
				fileToSign,
				signID,
				name,
				reason,
				location,
				signNumber,
				languageSign,
				tipusDocumentalID);

		String languageUI = "ca";
		String username = this.getPropertyUsername();
		String administrationID = null;
		String signerEmail = this.getSignerEmailProperty();

		FirmaSimpleCommonInfo commonInfo;
		commonInfo = new FirmaSimpleCommonInfo(perfil, languageUI, username, administrationID, signerEmail);

		log.debug("languageUI = |" + languageUI + "|");

		FirmaSimpleSignDocumentRequest signature;
		signature = new FirmaSimpleSignDocumentRequest(commonInfo, fileInfoSignature);

		FirmaSimpleSignatureResult fullResults = api.signDocument(signature);

		FirmaSimpleStatus transactionStatus = fullResults.getStatus();

		int status = transactionStatus.getStatus();

		switch (status) {

		case FirmaSimpleStatus.STATUS_INITIALIZING: // = 0;
			throw new SistemaExternException("API de firma simple ha tornat status erroni: Initializing ...Unknown Error (???)");

		case FirmaSimpleStatus.STATUS_IN_PROGRESS: // = 1;
			throw new SistemaExternException("API de firma simple ha tornat status erroni: In PROGRESS ...Unknown Error (???)");

		case FirmaSimpleStatus.STATUS_FINAL_ERROR: // = -1;
			throw new SistemaExternException("Error durant la realització de les firmes: " + transactionStatus.getErrorMessage() +"\r\n" +transactionStatus.getErrorStackTrace());

		case FirmaSimpleStatus.STATUS_CANCELLED: // = -2;
			throw new SistemaExternException("S'ha cancel·lat el procés de firmat.");

		case FirmaSimpleStatus.STATUS_FINAL_OK: // = 2;
		{
			log.debug(" ===== RESULTAT  =========");
			log.debug(" ---- Signature [ " + fullResults.getSignID() + " ]");
			log.debug(FirmaSimpleSignedFileInfo.toString(fullResults.getSignedFileInfo()));

			return fullResults;
		}
		default:
			throw new SistemaExternException("Status de firma desconegut");
		}
	}

	 public void getAvailableProfiles(ApiFirmaEnServidorSimple api) throws Exception {

		    final String languagesUI[] = new String[] { "ca", "es" };

		    for (String languageUI : languagesUI) {
		      log.debug(" ==== LanguageUI : " + languageUI + " ===========");

		      List<FirmaSimpleAvailableProfile> listProfiles = api.getAvailableProfiles(languageUI);
		      if (listProfiles.size() == 0) {
		        log.debug("NO HI HA PERFILS PER AQUEST USUARI APLICACIÓ");
		      } else {
		        for (FirmaSimpleAvailableProfile ap : listProfiles) {
		          log.debug("  + " + ap.getName() + ":");
		          log.debug("      * Codi: " + ap.getCode());
		          log.debug("      * Desc: " + ap.getDescription());
		        }
		      }
		    }
	 }
	 
	private String getPropertyUsername() {
		return env.getProperty(
				PROPERTIES_BASE + "username");
	}
	 
	private String getLocationProperty() {
		return env.getProperty(
				PROPERTIES_BASE + "location", "Palma");
	}

	private String getSignerEmailProperty() {
		return env.getProperty(
				PROPERTIES_BASE + "signer.email", "suport@caib.es");
	}
	 	
	private String getPropertyApiPerfil() {
		return env.getProperty(
				"app.plugin.firma.portafib.plugins.signatureserver.portafib.api_passarela_perfil");
	}

}
