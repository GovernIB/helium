package es.caib.helium.bpmn.handler;

/**
 * Noms de l'extensió de Helium per al BPMN 2.0.
 *
 * <p>Els handlers es declaren dins les service tasks fent servir el mecanisme estàndard
 * d'extensió del BPMN 2.0 ({@code extensionElements}) amb un espai de noms propi de Helium,
 * de manera que el XML no conté atributs ni elements de cap motor de workflow concret.</p>
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public final class HeliumBpmnXml {

	/** Espai de noms de les extensions de Helium. */
	public static final String NAMESPACE = "http://www.caib.es/helium/bpmn";
	/** Prefix recomanat per a l'espai de noms. */
	public static final String PREFIX = "helium";
	/** Valor de l'atribut estàndard {@code implementation} de les service tasks que executen un handler. */
	public static final String IMPLEMENTATION_HANDLER = NAMESPACE + "/handler";

	public static final String ELEMENT_HANDLER = "handler";
	public static final String ELEMENT_PARAMETRE = "parametre";

	public static final String ATTR_CLASSE = "classe";
	public static final String ATTR_NOM = "nom";
	public static final String ATTR_VALOR = "valor";
	/**
	 * Expressió: a {@code helium:parametre} és el valor del paràmetre; a {@code helium:handler} retorna la
	 * instància del handler (alternativa a {@link #ATTR_CLASSE}).
	 */
	public static final String ATTR_EXPRESSIO = "expressio";

	/**
	 * Dades que es passen a un subprocés: dins l'{@code extensionElements} d'una {@code callActivity},
	 * {@code <helium:entrada origen="dadaPare" desti="dadaFill"/>} copia una dada del procés al subprocés abans que
	 * comenci. En lloc d'{@link #ATTR_ORIGEN} pot tenir {@link #ATTR_EXPRESSIO}, que s'avalua al procés. Si no té
	 * {@link #ATTR_DESTI}, la dada del subprocés té el mateix codi.
	 */
	public static final String ELEMENT_ENTRADA = "entrada";
	/**
	 * Dades que retorna un subprocés: {@code <helium:sortida origen="dadaFill" desti="dadaPare"/>} copia una dada del
	 * subprocés al procés quan el subprocés acaba. L'{@link #ATTR_EXPRESSIO} s'avalua al subprocés.
	 */
	public static final String ELEMENT_SORTIDA = "sortida";
	/** Codi de la dada d'origen d'una entrada o sortida. */
	public static final String ATTR_ORIGEN = "origen";
	/** Codi de la dada de destí d'una entrada o sortida. */
	public static final String ATTR_DESTI = "desti";

	/**
	 * Atributs d'extensió de Helium ({@code helium:xxx}) per a les propietats d'execució que el
	 * BPMN 2.0 no defineix. Es posen directament sobre l'element BPMN corresponent
	 * (p. ex. {@code <bpmn:userTask helium:formKey="..." helium:dueDate="...">}) i cada motor de
	 * workflow els tradueix als seus.
	 *
	 * <p>L'assignació de les tasques d'usuari no fa servir atributs d'extensió sinó els elements
	 * estàndard {@code humanPerformer} (responsable) i {@code potentialOwner} (candidats, amb la
	 * sintaxi {@code user(codi)} i {@code group(codi)}).</p>
	 */
	public static final java.util.Set<String> ATRIBUTS_EXTENSIO = java.util.Collections.unmodifiableSet(
		new java.util.LinkedHashSet<>(java.util.Arrays.asList(
			// Procés
			"candidateStarterUsers",
			"candidateStarterGroups",
			// Esdeveniment d'inici i tasques d'usuari
			"initiator",
			"formKey",
			"dueDate",
			"priority",
			"category",
			// Service task i script task
			"expression",
			"resultVariableName",
			"resultVariable",
			"autoStoreVariables",
			// Call activity
			"calledElementType",
			"businessKey",
			"inheritVariables",
			"inheritBusinessKey",
			"fallbackToDefaultTenant",
			// Totes les activitats i fluxos de seqüència
			"async",
			"exclusive",
			"skipExpression",
			// Multi-instància
			"collection",
			"elementVariable")));

	private HeliumBpmnXml() {}

}
