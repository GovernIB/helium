<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<title>Helium - Error</title>
	<link href="<c:url value="/css/reset.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/common.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/layout.css"/>" rel="stylesheet" type="text/css"/>
<%-- menu --%>
	<link href="<c:url value="/css/dropdown/dropdown.css"/>" media="all" rel="stylesheet" type="text/css" />
	<link href="<c:url value="/css/dropdown/themes/helium/helium.css"/>" media="all" rel="stylesheet" type="text/css" />
<!--[if lt IE 7]>
<script type="text/javascript" src="<c:url value="/js/dropdown/jquery/jquery.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/dropdown/jquery/jquery.dropdown.js"/>"></script>
<![endif]-->
<%-- /menu --%>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.js"/>"></script>
<script type="text/javascript">
// <![CDATA[
function mostrarOcultar(img, objid) {
	var obj = document.getElementById(objid);
	if (obj.style.display=="none") {
		obj.style.display = "block";
		img.src = '<c:url value="/img/magnifier_zoom_out.png"/>';
	} else {
		obj.style.display = "none";
		img.src = '<c:url value="/img/magnifier_zoom_in.png"/>';
	}
}
// ]]>
</script>
</head>
<body>
	<div id="main">
		<div id="header">
			<div id="logo-wrapper">
				<h1 id="logo"><span>H</span>elium</h1>
			</div>
			<div id="menu-wrapper">
				
			</div>
			<div id="page-title">
				<h2><span>Error</span></h2>
			</div>
		</div>
		<div id="content">
			<div class="missatgesError">
				<h4 class="titol-missatge">
					S'ha produït un error a l'aplicació
					<img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="Mostrar/Ocultar" title="Mostrar/Ocultar" border="0" onclick="mostrarOcultar(this,'stack-error')"/>
				</h4>
				<pre id="stack-error" style="display:none"><%exception.printStackTrace(new java.io.PrintWriter(out, true));%></pre>
			</div>
			<br/><br/>
			<p style="text-align: center">Per favor, tornau <a href="javascript:back()">enrere</a> i provau novament a executar la mateixa petició en uns instants.</p>
			<br/>
			<p style="text-align: center">Si l'error persisteix posau-vos en contacte amb l'administrador de l'aplicació.</p>
		</div>
		<div id="push"></div>
	</div>
	<div id="footer">${globalProperties['app.copyright.text']}</div>
</body>
</html>