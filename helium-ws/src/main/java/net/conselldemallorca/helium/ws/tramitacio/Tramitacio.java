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

import net.conselldemallorca.helium.core.model.dto.DocumentDto;
import net.conselldemallorca.helium.core.model.dto.ExpedientDto;
import net.conselldemallorca.helium.core.model.dto.InstanciaProcesDto;
import net.conselldemallorca.helium.core.model.dto.TascaDto;
import net.conselldemallorca.helium.core.model.dto.TascaLlistatDto;
import net.conselldemallorca.helium.core.model.hibernate.Camp;
import net.conselldemallorca.helium.core.model.hibernate.Document;
import net.conselldemallorca.helium.core.model.hibernate.Entorn;
import net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus;
import net.conselldemallorca.helium.core.model.hibernate.Camp.TipusCamp;
import net.conselldemallorca.helium.core.model.hibernate.Expedient.IniciadorTipus;
import net.conselldemallorca.helium.core.model.service.DissenyService;
import net.conselldemallorca.helium.core.model.service.EntornService;
import net.conselldemallorca.helium.core.model.service.ExpedientService;
import net.conselldemallorca.helium.core.model.service.TascaService;
import net.conselldemallorca.helium.core.util.EntornActual;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementació del servei de tramitació d'expedients
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@WebService(endpointInterface = "net.conselldemallorca.helium.ws.tramitacio.TramitacioService")
public class Tramitacio implements TramitacioService {

