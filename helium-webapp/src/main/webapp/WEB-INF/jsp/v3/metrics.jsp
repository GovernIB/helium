<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<% pageContext.setAttribute("idioma",org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()); %>

<html>
<head>
	<title><spring:message code='expedient.metriques' /></title>
	<hel:modalHead/>
	<!--  And include the metrics-watcher library -->
	<script src="<c:url value='/js/metrics/metrics-watcher.js'/>"></script>
	<!-- And include the metrics stylesheet -->
	<link href="<c:url value='/js/metrics/metrics-watcher-style.css'/>" rel="stylesheet">
	<script src="<c:url value='/js/Blob.js'/>"></script>
	<script src="<c:url value='/js/FileSaver.min.js'/>"></script>
	
	
	 <link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
 <link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
 <script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
 <script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${idioma}.js"/>"></script>

	<style type="text/css">	
		.progress-title {position: absolute; right: 42px;}
		.metricsWatcher .progress {margin-bottom: 10px;}
		#headingOne {cursor: pointer;}
		#headingTwo {cursor: pointer;}
		.pes {margin-bottom: 2px;}
		.max {margin-bottom: 0px;}
		.timers {margin-bottom: 0px !important; margin-top: 10px !important; cursor: pointer;}
		.mitja {background-image: linear-gradient(45deg, rgba(255, 255, 255, 0.25) 25%, transparent 25%, transparent 50%, rgba(255, 255, 255, 0.25) 50%, rgba(255, 255, 255, 0.25) 75%, transparent 75%, transparent);}
		#llegenda {padding: 15px;}
		.ocult {display: none;}
	</style>
	<script>
	
		var metricsData = ${metriques};
	
		$(document).ready(function() {
			$("#entornCodi").select2({
			    //width: 'resolve',
			    theme: "bootstrap",
			    placeholder: "<spring:message code='perfil.usuari.entorn'/>",
			    allowClear: true,
			    minimumResultsForSearch: 10
			});
			$("#expedientTipusId").select2({
			    //width: 'resolve',
			    theme: "bootstrap",
			    placeholder: "<spring:message code='perfil.usuari.tipus.expedient'/>",
			    allowClear: true,
			    minimumResultsForSearch: 10
			});

			// Event del botó exportar: desa les mètriques en un fitxer de text (metrics.json)
			$('#exportar').click(function() {
				var blob = new Blob([JSON.stringify(metricsData)], {type: "text/plain;charset=utf-8"});
				saveAs(blob, "metrics.json");
			});
			// Event del botó importar: click al imput tipus file, per tal d'obrir el diàleg de selecció de fitxer.
			// Un cop seleccionat el fitxer, es llença l'event change de l'imput tipus file.
			$('#importar').click(function() {
				$('#files').click();
				$('#netejar').trigger('click');
			});
			// Event del botó exportar: desa les mètriques en un fitxer de text (metrics.json)
			$('#netejar').click(function() {
				$("#entornCodi").val(null).trigger("change");
				$('#consultar').trigger('click');
			});
			// Event del botó exportar: desa les mètriques en un fitxer de text (metrics.json)
			$('#consultar').click(function() {
				var entornCodi = $("#entornCodi").val();
				var expedientTipus = $("#expedientTipusId").val();
				var filtre = null;
				if (entornCodi) 
					filtre = '.' + entornCodi;
				if (expedientTipus)
					filtre = filtre + '.' + expedientTipus;
				$(".counterDiv, .timers").each(function(){
					if (filtre == null) {
						$(this).removeClass("ocult");
					} else {
						if ($(".counterTitle", this).text().indexOf(filtre) != -1) {
							$(this).removeClass("ocult");
						} else {
							$(this).addClass("ocult");
						}
					}
				});
			});
			
			// Event per a carregar el fitxer. TODO: Comprovar que el fitxer és JSON
			$('#files').on('change', function(event){
				var files = event.target.files; // FileList object
			    // Loop through the FileList and render image files as thumbnails.
			    if(files && files[0]) { 
			    	var reader = new FileReader();
			    	
			    	// Closure to capture the file information.
			        reader.onload = (function(theFile) {
			          return function(e) {
			            metricsData = JSON.parse(e.target.result);//reader.result);
				      	$('#counters').empty();
				      	$('#timers').empty();
				      	$('#timers').append(
				      			"<h4 class='ocult'><spring:message code='metriques.timers.dominis'/></h4>" +
								"<div id='timers-dominis' class='ocult'></div>" +
								"<h4 class='ocult'><spring:message code='metriques.timers.handlers'/></h4>" +
								"<div id='timers-handlers' class='ocult'></div>" +
								"<h4 class='ocult'><spring:message code='metriques.timers.jobs'/></h4>" +
								"<div id='timers-jobs' class='ocult'></div>" +
								"<h4 class='ocult'><spring:message code='metriques.timers.generics'/></h4>" +
								"<div id='timers-generics' class='ocult'></div>");
				      	metricsWatcher.clearGraphs();
				      	addGraphs(metricsData);
						metricsWatcher.initGraphs();
						metricsWatcher.updateGraphs(metricsData);
						
			          };
			        })(files[0]);
			    	
			      	reader.readAsText(files[0]);
			   	}
			});
			//document.getElementById('files').addEventListener('change', handleFileSelect, false);

			// Al fer clic en un timer, es desplegarà la informació extesa d'aquest
			$('#timers').delegate(".timers", "click", function(event) {
				event.stopPropagation();
				var divTimer = $("#" + $(this).data('id'));
				
				if (!divTimer.hasClass('loaded')) {
					metricsWatcher.clearGraphs();
					metricsWatcher.addTimer(
							$(this).data('id'),			// Div
							$(this).data('class'),		// Class
							$(this).data('nom'), 		// Metrics name
							$(this).data('max-target'),	// Max target value (frequency)
		 					" ",						// Titol
		 					" ",						// Nom event
		 					$(this).data('max-timer'));	// Duracio màxima
		 			metricsWatcher.initGraphs();
		 			metricsWatcher.updateGraphs(metricsData);
		 			divTimer.addClass('loaded');
				}
		 		
				divTimer.collapse('toggle');
			});
			
			// Afegim la informació gràfica de les mètriques 
			addGraphs(metricsData);
			metricsWatcher.initGraphs();
			metricsWatcher.updateGraphs(metricsData);

			// Al canviar l'entorn, s'actualitzen els tipus d'expedient
			$("#entornCodi").on('change', function () {
				$("#expedientTipusId").empty();
				$("#expedientTipusId").append($('<option value=""></option>'));
				if ($(this).val()) {
					$.ajax({
					    url:'perfil/consulta/' + $(this).val(),
					    type:'GET',
					    dataType: 'json',
					    success: function(json) {
					        $.each(json, function(i, value) {
					        	$("#expedientTipusId").append($('<option>').text(value.nom).attr('value', value.id));
					        });
					        $("#expedientTipusId").select2({
					        	width: 'resolve',
							    theme: "bootstrap",
							    placeholder: "<spring:message code='perfil.usuari.tipus.expedient'/>",
							    allowClear: true,
							    minimumResultsForSearch: 10});
					        $("#expedientTipusId").val(null).trigger("change");
					    }
					});
				} else {
					$("#expedientTipusId").select2({
			        	width: 'resolve',
					    theme: "bootstrap",
					    placeholder: "<spring:message code='perfil.usuari.tipus.expedient'/>",
					    allowClear: true,
					    minimumResultsForSearch: 10});
					$("#expedientTipusId").val(null).trigger("change");
				}
			});
			
		});
		
		function addGraphs(metricsData) {

			var countersList = new Array();
			
			var counterKeys = Object.keys(metricsData.counters);
			var maxCounter = 0;

			for (var counter in metricsData.counters) {
				var count =  metricsData.counters[counter].count;
				countersList.push({name: counter, value: count});
				if (count > maxCounter)
					maxCounter = count;
			}

			countersList.sort(function(a, b) {
				if (a.value < b.value) {
					return 1;
				}
				if (a.value > b.value) {
					return -1;
				}
				return 0;
			});

			for (var i in countersList) {
				var counter = countersList[i];
				var nomCounter = "counter_" + i;
				var index = counter.name.lastIndexOf('.');
				var classCounter = counter.name.substring(0, index);
				var metricNameCounter = counter.name.substring(index + 1);
				//var titol = counterKeys[i].substring(counterKeys[i].indexOf('helium'));

				$('#counters').append('<div id="' + nomCounter + '" class="counterDiv"></div>');

				metricsWatcher.addCounter(
				nomCounter,		 				// Div
				classCounter, 					// Class
				metricNameCounter, 				// Metrics name
				maxCounter,						// Max target value (frequency)
				counter.name);
				//"Counter '" + metricNameCounter + "'");		// Titol
			}

			var timersList = new Array();
			
			var timerKeys = Object.keys(metricsData.timers);
			var maxPes = 0;
			var maxMax = 0;
			
			for (var timer in metricsData.timers) {
				var mitja = metricsData.timers[timer].mean;
				var maxim = metricsData.timers[timer].max;
				var num = metricsData.timers[timer].count;
				var pes = mitja * num; 
				timersList.push({name: timer, count: num, mean: mitja, max: maxim, weight: pes});
				if (pes > maxPes)
					maxPes = pes;
				if (maxim > maxMax)
					maxMax = maxim;
			}
			
			timersList.sort(function(a, b) {
				if (a.weight < b.weight) {
					return 1;
				}
				if (a.weight > b.weight) {
					return -1;
				}
				return 0;
			});
			
// 			if (maxCounter > 1000) {
// 				var order = Math.floor(Math.log(maxTimer) / Math.LN10
//                        + 0.000000001); // because float math sucks like that
// 				base = Math.pow(10,order);
// 				maxTimer = Math.ceil(maxTimer / base) * base;
// 			}

			for (var i in timersList) {
				var timer = timersList[i];
				var nomTimer = "timer_" + i;
				var index = timer.name.lastIndexOf('.');
				var classTimer = timer.name.substring(0, index);
				var metricNameTimer = timer.name.substring(index + 1);
				//var titol = timer.substring(timerKeys[i].indexOf('helium'));

				var pctPes = timer.weight * 100 / maxPes;
				var pctMitja = timer.mean * 100 / maxMax;
				var pctMaxim = (timer.max - timer.mean) * 100 / maxMax;
				var pes = Math.round(timer.weight * 100) / 100;
				var mitja = Math.round(timer.mean * 100) / 100;
				var maxim = Math.round(timer.max * 100) / 100;
				
				var nomSeccio = "timers-generics";
				if (timer.name.indexOf(".DominiDao.") != -1) {
					nomSeccio = "timers-dominis";
				} else if (timer.name.indexOf(".DominiHelper.") != -1) {
					nomSeccio = "timers-dominis";
				} else if (timer.name.indexOf(".Action.handler.") != -1) {
					nomSeccio = "timers-handlers";
				} else if (timer.name.indexOf(".job.executor.") != -1) {
					nomSeccio = "timers-jobs";
				}
				
				var seccio = $("#" + nomSeccio);
				
				seccio.removeClass("ocult");
				seccio.prev().removeClass("ocult");
				seccio.append(
						"<div class='panel panel-default timers' data-id='" + nomTimer + "' data-class='" + classTimer + "' data-nom='" + metricNameTimer + "' data-max-target='" + timer.count + "' data-max-timer='" + timer.max + "'>" +
						"	<div class='panel-body timer-body'>" +
						"		<div class='counterTitle'>" + timer.name + " (" + timer.count + " execucions)</div>" +
						"		<div class='progress pes'>" +
						"			<div class='progress-bar progress-bar-success' style='width: " + pctPes + "%;'>" + pes + "</div>" +
						"		</div>" +
						"		<div class='progress max'>" +
						"			<div class='progress-bar progress-bar-striped progress-bar-danger mitja' style='width: " + pctMitja + "%;'>" + mitja + "</div>" +
						"			<div class='progress-bar progress-bar-danger' style='width: " + pctMaxim + "%;'>" + maxim + "</div>" +
						"		</div>" +
						"	</div>" +
						"</div>" +
						"<div class='collapse' id='" + nomTimer + "'></div><div style='clear:both;'></div>");

// 				metricsWatcher.addTimer(
// 				nomTimer, 							// Div
// 				classTimer, 						// Class
// 				metricNameTimer, 					// Metrics name
// 				1,									// Max target value (frequency)
// 				"Timer '" + metricNameTimer + "'",	// Titol
// 				"consultar",						// Nom event
// 				maxTimer);							// Duracio màxima

			}

// 			$('body').append('<div class="row"><div id="jvm"></div></div>');
// 			metricsWatcher.addJvm("jvm", "jvm", "JVM Statistics");
		}

