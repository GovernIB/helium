<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Tipus d'expedient: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="Disseny"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function mostrarOcultar(objid) {
	var obj = document.getElementById(objid);
	if (obj.style.display=="none") {
		obj.style.display = "block";
	} else {
		obj.style.display = "none";
	}
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="formext"/>
	</c:import>

	<form:form action="formext.html" cssClass="uniForm">
		<div class="inlineLabels">
			<form:hidden path="expedientTipusId"/>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="actiu"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Activar</c:param>
				<c:param name="onclick">mostrarOcultar('infoIntegracio')</c:param>
			</c:import>
			<div id="infoIntegracio"<c:if test="${not command.actiu}"> style="display:none"</c:if>>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="url"/>
					<c:param name="required" value="${true}"/>
					<c:param name="label">Url del servei d'integració amb formularis externs'</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="usuari"/>
					<c:param name="label">Usuari del servei</c:param>
					<c:param name="comment">Si no necessita autenticació es pot deixar en blanc</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="contrasenya"/>
					<c:param name="label">Contrasenya del servei</c:param>
					<c:param name="comment">Si no necessita autenticació es pot deixar en blanc</c:param>
				</c:import>
			</div>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit</c:param>
			<c:param name="titles">Guardar</c:param>
		</c:import>
	</form:form>

</body>
</html>
