package es.caib.helium.integracio.service.registre;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.stereotype.Service;

import es.caib.helium.integracio.domini.registre.DocumentRegistre;
import es.caib.helium.integracio.domini.registre.RegistreAssentament;
import es.caib.helium.integracio.domini.registre.RegistreAssentamentInteressat;
import es.caib.helium.integracio.domini.registre.RespostaAnotacioRegistre;
import es.caib.helium.integracio.domini.registre.RespostaConsultaRegistre;
import es.caib.helium.integracio.excepcions.registre.RegistreException;
import es.caib.regweb3.ws.api.v3.AnexoWs;
import es.caib.regweb3.ws.api.v3.AsientoRegistralWs;
import es.caib.regweb3.ws.api.v3.DatosInteresadoWs;
import es.caib.regweb3.ws.api.v3.InteresadoWs;
import es.caib.regweb3.ws.api.v3.RegWebAsientoRegistralWs;
import es.caib.regweb3.ws.api.v3.RegWebRegistroEntradaWs;
import es.caib.regweb3.ws.api.v3.RegistroEntradaWs;
import es.caib.regweb3.ws.api.v3.WsI18NException;
import es.caib.regweb3.ws.api.v3.WsValidationException;
import es.caib.regweb3.ws.api.v3.utils.WsClientUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Setter
public class RegistreServiceRegWeb3Impl implements RegistreService {
	
	private RegWebAsientoRegistralWs asientoRegistralApi;
	private RegWebRegistroEntradaWs registroEntradaApi; // TODO PENDEN DE SI ES FA SERVIR O NO
	
	@Override
	public Date obtenirDataJustificant(String numeroRegistre) throws RegistreException {
		
		// TODO No forma part d'aquesta implementacio
		
		return null;
	}
	
