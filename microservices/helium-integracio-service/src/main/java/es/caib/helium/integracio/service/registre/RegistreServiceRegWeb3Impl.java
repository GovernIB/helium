package es.caib.helium.integracio.service.registre;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import javax.xml.ws.BindingProvider;

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
import es.caib.regweb3.ws.api.v3.RegWebAsientoRegistralWsService;
import es.caib.regweb3.ws.api.v3.WsI18NException;
import es.caib.regweb3.ws.api.v3.WsValidationException;
import es.caib.regweb3.ws.api.v3.utils.WsClientUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RegistreServiceRegWeb3Impl implements RegistreService {
	
	private RegWebAsientoRegistralWs api;

	
	@Override
	public Date getJustificantDataRecepcio(String numeroRegistre) throws RegistreException {
		
		// TODO No forma part d'aquesta implementacio 
		return null;
	}
	
	public RespostaAnotacioRegistre registrarSortida(
			RegistreAssentament registreSortida,
			String aplicacioNom,
			String aplicacioVersio) throws RegistreException {

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
            AsientoRegistralWs identificadorWs = api.crearAsientoRegistral(
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
			
			AsientoRegistralWs registroSalida = api.obtenerAsientoRegistral(entitatCodi, numRegistre, 2L, false);
            
            resposta.setRegistreNumero(registroSalida.getNumeroRegistroFormateado());
            resposta.setRegistreData(registroSalida.getFechaRegistro());
            resposta.setEntitatCodi(registroSalida.getEntidadCodigo());
            resposta.setEntitatDenominacio(registroSalida.getEntidadDenominacion());
            resposta.setOficinaCodi("");	//Desapareix el codi d'oficina per Regweb3
            resposta.setOficinaDenominacio(""); 	//Desapareix el codi d'oficina per Regweb3
            // TODO GUARDAR CADENA BUIDA SEMBLA QUE NO ÉS CORRECTE
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

	public void crearClient(String host, String registroEntrada, String user, String password) throws Exception {

		final String endpoint = host + registroEntrada;
		final URL wsdl = new URL(endpoint + "?wsdl");
		RegWebAsientoRegistralWsService service = new RegWebAsientoRegistralWsService(wsdl);

		api = service.getRegWebAsientoRegistralWs();

		configAddressUserPassword(user, password, endpoint, api);
	}

	private static void configAddressUserPassword(String usr, String pwd, String endpoint, Object api) {

		Map<String, Object> reqContext = ((BindingProvider) api).getRequestContext();
		reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		reqContext.put(BindingProvider.USERNAME_PROPERTY, usr);
		reqContext.put(BindingProvider.PASSWORD_PROPERTY, pwd);
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
