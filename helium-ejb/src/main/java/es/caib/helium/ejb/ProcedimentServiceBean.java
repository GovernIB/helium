package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import org.springframework.beans.factory.annotation.Autowired;

import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.procediment.ProcedimentDto;
import es.caib.helium.commons.dto.procediment.ProcedimentFiltreDto;
import es.caib.helium.commons.dto.procediment.ProgresActualitzacioDto;
import es.caib.helium.logic.intf.service.ProcedimentService;


/**
 * Implementació de ProcedimentService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Stateless
//@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ProcedimentServiceBean implements ProcedimentService {
	
	@Autowired
	private ProcedimentService delegate;

	@Override
	public PaginaDto<ProcedimentDto> findAmbFiltre(ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltre(filtre, paginacioParams);
	}

	@Override
	public ProcedimentDto findByCodiSia(String codiSia) {
		return delegate.findByCodiSia(codiSia);		
	}

	@Override
	public List<ProcedimentDto> findByNomOrCodiSia(String nom) {
		return delegate.findByNomOrCodiSia(nom);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void actualitzaProcediments() {
		delegate.actualitzaProcediments();
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN"})
	public void actualitzaServeis() {
		delegate.actualitzaServeis();
	}

	@Override
	public boolean isUpdatingProcediments() {
		return delegate.isUpdatingProcediments();
	}
	
	@Override
	public boolean isUpdatingServeis() {
		return delegate.isUpdatingServeis();
	}
	
	@Override
	public ProgresActualitzacioDto getProgresActualitzacio() {
		return delegate.getProgresActualitzacio();
	}
	
	@Override
	public ProgresActualitzacioDto getProgresServisActualitzacio() {
		return delegate.getProgresServisActualitzacio();
	}
}
