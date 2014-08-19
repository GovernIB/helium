<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%
	request.setAttribute(
			"isNoCapsaleraPeu",
			new Boolean(net.conselldemallorca.helium.webapp.v3.helper.NoDecorarHelper.isNoCapsaleraNiPeu(request)));
%>
<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="utf-8">
	<title>Helium 3: <decorator:title default="<fmt:message key='decorators.default.benvinguts' />"/></title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<meta name="description" content=""/>
	<meta name="author" content=""/>
	<!-- Estils CSS -->
	<link href="<c:url value="/css/bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/font-awesome.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/estils-v3.css"/>" rel="stylesheet">
	<!--link href="<c:url value="/css/commonV3.css"/>" rel="stylesheet"/-->
	<link rel="shortcut icon" href="<c:url value="/img/ico/favicon.png"/>">
	<link rel="icon" type="image/png" href="<c:url value="/img/ico/favicon.png"/>">
	<script src="<c:url value="/js/jquery.js"/>"></script>
	<!-- Llibreria per a compatibilitat amb HTML5 -->
	<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->
	<script src="<c:url value="/js/bootstrap.min.js"/>"></script>

	<link href="<c:url value="/js/select2/select2.css"/>" rel="stylesheet"/>	
	<script src="<c:url value="/js/select2/select2.min.js"/>"></script>
<script type="text/javascript">
// <![CDATA[
	Timeline_ajax_url="<c:url value="/js/timeline_2.3.0/timeline_ajax/simile-ajax-api.js"/>";
	Timeline_urlPrefix="<c:url value="/js/timeline_2.3.0/timeline_js/"/>";       
	Timeline_parameters="bundle=true";
// ]]>
</script>
	<script src="<c:url value="/js/timeline_2.3.0/timeline_js/timeline-api.js?defaultLocale=ca"/>" type="text/javascript"></script>
<style>
body {
	background-image:url(<c:url value="/img/background-pattern.jpg"/>);
	color:#666666;
	padding-top: 120px;
}
</style>

	<decorator:head />
