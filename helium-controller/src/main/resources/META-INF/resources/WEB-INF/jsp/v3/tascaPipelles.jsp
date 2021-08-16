<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="numColumnes" value="${3}"/>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title>${tasca.expedientIdentificador}: ${tasca.titol}</title>
	<c:if test="${not isModal}">
		<meta name="capsaleraTipus" content="llistat"/>
		<meta name="title" content="${tasca.expedientIdentificador}: ${tasca.titol}"/>
		<meta name="title-icon-class" content="fa fa-clipboard"/>
	</c:if>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<link href="<c:url value="/css/tascaForm.css"/>" rel="stylesheet"/>
	
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">

	<script src="//code.jquery.com/ui/1.11.3/jquery-ui.js"></script>
	<link rel="stylesheet" href="//code.jquery.com/ui/1.11.3/themes/smoothness/jquery-ui.css">
	<script src="https://www.java.com/js/deployJava.js"></script>
	
	<c:choose>
		<c:when test="${isModal}"><c:url var="tascaFormAction" value="/modal/v3/tasca/${tasca.id}"/></c:when>
		<c:otherwise><c:url var="tascaFormAction" value="/v3/tasca/${tasca.id}"/></c:otherwise>
	</c:choose>
<script>
$(document).ready(function() {
	
	$('#contingut-alertes').prepend('<c:if test="${not empty tasca.createTime}"><p><spring:message code="tasca.llistat.columna.creada"/> <fmt:formatDate type="both" value="${tasca.createTime}" /></p></c:if>');
	
	<%-- Mostrar/ocultar dades de referència --%>
	$("i.agrupacio-desplegador").parent().parent().click(function() {
		var taula = $(this).parent().parent().parent();
		var tbody = $('tbody', taula).first();
		var i = $(this).find("i").first();
		tbody.toggleClass('hide');
		i.removeClass('icon-chevron-up');
		i.removeClass('icon-chevron-down');
		if (tbody.hasClass('hide'))
			i.addClass('icon-chevron-down');
		else
			i.addClass('icon-chevron-up');
	});
	<%-- Càrrega de contingut de les pipelles --%>
	$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
		var targetHref = $(e.target).attr('href');
		$(targetHref).html('<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>');
		$(targetHref).load(
			$(targetHref).data('href'),
			function (responseText, textStatus, jqXHR) {
				if (textStatus == 'error') {
					modalAjaxErrorFunction(jqXHR, textStatus);
				} else {
					modalRefrescarElements(
							window.frameElement, {
								adjustWidth: false,
								adjustHeight: false,
								maximize: true,
								buttonContainerId: "modal-botons",
								buttonCloseClass: "modal-tancar"
							});
					$(".modal-title", parent.document).html('<a href="<c:url value="/v3/expedient/${tasca.expedientId}"/>">${tasca.expedientIdentificadorEscaped}</a>: ${tasca.titolEscaped}');
				}
			}
		);
	});
	$(".tasca-boto").click(function(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		var accio = $(this).attr('value');
		var permetreFinalitzar = true;
		if (accio.indexOf('completar') == 0) {
			$.ajax({
		        type: 'POST',
		        url: "<c:url value='/v3/tasca/${tasca.id}/isPermetreFinalitzar'/>",
		        cache: false,
		        contentType: false,
		        processData: false,
		        async: false,
		        success: function(data) {
		        	if (data != "") {
		        		alert(data);
		        		permetreFinalitzar = false;
		        	} else if (!confirm("<spring:message code="tasca.tramitacio.confirm.finalitzar"/>")) {
		        		permetreFinalitzar = false;
		        	}
		        }
			});
	        if (!permetreFinalitzar)
	        	return false;
		}
		if ($("#command").length == 0) {
			$('<form/>', {id: 'command', method : 'POST'}).appendTo('body');
		}
		if ($(this).data('transicio') !== undefined) {
			$("#command").append( '<input type="hidden" id="transicio" name="transicio" value="'+$(this).data('transicio')+'"/>' );
		}
		if ($('#tasca-pipelles li[class=active]').length > 0) {
			$("#command").append( '<input type="hidden" id="pipellaActiva" name="pipellaActiva" value="'+$('#tasca-pipelles li[class=active]').attr('id')+'"/>' );
		}
		$("#command").attr('action', "${tascaFormAction}/" + $(this).attr('value'));
		modalDivCarregantDades(true);
		$("#command").submit();
	});
	<%-- Mostrar primera pipella activa --%>
	<c:choose>
		<c:when test="${not empty pipellaActiva}">$('#tasca-pipelles li#pipella-${pipellaActiva} a').click();</c:when>
		<c:otherwise>$('#tasca-pipelles li:first a').click();</c:otherwise>
	</c:choose>
});

