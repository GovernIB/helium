/**
 *
 */
package es.caib.helium.logic.helper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.commons.constants.ExpedientCamps;
import es.caib.helium.commons.dades.DadesValor;
import es.caib.helium.commons.dto.CampTipusEnum;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.TascaDadaDto;
import es.caib.helium.commons.dto.TerminiDto;
import es.caib.helium.persistence.entity.Camp;
import es.caib.helium.persistence.entity.DefinicioProces;
import es.caib.helium.persistence.entity.Entorn;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.ExpedientDades;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.repository.CampRepository;
import es.caib.helium.persistence.repository.DefinicioProcesRepository;
import es.caib.helium.persistence.repository.ExpedientDadesRepository;

/**
 * Helper per les dades d'expedient
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientDadaHelper {

	@Resource
	private ExpedientDadesRepository expedientDadesRepository;
	@Resource
	private CampRepository campRepository;

	@Resource
	private DefinicioProcesRepository definicioProcesRepository;

	@Resource
	private ExpedientTipusHelper expedientTipusHelper;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;
//	@Resource
//	private WorkflowEngineApi workflowEngineApi;
	@Resource
	private VariableHelper variableHelper;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	// Objectes estàtics per fer la conversió JSON/Map
	private static ObjectMapper mapper;
    private static TypeReference<HashMap<String,DadesValor>> typeRef;
	static {
		mapper = new ObjectMapper();
		typeRef = new TypeReference<HashMap<String,DadesValor>>() {};
	}

	public List<Camp> findCampsDisponiblesOrdenatsPerCodi(ExpedientTipus expedientTipus, DefinicioProces definicioProces) {
		if (expedientTipus.isAmbInfoPropia()) {
			return campRepository.findByExpedientTipusOrderByCodiAsc(expedientTipus);
		} else {
			return campRepository.findByDefinicioProcesOrderByCodiAsc(definicioProces);
		}
	}

	public void optimitzarValorPerConsultesDominiGuardar(
			ExpedientTipus expedientTipus,
			String processInstanceId,
			String varName,
			Object varValue) {
		Camp camp;
		if (expedientTipus.isAmbInfoPropia())
			camp = campRepository.findByExpedientTipusAndCodi(
					expedientTipus.getId(),
					varName,
					expedientTipus.getExpedientTipusPare() != null);
		else {
			camp = campRepository.findByDefinicioProcesAndCodi(
					null, //definicioProces,
					varName);
		}
		if (camp != null && camp.isDominiCacheText()) {
			if (varValue != null) {
				if (camp.getTipus().equals(CampTipusEnum.SELECCIO) ||
					camp.getTipus().equals(CampTipusEnum.SUGGEST)) {

					String text;
					try {
						// Consultem el valor de la variable
						text = variableHelper.getTextPerCamp(
								camp,
								varValue,
								null,
								null,
								processInstanceId);
					} catch (Exception e) {
						text = "";
					}

//					workflowEngineApi.setProcessInstanceVariable(processInstanceId, JbpmVars.PREFIX_VAR_DESCRIPCIO + varName, text);
				}
			}
		}
//		workflowEngineApi.setProcessInstanceVariable(processInstanceId, varName, varValue);
	}


	/** Estableix el valor per una dada de l'expedient
	 *
	 * @param expedientId
	 * @param processInstanceId
	 * @param varCodi
	 * @param varValor
	 */
	@Transactional
	public void setDada(Expedient expedient, String processId, String taskId, String varCodi, Object varValor) {

		// Recupera les dades per l'expedient segons el context de tasca, procés i expedient
		ExpedientDades expedientDadesEntity = this.getDades(expedient, processId, taskId);
		if (expedientDadesEntity == null) {
			expedientDadesEntity = new ExpedientDades();
			expedientDadesEntity.setExpedient(expedient);
			expedientDadesEntity.setExpedientTipus(expedient.getTipus());
			expedientDadesEntity.setProcessId(processId);
			expedientDadesEntity.setTaskId(taskId);
			expedientDadesRepository.save(expedientDadesEntity);
		}
		// Fixa el valor de la dada
		this.optimitzarValorPerConsultesDominiGuardar(expedient.getTipus(), processId, varCodi, varValor);

		try {
			Map<String, DadesValor> dades;
			if (expedientDadesEntity.getDades() != null) {
				dades = DadesToMap(expedientDadesEntity.getDades());
			} else {
				dades = new HashMap<String, DadesValor>();
			}
			dades.put(varCodi, new DadesValor(varValor, "s"));
			expedientDadesEntity.setDades(MapToDades(dades));
		} catch(Exception e) {
			throw new RuntimeException("Error fixant valor json a les dades per conversió de les dades.", e);
		}
	}

	/** Obté el valor per una dada de l'expedient
	 *
	 * @param expedientId
	 * @param processInstanceId
	 * @param varCodi
	 */
	@Transactional
	public Object getDada(Expedient expedient, String processId, String taskId, String varCodi) {

		Object valor = null;

		// Recupera les dades per l'expedient segons el context de tasca, procés i expedient
		ExpedientDades expedientDadesEntity = this.getDades(expedient, processId, taskId);
		if (expedientDadesEntity == null) {
			return valor;
		}
		try {
			Map<String, DadesValor> dades;
			if (expedientDadesEntity.getDades() != null) {
				dades = DadesToMap(expedientDadesEntity.getDades());
				if (dades.containsKey(varCodi)) {
					valor = dades.get(varCodi).getV();
				}
			}
		} catch(Exception e) {
			throw new RuntimeException("Error fixant valor json a les dades per conversió de les dades.", e);
		}
		return valor;
	}

	/** Obté tots els valors de les variables l'expedient
	 *
	 * @param expedientId
	 * @param processInstanceId
	 * @param varCodi
	 */
	@Transactional
	public Map<String, Object> getDadesValors(Expedient expedient, String processId, String taskId) {

		Map<String, Object> valors = new HashMap<String, Object>();

		// Recupera les dades per l'expedient segons el context de tasca, procés i expedient
		ExpedientDades expedientDadesEntity = this.getDades(expedient, processId, taskId);
		if (expedientDadesEntity == null) {
			return valors;
		}
		try {
			if (expedientDadesEntity.getDades() != null) {
				Map<String, DadesValor> dades = DadesToMap(expedientDadesEntity.getDades());
				for (String varCodi : dades.keySet()) {
					valors.put(varCodi, dades.get(varCodi).getV());
				}
			}
		} catch(Exception e) {
			throw new RuntimeException("Error fixant valor json a les dades per conversió de les dades.", e);
		}
		return valors;
	}


	private String MapToDades(Map<String, DadesValor> dades) throws Exception {
		String json = mapper.writeValueAsString(dades);
		return json;
	}

	private Map<String, DadesValor> DadesToMap(String dades) throws Exception {
	    HashMap<String,DadesValor> map = mapper.readValue(dades, typeRef);
	    return map;
	}

	/** Consulta les dades segons la tasca, procés i expedient. */
	private ExpedientDades getDades(Expedient expedient, String processId, String taskId) {
		ExpedientDades expedientDades = null;
		if (taskId != null) {
			expedientDades = expedientDadesRepository.findByExpedientAndTaskId(expedient, taskId);
		} else if (processId != null) {
			expedientDades = expedientDadesRepository.findByExpedientAndProcessId(expedient, processId);
		} else {
			expedientDades = expedientDadesRepository.findByExpedient(expedient);
		}
		return expedientDades;
	}

	/** Mètode per esborrar una dada de l'expedient donat el seu codi. */
	public void deleteDada(Expedient expedient, String processId, String taskId, String varCodi) {

		// Recupera les dades per l'expedient segons el context de tasca, procés i expedient
		ExpedientDades expedientDadesEntity = this.getDades(expedient, processId, taskId);
		if (expedientDadesEntity == null) {
			return;
		}
		try {
			Map<String, DadesValor> dades;
			if (expedientDadesEntity.getDades() != null) {
				dades = DadesToMap(expedientDadesEntity.getDades());
				if (dades.containsKey(varCodi)) {
					dades.remove(varCodi);
				}
				expedientDadesEntity.setDades(MapToDades(dades));
			}
		} catch(Exception e) {
			throw new RuntimeException("Error fixant valor json a les dades per conversió de les dades.", e);
		}
	}

	/** Actualitza les dades de l'expedient al servei de dade. */
	public boolean setExpedientDades(Expedient expedient) {
		// TODO Auto-generated method stub
		return true;
	}

	/** Mètode de l'IndexHelper que s'ha de substituir. */
	public List<Long> findExpedientsIdsByFiltre(Entorn entorn, ExpedientTipus expedientTipus, List<Camp> filtreCamps,
			Map<String, Object> filtreValors) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	public List<Map<String, Object>> queryConsultaPaginat(
			Long entornId,
			Long expedientTipusId,
			Map<String, Object> filtre,
			List<TascaDadaDto> filtreCamps,
			List<TascaDadaDto> informeCamps,
			PaginacioParamsDto paginacioParams) {

		List<Object> args = new ArrayList<Object>();
		StringBuilder query = new StringBuilder("SELECT * FROM ( SELECT rownum r__, t.* FROM ( ");
		query.append(" SELECT expedient.ID, ");

		// Afegim les columnes del select a la query
		query.append(String.join(
				", ",
				informeCamps
					.stream()
					.map((ic) -> {
						if(ic.getVarCodi().startsWith(ExpedientCamps.EXPEDIENT_PREFIX))
							return ic.getVarCodi().replace(ExpedientCamps.EXPEDIENT_PREFIX, "expedient.");
						return "d.DADES." + ic.getVarCodi() + ".v as " + ic.getVarCodi();
					})
					.collect(Collectors.toList())));

		query.append(" FROM HEL_EXPEDIENT_DADES d, HEL_EXPEDIENT expedient ");
		query.append(" WHERE expedient.ID = d.EXPEDIENT_ID ");
		query.append(" AND expedient.ENTORN_ID = ? ");
		args.add(entornId);
		query.append(" AND expedient.TIPUS_ID = ? ");
		args.add(expedientTipusId);

		SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
		AbstractEntityPersister persister = ((AbstractEntityPersister)sessionFactory.getClassMetadata(Expedient.class));

		// FILTRE
		for(TascaDadaDto f : filtreCamps) {
			Object filtreVal = filtre.get(f.getVarCodi());

			String campNom = f.getVarCodi();
			if(campNom.startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
				campNom = campNom.substring(campNom.indexOf("$")+1);
				String[] columns = persister.getPropertyColumnNames(campNom);
				campNom = "expedient." + columns[0];
			} else {
				campNom = "d.DADES." + campNom + ".v";
			}
			if(filtreVal == null)
				continue;

			if(filtreVal != null) {
				switch(f.getCampTipus()) {
//				case DATE:
//					Date[] fdates = (Date[])filtreVal;
//					if(fdates[0] == null && fdates[1] == null)
//						break;
//					query.append(" AND " + campNom + " BETWEEN ? AND ? ");
//					args.add(fdates[0] != null? fdates[0] : new Date());
//					args.add(fdates[1] != null? fdates[1] : new Date());
//					break;
				case TEXTAREA:
				case STRING:
					query.append(" AND " + campNom + " like ? ");
					args.add("%" + filtreVal + "%");
					break;
				case DATE:
				case INTEGER:
				case FLOAT:
				case PRICE:
					if(filtreVal instanceof Object[]) {
						Object[] frange = (Object[])filtreVal;
						if(frange[0] != null) {
							query.append(" AND " + campNom + " >= ? ");
							args.add(frange[0]);
						}
						if(frange[1] != null) {
							query.append(" AND " + campNom + " <= ? ");
							args.add(frange[1]);
						}
						break;
					}
				case SELECCIO:
				case SUGGEST:
				default:
					query.append(" AND " + campNom + " = ? ");
					args.add(filtreVal);
					break;
				}
			}
		}

		// ORDENACIÓ
		if(paginacioParams.getOrdres() == null || paginacioParams.getOrdres().isEmpty()) {
			query.append(" ORDER BY expedient.ID DESC ");
		} else {
			query.append(" ORDER BY " +
					String.join(", ",
							paginacioParams
							.getOrdres()
							.stream()
							.map(o -> {
								if(o.getCamp().equals("expedient.identificador")) {
									return " expedient.NUMERO " + (o.getDireccio() == PaginacioParamsDto.OrdreDireccioDto.ASCENDENT? "ASC" : "DESC") +
											" ,expedient.NUMERO_DEFAULT " + (o.getDireccio() == PaginacioParamsDto.OrdreDireccioDto.ASCENDENT? "ASC" : "DESC");
								}
								String camp = o.getCamp().replaceFirst("dadesExpedient.", "d.DADES.");
								return camp.substring(0, camp.lastIndexOf(".valor")) + ".v " + (o.getDireccio() == PaginacioParamsDto.OrdreDireccioDto.ASCENDENT? "ASC" : "DESC");
							})
							.collect(Collectors.toList())));
		}

		if(paginacioParams.getPaginaTamany() > 0) {
			query.append(" ) t WHERE rownum < ((? * ?) + 1 ) ");
			args.add(paginacioParams.getPaginaNum()+1);
			args.add(paginacioParams.getPaginaTamany());

			query.append(" ) WHERE r__ >= (((? - 1) * ?) + 1) ");
			args.add(paginacioParams.getPaginaNum()+1);
			args.add(paginacioParams.getPaginaTamany());
		} else {
			query.append(" ) t ) ");
		}

		List<Map<String, Object>> resposta = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> result = jdbcTemplate.queryForList(query.toString(), args.toArray());

		for(Map<String, Object> row : result) {
			Map<String, Object> fila = new HashMap<String, Object>();
			fila.put("id", ((BigDecimal)row.get("ID")).longValue());
			for(int i = 0; i < informeCamps.size(); i++) {
				TascaDadaDto td = informeCamps.get(i);
				fila.put(
						td.getVarCodi(),
						getComText(td.getCampTipus(), (String)row.get(td.getVarCodi()), null));
			}
			resposta.add(fila);
		}

		return resposta;
	}

	private String getComText(
			CampTipusEnum tipus,
			String valor,
			String valorDomini) {
		if (valor == null)
			return null;
		try {
			String text = null;
			if (tipus.equals(CampTipusEnum.INTEGER)) {
				text = new DecimalFormat("#").format(Long.valueOf(valor));
			} else if (tipus.equals(CampTipusEnum.FLOAT)) {
				text = new DecimalFormat("#.##########").format(Double.valueOf(valor));
			} else if (tipus.equals(CampTipusEnum.PRICE)) {
				text = new DecimalFormat("#,##0.00").format(new BigDecimal(valor));
			} else if (tipus.equals(CampTipusEnum.DATE)) {
				// text = new SimpleDateFormat("dd/MM/yyyy").format((Date)valor);
				text = valor;
			} else if (tipus.equals(CampTipusEnum.BOOLEAN)) {
				text = Boolean.valueOf(valor) ? "Si" : "No";
			} else if (tipus.equals(CampTipusEnum.SELECCIO)) {
				text = valorDomini;
			} else if (tipus.equals(CampTipusEnum.SUGGEST)) {
				text = valorDomini;
			} else if (tipus.equals(CampTipusEnum.TERMINI)) {
				//if (valor instanceof Termini) {
					//text = ((Termini)valor).toString();
				//} else {
					String termtxt = (String)valor;
					String[] parts = termtxt.split("/");
					TerminiDto t = new TerminiDto();
					t.setAnys((parts.length >= 0) ? new Integer(parts[0]).intValue() : 0);
					t.setMesos((parts.length >= 1) ? new Integer(parts[1]).intValue() : 0);
					t.setDies((parts.length >= 2) ? new Integer(parts[2]).intValue() : 0);
					text = t.toString();
				//}
			} else {
				text = valor.toString();
			}
			return text;
		} catch (Exception ex) {
			return valor.toString();
		}
	}
}
