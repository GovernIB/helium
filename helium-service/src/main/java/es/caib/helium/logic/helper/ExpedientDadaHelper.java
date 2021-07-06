/**
 * 
 */
package es.caib.helium.logic.helper;

import es.caib.helium.persist.entity.Camp;
import es.caib.helium.persist.entity.DefinicioProces;
import es.caib.helium.persist.entity.ExpedientTipus;
import es.caib.helium.persist.repository.CampRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Helper per les dades d'expedient
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExpedientDadaHelper {
	
	@Resource
	private CampRepository campRepository;

//	@Resource
//	private ExpedientTipusHelper expedientTipusHelper;
//	@Resource
//	private ExpedientHelper expedientHelper;
//	@Resource
//	private PermisosHelper permisosHelper;

	public List<Camp> findCampsDisponiblesOrdenatsPerCodi(ExpedientTipus expedientTipus, DefinicioProces definicioProces) {
		if (expedientTipus.isAmbInfoPropia()) {
			return campRepository.findByExpedientTipusOrderByCodiAsc(expedientTipus);
		} else {
			return campRepository.findByDefinicioProcesOrderByCodiAsc(definicioProces);
		}
	}

}
