<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>El meu perfil</title>
</head>
<body>

	<h3 class="capsalera">Dades personals</h3>
	<dl class="form-info">
		<dt>Codi</dt><dd>${info.codi}</dd>
		<dt>Nom</dt><dd>${info.nom}</dd>
		<dt>Llinatges</dt><dd>${info.llinatges}</dd>
		<%--dt>Data naixement</dt><dd><fmt:formatDate value="${info.dataNaixement}" pattern="dd/MM/yyyy" /></dd--%>
		<dt>DNI</dt><dd>${info.dni}&nbsp;</dd>
		<dt>A/E</dt><dd>${info.email}</dd>
		<%--dt>Notificar events per correu</dt><dd><c:choose><c:when test="${info.avisCorreu}">Si</c:when><c:otherwise>No</c:otherwise></c:choose></dd--%>
	</dl>

	<div style="clear:both"></div><br/>
	<form action="contrasenya.html"><button type="submit" class="submitButton">Canviar contrasenya</button></form><br/>
	<form action="form.html"><button type="submit" class="submitButton">Modificar perfil</button></form>

</body>
</html>
