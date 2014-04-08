<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>
<head>
	<title><spring:message code='expedient.info.informacio' /></title>
	<meta name="capsaleraTipus" content="llistat"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
</head>
<body>
	<form:form cssClass="form-horizontal form-tasca" id="modificarInformacio" name="modificarInformacio" action="modificarInformacio" method="post" commandName="expedientEditarCommand">
		<c:if test="${expedient.tipus.teNumero}">
			<div class="control-group fila_reducida">
				<label class="control-label"><spring:message code='expedient.consulta.numero' /></label>
				<div class="controls">
					<c:set var="campPath" value="numero"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="<c:if test="${not empty campErrors}"> error</c:if>">
						<spring:bind path="${campPath}">
							<input data-required="true" type="text" id="${campPath}" name="${campPath}" placeholder="<spring:message code='expedient.consulta.numero' />"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span11">
						</spring:bind>
					</div>
				</div>
			</div>
		</c:if>
		<c:if test="${expedient.tipus.teTitol}">
			<div class="control-group fila_reducida">
				<label class="control-label"><spring:message code='expedient.consulta.titol' /></label>
				<div class="controls">
					<c:set var="campPath" value="titol"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<div class="<c:if test="${not empty campErrors}"> error</c:if>">
						<spring:bind path="${campPath}">
							<input data-required="true" type="text" id="${campPath}" name="${campPath}" placeholder="<spring:message code='expedient.consulta.titol' />"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span11">
						</spring:bind>
					</div>
				</div>
			</div>
		</c:if>
		<div class="control-group fila_reducida">
			<label class="control-label"><spring:message code='expedient.consulta.datainici' /></label>
			<div class="controls">
				<div class="input-append date datepicker">
					<c:set var="campPath" value="dataInici"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<spring:bind path="${campPath}">
						<input type="text" id="${campPath}" name="${campPath}" placeholder="dd/mm/yyyy"<c:if test="${not empty status.value}"> value="<fmt:formatDate value="${expedientEditarCommand.dataInici}" pattern="dd/MM/yyyy HH:mm"/>"</c:if> class="span6">
					</spring:bind>
					<span class="add-on"><i class="icon-calendar"></i></span>
				</div>
				<script>$("#${campPath}").mask("99/99/9999");</script>
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
					<c:param name="suggestText">${expedientEditarCommand.responsableNomSencer}</c:param>
					<c:param name="value" value="${expedientEditarCommand.responsableCodi}"/>
				</c:import>
			</div>
		</div>
		<div class="control-group fila_reducida">
			<label class="control-label"><spring:message code='expedient.editar.comentari' /></label>
			<div class="controls">
				<c:set var="campPath" value="comentari"/>
				<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
				<div class="<c:if test="${not empty campErrors}"> error</c:if>">
					<spring:bind path="${campPath}">
						<textarea id="${campPath}" name="${campPath}" placeholder="<spring:message code='expedient.editar.comentari' />" class="span11"><c:if test="${not empty status.value}">${status.value}</c:if></textarea>
					</spring:bind>
				</div>
			</div>
		</div>
		<div class="control-group fila_reducida">
			<label class="control-label"><spring:message code='expedient.consulta.select.estat' /></label>
			<div class="controls">
				<c:set var="campPath" value="estatId"/>
				<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
				<form:select id="estatId" name="estatId" path="${campPath}" cssClass="span11">
					<option value=""><spring:message code='expedient.consulta.select.consula'/></option>
					<form:options items="${estats}" itemLabel="nom" itemValue="id"/>
				</form:select>
			</div>
		</div>
		<div class="control-group fila_reducida">
			<c:choose>
				<c:when test="${globalProperties['app.georef.actiu']}">
					<c:choose>
						<c:when test="${globalProperties['app.georef.tipus']=='ref'}">
							<label class="control-label"><spring:message code='comuns.georeferencia.codi' /></label>
							<div class="controls">
								<c:set var="campPath" value="geoReferencia"/>
								<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
								<spring:bind path="${campPath}">
									<input type="text" id="${campPath}" name="${campPath}" placeholder="<spring:message code='comuns.georeferencia.codi' />"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span11">
								</spring:bind>
							</div>
						</c:when>
						<c:otherwise>
							<label class="control-label"><spring:message code='comuns.georeferencia.codi' /></label>
							<div class="controls">
								<div class="row-fluid">
									<div class="span4">
										<c:set var="campPath" value="geoPosX"/>
										<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
										<spring:bind path="${campPath}">
											<input type="text" id="${campPath}" name="${campPath}" placeholder="<spring:message code='comuns.georeferencia.coordenadaX' />"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span12">
										</spring:bind>
									</div>
									<div class="span4">
										<c:set var="campPath" value="geoPosY"/>
										<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
										<spring:bind path="${campPath}">
											<input type="text" id="${campPath}" name="${campPath}" placeholder="<spring:message code='comuns.georeferencia.coordenadaY' />"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span12">
										</spring:bind>
									</div>
								</div>
							</div>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<div class="span4"></div>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="control-group fila_reducida">
			<label class="control-label"><spring:message code='expedient.editar.grup_codi' /></label>
			<div class="controls">
				<c:set var="campPath" value="grupCodi"/>
				<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
				<spring:bind path="${campPath}">
					<input type="text" id="${campPath}" name="${campPath}" placeholder="<spring:message code='expedient.editar.grup_codi' />"<c:if test="${not empty status.value}"> value="${status.value}"</c:if> class="span11">
				</spring:bind>
			</div>
		</div>
		<div id="formButtons">
			<button type="submit" class="btn btn-primary" id="submit" name="submit" value="submit">
				<spring:message code="comuns.modificar"/>
			</button>
		</div>
	</form:form>
 		
</body>
