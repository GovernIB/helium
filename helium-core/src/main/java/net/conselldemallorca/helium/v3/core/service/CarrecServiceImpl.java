package net.conselldemallorca.helium.v3.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.ConversioTipusHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.CarrecJbpmId;
import net.conselldemallorca.helium.core.model.hibernate.Persona.Sexe;
import net.conselldemallorca.helium.v3.core.api.dto.CarrecJbpmIdDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.CarrecService;
import net.conselldemallorca.helium.v3.core.repository.CarrecJbpmIdRepository;

@Service("carrecServiceV3")
public class CarrecServiceImpl implements CarrecService {
	
	@Resource
	private CarrecJbpmIdRepository carrecJbpmIdRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;

	@Transactional(readOnly = true)
	public PaginaDto<CarrecJbpmIdDto> findConfigurats(PaginacioParamsDto paginacioParams) {
		String filtre = paginacioParams.getFiltre();
		logger.debug("Consultant càrrecs configurats per la datatable (filtre= " + filtre + ", paginacioParams=" + paginacioParams + ")");
		return paginacioHelper.toPaginaDto(
				carrecJbpmIdRepository.findConfigurats(
						filtre == null || "".equals(filtre),
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				CarrecJbpmIdDto.class);
	}
	
	@Override
	public PaginaDto<CarrecJbpmIdDto> findSenseConfigurar(PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant els càrrecs sense configurar");
		String filtre = paginacioParams.getFiltre();
		List<Object[]> noConfigurats = carrecJbpmIdRepository.findSenseConfigurar(
						filtre == null || "".equals(filtre),
				filtre);
		List<CarrecJbpmId> carrecs = new ArrayList<CarrecJbpmId>();
		int paginaNum = paginacioParams.getPaginaNum();
		int tamany = paginacioParams.getPaginaTamany();
		int inici = paginaNum * tamany;
		int fi = Math.min(noConfigurats.size(), inici + tamany);
		for (int foo=inici; foo < fi; foo++) {
			CarrecJbpmId carrec = new CarrecJbpmId();
			carrec.setCodi((String)noConfigurats.get(foo)[0]);
			carrec.setGrup((String)noConfigurats.get(foo)[1]);
			carrecs.add(carrec);
		}
		Page<CarrecJbpmIdDto> page = new PageImpl(carrecs, paginacioHelper.toSpringDataPageable(paginacioParams), noConfigurats.size());
		PaginaDto<CarrecJbpmIdDto> pagina = paginacioHelper.toPaginaDto(
				page,
				CarrecJbpmIdDto.class);

		return pagina;
	}

	@Override
	public CarrecJbpmIdDto findAmbId(Long id) {
		return conversioTipusHelper.convertir(carrecJbpmIdRepository.findById(id), CarrecJbpmIdDto.class);
	}

	@Override
	public CarrecJbpmIdDto findAmbCodi(String codi) {
		return conversioTipusHelper.convertir(carrecJbpmIdRepository.findByCodi(codi), CarrecJbpmIdDto.class);
	}
	
	@Override
	public CarrecJbpmIdDto findAmbCodiAndGrup(String codi, String grup) {
		return conversioTipusHelper.convertir(carrecJbpmIdRepository.findByCodiAndGrup(codi, grup), CarrecJbpmIdDto.class);
	}

	@Override
	public CarrecJbpmIdDto create(CarrecJbpmIdDto carrec) {
		logger.debug("Configurant càrrec");
		CarrecJbpmId entity = new CarrecJbpmId();
		entity.setId(carrec.getId());
		entity.setCodi(carrec.getCodi());
		entity.setGrup(carrec.getGrup());
		entity.setNomHome(carrec.getNomHome());
		entity.setNomDona(carrec.getNomDona());
		entity.setTractamentHome(carrec.getTractamentHome());
		entity.setTractamentDona(carrec.getTractamentDona());
		entity.setPersonaSexe(carrec.getPersonaSexeId() == 0 ? Sexe.SEXE_HOME : Sexe.SEXE_DONA);
		entity.setDescripcio(carrec.getDescripcio());
		return conversioTipusHelper.convertir(carrecJbpmIdRepository.save(entity), CarrecJbpmIdDto.class);
	}

	@Override
	public CarrecJbpmIdDto update(CarrecJbpmIdDto carrec) {
		CarrecJbpmId entity = carrecJbpmIdRepository.findById(carrec.getId());
		entity.setNomHome(carrec.getNomHome());
		entity.setNomDona(carrec.getNomDona());
		entity.setTractamentHome(carrec.getTractamentHome());
		entity.setTractamentDona(carrec.getTractamentDona());
		entity.setPersonaSexe(carrec.getPersonaSexeId() == 0 ? Sexe.SEXE_HOME : Sexe.SEXE_DONA);
		entity.setDescripcio(carrec.getDescripcio());
		return conversioTipusHelper.convertir(carrecJbpmIdRepository.save(entity), CarrecJbpmIdDto.class);
	}
	
	@Override
	public void delete(Long carrecId) {
		logger.debug("Esborrant càrrec (càrrecId=" + carrecId + ")");
		carrecJbpmIdRepository.delete(carrecId);
	}

	private static final Logger logger = LoggerFactory.getLogger(CarrecServiceImpl.class);
}
