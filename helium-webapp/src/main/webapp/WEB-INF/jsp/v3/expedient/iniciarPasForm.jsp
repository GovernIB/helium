<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code='expedient.iniciar.iniciar_expedient' />: ${tasca.nom}</title>
	<meta name="capsaleraTipus" content="llistat"/>
	<script type="text/javascript" src="<c:url value="/js/jquery.keyfilter.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/helium.tramitar.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
	<hel:modalHead/>
	<style>
		body {background-image: none; padding-top: 0px;}
		.alert {margin-top: 20px;}
		input, select, textarea {
			width: 100%;
		}
		.form-group {
			padding-right: 	15px;
			margin-left: 	0px !important;
			margin-bottom:	15px;
		}
		.controls {
			padding-right: 0 !important;
		}
		.form-group input, .form-group textarea {
			width: 100%;
		}
		.input-group-multiple {padding-left: 15px;padding-right: 15px;}
		.form-group li > .select2-container {
			width: 100%;
			padding-right: 20px;
		}
		.form-group .select2-container {
			width: calc(100% + 14px);
		}
		.pad-left-col-xs-3 {left: 25%;}
		.form-group.condensed {
			margin-bottom: 0px;
		}
		.form-group.registre .btn_afegir{
 			margin-top: 10px; 
		}
		.registre table .colEliminarFila {
			width: 1px;
		}
		.registre table .opciones {
			text-align: center;
		}
		p.help-block {
			padding-top: 0;	
			margin-top: 4px !important;
		}
		.clear {
			clear: both;
		}
		.clearForm {
			clear: both;
			margin-bottom: 10px;
			border-bottom: solid 1px #EAEAEA;
		}
		.input-append {
			width: calc(100% - 27px);
		}
		.eliminarFila {
			padding: 0px;	
		}
		.btn_eliminar {
			padding: 0px;	
		}
		.tercpre {
			padding-left: 0px !important;
			padding-right: 8px !important;
		}
		.tercmig {
			padding-left: 4px !important;
			padding-right: 4px !important;
		}
		.tercpost {
			padding-left: 8px !important;
			padding-right: 0px !important;
		}
		.table {margin-bottom: 0px;}
		.col-xs-9 .checkbox {width: auto;}
		#tabnav .glyphicon {padding-right: 10px;}
	</style>
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
	<c:if test="${not empty tasca.formExtern}">
		<form action="iniciarPasForm" onclick="return clickFormExtern(this)">
			<input type="hidden" name="id" value="${tasca.id}"/>
			<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
			<button type="submit" class="submitButton"><spring:message code='tasca.form.obrir_form' /></button>
		</form><br/>
	</c:if>

	<form:form id="command" name="command" onsubmit="return confirmarInicio(this)" action="iniciarPasForm" cssClass="form-horizontal form-tasca" method="post">
		<form:hidden path="id"/>
		<form:hidden path="entornId"/>
		<form:hidden path="expedientTipusId"/>
		<form:hidden path="definicioProcesId"/>
		<c:if test="${(empty tasca.formExtern) or (not empty tasca.formExtern and tasca.validada)}">
			<c:forEach var="dada" items="${dades}" varStatus="varStatusMain">
				<c:set var="inline" value="${false}"/>
				<c:set var="isRegistre" value="${false}"/>
				<c:set var="isMultiple" value="${false}"/>
				<c:choose>
					<c:when test="${dada.campTipus != 'REGISTRE'}">
						<c:choose>
							<c:when test="${dada.campMultiple}">
								<div class="multiple">
									<label for="${dada.varCodi}" class="control-label col-xs-3<c:if test="${dada.required}"> obligatori</c:if>">${dada.campEtiqueta} - ${dada.campTipus}</label>
									<c:forEach var="membre" items="${dada.multipleDades}" varStatus="varStatusCab">
										<c:set var="inline" value="${true}"/>
										<c:set var="campCodi" value="${dada.varCodi}[${varStatusCab.index}]"/>
										<div class="col-xs-9 input-group-multiple <c:if test="${varStatusCab.index != 0}">pad-left-col-xs-3</c:if>">
											<c:set var="isMultiple" value="${true}"/>
											<%@ include file="../campsTasca.jsp" %>
											<c:set var="isMultiple" value="${false}"/>
										</div>
									</c:forEach>
									<c:if test="${empty dada.multipleDades}">
										<c:set var="inline" value="${true}"/>
										<c:set var="campCodi" value="${dada.varCodi}[0]"/>
										<div class="col-xs-9 input-group-multiple">
											<c:set var="isMultiple" value="${true}"/>
											<%@ include file="../campsTasca.jsp" %>
											<c:set var="isMultiple" value="${false}"/>
										</div>
									</c:if>
									<c:if test="${!dada.readOnly && !tasca.validada}">
										<div class="form-group">
											<div class="col-xs-9 pad-left-col-xs-3">
												<c:if test="${not empty dada.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p></c:if>
												<button id="button_add_var_mult_${campCodi}" type="button" class="btn btn-default pull-left btn_afegir btn_multiple"><spring:message code='comuns.afegir' /></button>
											</div>
										</div>
									</c:if>
								</div>
							</c:when>
							<c:otherwise>
								<c:set var="campCodi" value="${dada.varCodi}"/>
								<%@ include file="../campsTasca.jsp" %>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<%@ include file="../campsTascaRegistre.jsp" %>
					</c:otherwise>
				</c:choose>
				<c:if test="${not varStatusMain.last}"><div class="clearForm"></div></c:if>
			</c:forEach>
		</c:if>
		<div id="modal-botons">
			<button type="button" class="modal-tancar btn btn-default" name="submit" value="cancel">
				<spring:message code='comuns.cancelar' />
			</button>			
			<button type="submit" id="iniciar" name="accio" class="btn btn-primary" value="iniciar" onclick="accioInici=this.value">
				<spring:message code='comuns.iniciar' />
			</button>
		</div>
	</form:form>
</body>
</html>
