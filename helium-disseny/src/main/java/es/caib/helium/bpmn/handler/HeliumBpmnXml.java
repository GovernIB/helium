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
