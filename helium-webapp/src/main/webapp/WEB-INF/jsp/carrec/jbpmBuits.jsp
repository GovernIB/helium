<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key="comuns.carrecs"/></title>
	<meta name="titolcmp" content="<fmt:message key="comuns.configuracio"/>" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<c:import url="../common/tabsCarrecsJbpm.jsp">
		<c:param name="tabActiu" value="buits"/>
	</c:import>

	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable">
		<display:column property="codi" titleKey="comuns.codi" sortable="true"/>
		<display:column property="grup" titleKey="comuns.grup" sortable="true"/>
		<display:column>
			<form action="<c:url value="/carrec/jbpmForm.html"/>">
				<input type="hidden" name="codi" value="${registre.codi}"/>
				<input type="hidden" name="grup" value="${registre.grup}"/>
				<button type="submit" class="submitButton"><fmt:message key="comuns.configurar"/></button>
			</form>
		</display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>

</body>
</html>
