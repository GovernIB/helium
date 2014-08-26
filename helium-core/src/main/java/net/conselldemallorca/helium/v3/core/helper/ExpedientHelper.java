/**
 * 
 */
package net.conselldemallorca.helium.v3.core.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dto.ExpedientIniciantDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaDefaultAny;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper;
import net.conselldemallorca.helium.core.model.service.PermisosHelper;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.v3.core.api.dto.PermisTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.exception.NotAllowedException;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.PermissionService;
import net.conselldemallorca.helium.v3.core.repository.CampAgrupacioRepository;
import net.conselldemallorca.helium.v3.core.repository.CampRepository;
import net.conselldemallorca.helium.v3.core.repository.ConsultaCampRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;

import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.ExpressionEvaluator;
import org.jbpm.jpdl.el.VariableResolver;
import org.jbpm.jpdl.el.impl.ExpressionEvaluatorImpl;
import org.springframework.context.MessageSource;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Helper per a gestionar els expedients.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientHelper {

	@Resource
	private DefinicioProcesRepository definicioProcesRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private DominiHelper dominiHelper;
	@Resource(name="serviceUtilsV3")
	private ServiceUtils serviceUtils;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private ConsultaHelper consultaHelper;
	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;
	@Resource
	private MessageSource messageSource;
	@Resource
	private CampRepository campRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private CampAgrupacioRepository campAgrupacioRepository;
	@Resource
	private ConsultaCampRepository consultaCampRepository;
	@Resource(name="permissionServiceV3")
	private PermissionService permissionService;
	@Resource(name="dtoConverterV3")
	private DtoConverter dtoConverter;

	/*public Expedient getExpedientComprovantPermisosAny(
			Long expedientId,
			Permission[] permisos) {
		Expedient expedient = expedientRepository.findOne(expedientId);
		if (expedient == null) {
			logger.debug("No s'ha trobat l'expedient (id=" + expedientId + ")");
			throw new ExpedientNotFoundException();
		}
		boolean ambPermis = permisosHelper.isGrantedAny(
				expedient.getTipus().getId(),
				ExpedientTipus.class,
				permisos);
		if (!ambPermis) {
			logger.debug("No es tenen permisos per accedir a l'expedient (id=" + expedientId + ")");
			throw new ExpedientNotFoundException();
		}
		return expedient;
	}*/
	public Expedient getExpedientComprovantPermisos(
			Long id,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisDelete) throws NotFoundException, NotAllowedException {
		Expedient expedient = expedientRepository.findOne(id);
		if (expedient == null) {
			throw new NotFoundException(
					id,
					Expedient.class);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Entorn entorn = expedient.getEntorn();
		if (!permisosHelper.isGrantedAll(
				entorn.getId(),
				Entorn.class,
				new Permission[] {
					ExtendedPermission.READ},
				auth)) {
			throw new NotAllowedException(
					id,
					Entorn.class,
					PermisTipusEnumDto.READ);
		}
		ExpedientTipus expedientTipus = expedient.getTipus();
		if (comprovarPermisRead) {
			if (!permisosHelper.isGrantedAll(
					expedientTipus.getId(),
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.READ,
						ExtendedPermission.ADMINISTRATION},
					auth)) {
				throw new NotAllowedException(
						id,
						Expedient.class,
						PermisTipusEnumDto.READ);
			}
		}
		if (comprovarPermisWrite) {
			if (!permisosHelper.isGrantedAll(
					expedientTipus.getId(),
					Expedient.class,
					new Permission[] {
						ExtendedPermission.WRITE,
						ExtendedPermission.ADMINISTRATION},
					auth)) {
				throw new NotAllowedException(
						id,
						Expedient.class,
						PermisTipusEnumDto.WRITE);
			}
		}
		if (comprovarPermisDelete) {
			if (!permisosHelper.isGrantedAll(
					expedientTipus.getId(),
					Expedient.class,
					new Permission[] {
						ExtendedPermission.DELETE,
						ExtendedPermission.ADMINISTRATION},
					auth)) {
				throw new NotAllowedException(
						id,
						Expedient.class,
						PermisTipusEnumDto.DELETE);
			}
		}
		return expedient;
	}

	public void comprovarInstanciaProces(
			Expedient expedient,
			String processInstanceId) {
		if (!expedient.getProcessInstanceId().equals(processInstanceId)) {
			List<JbpmProcessInstance> processInstanceFills = jbpmHelper.getProcessInstanceTree(
					expedient.getProcessInstanceId());
			boolean trobada = false;
			for (JbpmProcessInstance processInstance: processInstanceFills) {
				if (expedient.getProcessInstanceId().equals(processInstance.getId())) {
					trobada = true;
					break;
				}
			}
			if (!trobada) {
				throw new NotFoundException(
						new Long(processInstanceId),
						JbpmProcessInstance.class);
			}
		}
	}

	public Expedient findAmbEntornIId(Long entornId, Long id) {
		return expedientRepository.findByEntornIdAndId(entornId, id);
	}
	
	public Expedient findExpedientByProcessInstanceId(String processInstanceId) {
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(processInstanceId);
		return findAmbProcessInstanceId(rootProcessInstance.getId());
	}
	public DefinicioProces findDefinicioProcesByProcessInstanceId(String processInstanceId) {
		String processDefinitionId = jbpmHelper.getProcessInstance(processInstanceId).getProcessDefinitionId();
		return definicioProcesRepository.findByJbpmId(processDefinitionId);
	}

	public boolean isFinalitzat(Expedient expedient) {
		return expedient.getDataFi() != null;
	}

	public List<Camp> findAllCampsExpedientConsulta() {
		List<Camp> resposta = new ArrayList<Camp>();
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_ID));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_NUMERO));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_TITOL));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_COMENTARI));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_INICIADOR));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI));
		resposta.add(
				getCampExpedient(ExpedientCamps.EXPEDIENT_CAMP_ESTAT));
		return resposta;
	}

	private Camp getCampExpedient(String campCodi) {
		if (ExpedientCamps.EXPEDIENT_CAMP_ID.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.INTEGER);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.id"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_NUMERO.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.numero"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_TITOL.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.titol"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_COMENTARI.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.STRING);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.comentari"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_INICIADOR.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.iniciador"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_RESPONSABLE.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SUGGEST);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.responsable"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_DATA_INICI.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.DATE);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.data_ini"));
			return campExpedient;
		}
		if (ExpedientCamps.EXPEDIENT_CAMP_ESTAT.equals(campCodi)) {
			Camp campExpedient = new Camp();
			campExpedient.setCodi(campCodi);
			campExpedient.setTipus(TipusCamp.SELECCIO);
			campExpedient.setEtiqueta(messageHelper.getMessage("etiqueta.exp.estat"));
			return campExpedient;
		}
		return null;
	}

	public String getNumeroExpedientActual(
			ExpedientTipus expedientTipus,
			int any,
			long increment) {
		long seq = expedientTipus.getSequencia();
		return getNumeroExpedientExpressio(
				expedientTipus,
				expedientTipus.getExpressioNumero(),
				seq,
				increment,
				any,
				expedientTipus.isReiniciarCadaAny(),
				false);
	}
	
	public String getNumeroExpedientDefaultActual(
			ExpedientTipus expedientTipus,
			int any,
			long increment) {
		long seq = expedientTipus.getSequenciaDefault();
		return getNumeroExpedientExpressio(
				expedientTipus,
				getNumexpDefaultExpression(),
				seq,
				increment,
				any,
				expedientTipus.isReiniciarCadaAny(),
				true);
	}

