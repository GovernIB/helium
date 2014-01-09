/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;
import java.util.Map;

import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.SeleccioOpcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.exception.CampNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TascaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.TaskInstanceNotFoundException;

/**
 * Servei per a enllaçar les llibreries jBPM 3 amb la funcionalitat
 * de Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface TascaService {

	public static final String VAR_PREFIX = "H3l1um#";

	public static final String VAR_TASCA_VALIDADA = "H3l1um#tasca.validada";
	public static final String VAR_TASCA_DELEGACIO = "H3l1um#tasca.delegacio";

	public static final String DEFAULT_SECRET_KEY = "H3l1umKy";
	public static final String DEFAULT_ENCRYPTION_SCHEME = "DES/ECB/PKCS5Padding";
	public static final String DEFAULT_KEY_ALGORITHM = "DES";

	public static final String TASKDESC_CAMP_AGAFADA = "agafada";

	/**
	 * Retorna les dades d'una instància de tasca.
	 * 
	 * @param tascaId
	 * @return
	 * @throws TaskInstanceNotFoundException
	 */
	public List<TascaDadaDto> findDadesPerTasca(
			String tascaId) throws TascaNotFoundException;

	/**
	 * Consulta els possibles valors per a un camp de tipus selecció
	 * del formulari de la tasca.
	 * 
	 * @param tascaId
	 * @param campId
	 * @return
	 * @throws TaskInstanceNotFoundException
	 * @throws CampNotFoundException
	 */
	public List<SeleccioOpcioDto> findOpcionsSeleccioPerCampTasca(
			String tascaId,
			Long campId) throws TaskInstanceNotFoundException, CampNotFoundException;

	public boolean isTascaValidada(Object task);
	
	public boolean isDocumentsComplet(Object task);
	
	public boolean isSignaturesComplet(Object task);
	
	public Object getVariable(
			Long entornId,
			String taskId,
			String codiVariable);

	public ExpedientTascaDto getById(
			Long entornId,
			String taskId,
			String usuari,
			Map<String, Object> valorsCommand,
			boolean ambVariables,
			boolean ambTexts);
	

	public ExpedientTascaDto getByIdSenseComprovacio(String taskId);
	
	public ExpedientTascaDto getByIdSenseComprovacio(String taskId, Map<String, Object> valorsCommand);
	
	public ExpedientTascaDto getByIdSenseComprovacio(String taskId, String usuari);
	
	public ExpedientTascaDto getByIdSenseComprovacio(String taskId, String usuari, Map<String, Object> valorsCommand);
	
	public ExpedientTascaDto getByIdSenseComprovacioIDades(String taskId);
	

	public ExpedientTascaDto guardarVariables(
			Long entornId,
			String taskId,
			Map<String, Object> variables,
			String usuari);

	public ExpedientTascaDto validar(
			Long entornId,
			String taskId,
			Map<String, Object> variables,
			boolean comprovarAssignacio);
	
	public ExpedientTascaDto validar(
			Long entornId,
			String taskId,
			Map<String, Object> variables,
			boolean comprovarAssignacio,
			String usuari);
	
	public ExpedientTascaDto restaurar(
			Long entornId,
			String taskId);
	
	public ExpedientTascaDto restaurar(
			Long entornId,
			String taskId,
			String user);
	
	public void completar(
			Long entornId,
			String taskId,
			boolean comprovarAssignacio,
			String usuari);
	
	public void completar(
			Long entornId,
			String taskId,
			boolean comprovarAssignacio,
			String usuari,
			String outcome);
	
	public void esborrarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			int index);
	
	public void esborrarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			int index,
			String usuari);

	public void executarAccio(
			Long entornId,
			String taskId,
			String accio);

	public void executarAccio(
			Long entornId,
			String taskId,
			String accio,
			String user);
	


	public void guardarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			Object[] valors);
	
	public void guardarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			Object[] valors,
			int index);
	
	public void guardarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			Object[] valors,
			String usuari);
	
	public void guardarRegistre(
			Long entornId,
			String taskId,
			String campCodi,
			Object[] valors,
			int index,
			String usuari);
	
	public void borrarVariables(Long entornId, String taskId, String variable, String usuari);

	public List<TascaDadaDto> findDadesPerTascaDto(ExpedientTascaDto tasca);
}
