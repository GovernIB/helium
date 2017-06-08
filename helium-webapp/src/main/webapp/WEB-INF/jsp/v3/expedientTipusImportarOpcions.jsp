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

			<c:if test="${avisImportacioExpedientTipusDiferent}">
				<p class="help-block" style="color: #8a6d3b">
					<span class="fa fa-exclamation-triangle"></span> 
					<spring:message code="expedient.tipus.importar.form.avis.codi.diferent" arguments="${exportacio.codi},${exportacio.nom}"></spring:message>
				</p>
			</c:if>

			<c:if test="${avisResonsableNoTrobat}">
				<p class="help-block" style="color: #8a6d3b">
					<span class="fa fa-exclamation-triangle"></span> 
					<spring:message code="expedient.tipus.importar.form.avis.responsable.no.trobat" arguments="${exportacio.responsableDefecteCodi}"></spring:message>
				</p>
			</c:if>

			
			<c:if test="${empty command.id}">
				<hel:inputText name="codi" textKey="expedient.tipus.importar.form.camp.codi" required="true" labelSize="2"/> 	
			</c:if>

			<%@include file="expedientTipusExportarOpcions.jsp"%>		

		</c:if>

		<input type="hidden" id="redireccioUrl" value="${redireccioUrl}"/>
		<input type="hidden" id="importacioFinalitzada" value="${importacioFinalitzada}"/>
	
	</form:form>	
		
	