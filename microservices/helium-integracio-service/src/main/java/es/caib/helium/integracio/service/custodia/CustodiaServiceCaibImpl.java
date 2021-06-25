package es.caib.helium.integracio.service.custodia;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.custodia.ClienteCustodiaCaib;
import es.caib.helium.integracio.domini.custodia.CustodiaRequest;
import es.caib.helium.integracio.domini.validacio.DadesCertificat;
import es.caib.helium.integracio.domini.validacio.RespostaValidacioSignatura;
import es.caib.helium.integracio.excepcions.custodia.CustodiaException;
import es.caib.helium.integracio.service.monitor.MonitorIntegracionsService;
import es.caib.helium.jms.domini.Parametre;
import es.caib.helium.jms.enums.CodiIntegracio;
import es.caib.helium.jms.enums.EstatAccio;
import es.caib.helium.jms.enums.TipusAccio;
import es.caib.helium.jms.events.IntegracioEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustodiaServiceCaibImpl implements CustodiaService {

	@Setter
	private ClienteCustodiaCaib client;
	@Setter
	private Map<String, String> cacheHash = new HashMap<String, String>();
	@Autowired
	private MonitorIntegracionsService monitor;
	
	@Override
	public String addSignature(CustodiaRequest request, Long entornId) throws CustodiaException {
		
		var t0  = System.currentTimeMillis();
		var descripcio = "Enviar document a custodia";
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("documentId", request.getDocumentId()));
		parametres.add(new Parametre("gesdocId", request.getGesDocId()));
		parametres.add(new Parametre("nomArxiuSignat", request.getNomArxiuSignat()));
		parametres.add(new Parametre("codiTipusCustodia", request.getCodiTipusCustodia()));
		try {
			var custodiaId = request.getDocumentId(); //getIdCustodia(id); Aquest mètode no està fent res a Helium 3.2
			var xml = client.custodiarPDFFirmado(new ByteArrayInputStream(request.getSignatura()), request.getNomArxiuSignat(), custodiaId, request.getCodiTipusCustodia());
			var resposta = client.parseResponse(xml);
			if (resposta.isError()) {
				throw new CustodiaException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
			}
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.CUSTODIA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Document enviat a custodia");
			return custodiaId;
			
		} catch (Exception ex) {
			var error = "No s'ha pogut custodiar la signatura";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.CUSTODIA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new CustodiaException(error, ex);
		}
	}
	
	@Override
	public List<byte[]> getSignatures(String docId, Long entornId) throws CustodiaException {
		
		var t0  = System.currentTimeMillis();
		var descripcio = "Obtenció de signatures";
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("documentId", docId));	
		try {
			var consultar = client.consultarDocumento(docId);
			var iniciXml = new byte[5];
			for (var i = 0; i < 5; i++) {
				iniciXml[i] = consultar[i];
			}
			if ("<?xml".equals(new String(iniciXml))) {
				var resposta = client.parseResponse(consultar);
				throw new CustodiaException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
			} 
			
			List<byte[]> resposta = new ArrayList<byte[]>();
			resposta.add(consultar);
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.CUSTODIA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Signatures obtingudes");
			return resposta;
			
		} catch (Exception ex) {
			var error = "No s'han pogut obtenirles signatures de la custòdia (documentId=" + docId + ")";
			log.error(error ,ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.CUSTODIA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new CustodiaException(error, ex);
		}
	}

	@Override
	public boolean deleteSignatures(String docId, Long entornId) throws CustodiaException {
		
		var t0  = System.currentTimeMillis();
		var descripcio = "Esborrar signatura";
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("documentId", docId));	
		try {
			var xml = client.eliminarDocumento(docId);
			var resposta = client.parseResponse(xml);
//			if (resposta.isError() && !"DOCUMENTO_NO_ENCONTRADO".equals(resposta.getErrorCodi())) {
			// Comentada la condicio original per poder obtenir la resposta correcta del microservei
			if (resposta.isError()) {
				throw new CustodiaException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
			}
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.CUSTODIA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Signatura esborrada");
			return true;
			
		} catch (Exception ex) {
			var error = "No s'ha pogut esborrar la signatura";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.CUSTODIA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new CustodiaException(error, ex);
		}
	}
	
	@Override
	public byte[] getSignaturesAmbArxiu(String docId, Long entornId) throws CustodiaException {
		
		var t0  = System.currentTimeMillis();
		var descripcio = "Obtenir signatures amb arxiu";
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("documentId", docId));	
		try {
			byte[] consultar = client.consultarDocumento(docId);
			byte[] iniciXml = new byte[5];
			for (int i = 0; i < 5; i++)
				iniciXml[i] = consultar[i];
			if ("<?xml".equals(new String(iniciXml))) {
				var resposta = client.parseResponse(consultar);
				throw new CustodiaException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
			} 
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.CUSTODIA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Obtingudes signatures amb arxiu");
			return consultar;
			
		} catch (Exception ex) {
			var error = "No s'ha pogut obtenir l'arxiu amb les signatures";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.CUSTODIA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new CustodiaException(error, ex);
		}
	}
	
	@Override
	public List<RespostaValidacioSignatura> dadesValidacioSignatura(String docId, Long entornId) throws CustodiaException {
		
		var t0  = System.currentTimeMillis();
		var descripcio = "Obtenció de dades de validació de signatura";
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("documentId", docId));	
		try {
			var xml = client.verificarDocumento(docId);
			var resposta = client.parseResponse(xml);
			if (resposta.isError()) {
				throw new CustodiaException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
			}
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.CUSTODIA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Dades de validació de signatura obtingudes correctament");
			return parseSignatures(xml);
			
		} catch (Exception ex) {
			var error = "No s'han pogut obtenir les dades de validació de la signatura";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.CUSTODIA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new CustodiaException(error, ex);
		}
	}
	
	@Override
	public String getUrlComprovacioSignatura(String docId, Long entornId) throws CustodiaException {
		
		var t0  = System.currentTimeMillis();
		var descripcio = "Obtenir URL de comprovació de signatura";
		List<Parametre> parametres = new ArrayList<>();
		parametres.add(new Parametre("documentId", docId));	
		try {
			String token = cacheHash.get(docId);
			if (token == null) {
				byte[] xml = client.reservarDocumento(docId);
				token = new String(xml);
				if (token.startsWith("<?xml")) {
					var resposta = client.parseResponse(xml);
					if (resposta.isError()) {
						throw new CustodiaException("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
					}
				}
				cacheHash.put(docId, token);
			}
			
			monitor.enviarEvent(IntegracioEvent.builder()
					.codi(CodiIntegracio.CUSTODIA)
					.entornId(entornId)
					.descripcio(descripcio)
					.data(new Date())
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.OK)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0).build());
			
			log.debug("Obtinguda URL de comprovacio de signatura");
			return client.getBaseUrl() + token;
			
		} catch (Exception ex) {
			var error = "No s'ha pogut generar la url de comprovació de signatura";
			log.error(error, ex);
			monitor.enviarEvent(IntegracioEvent.builder().codi(CodiIntegracio.CUSTODIA) 
					.entornId(entornId) 
					.descripcio(descripcio)
					.tipus(TipusAccio.ENVIAMENT)
					.estat(EstatAccio.ERROR)
					.parametres(parametres)
					.tempsResposta(System.currentTimeMillis() - t0)
					.errorDescripcio(error)
					.excepcioMessage(ex.getMessage())
					.excepcioStacktrace(ExceptionUtils.getStackTrace(ex)).build());
			throw new CustodiaException(error, ex);
		}
	}
	
	private List<RespostaValidacioSignatura> parseSignatures(byte[] response) throws DocumentException {
		
		List<RespostaValidacioSignatura> resposta = new ArrayList<RespostaValidacioSignatura>();
		var document = DocumentHelper.parseText(new String(response));
		var resultadoFirmas = document.getRootElement().element("VerificacionUltimaCustodia").element("ResultadoFirmas");
		var it = (Iterator<Element>)resultadoFirmas.elementIterator("ResultadoFirma");
		while (it.hasNext()) {
			var element = it.next();
			var certInfo = element.element("ValidacionCertificado");
			var verificada = "true".equalsIgnoreCase(certInfo.element("verificado").getText());
			var res = new RespostaValidacioSignatura();
			if (verificada) {
				res.setEstat(RespostaValidacioSignatura.ESTAT_OK);
			} else {
				res.setEstat(RespostaValidacioSignatura.ESTAT_ERROR);
			}
			//String certNumSerie = certInfo.element("numeroSerie").getText();
			String certSubject = certInfo.element("subjectName").getText();
			var dadesCertificat = new DadesCertificat();
			//dadesCertificat.setTipoCertificado();
			//dadesCertificat.setSubject();
			//dadesCertificat.setNombreResponsable(getCertSubjectToken(certSubject, "CN="));
			dadesCertificat.setNombreResponsable(getCertSubjectToken(certSubject, "GIVENNAME="));
			var cognoms = getCertSubjectToken(certSubject, "SURNAME=");
			var parts = cognoms.split(" ");
			if (parts.length == 1) {
				dadesCertificat.setPrimerApellidoResponsable(parts[0]);
			} else {
				StringBuilder sb = new StringBuilder();
				for (int i = 1; i < parts.length; i++) {
					sb.append(parts[i]);
					sb.append(" ");
				}
				dadesCertificat.setSegundoApellidoResponsable(sb.toString());
			}
			dadesCertificat.setNifResponsable(getCertSubjectToken(certSubject, "SERIALNUMBER="));
			//dadesCertificat.setIdEmisor();
			//dadesCertificat.setNifCif();
			dadesCertificat.setEmail(getCertSubjectToken(certSubject, ",E="));
			//dadesCertificat.setFechaNacimiento();
			//dadesCertificat.setRazonSocial();
			//dadesCertificat.setClasificacion();
			//dadesCertificat.setNumeroSerie();
			List<DadesCertificat> dc = new ArrayList<DadesCertificat>();
			dc.add(dadesCertificat);
			res.setDadesCertificat(dc);
			resposta.add(res);
		}
		return resposta;
	}
	
	private String getCertSubjectToken(String certSubject, String token) {
		
		var indexInici = certSubject.indexOf(token);
		var indexFi = certSubject.indexOf(",", indexInici + token.length());
		indexInici += token.length();
		//Afegida condicio, si token es ultim element indexFi sera -1
		return indexFi  != -1 ? certSubject.substring(indexInici, indexFi) : certSubject.substring(indexInici, certSubject.length());
	}
}
