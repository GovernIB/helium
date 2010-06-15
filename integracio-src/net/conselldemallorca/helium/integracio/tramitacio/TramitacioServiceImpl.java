/**
 * 
 */
package net.conselldemallorca.helium.integracio.tramitacio;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import net.conselldemallorca.helium.model.dao.DaoProxy;
import net.conselldemallorca.helium.model.dto.ExpedientDto;
import net.conselldemallorca.helium.model.dto.TascaDto;
import net.conselldemallorca.helium.model.hibernate.Entorn;
import net.conselldemallorca.helium.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.model.service.ExpedientService;
import net.conselldemallorca.helium.model.service.ServiceProxy;
import net.conselldemallorca.helium.model.service.TascaService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementació del servei de tramitació d'expedients
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@WebService(endpointInterface = "net.conselldemallorca.helium.integracio.tramitacio.TramitacioService")
public class TramitacioServiceImpl implements TramitacioService {

	public String iniciExpedient(
			String entorn,
			String expedientTipus,
			String numero,
			String titol,
			List<ParellaCodiValor> valorsFormulari) throws TramitacioException {
		ExpedientService expedientService = ServiceProxy.getInstance().getExpedientService();
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb aquest codi '" + entorn + "'");
		ExpedientTipus et = findExpedientTipusAmbEntornICodi(e.getId(), expedientTipus);
		if (et == null)
			throw new TramitacioException("No existeix cap tipus d'expedient amb aquest codi '" + expedientTipus + "'");
		if (numero != null && !et.getTeNumero())
			throw new TramitacioException("Aquest tipus d'expedient no suporta número d'expedient");
		if (titol != null && !et.getTeTitol())
			throw new TramitacioException("Aquest tipus d'expedient no suporta titol d'expedient");
		Map<String, Object> variables = null;
		if (valorsFormulari != null) {
			variables = new HashMap<String, Object>();
			for (ParellaCodiValor parella: valorsFormulari) {
				variables.put(
						parella.getCodi(),
						parella.getValor());
			}
		}
		try {
			ExpedientDto expedient = expedientService.iniciar(
					e.getId(),
					et.getId(),
					null,
					numero,
					titol,
					variables,
					null,
					IniciadorTipus.INTERN,
					null,
					null);
			return expedient.getNumeroDefault();
		} catch (Exception ex) {
			logger.error("No s'han pogut iniciar l'expedient", ex);
			throw new TramitacioException("No s'han pogut iniciar l'expedient: " + ex.getMessage());
		}
	}

