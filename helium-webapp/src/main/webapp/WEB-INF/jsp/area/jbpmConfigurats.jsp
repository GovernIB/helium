<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key="comuns.arees"/></title>
	<meta name="titolcmp" content="<fmt:message key="comuns.configuracio"/>"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key="area.jbpmid.confirmacio"/>");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsAreesJbpm.jsp">
		<c:param name="tabActiu" value="configurats"/>
	</c:import>

	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
		<display:column property="codi" titleKey="comuns.codi" sortable="true" url="/area/jbpmForm.html" paramId="id" paramProperty="id"/>
		<display:column property="nom" titleKey="comuns.nom" sortable="true"/>
		<display:column>
			<a href="<c:url value="/area/jbpmDelete.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key="comuns.esborrar"/>" title="<fmt:message key="comuns.esborrar"/>" border="0"/></a>
		</display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

</body>
</html>
