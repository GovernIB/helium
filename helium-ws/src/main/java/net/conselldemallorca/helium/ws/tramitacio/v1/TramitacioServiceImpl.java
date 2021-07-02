/**
 * 
 */
package net.conselldemallorca.helium.ws.tramitacio.v1;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.helium.logic.intf.dto.AccioDto;
import es.caib.helium.logic.intf.dto.ArxiuDto;
import es.caib.helium.logic.intf.dto.CampDto;
import es.caib.helium.logic.intf.dto.CampTipusDto;
import es.caib.helium.logic.intf.dto.DocumentDto;
import es.caib.helium.logic.intf.dto.EntornDto;
import es.caib.helium.logic.intf.dto.EstatDto;
import es.caib.helium.logic.intf.dto.ExpedientDto;
import es.caib.helium.logic.intf.dto.ExpedientTascaDto;
import es.caib.helium.logic.intf.dto.ExpedientTipusDto;
import es.caib.helium.logic.intf.dto.InstanciaProcesDto;
import es.caib.helium.logic.intf.dto.MostrarAnulatsDto;
import es.caib.helium.logic.intf.dto.PaginaDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto;
import es.caib.helium.logic.intf.dto.TascaDadaDto;
import es.caib.helium.logic.intf.dto.TascaDocumentDto;
import es.caib.helium.logic.intf.dto.ExpedientDto.EstatTipusDto;
import es.caib.helium.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.helium.logic.intf.service.DocumentService;
import es.caib.helium.logic.intf.service.EntornService;
import es.caib.helium.logic.intf.service.ExpedientDadaService;
import es.caib.helium.logic.intf.service.ExpedientDocumentService;
import es.caib.helium.logic.intf.service.ExpedientService;
import es.caib.helium.logic.intf.service.ExpedientTipusService;
import es.caib.helium.logic.intf.service.TascaService;
import es.caib.helium.logic.util.EntornActual;

