package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentFiltreDto;

/**
 * Declaració dels mètodes per a gestionar procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcedimentService {
	
	/**
	 * @param entitatId
	 * @param filtre
	 * @param paginacioParams
	 * @return La pàgina de procediments que compleixen el filtre.
	 */
	public PaginaDto<ProcedimentDto> findAmbFiltre(
			ProcedimentFiltreDto filtre, 
			PaginacioParamsDto paginacioParams);

	/** 
	 * Mètode per actualitzar la llista de procediments disponibles.
	 * @throws Exception 
	 * 
	 */
	public void findAndUpdateProcediments() throws Exception;
	

	/** 
	 * Mètode per cercar procediments pel seu codiSia.
	 * 
	 */
	public ProcedimentDto findByCodiSia(String codiSia);
	

	/** 
	 * Mètode per cercar procediments pel seu nom.
	 * 
	 */
	public List<ProcedimentDto> findByNomOrCodiSia(String nom);

}
