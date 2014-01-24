<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>
<head>
	<title><spring:message code='expedient.info.informacio' /></title>
	<c:import url="../utils/modalHead.jsp">
		<c:param name="titol" value="Modificar informació de l'expedient"/>
		<c:param name="buttonContainerId" value="botons"/>
	</c:import>
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>
	<jsp:include page="../import/helforms.jsp" />
	<form:form cssClass="form-horizontal form-tasca" id="modificarInformacio" name="modificarInformacio" action="modificarInformacio" method="post" commandName="expedientEditarCommand">
		<div class="modal-body">
			<input type="hidden" name="id" value="${param.id}"/>
			<c:if test="${expedient.tipus.teNumero}">
				<div class="control-group fila_reducida">
					<label class="control-label"><spring:message code='expedient.consulta.numero' /></label>
					<div class="controls">
						<input type="text" class="span3 input-block-level" id="numero" name="numero" value="${expedient.numero}">
					</div>
				</div>
			</c:if>
			<c:if test="${expedient.tipus.teTitol}">
				<div class="control-group fila_reducida">
					<label class="control-label"><spring:message code='expedient.consulta.titol' /></label>
					<div class="controls">
						<input type="text" class="span3 input-block-level" id="titol" name="titol" value="${expedient.titol}">
					</div>
				</div>
			</c:if>
			<div class="control-group fila_reducida">
				<label class="control-label"><spring:message code='expedient.consulta.datainici' /></label>
				<div class="span5 input-append date datepicker input-append-form">
					<input class="input-block-level span3" type="text" id="dataInici" name="dataInici" data-format="dd/MM/yyyy" placeholder="dd/mm/yyyy" <c:if test="${not empty expedient.dataInici}"> value="<fmt:formatDate value="${expedient.dataInici}" pattern="dd/MM/yyyy"/>"</c:if>>
					<span class="add-on"><i class="icon-calendar"></i></span>
				</div>
			</div>
			<div class="control-group fila_reducida">
				<label class="control-label"><spring:message code='expedient.editar.responsable' /></label>
				<div class="controls">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="responsableCodi"/>
						<c:param name="type" value="suggest"/>
						<c:param name="label"><spring:message code='expedient.editar.responsable' /></c:param>
						<c:param name="suggestUrl"><c:url value="persona/suggest"/></c:param>
						<c:param name="suggestText">${expedient.responsablePersona.nomSencer}</c:param>
						<c:param name="value" value="${expedient.responsablePersona.codi}"/>
					</c:import>
				</div>
			</div>
			<div class="control-group fila_reducida">
				<label class="control-label"><spring:message code='expedient.editar.comentari' /></label>
				<div class="controls">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="comentari"/>
						<c:param name="type" value="textarea"/>
						<c:param name="label"><spring:message code='expedient.editar.comentari' /></c:param>
						<c:param name="value" value="${expedient.comentari}"/>
					</c:import>
				</div>
			</div>
			<div class="control-group fila_reducida">
				<label class="control-label"><spring:message code='expedient.consulta.select.estat' /></label>
				<div class="controls">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="estatId"/>
						<c:param name="type" value="select"/>
						<c:param name="items" value="estats"/>
						<c:param name="itemLabel" value="nom"/>
						<c:param name="itemValue" value="id"/>
						<c:param name="itemBuit">&lt;&lt; <spring:message code='expedient.consulta.select.estat' /> &gt;&gt;</c:param>
						<c:param name="label"><spring:message code='expedient.editar.estat' /></c:param>
						<c:param name="value" value="${expedient.estat.nom}"/>
					</c:import>
				</div>
			</div>
			<div class="control-group fila_reducida">
				<c:if test="${globalProperties['app.georef.actiu']}">
					<c:choose>
						<c:when test="${globalProperties['app.georef.tipus']=='ref'}">
							<label class="control-label"><spring:message code='comuns.georeferencia.codi' /></label>
							<div class="controls">
								<c:import url="../common/formElement.jsp">
									<c:param name="property" value="geoReferencia"/>
									<c:param name="label"><spring:message code='comuns.georeferencia.codi' /></c:param>
									<c:param name="value" value="${expedient.geoReferencia}"/>
								</c:import>
							</div>
						</c:when>
						<c:otherwise>
							<label class="control-label"><spring:message code='comuns.georeferencia.coordenadaX' /></label>
							<div class="controls">
								<c:import url="../common/formElement.jsp">
									<c:param name="property" value="geoPosX"/>
									<c:param name="type" value="number"/>
									<c:param name="keyfilter">/[\d\-\.]/</c:param>
									<c:param name="label"><spring:message code='comuns.georeferencia.coordenadaX' /></c:param>
									<c:param name="value" value="${expedient.geoPosX}"/>
								</c:import>
							</div>
							<div class="controls">
								<label class="control-label"><spring:message code='comuns.georeferencia.coordenadaY' /></label>
								<c:import url="../common/formElement.jsp">
									<c:param name="property" value="geoPosY"/>
									<c:param name="type" value="number"/>
									<c:param name="keyfilter">/[\d\-\.]/</c:param>
									<c:param name="label"><spring:message code='comuns.georeferencia.coordenadaY' /></c:param>
									<c:param name="value" value="${expedient.geoPosY}"/>									
								</c:import>
							</div>
						</c:otherwise>
					</c:choose>
				</c:if>
			</div>
			<div class="control-group fila_reducida">
				<label class="control-label"><spring:message code='expedient.editar.grup_codi' /></label>
				<div class="controls">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="grupCodi"/>
						<c:param name="required" value="false"/>
						<c:param name="label"><spring:message code='expedient.editar.grup_codi' /></c:param>
						<c:param name="value" value="${expedient.grupCodi}"/>
					</c:import>
				</div>
			</div>
		</div>
		<div style="clear: both"></div>
		<div class="pull-right">
			<button type="submit" class="btn btn-primary"><spring:message code="comuns.modificar"/></button>
			<button type="button" class="btn btn-tancar"><spring:message code="comuns.cancelar"/></button>
		</div>
	</form:form>
	<script>
		$('.datepicker').datepicker({language: 'ca', autoclose: true});
	</script>
</body>
