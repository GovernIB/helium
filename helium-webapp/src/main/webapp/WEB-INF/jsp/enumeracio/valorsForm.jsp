<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><c:choose><c:when test="${empty command.id}"><fmt:message key="enumeracio.form.crear_nova"/></c:when><c:otherwise><fmt:message key="enumeracio.form.modificar"/></c:otherwise></c:choose></title>
	<meta name="titolcmp" content="<fmt:message key="comuns.disseny"/>" />
	<c:import url="../common/formIncludes.jsp"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
</head>

<body>
	<form:form action="valorsForm.html" cssClass="uniForm" method="post">
		<div class="inlineLabels">
			<input type="hidden" name="enumeracioId" value="${enumeracioId}"/>
			<c:if test="${not empty command.id}">
				<form:hidden path="id"/>
			</c:if>
			<c:if test="${not empty command.ordre}">
				<input type="hidden" name="ordre" value="${command.ordre}"/>
			</c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key="comuns.codi"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key="comuns.titol"/></c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><fmt:message key="comuns.modificar"/>,<fmt:message key="comuns.cancelar"/></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key="comuns.camps_marcats"/> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key="comuns.camp_oblig"/>" title="<fmt:message key="comuns.camp_oblig"/>" border="0"/> <fmt:message key="comuns.son_oblig"/></p>
</body>
</html>
