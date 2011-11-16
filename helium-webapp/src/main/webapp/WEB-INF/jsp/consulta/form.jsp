<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><c:choose><c:when test="${empty command.id}"><fmt:message key='consulta.form.crear_nova' /></c:when><c:otherwise><fmt:message key='consulta.form.modificar' /></c:otherwise></c:choose></title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>

	<form:form action="form.html" cssClass="uniForm" enctype="multipart/form-data">
		<div class="inlineLabels">
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='comuns.codi' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='comuns.titol' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="descripcio"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label"><fmt:message key='comuns.descripcio' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="expedientTipus"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="expedientTipus"/>
				<c:param name="itemLabel" value="nom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key='consulta.form.selec_tipus' /> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key='comuns.tipus_exp' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="informeContingut"/>
				<c:param name="type" value="file"/>
				<c:param name="fileUrl"><c:url value="/consulta/informeDownload.html"><c:param name="id" value="${command.id}"/></c:url></c:param>
				<c:param name="fileExists" value="${not empty command.informeNom}"/>
				<c:param name="label"><fmt:message key='consulta.form.informe' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="valorsPredefinits"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label"><fmt:message key='consulta.form.valors' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="exportarActiu"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='consulta.form.exportar' /></c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}"><fmt:message key='comuns.crear' />,<fmt:message key='comuns.cancelar' /></c:when><c:otherwise><fmt:message key='comuns.modificar' />,<fmt:message key='comuns.cancelar' /></c:otherwise></c:choose></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>

</body>
</html>
