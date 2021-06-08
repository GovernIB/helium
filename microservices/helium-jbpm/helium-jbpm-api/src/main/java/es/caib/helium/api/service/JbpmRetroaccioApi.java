package es.caib.helium.api.service;

import es.caib.helium.api.dto.ExpedientLogDto;
import es.caib.helium.api.dto.ExpedientTascaDto;
import es.caib.helium.api.dto.InformacioRetroaccioDto;
import es.caib.helium.api.dto.InstanciaProcesDto;
import es.caib.helium.api.exception.NoTrobatException;
import es.caib.helium.api.exception.PermisDenegatException;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

/**
 * @author sion
 *
 */
public interface JbpmRetroaccioApi {

	public void deleteProcessInstanceTreeLogs(String processInstanceId);

	public Long addProcessInstanceMessageLog(
			String processInstanceId,
			String message);

	public Long addTaskInstanceMessageLog(
			String taskInstanceId,
			String message);

	public void retrocedirFinsLog(
			ExpedientLogDto expedientLog,
			List<ExpedientLogDto> expedientLogs,
			boolean retrocedirPerTasques,
			Long iniciadorId);

	}
