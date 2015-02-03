<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code='expedient.iniciar.iniciar_expedient' />: ${tasca.nom}</title>
	<meta name="capsaleraTipus" content="llistat"/>
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
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
	<hel:modalHead/>
	<style>
		.alert {margin-top: 20px;}
		input, select, textarea {
			width: 100%;
		}
		label {
			font-weight: normal;
		}
		.form-group {
			padding-right: 	15px;
			margin-left: 	0px !important;
			margin-bottom:	0px;
		}
		.controls {
			padding-right: 0 !important;
		}
		.form-group input, .form-group textarea {
			width: 100%;
		}
		.input-group-multiple {
			padding-left: 15px;
			padding-right: 0px;}
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
			margin-bottom:	0px;
		}
		.clear {
			clear: both;
		}
		.clearForm {
			clear: both;
			margin-bottom: 10px;
			margin-top: 15px;
			border-bottom: solid 1px #DADADA;
		}
		.input-append {
			width: calc(100% - 27px);
		}
		.eliminarFila {
			background-color: #FFF;
			border: solid 1px rgb(204, 204, 204);
			border-radius: 4px;
			padding: 9px 6px;
/* 			margin-top: 3px; */
		}
		.eliminarFila:hover {
			color: #333;
			background-color: #e6e6e6;
			border-color: #adadad;
		}
		.btn_eliminar {
			background-color: #FFF;
			border: solid 1px rgb(204, 204, 204);
			border-radius: 4px;
			padding: 9px 6px;
/* 			margin-top: 3px; */
		}
		.table {
			margin-bottom: 0px;
		}
		.col-xs-9 .checkbox {
			width: auto;
		}
		.form_extern {
			padding-bottom: 15px;
			width: 100%;
			text-align: right;
			margin-right: -15px;
		}
		#tabnav .glyphicon {
			padding-right: 10px;
		}
		div.tab-content {
			width: calc(100% - 15px);
			padding-bottom: 15px;
		}
		.multiple input, .multiple textarea, .multiple_camp .input-group, .multiple_camp .inputcheck {
			float: left;
			width: calc(100% - 29px);
		}
		.multiple_camp div.suggest, .multiple_camp div.seleccio {
			float: left;
			width: calc(100% - 29px) !important;
		}
		.registre .multiple input, .registre .multiple textarea, .registre .multiple_camp .input-group, .registre .multiple_camp .inputcheck {
			float: left;
			width: 100% !important;
		}
		.registre .multiple_camp div.suggest, .registre .multiple_camp div.seleccio {
			float: left;
			width: 100% !important;
		}
		.registre .multiple input.checkbox {
			width: auto !important;
		}
		.multiple .termgrup {
			float: left;
			width: calc(100% - 14px);
		}
		.termgrup input, .termgrup select {
			float: left;
 			width: calc(100% - 65px); 
		}
		.tercpre {
			float: left;
			width: 32%;
 			padding-left: 0px !important;
 			padding-right: 8px !important;
		}
		.tercmig {
			float: left;
			width: 32%;
 			padding-left: 4px !important; 
 			padding-right: 4px !important; 
		}
		.tercpost {
			float: left;
			width: 36%;
			padding-left: 8px !important; 
 			padding-right: 0px !important; 
		}
		.label-term {
			float: left;
			width: 60px;
			text-align: left !important;
			margin-right: 5px;
			font-weight: normal !important;
		}
		.multiple_camp .btn_eliminar {
			float: left;
			margin-left: 4px;
		}
		.form-group.multiple_camp {
			margin-bottom: 6px;		
		}
		.btn_date {
			cursor: pointer;
		}
		.has-error .form-control {
			background-color: #ffefe !important;
		}
		#contingut-alertes, .tauladades {
			padding-right: 15px;
		}
/* 		input, textarea, .select2-choice { */
/* 			background-color: #FFFCF0 !important; */
/* 		} */
/* 		.select2-arrow { */
/* 			background-color: #FFFFFF !important; */
/* 		} */
/* 		.registre table { */
/* 			border: solid 0px; */
			
/* 		} */
/* 		.registre tr td:last-child, .registre tr th:last-child { */
/* 			border: solid 0px; */
/* 			padding-right: 0px; */
/* 		} */
		.registre th {
			border-bottom: solid 1px #CACACA !important;
			background: rgba(221,221,221,1);
			background: -moz-linear-gradient(top, rgba(221,221,221,1) 0%, rgba(245,245,245,1) 100%);
			background: -webkit-gradient(left top, left bottom, color-stop(0%, rgba(221,221,221,1)), color-stop(100%, rgba(245,245,245,1)));
			background: -webkit-linear-gradient(top, rgba(221,221,221,1) 0%, rgba(245,245,245,1) 100%);
			background: -o-linear-gradient(top, rgba(221,221,221,1) 0%, rgba(245,245,245,1) 100%);
			background: -ms-linear-gradient(top, rgba(221,221,221,1) 0%, rgba(245,245,245,1) 100%);
			background: linear-gradient(to bottom, rgba(221,221,221,1) 0%, rgba(245,245,245,1) 100%);
			filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#dddddd', endColorstr='#f5f5f5', GradientType=0 );
		}
		.contingut-carregant {
			padding-top: 50px;
	    	text-align: center;
	    }
	</style>
	<script type="text/javascript">
	// <![CDATA[
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
</head>
<body>
	<div class="well">				
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
										$("#linkClickFormExtern").attr('href', '<c:url value='../../../v3/expedient/formExtern'/>?width='+retval[1]+'&height='+retval[2]+'&url='+retval[0]).click();
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
					<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
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
	
		<form:form id="command" name="command" onsubmit="return confirmarInicio(this)" action="iniciarPasForm" cssClass="form-horizontal form-tasca" method="post">
			<form:hidden path="id"/>
			<form:hidden path="entornId"/>
			<form:hidden path="expedientTipusId"/>
			<form:hidden path="definicioProcesId"/>
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
			<div id="modal-botons">
				<button type="button" class="modal-tancar btn btn-default" name="submit" value="cancel">
					<spring:message code='comuns.cancelar' />
				</button>			
				<button type="submit" id="iniciar" name="accio" class="btn btn-primary" value="iniciar" onclick="accioInici=this.value">
					<spring:message code='comuns.iniciar' />
				</button>
			</div>
		</form:form>
	</div>
</body>
</html>