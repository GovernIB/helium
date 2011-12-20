<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" buffer="16kb" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:set var="isIframe" value="${not empty param.iframe}"/>
<html>
<head>
	<title>${tasca.nomLimitat}</title>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:if test="${isIframe}">
		<script type="text/javascript" src="<c:url value="/js/jquery/jquery.js"/>"></script>
		<c:if test="${not empty formRecursParams.css}">
			<link href="<c:url value="/definicioProces/recursDescarregar.html"><c:param name="definicioProcesId" value="${tasca.definicioProces.id}"></c:param><c:param name="resourceName" value="forms/${formRecursParams.css}"></c:param></c:url>" rel="stylesheet" type="text/css"/>
		</c:if>
	</c:if>
	<c:if test="${not isIframe}">
		<meta name="titolcmp" content="<fmt:message key='comuns.tasques' />" />
		<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	</c:if>
	<c:import url="../common/formIncludes.jsp">
		<c:param name="withoutCss" value="${isIframe}"/>
	</c:import>
	<c:if test="${isIframe}"><style>.ui-widget {font-size: 81%;}</style></c:if>
<script type="text/javascript">
// <![CDATA[
function confirmar(form) {
	if (accio == "validate")
		return confirm("<fmt:message key='tasca.form.confirmacio' />");
	else
		return true;
}
// ]]>
</script>
<c:if test="${not empty tasca.formExtern}">
	<script type="text/javascript" src="<c:url value="/dwr/interface/formulariExternDwrService.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