	@Override
	public RespostaAnotacioRegistre registrarSortida(RegistreAssentament registreSortida, String aplicacioNom, String aplicacioVersio) 
			throws RegistreException {

		AsientoRegistralWs registroSalidaWs = new AsientoRegistralWs();
		
    	registroSalidaWs.setTipoRegistro(2L); // Assentament de tipus sortida
        registroSalidaWs.setUnidadTramitacionOrigenCodigo(registreSortida.getOrgan());
        registroSalidaWs.setEntidadRegistralOrigenCodigo(registreSortida.getOficinaCodi());
        registroSalidaWs.setLibroCodigo(registreSortida.getLlibreCodi());
        registroSalidaWs.setResumen(registreSortida.getExtracte());
        registroSalidaWs.setTipoDocumentacionFisicaCodigo(registreSortida.getDocumentacioFisicaCodi() != null ? new Long(registreSortida.getDocumentacioFisicaCodi()) : (long)3);
        registroSalidaWs.setIdioma(getIdioma(registreSortida.getIdiomaCodi()));
        registroSalidaWs.setAplicacion(aplicacioNom);
        registroSalidaWs.setVersion(aplicacioVersio);
        registroSalidaWs.setCodigoUsuario(registreSortida.getUsuariCodi());
        registroSalidaWs.setNumeroExpediente(registreSortida.getExpedientNumero());
        registroSalidaWs.setObservaciones(registreSortida.getObservacions());
        registroSalidaWs.setReferenciaExterna(registreSortida.getReferencia());
        registroSalidaWs.setCodigoAsunto(registreSortida.getAssumpteCodi());
        registroSalidaWs.setTipoTransporte(registreSortida.getTransportTipusCodi());
        registroSalidaWs.setObservaciones(registreSortida.getObservacions());
        registroSalidaWs.setExpone(registreSortida.getExposa());
        registroSalidaWs.setSolicita(registreSortida.getSolicita());

        // Interesados
        for (RegistreAssentamentInteressat inter: registreSortida.getInteressats()) {
        	
        	InteresadoWs interesadoWs = new InteresadoWs();

            DatosInteresadoWs interesado = new DatosInteresadoWs();
            interesado.setTipoInteresado(inter.getTipus() != null ? Long.valueOf(inter.getTipus().getValor()) : null);
            interesado.setTipoDocumentoIdentificacion(inter.getDocumentTipus() != null ? inter.getDocumentTipus().getValor() : null);
            interesado.setDocumento(inter.getDocumentNum());
            interesado.setEmail(inter.getEmail());
            interesado.setNombre(inter.getNom());
            interesado.setApellido1(inter.getLlinatge1());
            interesado.setApellido2(inter.getLlinatge2());
            interesado.setRazonSocial(inter.getRaoSocial());
            interesado.setPais(inter.getPais() != null ? new Long(inter.getPais()) : null);
            interesado.setProvincia(inter.getProvincia() != null ? new Long(inter.getProvincia()) : null);
            interesado.setDireccion(inter.getAdresa());
            interesado.setCp(inter.getCodiPostal());
            interesado.setLocalidad(inter.getMunicipi() != null ? new Long(inter.getMunicipi()) : null);
            interesado.setTelefono(inter.getTelefon());
            interesadoWs.setInteresado(interesado);

            if (inter.getRepresentant() != null) {
            	RegistreAssentamentInteressat repre = inter.getRepresentant();
	            DatosInteresadoWs representante = new DatosInteresadoWs();
	            representante.setTipoInteresado(repre.getTipus() != null ? Long.valueOf(repre.getTipus().getValor()) : null);
	            representante.setTipoDocumentoIdentificacion(repre.getDocumentTipus() != null ? repre.getDocumentTipus().getValor() : null);
	            representante.setDocumento(repre.getDocumentNum());
	            representante.setEmail(repre.getEmail());
	            representante.setNombre(repre.getNom());
	            representante.setApellido1(repre.getLlinatge1());
	            representante.setApellido2(repre.getLlinatge2());
	            representante.setRazonSocial(repre.getRaoSocial());
	            representante.setPais(repre.getPais() != null ? new Long(repre.getPais()) : null);
	            representante.setProvincia(repre.getProvincia() != null ? new Long(repre.getProvincia()) : null);
	            representante.setDireccion(repre.getAdresa());
	            representante.setCp(repre.getCodiPostal());
	            representante.setLocalidad(repre.getMunicipi() != null ? new Long(repre.getMunicipi()) : null);
	            representante.setTelefono(repre.getTelefon());
	            interesadoWs.setRepresentante(representante);
            }
            
            registroSalidaWs.getInteresados().add(interesadoWs);
        }
        
        for (DocumentRegistre document: registreSortida.getDocuments()) {
        	AnexoWs anexoWs = new AnexoWs();
        	
        	anexoWs.setTitulo(document.getNom());
            anexoWs.setTipoDocumental(document.getTipusDocumental());
            anexoWs.setTipoDocumento(document.getTipusDocument());
            anexoWs.setOrigenCiudadanoAdmin(document.getOrigen());
            anexoWs.setObservaciones(document.getObservacions());
            anexoWs.setModoFirma(document.getModeFirma());
            anexoWs.setValidezDocumento(document.getValidesa());

            anexoWs.setFicheroAnexado(document.getArxiuContingut());
            anexoWs.setNombreFicheroAnexado(document.getArxiuNom());
            anexoWs.setFechaCaptura(new Timestamp(document.getData().getTime()));
            
            InputStream is = new BufferedInputStream(new ByteArrayInputStream(document.getArxiuContingut()));
            try {
				String mimeType = URLConnection.guessContentTypeFromStream(is);
				anexoWs.setTipoMIMEFicheroAnexado(mimeType);
			} catch (IOException e) {
				throw new RegistreException("Error IOException: " + e);
			}
        	
        	registroSalidaWs.getAnexos().add(anexoWs);
        }
       
        RespostaAnotacioRegistre resposta = null;
        try {
            AsientoRegistralWs identificadorWs = asientoRegistralApi.crearAsientoRegistral(
            		null,	//idSesion 
            		registreSortida.getEntitatCodi(), //entidad, codi Dir3 de l'entitat on es vol fer l'assentament 
            		registroSalidaWs, 
            		null, 	//tipoOperacion 1 notificació, 2 comunicació
            		false,	// justificant
            		false 	// Distribuir
            		);
            resposta = new RespostaAnotacioRegistre();
            resposta.setData(identificadorWs.getFechaRegistro());
            resposta.setNumero(String.valueOf(identificadorWs.getNumeroRegistro()));
            if (resposta.getNumero() == null || "0".equals(resposta.getNumero()))
            	throw new Exception("En la resposta del registre no s'ha assignat un número de registre vàlid: " + resposta.getNumero());
            resposta.setNumeroRegistroFormateado(identificadorWs.getNumeroRegistroFormateado());
            resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
        } catch (WsI18NException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistreException("Error WsI18NException: " + msg);
        } catch (WsValidationException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistreException("Error WsValidationException: " + msg);
        } catch (Exception e) {
        	throw new RegistreException("Error WsValidationException: ", e);
		}
        return resposta;
	}
	
