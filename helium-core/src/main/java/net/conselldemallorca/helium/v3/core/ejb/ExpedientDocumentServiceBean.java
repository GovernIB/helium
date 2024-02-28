/**
 * 
 */
package net.conselldemallorca.helium.v3.core.ejb;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import net.conselldemallorca.helium.v3.core.api.dto.DocumentInfoDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentListDto;
import net.conselldemallorca.helium.v3.core.api.dto.document.DocumentDetallDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDetallDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuFirmaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentStoreDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.FirmaResultatDto;
import net.conselldemallorca.helium.v3.core.api.dto.NotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiEstadoElaboracionEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiOrigenEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.NtiTipoDocumentalEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesSimpleTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortafirmesTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PortasignaturesDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaValidacioSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.exception.PermisDenegatException;
import net.conselldemallorca.helium.v3.core.api.exception.SistemaExternException;
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
			String ntiIdOrigen,
			List<ExpedientDocumentDto> annexosPerNotificar) {
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
				ntiIdOrigen,
				annexosPerNotificar);
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
	public Long guardarDocumentProces(String processInstanceId, String documentCodi, Date data, String arxiu,
			byte[] contingut, List<ExpedientDocumentDto> annexosPerNotificar) {
		return delegate.guardarDocumentProces(processInstanceId, documentCodi, data, arxiu, contingut, annexosPerNotificar);
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
    public List<DocumentListDto> findDocumentsExpedient(Long expedientId, Boolean tots, PaginacioParamsDto paginacioParams) throws NoTrobatException, PermisDenegatException {
        return delegate.findDocumentsExpedient(expedientId, tots, paginacioParams);
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
    public ArxiuDto arxiuPdfFindAmbDocument(Long expedientId, String processInstanceId, Long documentStoreId) {
        return delegate.arxiuPdfFindAmbDocument(expedientId, processInstanceId, documentStoreId);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto arxiuFindAmbDocumentVersio(
			Long expedientId, 
			String processInstanceId, 
			Long documentStoreId,
			String versio) throws NoTrobatException, PermisDenegatException {
		return delegate.arxiuFindAmbDocumentVersio(
				expedientId,
				processInstanceId,
				documentStoreId,
				versio);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto arxiuFindAmbDocumentStoreId(Long documentId) throws NoTrobatException {
		return delegate.arxiuFindAmbDocumentStoreId(documentId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto arxiuFindOriginal(
			Long expedientId, 
			Long documentStoreId) throws NoTrobatException {
		return delegate.arxiuFindOriginal(expedientId, documentStoreId);
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
	public PortasignaturesDto getPortasignaturesByDocumentStoreId(
			String processInstanceId, 
			Long documentStoreId) {
		return delegate.getPortasignaturesByDocumentStoreId(
				processInstanceId,
				documentStoreId);
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
	public PortasignaturesDto getPortasignaturesByDocumentId(Integer documentId) {
		return delegate.getPortasignaturesByDocumentId(documentId);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public List<PortasignaturesDto> getPortasignaturesByProcessInstanceAndDocumentStoreId(String processInstanceId, Long documentStoreId) {
        return delegate.getPortasignaturesByProcessInstanceAndDocumentStoreId(processInstanceId, documentStoreId);
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
    public DocumentDetallDto getDocumentDetalls(Long expedientId, Long documentStoreId) {
        return delegate.getDocumentDetalls(expedientId, documentStoreId);
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
			List<DocumentStoreDto> documentsDinsZip,
			DadesNotificacioDto dadesNotificacioDto,
			Long interessatsId,
			Long representantId) {
		return delegate.notificarDocument(expedientId, documentStoreId, documentsDinsZip, dadesNotificacioDto, interessatsId, representantId);		
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
    public Set<Long> findIdsDocumentsByExpedient(Long expedientId) {
        return delegate.findIdsDocumentsByExpedient(expedientId);
    }
    
    	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void processarFirmaClient(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			String arxiuNom,
			byte[] contingutFirmat) throws PermisDenegatException {
		delegate.processarFirmaClient(expedientId, processInstanceId, documentStoreId, arxiuNom, contingutFirmat);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void enviarPortasignatures(
			DocumentDto document, 
			List<DocumentDto> annexos, 
			ExpedientDto expedient, 
			String importancia, 
			Date dataLimit, 
			Long tokenId,
			Long processInstanceId, 
			String transicioOK, 
			String transicioKO, 
			PortafirmesSimpleTipusEnumDto portafirmesTipus,
			String[] responsables, 
			String portafirmesFluxId,
			PortafirmesTipusEnumDto fluxTipus) throws SistemaExternException {
		delegate.enviarPortasignatures(
				document, 
				annexos, 
				expedient, 
				importancia, 
				dataLimit, 
				tokenId, 
				processInstanceId, 
				transicioOK, 
				transicioKO,
				portafirmesTipus,
				responsables,
				portafirmesFluxId,
				fluxTipus);
		
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void portafirmesCancelar(Integer documentId) throws SistemaExternException {
		delegate.portafirmesCancelar(documentId);		
	}


    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public List<DocumentInfoDto> getDocumentsNoUtilitzatsPerEstats(Long expedientId) {
        return delegate.getDocumentsNoUtilitzatsPerEstats(expedientId);
    }

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String firmaSimpleWebStart(PersonaDto persona, ArxiuDto arxiu, String motiu, String lloc, String urlRetorn) {
		return delegate.firmaSimpleWebStart(persona, arxiu, motiu, lloc, urlRetorn);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FirmaResultatDto firmaSimpleWebEnd(String transactionID) {
		return delegate.firmaSimpleWebEnd(transactionID);
	}

}
