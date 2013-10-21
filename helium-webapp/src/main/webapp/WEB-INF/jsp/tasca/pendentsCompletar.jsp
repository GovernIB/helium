<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.List"%>
<c:set var="sessionCommand" value="${sessionScope.consultaExpedientsCommand}"/>

<html>
<head>
	<title><fmt:message key="tasca.pendents.completar"/></title>
	<meta name="titolcmp" content="<fmt:message key="comuns.mesures"/>" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
    <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/gisDwrService.js"/>"></script>
</head>
<body>
	<div id="dialog-error-user" title="Error" style="display:none">
	</div>
	<div id="dialog-error-admin" title="Error" style="display:none">
		<form method="POST" action="<c:url value="/expedient/limpiarTrazaError.html"/>">
			<input id="processInstanceId" name="processInstanceId" value="" type="hidden"/>
			<button type="submit" class="submitButton right"><fmt:message key="expedient.consulta.netejar"/></button>
		</form>
	</div>
	
	<security:accesscontrollist domainObject="${entornActual}" hasPermission="16">
		<c:if test="${not empty tasques}">
			<display:table name="tasques" id="registre" requestURI="" class="displaytag selectable">
				<display:column property="tipusExpedient" title="Tipus Expedient" sortable="true"/>
				<display:column property="expedient" title="Expedient" sortable="true"/>
				<display:column property="tasca" title="Tasca" sortable="true"/>
				<display:column property="tascaId" title="Id" sortable="true"/>
				<display:column property="inici" title="Iniciat el" format="{0,date,dd/MM/yyyy HH:mm:ss}" sortable="true"/>
				<display:column property="tempsExecucio" title="Temps execució" format="{0,number,#,###.00 s}" sortable="true"/>
			</display:table>
			<script type="text/javascript">initSelectable();</script>
		</c:if>
		<c:if test="${empty tasques}">
			<h3>No hi ha tasques pendents de completar.</h3>
		</c:if>
	</security:accesscontrollist>
</body>
</html>
