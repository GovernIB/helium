package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaDto;
import net.conselldemallorca.helium.v3.core.api.dto.UnitatOrganitzativaFiltreDto;
import net.conselldemallorca.helium.v3.core.api.service.UnitatOrganitzativaService;
import net.conselldemallorca.helium.v3.core.api.dto.ArbreDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;



/**
 * ImplementaciÃ³ de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class UnitatOrganitzativaServiceBean implements UnitatOrganitzativaService {

	@Autowired
	UnitatOrganitzativaService delegate;
	
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public UnitatOrganitzativaDto create(UnitatOrganitzativaDto unitatOrganitzativa) {
		return delegate.create(unitatOrganitzativa);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public UnitatOrganitzativaDto update(UnitatOrganitzativaDto unitatOrganitzativa) {
		return delegate.update(unitatOrganitzativa);
	}
	

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public UnitatOrganitzativaDto delete(Long id) {
		return delegate.delete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public UnitatOrganitzativaDto findById(Long id) {
		return delegate.findById(id);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> findByEntitat(String entitatCodi) { 
		return delegate.findByEntitat(entitatCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public UnitatOrganitzativaDto findByCodi(String unitatOrganitzativaCodi) {
		return delegate.findByCodi(unitatOrganitzativaCodi);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void synchronize(Long entitatId) {
		delegate.synchronize(entitatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArbreDto<UnitatOrganitzativaDto> findTree(Long id){
		return delegate.findTree(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> getObsoletesFromWS(Long entitatId) {
		return delegate.getObsoletesFromWS(entitatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> getVigentsFromWebService(Long entidadId) {
		return delegate.getVigentsFromWebService(entidadId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isFirstSincronization(Long entidadId) {
		return delegate.isFirstSincronization(entidadId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> predictFirstSynchronization(Long entitatId) {
		return delegate.predictFirstSynchronization(entitatId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> findByEntitatAndFiltre(String entitatCodi, String filtre, boolean ambArrel, boolean nomesAmbBusties) {
		return delegate.findByEntitatAndFiltre(entitatCodi, filtre, ambArrel, nomesAmbBusties);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> findByEntitatAndCodiUnitatSuperiorAndFiltre(String entitatCodi, String codiUnitatSuperior, String filtre, boolean ambArrel, boolean nomesAmbBusties) {
		return delegate.findByEntitatAndCodiUnitatSuperiorAndFiltre(entitatCodi, codiUnitatSuperior, filtre, ambArrel, nomesAmbBusties);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public UnitatOrganitzativaDto getLastHistoricos(UnitatOrganitzativaDto uo) {
		return delegate.getLastHistoricos(uo);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> getNewFromWS(Long entitatId) {
		return delegate.getNewFromWS(entitatId);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<UnitatOrganitzativaDto> findByCodiAndDenominacioFiltre(String text) {
		return delegate.findByCodiAndDenominacioFiltre(text);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<UnitatOrganitzativaDto> findAmbFiltrePaginat(UnitatOrganitzativaFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat(filtreDto, paginacioParams);
	}	


}
