<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code='alerta.llistat.expedient' />: ${expedientIdentificador}</title>
	<meta name="capsaleraTipus" content="llistat"/>
	<hel:modalHead/>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
		<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
		<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	
		<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
		<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
		<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
		<script src="<c:url value="/js/jsrender.min.js"/>"></script>
		<script src="<c:url value="/js/helium.datatable.js"/>"></script>
		<script src="<c:url value="/js/select2.min.js"/>"></script>
		<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
		
		<script src="<c:url value="/js/moment.js"/>"></script>
		<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
		<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
		<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
		
		<style>
			body {background-image: none; padding-top: 0px;}
			.col-xs-4 {width: auto;padding-left: 0px;}
			.col-xs-8 {width: 100%;padding-bottom: 100px;padding-left: 0px;padding-right: 0px;}
			#opciones .label-titol {padding-bottom: 0px;} 
	 		.control-group {width: 100%;display: inline-block;} 
	 		.control-group-mid {width: 48%;} 
	  		.control-group.left {float: left; margin-right:1%;} 
	  		.ctrlHolder {padding-top: 10px;}
	  		.select2-drop {left: 15px !important;right: 15px !important;}
	  		#div_timer label {float:left;} 
			.label-titol {background-color: #fefefe; border: 1px solid #e3e3e3; border-radius: 4px; box-shadow: 0 1px 1px rgba(0, 0, 0, 0.05) inset; margin-bottom: 20px; min-height: 20px; padding: 19px;}			
			.label-titol .control-label {padding-bottom: 20px;}		
			.label-titol .form-group .control-label{padding-bottom: 0px;}	
			.obligatori {
			    background-position: right 6px;
			}
		</style>
	<script>
			$(document).ready(function() {
				$('.date_time').datetimepicker({
					locale: moment.locale('${idioma}'),
					minDate: new Date(),
					format: "DD/MM/YYYY HH:mm"
			    });	
				
				$("#tipusExpressio").select2({
				    width: '100%',
				    minimumResultsForSearch: -1,
				    allowClear: true
				});
				
				$("#inici").on('focus', function() {
					$('.fa-calendar').click();
				});	
				
				$('#tipusExpressio').on('change', function() {
					$("#duser").hide();
					$("#dgrup").hide();
					$("#dexpr").hide();
					if ("user" == this.value) {
						$("#duser").show();
					} else if ("grup" == this.value) {
						$("#dgrup").show();
					} else if ("expr" == this.value) {
						$("#dexpr").show();
					}
				});
				$('#tipusExpressio').trigger('change');	
			});
			
			function confirmarReassignar(e) {
				var e = e || window.event;
				e.cancelBubble = true;
				if (e.stopPropagation) e.stopPropagation();
				return confirm("<spring:message code='expedient.eines.confirm_reassignar_expedients' />");
			}
		</script>
</head>
<body>
	<form:form action="reassignar" method="post" modelAttribute="expedientTascaReassignarCommand" onsubmit="return confirmarReassignar(event)">
		
		<div class="ctrlHolder">
			<label for="tipusExpressio"><spring:message code='expedient.eines.reassignar.tipus'/></label>
			<select id="tipusExpressio" name="tipusExpressio">
				<option value="user"><spring:message code='filtre.expressio.usuari'/></option>
				<option value="grup"><spring:message code='filtre.expressio.grup'/></option>
				<option value="expr"><spring:message code='filtre.expressio.expressio'/></option>
			</select>
		</div>
		
		<div class="ctrlHolder">
			<div id="duser">
				<hel:inputSuggest required="true" name="usuari" urlConsultaInicial="persona/suggestInici" urlConsultaLlistat="/helium/v3/expedient/persona/suggest" textKey="expedient.editar.responsable" placeholderKey="expedient.editar.responsable"/>
			</div>
			<div id="dgrup">
				<hel:inputText required="true" name="grup" textKey="filtre.expressio.grup" placeholderKey="filtre.expressio.grup"/>
			</div>
			<div id="dexpr">
				<hel:inputText required="true" name="expression" textKey="filtre.expressio.expressio" placeholderKey="filtre.expressio.expressio"/>
			</div>
		</div>				
				
		<div id="modal-botons">
			<button type="button" class="modal-tancar btn btn-default" name="submit" value="cancel">
				<spring:message code='comuns.cancelar' />
			</button>
			<button type="submit" name="submit" value="submit" class="btn btn-primary"><span class="fa fa-share-square-o"></span>&nbsp;<spring:message code="comuns.reassignar"/></button>
		</div>
	
		<!-- form:hidden path="taskId"/>

		<div class="control-group fila_reducida">
			<hel:inputText required="true" name="expression" textKey="expedient.tasca.expresio_assignacio" placeholderKey="expedient.tasca.expresio_assignacio"/>
		</div>
		
		<div id="modal-botons">
			<button type="button" class="modal-tancar btn btn-default" name="submit" value="cancel"><spring:message code="comuns.cancelar"/></button>
			<button type="submit" class="btn btn-primary" id="submit" name="submit" value="submit"><span class="fa fa-share-square-o"></span>&nbsp;<spring:message code="comuns.reassignar"/></button>
		</div-->
	</form:form>
</body>
</html>
