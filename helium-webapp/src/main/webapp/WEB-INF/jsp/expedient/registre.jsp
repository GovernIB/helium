<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title><fmt:message key='alerta.llistat.expedient' />: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.consultes' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="registre"/>
	</c:import>

	<h3 class="titol-tab titol-registre">
		<fmt:message key='expedient.registre' />
	</h3>
	<display:table name="registre" id="reg" class="displaytag">
		<display:column property="data" titleKey="expedient.document.data" format="{0,date,dd/MM/yyyy'&nbsp;'HH:mm:ss}"/>
		<display:column property="responsableCodi" titleKey="expedient.editar.responsable"/>
		<display:column property="accio" titleKey="expedient.registre.accio"/>
		<display:column titleKey="expedient.registre.entitat">${reg.entitat}&nbsp;[${reg.entitatId}]</display:column>
		<display:column property="missatge" titleKey="expedient.registre.missatge"/>
		<display:column property="valorVell" titleKey="expedient.registre.valorVell"/>
		<display:column property="valorNou" titleKey="expedient.registre.valorNou"/>
	</display:table>

</body>
</html>
