/**
 *
 */
package es.caib.helium.logic.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.EntityManagerFactory;

import es.caib.helium.commons.dto.*;
import es.caib.helium.persistence.repository.ExpedientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.commons.constants.ExpedientCamps;
import es.caib.helium.commons.dades.DadesValor;
import es.caib.helium.persistence.entity.Camp;
import es.caib.helium.persistence.entity.DefinicioProces;
import es.caib.helium.persistence.entity.Expedient;
import es.caib.helium.persistence.entity.ExpedientDades;
import es.caib.helium.persistence.entity.ExpedientTipus;
import es.caib.helium.persistence.repository.CampRepository;
import es.caib.helium.persistence.repository.ExpedientDadesRepository;

/**
 * Helper per les dades d'expedient
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientDadaHelper {

	public static final String CLAU_EXPEDIENT_ID = "EXPEDIENT_ID";

	@Resource
	private ExpedientDadesRepository expedientDadesRepository;
	@Resource
	private CampRepository campRepository;
	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private ExpedientHelper expedientHelper;
	@Resource(name = "permisosHelperV3")
	private PermisosHelper permisosHelper;
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
				if (camp.getTipus().equals(CampTipusDto.SELECCIO) ||
					camp.getTipus().equals(CampTipusDto.SUGGEST)) {

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
	public List<Long> findExpedientsIdsByFiltre(
		Long entornId,
		Long expedientTipusId,
		List<Camp> filtreCamps,
		Map<String, Object> filtreValors) {
		return findExpedientsIdsByFiltre(
			entornId,
			expedientTipusId,
			filtreCamps,
			filtreValors,
			List.of(new PaginacioParamsDto.OrdreDto(
				ExpedientCamps.EXPEDIENT_CAMP_ID,
				PaginacioParamsDto.OrdreDireccioDto.DESCENDENT))
		);
	}

	public List<Long> findExpedientsIdsByFiltre(
		Long entornId,
		Long expedientTipusId,
		List<Camp> filtreCamps,
		Map<String, Object> filtreValors,
		List<PaginacioParamsDto.OrdreDto> orders) {
		List<Map<String, Object>> result = queryDadesExpedients(
			entornId,
			expedientTipusId,
			null,
			filtreCamps,
			filtreValors,
			null,
			orders,
			0,
			-1);
		return result
				.stream()
				.map(row -> ((BigDecimal)row.get(CLAU_EXPEDIENT_ID)).longValue())
				.collect(Collectors.toList());
	}

	public List<Map<String, DadaIndexadaDto>> findDadesExpedients(
		Long entornId,
		Long expedientTipusId,
		List<Long> llistaExpedientIds,
		List<Camp> filtreCamps,
		Map<String, Object> filtre,
		List<Camp> informeCamps,
		List<PaginacioParamsDto.OrdreDto> orders,
		int page,
		int pageSize) {

		List<Map<String, Object>> result = queryDadesExpedients(
															entornId,
															expedientTipusId,
															llistaExpedientIds,
															filtreCamps,
															filtre,
															informeCamps,
															orders,
															page,
															pageSize);

		List<Map<String, DadaIndexadaDto>> resposta = new ArrayList<Map<String, DadaIndexadaDto>>();
		for(Map<String, Object> row : result) {
			Expedient expedient = expedientRepository.findById(((BigDecimal)row.get(CLAU_EXPEDIENT_ID)).longValue()).orElse(null);
			if (expedient != null) {
				Map<String, DadaIndexadaDto> dadesExpedient = new HashMap<String, DadaIndexadaDto>();
				for(String k : row.keySet()) {
					Camp camp = informeCamps.stream().filter(cc -> {
						return cc.getCodi()
							.replace(ExpedientCamps.EXPEDIENT_PREFIX, "")
							.equalsIgnoreCase(k);
					}).findAny().orElse(null);
					DadaIndexadaDto di;
					if(camp != null) {
						di = new DadaIndexadaDto(camp.getCodi(), camp.getCodiEtiqueta());
					} else {
						di = new DadaIndexadaDto(k, k);
					}
					Object value = row.get(k);
					di.setValor(value);
					di.addValorIndex(value != null? value.toString() : null);
					dadesExpedient.put(di.getCampCodi(), di);
				}
				resposta.add(dadesExpedient);
			}
		}

		return resposta;
	}


	private List<Map<String, Object>> queryDadesExpedients(
		Long entornId,
		Long expedientTipusId,
		List<Long> llistaExpedientIds,
		List<Camp> filtreCamps,
		Map<String, Object> filtre,
		List<Camp> informeCamps,
		List<PaginacioParamsDto.OrdreDto> orders,
		int page,
		int pageSize) {

		List<Object> args = new ArrayList<Object>();
		StringBuilder query = new StringBuilder("SELECT * FROM ( SELECT rownum r__, t.* FROM ( ");
		query.append(" SELECT expedient.ID as " + CLAU_EXPEDIENT_ID);

		if(informeCamps != null) {
			query.append(", ");
			// Afegim les columnes del select a la query
			query.append(String.join(
				", ",
				informeCamps
					.stream()
					.map((ic) -> {
						if (ic.getCodi().startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
							String colName = getColumnName(ic.getCodi().replace(ExpedientCamps.EXPEDIENT_PREFIX, ""), Expedient.class);
							return "expedient." + colName;
						}
						return "JSON_VALUE(d.DADES, '$." + ic.getCodi() + ".v') as " + ic.getCodi();
					})
					.collect(Collectors.toList())));
		}
		query.append(" FROM HEL_EXPEDIENT_DADES d, HEL_EXPEDIENT expedient ");
		query.append(" WHERE expedient.ID = d.EXPEDIENT_ID ");
		query.append(" AND expedient.ENTORN_ID = ? ");
		args.add(entornId);

		if(expedientTipusId != null) {
			query.append(" AND expedient.TIPUS_ID = ? ");
			args.add(expedientTipusId);
		}

		if(llistaExpedientIds != null) {
			List<String> stringIds = llistaExpedientIds
				.stream()
				.map(Object::toString)
				.collect(Collectors.toList());
			query.append(" AND expedient.ID IN (").append(String.join(", ", stringIds)).append(")");
		}

		// FILTRE
		if(filtre != null && !filtre.isEmpty()) {
			buildDataFilter(query, args, filtre, filtreCamps);
		}

		// ORDENACIÓ
		if(orders == null || orders.isEmpty()) {
			query.append(" ORDER BY expedient.ID DESC ");
		} else {
			query.append(" ORDER BY " +
				String.join(", ",
					orders
						.stream()
						.map(o -> {
							if(o.getCamp().equals("expedient$identificador") || o.getCamp().equals("expedient.identificador")) {
								return " expedient.NUMERO " + (o.getDireccio() == PaginacioParamsDto.OrdreDireccioDto.ASCENDENT? "ASC" : "DESC") +
									" ,expedient.NUMERO_DEFAULT " + (o.getDireccio() == PaginacioParamsDto.OrdreDireccioDto.ASCENDENT? "ASC" : "DESC");
							}
							if(o.getCamp().replaceFirst("dadesExpedient\\.", "").startsWith(ExpedientCamps.EXPEDIENT_PREFIX)
								|| o.getCamp().startsWith("expedient.")) {
								String columnName = getColumnName(
									o.getCamp()
										.replaceFirst("dadesExpedient\\.", "")
										.replace(ExpedientCamps.EXPEDIENT_PREFIX, "")
										.replace("expedient.", "")
										.replaceAll("\\.valor$", ""),
									Expedient.class);
								return " expedient." + columnName + (o.getDireccio() == PaginacioParamsDto.OrdreDireccioDto.ASCENDENT? " ASC" : " DESC");
							}
							String camp = o.getCamp().replaceFirst("dadesExpedient\\.", "JSON_VALUE(d.DADES, '\\$\\.");
							return camp.substring(0, camp.lastIndexOf(".valor")) + ".v') " + (o.getDireccio() == PaginacioParamsDto.OrdreDireccioDto.ASCENDENT? "ASC" : "DESC");
						})
						.collect(Collectors.toList())
				)
			);
		}

		if(pageSize > 0) {
			query.append(" ) t WHERE rownum < ((? * ?) + 1 ) ");
			args.add(page+1);
			args.add(pageSize);

			query.append(" ) WHERE r__ >= (((? - 1) * ?) + 1) ");
			args.add(page+1);
			args.add(pageSize);
		} else {
			query.append(" ) t ) ");
		}

		return jdbcTemplate.queryForList(query.toString(), args.toArray());
	}

	public Integer countValueInUse(Long expedientTipusId, String campCodi, String value) {
		List<Object> args = new ArrayList<Object>();
		StringBuilder query = new StringBuilder("SELECT COUNT(d.ID) FROM HEL_EXPEDIENT_DADES d ");

		query.append(" WHERE  d.EXPEDIENT_TIPUS_ID = ? ");
		args.add(expedientTipusId);
		//query.append(" AND JSON_EXISTS(d.DADES, '$.").append(campCodi).append(".v')");
		query.append(" AND JSON_VALUE(d.DADES, '$.").append(campCodi).append(".v') = ?");
		args.add(value);

		return jdbcTemplate.queryForObject(query.toString(), args.toArray(), Integer.class);
	}

	public void deleteByExpedient(Long expedientId) {
		expedientDadesRepository.deleteByExpedientId(expedientId);
	}

	private String getComText(
			CampTipusDto tipus,
			String valor,
			String valorDomini) {
		if (valor == null)
			return null;
		try {
			String text = null;
			if (tipus.equals(CampTipusDto.INTEGER)) {
				text = new DecimalFormat("#").format(Long.valueOf(valor));
			} else if (tipus.equals(CampTipusDto.FLOAT)) {
				text = new DecimalFormat("#.##########").format(Double.valueOf(valor));
			} else if (tipus.equals(CampTipusDto.PRICE)) {
				text = new DecimalFormat("#,##0.00").format(new BigDecimal(valor));
			} else if (tipus.equals(CampTipusDto.DATE)) {
				// text = new SimpleDateFormat("dd/MM/yyyy").format((Date)valor);
				text = valor;
			} else if (tipus.equals(CampTipusDto.BOOLEAN)) {
				text = Boolean.valueOf(valor) ? "Si" : "No";
			} else if (tipus.equals(CampTipusDto.SELECCIO)) {
				text = valorDomini;
			} else if (tipus.equals(CampTipusDto.SUGGEST)) {
				text = valorDomini;
			} else if (tipus.equals(CampTipusDto.TERMINI)) {
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

	/**
	 * Retorna el nom de la columna del camp d'una entitat especificada
	 * @param fieldName
	 * @param entityClass
	 * @return nom de la columna o fieldName si no es possible trobar la columna
	 */
	private String getColumnName(String fieldName, Class<?> entityClass) {
		for(Field f : entityClass.getDeclaredFields()) {
			if(!f.getName().equals(fieldName))
				continue;
			if (f.isAnnotationPresent(Column.class)) {
				Column column = f.getAnnotation(Column.class);
				return column.name();
			}
			try {
				Method getter = entityClass.getMethod("get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1));
				if (getter.isAnnotationPresent(Column.class)) {
					Column column = getter.getAnnotation(Column.class);
					return column.name();
				}
			} catch (NoSuchMethodException e) {
				// TODO:
			}

		}
		return fieldName;
	}

	private void buildDataFilter(StringBuilder query, List<Object> args, Map<String, Object> filtre, List<Camp> filtreCamps) {
		for(Camp f : filtreCamps) {
			Object filtreVal = filtre.get(f.getCodi());

			String campNom = f.getCodi();
			if(campNom.startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
				campNom = campNom.substring(campNom.indexOf(ExpedientCamps.EXPEDIENT_PREFIX_SEPARADOR)+1);
				campNom = "expedient." + getColumnName(campNom, Expedient.class);
			} else {
				campNom = "JSON_VALUE(d.DADES, '$." + campNom + ".v')";
			}
			if(filtreVal == null)
				continue;

			switch(f.getTipus()) {
				case BOOLEAN:
					query.append(" AND " + campNom + " = ? ");
					args.add(filtreVal.toString());
					break;
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

}
