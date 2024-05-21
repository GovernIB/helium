package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.helper.UsuariActualHelper;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
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
	@Resource private PaginacioHelper paginacioHelper;
	@Resource private UsuariActualHelper usuariActualHelper;
	
	@Override
	@Transactional(readOnly=true)
	public PaginaDto<PeticioPinbalDto> findAmbFiltrePaginat(PaginacioParamsDto paginacioParams, PeticioPinbalFiltreDto filtreDto) {
		
		List<Long> entornsPermesos = null;
		//Si el filtre ve del expedient, no filtrar per permisos de administració del entorn
		if (!filtreDto.isFromExpedient()) {
			//Si la cridada ve desde el llistat de peticions del menu Admin, sí que filtram
			if (!usuariActualHelper.isAdministrador()) {
				List<EntornDto> entornsAdminUsuari = usuariActualHelper.findEntornsActiusPermisAdmin();
				entornsPermesos = new ArrayList<Long>();
				if (entornsAdminUsuari==null || entornsAdminUsuari.size()==0) {
					entornsPermesos.add(0l);
				} else {
					for (EntornDto e: entornsAdminUsuari) {
						entornsPermesos.add(e.getId());
					}
				}
			}
		}
		
		return paginacioHelper.toPaginaDto(peticioPinbalRepository.findByFiltrePaginat(
				entornsPermesos == null,
				entornsPermesos,
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
				(paginacioParams.getFiltre() == null || "".equals(paginacioParams.getFiltre())),
				paginacioParams.getFiltre(),
				paginacioHelper.toSpringDataPageable(paginacioParams)), PeticioPinbalDto.class);

	}

	@Override
	@Transactional(readOnly=true)
	public PeticioPinbalDto findById(Long peticioPinbalId) {
		return conversioTipusHelper.convertir(peticioPinbalRepository.findOne(peticioPinbalId), PeticioPinbalDto.class);
	}

	@Override
	@Transactional(readOnly=true)
	public List<PeticioPinbalDto> findConsultesPinbalPerExpedient(Long expedientId) {
		return conversioTipusHelper.convertirList(peticioPinbalRepository.findByExpedientId(expedientId), PeticioPinbalDto.class);
	}
}