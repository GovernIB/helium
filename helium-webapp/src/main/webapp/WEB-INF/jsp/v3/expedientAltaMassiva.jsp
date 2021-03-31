<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<script type="text/javascript">
// <![CDATA[         
$(document).ready(function() {

	
});
// ]]>
</script>	

<html>
<head>
	<title><spring:message code="expedient.alta.massiva.titol"/></title>
	<hel:modalHead/>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>		

	<form:form id="altaMassiva" cssClass="form-horizontal" action="altaMassiva" enctype="multipart/form-data" method="post" commandName="command">

		<div>
			<hel:inputSelect emptyOption="true" required="true" name="expedientTipusId" textKey="expedient.llistat.filtre.camp.expedient.tipus" placeholderKey="expedient.llistat.filtre.camp.expedient.tipus" optionItems="${expedientTipusPermesos}" optionValueAttribute="id" optionTextAttribute="nom" />

			<div class="form-group">
				<label class="control-label col-xs-4 obligatori" for="file">
					<spring:message code="expedient.alta.massiva.form.file"/>
					<span class="fa fa-info-circle text-info checkbox_hiddenInfo" data-placement="auto" title="<spring:message code="expedient.alta.massiva.form.file.info"/>"></span>
				</label>
				<div class="col-xs-8">
					<input type="file" name="file" id="file" accept=".csv" />
					<c:set var="fileErrors"><form:errors path="file"/></c:set>
					<c:if test="${not empty fileErrors}">
						<div class="has-error">
							<p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="file"/></p>
						</div>
					</c:if>
				</div>
			</div>
			
		</div>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default right" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<button type="submit" class="btn btn-primary right" name="accio" value="altaMassiva"><i class="fa fa-file-o"></i> <spring:message code="expedient.iniciar.alta.csv"></spring:message></button>
		</div>

	</form:form>
	
	<script type="text/javascript">
		// <![CDATA[
		$(document).ready( function() {
		}); 
		// ]]>
	</script>	
</body>
</html>