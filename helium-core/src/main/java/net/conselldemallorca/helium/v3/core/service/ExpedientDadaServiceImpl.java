/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.common.JbpmVars;
import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.ExpedientDadaHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.core.helper.HerenciaHelper;
import net.conselldemallorca.helium.core.helper.IndexHelper;
import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.VariableHelper;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.CampAgrupacio;
import net.conselldemallorca.helium.core.model.hibernate.CampRegistre;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Registre;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.v3.core.api.dto.CampAgrupacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampInfoDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaListDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadaValorDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.regles.CampFormProperties;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDadaService;
import net.conselldemallorca.helium.v3.core.regles.ReglaHelper;
import net.conselldemallorca.helium.v3.core.repository.CampAgrupacioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.EstatRepository;
import net.conselldemallorca.helium.v3.core.repository.RegistreRepository;
import net.conselldemallorca.helium.v3.core.repository.TascaRepository;


/**
 * Implementació dels mètodes del servei ExpedientDadaService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientDadaServiceImpl implements ExpedientDadaService {

	@Resource
	private CampRepository campRepository;
	@Resource
	private CampAgrupacioRepository campAgrupacioRepository;
	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private RegistreRepository registreRepository;

	@Resource
	private ExpedientHelper expedientHelper;
	@Resource
	private VariableHelper variableHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Resource
	private ExpedientDadaHelper expedientDadaHelper;
	@Resource
	private TascaRepository tascaRepository;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private EstatRepository estatRepository;

	@Resource
	private ReglaHelper reglaHelper;



	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void create(
			Long expedientId,
			String processInstanceId,
			String varCodi,
			Object varValor) {
		logger.debug("Modificant dada de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"varCodi=" + varCodi + ", " +
				"varValor=" + varValor + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DATA_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_CREAR,
				varCodi);
		expedientDadaHelper.optimitzarValorPerConsultesDominiGuardar(expedient.getTipus(), processInstanceId, varCodi, varValor);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		Registre registre = crearRegistreInstanciaProces(
				expedientId,
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.MODIFICAR);
		registre.setMissatge("Crear variable '" + varCodi + "'");
		if (varValor != null)
			registre.setValorNou(varValor.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void update(
			Long expedientId,
			String processInstanceId,
			String varCodi,
			Object varValor) {
		
		logger.debug("Modificant dada de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"varCodi=" + varCodi + ", " +
				"varValor=" + varValor + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DATA_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		Object valorVell = variableHelper.getVariableJbpmProcesValor(
				processInstanceId,
				varCodi);
//		jbpmHelper.deleteProcessInstanceVariable(processInstanceId, varCodi);
		// Esborra la descripció per variables que mantenen el valor de la consulta
		Camp camp;
		InstanciaProcesDto instanciaProces = expedientHelper.getInstanciaProcesById(processInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findOne(instanciaProces.getDefinicioProces().getId());
		if (expedient.getTipus().isAmbInfoPropia()) {
			// obtenir el camp amb expedient tipus codi i codi de la variable
			camp = campRepository.findByExpedientTipusAndCodi(expedient.getTipus().getId(), varCodi, expedient.getTipus().getExpedientTipusPare() != null);
		}else {
			camp = campRepository.findByDefinicioProcesAndCodi(definicioProces, varCodi);
		}
		if (camp != null && camp.isDominiCacheText())
			jbpmHelper.deleteProcessInstanceVariable(processInstanceId, JbpmVars.PREFIX_VAR_DESCRIPCIO + varCodi);
		
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_MODIFICAR,
				varCodi);
		expedientDadaHelper.optimitzarValorPerConsultesDominiGuardar(
				expedient.getTipus(),
				processInstanceId,
				varCodi,
				varValor);
		indexHelper.expedientIndexLuceneUpdate(processInstanceId);
		Registre registre = crearRegistreInstanciaProces(
				expedientId,
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.MODIFICAR);
		registre.setMissatge("Modificar variable '" + varCodi + "'");
		if (valorVell != null)
			registre.setValorVell(valorVell.toString());
		if (varValor != null)
			registre.setValorNou(varValor.toString());
		registre.setProcessInstanceId(processInstanceId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(
			Long expedientId,
			String processInstanceId,
			String varCodi) {
		logger.debug("Esborrant dada de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"varCodi=" + varCodi + ")");
		Expedient e = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DATA_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		expedientLoggerHelper.afegirLogExpedientPerProces(
				processInstanceId,
				ExpedientLogAccioTipus.PROCES_VARIABLE_ESBORRAR,
				varCodi);
		jbpmHelper.deleteProcessInstanceVariable(processInstanceId, varCodi);
		// Esborra la descripció per variables que mantenen el valor de la consulta
		Camp camp;
		InstanciaProcesDto instanciaProces = expedientHelper.getInstanciaProcesById(processInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findOne(instanciaProces.getDefinicioProces().getId());
		if (e.getTipus().isAmbInfoPropia()) {
			// obtenir el camp amb expedient tipus codi i codi de la variable
			camp = campRepository.findByExpedientTipusAndCodi(e.getTipus().getId(), varCodi, e.getTipus().getExpedientTipusPare() != null);
		}else {
			camp = campRepository.findByDefinicioProcesAndCodi(definicioProces, varCodi);
		}			
		if (camp != null && camp.isDominiCacheText())
			jbpmHelper.deleteProcessInstanceVariable(processInstanceId, JbpmVars.PREFIX_VAR_DESCRIPCIO + varCodi);
		
		if (e.getTipus().isAmbInfoPropia()) {
			indexHelper.expedientIndexLuceneDelete(processInstanceId, varCodi);
		} else {
			indexHelper.expedientIndexLuceneDelete(processInstanceId, definicioProces.getJbpmKey() + "." + varCodi);
		}
		
		Registre registre = crearRegistreInstanciaProces(
				expedientId,
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.MODIFICAR);
		registre.setMissatge("Esborrar variable '" + varCodi + "'");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientDadaDto findOnePerInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String varCodi) {
		logger.debug("Consultant dada de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"varCodi=" + varCodi + ")");
		return variableHelper.getDadaPerInstanciaProces(
				processInstanceId,
				varCodi,
				true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public ExpedientDadaDto getDadaBuida(long campId) {
		logger.debug("Consultant dada buida per un camp (campId=" + campId + ")");
		return variableHelper.getDadaBuida(campId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ExpedientDadaDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		logger.debug("Consultant les variables de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ")");
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		if (processInstanceId == null) {
			return variableHelper.findDadesPerInstanciaProces(
					expedient.getProcessInstanceId(), 
					true);
		} else {
			expedientHelper.comprovarInstanciaProces(
					expedient,
					processInstanceId);
			return variableHelper.findDadesPerInstanciaProces(
					processInstanceId,
					true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CampAgrupacioDto> agrupacionsFindAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		logger.debug("Consulta de les agrupacions de dades de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ")");
		List<CampAgrupacio> agrupacions = new ArrayList<CampAgrupacio>();
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				true,
				false,
				false,
				false);
		
		ExpedientTipus expedientTipus = expedient.getTipus();
		
		boolean ambHerencia = HerenciaHelper.ambHerencia(expedientTipus);
		Set<Long> agrupacionsHeretadesIds = new HashSet<Long>();
		Set<String> sobreescritsCodis = new HashSet<String>();

		if (expedientTipus.isAmbInfoPropia()) {
			agrupacions = campAgrupacioRepository.findAmbExpedientTipusOrdenats(expedientTipus.getId(), ambHerencia);
			if (ambHerencia) {
				Long expedientTipusId = expedientTipus.getId();
				for(CampAgrupacio a : agrupacions)
					if(!expedientTipusId.equals(a.getExpedientTipus().getId()))
						agrupacionsHeretadesIds.add(a.getId());
				// Llistat d'elements sobreescrits
				for (CampAgrupacio a : campAgrupacioRepository.findSobreescrits(expedientTipusId)) 
					sobreescritsCodis.add(a.getCodi());
			}	
		} else {
			DefinicioProces definicioProces;
			if (processInstanceId == null) {
				definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
						expedient.getProcessInstanceId());
			} else {
				expedientHelper.comprovarInstanciaProces(
						expedient,
						processInstanceId);
				definicioProces = expedientHelper.findDefinicioProcesByProcessInstanceId(
						processInstanceId);
			}
			agrupacions = campAgrupacioRepository.findAmbDefinicioProcesOrdenats(definicioProces.getId());
		}
		List<CampAgrupacioDto> agrupacionsDto = conversioTipusHelper.convertirList(
				agrupacions, 
				CampAgrupacioDto.class);

		if (ambHerencia) {
			// Completa l'informació del dto
			for(CampAgrupacioDto dto : agrupacionsDto) {
				// Sobreescriu
				if (sobreescritsCodis.contains(dto.getCodi()))
					dto.setSobreescriu(true);
				// Heretat
				if(agrupacionsHeretadesIds.contains(dto.getId()))
					dto.setHeretat(true);
			}
		}		
		return agrupacionsDto;
	}

    @Override
	@Transactional(readOnly = true)
    public List<DadaListDto> findDadesExpedient(Long expedientId, Long estatId, Boolean totes, Boolean ambOcults, Boolean noPendents, PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant les dades de l'expedient (expedientId=" + expedientId + ")");
		Expedient expedient = null;

		if (totes) {
			// Comprovam que l'usuari té permisos d'administrador sobre l'expedient
			try {
				expedientHelper.getExpedientComprovantPermisos(expedientId, false, false, false, true);
			} catch (PermisDenegatException pde) {
				// Si no es tenen permisos d'administrador no es mostraran totes les dades
				totes = false;
			}
		}
		if (expedient == null) {
			expedient = expedientHelper.getExpedientComprovantPermisos(expedientId, true, false, false, false);
		}

		String processInstanceId = expedient.getProcessInstanceId();
		String filtre = paginacioParams.getFiltre();
		boolean filtrar = !StringUtils.isEmpty(filtre);

		List<ExpedientDadaDto> dadesExpedient = variableHelper.findDadesPerInstanciaProces(processInstanceId, true);
		List<Camp> camps = campRepository.findByExpedientTipusOrderByCodiAsc(expedient.getTipus());
		
		Estat estat = estatId != null? estatRepository.findOne(estatId) : expedient.getEstat();
		
		Map<String, CampFormProperties> campsFormProperties = reglaHelper.getCampFormProperties(expedient.getTipus(), estat);

		List<DadaListDto> dades = new ArrayList<DadaListDto>();
		List<String> dadesCodis = new ArrayList<String>();

		for (Camp camp: camps) {
			dadesCodis.add(camp.getCodi());
			if (camp.isOcult() && !ambOcults)
				continue;
			if (filtrar && !camp.getEtiqueta().contains(filtre))
				continue;
			CampFormProperties campFormProperties = campsFormProperties.get(camp.getCodi());
			if (!totes && campFormProperties != null && !campFormProperties.isVisible())
				continue;

			ExpedientDadaDto dadaExp = getDadaExpedient(dadesExpedient, camp.getId());
			if (dadaExp == null) {
				if (!noPendents)
					dades.add(DadaListDto.builder()
							.campId(camp.getId())
							.campCodi(camp.getCodi())
							.tipus(CampTipusDto.valueOf(camp.getTipus().name()))
							.nom(camp.getEtiqueta())
							.processInstanceId(processInstanceId)
							.expedientId(expedientId)
							.agrupacioOrdre(camp.getAgrupacio() != null ? camp.getAgrupacio().getOrdre() : Integer.MAX_VALUE - 1)
							.agrupacioNom(camp.getAgrupacio() != null ? camp.getAgrupacio().getNom() : "Sense agrupacio")
							.ocult(camp.isOcult())
							.ordre(camp.getOrdre())
							.visible(campFormProperties != null ? campFormProperties.isVisible() : true)
							.editable(campFormProperties != null ? campFormProperties.isEditable() : true)
							.obligatori(campFormProperties != null ? campFormProperties.isObligatori() : false)
							.obligatoriEntrada(campFormProperties != null ? campFormProperties.isObligatoriEntrada() : false)
							.valor(DadaValorDto.builder().build())
							.build());
			} else {
				dades.add(toDadaListDto(camp, dadaExp, campFormProperties, processInstanceId, expedientId));
			}
		}

		for (ExpedientDadaDto dadaExp: dadesExpedient) {
			if (dadesCodis.contains(dadaExp.getVarCodi()))
				continue;

			if (filtrar && !dadaExp.getVarCodi().contains(filtre))
				continue;

			DadaListDto dada = DadaListDto.builder()
					.id(dadaExp.getVarCodi())
					.nom(dadaExp.getVarCodi())
					.valor(DadaValorDto.builder().valorSimple(dadaExp.getText()).build())
					.campCodi(dadaExp.getVarCodi())
					.tipus(CampTipusDto.STRING)
					.agrupacioOrdre(Integer.MAX_VALUE)
					.agrupacioNom("Dades adjuntes")
					.processInstanceId(processInstanceId)
					.expedientId(expedientId)
					.ordre(dadaExp.getOrdre())
					.build();
			dades.add(dada);
		}

		List<PaginacioParamsDto.OrdreDto> ordres = paginacioParams.getOrdres();
		PaginacioParamsDto.OrdreDto ordreDto = null;

		if (ordres != null && !ordres.isEmpty()) {
			ordreDto = ordres.get(0);
		}

		final String ordre = ordreDto != null ? ordreDto.getCamp() : "nom";
		final PaginacioParamsDto.OrdreDireccioDto direccio = ordreDto != null ? ordreDto.getDireccio() : PaginacioParamsDto.OrdreDireccioDto.ASCENDENT;
		Collections.sort(dades, new Comparator<DadaListDto>() {
			@Override
			public int compare(DadaListDto o1, DadaListDto o2) {
				int result = compareDadaByCamp(o1, o2, ordre);
				return PaginacioParamsDto.OrdreDireccioDto.ASCENDENT.equals(direccio) ? result : -result;
			}
		});

		return dades;
	}

	private static DadaListDto toDadaListDto(Camp camp, ExpedientDadaDto dadaExp, CampFormProperties campFormProperties, String processInstanceId, Long expedientId) {
		return DadaListDto.builder()
				.id(dadaExp.getVarCodi())
				.nom(camp.getEtiqueta())
				.valor(getDadaValor(camp, dadaExp))
				.campId(camp.getId())
				.campCodi(camp.getCodi())
				.tipus(CampTipusDto.valueOf(camp.getTipus().name()))
				.registre(dadaExp.isCampTipusRegistre())
				.multiple(camp.isMultiple())
				.ocult(camp.isOcult())
				.jbpmAction(camp.getJbpmAction())
				.observacions(camp.getObservacions())
				.error(dadaExp.getError())
				.agrupacioOrdre(camp.getAgrupacio() != null ? camp.getAgrupacio().getOrdre() : Integer.MAX_VALUE - 1)
				.agrupacioNom(camp.getAgrupacio() != null ? camp.getAgrupacio().getNom() : "Sense agrupacio")
				.processInstanceId(processInstanceId)
				.expedientId(expedientId)
				.visible(campFormProperties != null ? campFormProperties.isVisible() : true)
				.editable(campFormProperties != null ? campFormProperties.isEditable() : true)
				.obligatori(campFormProperties != null ? campFormProperties.isObligatori() : false)
				.obligatoriEntrada(campFormProperties != null ? campFormProperties.isObligatoriEntrada() : false)
				.ordre(camp.getOrdre())
				.build();
	}

	private static DadaValorDto getDadaValor(Camp camp, ExpedientDadaDto dadaExp) {
		int files = 0;
		int columnes = 0;
		Map<String, Boolean> valorHeader = null;
		List<List<String>> valorBody = null;
		String valorSimple = null;
		List<String> valorMultiple = null;

		if (dadaExp.isCampTipusRegistre()) {
			// Capçalera
			valorHeader = new LinkedHashMap<String, Boolean>();
			for (CampRegistre fila : camp.getRegistreMembres()) {
				if (fila.isLlistar()) {
					valorHeader.put(fila.getMembre().getEtiqueta(), fila.isObligatori());
					columnes++;
				}
			}
			// Dades
			List<ExpedientDadaDto> dadesRegistrePerTaula = dadaExp.getDadesRegistrePerTaula();
			if (dadesRegistrePerTaula != null) {
				files = dadesRegistrePerTaula.size();
				valorBody = new ArrayList<List<String>>();
				for (ExpedientDadaDto fila : dadesRegistrePerTaula) {
					List<String> valorFila = new ArrayList<String>();
					for(ExpedientDadaDto cela: fila.getRegistreDades()) {
						if (cela.isLlistar()) {
							valorFila.add(cela.getTextMultiple());
						}
					}
					valorBody.add(valorFila);
				}
			}

		} else if (dadaExp.isCampMultiple()) {
			valorMultiple = new ArrayList<String>();
			for (ExpedientDadaDto dadaMultiple: dadaExp.getMultipleDades()) {
				valorMultiple.add(dadaMultiple.getText());
			}
		} else {
			valorSimple = dadaExp.getText();
		}
		return DadaValorDto.builder()
				.registre(dadaExp.isCampTipusRegistre())
				.multiple(dadaExp.isCampMultiple())
				.files(files)
				.columnes(columnes)
				.valorHeader(valorHeader)
				.valorBody(valorBody)
				.valorSimple(valorSimple)
				.valorMultiple(valorMultiple)
				.build();
	}

	private int compareDadaByCamp(DadaListDto o1, DadaListDto o2, String camp) {
		int result = Integer.compare(o1.getAgrupacioOrdre(),  o2.getAgrupacioOrdre());
		if (result == 0) {
			result = o1.getAgrupacioNom() == null ? (o2.getAgrupacioNom() == null ? 0 : -1) : (o2.getAgrupacioNom() == null ? 1 : o1.getAgrupacioNom().compareTo(o2.getAgrupacioNom()));			
		}
		if (result != 0)
			return result;
		if ("tipus".equals(camp)) {
			result = (o1.getTipus() == null && o2.getTipus() == null) ? 0 : o1.getTipus() == null ? -1 : o2.getTipus() == null ? 1 : o1.getTipus().name().toUpperCase().compareTo(o2.getTipus().name().toUpperCase());
		}
		
		if ("ordre".equals(camp)) {
			result = (o1.getOrdre() == null && o2.getOrdre() == null)? 0 : o1.getOrdre() == null ? -1 : o2.getOrdre() == null ? 1 : o1.getOrdre().compareTo(o2.getOrdre());
		}
		
		if ("codi".equals(camp) || result == 0) {
			result = (o1.getCampCodi() == null && o2.getCampCodi() == null) ? 0 : o1.getCampCodi() == null ? -1 : o2.getCampCodi() == null ? 1 : o1.getCampCodi().toUpperCase().compareTo(o2.getCampCodi().toUpperCase());
		}
		return result;
	}

	private ExpedientDadaDto getDadaExpedient(List<ExpedientDadaDto> dadesExpedient, Long campId) {
		for(ExpedientDadaDto dadaExpedient: dadesExpedient) {
			if (dadaExpedient.getCampId() != null && dadaExpedient.getCampId().equals(campId))
				return dadaExpedient;
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CampInfoDto> getCampsNoUtilitzatsPerEstats(Long expedientId) {
		logger.debug("Consultant els camps no utilitzats de l'expedient (expedientId=" + expedientId + ")");
		List<CampInfoDto> campsNoUtilitzats = new ArrayList<CampInfoDto>();
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(expedientId, true, false, false, false);
		List<Camp> camps = campRepository.findByExpedientTipusOrderByCodiAsc(expedient.getTipus());
		List<ExpedientDadaDto> dadesExpedient = variableHelper.findDadesPerInstanciaProces(expedient.getProcessInstanceId(), true);
		Map<String, CampFormProperties> campsFormProperties = reglaHelper.getCampFormProperties(expedient.getTipus(), expedient.getEstat());

		if (dadesExpedient != null && !dadesExpedient.isEmpty()) {
			Collections.sort(
					dadesExpedient,
					new Comparator<ExpedientDadaDto>() {
						public int compare(ExpedientDadaDto d1, ExpedientDadaDto d2) {
							return d1.getVarCodi().compareToIgnoreCase(d2.getVarCodi());
						}
					}
			);

			int i = 0;
			for(Camp camp: camps) {
				while (i < (dadesExpedient.size() - 1) && camp.getCodi().compareToIgnoreCase(dadesExpedient.get(i).getVarCodi()) > 0)
					i++;
				if (dadesExpedient.isEmpty() || !camp.getCodi().equals(dadesExpedient.get(i).getVarCodi())) {
					CampFormProperties campFormProperties = campsFormProperties.get(camp.getCodi());
					campsNoUtilitzats.add(toCampInfoDto(camp, campFormProperties));
				} else if (i < (dadesExpedient.size() - 1)){
					i++;
				}
			}
		} else {
			for(Camp camp: camps) {
				CampFormProperties campFormProperties = campsFormProperties.get(camp.getCodi());
				campsNoUtilitzats.add(toCampInfoDto(camp, campFormProperties));
			}
		}
		return campsNoUtilitzats;
	}

	@Override
	public DadaListDto getDadaList(Long expedientId, String procesId, String varCodi) {
		Expedient expedient = expedientHelper.getExpedientComprovantPermisos(expedientId, true, false, false, false);
		Camp camp = campRepository.findByExpedientTipusAndCodi(expedient.getTipus().getId(), varCodi, false);
		ExpedientDadaDto dadaExp = variableHelper.getDadaPerInstanciaProces(procesId, varCodi, true);

		if (camp != null) {
			return toDadaListDto(camp, dadaExp, null, procesId, expedientId);
		} else {
			return DadaListDto.builder()
					.id(dadaExp.getVarCodi())
					.nom(dadaExp.getVarCodi())
					.valor(DadaValorDto.builder().valorSimple(dadaExp.getText()).build())
					.campCodi(dadaExp.getVarCodi())
					.tipus(CampTipusDto.STRING)
					.agrupacioOrdre(Integer.MAX_VALUE)
					.agrupacioNom("Dades adjuntes")
					.processInstanceId(procesId)
					.expedientId(expedientId)
					.build();
		}
	}

	private CampInfoDto toCampInfoDto(Camp camp, CampFormProperties campFormProperties) {
		return CampInfoDto.builder()
				.codi(camp.getCodi())
				.etiqueta(camp.getEtiqueta())
				.visible(campFormProperties != null ? campFormProperties.isVisible() : true)
				.editable(campFormProperties != null ? campFormProperties.isEditable() : true)
				.obligatori(campFormProperties != null ? campFormProperties.isObligatori() : false)
				.build();
	}

	/*********************/

	private Registre crearRegistreInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String responsableCodi,
			Registre.Accio accio) {
		Registre registre = new Registre(
				new Date(),
				expedientId,
				responsableCodi,
				accio,
				Registre.Entitat.INSTANCIA_PROCES,
				processInstanceId);
		return registreRepository.save(registre);
	}

	private static final Logger logger = LoggerFactory.getLogger(ExpedientDadaServiceImpl.class);

}
