<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Expedient: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="Consultes"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<c:import url="../common/tabsExpedient.jsp">
		<c:param name="tabActiu" value="registre"/>
	</c:import>

	<h3 class="titol-tab titol-registre">
		Registre de l'expedient
	</h3>
	<display:table name="registre" id="reg" class="displaytag">
		<display:column property="data" title="Data" format="{0,date,dd/MM/yyyy'&nbsp;'HH:mm:ss}"/>
		<display:column property="responsableCodi" title="Responsable"/>
		<display:column property="accio" title="Acció"/>
		<display:column title="Entitat">${reg.entitat}&nbsp;[${reg.entitatId}]</display:column>
		<display:column property="missatge" title="Descripció"/>
		<display:column property="valorVell" title="Valor antic"/>
		<display:column property="valorNou" title="Valor nou"/>
	</display:table>

</body>
</html>
