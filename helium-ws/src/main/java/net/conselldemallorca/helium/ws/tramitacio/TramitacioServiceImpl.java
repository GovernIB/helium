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

import net.conselldemallorca.helium.core.helper.DocumentHelperV3;
import net.conselldemallorca.helium.core.helper.ExpedientHelper;
import net.conselldemallorca.helium.core.helper.ExpedientLoggerHelper;
import net.conselldemallorca.helium.core.helper.ExpedientRegistreHelper;
import net.conselldemallorca.helium.core.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientLog.ExpedientLogAccioTipus;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmHelper;
import net.conselldemallorca.helium.jbpm3.integracio.JbpmTask;
import net.conselldemallorca.helium.jbpm3.integracio.ResultatConsultaPaginadaJbpm;
import net.conselldemallorca.helium.v3.core.api.dto.CampDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.CampTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.DocumentTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.EntornDto;
import net.conselldemallorca.helium.v3.core.api.dto.EstatDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTascaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.MostrarAnulatsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginaDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto;
import net.conselldemallorca.helium.v3.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.exception.NoTrobatException;
import net.conselldemallorca.helium.v3.core.api.service.DefinicioProcesService;
import net.conselldemallorca.helium.v3.core.api.service.DissenyService;
import net.conselldemallorca.helium.v3.core.api.service.DocumentService;
import net.conselldemallorca.helium.v3.core.api.service.EntornService;
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
	private JbpmHelper jbpmHelper;
	@Autowired
	private DocumentHelperV3 documentHelper;
	@Autowired
	private ExpedientLoggerHelper expedientLoggerHelper;
	@Autowired
	private ExpedientHelper expedientHelper;
	@Autowired
	private ExpedientRegistreHelper expedientRegistreHelper;
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
//		if (numero != null && !et.getTeNumero())
//			throw new TramitacioException("Aquest tipus d'expedient no suporta número d'expedient");
//		if (titol != null && !et.getTeTitol())
//			throw new TramitacioException("Aquest tipus d'expedient no suporta titol d'expedient");
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
			
			net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto expedient = expedientService.create(
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
					net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto.INTERN,
					null,
					null,
					null,
					null,
					null,
					false);	
			logger.info("Expedient " + numero + " iniciat correctament");
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
		
			PaginaDto<ExpedientTascaDto> tasques2 = tascaService.findPerFiltrePaginat(
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
			for(ExpedientTascaDto tasca : tasques2) {
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

			net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto expedient = expedientService.findAmbIdAmbPermis(Long.valueOf(processInstanceId));	
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
			
			net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto expedient = expedientService.findAmbIdAmbPermis(Long.valueOf(processInstanceId));
			
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
		
			ResultatConsultaPaginadaJbpm<JbpmTask> tasks = jbpmHelper.tascaFindByFiltrePaginat(
					entornId,
					usuariBo,
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
					false, // tasquesPersona
					true, // tasquesGrup
					true, // nomesPendents
					paginacioParams);
			for (JbpmTask task: tasks.getLlista()) {
				if (task.getId().equals(tascaId)) {
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
	public List<CampTascaDto> consultaFormulariTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException {
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn
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
			
			net.conselldemallorca.helium.v3.core.api.dto.TascaDto tasca = tascaService.findTascaById(Long.valueOf(tascaId));	
			List<CampTascaDto> resposta = new ArrayList<CampTascaDto>();
			resposta.addAll(tasca.getCamps());
			
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
	public List<DocumentTascaDto> consultaDocumentsTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException {
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn
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

			net.conselldemallorca.helium.v3.core.api.dto.TascaDto tasca = tascaService.findTascaById(Long.valueOf(tascaId));
			List<DocumentTascaDto> resposta = tasca.getDocuments();

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

			if(tascaService.isTascaValidada(tascaId))
				this.guardarDocumentTasca(
						e.getId(), 
						tascaId, 
						documentCodi, 
						documentData, 
						arxiuNom, 
						arxiuContingut, 
						new MimetypesFileTypeMap().getContentType(arxiuNom),
						usuari);
			else {
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
	
	private Long guardarDocumentTasca(
			Long entornId,
			String taskInstanceId,
			String documentCodi,
			Date documentData,
			String arxiuNom,
			byte[] arxiuContingut,
			String arxiuContentType, 
			String user) {
		logger.debug("Crear document a dins la tasca (" +
				"entornId=" + entornId + ", " +
				"taskInstanceId=" + taskInstanceId + ", " +
				"documentCodi=" + documentCodi + ", " +
				"documentData=" + documentData + ", " +
				"arxiuNom=" + arxiuNom + ", " +
				"arxiuContingut=" + arxiuContingut + ", " +
				"arxiuContentType=" + arxiuContentType + ", " +
				"user=" + user + ")");
		JbpmTask task = jbpmHelper.getTaskById(taskInstanceId);
		DocumentStore documentStore = documentHelper.getDocumentStore(task, documentCodi);
		Expedient expedient = expedientHelper.findExpedientByProcessInstanceId(task.getProcessInstanceId());
		boolean creat = (documentStore == null);
		if (creat) {
			expedientLoggerHelper.afegirLogExpedientPerTasca(
					taskInstanceId,
					ExpedientLogAccioTipus.TASCA_DOCUMENT_AFEGIR,
					documentCodi);
		} else {
			expedientLoggerHelper.afegirLogExpedientPerTasca(
					taskInstanceId,
					ExpedientLogAccioTipus.TASCA_DOCUMENT_MODIFICAR,
					documentCodi);
		}
		String arxiuNomAntic = (documentStore != null) ? documentStore.getArxiuNom() : null;
		
		Long documentStoreId = documentHelper.crearActualitzarDocument(
				taskInstanceId, 
				taskInstanceId, 
				documentCodi, 
				documentData, 
				arxiuNomAntic, 
				arxiuContingut, 
				null, 
				null, 
				null, 
				documentCodi);
		// Registra l'acció
		if (user == null) {
			user = SecurityContextHolder.getContext().getAuthentication().getName();
		}
		if (creat) {
			expedientRegistreHelper.crearRegistreCrearDocumentTasca(
					expedient.getId(),
					taskInstanceId,
					user,
					documentCodi,
					arxiuNom);
		} else {
			expedientRegistreHelper.crearRegistreModificarDocumentTasca(
					expedient.getId(),
					taskInstanceId,
					user,
					documentCodi,
					arxiuNomAntic,
					arxiuNom);
		}
		return documentStoreId;
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
			
			net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto expedient = expedientService.findAmbIdAmbPermis(Long.valueOf(processInstanceId));
			ExpedientInfo expInfo = toExpedientInfo(expedient);
			return expInfo;		

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

			net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId);
			
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn
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

				expedientService.update(null, varCodi, varCodi, entorn, null, usuari, null, null, null, processInstanceId, varCodi, false);

			} else if (valor instanceof XMLGregorianCalendar) {

				expedientService.update(null, varCodi, varCodi, entorn, null, usuari, null, null, null, processInstanceId, varCodi, false);

			} else {
			
				expedientService.update(null, varCodi, varCodi, entorn, null, usuari, null, null, null, processInstanceId, varCodi, false);

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

			net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto expedient = expedientService.findAmbIdAmbPermis(Long.valueOf(processInstanceId));
			expedientService.delete(expedient.getId());
			
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

			net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId);
			
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
				+ " [" 
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

				net.conselldemallorca.helium.v3.core.api.dto.ArxiuDto arxiu = documentService.getArxiu(documentId);
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
	public void setDocumentProces(
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
			
			net.conselldemallorca.helium.v3.core.api.dto.InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId);
			if (instanciaProces == null)
				throw new TramitacioException("No s'ha pogut trobar la instancia de proces amb id " + processInstanceId);

			net.conselldemallorca.helium.v3.core.api.dto.DocumentDto documentdto = null;
			ExpedientInfo expInfo = this.getExpedientInfo(e.getCodi(),usuari,processInstanceId); 
			ExpedientTipusDto expTipusDto = expedientTipusService.findAmbId(expInfo.getExpedientTipusCodi() != null ? Long.valueOf(expInfo.getExpedientTipusCodi()): null);		
			if (expTipusDto.isAmbInfoPropia()) 
				documentdto = documentService.findAmbCodi(expTipusDto.getId(), null, documentCodi, expTipusDto.isAmbHerencia());
			else 
				documentdto = documentService.findAmbCodi(null, instanciaProces.getDefinicioProces().getId(), documentCodi, false);		
			
			if (documentdto == null)
				throw new TramitacioException("No s'ha pogut trobar el document amb codi " + documentCodi);
	
			net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto expedient = expedientService.findAmbIdAmbPermis(Long.valueOf(processInstanceId));	
			ExpedientDocumentDto expDocDto = expedientDocumentService.findOneAmbInstanciaProces(expedient.getId(), processInstanceId, documentCodi);
			//DocumentStore documentStore = documentHelper.getDocumentStore(task, documentCodi);			
			if(expDocDto!=null) 
				expedientDocumentService.update( 
						expedient.getId(),
						processInstanceId,
						expDocDto.getId(), //Long documentStoreId,
						(data != null ? data : expDocDto.getDataDocument()),
						expDocDto.getAdjuntTitol(), //String adjuntTitol
						arxiu,
						contingut,
						new MimetypesFileTypeMap().getContentType(arxiu),
						false, //boolean ambFirma,
						false, //boolean firmaSeparada,
						null, //byte[] firmaContingut,
						expDocDto.getNtiOrigen(), //NtiOrigenEnumDto ntiOrigen,
						expDocDto.getNtiEstadoElaboracion(), //NtiEstadoElaboracionEnumDto ntiEstadoElaboracion,
						expDocDto.getNtiTipoDocumental(), //NtiTipoDocumentalEnumDto ntiTipoDocumental,
						expDocDto.getNtiIdOrigen() //String ntiIdOrigen
						) ;
				
			
			else 
				expedientDocumentService.create(
						expedient.getId(),
						processInstanceId,
						documentCodi, // null en el cas dels adjunts
						(data != null ? data : new Date()),
						null, // Títol en el cas dels adjunts (al crear ja li posa el nom del document)
						arxiu,
						contingut,
						new MimetypesFileTypeMap().getContentType(arxiu),
						false, //command.isAmbFirma(),
						false, //DocumentTipusFirmaEnumDto.SEPARAT.equals(command.getTipusFirma()),
						null, //firmaContingut,
						null, //command.getNtiOrigen(),
						null, //command.getNtiEstadoElaboracion(),
						null, //command.getNtiTipoDocumental(),
						null  //command.getNtiIdOrigen()
						);
			
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
			documentService.delete(documentId);		
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
			
			net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto expedient = expedientService.findAmbIdAmbPermis(Long.valueOf(processInstanceId));
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
			jbpmHelper.evaluateScript(processInstanceId, script, new HashSet<String>());		
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

			net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto expedient = expedientService.findAmbIdAmbPermis(Long.valueOf(processInstanceId));
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
			
			net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto expedient = expedientService.findAmbIdAmbPermis(Long.valueOf(processInstanceId));
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
		logger.info(this.expLog.concat(
				this.getNomMetode()
				+ " [entorn=" + entorn
				+ ", expedientTipus=" + expedientTipusCodi 
				+ ", numero=" + numero 
				+ ", titol=" + titol 
				+ ", " + usuariAutenticat
				+ SecurityContextHolder.getContext().getAuthentication().getName()
				+ "]"));
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
					EstatDto estatDto = expedientTipusService.estatFindAmbCodi(expedientTipus.getId(), estatCodi);
					estatId = estatDto != null ? estatDto.getId() : null;
				}
			}
			// Estableix l'usuari autenticat
			establirUsuariAutenticat(usuari);
			// Consulta d'expedients
			List<Long> expedientsids = expedientService.findIdsAmbFiltre(
					e.getId(),
					(expedientTipus != null? expedientTipus.getId() : null),
					titol,
					numero,
					dataInici1,
					dataInici2,
					null, //dataFiInicial
					null, //dataFiFinal
					EstatTipusDto.INICIAT, //??
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
				
			Set<Long> seleccio = new HashSet<Long>();
			if (expedientsids != null) {
				for (Long id: expedientsids)
							seleccio.add(id);			
			}
			List<net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto> expedients = expedientService.findAmbIds(seleccio);
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
			
			net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto expedient = expedientService.findAmbIdAmbPermis(Long.valueOf(processInstanceId));
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
//		return expedientTipusService.findAmbCodiPerValidarRepeticio(entornId, codi);
		return expedientTipusService.findAmbCodi(entornId, codi);
	}

	private TascaTramitacio convertirTascaTramitacio(ExpedientTascaDto tasca) {
		TascaTramitacio tt = new TascaTramitacio();
		tt.setId(tasca.getId());
		tt.setCodi(tasca.getJbpmName());
		tt.setTitol(tasca.getTitol());
		//tt.setExpedient(tasca.getex.gete.getExpedientNumero());
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
	
	private ExpedientInfo toExpedientInfo (net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto expedient) {
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
