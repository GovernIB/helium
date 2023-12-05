package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProcedimentFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.procediment.ProgresActualitzacioDto;

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
	 * Mètode per cercar procediments pel seu codiSia.
	 * 
	 */
	public ProcedimentDto findByCodiSia(String codiSia);
	

	/** 
	 * Mètode per cercar procediments pel seu nom.
	 * 
	 */
	public List<ProcedimentDto> findByNomOrCodiSia(String nom);

	
	/// Mètodes per actualitzar procediments i obtenir informació de progrés
	
    /**
     * Actualitza els procediments amb la informació dels procediments actual
     * retornada pel plugin Gestor Documental Administratiu (GDA)
     *
     * @param entitat
     */
	public void actualitzaProcediments();

    /**
     * Consulta si existeix un procés en curs actualitzant els procediments.
     *
     * @return boolean Valor que indica si existeix un procés en segon pla actualitzant els procediements.
     */
	public boolean isUpdatingProcediments();

	/** Obté la informació del progrés d'actualització dels procediments.
	 * 
	 * @return Retorna un objecte amb la informació del progrés d'actualització.
	 */
	public ProgresActualitzacioDto getProgresActualitzacio();

	
}
