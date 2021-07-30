package es.caib.helium.integracio.service.arxiu;

import com.netflix.servo.util.Strings;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuPluginListener;
import es.caib.distribucio.backoffice.utils.arxiu.ArxiuResultat;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtils;
import es.caib.distribucio.backoffice.utils.arxiu.BackofficeArxiuUtilsImpl;
import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreEntrada;
import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId;
import es.caib.helium.integracio.domini.arxiu.Anotacio;
import es.caib.helium.integracio.domini.arxiu.Arxiu;
import es.caib.helium.integracio.domini.arxiu.ArxiuFirma;
import es.caib.helium.integracio.domini.arxiu.DocumentArxiu;
import es.caib.helium.integracio.domini.arxiu.ExpedientArxiu;
import es.caib.helium.integracio.enums.arxiu.NtiEstadoElaboracionEnum;
import es.caib.helium.integracio.enums.arxiu.NtiOrigenEnum;
import es.caib.helium.integracio.enums.arxiu.NtiTipoDocumentalEnum;
import es.caib.helium.integracio.excepcions.arxiu.ArxiuException;
import es.caib.helium.integracio.service.monitor.MonitorIntegracionsService;
import es.caib.helium.jms.domini.Parametre;
import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.enums.TipusAccio;
import es.caib.helium.jms.events.IntegracioEvent;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentExtensio;
import es.caib.plugins.arxiu.api.DocumentFormat;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.DocumentTipus;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaPerfil;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.plugins.arxiu.api.IArxiuPlugin;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ArxiuServiceCaibImpl implements ArxiuService {

	@Setter
	private IArxiuPlugin api;
	@Autowired
	private MonitorIntegracionsService monitor;
	
	@Override
	public Expedient getExpedient(String uuId, Long entornId) throws ArxiuException {

		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("uuId", uuId));
		var t0 = System.currentTimeMillis();
		var descripcio = "Obtinguent detall de l'expedient";
		try {
			var expedient = api.expedientDetalls(uuId, null);
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.ARXIU)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Detalls de l'expedient consutats correctament");
			return expedient;
			
		} catch(Exception ex) {
			var error = "Error obtinguent els detalls de l'expedient a l'arxiu";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.ARXIU) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new ArxiuException(error, ex);
		}
	}

	@Override
	public boolean crearExpedient(ExpedientArxiu expedientArxiu, Long entornId) throws ArxiuException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("identificador", expedientArxiu.getIdentificador()));
		var t0 = System.currentTimeMillis();
		var descripcio = "Crear expedient a l'arxiu";
		try {
			var expedient =  api.expedientCrear(toExpedient(expedientArxiu));
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.ARXIU)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Expedient creat correctament a l'arxiu");
			return expedient != null;
			
		} catch (Exception ex) {
			
			var error = "Error al crear l'expedient a l'arxiu";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.ARXIU) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new ArxiuException(error, ex);
		}
	}	

	@Override
	public boolean modificarExpedient(ExpedientArxiu expedientArxiu, Long entornId) throws ArxiuException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("identificador", expedientArxiu.getIdentificador()));
		var t0 = System.currentTimeMillis();
		var descripcio = "Modificar expedient a l'arxiu";
		try {
			var expedient =  api.expedientCrear(toExpedient(expedientArxiu));
		
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.ARXIU)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Expedient modificat correctament a l'arxiu");
			return expedient != null;
		
		} catch (Exception ex) {
			var error = "Error al modificar l'expedient a l'arxiu";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.ARXIU) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new ArxiuException(error, ex);
		}
	}

	@Override
	public boolean deleteExpedient(String uuId, Long entornId) throws ArxiuException {

		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("uuId", uuId));
		var t0 = System.currentTimeMillis();
		var descripcio = "Esborrar expedient a l'arxiu";
		try {
			api.expedientEsborrar(uuId);
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.ARXIU)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			return true;
			
		} catch (Exception ex) {
			var error = "Error al esborrar l'expedient a l'arxiu";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.ARXIU) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new ArxiuException(error, ex);
		}
	}

	@Override
	public boolean obrirExpedient(String uuId, Long entornId) throws ArxiuException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("uuId", uuId));
		var t0 = System.currentTimeMillis();
		var descripcio = "Obrir expedient a l'arxiu";
		try {
			api.expedientReobrir(uuId);
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.ARXIU)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			return true;
			
		} catch (Exception ex) {
			var error = "Error al obrir l'expedient a l'arxiu";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.ARXIU) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new ArxiuException(error, ex);
		}
	}

	@Override
	public boolean tancarExpedient(String uuId, Long entornId) throws ArxiuException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("uuId", uuId));
		var t0 = System.currentTimeMillis();
		var descripcio = "Tancar expedient a l'arxiu";
		try {
			
			var expedient = api.expedientTancar(uuId);
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.ARXIU)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			return expedient != null;
			
		} catch (Exception ex) {
			var error = "Error al tancar l'expedient a l'arxiu";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.ARXIU) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new ArxiuException(error, ex);
		}
	}
	
	private Expedient toExpedient(ExpedientArxiu expedientArxiu) {
		
		var expedient = new Expedient();
		expedient.setNom(this.treureCaractersEstranys(expedientArxiu.getIdentificador()));
		expedient.setIdentificador(expedientArxiu.getArxiuUuid());
		var metadades = new ExpedientMetadades();
		metadades.setDataObertura(expedientArxiu.getNtiDataObertura());
		metadades.setClassificacio(expedientArxiu.getNtiClassificacio());
		metadades.setEstat(expedientArxiu.isExpedientFinalitzat() ? ExpedientEstat.TANCAT : ExpedientEstat.OBERT);
		metadades.setOrgans(expedientArxiu.getNtiOrgans());
		metadades.setInteressats(expedientArxiu.getNtiInteressats());
		metadades.setSerieDocumental(expedientArxiu.getSerieDocumental());
		expedient.setMetadades(metadades);
		return expedient;
	}
	
	private String treureCaractersEstranys(String nom) {
		
		if (Strings.isNullOrEmpty(nom)) {
			return null;
		}
		var nomRevisat = nom.trim().replace("&", "&amp;").replaceAll("[~\"#%*:<\n\r\t>/?/|\\\\ ]", "_");
		// L'Arxiu no admet un punt al final del nom #1418
		if (nomRevisat.endsWith(".")) {
			nomRevisat = nomRevisat.substring(0, nomRevisat.length() - 1) + "_";
		}
		return nomRevisat;
	}

	@Override
	public Document getDocument(String uuId, String versio, boolean ambContingut, boolean isSignat, Long entornId) throws ArxiuException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("uuId", uuId));
		parametres.add(new Parametre("versio", versio));
		parametres.add(new Parametre("ambContingut", String.valueOf(ambContingut)));
		parametres.add(new Parametre("isSignat", String.valueOf(isSignat)));
		var t0 = System.currentTimeMillis();
		var descripcio = "Obtenir document de l'arxiu";
		try {
			var document = api.documentDetalls(uuId, versio, ambContingut);
			if (!ambContingut) {
				monitor.enviarEvent(IntegracioEvent.builder()
						.codi(CodiIntegracio.ARXIU)
						.entornId(entornId)
						.descripcio(descripcio)
						.data(new Date())
						.tipus(TipusAccio.ENVIAMENT)
						.estat(EstatAccio.OK)
						.parametres(parametres)
						.tempsResposta(System.currentTimeMillis() - t0).build());
				
				log.debug("Document sense contingut obtingut correctament de l'arxiu");
				return document;
			}
			
			boolean isFirmaPades = false;
			if (isSignat && document.getFirmes() != null) {
				for (Firma firma: document.getFirmes()) {
					if (FirmaTipus.PADES.equals(firma.getTipus())) {
						isFirmaPades = true;
						break;
					}
				}
			}
			if (isFirmaPades) {
				DocumentContingut documentContingut = api.documentImprimible(uuId);
				if (documentContingut != null && documentContingut.getContingut() != null) {
					document.getContingut().setContingut(
							documentContingut.getContingut());
					document.getContingut().setTamany(
							documentContingut.getContingut().length);
					document.getContingut().setTipusMime("application/pdf");
				}
			}
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.ARXIU)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Document obtingut correctament de l'arxiu");
			return document; 
			
		} catch (Exception ex) {
			var error = "Error obtenint el document de l'arxiu";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.ARXIU) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new ArxiuException(error, ex);
		}
	}

	@Override
	public boolean deleteDocument(String uuId, Long entornId) throws ArxiuException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("uuId", uuId));
		var t0 = System.currentTimeMillis();
		var descripcio = "Esborrar document de l'arxiu";
		try {
			api.documentEsborrar(uuId);
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.ARXIU)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Document esborrat de l'arxiu correctament");
			return true;
			
		} catch (Exception ex) {
			var error = "Error esborrant el document";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.ARXIU) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new ArxiuException(error, ex);
		}
	}

	@Override
	public boolean crearDocument(DocumentArxiu document, Long entornId) throws ArxiuException {

		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("identificador", document.getIdentificador()));
		parametres.add(new Parametre("documentNom", document.getNom()));
		parametres.add(new Parametre("arxiuNom", document.getArxiu().getNom()));
		parametres.add(new Parametre("arxiuTamany", document.getArxiu().getTamany() + ""));
		var t0 = System.currentTimeMillis();
		var descripcio = "Crear document de l'arxiu";
		try {
			String nomAmbExtensio = document.getNom() + "." + document.getArxiu().getExtensio();
			var arxiuDoc = toArxiuDocument(
					null, 
					nomAmbExtensio,
					document.getArxiu(),
					document.isDocumentAmbFirma(),
					document.isFirmaSeparada(),
					document.getFirmes(),
					document.getNtiIdentificador(), //al crear i modificar ho posa null sempre a Helium 3.2
					document.getNtiOrigen() != null ? document.getNtiOrigen() : NtiOrigenEnum.ADMINISTRACIO,
					document.getNtiOrgans(),
					document.getNtiDataCaptura(),
					document.getNtiEstatElaboracio(),
					document.getNtiTipusDocumental(),
					document.getNtiIdDocumentOrigen(),
					getExtensioPerArxiu(document.getArxiu()),
					document.getFirmes() != null ? DocumentEstat.DEFINITIU : DocumentEstat.ESBORRANY);
			var arxiu = api.documentCrear(arxiuDoc, document.getUuid()) != null;
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.ARXIU)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Document creat correctament a l'arxiu");
			return arxiu;
			
		} catch (Exception ex) {
			var error = "Error creant el document";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.ARXIU) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new ArxiuException(error, ex);
		}
	}

	@Override
	public boolean modificarDocument(DocumentArxiu document, Long entornId) throws ArxiuException {
		
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("identificador", document.getIdentificador()));
		parametres.add(new Parametre("documentNom", document.getNom()));
		parametres.add(new Parametre("arxiuNom", document.getArxiu().getNom()));
		parametres.add(new Parametre("arxiuTamany", document.getArxiu().getTamany() + ""));
		var t0 = System.currentTimeMillis();
		var descripcio = "Crear document de l'arxiu";
		try {
			String nomAmbExtensio = document.getNom() + "." + document.getArxiu().getExtensio();
			var arxiuDoc = toArxiuDocument(
					null, 
					nomAmbExtensio,
					document.getArxiu(),
					document.isDocumentAmbFirma(),
					document.isFirmaSeparada(),
					document.getFirmes(),
					document.getNtiIdentificador(), //al crear i modificar ho posa null sempre a Helium 3.2
					document.getNtiOrigen(),
					document.getNtiOrgans(),
					document.getNtiDataCaptura(),
					document.getNtiEstatElaboracio(),
					document.getNtiTipusDocumental(),
					document.getNtiIdDocumentOrigen(),
					getExtensioPerArxiu(document.getArxiu()),
					document.getEstat() );
			var doc = api.documentModificar(arxiuDoc);
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.ARXIU)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Document modificat correctament");
			return doc != null;
			
		} catch (Exception ex) {
			var error = "Error modificant el document";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.ARXIU) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new ArxiuException(error, ex);
		}
	}
	
	private DocumentExtensio getExtensioPerArxiu(Arxiu arxiu) {
		String fitxerExtensio = arxiu.getExtensio();
		String extensioAmbPunt = (fitxerExtensio.startsWith(".")) ? fitxerExtensio.toLowerCase() : "." + fitxerExtensio.toLowerCase();
		return DocumentExtensio.toEnum(extensioAmbPunt);
	}
	
	private Document toArxiuDocument(
			String identificador,
			String nom,
			Arxiu fitxer,
			boolean documentAmbFirma,
			boolean firmaSeparada,
			List<ArxiuFirma> firmes,
			String ntiIdentificador,
			NtiOrigenEnum ntiOrigen,
			List<String> ntiOrgans,
			Date ntiDataCaptura,
			NtiEstadoElaboracionEnum ntiEstatElaboracio,
			NtiTipoDocumentalEnum ntiTipusDocumental,
			String ntiIdDocumentOrigen, 
			DocumentExtensio extensio,
			DocumentEstat estat) {
		
		var document = new Document();
		document.setNom(nom);
		document.setIdentificador(identificador);
		var metadades = new DocumentMetadades();
		metadades.setIdentificador(ntiIdentificador);
		if (ntiOrigen != null) {
			switch (ntiOrigen) {
			case CIUTADA:
				metadades.setOrigen(ContingutOrigen.CIUTADA);
				break;
			case ADMINISTRACIO:
				metadades.setOrigen(ContingutOrigen.ADMINISTRACIO);
				break;
			}
		}
		metadades.setDataCaptura(ntiDataCaptura);
		DocumentEstatElaboracio estatElaboracio = null;
		switch (ntiEstatElaboracio) {
			case ORIGINAL:
				estatElaboracio = DocumentEstatElaboracio.ORIGINAL;
				break;
			case COPIA_CF:
				estatElaboracio = DocumentEstatElaboracio.COPIA_CF;
				break;
			case COPIA_DP:
				estatElaboracio = DocumentEstatElaboracio.COPIA_DP;
				break;
			case COPIA_PR:
				estatElaboracio = DocumentEstatElaboracio.COPIA_PR;
				break;
			case ALTRES:
				estatElaboracio = DocumentEstatElaboracio.ALTRES;
				break;
		}
		metadades.setEstatElaboracio(estatElaboracio);
		metadades.setIdentificadorOrigen(ntiIdDocumentOrigen);
		DocumentTipus tipusDocumental = null;
		switch (ntiTipusDocumental) {
			case RESOLUCIO:
				tipusDocumental = DocumentTipus.RESOLUCIO;
				break;
			case ACORD:
				tipusDocumental = DocumentTipus.ACORD;
				break;
			case CONTRACTE:
				tipusDocumental = DocumentTipus.CONTRACTE;
				break;
			case CONVENI:
				tipusDocumental = DocumentTipus.CONVENI;
				break;
			case DECLARACIO:
				tipusDocumental = DocumentTipus.DECLARACIO;
				break;
			case COMUNICACIO:
				tipusDocumental = DocumentTipus.COMUNICACIO;
				break;
			case NOTIFICACIO:
				tipusDocumental = DocumentTipus.NOTIFICACIO;
				break;
			case PUBLICACIO:
				tipusDocumental = DocumentTipus.PUBLICACIO;
				break;
			case JUSTIFICANT_RECEPCIO:
				tipusDocumental = DocumentTipus.JUSTIFICANT_RECEPCIO;
				break;
			case ACTA:
				tipusDocumental = DocumentTipus.ACTA;
				break;
			case CERTIFICAT:
				tipusDocumental = DocumentTipus.CERTIFICAT;
				break;
			case DILIGENCIA:
				tipusDocumental = DocumentTipus.DILIGENCIA;
				break;
			case INFORME:
				tipusDocumental = DocumentTipus.INFORME;
				break;
			case SOLICITUD:
				tipusDocumental = DocumentTipus.SOLICITUD;
				break;
			case DENUNCIA:
				tipusDocumental = DocumentTipus.DENUNCIA;
				break;
			case ALEGACIO:
				tipusDocumental = DocumentTipus.ALEGACIO;
				break;
			case RECURS:
				tipusDocumental = DocumentTipus.RECURS;
				break;
			case COMUNICACIO_CIUTADA:
				tipusDocumental = DocumentTipus.COMUNICACIO_CIUTADA;
				break;
			case FACTURA:
				tipusDocumental = DocumentTipus.FACTURA;
				break;
			case ALTRES_INCAUTATS:
				tipusDocumental = DocumentTipus.ALTRES_INCAUTATS;
				break;
			default:
				tipusDocumental = DocumentTipus.ALTRES;
				break;
		}
		metadades.setTipusDocumental(tipusDocumental);
		// Contingut i firmes
		DocumentContingut contingut = null;
		// Si no està firmat posa el contingut on toca
		if (!documentAmbFirma && fitxer != null) {
			// Sense firma
			contingut = new DocumentContingut();
			contingut.setArxiuNom(fitxer.getNom());
			contingut.setContingut(fitxer.getContingut());
			contingut.setTipusMime(fitxer.getTipusMime());
			document.setContingut(contingut);
		} else {
			// Amb firma
			if (!firmaSeparada) {
				// attached
				Firma firma = new Firma();
				ArxiuFirma primeraFirma;
				if (fitxer != null) {
					firma.setFitxerNom(fitxer.getNom());
					firma.setContingut(fitxer.getContingut());
					firma.setTipusMime(fitxer.getTipusMime());
					if (firmes != null && !firmes.isEmpty()) {
						primeraFirma = firmes.get(0);
						setFirmaTipusPerfil(firma, primeraFirma);
						firma.setCsvRegulacio(primeraFirma.getCsvRegulacio());
					}
				} else if (firmes != null && !firmes.isEmpty()) {
					primeraFirma = firmes.get(0);
					setFirmaTipusPerfil(firma, primeraFirma);
					firma.setFitxerNom(primeraFirma.getFitxerNom());
					firma.setContingut(primeraFirma.getContingut());
					firma.setTipusMime(primeraFirma.getTipusMime());
					firma.setCsvRegulacio(primeraFirma.getCsvRegulacio());
				}
				document.setFirmes(Arrays.asList(firma));
			} else {
				// detached
				contingut = new DocumentContingut();
				contingut.setArxiuNom(fitxer.getNom());
				contingut.setContingut(fitxer.getContingut());
				contingut.setTipusMime(fitxer.getTipusMime());
				document.setContingut(contingut);
				document.setFirmes(new ArrayList<Firma>());
				for (var firmaDto : firmes) {
					var firma = new Firma();
					firma.setFitxerNom(firmaDto.getFitxerNom());
					firma.setContingut(firmaDto.getContingut());
					firma.setTipusMime(firmaDto.getTipusMime());
					setFirmaTipusPerfil(firma, firmaDto);
					firma.setCsvRegulacio(firmaDto.getCsvRegulacio());
					document.getFirmes().add(firma);
				}
			}
		}
		if (extensio != null) {
			metadades.setExtensio(extensio);
			DocumentFormat format = null;
			switch (extensio) {
			case AVI:
				format = DocumentFormat.AVI;
				break;
			case CSS:
				format = DocumentFormat.CSS;
				break;
			case CSV:
				format = DocumentFormat.CSV;
				break;
			case DOCX:
				format = DocumentFormat.SOXML;
				break;
			case GML:
				format = DocumentFormat.GML;
				break;
			case GZ:
				format = DocumentFormat.GZIP;
				break;
			case HTM:
				format = DocumentFormat.XHTML; // HTML o XHTML!!!
				break;
			case HTML:
				format = DocumentFormat.XHTML; // HTML o XHTML!!!
				break;
			case JPEG:
				format = DocumentFormat.JPEG;
				break;
			case JPG:
				format = DocumentFormat.JPEG;
				break;
			case MHT:
				format = DocumentFormat.MHTML;
				break;
			case MHTML:
				format = DocumentFormat.MHTML;
				break;
			case MP3:
				format = DocumentFormat.MP3;
				break;
			case MP4:
				format = DocumentFormat.MP4V; // MP4A o MP4V!!!
				break;
			case MPEG:
				format = DocumentFormat.MP4V; // MP4A o MP4V!!!
				break;
			case ODG:
				format = DocumentFormat.OASIS12;
				break;
			case ODP:
				format = DocumentFormat.OASIS12;
				break;
			case ODS:
				format = DocumentFormat.OASIS12;
				break;
			case ODT:
				format = DocumentFormat.OASIS12;
				break;
			case OGA:
				format = DocumentFormat.OGG;
				break;
			case OGG:
				format = DocumentFormat.OGG;
				break;
			case PDF:
				format = DocumentFormat.PDF; // PDF o PDFA!!!
				break;
			case PNG:
				format = DocumentFormat.PNG;
				break;
			case PPTX:
				format = DocumentFormat.SOXML;
				break;
			case RTF:
				format = DocumentFormat.RTF;
				break;
			case SVG:
				format = DocumentFormat.SVG;
				break;
			case TIFF:
				format = DocumentFormat.TIFF;
				break;
			case TXT:
				format = DocumentFormat.TXT;
				break;
			case WEBM:
				format = DocumentFormat.WEBM;
				break;
			case XLSX:
				format = DocumentFormat.SOXML;
				break;
			case ZIP:
				format = DocumentFormat.ZIP;
				break;
			case CSIG:
				format = DocumentFormat.CSIG;
				break;
			case XSIG:
				format = DocumentFormat.XSIG;
				break;
			case XML:
				format = DocumentFormat.XML;
				break;
			}
			metadades.setFormat(format);
		}
		metadades.setOrgans(ntiOrgans);
		document.setMetadades(metadades);
		document.setEstat(estat);
		return document;
	}
	
	private void setFirmaTipusPerfil(Firma firma, ArxiuFirma arxiuFirmaDto) {
		
		if (arxiuFirmaDto.getTipus() != null) {
			switch(arxiuFirmaDto.getTipus()) {
			case CSV:
				firma.setTipus(FirmaTipus.CSV);
				break;
			case XADES_DET:
				firma.setTipus(FirmaTipus.XADES_DET);
				break;
			case XADES_ENV:
				firma.setTipus(FirmaTipus.XADES_ENV);
				break;
			case CADES_DET:
				firma.setTipus(FirmaTipus.CADES_DET);
				break;
			case CADES_ATT:
				firma.setTipus(FirmaTipus.CADES_ATT);
				break;
			case PADES:
				firma.setTipus(FirmaTipus.PADES);
				break;
			case SMIME:
				firma.setTipus(FirmaTipus.SMIME);
				break;
			case ODT:
				firma.setTipus(FirmaTipus.ODT);
				break;
			case OOXML:
				firma.setTipus(FirmaTipus.OOXML);
				break;
			}
		}
		if (arxiuFirmaDto.getPerfil() != null) {
			switch(arxiuFirmaDto.getPerfil()) {
			case BES:
				firma.setPerfil(FirmaPerfil.BES);
				break;
			case EPES:
				firma.setPerfil(FirmaPerfil.EPES);
				break;
			case LTV:
				firma.setPerfil(FirmaPerfil.LTV);
				break;
			case T:
				firma.setPerfil(FirmaPerfil.T);
				break;
			case C:
				firma.setPerfil(FirmaPerfil.C);
				break;
			case X:
				firma.setPerfil(FirmaPerfil.X);
				break;
			case XL:
				firma.setPerfil(FirmaPerfil.XL);
				break;
			case A:
				firma.setPerfil(FirmaPerfil.A);
				break;
			}
		}
	}

	@Override
	public ArxiuResultat crearExpedientAmbAnotacioRegistre(
			String arxiuUuId,
			Long entornId,
			ArxiuPluginListener arxiuPluginListener,
			Anotacio anotacio) throws ArxiuException {

		try {
			// Utilitza la llibreria d'utilitats de Distribució per incorporar la informació
			// de l'anotació directament a l'expedient dins l'Arxiu
			es.caib.plugins.arxiu.api.Expedient expedientArxiu = getExpedient(arxiuUuId, entornId);
			BackofficeArxiuUtils backofficeUtils = new BackofficeArxiuUtilsImpl(api);
			// Posarà els annexos en la carpeta de l'anotació
			backofficeUtils.setCarpeta(anotacio.getIdentificador());
			// S'enregistraran els events al monitor d'integració
			backofficeUtils.setArxiuPluginListener(arxiuPluginListener);
			// Prepara la consulta a Distribució
			es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId idWs = new AnotacioRegistreId();
			idWs.setClauAcces(anotacio.getDistribucioClauAcces());
			idWs.setIndetificador(anotacio.getDistribucioId());
			AnotacioRegistreEntrada anotacioRegistreEntrada;

			anotacioRegistreEntrada = distribucioHelper.consulta(idWs);
			return backofficeUtils.crearExpedientAmbAnotacioRegistre(expedientArxiu, anotacioRegistreEntrada);
		} catch (Exception e) {
			var errMsg = "Error reprocessant la informació de l'anotació de registre \""
					+ anotacio.getIdentificador() + "\" de Distribució: " + e.getMessage();
			log.error(errMsg, e);
			throw new ArxiuException(errMsg, e);
		}
	}
	
}
