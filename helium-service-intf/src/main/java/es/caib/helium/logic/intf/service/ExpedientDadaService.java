/**
 * 
 */
package es.caib.helium.logic.intf.service;

import es.caib.helium.logic.intf.dto.CampAgrupacioDto;
import es.caib.helium.logic.intf.dto.ExpedientDadaDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;

import java.util.List;


/**
 * Servei per a gestionar les dades (variables) d'un expedient.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ExpedientDadaService {

	/**
	 * Crea una nova variable a dins l'expedient.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @param varCodi
	 *            Codi de la variable a crear.
	 * @param varValor
	 *            Valor de la variable a crear.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void create(
			Long expedientId,
			String processInstanceId,
			String varCodi,
			Object varValor) throws NoTrobatException, PermisDenegatException;

	/**
	 * Modifica una variable de la instància de procés.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @param varCodi
	 *            Codi de la variable a modificar.
	 * @param varValor
	 *            Valor de la variable a modificar.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void update(
			Long expedientId,
			String processInstanceId,
			String varCodi,
			Object varValor) throws Exception;

	/**
	 * Esborra una variable de la instància de procés.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @param varCodi
	 *            Codi de la variable a esborrar.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public void delete(
			Long expedientId,
			String processInstanceId,
			String varCodi) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna una variable de la instància de procés.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés.
	 * @param varCodi
	 *            Codi de la variable a esborrar.
	 * @return la informació de la variable.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientDadaDto findOnePerInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String varCodi) throws Exception;

	/**
	 * Retorna una variable de la instància de procés segons l'identificador del camp.
	 * 
	 * @param campId
	 *            Identificador de la variable..
	 * @return la informació de la variable.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public ExpedientDadaDto getDadaBuida(long campId);
	
	/**
	 * Retorna les dades d'una instància de procés.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @return la llista de dades.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<ExpedientDadaDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) throws NoTrobatException, PermisDenegatException;

	/**
	 * Retorna la llista d'agrupacions de dades d'una instància
	 * de procés.
	 * 
	 * @param expedientId
	 *            Atribut id de l'expedient.
	 * @param processInstanceId
	 *            Atribut id de la instància de procés. Si no
	 *            s'especifica s'agafa l'instància de procés arrel.
	 * @return La llista de dades.
	 * @throws NoTrobatException
	 *             Si no s'ha trobat l'element amb l'id especificat.
	 * @throws PermisDenegatException
	 *             Si no es tenen els permisos adequats.
	 */
	public List<CampAgrupacioDto> agrupacionsFindAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) throws NoTrobatException, PermisDenegatException;
}
