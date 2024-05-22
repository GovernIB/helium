/**
 * 
 */
package net.conselldemallorca.helium.v3.core.service;


import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;


import es.caib.distribucio.backoffice.utils.arxiu.ArxiuPluginListener;


import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioFiltreDto;

import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;

import net.conselldemallorca.helium.v3.core.api.service.NotificacioService;


/**
 * Implementació del servei per a gestionar notificacions enviades a NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class NotificacioServiceImpl implements NotificacioService, ArxiuPluginListener {

	

	private static final Logger logger = LoggerFactory.getLogger(NotificacioServiceImpl.class);

	@Override
	public void event(String arg0, Map<String, String> arg1, boolean arg2, String arg3, Exception arg4, long arg5) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PaginaDto<NotificacioDto> findAmbFiltrePaginat(Long entornId,
			List<ExpedientTipusDto> expedientTipusDtoAccessibles, NotificacioFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> findIdsAmbFiltre(Long entornId, List<ExpedientTipusDto> expedientTipusDtoAccessibles,
			NotificacioFiltreDto filtreDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NotificacioDto findAmbId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
}