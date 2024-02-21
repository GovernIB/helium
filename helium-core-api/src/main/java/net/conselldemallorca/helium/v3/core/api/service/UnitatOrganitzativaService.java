package net.conselldemallorca.helium.v3.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import net.conselldemallorca.helium.v3.core.api.dto.ArbreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaFiltreDto;


/**
 * DeclaraciÃ³ dels mÃ¨todes per la gestiÃ³ d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UnitatOrganitzativaService {
	
	UnitatOrganitzativaDto create(UnitatOrganitzativaDto unitatOrganitzativa);

	UnitatOrganitzativaDto update(UnitatOrganitzativaDto avis);

	UnitatOrganitzativaDto delete(Long id);

	UnitatOrganitzativaDto findById(Long id);

//	PaginaDto<UnitatOrganitzativaDto> findPaginat(PaginacioParamsDto paginacioParams);

	/** 
	 * Retorna la llista d'unitats organitzatives paginada per la datatable.
	 * 
	 * @param filtre
	 *            Text per a filtrar els resultats.
	 * @param paginacioParams
	 *            Paràmetres per a la paginació dels resultats.
	 * @return La pàgina del llistat d'unitats.
	 */
//	public PaginaDto<UnitatOrganitzativaDto> findPerDatatable(
//			String filtre, 
//			PaginacioParamsDto paginacioParams);
	
	
	/** Mètode per consultar en el llistat les diferents unitats organitzatives */
	public PaginaDto<UnitatOrganitzativaDto> findAmbFiltrePaginat(
			UnitatOrganitzativaFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams); 
	
	/**
	 * Consulta les unitats organitzatives de l'entitat.
	 * 
	 * @param entitatCodi
	 *            Atribut codi de l'entitat a la qual pertany l'interessat.
	 * @return La llista d'unitats organitzatives.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	
	public List<UnitatOrganitzativaDto> findByEntitat(
			String entitatCodi);

	/**
	 * Consulta una unitat organitzativa donat el seu codi.
	 * 
	 * @param codi
	 *            Codi DIR3 de la unitat organitzativa.
	 * @return La unitat organitzativa trobada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb el codi especificat.
	 */
	public UnitatOrganitzativaDto findByCodi(
			String codi);

	public void synchronize(Long entitatId);

	public ArbreDto<UnitatOrganitzativaDto> findTree(Long id);

	public List<UnitatOrganitzativaDto> getObsoletesFromWS(Long entitatId);

	public List<UnitatOrganitzativaDto> getVigentsFromWebService(Long entidadId);

	public boolean isFirstSincronization(Long entidadId);

	public List<UnitatOrganitzativaDto> predictFirstSynchronization(Long entitatId);

	public List<UnitatOrganitzativaDto> findByEntitatAndFiltre(String entitatCodi, String filtre, boolean ambArrel, boolean nomesAmbBusties);

	public List<UnitatOrganitzativaDto> findByEntitatAndCodiUnitatSuperiorAndFiltre(String entitatCodi, String codiUnitatSuperior, String filtre, boolean ambArrel, boolean nomesAmbBusties);

	public UnitatOrganitzativaDto getLastHistoricos(UnitatOrganitzativaDto uo);

	public List<UnitatOrganitzativaDto> getNewFromWS(Long entitatId);	
	
	public List<UnitatOrganitzativaDto> findByCodiAndDenominacioFiltre(String text);	


}
