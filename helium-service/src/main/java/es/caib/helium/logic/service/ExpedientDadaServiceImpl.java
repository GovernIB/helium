/**
 * 
 */
package es.caib.helium.logic.service;

import es.caib.helium.client.dada.dades.DadaClient;
import es.caib.helium.client.engine.helper.DadaHelper;
import es.caib.helium.client.engine.model.CampRest;
import es.caib.helium.client.engine.model.WProcessDefinition;
import es.caib.helium.logic.helper.ConversioTipusServiceHelper;
import es.caib.helium.logic.helper.ExpedientHelper;
import es.caib.helium.logic.helper.HerenciaHelper;
import es.caib.helium.logic.helper.IndexHelper;
import es.caib.helium.logic.helper.VariableHelper;
import es.caib.helium.logic.intf.WorkflowEngineApi;
import es.caib.helium.logic.intf.WorkflowRetroaccioApi;
import es.caib.helium.logic.intf.dto.CampAgrupacioDto;
import es.caib.helium.logic.intf.dto.ExpedientDadaDto;
import es.caib.helium.logic.intf.service.ExpedientDadaService;
import es.caib.helium.logic.intf.util.Constants;
import es.caib.helium.logic.security.ExtendedPermission;
import es.caib.helium.persist.entity.Camp;
import es.caib.helium.persist.entity.Camp.TipusCamp;
import es.caib.helium.persist.entity.CampAgrupacio;
import es.caib.helium.persist.entity.DefinicioProces;
import es.caib.helium.persist.entity.Expedient;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.entity.Registre;
import es.caib.helium.persist.repository.CampAgrupacioRepository;
import es.caib.helium.persist.repository.CampRepository;
import es.caib.helium.persist.repository.DefinicioProcesRepository;
import es.caib.helium.persist.repository.RegistreRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Implementació dels mètodes del servei ExpedientDadaService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
@Slf4j
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
	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private WorkflowRetroaccioApi workflowRetroaccioApi;
	@Resource
	private IndexHelper indexHelper;
	@Resource
	private ConversioTipusServiceHelper conversioTipusServiceHelper;
	@Resource
	private DadaClient dadaClient;