	private EntornService entornService;
	private DissenyService dissenyService;
	private ExpedientService expedientService;
	private TascaService tascaService;



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
				variables.put(
						parella.getCodi(),
						parella.getValor());
			}
		}
		try {
			ExpedientDto expedient = expedientService.iniciar(
					e.getId(),
					usuari,
					et.getId(),
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
			return expedient.getProcessInstanceId();
		} catch (Exception ex) {
			logger.error("No s'han pogut iniciar l'expedient", ex);
			throw new TramitacioException("No s'han pogut iniciar l'expedient: " + ex.getMessage());
		}
	}

	public List<TascaTramitacio> consultaTasquesPersonals(
			String entorn,
			String usuari) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		try {
			List<TascaLlistatDto> tasques = tascaService.findTasquesPersonalsTramitacio(e.getId(), usuari, true);
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (TascaLlistatDto tasca: tasques)
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
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		try {
			List<TascaLlistatDto> tasques = tascaService.findTasquesGrupTramitacio(e.getId(), usuari, true);
			List<TascaTramitacio> resposta = new ArrayList<TascaTramitacio>();
			for (TascaLlistatDto tasca: tasques)
				resposta.add(convertirTascaTramitacio(tasca));
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	public void agafarTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		try {
			List<TascaLlistatDto> tasques = tascaService.findTasquesGrupTramitacio(e.getId(), usuari, false);
			boolean agafada = false;
			for (TascaLlistatDto tasca: tasques) {
				if (tasca.getId().equals(tascaId)) {
					tascaService.agafar(e.getId(), usuari, tascaId);
					agafada = true;
					break;
				}
			}
			if (!agafada)
				throw new TramitacioException("L'usuari '" + usuari + "' no té la tasca " + tascaId + " assignada");
		} catch (Exception ex) {
			logger.error("No s'ha pogut obtenir el llistat de tasques", ex);
			throw new TramitacioException("No s'ha pogut obtenir el llistat de tasques: " + ex.getMessage());
		}
	}

	public List<CampTasca> consultaFormulariTasca(
			String entorn,
			String usuari,
			String tascaId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		TascaDto tasca = tascaService.getById(e.getId(), tascaId, usuari);
		List<CampTasca> resposta = new ArrayList<CampTasca>();
		for (net.conselldemallorca.helium.core.model.hibernate.CampTasca campTasca: tasca.getCamps())
			resposta.add(convertirCampTasca(
					campTasca,
					tasca.getVariable(campTasca.getCamp().getCodi())));
		return resposta;
	}

	public void setDadesFormulariTasca(
			String entorn,
			String usuari,
			String tascaId,
			List<ParellaCodiValor> valors) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
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
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		TascaDto tasca = tascaService.getById(e.getId(), tascaId, usuari);
		List<DocumentTasca> resposta = new ArrayList<DocumentTasca>();
		for (net.conselldemallorca.helium.core.model.hibernate.DocumentTasca documentTasca: tasca.getDocuments())
			resposta.add(convertirDocumentTasca(
					documentTasca,
					tasca.getVarsDocuments().get(documentTasca.getDocument().getCodi())));
		return resposta;
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
		try {
			tascaService.guardarDocument(
					e.getId(),
					tascaId,
					arxiu,
					nom,
					data,
					contingut,
					usuari);
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar el document a la tasca", ex);
			throw new TramitacioException("No s'ha pogut guardar el document a la tasca: " + ex.getMessage());
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
		try {
			tascaService.esborrarDocument(
					e.getId(),
					tascaId,
					document,
					usuari);
		} catch (Exception ex) {
			logger.error("No s'ha pogut esborrar el document de la tasca", ex);
			throw new TramitacioException("No s'ha pogut esborrar el document de la tasca: " + ex.getMessage());
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
		try {
			tascaService.completar(
					e.getId(),
					tascaId,
					true,
					usuari,
					transicio);
		} catch (Exception ex) {
			logger.error("No s'ha pogut completar la tasca", ex);
			throw new TramitacioException("No s'ha pogut completar la tasca: " + ex.getMessage());
		}
	}

	public List<CampProces> consultarVariablesProces(
			String entorn,
			String usuari,
			String processInstanceId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		try {
			List<CampProces> resposta = new ArrayList<CampProces>();
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId, true);
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
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar la variable al procés", ex);
			throw new TramitacioException("No s'ha pogut guardar la variable al procés: " + ex.getMessage());
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
		try {
			expedientService.updateVariable(
					processInstanceId,
					varCodi,
					valor);
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar la variable al procés", ex);
			throw new TramitacioException("No s'ha pogut guardar la variable al procés: " + ex.getMessage());
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
		try {
			expedientService.deleteVariable(
					processInstanceId,
					varCodi);
		} catch (Exception ex) {
			logger.error("No s'ha pogut esborrar la variable al procés", ex);
			throw new TramitacioException("No s'ha pogut esborrar la variable al procés: " + ex.getMessage());
		}
	}

	public List<DocumentProces> consultarDocumentsProces(
			String entorn,
			String usuari,
			String processInstanceId) throws TramitacioException {
		Entorn e = findEntornAmbCodi(entorn);
		if (e == null)
			throw new TramitacioException("No existeix cap entorn amb el codi '" + entorn + "'");
		try {
			List<DocumentProces> resposta = new ArrayList<DocumentProces>();
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId, true);
			for (DocumentDto document: instanciaProces.getVarsDocuments().values()) {
				resposta.add(convertirDocumentProces(document));
			}
			return resposta;
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar la variable al procés", ex);
			throw new TramitacioException("No s'ha pogut guardar la variable al procés: " + ex.getMessage());
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
		try {
			InstanciaProcesDto instanciaProces = expedientService.getInstanciaProcesById(processInstanceId, false);
			if (instanciaProces == null)
				throw new TramitacioException("No s'ha pogut trobar la instancia de proces amb id " + processInstanceId);
			Document document = dissenyService.findDocumentAmbDefinicioProcesICodi(
					instanciaProces.getDefinicioProces().getId(),
					documentCodi);
			if (document == null)
				throw new TramitacioException("No s'ha pogut trobar el document amb codi " + documentCodi);
			return expedientService.guardarDocument(
					processInstanceId,
					document.getId(),
					data,
					arxiu,
					contingut);
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar el document al procés", ex);
			throw new TramitacioException("No s'ha pogut guardar el document al procés: " + ex.getMessage());
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
		try {
			expedientService.deleteDocument(
					processInstanceId,
					documentId);
		} catch (Exception ex) {
			logger.error("No s'ha pogut esborrar el document del procés", ex);
			throw new TramitacioException("No s'ha pogut esborrar el document del procés: " + ex.getMessage());
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
		try {
			expedientService.executarAccio(processInstanceId, accio);
		} catch (Exception ex) {
			logger.error("No s'ha pogut executar l'script", ex);
			throw new TramitacioException("No s'ha pogut executar l'script: " + ex.getMessage());
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
		try {
			expedientService.evaluateScript(
					processInstanceId,
					script,
					null);
		} catch (Exception ex) {
			logger.error("No s'ha pogut executar l'script", ex);
			throw new TramitacioException("No s'ha pogut executar l'script: " + ex.getMessage());
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
		tt.setExpedient(tasca.getExpedientNumeroDefault());
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

	private static final Log logger = LogFactory.getLog(Tramitacio.class);

}
