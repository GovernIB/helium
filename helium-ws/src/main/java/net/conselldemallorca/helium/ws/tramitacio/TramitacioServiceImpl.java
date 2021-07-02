/**
 * 
 */
package net.conselldemallorca.helium.ws.tramitacio;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

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
import net.conselldemallorca.helium.v3.core.api.dto.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementació del servei de tramitació d'expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(
		serviceName="TramitacioService",
		portName="TramitacioPort",
		endpointInterface = "net.conselldemallorca.helium.ws.tramitacio.TramitacioService",
		targetNamespace = "http://tramitacio.integracio.helium.conselldemallorca.net/")
public class TramitacioServiceImpl implements TramitacioService {

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
	@Autowired
	private MetricRegistry metricRegistry;



	@Override
	public String iniciExpedient(
			String entorn,
			String usuari,
			String expedientTipus,
			String numero,
			String titol,
			List<ParellaCodiValor> valorsFormulari) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb aquest codi '" + entorn + "'");
		ExpedientTipusDto et = findExpedientTipusAmbEntornICodi(e.getId(), expedientTipus);
		if (et == null)
			throw new TramitacioException("No existeix cap tipus d'expedient amb el codi '" + expedientTipus + "'");
		if (numero != null && !et.isDemanaNumero())
			throw new TramitacioException("Aquest tipus d'expedient no suporta número d'expedient");
		if (titol != null && !et.isDemanaTitol())
			throw new TramitacioException("Aquest tipus d'expedient no suporta titol d'expedient");
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
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"expedientIniciar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"expedientIniciar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"expedientIniciar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"expedientIniciar.count",
						e.getCodi()));
		countEntorn.inc();
		final Timer timerExptip = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"expedientIniciar",
						e.getCodi()));
		final Timer.Context contextExptip = timerExptip.time();
		Counter countExptip = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"expedientIniciar.count",
						e.getCodi()));
		countExptip.inc();
		// Informació de l'inici d'expedient pels logs
		String expLog = "[entorn=" +  entorn + ", expedientTipus=" + expedientTipus + ", numero=" + numero + ", titol=" + titol + "]";
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			es.caib.helium.logic.intf.dto.ExpedientDto expedient = expedientService.create(
					e.getId(),
					usuari,
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
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
			contextExptip.stop();
		}
	}

	@Override
	public List<TascaTramitacio> consultaTasquesPersonals(
			String entorn,
			String usuari) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaPersona"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaPersona.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaPersona",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaPersona.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			String usuariBo = authentication.getName();

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
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public List<TascaTramitacio> consultaTasquesGrup(
			String entorn,
			String usuari) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaGrup"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaGrup.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaGrup",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaGrup.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String usuariBo = authentication.getName();

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
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public List<TascaTramitacio> consultaTasquesPersonalsByCodi(
			String entorn,
			String usuari,
			String codi) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaByCodiPersona"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaByCodiPersona.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaByCodiPersona",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaByCodiPersona.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
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
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public List<TascaTramitacio> consultaTasquesGrupByCodi(
			String entorn,
			String usuari,
			String codi) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaByCodiGrup"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaByCodiGrup.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaByCodiGrup",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaConsultaByCodiGrup.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
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
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public List<TascaTramitacio> consultaTasquesPersonalsByProces(
			String entorn,
			String usuari,
			String processInstanceId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"consultaTasquesPersonalsByProces"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"consultaTasquesPersonalsByProces.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"consultaTasquesPersonalsByProces",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"consultaTasquesPersonalsByProces.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
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
					usuari,
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
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public List<TascaTramitacio> consultaTasquesGrupByProces(
			String entorn,
			String usuari,
			String processInstanceId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"consultaTasquesGrupByProces"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"consultaTasquesGrupByProces.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"consultaTasquesGrupByProces",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"consultaTasquesGrupByProces.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
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
					usuari,
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
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public void agafarTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		boolean agafada = false;
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaAgafar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaAgafar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaAgafar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaAgafar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String usuariBo = authentication.getName();
			
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
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
		if (!agafada)
			throw new TramitacioException("L'usuari '" + usuari + "' no té la tasca " + tascaId + " assignada");
	}

	@Override
	public void alliberarTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		boolean alliberada = false;
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaAlliberar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaAlliberar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaAlliberar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaAlliberar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String usuariBo = authentication.getName();

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
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
		if (!alliberada)
			throw new TramitacioException("L'usuari '" + usuari + "' no té la tasca " + tascaId + " assignada");
	}

	@Override
	public List<CampTasca> consultaFormulariTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaFormConsultar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaFormConsultar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaFormConsultar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaFormConsultar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			List<CampTasca> resposta = new ArrayList<CampTasca>();
			for (TascaDadaDto dada : tascaService.findDades(tascaId)) {
				resposta.add(convertirCampTasca(dada));
			}
			return resposta;
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public void setDadesFormulariTasca(
			String entorn,
			String usuari,
			String tascaId,
			List<ParellaCodiValor> valors) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaFormGuardar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaFormGuardar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaFormGuardar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaFormGuardar.count",
						e.getCodi()));
		countEntorn.inc();
		Map<String, Object> variables = null;
		if (valors != null) {
			variables = new HashMap<String, Object>();
			for (ParellaCodiValor parella: valors) {
				if (parella.getValor() instanceof XMLGregorianCalendar) {
					// Converteix les dates al tipus correcte
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
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			tascaService.validar(
					tascaId,
					variables);
		} catch (Exception ex) {
			logger.error("No s'han pogut guardar les variables a la tasca", ex);
			throw new TramitacioException("No s'han pogut guardar les variables a la tasca: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public List<DocumentTasca> consultaDocumentsTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaDocumentConsultar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaDocumentConsultar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaDocumentConsultar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaDocumentConsultar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			List<DocumentTasca> resposta = new ArrayList<DocumentTasca>();
			for (TascaDocumentDto document : tascaService.findDocuments(tascaId)) {
				resposta.add(convertirDocumentTasca(document));
			}		
			return resposta;
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public void setDocumentTasca(
			String entorn,
			String usuari,
			String tascaId,
			String arxiu,
			String nom,
			Date data,
			byte[] contingut) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaDocumentGuardar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaDocumentGuardar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaDocumentGuardar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaDocumentGuardar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String usuariBo = authentication.getName();
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
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public void esborrarDocumentTasca(
			String entorn,
			String usuari,
			String tascaId,
			String document) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaDocumentEsborrar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaDocumentEsborrar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaDocumentEsborrar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaDocumentEsborrar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);			
			String usuariBo = authentication.getName();
			if (tascaService.isTascaValidada(tascaId))
				tascaService.esborrarDocument(tascaId, document, usuariBo);
			else
				throw new IllegalStateException("La tasca no ha estat validada");
		} catch (Exception ex) {
			logger.error("No s'ha pogut esborrar el document de la tasca", ex);
			throw new TramitacioException("No s'ha pogut esborrar el document de la tasca: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public void finalitzarTasca(
			String entorn,
			String usuari,
			String tascaId,
			String transicio) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaCompletar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaCompletar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaCompletar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"tascaCompletar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			tascaService.completar(
					tascaId,
					transicio);
		} catch (Exception ex) {
			logger.error("No s'ha pogut completar la tasca", ex);
			throw new TramitacioException("No s'ha pogut completar la tasca: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public ExpedientInfo getExpedientInfo(
			String entornCodi,
			String usuari,
			String processInstanceId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entornCodi);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entornCodi + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"getExpedientInfo"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"getExpedientInfo.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"getExpedientInfo",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"getExpedientInfo.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
			return toExpedientInfo(
				expedientService.findAmbId(expedientId));
		} catch (Exception ex) {
			logger.error("No s'ha pogut consultar l'expedient", ex);
			throw new TramitacioException("No s'ha pogut consultar l'expedient: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public List<CampProces> consultarVariablesProces(
			String entorn,
			String usuari,
			String processInstanceId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesVariableConsultar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesVariableConsultar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesVariableConsultar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesVariableConsultar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
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
			logger.error("No s'ha pogut consultar las variables al procés", ex);
			throw new TramitacioException("No s'ha pogut consultar las variables al procés: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public void setVariableProces(
			String entorn,
			String usuari,
			String processInstanceId,
			String varCodi,
			Object valor) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesVariableGuardar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesVariableGuardar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesVariableGuardar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesVariableGuardar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
			ExpedientDto expedient = expedientService.findAmbId(expedientId);

			if (valor instanceof Object[]) {
				Object[] vs = (Object[])valor;
				for (int i = 0; i < vs.length; i++) {
					if (vs[i] instanceof Object[]) {
						Object[] vss = (Object[])vs[i];
						for (int j = 0; j < vss.length; j++) {
							if (vss[j] instanceof XMLGregorianCalendar)
								vss[j] = ((XMLGregorianCalendar)vss[j]).toGregorianCalendar().getTime();
						}
					} else {
						if (vs[i] instanceof XMLGregorianCalendar)
							vs[i] = ((XMLGregorianCalendar)vs[i]).toGregorianCalendar().getTime();
					}
				}
				expedientDadaService.update(
						expedient.getId(),
						processInstanceId,
						varCodi,
						valor);
			} else if (valor instanceof XMLGregorianCalendar) {
				expedientDadaService.update(
						expedient.getId(),
						processInstanceId,
						varCodi,
						((XMLGregorianCalendar)valor).toGregorianCalendar().getTime());
			} else {
				expedientDadaService.update(
						expedient.getId(),
						processInstanceId,
						varCodi,
						valor);
			}
		} catch (NotFoundException ex) {
			logger.error("No s'ha pogut guardar la variable al procés: " + ex.getMessage());
			throw new TramitacioException("No s'ha pogut guardar la variable al procés: " + ex.getMessage());
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar la variable al procés", ex);
			throw new TramitacioException("No s'ha pogut guardar la variable al procés: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public void esborrarVariableProces(
			String entorn,
			String usuari,
			String processInstanceId,
			String varCodi) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesVariableEsborrar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesVariableEsborrar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesVariableEsborrar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesVariableEsborrar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
			ExpedientDto expedient = expedientService.findAmbId(expedientId);
			expedientDadaService.delete(
					expedient.getId(),
					processInstanceId,
					varCodi);
		} catch (Exception ex) {
			logger.error("No s'ha pogut esborrar la variable al procés", ex);
			throw new TramitacioException("No s'ha pogut esborrar la variable al procés: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public List<DocumentProces> consultarDocumentsProces(
			String entorn,
			String usuari,
			String processInstanceId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesDocumentConsultar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesDocumentConsultar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesDocumentConsultar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesDocumentConsultar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			List<DocumentProces> resposta = new ArrayList<DocumentProces>();
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId);
			for (DocumentDto document: instanciaProces.getVarsDocuments().values()) {
				resposta.add(convertirDocumentProces(document));
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'han pogut consultar el documents del procés", ex);
			throw new TramitacioException("No s'han pogut consultar el documents del procés: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public ArxiuProces getArxiuProces(
			Long documentId) throws TramitacioException {
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesDocumentDescarregar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesDocumentDescarregar.count"));
		countTotal.inc();
		try {
			ArxiuProces resposta = null;
			if (documentId != null) {
				DocumentDto docInfo = documentService.findAmbId(null, documentId);
				String processInstanceId = docInfo.getProcessInstanceId();
				Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
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
		} finally {
			contextTotal.stop();
		}
	}

	@Override
	public Long setDocumentProces(
			String entorn,
			String usuari,
			String processInstanceId,
			String documentCodi,
			String arxiu,
			Date data,
			byte[] contingut) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesDocumentGuardar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesDocumentGuardar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesDocumentGuardar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesDocumentGuardar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId);
			if (instanciaProces == null)
				throw new TramitacioException("No s'ha pogut trobar la instancia de proces amb id " + processInstanceId);
			Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
			ExpedientDto expedient = expedientService.findAmbId(expedientId);
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
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public void esborrarDocumentProces(
			String entorn,
			String usuari,
			String processInstanceId,
			Long documentId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesDocumentEsborrar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesDocumentEsborrar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesDocumentEsborrar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesDocumentEsborrar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
			expedientDocumentService.delete(
					expedientId,
					processInstanceId, 
					documentId);
		} catch (Exception ex) {
			logger.error("No s'ha pogut esborrar el document del procés", ex);
			throw new TramitacioException("No s'ha pogut esborrar el document del procés: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public void executarAccioProces(
			String entorn,
			String usuari,
			String processInstanceId,
			String accio) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesAccioExecutar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesAccioExecutar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesAccioExecutar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesAccioExecutar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
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

			expedientService.accioExecutar(expedientId, processInstanceId, ac.getId());
		} catch (Exception ex) {
			logger.error("No s'ha pogut executar l'acció", ex);
			throw new TramitacioException("No s'ha pogut executar l'acció: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public void executarScriptProces(
			String entorn,
			String usuari,
			String processInstanceId,
			String script) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesScriptExecutar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesScriptExecutar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesScriptExecutar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesScriptExecutar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);

			expedientService.procesScriptExec(
					expedientId, 
					processInstanceId, 
					script);
		} catch (Exception ex) {
			logger.error("No s'ha pogut executar l'script", ex);
			throw new TramitacioException("No s'ha pogut executar l'script: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public void aturarExpedient(
			String entorn,
			String usuari,
			String processInstanceId,
			String motiu) throws TramitacioException{
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesExpedientAturar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesExpedientAturar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesExpedientAturar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesExpedientAturar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
			expedientService.aturar(
					expedientId,
					motiu);
		} catch (Exception ex) {
			logger.error("No s'ha pogut aturar l'expedient", ex);
			throw new TramitacioException("No s'ha pogut aturar l'expedient: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public void reprendreExpedient(
			String entorn,
			String usuari,
			String processInstanceId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesExpedientReprendre"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesExpedientReprendre.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"procesExpedientReprendre",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"procesExpedientReprendre.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);

			expedientService.reprendre(expedientId);
		} catch (Exception ex) {
			logger.error("No s'ha pogut reprendre l'expedient", ex);
			throw new TramitacioException("No s'ha pogut reprendre l'expedient: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public List<ExpedientInfo> consultaExpedients(
			String entorn,
			String usuari,
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
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"expedientConsulta"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"expedientConsulta.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"expedientConsulta",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"expedientConsulta.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
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
			// Estableix l'usuari autenticat
			establirUsuariAutenticat(usuari);
			
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
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	@Override
	public void deleteExpedient(
			String entorn,
			String usuari,
			String processInstanceId) throws TramitacioException {
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		final Timer timerTotal = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"expedientEsborrar"));
		final Timer.Context contextTotal = timerTotal.time();
		Counter countTotal = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"expedientEsborrar.count"));
		countTotal.inc();
		final Timer timerEntorn = metricRegistry.timer(
				MetricRegistry.name(
						TramitacioService.class,
						"expedientEsborrar",
						e.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"expedientEsborrar.count",
						e.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			Long expedientId = expedientService.findIdAmbProcessInstanceId(processInstanceId);
			expedientService.delete(expedientId);
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
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
		if (tasca.getResponsable() != null)
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
				resposta.setIniciadorTipus(net.conselldemallorca.helium.ws.tramitacio.ExpedientInfo.IniciadorTipus.INTERN);
			else if (expedient.getIniciadorTipus().equals(es.caib.helium.persist.entity.Expedient.IniciadorTipus.SISTRA))
				resposta.setIniciadorTipus(net.conselldemallorca.helium.ws.tramitacio.ExpedientInfo.IniciadorTipus.SISTRA);
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

	private void establirUsuariAutenticat(
			String usuariCodi) {
		Authentication authentication =  new UsernamePasswordAuthenticationToken(
				usuariCodi,
				null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private static final Log logger = LogFactory.getLog(TramitacioServiceImpl.class);

}
