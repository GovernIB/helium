package es.caib.helium.integracio.service.arxiu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.netflix.servo.util.Strings;

import es.caib.helium.integracio.domini.arxiu.Arxiu;
import es.caib.helium.integracio.domini.arxiu.ArxiuFirma;
import es.caib.helium.integracio.domini.arxiu.DocumentArxiu;
import es.caib.helium.integracio.domini.arxiu.ExpedientArxiu;
import es.caib.helium.integracio.enums.arxiu.NtiEstadoElaboracionEnum;
import es.caib.helium.integracio.enums.arxiu.NtiOrigenEnum;
import es.caib.helium.integracio.enums.arxiu.NtiTipoDocumentalEnum;
import es.caib.helium.integracio.excepcions.arxiu.ArxiuException;
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

@Service
public class ArxiuServiceCaibImpl implements ArxiuService {

	@Setter
	private IArxiuPlugin api;
	
	@Override
	public Expedient getExpedient(String uuId) throws ArxiuException {

		try {
			return api.expedientDetalls(uuId, null);
		} catch(Exception ex) {
			throw new ArxiuException("Error obtinguent els detalls de l'expedient " + uuId + " a l'arxiu", ex);
		}
	}

	@Override
	public boolean crearExpedient(ExpedientArxiu expedientArxiu) throws ArxiuException {
		try {
			return  api.expedientCrear(toExpedient(expedientArxiu)) != null;
		} catch (Exception e) {
			throw new ArxiuException("Error al crear l'expedient " + expedientArxiu.getIdentificador() + " a l'arxiu", e);
		}
	}	

	@Override
	public boolean modificarExpedient(ExpedientArxiu expedientArxiu) throws ArxiuException {
		
		try {
			return  api.expedientCrear(toExpedient(expedientArxiu)) != null;
		} catch (Exception e) {
			throw new ArxiuException("Error al modificar l'expedient " + expedientArxiu.getIdentificador() + " a l'arxiu", e);
		}
	}

	@Override
	public boolean deleteExpedient(String uuId) throws ArxiuException {

		try {
			api.expedientEsborrar(uuId);
			return true;
		} catch (Exception ex) {
			throw new ArxiuException("Error esborrant l'expedient " + uuId, ex);
		}
	}

	@Override
	public boolean obrirExpedient(String uuId) throws ArxiuException {
		
		try {
			api.expedientReobrir(uuId);
			return true;
		} catch (Exception ex) {
			throw new ArxiuException("Error obrint l'expedient " + uuId, ex);
		}
	}

	@Override
	public boolean tancarExpedient(String uuId) throws ArxiuException {
		
		try {
			return api.expedientTancar(uuId) != null;
		} catch (Exception ex) {
			throw new ArxiuException("Error tencant l'expedient " + uuId, ex);
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
	public Document getDocument(String uuId, String versio, boolean ambContingut, boolean isSignat) throws ArxiuException {
		
		try {
			var document = api.documentDetalls(uuId, versio, ambContingut);
			if (!ambContingut) {
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
			
			return document; 
		} catch (Exception ex) {
			throw new ArxiuException("Error cercant el document", ex);
		}
	}

	@Override
	public boolean deleteDocument(String uuId) throws ArxiuException {
		
		try {
			api.documentEsborrar(uuId);
			return true;
		} catch (Exception e) {
			throw new ArxiuException("Error esborrant el document" + uuId, e);
		}
	}
	
	@Override
	public boolean crearDocument(DocumentArxiu document) throws ArxiuException {

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
			return api.documentCrear(arxiuDoc, document.getUuid()) != null;
		} catch (Exception ex) {
			throw new ArxiuException("Error creant el document", ex);
		}
	}

	@Override
	public boolean modificarDocument(DocumentArxiu document) throws ArxiuException {
		
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
			return api.documentModificar(arxiuDoc) != null;
		} catch (Exception ex) {
			throw new ArxiuException("Error creant el document", ex);
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
	
}
