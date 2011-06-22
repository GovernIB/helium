<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><fmt:message key='expedient.iniciar.iniciar_expedient' />: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="Nou expedient"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
	var accioInici;
	function confirmar(e) {
		if (!e) var e = window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		if (accioInici == 'submit')
			return confirm("<fmt:message key='expedient.iniciar.confirm_iniciar' />");
		return true;
	}
	function editarRegistre(campId, campCodi, campEtiqueta, numCamps, index) {
		var amplada = 686;
		var alcada = 64 * numCamps + 80;
		var url = "iniciarRegistre.html?id=${expedientTipus.id}&registreId=" + campId;
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
		$('form#command').append('<input type="hidden" name="registreEsborrarId" value="' + campId + '"/>');
		$('form#command').append('<input type="hidden" name="registreEsborrarIndex" value="' + index + '"/>');
		refresh();
		return false;
	}
	function refresh() {
		$('form#command :button[name="submit"]').attr("name", "sbmt");
		$('form#command').submit();
	}
// ]]>
</script>
</head>
<body>

	<h3 class="titol-tab titol-dades-tasca">${tasca.nom}</h3>

	<form:form action="iniciarPasForm.html" cssClass="uniForm tascaForm zebraForm" onsubmit="return confirmar(event)">
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
			<c:param name="titles"><fmt:message key='comuns.iniciar' />,<fmt:message key='comuns.cancelar' /></c:param>
			<c:param name="onclick">accioInici=this.value</c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>

</body>
</html>
