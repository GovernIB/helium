<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><c:choose><c:when test="${not empty command.expedientTipusId}"><fmt:message key='comuns.tipus_exp' />: ${expedientTipus.nom}</c:when><c:otherwise><fmt:message key='defproc.deploy.despl_arxiu' /></c:otherwise></c:choose></title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
//<![CDATA[
function actualitzaExps() {
	var tipImp = $("#tipus0").val();
	var tipExp = $("#expedientTipusId0").val();
	if (tipImp == "JBPM") { //&& tipExp != "") {
		$(".actualitza").show();
		$("#actualitzarProcessosActius0").prop('disabled', $("#accioJbpm0").val() != "JBMP_DESPLEGAR");
	} else {
		$(".actualitza").hide();
	}
}

$(document).ready(function(){
	actualitzaExps();
});
	// ]]>
</script>
</head>
<body>

	<c:if test="${not empty command.expedientTipusId}">
		<c:import url="../common/tabsExpedientTipus.jsp">
			<c:param name="tabActiu" value="defprocs"/>
		</c:import>
	</c:if>

	<form:form action="deploy.html" cssClass="uniForm" enctype="multipart/form-data">
		<div class="inlineLabels">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="tipus"/>
				<c:param name="type" value="select"/>
				<c:param name="required" value="true"/>
				<c:param name="items" value="desplegamentTipus"/>
				<c:param name="itemLabel" value="valor"/>
				<c:param name="itemValue" value="codi"/>
				<c:param name="label"><fmt:message key='defproc.deploy.tipus_despl' /></c:param>
				<c:param name="onchange" value="actualitzaExps()"/>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="expedientTipusId"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="expedientTipus"/>
				<c:param name="itemLabel" value="nom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key='defproc.deploy.item_buit' /> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key='comuns.tipus_exp' /></c:param>
				<c:param name="onchange" value="actualitzaExps()"/>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="arxiu"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="file"/>
				<c:param name="label"><fmt:message key='defproc.deploy.arxiu_exp' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="etiqueta"/>
				<c:param name="label"><fmt:message key='comuns.etiqueta' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="accioJbpm"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="accionsJbmp"/>
				<c:param name="itemLabel" value="valor"/>
				<c:param name="itemValue" value="codi"/>
				<c:param name="label"><fmt:message key='desplegament.jbpm.accio' /></c:param>
				<c:param name="classHolder" value="actualitza"/>
				<c:param name="onchange" value="actualitzaExps()"/>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="actualitzarProcessosActius"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.deploy.expedients.actualitzar' /></c:param>
				<c:param name="classHolder" value="actualitza"/>
				<c:param name="disabled" value="${command.accioJbpm eq 'JBMP_DESPLEGAR' ? '' : 'disabled'}"/>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">submit,cancel</c:param>
				<c:param name="titles"><fmt:message key='defproc.deploy.desplegar' />,<fmt:message key='comuns.cancelar' /></c:param>
			</c:import>
		</div>
	</form:form>

	<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>

</body>
</html>