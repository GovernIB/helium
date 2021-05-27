package es.caib.helium.integracio.service.notificacio;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.notificacio.DadesNotificacioDto;
import es.caib.helium.integracio.domini.notificacio.DocumentNotificacio;
import es.caib.helium.integracio.domini.notificacio.Enviament;
import es.caib.helium.integracio.domini.notificacio.Notificacio;
import es.caib.helium.integracio.domini.notificacio.PersonaNotib;
import es.caib.helium.integracio.domini.notificacio.RespostaConsultaEstatEnviament;
import es.caib.helium.integracio.domini.notificacio.RespostaConsultaEstatNotificacio;
import es.caib.helium.integracio.domini.notificacio.RespostaEnviar;
import es.caib.helium.integracio.domini.notificacio.RespostaNotificacio;
import es.caib.helium.integracio.enums.notificacio.EnviamentEstat;
import es.caib.helium.integracio.enums.notificacio.NotificacioEstat;
import es.caib.helium.integracio.excepcions.notificacio.NotificacioException;
import es.caib.helium.integracio.repository.notificacio.NotificacioRepository;
import es.caib.notib.client.NotificacioRestClient;
import es.caib.notib.client.NotificacioRestClientFactory;
import es.caib.notib.ws.notificacio.Certificacio;
import es.caib.notib.ws.notificacio.DocumentV2;
import es.caib.notib.ws.notificacio.EntregaDeh;
import es.caib.notib.ws.notificacio.EntregaPostal;
import es.caib.notib.ws.notificacio.EntregaPostalViaTipusEnum;
import es.caib.notib.ws.notificacio.EnviamentEstatEnum;
import es.caib.notib.ws.notificacio.EnviamentReferencia;
import es.caib.notib.ws.notificacio.EnviamentTipusEnum;
import es.caib.notib.ws.notificacio.NotificaDomiciliConcretTipusEnumDto;
import es.caib.notib.ws.notificacio.NotificaServeiTipusEnumDto;
import es.caib.notib.ws.notificacio.NotificacioV2;
import es.caib.notib.ws.notificacio.RespostaAlta;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificacioServiceNotibImpl implements NotificacioService {

	private NotificacioRestClient client;
	@Autowired
	private NotificacioRepository notificacioRepository;

	public void crearClient(String url, String username, String password, Boolean isBasicAuth, Integer connectTimeout,
			Integer readTimeout) {

		if (client == null) {
			client = NotificacioRestClientFactory.getRestClient(url, username, password, isBasicAuth, connectTimeout,
					readTimeout);
		}
	}

	@Override
	public RespostaNotificacio altaNotificacio(DadesNotificacioDto dto) {

		RespostaNotificacio resposta;
		try {
			var respostaNotib = crearNotificacio(new Notificacio(dto));

			resposta = new RespostaNotificacio();

			resposta.setError(respostaNotib.isError());
			resposta.setErrorDescripcio(respostaNotib.getErrorDescripcio());
			resposta.setEnviamentIdentificador(respostaNotib.getIdentificador());
			resposta.setEnviatData(new Date());
			try {
				resposta.setEstat(NotificacioEstat.valueOf(respostaNotib.getEstat().name()));
			} catch (Exception e) {
				resposta.setError(true);
				resposta.setErrorDescripcio(
						"No s'ha pogut reconèixer l'estat \"" + respostaNotib.getEstat() + "\" de la resposta");
			}
			// Guarda la refererència de l'enviament
			resposta.setEnviamentReferencia(respostaNotib.getReferencies().get(0).getReferencia());
			// TODO: Posar les referències per cada interessat. Pendent desde Helium 3.2
//			for (EnviamentReferencia enviamentReferencia : respostaEnviar.getReferencies()) {
//				for (DocumentEnviamentInteressatEntity documentEnviamentInteressatEntity : notificacioEntity.getDocumentEnviamentInteressats()) {
//					if(documentEnviamentInteressatEntity.getInteressat().getDocumentNum().equals(enviamentReferencia.getTitularNif())) {
//						documentEnviamentInteressatEntity.updateEnviamentReferencia(enviamentReferencia.getReferencia());
//					}
//				}
//			}

			var documentNotificacio = new DocumentNotificacio(dto);
			notificacioRepository.save(documentNotificacio);

		} catch (Exception e) {
			var error = "Error al donar d'alta la notificacio, consultar el log del microservei";
			log.error(error, e);
			resposta = new RespostaNotificacio();
			resposta.setError(true);
			resposta.setErrorDescripcio(error);
		}
		return resposta;
	}

	private RespostaEnviar crearNotificacio(Notificacio notificacio) throws NotificacioException {

		try {

			DocumentV2 document = new DocumentV2();
			document.setArxiuNom(notificacio.getDocumentArxiuNom());
			if (notificacio.getDocumentArxiuContingut() != null) {
				document.setContingutBase64(new String(Base64.encodeBase64(notificacio.getDocumentArxiuContingut())));
			}
			document.setUuid(notificacio.getDocumentArxiuUuid());
			document.setCsv(notificacio.getDocumentArxiuCsv());

			NotificacioV2 notificacioNotib = new NotificacioV2();
			notificacioNotib.setEmisorDir3Codi(notificacio.getEmisorDir3Codi());
			notificacioNotib.setEnviamentTipus(notificacio.getEnviamentTipus() != null
					? EnviamentTipusEnum.valueOf(notificacio.getEnviamentTipus().toString())
					: null);
			notificacioNotib.setConcepte(notificacio.getConcepte());
			notificacioNotib.setDescripcio(notificacio.getDescripcio());
			notificacioNotib
					.setEnviamentDataProgramada(toXmlGregorianCalendar(notificacio.getEnviamentDataProgramada()));
			notificacioNotib.setRetard(notificacio.getRetard());
			notificacioNotib.setCaducitat(toXmlGregorianCalendar(notificacio.getCaducitat()));
			notificacioNotib.setDocument(document);
			notificacioNotib.setProcedimentCodi(notificacio.getProcedimentCodi());
			notificacioNotib.setGrupCodi(notificacio.getGrupCodi());
			notificacioNotib.setUsuariCodi(notificacio.getUsuariCodi());
			notificacioNotib.setNumExpedient(notificacio.getNumExpedient());

			if (notificacio.getEnviaments() != null) {
				for (Enviament enviament : notificacio.getEnviaments()) {

					es.caib.notib.ws.notificacio.Enviament enviamentNotib = new es.caib.notib.ws.notificacio.Enviament();
					enviamentNotib.setServeiTipus(enviament.getServeiTipusEnum() != null
							? NotificaServeiTipusEnumDto.valueOf(enviament.getServeiTipusEnum().toString())
							: null);
					enviamentNotib.setTitular(toPersonaNotib(enviament.getTitular()));
					if (enviament.getDestinataris() != null) {
						for (PersonaNotib destinatari : enviament.getDestinataris()) {
							enviamentNotib.getDestinataris().add(toPersonaNotib(destinatari));
						}
					}
					enviamentNotib.setEntregaPostalActiva(enviament.isEntregaPostalActiva());
					if (enviament.isEntregaPostalActiva()) {
						EntregaPostal entregaPostal = new EntregaPostal();
						entregaPostal.setTipus(NotificaDomiciliConcretTipusEnumDto
								.valueOf(enviament.getEntregaPostalTipus().toString()));
						entregaPostal.setViaTipus(enviament.getEntregaPostalViaTipus() != null
								? EntregaPostalViaTipusEnum.valueOf(enviament.getEntregaPostalViaTipus().toString())
								: null);
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

			RespostaAlta respostaAlta = client.alta(notificacioNotib);

			if (respostaAlta.isError()
					&& (respostaAlta.getReferencies() == null || respostaAlta.getReferencies().isEmpty())) {
				throw new NotificacioException(respostaAlta.getErrorDescripcio());
			}

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
				for (es.caib.notib.ws.notificacio.EnviamentReferencia ref : respostaAlta.getReferencies()) {
					EnviamentReferencia referencia = new EnviamentReferencia();
					referencia.setTitularNif(ref.getTitularNif());
					referencia.setReferencia(ref.getReferencia());
					referencies.add(referencia);
				}
				resposta.setReferencies(referencies);
			}
			resposta.setError(respostaAlta.isError());
			resposta.setErrorDescripcio(respostaAlta.getErrorDescripcio());
			return resposta;

		} catch (Exception ex) {
			throw new NotificacioException("No s'ha pogut enviar la notificació (" + "emisorDir3Codi="
					+ notificacio.getEmisorDir3Codi() + ", " + "enviamentTipus=" + notificacio.getEnviamentTipus()
					+ ", " + "concepte=" + notificacio.getConcepte() + ")", ex);
		}
	}

	@Override
	public RespostaConsultaEstatNotificacio consultaNotificacio(String identificador) throws NotificacioException {

		RespostaConsultaEstatNotificacio resposta = null;
		try {
			es.caib.notib.ws.notificacio.RespostaConsultaEstatNotificacio respostaConsultaEstat = client
					.consultaEstatNotificacio(identificador);
			resposta = new RespostaConsultaEstatNotificacio();
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
				case PROCESSADA:
					resposta.setEstat(NotificacioEstat.PROCESSADA);
					break;
				default:
					break;
				}
			}
			resposta.setError(respostaConsultaEstat.isError());
			resposta.setErrorData(respostaConsultaEstat.getErrorData() != null
					? respostaConsultaEstat.getErrorData().toGregorianCalendar().getTime()
					: null);
			resposta.setErrorDescripcio(respostaConsultaEstat.getErrorDescripcio());
		} catch (Exception ex) {
			throw new NotificacioException(
					"No s'ha pogut consultar l'estat de la notificació (" + "identificador=" + identificador + ")", ex);
		}
		return resposta;
	}

	@Override
	public RespostaConsultaEstatEnviament consultaEnviament(String referencia) throws NotificacioException {
		
		try {
			es.caib.notib.ws.notificacio.RespostaConsultaEstatEnviament respostaConsultaEstat = client
					.consultaEstatEnviament(referencia);

			RespostaConsultaEstatEnviament resposta = new RespostaConsultaEstatEnviament();

			resposta.setEstat(toEnviamentEstat(respostaConsultaEstat.getEstat()));
			resposta.setEstatData(toDate(respostaConsultaEstat.getEstatData()));
			resposta.setEstatDescripcio(respostaConsultaEstat.getEstatDescripcio());
			resposta.setEstatOrigen(respostaConsultaEstat.getEstatOrigen());
			resposta.setReceptorNif(respostaConsultaEstat.getReceptorNif());
			resposta.setReceptorNom(respostaConsultaEstat.getReceptorNom());
			if (respostaConsultaEstat.getCertificacio() != null) {
				Certificacio certificacio = respostaConsultaEstat.getCertificacio();
				resposta.setCertificacioData(toDate(certificacio.getData()));
				resposta.setCertificacioOrigen(certificacio.getOrigen());
				resposta.setCertificacioContingut(Base64.decodeBase64(certificacio.getContingutBase64().getBytes()));
				resposta.setCertificacioHash(certificacio.getHash());
				resposta.setCertificacioMetadades(certificacio.getMetadades());
				resposta.setCertificacioCsv(certificacio.getCsv());
				resposta.setCertificacioTipusMime(certificacio.getTipusMime());
			}
			resposta.setError(respostaConsultaEstat.isError());
			resposta.setErrorDescripcio(respostaConsultaEstat.getErrorDescripcio());

			return resposta;
		} catch (Exception ex) {
			throw new NotificacioException(
					"No s'ha pogut consultar l'estat de l'enviament (" + "referencia=" + referencia + ")", ex);
		}
	}

	private EnviamentEstat toEnviamentEstat(EnviamentEstatEnum estat) {
		
		if (estat == null) {
			return null;
		}
		
		EnviamentEstat enviamentEstat = null;
		switch (estat) {
			case NOTIB_ENVIADA:
				enviamentEstat = EnviamentEstat.NOTIB_ENVIADA;
				break;
			case NOTIB_PENDENT:
				enviamentEstat = EnviamentEstat.NOTIB_PENDENT;
				break;
			case ABSENT:
				enviamentEstat = EnviamentEstat.ABSENT;
				break;
			case ADRESA_INCORRECTA:
				enviamentEstat = EnviamentEstat.ADRESA_INCORRECTA;
				break;
			case DESCONEGUT:
				enviamentEstat = EnviamentEstat.DESCONEGUT;
				break;
			case ENTREGADA_OP:
				enviamentEstat = EnviamentEstat.ENTREGADA_OP;
				break;
			case ENVIADA_CI:
				enviamentEstat = EnviamentEstat.ENVIADA_CI;
				break;
			case ENVIADA_DEH:
				enviamentEstat = EnviamentEstat.ENVIADA_DEH;
				break;
			case ENVIAMENT_PROGRAMAT:
				enviamentEstat = EnviamentEstat.ENVIAMENT_PROGRAMAT;
				break;
			case ERROR_ENTREGA:
				enviamentEstat = EnviamentEstat.ERROR_ENTREGA;
				break;
			case EXPIRADA:
				enviamentEstat = EnviamentEstat.EXPIRADA;
				break;
			case EXTRAVIADA:
				enviamentEstat = EnviamentEstat.EXTRAVIADA;
				break;
			case LLEGIDA:
				enviamentEstat = EnviamentEstat.LLEGIDA;
				break;
			case MORT:
				enviamentEstat = EnviamentEstat.MORT;
				break;
			case NOTIFICADA:
				enviamentEstat = EnviamentEstat.NOTIFICADA;
				break;
			case PENDENT_CIE:
				enviamentEstat = EnviamentEstat.PENDENT_CIE;
				break;
			case PENDENT_DEH:
				enviamentEstat = EnviamentEstat.PENDENT_DEH;
				break;
			case PENDENT_ENVIAMENT:
				enviamentEstat = EnviamentEstat.PENDENT_ENVIAMENT;
				break;
			case PENDENT_SEU:
				enviamentEstat = EnviamentEstat.PENDENT_SEU;
				break;
			case REBUTJADA:
				enviamentEstat = EnviamentEstat.REBUTJADA;
				break;
			case SENSE_INFORMACIO:
				enviamentEstat = EnviamentEstat.SENSE_INFORMACIO;
				break;
			case ANULADA:
				enviamentEstat = EnviamentEstat.ANULADA;
				break;
			case ENVIADA:
				enviamentEstat = EnviamentEstat.ENVIADA;
				break;
			case ENVIAT_SIR:
				enviamentEstat = EnviamentEstat.ENVIAT_SIR;
				break;
			case FINALITZADA:
				enviamentEstat = EnviamentEstat.FINALITZADA;
				break;
			case PENDENT:
				enviamentEstat = EnviamentEstat.PENDENT;
				break;
			case PROCESSADA:
				enviamentEstat = EnviamentEstat.PROCESSADA;
				break;
			case REGISTRADA:
				enviamentEstat = EnviamentEstat.REGISTRADA;
				break;
		}
		
		return enviamentEstat;
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

	private es.caib.notib.ws.notificacio.Persona toPersonaNotib(PersonaNotib persona) {
		
		es.caib.notib.ws.notificacio.Persona p = null;
		if (persona == null) {
			return p;
		}
		p = new es.caib.notib.ws.notificacio.Persona();
		p.setTelefon(persona.getTelefon());
		p.setEmail(persona.getEmail());
		p.setInteressatTipus(es.caib.notib.ws.notificacio.InteressatTipusEnumDto.valueOf(persona.getTipus().name()));
		p.setNif(persona.getNif());
		p.setNom(persona.getNom());
		switch (persona.getTipus()) {
			case FISICA:
				p.setLlinatge1(persona.getLlinatge1());
				p.setLlinatge2(persona.getLlinatge2());
				break;
			case ADMINISTRACIO:
				p.setDir3Codi(persona.getCodiDir3());
				break;
			default:
				break;
		}
		return p;
	}
}
