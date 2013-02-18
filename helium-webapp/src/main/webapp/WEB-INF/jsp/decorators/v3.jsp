<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
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
	<link href="<c:url value="/css/default.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/orange-header.css"/>" rel="stylesheet"/>
	<!--[if lt IE 9]>
	      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	    <![endif]-->
	<link rel="shortcut icon" href="<c:url value="/img/ico/favicon.png"/>">
	<link rel="apple-touch-icon-precomposed" sizes="144x144" href="<c:url value="/img/ico/apple-touch-icon-144-precomposed.png"/>">
	<link rel="apple-touch-icon-precomposed" sizes="114x114" href="<c:url value="/img/ico/apple-touch-icon-114-precomposed.png"/>">
	<link rel="apple-touch-icon-precomposed" sizes="72x72" href="<c:url value="/img/ico/apple-touch-icon-72-precomposed.png"/>">
	<link rel="apple-touch-icon-precomposed" href="<c:url value="/img/ico/apple-touch-icon-57-precomposed.png"/>">
	<script src="<c:url value="/js/jquery.min.js"/>"></script>
	<decorator:head />
</head>
<body>
<div class="row-fluid container nav-container">
	<div class="govern-logo pull-left"><img src="<c:url value="/img/govern-logo.png"/>" width="159" height="36" alt="Govern de les Illes Balears" /></div>
	<div class="aplication-logo pull-left"><img src="<c:url value="/img/logo-helium-w.png"/>" width="217" height="61" alt="Helium: Gestor d'expedients corporatiu" /></div>
	<div class="pull-right main-menu">
		<ul class="user-nav pull-right dropdown">
			<li> <a class="dropdown-toggle text-wrap" data-toggle="dropdown" href="#"> <i class="icon-folder-close icon-white"></i> Juntes de Govern <span class="caret"></span> </a>
				<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
					<li><a tabindex="-1" href="#">Iniciatives parlamentàries</a></li>
					<li><a tabindex="-1" href="#">Juntes de Govern</a></li>
					<li class="divider"></li>
					<li><a tabindex="-1" href="#">Tots els tipus</a></li>
				</ul>
			</li>
			<li> <a class="dropdown-toggle text-wrap" data-toggle="dropdown" href="#"> <i class="icon-map-marker icon-white"></i> Adm. de Palma <span class="caret"></span> </a>
				<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
					<li><a tabindex="-1" href="#">Adm. de Manacor</a></li>
					<li><a tabindex="-1" href="#">Adm. de Felanitx</a></li>
				</ul>
			</li>
			<li> <a class="dropdown-toggle text-wrap" data-toggle="dropdown" href="#"> <i class="icon-user icon-white"></i> Rafel Sansó <span class="caret"></span> </a>
				<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
					<li><a tabindex="-1" href="#">Configuració</a></li>
					<li><a tabindex="-1" href="#">Sortir</a></li>
				</ul>
			</li>
		</ul>
		<div class="clearfix"></div>
		<div class="btn-group pull-right">
			<button class="btn btn-primary active">Expedients</button>
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
	<div class="well well-white">
		<div class="row-fluid">
			<jsp:include page="../common/missatgesInfoError.jsp"/>
			<decorator:body />
		</div>
		<div class="clearfix"></div>
	</div>
</div>
<!-- /container -->
<div class="container row-fluid">
	<div class="pull-left colophon">Helium v3.0 | Limit Tecnologies</div>
	<div class="pull-right govern-footer"> <img src="<c:url value="/img/govern-logo-neg.png"/>" width="129" height="30" alt="Govern de les Illes Balears" /></div>
</div>

<script src="<c:url value="/js/bootstrap.min.js"/>"></script>

</body>
</html>
