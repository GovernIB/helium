package es.caib.helium.bpmn.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compatibilitat amb els antics paràmetres {@code varXXX} dels handlers.
 *
 * <p>Abans, cada paràmetre {@code xxx} d'un handler predefinit tenia un paràmetre germà {@code varXxx} amb el codi
 * d'una dada de l'expedient: si la dada tenia valor, s'utilitzava en lloc de {@code xxx}. Ara les expressions
 * dels paràmetres llegeixen directament les dades de l'expedient, així que {@code varXxx = codi} és equivalent a
 * {@code xxx = ${codi}} i els {@code varXxx} s'han eliminat dels handlers.</p>
 *
 * <p>Aquesta classe tradueix els paràmetres antics de les definicions ja desplegades: un {@code varXxx} és obsolet
 * si el handler no el té i sí que té el paràmetre {@code xxx}. Els {@code varXxx} que el handler continua tenint
 * (p. ex. els de sortida, que indiquen on desar un resultat) no es toquen.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public final class ParametresVar {

	private static final Pattern NOM_VAR = Pattern.compile("^var([A-Za-z].*)$");
	private static final Pattern IDENTIFICADOR = Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*$");
	private static final Pattern EXPRESSIO_DADA = Pattern.compile("^\\$\\{\\s*([A-Za-z_][A-Za-z0-9_]*)\\s*\\}$");

	private ParametresVar() {}

	/**
	 * Retorna el paràmetre que substitueix l'antic {@code varXxx} indicat ({@code xxx}), o null si {@code nom} no
	 * és un paràmetre {@code varXxx} obsolet del handler.
	 */
	public static String substitut(Class<?> handlerClass, String nom) {
		if (handlerClass == null || nom == null)
			return null;
		Matcher m = NOM_VAR.matcher(nom);
		if (!m.matches() || HandlerParametres.teParametre(handlerClass, nom))
			return null;
		String resta = m.group(1);
		String substitut = Character.toLowerCase(resta.charAt(0)) + resta.substring(1);
		return HandlerParametres.teParametre(handlerClass, substitut) ? substitut : null;
	}

	/** Expressió que llegeix la dada de l'expedient indicada ({@code ${codi}}), o null si el codi no és vàlid. */
	public static String expressioDada(String codi) {
		if (codi == null)
			return null;
		String c = codi.trim();
		return IDENTIFICADOR.matcher(c).matches() ? "${" + c + "}" : null;
	}

	/**
	 * Codi de la dada si l'expressió és exactament una referència a una dada ({@code ${codi}}), o null si no ho és.
	 */
	public static String codiDada(String expressio) {
		if (expressio == null)
			return null;
		Matcher m = EXPRESSIO_DADA.matcher(expressio.trim());
		return m.matches() ? m.group(1) : null;
	}

	/**
	 * Tradueix els paràmetres {@code varXxx} obsolets de la definició: {@code varXxx = codi} passa a
	 * {@code xxx = ${codi}}. Si {@code xxx} tenia un valor literal, es conserva com a valor per defecte (s'utilitza
	 * si la dada no té valor), que és el comportament que tenia la parella {@code xxx}/{@code varXxx}.
	 *
	 * @return la mateixa definició si no hi ha res a traduir.
	 */
	public static HandlerDefinicio migrar(HandlerDefinicio definicio, Class<?> handlerClass) {
		if (definicio == null || handlerClass == null || definicio.getParametres().isEmpty())
			return definicio;
		Map<String, HandlerDefinicio.Parametre> parametres = new LinkedHashMap<>();
		for (HandlerDefinicio.Parametre p : definicio.getParametres()) {
			parametres.put(p.getNom(), p);
		}
		boolean canvis = false;
		for (HandlerDefinicio.Parametre p : definicio.getParametres()) {
			String substitut = substitut(handlerClass, p.getNom());
			if (substitut == null)
				continue;
			canvis = true;
			parametres.remove(p.getNom());
			String expressio = p.isExpressio() ? null : expressioDada(p.getValor());
			if (expressio == null)
				// Buit (no s'utilitzava) o un codi calculat amb una expressió, que no es pot traduir
				continue;
			HandlerDefinicio.Parametre anterior = parametres.get(substitut);
			String perDefecte = anterior != null && !anterior.isExpressio() ? buitANull(anterior.getValor()) : null;
			parametres.put(substitut, new HandlerDefinicio.Parametre(substitut, perDefecte, expressio));
		}
		if (!canvis)
			return definicio;
		return new HandlerDefinicio(definicio.getClasse(), definicio.getExpressio(), new ArrayList<>(parametres.values()));
	}

	private static String buitANull(String valor) {
		return valor == null || valor.isEmpty() ? null : valor;
	}

}
