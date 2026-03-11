package es.caib.helium.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import es.caib.helium.commons.dto.MunicipiDto;
import es.caib.helium.commons.dto.PaisDto;
import es.caib.helium.commons.dto.ProvinciaDto;
import es.caib.helium.commons.dto.TipusViaDto;
import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.logic.intf.service.DadesExternesService;
import es.caib.helium.service.helper.CacheHelper;


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
