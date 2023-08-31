package net.conselldemallorca.helium.webapp.v3.passarelafirma;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleStatus;
import org.fundaciobit.plugins.signature.api.CommonInfoSignature;
import org.fundaciobit.plugins.signature.api.FileInfoSignature;
import org.fundaciobit.plugins.signature.api.ITimeStampGenerator;
import org.fundaciobit.plugins.signature.api.PdfVisibleSignature;
import org.fundaciobit.plugins.signature.api.PolicyInfoSignature;
import org.fundaciobit.plugins.signature.api.SecureVerificationCodeStampInfo;
import org.fundaciobit.plugins.signature.api.SignaturesTableHeader;
import org.fundaciobit.plugins.signature.api.StatusSignature;
import org.fundaciobit.plugins.signature.api.StatusSignaturesSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.DocumentFormatRegistry;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.firmaweb.FirmaWebPluginPortafibRest;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.webapp.v3.helper.UrlHelper;

/**
 * Classes s'ajuda per a les accions de la passarel·la de firma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PassarelaFirmaHelper {

	public static final String CONTEXTWEB = "/v3/firmapassarela";

	private Map<String, PassarelaFirmaConfig> signaturesSetsMap = new HashMap<String, PassarelaFirmaConfig>();
	private long lastCheckFirmesCaducades = 0;
	private DocumentFormatRegistry documentFormatRegistry;


	public String iniciarProcesDeFirma(
			HttpServletRequest request,
			ArxiuDto fitxerPerFirmar,
			String documentId,
			String destinatariNif,
			String motiu,
			String llocFirma,
			String emailFirmant,
			String idiomaCodi,
			String urlFinalHelium,
			boolean navegadorSuportaJava) throws IOException {
		long signaturaId = generateUniqueSignaturesSetId();
		String signaturesSetId = new Long(signaturaId).toString();
		Calendar caducitat = Calendar.getInstance();
		caducitat.add(Calendar.MINUTE, 40);
		CommonInfoSignature commonInfoSignature;
		// TODO Veure manual de MiniApplet
		String filtreCertificats = GlobalProperties.getInstance().getProperty("app.signatura.certificats.filtre", "filters.1=nonexpired:");
		// TODO Definir politica de Firma (opcional)
		PolicyInfoSignature pis = null;
		commonInfoSignature = new CommonInfoSignature(
				idiomaCodi,
				filtreCertificats,
				request.getUserPrincipal().getName(),
				destinatariNif,
				pis);
		File filePerFirmar = getFitxerAFirmarPath(signaturaId);
		FileUtils.writeByteArrayToFile(
				filePerFirmar,
				fitxerPerFirmar.getContingut());
		FileInfoSignature fis = getFileInfoSignature(
				signaturesSetId,
				filePerFirmar, // File amb el fitxer a firmar
				//fitxerPerFirmar.getContentType(), // Tipus mime del fitxer a firmar
				getArxiuMimeType(fitxerPerFirmar.getNom()),
				fitxerPerFirmar.getNom(), // Nom del fitxer a firmar
				0, // posició taula firmes: 0, 1, -1 (sense, primera pag., darrera pag.)
				null, // SignaturesTableHeader 
				motiu,
				llocFirma,
				emailFirmant,
				1, // Nombre de firmes (nomes en suporta una)
				idiomaCodi,
				FileInfoSignature.SIGN_TYPE_PADES,
				FileInfoSignature.SIGN_ALGORITHM_SHA1,
				FileInfoSignature.SIGN_MODE_IMPLICIT,
				false, // userRequiresTimeStamp,
				null, // timeStampGenerator,
				null); // svcsi
		PassarelaFirmaConfig signaturesSet = new PassarelaFirmaConfig(
				signaturesSetId,
				caducitat.getTime(),
				commonInfoSignature,
				new FileInfoSignature[] {fis},
				urlFinalHelium,
				UrlHelper.getAbsoluteControllerBase(
						request,
						PassarelaFirmaHelper.CONTEXTWEB) + "/final/"+signaturesSetId, 
				documentId);
		startSignatureProcess(signaturesSet);
		return CONTEXTWEB + "/selectsignmodule/" + signaturesSetId;
	}

	public Integer getNumberPossiblePlugins() {
		List<FirmaWebPluginPortafibRest> plugins = getAllPluginsFromProperties();
		if (plugins == null) 
			return 0;
		else
			return plugins.size();
	}
	
	public List<FirmaWebPluginPortafibRest> getAllPlugins(
			HttpServletRequest request,
			String signaturesSetId) throws Exception {
	
		List<FirmaWebPluginPortafibRest> plugins = getAllPluginsFromProperties();
		if (plugins == null || plugins.size() == 0) {
			String msg = "S'ha produit un error llegint els plugins o no se n'han definit.";
			throw new Exception(msg);
		}
		List<FirmaWebPluginPortafibRest> pluginsFiltered = new ArrayList<FirmaWebPluginPortafibRest>();
		FirmaWebPluginPortafibRest signaturePlugin;
		for (FirmaWebPluginPortafibRest pluginDeFirma: plugins) {
			// 1.- Es pot instanciar el plugin ?
			signaturePlugin = getInstanceByPluginId(pluginDeFirma.getPluginId());
			if (signaturePlugin == null) {
				throw new Exception("No s'ha pogut instanciar el plugin amb id " + pluginDeFirma.getPluginId());
			}
			// 2.- Passa el filtre ...
			//if (signaturePlugin.filter(request, signaturesSet)) {
				pluginsFiltered.add(pluginDeFirma);
//			} else {
//				log.debug("Exclos plugin [" + pluginDeFirma.getNom() + "]: NO PASSA FILTRE");
//			}
		}
		return pluginsFiltered;
	}


	public String signDocuments(
			HttpServletRequest request,
			String signaturesSetId,
			String urlFinalTascaDocument) throws Exception {
		PassarelaFirmaConfig firmaconfig = getSignaturesSet(request, signaturesSetId);
		Long pluginId = firmaconfig.getPluginId();
		
		FirmaWebPluginPortafibRest signaturePlugin;
		signaturePlugin = getInstanceByPluginId(pluginId);
		
		if (signaturePlugin == null) {
			String msg = "plugin.signatureweb.noexist: " + String.valueOf(pluginId);
			throw new Exception(msg);
		}
		// Crida el teu plugin amb la info a partir signaturesSet
		FileInfoSignature fitxerPerFirmar = firmaconfig.getFileInfoSignatureArray()[0];

		final String urlFinal = UrlHelper.getAbsoluteControllerBase(request,"").concat(urlFinalTascaDocument + "?signaturesSetId=" + signaturesSetId);
				
		String urlToPluginWebPage = signaturePlugin.firmar(
					signaturesSetId, 
					fitxerPerFirmar.getName(), 
					fitxerPerFirmar.getReason(),
					FileUtils.readFileToByteArray(fitxerPerFirmar.getFileToSign()),
					fitxerPerFirmar.getMimeType(), 
					firmaconfig.documentId, 
					urlFinal,
					firmaconfig.getCommonInfoSignature().getAdministrationID());
					//urlFinalTascaDocument);
		String transactionID = urlToPluginWebPage.split("/start/")[1];
		firmaconfig.setTransactionId(transactionID);
		//firmaconfig.setFirmaSimpleStatus(FirmeSimpleStatus.);
		return urlToPluginWebPage;
	}

	public String requestPlugin(
			HttpServletRequest request,
			HttpServletResponse response,
			String signaturesSetId,
			int signatureIndex,
			String query) throws Exception {
		PassarelaFirmaConfig ss = getSignaturesSet(request, signaturesSetId);
		long pluginId = ss.getPluginId();
		FirmaWebPluginPortafibRest signaturePlugin;
		try {
			signaturePlugin = getInstanceByPluginId(pluginId);
		} catch (Exception e) {
			String msg = "plugin.signatureweb.noexist: " + String.valueOf(pluginId);
			throw new Exception(msg);
		}
		if (signaturePlugin == null) {
			String msg = "plugin.signatureweb.noexist: " + String.valueOf(pluginId);
			throw new Exception(msg);
		}
		String absoluteRequestPluginBasePath = getRequestPluginBasePath(
				UrlHelper.getAbsoluteControllerBase(
						request,
						PassarelaFirmaHelper.CONTEXTWEB),
				signaturesSetId,
				signatureIndex);
		String relativeRequestPluFirmaWebPluginPortafibRestginBasePath = getRequestPluginBasePath(
				getRelativeControllerBase(
						request,
						PassarelaFirmaHelper.CONTEXTWEB),
				signaturesSetId,
				signatureIndex);
		return absoluteRequestPluginBasePath;
	}

	public PassarelaFirmaConfig finalitzarProcesDeFirma(
			HttpServletRequest request,
			String signaturesSetId) throws FileNotFoundException, IOException, Exception {
		PassarelaFirmaConfig pss = getSignaturesSet(request, signaturesSetId);
	
		if (pss!=null) {
			StatusSignaturesSet sss = pss.getStatusSignaturesSet();
			pss.setFirmaSimpleStatus(new FirmaSimpleStatus());
			FileInfoSignature firmaInfo = pss.getFileInfoSignatureArray()[0];
			StatusSignature firmaStatus = firmaInfo.getStatusSignature();
			
			FirmaWebPluginPortafibRest plugin = this.plugins.get(0);
			
			String transactionID = pss.getTransactionId();
			FirmaSimpleStatus firmaSimpleStatus = new FirmaSimpleStatus();
			FirmaSimpleFile fsf = new FirmaSimpleFile();
					
			Map<FirmaSimpleFile, FirmaSimpleStatus> mapFirmaStatus =plugin.getResponse(pss.getTransactionId(), pss.getSignedData(), firmaSimpleStatus);

			if (!mapFirmaStatus.isEmpty()) {
				for (Map.Entry<FirmaSimpleFile, FirmaSimpleStatus> map : mapFirmaStatus.entrySet()) {
				    
				    try {
						  fsf = map.getKey(); 
					      plugin.getApi().closeTransaction(transactionID);
					      pss.setSignedData(fsf.getData());
					      pss.setFirmaSimpleStatus(map.getValue());
					      firmaStatus.setStatus(pss.getFirmaSimpleStatus().getStatus());
					      sss.setStatus(pss.getFirmaSimpleStatus().getStatus());

					    } catch (Throwable th) {
					      th.printStackTrace();
					    }
				}
			   
			return pss;
			}
		}
		else {
			String msg = "moduldefirma.caducat: " + signaturesSetId;
			throw new RuntimeException(msg);
		}

		return pss;
	}

	public PassarelaFirmaConfig getSignaturesSet(
			HttpServletRequest request,
			String signaturesSetId) {
		// Fer net peticions caducades SignaturesSet.getExpiryDate()
		// Check si existeix algun proces de firma caducat s'ha d'esborrar
		// Com a mínim cada minut es revisa si hi ha caducats
		Long now = System.currentTimeMillis();
		final long un_minut_en_ms = 60 * 60 * 1000;
		if (now + un_minut_en_ms > lastCheckFirmesCaducades) {
			lastCheckFirmesCaducades = now;
			List<PassarelaFirmaConfig> keysToDelete = new ArrayList<PassarelaFirmaConfig>();
			Set<String> ids = signaturesSetsMap.keySet();
			for (String id : ids) {
				PassarelaFirmaConfig ss = signaturesSetsMap.get(id);
				if (now > ss.getExpiryDate().getTime()) {
					keysToDelete.add(ss);
					SimpleDateFormat sdf = new SimpleDateFormat();
					log.debug("Tancant Signature SET amb id = " + id + " a causa de que està caducat " + "( ARA: "
							+ sdf.format(new Date(now)) + " | CADUCITAT: " + sdf.format(ss.getExpiryDate()) + ")");
				}
			}
			if (keysToDelete.size() != 0) {
				synchronized (signaturesSetsMap) {

					for (PassarelaFirmaConfig pss : keysToDelete) {
						closeSignaturesSet(request, pss);
					}
				}
			}
		}
		return signaturesSetsMap.get(signaturesSetId);
	}

	public void closeSignaturesSet(HttpServletRequest request, PassarelaFirmaConfig pss) {
		Long pluginId = pss.getPluginId();
		final String signaturesSetId = pss.getSignaturesSetID();
		if (pluginId != null) {
			FirmaWebPluginPortafibRest signaturePlugin = null;
			try {
				signaturePlugin = getInstanceByPluginId(pluginId);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return;
			}
			if (signaturePlugin == null) {
				log.error("plugin.signatureweb.noexist: " + String.valueOf(pluginId));
			}
			try {
				//signaturePlugin.closeSignaturesSet(request, signaturesSetId);
			} catch (Exception e) {
				log.error("Error borrant dades d'un SignaturesSet " + signaturesSetId + ": " + e.getMessage(), e);
			}
		}
		signaturesSetsMap.remove(signaturesSetId);
	}

	// -------------------------------------------------------------------------
	// -------------------------------------------------------------------------
	// ----------------------------- U T I L I T A T S ----------------------
	// -------------------------------------------------------------------------
	// -------------------------------------------------------------------------

	private void startSignatureProcess(PassarelaFirmaConfig signaturesSet) {
		synchronized (signaturesSetsMap) {
			final String signaturesSetId = signaturesSet.getSignaturesSetID();
			signaturesSetsMap.put(signaturesSetId, signaturesSet);
		}
	}

	private long generateUniqueSignaturesSetId() {
		long id;
		synchronized (PassarelaFirmaHelper.CONTEXTWEB) {
			id = (System.currentTimeMillis() * 1000000L) + System.nanoTime() % 1000000L;
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}
		}
		return id;
	}

	private static final String AUTOFIRMA = "AUTOFIRMA";
	private static final String autofirmaBasePath;
	static {
		String tempDir = System.getProperty("java.io.tmpdir");
		final File base = new File(tempDir, AUTOFIRMA);
		base.mkdirs();
		autofirmaBasePath = base.getAbsolutePath();
	}
	private File getFitxerAFirmarPath(long id) {
		File f = new File(
				autofirmaBasePath + File.separatorChar + id,
				String.valueOf(id) + "_original");
		f.getParentFile().mkdirs();
		return f;
	}

	private FileInfoSignature getFileInfoSignature(
			String signatureId,
			File fileToSign,
			String mimeType,
			String idname,
			int locationSignTableId,
			SignaturesTableHeader signaturesTableHeader,
			String reason,
			String location,
			String signerEmail,
			int signNumber,
			String languageSign,
			String signType,
			String signAlgorithm,
			int signModeUncheck,
			boolean userRequiresTimeStamp,
			ITimeStampGenerator timeStampGenerator,
			SecureVerificationCodeStampInfo csvStampInfo) {
		final int signMode = ((signModeUncheck == FileInfoSignature.SIGN_MODE_IMPLICIT)
				? FileInfoSignature.SIGN_MODE_IMPLICIT : FileInfoSignature.SIGN_MODE_EXPLICIT);
		PdfVisibleSignature pdfInfoSignature = null;
		if (FileInfoSignature.SIGN_TYPE_PADES.equals(signType)) {
			// PDF Visible
			pdfInfoSignature = new PdfVisibleSignature();
			if (locationSignTableId != FileInfoSignature.SIGNATURESTABLELOCATION_WITHOUT) {
				// No tenim generadors en aquest APP
				pdfInfoSignature.setRubricGenerator(null);
				pdfInfoSignature.setPdfRubricRectangle(null);
			}
		} else if (FileInfoSignature.SIGN_TYPE_CADES.equals(signType)) {
		} else if (FileInfoSignature.SIGN_TYPE_XADES.equals(signType)) {
		} else {
			throw new RuntimeException("Tipus de firma no suportada: " + signType);
		}
		if (FileInfoSignature.SIGN_ALGORITHM_SHA1.equals(signAlgorithm)
				|| FileInfoSignature.SIGN_ALGORITHM_SHA256.equals(signAlgorithm)
				|| FileInfoSignature.SIGN_ALGORITHM_SHA384.equals(signAlgorithm)
				|| FileInfoSignature.SIGN_ALGORITHM_SHA512.equals(signAlgorithm)) {
			// OK
		} else {
			throw new RuntimeException("Tipus d'algorisme no suportat " + signAlgorithm);
		}
		FileInfoSignature fis = new FileInfoSignature(
				signatureId,
				fileToSign,
				mimeType,
				idname,
				reason,
				location,
				signerEmail,
				signNumber,
				languageSign,
				signType,
				signAlgorithm,
				signMode,
				locationSignTableId,
				signaturesTableHeader,
				pdfInfoSignature,
				csvStampInfo,
				userRequiresTimeStamp,
				timeStampGenerator);
		return fis;
	}

	private List<FirmaWebPluginPortafibRest> plugins;
	private static final String PROPERTIES_BASE = "app.plugin.passarelafirma.";
	private List<FirmaWebPluginPortafibRest> getAllPluginsFromProperties() {
		if (plugins == null) {
			log.info("Carregant plugins de passarel.la de firma...");
			plugins = new ArrayList<FirmaWebPluginPortafibRest>();
			String idsStr = GlobalProperties.getInstance().getProperty(PROPERTIES_BASE + "ids");
			log.info("Identificadors de plugins: " + idsStr);
			if (idsStr != null && !idsStr.isEmpty()) {
				String[] ids = idsStr.split(",");
				for (String id: ids) {
					String base = PROPERTIES_BASE + id + ".";
					String nom = GlobalProperties.getInstance().getProperty(base + "nom");
					log.info("Plugin a carregar NOM[" + base + "nom" + "]: " + nom);
					String classe = GlobalProperties.getInstance().getProperty(base + "class");
					if (classe != null) {
						String descripcioCurta = GlobalProperties.getInstance().getProperty(base + "desc");
						Properties pluginProperties = GlobalProperties.getInstance().findByPrefix(base);
						Properties pluginPropertiesProcessat = new Properties();
						for (Object property: pluginProperties.keySet()) {
							String propertyStr = (String)property;
							if (propertyStr.startsWith(base)) {
								String value = GlobalProperties.getInstance().getProperty(propertyStr);
								String nomFinal = propertyStr.substring(base.length());
								pluginPropertiesProcessat.put(
										PROPERTIES_BASE + nomFinal,
										value);
							}
						}
						log.info(" -------------  PLUGIN " + id + "------------------");
						log.info("nom: " + nom);
						log.info("descripcioCurta: " + descripcioCurta);
						log.info("classe: " + classe);
						log.info("properties: " + pluginPropertiesProcessat);
						plugins.add(
								new FirmaWebPluginPortafibRest(
										nom,
										descripcioCurta,
										classe,
										pluginPropertiesProcessat, 
										Long.valueOf(id)));
					} else {
						log.info(" -------------  PLUGIN " + id + "------------------");
						log.info("el plugin no té una classe configurada.");
					}
				}
			}
		}
		return plugins;
	}

	private Map<Long, FirmaWebPluginPortafibRest> instancesCache = new HashMap<Long, FirmaWebPluginPortafibRest>();
	private Map<Long, FirmaWebPluginPortafibRest> pluginsCache = new HashMap<Long, FirmaWebPluginPortafibRest>();
	private FirmaWebPluginPortafibRest getInstanceByPluginId(
			long pluginId) throws Exception {
		FirmaWebPluginPortafibRest instance = instancesCache.get(pluginId);
		if (instance == null) {
			FirmaWebPluginPortafibRest plugin = getPluginFromCache(pluginId);
			if (plugin == null) {
				plugin = getPluginById(pluginId);
				if (plugin == null) {
					return null;
				}
				addPluginToCache(pluginId, plugin);
			}
			instance = new FirmaWebPluginPortafibRest(
					"nom",
					"descripcioCurta",
					"classe",
					plugin.getProperties(),
					pluginId);
			instancesCache.put(pluginId, instance);
		}
		return instance;
	}
	private void addPluginToCache(
			Long pluginId,
			FirmaWebPluginPortafibRest pluginInstance) {
		synchronized (pluginsCache) {
			pluginsCache.put(pluginId, pluginInstance);
		}
	}
	private FirmaWebPluginPortafibRest getPluginFromCache(
			Long pluginId) {
		synchronized (pluginsCache) {
			return pluginsCache.get(pluginId);
		}
	}
	private FirmaWebPluginPortafibRest getPluginById(long pluginId) {
		for (FirmaWebPluginPortafibRest plugin: getAllPluginsFromProperties()) {
			if (plugin.getPluginId() == pluginId) {
				return plugin;
			}
		}
		return null;
	}

	private String getRequestPluginBasePath(
			String base,
			String signaturesSetId,
			int signatureIndex) {
		String absoluteRequestPluginBasePath = base + "/requestPlugin/" + signaturesSetId + "/" + signatureIndex;
		return absoluteRequestPluginBasePath;
	}
	private String getRelativeControllerBase(
			HttpServletRequest request,
			String webContext) {
		return request.getContextPath() + webContext;
	}

	public String getArxiuMimeType(String nomArxiu) {
		DocumentFormat format = formatPerNomArxiu(nomArxiu);
		if (format == null)
			return new MimetypesFileTypeMap().getContentType(nomArxiu);
		else
			return format.getMimeType();
	}
	
	private DocumentFormat formatPerNomArxiu(String fileName) {
		int indexPunt = fileName.lastIndexOf(".");
		if (indexPunt != -1) {
			String extensio = fileName.substring(indexPunt + 1);
			return getDocumentFormatRegistry().getFormatByFileExtension(extensio);
		}
		return null;
	}
	
	private DocumentFormatRegistry getDocumentFormatRegistry() {
		if (documentFormatRegistry == null)
			documentFormatRegistry = new DefaultDocumentFormatRegistry();
		return documentFormatRegistry;
	}
	
	private static Logger log = LoggerFactory.getLogger(PassarelaFirmaHelper.class);

}
