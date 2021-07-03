/**
 * 
 */
package es.caib.helium.logic.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.intf.dto.ExpedientReindexacioDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.service.ExpedientReindexacioService;
import es.caib.helium.persist.entity.ExpedientReindexacio;
import es.caib.helium.persist.repository.ExpedientReindexacioRepository;
import es.caib.helium.persist.repository.ExpedientRepository;
import es.caib.helium.persist.repository.ExpedientTipusRepository;


/**
 * Implementació dels mètodes del servei ExpedientReindexacioService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExpedientReindexacioServiceImpl implements ExpedientReindexacioService {

	@Resource
	private ExpedientRepository expedientRepository;
	@Resource
	private ExpedientTipusRepository expedientTipusRepository;
	@Resource
	private ExpedientReindexacioRepository expedientReindexacioRepository;
	
	@Resource
	private ConversioTipusHelper conversioTipusHelper;


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Long consultaCountPendentsReindexacio(long expedientTipusId) {
		return expedientRepository.countPendentReindexacio(expedientTipusId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Long consultaCountErrorsReindexacio(long expedientTipusId) {
		return expedientRepository.countErrorsReindexacio(expedientTipusId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Long> consultaIdsErrorReindexació(long expedientTipusId) {
		return expedientRepository.findIdsErrorsReindexacio(expedientTipusId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Long> consultaIdsPendentReindexació(long expedientTipusId) {
		return expedientRepository.findIdsPendentsReindexacio(expedientTipusId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Long> consultaIdsExpedients(long expedientTipusId) {
		return expedientRepository.findIdsReindexacio(expedientTipusId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Long countPendentReindexacioAsincrona() {
		
		return expedientReindexacioRepository.countExpedientsPendents();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getDadesReindexacio() {
		
		Map<String, Object> dades = new HashMap<String, Object>();
		Sort sort = new Sort(Direction.ASC, "id");
		List<ExpedientReindexacio> llista = expedientReindexacioRepository.findAll(sort);
		// Total d'elements a la cua
		dades.put("cuaTotal", llista.size());
		// Expedients diferents a la cua
		dades.put("cuaExpedients", expedientReindexacioRepository.countExpedientsPendents());
		if (!llista.isEmpty()) {
			dades.put("primer", conversioTipusHelper.convertir(llista.get(0), ExpedientReindexacioDto.class));
			dades.put("darrer", conversioTipusHelper.convertir(llista.get(llista.size()-1), ExpedientReindexacioDto.class));
		} else {
			dades.put("primer", null);
			dades.put("darrer", null);			
		}
		List<ExpedientReindexacioDto> llistaDto = conversioTipusHelper.convertirList(llista, ExpedientReindexacioDto.class);
		dades.put("cuaLlista", llistaDto);
		
		return dades;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Object[]> getDades(Long entornId) {
		
		List<Object[]> dades = expedientReindexacioRepository.getDades(
				entornId == null, 
				entornId);
		// Consulta l'expedientTipusDto per la posició 0
		Long expedientTipusId;
		for (Object[] dada : dades) {
			expedientTipusId = (Long) dada[0];
			dada[0] = conversioTipusHelper.convertir(
						expedientTipusRepository.findById(expedientTipusId),
						ExpedientTipusDto.class);
		}
		return dades;
	}
	
	//private static final Logger logger = LoggerFactory.getLogger(ExpedientReindexacioServiceImpl.class);
}
