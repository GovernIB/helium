/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.AnotacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioFiltreDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioListDto;
import net.conselldemallorca.helium.v3.core.api.dto.AnotacioMapeigResultatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.AnotacioService;

/**
 * Servei per a gestionar les enumeracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AnotacioServiceBean implements AnotacioService {

	@Autowired
	AnotacioService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<AnotacioListDto> findAmbFiltrePaginat(
			Long entornId,
			AnotacioFiltreDto filtreDto,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltrePaginat( entornId, filtreDto, paginacioParams);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<Long> findIdsAmbFiltre(Long entornId, AnotacioFiltreDto filtreDto) {
		return delegate.findIdsAmbFiltre( entornId, filtreDto);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioDto findAmbId(Long id) throws NoTrobatException {
		return delegate.findAmbId(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void rebutjar(Long anotacioId, String observacions) {
		delegate.rebutjar(anotacioId, observacions);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioDto updateExpedient(Long anotacioId, Long expedientTipusId, Long expedientId) {
		return delegate.updateExpedient(anotacioId, expedientTipusId, expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioDto incorporarReprocessarExpedient(Long anotacioId, Long expedientTipusId, Long expedientId, boolean associarInteressats, boolean comprovarPermis, boolean reprocessar) {
		return delegate.incorporarReprocessarExpedient(anotacioId, expedientTipusId, expedientId, associarInteressats, comprovarPermis, reprocessar);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long anotacioId) {
		delegate.delete(anotacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioDto reprocessar(Long anotacioId) throws Exception {
		return delegate.reprocessar(anotacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioDto marcarPendent(Long anotacioId) throws Exception {
		return delegate.marcarPendent(anotacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioDto reintentarConsulta(Long anotacioId) throws Exception {
		return delegate.reintentarConsulta(anotacioId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getAnnexContingut(Long annexId) {
		return delegate.getAnnexContingut(annexId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ArxiuFirmaDto> getAnnexFirmes(Long annexId) {
		return delegate.getAnnexFirmes(annexId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void reintentarAnnex(Long anotacioId, Long annexId) throws Exception {
		delegate.reintentarAnnex(anotacioId, annexId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void esborrarAnotacionsExpedient(Long expedientId) {
		delegate.esborrarAnotacionsExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public AnotacioMapeigResultatDto reprocessarMapeigAnotacioExpedient(Long expedientId, Long anotacioId) {
		return delegate.reprocessarMapeigAnotacioExpedient(expedientId, anotacioId);
		
	}
}