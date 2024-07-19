package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.ComunitatAutonomaDto;
import net.conselldemallorca.helium.v3.core.api.dto.MunicipiDto;
import net.conselldemallorca.helium.v3.core.api.dto.NivellAdministracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaisDto;
import net.conselldemallorca.helium.v3.core.api.dto.ProvinciaDto;


/**
 * Declaració dels mètodes per la gestió de les dades de fonts externes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DadesExternesService {
	
	
	/**
	 * Retorna el llistat de tots els països.
	 * 
	 * @return el llistat de països.
	 */
	public List<PaisDto> findPaisos();

	/**
	 * Retorna el llistat de totes les províncies.
	 * 
	 * @return el llistat de províncies.
	 */
	public List<ProvinciaDto> findProvincies();

	/**
	 * Retorna el llistat de totes les comunitats.
	 * 
	 * @return el llistat de comunitats.
	 */
	public List<ComunitatAutonomaDto> findComunitats();
	
	/**
	 * Retorna el llistat de totes les províncies d'una comunitat.
	 * 
	 * @return el llistat de províncies.
	 */
	public List<ProvinciaDto> findProvinciesPerComunitat(String comunitatCodi);

	/**
	 * Retorna el llistat dels municipis d'una província.
	 * 
	 * @param provinciaCodi
	 *            El codi de la província.
	 * @return el llistat de municipis.
	 */
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi);

	/**
	 * Retorna el llistat dels nivells de les administracions.
	 * 
	 * @param provinciaCodi
	 *            El codi de la província.
	 * @return el llistat de nivells de administracions.
	 */
	public List<NivellAdministracioDto> findNivellAdministracions();

	public List<MunicipiDto> findMunicipisPerProvinciaPinbal(
			String provinciaCodi);

	
}
