package net.conselldemallorca.helium.v3.core.service;

import java.util.List;


import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.helper.DadesExternesHelper;
import net.conselldemallorca.helium.core.helper.CacheHelper;
import net.conselldemallorca.helium.v3.core.api.dto.MunicipiDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaisDto;
import net.conselldemallorca.helium.v3.core.api.dto.ProvinciaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TipusViaDto;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
import net.conselldemallorca.helium.v3.core.api.service.DadesExternesService;


/**
 * Implementació del servei de gestió de dades externes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DadesExternesServiceImpl implements DadesExternesService {

	@Resource
	private CacheHelper cacheHelper;
	
	
	@Override
	public List<PaisDto> findPaisos()  throws SistemaExternException {
		return cacheHelper.findPaisos();
	}

	@Override
	public List<ProvinciaDto> findProvincies()  throws SistemaExternException {
		return cacheHelper.findProvincies();
	}

	@Override
	public List<ProvinciaDto> findProvinciesPerComunitat(String comunitatCodi) throws SistemaExternException { 
		return cacheHelper.findProvinciesPerComunitat(comunitatCodi);
	}

	@Override
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi)  throws SistemaExternException {
		return cacheHelper.findMunicipisPerProvincia(provinciaCodi);
	}

	@Override
	public List<MunicipiDto> findMunicipisPerProvinciaPinbal(String provinciaCodi) {
		return null;
	}

	@Override
	public List<TipusViaDto> findTipusVia() throws SistemaExternException {
		return cacheHelper.findTipusVia();
	}

}
