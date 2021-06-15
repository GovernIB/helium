package net.conselldemallorca.helium.api.service;

import net.conselldemallorca.helium.api.dto.ExpedientLogDto;

import java.util.List;

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
