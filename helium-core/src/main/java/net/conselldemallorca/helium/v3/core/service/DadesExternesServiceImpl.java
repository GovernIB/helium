package net.conselldemallorca.helium.v3.core.service;

import java.util.List;


import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.helper.DadesExternesHelper;
import net.conselldemallorca.helium.v3.core.api.dto.ComunitatAutonomaDto;
import net.conselldemallorca.helium.v3.core.api.dto.MunicipiDto;
import net.conselldemallorca.helium.v3.core.api.dto.NivellAdministracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaisDto;
import net.conselldemallorca.helium.v3.core.api.dto.ProvinciaDto;
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
	private DadesExternesHelper dadesExternesHelper;
	
	
	@Override
	public List<PaisDto> findPaisos()  throws SistemaExternException {
		return dadesExternesHelper.dadesExternesPaisosFindAll();
	}

	@Override
	public List<ProvinciaDto> findProvincies()  throws SistemaExternException {
		return dadesExternesHelper.dadesExternesProvinciesFindAll();
	}

	@Override
	public List<ComunitatAutonomaDto> findComunitats()  throws SistemaExternException {
//		return dadesExternesHelper.dadesExternesComunitatsFindAll();
		return null;

	}

	@Override
	public List<ProvinciaDto> findProvinciesPerComunitat(String comunitatCodi) throws SistemaExternException { 
		return dadesExternesHelper.dadesExternesProvinciesFindAmbComunitat(comunitatCodi);
	}

	@Override
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi)  throws SistemaExternException {
		return dadesExternesHelper.dadesExternesMunicipisFindAmbProvincia(provinciaCodi);
	}

	@Override
	public List<NivellAdministracioDto> findNivellAdministracions() throws SistemaExternException {
		return dadesExternesHelper.dadesExternesNivellsAdministracioAll();
	}

	@Override
	public List<MunicipiDto> findMunicipisPerProvinciaPinbal(String provinciaCodi) {
		// TODO Auto-generated method stub
		return null;
	}

}
