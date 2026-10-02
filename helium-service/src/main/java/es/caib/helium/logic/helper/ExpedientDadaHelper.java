/**
 *
 */
package es.caib.helium.logic.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.JoinColumn;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import es.caib.helium.commons.dades.DadaTipusEnum;
import es.caib.helium.commons.dto.*;
import es.caib.helium.logic.config.ObjectArrayDeserializer;
import es.caib.helium.persistence.entity.*;
import es.caib.helium.persistence.repository.ExpedientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.helium.commons.constants.ExpedientCamps;
import es.caib.helium.commons.dades.DadesValor;
import es.caib.helium.persistence.repository.CampRepository;
import es.caib.helium.persistence.repository.ExpedientDadesRepository;

/**
 * Helper per les dades d'expedient
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExpedientDadaHelper {

	public static final String CLAU_EXPEDIENT_ID = "EXPEDIENT_ID";

	private final ExpedientDadesRepository expedientDadesRepository;
	private final CampRepository campRepository;
	private final ExpedientRepository expedientRepository;
	private final VariableHelper variableHelper;
	private final JdbcTemplate jdbcTemplate;

	// Objectes estàtics per fer la conversió JSON/Map
	private static final ObjectMapper mapper;
	private static final TypeReference<HashMap<String,DadesValor>> typeRef;
	private static final SimpleDateFormat dateFormatter;

	static {
		mapper = new ObjectMapper();
		typeRef = new TypeReference<HashMap<String,DadesValor>>() {};
		dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		mapper.setDateFormat(dateFormatter);
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Object.class, new ObjectArrayDeserializer());
		mapper.registerModule(module);

		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	/** Estableix el valor per una dada de l'expedient
	 *
	 * @param expedient Expedient
	 * @param processId Id del process instance
	 * @param taskId Id del task instance
	 * @param varCodi Codi del camp a desar
	 * @param varValor Valor de la variable a desar
	 */
	public void setDada(Expedient expedient, String processId, String taskId, String varCodi, Object varValor) {

		// Recupera les dades per l'expedient segons el context de tasca, procés i expedient
		ExpedientDades expedientDadesEntity = this.getDades(expedient, processId, taskId);
		if (expedientDadesEntity == null) {
			expedientDadesEntity = new ExpedientDades();
			expedientDadesEntity.setExpedient(expedient);
			expedientDadesEntity.setExpedientTipus(expedient.getTipus());
			expedientDadesEntity.setProcessId(processId);
			expedientDadesEntity.setTaskId(taskId);
			expedientDadesEntity.setPrincipal(taskId == null);
			expedientDadesRepository.save(expedientDadesEntity);
		}

		try {
			Map<String, DadesValor> dades;
			if (expedientDadesEntity.getDades() != null) {
				dades = dadesToMap(expedientDadesEntity.getDades());
			} else {
				dades = new HashMap<String, DadesValor>();
			}
			dades.put(varCodi, varValor != null ? new DadesValor(valorPerJson(varValor), DadaTipusEnum.getTipusByClass(varValor.getClass())) : null);
			expedientDadesEntity.setDades(mapToDades(dades));
		} catch(Exception e) {
			throw new RuntimeException("Error fixant valor json a les dades per conversió de les dades.", e);
		}
	}

	/** Estableix els valors per dades de l'expedient
	 *
	 * @param expedient Expedient
	 * @param processId Id del process instance
	 * @param taskId Id del task instance
	 * @param variablesProcessades Map de variables i valors que es volen desar
	 */
	public void setDades(Expedient expedient, String processId, String taskId, Map<String, Object> variablesProcessades) {
		// Recupera les dades per l'expedient segons el context de tasca, procés i expedient
		ExpedientDades expedientDadesEntity = this.getDades(expedient, processId, taskId);
		if (expedientDadesEntity == null) {
			expedientDadesEntity = new ExpedientDades();
			expedientDadesEntity.setExpedient(expedient);
			expedientDadesEntity.setExpedientTipus(expedient.getTipus());
			expedientDadesEntity.setProcessId(processId);
			expedientDadesEntity.setTaskId(taskId);
			expedientDadesEntity.setPrincipal(taskId == null);
			expedientDadesRepository.save(expedientDadesEntity);
		}

		try {
			Map<String, DadesValor> dades = new HashMap<String, DadesValor>();
			for(Map.Entry<String, Object> variable :  variablesProcessades.entrySet()) {
				Object varValor = variable.getValue();
				dades.put(variable.getKey(), new DadesValor(valorPerJson(varValor), DadaTipusEnum.getTipusByClass(varValor == null? String.class : varValor.getClass())));
			}
			expedientDadesEntity.setDades(mapToDades(dades));
		} catch(Exception e) {
			throw new RuntimeException("Error fixant valor json a les dades per conversió de les dades.", e);
		}
	}

	/** Obté el valor per una dada de l'expedient
	 *
	 * @param expedient Expedient
	 * @param processId Id del process instance
	 * @param taskId Id del task instance
	 * @param varCodi Codi del camp
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
				dades = dadesToMap(expedientDadesEntity.getDades());
				if (dades.containsKey(varCodi)) {
					DadesValor dada = dades.get(varCodi);
					if(dada != null)
						valor = parseData(dada.getV(), dada.getT());
				}
			}
		} catch(Exception e) {
			throw new RuntimeException("Error fixant valor json a les dades per conversió de les dades.", e);
		}
		return valor;
	}

	/** Obté tots els valors de les variables l'expedient
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
				Map<String, DadesValor> dades = dadesToMap(expedientDadesEntity.getDades());
				for (Map.Entry<String, DadesValor> dada : dades.entrySet()) {
					valors.put(dada.getKey(), parseData(dada.getValue().getV(), dada.getValue().getT()));
				}
			}
		} catch(Exception e) {
			throw new RuntimeException("Error fixant valor json a les dades per conversió de les dades.", e);
		}
		return valors;
	}

	private String mapToDades(Map<String, DadesValor> dades) throws
		JsonProcessingException {
		return mapper.writeValueAsString(dades);
	}

	private Map<String, DadesValor> dadesToMap(String dades) throws JsonProcessingException {
	    return mapper.readValue(dades, typeRef);
	}

	/** Consulta les dades segons la tasca, procés i expedient. */
	private ExpedientDades getDades(Expedient expedient, String processId, String taskId) {
		ExpedientDades expedientDades = null;
		if (taskId != null) {
			expedientDades = expedientDadesRepository.findByExpedientAndTaskId(expedient, taskId);
		} else if (processId != null) {
			expedientDades = expedientDadesRepository.findByExpedientAndProcessIdAndTaskIdIsNull(expedient, processId);
		} else {
			expedientDades = expedientDadesRepository.findByExpedientAndPrincipal(expedient, true);
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
				dades = dadesToMap(expedientDadesEntity.getDades());
				dades.remove(varCodi);
				expedientDadesEntity.setDades(mapToDades(dades));
			}
		} catch(Exception e) {
			throw new RuntimeException("Error fixant valor json a les dades per conversió de les dades.", e);
		}
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

	public Integer countDadesExpedients(
		Long entornId,
		Long expedientTipusId,
		List<Long> llistaExpedientIds,
		List<Camp> filtreCamps,
		Map<String, Object> filtre) {
		return queryCountDadesExpedients(entornId,
			expedientTipusId,
			llistaExpedientIds,
			filtreCamps,
			filtre);
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
					Camp camp = informeCamps.stream().filter(cc -> cc.getCodi()
							.replace(ExpedientCamps.EXPEDIENT_PREFIX, "")
							.equalsIgnoreCase(k)
					).findAny().orElse(null);
					DadaIndexadaDto di;
					if(camp != null) {
						di = new DadaIndexadaDto(camp.getCodi(), camp.getCodiEtiqueta());
					} else {
						di = new DadaIndexadaDto(k, k);
					}
					Object value = row.get(k);
					String indexValor = value != null? value.toString() : null;
					if (value != null &&
						camp != null &&
						camp.getTipus() == CampTipusDto.REGISTRE) {
						try {
							Object val = mapper.readValue(value.toString(), Object.class);
							Map<String, Object> indexValMap = new HashMap<String, Object>();
							indexValMap.put("v", val);
							indexValMap.put("c", camp.getRegistreMembres()
								.stream()
								.map(m -> m.getMembre().getCodiEtiqueta())
								.collect(Collectors.toList()));
							value = mapper.writeValueAsString(indexValMap);
						} catch (JsonProcessingException e) {
							log.error("Error mapegant valor del camp ", e);
						}
					}

					di.setValor(value);
					di.addValorIndex(indexValor);
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
		StringBuilder query = new StringBuilder();
		query.append(" SELECT expedient.ID as " + CLAU_EXPEDIENT_ID);

		if(informeCamps != null && !informeCamps.isEmpty()) {
			query.append(", ");
			// Afegim les columnes del select a la query
			query.append(informeCamps
				.stream()
				.map((ic) -> {
					if (ic.getCodi().startsWith(ExpedientCamps.EXPEDIENT_PREFIX)) {
						String colName = getColumnName(ic.getCodi().replace(ExpedientCamps.EXPEDIENT_PREFIX, ""), Expedient.class);
						return "expedient." + colName + " as \"" + ic.getCodi() + "\"";
					} else if(ic.getTipus() == CampTipusDto.REGISTRE || ic.isMultiple()) {
						return "JSON_QUERY(d.DADES, '$." + ic.getCodi() + ".v') as \"" + ic.getCodi() + "\"";
					} else if(
						ic.getTipus() ==  CampTipusDto.PRICE ||
						ic.getTipus() ==  CampTipusDto.INTEGER ||
						ic.getTipus() ==  CampTipusDto.FLOAT) {
						return "JSON_VALUE(d.DADES, '$." + ic.getCodi() + ".v' RETURNING NUMBER) as \"" + ic.getCodi() + "\"";
					}
					return "JSON_VALUE(d.DADES, '$." + ic.getCodi() + ".v') as \"" + ic.getCodi() + "\"";
				})
				.collect(Collectors.joining(", ")));
		}
		query.append(" FROM HEL_EXPEDIENT_DADES d, HEL_EXPEDIENT expedient ");
		query.append(" WHERE expedient.ID = d.EXPEDIENT_ID AND d.principal = 1 "); // Sempre cercam a les dades principals del expedient
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
		if(filtre != null && !filtre.isEmpty())
			query.append(buildDataFilter(args, filtre, filtreCamps));

		// ORDENACIÓ
		if(orders == null || orders.isEmpty()) {
			query.append(" ORDER BY expedient.ID DESC ");
		} else {
			query.append(" ORDER BY ")
				.append(
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
									.replaceAll("\\.valorMostrar$", "")
									.replaceAll("\\.valor$", ""),
								Expedient.class);
							return " expedient." + columnName + (o.getDireccio() == PaginacioParamsDto.OrdreDireccioDto.ASCENDENT? " ASC" : " DESC");
						}
						String camp = o.getCamp().replaceFirst("dadesExpedient\\.", "JSON_VALUE(d.DADES, '\\$\\.");
						return camp.substring(0, camp.lastIndexOf(".valor")) + ".v') " + (o.getDireccio() == PaginacioParamsDto.OrdreDireccioDto.ASCENDENT? "ASC" : "DESC");
					})
					.collect(Collectors.joining(", "))
			);
		}

		if(pageSize > 0) {
			int offset = page * pageSize;
			query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
			args.add(offset);
			args.add(pageSize);
		}

		return jdbcTemplate.queryForList(query.toString(), args.toArray());
	}

	private Integer queryCountDadesExpedients(
		Long entornId,
		Long expedientTipusId,
		List<Long> llistaExpedientIds,
		List<Camp> filtreCamps,
		Map<String, Object> filtre) {

		List<Object> args = new ArrayList<Object>();
		StringBuilder query = new StringBuilder(" SELECT COUNT(expedient.ID) as total ");

		query.append(" FROM HEL_EXPEDIENT_DADES d, HEL_EXPEDIENT expedient ");
		query.append(" WHERE expedient.ID = d.EXPEDIENT_ID AND d.principal = 1 "); // Sempre cercam a les dades principals del expedient
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
		if(filtre != null && !filtre.isEmpty())
			query.append(buildDataFilter(args, filtre, filtreCamps));
		List<java.util.Map<String, Object>> result = jdbcTemplate.queryForList(query.toString(), args.toArray());
		BigDecimal total = (BigDecimal) result.get(0).get("total");
		return total.intValue();
	}

	public Integer countValueInUse(Long expedientTipusId, String campCodi, String value) {
		List<Object> args = new ArrayList<Object>();
		StringBuilder query = new StringBuilder("SELECT COUNT(d.ID) FROM HEL_EXPEDIENT_DADES d ");

		query.append(" WHERE  d.EXPEDIENT_TIPUS_ID = ? ");
		args.add(expedientTipusId);
		query.append(" AND JSON_VALUE(d.DADES, '$.").append(campCodi).append(".v') = ?");
		args.add(value);

		return jdbcTemplate.queryForObject(query.toString(), args.toArray(), Integer.class);
	}

	public void deleteByExpedient(Long expedientId) {
		expedientDadesRepository.deleteByExpedientId(expedientId);
	}

	/**
	 * Retorna el nom de la columna del camp d'una entitat especificada
	 * @return nom de la columna o fieldName si no es possible trobar la columna
	 */
	private String getColumnName(String fieldName, Class<?> entityClass) {
		for(Field f : entityClass.getDeclaredFields()) {
			if(!f.getName().equals(fieldName))
				continue;
			if (f.isAnnotationPresent(Column.class)) {
				Column column = f.getAnnotation(Column.class);
				return column.name();
			} else if(f.isAnnotationPresent(JoinColumn.class)) {
				JoinColumn column = f.getAnnotation(JoinColumn.class);
				return column.name();
			}
			try {
				Method getter = entityClass.getMethod("get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1));
				if (getter.isAnnotationPresent(Column.class)) {
					Column column = getter.getAnnotation(Column.class);
					return column.name();
				} else if(getter.isAnnotationPresent(JoinColumn.class)) {
					JoinColumn column = getter.getAnnotation(JoinColumn.class);
					return column.name();
				}
			} catch (NoSuchMethodException e) {
				log.error(e.getMessage(), e);
			}

		}
		return fieldName;
	}

	private String buildDataFilter(List<Object> args, Map<String, Object> filtre, List<Camp> filtreCamps) {
		StringBuilder query = new StringBuilder();
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
					query
						.append(" AND ")
						.append(campNom)
						.append(" = ? ");
					args.add(filtreVal.toString());
					break;
				case TEXTAREA:
				case STRING:
					if(filtreVal instanceof Object[]) {
						List<Object> vals = Arrays
							.stream(((Object[]) filtreVal))
							.filter(Objects::nonNull)
							.collect(Collectors.toList());
						if(vals.isEmpty())
							break;

						query
							.append(" AND ")
							.append(campNom)
							.append(" in (")
							.append(vals.stream().map(o -> "?")
								.collect(Collectors.joining(", ")))
							.append(")");
						args.addAll(vals);
					} else {
						query
							.append(" AND UPPER(")
							.append(campNom)
							.append(") like UPPER(?)");
						args.add("%" + filtreVal + "%");
					}
					break;
				case DATE:
					if(filtreVal instanceof Date[]) {
						Date[] frange = (Date[])filtreVal;
						if(frange[0] != null) {
							query.append(" AND ").append(campNom).append(" >= ? ");
							args.add(dateFormatter.format(frange[0]));
						}
						if(frange[1] != null) {
							query.append(" AND ").append(campNom).append(" <= ? ");
							args.add(dateFormatter.format(frange[1]));
						}
					}
					break;
				case INTEGER:
				case FLOAT:
				case PRICE:
					if(filtreVal instanceof Object[]) {
						Object[] frange = (Object[])filtreVal;
						if(frange[0] != null) {
							query.append(" AND ").append(campNom).append(" >= ? ");
							args.add(frange[0]);
						}
						if(frange[1] != null) {
							query.append(" AND ").append(campNom).append(" <= ? ");
							args.add(frange[1]);
						}
					}
					break;
				case SELECCIO:
				case SUGGEST:
				default:
					query.append(" AND ").append(campNom).append(" = ? ");
					args.add(filtreVal);
					break;
			}
		}
		return query.toString();
	}

	private Object parseData(Object value, DadaTipusEnum tipus) {
		if(value == null) return null;
		if (tipus == DadaTipusEnum.d) {
			try {
				return dateFormatter.parse((String) value);
			} catch (ParseException e) {
				return value;
			}
		}
		if (tipus == DadaTipusEnum.p) {
			return BigDecimal.valueOf((Double) value);
		}
		return value;
	}

	private Object valorPerJson(Object valor) {
		if (valor == null) return null;
		if (valor instanceof Termini) {
			Termini term = (Termini) valor;
			return term.getAnys() + "/" + term.getMesos() + "/" + term.getDies();
		}
		return valor;
	}

}
