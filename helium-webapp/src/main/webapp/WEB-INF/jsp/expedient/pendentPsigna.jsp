<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title><fmt:message key="expedient.consulta.cons_general"/></title>
	<meta name="titolcmp" content="<fmt:message key="comuns.consultes"/>" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable" sort="external">
		<display:column property="portasignaturesId" title="Id psigna." sortable="true"/>
		<display:column property="estat" title="Estat" sortable="true"/>
		<display:column property="dataEnviat" title="Enviat" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true"/>
		<display:column property="dataCallbackDarrer" title="Darrer cb" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true"/>
		<display:column title="Error cb">
			<c:if test="${not empty registre.errorCallback}"><img src="<c:url value="/img/error.png"/>" border="0" title="${registre.errorCallback}"/></c:if>
		</display:column>
		<display:column property="expedient.identificador" title="Expedient" sortable="true"/>
		<display:column property="expedient.dataInici" title="Iniciat el" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true"/>
		<display:column property="expedient.tipus.nom" title="Tipus" sortable="true"/>
		<display:column title="Estat" sortable="true" sortProperty="estat.nom">
			<c:if test="${registre.expedient.aturat}"><img src="<c:url value="/img/stop.png"/>" alt="Aturat" title="Aturat" border="0"/></c:if>
			<c:choose>
				<c:when test="${empty registre.expedient.dataFi}">
					<c:choose><c:when test="${empty registre.expedient.estat}"><fmt:message key="expedient.consulta.iniciat"/></c:when><c:otherwise>${registre.expedient.estat.nom}</c:otherwise></c:choose>
				</c:when>
				<c:otherwise><fmt:message key="expedient.consulta.finalitzat"/></c:otherwise>
			</c:choose>
		</display:column>
		
	</display:table>
	<script type="text/javascript">initSelectable();</script>

</body>
</html>
