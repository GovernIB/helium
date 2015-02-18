<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code="consulta.form.informe" /></title>
	<meta name="title" content="${consulta.nom}"/>
	<meta name="title-icon-class" content="fa fa-search"/>
	<meta name="subtitle" content="${consulta.expedientTipus.nom}"/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${idioma}.js"/>"></script>
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
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
	<style>
		#filtresCollapsable .controls{ width: 100% !important;}
		#filtresCollapsable {padding-top: 20px;}
		input, select, textarea {width: 100%;}
		form .fila_reducida {padding-top: 0px;margin-bottom: 5px;}
		#expedientInformeCommand .row {margin-bottom: -5px;}
		#taulaDades {display: block;overflow-x: auto;border-left: 0 none;border-right: 0 none;border-bottom: 0 none;}
		.col-xs-13 {margin-left: -5px;margin-right: -15px;}
		.form-group {padding-right: 	15px;margin-left: 	10px !important;margin-bottom:	15px;}
		.form-group input, .form-group textarea {width: 100%;}		
		.form-group li > .select2-container {width: 100%;padding-right: 20px;}		
		.form-group .select2-container {width: calc(100% + 14px);}
		.condensed {margin-bottom: 0px;}
		.form-group.registre {padding-right: 1px;}
		.col-xs-9 {width: 82.5%}
		.registre table .colEliminarFila {width: 1px;}
		.registre table .opciones {text-align: center;padding: 4px;}
		p.help-block {padding-top: 0;margin-top: 4px !important;}
		.clear {clear: both;}
		.clearForm {clear: both;margin-bottom: 10px;border-bottom: solid 1px #EAEAEA;}
		.input-append {width: calc(100% - 27px);}
		.eliminarFila {padding: 4px 6px;}
		.pagination {margin : 0px !important;}
		#btn_exportar {padding-right : 10px;}
		.row {padding-bottom: 5px;}
		.col-xs-3 {width: 17.5%;}
		.control-label.col-xs-4 {width: auto !important;}
		.col-xs-5 {padding-left: 0px !important;margin-right: -55px;}
		.controls.form-group.col-xs-9 {margin-bottom: 0px !important;padding-bottom: 0px !important;}
		.col-xs-9 .form-group {margin-bottom: 0px !important;padding-bottom: 0px !important;}
		body .container .panel.panel-default {}
		body .container .panel.panel-default .panel-body .panel.panel-default {padding: 0px;margin-bottom: 0px;}
		#canviar_consulta {margin-top: 5px;}
		.col-md-1.btn-group {width: 4.333%;}
		.col-md-6.btn-group {width: 53.5%;}
	</style>
<script>
$(document).ready(function() {	
	$("#taulaDades").heliumDataTable({
		ajaxSourceUrl: "<c:url value="/v3/informe/${expedientInformeCommand.consultaId}/datatable"/>",
		localeUrl: "<c:url value="/js/dataTables-locales/dataTables_locale_ca.txt"/>",
		alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
		drawCallback: function() {
			$('.show-modal-error').click(function(e) {
				$('#modal-error .modal-title').html($(this).data('error-titol'));
				$('#modal-error .modal-body').html(
						'<p>' + $(this).data('error-missatge') + '</p>');
				if ($(this).data('error-detall')) {
					$('#modal-error .modal-body').append(
							'<p>' + $(this).data('error-detall') + '</p>');
				}
				if ($(this).data('error-pid')) {
					$('#modal-error .modal-body').append(
							'<p>processInstanceId: ' + $(this).data('error-pid') + '</p>');
				}
				$('#modal-error').modal('show');
				if (e.stopPropagation) e.stopPropagation();
			});
		},
		rowClickCallback: function(row, event) {
			var clickNomesDesplegar = true;
			var numTds = $('td', $(event.target).closest('tr')).length;
			var tdDesplegarIndex = numTds - 6;
			var isTdDesplegar = $(event.target).closest('td').is(':nth-child(' + tdDesplegarIndex + ')');
			if (!isTdDesplegar && !clickNomesDesplegar) {
				$('a.consultar-expedient', $(row))[0].click();
			} else {
				if ($(row).next().hasClass('table-pendents') || $(row).next().hasClass('tasques-pendents')) {
					while ($(row).next().hasClass('table-pendents') || $(row).next().hasClass('tasques-pendents')) {
						$(row).next().remove();
					}
					$('.icona-tasques-pendents', row).removeClass('fa-chevron-up').addClass('fa-chevron-down');
					$('.icona-tasques-pendents', row).attr('title', '<spring:message code="expedient.llistat.tasques.pendents.mostrar"/>');
				} else {
					var jqxhr = $.ajax({
						url: "<c:url value="/nodeco/v3/expedient/"/>" + $(row).find(".rdt-seleccio").val() + "/tasquesPendents/"+$('#nomesMeves').val()+"/"+$('#nomesTasquesPersonals').val()+"/"+$('#nomesTasquesGrup').val(),
						beforeSend: function(xhr) {
							$(row).after('<tr class="tasques-pendents"><td colspan="' + (numTds - 1) + '" style="text-align:center"><span class="fa fa-circle-o-notch fa-spin"></span></td></tr>');
						}
					}).done(function(data) {
						$(row).next(".tasques-pendents").remove();
						$(row).after(data);
						$('td:first', $(row).next(".tasques-pendents")).attr('colspan', numTds);
						$(row).next(".tasques-pendents").slideDown(1000);
						$('.icona-tasques-pendents', row).removeClass('fa-chevron-down').addClass('fa-chevron-up');
						$('.icona-tasques-pendents', row).attr('title', '<spring:message code="expedient.llistat.tasques.pendents.ocultar"/>');
					}).fail(function(jqXHR, exception) {
						modalAjaxErrorFunction(jqXHR, exception);
					});
				}
			}
		},
		seleccioCallback: function(seleccio) {
			$('#tramitacioMassivaCount').html(seleccio.length);
		}
	});
	$("button[data-toggle=button]").click(function() {
		$("input[name="+$(this).data("path")+"]").val(!$(this).hasClass('active'));
		$(this).blur();
		if ($(this).attr('id') == 'nomesTasquesPersonalsCheck') {
			$('#nomesTasquesGrupCheck').attr('disabled', !$(this).hasClass('active'));
		}
		if ($(this).attr('id') == 'nomesTasquesGrupCheck') {
			$('#nomesTasquesPersonalsCheck').attr('disabled', !$(this).hasClass('active'));
		}
		$("button[value=filtrar]").click();
	});
	if ($('#nomesTasquesPersonalsCheck').hasClass('active')) {
		$('#nomesTasquesGrupCheck').attr('disabled', true);
	}
	if ($('#nomesTasquesGrupCheck').hasClass('active')) {
		$('#nomesTasquesPersonalsCheck').attr('disabled', true);
	}
});
</script>
</head>
<body>		
	<form:form method="post" action="informe" cssClass="well form-horizontal form-tasca" commandName="expedientInformeCommand">
		<form:hidden path="consultaId"/>
		<div class="control-group fila_reducida">
			<c:forEach var="camp" items="${campsFiltre}">
				<c:set var="campActual" value="${camp}" scope="request"/>
				<c:set var="readonly" value="${false}" scope="request"/>
				<c:set var="required" value="${false}" scope="request"/>
				<c:import url="campsFiltre.jsp"/>
			</c:forEach>
		</div>		
		<div class="row">
			<div class="col-md-12">
				<form:hidden path="nomesAlertes"/>
				<form:hidden path="nomesTasquesPersonals"/>
				<form:hidden path="nomesTasquesGrup"/>
				<form:hidden path="nomesMeves"/>
				<div class="row">
					<div class="col-md-1 btn-group">
						<button id="nomesAlertesCheck" data-path="nomesAlertes" title="<spring:message code="expedient.llistat.filtre.camp.alertes"/>" class="btn btn-default<c:if test="${expedientInformeCommand.nomesAlertes}"> active</c:if>" data-toggle="button"><span class="fa fa-exclamation-triangle"></span></button>
					</div>
					<div class="col-md-5 btn-group">
						<c:if test="${potDissenyarEntorn or potAdministrarEntorn}">
							<button id="nomesMevesCheck" data-path="nomesMeves" title="<spring:message code="expedient.llistat.filtre.camp.meves"/>" class="btn btn-default<c:if test="${expedientInformeCommand.nomesMeves}"> active</c:if>" data-toggle="button"><span class="fa fa-male"></span></button>
						</c:if>
						<button id="nomesTasquesPersonalsCheck" data-path="nomesTasquesPersonals" title="<spring:message code="expedient.llistat.filtre.camp.personals"/>" class="btn btn-default<c:if test="${expedientInformeCommand.nomesTasquesPersonals}"> active</c:if>" data-toggle="button"><span class="fa fa-user"></span></button>
						<button id="nomesTasquesGrupCheck" data-path="nomesTasquesGrup" title="<spring:message code="expedient.llistat.filtre.camp.grup"/>" class="btn btn-default<c:if test="${expedientInformeCommand.nomesTasquesGrup}"> active</c:if>" data-toggle="button"><span class="fa fa-users"></span></button>
					</div>
					<div class="col-md-6 btn-group">
						<div class="pull-right">
							<input type="hidden" name="consultaRealitzada" value="true"/>
							<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
							<button type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</form:form>
				
	<table id="taulaDades" class="table table-striped table-bordered table-hover" data-rdt-button-template="tableButtonsTemplate" data-rdt-filtre-form-id="expedientInformeCommand" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0" <c:if test="${not empty preferenciesUsuari.numElementosPagina}">data-rdt-display-length-default="${preferenciesUsuari.numElementosPagina}"</c:if>>
		<thead>
			<tr class="panel-heading clicable proces" data-toggle="collapse">
				<th data-rdt-property="expedient.id" width="4%" data-rdt-sortable="false"></th>
				<th data-rdt-property="expedient.id" data-rdt-template="cellPendentsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="2%">
					<script id="cellPendentsTemplate" type="text/x-jsrender">
						<span class="icona-tasques-pendents fa fa-chevron-down" title="<spring:message code="expedient.llistat.tasques.pendents.mostrar"/>"></span>						
					</script>
				</th>
				<th data-rdt-property="expedient.identificador" data-rdt-sorting="desc" data-visible=true><spring:message code="expedient.llistat.columna.expedient"/></th>
				<c:forEach var="camp" items="${campsInforme}">
					<th <c:if test="${camp.varCodi == 'expedient__exp__estat'}">data-rdt-template="cellEstatTemplate"</c:if> data-rdt-property="dadesExpedient.${camp.varCodi}.valorMostrar" data-visible=true >
					${camp.campEtiqueta}
					<c:if test="${camp.varCodi == 'expedient__exp__estat'}">
						<script id="cellEstatTemplate" type="text/x-jsrender">
							{{:estat}}
							<div class="pull-right">
								{{if errorsIntegracions}}
									<span class="label label-danger" title="<spring:message code="expedient.consulta.error.integracions"/>"><span class="fa fa-exclamation-circle"></span> </span>
								{{/if}}
								{{if aturat}}
									<span class="label label-danger" title="{{:infoAturat}}">AT</span>
								{{/if}}
								{{if anulat}}
									<span class="label label-warning" title="{{:comentariAnulat}}">AN</span>
								{{/if}}
								{{if errorDesc}}
									{{if permisAdministration}}
										<span class="label label-warning show-modal-error" title="{{:errorDesc}}" data-error-titol="Informació sobre l'error" data-error-missatge="{{:errorDesc}}" data-error-detall="{{:errorFull}}" data-error-pid="{{:processInstanceId}}"><span class="fa fa-exclamation-circle"></span> </span>
									{{else}}
										<span class="label label-warning show-modal-error" title="{{:errorDesc}}" data-error-titol="Informació sobre l'error" data-error-missatge="{{:errorDesc}}"><span class="fa fa-exclamation-circle"></span> </span>
									{{/if}}						
								{{/if}}
							</div>
						</script>
					</c:if>
				</th>
				</c:forEach>
				<th data-rdt-property="infoAturat" data-rdt-visible="false"></th>
				<th data-rdt-property="estat" data-rdt-visible="false"></th>
				<th data-rdt-property="comentariAnulat" data-rdt-visible="false"></th>
				<th data-rdt-property="aturat" data-rdt-visible="false"></th>
				<th data-rdt-property="anulat" data-rdt-visible="false"></th>
				<th data-rdt-property="processInstanceId" data-rdt-visible="false"></th>
				<th data-rdt-property="permisCreate" data-rdt-visible="false"></th>
				<th data-rdt-property="permisAdministration" data-rdt-visible="false"></th>		
				<th data-rdt-property="permisRead" data-rdt-visible="false"></th>
				<th data-rdt-property="permisWrite" data-rdt-visible="false"></th>
				<th data-rdt-property="permisDelete" data-rdt-visible="false"></th>
				<th data-rdt-property="errorDesc" data-rdt-visible="false"></th>		
				<th data-rdt-property="errorFull" data-rdt-visible="false"></th>
				<th data-rdt-property="errorsIntegracions" data-rdt-visible="false"></th>			
				<th data-rdt-property="id" data-rdt-context="true" data-rdt-template="cellAccionsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a onclick="javascript: window.location=this.href" href="<c:url value="../v3/expedient/{{:id}}"/>" class="consultar-expedient"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.llistat.accio.consultar"/></a></li>
								{{if !aturat && permisWrite}}<li><a href="<c:url value="../v3/expedient/{{:id}}/suspend"/>" data-rdt-link-modal="true"><span class="fa fa-stop"></span>&nbsp;<spring:message code='comuns.aturar'/></a></li>{{/if}}
								{{if !anulat && permisWrite}}<li><a href="<c:url value="../v3/expedient/{{:id}}/cancel"/>" data-rdt-link-modal="true"><span class="fa fa-times"></span>&nbsp;<spring:message code='comuns.anular'/></a></li>{{/if}}
								{{if anulat && permisWrite}}<li><a href="<c:url value="../v3/expedient/{{:id}}/reprendre"/>" data-rdt-link-callback="recarregarTaula(taulaDades);" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code="expedient.consulta.confirm.desanular"/>">&nbsp;<span class="fa fa-reply"></span>&nbsp;<spring:message code="expedient.tasca.accio.reprendre"/></a></li>{{/if}}
								{{if permisDelete}}<li><a href="<c:url value="../v3/expedient/{{:id}}/delete"/>" data-rdt-link-callback="recarregarTaula(taulaDades);" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code='expedient.consulta.confirm.esborrar'/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code='comuns.esborrar'/></a></li>{{/if}}
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="tableButtonsTemplate" type="text/x-jsrender">
		<div class="btn-group pull-right">
			<a class="btn btn-default" href="<c:url value="../v3/informe/${consulta.id}/seleccioTots"/>" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.tots"/>"><span class="fa fa-check-square-o"></span></a>
			<a class="btn btn-default" href="<c:url value="../v3/informe/${consulta.id}/seleccioNetejar"/>" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.netejar"/>"><span class="fa fa-square-o"></span></a>
			<a class="btn btn-default" href="<c:url value="../v3/expedient/massiva?consultaId=${consulta.id}"/>"><spring:message code="expedient.llistat.accio.massiva"/>&nbsp;<span id="tramitacioMassivaCount" class="badge">&nbsp;</span></a>
		</div>
		<div id="btn_exportar" class="btn-toolbar pull-right btn_under_taulaDades">
			<c:if test="${consulta.exportarActiu}">
				<a id="exportar_excel" href="<c:url value="../v3/informe/${consulta.id}/exportar_excel"/>" class="btn btn-default">
					<span class="fa fa-download"></span>&nbsp;<spring:message code="comuns.descarregar"/>
				</a>
			</c:if>
			<c:if test="${not empty consulta.informeNom and not empty campsInformeParams}">
				<a data-rdt-link-modal="true" id="mostrar_informe" href="<c:url value="../v3/informe/${consulta.id}/mostrar_informe_params"/>" class="btn btn-default">
					<span class="fa fa-download"></span>&nbsp;<spring:message code="expedient.consulta.informe"/>
				</a>
			</c:if>
			<c:if test="${not empty consulta.informeNom and empty campsInformeParams}">
				<a id="mostrar_informe" href="<c:url value="../v3/informe/${consulta.id}/mostrar_informe"/>" class="btn btn-default">
					<span class="fa fa-download"></span>&nbsp;<spring:message code="expedient.consulta.informe"/>
				</a>
			</c:if>
		</div>			
	</script>
	
	<script type="text/javascript">
		function recarregarTaula(tableId, correcte) {
			if (correcte) {
				refrescarAlertas($("#"+tableId));
				$("#"+tableId).dataTable().fnDraw();
			}
		}
	
		function refrescarAlertas(e) {
			$.ajax({
				url: "<c:url value="/nodeco/v3/missatges"/>",
				async: false,
				timeout: 20000,
				success: function (data) {
					$('#contingut-alertes').html(data);
				}
			});
		}
		
		$("#tableButtonsTemplate a").heliumEvalLink({
			refrescarAlertes: true,
			refrescarPagina: false,
			alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
		});
	</script>
	<div id="modal-error" class="modal fade">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="<spring:message code="comu.boto.tancar"/>"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title"></h4>
				</div>
				<div class="modal-body">
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></button>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
