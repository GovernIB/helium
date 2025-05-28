package net.conselldemallorca.helium.v3.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTipusFirmaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;

/**
 * EJB que implementa la interfície del servei DocumentService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DocumentServiceBean implements DocumentService {

	@Autowired
	DocumentService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DocumentDto> findPerDatatable(Long expedientTipusId, Long definicioProcesId,
			String filtre, PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.findPerDatatable(expedientTipusId, definicioProcesId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto create(
			Long expedientTipusId,
			Long definicioProcesId,
			DocumentDto document) throws PermisDenegatException {
		return delegate.create(expedientTipusId, definicioProcesId, document);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto findAmbCodi(Long expedientTipusId, Long definicioProcesId, String codi, boolean herencia) {
		return delegate.findAmbCodi(expedientTipusId, definicioProcesId, codi, herencia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentDto> findAll(Long expedientTipusId, Long definicioProcesId) {
		return delegate.findAll(expedientTipusId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long documentId) throws NoTrobatException, PermisDenegatException {
		delegate.delete(documentId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto findAmbId(Long expedientTipusId, Long documentId) throws NoTrobatException {
		return delegate.findAmbId(expedientTipusId, documentId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto update(
			DocumentDto document,
			boolean actualitzarContingut)
			throws NoTrobatException, PermisDenegatException {
		return delegate.update(
				document,
				actualitzarContingut);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getArxiu(Long documentId) throws NoTrobatException {
		return delegate.getArxiu(documentId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String createDocumentPinbal(ExpedientDocumentPinbalDto expedientDocumentPinbalDto) {
		return delegate.createDocumentPinbal(expedientDocumentPinbalDto);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public int checkFirmaDocument(byte[] documentContingut, String contentType, DocumentTipusFirmaEnumDto tipusFirma,
			byte[] firmaContingut) {
		return delegate.checkFirmaDocument(documentContingut, contentType, tipusFirma, firmaContingut);
	}

}
