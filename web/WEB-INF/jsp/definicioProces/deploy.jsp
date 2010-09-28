<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><c:choose><c:when test="${not empty command.expedientTipusId}">Tipus d'expedient: ${expedientTipus.nom}</c:when><c:otherwise>Desplegar arxiu</c:otherwise></c:choose></title>
	<meta name="titolcmp" content="Disseny"/>
    <link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<c:if test="${not empty command.expedientTipusId}">
		<c:import url="../common/tabsExpedientTipus.jsp">
			<c:param name="tabActiu" value="defprocs"/>
		</c:import>
	</c:if>

	<form:form action="deploy.html" cssClass="uniForm" enctype="multipart/form-data">
		<div class="inlineLabels">
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="tipus"/>
				<c:param name="type" value="select"/>
				<c:param name="required" value="true"/>
				<c:param name="items" value="desplegamentTipus"/>
				<c:param name="itemLabel" value="valor"/>
				<c:param name="itemValue" value="codi"/>
				<c:param name="label">Tipus de desplegament</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="expedientTipusId"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="expedientTipus"/>
				<c:param name="itemLabel" value="nom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit" value="<< Qualsevol de l'entorn >>"/>
				<c:param name="label">Tipus d'expedient</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="arxiu"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="file"/>
				<c:param name="label">Arxiu exportat</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="etiqueta"/>
				<c:param name="label">Etiqueta</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">submit,cancel</c:param>
				<c:param name="titles">Desplegar,Cancel·lar</c:param>
			</c:import>
		</div>
	</form:form>

	<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>

</body>
</html>
