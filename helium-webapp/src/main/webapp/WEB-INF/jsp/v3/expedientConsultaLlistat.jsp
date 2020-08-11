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
	<meta name="screen" content="expedients">
	<meta name="subtitle" content="${consulta.expedientTipus.nom}"/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
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
	
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	
	<style>
		#filtresCollapsable .controls{ width: 100% !important;}
		#filtresCollapsable {padding-top: 20px;}
		form .fila_reducida {padding-top: 0px;margin-bottom: 5px;}
		#expedientConsultaCommand .row {margin-bottom: -5px;}
		#taulaDades {display: block;overflow-x: auto;border-left: 0 none;border-right: 0 none;border-bottom: 0 none;}
		.col-xs-13 {/*margin-left: -5px;margin-right: -15px;*/}
		.form-group {padding-right: 	15px;margin-left: 	10px !important;margin-bottom:	15px;}
		.form-group input, .form-group textarea {width: 100%;}		
		.form-group li > .select2-container {width: 100%;padding-right: 20px;}		
		.form-group .select2-container {width: calc(100% + 0px) !important;}
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
		.col-sm-0 {position: relative; min-height: 1px; padding-right: 15px; padding-left: 15px;}
		.mb-0 {margin-bottom:0;}
		.control-label.col-xs-4 {width: auto !important;}
		.col-xs-5 {padding-left: 0px !important;margin-right: -55px;}
		.controls.form-group.col-xs-9 {margin-bottom: 0px !important;padding-bottom: 0px !important;}
		.col-xs-9 .form-group {margin-bottom: 0px !important;padding-bottom: 0px !important;}
		body .container .panel.panel-default {}
		body .container .panel.panel-default .panel-body .panel.panel-default {padding: 0px;margin-bottom: 0px;}
		#canviar_consulta {margin-top: 5px;}
		.col-md-1.btn-group {width: 4.333%;}
		.col-md-6.btn-group {width: 53.5%;}
		.like-cols {
			 float: left;
			 padding-left: 0px;
			 margin-bottom: 6px;	
		}
		.top-label {
			padding-right: 11px; 
			text-align: left !important; 
			margin-bottom:4px !important;
		}
		.form-group .consulta-tipus-controls .form-group {
			margin-left: 0px !important;
		}
	</style>
