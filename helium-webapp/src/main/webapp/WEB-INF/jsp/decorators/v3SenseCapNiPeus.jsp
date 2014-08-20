<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>

<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8">
	<title><decorator:title default="Benvinguts" /></title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<meta name="description" content=""/>
	<meta name="author" content=""/>
	<!-- Estils CSS -->
	<link href="<c:url value="/css/bootstrap.min.css"/>" rel="stylesheet">
	<link href="<c:url value="/css/font-awesome.min.css"/>" rel="stylesheet">
	<link href="<c:url value="/css/estils-v3.css"/>" rel="stylesheet">
	<link rel="shortcut icon" href="<c:url value="/img/ico/favicon.png"/>">
	<link rel="icon" type="image/png" href="<c:url value="/img/ico/favicon.png"/>">
	<script src="<c:url value="/js/jquery.js"/>"></script>
	<!-- Llibreria per a compatibilitat amb HTML5 -->
	<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <script src="<c:url value="/js/bootstrap.min.js"/>"></script>
	<decorator:head />
</head>
<body>
	<div>
		<div id="contingut-alertes"><hel:missatges/></div>
		<decorator:body />
	</div>
</body>
</html>
