/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.ExpedientTipusHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.PermisosHelper;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.v3.core.repository.ExpedientTipusRepository;
import net.conselldemallorca.helium.v3.core.repository.SequenciaAnyRepository;

/**
 * Implementació del servei per gestionar tipus d'expedients
 *
 */
@Service("expedientTipusServiceV3")
public class ExpedientTipusServiceImpl implements ExpedientTipusService {

	@Resource
	private EntornHelper entornHelper;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private SequenciaAnyRepository sequenciaRepository;
	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource(name = "permisosHelperV3") 
	private PermisosHelper permisosHelper;
	@Resource
	private PaginacioHelper paginacioHelper;

	@Override
	@Transactional
	public ExpedientTipusDto create(
			Long entornId,
			String codi, 
			String nom, 
			boolean teTitol, 
			boolean demanaTitol, 
			boolean teNumero,
			boolean demanaNumero, 
			String expressioNumero, 
			boolean reiniciarCadaAny, 
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor, 
			Long sequencia,
			String responsableDefecteCodi, 
			boolean restringirPerGrup, 
			boolean seleccionarAny, 
			boolean ambRetroaccio) {

		logger.debug("Creant nou tipus d'expedient (" +
				"entornId=" + entornId + " + ", " + " +
				"codi=" + codi + ", " + 
				"nom=" + nom + ", " + 
				"teTitol=" + teTitol + ", " + 
				"demanaTitol=" + demanaTitol + ", " + 
				"teNumero=" + teNumero + ", " +
				"demanaNumero=" + demanaNumero + ", " + 
				"expressioNumero=" + expressioNumero + ", " + 
				"reiniciarCadaAny=" + reiniciarCadaAny + ", " + 
				"sequenciesAny=" + sequenciesAny + ", " + 
				"sequenciesValor=" + sequenciesValor + ", " + 
				"sequencia=" + sequencia + ", " +
				"responsableDefecteCodi=" + responsableDefecteCodi + ", " + 
				"restringirPerGrup=" + restringirPerGrup + ", " + 
				"seleccionarAny=" + seleccionarAny + ", " + 
				"ambRetroaccio=" + ambRetroaccio + ")");
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				false,
				false);
		ExpedientTipus tipus = new ExpedientTipus();
		tipus.setEntorn(entorn);
		
		tipus.setCodi(codi);
		tipus.setNom(nom);
		tipus.setTeTitol(teTitol);
		tipus.setDemanaTitol(demanaTitol);
		tipus.setTeNumero(teNumero);
		tipus.setDemanaNumero(demanaNumero);
		tipus.setExpressioNumero(expressioNumero);
		tipus.setReiniciarCadaAny(reiniciarCadaAny);
		if(reiniciarCadaAny) {
			for (int i=0; i<sequenciesAny.size(); i++) {
				SequenciaAny anyEntity = new SequenciaAny(
						tipus, 
						sequenciesAny.get(i), 
						sequenciesValor.get(i));
				tipus.getSequenciaAny().put(anyEntity.getAny(), anyEntity);
			}
		}
		tipus.setSequencia(sequencia);
		tipus.setResponsableDefecteCodi(responsableDefecteCodi);
		tipus.setRestringirPerGrup(restringirPerGrup);
		tipus.setSeleccionarAny(seleccionarAny);
		// Si pot administrar pot marcar la retroacció
		if(potAdministrar(entornId, null)){
			tipus.setAmbRetroaccio(ambRetroaccio);
		} else {
			tipus.setAmbRetroaccio(false);
		}
		// Guarda la nova entitat
		expedientTipusRepository.saveAndFlush(tipus);
		
		// Retorna la informació del tipus d'expedient que s'ha iniciat
		ExpedientTipusDto dto = conversioTipusHelper.convertir(
				tipus,
				ExpedientTipusDto.class);

