<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title><c:choose><c:when test="${empty command.id}">Crear nou entorn</c:when><c:otherwise>Modificar entorn</c:otherwise></c:choose></title>
	<meta name="titolcmp" content="Configuració"/>
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
	return confirm("Estau segur que voleu importar aquestes dades a dins l'entorn?");
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
						<c:param name="label">Codi</c:param>
					</c:import>
				</c:when>
				<c:otherwise>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="codi"/>
						<c:param name="type" value="static"/>
						<c:param name="required" value="true"/>
						<c:param name="label">Codi</c:param>
					</c:import>
				</c:otherwise>
			</c:choose>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label">Títol</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="descripcio"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label">Descripció</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="actiu"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Actiu?</c:param>
			</c:import>
		</div>
		<c:choose>
			<c:when test="${empty isAdmin || isAdmin}">
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit,cancel</c:param>
					<c:param name="titles"><c:choose><c:when test="${empty command.id}">Crear,Cancel·lar</c:when><c:otherwise>Modificar,Cancel·lar</c:otherwise></c:choose></c:param>
				</c:import>
			</c:when>
			<c:otherwise>
				<c:import url="../common/formElement.jsp">
					<c:param name="type" value="buttons"/>
					<c:param name="values">submit</c:param>
					<c:param name="titles"><c:choose><c:when test="${empty command.id}">Crear</c:when><c:otherwise>Modificar</c:otherwise></c:choose></c:param>
				</c:import>
			</c:otherwise>
		</c:choose>
	</form:form>

	<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>

	<c:if test="${not empty command.id}">
		<c:set var="mostrarExportacio" value="${false}"/>
		<security:authorize ifAllGranted="ROLE_ADMIN">
			<c:set var="mostrarExportacio" value="${true}"/>
		</security:authorize>
		<security:accesscontrollist domainObject="${entornActual}" hasPermission="16">
			<c:set var="mostrarExportacio" value="${true}"/>
		</security:accesscontrollist>
		<c:if test="${mostrarExportacio}">
			<br/>
			<div class="missatgesGris">
				<h3 class="titol-tab titol-delegacio">Importació de dades <img src="<c:url value="/img/magnifier_zoom_in.png"/>" alt="Mostrar/Ocultar" title="Mostrar/Ocultar" border="0" onclick="mostrarOcultar(this,'form-importar')"/></h3>
				<div id="form-importar" style="display:none">
					<form:form action="importar.html" cssClass="uniForm" enctype="multipart/form-data" commandName="commandImportacio" onsubmit="return confirmar(event)">
						<input type="hidden" name="id" value="${command.id}"/>
						<div class="inlineLabels">
							<c:import url="../common/formElement.jsp">
								<c:param name="property" value="arxiu"/>
								<c:param name="type" value="file"/>
								<c:param name="label">Arxiu exportat</c:param>
							</c:import>
							<c:import url="../common/formElement.jsp">
								<c:param name="type" value="buttons"/>
								<c:param name="values">submit</c:param>
								<c:param name="titles">Importar</c:param>
							</c:import>
						</div>
					</form:form>
				</div>
			</div>
			<div class="missatgesGris">
				<form action="<c:url value="/entorn/exportar.html"/>" method="post" style="display: inline">
					<input type="hidden" name="id" value="${command.id}"/>
					<button type="submit" class="submitButton">Exportar dades</button>
				</form>
			</div>
		</c:if>
	</c:if>

</body>
</html>
