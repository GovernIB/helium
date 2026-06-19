/**
 *
 */
package es.caib.helium.ejb;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.helium.commons.dto.ArxiuDetallDto;
import es.caib.helium.commons.dto.ArxiuDto;
import es.caib.helium.commons.dto.ArxiuFirmaDto;
import es.caib.helium.commons.dto.DadesNotificacioDto;
import es.caib.helium.commons.dto.DocumentDto;
import es.caib.helium.commons.dto.DocumentInfoDto;
import es.caib.helium.commons.dto.DocumentListDto;
import es.caib.helium.commons.dto.DocumentStoreDto;
import es.caib.helium.commons.dto.ExpedientDocumentDto;
import es.caib.helium.commons.dto.ExpedientDto;
import es.caib.helium.commons.dto.ExpedientFinalitzarDto;
import es.caib.helium.commons.dto.FirmaResultatDto;
import es.caib.helium.commons.dto.NtiEstadoElaboracionEnumDto;
import es.caib.helium.commons.dto.NtiOrigenEnumDto;
import es.caib.helium.commons.dto.NtiTipoDocumentalEnumDto;
import es.caib.helium.commons.dto.PaginacioParamsDto;
import es.caib.helium.commons.dto.PersonaDto;
import es.caib.helium.commons.dto.PortafirmesSimpleTipusEnumDto;
import es.caib.helium.commons.dto.PortafirmesTipusEnumDto;
import es.caib.helium.commons.dto.PortasignaturesDto;
import es.caib.helium.commons.dto.RespostaValidacioSignaturaDto;
import es.caib.helium.commons.dto.document.DocumentDetallDto;
import es.caib.helium.commons.exception.NoTrobatException;
import es.caib.helium.commons.exception.PermisDenegatException;
import es.caib.helium.commons.exception.SistemaExternException;
import es.caib.helium.ejb.base.AbstractServiceEjb;
import es.caib.helium.logic.intf.service.ExpedientDocumentService;
import lombok.experimental.Delegate;

