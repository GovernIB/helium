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
			.col-xs-8 {width: 100%;padding-bottom: 20px; padding-left: 0px;padding-right: 0px;}
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
			#duser {height: 200px;}
			#dgrup {height: 200px;}
			#dexpr {height: 200px;}
			#dgrup input {margin-bottom: 10px;}
			#badd {float: right; position: relative; top: -20px;}
			#brem {float: right; position: relative; top: -20px;}
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

				$("#badd").click(function() {
					let numGrups = $("#dgrup input").length;
					if (numGrups == 1) {
						$("#brem").show();
					}
					let newGroupImput = $('input[name="grups[0]"]').clone();
					newGroupImput.attr('name', 'grups[' + numGrups + ']')
					newGroupImput.insertAfter('input[name="grups[' + (numGrups - 1) + ']"]');
					$('input[name="grups[' + numGrups + ']"]').val("");
				});
				$("#brem").click(function() {
					let numGrups = $("#dgrup input").length;
					if (numGrups > 1) {
						$('input[name="grups[' + (numGrups - 1) + ']"]').remove();
					}
					if (numGrups == 2) {
						$("#brem").hide();
					}
				});
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

		<form:hidden id="motorTipus" path="motorTipus"/>
		<div class="ctrlHolder">
			<label for="tipusExpressio"><spring:message code='expedient.eines.reassignar.tipus'/></label>
			<select id="tipusExpressio" name="tipusExpressio">
				<option value="user"<c:if test="${tipusExpressio == 'user'}"> selected="selected"</c:if>><spring:message code='filtre.expressio.usuari'/></option>
				<option value="grup"<c:if test="${tipusExpressio == 'grup'}"> selected="selected"</c:if>><spring:message code='filtre.expressio.grup'/></option>
				<option value="expr"<c:if test="${tipusExpressio == 'expr'}"> selected="selected"</c:if>><spring:message code='filtre.expressio.expressio'/></option>
			</select>
		</div>
		
		<div class="ctrlHolder">
			<div id="duser">
				<hel:inputSuggest required="true" name="usuari" urlConsultaInicial="persona/suggestInici" urlConsultaLlistat="/heliumback/v3/expedient/persona/suggest" textKey="expedient.editar.responsable" placeholderKey="expedient.editar.responsable"/>
			</div>
			<div id="dgrup">
				<c:choose>
					<c:when test="${empty expedientTascaReassignarCommand.grups}">
						<hel:inputText required="true" name="grups[0]" textKey="filtre.expressio.grup" placeholderKey="filtre.expressio.grup"/>
						<c:if test="${expedientTascaReassignarCommand.motorTipus == 'CAMUNDA'}">
							<button id="badd" type="button" class="btn btn-default" name="add"><span class="fa fa-plus"></span></button>
							<button id="brem" style="display: none;" type="button" class="btn btn-default" name="add"><span class="fa fa-minus"></span></button>
						</c:if>
					</c:when>
					<c:otherwise>
						<c:forEach var="grup" items="${expedientTascaReassignarCommand.grups}" varStatus="index">
							<c:choose>
								<c:when test="${index.index == 0}">
									<hel:inputText required="true" name="grups[${index.index}]" textKey="filtre.expressio.grup" placeholderKey="filtre.expressio.grup"/>
								</c:when>
								<c:otherwise>
									<hel:inputText required="true" inline="true" name="grups[${index.index}]" textKey="filtre.expressio.grup" placeholderKey="filtre.expressio.grup"/>
								</c:otherwise>
							</c:choose>
						</c:forEach>
						<c:if test="${expedientTascaReassignarCommand.motorTipus == 'CAMUNDA'}">
							<button id="badd" type="button" class="btn btn-default" name="add"><span class="fa fa-plus"></span></button>
							<button id="brem" type="button" class="btn btn-default" name="add"><span class="fa fa-minus"></span></button>
						</c:if>
					</c:otherwise>
				</c:choose>
			</div>
			<div id="dexpr">
				<c:if test="${expedientTascaReassignarCommand.motorTipus == 'CAMUNDA'}">
					<hel:inputSelect required="true" name="expressionLanguage" textKey="filtre.expressio.expressio.llenguatge" optionItems="${languages}" optionValueAttribute="codi" optionTextAttribute="valor"/>
				</c:if>
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
