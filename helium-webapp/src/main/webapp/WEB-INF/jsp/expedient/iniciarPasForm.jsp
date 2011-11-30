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
		if ("cancel" == submitAction)
			return true;
<c:choose>
	<c:when test="${not ((expedientTipus.teNumero and expedientTipus.demanaNumero) or (expedientTipus.teTitol and expedientTipus.demanaTitol))}">return confirm("<fmt:message key='expedient.iniciar.confirm_iniciar' />");</c:when>
	<c:otherwise>return true</c:otherwise>
</c:choose>
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
<c:if test="${not empty tasca.formExtern}">
	<script type="text/javascript" src="<c:url value="/dwr/interface/formulariExternDwrService.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
<script type="text/javascript">
// <![CDATA[
	function clickFormExtern(form) {
		formulariExternDwrService.dadesIniciFormulariInicial(
				form.id.value,
				'${command.expedientTipusId}',
				<c:choose><c:when test="${not empty command.definicioProcesId}">'${command.definicioProcesId}'</c:when><c:otherwise>null</c:otherwise></c:choose>,
				{
					callback: function(retval) {
						if (retval) {
							$('<iframe id="formExtern" src="' + retval[0] + '"/>').dialog({
								title: '<fmt:message key="tasca.form.dades_form" />',
				                autoOpen: true,
				                modal: true,
				                autoResize: true,
				                width: parseInt(retval[1]),
				                height: parseInt(retval[2]),
				                close: function() {
									form.submit();
								}
				            }).width(parseInt(retval[1]) - 30).height(parseInt(retval[2]) - 30);
						} else {
							alert("<fmt:message key='tasca.form.error_ini' />");
						}
					},
					async: false
				});
		return false;
	}
// ]]>
</script>
</c:if>
</head>
<body>

	<h3 class="titol-tab titol-dades-tasca">${tasca.nom}</h3>

	<c:if test="${not empty tasca.formExtern}">
		<form action="iniciarPasForm.html" onclick="return clickFormExtern(this)">
			<input type="hidden" name="id" value="${tasca.id}"/>
			<input type="hidden" name="entornId" value="${command.entornId}"/>
			<input type="hidden" name="expedientTipusId" value="${command.expedientTipusId}"/>
			<button type="submit" class="submitButton"><fmt:message key='tasca.form.obrir_form' /></button>
		</form><br/>
	</c:if>

	<form:form action="iniciarPasForm.html" cssClass="uniForm tascaForm zebraForm" onsubmit="return confirmar(event)">
		<div class="inlineLabels">
			<form:hidden path="entornId"/>
			<form:hidden path="expedientTipusId"/>
			<input type="hidden" name="definicioProcesId" value="${param.definicioProcesId}"/>
			<c:if test="${(empty tasca.formExtern) or (not empty tasca.formExtern and tasca.validada)}">
				<c:if test="${not empty tasca.camps}">
					<c:forEach var="camp" items="${tasca.camps}">
						<c:set var="campTascaActual" value="${camp}" scope="request"/>
						<c:import url="../common/campTasca.jsp"/>
					</c:forEach>
				</c:if>
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
