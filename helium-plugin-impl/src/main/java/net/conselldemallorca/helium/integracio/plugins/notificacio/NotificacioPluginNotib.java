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
import org.apache.commons.codec.digest.DigestUtils;

import es.caib.notib.client.NotificacioRestClientFactory;
import es.caib.notib.ws.notificacio.Certificacio;
import es.caib.notib.ws.notificacio.Document;
import es.caib.notib.ws.notificacio.EntregaDeh;
import es.caib.notib.ws.notificacio.EntregaPostal;
import es.caib.notib.ws.notificacio.EntregaPostalTipusEnum;
import es.caib.notib.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.ws.notificacio.EnviamentTipusEnum;
import es.caib.notib.ws.notificacio.NotificacioService;
import es.caib.notib.ws.notificacio.PagadorCie;
import es.caib.notib.ws.notificacio.PagadorPostal;
import es.caib.notib.ws.notificacio.ParametresSeu;
import es.caib.notib.ws.notificacio.RespostaAlta;
import net.conselldemallorca.helium.core.util.GlobalProperties;

/**
 * Implementació de del plugin d'enviament de notificacions
 * emprant NOTIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NotificacioPluginNotib implements NotificacioPlugin {

	private NotificacioService notificacioService;

	@Override
	public RespostaEnviar enviar(
			Notificacio notificacio) throws NotificacioPluginException {
		try {
			es.caib.notib.ws.notificacio.Notificacio notificacioNotib = new es.caib.notib.ws.notificacio.Notificacio();
			notificacioNotib.setEmisorDir3Codi(notificacio.getEmisorDir3Codi());
			if (notificacio.getEnviamentTipus() != null) {
				switch (notificacio.getEnviamentTipus()) {
				case COMUNICACIO:
					notificacioNotib.setEnviamentTipus(EnviamentTipusEnum.COMUNICACIO);
					break;
				case NOTIFICACIO:
					notificacioNotib.setEnviamentTipus(EnviamentTipusEnum.NOTIFICACIO);
					break;
				}
			}
			notificacioNotib.setConcepte(notificacio.getConcepte());
			notificacioNotib.setDescripcio(notificacio.getDescripcio());
			notificacioNotib.setEnviamentDataProgramada(
					toXmlGregorianCalendar(notificacio.getEnviamentDataProgramada()));
			notificacioNotib.setRetard(notificacio.getRetard());
			notificacioNotib.setCaducitat(
					toXmlGregorianCalendar(notificacio.getCaducitat()));
			Document document = new Document();
			document.setArxiuNom(notificacio.getDocumentArxiuNom());
			document.setContingutBase64(
					new String(Base64.encodeBase64(notificacio.getDocumentArxiuContingut())));
			document.setHash(
					new String(Base64.encodeBase64(
							DigestUtils.sha256Hex(notificacio.getDocumentArxiuContingut()).getBytes())));
			notificacioNotib.setDocument(document);
			notificacioNotib.setProcedimentCodi(notificacio.getProcedimentCodi());
			if (notificacio.getPagadorPostalDir3Codi() != null) {
				PagadorPostal pagadorPostal = new PagadorPostal();
				pagadorPostal.setDir3Codi(
						notificacio.getPagadorPostalDir3Codi());
				pagadorPostal.setContracteNum(
						notificacio.getPagadorPostalContracteNum());
				pagadorPostal.setContracteDataVigencia(
						toXmlGregorianCalendar(notificacio.getPagadorPostalContracteDataVigencia()));
				pagadorPostal.setFacturacioClientCodi(
						notificacio.getPagadorPostalFacturacioClientCodi());
				notificacioNotib.setPagadorPostal(pagadorPostal);
			}
			if (notificacio.getPagadorCieDir3Codi() != null) {
				PagadorCie pagadorCie = new PagadorCie();
				pagadorCie.setDir3Codi(
						notificacio.getPagadorCieDir3Codi());
				pagadorCie.setContracteDataVigencia(
						toXmlGregorianCalendar(notificacio.getPagadorCieContracteDataVigencia()));
				notificacioNotib.setPagadorCie(pagadorCie);
			}
			if (notificacio.getEnviaments() != null) {
				for (Enviament enviament: notificacio.getEnviaments()) {
					es.caib.notib.ws.notificacio.Enviament enviamentNotib = new es.caib.notib.ws.notificacio.Enviament();
					enviamentNotib.setTitular(
							toPersonaNotib(enviament.getTitular()));
					if (enviament.getDestinataris() != null) {
						for (Persona destinatari: enviament.getDestinataris()) {
							enviamentNotib.getDestinataris().add(
									toPersonaNotib(destinatari));
						}
					}
					if (enviament.getEntregaPostalTipus() != null) {
						EntregaPostal entregaPostal = new EntregaPostal();
						switch (enviament.getEntregaPostalTipus()) {
						case NACIONAL:
							entregaPostal.setTipus(EntregaPostalTipusEnum.NACIONAL);
							break;
						case ESTRANGER:
							entregaPostal.setTipus(EntregaPostalTipusEnum.ESTRANGER);
							break;
						case APARTAT_CORREUS:
							entregaPostal.setTipus(EntregaPostalTipusEnum.APARTAT_CORREUS);
							break;
						case SENSE_NORMALITZAR:
							entregaPostal.setTipus(EntregaPostalTipusEnum.SENSE_NORMALITZAR);
							break;
						}
						if (enviament.getEntregaPostalViaTipus() != null) {
							switch (enviament.getEntregaPostalViaTipus()) {
							case ALAMEDA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.ALAMEDA);
								break;
							case AVENIDA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.AVENIDA);
								break;
							case AVINGUDA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.AVINGUDA);
								break;
							case BARRIO:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.BARRIO);
								break;
							case BULEVAR:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.BULEVAR);
								break;
							case CALLE:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CALLE);
								break;
							case CALLEJA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CALLEJA);
								break;
							case CAMI:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CAMI);
								break;
							case CAMINO:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CAMINO);
								break;
							case CAMPO:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CAMPO);
								break;
							case CARRER:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CARRER);
								break;
							case CARRERA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CARRERA);
								break;
							case CARRETERA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CARRETERA);
								break;
							case CUESTA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.CUESTA);
								break;
							case EDIFICIO:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.EDIFICIO);
								break;
							case ENPARANTZA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.ENPARANTZA);
								break;
							case ESTRADA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.ESTRADA);
								break;
							case GLORIETA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.GLORIETA);
								break;
							case JARDINES:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.JARDINES);
								break;
							case JARDINS:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.JARDINS);
								break;
							case KALEA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.KALEA);
								break;
							case OTROS:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.OTROS);
								break;
							case PARQUE:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.PARQUE);
								break;
							case PASAJE:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.PASAJE);
								break;
							case PASEO:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.PASEO);
								break;
							case PASSATGE:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.PASSATGE);
								break;
							case PASSEIG:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.PASSEIG);
								break;
							case PLACETA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.PLACETA);
								break;
							case PLAZA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.PLAZA);
								break;
							case PLAZUELA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.PLAZUELA);
								break;
							case PLAÇA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.PLAÇA);
								break;
							case POBLADO:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.POBLADO);
								break;
							case POLIGONO:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.POLIGONO);
								break;
							case PRAZA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.PRAZA);
								break;
							case RAMBLA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.RAMBLA);
								break;
							case RONDA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.RONDA);
								break;
							case RUA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.RUA);
								break;
							case SECTOR:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.SECTOR);
								break;
							case TRAVESIA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.TRAVESIA);
								break;
							case TRAVESSERA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.TRAVESSERA);
								break;
							case URBANIZACION:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.URBANIZACION);
								break;
							case VIA:
								entregaPostal.setViaTipus(EntregaPostalViaTipusEnum.VIA);
								break;
							}
						}
						entregaPostal.setViaNom(
								enviament.getEntregaPostalViaNom());
						entregaPostal.setNumeroCasa(
								enviament.getEntregaPostalNumeroCasa());
						entregaPostal.setNumeroQualificador(
								enviament.getEntregaPostalNumeroQualificador());
						entregaPostal.setPuntKm(
								enviament.getEntregaPostalPuntKm());
						entregaPostal.setApartatCorreus(
								enviament.getEntregaPostalApartatCorreus());
						entregaPostal.setPortal(
								enviament.getEntregaPostalPortal());
						entregaPostal.setEscala(
								enviament.getEntregaPostalEscala());
						entregaPostal.setPlanta(
								enviament.getEntregaPostalPlanta());
						entregaPostal.setPorta(
								enviament.getEntregaPostalPorta());
						entregaPostal.setBloc(
								enviament.getEntregaPostalBloc());
						entregaPostal.setComplement(
								enviament.getEntregaPostalComplement());
						entregaPostal.setCodiPostal(
								enviament.getEntregaPostalCodiPostal());
						entregaPostal.setPoblacio(
								enviament.getEntregaPostalPoblacio());
						entregaPostal.setMunicipiCodi(
								enviament.getEntregaPostalMunicipiCodi());
						entregaPostal.setProvinciaCodi(
								enviament.getEntregaPostalProvinciaCodi());
						entregaPostal.setPaisCodi(
								enviament.getEntregaPostalPaisCodi());
						entregaPostal.setLinea1(
								enviament.getEntregaPostalLinea1());
						entregaPostal.setLinea2(
								enviament.getEntregaPostalLinea2());
						entregaPostal.setCie(
								enviament.getEntregaPostalCie());
						entregaPostal.setFormatSobre(
								enviament.getEntregaPostalFormatSobre());
						entregaPostal.setFormatFulla(
								enviament.getEntregaPostalFormatFulla());
						enviamentNotib.setEntregaPostal(entregaPostal);
					}
					if (enviament.getEntregaDehProcedimentCodi() != null) {
						EntregaDeh entregaDeh = new EntregaDeh();
						entregaDeh.setObligat(enviament.isEntregaDehObligat());
						entregaDeh.setProcedimentCodi(enviament.getEntregaDehProcedimentCodi());
						enviamentNotib.setEntregaDeh(entregaDeh);
					}
					notificacioNotib.getEnviaments().add(enviamentNotib);
				}
			}
			ParametresSeu parametresSeu = new ParametresSeu();
			parametresSeu.setExpedientSerieDocumental(
					notificacio.getSeuExpedientSerieDocumental());
			parametresSeu.setProcedimentCodi(
					notificacio.getSeuProcedimentCodi());
			parametresSeu.setExpedientUnitatOrganitzativa(
					notificacio.getSeuExpedientUnitatOrganitzativa());
			parametresSeu.setExpedientIdentificadorEni(
					notificacio.getSeuExpedientIdentificadorEni());
			parametresSeu.setExpedientSerieDocumental(
					notificacio.getSeuExpedientSerieDocumental());
			parametresSeu.setExpedientTitol(
					notificacio.getSeuExpedientTitol());
			parametresSeu.setRegistreOficina(
					notificacio.getSeuRegistreOficina());
			parametresSeu.setRegistreLlibre(
					notificacio.getSeuRegistreLlibre());
			parametresSeu.setRegistreOrgan(
					notificacio.getSeuRegistreOrgan());
			parametresSeu.setIdioma(
					notificacio.getSeuIdioma());
			parametresSeu.setAvisTitol(
					notificacio.getSeuAvisTitol());
			parametresSeu.setAvisText(
					notificacio.getSeuAvisText());
			parametresSeu.setAvisTextMobil(
					notificacio.getSeuAvisTextMobil());
			parametresSeu.setOficiTitol(
					notificacio.getSeuOficiTitol());
			parametresSeu.setOficiText(
					notificacio.getSeuOficiText());
			notificacioNotib.setParametresSeu(parametresSeu);
			RespostaAlta respostaAlta = getNotificacioService().alta(notificacioNotib);
			if (respostaAlta.isError()) {
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
		}
		return p;
	}

	private NotificacioService getNotificacioService() {
		if (notificacioService == null) {
			notificacioService = NotificacioRestClientFactory.getRestClient(
					getUrl(),
					getUsername(),
					getPassword());
		}
		return notificacioService;
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
