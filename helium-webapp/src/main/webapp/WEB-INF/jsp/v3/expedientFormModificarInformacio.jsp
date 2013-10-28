<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>
<head>
	<title><spring:message code='expedient.info.informacio' /></title>
	<c:import url="utils/modalHead.jsp">
		<c:param name="titol" value="Modificar informació de l'expedient"/>
		<c:param name="buttonContainerId" value="botons"/>
	</c:import>
	<c:import url="common/formIncludes.jsp"/>
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
						<c:param name="label"><spring:message code='expedient.consulta.numero' /></c:param>
						<c:param name="value" value="${expedient.registreNumero}"/>
					</c:import>
				</c:if>
				<c:if test="${expedient.tipus.teTitol}">
					<c:import url="common/formElement.jsp">
						<c:param name="property" value="titol"/>
						<c:param name="type" value="textarea"/>
						<c:param name="required" value="true"/>
						<c:param name="label"><spring:message code='expedient.consulta.titol' /></c:param>
						<c:param name="value" value="${expedient.titol}"/>
					</c:import>
				</c:if>
				<c:import url="common/formElement.jsp">
					<c:param name="property" value="dataInici"/>
					<c:param name="required" value="true"/>
					<c:param name="type" value="date"/>
					<c:param name="label"><spring:message code='expedient.consulta.datainici' /></c:param>
					<c:param name="value" value="${expedient.dataInici}"/>
				</c:import>
				<script>$("#dataInici").mask("99/99/9999");</script>
				
				<c:import url="common/formElement.jsp">
					<c:param name="property" value="responsableCodi"/>
					<c:param name="type" value="suggest"/>
					<c:param name="label"><spring:message code='expedient.editar.responsable' /></c:param>
					<c:param name="suggestUrl"><c:url value="persona/suggest"/></c:param>
					<c:param name="suggestText">${expedient.responsablePersona.nomSencer}</c:param>
					<c:param name="value" value="${expedient.responsablePersona.nomSencer}"/>
				</c:import>
				<c:import url="common/formElement.jsp">
					<c:param name="property" value="comentari"/>
					<c:param name="type" value="textarea"/>
					<c:param name="label"><spring:message code='expedient.editar.comentari' /></c:param>
					<c:param name="value" value="${expedient.comentari}"/>
				</c:import>
				<c:import url="common/formElement.jsp">
					<c:param name="property" value="estatId"/>
					<c:param name="type" value="select"/>
					<c:param name="items" value="estats"/>
					<c:param name="itemLabel" value="nom"/>
					<c:param name="itemValue" value="id"/>
					<c:param name="itemBuit">&lt;&lt; <spring:message code='expedient.consulta.select.estat' /> &gt;&gt;</c:param>
					<c:param name="label"><spring:message code='expedient.editar.estat' /></c:param>
					<c:param name="value" value="${expedient.estat.nom}"/>
				</c:import>
				<c:if test="${globalProperties['app.georef.actiu']}">
					<c:choose>
						<c:when test="${globalProperties['app.georef.tipus']=='ref'}">
							<c:import url="common/formElement.jsp">
								<c:param name="property" value="geoReferencia"/>
								<c:param name="label"><spring:message code='comuns.georeferencia.codi' /></c:param>
								<c:param name="value" value="${expedient.geoReferencia}"/>
							</c:import>
						</c:when>
						<c:otherwise>
							<c:import url="common/formElement.jsp">
								<c:param name="property" value="geoPosX"/>
								<c:param name="type" value="number"/>
								<c:param name="keyfilter">/[\d\-\.]/</c:param>
								<c:param name="label"><spring:message code='comuns.georeferencia.coordenadaX' /></c:param>
								<c:param name="value" value="${expedient.geoPosX}"/>
							</c:import>
							<c:import url="common/formElement.jsp">
								<c:param name="property" value="geoPosY"/>
								<c:param name="type" value="number"/>
								<c:param name="keyfilter">/[\d\-\.]/</c:param>
								<c:param name="label"><spring:message code='comuns.georeferencia.coordenadaY' /></c:param>
								<c:param name="value" value="${expedient.geoPosY}"/>
							</c:import>
						</c:otherwise>
					</c:choose>
				</c:if>
				<c:import url="common/formElement.jsp">
					<c:param name="property" value="grupCodi"/>
					<c:param name="required" value="false"/>
					<c:param name="label"><spring:message code='expedient.editar.grup_codi' /></c:param>
					<c:param name="value" value="${expedient.grupCodi}"/>
				</c:import>
			</div>
		</div>
		<div id="botons" class="well">
			<button type="submit" class="btn btn-primary"><spring:message code="comuns.modificar"/></button>
			<button type="button" class="btn btn-tancar"><spring:message code="comuns.cancelar"/></button>
		</div>
	</form:form>
	<script>
		$('.datepicker').datepicker({language: 'ca', autoclose: true});
	</script>
</body>
