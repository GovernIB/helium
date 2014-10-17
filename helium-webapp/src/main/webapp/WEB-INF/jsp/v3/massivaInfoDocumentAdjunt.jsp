<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="expedient.document.adjuntar"/></title>
	<hel:modalHead/>
	<c:import url="../common/formIncludes.jsp"/>
</script>
</head>
<body>		
	<form:form action="documentModificarMas" cssClass="uniForm" enctype="multipart/form-data" method="post">
		<hel:inputTextarea required="true" name="motiu" textKey="expedient.accio.aturar.camp.motiu" placeholderKey="expedient.accio.aturar.camp.motiu"/>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
			<button type="submit" class="btn btn-primary"><span class="fa fa-stop"></span>&nbsp;<spring:message code="expedient.accio.aturar.boto.aturar"/></button>
		</div>
		<div class="inlineLabels">
			<c:if test="${not empty param.id}">
				<input type="hidden" id="id" name="id" value="param.id"/>
			</c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='expedient.document.titol' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="contingut"/>
				<c:param name="type" value="file"/>
				<c:param name="required" value="true"/>
				<c:param name="fileUrl">${downloadUrl}</c:param>
				<c:param name="fileExists" value="${not empty command.nom}"/>
				<c:param name="label"><fmt:message key='expedient.document.arxiu' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="data"/>
				<c:param name="type" value="date"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='expedient.document.data' /></c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">adjunt,cancel</c:param>
			<c:param name="titles"><fmt:message key='comuns.adjuntar' />,<fmt:message key='comuns.cancelar' /></c:param>
		</c:import>
	</form:form>
</body>
</html>