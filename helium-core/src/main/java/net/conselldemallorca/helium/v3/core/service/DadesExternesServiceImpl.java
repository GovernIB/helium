package net.conselldemallorca.helium.v3.core.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.conselldemallorca.helium.core.helper.DadesExternesHelper;
import net.conselldemallorca.helium.v3.core.api.dto.ComunitatAutonomaDto;
import net.conselldemallorca.helium.v3.core.api.dto.MunicipiDto;
import net.conselldemallorca.helium.v3.core.api.dto.NivellAdministracioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaisDto;
import net.conselldemallorca.helium.v3.core.api.dto.ProvinciaDto;
import net.conselldemallorca.helium.v3.core.api.service.DadesExternesService;


/**
 * Implementació del servei de gestió de dades externes
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DadesExternesServiceImpl implements DadesExternesService {

	@Autowired
	private DadesExternesHelper dadesExternesHelper;
	
	
	@Override
	public List<PaisDto> findPaisos() {
		return dadesExternesHelper.dadesExternesPaisosFindAll();
	}

	@Override
	public List<ProvinciaDto> findProvincies() {
		return dadesExternesHelper.dadesExternesProvinciesFindAll();
	}

	@Override
	public List<ComunitatAutonomaDto> findComunitats() {
//		return dadesExternesHelper.dadesExternesComunitatsFindAll();
		return null;

	}

	@Override
	public List<ProvinciaDto> findProvinciesPerComunitat(String comunitatCodi) {
		return dadesExternesHelper.dadesExternesProvinciesFindAmbComunitat(comunitatCodi);
	}

	@Override
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi) {
		return dadesExternesHelper.dadesExternesMunicipisFindAmbProvincia(provinciaCodi);
	}

	@Override
	public List<NivellAdministracioDto> findNivellAdministracions() {
		return dadesExternesHelper.dadesExternesNivellsAdministracioAll();
	}

	@Override
	public List<MunicipiDto> findMunicipisPerProvinciaPinbal(String provinciaCodi) {
		// TODO Auto-generated method stub
		return null;
	}

}
