<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="utf-8">
	<spring:message code="decorator.titol.default" var="titolDefault"/>
	<title>Helium: <decorator:title default="${titolDefault}"/></title>
	<meta name="viewport" content="width=device-width,initial-scale=1.0"/>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
	<meta name="description" content=""/>
	<meta name="author" content=""/>
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
		<script type='text/javascript' src="<c:url value="/js/respond.js"/>"></script>
	<![endif]-->
	<script src="<c:url value="/js/bootstrap.min.js"/>"></script>
	<style>
		body {
			background-image:url(<c:url value="/img/background-pattern.jpg"/>);
			color:#666666;
			padding-top: 120px;
		}
		.cabecera_reducida {height: 70px;}
		.cabecera_reducida #govern-logo {margin-top: -5px;}
		.cabecera_reducida #app-logo {margin-top: -5px;}
		.cabecera_reducida #app-logo > img {width :80%;}
		.cabecera_reducida .navbar-app .navbar-nav {padding-top: 4px;}
		.cabecera_reducida .nav.navbar-nav.navbar-right {padding-top: 8px;}
		.cabecera_reducida .navbar-btn {margin-top: -4px;}
		.cabecera_reducida .navbar-btn .btn {padding: 3px 12px;}
		.cabecera_reducida-main {margin-top: -30px;}
	</style>
	<decorator:head />
</head>
<body>
	<!-- default v3 -->
	<div class="navbar navbar-default navbar-fixed-top navbar-app <c:if test="${not preferenciesUsuari.cabeceraReducida}">nav-container</c:if><c:if test="${preferenciesUsuari.cabeceraReducida}">cabecera_reducida</c:if>" role="navigation">
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
							<c:if test="${fn:length(entorns) gt 1}"><a href="#" data-toggle="dropdown"></c:if>
							<span class="fa fa-cubes"></span> ${entornActual.nom}
							<c:if test="${fn:length(entorns) gt 1}"><b class="caret caret-white"></b></a></c:if>
							<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
								<c:forEach var="entorn" items="${entorns}">
									<li><a href="<c:url value="/v3/index"><c:param name="entornCanviarAmbId" value="${entorn.id}"/></c:url>">${entorn.nom}</a></li>
			    				</c:forEach>
							</ul>
						</li>
						<li class="dropdown">
							<span class="fa fa-cube"></span>
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
						<!-- <li>
							<span class="fa fa-bookmark"></span>
							<spring:message code="decorator.rol.usuari"/>
						</li> -->
						<li>
							<a href="<c:url value="/v3/perfil"/>">
								<span class="fa fa-user"></span>
								${dadesPersona.codi}
							</a>
						</li>
					</ul>
					<div class="clearfix"></div>
					<div class="btn-group navbar-btn navbar-right">
						<a class="btn btn-primary" href="<c:url value="/v3/expedient"/>"><spring:message code="decorator.menu.expedients"/></a>
						<a class="btn btn-primary" href="<c:url value="/v3/tasca"/>"><spring:message code="decorator.menu.tasques"/></a>
						<c:if test="${not empty consultesExpedientTipusActual || not empty expedientTipusAccessiblesAmbConsultesActives}">
							<div class="btn-group" >
								<c:choose>
									<c:when test="${not empty consultesExpedientTipusActual}">
										<button class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><spring:message code="expedient.llistat.consulta_avanzada"/> <span class="caret"></span></button>
										<ul class="dropdown-menu">
											<c:forEach var="consulte" items="${consultesExpedientTipusActual}">
												<li><a href="<c:url value="/v3/informe/consulta/${consulte.id}"></c:url>">${consulte.nom}</a></li>
											</c:forEach>
										</ul>
									</c:when>
									<c:otherwise>
										<c:if test="${not empty expedientTipusAccessiblesAmbConsultesActives}">
											<button class="btn btn-primary dropdown-toggle" data-toggle="dropdown"><spring:message code="decorator.menu.informes"/> <span class="caret"></span></button>
											<ul class="dropdown-menu">
												<c:forEach var="expedientTipus" items="${expedientTipusAccessiblesAmbConsultesActives}">
													<li><a href="<c:url value="/v3/informe/${expedientTipus.id}"></c:url>">${expedientTipus.nom}</a></li>
												</c:forEach>
											</ul>
										</c:if>
									</c:otherwise>
								</c:choose>
							</div>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="container container-main <c:if test="${preferenciesUsuari.cabeceraReducida}">cabecera_reducida-main</c:if>">
		<div class="panel panel-default">
			<c:set var="decoratorMetaTitle"><decorator:getProperty property="meta.title"/></c:set>
			<c:if test="${not empty decoratorMetaTitle}">
				<div class="panel-heading">
					<h2>
						<c:set var="metaTitleIconClass"><decorator:getProperty property="meta.title-icon-class"/></c:set>
						<c:if test="${not empty metaTitleIconClass}"><span class="${metaTitleIconClass}"></span></c:if>
						${decoratorMetaTitle}
						<c:set var="decoratorMetaSubtitle"><decorator:getProperty property="meta.subtitle"/></c:set>
						<c:if test="${not empty decoratorMetaSubtitle}"><small><decorator:getProperty property="meta.subtitle"/></small></c:if>
					</h2>
				</div>
			</c:if>
			<div class="panel-body">
				<div id="contingut-alertes"><hel:missatges/></div>
				<decorator:body />
			</div>
		</div>
	</div>
    <div class="container container-foot">
    	<div class="pull-left app-version"><p>Helium ${versioNom}</p></div>
        <div class="pull-right govern-footer"><p><img src="<c:url value="/img/govern-logo-neg.png"/>" width="129" height="30" alt="Govern de les Illes Balears" /></p></div>
    </div>

</body>
</html>