	@Override
	public RespostaConsultaRegistre obtenirRegistreSortida(String numRegistre, String usuariCodi, String entitatCodi)
			throws RegistreException {

		RespostaConsultaRegistre resposta = new RespostaConsultaRegistre();

		try {
			
			AsientoRegistralWs registroSalida = asientoRegistralApi.obtenerAsientoRegistral(entitatCodi, numRegistre, 2L, false);
            
            resposta.setRegistreNumero(registroSalida.getNumeroRegistroFormateado());
            resposta.setRegistreData(registroSalida.getFechaRegistro());
            resposta.setEntitatCodi(registroSalida.getEntidadCodigo());
            resposta.setEntitatDenominacio(registroSalida.getEntidadDenominacion());
            resposta.setOficinaCodi("");	//Desapareix el codi d'oficina per Regweb3
            resposta.setOficinaDenominacio(""); 	//Desapareix el codi d'oficina per Regweb3
            // TODO GUARDAR CADENA BUIDA SEMBLA QUE NO ÉS CORRECTE. Helium 3.2
		} catch (WsI18NException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistreException("Error WsI18NException: " + msg);
        } catch (WsValidationException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistreException("Error WsValidationException: " + msg);
        } catch (Exception e) {
        	log.error("Error al obtenir l'anotació de registre de sortida", e);
			throw new RegistreException("Error al obtenir l'anotació de registre de sortida", e);
		}
		
		return resposta;
	}
	
	@Override
	public void anularRegistreSortida(String registreNumero, String usuari, String entitat, boolean anular) throws RegistreException {
		
		throw new RegistreException("Regweb3 no permet anul·lar a través del WS");		
	}
	
