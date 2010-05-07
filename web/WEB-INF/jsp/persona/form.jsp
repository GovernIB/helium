<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><c:choose><c:when test="${empty command.id}">Crear nova persona</c:when><c:otherwise>Modificar persona</c:otherwise></c:choose></title>
	<meta name="titolcmp" content="Configuració">
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

	<form:form action="form.html" cssClass="uniForm" enctype="multipart/form-data" >
		<fieldset class="inlineLabels col first">
			<h3>Dades personals</h3>
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Codi</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Nom</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="llinatge1"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Primer llinatge</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="llinatge2"/>
				<c:param name="label">Segon llinatge</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="dni"/>
				<c:param name="label">DNI</c:param>
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
				<c:param name="label">A/E</c:param>
			</c:import>
			<%--c:import url="../common/formElement.jsp">
				<c:param name="property" value="avisCorreu"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="checkBoxType" value="seguit"/>
				<c:param name="label">Notificar events per correu</c:param>
			</c:import--%>
		</fieldset>
		<fieldset class="inlineLabels col last">
			<h3>Accés a l'aplicació</h3>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="login"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Persona amb accés a l'aplicació</c:param>
				<c:param name="onclick">mostrarAmagarAuth()</c:param>
			</c:import>
			<div id="dadesAuth"<c:if test="${not command.login}"> style="display:none"</c:if>>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="contrasenya"/>
					<c:param name="type" value="password"/>
					<c:param name="label">Contrasenya</c:param>
					<c:param name="comment">Si la contrasenya es deixa en blanc no es canviarà</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="repeticio"/>
					<c:param name="type" value="password"/>
					<c:param name="label">Repetició contrasenya</c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="permisos"/>
					<c:param name="type" value="multicheck"/>
					<c:param name="label">Permisos</c:param>
					<c:param name="items" value="permisos"/>
					<c:param name="itemLabel" value="codi"/>
					<c:param name="itemValue" value="codi"/>
				</c:import>
			</div>
		</fieldset>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}">Crear,Cancel·lar</c:when><c:otherwise>Modificar,Cancel·lar</c:otherwise></c:choose></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>

</body>
</html>
