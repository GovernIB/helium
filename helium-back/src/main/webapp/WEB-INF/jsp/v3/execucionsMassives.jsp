<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page import = "java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title>
		<c:choose>
			<c:when test="${nivell == 'admin'}">
				<c:choose>
					<c:when test="${dadesPersona.admin}">
						<spring:message code='comuns.massiu.helium' />
					</c:when>
					<c:otherwise>
						<spring:message code='comuns.massiu.entorn' arguments='${entornActual.nom}' />
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<spring:message code='comuns.massiu.usuari' />
			</c:otherwise>
		</c:choose>
	</title>
	<hel:modalHead/>
	
	<style type="text/css">	
		.table > tbody > tr > td {
			margin-bottom: 0px;
			margin-top: 0px !important;
			padding-bottom: 0px;
		    padding-top: 0px;
			border-bottom: 1px solid #cccccc; 
			overflow: auto; 
			padding: 2px 10px !important;
		}
		.table {
			margin-top: 0px;
		    table-layout:fixed;
		    border-collapse: collapse;
		}		
		.table td {
		    text-overflow:ellipsis;
		    overflow:hidden;
		    white-space:nowrap;
		}		
		.table td label {
		   font-weight: normal;
		}
		.contingut-carregant {
			text-align: center;
			padding: 8px;
		}
		.panel-default > .panel-heading {
			display: table;
			width: 100%;
			padding-bottom: 0px;
		}
		.progress {
			margin-bottom: 10px;
		}
		.massiu-progres {
			float: left; width: calc(100% - 120px - 50% - 45px);
		}
		.massiu-dades {
			width: 100%;
		}
		.massiu-accio {
			float: left; width: 50%;padding-left: 10px;
		}
		.badge {
			margin-left: 10px;
		}
		.panel-group {
		    margin-bottom: 5px;
		}
		.panel-group .panel {
		    margin-bottom: 5px;
		}
		.desc-limit{
			display: inline-block;
		    white-space: nowrap;
		    overflow: hidden !important;
		    text-overflow: ellipsis;
		    max-width: 425px;
		    margin-bottom: -5px;
		}
		.special-margin{
			margin-bottom: 10px;
		}
		.no-padding{
			padding:0px;
		}
		.in-line-badge{
			margin-left: 0px;
			margin-right: 5px;
		}
		.one-line{
			display: inline-flex;
		}
		.massiu-expedient-edp {
			width: 350px !important;
		}
		.massiu-estat-edp {
			text-overflow: ellipsis;
		    overflow: hidden;
		    white-space: normal !important;
		}
		.massiu-data-edp {
			width: 175px !important;
		}
		.lin-exp {
			margin-top: 7px;
		}
		.bexpborrartotslogs {
			margin-bottom: 8px;
		}
	</style>
	<script type="text/javascript">	
		var page = 0;
		var numResults = 10;
		var nivell = "${nivell}";

		// Funció per mostrar l'error amb salts de línia i caracters estranys
		function escapeHtml(html){
			  var text = document.createTextNode(html.replace(/\\n/g, '<br/>'));
			  var p = document.createElement('p');
			  p.appendChild(text);
			  return p.innerHTML;
			}

	    $(document).ready(function(){
		    
			$("button[name=refrescar]").click(function() {
				carregaExecucionsMassives(page, true);
			});
			$("button[name=nextPage]").click(function() {
				var nextPage = page + 1;
				carregaExecucionsMassives(nextPage, false);
			});

			$("button[name=previousPage]").click(function() {
				var previousPage = page > 0 ? (page - 1) : 0;
				carregaExecucionsMassives(previousPage, false);
			});
			
			carregaExecucionsMassives(0, true);
		});
	    
		var changeTooltipPosition = function(event) {
		 	$('div.tooltip').css({left: 20});
		};
		
		var showTooltip = function(event) {
		  	$('div.tooltip').remove();
		 	$("<div class='tooltip'>" + $(this).data("msg-error") + "</div>").css({
	            position: "absolute",
	            display: "none",
	            right: 20,
	            top: event.pageY,
	            top:event.pageY+4,
	            "background-color": "#FFFFCA",
	            color: "#000023",
	            opacity: 0.90,
				"background-clip": "padding-box",
			    border: "1px solid rgba(0, 0, 0, 0.15)",
			    "border-radius": "4px",
			    "box-shadow": "0 6px 12px rgba(0, 0, 0, 0.176)",
			    "font-size": "14px",
			    "list-style": "outside none none",
			    margin: "0",
			    "min-width": "160px",
			    padding: "10px",
			    "text-align": "left",
			    "word-wrap": "break-word",
			    "z-index": "1000"
	        }).appendTo("body").fadeIn(200);
		 	changeTooltipPosition(event);
			 
		 	$('div.tooltip').bind({
			   mouseleave: hideTooltip
			});							
		};
	 
		var hideTooltip = function(event) {
			var el = document.elementFromPoint(event.pageX, event.pageY);
	   		$('div.tooltip').remove();
		};
		
		function bindButtons() {
// 			$(".bexpborrarlog").off('click');
			$(".bexpborrartotslogs").off('click');
			
// 			$(".bexpborrarlog").on('click', function(){
// 				var btn = $(this);
// 				var expId = btn.data("id");
// 				var expTipus = btn.data("expedienttipus");
// 				$("body").css("cursor", "progress");
// 				$.ajax({
// 					type: "POST",
// 					url: '<c:url value="/nodeco/expedientTipus/borra_logsexp.html"/>',
// 					dataType: 'json',
// 					data: {expedientId: expId, expedientTipusId: expTipus},
// 					success: function(data){
// 						btn.after('<span class="exp_info">' + data.resultat + '</span>');
// 						btn.remove();
// 						$("body").css("cursor", "default");
// 					}
// 				})
// 				.fail(function( jqxhr, textStatus, error ) {
// 					var err = textStatus + ', ' + error;
// 					console.log( "Request Failed: " + err);
// 					btn.after('<span class="exp_info">' + err + '</span>');
// 					btn.remove();
// 					$("body").css("cursor", "default");
// 				})
// 			});
			
			$(".bexpborrartotslogs").on('click', function(){
				var btn = $(this);
				var exmid = btn.data("exmid");
				var dpid = btn.data("dpid");
				var cont_exp = $('#eliminacio_' + exmid);
				var expTipus = btn.data("expedienttipus");
				var expsId = []; 
				$(".bexpborrarlog", cont_exp).each(function(){
					expsId.push($(this).data("id"));
				});
				$("body").css("cursor", "progress");
				$.ajax({
					type: "POST",
					url: '<c:url value="/v3/expedientTipus/' + expTipus + '/borra_logsexps"/>',
					dataType: 'json',
					data: {definicioProcesId: dpid, expedientsId: JSON.stringify(expsId), expedientTipusId: expTipus},
					success: function(data){
						btn.after('<div class="alert alert-warning" role="alert">'
								  + '<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>'
								  + '<span class="sr-only">Error:</span>'
								  + ' ' + data.resultat
								  + '</div>');
						btn.remove();
						$("body").css("cursor", "default");
					}
				})
				.fail(function( jqxhr, textStatus, error ) {
					var err = textStatus + ', ' + error;
					console.log( "Request Failed: " + err);
					btn.after('<div class="alert alert-danger" role="alert">'
							  + '<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>'
							  + '<span class="sr-only">Error:</span>'
							  + ' ' + err
							  + '</div>');
					btn.remove();
					$("body").css("cursor", "default");
				})
			});
		}
	    
	    function createBar(id, executades) {
	    	var text = '<div class="progress">';
	    	text += '<div class="progress-bar progress-bar-striped progress-bar-success" role="progressbar"' +
	    	  		'     aria-valuenow="'+executades+'" aria-valuemin="0" aria-valuemax="100"' +
	    	  		'     style="width: '+executades+'%">' +
	    	  		'  <span><div class="value">'+executades+'%</div></span>' +
	    	  		'</div>';
	    	
	    	text += '</div>';
	    	$("#"+id).html(text);
	    }
	    
	    function createTit(execucio) {
	    	var title = execucio.text + "\nEntorn: " + execucio.entornCodi + " - " + execucio.entornNom + " \nExpedient tipus: " + execucio.expedientTipusCodi + " - " + execucio.expedientTipusNom;  
	    	let cancelada = execucio.processat < execucio.total && execucio.dataFi;
	    	var text =	
	    		'<div id="mass_' + execucio.id + '" href="#collapse_' + execucio.id + '" data-toggle="collapse" class="panel-heading clicable grup">' +
	    		'<div class="row pull-left massiu-dades">' +
	    		'<div class="col-md-2"><span class="desc-limit" title="' + title + '">' + execucio.text + '</span></div>' +
				'<div class="col-md-2 one-line"><div><span class="mass-badge badge in-line-badge">' + execucio.total + '</span></div> ' + 
												'<div class="massiu-dades" id="pbar_' + execucio.id + '"><span class="plabel" id="plabel_' + execucio.id + '">' + execucio.executades + '%</span></div> ' + 
												'<div class="mass-cancelar" style="position: absolute; right: -10px; display: ' + (execucio.executades == 100 ? 'none' : 'inline') + '"><a id="mass_cancelar_' + execucio.id + '" class="btn btn-default btn-xs" data-id="' + execucio.id + '" title="<spring:message code="comu.boto.cancelar"/>"><span class="fa fa-stop text-danger"></span></a></div>' +
												'<div class="mass-rependre" style="position: absolute; right: -10px; z-index: 999; display: ' + (!execucio.cancelada ? 'none' : 'inline') + '"><a id="mass_rependre_' + execucio.id + '" class="btn btn-default btn-xs" data-id="' + execucio.id + '" title="<spring:message code="comu.boto.rependre"/>"><span class="fa fa-play"></span></a></div>' +
												'<div class="mass-reintentar" style="position: absolute; right: -10px; z-index: 999; display: ' + (execucio.error && execucio.executades == 100 && !execucio.cancelada? 'inline' : 'none') + '"><a id="mass_reintentar_' + execucio.id + '" class="btn btn-default btn-xs" data-id="' + execucio.id + '" title="<spring:message code="comu.boto.reintentar"/>"><span class="fa fa-repeat"></span></a></div></div>' +		
				'<div class="mass-processat col-md-1 text-right">' + execucio.processat + '</div>' + 
				'<div class="mass-error col-md-1 text-right">' + execucio.error + '</div>' + 
				'<div class="col-md-2">' + execucio.data + '</div>' + 
				'<div class="mass-data-fi col-md-2">' + (execucio.dataFi != undefined ? execucio.dataFi : '') + '</div>' +
				'<div class="col-md-1">' + execucio.usuari + '</div>' + 
				'</div>';
			if (execucio.total > 0) {
				text +=	'<div class="pull-right">' +
						'<span class="icona-collapse fa fa-chevron-down"></span>' +
						'</div>' +
						'</div>';
			}
			return text;
	    }
	    
	    function putEstat(execucio, j, actualizar) {
	    	var expedient = execucio.expedients[j];
	    	var estat = "";
			var opcions = "";
			var row = "";
			if (expedient.estat == "ESTAT_CANCELAT"){
				estat = "<span class='fa fa-check-circle'></span><label style='padding-left: 10px'><spring:message code='expedient.termini.estat.cancelat'/></label>";
			} else if (expedient.estat == "ESTAT_ERROR"){
				if( expedient.error != undefined && expedient.error != ""){
					if (execucio.tipus == 'ELIMINAR_VERSIO_DEFPROC') {
						var error_tractat = expedient.error.replace(/\\/g, '');
						var split_error = error_tractat.split("####exp_afectats");
						var texte_error = split_error[0];
						var text_expedients = "";
						if (split_error[1] != undefined) {
							var dades_error = split_error[1].split("###");
							var dpId= dades_error[1];
							if (dades_error[2] != undefined) {
								var expedients_error = dades_error[2].split("&&&");
								expedients_error.shift();
								if (expedients_error.length > 0) {
									text_expedients = '<div id="eliminacio_' + expedient.id + '"><br><br>Expedients relacionats:<br>';
									$.each(expedients_error, function(key, exp) {
										var exp_split = exp.split("@"); 
				                        text_expedients += '<div class="lin-exp"><span class="pull-left bexpborrarlog" data-id="' + exp_split[1] + '" data-expedienttipus="' + execucio.expedientTipusId + '"><strong>' + exp_split[0] + '</strong></span></div><br>';
				                    });
									text_expedients += '<br><div class="pull-right"><button class="btn btn-default bexpborrartotslogs" data-dpid="' + dpId + '" data-exmid="' + expedient.id + '" data-expedienttipus="' + execucio.expedientTipusId + '">Programar borrat de logs dels expedients afectats i eliminar definició de procés</button></div></div>'
								}
							}
						}
						estat = texte_error + text_expedients;
					} else {
						estat = "<span class='fa fa-exclamation-circle'></span><label class='msg-error' data-msg-error='" + escapeHtml(expedient.error) + "' style='cursor: pointer;padding-left: 10px'><spring:message code='expedient.termini.estat.error'/></label>";					
					}
				} else {
					estat = "<span class='fa fa-exclamation-circle'></span><label class='msg-error' data-msg-error='<spring:message code="expedient.tramitacio.massiva.error.desconegut"/>' style='cursor: pointer;padding-left: 10px'><spring:message code='expedient.termini.estat.error'/></label>";					
				}
			} else if (expedient.estat == "ESTAT_FINALITZAT"){
				estat = "<span class='fa fa-check-circle'></span><label style='padding-left: 10px'><spring:message code='expedient.termini.estat.finalizat'/></label>";
			} else if (expedient.estat == "ESTAT_PENDENT"){
				estat = "<span class='fa fa-circle-o-notch fa-spin'></span><label style='padding-left: 10px'><spring:message code='expedient.termini.estat.pendent_solament'/>";
				if (expedient.tasca == "") {
					estat += "<i class='fa fa-times' onclick=\"cancelarExpedientMassiveAct('execucionsMassives/cancelExpedientMassiveAct','" + expedient.id + "')\" style='padding-left: 10px; cursor: pointer' title=\"<spring:message code='expedient.termini.estat.cancelat'/>\" alt=\"<spring:message code='expedient.termini.estat.cancelat'/>\"> </i>";
				}
				estat += "</label>";
			}
			
			if (actualizar) {				
				var estat_org = $("#massexp_" + expedient.id + " td:nth-child(2)").html();
				if (estat != estat_org) $("#massexp_" + expedient.id + " td:nth-child(2)").html(estat);	
			} else {
				if (execucio.tipus == 'ELIMINAR_VERSIO_DEFPROC') {
					return	'<tr id="massexp_' + expedient.id + '" + class="mass_expedient exp_' + execucio.id + ' ' + (j % 2 == 0 ? 'odd' : 'even') + (expedient.estat == "ESTAT_ERROR" ? ' danger' : '') + '">' +
					'<td class="massiu-expedient-edp">' + expedient.titol + '</td>' +
					'<td class="massiu-estat-edp">' + estat + '</td>' +
					'<td class="massiu-data-edp">' + (expedient.dataFi != undefined ? (expedient.dataFi) : '') + '</td>' +
					'</tr>';	
				} else {
					return	'<tr id="massexp_' + expedient.id + '" + class="mass_expedient exp_' + execucio.id + ' ' + (j % 2 == 0 ? 'odd' : 'even') + (expedient.estat == "ESTAT_ERROR" ? ' danger' : '') + '">' +
					'<td class="massiu-expedient">' + expedient.titol + '</td>' +
					'<td class="massiu-estat">' + estat + '</td>' +
					'<td class="massiu-estat">' + (expedient.dataFi != undefined ? (expedient.dataFi) : '') + '</td>' +
					'</tr>';	
				}
			}
	    }
		
		function cancelarExpedientMassiveAct(url,id) {
			$.post(url, { idExp: id }, function(data){
				carregaExecucionsMassives();
			});
		}
		
		function cancelarExecucioMassiva(id) {
			// Confirmació
			if (!confirm("<spring:message code="expedient.tramitacio.massiva.cancelar.confirm"/>"))
				return false;

			webutilEsborrarAlertes();
			$a = $('#mass_cancelar_' + id);
			$a.attr('disabled', 'disabled');
			$.ajax({
	            url : '<c:url value="/v3/execucionsMassives/cancelExecucioMassiva"/>', 
	            type : 'POST',
	            data : {id : id},
	            dataType : 'json',
	            success : function(data) {
	            	if (data.error) {
	            		webutilAlertaError(data.missatge);
	            	} else {
	            		webutilAlertaSuccess(data.missatge);
	            		carregaExecucionsMassives(page, true)
	            	}
	            },
	            error: function (request, status, error) {
	            	// Mostra l'error
	            	webutilAlertaError(request.responseText);
	            },
	            complete: function() {
					$a.removeAttr('disabled');
	            }
	        });	
		}
		
		function rependreExecucioMassiva(id) {
			// Confirmació
			if (!confirm("<spring:message code="expedient.tramitacio.massiva.rependre.confirm"/>"))
				return false;

			webutilEsborrarAlertes();
			$a = $('#mass_rependre_' + id);
			$a.attr('disabled', 'disabled');
			$.ajax({
	            url : '<c:url value="/v3/execucionsMassives/rependreExecucioMassiva"/>', 
	            type : 'POST',
	            data : {id : id},
	            dataType : 'json',
	            success : function(data) {
	            	if (data.error) {
	            		webutilAlertaError(data.missatge);
	            	} else {
	            		webutilAlertaSuccess(data.missatge);
	            		carregaExecucionsMassives(page, true)
	            	}
	            },
	            error: function (request, status, error) {
	            	// Mostra l'error
	            	console.log("bar");
	            	console.log(status);
	            	console.log(error);
	            	webutilAlertaError(request.responseText);
	            },
	            complete: function() {
					$a.removeAttr('disabled');
	            }
	        });	
		}
		
		function reintentarExecucioMassiva(id) {
			// Confirmació
			if (!confirm("<spring:message code="expedient.tramitacio.massiva.reintentar.confirm"/>"))
				return false;

			webutilEsborrarAlertes();
			$a = $('#mass_reintentar_' + id);
			$a.attr('disabled', 'disabled');
			$.ajax({
	            url : '<c:url value="/v3/execucionsMassives/reintentarExecucioMassiva"/>', 
	            type : 'POST',
	            data : {id : id},
	            dataType : 'json',
	            success : function(data) {
	            	if (data.error) {
	            		webutilAlertaError(data.missatge);
	            	} else {
	            		webutilAlertaSuccess(data.missatge);
	            		carregaExecucionsMassives(page, true)
	            	}
	            },
	            error: function (request, status, error) {
	            	// Mostra l'error
	            	console.log("bar");
	            	console.log(status);
	            	console.log(error);
	            	webutilAlertaError(request.responseText);
	            },
	            complete: function() {
					$a.removeAttr('disabled');
	            }
	        });	
		}
		
		function carregaExecucionsMassives(numResultats,header) {
			$.ajax({
				url: nivell + "/refreshBarsExpedientMassive",
				dataType: 'json',
				data: {results: numResultats},
				async: false,
				success: function(data){
					var length = data.length;
					if (length > 0) {
						page = numResultats;
						var content = "";
						if(header) {
							if (length == 0) {
								content = "<h4><spring:message code='execucions.massives.no'/></h4>";
							} else {
								content = '<div class="panel panel-default panel-heading special-margin">' +
									'<div class="row massiu-dades">' +
										'<div class="col-md-2"><strong><spring:message code="expedient.tramitacio.massiva.header.nom"/></strong></div>' +
										'<div class="col-md-2"><strong><spring:message code="expedient.tramitacio.massiva.header.execucio"/></strong></div>' +
										'<div class="col-md-1"><strong><spring:message code="expedient.tramitacio.massiva.header.processats"/></strong></div>' +
										'<div class="col-md-1"><strong><spring:message code="expedient.tramitacio.massiva.header.error"/></strong></div>' +
										'<div class="col-md-2"><strong><spring:message code="expedient.tramitacio.massiva.header.dataInici"/></strong></div>' +
										'<div class="col-md-2"><strong><spring:message code="expedient.tramitacio.massiva.header.dataFi"/></strong></div>' +
										'<div class="col-md-2"><strong><spring:message code="expedient.tramitacio.massiva.header.usuari"/></strong></div>' + 
									'</div>'+ 
									'<div class="pull-right">' +
										'<span>&nbsp;</span>' +
									'</div>'+ 
								'</div>';
								content += '<div id="accordio_massiva">';
							}
							$("#massiva_contens").html(content);
						}
						
	
						content = "";
						for (var i = 0; i < length; i++) {
							debugger;
							execucio = data[i];
							var error_class = (execucio.error > 0 ? "panel-danger" : "");
							
							content += '<div class="panel-group"><div id="panel_' + execucio.id + '" class="panel panel-default ' + error_class + '">';
							content += createTit(execucio);
							content +=	'<div id="collapse_' + execucio.id + '" class="panel-collapse collapse">';
							
							var exps =  execucio.total;
							if (exps > 0) {
								if(execucio.tipus=="ALTA_MASSIVA"){
									content += 
									'<div id="downloadCSV_' + execucio.id + '" class="panel panel-default">'+
										'<a id="downloadCSVa_' + execucio.id + '" href="<c:url value="/v3/execucionsMassives/getCsvOriginalContent/'+execucio.id +'"/>" class="badge" title="<spring:message code="expedient.document.descarregar"/>">'+
											'<span class="fa fa-download"></span> <spring:message code="expedient.consulta.recuperarCSV"></spring:message>'+
										'</a>' +
										'<a id="downloadResultat_' + execucio.id + '" href="<c:url value="/v3/execucionsMassives/getCsvResultat/'+execucio.id +'"/>" class="badge" title="<spring:message code="expedient.document.descarregar"/>">'+
											'<span class="fa fa-download"></span> <spring:message code="expedient.consulta.recuperarResultatCSV"></spring:message>'+
										'</a>' +
									'</div>'  ;
									
								}
								if(execucio.tipus=='REINTENTAR_CONSULTA_ANOTACIONS'){
									content += 
										'<table class="table table-striped table-bordered dataTable" id="massexpt_' + execucio.id + '">' +
										'<thead>' +
											'<tr>' +
												'<th class="massiu-expedient"><spring:message code="anotacio.llistat.accio.massiva.titol"/></th>' +
												'<th class="massiu-estat"><spring:message code="expedient.consulta.estat"/></th>' +
												'<th class="massiu-expedient"><spring:message code="expedient.tramitacio.massiva.header.data"/></th>' +
											'</tr>' +
										'</thead>' +
									'<tbody>';
								}
								if(execucio.tipus=='REINTENTAR_PROCESSAMENT_ANOTACIONS'){
									content += 
										'<table class="table table-striped table-bordered dataTable" id="massexpt_' + execucio.id + '">' +
										'<thead>' +
											'<tr>' +
												'<th class="massiu-expedient"><spring:message code="anotacio.llistat.accio.massiva.titol"/></th>' +
												'<th class="massiu-estat"><spring:message code="expedient.consulta.estat"/></th>' +
												'<th class="massiu-expedient"><spring:message code="expedient.tramitacio.massiva.header.data"/></th>' +
											'</tr>' +
										'</thead>' +
									'<tbody>';
								}
								if(execucio.tipus=='REINTENTAR_PROCESSAMENT_ANOTACIONS_NOMES_ANNEXOS'){
									content += 
										'<table class="table table-striped table-bordered dataTable" id="massexpt_' + execucio.id + '">' +
										'<thead>' +
											'<tr>' +
												'<th class="massiu-expedient"><spring:message code="anotacio.llistat.accio.massiva.titol"/></th>' +
												'<th class="massiu-estat"><spring:message code="expedient.consulta.estat"/></th>' +
												'<th class="massiu-expedient"><spring:message code="expedient.tramitacio.massiva.header.data"/></th>' +
											'</tr>' +
										'</thead>' +
									'<tbody>';
								}
								if(execucio.tipus=='REINTENTAR_MAPEIG_ANOTACIONS'){
									content += 
										'<table class="table table-striped table-bordered dataTable" id="massexpt_' + execucio.id + '">' +
										'<thead>' +
											'<tr>' +
												'<th class="massiu-expedient"><spring:message code="anotacio.llistat.accio.massiva.titol"/></th>' +
												'<th class="massiu-estat"><spring:message code="expedient.consulta.estat"/></th>' +
												'<th class="massiu-expedient"><spring:message code="expedient.tramitacio.massiva.header.data"/></th>' +
											'</tr>' +
										'</thead>' +
									'<tbody>';
								}
								if(execucio.tipus=='ESBORRAR_ANOTACIONS'){
									content += 
										'<table class="table table-striped table-bordered dataTable" id="massexpt_' + execucio.id + '">' +
										'<thead>' +
											'<tr>' +
												'<th class="massiu-expedient"><spring:message code="anotacio.llistat.accio.massiva.titol"/></th>' +
												'<th class="massiu-estat"><spring:message code="expedient.consulta.estat"/></th>' +
												'<th class="massiu-expedient"><spring:message code="expedient.tramitacio.massiva.header.data"/></th>' +
											'</tr>' +
										'</thead>' +
									'<tbody>';
								}
								if (execucio.tipus == 'ELIMINAR_VERSIO_DEFPROC') {
									content += 
										'<table class="table table-striped table-bordered dataTable" id="massexpt_' + execucio.id + '">' +
											'<thead>' +
												'<tr>' +
													'<th class="massiu-expedient-edp"><spring:message code="expedient.llistat.expedient"/></th>' +
													'<th class="massiu-estat-edp"><spring:message code="expedient.consulta.estat"/></th>' +
													'<th class="massiu-data-edp"><spring:message code="expedient.tramitacio.massiva.header.data"/></th>' +
												'</tr>' +
											'</thead>' +
										'<tbody>';
								} else if (execucio.tipus!='REINTENTAR_CONSULTA_ANOTACIONS' 
										&& execucio.tipus!='REINTENTAR_PROCESSAMENT_ANOTACIONS'
										&& execucio.tipus!='REINTENTAR_MAPEIG_ANOTACIONS'
										&& execucio.tipus!='REINTENTAR_PROCESSAMENT_ANOTACIONS_NOMES_ANNEXOS'
										&& execucio.tipus!='ESBORRAR_ANOTACIONS'){
									content += 
				
										'<table class="table table-striped table-bordered dataTable" id="massexpt_' + execucio.id + '">' +
											'<thead>' +
												'<tr>' +
													'<th class="massiu-expedient"><spring:message code="expedient.llistat.expedient"/></th>' +
													'<th class="massiu-estat"><spring:message code="expedient.consulta.estat"/></th>' +
													'<th class="massiu-expedient"><spring:message code="expedient.tramitacio.massiva.header.data"/></th>' +
												'</tr>' +
											'</thead>' +
										'<tbody>';
								}
								
								content += '</tbody></table>';
							}
							content += '</div>';
							content +=	'</div></div>';
						}
						$("#accordio_massiva").html(content);
						
						for (var i = 0; i < length; i++) {
							execucio = data[i];
							createBar("pbar_" + execucio.id, execucio.executades);
						}
						
						$('.mass-cancelar a').click(function(event) {
							cancelarExecucioMassiva($(this).data('id'));
							event.preventDefault();
							return false;
						});
						$('.mass-rependre a').click(function(event) {
							rependreExecucioMassiva($(this).data('id'));
							event.preventDefault();
							return false;
						});
						$('.mass-reintentar a').click(function(event) {
							reintentarExecucioMassiva($(this).data('id'));
							event.preventDefault();
							return false;
						});
					    $("#accordio_massiva .panel-heading").click(function() {
					    	$(this).find(".icona-collapse").toggleClass('fa-chevron-down');
					    	$(this).find(".icona-collapse").toggleClass('fa-chevron-up');
	
					    	if(!$($(this).attr('href')).hasClass('collapse in')){
						        var mass_id = $(this).prop('id').split('_')[1];
					        	$.ajax({
									url: "getExecucioMassivaDetall",
									dataType: 'json',
									data: {execucioMassivaId: mass_id},
									async: false,
									success: function(data) {
										var execucio = data;
										mostraDetall(execucio);
									}
								})
								.fail(function( jqxhr, textStatus, error ) {
									var err = textStatus + ', ' + error;
									console.log( "Request Failed: " + err);
								})
					        }
				   		});
					} else {
						$("#massiva_contens").html('<div class="well"><span class="fa fa-info-circle"></span> No hi ha execucions massives a mostrar</div>');	
					}
				}
			})
			.fail(function( jqxhr, textStatus, error ) {
				var err = textStatus + ', ' + error;
				console.log( "Request Failed: " + err);
			})
			.always(function() {
				$("body").removeClass("loading");
			});
		}

		function mostraDetall(execucio) {
			var exps = '';
			var expedients = execucio.expedients
			for (var j = 0; j < expedients.length; j++) {
				exps += putEstat(execucio, j, false);
			}
			$('#massexpt_' + execucio.id + ' tbody').html(exps);

			if (execucio.error > 0) {
				$('#panel_' + execucio.id).addClass('panel-danger');
			} else if($('#panel_' + execucio.id).hasClass('panel-danger')) {
				$('#panel_' + execucio.id).removeClass('panel-danger');
			}
			
			$('#mass_' + execucio.id + ' .massiu-dades .mass-badge').text(execucio.total);
			$('#mass_' + execucio.id + ' .massiu-dades .mass-processat').text(execucio.processat);
			$('#mass_' + execucio.id + ' .massiu-dades .mass-error').text(execucio.error);
			$('#mass_' + execucio.id + ' .massiu-dades .mass-data-fi').text(execucio.dataFi != undefined ? execucio.dataFi : '');
			let cancelada = execucio.processat < execucio.total && execucio.dataFi;
			if (execucio.executades == 100) {
				$('#mass_' + execucio.id + ' .mass-cancelar').hide();
			} else {
				$('#mass_' + execucio.id + ' .mass-cancelar').show();	
			}
			if (cancelada) {
				// Cancel·lada
				$('#mass_' + execucio.id + ' .mass-rependre').show();
			} else {
				$('#mass_' + execucio.id + ' .mass-rependre').hide();
			}
			if (!cancelada && execucio.error && execucio.executades == 100) {
				// Error
				$('#mass_' + execucio.id + ' .mass-reintentar').show();
			} else {
				$('#mass_' + execucio.id + ' .mass-reintentar').hide();
			}
			createBar("pbar_" + execucio.id, execucio.executades);

			$(".msg-error").unbind();
			$(".msg-error").bind({
				   mousemove : changeTooltipPosition,
				   mouseenter : showTooltip
			});
			bindButtons();
		}
		
	</script>
</head>
<body>	
	<div id="massiva_contens"><div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div></div>
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
		
		<button type="button" class="btn btn-primary" name="previousPage" value="previousPage"><span class="fa fa-arrow-left"></span>&nbsp;<spring:message code="comuns.previous"/></button>
		<button type="button" class="btn btn-primary" name="nextPage" value="nextPage"><spring:message code="comuns.next"/>&nbsp;<span class="fa fa-arrow-right"></span></button>
		<button type="button" class="btn btn-primary" name="refrescar" value="refrescar"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comuns.refrescar"/></button>
	</div>
</body>
</html>