// 		function keepUpdatingGraphs() {
// 			downloadMetricData();
// 			setTimeout(keepUpdatingGraphs, 60000); // 1 minute
// 		}

// 		function downloadMetricData() {
// // 			metricsWatcher.updateGraphs(exampleMetricsData);
// 			// Normally metric data will be loaded from a servlet with an Ajax call like the following.
// 			// For this example, it is hardcoded.
// 					var url = '<c:url value="/nodeco/v3/metrics"/>';
// 					$.ajax({
// 						contentType : "application/json",
// 						url : url,
// 						success : function(data) {
// 							metricsWatcher.updateGraphs(data);
// 						},
// 						error : function(xhr, ajaxOptions, thrownError) {
// 							//alert("Error - " + xhr.status + "Message: " + thrownError);
// 							metricsWatcher.initGraphs();
// 						},
// 						async : false
// 					});
// 		}
	</script>
</head>
<body>
	<div class=" row well">
		<div class="col-md-4">
			<div class="form-group">
				<label for="entornCodi" class="control-label col-xs-4 "><spring:message code="perfil.usuari.entorn"/></label>
				<div class="controls col-xs-8">
					<select id="entornCodi" class="form-control" name="entornCodi" title="<spring:message code='perfil.usuari.entorn'/>" style="width:100%">
						<option value=""></option>
						<c:forEach var="opt" items="${entorns}"><option value="${opt.codi}">${opt.nom}</option></c:forEach>
					</select>
				</div>
			</div>
		</div>
		<div class="col-md-4">
			<div class="form-group">
				<label for="expedientTipusId" class="control-label col-xs-4 "><spring:message code="perfil.usuari.tipus.expedient"/></label>
				<div class="controls col-xs-8">
					<select id="expedientTipusId" class="form-control" name="expedientTipusId" title="<spring:message code='perfil.usuari.tipus.expedient'/>" style="width:100%">
						<option value=""></option>
					</select>
				</div>
			</div>
		</div>
		<div class="col-md-4">
			<div class="pull-right">
