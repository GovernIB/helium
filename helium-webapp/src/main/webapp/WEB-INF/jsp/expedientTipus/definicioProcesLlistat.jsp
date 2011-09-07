<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Tipus d'expedient: ${expedientTipus.nom}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar la darrera versió d'aquesta definició de procés?");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="defprocs"/>
	</c:import>

	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
		<display:column title="Nom" sortable="true">
			<c:set var="potDissenyarEntorn" value="${false}"/>
			<security:accesscontrollist domainObject="${entornActual}" hasPermission="16,32">
				<c:set var="potDissenyarEntorn" value="${true}"/>
			</security:accesscontrollist>
			<c:choose>
				<c:when test="${potDissenyarEntorn or expedientTipus.id == registre.expedientTipus.id}"><a href="<c:url value="/definicioProces/info.html"><c:param name="definicioProcesId" value="${registre.id}"/></c:url>">${registre.jbpmName}</a></c:when>
				<c:otherwise>${registre.jbpmName}</c:otherwise>
			</c:choose>
		</display:column>
		<display:column property="numIdsWithSameKey" title="Versions" sortable="true"/>
		<display:column title="Inicial?" sortable="true">
			<c:choose>
				<c:when test="${expedientTipus.jbpmProcessDefinitionKey == registre.jbpmKey}">Si</c:when>
				<c:otherwise>No</c:otherwise>
			</c:choose>
		</display:column>
		<display:column>
			<form action="<c:url value="/expedientTipus/definicioProcesInicial.html"/>">
				<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
				<input type="hidden" name="jbpmKey" value="${registre.jbpmKey}"/>
				<button type="submit" class="submitButton">Marcar com a proces inicial</button>
			</form>
		</display:column>
		<display:column>
			<c:if test="${registre.expedientTipus.id == expedientTipus.id}"><a href="<c:url value="/expedientTipus/definicioProcesEsborrar.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/><c:param name="definicioProcesId" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="Esborrar darrera versió" title="Esborrar darrera versió" border="0"/></a></c:if>
		</display:column>
	</display:table>
	<script type="text/javascript">initSelectable(3);</script>

	<form action="<c:url value="/expedientTipus/deploy.html"/>">
		<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
		<button type="submit" class="submitButton">Desplegar arxiu</button>
	</form>

</body>
</html>
