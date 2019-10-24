/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.notificacio;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.binary.Base64;

import es.caib.notib.client.NotificacioRestClient;
import es.caib.notib.client.NotificacioRestClientFactory;
import es.caib.notib.ws.notificacio.Certificacio;
import es.caib.notib.ws.notificacio.DocumentV2;
import es.caib.notib.ws.notificacio.EntregaDeh;
import es.caib.notib.ws.notificacio.EntregaPostal;
import es.caib.notib.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.ws.notificacio.EnviamentTipusEnum;
import es.caib.notib.ws.notificacio.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.ws.notificacio.NotificaServeiTipusEnumDto;
import es.caib.notib.ws.notificacio.NotificacioV2;
import es.caib.notib.ws.notificacio.RespostaAlta;
import net.conselldemallorca.helium.core.util.GlobalProperties;

/**
 * Implementació de del plugin d'enviament de notificacions
 * emprant NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioPluginNotib implements NotificacioPlugin {

	
	private NotificacioRestClient clientV2;

	@Override
	public RespostaEnviar enviar(
			Notificacio notificacio) throws NotificacioPluginException {
		
		try {
			
			DocumentV2 document = new DocumentV2();
			document.setArxiuNom(notificacio.getDocumentArxiuNom());
			document.setContingutBase64(new String(Base64.encodeBase64(notificacio.getDocumentArxiuContingut())));
			document.setUuid(notificacio.getDocumentArxiuUuid());
			document.setCsv(notificacio.getDocumentArxiuCsv());
			
			
			NotificacioV2 notificacioNotib = new NotificacioV2();
			notificacioNotib.setEmisorDir3Codi(notificacio.getEmisorDir3Codi());
			notificacioNotib.setProcedimentCodi("");
			notificacioNotib.setEnviamentTipus(notificacio.getEnviamentTipus() != null ? EnviamentTipusEnum.valueOf(notificacio.getEnviamentTipus().toString()) : null);
			notificacioNotib.setConcepte(notificacio.getConcepte());
			notificacioNotib.setDescripcio(notificacio.getDescripcio());
			notificacioNotib.setEnviamentDataProgramada(toXmlGregorianCalendar(notificacio.getEnviamentDataProgramada()));
			notificacioNotib.setRetard(notificacio.getRetard());
			notificacioNotib.setCaducitat(toXmlGregorianCalendar(notificacio.getCaducitat()));
			notificacioNotib.setDocument(document);
			notificacioNotib.setProcedimentCodi(notificacio.getProcedimentCodi());
			notificacioNotib.setGrupCodi(notificacio.getGrupCodi());
			notificacioNotib.setUsuariCodi(notificacio.getUsuariCodi());
			
						
			if (notificacio.getEnviaments() != null) {
				for (Enviament enviament: notificacio.getEnviaments()) {
					
					es.caib.notib.ws.notificacio.Enviament enviamentNotib = new es.caib.notib.ws.notificacio.Enviament();
					enviamentNotib.setServeiTipus(enviament.getServeiTipusEnum() != null ? NotificaServeiTipusEnumDto.valueOf(enviament.getServeiTipusEnum().toString()) : null);
					enviamentNotib.setTitular(toPersonaNotib(enviament.getTitular()));
					if (enviament.getDestinataris() != null) {
						for (Persona destinatari : enviament.getDestinataris()) {
							enviamentNotib.getDestinataris().add(toPersonaNotib(destinatari));
						}
					}
					enviamentNotib.setEntregaPostalActiva(enviament.isEntregaPostalActiva());
					if (enviament.isEntregaPostalActiva()) {
						EntregaPostal entregaPostal = new EntregaPostal();
						entregaPostal.setTipus(NotificaDomiciliConcretTipusEnumDto.valueOf(enviament.getEntregaPostalTipus().toString()));
						entregaPostal.setViaTipus(enviament.getEntregaPostalViaTipus() != null ? EntregaPostalViaTipusEnum.valueOf(enviament.getEntregaPostalViaTipus().toString()) : null);
						entregaPostal.setViaNom(enviament.getEntregaPostalViaNom());
						entregaPostal.setNumeroCasa(enviament.getEntregaPostalNumeroCasa());
						entregaPostal.setNumeroQualificador(enviament.getEntregaPostalNumeroQualificador());
						entregaPostal.setPuntKm(enviament.getEntregaPostalPuntKm());
						entregaPostal.setApartatCorreus(enviament.getEntregaPostalApartatCorreus());
						entregaPostal.setPortal(enviament.getEntregaPostalPortal());
						entregaPostal.setEscala(enviament.getEntregaPostalEscala());
						entregaPostal.setPlanta(enviament.getEntregaPostalPlanta());
						entregaPostal.setPorta(enviament.getEntregaPostalPorta());
						entregaPostal.setBloc(enviament.getEntregaPostalBloc());
						entregaPostal.setComplement(enviament.getEntregaPostalComplement());
						entregaPostal.setCodiPostal(enviament.getEntregaPostalCodiPostal());
						entregaPostal.setPoblacio(enviament.getEntregaPostalPoblacio());
						entregaPostal.setMunicipiCodi(enviament.getEntregaPostalMunicipiCodi());
						entregaPostal.setProvincia(enviament.getEntregaPostalProvinciaCodi());
						entregaPostal.setPaisCodi(enviament.getEntregaPostalPaisCodi());
						entregaPostal.setLinea1(enviament.getEntregaPostalLinea1());
						entregaPostal.setLinea2(enviament.getEntregaPostalLinea2());
						entregaPostal.setCie(enviament.getEntregaPostalCie());
						entregaPostal.setFormatSobre(enviament.getEntregaPostalFormatSobre());
						entregaPostal.setFormatFulla(enviament.getEntregaPostalFormatFulla());
						enviamentNotib.setEntregaPostal(entregaPostal);
					}
					
					if (enviament.getEntregaDehProcedimentCodi() != null) {
						EntregaDeh entregaDeh = new EntregaDeh();
						entregaDeh.setObligat(enviament.isEntregaDehObligat());
						entregaDeh.setProcedimentCodi(enviament.getEntregaDehProcedimentCodi());
						enviamentNotib.setEntregaDehActiva(enviament.isEntregaDehActiva());
						enviamentNotib.setEntregaDeh(entregaDeh);
					}
					notificacioNotib.getEnviaments().add(enviamentNotib);
				}
			}

			RespostaAlta respostaAlta = getNotificacioService().alta(notificacioNotib);
			
			if (respostaAlta.isError() 
					&& (respostaAlta.getReferencies() == null || respostaAlta.getReferencies().isEmpty())) 
			{
				throw new NotificacioPluginException(respostaAlta.getErrorDescripcio());
			} else {
				RespostaEnviar resposta = new RespostaEnviar();
				if (respostaAlta.getEstat() != null) {
					switch (respostaAlta.getEstat()) {
					case PENDENT:
						resposta.setEstat(NotificacioEstat.PENDENT);
						break;
					case ENVIADA:
						resposta.setEstat(NotificacioEstat.ENVIADA);
						break;
					case FINALITZADA:
						resposta.setEstat(NotificacioEstat.FINALITZADA);
						break;
					case REGISTRADA:
						resposta.setEstat(NotificacioEstat.REGISTRADA);
						break;
					case PROCESSADA:
						resposta.setEstat(NotificacioEstat.PROCESSADA);
						break;
					default:
						break;
					}
				}
				resposta.setIdentificador(respostaAlta.getIdentificador());
				if (respostaAlta.getReferencies() != null) {
					List<EnviamentReferencia> referencies = new ArrayList<EnviamentReferencia>();
					for (es.caib.notib.ws.notificacio.EnviamentReferencia ref: respostaAlta.getReferencies()) {
						EnviamentReferencia referencia = new EnviamentReferencia();
						referencia.setTitularNif(ref.getTitularNif());
						referencia.setReferencia(ref.getReferencia());
						referencies.add(referencia);
					}
					resposta.setReferencies(referencies);
				}
				return resposta;
			}
		} catch (Exception ex) {
			throw new NotificacioPluginException(
					"No s'ha pogut enviar la notificació (" +
					"emisorDir3Codi=" + notificacio.getEmisorDir3Codi() + ", " +
					"enviamentTipus=" + notificacio.getEnviamentTipus() + ", " +
					"concepte=" + notificacio.getConcepte() + ")",
					ex);
		}
	}

	@Override
	public RespostaConsultaEstatNotificacio consultarNotificacio(
			String identificador) throws NotificacioPluginException {
		try {
			es.caib.notib.ws.notificacio.RespostaConsultaEstatNotificacio respostaConsultaEstat = getNotificacioService().consultaEstatNotificacio(identificador);
			if (respostaConsultaEstat.isError()) {
				throw new NotificacioPluginException(respostaConsultaEstat.getErrorDescripcio());
			} else {
				RespostaConsultaEstatNotificacio resposta = new RespostaConsultaEstatNotificacio();
				if (respostaConsultaEstat.getEstat() != null) {
					switch (respostaConsultaEstat.getEstat()) {
					case PENDENT:
						resposta.setEstat(NotificacioEstat.PENDENT);
						break;
					case ENVIADA:
						resposta.setEstat(NotificacioEstat.ENVIADA);
						break;
					case FINALITZADA:
						resposta.setEstat(NotificacioEstat.FINALITZADA);
						break;
					case REGISTRADA:
						resposta.setEstat(NotificacioEstat.REGISTRADA);
						break;
					default:
						break;
					}
				}
				return resposta;
			}
		} catch (Exception ex) {
			throw new NotificacioPluginException(
					"No s'ha pogut consultar l'estat de la notificació (" +
					"identificador=" + identificador + ")",
					ex);
		}
	}

	@Override
	public RespostaConsultaEstatEnviament consultarEnviament(
			String referencia) throws NotificacioPluginException {
		try {
			es.caib.notib.ws.notificacio.RespostaConsultaEstatEnviament respostaConsultaEstat = getNotificacioService().consultaEstatEnviament(referencia);
			if (respostaConsultaEstat.isError()) {
				throw new NotificacioPluginException(respostaConsultaEstat.getErrorDescripcio());
			} else {
				RespostaConsultaEstatEnviament resposta = new RespostaConsultaEstatEnviament();
				if (respostaConsultaEstat.getEstat() != null) {
					switch (respostaConsultaEstat.getEstat()) {
					case NOTIB_ENVIADA:
						resposta.setEstat(EnviamentEstat.NOTIB_ENVIADA);
						break;
					case NOTIB_PENDENT:
						resposta.setEstat(EnviamentEstat.NOTIB_PENDENT);
						break;
					case ABSENT:
						resposta.setEstat(EnviamentEstat.ABSENT);
						break;
					case ADRESA_INCORRECTA:
						resposta.setEstat(EnviamentEstat.ADRESA_INCORRECTA);
						break;
					case DESCONEGUT:
						resposta.setEstat(EnviamentEstat.DESCONEGUT);
						break;
					case ENTREGADA_OP:
						resposta.setEstat(EnviamentEstat.ENTREGADA_OP);
						break;
					case ENVIADA_CI:
						resposta.setEstat(EnviamentEstat.ENVIADA_CI);
						break;
					case ENVIADA_DEH:
						resposta.setEstat(EnviamentEstat.ENVIADA_DEH);
						break;
					case ENVIAMENT_PROGRAMAT:
						resposta.setEstat(EnviamentEstat.ENVIAMENT_PROGRAMAT);
						break;
					case ERROR_ENTREGA:
						resposta.setEstat(EnviamentEstat.ERROR_ENTREGA);
						break;
					case EXPIRADA:
						resposta.setEstat(EnviamentEstat.EXPIRADA);
						break;
					case EXTRAVIADA:
						resposta.setEstat(EnviamentEstat.EXTRAVIADA);
						break;
					case LLEGIDA:
						resposta.setEstat(EnviamentEstat.LLEGIDA);
						break;
					case MORT:
						resposta.setEstat(EnviamentEstat.MORT);
						break;
					case NOTIFICADA:
						resposta.setEstat(EnviamentEstat.NOTIFICADA);
						break;
					case PENDENT_CIE:
						resposta.setEstat(EnviamentEstat.PENDENT_CIE);
						break;
					case PENDENT_DEH:
						resposta.setEstat(EnviamentEstat.PENDENT_DEH);
						break;
					case PENDENT_ENVIAMENT:
						resposta.setEstat(EnviamentEstat.PENDENT_ENVIAMENT);
						break;
					case PENDENT_SEU:
						resposta.setEstat(EnviamentEstat.PENDENT_SEU);
						break;
					case REBUTJADA:
						resposta.setEstat(EnviamentEstat.REBUTJADA);
						break;
					case SENSE_INFORMACIO:
						resposta.setEstat(EnviamentEstat.SENSE_INFORMACIO);
						break;
					default:
						break;
					}
				}
				resposta.setEstatData(toDate(respostaConsultaEstat.getEstatData()));
				resposta.setEstatDescripcio(respostaConsultaEstat.getEstatDescripcio());
				resposta.setEstatOrigen(respostaConsultaEstat.getEstatOrigen());
				resposta.setReceptorNif(respostaConsultaEstat.getReceptorNif());
				resposta.setReceptorNom(respostaConsultaEstat.getReceptorNom());
				if (respostaConsultaEstat.getCertificacio() != null) {
					Certificacio certificacio = respostaConsultaEstat.getCertificacio();
					resposta.setCertificacioData(toDate(certificacio.getData()));
					resposta.setCertificacioOrigen(certificacio.getOrigen());
					resposta.setCertificacioContingut(
							Base64.decodeBase64(certificacio.getContingutBase64().getBytes()));
					resposta.setCertificacioHash(certificacio.getHash());
					resposta.setCertificacioMetadades(certificacio.getMetadades());
					resposta.setCertificacioCsv(certificacio.getCsv());
					resposta.setCertificacioTipusMime(certificacio.getTipusMime());
				}
				return resposta;
			}
		} catch (Exception ex) {
			throw new NotificacioPluginException(
					"No s'ha pogut consultar l'estat de l'enviament (" +
					"referencia=" + referencia + ")",
					ex);
		}
	}



	private XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
		if (date == null) {
			return null;
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
	}
	private Date toDate(XMLGregorianCalendar calendar) throws DatatypeConfigurationException {
		if (calendar == null) {
			return null;
		}
		return calendar.toGregorianCalendar().getTime();
	}

	private es.caib.notib.ws.notificacio.Persona toPersonaNotib(
			Persona persona) {
		es.caib.notib.ws.notificacio.Persona p = null;
		if (persona != null) {
			p = new es.caib.notib.ws.notificacio.Persona();
			p.setNif(persona.getNif());
			p.setNom(persona.getNom());
			p.setLlinatge1(persona.getLlinatge1());
			p.setLlinatge2(persona.getLlinatge2());
			p.setTelefon(persona.getTelefon());
			p.setEmail(persona.getEmail());
			p.setInteressatTipus(es.caib.notib.ws.notificacio.InteressatTipusEnumDto.valueOf(persona.getTipus().name()));
			
		}
		return p;
	}
	
	private NotificacioRestClient getNotificacioService() {
		if (clientV2 == null) {
			clientV2 = NotificacioRestClientFactory.getRestClientV2(
					getUrl(),
					getUsername(),
					getPassword());
		}
		return clientV2;
	}

	private String getUrl() {
		return GlobalProperties.getInstance().getProperty("app.notificacio.plugin.url");
	}
	private String getUsername() {
		return GlobalProperties.getInstance().getProperty("app.notificacio.plugin.username");
	}
	private String getPassword() {
		return GlobalProperties.getInstance().getProperty("app.notificacio.plugin.password");
	}

}
