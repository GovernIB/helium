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

import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ConsultaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DefinicioProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.FirmaTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TerminiDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacio;
import net.conselldemallorca.helium.v3.core.api.exportacio.DefinicioProcesExportacioCommandDto;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;

/**
 * EJB que implementa la interfície del servei DefinicioProcesService.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DefinicioProcesBean implements DefinicioProcesService {

	@Autowired
	DefinicioProcesService delegate;

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findByEntornTipusIdAndJbpmKey(Long entornId, Long expedientTipusId, String jbpmKey) {
		return delegate.findByEntornTipusIdAndJbpmKey(entornId, expedientTipusId, jbpmKey);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesDto> findSubDefinicionsProces(Long definicioProcesId) {
		return findSubDefinicionsProces(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesDto> findAll(Long entornId, Long expedientTipusId, boolean incloureGlobals) {
		return delegate.findAll(entornId, expedientTipusId, incloureGlobals);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findById(Long definicioProcesId) {
		return delegate.findById(definicioProcesId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DefinicioProcesDto> findPerDatatable(
			Long entornId, 			
			Long expedientTipusId, 
			boolean incloureGlobals,
			String filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findPerDatatable(entornId, expedientTipusId, incloureGlobals, filtre, paginacioParams);
	}	

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesExportacio exportar(
			Long entornId, 
			Long definicioProcesId,
			DefinicioProcesExportacioCommandDto command) {
		return delegate.exportar(entornId, definicioProcesId, command);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto importar(
			Long entornId, 
			Long expedientTipusId,
			Long definicioProcesId,
			DefinicioProcesExportacioCommandDto command, 
			DefinicioProcesExportacio importacio) {
		return delegate.importar(entornId, expedientTipusId, definicioProcesId, command, importacio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(Long entornId, Long definicioProcesId) throws Exception {
		delegate.delete(entornId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<TascaDto> tascaFindPerDatatable(Long entornId, Long expedientTipusId, Long definicioProcesId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.tascaFindPerDatatable(entornId, expedientTipusId, definicioProcesId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TascaDto tascaFindAmbId(Long expedientTipusId, Long tascaId) throws NoTrobatException {
		return delegate.tascaFindAmbId(expedientTipusId, tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TascaDto> tascaFindAll(Long definicioProcesId) {
		return delegate.tascaFindAll(definicioProcesId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto tascaFindDefinicioProcesDeTasca(Long tascaId) {
		return delegate.tascaFindDefinicioProcesDeTasca(tascaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TascaDto tascaUpdate(TascaDto tasca) throws NoTrobatException, PermisDenegatException {
		return delegate.tascaUpdate(tasca);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampTascaDto tascaCampCreate(Long tascaId, CampTascaDto tascaCamp) throws PermisDenegatException {
		return delegate.tascaCampCreate(tascaId, tascaCamp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampTascaDto tascaCampUpdate(CampTascaDto tascaCamp) throws NoTrobatException, PermisDenegatException {
		return delegate.tascaCampUpdate(tascaCamp);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void tascaCampDelete(Long id) throws NoTrobatException, PermisDenegatException {
		delegate.tascaCampDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<CampTascaDto> tascaCampFindPerDatatable(Long tascaId, Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.tascaCampFindPerDatatable(tascaId, expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampTascaDto> tascaCampFindAll(Long expedientTipusId, Long tascaId) {
		return delegate.tascaCampFindAll(expedientTipusId, tascaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean tascaCampMourePosicio(Long id, Long expedientTipusId, int posicio) {
		return delegate.tascaCampMourePosicio(id, expedientTipusId, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampTascaDto tascaCampFindById(Long expedientTipusId, Long campTascaId) {
		return delegate.tascaCampFindById(expedientTipusId, campTascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentTascaDto tascaDocumentCreate(Long tascaId, DocumentTascaDto tascaDocument)
			throws PermisDenegatException {
		return delegate.tascaDocumentCreate(tascaId, tascaDocument);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentTascaDto tascaDocumentUpdate(DocumentTascaDto tascaDocument)
			throws NoTrobatException, PermisDenegatException {
		return delegate.tascaDocumentUpdate(tascaDocument);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void tascaDocumentDelete(Long id) throws NoTrobatException, PermisDenegatException {
		delegate.tascaDocumentDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<DocumentTascaDto> tascaDocumentFindPerDatatable(Long tascaId, Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.tascaDocumentFindPerDatatable(tascaId, expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentTascaDto> tascaDocumentFindAll(Long expedientTipusId, Long tascaId) {
		return delegate.tascaDocumentFindAll(expedientTipusId, tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean tascaDocumentMourePosicio(Long id, Long expedientTipusId, int posicio) {
		return delegate.tascaDocumentMourePosicio(id, expedientTipusId, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentTascaDto tascaDocumentFindById(Long expedientTipusId, Long documentTascaId) {
		return delegate.tascaDocumentFindById(expedientTipusId, documentTascaId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FirmaTascaDto tascaFirmaCreate(Long tascaId, FirmaTascaDto tascaFirma)
			throws PermisDenegatException {
		return delegate.tascaFirmaCreate(tascaId, tascaFirma);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FirmaTascaDto tascaFirmaUpdate(FirmaTascaDto tascaFirma)
			throws NoTrobatException, PermisDenegatException {
		return delegate.tascaFirmaUpdate(tascaFirma);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void tascaFirmaDelete(Long id) throws NoTrobatException, PermisDenegatException {
		delegate.tascaFirmaDelete(id);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<FirmaTascaDto> tascaFirmaFindPerDatatable(Long tascaId, Long expedientTipusId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.tascaFirmaFindPerDatatable(tascaId, expedientTipusId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<FirmaTascaDto> tascaFirmaFindAll(Long expedientTipusId, Long tascaId) {
		return delegate.tascaFirmaFindAll(expedientTipusId, tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean tascaFirmaMourePosicio(Long id, Long expedientTipusId, int posicio) {
		return delegate.tascaFirmaMourePosicio(id, expedientTipusId, posicio);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FirmaTascaDto tascaFirmaFindAmbTascaDocument(Long tascaId, Long documentId, Long expedientTipusId) {
		return delegate.tascaFirmaFindAmbTascaDocument(tascaId, documentId, expedientTipusId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FirmaTascaDto tascaFirmaFindById(Long expedientTipusId, Long campTascaId) {
		return delegate.tascaFirmaFindById(expedientTipusId, campTascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiDto> terminiFindAll(Long definicioProcesId) throws NoTrobatException, PermisDenegatException {
		return delegate.terminiFindAll(definicioProcesId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findAmbIdPermisDissenyar(Long entornId, Long definicioProcesId) throws NoTrobatException {
		return delegate.findAmbIdPermisDissenyar(entornId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findAmbIdPermisDissenyarDelegat(Long entornId, Long definicioProcesId) throws NoTrobatException {
		return delegate.findAmbIdPermisDissenyarDelegat(entornId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> consultaFindByEntorn(Long entornId) throws NoTrobatException, PermisDenegatException {
		return delegate.consultaFindByEntorn(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void copiarDefinicioProces(Long origenId, Long destiId) {
		delegate.copiarDefinicioProces(origenId, destiId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String consultarStartTaskName(Long definicioProcesId) {
		return delegate.consultarStartTaskName(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void relacionarDarreresVersions(Long expedientTipusId) {
		delegate.relacionarDarreresVersions(expedientTipusId);
	}
}
