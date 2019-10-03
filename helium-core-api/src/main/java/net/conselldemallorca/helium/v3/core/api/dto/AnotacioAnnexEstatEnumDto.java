/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * Enumeració amb els possibles estats de registre annex. L'annex es crea a BBDD quan es consulta l'anotació,
 * està pendent quan s'accepta i mogut quan s'ha incorporat a l'Arxiu o a la taula de documents d'Helium.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum AnotacioAnnexEstatEnumDto {
	CREAT,  // annex created in db
	PENDENT, // pending to create document in db and move document in arxiu
	MOGUT // created in db and moved in arxiu
}
