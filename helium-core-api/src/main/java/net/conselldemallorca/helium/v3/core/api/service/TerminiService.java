package net.conselldemallorca.helium.v3.core.api.service;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.FestiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiIniciatDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;

public interface TerminiService {

	public TerminiIniciatDto iniciar(Long terminiId, Long expedientId, Date data, boolean esDataFi) throws NoTrobatException;

	public void pausar(Long terminiIniciatId, Date data) throws NoTrobatException, ValidacioException;

	public void continuar(Long terminiIniciatId, Date data) throws NoTrobatException, ValidacioException;

	public void cancelar(Long terminiIniciatId, Date data) throws NoTrobatException, ValidacioException;
	
	public List<TerminiIniciatDto> findIniciatsAmbProcessInstanceId(String processInstanceId);

	public List<TerminiDto> findTerminisAmbProcessInstanceId(String processInstanceId) throws NoTrobatException;

	public TerminiIniciatDto findIniciatAmbId(Long id) throws NoTrobatException;

	public void modificar(Long terminiId, Long expedientId, Date inicio, int anys, int mesos, int dies, boolean equals);

	public List<FestiuDto> findFestiuAmbAny(int any);

	public void createFestiu(String data) throws Exception;

	public void deleteFestiu(String data) throws ValidacioException, Exception;
}
