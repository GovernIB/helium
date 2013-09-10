<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

	
<div class="dialog-form-massive-bar" id="dialog-form-mass" style="display:none" title="<fmt:message key='expedient.massiva.proces' />">
	<div id="massiva_contens"></div>
</div>
<div id="dialog-error" title="Error" style="display:none">
  <p id="mass-error"></p>
</div>
<div class="dialog-form-temps" id="dialog-form-temps" style="display:none" title="<fmt:message key='expedient.mesura.temps' />">
	<div id="temps_contens"></div>
</div>
<div class="wait"></div>

<script>
	var timer = null;
	var numResults = 10;
	var progres;
	var datasets;
	var fam = "";
	
	$(function() {
		$( "#dialog-form-mass" ).dialog({
			autoOpen: false,
			height: 520,
			width: 900,
			modal: true,
			resizable: false,
			buttons: {
				<fmt:message key='comuns.tancar' />: function() {
					//clearTimeout(timer);
					$(this).dialog("close");
				},
				"<fmt:message key='comuns.mes.dades' />": function() {
					var numFiles = $("#accordio_massiva h3").length;
					carregaExecucionsMassives(numFiles + 10);
				},
				"<fmt:message key='comuns.refrescar' />": function() {
					refreshExecucionsMassives();
				}
			},
			close: function(){
				//clearTimeout(timer);
			}
		});
		
		$( "#dialog-form-temps" ).dialog({
			autoOpen: false,
			height: 700,
			width: 1000,
			modal: true,
			resizable: false,
			buttons: {
				<fmt:message key='comuns.tancar' />: function() {
					$(this).dialog("close");
				},
				"<fmt:message key='temps.exportar' />": function() {
					window.location="/helium/mesura/mesuresTempsExport.html";
				}
			},
			close: function(){
			}
		});
		
		$( "#dialog-error" ).dialog({
			autoOpen: false,
			height: 420,
			width: 1000,
			modal: true,
			resizable: true			
		});
		
		$("#botoMassiu")
		.click(function() {
			$("body").addClass("loading");
			carregaExecucionsMassives(10);
			$( "#dialog-form-mass" ).dialog( "open" );
		});
		
		$("#botoTemps")
		.click(function() {
			$("body").addClass("loading");
			carregaMesuresTemps();
			$( "#dialog-form-temps" ).dialog( "open" );
		});
	});
	
	function alerta(msg) {
		$("#mass-error").html(msg);
		$( "#dialog-error" ).dialog( "open" );
	}
	
	function carregaMesuresTempsFamilia(familia) {
		fam = familia;
		carregaMesuresTemps();
	}
	
	function carregaMesuresTemps() {
		$.ajax({
			url: "/helium/mesura/mesuresTemps.html",
			dataType: 'json',
			data: {familia: fam},
 			async: false,
			success: function(data){
				var length = 0;
				var content = "";
				if ($.isEmptyObject(data)) {
					content = "<h4><fmt:message key='execucions.mesura.temps.no'/></h4>";
					$("#temps_contens").html(content);
				} else {
					content = 	'<ul id="temps_tabnav">';
					if (fam == "") {
						content +=	'<li class="active"><span><fmt:message key="temps.familia.tot"/></span></li>';
					} else {
						content +=	'<li><a href="javascript:carregaMesuresTempsFamilia(\'\');"><fmt:message key="temps.familia.tot"/></a></li>';
					}
					for (var key in data.familia) {
						if (fam == key) {
							content +=	'<li class="active"><span>' + data.familia[key] + '</span></li>';
						} else {
							content +=	'<li><a href="javascript:carregaMesuresTempsFamilia(\'' + key + '\');">' + data.familia[key] + '</a></li>';
						}
					}
					content += 	'</ul>';
					
					length = data.clau.length;
					content += 	'<div id="mesures_temps">' +
								'<div class="temps_well">' + 
								'<div class="temps_fila fila_titol">' +
								'<div class="temps_col0"></div>' +
								'<div class="temps_col1"><fmt:message key="temps.clau"/></div>' +
								'<div class="temps_col2"><fmt:message key="temps.darrera"/></div>' +
								'<div class="temps_col2"><fmt:message key="temps.minima"/></div>' +
								'<div class="temps_col2"><fmt:message key="temps.maxima"/></div>' +
								'<div class="temps_col2"><fmt:message key="temps.numMesures"/></div>' +
								'<div class="temps_col2"><fmt:message key="temps.mitja"/></div>' +
								'<div class="temps_col2"><fmt:message key="temps.periode"/></div>' +
								'</div>';
					for (var i = 0; i < length; i++) {
						content += 	'<div class="temps_fila">' +
									'<div class="temps_col0"><input type="checkbox" name="' + data.clau[i] + '" checked="checked"></div>' +
									'<div class="temps_col1">' + data.clau[i] + '</div>' +
									'<div class="temps_col2">' + data.darrera[i] + '</div>' +
									'<div class="temps_col2">' + data.minima[i] + '</div>' +
									'<div class="temps_col2">' + data.maxima[i] + '</div>' +
									'<div class="temps_col2">' + data.numMesures[i] + '</div>' +
									'<div class="temps_col2">' + data.mitja[i] + '</div>' +
									'<div class="temps_col2">' + data.periode[i] + '</div>' +
									'</div>';
					}
					content += 	'<div class="temps_chart" id="placeholder"></div>' +
								'<div class="temps_legend" id="temps_legend"></div>' +
								'</div>' +
								'</div>';
					
					$("#temps_contens").html(content);
				
					datasets = data.series;
					var i = 0;
					$.each(datasets, function(key, val) {
						val.color = i;
						++i;
					});
					series = [];
					for (var key in data.series) {
						series.push(data.series[key]);
					}
					if ($.isFunction($.plot)) {
						$.plot($("#placeholder"), series, 
								{ 
									series: {
										points: { show: true},
										lines: { show: true}
									}, 
									xaxis: {
						    			mode: "time",
						    			timeformat: "%H:%M"
									},
									legend: {
										container: "#temps_legend"
									}
								});
					}
					$(":checkbox").click(plotAccordingToChoices);
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
	
	function plotAccordingToChoices() {

		var data = [];

		$("#mesures_temps").find("input:checked").each(function () {
			var key = $(this).attr("name");
			if (key && datasets[key]) {
				data.push(datasets[key]);
			}
		});

		$.plot("#placeholder", data, 
				{
					series: {
						points: { show: true},
						lines: { show: true}
					}, 
					xaxis: {
		    			mode: "time",
		    			timeformat: "%H:%M"
					},
					legend: {
						container: "#temps_legend"
					}
				});
	}
	
	function carregaExecucionsMassives(numResultats) {
		numResults = numResultats;
		$.ajax({
			url: "/helium/expedient/refreshBarsExpedientMassive.html",
			dataType: 'json',
			data: {results: numResultats},
			async: false,
			success: function(data){
					var length = data.length;
					var execucio = null;
					var content = "";
					if (length == 0) {
						content = "<h4><fmt:message key='execucions.massives.no'/></h4>";
					} else {
						content = '<div id="accordio_massiva">';
						for (var i = 0; i < length; i++) {
							execucio = data[i];
							var exps =  execucio.expedients.length;
							
							content +=	'<h3 id="mass_' + execucio.id + '">' +
											'<div class="massiu-data">' + execucio.data + '</div>' +
											'<div class="massiu-accio">' + execucio.text + '</div>' +
											'<div class="massiu-progres" id="pbar_' + execucio.id + '"><span class="plabel" id="plabel_' + execucio.id + '">' + execucio.progres + '%</span></div>' +
										'</h3>';
							content +=	'<div>';
							if (exps > 0) {
								var tableHeader = '<table class="displaytag" id="massexpt_' + execucio.id + '">' +
													'<thead>' +
													'<tr>' +
														'<th class="massiu-expedient"><fmt:message key="expedient.llistat.expedient"/></th>' +
														'<th class="massiu-estat"><fmt:message key="expedient.consulta.estat"/></th>' +
														'<th class="massiu-opcions"></th>' +
													'</tr>' +
													'</thead>' +
													'<tbody>';
								content += tableHeader;
								for (var j = 0; j < exps; j++) {
									var expedient = execucio.expedients[j];
									var estat = "";
									var opcions = "";
									if (expedient.estat == "ESTAT_CANCELAT"){
										estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" alt=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" src=\"/helium/img/mass_canceled.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.cancelat'/></label>";
									} else if (expedient.estat == "ESTAT_ERROR"){
										estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.error'/>\" alt=\"<fmt:message key='expedient.termini.estat.error'/>\" src=\"/helium/img/mass_error.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.error'/></label>";
										opcions = "<label class='msg-error' style=\"cursor: pointer\" onclick=\"alerta('" + expedient.error + "')\"><fmt:message key='expedient.termini.estat.error'/></label>";
									} else if (expedient.estat == "ESTAT_FINALITZAT"){
										estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.finalizat'/>\" alt=\"<fmt:message key='expedient.termini.estat.finalizat'/>\" src=\"/helium/img/mass_fin.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.finalizat'/></label>";
									} else if (expedient.estat == "ESTAT_PENDENT"){
										estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.pendent_solament'/>\" alt=\"<fmt:message key='expedient.termini.estat.pendent_solament'/>\" src=\"/helium/img/mass_pend.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.pendent_solament'/></label>";
										if (expedient.tasca == "") {
											opcions = "<img style=\"cursor: pointer\" onclick=\"cancelarExpedientMassiveAct('/helium/expedient/cancelExpedientMassiveAct.html','" + expedient.id + "')\" border=\"0\" title=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" alt=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" src=\"/helium/img/mass_cancel.png\">";
										}
									} else {
										estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.process'/>\" alt=\"<fmt:message key='expedient.termini.estat.process'/>\" src=\"/helium/img/mass_prog.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.process'/></label>";
									}
									
									var row =	'<tr id="massexp_' + expedient.id + '" + class="mass_expedient exp_' + execucio.id + ' ' + (j % 2 == 0 ? 'odd' : 'even') + '">' +
													'<td class="massiu-expedient">' + expedient.titol + '</td>' +
													'<td class="massiu-estat">' + estat + '</td>' +
													'<td class="massiu-opcions">' + opcions + '</td>' +
						   						'</tr>';
									content += row;
								}
								content += '</tbody></table>';
							}
							content += '</div>';
						}
					}
					$("#massiva_contens").html(content);
					if ($.isFunction($.fn.progressbar)) {
						for (var i = 0; i < length; i++) {
							execucio = data[i];
							$("#pbar_" + execucio.id).progressbar({value: execucio.progres});
						}
	    			}
					$( "#accordio_massiva" ).accordion({
					      collapsible: true,
					      active: false
				    });
					if (length > 0) {
						//timer = setTimeout(refreshExecucionsMassives, 1500);
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
	
	function refreshExecucionsMassives() {
		$.ajax({
			url: "/helium/expedient/refreshBarsExpedientMassive.html",
			dataType: 'json',
			data: {results: numResults},
			async: false,
			success: function(data){
					var length = data.length;
					var execucio = null;
					if (length > 0) {
						// Actualitzam barres de progrés
						if ($.isFunction($.fn.progressbar)) {
							for (var i = 0; i < length; i++) {
								execucio = data[i];
								progres = execucio.progres;
		    					$("#pbar_" + execucio.id).progressbar("value", progres);
		    					$("#plabel_" + execucio.id).text(progres + "%");
							}
		    			}
						
						for (var i = 0; i < length; i++) {
							execucio = data[i];
							var exps =  execucio.expedients.length;
							var content = "";
							// Afegim noves execucions
							if ($("#mass_" + execucio.id).length == 0) {
								content +=	'<h3 id="mass_' + execucio.id + '">' +
												'<div class="massiu-data">' + execucio.data + '</div>' +
												'<div class="massiu-accio">' + execucio.text + '</div>' +
												'<div class="massiu-progres" id="pbar_' + execucio.id + '"><span id="plabel_' + execucio.id + '">' + execucio.progres + '%</span></div>' +
											'</h3>';
								content +=	'<div>';
								if (exps > 0) {
									var tableHeader = '<table class="displaytag" id="massexpt_' + execucio.id + '">' +
														'<thead>' +
														'<tr>' +
															'<th class="massiu-expedient"><fmt:message key="expedient.llistat.expedient"/></th>' +
															'<th class="massiu-estat"><fmt:message key="expedient.consulta.estat"/></th>' +
															'<th class="massiu-opcions"></th>' +
														'</tr>' +
														'</thead>' +
														'<tbody>';
									content += tableHeader;
									for (var j = 0; j < exps; j++) {
										var expedient = execucio.expedients[j];
										var estat = "";
										var opcions = "";
										if (expedient.estat == "ESTAT_CANCELAT"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" alt=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" src=\"/helium/img/mass_canceled.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.cancelat'/></label>";
										} else if (expedient.estat == "ESTAT_ERROR"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.error'/>\" alt=\"<fmt:message key='expedient.termini.estat.error'/>\" src=\"/helium/img/mass_error.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.error'/></label>";
											opcions = "<label class='msg-error' onclick=\"alerta('" + expedient.error + "')\"><fmt:message key='expedient.termini.estat.error'/></label>";
										} else if (expedient.estat == "ESTAT_FINALITZAT"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.finalizat'/>\" alt=\"<fmt:message key='expedient.termini.estat.finalizat'/>\" src=\"/helium/img/mass_fin.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.finalizat'/></label>";
										} else if (expedient.estat == "ESTAT_PENDENT"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.pendent_solament'/>\" alt=\"<fmt:message key='expedient.termini.estat.pendent_solament'/>\" src=\"/helium/img/mass_pend.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.pendent_solament'/></label>";
											if (expedient.tasca == "") {
												opcions = "<img style=\"cursor: pointer\" onclick=\"cancelarExpedientMassiveAct('/helium/expedient/cancelExpedientMassiveAct.html','" + expedient.id + "')\" border=\"0\" title=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" alt=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" src=\"/helium/img/mass_cancel.png\">";
											}
										} else {
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.process'/>\" alt=\"<fmt:message key='expedient.termini.estat.process'/>\" src=\"/helium/img/mass_prog.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.process'/></label>";
										}
										
										var row =	'<tr id="massexp_' + expedient.id + '" + class="mass_expedient exp_' + execucio.id + ' ' + (j % 2 == 0 ? 'odd' : 'even') + '">' +
														'<td class="massiu-expedient">' + expedient.titol + '</td>' +
														'<td class="massiu-estat">' + estat + '</td>' +
														'<td class="massiu-opcions">' + opcions + '</td>' +
							   						'</tr>';
										content += row;
									}
									content += '</tbody></table>';
								}
								content += '</div>';
								$("#accordio_massiva").prepend(content);
								if ($.isFunction($.fn.progressbar)) {
									$("#pbar_" + execucio.id).progressbar({ value: execucio.progres	});
								}
							} else {
								// Actualitzam execucions existents
								if (exps > 0) {
									for (var j = 0; j < exps; j++) {
										var expedient = execucio.expedients[j];
										var estat = "";
										var opcio = "";
										if (expedient.estat == "ESTAT_CANCELAT"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" alt=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" src=\"/helium/img/mass_canceled.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.cancelat'/></label>";
										} else if (expedient.estat == "ESTAT_ERROR"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.error'/>\" alt=\"<fmt:message key='expedient.termini.estat.error'/>\" src=\"/helium/img/mass_error.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.error'/></label>";
											opcio = "<label class='msg-error' onclick=\"alerta('" + expedient.error + "')\"><fmt:message key='expedient.termini.estat.error'/></label>";
										} else if (expedient.estat == "ESTAT_FINALITZAT"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.finalizat'/>\" alt=\"<fmt:message key='expedient.termini.estat.finalizat'/>\" src=\"/helium/img/mass_fin.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.finalizat'/></label>";
										} else if (expedient.estat == "ESTAT_PENDENT"){
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.pendent_solament'/>\" alt=\"<fmt:message key='expedient.termini.estat.pendent_solament'/>\" src=\"/helium/img/mass_pend.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.pendent_solament'/></label>";
											if (expedient.tasca == "") {
												opcio = "<img style=\"cursor: pointer\" onclick=\"cancelarExpedientMassiveAct('/helium/expedient/cancelExpedientMassiveAct.html','" + expedient.id + "')\" border=\"0\" title=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" alt=\"<fmt:message key='expedient.termini.estat.cancelat'/>\" src=\"/helium/img/mass_cancel.png\">";
											}
										} else {
											estat = "<img border=\"0\" title=\"<fmt:message key='expedient.termini.estat.process'/>\" alt=\"<fmt:message key='expedient.termini.estat.process'/>\" src=\"/helium/img/mass_prog.png\"><label style=\"padding-left: 10px\"><fmt:message key='expedient.termini.estat.process'/></label>";
										}
										
										var estat_org = $("#massexp_" + expedient.id + " td:nth-child(2)").html();
										var opcio_org = $("#massexp_" + expedient.id + " td:nth-child(3)").html();
										if (estat != estat_org) $("#massexp_" + expedient.id + " td:nth-child(2)").html(estat);										
										if (opcio != opcio_org) $("#massexp_" + expedient.id + " td:nth-child(3)").html(opcio);
									}
								}
							}
						}
					}
					//timer = setTimeout(refreshExecucionsMassives, 1500);
				}
		})
		.fail(function( jqxhr, textStatus, error ) {
			var err = textStatus + ', ' + error;
			console.log( "Request Failed: " + err);
		})
// 		.always(function() { });
	}
	
	function cancelarExpedientMassiveAct(url,id) {
		$.post(url, { idExp: id }, function(data){});
	}
</script>