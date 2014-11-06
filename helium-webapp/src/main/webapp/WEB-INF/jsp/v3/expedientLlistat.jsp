<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="expedient.llistat.titol"/></title>
	<meta name="capsaleraTipus" content="llistat"/>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${idioma}.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/helium.datatable.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<style type="text/css">
		#consultaTipo {padding-bottom: 20px;padding-right: 15px;}
		.btn-mini {padding: 0 6px;}
	</style>
<script>
$(document).ready(function() {
	$("#taulaDades").heliumDataTable({
		ajaxSourceUrl: "<c:url value="/v3/expedient/datatable"/>",
		localeUrl: "<c:url value="/js/dataTables-locales/dataTables_locale_ca.txt"/>",
		alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
		drawCallback: function() {
			var seleccionable = $('#expedientTipusId').val() != '';
			$('#btnTramitacio').toggleClass("hide", !seleccionable);
			var seleccioColumna = $("#taulaDades").data("rdt-seleccionable-columna");			
			$("#taulaDades").find('tr').find("td:eq("+seleccioColumna+")").toggleClass("hide", !seleccionable);
			$("th:eq(" + seleccioColumna + ")", $("#taulaDades")).toggleClass("hide", !seleccionable);
		},
		rowClickCallback: function(row) {
// 			$('a.consultar-expedient', $(row))[0].click();
			$.ajax({
				"url": "<c:url value="/nodeco/v3/expedient/"/>" + $(row).find(".rdt-seleccio").val() + "/tasquesPendents",
				"beforeSend": function( xhr ) {	
					$('.fa-chevron-up').addClass('fa-chevron-down').removeClass('fa-chevron-up');
					$(row).find('.icona-collapse').removeClass('fa-chevron-down').addClass('fa-circle-o-notch fa-spin');
					$(".table-pendents").find('td').wrapInner('<div style="display: block;" />').parent().find('td > div').slideUp(400, function(){
					  	$(this).parent().parent().remove();
					});
				},
				"success": function (data) {
					$(row).find('.icona-collapse').removeClass('fa-circle-o-notch fa-spin').addClass('fa-chevron-up');
					$(row).after(data);
					$(".table-pendents").find('td').wrapInner('<div style="display: none;" />').parent().find('td > div').slideDown(400, function(){
						  var $set = $(this);
						  $set.replaceWith($set.contents());
					});
				},
			  	"error": function(XMLHttpRequest, textStatus, errorThrown) {
					$('.fa-chevron-up').removeClass('fa-chevron-down fa-circle-o-notch fa-spin fa-chevron-up');
					$(".table-pendents").remove();
				}
			});
		},
		seleccioCallback: function(seleccio) {
			$('#tramitacioMassivaCount').html(seleccio.length);
		}
	});
	$("#nomesPendentsCheck").click(function() {
		$("input[name=nomesPendents]").val(!$("#nomesPendentsCheck").hasClass('active'));
		$('#expedientConsultaCommand').submit();
	});
	$("#nomesAlertesCheck").click(function() {
		$("input[name=nomesAlertes]").val(!$("#nomesAlertesCheck").hasClass('active'));
		$('#expedientConsultaCommand').submit();
	});
	$("#mostrarAnulatsCheck").click(function() {
		$("input[name=mostrarAnulats]").val(!$("#mostrarAnulatsCheck").hasClass('active'));
		$('#expedientConsultaCommand').submit();
	});
	$('#expedientTipusId').on('change', function() {
		var tipus = $(this).val();
		$('#estatText').select2('val', '', true);
		$('#estatText option[value!=""]').remove();
		$('#consultaTipo').hide();
		
		if ($(this).val()) {
			$.get('expedient/estatsPerTipus/' + $(this).val())
			.done(function(data) {
				$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.INICIAT%>"><spring:message code="comu.estat.iniciat"/></option>');
				for (var i = 0; i < data.length; i++) {
					$('#estatText').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
				}
				$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.FINALITZAT%>"><spring:message code="comu.estat.finalitzat"/></option>');
			})
			.fail(function() {
				alert("<spring:message code="expedient.llistat.estats.ajax.error"/>");
			});

			$.get('expedient/consultas/' + $(this).val())
			.done(function(data) {
				var datos = '';
				for (var i = 0; i < data.length; i++) {
					datos += '<li><a href="<c:url value="/v3/informe/consulta/' + data[i].id + '"></c:url>">' + data[i].nom + '</a></li>';
				}
				$('#consultaTipo ul').html(datos);
				if (data.length > 0) {
					$('#consultaTipo').show();
				}
			})
			.fail(function() {
				alert("<spring:message code="expedient.llistat.consults.ajax.error"/>");
			});
		} else {
			$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.INICIAT%>"><spring:message code="comu.estat.iniciat"/></option>');
			$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.FINALITZAT%>"><spring:message code="comu.estat.finalitzat"/></option>');
		}
	});

	$('#expedientTipusId').trigger('change');
});
function alertaErrorUser(e, desc) {
	var e = e || window.event;
	e.cancelBubble = true;
	
	var text = desc + "<br/><br/>Póngase en contacto con el responsable del expediente.";
	$("#dialog-error-user").html(text);
	$("#dialog-error-user").data('title.dialog', 'Error en la ejecución del expediente'); 
	$("#dialog-error-user").dialog( "open" );
	if (e.stopPropagation) e.stopPropagation();
	
	return false;
}
function alertaErrorAdmin(e, id, desc, full) {
	var e = e || window.event;
	e.cancelBubble = true;

	var text = desc + "<br/><br/>Póngase en contacto con el responsable del expediente.";
	$("#dialog-error-admin").html(text+"<br/><br/>"+full+$("#dialog-error-admin").html());
	$("#processInstanceId").val(id);
	$("#dialog-error-admin").data('title.dialog', 'Error en la ejecución del expediente'); 
	$("#dialog-error-admin").dialog( "open" );

	if (e.stopPropagation) e.stopPropagation();
	
	return false;
}
</script>
</head>
<body>
	<input type="hidden" id="netejar" value="false"/>
									
	<form:form action="" method="post" cssClass="well" commandName="expedientConsultaCommand">
		<div id="consultaTipo" class="row">
			<div class="btn-group pull-right">
				<a class="btn btn-default btn-mini dropdown-toggle" href="#" data-toggle="dropdown">
					<spring:message code="expedient.llistat.consulta_avanzada"/> <span class="caret"></span>
				</a>
				<ul class="dropdown-menu"></ul>
			</div>
		</div>

		<div class="row">
			<div class="col-md-2">
				<hel:inputText name="numero" textKey="expedient.llistat.filtre.camp.numero" placeholderKey="expedient.llistat.filtre.camp.numero" inline="true"/>
			</div>
			<div class="col-md-4">
				<hel:inputText name="titol" textKey="expedient.llistat.filtre.camp.titol" placeholderKey="expedient.llistat.filtre.camp.titol" inline="true"/>
			</div>
			<div class="col-md-3">
				<hel:inputSelect name="expedientTipusId" textKey="expedient.llistat.filtre.camp.expedient.tipus" placeholderKey="expedient.llistat.filtre.camp.expedient.tipus" optionItems="${expedientTipusAccessibles}" optionValueAttribute="id" optionTextAttribute="nom" disabled="${not empty expedientTipusActual}" inline="true"/>
			</div>
			<div class="col-md-3">
				<hel:inputSelect name="estatText" textKey="expedient.llistat.filtre.camp.estat" placeholderKey="expedient.llistat.filtre.camp.estat" optionItems="${estats}" optionValueAttribute="id" optionTextAttribute="nom" inline="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-4">
				<label><spring:message code="expedient.llistat.filtre.camp.data.inici"/></label>
				<div class="row">
					<div class="col-md-6">
						<hel:inputDate name="dataIniciInicial" textKey="expedient.llistat.filtre.camp.data.inici.1" placeholder="dd/mm/yyyy" inline="true"/>
					</div>
					<div class="col-md-6">
						<hel:inputDate name="dataIniciFinal" textKey="expedient.llistat.filtre.camp.data.inici.2" placeholder="dd/mm/yyyy" inline="true"/>
					</div>
				</div>
			</div>
			<div class="col-md-4">
				<label><spring:message code="expedient.llistat.filtre.camp.data.fi"/></label>
				<div class="row">
					<div class="col-md-6">
						<hel:inputDate name="dataFiInicial" textKey="expedient.llistat.filtre.camp.data.fi.1" placeholder="dd/mm/yyyy" inline="true"/>
					</div>
					<div class="col-md-6">
						<hel:inputDate name="dataFiFinal" textKey="expedient.llistat.filtre.camp.data.fi.2" placeholder="dd/mm/yyyy" inline="true"/>
					</div>
				</div>
			</div>
			<div class="col-md-4">
				<c:if test="${globalProperties['app.georef.actiu']}">
					<label><spring:message code="expedient.llistat.filtre.camp.geopos"/></label>
					<c:choose>
						<c:when test="${globalProperties['app.georef.tipus']=='ref'}">
							<hel:inputText name="geoReferencia" textKey="expedient.llistat.filtre.camp.georef" placeholderKey="expedient.llistat.filtre.camp.georef" inline="true"/>
						</c:when>
						<c:otherwise>
							<div class="row">
								<div class="col-md-6">
									<hel:inputText name="geoPosX" textKey="expedient.llistat.filtre.camp.coordx" placeholderKey="expedient.llistat.filtre.camp.coordx" inline="true"/>
								</div>
								<div class="col-md-6">
									<hel:inputText name="geoPosY" textKey="expedient.llistat.filtre.camp.coordy" placeholderKey="expedient.llistat.filtre.camp.coordy" inline="true"/>
								</div>
							</div>
						</c:otherwise>
					</c:choose>
				</c:if>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6">
				<form:hidden path="nomesPendents"/>
				<form:hidden path="nomesAlertes"/>
				<form:hidden path="mostrarAnulats"/>
				<div class="btn-group">
					<button id="nomesPendentsCheck" title="<spring:message code="expedient.llistat.filtre.camp.tasques"/>" class="btn btn-default<c:if test="${expedientConsultaCommand.nomesPendents || preferenciesUsuari.filtroTareasActivas}"> active</c:if>" data-toggle="buttons"><span class="fa fa-user"></span></button>
					<button id="nomesAlertesCheck" title="<spring:message code="expedient.llistat.filtre.camp.alertes"/>" class="btn btn-default<c:if test="${expedientConsultaCommand.nomesAlertes}"> active</c:if>" data-toggle="buttons"><span class="fa fa-exclamation-triangle"></span></button>
					<button id="mostrarAnulatsCheck" title="<spring:message code="expedient.llistat.filtre.camp.anulats"/>" class="btn btn-default<c:if test="${expedientConsultaCommand.mostrarAnulats}"> active</c:if>" data-toggle="buttons"><span class="fa fa-times"></span></button>
				</div>
			</div>
			<div class="col-md-6">
				<div class="pull-right">
					<input type="hidden" name="consultaRealitzada" value="true"/>
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
					<button id="consultar" type="submit" name="accio" value="consultar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	<table id="taulaDades" class="table table-striped table-bordered table-hover" data-rdt-button-template="tableButtonsTemplate" data-rdt-filtre-form-id="expedientConsultaCommand" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0" <c:if test="${not empty preferenciesUsuari.numElementosPagina}">data-rdt-display-length-default="${preferenciesUsuari.numElementosPagina}"</c:if>>
		<thead>
			<tr>
				<th data-rdt-property="id" width="4%" data-rdt-sortable="false"></th>
				<th data-rdt-property="id" data-rdt-template="cellPendentsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="2%">
					<script id="cellPendentsTemplate" type="text/x-jsrender">
						<div class="pull-left">
							<span class="icona-collapse fa fa-chevron-down"></i>						
						</div>
					</script>
				</th>
				<th data-rdt-property="identificador" data-rdt-visible="true"><spring:message code="expedient.llistat.columna.expedient"/></th>
				<th data-rdt-property="tipus.nom" data-rdt-visible="true"><spring:message code="expedient.llistat.columna.tipus"/></th>
				<th data-rdt-property="dataInici" data-rdt-type="datetime" data-rdt-sorting="desc" data-rdt-visible="true"><spring:message code="expedient.llistat.columna.iniciat"/></th>
				<th data-rdt-property="dataFi" data-rdt-type="datetime" data-rdt-visible="true"><spring:message code="expedient.llistat.columna.finalitzat"/></th>
				<th data-rdt-property="estat.nom" data-rdt-template="cellEstatTemplate" data-rdt-visible="true">
					<spring:message code="expedient.llistat.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
					{{if dataFi}}
						<spring:message code="comu.estat.finalitzat"/>
					{{else estat_nom}}
						{{:estat_nom}}
					{{else}}
						<spring:message code="comu.estat.iniciat"/>
					{{/if}}

					<div class="pull-right">
						{{if aturat}}
							<span class="label label-danger" title="<spring:message code="expedient.info.aturat"/>">AT</span>
						{{else anulat}}
							<span class="label label-warning" title="<spring:message code="expedient.info.anulat"/>">AN</span>
						{{/if}}
						{{if errorDesc}}
							{{if isAdmin}}
								<span class="label label-warning" title="<spring:message code="expedient.info.anulat"/>"><span class="fa fa-exclamation-circle" onclick="return alertaErrorAdmin(event, {{:processInstanceId}}, '{{:errorDesc}}', '{{:errorFull}}')"></span></span>
							{{else}}
								<span class="label label-warning" title="<spring:message code="expedient.info.anulat"/>"><span class="fa fa-exclamation-circle" onclick="return alertaErrorUser(event, '{{:errorDesc}}')"></span></span>
							{{/if}}						
						{{/if}}
					</div>
					</script>
				</th>
				<th data-rdt-property="aturat" data-rdt-visible="false"></th>
				<th data-rdt-property="anulat" data-rdt-visible="false"></th>
				<th data-rdt-property="processInstanceId" data-rdt-visible="false"></th>
				<th data-rdt-property="permisCreate" data-rdt-visible="false"></th>			
				<th data-rdt-property="permisRead" data-rdt-visible="false"></th>
				<th data-rdt-property="permisWrite" data-rdt-visible="false"></th>
				<th data-rdt-property="permisDelete" data-rdt-visible="false"></th>
				<th data-rdt-property="errorDesc" data-rdt-visible="false"></th>				
