/**
 * 
 */
package net.conselldemallorca.helium.ws.tramitacio;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import net.conselldemallorca.helium.core.model.dto.ArxiuDto;
import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.dto.TascaLlistatDto;
import net.conselldemallorca.helium.core.model.exception.NotFoundException;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.Estat;
import net.conselldemallorca.helium.core.model.hibernate.Expedient;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.DocumentService;
import net.conselldemallorca.helium.core.model.service.EntornService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.util.EntornActual;
import net.conselldemallorca.helium.v3.core.api.service.ExpedientService.FiltreAnulat;

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

	private EntornService entornService;
	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private TascaService tascaService;
	private DocumentService documentService;

	@Autowired
	private MetricRegistry metricRegistry;



	public String iniciExpedient(
			String entorn,
			String usuari,
			String expedientTipus,
			String numero,
			String titol,
			List<ParellaCodiValor> valorsFormulari) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb aquest codi '" + entorn + "'");
		ExpedientTipus et = findExpedientTipusAmbEntornICodi(e.getId(), expedientTipus);
		if (et == null)
			throw new TramitacioException("No existeix cap tipus d'expedient amb el codi '" + expedientTipus + "'");
		if (numero != null && !et.getTeNumero())
			throw new TramitacioException("Aquest tipus d'expedient no suporta número d'expedient");
		if (titol != null && !et.getTeTitol())
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
		try {
			Authentication authentication =  new UsernamePasswordAuthenticationToken(usuari, null);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			ExpedientDto expedient;
			synchronized (ExpedientService.getObjecteSincronitzacio(et.getId())) 
			{
				expedient = expedientService.iniciar(
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
						IniciadorTipus.INTERN,
						null,
						null,
						null,
						null);
			}
			return expedient.getProcessInstanceId();
		} catch (Exception ex) {
			logger.error("No s'han pogut iniciar l'expedient", ex);
			throw new TramitacioException("No s'han pogut iniciar l'expedient: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
			contextExptip.stop();
		}
	}

	public List<TascaTramitacio> consultaTasquesPersonals(
			String entorn,
			String usuari) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			List<TascaLlistatDto> tasques = tascaService.findTasquesPersonalsTramitacio(e.getId(), usuari, null, true);
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (TascaLlistatDto tasca: tasques)
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

	public List<TascaTramitacio> consultaTasquesGrup(
			String entorn,
			String usuari) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			List<TascaLlistatDto> tasques = tascaService.findTasquesGrupTramitacio(e.getId(), usuari, null, true);
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (TascaLlistatDto tasca: tasques)
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

	public void agafarTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			if (tascaService.isTasquesGrupTramitacio(e.getId(), tascaId, usuari)) {
				tascaService.agafar(e.getId(), usuari, tascaId);
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
	
	public void alliberarTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			List<TascaLlistatDto> tasques = tascaService.findTasquesPersonalsTramitacio(e.getId(), usuari, null, false);
			for (TascaLlistatDto tasca: tasques) {
				if (tasca.getId().equals(tascaId)) {
					tascaService.alliberar(e.getId(), usuari, tascaId, true);
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

	public List<CampTasca> consultaFormulariTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			TascaDto tasca = tascaService.getById(
					e.getId(),
					tascaId,
					usuari,
					null,
					true,
					false);
			List<CampTasca> resposta = new ArrayList<CampTasca>();
			for (net.conselldemallorca.helium.core.model.hibernate.CampTasca campTasca: tasca.getCamps())
				resposta.add(convertirCampTasca(
						campTasca,
						tasca.getVariable(campTasca.getCamp().getCodi())));
			return resposta;
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	public void setDadesFormulariTasca(
			String entorn,
			String usuari,
			String tascaId,
			List<ParellaCodiValor> valors) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
					e.getId(),
					tascaId,
					variables,
					true,
					usuari);
		} catch (Exception ex) {
			logger.error("No s'han pogut guardar les variables a la tasca", ex);
			throw new TramitacioException("No s'han pogut guardar les variables a la tasca: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	public List<DocumentTasca> consultaDocumentsTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			TascaDto tasca = tascaService.getById(
					e.getId(),
					tascaId,
					usuari,
					null,
					true,
					false);
			List<DocumentTasca> resposta = new ArrayList<DocumentTasca>();
			for (net.conselldemallorca.helium.core.model.hibernate.DocumentTasca documentTasca: tasca.getDocuments())
				resposta.add(convertirDocumentTasca(
						documentTasca,
						tasca.getVarsDocuments().get(documentTasca.getDocument().getCodi())));
			return resposta;
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	public void setDocumentTasca(
			String entorn,
			String usuari,
			String tascaId,
			String arxiu,
			String nom,
			Date data,
			byte[] contingut) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			tascaService.comprovarTascaAssignadaIValidada(e.getId(), tascaId, usuari);
			documentService.guardarDocumentTasca(
					e.getId(),
					tascaId,
					arxiu,
					data,
					nom,
					contingut);
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar el document a la tasca", ex);
			throw new TramitacioException("No s'ha pogut guardar el document a la tasca: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}
	public void esborrarDocumentTasca(
			String entorn,
			String usuari,
			String tascaId,
			String document) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			tascaService.comprovarTascaAssignadaIValidada(e.getId(), tascaId, usuari);
			documentService.esborrarDocument(
					tascaId,
					null,
					document);
		} catch (Exception ex) {
			logger.error("No s'ha pogut esborrar el document de la tasca", ex);
			throw new TramitacioException("No s'ha pogut esborrar el document de la tasca: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	public void finalitzarTasca(
			String entorn,
			String usuari,
			String tascaId,
			String transicio) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
					e.getId(),
					tascaId,
					true,
					usuari,
					transicio);
		} catch (Exception ex) {
			logger.error("No s'ha pogut completar la tasca", ex);
			throw new TramitacioException("No s'ha pogut completar la tasca: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	public List<CampProces> consultarVariablesProces(
			String entorn,
			String usuari,
			String processInstanceId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId, true, true, true);
			if (instanciaProces.getVariables() != null) {
				for (String var: instanciaProces.getVariables().keySet()) {
					Camp campVar = null;
					for (Camp camp: instanciaProces.getCamps()) {
						if (camp.getCodi().equals(var)) {
							campVar = camp;
							break;
						}
					}
					if (campVar == null) {
						campVar = new Camp();
						campVar.setCodi(var);
						campVar.setEtiqueta(var);
						campVar.setTipus(TipusCamp.STRING);
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
	public void setVariableProces(
			String entorn,
			String usuari,
			String processInstanceId,
			String varCodi,
			Object valor) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
				expedientService.updateVariable(
						processInstanceId,
						varCodi,
						valor);
			} else if (valor instanceof XMLGregorianCalendar) {
				expedientService.updateVariable(
						processInstanceId,
						varCodi,
						((XMLGregorianCalendar)valor).toGregorianCalendar().getTime());
			} else {
				expedientService.updateVariable(
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
	public void esborrarVariableProces(
			String entorn,
			String usuari,
			String processInstanceId,
			String varCodi) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			expedientService.deleteVariable(
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

	public List<DocumentProces> consultarDocumentsProces(
			String entorn,
			String usuari,
			String processInstanceId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId, true, true, true);
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
				ArxiuDto arxiu = documentService.arxiuDocumentPerMostrar(documentId);
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
	public Long setDocumentProces(
			String entorn,
			String usuari,
			String processInstanceId,
			String documentCodi,
			String arxiu,
			Date data,
			byte[] contingut) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId, false, false, false);
			if (instanciaProces == null)
				throw new TramitacioException("No s'ha pogut trobar la instancia de proces amb id " + processInstanceId);
			Document document = dissenyService.findDocumentAmbDefinicioProcesICodi(
					instanciaProces.getDefinicioProces().getId(),
					documentCodi);
			if (document == null)
				throw new TramitacioException("No s'ha pogut trobar el document amb codi " + documentCodi);
			return documentService.guardarDocumentProces(
					processInstanceId,
					documentCodi,
					null,
					data,
					arxiu,
					contingut,
					false);
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar el document al procés", ex);
			throw new TramitacioException("No s'ha pogut guardar el document al procés: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}
	public void esborrarDocumentProces(
			String entorn,
			String usuari,
			String processInstanceId,
			Long documentId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			documentService.esborrarDocument(
					null,
					processInstanceId,
					documentService.getDocumentCodiPerDocumentStoreId(documentId));
		} catch (Exception ex) {
			logger.error("No s'ha pogut esborrar el document del procés", ex);
			throw new TramitacioException("No s'ha pogut esborrar el document del procés: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	public void executarAccioProces(
			String entorn,
			String usuari,
			String processInstanceId,
			String accio) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			expedientService.executarAccio(processInstanceId, accio);
		} catch (Exception ex) {
			logger.error("No s'ha pogut executar l'acció", ex);
			throw new TramitacioException("No s'ha pogut executar l'acció: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}
	public void executarScriptProces(
			String entorn,
			String usuari,
			String processInstanceId,
			String script) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			expedientService.evaluateScript(
					processInstanceId,
					script,
					null);
		} catch (Exception ex) {
			logger.error("No s'ha pogut executar l'script", ex);
			throw new TramitacioException("No s'ha pogut executar l'script: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}
	public void aturarExpedient(
			String entorn,
			String usuari,
			String processInstanceId,
			String motiu) throws TramitacioException{
		Entorn e = findEntornAmbCodi(entorn);
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
			
			expedientService.aturar(
					processInstanceId,
					motiu,
					usuari);
		} catch (Exception ex) {
			logger.error("No s'ha pogut aturar l'expedient", ex);
			throw new TramitacioException("No s'ha pogut aturar l'expedient: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}
	public void reprendreExpedient(
			String entorn,
			String usuari,
			String processInstanceId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			expedientService.reprendre(
					processInstanceId,
					usuari);
		} catch (Exception ex) {
			logger.error("No s'ha pogut reprendre l'expedient", ex);
			throw new TramitacioException("No s'ha pogut reprendre l'expedient: " + ex.getMessage());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}
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
		Entorn e = findEntornAmbCodi(entorn);
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
			ExpedientTipus expedientTipus = null;
			Long estatId = null;
			if (expedientTipusCodi != null && expedientTipusCodi.length() > 0) {
				expedientTipus = dissenyService.findExpedientTipusAmbEntornICodi(
					e.getId(),
					expedientTipusCodi);
			
				if (estatCodi != null && estatCodi.length() > 0) {
					for (Estat estat: expedientTipus.getEstats()) {
						if (estat.getCodi().equals(estatCodi)) {
							estatId = estat.getId();
							break;
						}
					}
				}
			} else {
				for (Estat estat: dissenyService.findEstatAmbEntorn(e.getId())) {
					if (estat.getCodi().equals(estatCodi)) {
						estatId = estat.getId();
						break;
					}
				}
			}
			// Estableix l'usuari autenticat
			establirUsuariAutenticat(usuari);
			// Consulta d'expedients
			List<ExpedientDto> expedients = expedientService.findAmbEntornConsultaGeneral(
					e.getId(),
					titol,
					numero,
					dataInici1,
					dataInici2,
					(expedientTipus != null? expedientTipus.getId() : null),
					estatId,
					iniciat,
					finalitzat,
					geoPosX,
					geoPosY,
					geoReferencia,
					FiltreAnulat.ACTIUS);
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

	public void deleteExpedient(
			String entorn,
			String usuari,
			String processInstanceId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			expedientService.delete(e.getId(), expedient.getId());
		} finally {
			contextTotal.stop();
			contextEntorn.stop();
		}
	}

	public List<TascaTramitacio> consultaTasquesPersonalsByCodi(
			String entorn,
			String usuari,
			String codi) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			List<TascaLlistatDto> tasques = tascaService.findTasquesPersonalsTramitacio(e.getId(), usuari, codi, true);
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (TascaLlistatDto tasca: tasques)
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

	public List<TascaTramitacio> consultaTasquesGrupByCodi(
			String entorn,
			String usuari,
			String codi) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
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
			
			List<TascaLlistatDto> tasques = tascaService.findTasquesGrupTramitacio(e.getId(), usuari, codi, true);
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (TascaLlistatDto tasca: tasques)
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



	@Autowired
	public void setEntornService(EntornService entornService) {
		this.entornService = entornService;
	}
	@Autowired
	public void setDissenyService(DissenyService dissenyService) {
		this.dissenyService = dissenyService;
	}
	@Autowired
	public void setExpedientService(ExpedientService expedientService) {
		this.expedientService = expedientService;
	}
	@Autowired
	public void setTascaService(TascaService tascaService) {
		this.tascaService = tascaService;
	}
	@Autowired
	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}



	private Entorn findEntornAmbCodi(String codi) {
		Entorn entorn = entornService.findAmbCodi(codi);
		if (entorn != null)
			EntornActual.setEntornId(entorn.getId());
		return entorn;
	}
	private ExpedientTipus findExpedientTipusAmbEntornICodi(Long entornId, String codi) {
		return dissenyService.findExpedientTipusAmbEntornICodi(entornId, codi);
	}
	private TascaTramitacio convertirTascaTramitacio(TascaLlistatDto tasca) {
		TascaTramitacio tt = new TascaTramitacio();
		tt.setId(tasca.getId());
		tt.setCodi(tasca.getCodi());
		tt.setTitol(tasca.getTitol());
		tt.setExpedient(tasca.getExpedientNumero());
		tt.setMissatgeInfo(tasca.getMissatgeInfo());
		tt.setMissatgeWarn(tasca.getMissatgeWarn());
		tt.setResponsable(tasca.getResponsable());
		tt.setResponsables(tasca.getResponsables());
		tt.setDataCreacio(tasca.getDataCreacio());
		tt.setDataInici(tasca.getDataInici());
		tt.setDataFi(tasca.getDataFi());
		tt.setDataLimit(tasca.getDataLimit());
		tt.setPrioritat(tasca.getPrioritat());
		tt.setOpen(tasca.isOberta());
		tt.setCompleted(tasca.isCompletada());
		tt.setCancelled(tasca.isCancelada());
		tt.setSuspended(tasca.isSuspesa());
		tt.setTransicionsSortida(tasca.getResultats());
		tt.setProcessInstanceId(tasca.getProcessInstanceId());
		return tt;
	}
	private CampTasca convertirCampTasca(
			net.conselldemallorca.helium.core.model.hibernate.CampTasca campTasca,
			Object valor) {
		CampTasca ct = new CampTasca();
		ct.setCodi(campTasca.getCamp().getCodi());
		ct.setEtiqueta(campTasca.getCamp().getEtiqueta());
		ct.setTipus(campTasca.getCamp().getTipus().name());
		ct.setObservacions(campTasca.getCamp().getObservacions());
		ct.setDominiId(campTasca.getCamp().getDominiId());
		ct.setDominiParams(campTasca.getCamp().getDominiParams());
		ct.setDominiCampValor(campTasca.getCamp().getDominiCampValor());
		ct.setDominiCampText(campTasca.getCamp().getDominiCampText());
		ct.setJbpmAction(campTasca.getCamp().getJbpmAction());
		ct.setReadFrom(campTasca.isReadFrom());
		ct.setWriteTo(campTasca.isWriteTo());
		ct.setRequired(campTasca.isRequired());
		ct.setReadOnly(campTasca.isReadOnly());
		ct.setMultiple(campTasca.getCamp().isMultiple());
		ct.setOcult(campTasca.getCamp().isOcult());
		ct.setValor(valor);
		return ct;
	}
	private DocumentTasca convertirDocumentTasca(
			net.conselldemallorca.helium.core.model.hibernate.DocumentTasca documentTasca,
			DocumentDto document) {
		DocumentTasca dt = new DocumentTasca();
		dt.setId(document.getId());
		dt.setCodi(documentTasca.getDocument().getCodi());
		dt.setNom(documentTasca.getDocument().getNom());
		dt.setDescripcio(documentTasca.getDocument().getDescripcio());
		dt.setArxiu(document.getArxiuNom());
		dt.setData(document.getDataDocument());
		if (document.isSignat()) {
			dt.setUrlCustodia(document.getUrlVerificacioCustodia());
		}
		return dt;
	}
	private CampProces convertirCampProces(
			Camp camp,
			Object valor) {
		CampProces ct = new CampProces();
		if (camp != null) {
			ct.setCodi(camp.getCodi());
			ct.setEtiqueta(camp.getEtiqueta());
			ct.setTipus(camp.getTipus().name());
			ct.setObservacions(camp.getObservacions());
			ct.setDominiId(camp.getDominiId());
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
	private ExpedientInfo toExpedientInfo(Expedient expedient) {
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

	private static final Log logger = LogFactory.getLog(TramitacioServiceImpl.class);

}
