package net.conselldemallorca.helium.api.service;

import net.conselldemallorca.helium.api.dto.ExpedientLogDto;
import net.conselldemallorca.helium.api.exception.HeliumJbpmException;

import java.util.List;

/**
 * @author sion
 *
 */
public interface JbpmRetroaccioApi {

	public void deleteProcessInstanceTreeLogs(String processInstanceId) throws HeliumJbpmException;

	public Long addProcessInstanceMessageLog(
			String processInstanceId,
			String message) throws HeliumJbpmException;

	public Long addTaskInstanceMessageLog(
			String taskInstanceId,
			String message) throws HeliumJbpmException;

	public void retrocedirFinsLog(
			ExpedientLogDto expedientLog,
			List<ExpedientLogDto> expedientLogs,
			boolean retrocedirPerTasques,
			Long iniciadorId) throws HeliumJbpmException;

	}
