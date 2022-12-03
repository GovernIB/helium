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
	<link href="<c:url value="/webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/font-awesome/4.5.0/css/font-awesome.min.css"/>" rel="stylesheet"/>
	<!-- Llibreria per a compatibilitat amb HTML5 -->
	<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
    <script src="<c:url value="/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"/>"></script>
	<decorator:head />
</head>
<script type="text/javascript">
	// Guarda l'idioma de la configuració de l'usuari a local storage
	// necesari per les traduccions de literals del paginador del dataTables
	localStorage.setItem('requestLocale', '${requestLocale}');
</script>
<body>
	<decorator:body />
</body>
</html>
