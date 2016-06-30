<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code='expedient.termini.modificar' />: ${expedientTerminiModificarCommand.nom}</title>
	<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/helium.datatable.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	
	<hel:modalHead/>
	<meta name="decorator" content="senseCapNiPeus"/>
	<script type="text/javascript">
	// <![CDATA[
		$(document).ready(function() {
			$('#tipus').on('change', function() {
				$("#terminiDurada").hide();
				$("#terminiDataInici").hide();
				$("#terminiDataFi").hide();
				if ("DURADA" == this.value) {
					$("#terminiDurada").show();
				} else if ("DATA_INICI" == this.value) {
					$("#terminiDataInici").show();
				} else if ("DATA_FI" == this.value) {
					$("#terminiDataFi").show();
				}
			});
			$('#tipus').trigger('change');
		});
	// ]]>
	</script>
	<style>
		#expedientTerminiModificarCommand {height: 150px;}
		.terminiDurada {padding-left: 0px;padding-right: 0px;}
		.bigdrop.select2-container .select2-results {max-height: 65px;}
		.bigdrop .select2-results {max-height: 65px;}
		.bigdrop .select2-choices {min-height: 65px; max-height: 65px; overflow-y: auto;}
		#tipus_termini {padding-bottom: 17px;}
	</style>
</head>
<body>
	<form:form action="modificar" commandName="expedientTerminiModificarCommand">
		<div class="inlineLabels">
			<div id="tipus_termini">
				<label><spring:message code="expedient.termini.modificar.tipus"/></label>
				<form:select multiple="false" itemLabel="codi" itemValue="valor" items="${listTipus}" path="tipus" id="tipus" />
			</div>
			<div id="terminiDurada">
				<label><spring:message code="expedient.termini.durada"/></label>
				<div class="form-group">
					<div class="col-xs-4 tercpre">
						<label class="control-label col-xs-4" for="anys"><spring:message code="common.camptasca.anys"/></label>
						<div class="col-xs-8">
							<form:select itemLabel="valor" itemValue="codi" items="${listTerminis}" path="anys" id="anys" />
						</div>
					</div>
					<div class="col-xs-4 tercmig">
	 					<label class="control-label col-xs-4" for="mesos"><spring:message code="common.camptasca.mesos"/></label>
	 					<div class="col-xs-8">
	 						<form:select itemLabel="valor" itemValue="codi" items="${listTerminis}" path="mesos" id="mesos" />
	 					</div>
	 				</div>
	 				<div class="col-xs-4 tercpost">
	 					<label class="control-label col-xs-4" for="dies"><spring:message code="common.camptasca.dies"/></label>
	 					<div class="col-xs-8">
	 						<hel:inputText inline="true" name="dies" textKey="common.camptasca.dies" placeholderKey="common.camptasca.dies"/>
	 					</div>
	 				</div>
	 			</div>
 				<script>
					$(document).ready(function() {
						$("#tipus").select2({
						    width: '100%',
						    minimumResultsForSearch: -1,
						    allowClear: true
						});
						$("#anys").select2({
						    width: '100%',
						    allowClear: true,
						    minimumResultsForSearch: -1,
						    dropdownCssClass: "bigdrop"
						});
						$("#mesos").select2({
							width: '100%',
						    minimumResultsForSearch: -1,
						    allowClear: true,
						    dropdownCssClass: "bigdrop"
						});
					});
				</script>
			</div>
			<div id="terminiDataInici">
				<label><spring:message code="expedient.termini.data.inici"/></label>
				<hel:inputDate inline="true" required="true" name="dataInici" textKey="expedient.termini.data.inici" placeholderKey="expedient.termini.data.inici" placeholder="dd/MM/aaaa"/>
			</div>
			<div id="terminiDataFi">
				<label><spring:message code="expedient.termini.data.fi"/></label>
				<hel:inputDate inline="true" required="true" name="dataFi" textKey="expedient.termini.data.fi" placeholderKey="expedient.termini.data.fi" placeholder="dd/MM/aaaa"/>
			</div>
		</div>
		<div id="modal-botons">
			<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel">
				<spring:message code="comu.boto.cancelar"/>
			</button>
			<button type="submit" class="btn btn-primary" id="submit" name="submit" value="submit">
				<span class="fa fa-pencil"></span>&nbsp;<spring:message code="comuns.modificar"/>
			</button>
		</div>
	</form:form>
</body>
</html>
