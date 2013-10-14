<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
	<meta name="author" content="">
	<link href="<c:url value="/css/bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/bootstrap-responsive.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/font-awesome.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/bootstrap-helium.css"/>" rel="stylesheet"/>
	<!--[if lt IE 9]>
	      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	    <![endif]-->
	<link rel="shortcut icon" href="<c:url value="/img/ico/favicon.png"/>">
	<link rel="apple-touch-icon-precomposed" sizes="144x144" href="<c:url value="/img/ico/apple-touch-icon-144-precomposed.png"/>">
	<link rel="apple-touch-icon-precomposed" sizes="114x114" href="<c:url value="/img/ico/apple-touch-icon-114-precomposed.png"/>">
	<link rel="apple-touch-icon-precomposed" sizes="72x72" href="<c:url value="/img/ico/apple-touch-icon-72-precomposed.png"/>">
	<link rel="apple-touch-icon-precomposed" href="<c:url value="/img/ico/apple-touch-icon-57-precomposed.png"/>">
	<script src="<c:url value="/js/jquery.js"/>"></script>
	<decorator:head />
</head>
<body>
<c:choose>
<c:when test="${not isNoCapsaleraPeu}">
<div class="row-fluid container nav-container">
	<div class="govern-logo pull-left"><img src="<c:url value="/img/govern-logo.png"/>" width="159" height="36" alt="Govern de les Illes Balears" /></div>
	<div class="aplication-logo pull-left"><img src="<c:url value="/img/logo-helium-w.png"/>" width="217" height="61" alt="Helium: Gestor d'expedients corporatiu" /></div>
	<div class="pull-right main-menu">
		<ul class="user-nav pull-right dropdown">
			<li>
				<a class="dropdown-toggle text-wrap" data-toggle="dropdown" href="#">
					<i class="icon-folder-close icon-white"></i>
					<c:choose>
						<c:when test="${not empty expedientTipusActual}">${expedientTipusActual.nom}</c:when>
						<c:otherwise>Tots els tipus</c:otherwise>
					</c:choose>
					<span class="caret"></span>
				</a>
				<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
					<c:forEach var="expedientTipus" items="${expedientTipusAccessibles}">
						<li><a href="<c:url value="/v3/index"><c:param name="expedientTipusCanviarAmbId" value="${expedientTipus.id}"/></c:url>">${expedientTipus.nom}</a></li>
					</c:forEach>
					<li class="divider"></li>
					<li><a href="<c:url value="/v3/index"><c:param name="expedientTipusCanviarAmbId" value=""/></c:url>">Tots els tipus</a></li>
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
			<li><i class="icon-user icon-white"></i> ${dadesPersona.codi}</li>
		</ul>
		<div class="clearfix"></div>
		<div class="btn-group pull-right">
			<a href="<c:url value="/v3/expedient"/>" class="btn btn-primary">Expedients</a>
			<button class="btn btn-primary dropdown-toggle" data-toggle="dropdown">Informes <span class="caret"></span></button>
				<ul class="dropdown-menu">
					<li><a href="#" tabindex="-1">Expedient iniciatives parlamentàries</a></li>
				</ul>
			<button class="btn btn-primary">Activitat</button>
			<button class="btn btn-primary">Arxiu</button>
		</div>
	</div>
</div>
<div class="row-fluid container main">
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
				<li id="pipella-terminis" class="pipella<c:if test="${tabActiu == 'terminis'}"> active</c:if>"><a href="#">Terminis</a></li>
				<li id="pipella-registre" class="pipella<c:if test="${tabActiu == 'registre'}"> active</c:if>"><a href="#">Registre</a></li>
			</ul>
			<div class="well well-white">
				<decorator:body />
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