//	public List<ExpedientConsultaDissenyDto> findAmbEntornConsultaDisseny(
//			Long entornId,
//			Long consultaId,
//			Map<String, Object> valors,
//			String sort,
//			boolean asc) {
//		return findAmbEntornConsultaDisseny(
//				entornId,
//				consultaId,
//				valors,
//				sort,
//				asc,
//				0,
//				-1);
//	}

//	private void afegirValorsPredefinits(
//			ConsultaDto consulta,
//			Map<String, Object> valors,
//			List<CampDto> camps) {
//		if (consulta.getValorsPredefinits() != null && consulta.getValorsPredefinits().length() > 0) {
//			String[] parelles = consulta.getValorsPredefinits().split(",");
//			for (String parelle : parelles) {
//				String[] parella = (parelle.contains(":")) ? parelle.split(":") : parelle.split("=");
//				if (parella.length == 2) {
//					String campCodi = parella[0];
//					String valor = parella[1];
//					for (CampDto camp: camps) {
//						if (camp.getCodi().equals(campCodi)) {
//							valors.put(
//									camp.getDefinicioProces().getJbpmKey() + "." + campCodi,
//									CampDto.getComObject(
//											camp.getTipus(),
//											valor));
//							break;
//						}
//					}
//				}
//			}
//		}
//	}

//	private ServiceUtils getServiceUtils() {
//		if (serviceUtils == null) {
//			serviceUtils = new ServiceUtils();
//		}
//		return serviceUtils;
//	}
	