<script>
$(document).ready(function() {	
	$("#taulaDades").heliumDataTable({
		ajaxSourceUrl: "<c:url value="/v3/expedient/consulta/${consulta.id}/datatable"/>",
		localeUrl: "<c:url value="/js/dataTables-locales/dataTables_locale_ca.txt"/>",
		alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
		ajaxRequestType: "POST",
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
						url: "<c:url value="/nodeco/v3/expedient/"/>" + $(row).find(".rdt-seleccio").val() + "/tasquesPendents/"+$('#nomesTasquesPersonals').val()+"/"+$('#nomesTasquesGrup').val()+"/consultesTipus",
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
	$("form#expedientConsultaCommand button[data-toggle=button]").click(function() {
		var $formulari = $(this).closest('form');
		$("input[name=" + $(this).data("path") + "]", $formulari).val(!$(this).hasClass('active'));
		if ($(this).is('#nomesTasquesPersonalsCheck', $formulari)) {
			$('#nomesTasquesGrupCheck', $formulari).removeClass('active');
			$("input#nomesTasquesGrup", $formulari).val(false);
		}
		if ($(this).is('#nomesTasquesGrupCheck', $formulari)) {
			$('#nomesTasquesPersonalsCheck', $formulari).removeClass('active');
			$("input#nomesTasquesPersonals", $formulari).val(false);
		}
		$(this).blur();
		$("button#consultar", $formulari).click();
	});
});
</script>
</head>
<body>		
	<form:form method="post" action="" cssClass="well form-horizontal form-tasca" commandName="expedientConsultaCommand">
		<form:hidden path="consultaId"/>
		<div class="control-group fila_reducida">
		
			<c:set var="ampleLabel">135px</c:set>
			<c:set var="ampleInput">calc(100% - ${ampleLabel})</c:set>
			<c:set var="comptadorCols">0</c:set>
			<div class="row">
			<c:forEach var="camp" items="${campsFiltre}">
				<c:set var="campActual" value="${camp}" scope="request"/>
				<c:set var="readonly" value="${false}" scope="request"/>
				<c:set var="required" value="${false}" scope="request"/>
				
				
				<c:set var="ampleCols">${camp.ampleCols}</c:set>
				<c:set var="buitCols">${camp.buitCols}</c:set>
				<c:set var="buitAbsCols">${buitCols < 0 ? -buitCols : buitCols}</c:set>
				<c:set var="ampleBuit">${buitAbsCols + ampleCols}</c:set>
				
				<c:set var="comptadorCols">${comptadorCols + ampleBuit}</c:set>
				
				<c:if test="${comptadorCols > 12}">
					<c:set var="comptadorCols">${comptadorCols - 12}</c:set>
					
					<!--tanquem row i la tornem a obrir per a la següent fila-->
					</div>
						<div class="row">
					<!------------------------->
				</c:if>
				
				<!-- si tenim el buit menor que 0, l'offset va al davant del camp -->
				<c:if test="${buitCols < 0}">
					<div class="col-sm-${buitAbsCols}"></div>
				</c:if>
				
				<div class="col-sm-${ampleCols > 0?ampleCols:12}">
					<%@ include file="campsFiltre.jsp" %>
				</div>
				
				<!-- si el buit es major que 0, l'offset va després del camp -->
				<c:if test="${buitCols > 0}">
					<div class="col-sm-${buitAbsCols}"></div>
				</c:if>
			</c:forEach>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12">
				<form:hidden path="nomesAlertes"/>
				<form:hidden path="nomesTasquesPersonals"/>
				<form:hidden path="nomesTasquesGrup"/>
				<%-- <form:hidden path="nomesErrors"/> --%>
				
				<button style="display:none" type="submit" name="accio" value="filtrar"></button>
				<div class="row">
					<div class="col-md-6">
						<div class="btn-group">
							<button id="nomesTasquesPersonalsCheck" data-path="nomesTasquesPersonals" title="<spring:message code="expedient.llistat.filtre.camp.personals"/>" class="btn btn-default<c:if test="${expedientConsultaCommand.nomesTasquesPersonals}"> active</c:if>" data-toggle="button"><span class="fa fa-user"></span></button>
							<button id="nomesTasquesGrupCheck" data-path="nomesTasquesGrup" title="<spring:message code="expedient.llistat.filtre.camp.grup"/>" class="btn btn-default<c:if test="${expedientConsultaCommand.nomesTasquesGrup}"> active</c:if>" data-toggle="button"><span class="fa fa-users"></span></button>
						</div>
						<%-- <button id="nomesErrorsCheck" data-path="nomesErrors" title="<spring:message code="expedient.llistat.filtre.camp.errors"/>" class="btn btn-default filtre-button<c:if test="${expedientConsultaCommand.nomesErrors}"> active</c:if>" data-toggle="button"><span class="fa fa-exclamation-triangle"></span></button> --%>
						<button id="nomesAlertesCheck" data-path="nomesAlertes" title="<spring:message code="expedient.llistat.filtre.camp.alertes"/>" class="btn btn-default<c:if test="${expedientConsultaCommand.nomesAlertes}"> active</c:if>" data-toggle="button"><span class="fa  fa-envelope"></span></button>
					</div>
					<div class="col-md-6">
						<div class="pull-right">
							<input type="hidden" name="consultaRealitzada" value="true"/>
							<button type="submit" name="accio" value="netejar" data-refrescar="true" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
							<button id="consultar" type="submit" name="accio" value="filtrar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</form:form>
	<table id="taulaDades" class="table table-striped table-bordered table-hover" data-rdt-button-template="tableButtonsTemplate" data-rdt-filtre-form-id="expedientConsultaCommand" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0" <c:if test="${not empty preferenciesUsuari.numElementosPagina}">data-rdt-display-length-default="${preferenciesUsuari.numElementosPagina}"</c:if> data-rdt-reduir-parametres="true">
		<thead>
			<tr class="panel-heading clicable proces" data-toggle="collapse">
				<th data-rdt-property="expedient.id" width="4%" data-rdt-sortable="false"></th>
				<th data-rdt-property="expedient.id" data-rdt-template="cellPendentsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="2%">
					<script id="cellPendentsTemplate" type="text/x-jsrender">
						<span class="icona-tasques-pendents fa fa-chevron-down" title="<spring:message code="expedient.llistat.tasques.pendents.mostrar"/>"></span>						
					</script>
				</th>
				<th data-rdt-property="expedient.identificador" data-rdt-template="cellReindexacioTemplate" data-rdt-sorting="desc" data-visible=true>
					<spring:message code="expedient.llistat.columna.expedient"/>
					<script id="cellReindexacioTemplate" type="text/x-jsrender">
					{{:expedient_identificador}}
					{{if reindexarData || reindexarError}}
						<div class="pull-right">
							<span class="fa fa-refresh {{if reindexarError}}text-danger {{/if}}" 
							title="{{if reindexarData}}<spring:message code="expedient.consulta.reindexacio.asincrona"/>{{/if}}
								   {{if reindexarError}}<spring:message code="expedient.consulta.reindexacio.error.full"/>{{/if}}"></span>
						</div>
					{{/if}}
					</script>
				</th>
				<c:forEach var="camp" items="${campsInforme}">
					<th <c:if test="${camp.varCodi == 'expedient__exp__estat'}">data-rdt-template="cellEstatTemplate"</c:if> data-rdt-property="dadesExpedient.${camp.varCodi}.valorMostrar" data-visible=true >
					${camp.campEtiqueta}
					<c:if test="${camp.varCodi == 'expedient__exp__estat'}">
						<script id="cellEstatTemplate" type="text/x-jsrender">
							{{if finalitzat}}
								<spring:message code="comu.estat.finalitzat"/>
							{{else}}
								{{:estat}}
							{{/if}}
							<div class="pull-right">
								{{if ambErrors}}
									<span class="label label-danger" title="<spring:message code="expedient.consulta.errors"/>"><span class="fa fa-exclamation-circle"></span> </span>
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
				<th data-rdt-property="reindexarData" data-rdt-visible="false"></th>
				<th data-rdt-property="reindexarError" data-rdt-visible="false"></th>
				<th data-rdt-property="tipus" data-rdt-visible="false"></th>
				<th data-rdt-property="infoAturat" data-rdt-visible="false"></th>
				<th data-rdt-property="estat" data-rdt-visible="false"></th>
				<th data-rdt-property="comentariAnulat" data-rdt-visible="false"></th>
				<th data-rdt-property="aturat" data-rdt-visible="false"></th>
				<th data-rdt-property="anulat" data-rdt-visible="false"></th>
				<th data-rdt-property="finalitzat" data-rdt-visible="false"></th>
				<th data-rdt-property="processInstanceId" data-rdt-visible="false"></th>
				<th data-rdt-property="permisCreate" data-rdt-visible="false"></th>
				<th data-rdt-property="permisAdministration" data-rdt-visible="false"></th>		
				<th data-rdt-property="permisRead" data-rdt-visible="false"></th>
				<th data-rdt-property="permisWrite" data-rdt-visible="false"></th>
				<th data-rdt-property="permisDelete" data-rdt-visible="false"></th>
				<th data-rdt-property="errorDesc" data-rdt-visible="false"></th>		
				<th data-rdt-property="errorFull" data-rdt-visible="false"></th>
				<th data-rdt-property="errorsIntegracions" data-rdt-visible="false"></th>	
				<th data-rdt-property="ambErrors" data-rdt-visible="false"></th>		
				<th data-rdt-property="id" data-rdt-context="true" data-rdt-template="cellAccionsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a onclick="javascript: window.location=this.href" href="<c:url value="../../../v3/expedient/{{:id}}"/>" class="consultar-expedient"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.llistat.accio.consultar"/></a></li>
								{{if !aturat && permisWrite}}<li><a href="<c:url value="../../../v3/expedient/{{:id}}/aturar"/>" data-rdt-link-modal="true"><span class="fa fa-stop"></span>&nbsp;<spring:message code='comuns.aturar'/></a></li>{{/if}}
								{{if !anulat && permisWrite}}<li><a href="<c:url value="../../../v3/expedient/{{:id}}/anular"/>" data-rdt-link-modal="true"><span class="fa fa-times"></span>&nbsp;<spring:message code='comuns.anular'/></a></li>{{/if}}
								{{if aturat && permisWrite}}<li><a href="<c:url value="../../../v3/expedient/{{:id}}/reprendre"/>" data-rdt-link-callback="recarregarTaula(taulaDades);" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code="expedient.consulta.confirm.desanular"/>">&nbsp;<span class="fa fa-reply"></span>&nbsp;<spring:message code="expedient.tasca.accio.reprendre"/></a></li>{{/if}}
								{{if permisDelete}}<li><a href="<c:url value="../../../v3/expedient/{{:id}}/delete"/>" data-rdt-link-callback="recarregarTaula(taulaDades);" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code='expedient.consulta.confirm.esborrar'/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code='comuns.esborrar'/></a></li>{{/if}}
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	
	<script id="tableButtonsTemplate" type="text/x-jsrender">
		<div class="btn-group pull-right">
			<a class="btn btn-default" href="<c:url value="../../../v3/expedient/consulta/${consulta.id}/selectionAll"/>" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.tots"/>"><span class="fa fa-check-square-o"></span></a>
			<a class="btn btn-default" href="<c:url value="../../../v3/expedient/consulta/${consulta.id}/selectionNone"/>" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.netejar"/>"><span class="fa fa-square-o"></span></a>
			<a class="btn btn-default" href="<c:url value="../../../v3/expedient/massiva?consultaId=${consulta.id}"/>"><spring:message code="expedient.llistat.accio.massiva"/>&nbsp;<span id="tramitacioMassivaCount" class="badge">&nbsp;</span></a>
		</div>
		<div id="btn_exportar" class="btn-toolbar pull-right btn_under_taulaDades">
			<c:if test="${consulta.exportarActiu}">
				<a id="exportar_excel" href="<c:url value="../../../v3/expedient/consulta/${consulta.id}/excel"/>" class="btn btn-default">
					<span class="fa fa-download"></span>&nbsp;<spring:message code="comuns.descarregar"/>
				</a>
			</c:if>
			<c:if test="${not empty consulta.informeNom and not empty campsInformeParams}">
				<a data-rdt-link-modal="true" data-rdt-link-modal-min-height="300" href="<c:url value="../../../v3/expedient/consulta/${consulta.id}/informeParams"/>" class="btn btn-default">
					<span class="fa fa-file-text-o"></span>&nbsp;<spring:message code="expedient.consulta.informe"/>
				</a>
			</c:if>
			<c:if test="${not empty consulta.informeNom and empty campsInformeParams}">
				<button type="button" class="btn btn-default" data-toggle="modal" data-target="#informeDescarregarModal">
					<span class="fa fa-file-text-o"></span>&nbsp;<spring:message code="expedient.consulta.informe"/>
				</button>
			</c:if>
		</div>			
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
	
	<div id="informeDescarregarModal" class="modal fade" role="dialog">
		<div class="modal-dialog">
		    <div class="modal-content">
        		<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="<spring:message code="comu.boto.cancelar"/>"><span aria-hidden="true">&times;</span></button>
          			<h4 class="modal-title"><spring:message code="expedient.informe.generacio"/></h4>
        		</div>
	        	<div class="modal-body">
	        		<p><spring:message code="expedient.informe.generacio.estat"/>: <label id="labelEstat">-</label></p>
	        		<div id="divError" style="display:none;"><span class="fa fa-exclamation-triangle text-danger"></span> <label id="labelError">[error]</label></div>
	        		<div id="divInfo" style="display:none;">
	        			<p><spring:message code="expedient.informe.generacio.consulta"/>: <label id="labelConsulta">-</label></p>
	        			<p><spring:message code="expedient.informe.numero.expedients"/>: <label id="labelNumeroExpedients">0</label></p>
	        		</div>
	        		<div class="row">
		        		<div class="col-sm-1">
		        			<span id="spinnerIcon" style="visibility:hidden;" class="fa fa-spinner fa-spin fa-2x"/>
		        		</div>
		        		<div class="col-sm-1">
		        			<span id="dataTransferIcon" style="visibility:hidden;" class="fa fa-exchange fa-rotate-90 fa-2x"/>
		        		</div>
	        		</div>
        		</div>
        		<div class="modal-footer">
					<button id="tancarInformeBtn" type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></button>
	        	</div>
      		</div>
	    </div>
	</div>
	
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
		
		
		// Variable on es guarda la informació de la generació en curs
		var informe = null;
		var interval = null;
		
		$(document).ready(function() {

			$('#informeDescarregarModal').on('shown.bs.modal', function () {
				informe = getConsultaInfo();
				if (informe != null && informe.estat != 'NO_TROBAT')
					consultarPeriodicament();
				else {
					// Inicia la generació i obté un objecte de consulta
					generarInforme();
				}
			});
			
			$('#informeDescarregarModal').on('hide.bs.modal', function (e) {
				if (informe != null && (["NO_TROBAT", "INICIALITZANT", "GENERANT"].indexOf(informe.estat) >= 0)) {
				    if (confirm("<spring:message code='expedient.informe.generacio.cancellar.confirmacio'/>")) {
					    // Cancel·lar la generació de l'informe
					    informe = cancellarInforme();
					    actualitzarInfoDescarrega(informe)
				    } else {
				    	e.preventDefault();
				    	return false;
				    }
				}
			});			
		});
		
		/** Inicia asíncronament la generació de l'informe. */
		function generarInforme() {

			$("#labelEstat").html(estats['INICIALITZANT']);
			$("#labelConsulta").html('-');
			$("#divError").hide()
			$("#divInfo").hide()
			$('#spinnerIcon').css('visibility', 'visible');
			$("#informeDescarregarModal").modal();
			
			$.ajax({
				type: 'GET',
				url: '<c:url value="/v3/expedient/consulta/${consulta.id}/informeAsync"/>',
				dataType: "json",
				traditional: true,
			  	data: {
			  	}
			})
				.done(function( data ) {
					informe = data;
					actualitzarInfoDescarrega(informe);
					consultarPeriodicament();		
				})
				.fail(function(jqXHR, textStatus) {
				    console.log( "Error iniciant la generació de l'informe: " + textStatus );
					$("#labelError").html(textStatus);
					$("#divError").show();
				});
		}
		
		function consultarPeriodicament() {
			console.log('consultarPeriodicament');
			// Consulta periòdica
			clearInterval(interval);
			interval = setInterval(function(){ 
				informe = getConsultaInfo();
				actualitzarInfoDescarrega(informe);
				if (informe == null || (["NO_TROBAT", "FINALITZAT", "CANCELLAT", "ERROR"].indexOf(informe.estat) >= 0))
					clearInterval(interval);
			}, 5000);		
		}
				
		/** Mètode per consultar l'estat de la consulta */
		function getConsultaInfo() {
			var ret = null;
			$('#dataTransferIcon').css('visibility', 'visible');
			$.ajax({
				type: 'GET',
				url: '<c:url value="/v3/expedient/consulta/${consulta.id}/informeAsync/info"/>',
				dataType: "json",
				traditional: true,
				async: false,
			  	data: {
			  	}
			})
				.done(function( data ) {
					ret = data;
				  })
				.fail(function(jqXHR, textStatus) {
				    console.log( "Error consultant la informació: " + textStatus );
					$("#labelError").html(textStatus);
					$("#divError").show();
				  })
				.always(function() {
					$('#dataTransferIcon').css('visibility', 'hidden');
				  });	
			return ret;
		}
		
		var estats = {};
		estats['NO_TROBAT'] = '<spring:message code="expedient.informe.estat.NO_TROBAT"/>';
		estats['INICIALITZANT'] = '<spring:message code="expedient.informe.estat.INICIALITZANT"/>';
		estats['GENERANT'] = '<spring:message code="expedient.informe.estat.GENERANT"/>';
		estats['FINALITZAT'] = '<spring:message code="expedient.informe.estat.FINALITZAT"/>';
		estats['CANCELLAT'] = '<spring:message code="expedient.informe.estat.CANCELLAT"/>';
		estats['ERROR'] = '<spring:message code="expedient.informe.estat.ERROR"/>';

		
		/** Actualitza visualment la modal de descàrrega segons la informació rebuda. */
		function actualitzarInfoDescarrega(info) {

			if (info == null || info.estat == 'NO_TROBAT') {
				informe = null;
				$('#informeDescarregarModal').modal('hide');
			} else {
				$('#informeDescarregarModal').modal();
				informe = info;
				$("#labelEstat").html(estats[informe.estat]);
				switch (informe.estat) {
				case 'INICIALITZANT':
					$('#spinnerIcon').css('visibility', 'visible');
					break;
				case 'GENERANT':
					$('#spinnerIcon').css('visibility', 'visible');
					$('#labelConsulta').html(info.consulta);
					$('#labelNumeroExpedients').html(info.numeroRegistres);
					$('#divInfo').show();
					break;
				case 'FINALITZAT':
					$('#spinnerIcon').css('visibility', 'hidden');
					// Descarrega el document
					window.location = '<c:url value="/v3/expedient/consulta/${consulta.id}/informeAsync/exportar"/>';
					informe = null;
					$('#informeDescarregarModal').modal('hide');
					break;
				case 'CANCELLAT':
					$('#spinnerIcon').css('visibility', 'hidden');
					break;
				case 'ERROR':
					$('#spinnerIcon').css('visibility', 'hidden');
					$("#labelError").html(info.missatge);
					$("#divError").show();
					break;
				default:
					;
				}
			}
		}
		
		function cancellarInforme() {
			var ret = null;
			if (interval != null)
				clearInterval(interval);
			informe = null;
			$('#dataTransferIcon').css('visibility', 'visible');
			$.ajax({
				type: 'GET',
				url: '<c:url value="/v3/expedient/consulta/${consulta.id}/informeAsync/cancellar"/>',
				dataType: "json",
				traditional: true,
				async: false,
			  	data: {
			  	}
			})
				.done(function( data ) {
					ret = data;
					if (ret == null || (["NO_TROBAT", "FINALITZAT", "CANCELLAT", "ERROR"].indexOf(ret.estat) >= 0))
						clearInterval(interval)
				  })
				.fail(function(jqXHR, textStatus) {
				    console.log( "Error cancel·lant la generació: " + textStatus );
					$("#labelError").html(textStatus);
					$("#divError").show();
				  })
				.always(function() {
					$('#dataTransferIcon').css('visibility', 'hidden');
				  });	
			return ret;
			
		}
	</script>
</body>
</html>