<%--
				<th data-rdt-property="id" data-rdt-template="cellPermisosTemplate" data-rdt-visible="true" data-rdt-sortable="false">
					Permisos
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						{{if permisCreate}}C{{/if}}
						{{if permisRead}}R{{/if}}
						{{if permisWrite}}W{{/if}}
						{{if permisDelete}}D{{/if}}
						{{if permisSupervision}}S{{/if}}
						{{if permisReassignment}}G{{/if}}
					</script>
				</th>
--%>
				<th data-rdt-property="id" data-rdt-template="cellAccionsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown navbar-right">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="expedient/{{:id}}" class="consultar-expedient"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.llistat.accio.consultar"/></a></li>
								{{if permisWrite}}<li><a href="../v3/expedient/{{:id}}/suspend" data-rdt-link-modal="true"><span class="fa fa-stop"></span>&nbsp;<spring:message code="expedient.llistat.accio.aturar"/>...</a></li>{{/if}}
								{{if permisWrite}}<li><a href="../v3/expedient/{{:id}}/cancel" data-rdt-link-modal="true"><span class="fa fa-times"></span>&nbsp;<spring:message code="expedient.llistat.accio.anular"/>...</a></li>{{/if}}
								{{if permisDelete}}<li><a href="../v3/expedient/{{:id}}/delete" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code="expedient.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>{{/if}}
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="tableButtonsTemplate" type="text/x-jsrender">
		<div style="text-align:right">
			<div id="btnTramitacio" class="btn-group">
				<a class="btn btn-default" href="../v3/expedient/seleccioTots" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.tots"/>"><span class="fa fa-check-square-o"></span></a>
				<a class="btn btn-default" href="../v3/expedient/seleccioNetejar" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.netejar"/>"><span class="fa fa-square-o"></span></a>
				<a class="btn btn-default" href="../v3/expedient/massiva"><spring:message code="expedient.llistat.accio.massiva"/>&nbsp;<span id="tramitacioMassivaCount" class="badge">&nbsp;</span></a>
			</div>
			<a data-rdt-link-modal="true" id="iniciar-modal" class="btn btn-default" href="<c:url value="../v3/expedient/iniciar"/>"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.llistat.accio.nou"/></a>
		</div>
	</script>

</body>
</html>