//	DateFormat dataFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS ");
//	DateFormat dataFormatLectura = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

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
		workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
				processInstanceId,
				WorkflowRetroaccioApi.ExpedientRetroaccioTipus.PROCES_VARIABLE_CREAR,
				varCodi);
		var camp = optimitzarValorPerConsultesDominiGuardar(expedient.getTipus(), processInstanceId, varCodi, varValor);
		Registre registre = crearRegistreInstanciaProces(
				expedientId,
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.MODIFICAR);
		registre.setMissatge("Crear variable '" + varCodi + "'");
		if (varValor != null) {
			registre.setValorNou(varValor.toString());
		}

		try {
//			var dades = prepararDades(varCodi, varValor, camp);
			var dades = DadaHelper.prepararDades(
					varCodi,
					varValor,
					conversioTipusServiceHelper.convertir(camp, CampRest.class));
			dadaClient.postDadesByExpedientId(expedientId, processInstanceId, dades);
		} catch (Exception ex) {
			log.error("", ex);
		}
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
			Object varValor) throws Exception {
		
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

		var camp = optimitzarValorPerConsultesDominiGuardar(expedient.getTipus(), processInstanceId, varCodi, varValor);
		// TODO: Moure a DadaHelper
//		var dada = prepararDadesUpdate(varCodi, varValor, camp);
		var dada = DadaHelper.prepararDades(
				varCodi,
				varValor,
				conversioTipusServiceHelper.convertir(camp, CampRest.class)).get(0);
		dadaClient.putDadaByExpedientIdProcesIdAndCodi(expedientId, processInstanceId, varCodi, dada);
		workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(
				processInstanceId,
				WorkflowRetroaccioApi.ExpedientRetroaccioTipus.PROCES_VARIABLE_MODIFICAR,
				varCodi);
		optimitzarValorPerConsultesDominiGuardar(
				expedient.getTipus(),
				processInstanceId,
				varCodi,
				varValor);
		Registre registre = crearRegistreInstanciaProces(
				expedientId,
				processInstanceId,
				SecurityContextHolder.getContext().getAuthentication().getName(),
				Registre.Accio.MODIFICAR);
		registre.setMissatge("Modificar variable '" + varCodi + "'");

		if (valorVell != null) {
			registre.setValorVell(valorVell.toString());
		}
		if (varValor != null) {
			registre.setValorNou(varValor.toString());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void delete(Long expedientId, String processInstanceId, String varCodi) {

		logger.debug("Esborrant dada de la instància de procés (" +
				"expedientId=" + expedientId + ", " +
				"processInstanceId=" + processInstanceId + ", " +
				"varCodi=" + varCodi + ")");
		Expedient e = expedientHelper.getExpedientComprovantPermisos(
				expedientId,
				new Permission[] {
						ExtendedPermission.DATA_MANAGE,
						ExtendedPermission.ADMINISTRATION});
		workflowRetroaccioApi.afegirInformacioRetroaccioPerProces(processInstanceId,
				WorkflowRetroaccioApi.ExpedientRetroaccioTipus.PROCES_VARIABLE_ESBORRAR,
				varCodi);

		dadaClient.deleteDadaByExpedientIdAndProcesIdAndCodi(expedientId, processInstanceId, varCodi);
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
			String varCodi) throws Exception {
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
		List<CampAgrupacioDto> agrupacionsDto = conversioTipusServiceHelper.convertirList(
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

	/*********************/

//	private List<Dada> prepararDades(String varCodi, Object varValor, Camp camp) throws Exception{
//
//		if (camp != null && !ObjectUtils.containsConstant(Tipus.values(), camp.getTipus().name())) {
//			throw new Exception("Error preparant les dades camp " + camp);
//		}
//
//		var dades = new ArrayList<Dada>();
//		// Nova variable de tipus String (camp == null)
//		var tipus = Tipus.STRING;
//		var multiple = false;
//
//		if (camp != null) {
//			tipus = Tipus.valueOf(camp.getTipus().name());
//			multiple = camp.isMultiple();
//		}
//
//		dades.add(getDada(varCodi, varValor, camp, tipus, multiple));
//		return dades;
//	}
//
//	private Dada prepararDadesUpdate(String varCodi, Object varValor, Camp camp) throws Exception{
//
//		if (camp != null && !ObjectUtils.containsConstant(Tipus.values(), camp.getTipus().name())) {
//			throw new Exception("Error preparant les dades camp " + camp);
//		}
//
//		// Nova variable de tipus String (camp == null)
//		var tipus = Tipus.STRING;
//		var multiple = false;
//
//		if (camp != null) {
//			tipus = Tipus.valueOf(camp.getTipus().name());
//			multiple = camp.isMultiple();
//		}
//
//		return getDada(varCodi, varValor, camp, tipus, multiple);
//	}
//
//	private Dada getDada(String varCodi, Object varValor, Camp camp, Tipus tipus, boolean multiple) {
//		var dada = new Dada();
//		dada.setCodi(varCodi);
//		dada.setTipus(tipus);
//		dada.setMultiple(multiple);
//
//		if (Tipus.REGISTRE.equals(tipus)) {
//			dada.setValor(getValorsRegistre(camp.getRegistreMembres(), varValor, multiple));
//		} else {
//			dada.setValor(getValorDada(varValor, multiple, tipus));
//		}
//		return dada;
//	}
//
//	private List<Valor> getValorsRegistre(
//			List<CampRegistre> registreMembres,
//			Object varValor,
//			boolean isMultiple) {
//
//		ArrayList<Valor> valors = new ArrayList<>();
//		if (!isMultiple) {
//			valors.add(getValorRegistre(registreMembres, varValor));
//			return valors;
//		}
//		var valorsObject = (Object[]) varValor;
//		for (var v : valorsObject) {
//			valors.add(getValorRegistre(registreMembres, v));
//		}
//		return valors;
//	}
//
//	private Valor getValorRegistre(List<CampRegistre> registreMembres, Object varValor) {
//
//		var valor = new ValorRegistre();
//		List<Dada> campsRegistre = new ArrayList<>();
//		var valorsObject = (Object[]) varValor;
//		for (int i = 0; i < registreMembres.size(); i++) {
//			var dadaRegistre = new Dada();
//			dadaRegistre.setCodi(registreMembres.get(i).getMembre().getCodi());
//			dadaRegistre.setTipus(Tipus.valueOf(registreMembres.get(i).getMembre().getTipus().name()));
//			dadaRegistre.setMultiple(false);
//			dadaRegistre.setValor(getValorDada(valorsObject[i], false, dadaRegistre.getTipus()));
//			campsRegistre.add(dadaRegistre);
//		}
//		valor.setCamps(campsRegistre);
//		return valor;
//	}
//
//	private ArrayList<Valor> getValorDada(Object varValor, boolean isMultiple, Tipus tipus) {
//
//		ArrayList<Valor> valors = new ArrayList<>();
//		if (!isMultiple) {
//			valors.add(getValorSimple(varValor, tipus));
//			return valors;
//		}
//		var valorsObject = (Object[]) varValor;
//		for (var v : valorsObject) {
//			valors.add(getValorSimple(v, tipus));
//		}
//		return valors;
//	}
//
//	private Valor getValorSimple(Object varValor, Tipus tipus) {
//		var valor = new ValorSimple();
//		valor.setValor(getStringFromObject(varValor));
//		valor.setValorText(getStringFormatFromObject(varValor, tipus));
//		return valor;
//	}
//
//	private String getStringFromObject(Object o) {
//		if (o == null) {
//			return "";
//		}
//		if (o instanceof Date) {
//			return dataFormat.format((Date) o);
//		}
//		if (o instanceof ParellaCodiValor) {
//			return ((ParellaCodiValor)o).getCodi();
//		}
//
//		return o.toString();
//	}
//
//	private String getStringFormatFromObject(Object o, Tipus tipus) {
//		if (o == null) {
//			return "";
//		}
//		switch (tipus) {
//			case BOOLEAN:
//				return (Boolean) o ? "Si" : "No";
//			case DATE:
//				return dataFormatLectura.format((Date) o);
//			case TERMINI:
//				Termini t = Termini.valueFromString((String)o);
//				return t.toString();
//			case SELECCIO:
//			case SUGGEST:
//				if (o instanceof ParellaCodiValor) {
//					return ((ParellaCodiValor) o).getValor().toString();
//				}
//			default:
//				return o.toString();
//		}
//
//	}

	private Camp optimitzarValorPerConsultesDominiGuardar(
			ExpedientTipus expedientTipus, 
			String processInstanceId,
			String varName,
			Object varValue) {
		WProcessDefinition jpd = workflowEngineApi.findProcessDefinitionWithProcessInstanceId(processInstanceId);
		DefinicioProces definicioProces = definicioProcesRepository.findByJbpmId(jpd.getId());
		Camp camp;
		if (expedientTipus.isAmbInfoPropia()) {
			camp = campRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(),
					varName,
					expedientTipus.getExpedientTipusPare() != null);
		} else {
			camp = campRepository.findByDefinicioProcesAndCodi(
					definicioProces,
					varName);			
		}
		if (camp != null && camp.isDominiCacheText()) {
			if (varValue != null) {
				if (camp.getTipus().equals(TipusCamp.SELECCIO) ||
					camp.getTipus().equals(TipusCamp.SUGGEST)) {
					
					String text;
					try {
						// Consultem el valor de la variable
						text = variableHelper.getTextPerCamp(
								camp, 
								varValue, 
								null, 
								null,
								processInstanceId,
								null,
								null);
					} catch (Exception e) {
						text = "";
					}

					workflowEngineApi.setProcessInstanceVariable(processInstanceId, Constants.PREFIX_VAR_DESCRIPCIO + varName, text);
				}
			}
		}
		workflowEngineApi.setProcessInstanceVariable(processInstanceId, varName, varValue);
		return camp;
	}

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
