<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><fmt:message key='expedient.iniciar.iniciar_expedient' />: ${expedientTipus.nom}</title>
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
		return confirm("<fmt:message key='tasca.form.cancelar' />");
	return confirm("<fmt:message key='expedient.iniciar.confirm_iniciar' />");
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
			<%--c:import url="../common/formElement.jsp">
				<c:param name="property" value="responsableCodi"/>
				<c:param name="type" value="suggest"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Responsable</c:param>
				<c:param name="suggestUrl"><c:url value="/persona/suggest.html"/></c:param>
				<c:param name="suggestText">${responsable.nomSencer}</c:param>
			</c:import--%>
			<c:if test="${expedientTipus.teNumero and expedientTipus.demanaNumero}">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="numero"/>
					<c:param name="required" value="true"/>
					<c:param name="label"><fmt:message key='expedient.consulta.numero' /></c:param>
				</c:import>
			</c:if>
			<c:if test="${expedientTipus.teTitol and expedientTipus.demanaTitol}">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="titol"/>
					<c:param name="type" value="textarea"/>
					<c:param name="required" value="true"/>
					<c:param name="label"><fmt:message key='expedient.consulta.titol' /></c:param>
				</c:import>
			</c:if>
			<c:if test="${expedientTipus.seleccionarAny}">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="any"/>
					<c:param name="required" value="true"/>
					<c:param name="type" value="select"/>
					<c:param name="items" value="anysSeleccionables"/>
					<c:param name="label"><fmt:message key="expedient.iniciar.canvi_any"/></c:param>
					<c:param name="onchange">canviAny(this)</c:param>
				</c:import>
				<input type="hidden" id="nomesRefrescar" name="nomesRefrescar"/>
			</c:if>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><fmt:message key='comuns.iniciar' />,<fmt:message key='comuns.cancelar' /></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>

</body>
</html>
