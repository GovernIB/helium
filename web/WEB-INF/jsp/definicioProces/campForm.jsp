<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Definició de procés: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="Disseny">
	<c:import url="../common/formIncludes.jsp"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function disable(blocid) {
	$("#" + blocid).find("input,select,textarea").attr("disabled", "disabled").attr("value", "");
}
function enable(blocid) {
	$("#" + blocid).find("input,select,textarea").removeAttr("disabled");
}
function canviTipus(input) {
	disable("camps_accio");
	disable("camps_consulta");
	if (input.value == "SELECCIO" || input.value == "SUGGEST") {
		enable("camps_consulta");
	} else if (input.value == "ACCIO") {
		enable("camps_accio");
	}
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="camps"/>
	</c:import>

	<form:form action="campForm.html" cssClass="uniForm">
		<div class="inlineLabels col first">
			<h3>Dades del camp</h3>
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<form:hidden path="definicioProces"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Codi</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="tipus"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="tipusCamp"/>
				<c:param name="itemBuit" value="<< Seleccioni un tipus >>"/>
				<c:param name="label">Tipus</c:param>
				<c:param name="onchange">canviTipus(this)</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="etiqueta"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Etiqueta</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="observacions"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label">Observacions</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="agrupacio"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="agrupacions"/>
				<c:param name="itemLabel" value="nom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit" value="<< Seleccioni una agrupació >>"/>
				<c:param name="label">Agrupació de variables</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="multiple"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Multiple</c:param>
			</c:import>
		</div>
		<div class="inlineLabels col last">
			<h3>Dades de l'acció</h3>
			<div id="camps_accio">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="jbpmAction"/>
					<c:param name="type" value="select"/>
					<c:param name="items" value="accionsJbpm"/>
					<c:param name="itemBuit" value="<< Seleccioni un handler >>"/>
					<c:param name="label">Handler</c:param>
				</c:import>
			</div>
			<h3>Dades per la consulta</h3>
			<div id="camps_consulta">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="enumeracio"/>
					<c:param name="type" value="select"/>
					<c:param name="items" value="enumeracions"/>
					<c:param name="itemLabel" value="nom"/>
					<c:param name="itemValue" value="id"/>
					<c:param name="itemBuit" value="<< Seleccioni una enumeració >>"/>
					<c:param name="label">Enumeracio</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="domini"/>
					<c:param name="type" value="select"/>
					<c:param name="items" value="dominis"/>
					<c:param name="itemLabel" value="nom"/>
					<c:param name="itemValue" value="id"/>
					<c:param name="itemBuit" value="<< Seleccioni un domini >>"/>
					<c:param name="label">Domini</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="dominiId"/>
					<c:param name="label">Id del domini</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="dominiParams"/>
					<c:param name="type" value="textarea"/>
					<c:param name="label">Paràmetres pel domini</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="dominiCampText"/>
					<c:param name="label">Camp amb el text</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="dominiCampValor"/>
					<c:param name="label">Camp amb el valor</c:param>
				</c:import>
			</div>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}">Crear,Cancel·lar</c:when><c:otherwise>Modificar,Cancel·lar</c:otherwise></c:choose></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>

<script type="text/javascript">$(document).ready(canviTipus(document.getElementById("tipus0")));</script>
</body>
</html>
