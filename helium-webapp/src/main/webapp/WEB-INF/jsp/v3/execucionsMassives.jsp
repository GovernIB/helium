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
	<title><spring:message code='expedient.massiva.proces' /></title>
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
			display: table-caption;
			width: 100%;
			padding-bottom: 0px;
		}
		.progress {
			margin-bottom: 10px;
		}
		.massiu-progres {
			float: left; width: calc(100% - 120px - 50% - 45px);
		}
		.massiu-data {
			float: left; width: 120px;
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
	</style>
	<script type="text/javascript">	
		var numResults = 10;

	    $(document).ready(function(){
			$("button[name=refrescar]").click(function() {
				refreshExecucionsMassives();
			});
			$("button[name=mesDades]").click(function() {
				var numFiles = $("#accordio_massiva .panel-heading").length;
				carregaExecucionsMassives(numFiles + 10);
			});
			carregaExecucionsMassives(numResults);
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
	    
	    function createBar(id, success, pendent, danger) {
	    	var text = '<div class="progress">';
	    	if (success > 0) {
		    	text += '<div class="progress-bar progress-bar-success" role="progressbar"' +
		    	  		'     aria-valuenow="'+success+'" aria-valuemin="0" aria-valuemax="100"' +
		    	  		'     style="width: '+success+'%">' +
		    	  		'  <span><div class="value">'+success+'%</div></span>' +
		    	  		'</div>';
	    	}
	    	if (pendent > 0) {
		    	text += '<div class="progress-bar progress-bar-warning" role="progressbar"' +
		    	  		'     aria-valuenow="'+pendent+'" aria-valuemin="0" aria-valuemax="100"' +
		    	  		'     style="width: '+pendent+'%">' +
		    	  		'  <span><div class="value">'+pendent+'%</div></span>' +
		    	  		'</div>';
	    	}
	    	if (danger > 0) {
		    	text += '<div class="progress-bar progress-bar-danger" role="progressbar"' +
		    	  		'     aria-valuenow="'+danger+'" aria-valuemin="0" aria-valuemax="100"' +
		    	  		'     style="width: '+danger+'%">' +
		    	  		'  <span><div class="value">'+danger+'%</div></span>' +
		    	  		'</div>';
	    	}
	    	text += '</div>';
	    	$("#"+id).html(text);
	    }
	    
	    function createTit(execucio) {
	    	var text =	
	    		'<div id="mass_' + execucio.id + '" href="#collapse_' + execucio.id + '" data-toggle="collapse" class="panel-heading clicable grup">' +
				'<div class="massiu-data">' + execucio.data + '</div>' +
				'<div class="massiu-progres" id="pbar_' + execucio.id + '"><span class="plabel" id="plabel_' + execucio.id + '">' + execucio.progres + '%</span></div>' +
				'<div class="massiu-accio">' + execucio.text +
				'<span class="badge">'+execucio.expedients.length+'</span>' +
				'</div>';
			if (execucio.expedients.length > 0) {
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
				return	'<tr id="massexp_' + expedient.id + '" + class="mass_expedient exp_' + execucio.id + ' ' + (j % 2 == 0 ? 'odd' : 'even') + '">' +
							'<td class="massiu-expedient">' + expedient.titol + '</td>' +
							'<td class="massiu-estat">' + estat + '</td>' +
   						'</tr>';
			}
	    }
		
		function cancelarExpedientMassiveAct(url,id) {
			$.post(url, { idExp: id }, function(data){
				refreshExecucionsMassives();
			});
		}
		
		function carregaExecucionsMassives(numResultats) {
			numResults = numResultats;
			$.ajax({
				url: "execucionsMassives/refreshBarsExpedientMassive",
				dataType: 'json',
				data: {results: numResultats},
				async: false,
				success: function(data){
					var length = data.length;
					var content = "";
					if (length == 0) {
						content = "<h4><spring:message code='execucions.massives.no'/></h4>";
					} else {
						content = '<div id="accordio_massiva">';
						for (var i = 0; i < length; i++) {
							execucio = data[i];
							content +=	'<div class="panel-group"><div class="panel panel-default">';
							content += createTit(execucio);
							content +=	'<div id="collapse_' + execucio.id + '" class="panel-collapse collapse">';
							
							var exps =  execucio.expedients.length;
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
								for (var j = 0; j < exps; j++) {
									content += putEstat(execucio, j, false);
								}
								content += '</tbody></table>';
							}
							content += '</div>';
							content +=	'</div></div>';
						}
					}
					$("#massiva_contens").html(content);
					for (var i = 0; i < length; i++) {
						execucio = data[i];
						createBar("pbar_" + execucio.id, execucio.success, execucio.pendent, execucio.danger);
					}
					
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
			})
			.fail(function( jqxhr, textStatus, error ) {
				var err = textStatus + ', ' + error;
				console.log( "Request Failed: " + err);
			})
			.always(function() {
				$("body").removeClass("loading");
			});
		}
		
		function refreshExecucionsMassives() {
			$.ajax({
				url: "execucionsMassives/refreshBarsExpedientMassive",
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
							createBar("pbar_" + execucio.id, execucio.success, execucio.pendent, execucio.danger);
						}
						
						for (var i = 0; i < length; i++) {
							execucio = data[i];
							var exps =  execucio.expedients.length;
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
									for (var j = 0; j < exps; j++) {
										content += createEstat(execucio, j);
									}
									content += '</tbody></table>';
								}
								content += '</div></div>';
								$("#accordio_massiva").prepend(content);
								createBar("pbar_" + execucio.id, execucio.success, execucio.pendent, execucio.danger);
								
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
							} else if (exps > 0) {
								// Actualitzam execucions existents
								for (var j = 0; exps > 0 && j < exps; j++) {
									putEstat(execucio, j, true);
								}
							}
						}
					}
				}
			})
			.fail(function( jqxhr, textStatus, error ) {
				var err = textStatus + ', ' + error;
				console.log( "Request Failed: " + err);
			})
		}
	</script>
</head>
<body>	
	<div id="massiva_contens"><div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div></div>
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.tancar"/></button>
		<button type="button" class="btn btn-primary" name="mesDades" value="mesDades"><span class="fa fa-plus"></span>&nbsp;<spring:message code="comuns.mes.dades"/></button>
		<button type="button" class="btn btn-primary" name="refrescar" value="refrescar"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comuns.refrescar"/></button>
	</div>
</body>
</html>