<script type="text/javascript">
// <![CDATA[
	function clickFormExtern(form) {
		formulariExternDwrService.dadesIniciFormulari(
				form.id.value,
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
<script type="text/javascript">
// <![CDATA[
    function verificarSignatura(element) {
		var amplada = 800;
		var alcada = 600;
		$('<iframe id="verificacio" src="' + element.href + '"/>').dialog({
			title: "<fmt:message key='tasca.form.verif_signa' />",
			autoOpen: true,
			modal: true,
			autoResize: true,
			width: parseInt(amplada),
			height: parseInt(alcada)
		}).width(amplada - 30).height(alcada - 30);
		return false;
	}
	function infoRegistre(docId) {
		var amplada = 600;
		var alcada = 200;
		$('<div>' + $("#registre_" + docId).html() + '</div>').dialog({
			title: "<fmt:message key='tasca.form.info_reg' />",
			autoOpen: true,
			modal: true,
			width: parseInt(amplada),
			height: parseInt(alcada)
		}).width(amplada - 30).height(alcada - 30);
		return false;
	}
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
		var url = "registre.html?id=${tasca.id}&registreId=" + campId;
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

	function campOnFocus(camp) {
		$('form#command :input[name="helCampFocus"]').val("" + $(window).scrollTop() + "#${param.id}");
	}
	function initFocus() {
		$("form#command :input").focus(function() {campOnFocus(this)});
		<c:if test="${not empty sessionScope.helCampFocus}">
			$(window).scrollTop(${sessionScope.helCampFocus});
			<c:remove var="helCampFocus" scope="session"/>
		</c:if>
	}
	$(document).ready(initFocus);
// ]]>
</script>
</head>
<body>
	<c:if test="${not isIframe}">
		<c:import url="../common/tabsTasca.jsp">
			<c:param name="tabActiu" value="form"/>
		</c:import>
		<c:if test="${not tasca.validada}">
			<div class="missatgesWarn">
				<c:choose>
					<c:when test="${empty tasca.formExtern}">
						<p><fmt:message key='tasca.form.no_validades' /></p>
					</c:when>
					<c:otherwise>
						<p><fmt:message key='tasca.form.compl_form' /></p>
					</c:otherwise>
				</c:choose>
			</div>
		</c:if>
		<c:import url="../common/tascaReadOnly.jsp"/>
		<c:if test="${tasca.campsNotReadOnly}">
			<h3 class="titol-tab titol-dades-tasca">
				<fmt:message key='tasca.form.form_tasca' />
			</h3>
		</c:if>
	</c:if>

	<c:if test="${tasca.campsNotReadOnly}">
		<c:set var="botons" scope="request">
			<c:choose>
				<c:when test="${tasca.validada}">
					<c:import url="../common/formElement.jsp">
						<c:param name="type" value="buttons"/>
						<c:param name="values">restore</c:param>
						<c:param name="titles"><fmt:message key='comuns.modificar' /></c:param>
						<c:param name="onclick" value="guardarAccio(this.value)"/>
					</c:import>
				</c:when>
				<c:otherwise>
					<c:import url="../common/formElement.jsp">
						<c:param name="type" value="buttons"/>
						<c:param name="values">submit,validate</c:param>
						<c:param name="titles"><fmt:message key='tasca.form.guardar' />,<fmt:message key='tasca.form.validar' /></c:param>
						<c:param name="onclick" value="guardarAccio(this.value)"/>
					</c:import>
				</c:otherwise>
			</c:choose>
		</c:set>
		<c:choose>
			<c:when test="${not empty tasca.formExtern}">
				<c:if test="${tasca.validada}">
					<form:form action="form.html" cssClass="uniForm tascaForm zebraForm" onsubmit="return confirmar(this)">
						<form:hidden path="id"/>
						<form:hidden path="entornId"/>
						<input type="hidden" name="helCampFocus"/>
						<div class="inlineLabels">
							<c:if test="${not empty tasca.camps}">
								<c:forEach var="camp" items="${tasca.camps}">
									<c:if test="${not camp.readOnly}">
										<c:set var="campTascaActual" value="${camp}" scope="request"/>
										<c:import url="../common/campTasca.jsp"/>
									</c:if>
								</c:forEach>
							</c:if>
							${botons}
						</div>
					</form:form><br/>
				</c:if>
				<form action="form.html" onclick="return clickFormExtern(this)">
					<input type="hidden" name="id" value="${tasca.id}"/>
					<button type="submit" class="submitButton"><fmt:message key='tasca.form.obrir_form' /></button>
				</form>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${empty param.toParent}">
						<c:choose>
							<c:when test="${empty tasca.recursForm or tasca.validada}">
								<form:form action="form.html" cssClass="uniForm tascaForm zebraForm" onsubmit="return confirmar(this)">
									<form:hidden path="id"/>
									<form:hidden path="entornId"/>
									<input type="hidden" name="helCampFocus"/>
									<div class="inlineLabels">
										<c:if test="${not empty tasca.camps}">
											<c:forEach var="camp" items="${tasca.camps}">
												<c:if test="${not camp.readOnly}">
													<c:set var="campTascaActual" value="${camp}" scope="request"/>
													<c:import url="../common/campTasca.jsp"/>
												</c:if>
											</c:forEach>
										</c:if>
										${botons}
									</div>
								</form:form>
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${isIframe}">
										<form:form action="formIframe.html" cssClass="uniForm tascaForm zebraForm" onsubmit="return confirmar(this)">
											<form:hidden path="id"/>
											<form:hidden path="entornId"/>
											<input type="hidden" name="helCampFocus"/>
											<input type="hidden" name="iframe" value="iframe"/>
											<c:import url="formRecurs.jsp"/>
										</form:form>
									</c:when>
									<c:otherwise>
										<c:set var="iframeWidth" value="200"/>
										<c:if test="${not empty iframeWidth}"><c:set var="iframeWidth" value="${formRecursParams['width']}"/></c:if>
										<c:set var="iframeHeight" value="400"/>
										<c:if test="${not empty iframeWidth}"><c:set var="iframeHeight" value="${formRecursParams['height']}"/></c:if>
										<iframe style="border:none" name="formRecurs" width="${iframeWidth}" height="${iframeHeight}" src="formIframe.html?id=${param.id}&iframe=iframe"></iframe>
									</c:otherwise>
								</c:choose>
							</c:otherwise>
						</c:choose>
						<c:if test="${not isIframe}">
							<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>
						</c:if>
					</c:when>
					<c:otherwise>
						<script type="text/javascript">window.parent.location="form.html?id=${param.id}"</script>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</c:if>

	<br/><c:import url="../common/tramitacioTasca.jsp">
		<c:param name="pipella" value="form"/>
	</c:import>

</body>
</html>
