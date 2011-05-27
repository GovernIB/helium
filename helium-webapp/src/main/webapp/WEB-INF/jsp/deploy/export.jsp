<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><fmt:message key='deploy.export.despl_arxiu' /></title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<c:import url="../common/formIncludes.jsp"/>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
</head>
<body>

	<c:import url="../common/tabsDeploy.jsp">
		<c:param name="tabActiu" value="export"/>
	</c:import>

	<form:form action="export.html" cssClass="uniForm" enctype="multipart/form-data">
		<div class="inlineLabels">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="etiqueta"/>
				<c:param name="label"><fmt:message key='comuns.etiqueta' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="arxiu"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="file"/>
				<c:param name="label"><fmt:message key='deploy.export.arxiu_exp' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">submit</c:param>
				<c:param name="titles"><fmt:message key='deploy.export.desplegar' /></c:param>
			</c:import>
		</div>
	</form:form>

	<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>

</body>
</html>
