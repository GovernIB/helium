package net.conselldemallorca.helium.integracio.plugins.unitat;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.List;

import net.conselldemallorca.helium.integracio.plugins.SistemaExternException;
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
}
