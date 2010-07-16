/**
 * 
 */
package net.conselldemallorca.helium.model.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.conselldemallorca.helium.integracio.domini.FilaResultat;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmDao;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.model.dao.AccioDao;
import net.conselldemallorca.helium.model.dao.CampAgrupacioDao;
import net.conselldemallorca.helium.model.dao.CampDao;
import net.conselldemallorca.helium.model.dao.CampRegistreDao;
import net.conselldemallorca.helium.model.dao.CampTascaDao;
import net.conselldemallorca.helium.model.dao.ConsultaCampDao;
import net.conselldemallorca.helium.model.dao.ConsultaDao;
import net.conselldemallorca.helium.model.dao.DefinicioProcesDao;
import net.conselldemallorca.helium.model.dao.DocumentDao;
import net.conselldemallorca.helium.model.dao.DocumentTascaDao;
import net.conselldemallorca.helium.model.dao.DominiDao;
import net.conselldemallorca.helium.model.dao.EntornDao;
import net.conselldemallorca.helium.model.dao.EnumeracioDao;
import net.conselldemallorca.helium.model.dao.EstatDao;
import net.conselldemallorca.helium.model.dao.ExpedientTipusDao;
import net.conselldemallorca.helium.model.dao.FirmaTascaDao;
import net.conselldemallorca.helium.model.dao.TascaDao;
import net.conselldemallorca.helium.model.dao.TerminiDao;
import net.conselldemallorca.helium.model.dao.ValidacioDao;
import net.conselldemallorca.helium.model.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.model.exception.DeploymentException;
import net.conselldemallorca.helium.model.exception.DominiException;
import net.conselldemallorca.helium.model.exception.ExportException;
import net.conselldemallorca.helium.model.exportacio.AgrupacioExportacio;
import net.conselldemallorca.helium.model.exportacio.CampExportacio;
import net.conselldemallorca.helium.model.exportacio.CampTascaExportacio;
import net.conselldemallorca.helium.model.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.model.exportacio.DocumentExportacio;
import net.conselldemallorca.helium.model.exportacio.DocumentTascaExportacio;
import net.conselldemallorca.helium.model.exportacio.FirmaTascaExportacio;
import net.conselldemallorca.helium.model.exportacio.RegistreMembreExportacio;
import net.conselldemallorca.helium.model.exportacio.TascaExportacio;
import net.conselldemallorca.helium.model.exportacio.TerminiExportacio;
import net.conselldemallorca.helium.model.exportacio.ValidacioExportacio;
import net.conselldemallorca.helium.model.hibernate.Accio;
import net.conselldemallorca.helium.model.hibernate.Camp;
import net.conselldemallorca.helium.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.model.hibernate.CampTasca;
import net.conselldemallorca.helium.model.hibernate.Consulta;
import net.conselldemallorca.helium.model.hibernate.ConsultaCamp;
import net.conselldemallorca.helium.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.model.hibernate.Document;
import net.conselldemallorca.helium.model.hibernate.DocumentTasca;
import net.conselldemallorca.helium.model.hibernate.Domini;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.Enumeracio;
import net.conselldemallorca.helium.model.hibernate.Estat;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.hibernate.FirmaTasca;
import net.conselldemallorca.helium.model.hibernate.Tasca;
import net.conselldemallorca.helium.model.hibernate.Termini;
import net.conselldemallorca.helium.model.hibernate.TerminiIniciat;
import net.conselldemallorca.helium.model.hibernate.Validacio;
import net.conselldemallorca.helium.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.model.hibernate.ConsultaCamp.TipusConsultaCamp;
import net.conselldemallorca.helium.model.hibernate.Tasca.TipusTasca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Servei per gestionar les tasques de disseny
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Service
public class DissenyService {

