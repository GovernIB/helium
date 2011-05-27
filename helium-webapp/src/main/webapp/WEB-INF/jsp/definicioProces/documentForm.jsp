<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><fmt:message key='comuns.def_proces' />: ${definicioProces.jbpmName}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
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
				<c:param name="label"><fmt:message key='comuns.codi' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key='comuns.nom' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="arxiuContingut"/>
				<c:param name="type" value="file"/>
				<c:param name="fileUrl"><c:url value="/definicioProces/documentDownload.html"><c:param name="definicioProcesId" value="${param.definicioProcesId}"/><c:param name="id" value="${command.id}"/></c:url></c:param>
				<c:param name="fileExists" value="${not empty command.arxiuNom}"/>
				<c:param name="label"><fmt:message key='comuns.arxiu' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="plantilla"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key='defproc.docform.es_plantilla' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="descripcio"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label"><fmt:message key='comuns.descripcio' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="campData"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="campsTipusData"/>
				<c:param name="itemLabel" value="codiEtiqueta"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key='defproc.docform.selec_camp' /> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key='defproc.docform.camp_data' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="contentType"/>
				<c:param name="label"><fmt:message key='defproc.docform.ctype' /></c:param>
				<c:param name="comment"><fmt:message key='defproc.docform.emprat_sign' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="custodiaCodi"/>
				<c:param name="label"><fmt:message key='defproc.docform.codi_custodia' /></c:param>
				<c:param name="comment"><fmt:message key='defproc.docform.emprat_sign' /></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="tipusDocPortasignatures"/>
				<c:param name="label"><fmt:message key='defproc.docform.tipus_doc' /></c:param>
				<c:param name="comment"><fmt:message key='defproc.docform.codi_doc' /></c:param>
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
