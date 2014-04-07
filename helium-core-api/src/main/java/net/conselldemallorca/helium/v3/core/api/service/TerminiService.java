package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.FestiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;

public interface TerminiService {

	public TerminiIniciatDto iniciar(Long terminiId, String processInstanceId, Date data, boolean esDataFi);

	public TerminiIniciatDto iniciar(Long terminiId, String processInstanceId, Date data, int anys, int mesos, int dies, boolean esDataFi);

	public void pausar(Long terminiIniciatId, Date data);

	public void continuar(Long terminiIniciatId, Date data);

	public void cancelar(Long terminiIniciatId, Date data);

	public Date getDataFiTermini(Date inici, int anys, int mesos, int dies, boolean laborable);

	public Date getDataIniciTermini(Date fi, int anys, int mesos, int dies, boolean laborable);

	public List<TerminiIniciatDto> findIniciatsAmbProcessInstanceId(String processInstanceId);

	public List<TerminiIniciatDto> findIniciatsAmbTaskInstanceIds(String[] taskInstanceIds);

	public TerminiIniciatDto findIniciatAmbTerminiIdIProcessInstanceId(Long terminiId, String processInstanceId);

	public FestiuDto getFestiuById(Long id);

	public void configurarTerminiIniciatAmbDadesJbpm(Long terminiIniciatId, String taskInstanceId, Long timerId);

	public FestiuDto createFestiu(FestiuDto entity);

	public void deleteFestiu(Long id);

	public FestiuDto updateFestiu(FestiuDto entity);

	public FestiuDto findFestiuAmbData(Date data);

	public List<FestiuDto> findFestiuAmbAny(int any);

	public void comprovarTerminisIniciats();

	public void modificarTerminiIniciat(Long terminiIniciatId, Date dataInici, int anys, int mesos, int dies);
}
