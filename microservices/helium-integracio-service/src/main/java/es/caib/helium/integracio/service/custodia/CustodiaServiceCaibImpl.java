package es.caib.helium.integracio.service.custodia;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.custodia.ClienteCustodiaCaib;
import es.caib.helium.integracio.domini.custodia.CustodiaRequest;
import es.caib.helium.integracio.domini.validacio.DadesCertificat;
import es.caib.helium.integracio.domini.validacio.RespostaValidacioSignatura;
import es.caib.helium.integracio.excepcions.custodia.CustodiaException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustodiaServiceCaibImpl implements CustodiaService {

	@Setter
	private ClienteCustodiaCaib client;
	@Setter
	private Map<String, String> cacheHash = new HashMap<String, String>();
	
	@Override
	public String addSignature(CustodiaRequest request) throws CustodiaException {
		
		try {
			var custodiaId = request.getId(); //getIdCustodia(id); Aquest mètode no està fent res a Helium 3.2
			var xml = client.custodiarPDFFirmado(new ByteArrayInputStream(request.getSignatura()), request.getArxiuNom(), custodiaId, request.getTipusDocument());
			var resposta = client.parseResponse(xml);
			if (resposta.isError()) {
				log.error("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
				return "";
			}
			return custodiaId;
		} catch (Exception ex) {
			throw new CustodiaException("No s'ha pogut custodiar la signatura", ex);
		}
	}
	
	@Override
	public List<byte[]> getSignatures(String docId) throws CustodiaException {
		
		try {
			var consultar = client.consultarDocumento(docId);
			var iniciXml = new byte[5];
			for (var i = 0; i < 5; i++) {
				iniciXml[i] = consultar[i];
			}
			if ("<?xml".equals(new String(iniciXml))) {
				var resposta = client.parseResponse(consultar);
				log.error("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
				return new ArrayList<byte[]>();
			} 
			
			List<byte[]> resposta = new ArrayList<byte[]>();
			resposta.add(consultar);
			return resposta;
		} catch (Exception ex) {
			throw new CustodiaException("No s'han pogut obtenir les signatures", ex);
		}
	}

	@Override
	public boolean deleteSignatures(String docId) throws CustodiaException {
		
		try {
			var xml = client.eliminarDocumento(docId);
			var resposta = client.parseResponse(xml);
//			if (resposta.isError() && !"DOCUMENTO_NO_ENCONTRADO".equals(resposta.getErrorCodi())) {
			// Comentada la condicio original per poder obtenir la resposta correcta del microservei
			if (resposta.isError()) {
				log.error("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
				return false;
			}
			return true;
		} catch (Exception ex) {
			throw new CustodiaException("No s'han pogut esborrar les signatures", ex);
		}
	}
	
	@Override
	public byte[] getSignaturesAmbArxiu(String docId) throws CustodiaException {
		
		try {
			byte[] consultar = client.consultarDocumento(docId);
			byte[] iniciXml = new byte[5];
			for (int i = 0; i < 5; i++)
				iniciXml[i] = consultar[i];
			if ("<?xml".equals(new String(iniciXml))) {
				var resposta = client.parseResponse(consultar);
				log.error("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
			} 
			return consultar;
		} catch (Exception ex) {
			throw new CustodiaException("No s'ha pogut obtenir l'arxiu amb les signatures", ex);
		}
	}
	
	@Override
	public List<RespostaValidacioSignatura> dadesValidacioSignatura(String id) throws CustodiaException {
		
		try {
			var xml = client.verificarDocumento(id);
			var resposta = client.parseResponse(xml);
			if (resposta.isError()) {
				log.error("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
				return new ArrayList<RespostaValidacioSignatura>();
			}
			return parseSignatures(xml);
		} catch (Exception ex) {
			throw new CustodiaException("No s'han pogut verificar les signatures", ex);
		}
	}
	
	@Override
	public String getUrlComprovacioSignatura(String docId) throws CustodiaException {
		try {
			String token = cacheHash.get(docId);
			if (token == null) {
				byte[] xml = client.reservarDocumento(docId);
				token = new String(xml);
				if (token.startsWith("<?xml")) {
					var resposta = client.parseResponse(xml);
					if (resposta.isError()) {
						log.error("Error en la petició de custòdia: [" + resposta.getErrorCodi() + "] " + resposta.getErrorDescripcio());
						
					}
				}
				cacheHash.put(docId, token);
			}
			return client.getBaseUrl() + token;
		} catch (Exception ex) {
			throw new CustodiaException("No s'ha pogut generar la url de comprovació de signatura", ex);
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
