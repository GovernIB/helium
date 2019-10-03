<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code="anotacio.form.acceptar.titol" arguments="${anotacio.identificador}"/></title>
	<hel:modalHead />
<style>
body {
	min-height: 400px;
}
.tab-content {
    margin-top: 0.8em;
}
.icona-doc {
	color: #666666
}
.file-dt {
	margin-top: 9px;
}
.file-dd {
	margin-top: 3px;
}
tr.odd {
	background-color: #f9f9f9;
}
tr.detall {
/* 	background-color: cornsilk; */
}
tr.clicable {
	cursor: pointer;
}
</style>	
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
</head>
<body>

	<form:form cssClass="form-horizontal" enctype="multipart/form-data" method="post" commandName="anotacioAcceptarCommand" style="min-height: 500px;">

		<script type="text/javascript">

		var accio = '${anotacioAcceptarCommand.accio}';
		
		function adaptarVisibilitat(accio) {
			debugger;
			if (accio != '')
				$('.guardar, .crear, .incorporar').addClass('hidden');
			if (accio == 'GUARDAR') {
				$('.guardar').removeClass('hidden');				
			} else if (accio == 'CREAR') {
				$('.crear').removeClass('hidden');
			} else if (accio == 'INCORPORAR') {
				$('.incorporar').removeClass('hidden');
			}
		}
		
		$(document).ready(function(){

			adaptarVisibilitat(accio);
		
			$('input[type=radio][name=accio]').on('change', function() {
				adaptarVisibilitat($(this).val());
				webutilModalAdjustHeight();
			});
			
			$('#expedientTipusId').on('change', function() {
				var tipus = $(this).val();
				if (tipus) {
					$("#expedientId").removeAttr('disabled');
					$("#any").removeAttr('disabled').change();
					$("#numero").removeAttr('disabled');
					var urlLlistat = "<c:url value="/v3/anotacio/suggest/expedient/llista/"/>" + tipus ;
					$('#expedientId').data('urlLlistat', urlLlistat)
				} else {
					$("#expedientId").attr('disabled','disabled');
					$("#any").attr('disabled','disabled');
					$("#numero").attr('disabled','disabled');
				}
				$('#expedientId').select2('val', '', true);					
			});
			
			$('select[name=any]').on('change', function () {
				var tipus = $('#expedientTipusId').val();
				if ($(this).val() && tipus) {
					$.ajax({
						url:'<c:url value="/v3/expedient/canviAny/"/>' + $(this).val() + '/${anotacioAcceptarCommand.entornId}/' + tipus,
					    type:'GET',
					    dataType: 'json',
					    success: function(json) {
					    	$("#numero").val(json);
					    }
					});
				}
			});	
			
		});
		
		</script>

		<div class="row">
			<div class="col-sm-9" style="float: none; margin: 0 auto;">
				<div class="row">
					<div class="col-sm-12">
						<div class="panel panel-default">
							<div class="panel-heading">
								<h3 class="panel-title"><spring:message code="anotacio.form.acceptar.informacio.legend"/></h3>
							</div>
							<!-- Taula amb informació -->		
							<table class="table table-bordered">
							<tbody>
								<tr>
									<td><strong><spring:message code="anotacio.detalls.camp.numero"/></strong></td>
									<td colspan="3">${anotacio.identificador}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="anotacio.detalls.camp.extracte"/></strong></td>
									<td colspan="3">${anotacio.extracte}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="anotacio.detalls.camp.tipus"/></strong></td>
									<td><spring:message code="anotacio.detalls.entrada"/></td>
									<td><strong><spring:message code="anotacio.detalls.camp.procediment.codi"/></strong></td>
									<td>${anotacio.procedimentCodi}</td>
								</tr>
								<tr>
									<td><strong><spring:message code="anotacio.detalls.camp.oficina"/></strong></td>
									<td>${anotacio.oficinaDescripcio} (${anotacio.oficinaCodi})</td>
									<td><strong><spring:message code="anotacio.detalls.camp.assumpte.tipus"/></strong></td>
									<td>${anotacio.assumpteTipusDescripcio} (${anotacio.assumpteTipusCodi})</td>
								</tr>
								<tr>
									<td><strong><spring:message code="anotacio.detalls.camp.data"/></strong></td>
									<td><fmt:formatDate value="${anotacio.data}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
									<td><strong><spring:message code="anotacio.detalls.camp.numexp"/></strong></td>
									<td>${anotacio.expedientNumero}</td>
								</tr>
							</tbody>
							</table>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-12">
						<form:hidden path="id" />
				
						<hel:inputRadio name="accio" required="true" textKey="anotacio.form.acceptar.camp.accio" botons="true" optionItems="${accions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
				
						<hel:inputSelect name="expedientTipusId" textKey="anotacio.form.acceptar.camp.expedientTipus"
							optionItems="${expedientsTipus}" optionValueAttribute="codi" emptyOption="true"
							optionTextAttribute="valor"
							comment="anotacio.form.acceptar.camp.expedientTipus.info" />
							
						<div class="guardar incorporar">
							<hel:inputSuggest 
								name="expedientId"
								urlConsultaInicial="/helium/v3/anotacio/suggest/expedient/inici" 
								urlConsultaLlistat="/helium/v3/anotacio/suggest/expedient/llista/${anotacioAcceptarCommand.expedientTipusId}" 
								textKey="anotacio.form.acceptar.camp.expedient"
								disabled="${anotacioAcceptarCommand.expedientTipusId == null}"/>
						</div>

						<div class="hidden crear">
							<hel:inputText name="titol" textKey="anotacio.form.acceptar.camp.titol" />
							<hel:inputSelect name="any" disabled="${anotacioAcceptarCommand.expedientTipusId == null}" textKey="anotacio.form.acceptar.camp.any"  optionItems="${anysSeleccionables}" optionValueAttribute="valor" optionTextAttribute="codi" inline="false"/>
							<hel:inputText text="" name="numero" disabled="${anotacioAcceptarCommand.expedientTipusId == null}" textKey="anotacio.form.acceptar.camp.numero" inline="false"/>
						</div>
							
						<div class="hidden crear incorporar">
							<hel:inputCheckbox name="associarInteressats"
					 			textKey="anotacio.form.acceptar.camp.associarInteressats"/> 
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-8 col-sm-offset-4">
						<div class="hidden crear">
							<div class="alert alert-info">
								<span class="fa fa-info-circle"></span>
								<spring:message code="anotacio.form.acceptar.crear.info"></spring:message>
							</div>
						</div>
					
						<div class="hidden guardar">		
							<div class="alert alert-info">
								<span class="fa fa-info-circle"></span>
								<spring:message code="anotacio.form.acceptar.guardar.info"></spring:message>
							</div>
						</div>
					</div>
				</div>
				
			</div>
		</div>

		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-primary right">
				<span class="fa fa-cog"></span> <spring:message code="comu.boto.processar"/>
			</button>
			<button type="button" class="btn btn-default" data-modal-cancel="true">
				<spring:message code="comu.boto.cancelar"/>
			</button>
		</div>
		
	</form:form>
</body>
</html>

