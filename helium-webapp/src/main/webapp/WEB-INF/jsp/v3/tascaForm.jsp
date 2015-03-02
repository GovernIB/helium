<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="numColumnes" value="${3}"/>
<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
<c:if test="${not tasca.validada}">
	<div class="alert alert-warning">
		<c:choose>
			<c:when test="${empty tasca.tascaFormExternCodi}">
				<button type="button" class="close" data-dismiss="alert" aria-label="<spring:message code="comu.boto.tancar"/>"><span aria-hidden="true">&times;</span></button>
				<p>
					<span class="fa fa-warning"></span>
					<spring:message code="tasca.tramitacio.form.no.validat"/>
				</p>
			</c:when>
			<c:otherwise>
				<p>
					<span class="fa fa-warning"></span>
					<spring:message code="tasca.form.compl_form"/>
					<a id="boto-formext" href="<c:url value="/v3/expedient/${tasca.expedientId}/tasca/${tasca.id}/formExtern"/>" class="btn btn-xs btn-default pull-right"><span class="fa fa-external-link"></span> <spring:message code="tasca.form.obrir_form"/></a>
				</p>
			</c:otherwise>
		</c:choose>
	</div>
</c:if>
<%--c:if test="${not empty tasca.tascaFormExternCodi}">	
	<script type="text/javascript" src="<c:url value="/dwr/interface/formulariExternDwrService.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript">
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
	</script>
	<div class="form-group">
		<form id="formExtern" action="formExtern" class="form-horizontal form-tasca" onclick="return clickFormExtern(this)">
			<input type="hidden" name="id" value="${tasca.id}"/>
			<div id="modal-botons-form-extern" class="pull-right form_extern">
				<button type="submit" id="btn_formextern" name="accio" value="formextern" class="btn btn-default"><span class="fa fa-pencil-square-o"></span>&nbsp;<spring:message code='tasca.form.obrir_form' /></button>
			</div>								
			<a id="linkClickFormExtern" data-rdt-link-modal="true" data-rdt-link-modal-min-height="400" data-rdt-link-callback="recargarPanel(this);" href="#" class="hide"></a>
			<script type="text/javascript">
				$('#linkClickFormExtern').heliumEvalLink({
					refrescarAlertes: true,
					refrescarPagina: false,
					alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>"
				});
				function recargarPanel(tag, correcte) {
					if (correcte) {
						location.reload();
					}
				}
			</script>
		</form>
	</div>
</c:if--%>
	
<c:choose>
	<c:when test="${isModal}"><c:url var="tascaFormAction" value="/modal/v3/expedient/${tasca.expedientId}/tasca/${tasca.id}"/></c:when>
	<c:otherwise><c:url var="tascaFormAction" value="/modal/v3/expedient/${tasca.expedientId}/tasca/${tasca.id}"/></c:otherwise>
</c:choose>

<form:form onsubmit="return confirmar(this)" action="${tascaFormAction}" cssClass="form-horizontal form-tasca" method="post" commandName="command">
	<input type="hidden" id="tascaId" name="tascaId" value="${tasca.id}">
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
</form:form>
<%--</body>
</html>--%>