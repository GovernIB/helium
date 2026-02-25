<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

	<form:form id="opcions-form" cssClass="form-horizontal" action="importar" enctype="multipart/form-data" method="post" commandName="command" style="min-height: 500px;">
	
		<c:set var="fileErrors"><form:errors path="file"/></c:set>
		<c:if test="${not empty fileErrors}">
			<div class="form-group has-error" style="margin-left: 60px;">
				<p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="file"/></p>
			</div>
		</c:if>

		<c:if test="${fitxerImportat}">

			<c:if test="${avisImportacioDefinicioProcesDiferent}">
				<p class="help-block" style="color: #8a6d3b">
					<span class="fa fa-exclamation-triangle"></span> 
					<spring:message code="definicio.proces.importar.form.avis.codi.diferent" arguments="${exportacio.definicioProcesDto.jbpmKey}, ${exportacio.definicioProcesDto.etiqueta}, ${exportacio.definicioProcesDto.versio}"></spring:message>
				</p>
			</c:if>
			<c:if test="${empty command.id}">
				<div class="row">
					<div class="col-sm-9">
						<input type="hidden" name="codi" id="codi" value="${command.codi}" />
						<hel:inputText name="codi" disabled="true" textKey="definicio.proces.importar.form.camp.codi" labelSize="2"/> 	
					</div>
					<div class="col-sm-3">
						<input type="hidden" name="versio" id="versio" value="${command.versio}" />
						<hel:inputText name="versio" disabled="true" textKey="definicio.proces.importar.form.camp.versio" labelSize="4"/> 	
					</div>
				</div>
			</c:if>

			<%@include file="definicioProcesExportarOpcions.jsp"%>		

		</c:if>

		<input type="hidden" id="redireccioUrl" value="${redireccioUrl}"/>
		<input type="hidden" id="importacioFinalitzada" value="${importacioFinalitzada}"/>
	
	</form:form>	
		
	