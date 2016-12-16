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
import net.conselldemallorca.helium.v3.core.api.dto.DominiDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnumeracioDto;
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
	public DefinicioProcesDto findByEntornIdAndJbpmKey(Long entornId, String jbpmKey) {
		return delegate.findByEntornIdAndJbpmKey(entornId, jbpmKey);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesDto> findSubDefinicionsProces(Long definicioProcesId) {
		return findSubDefinicionsProces(definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DefinicioProcesDto> findAll(Long entornId, Long expedientTipusId) {
		return delegate.findAll(entornId, expedientTipusId);
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
	public PaginaDto<TascaDto> tascaFindPerDatatable(Long entornId, Long definicioProcesId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.tascaFindPerDatatable(entornId, definicioProcesId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public TascaDto tascaFindAmbId(Long id) throws NoTrobatException {
		return delegate.tascaFindAmbId(id);
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
	public PaginaDto<CampTascaDto> tascaCampFindPerDatatable(Long tascaId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.tascaCampFindPerDatatable(tascaId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean tascaCampMourePosicio(Long id, int posicio) {
		return delegate.tascaCampMourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<CampTascaDto> tascaCampFindCampAmbTascaId(Long tascaId) {
		return delegate.tascaCampFindCampAmbTascaId(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public CampTascaDto tascaCampFindById(Long campTascaId) {
		return delegate.tascaCampFindById(campTascaId);
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
	public PaginaDto<DocumentTascaDto> tascaDocumentFindPerDatatable(Long tascaId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.tascaDocumentFindPerDatatable(tascaId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean tascaDocumentMourePosicio(Long id, int posicio) {
		return delegate.tascaDocumentMourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DocumentTascaDto> tascaDocumentFindDocumentAmbTascaId(Long tascaId) {
		return delegate.tascaDocumentFindDocumentAmbTascaId(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentTascaDto tascaDocumentFindById(Long documentTascaId) {
		return delegate.tascaDocumentFindById(documentTascaId);
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
	public PaginaDto<FirmaTascaDto> tascaFirmaFindPerDatatable(Long tascaId, String filtre,
			PaginacioParamsDto paginacioParams) throws NoTrobatException {
		return delegate.tascaFirmaFindPerDatatable(tascaId, filtre, paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean tascaFirmaMourePosicio(Long id, int posicio) {
		return delegate.tascaFirmaMourePosicio(id, posicio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<FirmaTascaDto> tascaFirmaFindAmbTascaId(Long tascaId) {
		return delegate.tascaFirmaFindAmbTascaId(tascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FirmaTascaDto tascaFirmaFindById(Long firmaTascaId) {
		return delegate.tascaFirmaFindById(firmaTascaId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<TerminiDto> terminiFindAll(Long definicioProcesId) throws NoTrobatException, PermisDenegatException {
		return delegate.terminiFindAll(definicioProcesId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DefinicioProcesDto findAmbIdAndEntorn(Long entornId, Long definicioProcesId) throws NoTrobatException {
		return delegate.findAmbIdAndEntorn(entornId, definicioProcesId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<EnumeracioDto> enumeracioFindByEntorn(Long entornId) throws NoTrobatException, PermisDenegatException {
		return delegate.enumeracioFindByEntorn(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<DominiDto> dominiFindByEntorn(Long entornId) throws NoTrobatException, PermisDenegatException {
		return delegate.dominiFindByEntorn(entornId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ConsultaDto> consultaFindByEntorn(Long entornId) throws NoTrobatException, PermisDenegatException {
		return delegate.consultaFindByEntorn(entornId);
	}
}
