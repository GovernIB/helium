<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Iniciar expedient: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="Nou expedient"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	if ("cancel" == submitAction)
		return true;
	if (${empty tascaInicial})
		return confirm("Estau segur que voleu iniciar un nou expedient?");
	else
		return true;
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
					<c:param name="label">Número</c:param>
				</c:import>
			</c:if>
			<c:if test="${expedientTipus.teTitol and expedientTipus.demanaTitol}">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="titol"/>
					<c:param name="type" value="textarea"/>
					<c:param name="required" value="true"/>
					<c:param name="label">Títol</c:param>
				</c:import>
			</c:if>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles">Iniciar,Cancel·lar</c:param>
		</c:import>
	</form:form>

	<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>

</body>
</html>
