<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code='expedient.iniciar.iniciar_expedient' />: ${expedientTipus.nom}</title>
	<meta name="capsaleraTipus" content="llistat"/>
	<hel:modalHead/>
	<style>
		body {background-image: none; padding-top: 0px;}
		.form-horizontal .form-group {margin-right: 0px;margin-left: 0px;;padding-left: 0px;}
		.form-horizontal .control-label {text-align: left;}
		.fila_reducida {margin-left: 0px;margin-right: 0px;padding-left: 0px;padding-right: 0px;}
		#s2id_any {width: 100% !important;}
	</style>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/helium3Tasca.js"/>"></script>
	
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script type="text/javascript">
		// <![CDATA[
		function confirmar(e) {
			var e = e || window.event;
			e.cancelBubble = true;
			if (e.stopPropagation) e.stopPropagation();
			return confirm("<spring:message code='expedient.iniciar.confirm_iniciar' />");
		}

		$(document).ready(function() {
			$('select[name=any]').on('change', function () {
				if ($(this).val()) {
					$.ajax({
						url:'<c:url value="/v3/expedient/canviAny/"/>' + $(this).val() + '/${expedientInicioPasTitolCommand.entornId}/${expedientTipus.id}',
					    type:'GET',
					    dataType: 'json',
					    success: function(json) {
					    	$("#numero").val(json);
					    }
					});
				}
			});	
		});	
		// ]]>
	</script>
</head>
<body>
	<form:form method="post" action="../../iniciarTitol/${expedientTipus.id}/${definicioProces.id}" id="command" name="command" cssClass="form-horizontal form-tasca" commandName="expedientInicioPasTitolCommand" onsubmit="return confirmar(event)">
		<form:hidden path="responsableCodi"/>
		<form:hidden path="entornId"/>
		<form:hidden path="expedientTipusId"/>
		<input type="hidden" name="definicioProcesId" value="${definicioProces.id}"/>

		<c:if test="${anotacioAcceptarCommand != null}">
			<div class="alert alert-info">
				<span class="fa fa-info-circle"></span>
				<spring:message code="anotacio.form.acceptar.crear.info"></spring:message>
			</div>
		</c:if>

		<c:if test="${expedientTipus.teNumero and expedientTipus.demanaNumero}">
			<div class="controls fila_reducida">
				<hel:inputText required="true" text="" name="numero" textKey="expedient.consulta.numero" placeholderKey="expedient.consulta.numero" inline="false"/>
			</div>
		</c:if>
		<c:if test="${expedientTipus.teTitol and expedientTipus.demanaTitol}">
			<div class="control-group fila_reducida">			
				<hel:inputTextarea required="true" name="titol" textKey="expedient.consulta.titol" placeholderKey="expedient.consulta.titol"/>
			</div>
		</c:if>
		<c:if test="${expedientTipus.seleccionarAny}">
			<div class="control-group fila_reducida select2-container-fix">
				<hel:inputSelect required="true" name="any" textKey="expedient.iniciar.canvi_any"  optionItems="${anysSeleccionables}" optionValueAttribute="valor" optionTextAttribute="codi" inline="false"/>
			</div>
			<div style="min-height: 120px;"></div>
		</c:if>
		<div id="modal-botons">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code='comuns.cancelar' /></button>		
			<button type="submit" id="iniciar" name="accio" class="btn btn-primary" value="iniciar">
				<spring:message code='comuns.iniciar' />
			</button>
		</div>
	</form:form>
</body>
</html>
