/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.helper;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.AutenticacioTipus;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DetalleAviso;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentPresencial;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentTelematic;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentTramit;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo.IniciadorTipus;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.JustificantRecepcioInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Signatura;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TipoAviso;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TipoConfirmacionAviso;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TipoEstadoNotificacion;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Tramit;
import net.conselldemallorca.helium.v3.core.api.dto.DetalleAvisoDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaJustificantDetallRecepcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDocumentDto.TramitDocumentSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDto.TramitAutenticacioTipusDto;

/**
 * Helper per a convertir entre diferents formats de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConversioTipusHelper {

	public static ExpedientInfo toExpedientInfo(ExpedientDto expedient) {
		if (expedient != null) {
			ExpedientInfo resposta = new ExpedientInfo();
			resposta.setId(expedient.getId());
			resposta.setTitol(expedient.getTitol());
			resposta.setNumero(expedient.getNumero());
			resposta.setNumeroDefault(expedient.getNumeroDefault());
			resposta.setDataInici(expedient.getDataInici());
			resposta.setDataFi(expedient.getDataFi());
			resposta.setComentari(expedient.getComentari());
			resposta.setComentariAnulat(expedient.getComentariAnulat());
			resposta.setInfoAturat(expedient.getInfoAturat());
			if (expedient.getIniciadorTipus().equals(IniciadorTipusDto.INTERN))
				resposta.setIniciadorTipus(IniciadorTipus.INTERN);
			else if (expedient.getIniciadorTipus().equals(IniciadorTipusDto.SISTRA))
				resposta.setIniciadorTipus(IniciadorTipus.SISTRA);
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
			resposta.setEntornNom(expedient.getEntorn().getNom());
			if (expedient.getEstat() != null) {
				resposta.setEstatCodi(expedient.getEstat().getCodi());
				resposta.setEstatNom(expedient.getEstat().getNom());
			}
			resposta.setProcessInstanceId(new Long(expedient.getProcessInstanceId()).longValue());
			resposta.setAmbRetroaccio(expedient.isAmbRetroaccio());
			return resposta;
		}
		return null;
	}
	
	public static Tramit toTramit(TramitDto dadesTramit) {
		if (dadesTramit == null)
			return null;
		Tramit tramit = new Tramit();
		tramit.setNumero(dadesTramit.getNumero());
		tramit.setClauAcces(dadesTramit.getClauAcces());
		tramit.setIdentificador(dadesTramit.getIdentificador());
		tramit.setUnitatAdministrativa(dadesTramit.getUnitatAdministrativa());
		tramit.setVersio(dadesTramit.getVersio());
		tramit.setData(dadesTramit.getData());
		tramit.setIdioma(dadesTramit.getIdioma());
		tramit.setRegistreNumero(dadesTramit.getRegistreNumero());
		tramit.setRegistreData(dadesTramit.getRegistreData());
		tramit.setPreregistreTipusConfirmacio(dadesTramit.getPreregistreTipusConfirmacio());
		tramit.setPreregistreNumero(dadesTramit.getPreregistreNumero());
		tramit.setPreregistreData(dadesTramit.getPreregistreData());
		if (dadesTramit.getAutenticacioTipus() != null) {
			if (TramitAutenticacioTipusDto.ANONIMA.equals(dadesTramit.getAutenticacioTipus()))
				tramit.setAutenticacioTipus(AutenticacioTipus.ANONIMA);
			if (TramitAutenticacioTipusDto.USUARI.equals(dadesTramit.getAutenticacioTipus()))
				tramit.setAutenticacioTipus(AutenticacioTipus.USUARI);
			if (TramitAutenticacioTipusDto.CERTIFICAT.equals(dadesTramit.getAutenticacioTipus()))
				tramit.setAutenticacioTipus(AutenticacioTipus.CERTIFICAT);
		}
		tramit.setTramitadorNif(dadesTramit.getTramitadorNif());
		tramit.setTramitadorNom(dadesTramit.getTramitadorNom());
		tramit.setInteressatNif(dadesTramit.getInteressatNif());
		tramit.setInteressatNom(dadesTramit.getInteressatNom());
		tramit.setRepresentantNif(dadesTramit.getRepresentantNif());
		tramit.setRepresentantNom(dadesTramit.getRepresentantNom());
		tramit.setSignat(dadesTramit.isSignat());
		tramit.setAvisosHabilitats(dadesTramit.isAvisosHabilitats());
		tramit.setAvisosEmail(dadesTramit.getAvisosEmail());
		tramit.setAvisosSms(dadesTramit.getAvisosSms());
		tramit.setNotificacioTelematicaHabilitada(dadesTramit.isNotificacioTelematicaHabilitada());
		if (dadesTramit.getDocuments() != null) {
			List<DocumentTramit> documents = new ArrayList<DocumentTramit>();
			for (TramitDocumentDto documento: dadesTramit.getDocuments()) {
				DocumentTramit document = new DocumentTramit();
				document.setNom(documento.getNom());
				document.setIdentificador(documento.getIdentificador());
				document.setInstanciaNumero(documento.getInstanciaNumero());
				if (documento.isPresencial()) {
					DocumentPresencial documentPresencial = new DocumentPresencial();
					documentPresencial.setDocumentCompulsar(
							documento.getPresencialDocumentCompulsar());
					documentPresencial.setSignatura(
							documento.getPresencialSignatura());
					documentPresencial.setFotocopia(
							documento.getPresencialFotocopia());
					documentPresencial.setTipus(
							documento.getPresencialTipus());
					document.setDocumentPresencial(documentPresencial);
				}
				if (documento.isTelematic()) {
					DocumentTelematic documentTelematic = new DocumentTelematic();
					documentTelematic.setArxiuNom(
							documento.getTelematicArxiuNom());
					documentTelematic.setArxiuExtensio(
							documento.getTelematicArxiuExtensio());
					documentTelematic.setArxiuContingut(
							documento.getTelematicArxiuContingut());
					documentTelematic.setReferenciaCodi(
							documento.getTelematicReferenciaCodi());
					documentTelematic.setReferenciaClau(
							documento.getTelematicReferenciaClau());
					if (documento.getTelematicSignatures() != null) {
						List<Signatura> signatures = new ArrayList<Signatura>();
						for (TramitDocumentSignaturaDto firma: documento.getTelematicSignatures()) {
							Signatura signatura = new Signatura();
							signatura.setFormat(firma.getFormat());
							signatura.setSignatura(firma.getSignatura());
							signatures.add(signatura);
						}
						documentTelematic.setSignatures(signatures);
					}
					document.setDocumentTelematic(documentTelematic);
				}
				documents.add(document);
			}
			tramit.setDocuments(documents);
		}
		return tramit;
	}

	public static JustificantRecepcioInfo toJustificantRecepcioInfo(RespostaJustificantDetallRecepcioDto justificant) {
		if (justificant != null) {
			JustificantRecepcioInfo justificantInfo = new JustificantRecepcioInfo();
			justificantInfo.setData(justificant.getData());
			justificantInfo.setEstado(justificant.getEstado() == null ? null : TipoEstadoNotificacion.fromValue(justificant.getEstado().value()));
			justificantInfo.setFechaAcuseRecibo(justificant.getFechaAcuseRecibo());
			justificantInfo.setFicheroAcuseReciboClave(justificant.getFicheroAcuseRecibo().getClave());
			justificantInfo.setFicheroAcuseReciboCodigo(justificant.getFicheroAcuseRecibo().getCodigo());
			List<DetalleAviso> avisos = new ArrayList<DetalleAviso>();
			if (justificant.getAvisos() != null) {
				for (DetalleAvisoDto detall: justificant.getAvisos().getAviso()) {
					avisos.add(toDetalleAviso(detall));
				}
			}
			justificantInfo.setAvisos(avisos);
			return justificantInfo;
		}
		return null;
	}
	
	public static DetalleAviso toDetalleAviso(DetalleAvisoDto detall) {
		if (detall != null) {
			DetalleAviso detallAvis = new DetalleAviso();
			detallAvis.setTipo(detall.getTipo() == null ? null : TipoAviso.fromValue(detall.getTipo().value()));
			detallAvis.setDestinatario(detall.getDestinatario());
			detallAvis.setEnviado(detall.isEnviado());
			detallAvis.setFechaEnvio(detall.getFechaEnvio());
			detallAvis.setConfirmarEnvio(detall.isConfirmarEnvio());
			detallAvis.setConfirmadoEnvio(detall.getConfirmadoEnvio() == null ? null : TipoConfirmacionAviso.fromValue(detall.getConfirmadoEnvio().value()));
			return detallAvis;
		}
		return null;
	}
//	public static DocumentInfo toDocumentInfo(DocumentDto document) {
//		if (document != null) {
//			DocumentInfo resposta = new DocumentInfo();
//			resposta.setId(document.getId());
//			if (document.isAdjunt()) {
//				resposta.setTitol(document.getAdjuntTitol());
//			} else {
//				resposta.setTitol(document.getDocumentNom());
//			}
//			resposta.setDataCreacio(document.getDataCreacio());
//			resposta.setDataDocument(document.getDataDocument());
//			resposta.setSignat(document.isSignat());
//			if (document.isRegistrat()) {
//				resposta.setRegistrat(true);
//				resposta.setRegistreNumero(document.getRegistreNumero());
//				resposta.setRegistreData(document.getRegistreData());
//				resposta.setRegistreOficinaCodi(document.getRegistreOficinaCodi());
//				resposta.setRegistreOficinaNom(document.getRegistreOficinaNom());
//				resposta.setRegistreEntrada(document.isRegistreEntrada());
//			}
//			ArxiuDto arxiu = Jbpm3HeliumBridge.getInstanceService().getArxiuPerMostrar(document.getId());
//			resposta.setArxiuNom(arxiu.getNom());
//			resposta.setArxiuContingut(arxiu.getContingut());
//			return resposta;
//		}
//		return null;
//	}
//	public static TokenInfo toTokenInfo(Token token) {
//		return new TokenInfo(
//				token.getId(),
//				token.getName(),
//				token.getStart(),
//				token.getEnd(),
//				token.getNode(),
//				token.getNodeEnter(),
//				token.getProcessInstance(),
//				token.getParent(),
//				token.getChildren(),
//				token.getSubProcessInstance(),
//				token.isAbleToReactivateParent(),
//				token.isTerminatedImplicitly(),
//				token.isSuspended(),
//				token.getLockOwner());
//	}
	
//	private static TokenInfo toTokenInfo(Token token, Map<Long, TokenInfo> tokens) {
//		if (token == null)
//			return null;
//		
//		if (tokens.containsKey(token.getId())) { 
//			return tokens.get(token.getId());
//		} else {
//			Map<String, TokenInfo> tokensFills = new HashMap<String, TokenInfo>();
//			for (Entry<String, Token> entry: token.getChildren().entrySet()) {
//				Token fill = entry.getValue();
//				if (tokens.containsKey(fill.getId())) {
//					tokensFills.put(entry.getKey(), tokensFills.get(fill.getId()));
//				} else { 
//					TokenInfo tokenFill = toTokenInfo(fill, tokens);
//					tokens.put(fill.getId(), tokenFill);
//					tokensFills.put(entry.getKey(), tokenFill);
//				}
//			}
//			TokenInfo tokenInfo = new TokenInfo(
//					token.getId(),
//					token.getName(),
//					token.getStart(),
//					token.getEnd(),
//					token.getNode(),
//					token.getNodeEnter(),
//					token.getProcessInstance(),
//					token.getParent(),
//					token.getChildren(),
//					token.getSubProcessInstance(),
//					token.isAbleToReactivateParent(),
//					token.isTerminatedImplicitly(),
//					token.isSuspended(),
//					token.getLockOwner());
//			tokens.put(token.getId(), tokenInfo);
//			return tokenInfo;
//		}
//	}

		
//	public static NodeInfo toNodeInfo(Node node) {
//		if (node == null)
//			return null;
//		
//		List<TransitionInfo> leavingTransitions = new ArrayList<TransitionInfo>();
//		Set<TransitionInfo> arrivingTransitions = new HashSet<TransitionInfo>();
//		
//		for (Transition t: node.getLeavingTransitions()) {
//			leavingTransitions.add(toTransitionInfo(t));
//		}
//		for (Transition t: node.getArrivingTransitions()) {
//			arrivingTransitions.add(toTransitionInfo(t));
//		}
//		
//		NodeInfo nodeInfo = new NodeInfo(
//				node.getId(),
//				node.getName(),
//				node.getDescription(),
//				leavingTransitions,
//				arrivingTransitions,
//				toActionInfo(node.getAction()),
//				node.isAsync(),
//				node.isAsyncExclusive());
//		return nodeInfo;
//	}
//	
//	public static ProcessDefinitionInfo toProcessDefinitionInfo(ProcessDefinition processDefinition) {
//		return null;
//	}
//
//	public static ProcessInstanceInfo toProcessInstanceInfo(ProcessInstance processInstance) {
//		return null;
//	}
//
//	public static ActionInfo toActionInfo(Action action) {
//		return null;
//	}
//
//	public static EventInfo toEventInfo(Event event) {
//		return null;
//	}
//
//	public static TransitionInfo toTransitionInfo(Transition transition) {
//		return null;
//	}
//
//	public static TaskInfo toTaskInfo(Task task) {
//		return null;
//	}
//
//	public static TaskInstanceInfo toTaskInstanceInfo(TaskInstance taskInstance) {
//		return null;
//	}
//
//	public static TimerInfo toTimerInfo(Timer timer) {
//		return null;
//	}
}
