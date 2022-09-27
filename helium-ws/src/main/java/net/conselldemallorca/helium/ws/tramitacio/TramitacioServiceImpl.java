/**
 * 
 */
package net.conselldemallorca.helium.ws.tramitacio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;
import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDadaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TascaDocumentDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.EntornService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDadaService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientDocumentService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientTipusService;
import net.conselldemallorca.helium.v3.core.api.service.TascaService;

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
	private ExpedientService expedientService;
	@Autowired
	private EntornService entornService;
	@Autowired
	private DissenyService dissenyService;
	@Autowired
	private ExpedientTipusService expedientTipusService;
	@Autowired
	private DocumentService documentService;
	@Autowired
	private TascaService tascaService;
	@Autowired
	private DefinicioProcesService definicioProcesService;
	@Autowired
	private ExpedientDocumentService expedientDocumentService;
	@Autowired
	private ExpedientDadaService expedientDadaService;
	@Autowired
	private JbpmHelper jbpmHelper;
	@Autowired
	private MetricRegistry metricRegistry;
	private final String expLog = "Invocació del mètode del ws Tramitacio Externa: ";
			

	private String usuariAutenticat = " usuari autenticat: " ;



	@Override
	public String iniciExpedient(
			String entorn,
			String usuari,
			String expedientTipus,
			String numero,
			String titol,
			List<ParellaCodiValor> valorsFormulari) throws TramitacioException {
				
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", expedientTipus=" + expedientTipus 
				+ ", numero=" + numero 
				+ ", titol=" + titol 
				+ ", " + usuariAutenticat
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+ "]"));
		
		EntornDto e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb aquest codi '" + entorn + "'");
		ExpedientTipusDto et = findExpedientTipusAmbEntornICodi(e.getId(), expedientTipus);
		if (et == null)
			throw new TramitacioException("No existeix cap tipus d'expedient amb el codi '" + expedientTipus + "'");
		if (numero != null && !et.isTeNumero())
			throw new TramitacioException("Aquest tipus d'expedient no suporta número d'expedient");
		if (titol != null && !et.isTeTitol())
			throw new TramitacioException("Aquest tipus d'expedient no suporta titol d'expedient");
		Map<String, Object> variables = null;
		if (valorsFormulari != null) {
			variables = new HashMap<String, Object>();
			for (ParellaCodiValor parella : valorsFormulari) {
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
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			ExpedientDto expedient = expedientService.create(
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
					ExpedientDto.IniciadorTipusDto.INTERN,
					null,
					null,
					null,
					null,
					null,
					false);	
			logger.info("Expedient " + expedient.getNumero() + " iniciat a través del WS de tramitació externa V1 " + expLog);
			return expedient.getProcessInstanceId();
		} catch (Exception ex) {
			logger.error("Error iniciant l'expedient a través del WS de tramitació externa "  + expLog + ": " + ex.getMessage(), ex);
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

		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
				
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
			PaginacioParamsDto paginacio = this.findTotesPaginades(); 
		
			PaginaDto<ExpedientTascaDto> tasques = tascaService.findPerFiltrePaginat(
					e.getId(), 
					null, 
					null, 
					null, 
					null, 
					usuari,
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					true, //nomesTasquesPersonals
					false, //nomesTasquesGrup
					false, //nomesTasquesMeves
					paginacio);	
			
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
	public List<TascaTramitacio> consultaTasquesGrup(
			String entorn,
			String usuari) throws TramitacioException {
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
		
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
			PaginacioParamsDto paginacio = this.findTotesPaginades();
		
			PaginaDto<ExpedientTascaDto> tasques = tascaService.findPerFiltrePaginat(
					e.getId(), 
					null, 
					null, 
					null, 
					null, 
					usuari,
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					false, //nomesTasquesPersonals
					true, //nomesTasquesGrup
					false, //nomesTasquesMeves
					paginacio);
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for(ExpedientTascaDto tasca : tasques) {
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
	public List<TascaTramitacio> consultaTasquesPersonalsByCodi(
			String entorn,
			String usuari,
			String codi) throws TramitacioException {
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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
		
			PaginacioParamsDto paginacio = this.findTotesPaginades();
			PaginaDto<ExpedientTascaDto> tasques = tascaService.findPerFiltrePaginat(
					e.getId(), null, null, null, null, usuari, null, null, null, null, null, null, false, true, false, paginacio);
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
	public List<TascaTramitacio> consultaTasquesGrupByCodi(
			String entorn,
			String usuari,
			String codi) throws TramitacioException {
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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
			
			PaginacioParamsDto paginacio = this.findTotesPaginades();
			
			PaginaDto<ExpedientTascaDto> tasques = tascaService.findPerFiltrePaginat(
					e.getId(), null, null, null, null, usuari, codi, null, null, null, null, null, false, true, false, paginacio);
			
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
	public List<TascaTramitacio> consultaTasquesPersonalsByProces(
			String entorn,
			String usuari,
			String processInstanceId) throws TramitacioException {
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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

			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);	
			PaginacioParamsDto paginacio = new PaginacioParamsDto();
			paginacio.afegirOrdre(
					"dataCreacio",
					OrdreDireccioDto.DESCENDENT);
			paginacio.setPaginaNum(0);
			paginacio.setPaginaTamany(1000);
			PaginaDto<ExpedientTascaDto> tasques = tascaService.findPerFiltrePaginat(
					e.getId(), null, expedient.getTipus().getId(), expedient.getTitol(), null, usuari, null, null, null, null, null, null, true, false, false, paginacio);
			
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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
			
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
			
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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
			
			if (this.isTasquesGrupTramitacio(e.getId(), tascaId, usuari)) {
				tascaService.agafar(tascaId);
				agafada = true;
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
	
	private boolean isTasquesGrupTramitacio(Long entornId, String tascaId, String usuari) {
		boolean res = false;
		String usuariBo = usuari;
		if (usuariBo == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			usuariBo = auth.getName();
		}
		try {
			
			PaginacioParamsDto paginacioParams = this.findTotesPaginades();
		
			paginacioParams.setPaginaNum(0);
			paginacioParams.setPaginaTamany(1000);
			PaginaDto<ExpedientTascaDto> tasques = tascaService.findPerFiltrePaginat(
					entornId,
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
					true,
					paginacioParams);
			for (ExpedientTascaDto tasca: tasques.getContingut()) {
				if (tasca.getId().equals(tascaId)) {
					res = true;
					break;
				}
			}
		} catch (NumberFormatException ignored) {}
		
		//mesuresTemporalsHelper.mesuraCalcular("is tasques grup", "consulta");
		return res;
	}
	
	private PaginacioParamsDto findTotesPaginades () {
		PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
		paginacioParams.setPaginaNum(0);
		paginacioParams.setPaginaTamany(-1);
		return paginacioParams;
	}

	@Override
	public void alliberarTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException {
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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
	
			PaginacioParamsDto paginacio = this.findTotesPaginades();

			PaginaDto<ExpedientTascaDto> expedientTasques = tascaService.findPerFiltrePaginat(
					e.getId(), null, null, null, null, usuari, null, null, null, null, null, null, true, false, false, paginacio);
			List<TascaTramitacio> tasques = new ArrayList<TascaTramitacio>();
			for (ExpedientTascaDto tasca: expedientTasques.getContingut()) {
				tasques.add(convertirTascaTramitacio(tasca));
			}
			
			for (TascaTramitacio tasca: tasques) {
				if (tasca.getId().equals(tascaId)) {
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+ "]"));
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
			
			// Consulta els camps de la tasca
			ExpedientTascaDto expedienTasca = tascaService.findAmbIdPerTramitacio(tascaId);
			Map<String, CampDto> camps = new HashMap<String, CampDto>();
			for (CampTascaDto campTasca : definicioProcesService.tascaCampFindAll(expedienTasca.getExpedientTipusId(), expedienTasca.getTascaId())) {
				camps.put(campTasca.getCamp().getCodi(), campTasca.getCamp());
			}
			List<CampTasca> resposta = new ArrayList<CampTasca>();
			for (TascaDadaDto campTasca: tascaService.findDades(tascaId))
				resposta.add(
						convertirCampTasca(
								campTasca,
								camps.get(campTasca.getVarCodi())));
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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
			tascaService.validar(tascaId, variables);
			
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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


			// Consulta els documents de la tasca
			ExpedientTascaDto expedienTasca = tascaService.findAmbIdPerTramitacio(tascaId);
			Map<String, DocumentDto> documents = new HashMap<String, DocumentDto>();
			for (DocumentTascaDto documentTasca : definicioProcesService.tascaDocumentFindAll(expedienTasca.getExpedientTipusId(), expedienTasca.getTascaId())) {
				documents.put(documentTasca.getDocument().getCodi(), documentTasca.getDocument());
			}
			List<DocumentTasca> resposta = new ArrayList<DocumentTasca>();
			for (TascaDocumentDto documentTasca: tascaService.findDocuments(tascaId)) {
				if (documentTasca.getDocumentStoreId() != null) {
					resposta.add(
							convertirDocumentTasca(
									documentTasca,
									documents.get(documentTasca.getDocumentCodi())));
				}
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
			String documentCodi,
			String arxiuNom,
			Date documentData,
			byte[] arxiuContingut) throws TramitacioException {
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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

			if(tascaService.isTascaValidada(tascaId)) {
				tascaService.guardarDocumentTasca(
						e.getId(), 
						tascaId, 
						documentCodi, 
						documentData, 
						arxiuNom, 
						arxiuContingut, 
						new MimetypesFileTypeMap().getContentType(arxiuNom), 
						false, 
						false, 
						null, 
						usuari);
			} else {
				logger.error("Tasca no validada");
				throw new TramitacioException("error.tascaService.noValidada");	
			}		
			
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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

			if(tascaService.isTascaValidada(tascaId))
				documentService.delete(Long.valueOf(document));
			else {
				logger.error("Tasca no validada");
				throw new TramitacioException("error.tascaService.noValidada");	
			}					
			
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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
			tascaService.completar(tascaId, transicio);
			
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entornCodi 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
			if (expedient == null) {
				throw new Exception("No s'ha trobat cap expedient amb process instance id " + processInstanceId);
			}
			return toExpedientInfo(expedient);
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
			
			// Consulta els valors
			Map<String, Object> valors = new HashMap<String, Object>();
			for ( ExpedientDadaDto expedientDada : expedientDadaService.findAmbInstanciaProces(expedient.getId(), processInstanceId)) {
				valors.put(expedientDada.getVarCodi(), expedientDada.getVarValor());
			}
			// Emplena el resultat
			for (CampDto campVar : dissenyService.findCampsOrdenatsPerCodi(expedient.getTipus().getId(), instanciaProces.getDefinicioProces().getId(), true)) {
				if (valors.containsKey(campVar.getCodi())) {
					resposta.add(convertirCampProces(
							campVar,
							valors.get(campVar.getCodi())));
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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
			if (expedientId == null) {
				throw new TramitacioException("No s'ha trobat cap expedient per la instància de procés pogut guardar la variable al procés: " + processInstanceId);
			}
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
						expedientId,
						processInstanceId,
						varCodi,
						valor);
			} else if (valor instanceof XMLGregorianCalendar) {
				expedientDadaService.update(
						expedientId,
						processInstanceId,
						varCodi,
						((XMLGregorianCalendar)valor).toGregorianCalendar().getTime());
			} else {
				expedientDadaService.update(
						expedientId,
						processInstanceId,
						varCodi,
						valor);
			}
		} catch (NoTrobatException ex) {
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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

			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
			if (expedient == null) {
				throw new Exception("No s'ha trobat cap expedient amb process instance id " + processInstanceId);
			}

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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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
			
			for (net.conselldemallorca.helium.v3.core.api.dto.DocumentDto document: instanciaProces.getVarsDocuments().values()) {
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [documentId= " + documentId + ", " 
				+ usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+ "]"));
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
				ArxiuDto arxiu = expedientDocumentService.arxiuFindAmbDocumentStoreId(documentId);
				if (arxiu != null) {
					resposta = new ArxiuProces();
					resposta.setNom(arxiu.getNom());
					resposta.setContingut(arxiu.getContingut());

					logger.info(this.expLog.concat(
							this.getNomMetode()
							+ " ["
							+ ", document id =" + documentId
							+ ", document nom =" + arxiu.getNom() 
							+ ", " + usuariAutenticat
							+ SecurityContextHolder.getContext().getAuthentication().getName()
							+ "]"));
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
		
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+"]"));
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

			DocumentDto document = documentService.findAmbCodi(
					instanciaProces.getDefinicioProces().getExpedientTipus() != null ? instanciaProces.getDefinicioProces().getExpedientTipus().getId() : null,
					instanciaProces.getDefinicioProces().getId(),
					documentCodi,
					true);
			if (document == null)
				throw new TramitacioException("No s'ha pogut trobar el document amb codi " + documentCodi);
			if (data == null)
				data = new Date();
			
			return expedientDocumentService.guardarDocumentProces(
					processInstanceId,
					documentCodi,
					data,
					arxiu,
					contingut);
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+ "]"));
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
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
			if (expedient == null)
				throw new TramitacioException("No s'ha pogut trobar l'expedient per la instancia de proces amb id " + processInstanceId);
			expedientDocumentService.delete(
					expedient.getId(), 
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+ "]"));
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
			
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
			if (expedient == null)
				throw new TramitacioException("No s'ha pogut trobar l'expedient per la instancia de proces amb id " + processInstanceId);
			expedientService.executarCampAccio(expedient.getId(), processInstanceId, accio);	
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+ "]"));
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
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
			if (expedient == null)
				throw new TramitacioException("No s'ha pogut trobar l'expedient per la instancia de proces amb id " + processInstanceId);
			expedientService.procesScriptExec(expedient.getId(), processInstanceId, script);;
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+ "]"));
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

			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
			if (expedient == null)
				throw new TramitacioException("No s'ha pogut trobar l'expedient per la instancia de proces amb id " + processInstanceId);
			expedientService.aturar(expedient.getId(), motiu);	
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn 
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+ "]"));
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
			
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
			if (expedient == null)
				throw new TramitacioException("No s'ha pogut trobar l'expedient per la instancia de proces amb id " + processInstanceId);
			expedientService.reprendre(expedient.getId());
			
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
			String entornCodi,
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entornCodi=" + entornCodi
				+ ", expedientTipus=" + expedientTipusCodi 
				+ ", numero=" + numero 
				+ ", titol=" + titol 
				+ ", " + usuariAutenticat
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+ "]"));
		EntornDto entorn = findEntornAmbCodi(entornCodi);
		if (entorn == null)
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
						entorn.getCodi()));
		final Timer.Context contextEntorn = timerEntorn.time();
		Counter countEntorn = metricRegistry.counter(
				MetricRegistry.name(
						TramitacioService.class,
						"expedientConsulta.count",
						entorn.getCodi()));
		countEntorn.inc();
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			// Obtencio de dades per a fer la consulta
			ExpedientTipusDto expedientTipus = null; 
			Long estatId = null;
			if (expedientTipusCodi != null && expedientTipusCodi.length() > 0) {
				expedientTipus = expedientTipusService.findAmbCodi(entorn.getId(),  expedientTipusCodi);
				
				if (estatCodi != null && estatCodi.length() > 0) {
					EstatDto estatDto = expedientTipusService.estatFindAmbCodi(expedientTipus.getId(), estatCodi);
					estatId = estatDto != null ? estatDto.getId() : null;
				}
			}
			// Estableix l'usuari autenticat
			establirUsuariAutenticat(usuari);
			
			EstatTipusDto estatTipus = null;
			if (iniciat) {
				estatTipus = EstatTipusDto.INICIAT;
			} else if (finalitzat) {
				estatTipus = EstatTipusDto.FINALITZAT;
			} else if (estatId != null) {
				estatTipus = EstatTipusDto.CUSTOM;
			}
			// Consulta d'expedients
			List<Long> expedientsIds = expedientService.findIdsAmbFiltre(
					entorn.getId(),
					(expedientTipus != null? expedientTipus.getId() : null),
					titol,
					numero,
					dataInici1,
					dataInici2,
					null, //dataFiInicial
					null, //dataFiFinal
					estatTipus,
					estatId,
					geoPosX,
					geoPosY,
					geoReferencia,
					null, //registreNumero
					false, //filtreCommand.isNomesTasquesPersonals(),
					false, //filtreCommand.isNomesTasquesGrup(),
					false, //filtreCommand.isNomesAlertes(),
					false, //filtreCommand.isNomesErrors(),
					MostrarAnulatsDto.NO); //
				
			List<net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto> expedients = expedientService.findAmbIds(new HashSet<Long>(expedientsIds));
			// Construcció de la resposta
			List<ExpedientInfo> resposta = new ArrayList<ExpedientInfo>();
			for (net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto dto: expedients)
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn
				+ ", usuari=" + usuari
				+ ", " + usuariAutenticat 
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+ "]"));
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
			
			ExpedientDto expedient = expedientService.findExpedientAmbProcessInstanceId(processInstanceId);
			expedientService.delete(expedient.getId());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	private EntornDto findEntornAmbCodi(String codi) {
		EntornDto entornDto = entornService.findAmbCodi(codi);
		if (entornDto != null)
			EntornActual.setEntornId(entornDto.getId());
		return entornDto;
	}
	private ExpedientTipusDto findExpedientTipusAmbEntornICodi(Long entornId, String codi) {
		return expedientTipusService.findAmbCodi(entornId, codi);
	}

	private TascaTramitacio convertirTascaTramitacio(ExpedientTascaDto tasca) {
		TascaTramitacio tt = new TascaTramitacio();
		tt.setId(tasca.getId());
		tt.setCodi(tasca.getJbpmName());
		tt.setTitol(tasca.getTitol());
		tt.setMissatgeInfo(tasca.getTascaMissatgeInfo());
		tt.setMissatgeWarn(tasca.getTascaMissatgeWarn());
		if (tasca.getResponsable() != null)
			tt.setResponsable(tasca.getResponsable().getCodi());
		Set<String> responsables = new HashSet<String>();
		if (tasca.getResponsables() != null)
			for (PersonaDto responsable: tasca.getResponsables()) {
				responsables.add(responsable.getCodi());
			}
		tt.setResponsables(responsables);
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
			TascaDadaDto campTasca, 
			CampDto campDto) {
		CampTasca ct = new CampTasca();
		ct.setCodi(campTasca.getVarCodi());
		ct.setEtiqueta(campTasca.getCampEtiqueta());
		ct.setTipus(campTasca.getCampTipus().name());
		ct.setObservacions(campTasca.getObservacions());
		if (campDto != null) {
			ct.setDominiId(campDto.getDominiIdentificador());
			ct.setDominiParams(campDto.getDominiParams());
			ct.setDominiCampValor(campDto.getDominiCampValor());
			ct.setDominiCampText(campDto.getDominiCampText());
		}
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
			TascaDocumentDto documentTasca,
			DocumentDto document) {
		DocumentTasca dt = new DocumentTasca();
		dt.setId(documentTasca.getDocumentStoreId());
		if (document != null) {
			dt.setCodi(document.getCodi());
			dt.setNom(document.getNom());
			dt.setDescripcio(document.getDescripcio());
		} else {
			dt.setCodi(documentTasca.getDocumentCodi());
			dt.setNom(documentTasca.getDocumentNom());
		}
		dt.setArxiu(documentTasca.getArxiuNom());
		dt.setData(documentTasca.getDataDocument());
		if (documentTasca.isSignat()) {
			dt.setUrlCustodia(documentTasca.getUrlVerificacioCustodia());
		}
		return dt;
	}
	private CampProces convertirCampProces(
			CampDto campVar,
			Object valor) {
		CampProces ct = new CampProces();
		if (campVar != null) {
			ct.setCodi(campVar.getCodi());
			ct.setEtiqueta(campVar.getEtiqueta());
			ct.setTipus(campVar.getTipus().name());
			ct.setObservacions(campVar.getObservacions());
			ct.setDominiId(campVar.getDomini() != null ? String.valueOf(campVar.getDomini().getId()) : null);
			ct.setDominiParams(campVar.getDominiParams());
			ct.setDominiCampValor(campVar.getDominiCampValor());
			ct.setDominiCampText(campVar.getDominiCampText());
			ct.setJbpmAction(campVar.getJbpmAction());
			ct.setMultiple(campVar.isMultiple());
			ct.setOcult(campVar.isOcult());
			ct.setValor(valor);
		}
		return ct;
	}
	private DocumentProces convertirDocumentProces(net.conselldemallorca.helium.v3.core.api.dto.DocumentDto document) {
		DocumentProces dt = new DocumentProces();
		dt.setId(document.getId());
		dt.setCodi(document.getDocumentCodi());
		dt.setNom(document.getDocumentNom());
		dt.setArxiu(document.getArxiuNom());
		dt.setData(document.getDataDocument());
		return dt;
	}
	
	private ExpedientInfo toExpedientInfo (ExpedientDto expedient) {
		if(expedient!=null) {
			ExpedientInfo resposta = new ExpedientInfo();
			resposta.setTitol(expedient.getTitol());
			resposta.setNumero(expedient.getNumero());
			resposta.setNumeroDefault(expedient.getNumeroDefault());
			resposta.setIdentificador(expedient.getIdentificador());
			resposta.setDataInici(expedient.getDataInici());
			resposta.setDataFi(expedient.getDataFi());
			resposta.setComentari(expedient.getComentari());
			resposta.setInfoAturat(expedient.getInfoAturat());
			if (expedient.getIniciadorTipus().equals(net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus.INTERN))
				resposta.setIniciadorTipus(net.conselldemallorca.helium.ws.tramitacio.ExpedientInfo.IniciadorTipus.INTERN);
			else if (expedient.getIniciadorTipus().equals(net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus.SISTRA))
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

	@Autowired
	public EntornService getEntornService() {
		return entornService;
	}
	@Autowired
	public void setEntornService(EntornService entornService) {
		this.entornService = entornService;
	}
	@Autowired
	public ExpedientService getExpedientService() {
		return expedientService;
	}
	@Autowired
	public void setExpedientService(
			ExpedientService expedientService) {
		this.expedientService = expedientService;
	}
	@Autowired
	public DissenyService getDissenyService() {
		return dissenyService;
	}
	@Autowired
	public void setDissenyService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}
	@Autowired
	public ExpedientTipusService getExpedientTipusService() {
		return expedientTipusService;
	}
	@Autowired
	public void setExpedientTipusService(
			ExpedientTipusService expedientTipusService) {
		this.expedientTipusService = expedientTipusService;
	}

	@Autowired
	public void setTascaService(TascaService tascaService) {
		this.tascaService = tascaService;
	}
	@Autowired
	public JbpmHelper getJbpmHelper() {
		return jbpmHelper;
	}
	@Autowired
	public void setJbpmHelper(JbpmHelper jbpmHelper) {
		this.jbpmHelper = jbpmHelper;
	}
	@Autowired
	public DocumentService getDocumentService() {
		return documentService;
	}
	@Autowired
	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}
	@Autowired
	public TascaService getTascaService() {
		return tascaService;
	}
	@Autowired
	public DefinicioProcesService getDefinicioProcesService() {
		return definicioProcesService;
	}
	@Autowired
	public void setDefinicioProcesService(DefinicioProcesService definicioProcesService) {
		this.definicioProcesService = definicioProcesService;
	}
	@Autowired
	public ExpedientDocumentService getExpedientDocumentService() {
		return expedientDocumentService;
	}
	@Autowired
	public void setExpedientDocumentService(ExpedientDocumentService expedientDocumentService) {
		this.expedientDocumentService = expedientDocumentService;
	}
	

	/** Obté el nom del mètode des d'on es crida */
	private String getNomMetode() {
		return new Exception().getStackTrace()[1].getMethodName();
	}


	private static final Log logger = LogFactory.getLog(TramitacioServiceImpl.class);

}
