package es.caib.helium.logic.intf.service;

import java.util.List;

import es.caib.helium.commons.dto.ComunitatAutonomaDto;
import es.caib.helium.commons.dto.MunicipiDto;
import es.caib.helium.commons.dto.NivellAdministracioDto;
import es.caib.helium.commons.dto.PaisDto;
import es.caib.helium.commons.dto.ProvinciaDto;
import es.caib.helium.commons.dto.TipusViaDto;
import es.caib.helium.commons.exception.SistemaExternException;


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
	public List<PaisDto> findPaisos()  throws SistemaExternException ;

	/**
	 * Retorna el llistat de totes les províncies.
	 * 
	 * @return el llistat de províncies.
	 */
	public List<ProvinciaDto> findProvincies()  throws SistemaExternException ;

	
	/**
	 * Retorna el llistat de totes les províncies d'una comunitat.
	 * 
	 * @return el llistat de províncies.
	 */
	public List<ProvinciaDto> findProvinciesPerComunitat(String comunitatCodi)  throws SistemaExternException ;

	/**
	 * Retorna el llistat dels municipis d'una província.
	 * 
	 * @param provinciaCodi
	 *            El codi de la província.
	 * @return el llistat de municipis.
	 */
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi)  throws SistemaExternException ;


	public List<MunicipiDto> findMunicipisPerProvinciaPinbal(
			String provinciaCodi);
	
	/**
	 * Retorna el llistat de tipus de via
	 * @return el llistat  de tipus de via
	 */
	public List<TipusViaDto> findTipusVia()  throws SistemaExternException ;

	
}