/**
 * Implementació del servei de tramitació d'expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		endpointInterface = "net.conselldemallorca.helium.ws.tramitacio.v1.TramitacioService",
		targetNamespace = "http://conselldemallorca.net/helium/ws/tramitacio/v1")
public class TramitacioServiceImpl implements TramitacioService {

	private static final boolean PRINT_USUARI = false;

	@Autowired
	private EntornService entornService;
	@Autowired
	private ExpedientTipusService expedientTipusService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private ExpedientDadaService expedientDadaService;
	@Autowired
	private ExpedientDocumentService expedientDocumentService;
	@Autowired
	private TascaService tascaService;
	@Autowired
	private DocumentService documentService;

	@Override
	public String iniciExpedient(
			String entorn,
			String expedientTipus,
			String numero,
			String titol,
			List<ParellaCodiValor> valorsFormulari) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb aquest codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		ExpedientTipusDto et = findExpedientTipusAmbEntornICodi(e.getId(), expedientTipus);
		if (!validarPermisExpedientTipusCreate(et))
			throw new TramitacioException("No té permisos per iniciar un expedient de tipus '" + expedientTipus + "'");
		if (et == null)
			throw new TramitacioException("No existeix cap tipus d'expedient amb el codi '" + expedientTipus + "'");
		if (numero != null && !et.isDemanaNumero())
			throw new TramitacioException("Aquest tipus d'expedient no suporta número d'expedient");
		if (titol != null && !et.isDemanaTitol())
			throw new TramitacioException("Aquest tipus d'expedient no suporta títol d'expedient");
		Map<String, Object> variables = null;
		if (valorsFormulari != null) {
			variables = new HashMap<String, Object>();
			for (ParellaCodiValor parella: valorsFormulari) {
				if (parella.getValor() instanceof XMLGregorianCalendar)
					variables.put(
							parella.getCodi(),
							((XMLGregorianCalendar)parella.getValor()).toGregorianCalendar().getTime());
				else
					variables.put(
							parella.getCodi(),
							parella.getValor());
			}
		}
		// Informació de l'inici d'expedient pels logs
		String expLog = "[entorn=" +  entorn + ", expedientTipus=" + expedientTipus + ", numero=" + numero + ", titol=" + titol + "]";
		try {
			es.caib.helium.logic.intf.dto.ExpedientDto expedient = expedientService.create(
					e.getId(),
					null,
					et.getId(),
					null,
					null,
					numero,
					titol,
					null,
					null,
					null,
					null,
					false,
					null,
					null,
					null,
					null,
					null,
					null,
					false,
					null,
					null,
					false,
					variables,
					null,
					es.caib.helium.logic.intf.dto.ExpedientDto.IniciadorTipusDto.INTERN,
					null,
					null,
					null,
					null,
					null,
					false);
			logger.info("Expedient iniciat a través del WS de tramitació externa V1 " + expLog);
			return expedient.getProcessInstanceId();
		} catch (Exception ex) {
			logger.error("Error iniciant l'expedient a través del WS de tramitació externa V1 "  + expLog + ": " + ex.getMessage(), ex);
			throw new TramitacioException("No s'han pogut iniciar l'expedient: " + ex.getMessage());
		}
	}

	@Override
	public List<TascaTramitacio> consultaTasquesPersonals(
			String entorn) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String usuariBo = auth.getName();

			PaginaDto<ExpedientTascaDto> tasques = tascaService.findPerFiltrePaginat(
					e.getId(), 
					null, 
					null, 
					null, 
					null, 
					usuariBo, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					false, 
					false, 
					true, 
					new PaginacioParamsDto());
			
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (ExpedientTascaDto tasca: tasques.getContingut())
				resposta.add(convertirTascaTramitacio(tasca));
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	@Override
	public List<TascaTramitacio> consultaTasquesGrup(
			String entorn) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String usuariBo = auth.getName();

			PaginaDto<ExpedientTascaDto> tasques = tascaService.findPerFiltrePaginat(
					e.getId(), 
					null, 
					null, 
					null, 
					null, 
					usuariBo, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					false, 
					true, 
					false, 
					new PaginacioParamsDto());
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (ExpedientTascaDto tasca: tasques)
				resposta.add(convertirTascaTramitacio(tasca));
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	@Override
	public List<TascaTramitacio> consultaTasquesPersonalsByCodi(
			String entorn, 
			String codi) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {
			PaginaDto<ExpedientTascaDto> tasques = tascaService.findPerFiltrePaginat(
					e.getId(), 
					null, 
					null, 
					null, 
					null, 
					codi, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					true, 
					false, 
					true, 
					new PaginacioParamsDto());
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (ExpedientTascaDto tasca: tasques)
				resposta.add(convertirTascaTramitacio(tasca));
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	@Override
	public List<TascaTramitacio> consultaTasquesGrupByCodi(
			String entorn, 
			String codi) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {
			PaginaDto<ExpedientTascaDto> tasques = tascaService.findPerFiltrePaginat(
					e.getId(), 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					false, 
					true, 
					false, 
					new PaginacioParamsDto());
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (ExpedientTascaDto tasca: tasques)
				resposta.add(convertirTascaTramitacio(tasca));
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	@Override
	public List<TascaTramitacio> consultaTasquesPersonalsByProces(
			String entorn, 
			String processInstanceId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {
			Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
			ExpedientDto expedient = expedientService.findAmbId(expedientId);
			PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
			paginacioParams.afegirOrdre(
					"dataCreacio",
					OrdreDireccioDto.DESCENDENT);
			paginacioParams.setPaginaNum(0);
			paginacioParams.setPaginaTamany(1000);
			PaginaDto<ExpedientTascaDto> tasques = tascaService.findPerFiltrePaginat(
					e.getId(),
					null,
					expedient.getTipus().getId(),
					null,
					null,
					null,
					expedient.getTitol(),
					null,
					null,
					null,
					null,
					null,
					true,
					false,
					true,
					paginacioParams);
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (ExpedientTascaDto tasca: tasques.getContingut()) {
				resposta.add(convertirTascaTramitacio(tasca));
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	@Override
	public List<TascaTramitacio> consultaTasquesGrupByProces(
			String entorn, 
			String processInstanceId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {
			Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
			ExpedientDto expedient = expedientService.findAmbId(expedientId);
			PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
			paginacioParams.afegirOrdre(
					"dataCreacio",
					OrdreDireccioDto.DESCENDENT);
			paginacioParams.setPaginaNum(0);
			paginacioParams.setPaginaTamany(1000);
			PaginaDto<ExpedientTascaDto> tasques = tascaService.findPerFiltrePaginat(
					e.getId(),
					null,
					expedient.getTipus().getId(),
					null,
					null,
					null,
					expedient.getTitol(),
					null,
					null,
					null,
					null,
					null,
					false,
					true,
					true,
					paginacioParams);
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (ExpedientTascaDto tasca: tasques.getContingut()) {
				resposta.add(convertirTascaTramitacio(tasca));
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	@Override
	public void agafarTasca(
			String entorn,
			String tascaId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariBo = auth.getName();

		boolean agafada = false;
		try {
			PaginaDto<ExpedientTascaDto> tasques = tascaService.findPerFiltrePaginat(
					e.getId(),
					null,
					null,
					null,
					null,
					usuariBo,
					null,
					null,
					null,
					null,
					null,
					null,
					false,
					true,
					false,
					new PaginacioParamsDto());
			for (ExpedientTascaDto tasca: tasques.getContingut()) {
				if (tascaId.equals(tasca.getId())) {
					agafada = true;
					tascaService.agafar(tascaId);
					break;
				}
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut agafar la tasca", ex);
			throw new TramitacioException("No s'ha pogut agafar la tasca: " + ex.getMessage());
		}
		if (!agafada)
			throw new TramitacioException("Aquest usuari no té la tasca " + tascaId + " assignada");
	}

	@Override
	public void alliberarTasca(
			String entorn,
			String tascaId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuariBo = auth.getName();
		boolean alliberada = false;
		try {
			PaginaDto<ExpedientTascaDto> tasques = tascaService.findPerFiltrePaginat(
					e.getId(),
					null,
					null,
					null,
					null,
					usuariBo,
					null,
					null,
					null,
					null,
					null,
					null,
					true,
					false,
					false,
					new PaginacioParamsDto());
			for (ExpedientTascaDto tasca: tasques.getContingut()) {
				if (tascaId.equals(tasca.getId())) {
					tascaService.alliberar(tascaId);
					alliberada = true;
					break;
				}
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut alliberar la tasca", ex);
			throw new TramitacioException("No s'ha pogut alliberar la tasca: " + ex.getMessage());
		}
		if (!alliberada)
			throw new TramitacioException("Aquest usuari no té la tasca " + tascaId + " assignada");
	}

	@Override
	public List<CampTasca> consultaFormulariTasca(
			String entorn,
			String tascaId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		
		List<CampTasca> resposta = new ArrayList<CampTasca>();
		for (TascaDadaDto dada : tascaService.findDades(tascaId)) {
			resposta.add(convertirCampTasca(dada));
		}
		return resposta;
	}

	@Override
	public void setDadesFormulariTasca(
			String entorn,
			String tascaId,
			List<ParellaCodiValor> valors) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Map<String, Object> variables = null;
		if (valors != null) {
			variables = new HashMap<String, Object>();
			for (ParellaCodiValor parella: valors) {
				if (parella.getValor() instanceof XMLGregorianCalendar) {
					variables.put(
							parella.getCodi(),
							((XMLGregorianCalendar)parella.getValor()).toGregorianCalendar().getTime());
				} else if (parella.getValor() instanceof Object[]) {
					Object[] multiple = (Object[])parella.getValor();
					// Converteix les dates dins registres i vars múltiples
					// al tipus corecte 
					for (int i = 0; i < multiple.length; i++) {
						if (multiple[i] instanceof Object[]) {
							Object[] fila = (Object[])multiple[i];
							for (int j = 0; j < fila.length; j++) {
								if (fila[j] instanceof XMLGregorianCalendar) {
									fila[j] = ((XMLGregorianCalendar)fila[j]).toGregorianCalendar().getTime();
								}
							}
						} else if (multiple[i] instanceof XMLGregorianCalendar) {
							multiple[i] = ((XMLGregorianCalendar)multiple[i]).toGregorianCalendar().getTime();
						}
					}
					variables.put(
							parella.getCodi(),
							parella.getValor());
				} else {
					variables.put(
							parella.getCodi(),
							parella.getValor());
				}
			}
		}
		try {
			tascaService.validar(
					tascaId,
					variables);
		} catch (Exception ex) {
			logger.error("No s'han pogut guardar les variables a la tasca", ex);
			throw new TramitacioException("No s'han pogut guardar les variables a la tasca: " + ex.getMessage());
		}
	}

	@Override
	public List<DocumentTasca> consultaDocumentsTasca(
			String entorn,
			String tascaId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		List<DocumentTasca> resposta = new ArrayList<DocumentTasca>();
		for (TascaDocumentDto document : tascaService.findDocuments(tascaId)) {
			resposta.add(convertirDocumentTasca(document));
		}		
		return resposta;
	}

	@Override
	public void setDocumentTasca(
			String entorn,
			String tascaId,
			String arxiu,
			String nom,
			Date data,
			byte[] contingut) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String usuariBo = auth.getName();
			if (tascaService.isTascaValidada(tascaId))
				tascaService.guardarDocumentTasca(
					e.getId(), 
					tascaId, 
					arxiu, 
					data, 
					null, 
					contingut, 
					null, 
					false, 
					false, 
					null, 
					usuariBo);
			else
				throw new IllegalStateException("La tasca no ha estat validada");
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar el document a la tasca", ex);
			throw new TramitacioException("No s'ha pogut guardar el document a la tasca: " + ex.getMessage());
		}
	}

	@Override
	public void esborrarDocumentTasca(
			String entorn,
			String tascaId,
			String document) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String usuariBo = auth.getName();
			if (tascaService.isTascaValidada(tascaId))
				tascaService.esborrarDocument(tascaId, document, usuariBo);
			else
				throw new IllegalStateException("La tasca no ha estat validada");
		} catch (Exception ex) {
			logger.error("No s'ha pogut esborrar el document de la tasca", ex);
			throw new TramitacioException("No s'ha pogut esborrar el document de la tasca: " + ex.getMessage());
		}
	}

	@Override
	public void finalitzarTasca(
			String entorn,
			String tascaId,
			String transicio) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		try {
			tascaService.completar(tascaId, transicio);
		} catch (Exception ex) {
			logger.error("No s'ha pogut completar la tasca", ex);
			throw new TramitacioException("No s'ha pogut completar la tasca: " + ex.getMessage());
		}
	}

	@Override
	public ExpedientInfo getExpedientInfo(
			String entornCodi,
			String processInstanceId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entornCodi);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entornCodi + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entornCodi + "'");
		Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
		return toExpedientInfo(
			expedientService.findAmbId(expedientId));
	}

	@Override
	public List<CampProces> consultarVariablesProces(
			String entorn,
			String processInstanceId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if (!validarPermisExpedientTipusRead(expedient.getTipus()))
			throw new TramitacioException("No té permisos per llegir les dades del proces '" + processInstanceId + "'");
		try {
			List<CampProces> resposta = new ArrayList<CampProces>();
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId);
			if (instanciaProces.getVariables() != null) {
				for (String var: instanciaProces.getVariables().keySet()) {
					CampDto campVar = null;
					for (CampDto camp: instanciaProces.getCamps()) {
						if (camp.getCodi().equals(var)) {
							campVar = camp;
							break;
						}
					}
					if (campVar == null) {
						campVar = new CampDto();
						campVar.setCodi(var);
						campVar.setEtiqueta(var);
						campVar.setTipus(CampTipusDto.STRING);
					}
					resposta.add(convertirCampProces(
							campVar,
							instanciaProces.getVariable(var)));
				}
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar la variable al procés", ex);
			throw new TramitacioException("No s'ha pogut guardar la variable al procés: " + ex.getMessage());
		}
	}

	@Override
	public void setVariableProces(
			String entorn,
			String processInstanceId,
			String varCodi,
			Object valor) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
			throw new TramitacioException("No té permisos per modificar les dades del proces '" + processInstanceId + "'");
		try {
			if (valor instanceof XMLGregorianCalendar) {
				expedientDadaService.update(
						expedient.getId(),
						processInstanceId,
						varCodi,
						((XMLGregorianCalendar)valor).toGregorianCalendar().getTime());
			} else if (valor instanceof Object[]) {
				Object[] multiple = (Object[])valor;
				// Converteix les dates dins registres i vars múltiples
				// al tipus corecte 
				for (int i = 0; i < multiple.length; i++) {
					if (multiple[i] instanceof Object[]) {
						Object[] fila = (Object[])multiple[i];
						for (int j = 0; j < fila.length; j++) {
							if (fila[j] instanceof XMLGregorianCalendar) {
								fila[j] = ((XMLGregorianCalendar)fila[j]).toGregorianCalendar().getTime();
							}
						}
					} else if (multiple[i] instanceof XMLGregorianCalendar) {
						multiple[i] = ((XMLGregorianCalendar)multiple[i]).toGregorianCalendar().getTime();
					}
				}
				expedientDadaService.update(
						expedient.getId(),
						processInstanceId,
						varCodi,
						valor);
			} else {
				expedientDadaService.update(
						expedient.getId(),
						processInstanceId,
						varCodi,
						valor);
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar la variable al procés", ex);
			throw new TramitacioException("No s'ha pogut guardar la variable al procés: " + ex.getMessage());
		}
	}

	@Override
	public void esborrarVariableProces(
			String entorn,
			String processInstanceId,
			String varCodi) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
			throw new TramitacioException("No té permisos per modificar les dades del proces '" + processInstanceId + "'");
		try {
			expedientDadaService.delete(
					expedient.getId(),
					processInstanceId,
					varCodi);
		} catch (Exception ex) {
			logger.error("No s'ha pogut esborrar la variable al procés", ex);
			throw new TramitacioException("No s'ha pogut esborrar la variable al procés: " + ex.getMessage());
		}
	}

	@Override
	public List<DocumentProces> consultarDocumentsProces(
			String entorn,
			String processInstanceId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if (!validarPermisExpedientTipusRead(expedient.getTipus()))
			throw new TramitacioException("No té permisos per accedir a les dades del proces '" + processInstanceId + "'");
		try {
			List<DocumentProces> resposta = new ArrayList<DocumentProces>();
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId);
			for (DocumentDto document: instanciaProces.getVarsDocuments().values()) {
				resposta.add(convertirDocumentProces(document));
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'han pogut consultar el documents del procés", ex);
			throw new TramitacioException("No s'han pogut consultar el documents del procés: " + ex.getMessage());
		}
	}

	@Override
	public ArxiuProces getArxiuProces(
			Long documentId) throws TramitacioException {
		try {
			ArxiuProces resposta = null;
			if (documentId != null) {
				DocumentDto docInfo = documentService.findAmbId(null, documentId);
				String processInstanceId = docInfo.getProcessInstanceId();
				Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
				ExpedientDto expedient = expedientService.findAmbId(expedientId);
				if (!validarPermisExpedientTipusRead(expedient.getTipus()))
					throw new TramitacioException("No té permisos per llegir l'arxiu '" + documentId + "'");
				ArxiuDto arxiu = expedientDocumentService.arxiuFindAmbDocument(
						expedientId, 
						processInstanceId, 
						documentId);
				if (arxiu != null) {
					resposta = new ArxiuProces();
					resposta.setNom(arxiu.getNom());
					resposta.setContingut(arxiu.getContingut());
				}
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir l'arxiu del procés", ex);
			throw new TramitacioException("No s'ha pogut obtenir l'arxiu del procés: " + ex.getMessage());
		}
	}

	@Override
	public Long setDocumentProces(
			String entorn,
			String processInstanceId,
			String documentCodi,
			String arxiu,
			Date data,
			byte[] contingut) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
			throw new TramitacioException("No té permisos per modificar les dades del proces '" + processInstanceId + "'");
		try {
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId);
			if (instanciaProces == null)
				throw new TramitacioException("No s'ha pogut trobar la instancia de proces amb id " + processInstanceId);
			DocumentDto document = documentService.findAmbCodi(
					expedient.getTipus().isAmbInfoPropia() ? expedient.getTipus().getId() : null, 
					instanciaProces.getDefinicioProces().getId(), 
					documentCodi, 
					true);
			if (document == null)
				throw new TramitacioException("No s'ha pogut trobar el document amb codi " + documentCodi);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			String usuariBo = auth.getName();
			return expedientDocumentService.guardarDocumentProces(
					expedient.getId(),
					processInstanceId,
					documentCodi,
					null,
					data,
					arxiu,
					contingut,
					false,
					usuariBo);
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar el document al procés", ex);
			throw new TramitacioException("No s'ha pogut guardar el document al procés: " + ex.getMessage());
		}
	}

	@Override
	public void esborrarDocumentProces(
			String entorn,
			String processInstanceId,
			Long documentId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
			throw new TramitacioException("No té permisos per modificar les dades del proces '" + processInstanceId + "'");
		try {
			expedientDocumentService.delete(
					expedientId, 
					processInstanceId, 
					documentId);
		} catch (Exception ex) {
			logger.error("No s'ha pogut esborrar el document del procés", ex);
			throw new TramitacioException("No s'ha pogut esborrar el document del procés: " + ex.getMessage());
		}
	}

	@Override
	public void executarAccioProces(
			String entorn,
			String processInstanceId,
			String accio) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		
		Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
		List<AccioDto> accions = expedientService.accioFindVisiblesAmbProcessInstanceId(expedientId, processInstanceId);
		AccioDto ac = null;
		for (AccioDto a : accions) {
			if( a.getCodi().equals(accio)) {
				ac = a;
				break;
			}
		}
		if (ac == null || ac.isOculta())
			throw new TramitacioException("No existeix cap accio amb el codi '" + accio + "'");
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if (ac.isPublica()) {
			if (!validarPermisExpedientTipusRead(expedient.getTipus()) && !validarPermisExpedientTipusWrite(expedient.getTipus()))
				throw new TramitacioException("No té permisos per executar l'acció '" + accio + "'");
		} else {
			if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
				throw new TramitacioException("No té permisos per executar l'acció '" + accio + "'");
		}
		try {
			expedientService.accioExecutar(
					expedientId, 
					processInstanceId, 
					ac.getId());
		} catch (Exception ex) {
			logger.error("No s'ha pogut executar l'acciÃ³", ex);
			throw new TramitacioException("No s'ha pogut executar l'acció: " + ex.getMessage());
		}
	}

	@Override
	public void executarScriptProces(
			String entorn,
			String processInstanceId,
			String script) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
			throw new TramitacioException("No té permisos per modificar les dades del proces '" + processInstanceId + "'");
		try {
			expedientService.procesScriptExec(
					expedientId, 
					processInstanceId, 
					script);
		} catch (Exception ex) {
			logger.error("No s'ha pogut executar l'script", ex);
			throw new TramitacioException("No s'ha pogut executar l'script: " + ex.getMessage());
		}
	}

	@Override
	public void aturarExpedient(
			String entorn,
			String processInstanceId,
			String motiu) throws TramitacioException{
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
			throw new TramitacioException("No té permisos per modificar les dades del proces '" + processInstanceId + "'");
		try {
			expedientService.aturar(
					expedientId, 
					motiu);
		} catch (Exception ex) {
			logger.error("No s'ha pogut aturar l'expedient", ex);
			throw new TramitacioException("No s'ha pogut aturar l'expedient: " + ex.getMessage());
		}
	}

	@Override
	public void reprendreExpedient(
			String entorn,
			String processInstanceId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if (!validarPermisExpedientTipusWrite(expedient.getTipus()))
			throw new TramitacioException("No té permisos per modificar les dades del proces '" + processInstanceId + "'");
		try {
			expedientService.reprendre(
					expedientId);
		} catch (Exception ex) {
			logger.error("No s'ha pogut reprendre l'expedient", ex);
			throw new TramitacioException("No s'ha pogut reprendre l'expedient: " + ex.getMessage());
		}
	}

	@Override
	public List<ExpedientInfo> consultaExpedients(
			String entorn,
			String titol,
			String numero,
			Date dataInici1,
			Date dataInici2,
			String expedientTipusCodi,
			String estatCodi,
			boolean iniciat,
			boolean finalitzat,
			Double geoPosX,
			Double geoPosY,
			String geoReferencia) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		// Obtencio de dades per a fer la consulta
		ExpedientTipusDto expedientTipus = null;
		Long estatId = null;
		if (expedientTipusCodi != null && expedientTipusCodi.length() > 0) {
			expedientTipus = expedientTipusService.findAmbCodiPerValidarRepeticio(
					e.getId(), 
					expedientTipusCodi);
			if (estatCodi != null && estatCodi.length() > 0) {
				for (EstatDto estat: expedientTipus.getEstats()) {
					if (estat.getCodi().equals(estatCodi)) {
						estatId = estat.getId();
						break;
					}
				}
			}
		}
		EstatTipusDto estatTipus = iniciat ? 
				EstatTipusDto.INICIAT : 
					finalitzat? EstatTipusDto.FINALITZAT : null;

		// Tots els resultats
		PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
		paginacioParams.setPaginaNum(0);
		paginacioParams.setPaginaTamany(-1);

		// Consulta d'expedients
		PaginaDto<ExpedientDto> paginaExpedients = expedientService.findAmbFiltrePaginat(
				e.getId(), 
				null, // Passar llista d'expedients permesos 
				titol, 
				numero, 
				dataInici1, 
				dataInici2, 
				null, 
				null, 
				estatTipus, 
				estatId, 
				geoPosX, 
				geoPosY, 
				geoReferencia, 
				false, 
				false, 
				false, 
				false, 
				MostrarAnulatsDto.NO, 
				paginacioParams);
		List<ExpedientDto> expedients = paginaExpedients.getContingut();
		
		// Construcció de la resposta
		List<ExpedientInfo> resposta = new ArrayList<ExpedientInfo>();
		for (ExpedientDto dto: expedients)
			resposta.add(toExpedientInfo(dto));
		return resposta;
	}

	@Override
	public void deleteExpedient(
			String entorn,
			String processInstanceId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		if (!validarPermisEntornRead(e))
			throw new TramitacioException("No té permisos per accedir a l'entorn '" + entorn + "'");
		Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
		ExpedientDto expedient = expedientService.findAmbId(expedientId);
		if (!validarPermisExpedientTipusDelete(expedient.getTipus()))
			throw new TramitacioException("No té permisos per a esborrar l'expedient del proces '" + processInstanceId + "'");
		expedientService.delete(expedientId);
	}

	private EntornDto findEntornAmbCodi(String codi) {
		EntornDto entorn = entornService.findAmbCodi(codi);
		if (entorn != null)
			EntornActual.setEntornId(entorn.getId());
		return entorn;
	}

	private ExpedientTipusDto findExpedientTipusAmbEntornICodi(Long entornId, String codi) {
		return expedientTipusService.findAmbCodiPerValidarRepeticio(entornId, codi);
	}
	private TascaTramitacio convertirTascaTramitacio(ExpedientTascaDto tasca) {
		TascaTramitacio tt = new TascaTramitacio();
		tt.setId(tasca.getId());
		tt.setCodi(tasca.getJbpmName());
		tt.setTitol(tasca.getTitol());
		tt.setExpedient(tasca.getExpedientNumero());
		tt.setMissatgeInfo(tasca.getTascaMissatgeInfo());
		tt.setMissatgeWarn(tasca.getTascaMissatgeWarn());
<<<<<<< HEAD
		tt.setResponsable(tasca.getResponsableString());
		tt.setResponsables(tasca.getResponsablesString());
=======
		if (tasca.getResponsable() != null)
			tt.setResponsable(tasca.getResponsable().getCodi());
		Set<String> responsables = new HashSet<String>();
		if (tasca.getResponsables() != null)
			for (PersonaDto responsable: tasca.getResponsables()) {
				responsables.add(responsable.getCodi());
			}
		tt.setResponsables(responsables);
>>>>>>> refs/remotes/origin/helium-dev
		tt.setDataCreacio(tasca.getCreateTime());
		tt.setDataInici(tasca.getStartTime());
		tt.setDataFi(tasca.getEndTime());
		tt.setDataLimit(tasca.getDueDate());
		tt.setPrioritat(tasca.getPriority());
		tt.setOpen(tasca.isOpen());
		tt.setCompleted(tasca.isCompleted());
		tt.setCancelled(tasca.isCancelled());
		tt.setSuspended(tasca.isSuspended());
		tt.setTransicionsSortida(tasca.getOutcomes());
		tt.setProcessInstanceId(tasca.getProcessInstanceId());
		return tt;
	}
	
	private CampTasca convertirCampTasca(
			TascaDadaDto campTasca) {
		CampTasca ct = new CampTasca();
		ct.setCodi(campTasca.getVarCodi());
		ct.setEtiqueta(campTasca.getCampEtiqueta());
		ct.setTipus(campTasca.getCampTipus().name());
		ct.setObservacions(campTasca.getObservacions());
		ct.setDominiId(campTasca.getDominiId());
		ct.setDominiParams(campTasca.getDominiParams());
		ct.setDominiCampValor(campTasca.getDominiCampValor());
		ct.setDominiCampText(campTasca.getDominiCampText());
		ct.setJbpmAction(campTasca.getJbpmAction());
		ct.setReadFrom(campTasca.isReadFrom());
		ct.setWriteTo(campTasca.isWriteTo());
		ct.setRequired(campTasca.isRequired());
		ct.setReadOnly(campTasca.isReadOnly());
		ct.setMultiple(campTasca.isCampMultiple());
		ct.setOcult(campTasca.isCampOcult());
		ct.setValor(campTasca.getVarValor());
		return ct;
	}
	private DocumentTasca convertirDocumentTasca(
			TascaDocumentDto document) {
		DocumentTasca dt = new DocumentTasca();
		dt.setId(document.getId());
		dt.setCodi(document.getDocumentCodi());
		dt.setNom(document.getDocumentNom());
		dt.setDescripcio(document.getDocumentDescripcio());
		dt.setArxiu(document.getArxiuNom());
		dt.setData(document.getDataDocument());
		if (document.isSignat()) {
			dt.setUrlCustodia(document.getUrlVerificacioCustodia());
		}
		return dt;
	}
	private CampProces convertirCampProces(
			CampDto camp,
			Object valor) {
		CampProces ct = new CampProces();
		if (camp != null) {
			ct.setCodi(camp.getCodi());
			ct.setEtiqueta(camp.getEtiqueta());
			ct.setTipus(camp.getTipus().name());
			ct.setObservacions(camp.getObservacions());
			ct.setDominiId(camp.getDominiIdentificador());
			ct.setDominiParams(camp.getDominiParams());
			ct.setDominiCampValor(camp.getDominiCampValor());
			ct.setDominiCampText(camp.getDominiCampText());
			ct.setJbpmAction(camp.getJbpmAction());
			ct.setMultiple(camp.isMultiple());
			ct.setOcult(camp.isOcult());
			ct.setValor(valor);
		}
		return ct;
	}
	private DocumentProces convertirDocumentProces(DocumentDto document) {
		DocumentProces dt = new DocumentProces();
		dt.setId(document.getId());
		dt.setCodi(document.getDocumentCodi());
		dt.setNom(document.getDocumentNom());
		dt.setArxiu(document.getArxiuNom());
		dt.setData(document.getDataDocument());
		return dt;
	}
	private ExpedientInfo toExpedientInfo(ExpedientDto expedient) {
		if (expedient != null) {
			ExpedientInfo resposta = new ExpedientInfo();
			resposta.setTitol(expedient.getTitol());
			resposta.setNumero(expedient.getNumero());
			resposta.setNumeroDefault(expedient.getNumeroDefault());
			resposta.setIdentificador(expedient.getIdentificador());
			resposta.setDataInici(expedient.getDataInici());
			resposta.setDataFi(expedient.getDataFi());
			resposta.setComentari(expedient.getComentari());
			resposta.setInfoAturat(expedient.getInfoAturat());
			if (expedient.getIniciadorTipus().equals(es.caib.helium.persist.entity.Expedient.IniciadorTipus.INTERN))
				resposta.setIniciadorTipus(net.conselldemallorca.helium.ws.tramitacio.v1.ExpedientInfo.IniciadorTipus.INTERN);
			else if (expedient.getIniciadorTipus().equals(es.caib.helium.persist.entity.Expedient.IniciadorTipus.SISTRA))
				resposta.setIniciadorTipus(net.conselldemallorca.helium.ws.tramitacio.v1.ExpedientInfo.IniciadorTipus.SISTRA);
			resposta.setIniciadorCodi(expedient.getIniciadorCodi());
			resposta.setResponsableCodi(expedient.getResponsableCodi());
			resposta.setGeoPosX(expedient.getGeoPosX());
			resposta.setGeoPosY(expedient.getGeoPosY());
			resposta.setGeoReferencia(expedient.getGeoReferencia());
			resposta.setRegistreNumero(expedient.getRegistreNumero());
			resposta.setRegistreData(expedient.getRegistreData());
			resposta.setUnitatAdministrativa(expedient.getUnitatAdministrativa());
			resposta.setIdioma(expedient.getIdioma());
			resposta.setAutenticat(expedient.isAutenticat());
			resposta.setTramitadorNif(expedient.getTramitadorNif());
			resposta.setTramitadorNom(expedient.getTramitadorNom());
			resposta.setInteressatNif(expedient.getInteressatNif());
			resposta.setInteressatNom(expedient.getInteressatNom());
			resposta.setRepresentantNif(expedient.getRepresentantNif());
			resposta.setRepresentantNom(expedient.getRepresentantNom());
			resposta.setAvisosHabilitats(expedient.isAvisosHabilitats());
			resposta.setAvisosEmail(expedient.getAvisosEmail());
			resposta.setAvisosMobil(expedient.getAvisosMobil());
			resposta.setNotificacioTelematicaHabilitada(expedient.isNotificacioTelematicaHabilitada());
			resposta.setTramitExpedientIdentificador(expedient.getTramitExpedientIdentificador());
			resposta.setTramitExpedientClau(expedient.getTramitExpedientClau());
			resposta.setExpedientTipusCodi(expedient.getTipus().getCodi());
			resposta.setExpedientTipusNom(expedient.getTipus().getNom());
			resposta.setEntornCodi(expedient.getEntorn().getCodi());
			if (expedient.getEstat() != null) {
				resposta.setEstatCodi(expedient.getEstat().getCodi());
				resposta.setEstatNom(expedient.getEstat().getNom());
			}
			resposta.setProcessInstanceId(new Long(expedient.getProcessInstanceId()).longValue());
			return resposta;
		}
		return null;
	}

	private boolean validarPermisEntornRead(EntornDto entorn) {
		printUsuari();
		return entornService.findAmbIdPermisAcces(entorn.getId()) != null;
	}
	private boolean validarPermisExpedientTipusCreate(ExpedientTipusDto expedientTipus) {
		printUsuari();
		return expedientTipusService.findAmbIdPermisCrear(
				expedientTipus.getEntorn().getId(), 
				expedientTipus.getId()) != null;
	}
	private boolean validarPermisExpedientTipusRead(ExpedientTipusDto expedientTipus) {
		printUsuari();
		return expedientTipusService.findAmbIdPermisLectura(
				expedientTipus.getEntorn().getId(), 
				expedientTipus.getId()) != null;
	}
	private boolean validarPermisExpedientTipusWrite(ExpedientTipusDto expedientTipus) {
		printUsuari();
		return expedientTipusService.findAmbIdPermisEscriptura(
				expedientTipus.getEntorn().getId(), 
				expedientTipus.getId()) != null;
	}
	private boolean validarPermisExpedientTipusDelete(ExpedientTipusDto expedientTipus) {
		printUsuari();
		return expedientTipusService.findAmbIdPermisEsborrar(
				expedientTipus.getEntorn().getId(), 
				expedientTipus.getId()) != null;
	}
	private void printUsuari() {
		if (PRINT_USUARI) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			logger.info(">>> " + auth.getName());
		}
	}

	private static final Log logger = LogFactory.getLog(TramitacioServiceImpl.class);

}
