/**
 * 
 */
package net.conselldemallorca.helium.jbpm3.helper;

import java.util.ArrayList;
import java.util.List;

import net.conselldemallorca.helium.jbpm3.handlers.tipus.AutenticacioTipus;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesConsultaPinbal;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesEnviament;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DadesNotificacio;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DetalleAviso;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentPresencial;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentTelematic;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.DocumentTramit;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.EnviamentReferencia;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.ExpedientInfo.IniciadorTipus;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Interessat;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.JustificantRecepcioInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.PersonaInfo;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.RespostaEnviar;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Signatura;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TipoAviso;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TipoConfirmacionAviso;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.TipoEstadoNotificacion;
import net.conselldemallorca.helium.jbpm3.handlers.tipus.Tramit;
import net.conselldemallorca.helium.v3.core.api.dto.DadesConsultaPinbalDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesNotificacioDto;
import net.conselldemallorca.helium.v3.core.api.dto.DetalleAvisoDto;
import net.conselldemallorca.helium.v3.core.api.dto.EnviamentTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto;
import net.conselldemallorca.helium.v3.core.api.dto.IdiomaEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.PersonaDto;
import net.conselldemallorca.helium.v3.core.api.dto.ReferenciaNotificacio;
import net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.IniciadorTipusDto;
import net.conselldemallorca.helium.v3.core.api.dto.InteressatDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaNotificacio.NotificacioEstat;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaJustificantDetallRecepcioDto;
import net.conselldemallorca.helium.v3.core.api.dto.RespostaNotificacio;
import net.conselldemallorca.helium.v3.core.api.dto.ServeiTipusEnumDto;
import net.conselldemallorca.helium.v3.core.api.dto.TitularDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDocumentDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDocumentDto.TramitDocumentSignaturaDto;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDto;
import net.conselldemallorca.helium.v3.core.api.dto.DadesEnviamentDto.EntregaPostalTipus;
import net.conselldemallorca.helium.v3.core.api.dto.TramitDto.TramitAutenticacioTipusDto;
import net.conselldemallorca.helium.v3.core.api.exception.ValidacioException;

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
	
	public static DadesConsultaPinbalDto toDadesConsultaPinbalDto(DadesConsultaPinbal dadesConsultaPinbal) {
		DadesConsultaPinbalDto dadesDto = new DadesConsultaPinbalDto();
		TitularDto titularDto = new TitularDto();
		if(dadesConsultaPinbal.getTitular() != null) {
			titularDto.setNombre(dadesConsultaPinbal.getTitular().getNombre());
			titularDto.setApellido1(dadesConsultaPinbal.getTitular().getApellido1());
			titularDto.setApellido2(dadesConsultaPinbal.getTitular().getApellido2());
			titularDto.setNombreCompleto(dadesConsultaPinbal.getTitular().getNombreCompleto());	
			titularDto.setDocumentacion(dadesConsultaPinbal.getTitular().getDocumentacion());
			titularDto.setTipoDocumentacion(dadesConsultaPinbal.getTitular().getTipoDocumentacion());
			dadesDto.setTitular(titularDto);
		}
		if(dadesConsultaPinbal.getDocumentCodi()!=null) {
			dadesDto.setDocumentCodi(dadesConsultaPinbal.getDocumentCodi());
		}
		if(dadesConsultaPinbal.getXmlDadesEspecifiques()!=null) {
			dadesDto.setXmlDadesEspecifiques(dadesConsultaPinbal.getXmlDadesEspecifiques());
		}
		if(dadesConsultaPinbal.getServeiCodi()!=null) {
			dadesDto.setServeiCodi(dadesConsultaPinbal.getServeiCodi());
		}
		if(dadesConsultaPinbal.getConsentiment()!=null) {
			dadesDto.setConsentiment(dadesConsultaPinbal.getConsentiment());
		}
		if(dadesConsultaPinbal.getFinalitat()!=null) {
			dadesDto.setFinalitat(dadesConsultaPinbal.getFinalitat());
		}
		if(dadesConsultaPinbal.getInteressatCodi()!=null) {
			dadesDto.setInteressatCodi(dadesConsultaPinbal.getInteressatCodi());
		}
		if(dadesConsultaPinbal.isAsincrona()) {
			dadesDto.setAsincrona(dadesConsultaPinbal.isAsincrona());
		}
		dadesDto.setTransicioOK(dadesConsultaPinbal.getTransicioOK());
		dadesDto.setTransicioKO(dadesConsultaPinbal.getTransicioKO());
		if(dadesConsultaPinbal.getAnyNaixement()!=null) {
			dadesDto.setAnyNaixement(dadesConsultaPinbal.getAnyNaixement());
		}
		return dadesDto;	
	}

	public static DadesNotificacioDto toDadesNotificacioDto(Long expedientId, DadesNotificacio dadesNotificacio) {
		
		DadesNotificacioDto notificacio = new DadesNotificacioDto();
		notificacio.setExpedientId(expedientId);
		notificacio.setEmisorDir3Codi(dadesNotificacio.getEmisorDir3Codi());
		if (dadesNotificacio.getEnviamentTipus() != null)
			notificacio.setEnviamentTipus(EnviamentTipusEnumDto.valueOf(dadesNotificacio.getEnviamentTipus().name()));
		notificacio.setConcepte(dadesNotificacio.getConcepte());
		notificacio.setDescripcio(dadesNotificacio.getDescripcio());
		notificacio.setEnviamentDataProgramada(dadesNotificacio.getEnviamentDataProgramada());
		notificacio.setRetard(dadesNotificacio.getRetard());
		notificacio.setCaducitat(dadesNotificacio.getCaducitat());
		notificacio.setDocumentArxiuNom(dadesNotificacio.getDocumentArxiuNom());
		notificacio.setDocumentArxiuContingut(dadesNotificacio.getDocumentArxiuContingut());
		notificacio.setDocumentArxiuUuid(dadesNotificacio.getDocumentArxiuUuid());
		notificacio.setDocumentId(dadesNotificacio.getDocumentId());
		notificacio.setProcedimentCodi(dadesNotificacio.getProcedimentCodi());
		if (dadesNotificacio.getIdioma() != null)
			notificacio.setIdioma(IdiomaEnumDto.valueOf(dadesNotificacio.getIdioma().toString()));
		
		List<DadesEnviamentDto> enviaments = new ArrayList<DadesEnviamentDto>();
		for (DadesEnviament dadesEnviament: dadesNotificacio.getEnviaments()) {
			DadesEnviamentDto enviament = new DadesEnviamentDto();
			
			PersonaInfo dadesTitular = dadesEnviament.getTitular();
			PersonaDto titular = new PersonaDto();
			titular.setNom(dadesTitular.getNom());
			titular.setLlinatge1(dadesTitular.getLlinatge1());
			titular.setLlinatge2(dadesTitular.getLlinatge2());
			titular.setDni(dadesTitular.getDni());
			titular.setTelefon(dadesTitular.getTelefon());;
			titular.setEmail(dadesTitular.getEmail());
			titular.setCodiDir3(dadesTitular.getCodiDir3());
			titular.setTipus(InteressatTipusEnumDto.valueOf(dadesTitular.getTipus()));
			enviament.setTitular(titular);

			List<PersonaDto> destinataris = new ArrayList<PersonaDto>();
			for (PersonaInfo dadesDestinatari: dadesEnviament.getDestinataris()) {
				
				PersonaDto destinatari = new PersonaDto();
				destinatari.setNom(dadesDestinatari.getNom());
				destinatari.setLlinatge1(dadesDestinatari.getLlinatge1());
				destinatari.setLlinatge2(dadesDestinatari.getLlinatge2());
				destinatari.setDni(dadesDestinatari.getDni());
				destinatari.setTelefon(dadesDestinatari.getTelefon());;
				destinatari.setEmail(dadesDestinatari.getEmail());
				destinatari.setCodiDir3(dadesDestinatari.getCodiDir3());
				destinatari.setTipus(InteressatTipusEnumDto.valueOf(dadesDestinatari.getTipus()));
				destinataris.add(destinatari);
			}
			enviament.setDestinataris(destinataris);
			enviament.setEntregaPostalActiva(false); //Forcem false issue #1675
//			enviament.setEntregaPostalActiva(dadesEnviament.isEntregaPostalActiva());
			if (dadesEnviament.getEntregaPostalTipus() != null)
				enviament.setEntregaPostalTipus(DadesEnviamentDto.EntregaPostalTipus.valueOf(dadesEnviament.getEntregaPostalTipus().name()));
			if (dadesEnviament.getEntregaPostalViaTipus() != null)
				enviament.setEntregaPostalViaTipus(DadesEnviamentDto.EntregaPostalViaTipus.valueOf(dadesEnviament.getEntregaPostalViaTipus().name()));
			enviament.setEntregaPostalViaNom(dadesEnviament.getEntregaPostalViaNom());
			enviament.setEntregaPostalNumeroCasa(dadesEnviament.getEntregaPostalNumeroCasa());
			enviament.setEntregaPostalNumeroQualificador(dadesEnviament.getEntregaPostalNumeroQualificador());
			enviament.setEntregaPostalPuntKm(dadesEnviament.getEntregaPostalPuntKm());
			enviament.setEntregaPostalApartatCorreus(dadesEnviament.getEntregaPostalApartatCorreus());
			enviament.setEntregaPostalPortal(dadesEnviament.getEntregaPostalPortal());
			enviament.setEntregaPostalEscala(dadesEnviament.getEntregaPostalEscala());
			enviament.setEntregaPostalPlanta(dadesEnviament.getEntregaPostalPlanta());
			enviament.setEntregaPostalPorta(dadesEnviament.getEntregaPostalPorta());
			enviament.setEntregaPostalBloc(dadesEnviament.getEntregaPostalBloc());
			enviament.setEntregaPostalComplement(dadesEnviament.getEntregaPostalComplement());
			enviament.setEntregaPostalCodiPostal(dadesEnviament.getEntregaPostalCodiPostal());
			enviament.setEntregaPostalPoblacio(dadesEnviament.getEntregaPostalPoblacio());
			enviament.setEntregaPostalMunicipiCodi(dadesEnviament.getEntregaPostalMunicipiCodi());
			enviament.setEntregaPostalProvinciaCodi(dadesEnviament.getEntregaPostalProvinciaCodi());
			enviament.setEntregaPostalPaisCodi(dadesEnviament.getEntregaPostalPaisCodi());
			enviament.setEntregaPostalLinea1(dadesEnviament.getEntregaPostalLinea1());
			enviament.setEntregaPostalLinea2(dadesEnviament.getEntregaPostalLinea2());
			enviament.setEntregaPostalCie(dadesEnviament.getEntregaPostalCie());
			enviament.setEntregaPostalFormatSobre(dadesEnviament.getEntregaPostalFormatSobre());
			enviament.setEntregaPostalFormatFulla(dadesEnviament.getEntregaPostalFormatFulla());
			enviament.setEntregaDehActiva(dadesEnviament.isEntregaDehActiva());
			enviament.setEntregaDehObligat(dadesEnviament.isEntregaDehObligat());
			enviament.setEntregaDehProcedimentCodi(dadesEnviament.getEntregaDehProcedimentCodi());
			
			// Per defecte tipus de servei normal.
			enviament.setServeiTipusEnum(dadesNotificacio.getServeiTipus() != null ? ServeiTipusEnumDto.valueOf(dadesNotificacio.getServeiTipus()) : ServeiTipusEnumDto.NORMAL);
			
			enviaments.add(enviament);
		}
		notificacio.setEnviaments(enviaments);
		
		return notificacio;
	}

	public static RespostaEnviar toRespostaEnviar(RespostaNotificacio respostaNotificacio) {
		RespostaEnviar resposta = new RespostaEnviar();
		switch(respostaNotificacio.getEstat()) {
		case ENVIADA:
			respostaNotificacio.setEstat(NotificacioEstat.ENVIADA);
			break;
		case FINALITZADA:
			respostaNotificacio.setEstat(NotificacioEstat.FINALITZADA);
			break;
		case PENDENT:
			respostaNotificacio.setEstat(NotificacioEstat.PENDENT);
			break;
		case PROCESSADA:
			respostaNotificacio.setEstat(NotificacioEstat.PROCESSADA);
			break;
		case REGISTRADA:
			respostaNotificacio.setEstat(NotificacioEstat.REGISTRADA);
			break;
		}
		resposta.setIdentificador(respostaNotificacio.getIdentificador());
		List<EnviamentReferencia> referencies = new ArrayList<EnviamentReferencia>();
		for (ReferenciaNotificacio referenciaNotificacio : respostaNotificacio.getReferencies()) {
			EnviamentReferencia enviamentReferencia = new EnviamentReferencia();
			enviamentReferencia.setReferencia(referenciaNotificacio.getReferencia());
			enviamentReferencia.setTitularNif(referenciaNotificacio.getTitularNif());
			referencies.add(enviamentReferencia);
		}
		resposta.setReferencies(referencies);
	
		return resposta;

	}

	
	public static InteressatDto toInteressatDto(Interessat interessat) {
		InteressatDto interessatDto = new InteressatDto();

		interessatDto.setCodi(interessat.getCodi());
		interessatDto.setNom(interessat.getNom());
		interessatDto.setNif(interessat.getNif());
		interessatDto.setDir3Codi(interessat.getDir3Codi());
		interessatDto.setLlinatge1(interessat.getLlinatge1());
		interessatDto.setLlinatge2(interessat.getLlinatge2());
		if (interessat.getTipus() != null && !interessat.getTipus().isEmpty()) {
			try {
				interessatDto.setTipus(InteressatTipusEnumDto.valueOf(interessat.getTipus().toUpperCase()));
			} catch(Exception e) {
				throw new ValidacioException("No es reconeix el tipus \"" + interessat.getTipus() + "\", " +
										     "els possibles tipus són : " + InteressatTipusEnumDto.FISICA + ", " + 
										     								InteressatTipusEnumDto.ADMINISTRACIO + " i " + 
										     								InteressatTipusEnumDto.JURIDICA, e);
			}
		}
		interessatDto.setEmail(interessat.getEmail());
		interessatDto.setTelefon(interessat.getTelefon());
		interessatDto.setExpedientId(interessat.getExpedientId());
		interessatDto.setEntregaPostal(interessat.isEntregaPostal());
		if (interessat.getEntregaTipus() != null && !interessat.getEntregaTipus().isEmpty()) {
			try {
				interessatDto.setEntregaTipus(EntregaPostalTipus.valueOf(interessat.getTipus().toUpperCase()));
			} catch(Exception e) {
				throw new ValidacioException("No es reconeix el tipus \"" + interessat.getTipus() + "\", " +
										     "els possibles tipus són : " + EntregaPostalTipus.SENSE_NORMALITZAR, e);
			}
		}
		interessatDto.setLinia1(interessat.getLinia1());
		interessatDto.setLinia2(interessat.getLinia2());
		interessatDto.setCodiPostal(interessat.getCodiPostal());
		interessatDto.setEntregaDeh(interessat.isEntregaDeh());
		interessatDto.setEntregaDehObligat(interessat.isEntregaDehObligat());

		return interessatDto;
	}	
}
