/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;

import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.exception.TerminiIniciatNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TerminiNotFoundException;


/**
 * Servei per a gestionar terminis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TerminiService {

	public TerminiIniciatDto iniciar(
			Long terminiId,
			String processInstanceId,
			Date data,
			boolean esDataFi) throws TerminiNotFoundException;
	public TerminiIniciatDto iniciar(
			Long terminiId,
			String processInstanceId,
			Date data,
			int anys,
			int mesos,
			int dies,
			boolean esDataFi) throws TerminiNotFoundException;

	public void cancelar(
			Long terminiIniciatId,
			Date data);
	public void pausar(
			Long terminiIniciatId,
			Date data);
	public void continuar(
			Long terminiIniciatId,
			Date data);

	public TerminiIniciatDto findIniciatAmbTerminiIProcessInstance(
			Long terminiId,
			String processInstanceId) throws TerminiNotFoundException;
	public TerminiIniciatDto findIniciatAmbCodiIProcessInstance(
			String codi,
			String processInstanceId) throws TerminiNotFoundException;

	public Date getDataFiTermini(
			Date inici,
			int anys,
			int mesos,
			int dies,
			boolean laborable);
	public Date getDataIniciTermini(
			Date fi,
			int anys,
			int mesos,
			int dies,
			boolean laborable);

	public void configurarIniciatAmbDadesJbpm(
			Long terminiIniciatId,
			String taskInstanceId,
			Long timerId) throws TerminiIniciatNotFoundException;

}
