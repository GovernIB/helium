/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.model.hibernate.Area;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.v3.core.api.dto.AreaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CarrecDto;
import net.conselldemallorca.helium.v3.core.api.exception.AreaNotFoundException;
import net.conselldemallorca.helium.v3.core.api.exception.EntornNotFoundException;
import net.conselldemallorca.helium.v3.core.api.service.OrganitzacioService;
import net.conselldemallorca.helium.v3.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.v3.core.repository.AreaRepository;
import net.conselldemallorca.helium.v3.core.repository.CarrecRepository;
import net.conselldemallorca.helium.v3.core.repository.EntornRepository;

/**
 * Servei per gestionar l'organigrama.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service("organitzacioServiceV3")
public class OrganitzacioServiceImpl implements OrganitzacioService {

	@Resource
	private EntornRepository entornRepository;
	@Resource
	private AreaRepository areaRepository;
	@Resource
	private CarrecRepository carrecRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;



	public AreaDto getAreaAmbEntornICodi(
			Long entornId,
			String codi)
			throws EntornNotFoundException {
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		return conversioTipusHelper.convertir(
				areaRepository.findByEntornAndCodi(
						entorn,
						codi),
				AreaDto.class);
	}

	public CarrecDto getCarrecAmbEntornIAreaICodi(
			Long entornId,
			String areaCodi,
			String carrecCodi) throws EntornNotFoundException, AreaNotFoundException {
		Entorn entorn = entornRepository.findOne(entornId);
		if (entorn == null)
			throw new EntornNotFoundException();
		Area area = areaRepository.findByEntornAndCodi(entorn, areaCodi);
		if (area == null)
			throw new AreaNotFoundException();
		return conversioTipusHelper.convertir(
				carrecRepository.findByEntornAndAreaAndCodi(
						entorn,
						area,
						carrecCodi),
				CarrecDto.class);
	}

}
