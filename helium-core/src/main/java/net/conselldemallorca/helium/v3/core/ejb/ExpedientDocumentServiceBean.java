/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;

/**
 * EJB que implementa la interfície del servei ExpedientDocumentService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ExpedientDocumentServiceBean implements ExpedientDocumentService {

	@Autowired
	ExpedientDocumentService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void createOrUpdate(
			Long expedientId,
			String processInstanceId,
			Long documentId,
			Long documentStoreId,
			String titol,
			String arxiuNom,
			byte[] arxiuContingut,
			Date data) {
		delegate.createOrUpdate(
				expedientId,
				processInstanceId,
				documentId,
				documentStoreId,
				titol,
				arxiuNom,
				arxiuContingut,
				data);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		delegate.delete(
				expedientId,
				processInstanceId,
				documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDocumentDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		return delegate.findAmbInstanciaProces(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDocumentDto findOneAmbInstanciaProces(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		return delegate.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto arxiuFindAmbDocument(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		return delegate.arxiuFindAmbDocument(
				expedientId,
				processInstanceId,
				documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PortasignaturesDto> portasignaturesFindPendents(
			Long expedientId,
			String processInstanceId) {
		return delegate.portasignaturesFindPendents(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto generarAmbPlantilla(
			Long expedientId,
			String processInstanceId,
			String documentCodi) {
		return delegate.generarAmbPlantilla(
				expedientId,
				processInstanceId,
				documentCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isExtensioPermesa(
			Long expedientId,
			String processInstanceId,
			String documentCodi,
			String arxiuNom) {
		return delegate.isExtensioPermesa(
				expedientId,
				processInstanceId,
				documentCodi,
				arxiuNom);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto generarAmbPlantillaPerTasca(
			String tascaId,
			String documentCodi) {
		return delegate.generarAmbPlantillaPerTasca(
				tascaId,
				documentCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isExtensioPermesaPerTasca(
			String tascaId,
			String documentCodi,
			String arxiuNom) {
		return delegate.isExtensioPermesaPerTasca(
				tascaId,
				documentCodi,
				arxiuNom);
	}

}