function refrescarAlertesFunction() {
	$.ajax({
		url: "<c:url value="/nodeco/v3/missatges"/>",
		async: false,
		timeout: 20000,
		success: function (data) {
			$('#contingut-alertes *').remove();
			$('#contingut-alertes').append(data);
		}
	});
}
</script>
</head>
<body>
	<c:if test="${not empty tasca.tascaMissatgeInfo}">
		<div class="alert alert-info" role="alert">
			<span class="fa fa-info-circle"></span>
			${tasca.tascaMissatgeInfo}
		</div>
	</c:if>
	<c:if test="${not empty tasca.tascaMissatgeWarn}">
		<div class="alert alert-warning" role="alert">
			<span class="fa fa-warning"></span>
			${tasca.tascaMissatgeWarn}
		</div>
	</c:if>
	<c:if test="${not empty tasquesTramitar}">
		<div id="tasquesTramitar">
			<c:import url="import/tasquesMassiva.jsp">
				<c:param name="count" value="${fn:length(tasquesTramitar)}"/>
			</c:import>
		</div>
	</c:if>
	<c:if test="${not empty nomesLectura}">
		<c:import url="import/expedientTaula.jsp">
			<c:param name="dadesAttribute" value="nomesLectura"/>
			<c:param name="titol" value="Dades de referència"/>
			<c:param name="numColumnes" value="${numColumnes}"/>
			<c:param name="count" value="${fn:length(dadesNomesLectura)}"/>
			<c:param name="desplegat" value="${true}"/>
			<c:param name="desplegadorClass" value="agrupacio-desplegador"/>
		</c:import>
	</c:if>
	<c:choose>
		<c:when test="${hasFormulari or hasDocuments or hasSignatures}">
			<c:set var="pipellaIndex" value="${1}"/>
			<ul id="tasca-pipelles" class="nav nav-tabs">
				<c:if test="${hasFormulari}">
					<li id="pipella-form">
						<a href="#tasca-form" data-toggle="tab">
							<c:if test="${not tasca.validada}"><span class="fa fa-warning"></span></c:if>
							${pipellaIndex}.
							<spring:message code="tasca.tramitacio.pipella.form"/>
						</a>
					</li>
					<c:set var="pipellaIndex" value="${pipellaIndex + 1}"/>
				</c:if>
				<c:if test="${hasDocuments}">
					<li id="pipella-document">
						<a href="#tasca-document" data-toggle="tab">
							<span class="fa fa-warning" <c:if test="${tasca.documentsComplet}">style="display: none"</c:if>></span>
							${pipellaIndex}.
							<spring:message code="tasca.tramitacio.pipella.document"/>
						</a>
					</li>
					<c:set var="pipellaIndex" value="${pipellaIndex + 1}"/>
				</c:if>
				<c:if test="${hasSignatures}">
					<li id="pipella-signatura">
						<a href="#tasca-signatura" data-toggle="tab">
							<span class="fa fa-warning" <c:if test="${tasca.signaturesComplet}">style="display: none"</c:if>></span>
							${pipellaIndex}.
							<spring:message code="tasca.tramitacio.pipella.signatura"/>
						</a>
					</li>
					<c:set var="pipellaIndex" value="${pipellaIndex + 1}"/>
				</c:if>
			</ul>
			<div class="tab-content">
				<c:if test="${hasFormulari}">
					<div id="tasca-form" class="tab-pane active" data-href="<c:url value="/nodeco/v3/tasca/${tasca.id}/form"/>"></div>
				</c:if>
				<c:if test="${hasDocuments}">
					<c:choose>
						<c:when test="${isModal}"><div id="tasca-document" class="tab-pane" data-href="<c:url value="/nodeco/modal/v3/tasca/${tasca.id}/document"/>"></div></c:when>
						<c:otherwise><div id="tasca-document" class="tab-pane" data-href="<c:url value="/nodeco/v3/tasca/${tasca.id}/document"/>"></div></c:otherwise>
					</c:choose>
				</c:if>
				<c:if test="${hasSignatures}">
					<div id="tasca-signatura" class="tab-pane" data-href="<c:url value="/nodeco/v3/tasca/${tasca.id}/signatura"/>"></div>
				</c:if>
			</div>
		</c:when>
		<c:otherwise>
			<div class="well"><span class="fa fa-info-circle"></span> <spring:message code="tasca.tramitacio.sense.pipelles"/></div>
		</c:otherwise>
	</c:choose>
	<c:if test="${!bloquejarEdicioTasca}">
		<div id="guardarValidarTarea">
			<%@ include file="campsTascaBotons.jsp" %>
		</div>
	</c:if>
</body>
</html>