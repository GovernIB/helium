<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:set var="titol"><spring:message code="expedient.tipus.accio.desplegar.form.titol" arguments="${expedientTipus.codi}, ${expedientTipus.nom}"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>		
	<form:form id="desplegar-form" cssClass="form-horizontal" action="desplegar" enctype="multipart/form-data" method="post" commandName="command" style="min-height: 200px;">

		<input type="hidden" name="entornId" id="entornId" value="${command.entornId}" />
		<input type="hidden" name="expedientTipusId" id="expedientTipusId" value="${command.expedientTipusId}" />
		<div class="form-group">
			<label class="control-label col-xs-4 obligatori" for="file"><spring:message code="definicio.proces.desplegar.form.file"/></label>
			<div class="col-xs-8">
				<input type="file" name="file" id="file" />
				<c:set var="fileErrors"><form:errors path="file"/></c:set>
				<c:if test="${not empty fileErrors}">
					<div class="has-error">
						<p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="file"/></p>
					</div>
				</c:if>
			</div>
		</div>

		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<button id="desplegarButton" type="submit" class="btn btn-success right">
				<span class="fa fa-download"></span> <spring:message code="comu.filtre.desplegar"/>
			</button>
		</div>
	</form:form>
</body>
</html>