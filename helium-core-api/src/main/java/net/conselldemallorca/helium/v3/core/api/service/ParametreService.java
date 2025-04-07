package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.ParametreDto;


/**
 * Declaració dels mètodes per a la gestió de paràmetres
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ParametreService {
	
	/**Paràmetre true/false per propagar l'esborrat d'expedients quan s'esborri un tipus d'expedient**/
	public static final String APP_CONFIGURACIO_PROPAGAR_ESBORRAR_EXPEDIENTS = "app.configuracio.propagar.esborrar.expedients";
	/** Codi DIR3 de la UO arrel de l'arbre d'unitats. */
	public static final String APP_CONFIGURACIO_CODI_ARREL_UO = "app.net.caib.helium.unitats.organitzatives.arrel.codi";
	/** Data de la primera sincronització de les UO's. */
	public static final String APP_CONFIGURACIO_DATA_SINCRONITZACIO_UO = "app.net.caib.helium.unitats.organitzatives.data.sincronitzacio";
	/** Data de la darrera actualització de les UO's. */
	public static final String APP_CONFIGURACIO_DATA_ACTUALITZACIO_UO = "app.net.caib.helium.unitats.organitzatives.data.actualitzacio";
	/** Mida màxima d'un fitxer que es pot pujar. */
	public static final String APP_CONFIGURACIO_FITXER_MIDA_MAXIM = "app.configuracio.fitxer.mida.maxim";
	
	
	public ParametreDto create(ParametreDto parametre);

	public ParametreDto update(ParametreDto parametre);

	public ParametreDto delete(Long id);

	public ParametreDto findById(Long id);
	
	public ParametreDto findByCodi(String codi);
	
	public List<ParametreDto> findAll();

	public PaginaDto<ParametreDto> findPaginat(PaginacioParamsDto paginacioParams);

}
