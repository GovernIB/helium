package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * EJB que implementa la interfície del servei DocumentService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class DocumentService extends AbstractService<es.caib.helium.logic.intf.service.DocumentService> implements es.caib.helium.logic.intf.service.DocumentService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DocumentDto> findPerDatatable(Long expedientTipusId, Long definicioProcesId,
			String filtre, PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return getDelegateService().findPerDatatable(expedientTipusId, definicioProcesId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto create(
			Long expedientTipusId,
			Long definicioProcesId,
			DocumentDto document) throws PermisDenegatException {
		return getDelegateService().create(expedientTipusId, definicioProcesId, document);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto findAmbCodi(Long expedientTipusId, Long definicioProcesId, String codi, boolean herencia) {
		return getDelegateService().findAmbCodi(expedientTipusId, definicioProcesId, codi, herencia);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentDto> findAll(Long expedientTipusId, Long definicioProcesId) {
		return getDelegateService().findAll(expedientTipusId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long documentId) throws NoTrobatException, PermisDenegatException {
		getDelegateService().delete(documentId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto findAmbId(Long expedientTipusId, Long documentId) throws NoTrobatException {
		return getDelegateService().findAmbId(expedientTipusId, documentId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto update(
			DocumentDto document,
			boolean actualitzarContingut)
			throws NoTrobatException, PermisDenegatException {
		return getDelegateService().update(
				document,
				actualitzarContingut);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto getArxiu(Long documentId) throws NoTrobatException {
		return getDelegateService().getArxiu(documentId);
	}

}
