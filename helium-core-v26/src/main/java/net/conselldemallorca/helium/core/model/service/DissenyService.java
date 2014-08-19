/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.conselldemallorca.helium.core.extern.domini.FilaResultat;
import net.conselldemallorca.helium.core.model.dao.AccioDao;
import net.conselldemallorca.helium.core.model.dao.CampAgrupacioDao;
import net.conselldemallorca.helium.core.model.dao.CampDao;
import net.conselldemallorca.helium.core.model.dao.CampRegistreDao;
import net.conselldemallorca.helium.core.model.dao.CampTascaDao;
import net.conselldemallorca.helium.core.model.dao.ConsultaCampDao;
import net.conselldemallorca.helium.core.model.dao.ConsultaDao;
import net.conselldemallorca.helium.core.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.core.model.dao.DocumentDao;
import net.conselldemallorca.helium.core.model.dao.DocumentStoreDao;
import net.conselldemallorca.helium.core.model.dao.DocumentTascaDao;
import net.conselldemallorca.helium.core.model.dao.DominiDao;
import net.conselldemallorca.helium.core.model.dao.EntornDao;
import net.conselldemallorca.helium.core.model.dao.EnumeracioDao;
import net.conselldemallorca.helium.core.model.dao.EnumeracioValorsDao;
import net.conselldemallorca.helium.core.model.dao.EstatDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientDao;
import net.conselldemallorca.helium.core.model.dao.ExpedientTipusDao;
import net.conselldemallorca.helium.core.model.dao.FirmaTascaDao;
import net.conselldemallorca.helium.core.model.dao.LuceneDao;
import net.conselldemallorca.helium.core.model.dao.MapeigSistraDao;
import net.conselldemallorca.helium.core.model.dao.SequenciaAnyDao;
import net.conselldemallorca.helium.core.model.dao.TascaDao;
import net.conselldemallorca.helium.core.model.dao.TerminiDao;
import net.conselldemallorca.helium.core.model.dao.ValidacioDao;
import net.conselldemallorca.helium.core.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.core.model.exception.DeploymentException;
import net.conselldemallorca.helium.core.model.exception.DominiException;
import net.conselldemallorca.helium.core.model.exception.ExportException;
import net.conselldemallorca.helium.core.model.exportacio.AccioExportacio;
import net.conselldemallorca.helium.core.model.exportacio.AgrupacioExportacio;
import net.conselldemallorca.helium.core.model.exportacio.CampExportacio;
import net.conselldemallorca.helium.core.model.exportacio.CampTascaExportacio;
import net.conselldemallorca.helium.core.model.exportacio.ConsultaCampExportacio;
import net.conselldemallorca.helium.core.model.exportacio.ConsultaExportacio;
import net.conselldemallorca.helium.core.model.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.core.model.exportacio.DocumentExportacio;
import net.conselldemallorca.helium.core.model.exportacio.DocumentTascaExportacio;
import net.conselldemallorca.helium.core.model.exportacio.DominiExportacio;
import net.conselldemallorca.helium.core.model.exportacio.EnumeracioExportacio;
import net.conselldemallorca.helium.core.model.exportacio.EstatExportacio;
import net.conselldemallorca.helium.core.model.exportacio.ExpedientTipusExportacio;
import net.conselldemallorca.helium.core.model.exportacio.FirmaTascaExportacio;
import net.conselldemallorca.helium.core.model.exportacio.MapeigSistraExportacio;
import net.conselldemallorca.helium.core.model.exportacio.RegistreMembreExportacio;
import net.conselldemallorca.helium.core.model.exportacio.TascaExportacio;
import net.conselldemallorca.helium.core.model.exportacio.TerminiExportacio;
import net.conselldemallorca.helium.core.model.exportacio.ValidacioExportacio;
import net.conselldemallorca.helium.core.model.hibernate.Accio;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.CampTasca;
import net.conselldemallorca.helium.core.model.hibernate.Consulta;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusParamConsultaCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.core.model.hibernate.Domini;
import net.conselldemallorca.helium.core.model.hibernate.Domini.TipusDomini;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.core.model.hibernate.EnumeracioValors;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra;
import net.conselldemallorca.helium.core.model.hibernate.MapeigSistra.TipusMapeig;
import net.conselldemallorca.helium.core.model.hibernate.Tasca;
import net.conselldemallorca.helium.core.model.hibernate.Tasca.TipusTasca;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.core.model.hibernate.Validacio;
import net.conselldemallorca.helium.core.security.AclServiceDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar les tasques de disseny
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DissenyService {

	private DefinicioProcesDao definicioProcesDao;
	private EntornDao entornDao;
	private TascaDao tascaDao;
	private CampDao campDao;
	private CampTascaDao campTascaDao;
	private CampRegistreDao campRegistreDao;
	private DocumentStoreDao documentStoreDao;
	private DocumentDao documentDao;
	private DocumentTascaDao documentTascaDao;
	private FirmaTascaDao firmaTascaDao;
	private ValidacioDao validacioDao;
	private ExpedientTipusDao expedientTipusDao;
	private ExpedientDao expedientDao;
	private EnumeracioDao enumeracioDao;
	private EnumeracioValorsDao enumeracioValorsDao;
	private TerminiDao terminiDao;
	private EstatDao estatDao;
	private MapeigSistraDao mapeigSistraDao;
	private DominiDao dominiDao;
	private CampAgrupacioDao campAgrupacioDao;
	private ConsultaDao consultaDao;
	private ConsultaCampDao consultaCampDao;
	private AccioDao accioDao;
	private SequenciaAnyDao sequenciaAnyDao;

	private DtoConverter dtoConverter;
	private JbpmHelper jbpmDao;
	private LuceneDao luceneDao;
	private AclServiceDao aclServiceDao;
	private MessageSource messageSource;

	private ServiceUtils serviceUtils;

	private Map<Long, Boolean> hasStartTask = new HashMap<Long, Boolean>();



	public DefinicioProces deploy(
			Long entornId,
			Long expedientTipusId,
			String nomArxiu,
			byte[] contingut,
			String etiqueta,
			boolean copiarDades) {
		JbpmProcessDefinition dpd = jbpmDao.desplegar(nomArxiu, contingut);
		if (dpd != null) {
			DefinicioProces darrera = definicioProcesDao.findDarreraVersioAmbEntornIJbpmKey(
					entornId,
					dpd.getKey());
			if (darrera != null) {
				if ((darrera.getExpedientTipus() != null && expedientTipusId == null)) {
					throw new DeploymentException(
							getServiceUtils().getMessage("error.dissenyService.defprocDesplTipusExp", new Object[]{darrera.getExpedientTipus().getNom()}));
				}
				if (darrera.getExpedientTipus() == null && expedientTipusId != null) {
					throw new DeploymentException(
							getServiceUtils().getMessage("error.dissenyService.defprocDesplEntorn"));
				}
				if (darrera.getExpedientTipus() != null && expedientTipusId != null) {
					if (expedientTipusId.longValue() != darrera.getExpedientTipus().getId().longValue()) {
						throw new DeploymentException("Aquesta definició de procés ja està desplegada a dins el tipus d'expedient \"" + darrera.getExpedientTipus().getNom() + "\"");
					}
				}
			}
			Entorn entorn = entornDao.getById(entornId, false);
			// Crea la nova definició de procés
			DefinicioProces definicioProces = new DefinicioProces(
					dpd.getId(),
					dpd.getKey(),
					dpd.getVersion(),
					entorn);
			if (etiqueta != null || etiqueta !="")
				definicioProces.setEtiqueta(etiqueta);
			if (expedientTipusId != null)
				definicioProces.setExpedientTipus(expedientTipusDao.getById(expedientTipusId, false));
			definicioProcesDao.saveOrUpdate(definicioProces);
			// Crea les tasques de la definició de procés
			for (String nomTasca: jbpmDao.getTaskNamesFromDeployedProcessDefinition(dpd)) {
				Tasca tasca = new Tasca(
						definicioProces,
						nomTasca,
						nomTasca,
						TipusTasca.ESTAT);
				String recursForm = getRecursFormPerTasca(dpd.getId(), nomTasca);
				if (recursForm != null) {
					tasca.setTipus(TipusTasca.FORM);
					tasca.setRecursForm(recursForm);
				}
				definicioProces.addTasca(tasca);
			}
			// Mira si ha de copiar les dades de la darrera versió
			if (copiarDades) {
				if (darrera != null) {
					copiarDadesDefinicioProces(
							darrera,
							definicioProces);
				}
			}
			return definicioProces;
		} else {
			throw new DeploymentException(
					getServiceUtils().getMessage("error.dissenyService.noConte"));
		}
	}

	public void undeploy(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId) {
		if (expedientTipusId == null) {
			if (comprovarEntorn(entornId, definicioProcesId)) {
				DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
				jbpmDao.esborrarDesplegament(definicioProces.getJbpmId());
				for (Document doc: definicioProces.getDocuments())
					documentDao.delete(doc.getId());
				for (Termini termini: definicioProces.getTerminis())
					deleteTermini(termini.getId());
				definicioProcesDao.delete(definicioProcesId);
			} else {
				throw new IllegalArgumentException(
						getServiceUtils().getMessage("error.dissenyService.noEntorn"));
			}
		} else {
			if (comprovarExpedientTipus(expedientTipusId, definicioProcesId)) {
				DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
				jbpmDao.esborrarDesplegament(definicioProces.getJbpmId());
				for (Document doc: definicioProces.getDocuments())
					documentDao.delete(doc.getId());
				for (Termini termini: definicioProces.getTerminis())
					deleteTermini(termini.getId());
				definicioProcesDao.delete(definicioProcesId);
			} else {
				throw new IllegalArgumentException(
						getServiceUtils().getMessage("error.dissenyService.noTipusExp"));
			}
		}
	}

	public DefinicioProcesDto getById(
			Long id,
			boolean ambTascaInicial) {
			DefinicioProces definicioProces = definicioProcesDao.getById(id, false);
			return toDto(definicioProces, ambTascaInicial);
	}

	public DefinicioProcesDto getByIdAmbComprovacio(
			Long entornId,
			Long id) {
		if (comprovarEntorn(entornId, id)) {
			DefinicioProces definicioProces = definicioProcesDao.getById(id, false);
			return toDto(definicioProces, false);
		} else {
			throw new IllegalArgumentException(
					getServiceUtils().getMessage("error.dissenyService.noEntorn"));
		}
	}

	public List<DefinicioProcesDto> findDarreresAmbEntorn(Long entornId) {
		List<DefinicioProcesDto> resposta = new ArrayList<DefinicioProcesDto>();
		List<DefinicioProces> darreres = definicioProcesDao.findDarreresVersionsAmbEntorn(entornId);
		for (DefinicioProces definicioProces: darreres)
			resposta.add(toDto(definicioProces, false));
		return resposta;
	}
	public List<DefinicioProcesDto> findDarreresAmbExpedientTipusEntorn(
			Long entornId,
			Long expedientTipusId,
			boolean incloureGlobals) {
		List<DefinicioProcesDto> resposta = new ArrayList<DefinicioProcesDto>();
		List<DefinicioProces> dps = definicioProcesDao.findDarreresVersionsAmbEntorn(entornId);
		for (DefinicioProces definicionProces: dps) {
			
			if (	(definicionProces.getExpedientTipus() != null && expedientTipusId.equals(definicionProces.getExpedientTipus().getId())) ||
					(incloureGlobals && definicionProces.getExpedientTipus() == null))
				resposta.add(toDto(definicionProces, false));
		}
		return resposta;
	}

	public List<DefinicioProcesDto> findSubDefinicionsProces(Long id) {
		List<DefinicioProcesDto> resposta = new ArrayList<DefinicioProcesDto>();
		DefinicioProces definicioProces = definicioProcesDao.getById(id, false);
		List<JbpmProcessDefinition> subpds = jbpmDao.getSubProcessDefinitions(definicioProces.getJbpmId());
		for (JbpmProcessDefinition jbpmProcessDefinition: subpds) {
			resposta.add(toDto(definicioProcesDao.findAmbJbpmId(jbpmProcessDefinition.getId()), false));
		}
		return resposta;
	}

	public DefinicioProcesDto findDarreraAmbExpedientTipus(Long expedientTipusId) {
		ExpedientTipus expedientTipus = expedientTipusDao.getById(expedientTipusId, false);
		return toDto(definicioProcesDao.findDarreraVersioAmbEntornIJbpmKey(
				expedientTipus.getEntorn().getId(),
				expedientTipus.getJbpmProcessDefinitionKey()),
				false);
	}

	public Tasca getTascaById(Long id) {
		return tascaDao.getById(id, false);
	}
	public Tasca updateTasca(Tasca entity) {
		Tasca tasca = getTascaById(entity.getId());
		tasca.setNom(entity.getNom());
		tasca.setTipus(entity.getTipus());
		tasca.setMissatgeInfo(entity.getMissatgeInfo());
		tasca.setMissatgeWarn(entity.getMissatgeWarn());
		tasca.setNomScript(entity.getNomScript());
		tasca.setExpressioDelegacio(entity.getExpressioDelegacio());
		tasca.setRecursForm(entity.getRecursForm());
		tasca.setFormExtern(entity.getFormExtern());
		return tasca;
	}
	public List<Tasca> findTasquesAmbDefinicioProces(Long definicioProcesId) {
		return tascaDao.findAmbDefinicioProces(definicioProcesId);
	}

	public Camp getCampById(Long id) {
		return campDao.getById(id, false);
	}
	public Camp createCamp(Camp entity) {
		if (entity.getAgrupacio() != null) {
			Integer maxOrdre = campDao.getNextOrdre(
					entity.getDefinicioProces().getId(),
					entity.getAgrupacio().getId());
			entity.setOrdre(maxOrdre);
		}
		Camp saved = campDao.saveOrUpdate(entity);
		return saved;
	}
	public Camp updateCamp(Camp entity) {
		if ((entity.getAgrupacio() != null) && (entity.getOrdre() == null)) {
			Integer maxOrdre = campDao.getNextOrdre(
					entity.getDefinicioProces().getId(),
					entity.getAgrupacio().getId());
			entity.setOrdre(maxOrdre);
		}
		return campDao.merge(entity);
	}
	public void deleteCamp(Long id) {
		Camp vell = getCampById(id);
		if (vell != null) {
			for (CampTasca campTasca: vell.getCampsTasca()) {
				campTasca.getTasca().removeCamp(campTasca);
				int i = 0;
				for (CampTasca ct: campTasca.getTasca().getCamps())
					ct.setOrder(i++);
			}
			campDao.delete(id);
			if (vell.getAgrupacio() != null)
				reordenarCamps(vell.getDefinicioProces().getId(), vell.getAgrupacio().getId());
		}
	}
	public List<Camp> findCampsAmbDefinicioProces(Long definicioProcesId) {
		return campDao.findAmbDefinicioProces(definicioProcesId);
	}
	public List<Camp> findCampsAmbDefinicioProcesOrdenatsPerCodi(Long definicioProcesId) {
		return campDao.findAmbDefinicioProcesOrdenatsPerCodi(definicioProcesId);
	}
	public Camp findCampAmbDefinicioProcesICodi(Long definicioProcesId, String codi) {
		return campDao.findAmbDefinicioProcesICodi(definicioProcesId, codi);
	}
	public Camp findCampAmbDefinicioProcesICodiSimple(Long definicioProcesId, String codi) {
		return campDao.findAmbDefinicioProcesICodiSimple(definicioProcesId, codi);
	}
	public List<Camp> findCampAmbDefinicioProcesITipus(Long definicioProcesId, TipusCamp tipus) {
		return campDao.findAmbDefinicioProcesITipus(definicioProcesId, tipus);
	}
	public List<Camp> findCampAmbDefinicioProcesIMultiple(Long definicioProcesId) {
		return campDao.findAmbDefinicioProcesIMultiple(definicioProcesId);
	}

	public CampTasca getCampTascaById(Long id) {
		return campTascaDao.getById(id, false);		
	}
	public CampTasca addCampTasca(
			Long tascaId,
			Long campId,
			boolean readFrom,
			boolean writeTo,
			boolean required,
			boolean readOnly) {
		CampTasca existent = campTascaDao.findAmbTascaCamp(tascaId, campId);
		if (existent != null) {
			existent.setReadFrom(readFrom);
			existent.setWriteTo(writeTo);
			existent.setRequired(required);
			existent.setReadOnly(readOnly);
			return existent;
		} else {
			CampTasca campTasca = new CampTasca(
					campDao.getById(campId, false),
					tascaDao.getById(tascaId, false),
					readFrom,
					writeTo,
					required,
					readOnly,
					campTascaDao.getNextOrder(tascaId));
			return campTascaDao.saveOrUpdate(campTasca);
		}
	}
	public void deleteCampTasca(Long id) {
		CampTasca vell = getCampTascaById(id);
		if (vell != null) {
			vell.getTasca().removeCamp(vell);
			campTascaDao.delete(id);
			reordenarCampsTasca(vell.getTasca().getId());
		}
	}
	public List<CampTasca> findCampTascaAmbTasca(Long tascaId) {
		return campTascaDao.findAmbTascaOrdenats(tascaId);
	}
	public void goUpCampTasca(Long id) {
		CampTasca campTasca = getCampTascaById(id);
		int ordreActual = campTasca.getOrder();
		CampTasca anterior = campTascaDao.getAmbOrdre(
				campTasca.getTasca().getId(),
				ordreActual - 1);
		if (anterior != null) {
			campTasca.setOrder(-1);
			anterior.setOrder(ordreActual);
			campTascaDao.merge(campTasca);
			campTascaDao.merge(anterior);
			campTascaDao.flush();
			campTasca.setOrder(ordreActual - 1);
		}
	}
	public void goDownCampTasca(Long id) {
		CampTasca campTasca = getCampTascaById(id);
		int ordreActual = campTasca.getOrder();
		CampTasca seguent = campTascaDao.getAmbOrdre(
				campTasca.getTasca().getId(),
				ordreActual + 1);
		if (seguent != null) {
			campTasca.setOrder(-1);
			seguent.setOrder(ordreActual);
			campTascaDao.merge(campTasca);
			campTascaDao.merge(seguent);
			campTascaDao.flush();
			campTasca.setOrder(ordreActual + 1);
		}
	}

	public Document getDocumentById(Long id) {
		return documentDao.getById(id, false);
	}

	public Document getDocumentStoreById(Long id) {
		DocumentStore documentStore = documentStoreDao.getById(id, false);
		Document document = new Document();
		document.setId(documentStore.getId());
		document.setCodi(documentStore.getCodiDocument());
		document.setArxiuContingut(documentStore.getArxiuContingut());
		document.setArxiuNom(documentStore.getArxiuNom());		
		return document;
	}
	
	public Document createDocument(Document entity) {
		Document saved = documentDao.saveOrUpdate(entity);
		return saved;
	}
	public Document updateDocument(Document entity, boolean delete) {
		Document vella = documentDao.getById(entity.getId(), false);
		if (vella != null && !delete) {
			if (entity.getArxiuContingut() == null || entity.getArxiuContingut().length == 0) {
				entity.setArxiuNom(vella.getArxiuNom());
				entity.setArxiuContingut(vella.getArxiuContingut());
			}
		}
		return documentDao.merge(entity);
	}
	public void deleteDocument(Long id) {
		Document vell = getDocumentById(id);
		if (vell != null) {
			for (DocumentTasca documentTasca: vell.getTasques()) {
				documentTasca.getTasca().removeDocument(documentTasca);
				int i = 0;
				for (DocumentTasca dt: documentTasca.getTasca().getDocuments())
					dt.setOrder(i++);
			}
			documentDao.delete(id);
		}
	}
	public List<Document> findDocumentsAmbDefinicioProces(Long definicioProcesId) {
		return documentDao.findAmbDefinicioProces(definicioProcesId);
	}
	public List<Document> findDocumentsAmbDefinicioProcesOrdenatsPerCodi(Long definicioProcesId) {
		return documentDao.findAmbDefinicioProcesOrdenatsPerCodi(definicioProcesId);
	}
	public Document findDocumentAmbDefinicioProcesICodi(Long definicioProcesId, String codi) {
		return documentDao.findAmbDefinicioProcesICodi(definicioProcesId, codi);
	}

	public DocumentTasca getDocumentTascaById(Long id) {
		return documentTascaDao.getById(id, false);		
	}
	public DocumentTasca addDocumentTasca(
			Long documentId,
			Long tascaId,
			boolean required,
			boolean readOnly) {
		DocumentTasca existent = documentTascaDao.findAmbDocumentTasca(documentId, tascaId);
		if (existent != null) {
			existent.setRequired(required);
			existent.setReadOnly(readOnly);
			return existent;
		} else {
			DocumentTasca documentTasca = new DocumentTasca(
					documentDao.getById(documentId, false),
					tascaDao.getById(tascaId, false),
					required,
					readOnly,
					documentTascaDao.getNextOrder(tascaId));
			return documentTascaDao.saveOrUpdate(documentTasca);
		}
	}
	public void deleteDocumentTasca(Long id) {
		DocumentTasca vell = getDocumentTascaById(id);
		if (vell != null) {
			vell.getTasca().removeDocument(vell);
			vell.getDocument().removeTasca(vell);
			documentTascaDao.delete(id);
			reordenarDocumentsTasca(vell.getTasca().getId());
		}
	}
	public List<DocumentTasca> findDocumentTascaAmbTasca(Long tascaId) {
		return documentTascaDao.findAmbTascaOrdenats(tascaId);
	}
	public void goUpDocumentTasca(Long id) {
		DocumentTasca documentTasca = getDocumentTascaById(id);
		int ordreActual = documentTasca.getOrder();
		DocumentTasca anterior = documentTascaDao.getAmbOrdre(
				documentTasca.getTasca().getId(),
				ordreActual - 1);
		if (anterior != null) {
			documentTasca.setOrder(-1);
			documentTascaDao.merge(documentTasca);
			documentTascaDao.flush();
			anterior.setOrder(ordreActual);
			documentTascaDao.merge(anterior);
			documentTascaDao.flush();
			documentTasca.setOrder(ordreActual - 1);
		}
	}
	public void goDownDocumentTasca(Long id) {
		DocumentTasca documentTasca = getDocumentTascaById(id);
		int ordreActual = documentTasca.getOrder();
		DocumentTasca seguent = documentTascaDao.getAmbOrdre(
				documentTasca.getTasca().getId(),
				ordreActual + 1);
		if (seguent != null) {
			documentTasca.setOrder(-1);
			documentTascaDao.merge(documentTasca);
			documentTascaDao.flush();
			seguent.setOrder(ordreActual);
			documentTascaDao.merge(seguent);
			documentTascaDao.flush();
			documentTasca.setOrder(ordreActual + 1);
		}
	}
	
	public FirmaTasca getFirmaTascaById(Long id) {
		return firmaTascaDao.getById(id, false);		
	}
	public FirmaTasca addFirmaTasca(
			Long documentId,
			Long tascaId,
			boolean required) {
		FirmaTasca existent = firmaTascaDao.findAmbDocumentTasca(documentId, tascaId);
		if (existent != null) {
			existent.setRequired(required);
			return existent;
		} else {
			FirmaTasca firmaTasca = new FirmaTasca(
					documentDao.getById(documentId, false),
					tascaDao.getById(tascaId, false),
					required,
					firmaTascaDao.getNextOrder(tascaId));
			return firmaTascaDao.saveOrUpdate(firmaTasca);
		}
	}
	public void deleteFirmaTasca(Long id) {
		FirmaTasca vell = getFirmaTascaById(id);
		if (vell != null) {
			firmaTascaDao.delete(id);
			reordenarFirmesTasca(vell.getTasca().getId());
		}
	}
	public List<FirmaTasca> findFirmaTascaAmbTasca(Long tascaId) {
		return firmaTascaDao.findAmbTascaOrdenats(tascaId);
	}
	public void goUpFirmaTasca(Long id) {
		FirmaTasca firmaTasca = getFirmaTascaById(id);
		int ordreActual = firmaTasca.getOrder();
		FirmaTasca anterior = firmaTascaDao.getAmbOrdre(
				firmaTasca.getTasca().getId(),
				ordreActual - 1);
		if (anterior != null) {
			firmaTasca.setOrder(-1);
			anterior.setOrder(ordreActual);
			firmaTascaDao.merge(firmaTasca);
			firmaTascaDao.merge(anterior);
			firmaTascaDao.flush();
			firmaTasca.setOrder(ordreActual - 1);
		}
	}
	public void goDownFirmaTasca(Long id) {
		FirmaTasca firmaTasca = getFirmaTascaById(id);
		int ordreActual = firmaTasca.getOrder();
		FirmaTasca seguent = firmaTascaDao.getAmbOrdre(
				firmaTasca.getTasca().getId(),
				ordreActual + 1);
		if (seguent != null) {
			firmaTasca.setOrder(-1);
			seguent.setOrder(ordreActual);
			firmaTascaDao.merge(firmaTasca);
			firmaTascaDao.merge(seguent);
			firmaTascaDao.flush();
			firmaTasca.setOrder(ordreActual + 1);
		}
	}

	public Validacio getValidacioById(Long id) {
		return validacioDao.getById(id, false);
	}
	public Validacio createValidacioCamp(Long campId, String expressio, String missatge) {
		Validacio validacio = new Validacio(campDao.getById(campId, false), expressio, missatge);
		validacio.setOrdre(validacioDao.getNextOrderPerCamp(campId));
		return validacioDao.saveOrUpdate(validacio);
	}
	public List<Validacio> findValidacionsAmbCamp(Long campId) {
		return validacioDao.findAmbCampOrdenats(campId);
	}
	public Validacio createValidacioTasca(Long tascaId, String expressio, String missatge) {
		Validacio validacio = new Validacio(tascaDao.getById(tascaId, false), expressio, missatge);
		validacio.setOrdre(validacioDao.getNextOrderPerTasca(tascaId));
		return validacioDao.saveOrUpdate(validacio);
	}
	public List<Validacio> findValidacionsAmbTasca(Long tascaId) {
		return validacioDao.findAmbTascaOrdenats(tascaId);
	}
	public void goUpValidacio(Long id) {
		Validacio validacio = validacioDao.getById(id, false);
		int ordreActual = validacio.getOrdre();
		Validacio anterior;
		if (validacio.getTasca() != null)
			anterior = validacioDao.getAmbOrdrePerTasca(
					validacio.getTasca().getId(),
					ordreActual - 1);
		else
			anterior = validacioDao.getAmbOrdrePerCamp(
					validacio.getCamp().getId(),
					ordreActual - 1);
		if (anterior != null) {
			validacio.setOrdre(-1);
			anterior.setOrdre(ordreActual);
			validacioDao.merge(validacio);
			validacioDao.merge(anterior);
			validacioDao.flush();
			validacio.setOrdre(ordreActual - 1);
		}
	}
	public void goDownValidacio(Long id) {
		Validacio validacio = validacioDao.getById(id, false);
		int ordreActual = validacio.getOrdre();
		Validacio seguent;
		if (validacio.getTasca() != null) {
			seguent = validacioDao.getAmbOrdrePerTasca(
					validacio.getTasca().getId(),
					ordreActual + 1);
		} else {
			seguent = validacioDao.getAmbOrdrePerCamp(
					validacio.getCamp().getId(),
					ordreActual + 1);
		}
		if (seguent != null) {
			validacio.setOrdre(-1);
			seguent.setOrdre(ordreActual);
			validacioDao.merge(validacio);
			validacioDao.merge(seguent);
			validacioDao.flush();
			validacio.setOrdre(ordreActual + 1);
		}
	}
	public void deleteValidacio(Long id) {
		Validacio vell = getValidacioById(id);
		if (vell != null) {
			if (vell.getTasca() != null) {
				Long tascaId = vell.getTasca().getId();
				validacioDao.delete(id);
				reordenarValidacionsTasca(tascaId);
			} else {
				Long campId = vell.getCamp().getId();
				validacioDao.delete(id);
				reordenarValidacionsCamp(campId);
			}
		}
	}

	public CampRegistre getCampRegistreById(Long id) {
		return campRegistreDao.getById(id, false);		
	}
	public CampRegistre addCampRegistre(
			Long registreId,
			Long membreId,
			boolean obligatori,
			boolean llistar) {
		CampRegistre existent = campRegistreDao.findAmbRegistreMembre(registreId, membreId);
		if (existent != null) {
			existent.setObligatori(obligatori);
			existent.setLlistar(llistar);
			return existent;
		} else {
			CampRegistre campRegistre = new CampRegistre(
					campDao.getById(registreId, false),
					campDao.getById(membreId, false),
					campRegistreDao.getNextOrder(registreId));
			campRegistre.setObligatori(obligatori);
			campRegistre.setLlistar(llistar);
			return campRegistreDao.saveOrUpdate(campRegistre);
		}
	}
	public void deleteCampRegistre(Long id) {
		CampRegistre vell = getCampRegistreById(id);
		if (vell != null) {
			vell.getRegistre().removeRegistreMembre(vell);
			campRegistreDao.delete(id);
			reordenarMembresRegistre(vell.getRegistre().getId());
		}
	}
	public List<CampRegistre> findCampMembreAmbRegistre(Long registreId) {
		return campRegistreDao.findAmbRegistreOrdenats(registreId);
	}
	public void goUpCampRegistre(Long id) {
		CampRegistre campRegistre = getCampRegistreById(id);
		int ordreActual = campRegistre.getOrdre();
		CampRegistre anterior = campRegistreDao.getAmbOrdre(
				campRegistre.getRegistre().getId(),
				ordreActual - 1);
		if (anterior != null) {
			campRegistre.setOrdre(-1);
			anterior.setOrdre(ordreActual);
			campRegistreDao.merge(campRegistre);
			campRegistreDao.merge(anterior);
			campRegistreDao.flush();
			campRegistre.setOrdre(ordreActual - 1);
		}
	}
	public void goDownCampRegistre(Long id) {
		CampRegistre campRegistre = getCampRegistreById(id);
		int ordreActual = campRegistre.getOrdre();
		CampRegistre seguent = campRegistreDao.getAmbOrdre(
				campRegistre.getRegistre().getId(),
				ordreActual + 1);
		if (seguent != null) {
			campRegistre.setOrdre(-1);
			seguent.setOrdre(ordreActual);
			campRegistreDao.merge(campRegistre);
			campRegistreDao.merge(seguent);
			campRegistreDao.flush();
			campRegistre.setOrdre(ordreActual + 1);
		}
	}

	public ExpedientTipus getExpedientTipusById(Long id) {
		return expedientTipusDao.getById(id, false);
	}
	public ExpedientTipus createExpedientTipus(ExpedientTipus entity) {
		ExpedientTipus saved = expedientTipusDao.saveOrUpdate(entity);
		return saved;
	}
	public ExpedientTipus updateExpedientTipus(ExpedientTipus entity) {
		ExpedientTipus et = expedientTipusDao.merge(entity);
		sequenciaAnyDao.clearSequencies();
		return et;
	}
	public void deleteExpedientTipus(Long id) {
		ExpedientTipus vell = getExpedientTipusById(id);
		if (vell != null) {
			Iterator<DefinicioProces> it = vell.getDefinicionsProces().iterator();
			while (it.hasNext()) {
				DefinicioProces definicioProces = it.next();
				it.remove();
				undeploy(
						definicioProces.getEntorn().getId(),
						vell.getId(),
						definicioProces.getId());
			}
			for (Estat estat : findEstatAmbExpedientTipus(id)) {
				estatDao.delete(estat);
			}
			for (Consulta consulte : findConsultesAmbEntornAmbOSenseTipusExp(vell.getEntorn().getId(), id)) {
				consultaDao.delete(consulte);
			}
			expedientTipusDao.delete(id);
		}
	}
	public List<ExpedientTipus> findExpedientTipusAmbEntorn(Long entornId) {
		return expedientTipusDao.findAmbEntorn(entornId);
	}
	public List<ExpedientTipus> findExpedientTipusAmbEntornOrdenat(Long entornId, String ordre) {
		return expedientTipusDao.findAmbEntornOrdenat(entornId, ordre);
	}
	public ExpedientTipus findExpedientTipusAmbEntornICodi(Long entornId, String codi) {
		return expedientTipusDao.findAmbEntornICodi(entornId, codi);
	}
	public List<ExpedientTipus> findExpedientTipusAmbSistraTramitCodi(String tramitCodi) {
		return expedientTipusDao.findAmbSistraTramitCodi(tramitCodi);
	}
	public List<ExpedientTipus> findExpedientTipusTots() {
		return expedientTipusDao.findAll();
	}
	public DefinicioProcesDto findDarreraDefinicioProcesForExpedientTipus(
			Long expedientTipusId,
			boolean ambTascaInicial) {
		ExpedientTipus expedientTipus = getExpedientTipusById(expedientTipusId);
		if (expedientTipus.getJbpmProcessDefinitionKey() != null) {
			DefinicioProces definicioProces = definicioProcesDao.findDarreraVersioAmbEntornIJbpmKey(
					expedientTipus.getEntorn().getId(),
					expedientTipus.getJbpmProcessDefinitionKey());
			if (definicioProces != null)
				return toDto(definicioProces, ambTascaInicial);
		}
		return null;
	}
	public void setDefinicioProcesInicialPerExpedientTipus(
			Long id,
			String jbpmProcessDefinitionKey) {
		ExpedientTipus expedientTipus = getExpedientTipusById(id);
		expedientTipus.setJbpmProcessDefinitionKey(jbpmProcessDefinitionKey);
	}

	public Set<String> findDeploymentResources(Long definicioProcesId) {
		DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
		return jbpmDao.getResourceNames(definicioProces.getJbpmId());
	}
	public byte[] getDeploymentResource(
			Long definicioProcesId,
			String resourceName) {
		DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
		return jbpmDao.getResourceBytes(
				definicioProces.getJbpmId(),
				resourceName);
	}
	public byte[] getImatgeDefinicioProces(
			Long definicioProcesId) {
		return getDeploymentResource(definicioProcesId, "processimage.jpg");
	}

	public Enumeracio getEnumeracioById(Long id) {
		return enumeracioDao.getById(id, false);
	}
	public Enumeracio createEnumeracio(Enumeracio entity) {
		Enumeracio saved = enumeracioDao.saveOrUpdate(entity);
		return saved;
	}
	public Enumeracio updateEnumeracio(Enumeracio entity) {
		return enumeracioDao.merge(entity);
	}
	public void deleteEnumeracio(Long id) {
		Enumeracio vell = getEnumeracioById(id);
		if (vell != null)
			enumeracioDao.delete(id);
	}
	public List<Enumeracio> findEnumeracionsAmbEntornSenseTipusExp(Long entornId) {
		return enumeracioDao.findAmbEntornSenseTipusExp(entornId);
	}
	public List<Enumeracio> findEnumeracionsAmbEntornITipusExp(Long entornId, Long tipusExpId) {
		return enumeracioDao.findAmbEntornITipusExp(entornId, tipusExpId);
	}
	public List<Enumeracio> findEnumeracionsAmbEntornAmbOSenseTipusExp(Long entornId, Long tipusExpId) {
		return enumeracioDao.findAmbEntornAmbOSenseTipusExp(entornId, tipusExpId);
	}
	public Enumeracio findAmbEntornSenseTipusExpICodi(Long entornId, String codi) {
		return enumeracioDao.findAmbEntornSenseTipusExpICodi(entornId, codi);
	}
	
	public Enumeracio findAmbEntornTipusExpICodi(Long entornId, Long tipusExp, String codi) {
		return enumeracioDao.findAmbEntornAmbTipusExpICodi(entornId ,tipusExp, codi);
	}
	
	public EnumeracioValors getEnumeracioValorsById(Long id) {
		return enumeracioValorsDao.getById(id, false);
	}
	public EnumeracioValors createEnumeracioValors(EnumeracioValors entity) {
		entity.setOrdre(enumeracioValorsDao.getNextOrder(entity.getEnumeracio().getId()));
		EnumeracioValors saved = enumeracioValorsDao.saveOrUpdate(entity);
		return saved;
	}
	public EnumeracioValors createOrUpdateEnumeracioValors(EnumeracioValors entity) {
		EnumeracioValors enumeracioValors = findEnumeracioValorsAmbCodi(
				entity.getEnumeracio().getId(), 
				entity.getCodi());
		if (enumeracioValors == null) {			
			return createEnumeracioValors(entity);
		} else {
			enumeracioValors.setNom(entity.getNom());
			return updateEnumeracioValors(enumeracioValors);
		}
	}
	public EnumeracioValors updateEnumeracioValors(EnumeracioValors entity) {
		return enumeracioValorsDao.merge(entity);
	}
	public void deleteEnumeracioValors(Long id) {
		EnumeracioValors vell = getEnumeracioValorsById(id);
		if (vell != null) {
			enumeracioValorsDao.delete(id);
			reordenarEnumeracioValors(vell.getEnumeracio().getId());
		}
	}
	public List<EnumeracioValors> findEnumeracioValorsAmbEnumeracio(Long enumeracioId) {
		return enumeracioValorsDao.findAmbEnumeracioOrdenat(enumeracioId);
	}
	public EnumeracioValors findEnumeracioValorsAmbCodi(Long enumeracioId, String codi) {
		return enumeracioValorsDao.findAmbEnumeracioICodi(enumeracioId, codi);
	}
	
	public void goUpEnumeracioValor(Long id) {
		EnumeracioValors valor = enumeracioValorsDao.getById(id, false);
		int ordreActual = valor.getOrdre();
		EnumeracioValors anterior = enumeracioValorsDao.getAmbOrdre(
				valor.getEnumeracio().getId(),
				ordreActual - 1);
		if (anterior != null) {
			valor.setOrdre(-1);
			anterior.setOrdre(ordreActual);
			enumeracioValorsDao.merge(valor);
			enumeracioValorsDao.merge(anterior);
			enumeracioValorsDao.flush();
			valor.setOrdre(ordreActual - 1);
		}
	}
	public void goDownEnumeracioValor(Long id) {
		EnumeracioValors valor = enumeracioValorsDao.getById(id, false);
		int ordreActual = valor.getOrdre();
		EnumeracioValors seguent = enumeracioValorsDao.getAmbOrdre(
				valor.getEnumeracio().getId(),
				ordreActual + 1);
		if (seguent != null) {
			valor.setOrdre(-1);
			seguent.setOrdre(ordreActual);
			enumeracioValorsDao.merge(valor);
			enumeracioValorsDao.merge(seguent);
			enumeracioValorsDao.flush();
			valor.setOrdre(ordreActual + 1);
		}
	}

	public Termini getTerminiById(Long id) {
		return terminiDao.getById(id, false);
	}
	public Termini createTermini(Termini entity) {
		Termini saved = terminiDao.saveOrUpdate(entity);
		return saved;
	}
	public Termini updateTermini(Termini entity) {
		return terminiDao.merge(entity);
	}
	public void deleteTermini(Long id) {
		Termini vell = getTerminiById(id);
		if (vell != null) {
			for (TerminiIniciat iniciat: vell.getIniciats()) {
				vell.removeIniciat(iniciat);
				iniciat.setTermini(null);
			}
			terminiDao.delete(id);
		}
	}
	public List<Termini> findTerminisAmbDefinicioProces(Long definicioProcesId) {
		return terminiDao.findAmbDefinicioProces(definicioProcesId);
	}
	public Termini findTerminiAmbDefinicioProcesICodi(Long definicioProcesId, String codi) {
		return terminiDao.findAmbDefinicioProcesICodi(definicioProcesId, codi);
	}

	public Estat getEstatById(Long id) {
		return estatDao.getById(id, false);
	}
	public Estat createEstat(Estat entity) {
		entity.setOrdre(estatDao.getSeguentOrdre(entity.getExpedientTipus().getId()));
		return estatDao.saveOrUpdate(entity);
	}

	public Estat createOrUpdateEstat(Estat entity) {
		Estat estat = estatDao.findAmbExpedientTipusICodi(
				entity.getExpedientTipus().getId(),
				entity.getCodi());
		if (estat == null) {
			return createEstat(entity);
		} else {			
			estat.setNom(entity.getNom());
			return updateEstat(estat);
		}		
	}
	
	public Estat updateEstat(Estat entity) {
		return estatDao.merge(entity);
	}	
	
	public void deleteEstat(Long id, Long expedientTipusId) {
		int i = 0;
		for (Estat estat: findEstatAmbExpedientTipus(expedientTipusId)) {
			if (id.equals(estat.getId())) {
				estatDao.delete(estat);	
			} else {
				estat.setOrdre(i++);
			}		
		}
	}
	public List<Estat> findEstatAmbExpedientTipus(Long expedientTipusId) {
		return estatDao.findAmbExpedientTipusOrdenats(expedientTipusId);
	}
	public List<Estat> findEstatAmbEntorn(Long entornId) {
		return estatDao.findAmbEntornOrdenats(entornId);
	}
	public Estat findEstatAmbExpedientTipusICodi(Long expedientTipusId, String codi) {
		return estatDao.findAmbExpedientTipusICodi(expedientTipusId, codi);
	}
	public void goUpEstat(Long id) {
		Estat estat = getEstatById(id);
		int ordreActual = estat.getOrdre();
		Estat anterior = estatDao.getAmbOrdre(
				estat.getExpedientTipus().getId(),
				ordreActual - 1);
		if (anterior != null) {
			estat.setOrdre(-1);
			anterior.setOrdre(ordreActual);
			estatDao.merge(estat);
			estatDao.merge(anterior);
			estatDao.flush();
			estat.setOrdre(ordreActual - 1);
		}
	}
	public void goDownEstat(Long id) {
		Estat estat = getEstatById(id);
		int ordreActual = estat.getOrdre();
		Estat seguent = estatDao.getAmbOrdre(
				estat.getExpedientTipus().getId(),
				ordreActual + 1);
		if (seguent != null) {
			estat.setOrdre(-1);
			seguent.setOrdre(ordreActual);
			estatDao.merge(estat);
			estatDao.merge(seguent);
			estatDao.flush();
			estat.setOrdre(ordreActual + 1);
		}
	}

	public MapeigSistra getMapeigSistraById(Long id) {
		return mapeigSistraDao.getById(id, false);
	}
	public MapeigSistra createMapeigSistra(String codiHelium, String codiSistra, TipusMapeig tipus, ExpedientTipus expedientTipus) {
		MapeigSistra mapeig = new MapeigSistra(expedientTipus, codiHelium, codiSistra, tipus);
		/*mapeig.setCodiHelium(codiHelium);
		mapeig.setCodiSistra(codiSistra);
		mapeig.setTipus(tipus);
		mapeig.setExpedientTipus(expedientTipus);*/
		return mapeigSistraDao.saveOrUpdate(mapeig);
	}
	public List<MapeigSistra> findMapeigSistraVariablesAmbExpedientTipus(Long expedientTipusId) {
		return mapeigSistraDao.findVariablesAmbExpedientTipusOrdenats(expedientTipusId);
	}
	public List<MapeigSistra> findMapeigSistraDocumentsAmbExpedientTipus(Long expedientTipusId) {
		return mapeigSistraDao.findDocumentsAmbExpedientTipusOrdenats(expedientTipusId);
	}
	public List<MapeigSistra> findMapeigSistraAdjuntsAmbExpedientTipus(Long expedientTipusId) {
		return mapeigSistraDao.findAdjuntsAmbExpedientTipusOrdenats(expedientTipusId);
	}
	public MapeigSistra findMapeigSistraAmbExpedientTipusICodi(Long expedientTipusId, String codiHelium) {
		return mapeigSistraDao.findAmbExpedientTipusICodi(expedientTipusId, codiHelium);
	}
	public List<MapeigSistra> findMapeigSistraTots() {
		return mapeigSistraDao.findAll();
	}
	public void deleteMapeigSistra(Long id) {
		MapeigSistra vell = getMapeigSistraById(id);
		if (vell != null) {
			mapeigSistraDao.delete(id);
		}
	}

	public DefinicioProcesExportacio exportar(Long definicioProcesId) {
		DefinicioProcesExportacio definicioProcesExportacio = new DefinicioProcesExportacio();
		// Afegeix els camps
		List<Camp> camps = campDao.findAmbDefinicioProces(definicioProcesId);
		List<CampExportacio> campsDto = new ArrayList<CampExportacio>();
		for (Camp camp: camps) {
			boolean necessitaDadesExternes = TipusCamp.SELECCIO.equals(camp.getTipus()) || TipusCamp.SUGGEST.equals(camp.getTipus());			
			CampExportacio dto = new CampExportacio(
                    camp.getCodi(),
                    camp.getTipus(),
                    camp.getEtiqueta(),
                    camp.getObservacions(),
                    (necessitaDadesExternes) ? camp.getDominiId() : null,
                    (necessitaDadesExternes) ? camp.getDominiParams() : null,
                    (necessitaDadesExternes) ? camp.getDominiCampText() : null,
                    (necessitaDadesExternes) ? camp.getDominiCampValor() : null,
                    (necessitaDadesExternes) ? camp.getConsultaParams() : null,
                    (necessitaDadesExternes) ? camp.getConsultaCampText() : null,
                    (necessitaDadesExternes) ? camp.getConsultaCampValor() : null,
                    camp.isMultiple(),
                    camp.isOcult(),
                    camp.isDominiIntern(),
                    camp.isDominiCacheText(),
                    (necessitaDadesExternes && camp.getEnumeracio() != null) ? camp.getEnumeracio().getCodi() : null,
                    (necessitaDadesExternes && camp.getDomini() != null) ? camp.getDomini().getCodi() : null,
                    (camp.getAgrupacio() != null) ? camp.getAgrupacio().getCodi() : null,
                    camp.getJbpmAction(),
                    camp.getOrdre(),
                    camp.isIgnored());
			
			// Afegeix les validacions del camp
			for (Validacio validacio: camp.getValidacions()) {
				dto.addValidacio(new ValidacioExportacio(
						validacio.getNom(),
						validacio.getExpressio(),
						validacio.getMissatge(),
						validacio.getOrdre()));
			}
			// Afegeix els membres dels camps de tipus registre
			for (CampRegistre membre: camp.getRegistreMembres()) {
				dto.addRegistreMembre(new RegistreMembreExportacio(
						membre.getMembre().getCodi(),
						membre.isObligatori(),
						membre.isLlistar(),
						membre.getOrdre()));
			}
			campsDto.add(dto);
		}
		definicioProcesExportacio.setCamps(campsDto);
		// Afegeix les tasques
		List<Tasca> tasques = tascaDao.findAmbDefinicioProces(definicioProcesId);
		List<TascaExportacio> tasquesDto = new ArrayList<TascaExportacio>();
		for (Tasca tasca: tasques) {
			TascaExportacio dto = new TascaExportacio(
					tasca.getNom(),
					tasca.getTipus(),
					tasca.getJbpmName());
			dto.setMissatgeInfo(tasca.getMissatgeInfo());
			dto.setMissatgeWarn(tasca.getMissatgeWarn());
			dto.setNomScript(tasca.getNomScript());
			dto.setExpressioDelegacio(tasca.getExpressioDelegacio());
			dto.setRecursForm(tasca.getRecursForm());
			dto.setFormExtern(tasca.getFormExtern());
			dto.setTramitacioMassiva(tasca.isTramitacioMassiva());
			// Afegeix els camps de la tasca
			for (CampTasca camp: tasca.getCamps()) {
				CampTascaExportacio ctdto = new CampTascaExportacio(
						camp.getCamp().getCodi(),
						camp.isReadFrom(),
						camp.isWriteTo(),
						camp.isRequired(),
						camp.isReadOnly(),
						camp.getOrder());
				dto.addCamp(ctdto);
			}
			// Afegeix els documents de la tasca
			for (DocumentTasca document: tasca.getDocuments()) {
				DocumentTascaExportacio dtdto = new DocumentTascaExportacio(
						document.getDocument().getCodi(),
						document.isRequired(),
						document.isReadOnly(),
						document.getOrder());
				dto.addDocument(dtdto);
			}
			// Afegeix les signatures de la tasca
			for (FirmaTasca firma: tasca.getFirmes()) {
				FirmaTascaExportacio ftdto = new FirmaTascaExportacio(
						firma.getDocument().getCodi(),
						firma.isRequired(),
						firma.getOrder());
				dto.addFirma(ftdto);
			}
			// Afegeix les validacions de la tasca
			for (Validacio validacio: tasca.getValidacions()) {
				ValidacioExportacio vtdto = new ValidacioExportacio(
						validacio.getNom(),
						validacio.getExpressio(),
						validacio.getMissatge(),
						validacio.getOrdre());
				dto.addValidacio(vtdto);
			}
			tasquesDto.add(dto);
		}
		definicioProcesExportacio.setTasques(tasquesDto);
		// Afegeix els documents
		List<Document> documents = documentDao.findAmbDefinicioProces(definicioProcesId);
		List<DocumentExportacio> documentsDto = new ArrayList<DocumentExportacio>();
		for (Document document: documents) {
			DocumentExportacio dto = new DocumentExportacio(
					document.getCodi(),
					document.getNom(),
					document.getDescripcio(),
					document.getArxiuContingut(),
					document.getArxiuNom(),
					document.isPlantilla());
					document.getConvertirExtensio();
			dto.setCustodiaCodi(document.getCustodiaCodi());
			dto.setContentType(document.getContentType());
			dto.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
			dto.setAdjuntarAuto(document.isAdjuntarAuto());
			if (document.getCampData() != null)
				dto.setCodiCampData(document.getCampData().getCodi());
			dto.setConvertirExtensio(document.getConvertirExtensio());
			dto.setExtensionsPermeses(document.getExtensionsPermeses());
			documentsDto.add(dto);
		}
		definicioProcesExportacio.setDocuments(documentsDto);
		// Afegeix els terminis
		List<Termini> terminis = terminiDao.findAmbDefinicioProces(definicioProcesId);
		List<TerminiExportacio> terminisDto = new ArrayList<TerminiExportacio>();
		for (Termini termini: terminis) {
			TerminiExportacio dto = new TerminiExportacio(
					termini.getCodi(),
					termini.getNom(),
					termini.getDescripcio(),
					termini.getAnys(),
					termini.getMesos(),
					termini.getDies(),
					termini.isLaborable(),
					termini.getDiesPrevisAvis(),
					termini.isAlertaPrevia(),
					termini.isAlertaFinal(),
					termini.isManual());
			terminisDto.add(dto);
		}
		definicioProcesExportacio.setTerminis(terminisDto);
		// Afegeix les agrupacions
		List<CampAgrupacio> agrupacions = campAgrupacioDao.findAmbDefinicioProcesOrdenats(definicioProcesId);
		List<AgrupacioExportacio> agrupacionsDto = new ArrayList<AgrupacioExportacio>();
		for (CampAgrupacio agrupacio: agrupacions) {
			AgrupacioExportacio dto = new AgrupacioExportacio(
					agrupacio.getCodi(),
					agrupacio.getNom(),
					agrupacio.getDescripcio(),
					agrupacio.getOrdre());
			agrupacionsDto.add(dto);
		}
		
		definicioProcesExportacio.setAgrupacions(agrupacionsDto);
		// Afegeix les accions
		List<Accio> accions = accioDao.findAmbDefinicioProces(definicioProcesId);
		List<AccioExportacio> accionsDto = new ArrayList<AccioExportacio>();
		for (Accio accio: accions) {
			AccioExportacio dto = new AccioExportacio(
					accio.getCodi(),
					accio.getNom(),
					accio.getDescripcio(),
					accio.getJbpmAction(),
					accio.isPublica(),
					accio.isOculta(),
					accio.getRols());
			accionsDto.add(dto);
		}
		definicioProcesExportacio.setAccions(accionsDto);
		// Afegeix el deploy pel jBPM
		DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
		definicioProcesExportacio.setNomDeploy("export.par");
		Set<String> resourceNames = jbpmDao.getResourceNames(definicioProces.getJbpmId());
		if (resourceNames != null && resourceNames.size() > 0) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ZipOutputStream zos = new ZipOutputStream(baos);
				byte[] data = new byte[1024];
				for (String resource: resourceNames) {
					byte[] bytes = jbpmDao.getResourceBytes(
							definicioProces.getJbpmId(),
							resource);
					if (bytes != null) {
						InputStream is = new ByteArrayInputStream(jbpmDao.getResourceBytes(
								definicioProces.getJbpmId(),
								resource));
						zos.putNextEntry(new ZipEntry(resource));
						int count;
						while ((count = is.read(data, 0, 1024)) != -1)
							zos.write(data, 0, count);
				        zos.closeEntry();
					}
				}
				zos.close();
				definicioProcesExportacio.setContingutDeploy(baos.toByteArray());
			} catch (Exception ex) {
				throw new ExportException(
						getServiceUtils().getMessage("error.dissenyService.generantContingut"), ex);
			}
		}
		
        return definicioProcesExportacio;
	}
	public void importar(
			Long entornId,
			Long expedientTipusId,
			DefinicioProcesExportacio exportacio,
			String etiqueta) {
		DefinicioProces definicioProces = deploy(
			entornId,
			expedientTipusId,
			exportacio.getNomDeploy(),
			exportacio.getContingutDeploy(),
			etiqueta,
			false);
		importacio(
				entornId,
				expedientTipusId,
				exportacio,
				definicioProces);
	}

	public ExpedientTipusExportacio exportarExpedientTipus(Long expedientTipusId) {
		ExpedientTipus expedientTipus = expedientTipusDao.getById(expedientTipusId, false);
		ExpedientTipusExportacio dto = new ExpedientTipusExportacio(expedientTipus.getCodi(), expedientTipus.getNom());
		dto.setTeNumero(expedientTipus.getTeNumero());
		dto.setTeTitol(expedientTipus.getTeTitol());
		dto.setDemanaNumero(expedientTipus.getDemanaNumero());
		dto.setDemanaTitol(expedientTipus.getDemanaTitol());
		dto.setExpressioNumero(expedientTipus.getExpressioNumero());
		dto.setReiniciarCadaAny(expedientTipus.isReiniciarCadaAny());
		dto.setSistraTramitCodi(expedientTipus.getSistraTramitCodi());
		dto.setFormextUrl(expedientTipus.getFormextUrl());
		dto.setFormextUsuari(expedientTipus.getFormextUsuari());
		dto.setFormextContrasenya(expedientTipus.getFormextContrasenya());
		dto.setJbpmProcessDefinitionKey(expedientTipus.getJbpmProcessDefinitionKey());
		List<EstatExportacio> estats = new ArrayList<EstatExportacio>();
		for (Estat estat: expedientTipus.getEstats()) {
			estats.add(new EstatExportacio(estat.getCodi(), estat.getNom(), estat.getOrdre()));
		}
		dto.setEstats(estats);
		List<MapeigSistraExportacio> mapeigs = new ArrayList<MapeigSistraExportacio>();
		for (MapeigSistra mapeig : expedientTipus.getMapeigSistras()){
			mapeigs.add(new MapeigSistraExportacio(mapeig.getCodiHelium(), mapeig.getCodiSistra(), mapeig.getTipus()));
		}
		dto.setMapeigSistras(mapeigs);
		List<DominiExportacio> dominisExp = new ArrayList<DominiExportacio>();
		for (Domini domini : expedientTipus.getDominis()){
			DominiExportacio dtoExp = new DominiExportacio(
					domini.getCodi(),
					domini.getNom(),
					domini.getTipus().toString());
			dtoExp.setDescripcio(domini.getDescripcio());
			dtoExp.setUrl(domini.getUrl());
			dtoExp.setJndiDatasource(domini.getJndiDatasource());
			dtoExp.setSql(domini.getSql());
			dtoExp.setCacheSegons(domini.getCacheSegons());
			dominisExp.add(dtoExp);
		}
		dto.setDominis(dominisExp); 
		List<EnumeracioExportacio> enumeracionsExp = new ArrayList<EnumeracioExportacio>();
		for (Enumeracio enumeracio: expedientTipus.getEnumeracions()) {
			EnumeracioExportacio dtoExp = new EnumeracioExportacio(
					enumeracio.getCodi(),
					enumeracio.getNom(),
					enumeracioValorsDao.findAmbEnumeracioOrdenat(enumeracio.getId()));
			enumeracionsExp.add(dtoExp);
		}
		dto.setEnumeracions(enumeracionsExp);
		List<ConsultaExportacio> consultes = new ArrayList<ConsultaExportacio>();
		for (Consulta consulta: expedientTipus.getConsultes()) {
			ConsultaExportacio consultaExp = new ConsultaExportacio(
					consulta.getCodi(),
					consulta.getNom());
			consultaExp.setDescripcio(consulta.getDescripcio());
			consultaExp.setValorsPredefinits(consulta.getValorsPredefinits());
			consultaExp.setExportarActiu(consulta.isExportarActiu());
			consultaExp.setOcultarActiu(consulta.isOcultarActiu());
			consultaExp.setOrdre(consulta.getOrdre());
			if (consulta.getInformeContingut() != null && consulta.getInformeContingut().length > 0) {
				consultaExp.setInformeNom(consulta.getInformeNom());
				consultaExp.setInformeContingut(consulta.getInformeContingut());
			}
			List<Camp> campsConsulta = getServiceUtils().findCampsPerCampsConsulta(consulta, null);
			List<ConsultaCampExportacio> consultaCampsExp = new ArrayList<ConsultaCampExportacio>();
			for (ConsultaCamp consultaCamp: consulta.getCamps()) {
				for (Camp camp: campsConsulta) {
					if (camp.getCodi().equals(consultaCamp.getCampCodi())) {
						ConsultaCampExportacio consultaCampExp = new ConsultaCampExportacio(
								consultaCamp.getCampCodi(),
								consultaCamp.getDefprocJbpmKey(),
								consultaCamp.getTipus(),
								consultaCamp.getParamTipus(),
								camp.getTipus(),
								consultaCamp.getCampDescripcio(),
								consultaCamp.getOrdre());
						consultaCampsExp.add(consultaCampExp);
						break;
					}
				}
			}
			consultaExp.setCamps(consultaCampsExp);
			consultes.add(consultaExp);
		}
		dto.setConsultes(consultes);
		List<DefinicioProcesExportacio> definicionsProces = new ArrayList<DefinicioProcesExportacio>();
		List<String> jbpmKeyOrdenats = new ArrayList<String>();
		for (DefinicioProces definicioProces : definicioProcesDao.findDarreresVersionsAmbEntorn(expedientTipus.getEntorn().getId())) {
			if (	definicioProces.getExpedientTipus() != null &&
					definicioProces.getExpedientTipus().getId().equals(expedientTipus.getId())) {
				afegirJbpmKeyProcesAmbSubprocessos(
						jbpmDao.getProcessDefinition(definicioProces.getJbpmId()),
						jbpmKeyOrdenats);
			}
		}
		for (String jbpmKey: jbpmKeyOrdenats) {
			for (DefinicioProces definicioProces : definicioProcesDao.findDarreresVersionsAmbEntorn(expedientTipus.getEntorn().getId())) {
				if (	definicioProces.getExpedientTipus() != null &&
						definicioProces.getExpedientTipus().getId().equals(expedientTipus.getId())) {
					if (definicioProces.getJbpmKey().equals(jbpmKey)) {
						definicionsProces.add(exportar(definicioProces.getId()));
						break;
					}
				}
			}
		}
		dto.setDefinicionsProces(definicionsProces);
		return dto;
	}
	public void importarExpedientTipus(
			Long entornId,
			Long expedientTipusId,
			ExpedientTipusExportacio exportacio) {
		Entorn entorn = entornDao.getById(entornId, false);
		if (entorn == null)
			throw new DeploymentException("No s'ha trobat l'entorn amb id: " + entornId);
		ExpedientTipus expedientTipus;
		// Si no se pasa un Id de expediente (se le llama desde desplegar arxiu), se creará el nuevo tipo de expediente.
		if (expedientTipusId == null){
			// Comprobamos que no exista ya un tipo de expediente con el mismo código.
			expedientTipus = expedientTipusDao.findAmbEntornICodi(entornId, exportacio.getCodi());
			//if (expedientTipusDao.findAmbEntornICodi(entornId, exportacio.getCodi()) == null) {
			if (expedientTipus == null) {
				expedientTipus = new ExpedientTipus(exportacio.getCodi(), exportacio.getNom(), entorn);
			} else {
				throw new DeploymentException("Tipus d'expedient ja existent: " + exportacio.getCodi());
			}  
		// Si se pasa un Id, lo buscamos en BBDD.
		} else {
			expedientTipus = expedientTipusDao.getById(expedientTipusId, false);
			if (expedientTipus == null)
				throw new DeploymentException("No s'ha trobat l'expedient amb id: " + expedientTipusId);
		}
		// Comprobamos que el codi del fichero a importar y del expediente a modificar son el mismo.
		if (!(expedientTipus.getCodi().equals(exportacio.getCodi()))){
			throw new DeploymentException("El codi del tipus d'expedient no correspon amb el del tipus a importar: "  + exportacio.getCodi());
		}
		expedientTipus.setNom(exportacio.getNom());
		expedientTipus.setTeNumero(exportacio.getTeNumero());
		expedientTipus.setTeTitol(exportacio.getTeTitol());
		expedientTipus.setDemanaNumero(exportacio.getDemanaNumero());
		expedientTipus.setDemanaTitol(exportacio.getDemanaTitol());
		expedientTipus.setExpressioNumero(exportacio.getExpressioNumero());
		expedientTipus.setReiniciarCadaAny(exportacio.isReiniciarCadaAny());
		expedientTipus.setSistraTramitCodi(exportacio.getSistraTramitCodi());
		expedientTipus.setJbpmProcessDefinitionKey(exportacio.getJbpmProcessDefinitionKey());
		/*expedientTipus.setSistraTramitMapeigCamps(exportacio.getSistraTramitMapeigCamps());
		expedientTipus.setSistraTramitMapeigDocuments(exportacio.getSistraTramitMapeigDocuments());
		expedientTipus.setSistraTramitMapeigAdjunts(exportacio.getSistraTramitMapeigAdjunts());*/
		expedientTipus.setFormextUrl(exportacio.getFormextUrl());
		expedientTipus.setFormextUsuari(exportacio.getFormextUsuari());
		expedientTipus.setFormextContrasenya(exportacio.getFormextContrasenya());
		expedientTipusDao.saveOrUpdate(expedientTipus);
		// Crea els estats del tipus d'expedient
		if (exportacio.getEstats() != null) {
			for (EstatExportacio estat: exportacio.getEstats()) {
				Estat enou = null;
				if (expedientTipus.getId() != null) {
					enou = estatDao.findAmbExpedientTipusICodi(
						expedientTipus.getId(),
						estat.getCodi());
				}
				if (enou == null) {
					enou = new Estat(
							expedientTipus,
							estat.getCodi(),
							estat.getNom());
				} else {
					enou.setNom(estat.getNom());
				}
				enou.setOrdre(estat.getOrdre());
				estatDao.saveOrUpdate(enou);
			}
		}
		// Crea els mapejos del tipus d'expedient
		if (exportacio.getMapeigSistras() != null) {
			for (MapeigSistraExportacio mapeig: exportacio.getMapeigSistras()) {
				MapeigSistra mnou = null;
				if (expedientTipus.getId() != null) {
					mnou = mapeigSistraDao.findAmbExpedientTipusICodi(
							expedientTipus.getId(),
						mapeig.getCodiHelium());
				}
				if (mnou == null) {
					mnou = new MapeigSistra(
							expedientTipus,
							mapeig.getCodiHelium(),
							mapeig.getCodiSistra(),
							mapeig.getTipus());
				} else {
					mnou.setCodiSistra(mapeig.getCodiSistra());
					mnou.setTipus(mapeig.getTipus());
				}
				mapeigSistraDao.saveOrUpdate(mnou);
			}
		}
		// Crea els dominis del tipus d'expedient
		if (exportacio.getDominis() != null) {
			for (DominiExportacio domini: exportacio.getDominis()) {
				Domini dnou = dominiDao.findAmbEntornICodi(
						entornId,
						domini.getCodi());
				if (dnou == null) {
					dnou = new Domini(
							domini.getCodi(),
							domini.getNom(),
							entorn);
					dnou.setExpedientTipus(expedientTipus);
				} else {
					dnou.setNom(domini.getNom());
				}
				dnou.setDescripcio(domini.getDescripcio());
				dnou.setTipus(TipusDomini.valueOf(domini.getTipus()));
				dnou.setCacheSegons(domini.getCacheSegons());
				dnou.setSql(domini.getSql());
				dnou.setJndiDatasource(domini.getJndiDatasource());
				dnou.setUrl(domini.getUrl());
				dominiDao.saveOrUpdate(dnou);
			}
		}
		// Crea les enumeracions del tipus d'expedient
		if (exportacio.getEnumeracions() != null) {
			for (EnumeracioExportacio enumeracio: exportacio.getEnumeracions()) {
				Enumeracio nova = enumeracioDao.findAmbEntornAmbTipusExpICodi(
						entornId,
						expedientTipusId,
						enumeracio.getCodi());
				if (nova == null) {
					nova = new Enumeracio(
							entorn,
							enumeracio.getCodi(),
							enumeracio.getNom());
					nova.setExpedientTipus(expedientTipus);
				} else {
					nova.setNom(enumeracio.getNom());
					for (EnumeracioValors enumValor: nova.getEnumeracioValors()) {
						enumValor.setEnumeracio(null);
						enumeracioValorsDao.delete(enumValor);
					}
					nova.getEnumeracioValors().clear();
				}
				for (EnumeracioValors enumValor: enumeracio.getValors()) {
					EnumeracioValors novaEnumValors = null;
					for (EnumeracioValors novaValor: nova.getEnumeracioValors()) {
						if (novaValor.getCodi().equals(enumValor.getCodi())) {
							novaEnumValors = novaValor;
							break;
						}
					}
					if (novaEnumValors == null) {
						novaEnumValors = new EnumeracioValors();
						novaEnumValors.setCodi(enumValor.getCodi());
						novaEnumValors.setEnumeracio(nova);
					}
					novaEnumValors.setNom(enumValor.getNom());
					nova.addEnumeracioValors(novaEnumValors);
				}
				enumeracioDao.saveOrUpdate(nova);
			}
		}
		// Importa les definicions de procés
		if (exportacio.getDefinicionsProces() != null) {
			for (DefinicioProcesExportacio definicio : exportacio.getDefinicionsProces()) {
				//System.out.println(">>> [ET_IMP] Important definició de procés (nom=" + definicio.getNomDeploy() + ")");
				importar(
					entornId,
					expedientTipus.getId(),
					definicio,
					null);
			}
		}
		// Crea les consultes del tipus d'expedient
		if (exportacio.getConsultes() != null) {
			List<Consulta> llista = consultaDao.findAmbEntornIExpedientTipusOrdenat(entornId, expedientTipusId);
			for (ConsultaExportacio consulta: exportacio.getConsultes()) {
				Consulta nova = consultaDao.findAmbEntornICodi(
						entornId,
						consulta.getCodi());
				if (nova == null) {
					nova = new Consulta(
							consulta.getCodi(),
							consulta.getNom());
				} else {
					nova.setNom(consulta.getNom());
					llista.remove(nova);
				}
				nova.setDescripcio(consulta.getDescripcio());
				nova.setValorsPredefinits(consulta.getValorsPredefinits());
				if (consulta.getInformeContingut() != null) {
					nova.setInformeNom(consulta.getInformeNom());
					nova.setInformeContingut(consulta.getInformeContingut());
				}
				nova.setExportarActiu(consulta.isExportarActiu());
				nova.setOcultarActiu(consulta.isOcultarActiu());
				nova.setOrdre(consulta.getOrdre());
				nova.setEntorn(entorn);
				nova.setExpedientTipus(expedientTipus);
				for (ConsultaCamp consultaCamp: nova.getCamps()) {
					consultaCamp.setConsulta(null);
					consultaCampDao.delete(consultaCamp);
				}
				nova.getCamps().clear();
				consultaCampDao.flush();
				for (ConsultaCampExportacio consultaCamp: consulta.getCamps()) {
					ConsultaCamp campNou = new ConsultaCamp(
							consultaCamp.getCodi(),
							consultaCamp.getTipusConsultaCamp());
					campNou.setConsulta(nova);
					campNou.setDefprocJbpmKey(consultaCamp.getJbpmKey());
					for (Camp camp: consultaCampDao.findCampsDefinicioProcesAmbJbpmKey(
							entornId,
							consultaCamp.getJbpmKey())) {
						if (camp.getCodi().equals(consultaCamp.getCodi())) {
							if (camp.getTipus().equals(consultaCamp.getTipusCamp())) {
								campNou.setDefprocVersio(camp.getDefinicioProces().getVersio());
								break;
							}
						}
					}
					campNou.setCampDescripcio(consultaCamp.getCampDescripcio());
					campNou.setParamTipus(consultaCamp.getTipusParamConsultaCamp());
					campNou.setOrdre(consultaCamp.getOrdre());
					nova.addCamp(campNou);
				}
				consultaDao.saveOrUpdate(nova);
			}
			int nombreConsultes = exportacio.getConsultes().size();
			for (int i = 0; i < llista.size(); i++){
				Consulta c = llista.get(i);
				c.setOrdre(nombreConsultes + i);
				consultaDao.saveOrUpdate(c);
			}
		}
	}

	public void configurarAmbExportacio(
			Long entornId,
			Long definicioProcesId,
			DefinicioProcesExportacio exportacio) {
		DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
		importacio(
				entornId,
				(definicioProces.getExpedientTipus() != null) ? definicioProces.getExpedientTipus().getId() : null,
				exportacio,
				definicioProces);
	}
	public void goUpConsulta(Long expedientTipusId, Long consultaId) {
		
		Consulta valor = consultaDao.getById(consultaId, false);
		int ordreActual = valor.getOrdre();
		Consulta anterior = consultaDao.getAmbOrdre(
				expedientTipusId,
				ordreActual - 1);
		
		if (anterior != null) {
			valor.setOrdre(-1);
			anterior.setOrdre(ordreActual);
			consultaDao.merge(valor);
			consultaDao.merge(anterior);
			consultaDao.flush();
			valor.setOrdre(ordreActual - 1);
		}
	}
	public void goDownConsulta(Long expedientTipusId, Long consultaId) {
		
		Consulta valor = consultaDao.getById(consultaId, false);
		int ordreActual = valor.getOrdre();
		Consulta seguent = consultaDao.getAmbOrdre(
				expedientTipusId,
				ordreActual + 1);
		
		if (seguent != null) {
			valor.setOrdre(+1);
			seguent.setOrdre(ordreActual);
			consultaDao.merge(valor);
			consultaDao.merge(seguent);
			consultaDao.flush();
			valor.setOrdre(ordreActual + 1);
		}
	}

	public Domini getDominiById(Long id) {
		return dominiDao.getById(id, false);
	}
	public Domini createDomini(Domini entity) {
		return dominiDao.saveOrUpdate(entity);
	}
	public Domini updateDomini(Domini entity) {
		dominiDao.makeDirty(entity.getId());
		return dominiDao.merge(entity);
	}
	public void deleteDomini(Long id) {
		Domini vell = getDominiById(id);
		if (vell != null) {
			dominiDao.makeDirty(id);
			dominiDao.delete(id);
		}
	}
	public List<Domini> findDominiAmbEntorn(Long entornId) {
		return dominiDao.findAmbEntorn(entornId);
	}
	public List<Domini> findDominiAmbEntornITipusExp(Long entornId, Long tipusExpId) {
		return dominiDao.findAmbEntornITipusExp(entornId, tipusExpId);
	}
	public List<Domini> findDominiAmbEntornITipusExpONull(Long entornId, Long tipusExpId) {
		return dominiDao.findAmbEntornITipusExpONull(entornId, tipusExpId);
	}

	public Domini findDominiAmbEntornICodi(Long entornId, String codi) {
		return dominiDao.findAmbEntornICodi(entornId, codi);
	}

	public List<FilaResultat> consultaDomini(Long entornId, Long dominiId) {
		return consultaDomini(entornId, dominiId, null, null);
	}
	public List<FilaResultat> consultaDomini(Long entornId, Long dominiId, Map<String, Object> params) {
		try {
			return dominiDao.consultar(entornId, dominiId, null, params);
		} catch (Exception ex) {
			throw new DominiException(
					getServiceUtils().getMessage("error.dissenyService.consultantDomini"), ex);
		}
	}
	
	public List<FilaResultat> consultaDomini(Long entornId, Long dominiId, String dominiWsId) {
		return consultaDomini(entornId, dominiId, dominiWsId, null);
	}
	
	public List<FilaResultat> consultaDomini(Long entornId, Long dominiId, String dominiWsId, Map<String, Object> params) {
		try {
			return dominiDao.consultar(entornId, dominiId, dominiWsId, params);
		} catch (Exception ex) {
			throw new DominiException(
					getServiceUtils().getMessage("error.dissenyService.consultantDomini") + " : " + dominiWsId + " << parametros >> " + params, ex);
		}
	}
	
	

	public DefinicioProcesDto findDefinicioProcesAmbProcessInstanceId(String processInstanceId) {
		String processDefinitionId = jbpmDao.getProcessInstance(processInstanceId).getProcessDefinitionId();
		return toDto(definicioProcesDao.findAmbJbpmId(processDefinitionId), false);
	}

	public List<FilaResultat> getResultatConsultaDomini(
			Long definicioProcesId,
			String campCodi,
			String textInicial) throws DominiException {
		Camp camp = campDao.findAmbDefinicioProcesICodi(definicioProcesId, campCodi);
		return dtoConverter.getResultatConsultaDominiPerCamp(camp, null, textInicial);
	}

	public List<FilaResultat> getResultatConsultaCamp(
			String taskId,
			String processInstanceId,
			Long definicioProcesId,
			String campCodi,
			String textInicial,
			Map<String, Object> valorsAddicionals) {
		if (definicioProcesId != null) {
			DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
			Camp camp = null;
			for (Camp c: definicioProces.getCamps()) {
				if (c.getCodi().equals(campCodi)) {
					camp = c;
					break;
				}
			}
			if (camp != null && camp.getEnumeracio() != null) {
				return dtoConverter.getResultatConsultaEnumeracio(definicioProces, campCodi, textInicial);
			} else if (camp != null && (camp.getDomini() != null || camp.isDominiIntern())) {
				return dtoConverter.getResultatConsultaDomini(
						definicioProces,
						taskId,
						processInstanceId,
						campCodi,
						textInicial,
						valorsAddicionals);
			} else {
				return dtoConverter.getResultatConsultaConsulta(
						definicioProces,
						taskId,
						processInstanceId,
						campCodi,
						textInicial,
						valorsAddicionals);
			}
		} else {
			DefinicioProces dp = null;
			if (taskId != null) {
				JbpmTask task = jbpmDao.getTaskById(taskId);
				dp = definicioProcesDao.findAmbJbpmId(task.getProcessDefinitionId());
			} else {
				JbpmProcessDefinition jpd = jbpmDao.findProcessDefinitionWithProcessInstanceId(processInstanceId);
				dp = definicioProcesDao.findAmbJbpmId(jpd.getId());
			}
			return dtoConverter.getResultatConsultaDomini(
					dp,
					taskId,
					processInstanceId,
					campCodi,
					textInicial,
					valorsAddicionals);
		}
	}

	public CampAgrupacio getCampAgrupacioById(Long id) {
		return campAgrupacioDao.getById(id, false);
	}
	public CampAgrupacio createCampAgrupacio(CampAgrupacio entity) {
		entity.setOrdre(campAgrupacioDao.getNextOrder(entity.getDefinicioProces().getId()));
		return campAgrupacioDao.saveOrUpdate(entity);
	}
	public CampAgrupacio updateCampAgrupacio(CampAgrupacio entity) {
		return campAgrupacioDao.merge(entity);
	}
	public void deleteCampAgrupacio(Long id) {
		CampAgrupacio vell = getCampAgrupacioById(id);
		if (vell != null) {
			campAgrupacioDao.delete(id);
			
			for (Camp c : vell.getCamps()) {
				c.setAgrupacio(null);
				c.setOrdre(null);
				campDao.saveOrUpdate(c);
			}
			
			reordenarAgrupacions(vell.getDefinicioProces().getId());
		}
	}
	public List<CampAgrupacio> findCampAgrupacioAmbDefinicioProces(Long definicioProcesId) {
		return campAgrupacioDao.findAmbDefinicioProcesOrdenats(definicioProcesId);
	}
	public CampAgrupacio findCampAgrupacioPerId(Long definicioProcesId, String agrupacioCodi) {
		return campAgrupacioDao.findAmbDefinicioProcesICodi(definicioProcesId, agrupacioCodi);
	}
	public void goUpCampAgrupacio(Long id) {
		CampAgrupacio campAgrupacio = getCampAgrupacioById(id);
		List<CampAgrupacio> campsOrdenats = campAgrupacioDao.findAmbDefinicioProcesOrdenats(
				campAgrupacio.getDefinicioProces().getId());
		int index = 0;
		CampAgrupacio anterior = null;
		for (CampAgrupacio ca: campsOrdenats) {
			if (anterior != null && ca.getId().equals(id)) {
				ca.setOrdre(index - 1);
				anterior.setOrdre(index++);
			} else {
				ca.setOrdre(index++);
			}
			anterior = ca;
		}
	}
	public void goDownCampAgrupacio(Long id) {
		CampAgrupacio campAgrupacio = getCampAgrupacioById(id);
		List<CampAgrupacio> campsOrdenats = campAgrupacioDao.findAmbDefinicioProcesOrdenats(
				campAgrupacio.getDefinicioProces().getId());
		int index = 0;
		CampAgrupacio anteriorPerModificar = null;
		for (CampAgrupacio ca: campsOrdenats) {
			if (anteriorPerModificar != null) {
				ca.setOrdre(index - 1);
				anteriorPerModificar.setOrdre(index++);
			} else {
				ca.setOrdre(index++);
			}
			if (ca.getId().equals(id)) {
				anteriorPerModificar = ca;
			} else {
				anteriorPerModificar = null;
			}
		}
	}
	
	public void goUpCamp(Long id, String agrupacioCodi) {
		Camp camp = getCampById(id);
		List<Camp> campsOrdenats = campDao.findAmbDefinicioProcesIAgrupacioOrdenats(
				camp.getDefinicioProces().getId(),
				camp.getAgrupacio().getId());
		int index = 0;
		Camp anterior = null;
		for (Camp ca: campsOrdenats) {
			if (anterior != null && ca.getId().equals(id)) {
				ca.setOrdre(index - 1);
				anterior.setOrdre(index++);
			} else {
				ca.setOrdre(index++);
			}
			anterior = ca;
		}
	}
	
	public void goDownCamp(Long id, String agrupacioCodi) {
		Camp camp = getCampById(id);
		List<Camp> campsOrdenats = campDao.findAmbDefinicioProcesIAgrupacioOrdenats(
				camp.getDefinicioProces().getId(),
				camp.getAgrupacio().getId());
		int index = 0;
		Camp anteriorPerModificar = null;
		for (Camp ca: campsOrdenats) {
			if (anteriorPerModificar != null) {
				ca.setOrdre(index - 1);
				anteriorPerModificar.setOrdre(index++);
			} else {
				ca.setOrdre(index++);
			}
			if (ca.getId().equals(id)) {
				anteriorPerModificar = ca;
			} else {
				anteriorPerModificar = null;
			}
		}
	}

	public Consulta getConsultaById(Long id) {
		return consultaDao.getById(id, false);
	}
	public Consulta createConsulta(Consulta entity) {
		entity.setOrdre(consultaDao.getNextOrder(entity.getExpedientTipus().getId()));
		return consultaDao.saveOrUpdate(entity);
	}
	public Consulta updateConsulta(Consulta entity, boolean delete) {
//		Consulta vella = consultaDao.getById(entity.getId(), false);
//		if (vella != null && !delete) {
//			if (entity.getInformeContingut() == null || entity.getInformeContingut().length == 0) {
//				entity.setInformeNom(vella.getInformeNom());
//				entity.setInformeContingut(vella.getInformeContingut());
//			}
//		}
		return consultaDao.merge(entity);
	}
	public Consulta updateConsulta(Consulta entity) {
		return consultaDao.merge(entity);
	}
	public void deleteConsulta(Long id) {
		Consulta vell = getConsultaById(id);
		Long expedientTipusId = vell.getExpedientTipus().getId();
		Long entornId = vell.getEntorn().getId();
		if (vell != null){
			consultaDao.delete(id);
			reordenarConsultes(entornId, expedientTipusId); 
		}
	}
	public List<Consulta> findConsultesAmbEntorn(Long entornId) {
		return consultaDao.findAmbEntorn(entornId);
	}
	public List<Consulta> findConsultesAmbEntornAmbOSenseTipusExp(Long entornId, Long expedientTipusId) {
		return consultaDao.findAmbEntornAmbOSenseTipusExp(entornId, expedientTipusId);
	}
	public List<Consulta> findConsultesAmbEntornIExpedientTipusOrdenat(Long entornId, Long expedientTipusId) {
		return consultaDao.findAmbEntornIExpedientTipusOrdenat(entornId, expedientTipusId);
	}

	public ConsultaCamp getConsultaCampById(Long id) {
		return consultaCampDao.getById(id, false);
	}
	public ConsultaCamp createConsultaCamp(ConsultaCamp entity) {
		entity.setOrdre(consultaCampDao.getNextOrderPerTipus(entity.getConsulta().getId(), entity.getTipus()));
		return consultaCampDao.saveOrUpdate(entity);
	}
	public ConsultaCamp updateConsultaCamp(ConsultaCamp entity) {
		return consultaCampDao.merge(entity);
	}
	public void deleteConsultaCamp(Long id) {
		ConsultaCamp vell = getConsultaCampById(id);
		if (vell != null) {
			consultaCampDao.delete(id);
			reordenarConsultaCamp(vell.getConsulta().getId(), vell.getTipus());
		}
	}
	public List<ConsultaCamp> findConsultaCampAmbConsultaITipus(Long consultaId, TipusConsultaCamp tipus) {
		return consultaCampDao.findAmbConsultaITipusOrdenats(consultaId, tipus);
	}
	public List<Camp> findCampsProces(Long consultaId, String defprocJbpmKey) {
		List<Camp> list = new ArrayList<Camp>();
		Consulta consulta = consultaDao.getById(consultaId, false);
		if (consulta != null) {
			List<Camp> campsDefinicioProces = consultaCampDao.findCampsDefinicioProcesAmbJbpmKey(
					consulta.getEntorn().getId(),
					defprocJbpmKey);
			for(Camp camp:campsDefinicioProces){
				//if(NoExisteixAInforme(campsConsulta,camp)){
					if (!camp.getTipus().equals(TipusCamp.REGISTRE)) {
						Camp c = new Camp();
						c.setId(camp.getId());
						c.setCodi(camp.getCodi());
						c.setEtiqueta(camp.getEtiqueta());
						c.setTipus(camp.getTipus());
						DefinicioProces dp = new DefinicioProces();
						dp.setId(camp.getDefinicioProces().getId());
						dp.setJbpmId(camp.getDefinicioProces().getJbpmId());
						dp.setJbpmKey(camp.getDefinicioProces().getJbpmKey());
						dp.setVersio(camp.getDefinicioProces().getVersio());
						c.setDefinicioProces(dp);
						list.add(c);
					}
				//}
			}
		}
		return list;
	}
	/*private boolean NoExisteixAInforme(Set<ConsultaCamp> consultaCamps,Camp camp){
		for (ConsultaCamp consultaCamp:consultaCamps) {
			if (consultaCamp.getCampCodi().equals(camp.getCodi())) {
				if (consultaCamp.getTipus().name().equals("FILTRE") && !consultaCamp.getTipus().name().equals("INFORME"))
					return false;
				if (consultaCamp.getTipus().name().equals("INFORME") && !consultaCamp.getTipus().name().equals("FILTRE"))
					return true;
			} else {
				return true;
			}
		}
		return true;
	}*/

	public List<ConsultaCamp> findCampsConsulta(Long consultaId, TipusConsultaCamp tipus) {
		return consultaCampDao.findCampsConsulta(consultaId, tipus);
	}
	public List<Camp> findCampsPerCampsConsulta(
			Long consultaId,
			TipusConsultaCamp tipus,
			boolean filtrarValorsPredefinits) {
		List<Camp> camps = getServiceUtils().findCampsPerCampsConsulta(
				consultaDao.getById(consultaId, false),
				tipus);
		if (filtrarValorsPredefinits && tipus.equals(TipusConsultaCamp.FILTRE))
			filtrarCampsConsultaFiltre(consultaId, camps);
		return camps;
	}
	public void goUpConsultaCamp(Long id) {
		ConsultaCamp consultaCamp = getConsultaCampById(id);
		int ordreActual = consultaCamp.getOrdre();
		ConsultaCamp anterior = consultaCampDao.getAmbTipusIOrdre(
				consultaCamp.getConsulta().getId(),
				consultaCamp.getTipus(),
				ordreActual - 1);
		if (anterior != null) {
			consultaCamp.setOrdre(-1);
			anterior.setOrdre(ordreActual);
			consultaCampDao.merge(consultaCamp);
			consultaCampDao.merge(anterior);
			consultaCampDao.flush();
			consultaCamp.setOrdre(ordreActual - 1);
		}
	}
	public void goDownConsultaCamp(Long id) {
		ConsultaCamp consultaCamp = getConsultaCampById(id);
		int ordreActual = consultaCamp.getOrdre();
		ConsultaCamp seguent = consultaCampDao.getAmbTipusIOrdre(
				consultaCamp.getConsulta().getId(),
				consultaCamp.getTipus(),
				ordreActual + 1);
		if (seguent != null) {
			consultaCamp.setOrdre(-1);
			seguent.setOrdre(ordreActual);
			consultaCampDao.merge(consultaCamp);
			consultaCampDao.merge(seguent);
			consultaCampDao.flush();
			consultaCamp.setOrdre(ordreActual + 1);
		}
	}

	public void addConsultaParam(
			Long id,
			String paramCodi,
			String paramDescripcio,
			TipusParamConsultaCamp paramTipus) {
		ConsultaCamp consultaCamp = new ConsultaCamp();
		consultaCamp.setCampCodi(paramCodi);
		consultaCamp.setCampDescripcio(paramDescripcio);
		consultaCamp.setTipus(TipusConsultaCamp.PARAM);
		consultaCamp.setParamTipus(paramTipus);
		consultaCamp.setConsulta(
				consultaDao.getById(id, false));
		consultaCamp.setOrdre(
				consultaCampDao.getNextOrderPerTipus(
						id,
						TipusConsultaCamp.PARAM));
		consultaCampDao.saveOrUpdate(consultaCamp);
	}
	public void deleteConsultaParam(
			Long id,
			Long paramId) {
		ConsultaCamp consultaCamp = consultaCampDao.getAmbTipusIId(
				id,
				paramId,
				TipusConsultaCamp.PARAM);
		if (consultaCamp != null) {
			consultaCampDao.delete(consultaCamp);
		}
	}

	public void deleteCampFromAgrupacio(Long id) {
		Camp camp = getCampById(id);
		if (camp != null) {
			CampAgrupacio agrupacio = camp.getAgrupacio();
			camp.setOrdre(null);
			camp.setAgrupacio(null);
			campDao.merge(camp);
			if (agrupacio != null)
				reordenarCamps(camp.getDefinicioProces().getId(), agrupacio.getId());
		}
	}

	public Accio getAccioById(Long id) {
		Accio accio = accioDao.getById(id, false);
		return accio;
	}
	public Accio createAccio(Accio entity) {
		Accio saved = accioDao.saveOrUpdate(entity);
		return saved;
	}
	public Accio updateAccio(Accio entity) {
		return accioDao.merge(entity);
	}
	public void deleteAccio(Long id) {
		Accio vella = getAccioById(id);
		if (vella != null)
			accioDao.delete(id);
	}
	public List<Accio> findAccionsAmbDefinicioProces(Long definicioProcesId) {
		return accioDao.findAmbDefinicioProces(definicioProcesId);
	}
	public List<Accio> findAccionsVisiblesAmbDefinicioProces(Long definicioProcesId) {
		return accioDao.findVisiblesAmbDefinicioProces(definicioProcesId);
	}
	public Accio findAccioAmbDefinicioProcesICodi(Long definicioProcesId, String codi) {
		return accioDao.findAmbDefinicioProcesICodi(definicioProcesId, codi);
	}
	public List<String> findAccionsJbpmOrdenades(Long id) {
		DefinicioProces definicioProces = definicioProcesDao.getById(id, false);
		List<String> accions = jbpmDao.listActions(definicioProces.getJbpmId());
		Collections.sort(accions);
		return accions;
	}

	@Autowired
	public void setDefinicioProcesDao(DefinicioProcesDao definicioProcesDao) {
		this.definicioProcesDao = definicioProcesDao;
	}
	@Autowired
	public void setEntornDao(EntornDao entornDao) {
		this.entornDao = entornDao;
	}
	@Autowired
	public void setTascaDao(TascaDao tascaDao) {
		this.tascaDao = tascaDao;
	}
	@Autowired
	public void setCampDao(CampDao campDao) {
		this.campDao = campDao;
	}
	@Autowired
	public void setCampTascaDao(CampTascaDao campTascaDao) {
		this.campTascaDao = campTascaDao;
	}
	@Autowired
	public void setCampRegistreDao(CampRegistreDao campRegistreDao) {
		this.campRegistreDao = campRegistreDao;
	}
	@Autowired
	public void setDocumentDao(DocumentDao documentDao) {
		this.documentDao = documentDao;
	}
	@Autowired
	public void setDocumentTascaDao(DocumentTascaDao documentTascaDao) {
		this.documentTascaDao = documentTascaDao;
	}
	@Autowired
	public void setFirmaTascaDao(FirmaTascaDao firmaTascaDao) {
		this.firmaTascaDao = firmaTascaDao;
	}
	@Autowired
	public void setValidacioDao(ValidacioDao validacioDao) {
		this.validacioDao = validacioDao;
	}
	@Autowired
	public void setExpedientTipusDao(ExpedientTipusDao expedientTipusDao) {
		this.expedientTipusDao = expedientTipusDao;
	}
	@Autowired
	public void setExpedientDao(ExpedientDao expedientDao) {
		this.expedientDao = expedientDao;
	}
	@Autowired
	public void setEnumeracioDao(EnumeracioDao enumeracioDao) {
		this.enumeracioDao = enumeracioDao;
	}
	@Autowired
	public void setEnumeracioValorsDao(EnumeracioValorsDao enumeracioValorsDao) {
		this.enumeracioValorsDao = enumeracioValorsDao;
	}
	@Autowired
	public void setTerminiDao(TerminiDao terminiDao) {
		this.terminiDao = terminiDao;
	}
	@Autowired
	public void setEstatDao(EstatDao estatDao) {
		this.estatDao = estatDao;
	}
	@Autowired
	public void setMapeigSistraDao(MapeigSistraDao mapeigSistraDao) {
		this.mapeigSistraDao = mapeigSistraDao;
	}
	@Autowired
	public void setDominiDao(DominiDao dominiDao) {
		this.dominiDao = dominiDao;
	}
	@Autowired
	public void setCampAgrupacioDao(CampAgrupacioDao campAgrupacioDao) {
		this.campAgrupacioDao = campAgrupacioDao;
	}
	@Autowired
	public void setConsultaDao(ConsultaDao consultaDao) {
		this.consultaDao = consultaDao;
	}
	@Autowired
	public void setConsultaCampDao(ConsultaCampDao consultaCampDao) {
		this.consultaCampDao = consultaCampDao;
	}
	@Autowired
	public void setAccioDao(AccioDao accioDao) {
		this.accioDao = accioDao;
	}
	@Autowired
	public void setDtoConverter(DtoConverter dtoConverter) {
		this.dtoConverter = dtoConverter;
	}
	@Autowired
	public void setJbpmDao(JbpmHelper jbpmDao) {
		this.jbpmDao = jbpmDao;
	}
	@Autowired
	public void setLuceneDao(LuceneDao luceneDao) {
		this.luceneDao = luceneDao;
	}
	@Autowired
	public void setAclServiceDao(AclServiceDao aclServiceDao) {
		this.aclServiceDao = aclServiceDao;
	}
	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	@Autowired
	public void setSequenciaAnyDao(SequenciaAnyDao sequenciaAnyDao) {
		this.sequenciaAnyDao = sequenciaAnyDao;
	}
	@Autowired
	public void setDocumentStoreDao(DocumentStoreDao documentStoreDao) {
		this.documentStoreDao = documentStoreDao;
	}

	private DefinicioProcesDto toDto(
			DefinicioProces definicioProces,
			boolean ambTascaInicial) {
		DefinicioProcesDto dto = new DefinicioProcesDto();
		dto.setId(definicioProces.getId());
		dto.setJbpmId(definicioProces.getJbpmId());
		dto.setJbpmKey(definicioProces.getJbpmKey());
		dto.setVersio(definicioProces.getVersio());
		dto.setEtiqueta(definicioProces.getEtiqueta());
		dto.setDataCreacio(definicioProces.getDataCreacio());
		dto.setEntorn(definicioProces.getEntorn());
		dto.setExpedientTipus(definicioProces.getExpedientTipus());
		JbpmProcessDefinition jpd = jbpmDao.getProcessDefinition(definicioProces.getJbpmId());
		if (jpd != null)
			dto.setJbpmName(jpd.getName());
		else
			dto.setJbpmName("[" + definicioProces.getJbpmKey() + "]");

		List<DefinicioProces> mateixaKeyIEntorn = definicioProcesDao.findAmbEntornIJbpmKey(
				definicioProces.getEntorn().getId(),
				definicioProces.getJbpmKey());
		dto.setIdsWithSameKey(new Long[mateixaKeyIEntorn.size()]);
		dto.setIdsMostrarWithSameKey(new String[mateixaKeyIEntorn.size()]);
		dto.setJbpmIdsWithSameKey(new String[mateixaKeyIEntorn.size()]);
		for (int i = 0; i < mateixaKeyIEntorn.size(); i++) {
			dto.getIdsWithSameKey()[i] = mateixaKeyIEntorn.get(i).getId();
			dto.getIdsMostrarWithSameKey()[i] = mateixaKeyIEntorn.get(i).getIdPerMostrar();
			dto.getJbpmIdsWithSameKey()[i] = mateixaKeyIEntorn.get(i).getJbpmId();
		}
		if (ambTascaInicial) {
			dto.setHasStartTask(hasStartTask(definicioProces));
			dto.setStartTaskName(jbpmDao.getStartTaskName(definicioProces.getJbpmId()));
			dto.setHasStartTaskWithSameKey(new Boolean[mateixaKeyIEntorn.size()]);
			for (int i = 0; i < mateixaKeyIEntorn.size(); i++) {
				dto.getHasStartTaskWithSameKey()[i] = new Boolean(
						hasStartTask(mateixaKeyIEntorn.get(i)));
			}
		}
		return dto;
	}
	private boolean hasStartTask(DefinicioProces definicioProces) {
		Long definicioProcesId = definicioProces.getId();
		Boolean result = hasStartTask.get(definicioProcesId);
		if (result == null) {
			result = new Boolean(false);
			String startTaskName = jbpmDao.getStartTaskName(
					definicioProces.getJbpmId());
			if (startTaskName != null) {
				Tasca tasca = tascaDao.findAmbActivityNameIDefinicioProces(
						startTaskName,
						definicioProces.getId());
				if (tasca != null) {
					List<CampTasca> camps = campTascaDao.findAmbTascaOrdenats(tasca.getId());
					result = new Boolean(camps.size() > 0);
				}
			}
			hasStartTask.put(definicioProcesId, result);
		}
		return result.booleanValue();
	}

	private void reordenarCampsTasca(Long tascaId) {
		List<CampTasca> campsTasca = campTascaDao.findAmbTascaOrdenats(tascaId);
		int i = 0;
		for (CampTasca campTasca: campsTasca)
			campTasca.setOrder(i++);
	}
	private void reordenarDocumentsTasca(Long tascaId) {
		List<DocumentTasca> documentsTasca = documentTascaDao.findAmbTascaOrdenats(tascaId);
		int i = 0;
		for (DocumentTasca documentTasca: documentsTasca) {
			documentTasca.setOrder(i++);
			documentTascaDao.merge(documentTasca);
			documentTascaDao.flush();
		}
	}
	private void reordenarFirmesTasca(Long tascaId) {
		List<FirmaTasca> firmesTasca = firmaTascaDao.findAmbTascaOrdenats(tascaId);
		int i = 0;
		for (FirmaTasca firmaTasca: firmesTasca)
			firmaTasca.setOrder(i++);
	}
	private void reordenarValidacionsTasca(Long tascaId) {
		List<Validacio> validacions = validacioDao.findAmbTascaOrdenats(tascaId);
		int i = 0;
		for (Validacio validacio: validacions)
			validacio.setOrdre(i++);
	}
	private void reordenarValidacionsCamp(Long campId) {
		List<Validacio> validacions = validacioDao.findAmbCampOrdenats(campId);
		int i = 0;
		for (Validacio validacio: validacions)
			validacio.setOrdre(i++);
	}
	private void reordenarMembresRegistre(Long registreId) {
		List<CampRegistre> membres = campRegistreDao.findAmbRegistreOrdenats(registreId);
		int i = 0;
		for (CampRegistre campRegistre: membres)
			campRegistre.setOrdre(i++);
	}
	private void reordenarAgrupacions(Long definicioProcesId) {
		List<CampAgrupacio> campsAgrupacio = campAgrupacioDao.findAmbDefinicioProcesOrdenats(definicioProcesId);
		int i = 0;
		for (CampAgrupacio campAgrupacio: campsAgrupacio)
			campAgrupacio.setOrdre(i++);
	}
	private void reordenarCamps(Long definicioProcesId, Long agrupacioId) {
		List<Camp> camps = campDao.findAmbDefinicioProcesIAgrupacioOrdenats(definicioProcesId, agrupacioId);
		int i = 0;
		for (Camp camp: camps)
			camp.setOrdre(i++);
	}
	private void reordenarConsultaCamp(Long consultaId, TipusConsultaCamp tipus) {
		List<ConsultaCamp> consultaCamp = consultaCampDao.findAmbConsultaITipusOrdenats(consultaId, tipus);
		int i = 0;
		for (ConsultaCamp camps: consultaCamp)
			camps.setOrdre(i++);
	}

	private void reordenarConsultes(Long entornId, Long expedientTipusId) {
		
		List<Consulta> consultes = this.findConsultesAmbEntornIExpedientTipusOrdenat(
				entornId,
				expedientTipusId);
		
		int i = 0;
		for (Consulta consulta: consultes)
			consulta.setOrdre(i++);
	}

	private void reordenarEnumeracioValors(Long enumeracioId) {
		List<EnumeracioValors> valors = enumeracioValorsDao.findAmbEnumeracioOrdenat(enumeracioId);
		int i = 0;
		for (EnumeracioValors valor: valors)
			valor.setOrdre(i++);
	}

	private boolean comprovarEntorn(Long entornId, Long definicioProcesId) {
		Entorn entorn = entornDao.getById(entornId, false);
		if (entorn != null) {
			DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
			return entorn.equals(definicioProces.getEntorn());
		}
		return false;
	}
	private boolean comprovarExpedientTipus(Long expedientTipusId, Long definicioProcesId) {
		ExpedientTipus expedientTipus = expedientTipusDao.getById(expedientTipusId, false);
		if (expedientTipus != null) {
			DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
			return expedientTipus.equals(definicioProces.getExpedientTipus());
		}
		return false;
	}

	private void copiarDadesDefinicioProces(
			DefinicioProces origen,
			DefinicioProces desti) {
		// Propaga les agrupacions
		Map<String, CampAgrupacio> agrupacions = new HashMap<String, CampAgrupacio>();
		for (CampAgrupacio agrupacio: origen.getAgrupacions()) {
			CampAgrupacio nova = new CampAgrupacio(
					desti,
					agrupacio.getCodi(),
					agrupacio.getNom(),
					agrupacio.getOrdre());
			nova.setDescripcio(agrupacio.getDescripcio());
			campAgrupacioDao.saveOrUpdate(nova);
			agrupacions.put(agrupacio.getCodi(), nova);
		}
		// Propaga les accions
		for (Accio accio: origen.getAccions()) {
			Accio nova = new Accio(
					desti,
					accio.getCodi(),
					accio.getNom(),
					accio.getJbpmAction());
			nova.setDescripcio(accio.getDescripcio());
			nova.setOculta(accio.isOculta());
			nova.setPublica(accio.isPublica());
			nova.setRols(accio.getRols());
			accioDao.saveOrUpdate(nova);
		}
		// Propaga els camps
		Map<String, Camp> camps = new HashMap<String, Camp>();
		for (Camp camp: origen.getCamps()) {
			Camp nou = new Camp(
					desti,
					camp.getCodi(),
					camp.getTipus(),
					camp.getEtiqueta());
			nou.setObservacions(camp.getObservacions());
			nou.setMultiple(camp.isMultiple());
			nou.setOcult(camp.isOcult());
			nou.setDomini(camp.getDomini());
			nou.setDominiId(camp.getDominiId());
			nou.setDominiCampText(camp.getDominiCampText());
			nou.setDominiCampValor(camp.getDominiCampValor());
			nou.setDominiParams(camp.getDominiParams());
			nou.setDominiIntern(camp.isDominiIntern());
			nou.setEnumeracio(camp.getEnumeracio());
			nou.setJbpmAction(camp.getJbpmAction());
			nou.setOrdre(camp.getOrdre());
			nou.setIgnored(camp.isIgnored());
			campDao.saveOrUpdate(nou);
			camps.put(nou.getCodi(), nou);
			// Copia les validacions dels camps
			for (Validacio validacio: camp.getValidacions()) {
				Validacio novaValidacio = new Validacio(
						nou,
						validacio.getExpressio(),
						validacio.getMissatge());
				nou.addValidacio(novaValidacio);
			}
			// Configura l'agrupació
			if (camp.getAgrupacio() != null)
				nou.setAgrupacio(agrupacions.get(camp.getAgrupacio().getCodi()));
		}
		// Propaga els membres dels camps de tipus registre
		for (Camp camp: origen.getCamps()) {
			if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
				for (CampRegistre membre: camp.getRegistreMembres()) {
					CampRegistre campRegistre = new CampRegistre(
							camps.get(camp.getCodi()),
							camps.get(membre.getMembre().getCodi()),
							membre.getOrdre());
					campRegistre.setLlistar(membre.isLlistar());
					campRegistre.setObligatori(membre.isObligatori());
					campRegistreDao.saveOrUpdate(campRegistre);
				}
			}
		}
		// Propaga els documents
		Map<String, Document> documents = new HashMap<String, Document>();
		for (Document document: origen.getDocuments()) {
			Document nou = new Document(
					desti,
					document.getCodi(),
					document.getNom());
			documentDao.saveOrUpdate(nou);
			nou.setDescripcio(document.getDescripcio());
			nou.setArxiuNom(document.getArxiuNom());
			nou.setArxiuContingut(document.getArxiuContingut());
			nou.setPlantilla(document.isPlantilla());
			nou.setCustodiaCodi(document.getCustodiaCodi());
			nou.setContentType(document.getContentType());
			nou.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
			nou.setAdjuntarAuto(document.isAdjuntarAuto());
			nou.setConvertirExtensio(document.getConvertirExtensio());
			if (document.getCampData() != null)
				nou.setCampData(camps.get(document.getCampData().getCodi()));
			documentDao.saveOrUpdate(nou);
			documentDao.flush();
			documents.put(nou.getCodi(), nou);
		}
		// Propaga els terminis
		for (Termini termini: origen.getTerminis()) {
			Termini nou = new Termini(
					desti,
					termini.getCodi(),
					termini.getNom(),
					termini.getAnys(),
					termini.getMesos(),
					termini.getDies(),
					termini.isLaborable());
			nou.setDescripcio(termini.getDescripcio());
			nou.setDiesPrevisAvis(termini.getDiesPrevisAvis());
			nou.setAlertaPrevia(termini.isAlertaPrevia());
			nou.setAlertaFinal(termini.isAlertaFinal());
			nou.setAlertaCompletat(termini.isAlertaCompletat());
			terminiDao.saveOrUpdate(nou);
		}
		// Propaga les dades de les tasques
		for (Tasca nova: desti.getTasques()) {
			for (Tasca vella: origen.getTasques()) {
				if (nova.getJbpmName().equals(vella.getJbpmName())) {
					nova.setNom(vella.getNom());
					nova.setTipus(vella.getTipus());
					nova.setMissatgeInfo(vella.getMissatgeInfo());
					nova.setMissatgeWarn(vella.getMissatgeWarn());
					nova.setNomScript(vella.getNomScript());
					nova.setRecursForm(vella.getRecursForm());
					nova.setFormExtern(vella.getFormExtern());
					nova.setExpressioDelegacio(vella.getExpressioDelegacio());
					nova.setTramitacioMassiva(vella.isTramitacioMassiva());
					// Copia els camps de les tasques
					for (CampTasca camp: vella.getCamps()) {
						CampTasca nouCamp = new CampTasca(
								camps.get(camp.getCamp().getCodi()),
								nova,
								camp.isReadFrom(),
								camp.isWriteTo(),
								camp.isRequired(),
								camp.isReadOnly(),
								camp.getOrder());
						nova.addCamp(nouCamp);
					}
					// Copia els documents de la tasca
					for (DocumentTasca document: vella.getDocuments()) {
						DocumentTasca nouDocument = new DocumentTasca(
								documents.get(document.getDocument().getCodi()),
								nova,
								document.isRequired(),
								document.isReadOnly(),
								document.getOrder());
						nova.addDocument(nouDocument);
					}
					// Copia les firmes de la tasca
					for (FirmaTasca firma: vella.getFirmes()) {
						FirmaTasca novaFirma = new FirmaTasca(
								documents.get(firma.getDocument().getCodi()),
								nova,
								firma.isRequired(),
								firma.getOrder());
						nova.addFirma(novaFirma);
					}
					// Copia les validacions de la tasca
					for (Validacio validacio: vella.getValidacions()) {
						Validacio novaValidacio = new Validacio(
								nova,
								validacio.getExpressio(),
								validacio.getMissatge());
						nova.addValidacio(novaValidacio);
					}
					break;
				}
			}
		}
	}

	private void importacio(
			Long entornId,
			Long expedientTipusId,
			DefinicioProcesExportacio exportacio,
			DefinicioProces definicioProces) {
		// Propaga les agrupacions
		Map<String, CampAgrupacio> agrupacions = new HashMap<String, CampAgrupacio>();
		for (AgrupacioExportacio agrupacio: exportacio.getAgrupacions()) {
			CampAgrupacio nova = campAgrupacioDao.findAmbDefinicioProcesICodi(
					definicioProces.getId(),
					agrupacio.getCodi());
			if (nova != null) {
				nova.setNom(agrupacio.getNom());
				nova.setOrdre(agrupacio.getOrdre());
			} else {
				nova = new CampAgrupacio(
						definicioProces,
						agrupacio.getCodi(),
						agrupacio.getNom(),
						agrupacio.getOrdre());
			}
			nova.setDescripcio(agrupacio.getDescripcio());
			campAgrupacioDao.saveOrUpdate(nova);
			agrupacions.put(agrupacio.getCodi(), nova);
		}
		// Propaga les accions
		for (AccioExportacio accio: exportacio.getAccions()) {
			Accio nova = accioDao.findAmbDefinicioProcesICodi(
					definicioProces.getId(),
					accio.getCodi());
			if (nova != null) {
				nova.setNom(accio.getNom());
				nova.setJbpmAction(accio.getJbpmAction());
			} else {
				nova = new Accio(
						definicioProces,
						accio.getCodi(),
						accio.getNom(),
						accio.getJbpmAction());
			}
			nova.setDescripcio(accio.getDescripcio());
			nova.setPublica(accio.isPublica());
			nova.setOculta(accio.isOculta());
			nova.setRols(accio.getRols());
			accioDao.saveOrUpdate(nova);
		}
		// Propaga els camps
		Map<String, Camp> camps = new HashMap<String, Camp>();
		for (CampExportacio camp: exportacio.getCamps()) {
			Camp nou = campDao.findAmbDefinicioProcesICodi(
					definicioProces.getId(),
					camp.getCodi());
			
			if (nou != null) {
				nou.setTipus(camp.getTipus());
				nou.setEtiqueta(camp.getEtiqueta());
			} else {
				nou = new Camp(
						definicioProces,
						camp.getCodi(),
						camp.getTipus(),
						camp.getEtiqueta());
			}
			nou.setIgnored(camp.isIgnored());
			nou.setObservacions(camp.getObservacions());
			nou.setDominiId(camp.getDominiId());
			nou.setDominiParams(camp.getDominiParams());
			nou.setDominiCampText(camp.getDominiCampText());
			nou.setDominiCampValor(camp.getDominiCampValor());
			nou.setDominiCacheText(camp.isDominiCacheText());
			nou.setMultiple(camp.isMultiple());
			nou.setOcult(camp.isOcult());
			nou.setDominiIntern(camp.isDominiIntern());
			nou.setJbpmAction(camp.getJbpmAction());
			nou.setOrdre(camp.getOrdre());
			
			if (camp.getCodiEnumeracio() != null) {
				
				Enumeracio enumeracioEntorn = enumeracioDao.findAmbEntornSenseTipusExpICodi(
						entornId,
						camp.getCodiEnumeracio());
				
				if(enumeracioEntorn==null){
					Enumeracio enumeracio = enumeracioDao.findAmbEntornAmbTipusExpICodi(
							entornId,
							expedientTipusId,
							camp.getCodiEnumeracio());
					if (enumeracio != null) {
						enumeracio.setCodi(camp.getCodiEnumeracio());
						nou.setEnumeracio(enumeracio);
					} else {
							enumeracio = new Enumeracio();
							enumeracio.setEntorn(entornDao.getById(entornId, false));
							enumeracio.setCodi(camp.getCodiEnumeracio());
							enumeracio.setNom(camp.getCodiEnumeracio());
							if (expedientTipusId != null)
								enumeracio.setExpedientTipus(
										expedientTipusDao.getById(expedientTipusId, false));
						enumeracioDao.saveOrUpdate(enumeracio);
					}
									
				}else{
					nou.setEnumeracio(enumeracioEntorn);
				}
				
			}
			if (camp.getCodiDomini() != null) {
				Domini domini = dominiDao.findAmbEntornICodi(entornId, camp.getCodiDomini());
				if (!camp.getCodiDomini().equalsIgnoreCase("intern")) {	
					if (domini != null) {
						nou.setDomini(domini);
					} else {
						domini = new Domini();
						domini.setEntorn(entornDao.getById(entornId, false));
						domini.setCodi(camp.getCodiDomini());
						domini.setNom(camp.getCodiDomini());
						domini.setTipus(TipusDomini.CONSULTA_SQL);
						if (expedientTipusId != null)
							domini.setExpedientTipus(
									expedientTipusDao.getById(expedientTipusId, false));
						dominiDao.saveOrUpdate(domini);
					}
				}
			}
			if (camp.getAgrupacioCodi() != null)
				nou.setAgrupacio(agrupacions.get(camp.getAgrupacioCodi()));
			// Propaga les validacions del camp
			for (Validacio validacio: nou.getValidacions())
				validacioDao.delete(validacio);
			nou.getValidacions().clear();
			for (ValidacioExportacio validacio: camp.getValidacions()) {
				Validacio nova = new Validacio(
						nou,
						validacio.getExpressio(),
						validacio.getMissatge());
				nova.setOrdre(validacio.getOrdre());
				nou.addValidacio(nova);
			}
			campDao.saveOrUpdate(nou);
			camps.put(nou.getCodi(), nou);
		}
		// Propaga els membres dels camps de tipus registre
		for (Camp camp: camps.values()) {
			if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
				for (CampRegistre campRegistre: camp.getRegistreMembres()) {
					campRegistre.getMembre().getRegistrePares().remove(campRegistre);
					campRegistre.setMembre(null);
					campRegistre.setRegistre(null);
					campRegistreDao.delete(campRegistre);
				}
				camp.getRegistreMembres().clear();
				campRegistreDao.flush();
			}
		}
		for (CampExportacio camp: exportacio.getCamps()) {
			if (camp.getTipus().equals(TipusCamp.REGISTRE)) {
				for (RegistreMembreExportacio membre: camp.getRegistreMembres()) {
					CampRegistre campRegistre = new CampRegistre(
							camps.get(camp.getCodi()),
							camps.get(membre.getCodi()),
							membre.getOrdre());
					campRegistre.setLlistar(membre.isLlistar());
					campRegistre.setObligatori(membre.isObligatori());
					campRegistreDao.saveOrUpdate(campRegistre);
				}
			}
		}
		// Propaga els documents
		Map<String, Document> documents = new HashMap<String, Document>();
		for (DocumentExportacio document: exportacio.getDocuments()) {
			Document nou = documentDao.findAmbDefinicioProcesICodi(
					definicioProces.getId(),
					document.getCodi());
			if (nou != null) {
				nou.setNom(document.getNom());
			} else {
				nou = new Document(
						definicioProces,
						document.getCodi(),
						document.getNom());
			}
			documentDao.saveOrUpdate(nou);
			nou.setDescripcio(document.getDescripcio());
			nou.setArxiuNom(document.getArxiuNom());
			nou.setArxiuContingut(document.getArxiuContingut());
			nou.setPlantilla(document.isPlantilla());
			nou.setCustodiaCodi(document.getCustodiaCodi());
			nou.setContentType(document.getContentType());
			nou.setTipusDocPortasignatures(document.getTipusDocPortasignatures());
			nou.setAdjuntarAuto(document.isAdjuntarAuto());
			if (document.getCodiCampData() != null)
				nou.setCampData(camps.get(document.getCodiCampData()));
			nou.setConvertirExtensio(document.getConvertirExtensio());
			nou.setExtensionsPermeses(document.getExtensionsPermeses());
			documentDao.saveOrUpdate(nou);
			documentDao.flush();
			documents.put(nou.getCodi(), nou);
		}
		// Propaga els terminis
		for (TerminiExportacio termini: exportacio.getTerminis()) {
			Termini nou = terminiDao.findAmbDefinicioProcesICodi(
					definicioProces.getId(),
					termini.getCodi());
			if (nou != null) {
				nou.setNom(termini.getNom());
				nou.setAnys(termini.getAnys());
				nou.setMesos(termini.getMesos());
				nou.setDies(termini.getDies());
				nou.setLaborable(termini.isLaborable());
			} else {
				nou = new Termini(
						definicioProces,
						termini.getCodi(),
						termini.getNom(),
						termini.getAnys(),
						termini.getMesos(),
						termini.getDies(),
						termini.isLaborable());
			}
			nou.setDescripcio(termini.getDescripcio());
			nou.setDiesPrevisAvis(termini.getDiesPrevisAvis());
			nou.setAlertaPrevia(termini.isAlertaPrevia());
			nou.setAlertaFinal(termini.isAlertaFinal());
			nou.setAlertaCompletat(termini.isAlertaCompletat());
			nou.setManual(termini.isManual());
			terminiDao.saveOrUpdate(nou);
		}
		// Propaga les tasques
		for (TascaExportacio vella: exportacio.getTasques()) {
			for (Tasca nova: definicioProces.getTasques()) {
				if (nova.getJbpmName().equals(vella.getJbpmName())) {
					nova.setNom(vella.getNom());
					nova.setTipus(vella.getTipus());
					nova.setMissatgeInfo(vella.getMissatgeInfo());
					nova.setMissatgeWarn(vella.getMissatgeWarn());
					nova.setNomScript(vella.getNomScript());
					nova.setExpressioDelegacio(vella.getExpressioDelegacio());
					nova.setRecursForm(vella.getRecursForm());
					nova.setFormExtern(vella.getFormExtern());
					nova.setTramitacioMassiva(vella.isTramitacioMassiva());
					// Propaga els camps de la tasca
					List<CampTascaExportacio> llistaCampsExportacio = new ArrayList<CampTascaExportacio>();
					llistaCampsExportacio.addAll(vella.getCamps());
					// Ordena la llista de camps tasca de l'exportació origen
					Collections.sort(
							llistaCampsExportacio,
							new Comparator<CampTascaExportacio>() {
								public int compare(
										CampTascaExportacio o1,
										CampTascaExportacio o2) {
									return new Integer(o1.getOrder()).compareTo(new Integer(o2.getOrder()));
								}
								
							});
					int indexExport = 0;
					for (CampTascaExportacio cte: llistaCampsExportacio)
						cte.setOrder(indexExport++);
					// Per evitar el ConstraintViolation amb l'ordre
					int indexConstraint = nova.getCamps().size() + llistaCampsExportacio.size();
					for (CampTasca campTasca: nova.getCamps())
						campTasca.setOrder(indexConstraint++);
					campTascaDao.flush();
					// Inserta els camps de l'exportació que no existeixin
					for (CampTascaExportacio campTasca: llistaCampsExportacio) {
						boolean trobat = false;
						for (CampTasca ct: nova.getCamps()) {
							if (ct.getCamp().getCodi().equals(campTasca.getCampCodi())) {
								trobat = true;
								break;
							} 
						}
						if (!trobat) {
							CampTasca nouct = new CampTasca(
									camps.get(campTasca.getCampCodi()),
									nova,
									campTasca.isReadFrom(),
									campTasca.isWriteTo(),
									campTasca.isRequired(),
									campTasca.isReadOnly(),
									campTasca.getOrder());
							campTascaDao.saveOrUpdate(nouct);
							nova.addCamp(nouct);
						}
					}
					// Actualitza els camps ja existents
					int indexCamp = llistaCampsExportacio.size();
					for (CampTasca campTasca: nova.getCamps()) {
						boolean trobat = false;
						for (CampTascaExportacio cte: llistaCampsExportacio) {
							if (campTasca.getCamp().getCodi().equals(cte.getCampCodi())) {
								campTasca.setReadFrom(cte.isReadFrom());
								campTasca.setWriteTo(cte.isWriteTo());
								campTasca.setRequired(cte.isRequired());
								campTasca.setReadOnly(cte.isReadOnly());
								campTasca.setOrder(cte.getOrder());
								trobat = true;
								break;
							}
						}
						if (!trobat) {
							campTasca.setOrder(indexCamp++);
						}
					}
					// Propaga els documents de la tasca
					int indexDoc = nova.getDocuments().size();
					List<DocumentTasca> documentsNous = new ArrayList<DocumentTasca>();
					List<DocumentTascaExportacio> llistaDocsExportacio = new ArrayList<DocumentTascaExportacio>();
					llistaDocsExportacio.addAll(vella.getDocuments());
					Collections.sort(
							llistaDocsExportacio,
							new Comparator<DocumentTascaExportacio>() {
								public int compare(
										DocumentTascaExportacio o1,
										DocumentTascaExportacio o2) {
									return new Integer(o1.getOrder()).compareTo(new Integer(o2.getOrder()));
								}
								
							});
					for (DocumentTascaExportacio documentTasca: llistaDocsExportacio) {
						boolean trobat = false;
						for (DocumentTasca dt: nova.getDocuments()) {
							if (dt.getDocument().getCodi().equals(documentTasca.getDocumentCodi())) {
								dt.setRequired(documentTasca.isRequired());
								dt.setReadOnly(documentTasca.isReadOnly());
								dt.setOrder(indexDoc++);
								documentsNous.add(dt);
								trobat = true;
								break;
							} 
						}
						if (!trobat) {
							DocumentTasca noudt = new DocumentTasca(
									documents.get(documentTasca.getDocumentCodi()),
									nova,
									documentTasca.isRequired(),
									documentTasca.isReadOnly(),
									indexDoc++);
							documentTascaDao.saveOrUpdate(noudt);
							documentsNous.add(noudt);
						}
					}
					for (DocumentTasca dt: nova.getDocuments()) {
						boolean trobat = false;
						for (DocumentTascaExportacio documentTasca: llistaDocsExportacio) {
							if (dt.getDocument().getCodi().equals(documentTasca.getDocumentCodi())) {
								trobat = true;
								break;
							}
						}
						if (!trobat) {
							dt.setOrder(indexDoc++);
							documentTascaDao.saveOrUpdate(dt);
							documentsNous.add(dt);
						}
					}
					nova.getDocuments().clear();
					nova.getDocuments().addAll(documentsNous);
					// Propaga les firmes de la tasca
					nova.getFirmes().clear();
					for (FirmaTascaExportacio firmaTasca: vella.getFirmes()) {
						FirmaTasca nouft = new FirmaTasca(
								documents.get(firmaTasca.getDocumentCodi()),
								nova,
								firmaTasca.isRequired(),
								firmaTasca.getOrder());
						nova.addFirma(nouft);
						firmaTascaDao.saveOrUpdate(nouft);
					}
					// Propaga les validacions de la tasca
					for (ValidacioExportacio validacio: vella.getValidacions()) {
						Validacio novav = new Validacio(
								nova,
								validacio.getExpressio(),
								validacio.getMissatge());
						nova.addValidacio(novav);
						validacioDao.saveOrUpdate(novav);
					}
					break;
				}
			}
		}
	}

	private String getRecursFormPerTasca(String jbpmId, String nomTasca) {
		String prefixRecursBo = "forms/" + nomTasca;
		for (String resourceName: jbpmDao.getResourceNames(jbpmId)) {
			if (resourceName.startsWith(prefixRecursBo))
				return resourceName;
		}
		return null;
	}

	public List<DefinicioProcesDto> getSubprocessosByProces(String jbpmPdId) {
		List<DefinicioProcesDto> subprocessos = new ArrayList<DefinicioProcesDto>();
		List<String> ids = new ArrayList<String>(); 
		afegirJbpmIdProcesAmbSubprocessos(jbpmDao.getProcessDefinition(jbpmPdId), ids, false);
		
		for(String id: ids){
			subprocessos.add(findDefinicioProcesAmbJbpmId(id));
		}
		return subprocessos;
	}
	private void afegirJbpmIdProcesAmbSubprocessos(
			JbpmProcessDefinition jpd,
			List<String> jbpmIds, 
			Boolean incloure) {
		if (jpd != null) {
			List<JbpmProcessDefinition> subPds = jbpmDao.getSubProcessDefinitions(jpd.getId());
			if (subPds != null) {
				for (JbpmProcessDefinition subPd: subPds) {
					afegirJbpmIdProcesAmbSubprocessos(subPd, jbpmIds, true);
					if (!jbpmIds.contains(subPd.getId()))
						jbpmIds.add(subPd.getId());
				}
			}
			if (!jbpmIds.contains(jpd.getId()) && incloure)
				jbpmIds.add(jpd.getId());
		}
	}
	private DefinicioProcesDto findDefinicioProcesAmbJbpmId(String jbpmId) {
		String processDefinitionId = jbpmDao.getProcessDefinition(jbpmId).getId();
		return toDto(definicioProcesDao.findAmbJbpmId(processDefinitionId), false);
	}
	
	private void afegirJbpmKeyProcesAmbSubprocessos(
			JbpmProcessDefinition jpd,
			List<String> jbpmKeys) {
		List<JbpmProcessDefinition> subPds = jbpmDao.getSubProcessDefinitions(jpd.getId());
		if (subPds != null) {
			for (JbpmProcessDefinition subPd: subPds) {
				afegirJbpmKeyProcesAmbSubprocessos(subPd, jbpmKeys);
				if (!jbpmKeys.contains(subPd.getKey()))
					jbpmKeys.add(subPd.getKey());
			}
		}
		if (!jbpmKeys.contains(jpd.getKey()))
			jbpmKeys.add(jpd.getKey());
	}
	
	public List<DefinicioProcesDto> findDefinicionsProcesAmbExpedientTipus(ExpedientTipus expedientTipus) {
		
		List<DefinicioProcesDto> llista = new ArrayList<DefinicioProcesDto>();
		JbpmProcessDefinition jpd = null;
		List<String> jbpmKeys = new ArrayList<String>();
		
		List<DefinicioProcesDto> defsProces = findDarreresAmbExpedientTipusEntorn(expedientTipus.getEntorn().getId(), expedientTipus.getId(), true);
		for (DefinicioProcesDto definicioProces: defsProces) {
			if (definicioProces.getJbpmKey().equals(expedientTipus.getJbpmProcessDefinitionKey())) {
				jpd = jbpmDao.getProcessDefinition(definicioProces.getJbpmId());
				break;
			}
		}
		if (jpd != null) {
			afegirJbpmKeyProcesAmbSubprocessos(jpd, jbpmKeys);
			for (String jbpmKey: jbpmKeys) {
				for (DefinicioProcesDto definicioProces : defsProces) {
//					if (	definicioProces.getExpedientTipus() != null &&
//							definicioProces.getExpedientTipus().getId().equals(expedientTipus.getId())) {
					if (definicioProces.getJbpmKey().equals(jbpmKey)) {
						llista.add(definicioProces);
						break;
					}
//					}
				}
			}
		}
		return llista;
	}

	public List<Camp> getVariablesSenseAgruapcio(Long definicioProcesId) {
		return campDao.findVariablesSenseAgrupacio(definicioProcesId);
	}

	public void afegirCampAgrupacio(Long definicioProcesId, String agrupacioCodi, Long id) {
		CampAgrupacio campAgrupacio = campAgrupacioDao.findAmbDefinicioProcesICodi(definicioProcesId, agrupacioCodi);
		Camp camp = campDao.getById(id, false);
		camp.setAgrupacio(campAgrupacio);
		Integer maxOrdre = campDao.getNextOrdre(definicioProcesId, camp.getAgrupacio().getId());
		camp.setOrdre(maxOrdre);
		campDao.merge(camp);
	}



	private void filtrarCampsConsultaFiltre(
			Long consultaId,
			List<Camp> camps) {
		Consulta consulta = consultaDao.getById(consultaId, false);
		if (consulta.getValorsPredefinits() != null) {
			String[] parelles = consulta.getValorsPredefinits().split(",");
			for (int i = 0; i < parelles.length; i++) {
				String[] parella = parelles[i].split(":");
				if (parella.length == 2) {
					String campCodi = parella[0];
					Iterator<Camp> it = camps.iterator();
					while (it.hasNext()) {
						if (it.next().getCodi().equals(campCodi))
							it.remove();
					}
				}
			}
		}
	}
	private ServiceUtils getServiceUtils() {
		if (serviceUtils == null) {
			serviceUtils = new ServiceUtils(
					expedientDao,
					definicioProcesDao,
					campDao,
					consultaCampDao,
					luceneDao,
					dtoConverter,
					jbpmDao,
					aclServiceDao,
					messageSource);
		}
		return serviceUtils;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void goToSignaturaTasca(Long id, int NouOrd) {
		FirmaTasca firmaTasca = getFirmaTascaById(id);
		int ordreAntic = firmaTasca.getOrder();
		
		// Si no s'ha canviat l'ordre, sortim sense fer res.
		if (ordreAntic == NouOrd) return;
		firmaTasca.setOrder(-1);				
		Tasca tasca = firmaTasca.getTasca();
		List<FirmaTasca> firmes = tasca.getFirmes();
		//List<CampTasca> camps = (List<CampTasca>) campTascaDao.findAmbTascaOrdenats(tasca.getId());
		
		if (ordreAntic < NouOrd) {
			//Collections.reverse(camps);
			Collections.sort(
					firmes,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((FirmaTasca)o1).getOrder()<((FirmaTasca)o2).getOrder() ? -1 : ((FirmaTasca)o1).getOrder()==((FirmaTasca)o2).getOrder() ? 0 : 1);
					}
					});
			
			for (FirmaTasca ft : firmes){
				int ordre = ft.getOrder();
				if (ordre > ordreAntic) {
					if (ordre <= NouOrd) {
						ft.setOrder(ordre - 1);
						firmaTascaDao.saveOrUpdate(ft);
						firmaTascaDao.flush();
					}
				}
			}
	
		} else {
			
			Collections.sort(
					firmes,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((FirmaTasca)o1).getOrder()>((FirmaTasca)o2).getOrder() ? -1 : ((FirmaTasca)o1).getOrder()==((FirmaTasca)o2).getOrder() ? 0 : 1);
					}
					});
			
			for (FirmaTasca ft : firmes){
			
				int ordre = ft.getOrder();
				if (ordre < ordreAntic)  {
					if (ordre >= NouOrd) {
						ft.setOrder(ordre + 1);
						firmaTascaDao.saveOrUpdate(ft);
						firmaTascaDao.flush();
					}
				}
			}
		}
		firmaTasca.setOrder(NouOrd);
		firmaTascaDao.saveOrUpdate(firmaTasca);
		tascaDao.merge(tasca);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void goToDocumentTasca(Long id, int NouOrd) {
		DocumentTasca documentTasca = getDocumentTascaById(id);
		int ordreAntic = documentTasca.getOrder();
		
		// Si no s'ha canviat l'ordre, sortim sense fer res.
		if (ordreAntic == NouOrd) return;
		documentTasca.setOrder(-1);				
		Tasca tasca = documentTasca.getTasca();
		List<DocumentTasca> documents = tasca.getDocuments();
		//List<CampTasca> camps = (List<CampTasca>) campTascaDao.findAmbTascaOrdenats(tasca.getId());
		
		if (ordreAntic < NouOrd) {
			//Collections.reverse(camps);
			Collections.sort(
					documents,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((DocumentTasca)o1).getOrder()<((DocumentTasca)o2).getOrder() ? -1 : ((DocumentTasca)o1).getOrder()==((DocumentTasca)o2).getOrder() ? 0 : 1);
					}
					});
			
			for (DocumentTasca dt : documents){
				int ordre = dt.getOrder();
				if (ordre > ordreAntic) {
					if (ordre <= NouOrd) {
						dt.setOrder(ordre - 1);
						documentTascaDao.saveOrUpdate(dt);
						documentTascaDao.flush();
					}
				}
			}
	
		} else {
			
			Collections.sort(
					documents,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((DocumentTasca)o1).getOrder()>((DocumentTasca)o2).getOrder() ? -1 : ((DocumentTasca)o1).getOrder()==((DocumentTasca)o2).getOrder() ? 0 : 1);
					}
					});
			
			for (DocumentTasca dt : documents){
			
				int ordre = dt.getOrder();
				if (ordre < ordreAntic)  {
					if (ordre >= NouOrd) {
						dt.setOrder(ordre + 1);
						documentTascaDao.saveOrUpdate(dt);
						documentTascaDao.flush();
					}
				}
			}
		}
		documentTasca.setOrder(NouOrd);
		documentTascaDao.saveOrUpdate(documentTasca);
		tascaDao.merge(tasca);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void goToCampTasca(Long id, int NouOrd) {
		CampTasca campTasca = getCampTascaById(id);
		Tasca tasca = campTasca.getTasca();
		List<CampTasca> camps = tasca.getCamps();
		int nouOrdre= 0;
		for(CampTasca ct:camps){
			int ordre = ct.getOrder();
			if(ordre!=nouOrdre){
				ct.setOrder(nouOrdre);
			}
			campTascaDao.saveOrUpdate(ct);
			campTascaDao.flush();
			nouOrdre++;
		}
		
		
		int ordreAntic = campTasca.getOrder();
		
		// Si no s'ha canviat l'ordre, sortim sense fer res.
		if (ordreAntic == NouOrd) return;
		campTasca.setOrder(-1);				
		
		//List<CampTasca> camps = (List<CampTasca>) campTascaDao.findAmbTascaOrdenats(tasca.getId());
		
		if (ordreAntic < NouOrd) {
			//Collections.reverse(camps);
			Collections.sort(
					camps,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((CampTasca)o1).getOrder()<((CampTasca)o2).getOrder() ? -1 : ((CampTasca)o1).getOrder()==((CampTasca)o2).getOrder() ? 0 : 1);
					}
					});
			
			for (CampTasca ct : camps){
				int ordre = ct.getOrder();
				if (ordre > ordreAntic) {
					if (ordre <= NouOrd) {
						ct.setOrder(ordre - 1);
						campTascaDao.saveOrUpdate(ct);
						campTascaDao.flush();
					}
				}
			}
	
		} else {
			
			Collections.sort(
					camps,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((CampTasca)o1).getOrder()>((CampTasca)o2).getOrder() ? -1 : ((CampTasca)o1).getOrder()==((CampTasca)o2).getOrder() ? 0 : 1);
					}
					});
			
			for (CampTasca ct : camps){
			
				int ordre = ct.getOrder();
				if (ordre < ordreAntic)  {
					if (ordre >= NouOrd) {
						ct.setOrder(ordre + 1);
						campTascaDao.saveOrUpdate(ct);
						campTascaDao.flush();
					}
				}
			}
		}
		campTasca.setOrder(NouOrd);
		campTascaDao.saveOrUpdate(campTasca);
		tascaDao.merge(tasca);
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void goToCampEstat(Long id, int NouOrd) {
		Estat estat = getEstatById(id);
		int ordreAntic = estat.getOrdre();
		ExpedientTipus expTip = estat.getExpedientTipus();
		List<Estat> estats = findEstatAmbExpedientTipus(expTip.getId());
		
		// Si no s'ha canviat l'ordre, sortim sense fer res.
		if (ordreAntic == NouOrd) return;
		estat.setOrdre(-1);				
				
		if (ordreAntic < NouOrd) {
			//Collections.reverse(camps);
			Collections.sort(
					estats,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((Estat)o1).getOrdre()<((Estat)o2).getOrdre() ? -1 : ((Estat)o1).getOrdre()==((Estat)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (Estat ce : estats){
				int ordre = ce.getOrdre();
				if (ordre > ordreAntic) {
					if (ordre <= NouOrd) {
						ce.setOrdre(ordre - 1);
						estatDao.saveOrUpdate(ce);
						estatDao.flush();
					}
				}
			}
	
		} else {
			
			Collections.sort(
					estats,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((Estat)o1).getOrdre()>((Estat)o2).getOrdre() ? -1 : ((Estat)o1).getOrdre()==((Estat)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (Estat ct : estats){
			
				int ordre = ct.getOrdre();
				if (ordre < ordreAntic)  {
					if (ordre >= NouOrd) {
						ct.setOrdre(ordre + 1);
						estatDao.saveOrUpdate(ct);
						estatDao.flush();
					}
				}
			}
		}
		estat.setOrdre(NouOrd);
		estatDao.saveOrUpdate(estat);
		estatDao.merge(estat);
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void goToConsultaCamp(Long id, int NouOrd) {
		ConsultaCamp consultaCamp = getConsultaCampById(id);
		Long consultaId = consultaCamp.getConsulta().getId();
		int ordreAntic = consultaCamp.getOrdre();
		List<ConsultaCamp> camps = findCampsConsulta(consultaId, consultaCamp.getTipus());
		
		// Si no s'ha canviat l'ordre, sortim sense fer res.
		if (ordreAntic == NouOrd) return;
		consultaCamp.setOrdre(-1);				
				
		if (ordreAntic < NouOrd) {
			//Collections.reverse(camps);
			Collections.sort(
					camps,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((ConsultaCamp)o1).getOrdre()<((ConsultaCamp)o2).getOrdre() ? -1 : ((ConsultaCamp)o1).getOrdre()==((ConsultaCamp)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (ConsultaCamp ce : camps){
				int ordre = ce.getOrdre();
				if (ordre > ordreAntic) {
					if (ordre <= NouOrd) {
						ce.setOrdre(ordre - 1);
						consultaCampDao.saveOrUpdate(ce);
						consultaCampDao.flush();
					}
				}
			}
	
		} else {
			
			Collections.sort(
					camps,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((ConsultaCamp)o1).getOrdre()>((ConsultaCamp)o2).getOrdre() ? -1 : ((ConsultaCamp)o1).getOrdre()==((ConsultaCamp)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (ConsultaCamp ct : camps){
			
				int ordre = ct.getOrdre();
				if (ordre < ordreAntic)  {
					if (ordre >= NouOrd) {
						ct.setOrdre(ordre + 1);
						consultaCampDao.saveOrUpdate(ct);
						consultaCampDao.flush();
					}
				}
			}
		}
		consultaCamp.setOrdre(NouOrd);
		consultaCampDao.saveOrUpdate(consultaCamp);
		consultaCampDao.merge(consultaCamp);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void goToCampValidacio(Long id, int NouOrd) {
		Validacio validacio = getValidacioById(id);
		int ordreAntic = validacio.getOrdre();
		Long campId = validacio.getCamp().getId();
		List<Validacio> validacions = findValidacionsAmbCamp(campId);
		
		// Si no s'ha canviat l'ordre, sortim sense fer res.
		if (ordreAntic == NouOrd) return;
		validacio.setOrdre(-1);				
				
		if (ordreAntic < NouOrd) {
			//Collections.reverse(camps);
			Collections.sort(
					validacions,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((Validacio)o1).getOrdre()<((Validacio)o2).getOrdre() ? -1 : ((Validacio)o1).getOrdre()==((Validacio)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (Validacio ce : validacions){
				int ordre = ce.getOrdre();
				if (ordre > ordreAntic) {
					if (ordre <= NouOrd) {
						ce.setOrdre(ordre - 1);
						validacioDao.saveOrUpdate(ce);
						validacioDao.flush();
					}
				}
			}
	
		} else {
			
			Collections.sort(
					validacions,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((Validacio)o1).getOrdre()>((Validacio)o2).getOrdre() ? -1 : ((Validacio)o1).getOrdre()==((Validacio)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (Validacio ct : validacions){
			
				int ordre = ct.getOrdre();
				if (ordre < ordreAntic)  {
					if (ordre >= NouOrd) {
						ct.setOrdre(ordre + 1);
						validacioDao.saveOrUpdate(ct);
						validacioDao.flush();
					}
				}
			}
		}
		validacio.setOrdre(NouOrd);
		validacioDao.saveOrUpdate(validacio);
		validacioDao.merge(validacio);
	}
	
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void goToEnumeracioValors(Long id, int NouOrd) {
		EnumeracioValors enumeracioValors = getEnumeracioValorsById(id);
		int ordreAntic = enumeracioValors.getOrdre();
		List<EnumeracioValors> enumeracions = findEnumeracioValorsAmbEnumeracio(enumeracioValors.getEnumeracio().getId());
		
		// Si no s'ha canviat l'ordre, sortim sense fer res.
		if (ordreAntic == NouOrd) return;
		enumeracioValors.setOrdre(-1);				
				
		if (ordreAntic < NouOrd) {
			Collections.sort(
					enumeracions,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((EnumeracioValors)o1).getOrdre()<((EnumeracioValors)o2).getOrdre() ? -1 : ((EnumeracioValors)o1).getOrdre()==((EnumeracioValors)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (EnumeracioValors ce : enumeracions){
				int ordre = ce.getOrdre();
				if (ordre > ordreAntic) {
					if (ordre <= NouOrd) {
						ce.setOrdre(ordre - 1);
						enumeracioValorsDao.saveOrUpdate(ce);
						enumeracioValorsDao.flush();
					}
				}
			}
	
		} else {
			
			Collections.sort(
					enumeracions,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((EnumeracioValors)o1).getOrdre()>((EnumeracioValors)o2).getOrdre() ? -1 : ((EnumeracioValors)o1).getOrdre()==((EnumeracioValors)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (EnumeracioValors ct : enumeracions){
			
				int ordre = ct.getOrdre();
				if (ordre < ordreAntic)  {
					if (ordre >= NouOrd) {
						ct.setOrdre(ordre + 1);
						enumeracioValorsDao.saveOrUpdate(ct);
						enumeracioValorsDao.flush();
					}
				}
			}
		}
		enumeracioValors.setOrdre(NouOrd);
		enumeracioValorsDao.saveOrUpdate(enumeracioValors);
		enumeracioValorsDao.merge(enumeracioValors);
	}
	
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void goToCampAgrupacio(Long id, int NouOrd) {
		Camp camp = getCampById(id);
		CampAgrupacio campAgrupacio = camp.getAgrupacio();
		int ordreAntic = camp.getOrdre();
		
		// Si no s'ha canviat l'ordre, sortim sense fer res.
		if (ordreAntic == NouOrd) return;
		camp.setOrdre(-1);				
		List<Camp> campsAgrupacio =  campAgrupacio.getCamps();
		
		if (ordreAntic < NouOrd) {
			Collections.sort(
					campsAgrupacio,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((Camp)o1).getOrdre()<((Camp)o2).getOrdre() ? -1 : ((Camp)o1).getOrdre()==((Camp)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (Camp ca : campsAgrupacio){
				int ordre = ca.getOrdre();
				if (ordre > ordreAntic) {
					if (ordre <= NouOrd) {
						ca.setOrdre(ordre - 1);
						campDao.saveOrUpdate(ca);
						campDao.flush();
					}
				}
			}
	
		} else {
			
			Collections.sort(
					campsAgrupacio,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((Camp)o1).getOrdre()>((Camp)o2).getOrdre() ? -1 : ((Camp)o1).getOrdre()==((Camp)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (Camp ca : campsAgrupacio){
			
				int ordre = ca.getOrdre();
				if (ordre < ordreAntic)  {
					if (ordre >= NouOrd) {
						ca.setOrdre(ordre + 1);
						campDao.saveOrUpdate(ca);
						campDao.flush();
					}
				}
			}
		}
		camp.setOrdre(NouOrd);
		campDao.saveOrUpdate(camp);
		campDao.merge(camp);
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void goToCampAgrupacioLlista(Long id, int NouOrd) {
		CampAgrupacio campAgrupacio = getCampAgrupacioById(id);
		int ordreAntic = campAgrupacio.getOrdre();
		Long procesId = campAgrupacio.getDefinicioProces().getId();
		
		// Si no s'ha canviat l'ordre, sortim sense fer res.
		if (ordreAntic == NouOrd) return;
		campAgrupacio.setOrdre(-1);				
		List<CampAgrupacio> campsAgrupacio =  findCampAgrupacioAmbDefinicioProces(procesId);
		
		if (ordreAntic < NouOrd) {
			Collections.sort(
					campsAgrupacio,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((CampAgrupacio)o1).getOrdre()<((CampAgrupacio)o2).getOrdre() ? -1 : ((CampAgrupacio)o1).getOrdre()==((CampAgrupacio)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (CampAgrupacio ca : campsAgrupacio){
				int ordre = ca.getOrdre();
				if (ordre > ordreAntic) {
					if (ordre <= NouOrd) {
						ca.setOrdre(ordre - 1);
						campAgrupacioDao.saveOrUpdate(ca);
						campAgrupacioDao.flush();
					}
				}
			}
	
		} else {
			
			Collections.sort(
					campsAgrupacio,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((CampAgrupacio)o1).getOrdre()>((CampAgrupacio)o2).getOrdre() ? -1 : ((CampAgrupacio)o1).getOrdre()==((CampAgrupacio)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (CampAgrupacio ca : campsAgrupacio){
			
				int ordre = ca.getOrdre();
				if (ordre < ordreAntic)  {
					if (ordre >= NouOrd) {
						ca.setOrdre(ordre + 1);
						campAgrupacioDao.saveOrUpdate(ca);
						campAgrupacioDao.flush();
					}
				}
			}
		}
		campAgrupacio.setOrdre(NouOrd);
		campAgrupacioDao.saveOrUpdate(campAgrupacio);
		campAgrupacioDao.merge(campAgrupacio);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void goToValors(Long id, int NouOrd) {
		EnumeracioValors enumeracioValors = getEnumeracioValorsById(id);
		int ordreAntic = enumeracioValors.getOrdre();
		Long enumeracioId = enumeracioValors.getEnumeracio().getId();
		List<EnumeracioValors> valors = findEnumeracioValorsAmbEnumeracio(enumeracioId);
		
		// Si no s'ha canviat l'ordre, sortim sense fer res.
		if (ordreAntic == NouOrd) return;
		enumeracioValors.setOrdre(-1);				
				
		if (ordreAntic < NouOrd) {
			//Collections.reverse(camps);
			Collections.sort(
					valors,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((EnumeracioValors)o1).getOrdre()<((EnumeracioValors)o2).getOrdre() ? -1 : ((EnumeracioValors)o1).getOrdre()==((EnumeracioValors)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (EnumeracioValors ce : valors){
				int ordre = ce.getOrdre();
				if (ordre > ordreAntic) {
					if (ordre <= NouOrd) {
						ce.setOrdre(ordre - 1);
						enumeracioValorsDao.saveOrUpdate(ce);
						enumeracioValorsDao.flush();
					}
				}
			}
	
		} else {
			
			Collections.sort(
					valors,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((EnumeracioValors)o1).getOrdre()>((EnumeracioValors)o2).getOrdre() ? -1 : ((EnumeracioValors)o1).getOrdre()==((EnumeracioValors)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (EnumeracioValors ct : valors){
			
				int ordre = ct.getOrdre();
				if (ordre < ordreAntic)  {
					if (ordre >= NouOrd) {
						ct.setOrdre(ordre + 1);
						enumeracioValorsDao.saveOrUpdate(ct);
						enumeracioValorsDao.flush();
					}
				}
			}
		}
		enumeracioValors.setOrdre(NouOrd);
		enumeracioValorsDao.saveOrUpdate(enumeracioValors);
		enumeracioValorsDao.merge(enumeracioValors);
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void goToCampRegistreMembres(Long id, int NouOrd) {
		CampRegistre campRegistre = getCampRegistreById(id);
		int ordreAntic = campRegistre.getOrdre();
		List<CampRegistre> campsRegistre = campRegistre.getRegistre().getRegistreMembres();
		
		try{
		// Si no s'ha canviat l'ordre, sortim sense fer res.
		if (ordreAntic == NouOrd) return;
		campRegistre.setOrdre(-1);		
		
		if (ordreAntic < NouOrd) {
			Collections.sort(
					campsRegistre,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((CampRegistre)o1).getOrdre()<((CampRegistre)o2).getOrdre() ? -1 : ((CampRegistre)o1).getOrdre()==((CampRegistre)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (CampRegistre ca : campsRegistre){
				int ordre = ca.getOrdre();
				if (ordre > ordreAntic) {
					if (ordre <= NouOrd) {
						ca.setOrdre(ordre - 1);
						campRegistreDao.saveOrUpdate(ca);
						campRegistreDao.flush();
						
					}
				}
			}
			
		} else {
			
			Collections.sort(
					campsRegistre,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((CampRegistre)o1).getOrdre()>((CampRegistre)o2).getOrdre() ? -1 : ((CampRegistre)o1).getOrdre()==((CampRegistre)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (CampRegistre ca : campsRegistre){
				int ordre = ca.getOrdre();
					if (ordre < ordreAntic)  {
						if (ordre >= NouOrd) {
							ca.setOrdre(ordre + 1);
							campRegistreDao.saveOrUpdate(ca);
							campRegistreDao.flush();
						}
					}
			}
		}
		
		campRegistre.setOrdre(NouOrd);
		campRegistreDao.saveOrUpdate(campRegistre);
		campRegistreDao.merge(campRegistre);

		}
		catch(Exception e){e.getMessage();}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void goToCampConsLlistat(Long id, int NouOrd) {
		Consulta consulta = getConsultaById(id);
		Long idEntorn  = consulta.getEntorn().getId(); 
		Long idExpedientTipus = consulta.getExpedientTipus().getId();
		int ordreAntic = consulta.getOrdre();
		
		
		// Si no s'ha canviat l'ordre, sortim sense fer res.		
		if (ordreAntic == NouOrd) return;
		consulta.setOrdre(-1);
				
		List<Consulta> codisConsulta =  (List<Consulta>) consultaDao.findAmbEntornIExpedientTipus(idEntorn,idExpedientTipus);
		
		if (ordreAntic < NouOrd) {
			Collections.sort(
					codisConsulta,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((Consulta)o1).getOrdre()<((Consulta)o2).getOrdre() ? -1 : ((Consulta)o1).getOrdre()==((Consulta)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (Consulta ca : codisConsulta){
				int ordre = ca.getOrdre();
				if (ordre > ordreAntic) {
					if (ordre <= NouOrd) {
						ca.setOrdre(ordre - 1);
						consultaDao.saveOrUpdate(ca);
						consultaDao.flush();
					}
				}
			}
	
		} else {
			
			Collections.sort(
					codisConsulta,
					new Comparator() {
					public int compare(Object o1, Object o2) {
						if (o1 == null && o2 == null) return 0;
						return (((Consulta)o1).getOrdre()>((Consulta)o2).getOrdre() ? -1 : ((Consulta)o1).getOrdre()==((Consulta)o2).getOrdre() ? 0 : 1);
					}
					});
			
			for (Consulta ca : codisConsulta){
			
				int ordre = ca.getOrdre();
				if (ordre < ordreAntic)  {
					if (ordre >= NouOrd) {
						ca.setOrdre(ordre + 1);
						consultaDao.saveOrUpdate(ca);
						consultaDao.flush();
					}
				}
			}
		}
		consulta.setOrdre(NouOrd);
		consultaDao.saveOrUpdate(consulta);
		consultaDao.merge(consulta);
	}
	
	
	
	
	
	
	
	 
	public void goToCampProces(Long id, int NouOrd) {
		CampTasca campTasca = getCampTascaById(id);
		int ordreAntic = campTasca.getOrder();
		try{
		// Si no s'ha canviat l'ordre, sortim sense fer res.
		if (ordreAntic == NouOrd) return;
				
		Tasca tasca = campTasca.getTasca();
		List<CampTasca> camps = tasca.getCamps();
				
		if (ordreAntic < NouOrd) {
			for (CampTasca ct : camps){
				int ordre = ct.getOrder();
				if (ordre > ordreAntic) {
					if (ordre <= NouOrd) {
						ct.setOrder(ordre - 1);
						campTascaDao.merge(ct);
						//campTascaDao.saveOrUpdate(ct);
					}
				}
			}
		} else {
			for (CampTasca ct : camps){
				int ordre = ct.getOrder();
				if (ordre < ordreAntic)  {
					if (ordre >= NouOrd) {
						ct.setOrder(ordre + 1);
						//campTascaDao.merge(ct);
					}
				}
			}
		}
		campTasca.setOrder(NouOrd);
		//campTascaDao.saveOrUpdate(campTasca);
		campTascaDao.merge(campTasca);
		//campTascaDao.flush();
		}
		catch(Exception e){e.getMessage();}
	}

	public EnumeracioValors findEnumeracioValorsAmbId(Long enumeracioId, Long id) {
		return enumeracioValorsDao.findAmbEnumeracioIId(enumeracioId, id);
	}	

}