<!-- 				<input type="hidden" name="consultaRealitzada" value="true"/> -->
				<input type="file" id="files" name="files" style="display:none;"/>
				<button id="importar" type="button" name="accio" value="importar" class="btn btn-default"><spring:message code="comu.importar"/></button>
				<button id="exportar" type="button" name="accio" value="exportar" class="btn btn-default"><spring:message code="comu.filtre.exportar"/></button>
				<button id="netejar" type="button" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
				<button id="consultar" type="button" name="accio" value="consultar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
			</div>
		</div>
	</div>
	<!-- Nav tabs -->
  	<ul class="nav nav-tabs" role="tablist">
    	<li role="presentation" class="active"><a href="#counters-tab" aria-controls="counters-tab" role="tab" data-toggle="tab">Counters</a></li>
    	<li role="presentation"><a href="#timers-tab" aria-controls="timers-tab" role="tab" data-toggle="tab">Timers</a></li>
  	</ul>

  	<!-- Tab panes -->
	<div class="tab-content">
  		<div role="tabpanel" class="tab-pane fade in active" id="counters-tab">
  			<div id="counters" class="panel-body"></div>
  		</div>
  		<div role="tabpanel" class="tab-pane fade" id="timers-tab">
  			<div id="llegenda">
				<div class='counterTitle'><spring:message code="metriques.timers.llegenda"/></div>
				<div class='progress pes'>
					<div class='progress-bar progress-bar-success' style='width: 100%;'><spring:message code="metriques.timers.pes"/></div>
				</div>
				<div class='progress max'>
					<div class='progress-bar progress-bar-striped progress-bar-danger mitja' style='width: 50%;'><spring:message code="metriques.timers.temps.mig"/></div>
					<div class='progress-bar progress-bar-danger' style='width: 50%;'><spring:message code="metriques.timers.temps.maxim"/></div>
				</div>
			</div>
      		<div id="timers" class="panel-body">
				<h4 class="ocult"><spring:message code="metriques.timers.dominis"/></h4>
				<div id="timers-dominis" class="ocult"></div>
				<h4 class="ocult"><spring:message code="metriques.timers.handlers"/></h4>
				<div id="timers-handlers" class="ocult"></div>
				<h4 class="ocult"><spring:message code="metriques.timers.jobs"/></h4>
				<div id="timers-jobs" class="ocult"></div>
				<h4 class="ocult"><spring:message code="metriques.timers.generics"/></h4>
				<div id="timers-generics" class="ocult"></div>
      		</div>
  		</div>
	</div>
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
	</div>
</body>
</html>
