<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key='entorn.seleccio.selec_entorn' /></title>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<c:set var="missatgeTaulaBuida"><span class="nothingFound"><fmt:message key='entorn.seleccio.no_hi_ha' /></span></c:set>
	
	<display:table name="entorns" id="registre" requestURI="" class="displaytag">
	    <display:column>
	    	<c:choose>
	    		<c:when test="${preferencies.defaultEntornCodi == registre.codi}">
	    			<c:url value="/entorn/configDefault.html" var="urlLink"/>
	    			<c:url value="/img/star.png" var="urlStar"/>
	    		</c:when>
	    		<c:otherwise>
	    			<c:url value="/entorn/configDefault.html" var="urlLink"><c:param name="entornId" value="${registre.id}"/></c:url>
	    			<c:url value="/img/star_grey.png" var="urlStar"/>
	    		</c:otherwise>
	    	</c:choose>
			<a href="${urlLink}"><img src="${urlStar}" alt="<fmt:message key='entorn.seleccio.per_defecte' />" title="<fmt:message key='entorn.seleccio.per_defecte' />" border="0"/></a>
		</display:column>
	    <display:column property="nom" title="Entorn" sortable="true"/>
	    <display:column>
	    	<form action="<c:url value="/index.html"/>">
				<input type="hidden" name="entornCanviarAmbId" value="${registre.id}"/>
				<button type="submit" class="submitButton"><fmt:message key='entorn.seleccio.seleccionar' /></button>
			</form>
	    </display:column>
	    <display:setProperty name="basic.msg.empty_list" value="${missatgeTaulaBuida}" />
	</display:table>

</body>
</html>