/**
 * EJB que implementa la interfície del servei ExpedientDocumentService.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExpedientDocumentServiceBean extends AbstractServiceEjb<ExpedientDocumentService> implements ExpedientDocumentService {

	@Delegate
	ExpedientDocumentService delegateService;

	protected void setDelegateService(ExpedientDocumentService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentStoreDto create(
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
		return delegateService.create(
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
	public DocumentStoreDto update(
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
		return delegateService.update(
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
		return delegateService.guardarDocumentProces(processInstanceId, documentCodi, data, arxiu, contingut, annexosPerNotificar);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void delete(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		delegateService.delete(
				expedientId,
				processInstanceId,
				documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<ExpedientDocumentDto> findAmbInstanciaProces(
			Long expedientId,
			String processInstanceId) {
		return delegateService.findAmbInstanciaProces(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public List<DocumentListDto> findDocumentsExpedient(Long expedientId, Long nextEstatId, Boolean tots, PaginacioParamsDto paginacioParams) throws NoTrobatException, PermisDenegatException {
        return delegateService.findDocumentsExpedient(expedientId, nextEstatId, tots, paginacioParams);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientDocumentDto findOneAmbInstanciaProces(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		return delegateService.findOneAmbInstanciaProces(
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
		return delegateService.findOneAmbInstanciaProces(
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
		return delegateService.arxiuFindAmbDocument(
				expedientId,
				processInstanceId,
				documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public ArxiuDto arxiuPdfFindAmbDocument(Long expedientId, String processInstanceId, Long documentStoreId) {
        return delegateService.arxiuPdfFindAmbDocument(expedientId, processInstanceId, documentStoreId);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto arxiuFindAmbDocumentVersio(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			String versio) throws NoTrobatException, PermisDenegatException {
		return delegateService.arxiuFindAmbDocumentVersio(
				expedientId,
				processInstanceId,
				documentStoreId,
				versio);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto arxiuFindAmbDocumentStoreId(Long documentId) throws NoTrobatException {
		return delegateService.arxiuFindAmbDocumentStoreId(documentId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto arxiuFindOriginal(
			Long expedientId,
			Long documentStoreId) throws NoTrobatException {
		return delegateService.arxiuFindOriginal(expedientId, documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<PortasignaturesDto> portasignaturesFindPendents(
			Long expedientId,
			String processInstanceId) {
		return delegateService.portasignaturesFindPendents(
				expedientId,
				processInstanceId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PortasignaturesDto getPortasignaturesByDocumentStoreId(
			String processInstanceId,
			Long documentStoreId) {
		return delegateService.getPortasignaturesByDocumentStoreId(
				processInstanceId,
				documentStoreId);
	}


	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto generarAmbPlantilla(
			Long expedientId,
			String processInstanceId,
			String documentCodi) {
		return delegateService.generarAmbPlantilla(
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
		return delegateService.isExtensioPermesa(
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
		return delegateService.generarAmbPlantillaPerTasca(
				tascaId,
				documentCodi);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean isExtensioPermesaPerTasca(
			String tascaId,
			Long documentId,
			String arxiuNom) {
		return delegateService.isExtensioPermesaPerTasca(
				tascaId,
				documentId,
				arxiuNom);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public List<RespostaValidacioSignaturaDto> verificarSignatura(Long documentStoreId) {
		return delegateService.verificarSignatura(documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public PortasignaturesDto getPortasignaturesByDocumentId(Integer documentId) {
		return delegateService.getPortasignaturesByDocumentId(documentId);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public List<PortasignaturesDto> getPortasignaturesByProcessInstanceAndDocumentStoreId(String processInstanceId, Long documentStoreId) {
        return delegateService.getPortasignaturesByProcessInstanceAndDocumentStoreId(processInstanceId, documentStoreId);
    }

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto findArxiuAmbTokenPerMostrar(
			String token) throws NoTrobatException {
		return delegateService.findArxiuAmbTokenPerMostrar(token);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDto findArxiuAmbTokenPerSignar(
			String token) throws NoTrobatException {
		return delegateService.findArxiuAmbTokenPerSignar(token);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto findDocumentAmbId(
			Long documentStoreId) throws NoTrobatException {
		return delegateService.findDocumentAmbId(documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public DocumentDetallDto getDocumentDetalls(Long expedientId, Long documentStoreId) {
        return delegateService.getDocumentDetalls(expedientId, documentStoreId);
    }

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuDetallDto getArxiuDetall(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId) {
		return delegateService.getArxiuDetall(
				expedientId,
				processInstanceId,
				documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void notificacioActualitzarEstat(
			String identificador,
			String referenciaEnviament) {
		delegateService.notificacioActualitzarEstat(identificador, referenciaEnviament);
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
		return delegateService.notificarDocument(expedientId, documentStoreId, documentsDinsZip, dadesNotificacioDto, interessatsId, representantId);
	}
	
	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ArxiuFirmaDto getArxiuFirma(Long expedientId, Long documentStoreId, int firmaIndex) {
		return delegateService.getArxiuFirma(expedientId, documentStoreId, firmaIndex);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void migrarArxiu(Long expedientId, Long documentStoreId) throws NoTrobatException, PermisDenegatException {
		delegateService.migrarArxiu(expedientId, documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void trySincronitzarArxiu(
			Long expedientId,
			Long documentStoreId) {
		delegateService.trySincronitzarArxiu(expedientId, documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public Set<Long> findIdsDocumentsByExpedient(Long expedientId) {
        return delegateService.findIdsDocumentsByExpedient(expedientId);
    }

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void processarFirmaClient(
			Long expedientId,
			String processInstanceId,
			Long documentStoreId,
			String arxiuNom,
			byte[] contingutFirmat) throws PermisDenegatException {
		delegateService.processarFirmaClient(expedientId, processInstanceId, documentStoreId, arxiuNom, contingutFirmat);
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
		delegateService.enviarPortasignatures(
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
		delegateService.portafirmesCancelar(documentId);
	}

    @Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
    public List<DocumentInfoDto> getDocumentsNoUtilitzatsPerEstats(Long expedientId) {
        return delegateService.getDocumentsNoUtilitzatsPerEstats(expedientId);
    }

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public String firmaSimpleWebStart(PersonaDto persona, ArxiuDto arxiu, String signId, String motiu, String lloc, String urlRetorn) {
		return delegateService.firmaSimpleWebStart(persona, arxiu, signId, motiu, lloc, urlRetorn);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public FirmaResultatDto firmaSimpleWebEnd(String transactionID) {
		return delegateService.firmaSimpleWebEnd(transactionID);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public ExpedientFinalitzarDto findDocumentsFinalitzar(Long expedientId) throws Exception {
		return delegateService.findDocumentsFinalitzar(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public boolean validarFinalitzaExpedient(Long expedientId) throws Exception {
		return delegateService.validarFinalitzaExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto generarIndexExpedient(Long expedientId) throws Exception {
		return delegateService.generarIndexExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto exportarEniDocumentsAmbIndex(Long expedientId) throws Exception {
		return delegateService.exportarEniDocumentsAmbIndex(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public DocumentDto exportarEniExpedient(Long expedientId) throws Exception {
		return delegateService.exportarEniExpedient(expedientId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void migrateDocument(Long expedientId, Long documentStoreId) {
		delegateService.migrateDocument(expedientId, documentStoreId);
	}

	@Override
	@RolesAllowed({"HEL_ADMIN", "HEL_USER", "TOTHOM", "tothom"})
	public void firmaServidor(String processInstanceId, Long documentStoreId, String motiu, byte[] arxiuContingut) {
		delegateService.firmaServidor(processInstanceId, documentStoreId, motiu, arxiuContingut);
	}

}