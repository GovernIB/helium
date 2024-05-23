package net.conselldemallorca.helium.v3.core.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Portasignatures;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultesPortafibFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.PortasignaturesService;
import net.conselldemallorca.helium.v3.core.repository.PortasignaturesRepository;

@Service
public class PortasignaturesServiceImpl implements PortasignaturesService {

	@Resource private PortasignaturesRepository portasignaturesRepository;
	@Resource private ConversioTipusHelper conversioTipusHelper;
	@Resource private PaginacioHelper paginacioHelper;
	@Resource private ExpedientHelper expedientHelper;
	
	@Override
	public PaginaDto<PortasignaturesDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams, ConsultesPortafibFiltreDto filtreDto) {
		return paginacioHelper.toPaginaDto(
				portasignaturesRepository.findAll(paginacioHelper.toSpringDataPageable(paginacioParams)),
						PortasignaturesDto.class);
	}

	@Override
	public PortasignaturesDto findById(Long portafirmesId) throws PermisDenegatException {
		Portasignatures ps = portasignaturesRepository.findById(portafirmesId);
		expedientHelper.getExpedientComprovantPermisos(ps.getExpedient().getId(), true, false, false, false);
		return conversioTipusHelper.convertir(ps, PortasignaturesDto.class);
	}

}
