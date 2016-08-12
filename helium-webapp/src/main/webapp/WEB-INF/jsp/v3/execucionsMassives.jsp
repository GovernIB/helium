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
				<spring:message code='comuns.massiu.entorn' />
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
	</style>
	<script type="text/javascript">	
		var page = 0;
		var numResults = 10;
		var nivell = "${nivell}";
		var col_span = "col-md-2";
		
		if (nivell == "admin"){
			col_span = "col-md-1 no-padding";
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
	    
	    function createBar(id, executades) {
	    	var text = '<div class="progress">';
	    	text += '<div class="progress-bar progress-bar-success" role="progressbar"' +
	    	  		'     aria-valuenow="'+executades+'" aria-valuemin="0" aria-valuemax="100"' +
	    	  		'     style="width: '+executades+'%">' +
	    	  		'  <span><div class="value">'+executades+'%</div></span>' +
	    	  		'</div>';
	    	
	    	text += '</div>';
	    	$("#"+id).html(text);
	    }
	    
	    function createTit(execucio) {
	    	var text =	
	    		'<div id="mass_' + execucio.id + '" href="#collapse_' + execucio.id + '" data-toggle="collapse" class="panel-heading clicable grup">' +
	    		'<div class="row pull-left massiu-dades">' +
	    		'<div class="col-md-3"><span class="desc-limit" title="' + execucio.text + '">' + execucio.text + '</span></div>' +
				'<div class="col-md-2 one-line"><div><span class="mass-badge badge in-line-badge">' + execucio.total + '</span></div> <div class="massiu-dades" id="pbar_' + execucio.id + '"><span class="plabel" id="plabel_' + execucio.id + '">' + execucio.executades + '%</span></div></div>' +
				//'<div class="col-md-1">' + execucio.ok + '</div>' + 
				'<div class="mass-error col-md-1">' + execucio.error + '</div>' + 
				'<div class="' + col_span + '">' + execucio.data + '</div>' + 
				'<div class="mass-data-fi ' + col_span + '">' + (execucio.dataFi != undefined ? execucio.dataFi : '') + '</div>' +
				'<div class="col-md-2">' + execucio.usuari + '</div>' + 
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
				estat = "<span class='fa fa-exclamation-circle'></span><label class='msg-error' data-msg-error='" + expedient.error + "' style='cursor: pointer;padding-left: 10px'><spring:message code='expedient.termini.estat.error'/></label>";
			} else if (expedient.estat == "ESTAT_FINALITZAT"){
				estat = "<span class='fa fa-check-circle'></span><label style='padding-left: 10px'><spring:message code='expedient.termini.estat.finalizat'/></label>";
			} else if (expedient.estat == "ESTAT_PENDENT"){
				estat = "<span class='fa fa-circle-o-notch fa-spin'></span><label style='padding-left: 10px'><spring:message code='expedient.termini.estat.pendent_solament'/>";
				if (expedient.tasca == "") {
					estat += "<i class='fa fa-times' onclick=\"cancelarExpedientMassiveAct('execucionsMassives/cancelExpedientMassiveAct','" + expedient.id + "')\" style='padding-left: 10px; cursor: pointer' title=\"<spring:message code='expedient.termini.estat.cancelat'/>\" alt=\"<spring:message code='expedient.termini.estat.cancelat'/>\"> </i>";
				}
				estat += "</label>";
			} else {
				estat = "<span class='fa fa-clock-o'></span><label style='padding-left: 10px'><spring:message code='expedient.termini.estat.process'/></label>";
			}
			
			if (actualizar) {				
				var estat_org = $("#massexp_" + expedient.id + " td:nth-child(2)").html();
				if (estat != estat_org) $("#massexp_" + expedient.id + " td:nth-child(2)").html(estat);	
			} else {
				return	'<tr id="massexp_' + expedient.id + '" + class="mass_expedient exp_' + execucio.id + ' ' + (j % 2 == 0 ? 'odd' : 'even') + (expedient.estat == "ESTAT_ERROR" ? ' danger' : '') + '">' +
							'<td class="massiu-expedient">' + expedient.titol + '</td>' +
							'<td class="massiu-estat">' + estat + '</td>' +
							'<td class="massiu-estat">' + (expedient.dataFi != undefined ? (expedient.dataFi) : '') + '</td>' +
   						'</tr>';
			}
	    }
		
		function cancelarExpedientMassiveAct(url,id) {
			$.post(url, { idExp: id }, function(data){
				refreshExecucionsMassives();
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
										'<div class="col-md-3"><strong><spring:message code="expedient.tramitacio.massiva.header.nom"/></strong></div>' +
										'<div class="col-md-2"><strong><spring:message code="expedient.tramitacio.massiva.header.execucio"/></strong></div>' +
										//'<div class="col-md-1"><strong><spring:message code="expedient.tramitacio.massiva.header.ok"/></strong></div>' +
										'<div class="col-md-1"><strong><spring:message code="expedient.tramitacio.massiva.header.error"/></strong></div>' +
										'<div class="' + col_span + '"><strong><spring:message code="expedient.tramitacio.massiva.header.dataInici"/></strong></div>' +
										'<div class="' + col_span + '"><strong><spring:message code="expedient.tramitacio.massiva.header.dataFi"/></strong></div>' +
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
							execucio = data[i];
							var error_class = (execucio.error > 0 ? "panel-danger" : "");
							
							content += '<div class="panel-group"><div id="panel_' + execucio.id + '" class="panel panel-default ' + error_class + '">';
							content += createTit(execucio);
							content +=	'<div id="collapse_' + execucio.id + '" class="panel-collapse collapse">';
							
							var exps =  execucio.total;
							if (exps > 0) {
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
			$('#mass_' + execucio.id + ' .massiu-dades .mass-error').text(execucio.error);
			$('#mass_' + execucio.id + ' .massiu-dades .mass-data-fi').text(execucio.dataFi != undefined ? execucio.dataFi : '');

			createBar("pbar_" + execucio.id, execucio.executades);

			$(".msg-error").unbind();
			$(".msg-error").bind({
				   mousemove : changeTooltipPosition,
				   mouseenter : showTooltip
			});
		}
		
		/*function refreshExecucionsMassives() {
			$.ajax({
				url: nivell + "/refreshBarsExpedientMassive",
				dataType: 'json',
				data: {results: numResults},
				async: false,
				success: function(data) {
					var length = data.length;
					var execucio = null;
					if (length > 0) {
						// Actualitzam barres de progrés
						for (var i = 0; i < length; i++) {
							execucio = data[i];
							createBar("pbar_" + execucio.id, execucio.executades);
						}
						
						for (var i = 0; i < length; i++) {
							execucio = data[i];
							var exps =  execucio.total;
							var content = "";
							// Afegim noves execucions
							if ($("#mass_" + execucio.id).length == 0) {
								content +=	'<div class="panel-group"><div class="panel panel-default">';
								content += createTit(execucio);
								content +=	'<div id="collapse_' + execucio.id + '" class="panel-collapse collapse">';
								
								if (exps > 0) {
									content += 
										'<table class="table table-striped table-bordered dataTable" id="massexpt_' + execucio.id + '">' +
											'<thead>' +
												'<tr>' +
													'<th class="massiu-expedient"><spring:message code="expedient.llistat.expedient"/></th>' +
													'<th class="massiu-estat"><spring:message code="expedient.consulta.estat"/></th>' +
												'</tr>' +
											'</thead>' +
										'<tbody>';
									content += '</tbody></table>';
								}
								content += '</div></div>';
								$("#accordio_massiva").prepend(content);
								createBar("pbar_" + execucio.id, execucio.executades);
								
							    $("#accordio_massiva .panel-heading").click(function() {
							    	$(this).find(".icona-collapse").toggleClass('fa-chevron-down');
							    	$(this).find(".icona-collapse").toggleClass('fa-chevron-up');
						   		});
							    
							    $(document).ready(function(){					 
									$(".msg-error").bind({
									   mousemove : changeTooltipPosition,
									   mouseenter : showTooltip
									});
								});
							}
						}
					}
				}
			})
			.fail(function( jqxhr, textStatus, error ) {
				var err = textStatus + ', ' + error;
				console.log( "Request Failed: " + err);
			})
		}*/
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
