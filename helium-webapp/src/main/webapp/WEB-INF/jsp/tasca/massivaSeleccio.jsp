<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
<head>
	<title><fmt:message key="tasca.pllistat.tasq_pendents"/></title>
	<meta name="titolcmp" content="<fmt:message key="comuns.tasques"/>" />
	<c:import url="../common/formIncludes.jsp"/>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript">
	// <![CDATA[
		$(document).ready(function(){
			var d = new Date();
			var dia = ("0" + d.getDate()).slice(-2);
		    var mes = ("0" + (d.getMonth() + 1)).slice(-2);
		    var any = d.getFullYear();
		    var hora = ("0" + d.getHours()).slice(-2);
		    var minuts = ("0" + d.getMinutes()).slice(-2);
		    var ara = dia + '/' + mes + '/' + any + ' ' + hora + ':' + minuts
		    
			$('#inici').datetimepicker({defaultValue: ara});
		});
		function valorCorreu() {
			var correu = $("#correu").is(":checked") ? true : false;
			$("#correu").val(correu);
		}
		// ]]>
	</script>
</head>
<body>

	<c:import url="../common/tabsTasques.jsp">
		<c:param name="tabActiu" value="persona"/>
	</c:import>

	<h3 class="titol-tab titol-tasca-massiva">
		<fmt:message key="tasca.massiva.tramitacio"/>
	</h3>

	<form action="massivaTramitacio.html" method="post" onSubmit="valorCorreu()">
		<div class="uniForm">
			<div class="inlineLabels col first">
				<div class="ctrlHolder" style="height:45px;">
					<label for="inici"><fmt:message key="expedient.consulta.datainici"/></label>
					<input id="inici" name="inici" type="text" class="textInput" <c:if test="${not empty param.inici}">value="${param.inici}"</c:if>/>
				</div>
			</div>
			<div class="inlineLabels uniForm col last">
				<div class="ctrlHolder" style="height:45px;">
					<label for="correu"><fmt:message key="expedient.massiva.correu"/></label>
					<input id="correu" name="correu" type="checkbox"  <c:if test="${not empty param.correu and param.correu == 'true'}">checked="checked"</c:if>/>
				</div>
			</div>
		</div>
		<input type="hidden" name="id" value="${param.id}"/>
		<display:table name="personaLlistat" id="registre" requestURI="" class="displaytag selectable">
			<display:column>
				<c:if test="${registre.id == param.id}"><input type="hidden" name="tascaId" value="${registre.id}"/></c:if>
				<c:set var="tascaSeleccionada" value="${registre.id == param.id}"/>
				<c:forEach var="tascaMassiva" items="${seleccioMassiva}">
					<c:if test="${tascaMassiva.id == registre.id}"><c:set var="tascaSeleccionada" value="${true}"/></c:if>
				</c:forEach>
				<input type="checkbox" name="tascaId" value="${registre.id}"<c:if test="${tascaSeleccionada}"> checked="checked"</c:if><c:if test="${registre.id == param.id}"> disabled="disabled"</c:if>/>
			</display:column>
			<display:column property="titol" titleKey="tasca.pllistat.tasca"/>
			<display:column sortProperty="expedientTitol" titleKey="tasca.pllistat.expedient" sortable="true">
				<a href="<c:url value="/expedient/info.html"><c:param name="id" value="${registre.expedientProcessInstanceId}"/></c:url>">${registre.expedientTitol}</a>
			</display:column>
			<display:column property="expedientTipusNom" titleKey="comuns.tipus_exp" sortable="true"/>
			<display:column property="dataCreacio" titleKey="tasca.pllistat.creada_el" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true"/>
			<display:column titleKey="tasca.pllistat.prioritat" sortable="true">
				<c:choose>
					<c:when test="${registre.prioritat == 2}"><fmt:message key='tasca.pllistat.m_alta' /></c:when>
					<c:when test="${registre.prioritat == 1}"><fmt:message key='tasca.pllistat.alta' /></c:when>
					<c:when test="${registre.prioritat == 0}"><fmt:message key='tasca.pllistat.normal' /></c:when>
					<c:when test="${registre.prioritat == -1}"><fmt:message key='tasca.pllistat.baixa' /></c:when>
					<c:when test="${registre.prioritat == -2}"><fmt:message key='tasca.pllistat.m_baixa' /></c:when>
					<c:otherwise>${registre.prioritat}</c:otherwise>
				</c:choose>
			</display:column>
			<c:choose>
				<c:when test="${not empty terminisIniciats[registre_rowNum - 1] and terminisIniciats[registre_rowNum - 1].estat == 'NORMAL'}"><c:set var="estilData">color:white;background-color:green</c:set></c:when>
				<c:when test="${not empty terminisIniciats[registre_rowNum - 1] and terminisIniciats[registre_rowNum - 1].estat == 'AVIS'}"><c:set var="estilData">color:white;background-color:orange</c:set></c:when>
				<c:when test="${not empty terminisIniciats[registre_rowNum - 1] and terminisIniciats[registre_rowNum - 1].estat == 'CADUCAT'}"><c:set var="estilData">color:white;background-color:red</c:set></c:when>
				<c:otherwise><c:set var="estilData"></c:set></c:otherwise>
			</c:choose>
			<display:column property="dataLimit" titleKey="tasca.pllistat.data_limit" format="{0,date,dd/MM/yyyy HH:mm}" sortable="true" style="${estilData}"/>
		</display:table>
		<button type="submit" class="submitButton"><fmt:message key="tasca.massiva.tramitar"/></button>
	</form>

</body>
</html>
