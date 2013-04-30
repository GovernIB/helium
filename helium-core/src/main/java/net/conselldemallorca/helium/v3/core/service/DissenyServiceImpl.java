/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Termini;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessDefinition;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDissenyDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.exception.DefinicioProcesNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.ExpedientTipusNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.helper.PermisosHelper;
import net.conselldemallorca.helium.v3.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.DocumentRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.TerminiRepository;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servei per gestionar les tasques de disseny.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("dissenyServiceV3")
public class DissenyServiceImpl implements DissenyService {

	@Resource
	private EntornRepository entornRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private EstatRepository estatRepository;
	@Resource
	private DocumentRepository documentRepository;
	@Resource
	private TerminiRepository terminiRepository;

	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PermisosHelper permisosHelper;


	public ExpedientTipusDto findExpedientTipusAmbEntornICodi(
			Long entornId,
			String expedientTipusCodi) throws EntornNotFoundException {
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		ExpedientTipus expedientTipus = expedientTipusRepository.findByEntornAndCodi(
				entorn,
				expedientTipusCodi);
		return conversioTipusHelper.convertir(
				expedientTipus,
				ExpedientTipusDto.class);
	}

	public List<EstatDto> findEstatByExpedientTipus(
			Long expedientTipusId) throws ExpedientTipusNotFoundException {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(
				expedientTipusId);
		if (expedientTipus == null)
			throw new ExpedientTipusNotFoundException();
		return conversioTipusHelper.convertirLlista(
				estatRepository.findByExpedientTipus(expedientTipus),
				EstatDto.class);
	}
	
	public EstatDto findEstatAmbExpedientTipusICodi(
			Long expedientTipusId,
			String estatCodi) throws ExpedientTipusNotFoundException {
		ExpedientTipus expedientTipus = expedientTipusRepository.findOne(
				expedientTipusId);
		if (expedientTipus == null)
			throw new ExpedientTipusNotFoundException();
		Estat estat = estatRepository.findByExpedientTipusAndCodi(
				expedientTipus,
				estatCodi);
		return conversioTipusHelper.convertir(
				estat,
				EstatDto.class);
	}

	public DocumentDissenyDto getDocumentDisseny(
			Long definicioProcesId,
			String documentCodi) throws DefinicioProcesNotFoundException {
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
		if (definicioProces == null)
			throw new DefinicioProcesNotFoundException();
		Document document = documentRepository.findByDefinicioProcesAndCodi(
				definicioProces,
				documentCodi);
		return conversioTipusHelper.convertir(
				document,
				DocumentDissenyDto.class);
	}

	public DefinicioProcesDto getDefinicioProcesAmbJbpmKeyIVersio(
			String jbpmKey,
			int versio) {
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmKeyAndVersio(
				jbpmKey,
				versio);
		return conversioTipusHelper.convertir(
				definicioProces,
				DefinicioProcesDto.class);
	}

	public DefinicioProcesDto getDarreraVersioAmbEntornIJbpmKey(
			Long entornId,
			String jbpmKey) throws EntornNotFoundException {
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		DefinicioProces definicioProces = definicioProcesRepository.findDarreraVersioByEntornAndJbpmKey(
				entorn,
				jbpmKey);
		return conversioTipusHelper.convertir(
				definicioProces,
				DefinicioProcesDto.class);
	}

	public DefinicioProcesDto getDefinicioProcesPerProcessInstanceId(
			String processInstanceId) throws DefinicioProcesNotFoundException {
		JbpmProcessDefinition jpd = jbpmHelper.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(jpd.getId());
		if (definicioProces == null)
			throw new DefinicioProcesNotFoundException();
		return conversioTipusHelper.convertir(
				definicioProces,
				DefinicioProcesDto.class);
	}

	public TerminiDto findTerminiAmbDefinicioProcesICodi(
			Long definicioProcesId,
			String codi) throws DefinicioProcesNotFoundException {
		DefinicioProces definicioProces = definicioProcesRepository.findOne(definicioProcesId);
		if (definicioProces == null)
			throw new DefinicioProcesNotFoundException();
		Termini termini = terminiRepository.findByDefinicioProcesAndCodi(
				definicioProces,
				codi);
		return conversioTipusHelper.convertir(
				termini,
				TerminiDto.class);
	}

	public List<ExpedientTipusDto> findExpedientTipusAmbPermisReadUsuariActual(
			Long entornId) throws EntornNotFoundException {
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION});
	}
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisDissenyUsuariActual(
			Long entornId) throws EntornNotFoundException {
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.DESIGN,
						ExtendedPermission.ADMINISTRATION});
	}
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisGestioUsuariActual(
			Long entornId) throws EntornNotFoundException {
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.MANAGE,
						ExtendedPermission.ADMINISTRATION});
	}
	public List<ExpedientTipusDto> findExpedientTipusAmbPermisCrearUsuariActual(
			Long entornId) throws EntornNotFoundException {
		return findExpedientTipusAmbPermisosUsuariActual(
				entornId,
				new Permission[] {
						ExtendedPermission.CREATE,
						ExtendedPermission.ADMINISTRATION});
	}



	private List<ExpedientTipusDto> findExpedientTipusAmbPermisosUsuariActual(
			Long entornId,
			Permission[] permisos) {
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		List<ExpedientTipus> expedientsTipus = expedientTipusRepository.findByEntorn(entorn);
		permisosHelper.filterGrantedAny(
				expedientsTipus,
				new ObjectIdentifierExtractor<ExpedientTipus>() {
					public Long getObjectIdentifier(ExpedientTipus expedientTipus) {
						return expedientTipus.getId();
					}
				},
				ExpedientTipus.class,
				permisos,
				SecurityContextHolder.getContext().getAuthentication());
		return conversioTipusHelper.convertirLlista(
				expedientsTipus,
				ExpedientTipusDto.class);
	}

}