		return dto;
	}

	@Override
	@Transactional
	public void update(
			Long entornId,
			Long expedientTipusId, 
			String nom, 
			boolean teTitol, 
			boolean demanaTitol, 
			boolean teNumero,
			boolean demanaNumero, 
			String expressioNumero, 
			boolean reiniciarCadaAny, 
			List<Integer> sequenciesAny, 
			List<Long> sequenciesValor, 
			Long sequencia,
			String responsableDefecteCodi, 
			boolean restringirPerGrup, 
			boolean seleccionarAny, 
			boolean ambRetroaccio) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug(
				"Modificar informació del tipus d'expedient (" +
				"entornId=" + entornId + ", " + 
				"expedientTipusId=" + expedientTipusId + ", " + 
				"nom=" + nom + ", " + 
				"teTitol=" + teTitol + ", " + 
				"demanaTitol=" + demanaTitol + ", " + 
				"teNumero=" + teNumero + ", " +
				"demanaNumero=" + demanaNumero + ", " + 
				"expressioNumero=" + expressioNumero + ", " + 
				"reiniciarCadaAny=" + reiniciarCadaAny + ", " + 
				"sequenciesAny=" + sequenciesAny + ", " + 
				"sequenciesValor=" + sequenciesValor + ", " + 
				"sequencia=" + sequencia + ", " +
				"responsableDefecteCodi=" + responsableDefecteCodi + ", " + 
				"restringirPerGrup=" + restringirPerGrup + ", " + 
				"seleccionarAny=" + seleccionarAny + ", " + 
				"ambRetroaccio=" + ambRetroaccio + ")");
		
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				false,
				true,
				false);				
		boolean potDissenyarEntorn =
					permisosHelper.isGrantedAny(
							entornId, 
							Entorn.class,
							new Permission[] {
									ExtendedPermission.ADMINISTRATION,
									ExtendedPermission.DESIGN}, 
							auth);
		ExpedientTipus tipus;
		if (potDissenyarEntorn) {
			// si pot dissenyar l'entorn els pot veure tots
			tipus = expedientTipusRepository.findByEntornAndId(entorn, expedientTipusId);
		} else {
			// Obté comprovant permisos
			tipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					expedientTipusId,
					false,
					false,
					true);			
		}		
		tipus.setNom(nom);
		tipus.setTeTitol(teTitol);
		tipus.setDemanaTitol(demanaTitol);
		tipus.setTeNumero(teNumero);
		tipus.setDemanaNumero(demanaNumero);
		tipus.setExpressioNumero(expressioNumero);
		tipus.setReiniciarCadaAny(reiniciarCadaAny);
		// Buida les seqüències actuals
		while (tipus.getSequenciaAny().size() > 0) {
			SequenciaAny s = tipus.getSequenciaAny().get(tipus.getSequenciaAny().firstKey());			
			tipus.getSequenciaAny().remove(s.getAny());
			sequenciaRepository.delete(s);			
		}
		if(reiniciarCadaAny) {
			for (int i=0; i<sequenciesAny.size(); i++) {
				SequenciaAny sequenciaEntity = new SequenciaAny(
						tipus, 
						sequenciesAny.get(i), 
						sequenciesValor.get(i));
				tipus.getSequenciaAny().put(sequenciaEntity.getAny(), sequenciaEntity);
				sequenciaRepository.save(sequenciaEntity);
			}
		}
		tipus.setSequencia(sequencia);
		tipus.setResponsableDefecteCodi(responsableDefecteCodi);
		tipus.setRestringirPerGrup(restringirPerGrup);
		tipus.setSeleccionarAny(seleccionarAny);
		tipus.setAmbRetroaccio(ambRetroaccio);
		// Si pot administrar pot modificar la retroacció
		if(potAdministrar(entornId, expedientTipusId)){
			tipus.setAmbRetroaccio(ambRetroaccio);
		} 
	}



	@Override
	@Transactional
	public void delete(
			Long entornId,
			Long expedientTipusId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Esborrant el tipus d''expedient (entornId=" + entornId + 
			", expedientTipusId = " + expedientTipusId + ")");

		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				false,
				false);				
		boolean potDissenyarEntorn =
					permisosHelper.isGrantedAny(
							entornId, 
							Entorn.class,
							new Permission[] {
									ExtendedPermission.READ,
									ExtendedPermission.ADMINISTRATION}, 
							auth);
		ExpedientTipus tipus;
		if (potDissenyarEntorn) {
			// si pot dissenyar l'entorn els pot veure tots
			tipus = expedientTipusRepository.findByEntornAndId(entorn, expedientTipusId);
		} else {
			// Obté comprovant permisos
			tipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					expedientTipusId,
					false,
					false,
					true);			
		}		
		expedientTipusRepository.delete(tipus);		
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ExpedientTipusDto> findTipusAmbFiltrePaginat(
			Long entornId,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consulta general de tipus d'expedients paginada (" +
				"filtre=" + filtre + ")");
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				false,
				false);				
		boolean potDissenyarEntorn =
					permisosHelper.isGrantedAny(
							entornId, 
							Entorn.class,
							new Permission[] {
									ExtendedPermission.READ,
									ExtendedPermission.ADMINISTRATION}, 
							auth);
		
		// Obté la llista de tipus d'expedient permesos
		List<Long> tipusPermesosIds;
		if (potDissenyarEntorn) {
			// si pot dissenyar l'entorn els pot veure tots
			tipusPermesosIds = expedientTipusHelper.findIdsAmbEntorn(entorn);
		} else {
			// si no obté els els que pot dissenyar o administrar
			List<ExpedientTipus> expedientsTipus = expedientTipusHelper.findAmbEntorn(entorn);
			tipusPermesosIds = new ArrayList<Long>();
			for (ExpedientTipus tipus : expedientsTipus) {
				if (permisosHelper.isGrantedAny(
						tipus.getId(), 
						ExpedientTipus.class, 
						new Permission[] {
								ExtendedPermission.ADMINISTRATION,
								ExtendedPermission.DESIGN,
								ExtendedPermission.MANAGE}, 
						auth)) {
					tipusPermesosIds.add(tipus.getId());
				}
			}
		}
		// Realitza la consulta 
		return paginacioHelper.toPaginaDto(
				expedientTipusRepository.findByFiltreGeneralPaginat(
						entorn, 
						tipusPermesosIds, 
						filtre == null || "".equals(filtre), 
						filtre, 
						paginacioHelper.toSpringDataPageable(
								paginacioParams)));
	}	

	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findTipusAmbId(
			Long entornId,
			Long expedientTipusId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consultant el tipus d'expedient (entornId=" + entornId +
			", expedientTipusId = " + expedientTipusId + ")");
		
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				false,
				false);				
		boolean potDissenyarEntorn =
					permisosHelper.isGrantedAny(
							entornId, 
							Entorn.class,
							new Permission[] {
									ExtendedPermission.READ,
									ExtendedPermission.ADMINISTRATION}, 
							auth);
		ExpedientTipus tipus;
		if (potDissenyarEntorn) {
			// si pot dissenyar l'entorn els pot veure tots
			tipus = expedientTipusRepository.findByEntornAndId(entorn, expedientTipusId);
		} else {
			// Obté comprovant permisos
			tipus = expedientTipusHelper.getExpedientTipusComprovantPermisos(
					expedientTipusId,
					true,
					false,
					false);			
		}		
		ExpedientTipusDto tipusDto = conversioTipusHelper.convertir(
				tipus,
				ExpedientTipusDto.class);
		return tipusDto;
	}

	@Override
	@Transactional(readOnly = true)
	public ExpedientTipusDto findTipusAmbCodi(
			Long entornId,
			String codi) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Consultant el tipus d'expedient (entornId=" + entornId +
			", codi = " + codi + ")");
		
		Entorn entorn = entornHelper.getEntornComprovantPermisos(
				entornId,
				true,
				false,
				false);				
		ExpedientTipus tipus = expedientTipusRepository.findByEntornAndCodi(entorn, codi);
		if (tipus != null) {
			boolean potDissenyarEntorn =
					permisosHelper.isGrantedAny(
							entornId, 
							Entorn.class,
							new Permission[] {
									ExtendedPermission.READ,
									ExtendedPermission.DESIGN,
									ExtendedPermission.ADMINISTRATION}, 
							auth);
			if (!potDissenyarEntorn) {
				// Comprova els permisos per al tipus d'expedient
				boolean potDissenyarTipus =
						permisosHelper.isGrantedAny(
								tipus.getId(), 
								ExpedientTipus.class,
								new Permission[] {
										ExtendedPermission.READ,
										ExtendedPermission.DESIGN,
										ExtendedPermission.ADMINISTRATION}, 
								auth);
				if (!potDissenyarTipus)
					tipus = null;
			}			
		}
		ExpedientTipusDto tipusDto = conversioTipusHelper.convertir(
				tipus,
				ExpedientTipusDto.class);
		return tipusDto;
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean potEscriure(
			Long entornId, 
			Long expedientTipusId) {
		
		boolean ret = false;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Comprova permisos d'escriptura per al tipus d'expedient (entornId=" + entornId + 
				", expedientTipusId = "	+ expedientTipusId + ")");

		boolean potDissenyarEntorn = 
				permisosHelper.isGrantedAny(
						entornId, 
						Entorn.class,
						new Permission[] { 
								ExtendedPermission.ADMINISTRATION, 
								ExtendedPermission.DESIGN }, 
						auth);
		if (potDissenyarEntorn) {
			ret = true;
		} else {
			ret = permisosHelper.isGrantedAny(
					entornId, 
					Entorn.class,
					new Permission[] { 
							ExtendedPermission.ADMINISTRATION, 
							ExtendedPermission.DESIGN,
							ExtendedPermission.WRITE }, 
					auth);
		}
		return ret;
	}
	
	@Override
	@Transactional(readOnly = true)
	public boolean potAdministrar(
			Long entornId, 
			Long expedientTipusId) {
		
		boolean ret = false;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Comprova permisos d'escriptura per al tipus d'expedient (entornId=" + entornId + 
				", expedientTipusId = "	+ expedientTipusId + ")");

		boolean potDissenyarEntorn = 
				permisosHelper.isGrantedAny(
						entornId, 
						Entorn.class,
						new Permission[] { 
								ExtendedPermission.ADMINISTRATION}, 
						auth);
		if (potDissenyarEntorn) {
			ret = true;
		} else {
			if(expedientTipusId != null) {
				ret = permisosHelper.isGrantedAny(
						entornId, 
						Entorn.class,
						new Permission[] { 
								ExtendedPermission.ADMINISTRATION}, 
						auth);
			}
		}
		return ret;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean potEsborrar(
			Long entornId, 
			Long expedientTipusId) {
		boolean ret = false;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Comprova permisos d'esborrat per al tipus d'expedient (entornId=" + entornId + 
				", expedientTipusId = "	+ expedientTipusId + ")");

		boolean potDissenyarEntorn = 
				permisosHelper.isGrantedAny(
						entornId, 
						Entorn.class,
						new Permission[] { 
								ExtendedPermission.ADMINISTRATION, 
								ExtendedPermission.DESIGN }, 
						auth);
		if (potDissenyarEntorn) {
			ret = true;
		} else {
			ret = permisosHelper.isGrantedAny(
					entornId, 
					Entorn.class,
					new Permission[] { 
							ExtendedPermission.ADMINISTRATION, 
							ExtendedPermission.DESIGN,
							ExtendedPermission.DELETE}, 
					auth);
		}
		return ret;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExpedientServiceImpl.class);
}
