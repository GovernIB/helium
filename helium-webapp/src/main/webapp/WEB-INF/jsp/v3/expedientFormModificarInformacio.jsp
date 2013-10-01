<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
	<title>Expedient</title>
	<script type="text/javascript" src="<c:url value="/js/jquery.keyfilter.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<link href="<c:url value="/css/autocomplete.css"/>" rel="stylesheet">
	<link href="<c:url value="/css/commonV3.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jquery/jquery.autocomplete.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
</head>
<body>

<jsp:include page="import/helforms.jsp" />

<form:form id="modificarInformacio" name="modificarInformacio" action="modificarInformacio" method="post" commandName="expedientEditarCommand">
	<div class="modal-body">
		<div class="inlineLabels">
			<input type="hidden" name="id" value="${param.id}"/>
			<c:if test="${expedient.tipus.teNumero}">
				<c:import url="common/formElement.jsp">
					<c:param name="property" value="numero"/>
					<c:param name="required" value="true"/>
					<c:param name="label"><fmt:message key='expedient.consulta.numero' /></c:param>
					<c:param name="value" value="${expedient.registreNumer}"/>
				</c:import>
				</c:if>
			<c:if test="${expedient.tipus.teTitol}">
				<c:import url="common/formElement.jsp">
					<c:param name="property" value="titol"/>
					<c:param name="type" value="textarea"/>
					<c:param name="required" value="true"/>
					<c:param name="label"><fmt:message key='expedient.consulta.titol' /></c:param>
					<c:param name="value" value="${expedient.titol}"/>
				</c:import>
			</c:if>
			<c:import url="common/formElement.jsp">
				<c:param name="property" value="dataInici"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="date"/>
				<c:param name="label"><fmt:message key='expedient.consulta.datainici' /></c:param>
				<c:param name="value" value="${expedient.dataInici}"/>
			</c:import>
			<c:import url="common/formElement.jsp">
				<c:param name="property" value="responsableCodi"/>
				<c:param name="type" value="suggest"/>
				<c:param name="label"><fmt:message key='expedient.editar.responsable' /></c:param>
				<c:param name="suggestUrl"><c:url value="persona/suggest"/></c:param>
				<c:param name="suggestText">${expedient.responsablePersona.nomSencer}</c:param>
				<c:param name="value" value="${expedient.responsablePersona.nomSencer}"/>
			</c:import>
			<c:import url="common/formElement.jsp">
				<c:param name="property" value="comentari"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label"><fmt:message key='expedient.editar.comentari' /></c:param>
				<c:param name="value" value="${expedient.comentari}"/>
			</c:import>
			<c:import url="common/formElement.jsp">
				<c:param name="property" value="estatId"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="estats"/>
				<c:param name="itemLabel" value="nom"/>
				<c:param name="itemValue" value="id"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key='expedient.consulta.select.estat' /> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key='expedient.editar.estat' /></c:param>
				<c:param name="value" value="${expedient.estat.nom}"/>
			</c:import>
			<c:if test="${globalProperties['app.georef.actiu']}">
				<c:choose>
					<c:when test="${globalProperties['app.georef.tipus']=='ref'}">
						<c:import url="common/formElement.jsp">
							<c:param name="property" value="geoReferencia"/>
							<c:param name="label"><fmt:message key='comuns.georeferencia.codi' /></c:param>
							<c:param name="value" value="${expedient.geoReferencia}"/>
						</c:import>
					</c:when>
					<c:otherwise>
						<c:import url="common/formElement.jsp">
							<c:param name="property" value="geoPosX"/>
							<c:param name="type" value="number"/>
							<c:param name="keyfilter">/[\d\-\.]/</c:param>
							<c:param name="label"><fmt:message key='comuns.georeferencia.coordenadaX' /></c:param>
							<c:param name="value" value="${expedient.geoPosX}"/>
						</c:import>
						<c:import url="common/formElement.jsp">
							<c:param name="property" value="geoPosY"/>
							<c:param name="type" value="number"/>
							<c:param name="keyfilter">/[\d\-\.]/</c:param>
							<c:param name="label"><fmt:message key='comuns.georeferencia.coordenadaY' /></c:param>
							<c:param name="value" value="${expedient.geoPosY}"/>
						</c:import>
					</c:otherwise>
				</c:choose>
			</c:if>
			<c:import url="common/formElement.jsp">
				<c:param name="property" value="grupCodi"/>
				<c:param name="required" value="false"/>
				<c:param name="label"><fmt:message key='expedient.editar.grup_codi' /></c:param>
				<c:param name="value" value="${expedient.grupCodi}"/>
			</c:import>
		</div>
	</div>
</form:form>
<script>
	$('.datepicker').datepicker({language: 'ca', autoclose: true});

	window.parent.canviTitolModal("Modificar informació de l'expedient");
	var transicions = new Array();
	var texts = new Array();
	transicions.push('modificarInformacio');texts.push('Modificar');
	window.parent.substituirBotonsPeuModal(transicions, texts);
	function test(codi) {
		if (confirm('Estau segur que voleu donar aquesta tasca per finalitzada?')) {
			$("#modificarInformacio").submit();
			$("#tramitacio-modal").hide();
		}
	}
</script>
</body>
