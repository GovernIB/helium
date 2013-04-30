<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title><c:choose><c:when test="${empty command.id}"><fmt:message key='entorn.form.crear_nou' /></c:when><c:otherwise><fmt:message key='entorn.form.modificar' /></c:otherwise></c:choose></title>
	<meta name="titolcmp" content="<fmt:message key='comuns.configuracio' />" />
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function mostrarOcultar(img, objid) {
	var obj = document.getElementById(objid);
	if (obj.style.display=="none") {
		obj.style.display = "block";
		img.src = '<c:url value="/img/magnifier_zoom_out.png"/>';
	} else {
		obj.style.display = "none";
		img.src = '<c:url value="/img/magnifier_zoom_in.png"/>';
	}
}
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='entorn.form.confirmacio' />");
}
// ]]>
</script>
</head>
<body>

	<form:form action="form.html" cssClass="uniForm">
		<div class="inlineLabels">
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<c:choose>
				<c:when test="${empty command.id}">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="codi"/>
						<c:param name="required" value="true"/>
						<c:param name="label"><fmt:message key='comuns.codi' /></c:param>
					</c:import>
				</c:when>
				<c:otherwise>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="codi"/>
						<c:param name="type" value="static"/>
						<c:param name="required" value="true"/>
						<c:param name="label"><fmt:message key='comuns.codi' /></c:param>
					</c:import>
				</c:otherwise>
			</c:choose>
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
				<c:param name="property" value="actiu"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='entorn.form.actiuq' /></c:param>
			</c:import>
		</div>
		<c:choose>
			<c:when test="${empty isAdmin || isAdmin}">
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,cancel</c:param>
					<c:param name="titles"><c:choose><c:when test="${empty command.id}"><fmt:message key='comuns.crear' />,<fmt:message key='comuns.cancelar' /></c:when><c:otherwise><fmt:message key='comuns.modificar' />,<fmt:message key='comuns.cancelar' /></c:otherwise></c:choose></c:param>
				</c:import>
			</c:when>
			<c:otherwise>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit</c:param>
					<c:param name="titles"><c:choose><c:when test="${empty command.id}"><fmt:message key='comuns.crear' /></c:when><c:otherwise><fmt:message key='comuns.modificar' /></c:otherwise></c:choose></c:param>
				</c:import>
			</c:otherwise>
		</c:choose>
	</form:form>

	<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>

	<c:if test="${not empty command.id}">
		<c:set var="mostrarExportacio" value="${false}"/>
		<security:authorize access="hasRole('ROLE_ADMIN')">
			<c:set var="mostrarExportacio" value="${true}"/>
		</security:authorize>
		<security:accesscontrollist domainObject="${entornActual}" hasPermission="16">
			<c:set var="mostrarExportacio" value="${true}"/>
		</security:accesscontrollist>
		<c:if test="${mostrarExportacio}">
			<br/>
			<div class="missatgesGris">
				<h3 class="titol-tab titol-delegacio"><fmt:message key='entorn.form.importacio' /> <img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="<fmt:message key='entorn.form.mos_ocul' />" title="<fmt:message key='entorn.form.mos_ocul' />" border="0" onclick="mostrarOcultar(this,'form-importar')"/></h3>
				<div id="form-importar" style="display:none">
					<form:form action="importar.html" cssClass="uniForm" enctype="multipart/form-data" commandName="commandImportacio" onsubmit="return confirmar(event)">
						<input type="hidden" name="id" value="${command.id}"/>
						<div class="inlineLabels">
							<c:import url="../common/formElement.jsp">
								<c:param name="property" value="arxiu"/>
								<c:param name="type" value="file"/>
								<c:param name="label"><fmt:message key='entorn.form.arxiu_exp' /></c:param>
							</c:import>
							<c:import url="../common/formElement.jsp">
								<c:param name="type" value="buttons"/>
								<c:param name="values">submit</c:param>
								<c:param name="titles"><fmt:message key='entorn.form.importar' /></c:param>
							</c:import>
						</div>
					</form:form>
				</div>
			</div>
			<div class="missatgesGris">
				<form action="<c:url value="/entorn/exportar.html"/>" method="post" style="display: inline">
					<input type="hidden" name="id" value="${command.id}"/>
					<button type="submit" class="submitButton"><fmt:message key='entorn.form.exp_dades' /></button>
				</form>
			</div>
		</c:if>
	</c:if>

</body>
</html>
