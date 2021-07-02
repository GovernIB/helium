/**
 * 
 */
package es.caib.helium.ejb;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.emiserv.logic.intf.exception.NoTrobatException;
import es.caib.emiserv.logic.intf.exception.PermisDenegatException;
import es.caib.helium.logic.intf.dto.ArxiuDetallDto;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.ArxiuFirmaDto;
import es.caib.helium.logic.intf.dto.DadesNotificacioDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.ExpedientDocumentDto;
import es.caib.helium.logic.intf.dto.NotificacioDto;
import es.caib.helium.logic.intf.dto.NtiEstadoElaboracionEnumDto;
import es.caib.helium.logic.intf.dto.NtiOrigenEnumDto;
import es.caib.helium.logic.intf.dto.NtiTipoDocumentalEnumDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.PortasignaturesDto;
import es.caib.helium.logic.intf.dto.RespostaValidacioSignaturaDto;
import es.caib.helium.logic.intf.service.ExpedientDocumentService;

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
	public Long create(
			Long expedientId,
			String processInstanceId,
			String documentCodi,
			Date data,
			String adjuntTitol,
			String arxiuNom,
			byte[] arxiuContingut,
			String arxiuContentType,
			boolean ambFirma,
			boolean firmaSeparada,
			byte[] firmaContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen) {
		return delegate.create(
				expedientId,
				processInstanceId,
				documentCodi,
				data,
				adjuntTitol,
				arxiuNom,
				arxiuContingut,
				arxiuContentType,
				ambFirma,
				firmaSeparada,
				firmaContingut,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				ntiIdOrigen);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void update(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			Date data,
			String adjuntTitol,
			String arxiuNom,
			byte[] arxiuContingut,
			String arxiuContentType,
			boolean ambFirma,
			boolean firmaSeparada,
			byte[] firmaContingut,
			NtiOrigenEnumDto ntiOrigen,
			NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
			NtiTipoDocumentalEnumDto ntiTipoDocumental,
			String ntiIdOrigen) {
		delegate.update(
				expedientId,
				processInstanceId,
				documentStoreId,
				data,
				adjuntTitol,
				arxiuNom,
				arxiuContingut,
				arxiuContentType,
				ambFirma,
				firmaSeparada,
				firmaContingut,
				ntiOrigen,
				ntiEstadoElaboracion,
				ntiTipoDocumental,
				ntiIdOrigen);
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
	public ExpedientDocumentDto findOneAmbInstanciaProces(
			Long expedientId,
			String processInstanceId,
			String documentCodi) throws NoTrobatException, PermisDenegatException {
		return delegate.findOneAmbInstanciaProces(
				expedientId,
				processInstanceId,
				documentCodi);
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
			Long documentId,
			String arxiuNom) {
		return delegate.isExtensioPermesaPerTasca(
				tascaId,
				documentId,
				arxiuNom);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<RespostaValidacioSignaturaDto> verificarSignatura(Long documentStoreId) {
		return delegate.verificarSignatura(documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Object findPortasignaturesInfo(Long expedientId, String processInstanceId, Long documentStoreId)
			throws NoTrobatException {
		return delegate.findPortasignaturesInfo(expedientId, processInstanceId, documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto findArxiuAmbTokenPerMostrar(
			String token) throws NoTrobatException {
		return delegate.findArxiuAmbTokenPerMostrar(token);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto findArxiuAmbTokenPerSignar(
			String token) throws NoTrobatException {
		return delegate.findArxiuAmbTokenPerSignar(token);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto findDocumentAmbId(
			Long documentStoreId) throws NoTrobatException {
		return delegate.findDocumentAmbId(documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDetallDto getArxiuDetall(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		return delegate.getArxiuDetall(
				expedientId,
				processInstanceId,
				documentStoreId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void notificacioActualitzarEstat(
			String identificador, 
			String referenciaEnviament) {
		delegate.notificacioActualitzarEstat(identificador, referenciaEnviament);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DadesNotificacioDto notificarDocument(
			Long expedientId, 
			Long documentStoreId, 
			DadesNotificacioDto dadesNotificacioDto,
			Long interessatsId,
			Long representantId) {
		return delegate.notificarDocument(expedientId, documentStoreId, dadesNotificacioDto, interessatsId, representantId);		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PaginaDto<NotificacioDto> findNotificacionsPerDatatable(
			String filtre, 
			PaginacioParamsDto paginacioParams) {
		return delegate.findNotificacionsPerDatatable(
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuFirmaDto getArxiuFirma(Long expedientId, Long documentStoreId, int firmaIndex) {
		return delegate.getArxiuFirma(expedientId, documentStoreId, firmaIndex);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void migrarArxiu(Long expedientId, Long documentStoreId) throws NoTrobatException, PermisDenegatException {
		delegate.migrarArxiu(expedientId, documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public Long guardarDocumentProces(Long expedientId, String processInstanceId, String documentCodi,
			String adjuntTitol, Date documentData, String arxiuNom, byte[] arxiuContingut, boolean isAdjunt,
			String user) {
		return delegate.guardarDocumentProces(expedientId, processInstanceId, documentCodi, adjuntTitol, documentData, arxiuNom, arxiuContingut, isAdjunt, user);
	}

}
