<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

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
	return confirm("<fmt:message key='consulta.llistat.confirmacio' />");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsExpedientTipus.jsp">
		<c:param name="tabActiu" value="consultes"/>
	</c:import>

	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable" defaultsort="1" defaultorder="ascending">
		<display:column property="ordre" titleKey="comuns.ordre"  />
		<display:column property="codi" titleKey="comuns.codi" sortable="false" url="/expedientTipus/consultaForm.html?expedientTipusId=${expedientTipus.id}" paramId="id" paramProperty="id"/>
		<display:column property="nom" titleKey="comuns.titol" sortable="false"/>
		<display:column property="expedientTipus.nom" titleKey="comuns.tipus_exp" sortable="false" />
		
		<display:column>
			<a href="<c:url value="/consulta/valorsPujar.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/><c:param name="consultaId" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_up.png"/>" alt="<fmt:message key="comuns.amunt"/>" title="<fmt:message key="comuns.amunt"/>" border="0"/></a>
			<a href="<c:url value="/consulta/valorsBaixar.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/><c:param name="consultaId" value="${registre.id}"/></c:url>"><img src="<c:url value="/img/famarrow_down.png"/>" alt="<fmt:message key="comuns.avall"/>" title="<fmt:message key="comuns.avall"/>" border="0"/></a>
		</display:column>
		<display:column>
			<c:set var="numCampsFiltre" value="${0}"/>
	    	<c:forEach var="camp" items="${registre.camps}">
    			<c:if test="${camp.tipus=='FILTRE'}"><c:set var="numCampsFiltre" value="${numCampsFiltre+1}"/></c:if>
	    	</c:forEach>
	    	<form action="consultaCamps.html">
				<input type="hidden" name="id" value="${registre.id}"/>
				<input type="hidden" name="tipus" value="${tipusFiltre}"/>
				<input type="hidden" name="expedientTipusId" value="${param.expedientTipusId}"/>
				<button type="submit" class="submitButton"><fmt:message key='consulta.llistat.vars_filtre' />&nbsp;(${numCampsFiltre})</button>
			</form>
	    </display:column>
	    <display:column>
	    	<c:set var="numCampsInforme" value="${0}"/>
	    	<c:forEach var="camp" items="${registre.camps}">
    			<c:if test="${camp.tipus=='INFORME'}"><c:set var="numCampsInforme" value="${numCampsInforme+1}"/></c:if>
	    	</c:forEach>
	    	<form action="consultaCamps.html">
				<input type="hidden" name="id" value="${registre.id}"/>
				<input type="hidden" name="tipus" value="${tipusInforme}"/>
				<input type="hidden" name="expedientTipusId" value="${param.expedientTipusId}"/>
				<button type="submit" class="submitButton">Variables de l'informe&nbsp;(${numCampsInforme})</button>
			</form>
	    </display:column>
	    <%--display:column>
	    	<form action="subconsultes.html">
				<input type="hidden" name="id" value="${registre.id}"/>
				<button type="submit" class="submitButton">Subconsultes&nbsp;(${fn:length(registre.subConsultes)})</button>
			</form>
	    </display:column--%>
		<display:column>
			<a href="<c:url value="/expedientTipus/consultaEsborrar.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/><c:param name="consultaId" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		</display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

	<form action="<c:url value="/expedientTipus/consultaForm.html"/>">
		<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
		<button type="submit" class="submitButton"><fmt:message key='consulta.llistat.nova' /></button>
	</form>

</body>
</html>
