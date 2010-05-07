<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Desplegar arxiu jBPM</title>
	<meta name="titolcmp" content="Disseny">
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<c:import url="../common/tabsDeploy.jsp">
		<c:param name="tabActiu" value="jbpm"/>
	</c:import>

	<form:form action="jbpm.html" cssClass="uniForm" enctype="multipart/form-data">
		<div class="inlineLabels">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="etiqueta"/>
				<c:param name="label">Etiqueta</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="arxiu"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="file"/>
				<c:param name="label">Arxiu exportat</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">submit</c:param>
				<c:param name="titles">Desplegar</c:param>
			</c:import>
		</div>
	</form:form>

	<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>

</body>
</html>
