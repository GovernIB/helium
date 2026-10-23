package es.caib.helium.logic.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.helium.commons.dto.CarrecBpmIdDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.CarrecService;
import es.caib.helium.persistence.entity.CarrecBpmId;
import es.caib.helium.persistence.entity.Persona.Sexe;
import es.caib.helium.persistence.repository.CarrecBpmIdRepository;
import es.caib.helium.logic.helper.ConversioTipusHelper;
import es.caib.helium.logic.helper.PaginacioHelper;

@Service("carrecServiceV3")
public class CarrecServiceImpl implements CarrecService {

	@Resource
	private CarrecBpmIdRepository carrecBpmIdRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;

	@Transactional(readOnly = true)
	public PaginaDto<CarrecBpmIdDto> findConfigurats(PaginacioParamsDto paginacioParams) {
		String filtre = paginacioParams.getFiltre();
		logger.debug("Consultant càrrecs configurats per la datatable (filtre= " + filtre + ", paginacioParams=" + paginacioParams + ")");
		return paginacioHelper.toPaginaDto(
				carrecBpmIdRepository.findConfigurats(
						filtre == null || "".equals(filtre),
						filtre,
						paginacioHelper.toSpringDataPageable(
								paginacioParams)),
				CarrecBpmIdDto.class);
	}

	@Override
	public PaginaDto<CarrecBpmIdDto> findSenseConfigurar(PaginacioParamsDto paginacioParams) {
		logger.debug("Consultant els càrrecs sense configurar");
		String filtre = paginacioParams.getFiltre();
		List<Object[]> noConfigurats = carrecBpmIdRepository.findSenseConfigurar(
						filtre == null || "".equals(filtre),
				filtre);
		List<CarrecBpmId> carrecs = new ArrayList<CarrecBpmId>();
		int paginaNum = paginacioParams.getPaginaNum();
		int tamany = paginacioParams.getPaginaTamany();
		int inici = paginaNum * tamany;
		int fi = Math.min(noConfigurats.size(), inici + tamany);
		for (int foo=inici; foo < fi; foo++) {
			CarrecBpmId carrec = new CarrecBpmId();
			carrec.setCodi((String)noConfigurats.get(foo)[0]);
			carrec.setGrup((String)noConfigurats.get(foo)[1]);
			carrecs.add(carrec);
		}
		Page<CarrecBpmIdDto> page = new PageImpl(carrecs, paginacioHelper.toSpringDataPageable(paginacioParams), noConfigurats.size());
		PaginaDto<CarrecBpmIdDto> pagina = paginacioHelper.toPaginaDto(
				page,
				CarrecBpmIdDto.class);

		return pagina;
	}

	@Override
	public CarrecBpmIdDto findAmbId(Long id) {
		return conversioTipusHelper.convertir(carrecBpmIdRepository.findById(id), CarrecBpmIdDto.class);
	}

	@Override
	public CarrecBpmIdDto findAmbCodi(String codi) {
		return conversioTipusHelper.convertir(carrecBpmIdRepository.findByCodi(codi), CarrecBpmIdDto.class);
	}

	@Override
	public CarrecBpmIdDto findAmbCodiAndGrup(String codi, String grup) {
		return conversioTipusHelper.convertir(carrecBpmIdRepository.findByCodiAndGrup(codi, grup), CarrecBpmIdDto.class);
	}

	@Override
	public CarrecBpmIdDto create(CarrecBpmIdDto carrec) {
		logger.debug("Configurant càrrec");
		CarrecBpmId entity = new CarrecBpmId();
		entity.setId(carrec.getId());
		entity.setCodi(carrec.getCodi());
		entity.setGrup(carrec.getGrup());
		entity.setNomHome(carrec.getNomHome());
		entity.setNomDona(carrec.getNomDona());
		entity.setTractamentHome(carrec.getTractamentHome());
		entity.setTractamentDona(carrec.getTractamentDona());
		entity.setPersonaSexe(carrec.getPersonaSexeId() == 0 ? Sexe.SEXE_HOME : Sexe.SEXE_DONA);
		entity.setDescripcio(carrec.getDescripcio());
		return conversioTipusHelper.convertir(carrecBpmIdRepository.save(entity), CarrecBpmIdDto.class);
	}

	@Override
	public CarrecBpmIdDto update(CarrecBpmIdDto carrec) {
		CarrecBpmId entity = carrecBpmIdRepository.findById(carrec.getId()).orElse(null);
		entity.setNomHome(carrec.getNomHome());
		entity.setNomDona(carrec.getNomDona());
		entity.setTractamentHome(carrec.getTractamentHome());
		entity.setTractamentDona(carrec.getTractamentDona());
		entity.setPersonaSexe(carrec.getPersonaSexeId() == 0 ? Sexe.SEXE_HOME : Sexe.SEXE_DONA);
		entity.setDescripcio(carrec.getDescripcio());
		return conversioTipusHelper.convertir(carrecBpmIdRepository.save(entity), CarrecBpmIdDto.class);
	}

	@Override
	public void delete(Long carrecId) {
		logger.debug("Esborrant càrrec (càrrecId=" + carrecId + ")");
		carrecBpmIdRepository.deleteById(carrecId);
	}

	private static final Logger logger = LoggerFactory.getLogger(CarrecServiceImpl.class);
}