//	public List<ExpedientConsultaDissenyDto> findAmbEntornConsultaDisseny(
//			Long entornId,
//			Long consultaId,
//			Map<String, Object> valors,
//			String sort,
//			boolean asc,
//			int firstRow,
//			int maxResults) {
//		List<ExpedientConsultaDissenyDto> resposta = new ArrayList<ExpedientConsultaDissenyDto>();
//		Consulta consulta = consultaHelper.findById(consultaId);
//		ConsultaDto consultaDto = conversioTipusHelper.convertir(consulta, ConsultaDto.class);
//		List<Camp> oCampsFiltre = getServiceUtils().findCampsPerCampsConsulta(
//				consulta,
//				net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp.FILTRE);
//		List<Camp> oCampsInforme = getServiceUtils().findCampsPerCampsConsulta(
//				consulta,
//				net.conselldemallorca.helium.core.model.hibernate.ConsultaCamp.TipusConsultaCamp.INFORME);
//		afegirValorsPredefinits(consultaDto, valors, campsFiltre);
//		List<Map<String, DadaIndexadaDto>> dadesExpedients = luceneHelper.findAmbDadesExpedientV3(
//				consulta.getEntorn().getCodi(),
//				consulta.getExpedientTipus().getCodi(),
//				campsFiltre,
//				valors,
//				campsInforme,
//				sort,
//				asc,
//				firstRow,
//				maxResults);
//		
//		List<CampDto> campsInformeDto = new ArrayList<CampDto>();
//		for(CampDto campTmp : campsInforme) {
//			campsInformeDto.add(campTmp);
//		}
//		
//		for (Map<String, DadaIndexadaDto> dadesExpedient: dadesExpedients) {
//			DadaIndexadaDto dadaExpedientId = dadesExpedient.get(LuceneHelper.CLAU_EXPEDIENT_ID);
//			ExpedientConsultaDissenyDto fila = new ExpedientConsultaDissenyDto();
//			Expedient expedient = expedientRepository.findById(Long.parseLong(dadaExpedientId.getValorIndex()));
//			if (expedient != null) {
//				fila.setExpedient(new ModelMapper().map(expedient, ExpedientDto.class));				
//				
//				dtoConverter.revisarDadesExpedientAmbValorsEnumeracionsODominis(
//						dadesExpedient,
//						campsInformeDto);
//				fila.setDadesExpedient(dadesExpedient);
//				resposta.add(fila);
//			}
//			dadesExpedient.remove(LuceneHelper.CLAU_EXPEDIENT_ID);
//		}
//		return resposta;
//	}



	private Expedient findAmbProcessInstanceId(String processInstanceId) {
		List<Expedient> expedients = expedientRepository.findByProcessInstanceId(processInstanceId);
		if (expedients.size() > 0) {
			return expedients.get(0);
		} else {
			Expedient expedientIniciant = ExpedientIniciantDto.getExpedient();
			if (expedientIniciant != null && expedientIniciant.getProcessInstanceId().equals(processInstanceId))
				return expedientIniciant;
		}
		return null;
	}

	private String getNumeroExpedientExpressio(
			ExpedientTipus expedientTipus,
			String expressio,
			long seq,
			long increment,
			int any,
			boolean reiniciarCadaAny,
			boolean numeroDefault) {
		if (reiniciarCadaAny) {
			if (any != 0) {
				if (numeroDefault) {
					if (expedientTipus.getSequenciaDefaultAny().containsKey(any)) {
						seq = expedientTipus.getSequenciaDefaultAny().get(any).getSequenciaDefault() + increment;
					} else {
						// TODO: podriem comprovar, segons el format del número per defecte si hi ha expedients ja creats de 
						// l'any, i d'aquesta manera assignar com a número inicial el major utilitzat + 1
						SequenciaDefaultAny sda = new SequenciaDefaultAny(
								expedientTipus, any, 1L);
						expedientTipus.getSequenciaDefaultAny().put(any, sda);
						seq = 1;
					}
				} else {
					if (expedientTipus.getSequenciaAny().containsKey(any)) {
						seq = expedientTipus.getSequenciaAny().get(any).getSequencia() + increment;
					} else {
						SequenciaAny sa = new SequenciaAny(expedientTipus, any, 1L);
						expedientTipus.getSequenciaAny().put(any, sa);
						seq = 1;
					}
				}
			}
		} else {
			seq = seq + increment;
		}
		if (expressio != null) {
			try {
				final Map<String, Object> context = new HashMap<String, Object>();
				context.put("entorn_cod", expedientTipus.getEntorn().getCodi());
				context.put("tipexp_cod", expedientTipus.getCodi());
				context.put("any", any);
				context.put("seq", seq);
				ExpressionEvaluator evaluator = new ExpressionEvaluatorImpl();
				String resultat = (String)evaluator.evaluate(
						expressio,
						String.class,
						new VariableResolver() {
							public Object resolveVariable(String name)
									throws ELException {
								return context.get(name);
							}
						},
						null);
				return resultat;
			} catch (Exception ex) {
				return "#invalid expression#";
			}
		} else {
			return new Long(seq).toString();
		}
	}

	private String getNumexpDefaultExpression() {
		return GlobalProperties.getInstance().getProperty("app.numexp.expression");
	}

}
