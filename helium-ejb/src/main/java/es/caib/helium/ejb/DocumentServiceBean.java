package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.ArxiuDto;
import es.caib.helium.commons.dto.ArxiuFirmaValidacioDetallDto;
import es.caib.helium.commons.dto.DocumentDto;
import es.caib.helium.commons.dto.DocumentTipusFirmaEnumDto;
import es.caib.helium.commons.dto.ExpedientDocumentPinbalDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.logic.intf.service.DocumentService;

/**
 * EJB que implementa la interfície del servei DocumentService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class DocumentServiceBean extends AbstractServiceEjb<DocumentService> implements DocumentService {

	@Delegate
	DocumentService delegateService;

	protected void setDelegateService(DocumentService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DocumentDto> findPerDatatable(Long expedientTipusId, Long definicioProcesId,
			String filtre, PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.findPerDatatable(expedientTipusId, definicioProcesId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto create(
			Long expedientTipusId,
			Long definicioProcesId,
			DocumentDto document) throws PermisDenegatException {
		return delegateService.create(expedientTipusId, definicioProcesId, document);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto findAmbCodi(Long expedientTipusId, Long definicioProcesId, String codi, boolean herencia) {
		return delegateService.findAmbCodi(expedientTipusId, definicioProcesId, codi, herencia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentDto> findAll(Long expedientTipusId, Long definicioProcesId) {
		return delegateService.findAll(expedientTipusId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long documentId) throws NoTrobatException, PermisDenegatException {
		delegateService.delete(documentId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto findAmbId(Long expedientTipusId, Long documentId) throws NoTrobatException {
		return delegateService.findAmbId(expedientTipusId, documentId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto update(
			DocumentDto document,
			boolean actualitzarContingut)
			throws NoTrobatException, PermisDenegatException {
		return delegateService.update(
				document,
				actualitzarContingut);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getArxiu(Long documentId) throws NoTrobatException {
		return delegateService.getArxiu(documentId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String createDocumentPinbal(ExpedientDocumentPinbalDto expedientDocumentPinbalDto) {
		return delegateService.createDocumentPinbal(expedientDocumentPinbalDto);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuFirmaValidacioDetallDto validateFirmaDocument(String documentNom, byte[] documentContingut, String contentType, DocumentTipusFirmaEnumDto tipusFirma,
			byte[] firmaContingut) throws Exception {
		return delegateService.validateFirmaDocument(documentNom, documentContingut, contentType, tipusFirma, firmaContingut);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public byte[] removeSignaturesPdf(byte[] arxiuContingut) {
		return delegateService.removeSignaturesPdf(arxiuContingut);
	}

}
