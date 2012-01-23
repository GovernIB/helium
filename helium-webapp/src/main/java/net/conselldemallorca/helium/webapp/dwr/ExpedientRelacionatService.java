package net.conselldemallorca.helium.webapp.dwr;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.service.ExpedientService;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Servei DWR per a la obtenció de candidats a expedients relacionats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExpedientRelacionatService {

	private ExpedientService expedientService;

	@Autowired
	public ExpedientRelacionatService(ExpedientService expedientService) {
		this.expedientService = expedientService;
	}

	public List<ExpedientRelacionat> findExpedientsRelacionats(Long entornId, String text) {
		List<ExpedientRelacionat> expedients = new ArrayList<ExpedientRelacionat>();
		for (ExpedientDto expedient: expedientService.findAmbEntornLikeIdentificador(entornId, text)) {
			ExpedientRelacionat erel = new ExpedientRelacionat();
			erel.setId(expedient.getId());
			erel.setIdentificador(expedient.getIdentificador());
			expedients.add(erel);
		}
		return expedients;
	}

	public class ExpedientRelacionat {
		Long id;
		String identificador;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getIdentificador() {
			return identificador;
		}
		public void setIdentificador(String identificador) {
			this.identificador = identificador;
		}
	}

}
