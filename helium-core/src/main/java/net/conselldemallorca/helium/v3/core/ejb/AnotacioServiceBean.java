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

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.dto.AnotacioDto;
import es.caib.helium.logic.intf.dto.AnotacioFiltreDto;
import es.caib.helium.logic.intf.dto.AnotacioListDto;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.ArxiuFirmaDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.service.AnotacioService;

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
	public AnotacioDto incorporarExpedient(Long anotacioId, Long expedientTipusId, Long expedientId, boolean associarInteressats, boolean comprovarPermis) {
		return delegate.incorporarExpedient(anotacioId, expedientTipusId, expedientId, associarInteressats, comprovarPermis);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long anotacioId) {
		delegate.delete(anotacioId);
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
}