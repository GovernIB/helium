package es.caib.helium.logic.intf.service;

import java.util.List;
import java.util.Map;

import es.caib.helium.logic.intf.dto.ReproDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.ValidacioException;

/** Servei pel manteniment de repros associades als formularis d'inici dels expedients
 * o de les tasques.
 *
 */
public interface ReproService {
	
	/** Retorna les repros segons el tipus d'expedient i la tasca per a l'usuari actual.
	 * 
	 * @param expedientTipusId
	 * @param tascaCodi
	 * @return
	 */
	public List<ReproDto> findReprosByUsuariTipusExpedient(Long expedientTipusId, String tascaCodi);
	
	public ReproDto findById(Long id) throws NoTrobatException, ValidacioException;

	public Map<String,Object> findValorsById(Long id) throws NoTrobatException;
	
	public ReproDto create(Long expedientTipusId, String nom, Map<String, Object> valors) throws NoTrobatException, ValidacioException;
	
	public ReproDto createTasca(Long expedientTipusId, Long tascaId, String nom, Map<String, Object> valors) throws NoTrobatException, ValidacioException;
	
	public String deleteById(Long id) throws NoTrobatException;
}
