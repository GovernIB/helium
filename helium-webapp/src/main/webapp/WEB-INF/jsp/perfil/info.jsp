<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key='perfil.info.meu_perfil' /></title>
</head>
<body>

	<h3 class="capsalera"><fmt:message key='perfil.info.dades_perso' /></h3>
	<dl class="form-info">
		<dt><fmt:message key='comuns.codi' /></dt><dd>${info.codi}</dd>
		<dt><fmt:message key='comuns.nom' /></dt><dd>${info.nom}</dd>
		<dt><fmt:message key='perfil.info.llinatges' /></dt><dd>${info.llinatges}</dd>
		<%--dt>Data naixement</dt><dd><fmt:formatDate value="${info.dataNaixement}" pattern="dd/MM/yyyy" /></dd--%>
		<dt><fmt:message key='perfil.info.dni' /></dt><dd>${info.dni}&nbsp;</dd>
		<dt><fmt:message key='perfil.info.ae' /></dt><dd>${info.email}</dd>
		<dt><fmt:message key='comuns.sexe' /></dt><dd><c:choose><c:when test="${info.sexe == 'SEXE_HOME'}"><fmt:message key='perfil.info.home' /></c:when><c:otherwise><fmt:message key='perfil.info.dona' /></c:otherwise></c:choose></dd>
		<%--dt>Notificar events per correu</dt><dd><c:choose><c:when test="${info.avisCorreu}">Si</c:when><c:otherwise>No</c:otherwise></c:choose></dd--%>
	</dl>

	<c:set var="esReadOnly" value="${globalProperties['app.persones.readonly'] == 'true'}"/>
	<c:if test="${not esReadOnly}">
		<div style="clear:both"></div><br/>
		<form action="contrasenya.html"><button type="submit" class="submitButton"><fmt:message key='perfil.info.canviar' /></button></form><br/>
		<form action="form.html"><button type="submit" class="submitButton"><fmt:message key='perfil.info.modificar' /></button></form>
	</c:if>

</body>
</html>
