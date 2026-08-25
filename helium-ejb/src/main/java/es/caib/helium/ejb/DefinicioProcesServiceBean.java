package es.caib.helium.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;

import es.caib.helium.commons.dto.CampTascaDto;
import es.caib.helium.commons.dto.ConsultaDto;
import es.caib.helium.commons.dto.DefinicioProcesDto;
import es.caib.helium.commons.dto.DocumentTascaDto;
import es.caib.helium.commons.dto.FirmaTascaDto;
import es.caib.helium.commons.dto.PaginaDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.TascaDto;
import es.caib.helium.commons.dto.TerminiDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.commons.exportacio.DefinicioProcesExportacio;
import es.caib.helium.commons.exportacio.DefinicioProcesExportacioCommandDto;
import es.caib.helium.logic.intf.service.DefinicioProcesService;

/**
 * EJB que implementa la interfície del servei DefinicioProcesService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class DefinicioProcesServiceBean extends AbstractServiceEjb<DefinicioProcesService> implements DefinicioProcesService {

	@Delegate
	private DefinicioProcesService delegateService;

	protected void setDelegateService(DefinicioProcesService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findByEntornTipusIdAndJbpmKey(Long entornId, Long expedientTipusId, String jbpmKey) {
		return delegateService.findByEntornTipusIdAndJbpmKey(entornId, expedientTipusId, jbpmKey);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesDto> findSubDefinicionsProces(Long definicioProcesId) {
		return delegateService.findSubDefinicionsProces(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesDto> findAll(Long entornId, Long expedientTipusId, boolean incloureGlobals) {
		return delegateService.findAll(entornId, expedientTipusId, incloureGlobals);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findById(Long definicioProcesId) {
		return delegateService.findById(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DefinicioProcesDto> findPerDatatable(
			Long entornId,
			Long expedientTipusId,
			boolean incloureGlobals,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findPerDatatable(entornId, expedientTipusId, incloureGlobals, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String getXml(Long entornId, Long definicioProcesId) {
		return delegateService.getXml(entornId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesExportacio exportar(
			Long entornId,
			Long definicioProcesId,
			DefinicioProcesExportacioCommandDto command) {
		return delegateService.exportar(entornId, definicioProcesId, command);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto importar(
			Long entornId,
			Long expedientTipusId,
			Long definicioProcesId,
			DefinicioProcesExportacioCommandDto command,
			DefinicioProcesExportacio importacio) {
		return delegateService.importar(entornId, expedientTipusId, definicioProcesId, command, importacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long entornId, Long definicioProcesId) throws Exception {
		delegateService.delete(entornId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<TascaDto> tascaFindPerDatatable(Long entornId, Long expedientTipusId, Long definicioProcesId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.tascaFindPerDatatable(entornId, expedientTipusId, definicioProcesId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TascaDto tascaFindAmbId(Long expedientTipusId, Long tascaId) throws NoTrobatException {
		return delegateService.tascaFindAmbId(expedientTipusId, tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDto> tascaFindAll(Long definicioProcesId) {
		return delegateService.tascaFindAll(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto tascaFindDefinicioProcesDeTasca(Long tascaId) {
		return delegateService.tascaFindDefinicioProcesDeTasca(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TascaDto tascaUpdate(TascaDto tasca) throws NoTrobatException, PermisDenegatException {
		return delegateService.tascaUpdate(tasca);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampTascaDto tascaCampCreate(Long tascaId, CampTascaDto tascaCamp) throws PermisDenegatException {
		return delegateService.tascaCampCreate(tascaId, tascaCamp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampTascaDto tascaCampUpdate(CampTascaDto tascaCamp) throws NoTrobatException, PermisDenegatException {
		return delegateService.tascaCampUpdate(tascaCamp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void tascaCampDelete(Long id) throws NoTrobatException, PermisDenegatException {
		delegateService.tascaCampDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<CampTascaDto> tascaCampFindPerDatatable(Long tascaId, Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.tascaCampFindPerDatatable(tascaId, expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampTascaDto> tascaCampFindAll(Long expedientTipusId, Long tascaId) {
		return delegateService.tascaCampFindAll(expedientTipusId, tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean tascaCampMourePosicio(Long id, Long expedientTipusId, int posicio) {
		return delegateService.tascaCampMourePosicio(id, expedientTipusId, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampTascaDto tascaCampFindById(Long expedientTipusId, Long campTascaId) {
		return delegateService.tascaCampFindById(expedientTipusId, campTascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentTascaDto tascaDocumentCreate(Long tascaId, DocumentTascaDto tascaDocument)
			throws PermisDenegatException {
		return delegateService.tascaDocumentCreate(tascaId, tascaDocument);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentTascaDto tascaDocumentUpdate(DocumentTascaDto tascaDocument)
			throws NoTrobatException, PermisDenegatException {
		return delegateService.tascaDocumentUpdate(tascaDocument);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void tascaDocumentDelete(Long id) throws NoTrobatException, PermisDenegatException {
		delegateService.tascaDocumentDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DocumentTascaDto> tascaDocumentFindPerDatatable(Long tascaId, Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.tascaDocumentFindPerDatatable(tascaId, expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentTascaDto> tascaDocumentFindAll(Long expedientTipusId, Long tascaId) {
		return delegateService.tascaDocumentFindAll(expedientTipusId, tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean tascaDocumentMourePosicio(Long id, Long expedientTipusId, int posicio) {
		return delegateService.tascaDocumentMourePosicio(id, expedientTipusId, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentTascaDto tascaDocumentFindById(Long expedientTipusId, Long documentTascaId) {
		return delegateService.tascaDocumentFindById(expedientTipusId, documentTascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FirmaTascaDto tascaFirmaCreate(Long tascaId, FirmaTascaDto tascaFirma)
			throws PermisDenegatException {
		return delegateService.tascaFirmaCreate(tascaId, tascaFirma);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FirmaTascaDto tascaFirmaUpdate(FirmaTascaDto tascaFirma)
			throws NoTrobatException, PermisDenegatException {
		return delegateService.tascaFirmaUpdate(tascaFirma);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void tascaFirmaDelete(Long id) throws NoTrobatException, PermisDenegatException {
		delegateService.tascaFirmaDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<FirmaTascaDto> tascaFirmaFindPerDatatable(Long tascaId, Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegateService.tascaFirmaFindPerDatatable(tascaId, expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<FirmaTascaDto> tascaFirmaFindAll(Long expedientTipusId, Long tascaId) {
		return delegateService.tascaFirmaFindAll(expedientTipusId, tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean tascaFirmaMourePosicio(Long id, Long expedientTipusId, int posicio) {
		return delegateService.tascaFirmaMourePosicio(id, expedientTipusId, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FirmaTascaDto tascaFirmaFindAmbTascaDocument(Long tascaId, Long documentId, Long expedientTipusId) {
		return delegateService.tascaFirmaFindAmbTascaDocument(tascaId, documentId, expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FirmaTascaDto tascaFirmaFindById(Long expedientTipusId, Long campTascaId) {
		return delegateService.tascaFirmaFindById(expedientTipusId, campTascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiDto> terminiFindAll(Long definicioProcesId) throws NoTrobatException, PermisDenegatException {
		return delegateService.terminiFindAll(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findAmbIdPermisDissenyar(Long entornId, Long definicioProcesId) throws NoTrobatException {
		return delegateService.findAmbIdPermisDissenyar(entornId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findAmbIdPermisDissenyarDelegat(Long entornId, Long definicioProcesId) throws NoTrobatException {
		return delegateService.findAmbIdPermisDissenyarDelegat(entornId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> consultaFindByEntorn(Long entornId) throws NoTrobatException, PermisDenegatException {
		return delegateService.consultaFindByEntorn(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void copiarDefinicioProces(Long origenId, Long destiId) {
		delegateService.copiarDefinicioProces(origenId, destiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String consultarStartTaskName(Long definicioProcesId) {
		return delegateService.consultarStartTaskName(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void relacionarDarreresVersions(Long expedientTipusId) {
		delegateService.relacionarDarreresVersions(expedientTipusId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findAmbProcessInstanceId(String processInstanceId) {
		return delegateService.findAmbProcessInstanceId(processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findByJbpmKeyAndVersio(String defprocJbpmKey, int defprocVersio) {
		return delegateService.findByJbpmKeyAndVersio(defprocJbpmKey, defprocVersio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto update(long entornId, long definicioProcesId, String etiqueta, boolean hasStartTask) {
		return delegateService.update(entornId, definicioProcesId, etiqueta, hasStartTask);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String checkTascaInicial(long definicioProcesId) {
		return delegateService.checkTascaInicial(definicioProcesId);
	}

}
