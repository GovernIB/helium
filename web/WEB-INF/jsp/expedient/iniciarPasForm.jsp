<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
	return confirm("Estau segur que voleu iniciar un nou expedient?");
}
// ]]>
</script>
</head>
<body>

	<h3 class="titol-tab titol-dades-tasca">${tasca.nom}</h3>

	<form:form action="iniciarPasForm.html" cssClass="uniForm" onsubmit="return confirmar(event)">
		<div class="inlineLabels">
			<form:hidden path="entornId"/>
			<form:hidden path="expedientTipusId"/>
			<c:if test="${not empty param.definicioProcesId}"><input type="hidden" name="definicioProcesId" value="${param.definicioProcesId}"/></c:if>
			<c:if test="${not empty tasca.camps}">
				<c:forEach var="camp" items="${tasca.camps}">
					<c:set var="campTascaActual" value="${camp}" scope="request"/>
					<c:import url="../common/campTasca.jsp"/>
				</c:forEach>
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
