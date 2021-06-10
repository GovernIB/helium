package es.caib.helium.integracio.service.registre;

import java.text.SimpleDateFormat;
import java.util.Date;

import es.caib.helium.integracio.domini.registre.RegistreEntrada;
import es.caib.helium.integracio.domini.registre.RespostaAnotacioRegistre;
import es.caib.helium.integracio.excepcions.registre.RegistreException;
import es.caib.regweb.ws.model.ParametrosRegistroEntradaWS;
import es.caib.regweb.ws.services.regwebfacade.RegwebFacade_PortType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class ApiRegWebWs { //implements RegistreApi {

	private RegwebFacade_PortType api;
	private static final String SEPARADOR_ENTITAT = "-";
	private static final String SEPARADOR_NUMERO = "/";
	private String usuarioConexion;
	private String password;
	private String usuariRegistre;

	public RespostaAnotacioRegistre registrarEntrada(RegistreEntrada registreEntrada) throws RegistreException {
		try {
			var ara = new Date();
			var paramsws = new ParametrosRegistroEntradaWS();
			paramsws.setUsuarioConexion(usuarioConexion);
			paramsws.setPassword(password);
			paramsws.setUsuarioRegistro(usuariRegistre);
			paramsws.setDataentrada(new SimpleDateFormat("dd/MM/yyyy").format(ara));
			paramsws.setHora(new SimpleDateFormat("HH:mm").format(ara));
			if (registreEntrada.getDadesOficina() != null) {
				String oficinaCodi = registreEntrada.getDadesOficina().getOficinaCodi();
				if (oficinaCodi != null) {
					int indexBarra = oficinaCodi.indexOf(SEPARADOR_ENTITAT);
					if (indexBarra != -1) {
						paramsws.setOficina(oficinaCodi.substring(0, indexBarra));
						paramsws.setOficinafisica(oficinaCodi.substring(indexBarra + 1));
					}
				}
				if (registreEntrada.getDadesOficina().getOrganCodi() != null)
					paramsws.setDestinatari(
							registreEntrada.getDadesOficina().getOrganCodi());
			}
			if (registreEntrada.getDadesInteressat() != null) {
				String entitatCodi = registreEntrada.getDadesInteressat().getEntitatCodi();
				if (entitatCodi != null) {
					int indexBarra = entitatCodi.indexOf(SEPARADOR_ENTITAT);
					if (indexBarra != -1) {
						paramsws.setEntidad1(entitatCodi.substring(0, indexBarra));
						paramsws.setEntidad2(entitatCodi.substring(indexBarra + 1));
						paramsws.setEntidadCastellano(paramsws.getEntidad1());
					}
				}
				if (registreEntrada.getDadesInteressat().getNomAmbCognoms() != null)
					paramsws.setAltres(
							registreEntrada.getDadesInteressat().getNomAmbCognoms());
				if (registreEntrada.getDadesInteressat().getMunicipiCodi() != null)
					paramsws.setBalears(
							registreEntrada.getDadesInteressat().getMunicipiCodi());
				if (registreEntrada.getDadesInteressat().getMunicipiNom() != null)
					paramsws.setFora(
							registreEntrada.getDadesInteressat().getMunicipiNom());
			}
			if (registreEntrada.getDadesAssumpte() != null) {
				if (registreEntrada.getDadesAssumpte().getTipus() != null) {
					paramsws.setTipo(registreEntrada.getDadesAssumpte().getTipus());
				}
				if (registreEntrada.getDadesAssumpte().getRegistreNumero() != null) {
					paramsws.setSalida1(registreEntrada.getDadesAssumpte().getRegistreNumero());
					paramsws.setSalida2(registreEntrada.getDadesAssumpte().getRegistreAny());
				}
				if (registreEntrada.getDadesAssumpte().getIdiomaCodi() != null) {
					paramsws.setIdioex(convertirIdioma(registreEntrada.getDadesAssumpte().getIdiomaCodi()));
				}
				if (registreEntrada.getDadesAssumpte().getAssumpte() != null) {
					paramsws.setComentario(registreEntrada.getDadesAssumpte().getAssumpte());
				}
			}
			if (registreEntrada.getDocuments() != null && registreEntrada.getDocuments().size() > 0) {
				if (registreEntrada.getDocuments().size() == 1) {
					var document = registreEntrada.getDocuments().get(0);
					paramsws.setData(new SimpleDateFormat("dd/MM/yyyy").format(document.getData()));
					paramsws.setIdioma(convertirIdioma(document.getIdiomaCodi()));
				} else {
					throw new RegistreException("Nomes es pot registrar un document alhora");
				}
			} else {
				throw new RegistreException("S'ha d'especificar algun document per registrar");
			}
			ParametrosRegistroEntradaWS respostaValidacio = api.validarEntrada(paramsws);
			if (respostaValidacio.getValidado()) {
				ParametrosRegistroEntradaWS respostaGrabacio = api.grabarEntrada(paramsws);
				RespostaAnotacioRegistre resposta = new RespostaAnotacioRegistre();
				if (respostaGrabacio.getRegistroGrabado()) {
					resposta.setErrorCodi(RespostaAnotacioRegistre.ERROR_CODI_OK);
					resposta.setNumero(respostaGrabacio.getNumeroEntrada() + SEPARADOR_NUMERO + respostaGrabacio.getAnoEntrada());
					resposta.setData(ara);
					return resposta;
				} else {
					throw new RegistreException("No s'ha pogut guardar l'entrada");
				}
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("Errors de validació:\n");
				if (respostaValidacio.getErrores() != null) {
					for (var error: respostaValidacio.getErrores().getErrores()) {
						sb.append(" | [" + error.getCodigo() + "] " + error.getDescripcion());	
					}
				}
				throw new RegistreException("S'han produit errors de validació de l'entrada: " + sb.toString());
			}
		} catch (Exception ex) {
			log.error("Error al registrar l'entrada", ex);
			throw new RegistreException("Error al registrar l'entrada", ex);
		}
	}
	
	private String convertirIdioma(String iso6391) {
		if ("es".equalsIgnoreCase(iso6391)) {
			return "1";
		} else if ("ca".equalsIgnoreCase(iso6391)) {
			return "2";
		} else if ("eu".equalsIgnoreCase(iso6391)) {
			return "4";
		} else if ("gl".equalsIgnoreCase(iso6391)) {
			return "5";
		} else if ("as".equalsIgnoreCase(iso6391)) {
			return "6";
		} else if ("de".equalsIgnoreCase(iso6391)) {
			return "C";
		} else if ("en".equalsIgnoreCase(iso6391)) {
			return "A";
		} else if ("fr".equalsIgnoreCase(iso6391)) {
			return "B";
		} else if ("it".equalsIgnoreCase(iso6391)) {
			return "E";
		} else if ("pt".equalsIgnoreCase(iso6391)) {
			return "F";
		}
		return "2";
	}
}
