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
import net.conselldemallorca.helium.core.model.hibernate.Alerta;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.DefinicioProces;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaAny;
import net.conselldemallorca.helium.core.model.hibernate.SequenciaDefaultAny;
import net.conselldemallorca.helium.core.security.ExtendedPermission;
import net.conselldemallorca.helium.core.util.ExpedientCamps;
import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmProcessInstance;
import net.conselldemallorca.helium.v3.core.api.dto.ControlPermisosDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.PermisTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.exception.NotAllowedException;
import net.conselldemallorca.helium.v3.core.api.exception.NotFoundException;
import net.conselldemallorca.helium.v3.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import net.conselldemallorca.helium.v3.core.repository.AlertaRepository;
import net.conselldemallorca.helium.v3.core.repository.DefinicioProcesRepository;
import net.conselldemallorca.helium.v3.core.repository.ExpedientRepository;

import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.ExpressionEvaluator;
import org.jbpm.jpdl.el.VariableResolver;
import org.jbpm.jpdl.el.impl.ExpressionEvaluatorImpl;
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
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;
	@Resource
	private JbpmHelper jbpmHelper;
	@Resource
	private PersonaHelper personaHelper;	
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private AlertaRepository alertaRepository;
	
	public ExpedientDto toExpedientDto(Expedient expedient) {
		ExpedientDto dto = new ExpedientDto();
		dto.setId(expedient.getId());
		dto.setProcessInstanceId(expedient.getProcessInstanceId());
		dto.setTitol(expedient.getTitol());
		dto.setNumero(expedient.getNumero());
		dto.setNumeroDefault(expedient.getNumeroDefault());
		dto.setComentari(expedient.getComentari());
		dto.setInfoAturat(expedient.getInfoAturat());
		dto.setComentariAnulat(expedient.getComentariAnulat());
		dto.setAnulat(expedient.isAnulat());
		dto.setDataInici(expedient.getDataInici());
		dto.setIniciadorCodi(expedient.getIniciadorCodi());
		dto.setIniciadorTipus(conversioTipusHelper.convertir(expedient.getIniciadorTipus(), IniciadorTipusDto.class));
		dto.setResponsableCodi(expedient.getResponsableCodi());
		dto.setGrupCodi(expedient.getGrupCodi());
		if (expedient.getIniciadorTipus().equals(IniciadorTipus.INTERN)) {
			if (expedient.getIniciadorCodi() != null)
				dto.setIniciadorPersona(personaHelper.findAmbCodi(expedient.getIniciadorCodi()));
			if (expedient.getResponsableCodi() != null)
				dto.setResponsablePersona(personaHelper.findAmbCodi(expedient.getResponsableCodi()));
		}
		if (expedient.getIniciadorTipus().equals(IniciadorTipus.SISTRA))
			dto.setBantelEntradaNum(expedient.getNumeroEntradaSistra());
		dto.setTipus(conversioTipusHelper.convertir(expedient.getTipus(), ExpedientTipusDto.class));
		dto.setEntorn(conversioTipusHelper.convertir(expedient.getEntorn(), EntornDto.class));
		if (expedient.getEstat() != null)
			dto.setEstat(conversioTipusHelper.convertir(expedient.getEstat(), EstatDto.class));
		dto.setGeoPosX(expedient.getGeoPosX());
		dto.setGeoPosY(expedient.getGeoPosY());
		dto.setGeoReferencia(expedient.getGeoReferencia());
		dto.setRegistreNumero(expedient.getRegistreNumero());
		dto.setRegistreData(expedient.getRegistreData());
		dto.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
		dto.setIdioma(expedient.getIdioma());
		dto.setAutenticat(expedient.isAutenticat());
		dto.setTramitadorNif(expedient.getTramitadorNif());
		dto.setTramitadorNom(expedient.getTramitadorNom());
		dto.setInteressatNif(expedient.getInteressatNif());
		dto.setInteressatNom(expedient.getInteressatNom());
		dto.setRepresentantNif(expedient.getRepresentantNif());
		dto.setRepresentantNom(expedient.getRepresentantNom());
		dto.setAvisosHabilitats(expedient.isAvisosHabilitats());
		dto.setAvisosEmail(expedient.getAvisosEmail());
		dto.setAvisosMobil(expedient.getAvisosMobil());
		dto.setErrorDesc(expedient.getErrorDesc());
		dto.setErrorFull(expedient.getErrorFull());
		dto.setErrorsIntegracions(expedient.isErrorsIntegracions());
		dto.setNotificacioTelematicaHabilitada(expedient.isNotificacioTelematicaHabilitada());
		dto.setTramitExpedientIdentificador(expedient.getTramitExpedientIdentificador());
		dto.setTramitExpedientClau(expedient.getTramitExpedientClau());
		dto.setErrorsIntegracions(expedient.isErrorsIntegracions());		
		dto.setDataFi(expedient.getDataFi());
		
		return dto;
	}

	public Expedient getExpedientComprovantPermisos(
			Long id,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisDelete,
			boolean comprovarPermisAdministration) throws NotFoundException, NotAllowedException {
		Expedient expedient = expedientRepository.findOne(id);
		if (expedient == null) {
			throw new NotFoundException(
					id,
					Expedient.class);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Entorn entorn = expedient.getEntorn();
		if (!permisosHelper.isGrantedAny(
				entorn.getId(),
				Entorn.class,
				new Permission[] {
					ExtendedPermission.READ,
					ExtendedPermission.ADMINISTRATION},
				auth)) {
			throw new NotAllowedException(
					id,
					Entorn.class,
					PermisTipusEnumDto.READ);
		}
		ExpedientTipus expedientTipus = expedient.getTipus();
		if (comprovarPermisRead) {
			if (!permisosHelper.isGrantedAny(
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
			if (!permisosHelper.isGrantedAny(
					expedientTipus.getId(),
					ExpedientTipus.class,
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
			if (!permisosHelper.isGrantedAny(
					expedientTipus.getId(),
					ExpedientTipus.class,
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
		if (comprovarPermisAdministration) {
			if (!permisosHelper.isGrantedAny(
					expedientTipus.getId(),
					ExpedientTipus.class,
					new Permission[] {
						ExtendedPermission.ADMINISTRATION},
					auth)) {
				throw new NotAllowedException(
						id,
						Expedient.class,
						PermisTipusEnumDto.ADMINISTRATION);
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
		JbpmProcessInstance rootProcessInstance = jbpmHelper.getRootProcessInstance(
				processInstanceId);
		return findAmbProcessInstanceId(
				rootProcessInstance.getId());
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

	public void omplirPermisosExpedientsTipus(List<ExpedientTipusDto> expedientsTipus) {
		List<Long> expedientTipusIds = new ArrayList<Long>();
		for (ExpedientTipusDto expedientTipus: expedientsTipus) {
			expedientTipusIds.add(expedientTipus.getId());
		}
		omplirPermisosDto(
				expedientTipusIds,
				expedientsTipus);
	}
	public void omplirPermisosExpedients(List<ExpedientDto> expedients) {
		List<Long> expedientTipusIds = new ArrayList<Long>();
		for (ExpedientDto expedient: expedients) {
			expedientTipusIds.add(expedient.getTipus().getId());
		}
		omplirPermisosDto(
				expedientTipusIds,
				expedients);
	}
	public void omplirPermisosExpedient(ExpedientDto expedient) {
		List<ExpedientDto> expedients = new ArrayList<ExpedientDto>();
		expedients.add(expedient);
		omplirPermisosExpedients(expedients);
	}
	public void trobarAlertesExpedients(List<ExpedientDto> expedients) {
		List<Long> expedientIds = new ArrayList<Long>();
		for (ExpedientDto expedient: expedients) {
			expedientIds.add(expedient.getId());
		}
		omplirNumAlertes(
				expedientIds,
				expedients);
	}
	public void trobarAlertesExpedient(ExpedientDto dto) {
		Expedient expedient = this.getExpedientComprovantPermisos(
				dto.getId(),
				true,
				false,
				false,
				false);
		List<Alerta> alertes = alertaRepository.findByExpedientAndDataEliminacioNull(expedient);
		
		long pendents = 0L;
		if (!alertes.isEmpty()) {
			dto.setAlertesTotals(new Long(alertes.size()));
			dto.setAlertesPendents(0L);
			for (Alerta alerta: alertes) {
				if (alerta.getDataLectura() == null)
					pendents ++;
			}
			dto.setAlertesPendents(pendents);
		}
		
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

	private Expedient findAmbProcessInstanceId(String processInstanceId) {
		List<Expedient> expedients = expedientRepository.findByProcessInstanceId(
				processInstanceId);
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

	private void omplirPermisosDto(
			List<Long> ids,
			List<? extends ControlPermisosDto> dtos) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ObjectIdentifierExtractor<Long> oie = new ObjectIdentifierExtractor<Long>() {
			public Long getObjectIdentifier(Long id) {
				return id;
			}
		};
		List<Long> idsAmbPermisCreate = new ArrayList<Long>();
		idsAmbPermisCreate.addAll(ids);
		permisosHelper.filterGrantedAny(
				idsAmbPermisCreate,
				oie,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.CREATE,
					ExtendedPermission.ADMINISTRATION},
				auth);
		List<Long> idsAmbPermisRead = new ArrayList<Long>();
		idsAmbPermisRead.addAll(ids);
		permisosHelper.filterGrantedAny(
				idsAmbPermisRead,
				oie,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.READ,
					ExtendedPermission.ADMINISTRATION},
				auth);
		List<Long> idsAmbPermisWrite = new ArrayList<Long>();
		idsAmbPermisWrite.addAll(ids);
		permisosHelper.filterGrantedAny(
				idsAmbPermisWrite,
				oie,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.WRITE,
					ExtendedPermission.ADMINISTRATION},
				auth);
		List<Long> idsAmbPermisDelete = new ArrayList<Long>();
		idsAmbPermisDelete.addAll(ids);
		permisosHelper.filterGrantedAny(
				idsAmbPermisDelete,
				oie,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.DELETE,
					ExtendedPermission.ADMINISTRATION},
				auth);
		List<Long> idsAmbPermisSupervision = new ArrayList<Long>();
		idsAmbPermisSupervision.addAll(ids);
		permisosHelper.filterGrantedAny(
				idsAmbPermisSupervision,
				oie,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.SUPERVISION,
					ExtendedPermission.ADMINISTRATION},
				auth);
		List<Long> idsAmbPermisReassignment = new ArrayList<Long>();
		idsAmbPermisReassignment.addAll(ids);
		permisosHelper.filterGrantedAny(
				idsAmbPermisReassignment,
				oie,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.REASSIGNMENT,
					ExtendedPermission.ADMINISTRATION},
				auth);
		List<Long> idsAmbPermisAdministration = new ArrayList<Long>();
		idsAmbPermisAdministration.addAll(ids);
		permisosHelper.filterGrantedAll(
				idsAmbPermisAdministration,
				oie,
				ExpedientTipus.class,
				new Permission[] {
					ExtendedPermission.ADMINISTRATION},
				auth);
		for (int i = 0; i < ids.size(); i++) {
			Long id = ids.get(i);
			ControlPermisosDto dto = dtos.get(i);
			dto.setPermisCreate(
					idsAmbPermisCreate.contains(id));
			dto.setPermisRead(
					idsAmbPermisRead.contains(id));
			dto.setPermisWrite(
					idsAmbPermisWrite.contains(id));
			dto.setPermisDelete(
					idsAmbPermisDelete.contains(id));
			dto.setPermisSupervision(
					idsAmbPermisSupervision.contains(id));
			dto.setPermisReassignment(
					idsAmbPermisReassignment.contains(id));
			dto.setPermisAdministration(
					idsAmbPermisAdministration.contains(id));
		}
	}
	
	private void omplirNumAlertes(List<Long> ids, List<ExpedientDto> dtos) {
		List<Object[]> comptadorsAlertes = alertaRepository.findByExpedientIds(ids);
		
		for (Object[] comptadorsAlerta: comptadorsAlertes) {
			long id = (Long)comptadorsAlerta[0];
			for (ExpedientDto dto: dtos) {
				if (id == dto.getId()) {
					dto.setAlertesTotals((Long)comptadorsAlerta[1]);
					dto.setAlertesPendents((Long)comptadorsAlerta[2]);
				}
			}
		}
	}

	private String getNumexpDefaultExpression() {
		return GlobalProperties.getInstance().getProperty("app.numexp.expression");
	}

}
