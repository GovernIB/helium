/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.conselldemallorca.helium.core.helper.MessageHelper;
import net.conselldemallorca.helium.core.helper.PaginacioHelper;
import net.conselldemallorca.helium.core.model.hibernate.Anotacio;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioListDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.service.AnotacioService;
import net.conselldemallorca.helium.v3.core.repository.AnotacioRepository;

/**
 * Implementació del servei per a gestionar anotacions de distribució.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AnotacioServiceImpl implements AnotacioService {

	@Resource
	private AnotacioRepository anotacioRepository;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private MessageHelper messageHelper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public PaginaDto<AnotacioListDto> findAmbFiltrePaginat(
			Long entornId,
			AnotacioFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		logger.debug(
				"Consultant les anotacions per datatable (" +
				"entornId=" + entornId + ", " +
				"anotacioFiltreDto=" + filtreDto + ")");
		
		Page<Anotacio> page = anotacioRepository.findAmbFiltrePaginat(
				paginacioHelper.toSpringDataPageable(paginacioParams));
		
		PaginaDto<AnotacioListDto> pagina = paginacioHelper.toPaginaDto(page, AnotacioListDto.class);

		return pagina;
	}
			
	private static final Logger logger = LoggerFactory.getLogger(AnotacioServiceImpl.class);
}