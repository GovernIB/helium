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
	<title><spring:message code="expedient.tipus.info.titol"/></title>
	<meta name="title" content="<spring:message code="expedient.tipus.info.titol"/>"/>
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
	#expedientTipus-info h3 {
		font-weight: bold;
		margin-top: 0;
		border-bottom: 1px solid #e3e3e3;
		padding-bottom: .2em;
	}
	#expedientTipus-info h4 {
		font-weight: bold;
		margin-top: 0;
		border-bottom: 1px solid #e3e3e3;
		padding-bottom: .2em;
		margin-bottom: 0.4em;
	}
	#expedientTipus-info dt {
		color: #999;
		font-size: small;
		font-style: italic;
		font-weight: normal;
	}
	#expedientTipus-info dd {
		font-size: medium;
		font-weight: bold;
		margin-bottom: 0.4em;
	}
	#expedientTipus-info-accio {
		margin-top: 1em;
	}
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
		<div class="col-md-3">
			<div id="expedientTipus-info" class="well">
				<h3><spring:message code="expedient.tipus.info.informacio"/></h3>
				<dl>
					<dt><spring:message code="expedient.tipus.info.camp.codi"/></dt>
					<dd>${expedientTipus.codi}</dd>
					<dt><spring:message code="expedient.tipus.info.camp.titol"/></dt>
					<dd>${expedientTipus.nom}</dd>				
					<dt><spring:message code="expedient.tipus.info.camp.amb.titol"/></dt>
					<dd><spring:message code="comu.${expedientTipus.teTitol}"></spring:message></dd>
					<dt><spring:message code="expedient.tipus.info.camp.demana.titol"/></dt>
					<dd><spring:message code="comu.${expedientTipus.demanaTitol}"></spring:message></dd>
					<dt><spring:message code="expedient.tipus.info.camp.amb.numero"/></dt>
					<dd><spring:message code="comu.${expedientTipus.teNumero}"></spring:message></dd>
					<dt><spring:message code="expedient.tipus.info.camp.demana.numero"/></dt>
					<dd><spring:message code="comu.${expedientTipus.demanaNumero}"></spring:message></dd>
					<dt><spring:message code="expedient.tipus.info.camp.permet.retroaccio"/></dt>
					<dd><spring:message code="comu.${expedientTipus.ambRetroaccio}"></spring:message></dd>
					<dt><spring:message code="expedient.tipus.info.camp.permet.reindexacioAsincrona"/></dt>
					<dd><spring:message code="comu.${expedientTipus.reindexacioAsincrona}"></spring:message></dd>
					<c:if test="${not empty expedientTipus.responsableDefecteCodi}">
						<dt><spring:message code="expedient.tipus.info.camp.reponsable.defecte"></spring:message></dt>
						<dd>${responsableDefecte.nomSencer}</dd></c:if>
					<c:if test="${not empty definicioProcesInicial}">
						<dt><spring:message code="expedient.tipus.info.camp.definicio.proces.inicial"/></dt>
						<dd>${definicioProcesInicial.jbpmKey}</dd>
					</c:if>
				</dl>
				<c:if test="${potEscriure or potEsborrar}">
					<div id="expedientTipus-info-accio" class="dropdown">
						<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="<c:url value="/v3/expedientTipus/${expedientTipus.id}}/imatgeProces"/>"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.eines"/>&nbsp;<span class="caret"></span></a>
						<ul class="dropdown-menu">
							<c:if test="${potEsborrar}">
								<li><a href="<c:url value="/v3/expedientTipus/${expedientTipus.id}/delete"/>" data-confirm="<spring:message code="expedient.tipus.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
							</c:if>
							<c:if test="${potEscriure and potEsborrar}">
								<li class="divider"></li>
							</c:if>
							<c:if test="${potEscriure}">
								<li><a data-toggle="modal" href="<c:url value="/v3/expedientTipus/${expedientTipus.id}/update"/>"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
							</c:if>
						</ul>
					</div>
				</c:if>
			</div>
		</div>
		<div id="expedientTipus-pipelles" class="col-md-9">
			<ul class="nav nav-tabs" role="tablist">
			
				<li id="pipella-estats"><a href="#contingut-estats" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.info.pipella.estats"/></a></li>
				<li id="pipella-variables"><a href="#contingut-variables" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.info.pipella.variables"/></a></li>
				<li id="pipella-definicions-proces"><a href="#contingut-definicions-proces" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.info.pipella.definicions.proces"/></a></li>
				<li id="pipella-integracio-tramits"><a href="#contingut-integracio-tramits" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.info.pipella.integracio.tramits"/></a></li>
				<li id="pipella-integracio-forms"><a href="#contingut-integracio-forms" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.info.pipella.integracio.forms"/></a></li>
				<li id="pipella-enumeracions"><a href="#contingut-enumeracions" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.info.pipella.enumeracions"/></a></li>
				<li id="pipella-documents"><a href="#contingut-documents" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.info.pipella.documents"/></a></li>
				<li id="pipella-terminis"><a href="#contingut-terminis" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.info.pipella.terminis"/></a></li>
				<li id="pipella-accions"><a href="#contingut-accions" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.info.pipella.accions"/></a></li>
				<li id="pipella-dominis"><a href="#contingut-dominis" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.info.pipella.dominis"/></a></li>
				<li id="pipella-consultes"><a href="#contingut-consultes" role="tab" data-toggle="tab"><spring:message code="expedient.tipus.info.pipella.consultes"/></a></li>
			</ul>
			<div class="tab-content">
				<div id="contingut-estats" class="tab-pane">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-variables" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedientTipus/${expedientTipus.id}/variables"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-definicions-proces" class="tab-pane">
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