	@Override
	public RespostaAnotacioRegistre registrarEntrada(RegistreAssentament registreEntrada, String aplicacioNom, String aplicacioVersio) 
			throws RegistreException {
		
		// 		Camps del registre
		//		------------------------------------------------------------------------------------------------------------------------------------------
		//		Camp								| 	Obligatori	|	Descripció
		//		------------------------------------------------------------------------------------------------------------------------------------------
		//		Dades de l'ENVIAMENT
		//		------------------------------------------------------------------------------------------------------------------------------------------
		//		String desti;  						|	Si			|	Codi DIR3 de l'òrgan destinatari de l'assentament.
		//		String oficina;						|	Si			|	Codi DIR3 de l'entitat registral on es registre l'assentament
		//		String libro;						|	Si			|	Codi del llibre al qual fer l'assentament.
		//		Long docFisica;						|	Si			|	Indica si a l'assentament l'acompanya documentació física o tota la documentació és electrònica. 
		//											|				|	Codi definit a SICRES3:
		//											|				|		'01' = Acompanya documentació física requerida.
		//											|				|		'02' = Acompanya documentació física complementària.
		//											|	Si			|		'03' = No acompanya documentació física ni altres suports.
		//		String idioma;						|				|	Codi de l'idioma en què es fa l'assentament.
		//											|				|	Els codis disponibles són:
		//											|				|		'ca' = Català.
		//											|				|		'es' = Castellà.
		//											|				|		'gl' = Gallec.
		//											|				|		'eu' = Basc.
		//											|				|		'en' = Anglès.
		//		String aplicacion;					|	No			|	Codi de l'aplicació que fa l'assentament
		//		String version;						|	No			|	Versió de l'aplicació que fa l'assentament
		//		String codigoUsuario;				|	Si			|	Nom de l'usuari que fa l'assentament. Si és un registre electrònic serà el nom de l'aplicació
		//		String contactoUsuario;				|	No			|	Contacte de l'usuari d'origen. Si és un registre electrònic serà el responsable del tràmit.
		//		String numExpediente;				|	No			|	Referència al nombre d'expedient de l'assentament
		//	    String numTransporte;				|	No			|	Número del transport d'entrada
		//	    String tipoTransporte;				|	No			|	Forma d'arribada del registre d'entrada. 
		//											|				|	Codi definit a SICRES3:
		//											|				|		'01' = Servei de missatgers
		//											|				|		'02' = Correu postal
		//											|				|		'03' = Correu postal certificat
		//											|				|		'04' = Burofax
		//											|				|		'05' = En ma
		//											|				|		'06' = Fax
		//											|				|		'07' = Altres
		//											|				|		En cas de ser un registre electrònic aquest camp prendrà el valor Altres (07).
		//		String extracto;					|	Si			|	Extracte o resum de l'assentament
		//		String tipoAsunto;					|	Si			|	Codi del tipus assumpte de l'expedient
		//	    String codigoAsunto;				|	No			|	Codi de l'assumpte en destí. Aquest camp contindrà el codi del procediment/tràmit a què pertany l'assentament. Està tipificat a dins REGWEB.
		//	    String refExterna;					|	No			|	Referència externa a qualque element de l'assentament (matrícula de cotxe, rebut,...)
		//	    String observaciones;				|	No			|	Observacions de l'assentament.
		//											|				|
	    //		String expone;						|	No			|	Exposició de fets. Només s'ha d'emprar quan l'assentament és una sol·licitud genèrica electrònica.
		//	    String solicita;					|	No			|	Objecte de la sol·licitud. Només s'ha d'emprar quan l'assentament és una sol·licitud genèrica electrònica.
		//											|				|
		//		List<InteresadoWs> interesados;		|	Si			|	Dades de l'interessat en l'assentament. Pot haver-n'hi més d'1.
		//		List<AnexoWs> anexos;				|	No			|	Fitxers annexes a l'assentament
		//											|				|		
		//											|				|
		//		------------------------------------------------------------------------------------------------------------------------------------------
		//		Dades de la RESPOSTA
		//		------------------------------------------------------------------------------------------------------------------------------------------
		//	    Timestamp fecha;					|				|	Data del assentament
		//	    Integer numero;						|				|	Número de l'assentament
		//	    String numeroRegistroFormateado;	|				|	Número de l'assentament formateat
		
		var registroEntradaWs = new RegistroEntradaWs();

		registroEntradaWs.setDestino(registreEntrada.getOrgan());
		registroEntradaWs.setOficina(registreEntrada.getOficinaCodi());
		registroEntradaWs.setLibro(registreEntrada.getLlibreCodi());
		registroEntradaWs.setDocFisica(registreEntrada.getDocumentacioFisicaCodi() != null ? new Long(registreEntrada.getDocumentacioFisicaCodi()) : (long)3);
		registroEntradaWs.setIdioma(registreEntrada.getIdiomaCodi() == null? "ca" : registreEntrada.getIdiomaCodi().toLowerCase());
		registroEntradaWs.setAplicacion(aplicacioNom);
		registroEntradaWs.setVersion(aplicacioVersio);
		registroEntradaWs.setCodigoUsuario(registreEntrada.getUsuariCodi());
		registroEntradaWs.setContactoUsuario(registreEntrada.getUsuariContacte());
		registroEntradaWs.setNumExpediente(registreEntrada.getExpedientNumero());
		registroEntradaWs.setNumTransporte(registreEntrada.getTransportNumero());
		registroEntradaWs.setTipoTransporte(registreEntrada.getTransportNumero());	// En cas de ser un registre electrònic aquest camp prendrà el valor Altres ("07").
		registroEntradaWs.setExtracto(registreEntrada.getExtracte());
		registroEntradaWs.setTipoAsunto(registreEntrada.getAssumpteTipusCodi());
		registroEntradaWs.setCodigoAsunto(registreEntrada.getAssumpteCodi());
		registroEntradaWs.setRefExterna(registreEntrada.getReferencia());
		registroEntradaWs.setObservaciones(registreEntrada.getObservacions());
		
        registroEntradaWs.setExpone(registreEntrada.getExposa());		// Només s'ha d'emprar quan l'assentament és una sol·licitud genèrica electrònica.
        registroEntradaWs.setSolicita(registreEntrada.getSolicita());	// Només s'ha d'emprar quan l'assentament és una sol·licitud genèrica electrònica.

        // Interesados
        for (RegistreAssentamentInteressat inter: registreEntrada.getInteressats()) {
        	InteresadoWs interesadoWs = new InteresadoWs();

            DatosInteresadoWs interesado = new DatosInteresadoWs();
            interesado.setTipoInteresado(Long.valueOf(inter.getTipus().getValor()));
            interesado.setTipoDocumentoIdentificacion(inter.getDocumentTipus().getValor());
            interesado.setDocumento(inter.getDocumentNum());
            interesado.setEmail(inter.getEmail());
            interesado.setNombre(inter.getNom());
            interesado.setApellido1(inter.getLlinatge1());
            interesado.setApellido2(inter.getLlinatge2());
            interesado.setRazonSocial(inter.getRaoSocial());
            interesado.setPais(inter.getPais() != null ? new Long(inter.getPais()) : null);
            interesado.setProvincia(inter.getProvincia() != null ? new Long(inter.getProvincia()) : null);
            interesado.setDireccion(inter.getAdresa());
            interesado.setCp(inter.getCodiPostal());
            interesado.setLocalidad(inter.getMunicipi() != null ? new Long(inter.getMunicipi()) : null);
            interesado.setTelefono(inter.getTelefon());
            interesadoWs.setInteresado(interesado);

            if (inter.getRepresentant() != null) {
            	RegistreAssentamentInteressat repre = inter.getRepresentant();
	            DatosInteresadoWs representante = new DatosInteresadoWs();
	            representante.setTipoInteresado(Long.valueOf(repre.getTipus().getValor()));
	            representante.setTipoDocumentoIdentificacion(repre.getDocumentTipus().getValor());
	            representante.setDocumento(repre.getDocumentNum());
	            representante.setEmail(repre.getEmail());
	            representante.setNombre(repre.getNom());
	            representante.setApellido1(repre.getLlinatge1());
	            representante.setApellido2(repre.getLlinatge2());
	            representante.setPais(repre.getPais() != null ? new Long(repre.getPais()) : null);
	            representante.setProvincia(repre.getProvincia() != null ? new Long(repre.getProvincia()) : null);
	            representante.setDireccion(repre.getAdresa());
	            representante.setCp(repre.getCodiPostal());
	            representante.setLocalidad(repre.getMunicipi() != null ? new Long(repre.getMunicipi()) : null);
	            representante.setTelefono(repre.getTelefon());
	            interesadoWs.setRepresentante(representante);
            }
            
            registroEntradaWs.getInteresados().add(interesadoWs);
        }
        
        for (DocumentRegistre document: registreEntrada.getDocuments()) {
        	AnexoWs anexoWs = new AnexoWs();
        	
        	anexoWs.setTitulo(document.getNom());
            anexoWs.setTipoDocumental(document.getTipusDocumental());
            anexoWs.setTipoDocumento(document.getTipusDocument());
            anexoWs.setOrigenCiudadanoAdmin(document.getOrigen());
            anexoWs.setObservaciones(document.getObservacions());
            anexoWs.setModoFirma(document.getModeFirma());

            anexoWs.setFicheroAnexado(document.getArxiuContingut());
            anexoWs.setNombreFicheroAnexado(document.getArxiuNom());
            anexoWs.setFechaCaptura(new Timestamp(document.getData().getTime()));
            
            InputStream is = new BufferedInputStream(new ByteArrayInputStream(document.getArxiuContingut()));
            try {
				String mimeType = URLConnection.guessContentTypeFromStream(is);
				anexoWs.setTipoMIMEFicheroAnexado(mimeType);
			} catch (IOException e) {
				throw new RegistreException("Error IOException: " + e);
			}
        	
            registroEntradaWs.getAnexos().add(anexoWs);
        }

       
        RespostaAnotacioRegistre resposta = null;
        try {
            var identificadorWs = registroEntradaApi.altaRegistroEntrada(registroEntradaWs);
            resposta = new RespostaAnotacioRegistre();
            resposta.setData(identificadorWs.getFecha());
            resposta.setNumero(identificadorWs.getNumero().toString());
            resposta.setNumeroRegistroFormateado(identificadorWs.getNumeroRegistroFormateado());
            resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
        } catch (WsI18NException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistreException("Error WsI18NException: " + msg);
        } catch (WsValidationException e) {
            String msg = WsClientUtils.toString(e);
            throw new RegistreException("Error WsValidationException: " + msg);
        } catch (Exception e) {
        	throw new RegistreException("Error WsValidationException: ", e);
		}
        return resposta;
	}
	

    /**
     *     Idioma de l’assentament.
     *         1: Català
     *         2: Castellà
     *         3: Galleg
     *         4: Euskera
     *         5: Anglès
     *         6: Altres
     * @param idiomaCodi
     * @return
     */
    private Long getIdioma(String idiomaCodi) {
    	Long idioma = 1L;
    	if (idiomaCodi != null && !idiomaCodi.isEmpty()) {
	    	if ("ca".equals(idiomaCodi)) {
	    		idioma = 1L;
	    	} else if ("es".equals(idiomaCodi)) {
	    		idioma = 2L;
	    	} else if ("gl".equals(idiomaCodi)) {
	    		idioma = 3L;
	    	} else if ("eu".equals(idiomaCodi)) {
	    		idioma = 4L;
	    	} else if ("en".equals(idiomaCodi)) {
	    		idioma = 5L;
	    	} else {
	    		idioma = 6L;
	    	}
    	}
    	return idioma;
    }
}
