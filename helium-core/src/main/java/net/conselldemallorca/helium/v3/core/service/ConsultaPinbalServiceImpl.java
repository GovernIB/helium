package net.conselldemallorca.helium.v3.core.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.EntornHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PeticioPinbalFiltreDto;
import net.conselldemallorca.helium.v3.core.api.service.ConsultaPinbalService;
import net.conselldemallorca.helium.v3.core.repository.PeticioPinbalRepository;

@Service
public class ConsultaPinbalServiceImpl implements ConsultaPinbalService {

	@Resource private PeticioPinbalRepository peticioPinbalRepository;
	@Resource private ConversioTipusHelper conversioTipusHelper;
	@Resource private EntornHelper entornHelper;
	@Resource private PaginacioHelper paginacioHelper;
	
	@Override
	@Transactional(readOnly=true)
	public PaginaDto<PeticioPinbalDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams, PeticioPinbalFiltreDto filtreDto) {
		// Comprova l'accés a l'entorn
		Entorn entorn = entornHelper.getEntornComprovantPermisos(filtreDto.getEntornId(), true);
		if (entorn!=null) {
			return paginacioHelper.toPaginaDto(peticioPinbalRepository.findByFiltrePaginat(
					filtreDto.getEntornId() == null,
					filtreDto.getEntornId(),
					filtreDto.getTipusId() == null,
					filtreDto.getTipusId(),
					filtreDto.getExpedientId() == null,
					filtreDto.getExpedientId(),
					filtreDto.getNumeroExpedient() == null,
					filtreDto.getNumeroExpedient(),
					filtreDto.getProcediment() == null,
					filtreDto.getProcediment(),
					filtreDto.getUsuari() == null,
					filtreDto.getUsuari(),
					filtreDto.getEstat() == null,
					filtreDto.getEstat(),
					filtreDto.getDataPeticioIni() == null,
					filtreDto.getDataPeticioIni(),
					filtreDto.getDataPeticioFi() == null,
					filtreDto.getDataPeticioFi(),
					paginacioHelper.toSpringDataPageable(paginacioParams)), PeticioPinbalDto.class);
		} else {
			return null;
		}
	}

	@Override
	@Transactional(readOnly=true)
	public PeticioPinbalDto findById(Long peticioPinbalId) {
		return conversioTipusHelper.convertir(peticioPinbalRepository.findOne(peticioPinbalId), PeticioPinbalDto.class);
	}

	
}