</head>
<body>
<c:choose>
<c:when test="${not isNoCapsaleraPeu}">
	<div class="navbar navbar-default navbar-fixed-top navbar-app" role="navigation">
		<div class="container">
			<div class="navbar-header">
				<%--button class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button--%>
				<div class="navbar-brand">
					<div id="govern-logo" class="pull-left">
						<img src="<c:url value="/img/govern-logo.png"/>" alt="Govern de les Illes Balears" />
					</div>
					<div id="app-logo" class="pull-left">
						<img src="<c:url value="/img/logo-helium-w.png"/>" alt="Helium" />
					</div>
				</div>
			</div>
			<div class="navbar-collapse collapse">
				<div class="nav navbar-nav navbar-right">
				
					<ul class="list-inline pull-right">
						<li class="dropdown">
							<c:if test="${fn:length(expedientTipusAccessibles) gt 0}"><a href="#" data-toggle="dropdown"></c:if>
							<c:choose>
								<c:when test="${not empty expedientTipusActual}">${expedientTipusActual.nom}</c:when>
								<c:otherwise><spring:message code="comuns.tots.tipus"/></c:otherwise>
							</c:choose>
							<c:if test="${fn:length(expedientTipusAccessibles) gt 0}"><b class="caret caret-white"></b></a></c:if>
							<ul class="dropdown-menu">
								<c:forEach var="expedientTipus" items="${expedientTipusAccessibles}">
									<li><a href="<c:url value="/v3/index"><c:param name="expedientTipusCanviarAmbId" value="${expedientTipus.id}"/></c:url>">${expedientTipus.nom}</a></li>
								</c:forEach>
								<li class="divider"></li>
								<li><a href="<c:url value="/v3/index"><c:param name="expedientTipusCanviarAmbId" value=""/></c:url>"><spring:message code="comuns.tots.tipus"/></a></li>
							</ul>
						</li>
						<li class="dropdown">
							<c:if test="${fn:length(entorns) gt 1}"><a href="#" data-toggle="dropdown"></c:if>
							<span class="fa fa-home"></span> ${entornActual.nom}
							<c:if test="${fn:length(entorns) gt 1}"><b class="caret caret-white"></b></a></c:if>
							<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
								<c:forEach var="entorn" items="${entorns}">
									<li><a href="<c:url value="/v3/index"><c:param name="entornCanviarAmbId" value="${entorn.id}"/></c:url>">${entorn.nom}</a></li>
			    				</c:forEach>
							</ul>
						</li>
						<li class="dropdown">
							<a href="<c:url value="/v3/perfil"/>">
								<span class="fa fa-user"></span>
								${dadesPersona.codi}
							</a>
						</li>
					</ul>
					<div class="clearfix"></div>
					<div class="btn-group navbar-btn navbar-right">
						<div class="btn-group">
							<a class="btn btn-primary" href="<c:url value="/v3/expedient"/>"><spring:message code="comuns.expedients"/></a>
							<a href="#" class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
								<span class="caret"></span>
								<span class="sr-only">Desplegar</span>
							</a>
							<ul class="dropdown-menu" role="menu">
								<li><a href="<c:url value="/v3/expedient/iniciar"/>">Nou expedient</a></li>
							</ul>
						</div>
						<a class="btn btn-primary" href="<c:url value="/v3/tasca"/>"><spring:message code="comuns.tasques"/></a>
						<div class="btn-group">
							<c:choose>
								<c:when test="${not empty expedientTipusActual}"><a href="<c:url value="/v3/informe/${expedientTipusActual.id}"></c:url>" class="btn btn-primary"><spring:message code="comuns.informes"/></a></c:when>
								<c:otherwise>
									<button class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><spring:message code="comuns.informes"/> <span class="caret"></span></button>
									<ul class="dropdown-menu">
										<c:forEach var="expedientTipus" items="${expedientTipusAccessibles}">
											<c:if test="${expedientTipus.conConsultasActivasPorTipo}">
												<li><a href="<c:url value="/v3/informe/${expedientTipus.id}"></c:url>">${expedientTipus.nom}</a></li>
											</c:if>
										</c:forEach>
									</ul>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="container container-main">
		<c:set var="capsaleraTipus"><decorator:getProperty property="meta.capsaleraTipus"/></c:set>
		<c:set var="tabActiu"><decorator:getProperty property="meta.tabActiu"/></c:set>
		<c:choose>
			<c:when test="${capsaleraTipus == 'expedient'}">
				<header>
					<div class="row-fluid">
						<div class="span12">
							<div class="contingut-alertes"><jsp:include page="../v3/missatges.jsp"/></div>						
							<div class="lead">
								<strong>${expedient.identificador}</strong>
								<a id="refrescar-btn" class="btn btn-small pull-right" href="<c:url value="/v3/expedient/${expedient.id}"/>"><i class="icon-refresh"></i></a>
							</div>
						</div>
					</div>
				</header>
				<div class="clearfix"></div>
				<ul id="pipelles-expedient" class="nav nav-tabs custom-submenu">
					<li id="pipella-tasques" class="pipella<c:if test="${tabActiu == 'tasques'}"> active</c:if>"><a href="<c:url value="/v3/expedient/${expedient.id}/tasques"/>">Tasques</a></li>
					<li id="pipella-dades" class="pipella<c:if test="${tabActiu == 'dades'}"> active</c:if>"><a href="<c:url value="/v3/expedient/${expedient.id}/dades"/>">Dades</a></li>
					<li id="pipella-documents" class="pipella<c:if test="${tabActiu == 'documents'}"> active</c:if>"><a href="<c:url value="/v3/expedient/${expedient.id}/documents"/>">Documents</a></li>
					<li id="pipella-terminis" class="pipella<c:if test="${tabActiu == 'terminis'}"> active</c:if>"><a href="<c:url value="/v3/expedient/${expedient.id}/terminis"/>">Terminis</a></li>
					<li id="pipella-registre" class="pipella<c:if test="${tabActiu == 'registre'}"> active</c:if>"><a href="<c:url value="/v3/expedient/${expedient.id}/registre"/>">Registre</a></li>
					<li id="pipella-cronograma" class="pipella<c:if test="${tabActiu == 'cronograma'}"> active</c:if>"><a href="<c:url value="/v3/expedient/${expedient.id}/timeline"/>">Cronograma</a></li>
				</ul>
				<div class="well well-white">
					<decorator:body />
				</div>
			</c:when>
			<c:when test="${capsaleraTipus == 'llistat'}">
				<div class="well well-white">
					<div class="row-fluid">
						<div class="contingut-alertes"><jsp:include page="../v3/missatges.jsp"/></div>
						<decorator:body />
					</div>
					<div class="clearfix"></div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="well well-white">
					<div class="row-fluid">
						<div class="contingut-alertes"></div>
						<decorator:body />
					</div>
					<div class="clearfix"></div>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
    <div class="container container-foot">
    	<div class="pull-left app-version"><p>Helium v${versioNom}</p></div>
        <div class="pull-right govern-footer"><p><img src="<c:url value="/img/govern-logo-neg.png"/>" width="129" height="30" alt="Govern de les Illes Balears" /></p></div>
    </div>
</c:when>
<c:otherwise>
<style>
body {background-image:none;}
</style>
	<div class="row-fluid container">
		<jsp:include page="../v3/missatges.jsp"/>
		<decorator:body />
	</div>
</c:otherwise>
</c:choose>
<script>
	$(document).ready(function() { $("select").select2(); });
	$( '[data-required="true"]' )
		.closest(".control-group")
		.children("label")
		.prepend("<i class='icon-asterisk'></i> ");
</script>

</body>
</html>
