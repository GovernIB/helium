<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title>Expedient: ${expedient.identificador}</title>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<meta name="title" content="${fn:escapeXml(expedient.identificador)}"/>
	<meta name="title-icon-class" content="fa fa-folder-open"/>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>	
</head>
<body>


	<c:if test="${not empty dadesExpedient}">
		<h3 class="titol-tab titol-dades-tasques">
			Dades expedient
		</h3>
		<display:table name="dadesExpedient" id="registre" class="displaytag">
			<display:column property="campCodi" title="Dada"/>
			<display:column property="valorIndex" title="Valor indexat"/>
			<display:column property="valorMostrar" title="Valor mostrar"/>
		</display:table>
	</c:if>
	<c:if test="${not empty dadesCamps}">
		<h3 class="titol-tab titol-dades-tasques">
			Dades camps
		</h3>
		<display:table name="dadesCamps" id="registre" requestURI="" class="displaytag">
			<display:column property="reportFieldName" title="Camp codi" sortable="true"/>
			<display:column property="etiqueta" title="Camp"/>
			<display:column title="Valor">
				${registre}
			</display:column>
		</display:table>
	</c:if>

	
</body>
</html>
