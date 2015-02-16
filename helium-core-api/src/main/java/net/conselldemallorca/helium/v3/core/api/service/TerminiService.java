package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;

public interface TerminiService {

	public TerminiIniciatDto iniciar(Long terminiId, Long expedientId, Date data, boolean esDataFi);

	public void pausar(Long terminiIniciatId, Date data);

	public void continuar(Long terminiIniciatId, Date data);

	public void cancelar(Long terminiIniciatId, Date data);
	
	public List<TerminiIniciatDto> findIniciatsAmbProcessInstanceId(String processInstanceId);

	public void modificar(Long terminiId, Long expedientId, Date inicio, int anys, int mesos, int dies, boolean equals);
}