	public List<TascaTramitacio> consultaTasquesPersonals(
			String entorn,
			String usuari) throws TramitacioException {
		TascaService tascaService = ServiceProxy.getInstance().getTascaService();
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb aquest codi '" + entorn + "'");
		try {
			List<TascaDto> tasques = tascaService.findTasquesGrup(e.getId());
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (TascaDto tasca: tasques)
				resposta.add(convertirTascaTramitacio(tasca));
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	public List<TascaTramitacio> consultaTasquesGrup(
			String entorn,
			String usuari) throws TramitacioException {
		TascaService tascaService = ServiceProxy.getInstance().getTascaService();
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb aquest codi '" + entorn + "'");
		try {
			List<TascaDto> tasques = tascaService.findTasquesGrup(e.getId());
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (TascaDto tasca: tasques)
				resposta.add(convertirTascaTramitacio(tasca));
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	public List<CampTasca> consultaFormulariTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException {
		TascaService tascaService = ServiceProxy.getInstance().getTascaService();
		Entorn e = findEntornAmbCodi(entorn);
		TascaDto tasca = tascaService.getById(e.getId(), tascaId);
		List<CampTasca> resposta = new ArrayList<CampTasca>();
		for (net.conselldemallorca.helium.model.hibernate.CampTasca campTasca: tasca.getCamps())
			resposta.add(convertirCampTasca(campTasca));
		return resposta;
	}

	public void setDadesFormulariTasca(
			String entorn,
			String usuari,
			String tascaId,
			List<ParellaCodiValor> valors) throws TramitacioException {
		TascaService tascaService = ServiceProxy.getInstance().getTascaService();
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb aquest codi '" + entorn + "'");
		Map<String, Object> variables = null;
		if (valors != null) {
			variables = new HashMap<String, Object>();
			for (ParellaCodiValor parella: valors) {
				variables.put(
						parella.getCodi(),
						parella.getValor());
			}
		}
		try {
			tascaService.validar(
					e.getId(),
					tascaId,
					variables,
					true,
					usuari);
		} catch (Exception ex) {
			logger.error("No s'han pogut guardar les variables a la tasca", ex);
			throw new TramitacioException("No s'han pogut guardar les variables a la tasca: " + ex.getMessage());
		}
	}

	public List<DocumentTasca> consultaDocumentsTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException {
		TascaService tascaService = ServiceProxy.getInstance().getTascaService();
		Entorn e = findEntornAmbCodi(entorn);
		TascaDto tasca = tascaService.getById(e.getId(), tascaId);
		List<DocumentTasca> resposta = new ArrayList<DocumentTasca>();
		for (net.conselldemallorca.helium.model.hibernate.DocumentTasca documentTasca: tasca.getDocuments())
			resposta.add(convertirDocumentTasca(documentTasca));
		return resposta;
	}

	public void setDocumentTasca(
			String entorn,
			String usuari,
			String tascaId,
			String document,
			String nom,
			Date data,
			byte[] contingut) throws TramitacioException {
		TascaService tascaService = ServiceProxy.getInstance().getTascaService();
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb aquest codi '" + entorn + "'");
		try {
			tascaService.guardarDocument(
					e.getId(),
					tascaId,
					document,
					nom,
					data,
					contingut);
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar el document a la tasca", ex);
			throw new TramitacioException("No s'ha pogut guardar el document a la tasca: " + ex.getMessage());
		}
	}

	public void finalitzarTasca(
			String entorn,
			String usuari,
			String tascaId,
			String transicio) throws TramitacioException {
		TascaService tascaService = ServiceProxy.getInstance().getTascaService();
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb aquest codi '" + entorn + "'");
		try {
			tascaService.completar(
					e.getId(),
					tascaId,
					true,
					transicio);
		} catch (Exception ex) {
			logger.error("No s'ha pogut completar la tasca", ex);
			throw new TramitacioException("No s'ha pogut completar la tasca: " + ex.getMessage());
		}
	}



	private Entorn findEntornAmbCodi(String codi) {
		return DaoProxy.getInstance().getEntornDao().findAmbCodi(codi);
	}
	private ExpedientTipus findExpedientTipusAmbEntornICodi(Long entornId, String codi) {
		return DaoProxy.getInstance().getExpedientTipusDao().findAmbEntornICodi(
				entornId,
				codi);
	}
	private TascaTramitacio convertirTascaTramitacio(TascaDto tasca) {
		TascaTramitacio tt = new TascaTramitacio();
		tt.setId(tasca.getId());
		tt.setCodi(tasca.getJbpmName());
		tt.setTitol(tasca.getNom());
		tt.setMissatgeInfo(tasca.getMissatgeInfo());
		tt.setMissatgeWarn(tasca.getMissatgeWarn());
		tt.setResponsable(tasca.getAssignee());
		tt.setResponsables(tasca.getPooledActors());
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
		return tt;
	}
	private CampTasca convertirCampTasca(
			net.conselldemallorca.helium.model.hibernate.CampTasca campTasca) {
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
		ct.setMultiple(campTasca.getCamp().isMultiple());
		ct.setOcult(campTasca.getCamp().isOcult());
		return ct;
	}
	private DocumentTasca convertirDocumentTasca(
			net.conselldemallorca.helium.model.hibernate.DocumentTasca documentTasca) {
		DocumentTasca dt = new DocumentTasca();
		dt.setCodi(documentTasca.getDocument().getCodi());
		dt.setNom(documentTasca.getDocument().getNom());
		dt.setDescripcio(documentTasca.getDocument().getDescripcio());
		return dt;
	}

	private static final Log logger = LogFactory.getLog(TramitacioServiceImpl.class);

}
