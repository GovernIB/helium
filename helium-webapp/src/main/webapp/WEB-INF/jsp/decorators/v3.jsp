<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
	<title>Helium v3: <decorator:title default="<fmt:message key='decorators.default.benvinguts' />"/></title>
	<meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
	<meta name="author" content="">
	<link href="<c:url value="/css/bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/bootstrap-responsive.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/font-awesome.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/bootstrap-helium.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/commonV3.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/js/select2/select2.css"/>" rel="stylesheet"/>	
	<!--[if lt IE 9]>
	      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	    <![endif]-->
	<link rel="shortcut icon" href="<c:url value="/img/ico/favicon.png"/>">
	<link rel="apple-touch-icon-precomposed" sizes="144x144" href="<c:url value="/img/ico/apple-touch-icon-144-precomposed.png"/>">
	<link rel="apple-touch-icon-precomposed" sizes="114x114" href="<c:url value="/img/ico/apple-touch-icon-114-precomposed.png"/>">
	<link rel="apple-touch-icon-precomposed" sizes="72x72" href="<c:url value="/img/ico/apple-touch-icon-72-precomposed.png"/>">
	<link rel="apple-touch-icon-precomposed" href="<c:url value="/img/ico/apple-touch-icon-57-precomposed.png"/>">
	<script src="<c:url value="/js/jquery.js"/>"></script>
	<script src="<c:url value="/js/select2/select2.min.js"/>"></script>
	
	<script type="text/javascript">
	// <![CDATA[
		Timeline_ajax_url="<c:url value="/js/timeline_2.3.0/timeline_ajax/simile-ajax-api.js"/>";
		Timeline_urlPrefix="<c:url value="/js/timeline_2.3.0/timeline_js/"/>";       
		Timeline_parameters="bundle=true";
	// ]]>
	</script>
	<script src="<c:url value="/js/timeline_2.3.0/timeline_js/timeline-api.js?defaultLocale=ca"/>" type="text/javascript"></script>
			 
<script>
	$(document).ready(function() { $("#e1").select2(); });
</script>	
	<decorator:head />
</head>
<body>
<c:choose>
<c:when test="${not isNoCapsaleraPeu}">
<div class="row-fluid container <c:if test="${not preferenciesUsuari.cabeceraReducida}">nav-container</c:if> <c:if test="${preferenciesUsuari.cabeceraReducida}">cabecera_reducida</c:if>">
	<div class="govern-logo pull-left"><img src="<c:url value="/img/govern-logo.png"/>" width="159" height="36" alt="Govern de les Illes Balears" /></div>
	<div class="aplication-logo pull-left"><img src="<c:url value="/img/logo-helium-w.png"/>" width="217" height="61" alt="Helium: Gestor d'expedients corporatiu" /></div>
	<div class="pull-right main-menu">
		<ul class="user-nav pull-right dropdown">
			<li>
				<a class="dropdown-toggle text-wrap" data-toggle="dropdown" href="#">
					<i class="icon-folder-close icon-white"></i>
					<c:choose>
						<c:when test="${not empty expedientTipusActual}">${expedientTipusActual.nom}</c:when>
						<c:otherwise><spring:message code="comuns.tots.tipus"/></c:otherwise>
					</c:choose>
					<span class="caret"></span>
				</a>
				<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
					<c:forEach var="expedientTipus" items="${expedientTipusAccessibles}">
						<li><a href="<c:url value="/v3/index"><c:param name="expedientTipusCanviarAmbId" value="${expedientTipus.id}"/></c:url>">${expedientTipus.nom}</a></li>
					</c:forEach>
					<li class="divider"></li>
					<li><a href="<c:url value="/v3/index"><c:param name="expedientTipusCanviarAmbId" value=""/></c:url>"><spring:message code="comuns.tots.tipus"/></a></li>
				</ul>
			</li>
			<li>
				<a class="dropdown-toggle text-wrap" data-toggle="dropdown" href="#"> <i class="icon-map-marker icon-white"></i> ${entornActual.nom} <span class="caret"></span> </a>
				<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
					<c:forEach var="entorn" items="${entorns}">
						<li><a href="<c:url value="/v3/index"><c:param name="entornCanviarAmbId" value="${entorn.id}"/></c:url>">${entorn.nom}</a></li>
    				</c:forEach>
				</ul>
			</li>
			<li><a class="dropdown-toggle text-wrap" href="<c:url value="/v3/perfil"/>"><i class="icon-user icon-white"></i> ${dadesPersona.codi}</a></li>
		</ul>
		<div class="clearfix"></div>
		<div class="btn-group">
			<button type="button" class="btn btn-primary dropdown-toggle radius-left" data-toggle="dropdown"><spring:message code="comuns.expedients"/>&nbsp;<span class="caret"></span></button>
			<ul class="dropdown-menu">
				<li><a href="<c:url value="/v3/expedient"/>">Consultar</a></li>
				<li><a href="<c:url value="/v3/expedient/iniciar"/>">Nou expedient</a></li>
			</ul>
		</div>
		<div class="btn-group">
			<button type="button" class="btn btn-primary dropdown-toggle radius-left" data-toggle="dropdown"><spring:message code="comuns.tasques"/>&nbsp;<span class="caret"></span></button>
			<ul class="dropdown-menu">
				<li><a href="<c:url value="/v3/tasca"/>">Consultar</a></li>
			</ul>
		</div>
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
		<div class="btn-group">
			<button type="button" class="btn btn-primary radius-none">Activitat</button>		
		</div>
		<div class="btn-group">
			<button type="button" class="btn btn-primary radius-right">Arxiu</button>
		</div>
	</div>
</div>
<div class="row-fluid container main <c:if test="${preferenciesUsuari.cabeceraReducida}">main_reducida</c:if>">
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
<div class="container row-fluid">
	<div class="pull-left colophon">Helium v${versioNom} | Limit Tecnologies</div>
	<div class="pull-right govern-footer"> <img src="<c:url value="/img/govern-logo-neg.png"/>" width="129" height="30" alt="Govern de les Illes Balears" /></div>
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
<script src="<c:url value="/js/bootstrap.min.js"/>"></script>
</body>
</html>
