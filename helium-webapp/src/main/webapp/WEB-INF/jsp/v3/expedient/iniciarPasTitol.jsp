<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><spring:message code='expedient.iniciar.iniciar_expedient' />: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="Nou expedient"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	if ($('#nomesRefrescar').val() == 'true') {
		$('button.submitButton').remove();
		return true;
	}
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	if ("cancel" == submitAction)
		return true;
	return confirm("<spring:message code='expedient.iniciar.confirm_iniciar' />");
}
function canviAny(element) {
	//alert(element.value);
	$('#nomesRefrescar').val('true');
	$('#command').submit();
}
// ]]>
</script>
</head>
<body>

	<form:form action="iniciarPasTitol.html" cssClass="uniForm" onsubmit="return confirmar(event)">
		<div class="inlineLabels">
			<c:if test="${not empty param.definicioProcesId}"><input type="hidden" name="definicioProcesId" value="${param.definicioProcesId}"/></c:if>
			<form:hidden path="expedientTipusId"/>
			<c:if test="${expedientTipus.teNumero and expedientTipus.demanaNumero}">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="numero"/>
					<c:param name="required" value="true"/>
					<c:param name="label"><spring:message code='expedient.consulta.numero' /></c:param>
				</c:import>
			</c:if>
			<c:if test="${expedientTipus.teTitol and expedientTipus.demanaTitol}">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="titol"/>
					<c:param name="type" value="textarea"/>
					<c:param name="required" value="true"/>
					<c:param name="label"><spring:message code='expedient.consulta.titol' /></c:param>
				</c:import>
			</c:if>
			<c:if test="${expedientTipus.seleccionarAny}">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="any"/>
					<c:param name="required" value="true"/>
					<c:param name="type" value="select"/>
					<c:param name="items" value="anysSeleccionables"/>
					<c:param name="label"><spring:message code="expedient.iniciar.canvi_any"/></c:param>
					<c:param name="onchange">canviAny(this)</c:param>
				</c:import>
				<input type="hidden" id="nomesRefrescar" name="nomesRefrescar"/>
			</c:if>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><spring:message code='comuns.iniciar' />,<spring:message code='comuns.cancelar' /></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><spring:message code='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<spring:message code='comuns.camp_oblig' />" title="<spring:message code='comuns.camp_oblig' />" border="0"/> <spring:message code='comuns.son_oblig' /></p>

</body>
</html>
