package es.caib.helium.integracio.plugins.unitat;

import java.util.List;

import es.caib.helium.integracio.plugins.SistemaExternException;

/**
 * Plugin per a obtenir l'arbre d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UnitatsOrganiquesPlugin {

	/**
	 * Retorna la llista d'unitats organitzatives filles donada
	 * una unitat pare.
	 * 
	 * @param pareCodi
	 *            Codi de la unitat pare.
	 * @return La llista d'unitats organitzatives.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public List<UnitatOrganica> findAmbPare(
			String pareCodi) throws SistemaExternException;

	/**
	 * Retorna l'unitat organitzativa donat el seu codi.
	 * 
	 * @param codi
	 *            Codi de l'unitat organitzativa.
	 * @return La unitat organitzativa.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les unitats organitzatives.
	 */
	public UnitatOrganica findAmbCodi(
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
	public List<UnitatOrganica> cercaUnitats(
			String codi, 
			String denominacio,
			Long nivellAdministracio, 
			Long comunitatAutonoma, 
			Boolean ambOficines, 
			Boolean esUnitatArrel,
			Long provincia, 
			String municipi) throws SistemaExternException;

}
