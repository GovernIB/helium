<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key='comuns.def_proces' />: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='defproc.campllistat.confirmacio' />");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="camps"/>
	</c:import>

	<display:table name="camps" id="registre" requestURI="" defaultsort="1" class="displaytag selectable">
		<display:column property="codi" titleKey="comuns.codi" sortable="true" url="/definicioProces/campForm.html?definicioProcesId=${param.definicioProcesId}" paramId="id" paramProperty="id"/>
		<display:column property="etiqueta" titleKey="comuns.etiqueta" sortable="true"/>
		<display:column property="tipus" titleKey="comuns.tipus" sortable="true"/>
		<display:column titleKey="defproc.campllistat.multipleq">
			<c:choose><c:when test="${registre.multiple}"><fmt:message key='comuns.si' /></c:when><c:otherwise><fmt:message key='comuns.no' /></c:otherwise></c:choose>
		</display:column>
		<display:column>
	    	<form action="campValidacions.html">
				<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
				<input type="hidden" name="campId" value="${registre.id}"/>
				<button type="submit" class="submitButton"><fmt:message key='defproc.campllistat.validacions' />&nbsp;(${fn:length(registre.validacions)})</button>
			</form>
	    </display:column>
	    <display:column>
	    	<c:if test="${registre.tipus == 'REGISTRE'}">
		    	<form action="campRegistreMembres.html">
					<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
					<input type="hidden" name="registreId" value="${registre.id}"/>
					<button type="submit" class="submitButton"><fmt:message key='defproc.campllistat.vars' />&nbsp;<fmt:message key='defproc.campllistat.registre' />&nbsp;(${fn:length(registre.registreMembres)})</button>
				</form>
			</c:if>
	    </display:column>
		<display:column>
	    	<a href="<c:url value="/definicioProces/campDelete.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
	    </display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

	<form action="<c:url value="/definicioProces/campForm.html"/>">
		<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
		<button type="submit" class="submitButton"><fmt:message key='defproc.campllistat.nova_var' /></button>
	</form>

</body>
</html>
