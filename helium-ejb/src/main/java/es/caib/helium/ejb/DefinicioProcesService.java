/**
 * 
 */
package es.caib.helium.ejb;

import es.caib.helium.logic.intf.dto.CampTascaDto;
import es.caib.helium.logic.intf.dto.ConsultaDto;
import es.caib.helium.logic.intf.dto.DefinicioProcesDto;
import es.caib.helium.logic.intf.dto.DocumentTascaDto;
import es.caib.helium.logic.intf.dto.FirmaTascaDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.TascaDto;
import es.caib.helium.logic.intf.dto.TerminiDto;
import es.caib.helium.logic.intf.exception.NoTrobatException;
import es.caib.helium.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.exportacio.DefinicioProcesExportacio;
import es.caib.helium.logic.intf.exportacio.DefinicioProcesExportacioCommandDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * EJB que implementa la interfície del servei DefinicioProcesService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class DefinicioProcesService extends AbstractService<es.caib.helium.logic.intf.service.DefinicioProcesService> implements es.caib.helium.logic.intf.service.DefinicioProcesService {

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findByEntornIdAndJbpmKey(Long entornId, String jbpmKey) {
		return getDelegateService().findByEntornIdAndJbpmKey(entornId, jbpmKey);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findPreviaByEntornIdAndJbpmKey(
			Long entornId,
			String jbpmKey) {
		return getDelegateService().findPreviaByEntornIdAndJbpmKey(entornId, jbpmKey);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesDto> findSubDefinicionsProces(Long definicioProcesId) {
		return findSubDefinicionsProces(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesDto> findAll(Long entornId, Long expedientTipusId, boolean incloureGlobals) {
		return getDelegateService().findAll(entornId, expedientTipusId, incloureGlobals);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findById(Long definicioProcesId) {
		return getDelegateService().findById(definicioProcesId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DefinicioProcesDto> findPerDatatable(
			Long entornId, 			
			Long expedientTipusId, 
			boolean incloureGlobals,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().findPerDatatable(entornId, expedientTipusId, incloureGlobals, filtre, paginacioParams);
	}	

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesExportacio exportar(
			Long entornId, 
			Long definicioProcesId,
			DefinicioProcesExportacioCommandDto command) {
		return getDelegateService().exportar(entornId, definicioProcesId, command);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesDto> importar(
			Long entornId, 
			Long expedientTipusId,
			Long definicioProcesId,
			DefinicioProcesExportacioCommandDto command, 
			DefinicioProcesExportacio importacio) {
		return getDelegateService().importar(entornId, expedientTipusId, definicioProcesId, command, importacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long entornId, Long definicioProcesId) throws Exception {
		getDelegateService().delete(entornId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<TascaDto> tascaFindPerDatatable(Long entornId, Long expedientTipusId, Long definicioProcesId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return getDelegateService().tascaFindPerDatatable(entornId, expedientTipusId, definicioProcesId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TascaDto tascaFindAmbId(Long expedientTipusId, Long tascaId) throws NoTrobatException {
		return getDelegateService().tascaFindAmbId(expedientTipusId, tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDto> tascaFindAll(Long definicioProcesId) {
		return getDelegateService().tascaFindAll(definicioProcesId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto tascaFindDefinicioProcesDeTasca(Long tascaId) {
		return getDelegateService().tascaFindDefinicioProcesDeTasca(tascaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TascaDto tascaUpdate(TascaDto tasca) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().tascaUpdate(tasca);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampTascaDto tascaCampCreate(Long tascaId, CampTascaDto tascaCamp) throws PermisDenegatException {
		return getDelegateService().tascaCampCreate(tascaId, tascaCamp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampTascaDto> tascaCampsCreate(Long tascaId, List<CampTascaDto> tasquesCamp) throws PermisDenegatException {
		return getDelegateService().tascaCampsCreate(tascaId, tasquesCamp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampTascaDto tascaCampUpdate(CampTascaDto tascaCamp) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().tascaCampUpdate(tascaCamp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void tascaCampDelete(Long id) throws NoTrobatException, PermisDenegatException {
		getDelegateService().tascaCampDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<CampTascaDto> tascaCampFindPerDatatable(Long tascaId, Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return getDelegateService().tascaCampFindPerDatatable(tascaId, expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampTascaDto> tascaCampFindAll(Long expedientTipusId, Long tascaId) {
		return getDelegateService().tascaCampFindAll(expedientTipusId, tascaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean tascaCampMourePosicio(Long id, Long expedientTipusId, int posicio) {
		return getDelegateService().tascaCampMourePosicio(id, expedientTipusId, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampTascaDto tascaCampFindById(Long expedientTipusId, Long campTascaId) {
		return getDelegateService().tascaCampFindById(expedientTipusId, campTascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentTascaDto tascaDocumentCreate(Long tascaId, DocumentTascaDto tascaDocument)
			throws PermisDenegatException {
		return getDelegateService().tascaDocumentCreate(tascaId, tascaDocument);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentTascaDto tascaDocumentUpdate(DocumentTascaDto tascaDocument)
			throws NoTrobatException, PermisDenegatException {
		return getDelegateService().tascaDocumentUpdate(tascaDocument);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void tascaDocumentDelete(Long id) throws NoTrobatException, PermisDenegatException {
		getDelegateService().tascaDocumentDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DocumentTascaDto> tascaDocumentFindPerDatatable(Long tascaId, Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return getDelegateService().tascaDocumentFindPerDatatable(tascaId, expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentTascaDto> tascaDocumentFindAll(Long expedientTipusId, Long tascaId) {
		return getDelegateService().tascaDocumentFindAll(expedientTipusId, tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean tascaDocumentMourePosicio(Long id, Long expedientTipusId, int posicio) {
		return getDelegateService().tascaDocumentMourePosicio(id, expedientTipusId, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentTascaDto tascaDocumentFindById(Long expedientTipusId, Long documentTascaId) {
		return getDelegateService().tascaDocumentFindById(expedientTipusId, documentTascaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FirmaTascaDto tascaFirmaCreate(Long tascaId, FirmaTascaDto tascaFirma)
			throws PermisDenegatException {
		return getDelegateService().tascaFirmaCreate(tascaId, tascaFirma);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FirmaTascaDto tascaFirmaUpdate(FirmaTascaDto tascaFirma)
			throws NoTrobatException, PermisDenegatException {
		return getDelegateService().tascaFirmaUpdate(tascaFirma);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void tascaFirmaDelete(Long id) throws NoTrobatException, PermisDenegatException {
		getDelegateService().tascaFirmaDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<FirmaTascaDto> tascaFirmaFindPerDatatable(Long tascaId, Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return getDelegateService().tascaFirmaFindPerDatatable(tascaId, expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<FirmaTascaDto> tascaFirmaFindAll(Long expedientTipusId, Long tascaId) {
		return getDelegateService().tascaFirmaFindAll(expedientTipusId, tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean tascaFirmaMourePosicio(Long id, Long expedientTipusId, int posicio) {
		return getDelegateService().tascaFirmaMourePosicio(id, expedientTipusId, posicio);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FirmaTascaDto tascaFirmaFindAmbTascaDocument(Long tascaId, Long documentId, Long expedientTipusId) {
		return getDelegateService().tascaFirmaFindAmbTascaDocument(tascaId, documentId, expedientTipusId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FirmaTascaDto tascaFirmaFindById(Long expedientTipusId, Long campTascaId) {
		return getDelegateService().tascaFirmaFindById(expedientTipusId, campTascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiDto> terminiFindAll(Long definicioProcesId) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().terminiFindAll(definicioProcesId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findAmbIdPermisDissenyar(Long entornId, Long definicioProcesId) throws NoTrobatException {
		return getDelegateService().findAmbIdPermisDissenyar(entornId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findAmbIdPermisDissenyarDelegat(Long entornId, Long definicioProcesId) throws NoTrobatException {
		return getDelegateService().findAmbIdPermisDissenyarDelegat(entornId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> consultaFindByEntorn(Long entornId) throws NoTrobatException, PermisDenegatException {
		return getDelegateService().consultaFindByEntorn(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void copiarDefinicioProces(Long origenId, Long destiId) {
		getDelegateService().copiarDefinicioProces(origenId, destiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String consultarStartTaskName(Long definicioProcesId) {
		return getDelegateService().consultarStartTaskName(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void relacionarDarreresVersions(Long expedientTipusId) {
		getDelegateService().relacionarDarreresVersions(expedientTipusId);
	}
}