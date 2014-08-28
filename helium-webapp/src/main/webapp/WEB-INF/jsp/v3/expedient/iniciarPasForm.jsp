<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<html>
<head>
	<title><spring:message code='expedient.iniciar.iniciar_expedient' />: ${expedientTipus.nom}</title>
	<meta name="capsaleraTipus" content="llistat"/>
	<script type="text/javascript" src="<c:url value="/js/jquery.keyfilter.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/helium.tramitar.js"/>"></script>
	<hel:modalHead/>
	<style>
		body {background-image: none; padding-top: 0px;}
	</style>
	<script type="text/javascript">
	// <![CDATA[		
		var accioInici;
		function confirmarInicio(e) {
			if (!e) var e = window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			if ("cancel" == submitAction || "guardar" == submitAction) {
				return true;
			}
	
			$("table").each(function(){
				if ($(this).hasClass("hide")) {
					$(this).remove();
				}
			});
			
			<c:choose>
				<c:when test="${not ((expedientTipus.teNumero and expedientTipus.demanaNumero) or (expedientTipus.teTitol and expedientTipus.demanaTitol))}">
					return confirm("<spring:message code='expedient.iniciar.confirm_iniciar' />");
				</c:when>
				<c:otherwise>
					return true
				</c:otherwise>
			</c:choose>
		}
	// ]]>
	</script>
	<c:if test="${not empty tasca.formExtern}">
		<script type="text/javascript" src="<c:url value="/dwr/interface/formulariExternDwrService.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript">
	// <![CDATA[
		function clickFormExtern(form) {
			formulariExternDwrService.dadesIniciFormulariInicial(
					form.id.value,
					'${expedientTipus.id}',
					<c:choose><c:when test="${not empty definicioProces.id}">'${definicioProces.id}'</c:when><c:otherwise>null</c:otherwise></c:choose>,
					{
						callback: function(retval) {
							if (retval) {
								$('<iframe id="formExtern" src="' + retval[0] + '"/>').dialog({
									title: '<spring:message code="tasca.form.dades_form" />',
					                autoOpen: true,
					                modal: true,
					                autoResize: true,
					                width: parseInt(retval[1]),
					                height: parseInt(retval[2]),
					                close: function() {
										form.submit();
									}
					            }).width(parseInt(retval[1]) - 30).height(parseInt(retval[2]) - 30);
							} else {
								alert("<spring:message code='tasca.form.error_ini' />");
							}
						},
						async: false
					});
			return false;
		}
		
		function editarRegistre(campId, campCodi, campEtiqueta, numCamps, index) {
			var amplada = 686;
			var alcada = 64 * numCamps + 80;
			var url = "registre.html?id=${tasca.id}&registreId=" + campId;
			if (index != null)
				url = url + "&index=" + index;
			$('<iframe id="' + campCodi + '" src="' + url + '" frameborder="0" marginheight="0" marginwidth="0"/>').dialog({
				title: campEtiqueta,
				autoOpen: true,
				modal: true,
				autoResize: true,
				width: parseInt(amplada),
				height: parseInt(alcada)
			}).width(amplada - 30).height(alcada - 30);
			return false;
		}
	// ]]>
	</script>
	</c:if>
</head>
<body>
	<h3 class="titol-tab titol-dades-tasca"><i class="fa fa-file-text-o"></i> ${tasca.nom}</h3>

	<c:if test="${not empty tasca.formExtern}">
		<form action="iniciarPasForm" onclick="return clickFormExtern(this)">
			<input type="hidden" name="id" value="${tasca.id}"/>
			<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
			<button type="submit" class="submitButton"><spring:message code='tasca.form.obrir_form' /></button>
		</form><br/>
	</c:if>

	<form:form id="command" name="command" onsubmit="return confirmarInicio(this)" action="iniciarPasForm" cssClass="form-horizontal form-tasca" method="post">
		<input type="hidden" id="id" name="id" value="${tasca.id}"/>
		<input type="hidden" name="entornId" value="${entornId}"/>
		<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
		<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>
		<c:if test="${(empty tasca.formExtern) or (not empty tasca.formExtern and tasca.validada)}">
			<c:forEach var="dada" items="${dades}" varStatus="varStatusMain">
				<c:if test="${not dada.readOnly}">
					<div class="control-group fila_reducida <c:if test='${dada.readOnly || tasca.validada}'>fila_reducida</c:if>">
						<label class="control-label" for="${dada.varCodi}">${dada.campEtiqueta} - ${dada.campTipus}</label>
						
						<c:set var="dada" value="${dada}"/>
						<%@ include file="../campsTasca.jsp" %>
						<%@ include file="../campsTascaRegistre.jsp" %>
					</div>
				</c:if>
			</c:forEach>
		</c:if>
		<div id="modal-botons">
			<button type="button" class="modal-tancar btn" name="submit" value="cancel">
				<spring:message code='comuns.cancelar' />
			</button>			
			<button type="submit" id="iniciar" name="accio" class="btn btn-primary" value="iniciar" onclick="accioInici=this.value">
				<spring:message code='comuns.iniciar' />
			</button>
		</div>
	</form:form>

	<div style="clear: both;"></div>
	<p class="aclaracio"><spring:message code='comuns.camps_marcats' /> <i class="fa fa-asterisk"></i> <spring:message code='comuns.son_oblig' /></p>
</body>
</html>
