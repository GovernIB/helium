<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><fmt:message key='common.filtres.expedient' />: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.consultes' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
	function canviTermini(input) {
		var campId = input.id.substring(0, input.id.lastIndexOf("_"));
		var anys = document.getElementById(campId + "_anys").value;
		var mesos = document.getElementById(campId + "_mesos").value;
		var dies = document.getElementById(campId + "_dies").value;
		document.getElementById(campId).value = anys + "/" + mesos + "/" + dies;
	}
	function editarRegistre(campId, campCodi, campEtiqueta, numCamps, index) {
		var amplada = 686;
		var alcada = 64 * numCamps + 80;
		var url = "varRegistre.html?id=${param.id}&registreId=" + campId;
		if (index != null)
			url = url + "&index=" + index;
		$('<iframe id="' + campCodi + '" src="' + url + '" frameborder="0" marginheight="0" marginwidth="0"/>').dialog({
			title: campEtiqueta,
			autoOpen: true,
			modal: true,
			autoResize: true,
			width: parseInt(amplada),
			height: parseInt(alcada)
		}).width(amplada - 30).height(alcada - 30);
		return false;
	}
	function esborrarRegistre(e, campId, index) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		window.location = "varRegistreEsborrar.html?id=${param.id}&registreId=" + campId + "&index=" + index;
		return false;
	}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="dades"/>
	</c:import>

	<h3 class="titol-tab titol-dades-tasca"><fmt:message key='expedient.dada.modificar_dada' /></h3>

	<form:form action="dadaModificar.html" cssClass="uniForm tascaForm zebraForm">
		<div class="inlineLabels">
			<c:if test="${not empty param.id}">
				<form:hidden path="id"/>
			</c:if>
			<c:if test="${not empty param.taskId}">
				<form:hidden path="taskId"/>
			</c:if>
			<form:hidden path="var"/>
			<c:if test="${not empty tasca.camps}">
				<c:forEach var="camp" items="${tasca.camps}">
					<c:set var="campTascaActual" value="${camp}" scope="request"/>
					<c:import url="../common/campTasca.jsp"/>
				</c:forEach>
			</c:if>
		</div>
		<c:choose>
			<c:when test="${tasca.camps[0].camp.tipus == 'REGISTRE'}">
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">cancel</c:param>
					<c:param name="titles"><fmt:message key='comuns.tornar' /></c:param>
				</c:import>
			</c:when>
			<c:otherwise>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,cancel</c:param>
					<c:param name="titles"><fmt:message key='comuns.modificar' />,<fmt:message key='comuns.cancelar' /></c:param>
				</c:import>
			</c:otherwise>
		</c:choose>
	</form:form>

</body>
</html>
