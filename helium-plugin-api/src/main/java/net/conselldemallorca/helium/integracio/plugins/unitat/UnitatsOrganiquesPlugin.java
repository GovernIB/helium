package net.conselldemallorca.helium.integracio.plugins.unitat;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.dto.NivellAdministracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaDto;

/**
 * Plugin per a obtenir l'arbre d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UnitatsOrganiquesPlugin {
	
	/**
	 * Retorna la unitat organtizativa donat el pareCodi
	 * 
	 * @param pareCodi
	 *            Codi de la unitat pare.
	 * @param fechaActualizacion
	 *            Data de la darrera actualitzaciÃ³.
	 * @param fechaSincronizacion
	 *            Data de la primera sincronitzaciÃ³.
	 * @return La unitat organitzativa trobada.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public UnitatOrganitzativaDto findUnidad(
			String pareCodi, 
			Timestamp fechaActualizacion, 
			Timestamp fechaSincronizacion) throws MalformedURLException;

	/**
	 * Retorna la llista d'unitats organitzatives filles donada
	 * una unitat pare.
	 * If you put fechaActualizacion==null and fechaSincronizacion==null it returns all unitats that are now vigent (current tree)
	 * If you put fechaActualizacion!=null and fechaSincronizacion!=null it returns all the changes in unitats from the time of last syncronization (@param fechaActualizacion) to now  
	 * 
	 * @param pareCodi
	 *            Codi de la unitat pare. It doesnt have to be arrel
	 * @param fechaActualizacion
	 *            Data de la darrera actualitzaciÃ³.
	 * @param fechaSincronizacion
	 *            Data de la primera sincronitzaciÃ³.
	 * @return La llista d'unitats organitzatives.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public List<UnitatOrganitzativaDto> findAmbPare(
			String pareCodi,
			Timestamp fechaActualizacion,
			Timestamp fechaSincronizacion) throws SistemaExternException;
	
	
	/**
	 * Retorna la unitat organtizativa donat codi
	 * 
	 * @param codi
	 *            Codi de la unitat.
	 * @return La unitat organitzativa trobada.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public UnitatOrganitzativaDto unitatsOrganitzativesFindByCodi(
			String codi) throws SistemaExternException;

	
	/**
	 * Retorna la llista d'unitats organitzatives filles donat un filtre.
	 * 
	 * @param codi
	 *            Codi de la unitat.
	 * @param denominacio
	 *            Denominació de la unitat de la unitat
	 * @param nivellAdministracio
	 *            Nivell de administració de la unitat.
	 * @param comunitatAutonoma
	 *            Codi de la comunitat de la unitat.
	 * @param ambOficines
	 *            Indica si les unitats retornades tenen oficines.
	 * @param esUnitatArrel
	 *            Indica si les unitats retornades són unitats arrel.
	 * @param provincia
	 *            Codi de la provincia de la unitat.
	 * @param localitat
	 *            Codi de la localitat de la unitat.
	 *            
	 * @return La llista d'unitats organitzatives.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public List<UnitatOrganitzativaDto> cercaUnitats(
			String codi, 
			String denominacio,
			String nivellAdministracio, 
			Long comunitatAutonoma, 
			Boolean ambOficines, 
			Boolean esUnitatArrel,
			String provincia, 
			String municipi) throws SistemaExternException;
	
	/**
	 * Retorna el llistat de nivells d'administració
	 * 
	 * @return Llista de nivells d'administració
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els nivells d'administració.
	 */
	public List<NivellAdministracioDto> nivellAdministracioFindAll() throws SistemaExternException;
}
