<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>Definició de procés: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="Disseny">
	<c:import url="../common/formIncludes.jsp"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function mostrarAmagar(id) {
	var el = document.getElementById(id);
	if (el.style.display == '')
		el.style.display = 'none';
	else
		el.style.display = '';
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsDefinicioProces.jsp">
		<c:param name="tabActiu" value="documents"/>
	</c:import>

	<form:form action="documentForm.html" cssClass="uniForm" enctype="multipart/form-data">
		<div class="inlineLabels">
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<form:hidden path="definicioProces"/>
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
				<c:param name="property" value="arxiuContingut"/>
				<c:param name="type" value="file"/>
				<c:param name="fileUrl"><c:url value="/definicioProces/documentDownload.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${command.id}"/></c:url></c:param>
				<c:param name="fileExists" value="${not empty command.arxiuNom}"/>
				<c:param name="label">Arxiu</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="plantilla"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label">Es plantilla</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="descripcio"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label">Descripcio</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="campData"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="campsTipusData"/>
				<c:param name="itemLabel" value="codiEtiqueta"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit" value="<< Seleccioni un camp >>"/>
				<c:param name="label">Camp amb la data</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="contentType"/>
				<c:param name="label">Content type</c:param>
				<c:param name="comment">Emprat en la signatura del document</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="custodiaCodi"/>
				<c:param name="label">Codi per a la custòdia</c:param>
				<c:param name="comment">Emprat en la signatura del document</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="tipusDocPortasignatures"/>
				<c:param name="label">Tipus de document</c:param>
				<c:param name="comment">Codi de document pel portasignatures</c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}">Crear,Cancel·lar</c:when><c:otherwise>Modificar,Cancel·lar</c:otherwise></c:choose></c:param>
		</c:import>
	</form:form>

	<p class="aclaracio">Els camps marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Camp obligatori" title="Camp obligatori" border="0"/> són obligatoris</p>

</body>
</html>