	private DefinicioProcesDao definicioProcesDao;
	private EntornDao entornDao;
	private TascaDao tascaDao;
	private CampDao campDao;
	private CampTascaDao campTascaDao;
	private CampRegistreDao campRegistreDao;
	private DocumentDao documentDao;
	private DocumentTascaDao documentTascaDao;
	private FirmaTascaDao firmaTascaDao;
	private ValidacioDao validacioDao;
	private ExpedientTipusDao expedientTipusDao;
	private EnumeracioDao enumeracioDao;
	private TerminiDao terminiDao;
	private EstatDao estatDao;
	private DominiDao dominiDao;
	private CampAgrupacioDao campAgrupacioDao;
	private ConsultaDao consultaDao;
	private ConsultaCampDao consultaCampDao;
	private AccioDao accioDao;

	private DtoConverter dtoConverter;
	private JbpmDao jbpmDao;



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
					throw new DeploymentException("Aquesta definició de procés ja està desplegada a dins el tipus d'expedient \"" + darrera.getExpedientTipus().getNom() + "\"");
				}
				if (darrera.getExpedientTipus() == null && expedientTipusId != null) {
					throw new DeploymentException("Aquesta definició de procés ja està desplegada a dins l'entorn");
				}
			}
			Entorn entorn = entornDao.getById(entornId, false);
			// Crea la nova definició de procés
			DefinicioProces definicioProces = new DefinicioProces(
					dpd.getId(),
					dpd.getKey(),
					dpd.getVersion(),
					entorn);
			if (etiqueta != null)
				definicioProces.setEtiqueta(etiqueta);
			if (expedientTipusId != null)
				definicioProces.setExpedientTipus(expedientTipusDao.getById(expedientTipusId, false));
			definicioProcesDao.saveOrUpdate(definicioProces);
			// Crea les tasques de la definició de procés
			for (String nomTasca: jbpmDao.TaskNamesFromDeployedProcessDefinition(dpd)) {
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
			throw new DeploymentException("L'arxiu no conté cap definició de procés.");
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
				for (Termini termini: definicioProces.getTerminis())
					deleteTermini(termini.getId());
				definicioProcesDao.delete(definicioProcesId);
			} else {
				throw new IllegalArgumentException("No s'ha especificat cap entorn o la definició de procés no correspon amb l'entorn especificat");
			}
		} else {
			if (comprovarExpedientTipus(expedientTipusId, definicioProcesId)) {
				DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
				jbpmDao.esborrarDesplegament(definicioProces.getJbpmId());
				for (Termini termini: definicioProces.getTerminis())
					deleteTermini(termini.getId());
				definicioProcesDao.delete(definicioProcesId);
			} else {
				throw new IllegalArgumentException("No s'ha especificat cap tipus d'expedient o la definició de procés no correspon amb el tipus d'expedient especificat");
			}
		}
	}

	public DefinicioProcesDto getById(
			Long id) {
			DefinicioProces definicioProces = definicioProcesDao.getById(id, false);
			return toDto(definicioProces);
	}

	public DefinicioProcesDto getByIdAmbComprovacio(
			Long entornId,
			Long id) {
		if (comprovarEntorn(entornId, id)) {
			DefinicioProces definicioProces = definicioProcesDao.getById(id, false);
			return toDto(definicioProces);
		} else {
			throw new IllegalArgumentException("No s'ha especificat cap entorn o la definició de procés no correspon amb l'entorn especificat");
		}
	}

	public List<DefinicioProcesDto> findDarreresAmbEntorn(Long entornId) {
		List<DefinicioProcesDto> resposta = new ArrayList<DefinicioProcesDto>();
		for (DefinicioProces definicionProces: definicioProcesDao.findDarreresVersionsAmbEntorn(entornId))
			resposta.add(toDto(definicionProces));
		return resposta;
	}
	public List<DefinicioProcesDto> findDarreresAmbExpedientTipusIGlobalsEntorn(
			Long entornId,
			Long expedientTipusId) {
		List<DefinicioProcesDto> resposta = new ArrayList<DefinicioProcesDto>();
		List<DefinicioProces> dps = definicioProcesDao.findDarreresVersionsAmbEntorn(entornId);
		for (DefinicioProces definicionProces: dps)
			if (definicionProces.getExpedientTipus() == null || definicionProces.getExpedientTipus().getId().equals(expedientTipusId))
				resposta.add(toDto(definicionProces));
		return resposta;
	}

	public List<DefinicioProcesDto> findSubDefinicionsProces(Long id) {
		List<DefinicioProcesDto> resposta = new ArrayList<DefinicioProcesDto>();
		DefinicioProces definicioProces = definicioProcesDao.getById(id, false);
		List<JbpmProcessDefinition> subpds = jbpmDao.getSubProcessDefinitions(definicioProces.getJbpmId());
		for (JbpmProcessDefinition jbpmProcessDefinition: subpds) {
			resposta.add(toDto(definicioProcesDao.findAmbJbpmId(jbpmProcessDefinition.getId())));
		}
		return resposta;
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
		Camp saved = campDao.saveOrUpdate(entity);
		return saved;
	}
	public Camp updateCamp(Camp entity) {
		return campDao.merge(entity);
	}
	public void deleteCamp(Long id) {
		Camp vell = getCampById(id);
		if (vell != null)
			campDao.delete(id);
	}
	public List<Camp> findCampsAmbDefinicioProces(Long definicioProcesId) {
		return campDao.findAmbDefinicioProces(definicioProcesId);
	}
	public Camp findCampAmbDefinicioProcesICodi(Long definicioProcesId, String codi) {
		return campDao.findAmbDefinicioProcesICodi(definicioProcesId, codi);
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
		Document document = documentDao.getById(id, false);
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
		if (vell != null)
			documentDao.delete(id);
	}
	public List<Document> findDocumentsAmbDefinicioProces(Long definicioProcesId) {
		return documentDao.findAmbDefinicioProces(definicioProcesId);
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
			anterior.setOrder(ordreActual);
			documentTascaDao.merge(documentTasca);
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
			seguent.setOrder(ordreActual);
			documentTascaDao.merge(documentTasca);
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
		return expedientTipusDao.merge(entity);
	}
	public void deleteExpedientTipus(Long id) {
		ExpedientTipus vell = getExpedientTipusById(id);
		if (vell != null) {
			for (DefinicioProces definicioProces: vell.getDefinicionsProces()) {
				undeploy(
						definicioProces.getEntorn().getId(),
						vell.getId(),
						definicioProces.getId());
			}
			expedientTipusDao.delete(id);
		}
	}
	public List<ExpedientTipus> findExpedientTipusAmbEntorn(Long entornId) {
		return expedientTipusDao.findAmbEntorn(entornId);
	}
	public ExpedientTipus findExpedientTipusAmbEntornICodi(Long entornId, String codi) {
		return expedientTipusDao.findAmbEntornICodi(entornId, codi);
	}
	public List<ExpedientTipus> findExpedientTipusAmbSistraTramitCodi(String tramitCodi) {
		return expedientTipusDao.findAmbSistraTramitCodi(tramitCodi);
	}
	public DefinicioProcesDto findDarreraDefinicioProcesForExpedientTipus(Long expedientTipusId) {
		ExpedientTipus expedientTipus = getExpedientTipusById(expedientTipusId);
		if (expedientTipus.getJbpmProcessDefinitionKey() != null) {
			DefinicioProces definicioProces = definicioProcesDao.findDarreraVersioAmbEntornIJbpmKey(
					expedientTipus.getEntorn().getId(),
					expedientTipus.getJbpmProcessDefinitionKey());
			if (definicioProces != null)
				return toDto(definicioProces);
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
	public List<Enumeracio> findEnumeracionsAmbEntorn(Long entornId) {
		return enumeracioDao.findAmbEntorn(entornId);
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
	public void deleteEstat(Long id) {
		Estat vell = getEstatById(id);
		if (vell != null) {
			estatDao.delete(id);
			reordenarEstats(vell.getExpedientTipus().getId());
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

	public DefinicioProcesExportacio exportar(Long definicioProcesId) {
		DefinicioProcesExportacio definicioProcesExportacio = new DefinicioProcesExportacio();
		// Afegeix els camps
		List<Camp> camps = campDao.findAmbDefinicioProces(definicioProcesId);
		List<CampExportacio> campsDto = new ArrayList<CampExportacio>();
		for (Camp camp: camps) {
			CampExportacio dto = new CampExportacio(
					camp.getCodi(),
					camp.getTipus(),
					camp.getEtiqueta(),
					camp.getObservacions(),
					camp.getDominiId(),
					camp.getDominiParams(),
					camp.getDominiCampText(),
					camp.getDominiCampValor(),
					camp.isMultiple(),
					camp.isOcult(),
					(camp.getEnumeracio() != null) ? camp.getEnumeracio().getCodi() : null,
					(camp.getDomini() != null) ? camp.getDomini().getCodi() : null,
					(camp.getAgrupacio() != null) ? camp.getAgrupacio().getCodi() : null,
					camp.getJbpmAction());
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
			dto.setCustodiaCodi(document.getCustodiaCodi());
			dto.setContentType(document.getContentType());
			if (document.getCampData() != null)
				dto.setCodiCampData(document.getCampData().getCodi());
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
					termini.isLaborable());
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
		// Afegeix el deploy pel jBPM
		DefinicioProces definicioProces = definicioProcesDao.getById(definicioProcesId, false);
		definicioProcesExportacio.setNomDeploy("export.par");
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);
			byte[] data = new byte[1024];
			for (String resource: jbpmDao.getResourceNames(definicioProces.getJbpmId())) {
				InputStream is = new ByteArrayInputStream(jbpmDao.getResourceBytes(
						definicioProces.getJbpmId(),
						resource));
				zos.putNextEntry(new ZipEntry(resource));
				int count;
				while ((count = is.read(data, 0, 1024)) != -1) {
					zos.write(data, 0, count);
				}
		        zos.closeEntry();
			}
			zos.close();
			definicioProcesExportacio.setContingutDeploy(baos.toByteArray());
		} catch (Exception ex) {
			throw new ExportException("Error generant el contingut del desplegament", ex);
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
		// Propaga les agrupacions
		Map<String, CampAgrupacio> agrupacions = new HashMap<String, CampAgrupacio>();
		for (AgrupacioExportacio agrupacio: exportacio.getAgrupacions()) {
			CampAgrupacio nova = new CampAgrupacio(
					definicioProces,
					agrupacio.getCodi(),
					agrupacio.getNom(),
					agrupacio.getOrdre());
			nova.setDescripcio(agrupacio.getDescripcio());
			campAgrupacioDao.saveOrUpdate(nova);
			agrupacions.put(agrupacio.getCodi(), nova);
		}
		// Propaga els camps
		Map<String, Camp> camps = new HashMap<String, Camp>();
		for (CampExportacio camp: exportacio.getCamps()) {
			Camp nou = new Camp(
					definicioProces,
					camp.getCodi(),
					camp.getTipus(),
					camp.getEtiqueta());
			nou.setObservacions(camp.getObservacions());
			nou.setDominiId(camp.getDominiId());
			nou.setDominiParams(camp.getDominiParams());
			nou.setDominiCampText(camp.getDominiCampText());
			nou.setDominiCampValor(camp.getDominiCampValor());
			nou.setMultiple(camp.isMultiple());
			nou.setOcult(camp.isOcult());
			nou.setJbpmAction(camp.getJbpmAction());
			if (camp.getCodiEnumeracio() != null) {
				Enumeracio enumeracio = enumeracioDao.findAmbEntornICodi(entornId, camp.getCodiEnumeracio());
				if (enumeracio != null)
					nou.setEnumeracio(enumeracio);
				else
					throw new DeploymentException("L'enumeració '" + camp.getCodiEnumeracio() + "' no està definida");
			}
			if (camp.getCodiDomini() != null) {
				Domini domini = dominiDao.findAmbEntornICodi(entornId, camp.getCodiDomini());
				if (domini != null)
					nou.setDomini(domini);
				else
					throw new DeploymentException("El domini '" + camp.getCodiDomini() + "' no està definit");
			}
			if (camp.getAgrupacioCodi() != null)
				nou.setAgrupacio(agrupacions.get(camp.getAgrupacioCodi()));
			// Propaga les validacions del camp
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
			Document nou = new Document(
					definicioProces,
					document.getCodi(),
					document.getNom());
			documentDao.saveOrUpdate(nou);
			nou.setDescripcio(document.getDescripcio());
			nou.setArxiuNom(document.getArxiuNom());
			nou.setArxiuContingut(document.getArxiuContingut());
			nou.setPlantilla(document.isPlantilla());
			nou.setCustodiaCodi(document.getCustodiaCodi());
			nou.setContentType(document.getContentType());
			if (document.getCodiCampData() != null)
				nou.setCampData(camps.get(document.getCodiCampData()));
			documents.put(nou.getCodi(), nou);
		}
		// Propaga els terminis
		for (TerminiExportacio termini: exportacio.getTerminis()) {
			Termini nou = new Termini(
					definicioProces,
					termini.getCodi(),
					termini.getNom(),
					termini.getAnys(),
					termini.getMesos(),
					termini.getDies(),
					termini.isLaborable());
			nou.setDescripcio(termini.getDescripcio());
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
					// Propaga els camps de la tasca
					for (CampTascaExportacio campTasca: vella.getCamps()) {
						CampTasca nouct = new CampTasca(
								camps.get(campTasca.getCampCodi()),
								nova,
								campTasca.isReadFrom(),
								campTasca.isWriteTo(),
								campTasca.isRequired(),
								campTasca.isReadOnly(),
								campTasca.getOrder());
						nova.addCamp(nouct);
						campTascaDao.saveOrUpdate(nouct);
					}
					// Propaga els documents de la tasca
					for (DocumentTascaExportacio documentTasca: vella.getDocuments()) {
						DocumentTasca noudt = new DocumentTasca(
								documents.get(documentTasca.getDocumentCodi()),
								nova,
								documentTasca.isRequired(),
								documentTasca.isReadOnly(),
								documentTasca.getOrder());
						nova.addDocument(noudt);
						documentTascaDao.saveOrUpdate(noudt);
					}
					// Propaga les firmes de la tasca
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
	public Domini findDominiAmbEntornICodi(Long entornId, String codi) {
		return dominiDao.findAmbEntornICodi(entornId, codi);
	}

	public List<FilaResultat> consultaDomini(Long dominiId) {
		return consultaDomini(dominiId, null);
	}
	public List<FilaResultat> consultaDomini(Long dominiId, Map<String, Object> params) {
		try {
			return dominiDao.consultar(dominiId, null, params);
		} catch (Exception ex) {
			throw new DominiException("Error consultant el domini", ex);
		}
	}

	public DefinicioProcesDto findDefinicioProcesAmbProcessInstanceId(String processInstanceId) {
		String processDefinitionId = jbpmDao.getProcessInstance(processInstanceId).getProcessDefinitionId();
		return toDto(definicioProcesDao.findAmbJbpmId(processDefinitionId));
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
			} else {
				return dtoConverter.getResultatConsultaDomini(
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
			reordenarAgrupacions(vell.getDefinicioProces().getId());
		}
	}
	public List<CampAgrupacio> findCampAgrupacioAmbDefinicioProces(Long definicioProcesId) {
		return campAgrupacioDao.findAmbDefinicioProcesOrdenats(definicioProcesId);
	}
	public void goUpCampAgrupacio(Long id) {
		CampAgrupacio campAgrupacio = getCampAgrupacioById(id);
		int ordreActual = campAgrupacio.getOrdre();
		CampAgrupacio anterior = campAgrupacioDao.getAmbOrdre(
				campAgrupacio.getDefinicioProces().getId(),
				ordreActual - 1);
		if (anterior != null) {
			campAgrupacio.setOrdre(-1);
			anterior.setOrdre(ordreActual);
			campAgrupacioDao.merge(campAgrupacio);
			campAgrupacioDao.merge(anterior);
			campAgrupacioDao.flush();
			campAgrupacio.setOrdre(ordreActual - 1);
		}
	}
	public void goDownCampAgrupacio(Long id) {
		CampAgrupacio campAgrupacio = getCampAgrupacioById(id);
		int ordreActual = campAgrupacio.getOrdre();
		CampAgrupacio seguent = campAgrupacioDao.getAmbOrdre(
				campAgrupacio.getDefinicioProces().getId(),
				ordreActual + 1);
		if (seguent != null) {
			campAgrupacio.setOrdre(-1);
			seguent.setOrdre(ordreActual);
			campAgrupacioDao.merge(campAgrupacio);
			campAgrupacioDao.merge(seguent);
			campAgrupacioDao.flush();
			campAgrupacio.setOrdre(ordreActual + 1);
		}
	}

	public Consulta getConsultaById(Long id) {
		return consultaDao.getById(id, false);
	}
	public Consulta createConsulta(Consulta entity) {
		return consultaDao.saveOrUpdate(entity);
	}
	public Consulta updateConsulta(Consulta entity) {
		return consultaDao.merge(entity);
	}
	public void deleteConsulta(Long id) {
		Consulta vell = getConsultaById(id);
		if (vell != null)
			consultaDao.delete(id);
	}
	public List<Consulta> findConsultesAmbEntorn(Long entornId) {
		return consultaDao.findAmbEntorn(entornId);
	}
	public List<Consulta> findConsultesAmbEntornIExpedientTipus(Long entornId, Long expedientTipusId) {
		return consultaDao.findAmbEntornIExpedientTipus(entornId, expedientTipusId);
	}
	public List<Camp> findCampsPerConsulta(Long consultaId) {
		return consultaDao.findCampsFiltre(consultaId);
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
		for (Camp camp: consultaCampDao.findCampsProces(consultaId, defprocJbpmKey)) {
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
		return list;
	}
	public List<ConsultaCamp> findCampsConsulta(Long consultaId, TipusConsultaCamp tipus) {
		return consultaCampDao.findCampsConsulta(consultaId, tipus);
	}
	public List<Camp> findCampsPerCampsConsulta(Long consultaId, TipusConsultaCamp tipus) {
		List<Camp> resposta = new ArrayList<Camp>();
		List<ConsultaCamp> camps = consultaCampDao.findCampsConsulta(consultaId, tipus);
		for (ConsultaCamp camp: camps) {
			DefinicioProces definicioProces = definicioProcesDao.findAmbJbpmKeyIVersio(
					camp.getDefprocJbpmKey(),
					camp.getDefprocVersio());
			resposta.add(campDao.findAmbDefinicioProcesICodi(
					definicioProces.getId(),
					camp.getCampCodi()));
		}
		return resposta;
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
	public Accio findAccioAmbDefinicioProcesICodi(Long definicioProcesId, String codi) {
		return accioDao.findAmbDefinicioProcesICodi(definicioProcesId, codi);
	}
	public List<String> findAccionsJbpm(Long id) {
		DefinicioProces definicioProces = definicioProcesDao.getById(id, false);
		return jbpmDao.listActions(definicioProces.getJbpmId());
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
	public void setEnumeracioDao(EnumeracioDao enumeracioDao) {
		this.enumeracioDao = enumeracioDao;
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
	public void setJbpmDao(JbpmDao jbpmDao) {
		this.jbpmDao = jbpmDao;
	}



	private DefinicioProcesDto toDto(
			DefinicioProces definicioProces) {
		DefinicioProcesDto dto = new DefinicioProcesDto();
		dto.setId(definicioProces.getId());
		dto.setJbpmId(definicioProces.getJbpmId());
		dto.setJbpmKey(definicioProces.getJbpmKey());
		dto.setVersio(definicioProces.getVersio());
		dto.setEtiqueta(definicioProces.getEtiqueta());
		dto.setDataCreacio(definicioProces.getDataCreacio());
		dto.setEntorn(definicioProces.getEntorn());
		JbpmProcessDefinition jpd = jbpmDao.getProcessDefinition(definicioProces.getJbpmId());
		dto.setJbpmName(jpd.getName());
		dto.setHasStartTask(jbpmDao.getStartTaskName(definicioProces.getJbpmId()) != null);
		List<DefinicioProces> mateixaKeyIEntorn = definicioProcesDao.findAmbEntornIJbpmKey(
				definicioProces.getEntorn().getId(),
				definicioProces.getJbpmKey());
		dto.setIdsWithSameKey(new Long[mateixaKeyIEntorn.size()]);
		dto.setIdsMostrarWithSameKey(new String[mateixaKeyIEntorn.size()]);
		dto.setJbpmIdsWithSameKey(new String[mateixaKeyIEntorn.size()]);
		dto.setHasStartTaskWithSameKey(new Boolean[mateixaKeyIEntorn.size()]);
		for (int i = 0; i < mateixaKeyIEntorn.size(); i++) {
			dto.getIdsWithSameKey()[i] = mateixaKeyIEntorn.get(i).getId();
			dto.getIdsMostrarWithSameKey()[i] = mateixaKeyIEntorn.get(i).getIdPerMostrar();
			dto.getJbpmIdsWithSameKey()[i] = mateixaKeyIEntorn.get(i).getJbpmId();
			dto.getHasStartTaskWithSameKey()[i] = new Boolean(
					jbpmDao.getStartTaskName(mateixaKeyIEntorn.get(i).getJbpmId()) != null);
		}
		dto.setExpedientTipus(definicioProces.getExpedientTipus());
		return dto;
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
		for (DocumentTasca documentTasca: documentsTasca)
			documentTasca.setOrder(i++);
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
	private void reordenarEstats(Long expedientTipusId) {
		List<Estat> estats = estatDao.findAmbExpedientTipusOrdenats(expedientTipusId);
		int i = 0;
		for (Estat estat: estats)
			estat.setOrdre(i++);
	}
	private void reordenarAgrupacions(Long definicioProcesId) {
		List<CampAgrupacio> campsAgrupacio = campAgrupacioDao.findAmbDefinicioProcesOrdenats(definicioProcesId);
		int i = 0;
		for (CampAgrupacio campAgrupacio: campsAgrupacio)
			campAgrupacio.setOrdre(i++);
	}
	private void reordenarConsultaCamp(Long consultaId, TipusConsultaCamp tipus) {
		List<ConsultaCamp> consultaCamp = consultaCampDao.findAmbConsultaITipusOrdenats(consultaId, tipus);
		int i = 0;
		for (ConsultaCamp camps: consultaCamp)
			camps.setOrdre(i++);
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
			nou.setEnumeracio(camp.getEnumeracio());
			nou.setJbpmAction(camp.getJbpmAction());
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
			if (document.getCampData() != null)
				nou.setCampData(camps.get(document.getCampData().getCodi()));
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
			terminiDao.saveOrUpdate(nou);
		}
		// Propaga les agrupacions
		for (CampAgrupacio agrupacio: origen.getAgrupacions()) {
			CampAgrupacio nova = new CampAgrupacio(
					desti,
					agrupacio.getCodi(),
					agrupacio.getNom(),
					agrupacio.getOrdre());
			nova.setDescripcio(agrupacio.getDescripcio());
			campAgrupacioDao.saveOrUpdate(nova);
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

	private String getRecursFormPerTasca(String jbpmId, String nomTasca) {
		String prefixRecursBo = "forms/" + nomTasca;
		for (String resourceName: jbpmDao.getResourceNames(jbpmId)) {
			if (resourceName.startsWith(prefixRecursBo))
				return resourceName;
		}
		return null;
	}

}
