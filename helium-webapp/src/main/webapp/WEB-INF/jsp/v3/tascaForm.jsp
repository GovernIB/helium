<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="numColumnes" value="${3}"/>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title>${tasca.titol}</title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/helium.tramitar.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
	<link href="<c:url value="/css/tascaForm.css"/>" rel="stylesheet"/>
</head>
<body>
	<c:if test="${not tasca.validada}">
		<div class="alert alert-warning">
			<button class="close" data-dismiss="alert">×</button>
			<c:choose>
				<c:when test="${empty tasca.formExtern}">
					<p><spring:message code='tasca.form.no_validades' /></p>
				</c:when>
				<c:otherwise>
					<p><spring:message code='tasca.form.compl_form' /></p>
				</c:otherwise>
			</c:choose>
		</div>
	</c:if>
	<c:if test="${not empty tasca.formExtern}">	
		<script type="text/javascript" src="<c:url value="/dwr/interface/formulariExternDwrService.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
		<script type="text/javascript">
		// <![CDATA[
			function clickFormExtern(form) {
				formulariExternDwrService.dadesIniciFormulari(
						form.id.value,
						{
							callback: function(retval) {
								if (retval) {
									$("#linkClickFormExtern").attr('href', '<c:url value='../../../../../v3/expedient/formExtern'/>?width='+retval[1]+'&height='+retval[2]+'&url='+retval[0]).click();
								} else {
									alert("<spring:message code='tasca.form.error_ini' />");
								}
							},
							async: false
						});
				return false;
			}
		// ]]>
		</script>
		<div class="form-group">
			<form id="formExtern" action="formExtern" class="form-horizontal form-tasca" onclick="return clickFormExtern(this)">
				<input type="hidden" name="id" value="${tasca.id}"/>
				<div id="modal-botons-form-extern" class="pull-right form_extern">
					<button type="submit" id="btn_formextern" name="accio" value="formextern" class="btn btn-default"><span class="fa fa-pencil-square-o"></span>&nbsp;<spring:message code='tasca.form.obrir_form' /></button>
				</div>								
				<a 	id="linkClickFormExtern" data-rdt-link-modal="true" data-rdt-link-modal-min-height="400" data-rdt-link-callback="recargarPanel(this);" href="#" class="hide"></a>
						
				<script type="text/javascript">
					// <![CDATA[
						$('#linkClickFormExtern').heliumEvalLink({
							refrescarAlertes: true,
							refrescarPagina: false,
							alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>"
						});

						function recargarPanel (tag, correcte) {
							if (correcte) {
								location.reload();
							}
						}
					//]]>
				</script>
			</form>
		</div>
	</c:if>
<c:choose>
	<c:when test="${isModal}"><c:url var="tascaFormAction" value="/modal/v3/expedient/${tasca.expedientId}/tasca/${tasca.id}/form"/></c:when>
	<c:otherwise><c:url var="tascaFormAction" value="/modal/v3/expedient/${tasca.expedientId}/tasca/${tasca.id}/form"/></c:otherwise>
</c:choose>
	<form:form onsubmit="return confirmar(this)" action="${tascaFormAction}" cssClass="form-horizontal form-tasca" method="post" commandName="command">
		<input type="hidden" id="tascaId" name="tascaId" value="${tasca.id}">
		<form:hidden path="inici"/>
		<form:hidden path="correu"/>
		<form:hidden path="numTascaMassiva"/>
		<input type="hidden" id="__transicio__" name="__transicio__" value=""/>
		<c:forEach var="dada" items="${dades}" varStatus="varStatusMain">
			<c:set var="inline" value="${false}"/>
			<c:set var="isRegistre" value="${false}"/>
			<c:set var="isMultiple" value="${false}"/>
			<c:choose>
				<c:when test="${dada.campTipus != 'REGISTRE'}">
					<c:choose>
						<c:when test="${dada.campMultiple}">
							<c:set var="campErrorsMultiple"><form:errors path="${dada.varCodi}"/></c:set>
							<div class="multiple<c:if test="${not empty campErrorsMultiple}"> has-error</c:if>">	
								<label for="${dada.varCodi}" class="control-label col-xs-3<c:if test="${dada.required}"> obligatori</c:if>">${dada.campEtiqueta}</label>
								<c:forEach var="membre" items="${command[dada.varCodi]}" varStatus="varStatusCab">
									<c:set var="inline" value="${true}"/>
									<c:set var="campCodi" value="${dada.varCodi}[${varStatusCab.index}]"/>
									<c:set var="campNom" value="${dada.varCodi}"/>
									<c:set var="campIndex" value="${varStatusCab.index}"/>
									<div class="col-xs-9 input-group-multiple <c:if test="${varStatusCab.index != 0}">pad-left-col-xs-3</c:if>">
										<c:set var="isMultiple" value="${true}"/>
										<%@ include file="campsTasca.jsp" %>
										<c:set var="isMultiple" value="${false}"/>
									</div>
								</c:forEach>
								<c:if test="${empty dada.multipleDades}">
									Buit!!
									<c:set var="inline" value="${true}"/>
									<c:set var="campCodi" value="${dada.varCodi}[0]"/>
									<c:set var="campNom" value="${dada.varCodi}"/>
									<c:set var="campIndex" value="0"/>
									<div class="col-xs-9 input-group-multiple">
										<c:set var="isMultiple" value="${true}"/>
										<%@ include file="campsTasca.jsp" %>
										<c:set var="isMultiple" value="${false}"/>
									</div>
								</c:if>
								<c:if test="${!dada.readOnly && !tasca.validada}">
									<div class="form-group">
										<div class="col-xs-9 pad-left-col-xs-3">
											<c:if test="${not empty dada.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p></c:if>
											<button id="button_add_var_mult_${campCodi}" type="button" class="btn btn-default pull-left btn_afegir btn_multiple"><spring:message code='comuns.afegir' /></button>
											<div class="clear"></div>
											<c:if test="${not empty campErrorsMultiple}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${dada.varCodi}"/></p></c:if>
										</div>
									</div>
								</c:if>
							</div>
						</c:when>
						<c:otherwise>
							<c:set var="campCodi" value="${dada.varCodi}"/>
							<c:set var="campNom" value="${dada.varCodi}"/>
							<%@ include file="campsTasca.jsp" %>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<%@ include file="campsTascaRegistre.jsp" %>
				</c:otherwise>
			</c:choose>
			<c:if test="${not varStatusMain.last}"><div class="clearForm"></div></c:if>
		</c:forEach>
		<div id="guardarValidarTarea">
			<%@ include file="campsTascaBotons.jsp" %>
		</div>
	</form:form>
</body>
</html>
