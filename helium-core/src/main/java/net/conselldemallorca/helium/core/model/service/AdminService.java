/**
 * 
 */
package net.conselldemallorca.helium.core.model.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import net.conselldemallorca.helium.core.model.dto.IntervalEventDto;
import net.conselldemallorca.helium.core.model.dto.MesuraTemporalDto;
import net.conselldemallorca.helium.core.model.service.MesuresTemporalsHelper.IntervalEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servei per gestionar la configuració de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AdminService {

	@Resource
	private MesuresTemporalsHelper mesuresTemporalsHelper;

	public List<MesuraTemporalDto> findMesuresTemporals() {
		logger.debug("Consultant el llistat de les mesures temporals");
		List<MesuraTemporalDto> resposta = new ArrayList<MesuraTemporalDto>();
		for (String clau: mesuresTemporalsHelper.getClausMesures()) {
			MesuraTemporalDto dto = new MesuraTemporalDto();
			dto.setClau(clau);
			dto.setDarrera(mesuresTemporalsHelper.getDarreraMesura(clau));
			dto.setMitja(mesuresTemporalsHelper.getMitja(clau));
			dto.setMinima(mesuresTemporalsHelper.getMinim(clau));
			dto.setMaxima(mesuresTemporalsHelper.getMaxim(clau));
			dto.setNumMesures(mesuresTemporalsHelper.getNumMesures(clau));
			LinkedList<IntervalEventDto> intervalEvents = new LinkedList<IntervalEventDto>();
			long iniMilis = 0;
			long fiMilis = 0;
			for (IntervalEvent event : mesuresTemporalsHelper.getIntervalEvents(clau)) {
				intervalEvents.add(new IntervalEventDto(event.getDate(), event.getDuracio()));
				long milis =  event.getDate().getTime();
				if (iniMilis == 0 || iniMilis > milis) iniMilis = milis;
				if (fiMilis < milis) fiMilis = milis;
			}
			dto.setEvents(intervalEvents);
			if (dto.getNumMesures() > 1) {
				dto.setPeriode((fiMilis - iniMilis) / dto.getNumMesures());
			}
			resposta.add(dto);
		}
		return resposta;
	}

	public MesuresTemporalsHelper getMesuresTemporalsHelper() {
		return mesuresTemporalsHelper;
	}

	private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

}
