<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code="expedient.tipus.pipelles.titol"/></title>
	<meta name="title" content="<spring:message code="expedient.tipus.pipelles.titol"/>"/>
	<meta name="subtitle" content="${fn:escapeXml(expedientTipus.nom)}"/>
	<meta name="title-icon-class" content="fa fa-folder-open"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
<style type="text/css">
	#expedientTipus-pipelles .tab-pane {
		margin-top: .6em;
	}
	.contingut-carregant {
		margin-top: 4em;
		text-align: center;
	}
	.edita {
		color: #428bca
	}
	.edita:hover {
		color: #3071a9
	}
	.right-btn {
		float: right;
		margin-top: -4px;
	}
</style>
<script type="text/javascript">
	$(document).ready(function() {		
		$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
			var targetHref = $(e.target).attr('href');
			var loaded = $(targetHref).data('loaded')
			if (true) {			//Condició per carregar cada vegada les pipelles
				carregaTab(targetHref);
			}
		})
		<c:choose>
			<c:when test="${not empty pipellaActiva}">$('#expedientTipus-pipelles li#pipella-${pipellaActiva} a').click();</c:when>
			<c:otherwise>$('#expedientTipus-pipelles li:first a').click();</c:otherwise>
		</c:choose>
	});
	
	function carregaTab(targetHref) {
		//mostrem cada cop l'icona de càrrega
		$(targetHref).html('<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>'); 
		///////////////
		
		$(targetHref).load(
			$(targetHref).data('href'),
			function (responseText, textStatus, jqXHR) {
				if (textStatus == 'error') {
					modalAjaxErrorFunction(jqXHR, textStatus);
				} else {
					$(this).data('loaded', 'true');
				}
			}
		);
	}	
</script>
</head>
<body>

	<div class="row">
		<div id="expedientTipus-pipelles" class="col-md-12">
			<ul class="nav nav-tabs" role="tablist">
			
				<li id="pipella-informacio"><a href="#contingut-informacio" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.informacio"/></a></li>
				<li id="pipella-estats"><a href="#contingut-estats" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.estats"/></a></li>
				<li id="pipella-variables"><a href="#contingut-variables" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.variables"/></a></li>
				<li id="pipella-definicions-proces"><a href="#contingut-definicions-proces" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.definicions.proces"/></a></li>
				<li id="pipella-integracio-tramits"><a href="#contingut-integracio-tramits" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.integracio.tramits"/></a></li>
				<li id="pipella-integracio-forms"><a href="#contingut-integracio-forms" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.integracio.forms"/></a></li>
				<li id="pipella-enumeracions"><a href="#contingut-enumeracions" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.enumeracions"/></a></li>
				<li id="pipella-documents"><a href="#contingut-documents" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.documents"/></a></li>
				<li id="pipella-terminis"><a href="#contingut-terminis" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.terminis"/></a></li>
				<li id="pipella-accions"><a href="#contingut-accions" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.accions"/></a></li>
				<li id="pipella-dominis"><a href="#contingut-dominis" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.dominis"/></a></li>
				<li id="pipella-consultes"><a href="#contingut-consultes" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.pipelles.pipella.consultes"/></a></li>
			</ul>
			<div class="tab-content">
				<div id="contingut-informacio" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/informacio"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-estats" class="tab-pane">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-variables" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/variables"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-definicions-proces" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/definicionsProces"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-integracio-tramits" class="tab-pane">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-integracio-forms" class="tab-pane">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-enumeracions" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/enumeracions"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-documents" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/documents"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-terminis" class="tab-pane">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-accions" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/accions"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-dominis" class="tab-pane">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-consultes" class="tab-pane">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
	// <![CDATA[
	//]]>
	</script>
</body>
</html>
