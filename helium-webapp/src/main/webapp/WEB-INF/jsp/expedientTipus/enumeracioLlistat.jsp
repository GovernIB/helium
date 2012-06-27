<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title>Tipus d'expedient: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="<fmt:message key="comuns.disseny"/>" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key="enumeracio.llistat.confirmacio"/>");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="enum"/>
	</c:import>
	
	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
		<display:column property="codi" titleKey="comuns.codi" sortable="true" url="/expedientTipus/enumeracioForm.html?expedientTipusId=${expedientTipus.id}" paramId="id" paramProperty="id"/>
		<display:column property="nom" titleKey="comuns.titol" sortable="true"/>
		<display:column>
			<form action="enumeracioValors.html">
				<input type="hidden" name="id" value="${registre.id}"/>
				<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
				<button type="submit" class="submitButton"><fmt:message key="enumeracio.llistat.valors"/>&nbsp;(${fn:length(registre.enumeracioValors)})</button>
			</form>
		</display:column>
		<display:column>
			<a href="<c:url value="/expedientTipus/enumeracioEsborrar.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/><c:param name="enumeracioId" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key="comuns.esborrar"/>" title="<fmt:message key="comuns.esborrar"/>" border="0"/></a>
		</display:column>
	</display:table>
	<script type="text/javascript">initSelectable(7);</script>

	<form action="<c:url value="/expedientTipus/enumeracioForm.html"/>">
		<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
		<button type="submit" class="submitButton"><fmt:message key="enumeracio.llistat.nova"/></button>
	</form>

</body>
</html>
