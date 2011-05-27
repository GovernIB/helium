<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><c:choose><c:when test="${empty command.id}"><fmt:message key='persona.form.crear_nova' /></c:when><c:otherwise><fmt:message key='persona.form.modificar' /></c:otherwise></c:choose></title>
	<meta name="titolcmp" content="<fmt:message key='comuns.configuracio' />" />
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function mostrarAmagarAuth() {
	var el = document.getElementById('dadesAuth');
	if (el.style.display == '')
		el.style.display = 'none';
	else
		el.style.display = '';
}
// ]]>
</script>
</head>
<body>
	<c:set var="esReadOnly" value="${globalProperties['app.persones.readonly'] == 'true'}"/>
	<c:set var="tipusText"><c:choose><c:when test="${not esReadOnly}">text</c:when><c:otherwise>static</c:otherwise></c:choose></c:set>
	<c:set var="tipusSelect"><c:choose><c:when test="${not esReadOnly}">select</c:when><c:otherwise>static</c:otherwise></c:choose></c:set>
	<form:form action="form.html" cssClass="uniForm" enctype="multipart/form-data" >
		<fieldset class="inlineLabels col first">
			<h3><fmt:message key='persona.form.dades_perso' /></h3>
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="${tipusText}"/>
				<c:param name="label"><fmt:message key='comuns.codi' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="${tipusText}"/>
				<c:param name="label"><fmt:message key='comuns.nom' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="llinatge1"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="${tipusText}"/>
				<c:param name="label"><fmt:message key='persona.form.primer_llin' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="llinatge2"/>
				<c:param name="type" value="${tipusText}"/>
				<c:param name="label"><fmt:message key='persona.form.segon_llin' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="dni"/>
				<c:param name="type" value="${tipusText}"/>
				<c:param name="label"><fmt:message key='persona.form.dni' /></c:param>
			</c:import>
			<%--c:import url="../common/formElement.jsp">
				<c:param name="property" value="dataNaixement"/>
				<c:param name="type" value="date"/>
				<c:param name="label">Data naixement</c:param>
				<c:param name="mask">99/99/9999</c:param>
			</c:import--%>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="email"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="${tipusText}"/>
				<c:param name="label"><fmt:message key='persona.form.ae' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="sexe"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="${tipusSelect}"/>
				<c:param name="items" value="sexes"/>
				<c:param name="itemLabel" value="codi"/>
				<c:param name="itemValue" value="valor"/>
				<c:param name="label"><fmt:message key='comuns.sexe' /></c:param>
			</c:import>
			<%--c:import url="../common/formElement.jsp">
				<c:param name="property" value="avisCorreu"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="checkBoxType" value="seguit"/>
				<c:param name="label">Notificar events per correu</c:param>
			</c:import--%>
		</fieldset>
		<fieldset class="inlineLabels col last">
			<h3><fmt:message key='persona.form.acces_aplic' /></h3>
			<c:choose>
				<c:when test="${not esReadOnly}">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="login"/>
						<c:param name="type" value="checkbox"/>
						<c:param name="label"><fmt:message key='persona.form.perso_amb' /></c:param>
						<c:param name="onclick">mostrarAmagarAuth()</c:param>
					</c:import>
				</c:when>
				<c:otherwise>
					<input type="hidden" name="login" value="on"/>
					<input type="hidden" name="contrasenya" value=""/>
					<input type="hidden" name="repeticio" value=""/>
				</c:otherwise>
			</c:choose>
			<div id="dadesAuth"<c:if test="${not command.login}"> style="display:none"</c:if>>
				<c:if test="${not esReadOnly}">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="contrasenya"/>
						<c:param name="type" value="password"/>
						<c:param name="label"><fmt:message key='persona.form.contrasenya' /></c:param>
						<c:param name="comment"><fmt:message key='persona.form.en_blanc' /></c:param>
					</c:import>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="repeticio"/>
						<c:param name="type" value="password"/>
						<c:param name="label"><fmt:message key='persona.form.repeticio' /></c:param>
					</c:import>
				</c:if>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="permisos"/>
					<c:param name="type" value="multicheck"/>
					<c:param name="label"><fmt:message key='persona.form.permisos' /></c:param>
					<c:param name="items" value="permisos"/>
					<c:param name="itemLabel" value="codi"/>
					<c:param name="itemValue" value="codi"/>
				</c:import>
			</div>
		</fieldset>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}"><fmt:message key='comuns.crear' />,<fmt:message key='comuns.cancelar' /></c:when><c:otherwise><fmt:message key='comuns.modificar' />,<fmt:message key='comuns.cancelar' /></c:otherwise></c:choose></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>

</body>
</html>
