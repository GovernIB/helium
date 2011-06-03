<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Canvi Versió Mapeig Sistra</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="common/formIncludes.jsp"/>
</head>
<body>

	<c:if test="${not mapeigSistraBuit}">
		<h2>ATENCIÓ: La nova Taula (MapeigSistra) ja conté valors!!! Els registres amb clau duplicats no s'inseriràn a la base de dades.</h2>
	</c:if>
	
	<form:form action="canviVersioMapeig.html" >
		<c:import url="common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit</c:param>
			<c:param name="titles">Canviar Versió</c:param>
		</c:import>
	</form:form>
	
</body>
</html>
