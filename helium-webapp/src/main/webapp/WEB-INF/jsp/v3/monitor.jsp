<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code='expedient.monitor.det' /></title>
	<hel:modalHead/>
	<style type="text/css">	
		body.loading {
		    overflow: hidden;   
		}
		body.loading .wait {
		    display: block;
		}
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
		    table-layout:fixed;
		    border-collapse: collapse;
		 }
		
		.table td {
		    text-overflow:ellipsis;
		    overflow:hidden;
		    white-space:nowrap;
		}
		
		.monitor_hilo {
			width: 650px;
		}
		
		.contingut-carregant {
			text-align: center;
			padding: 8px;
		}
		
		.min_width {
			width: 120px;
		}
		.mid_width {
			width: 240px;
		}
	</style>
	<script type="text/javascript">
		$(document).ready(function(){
			$("button[name=refrescar]").click(function() {
				carregaMonitor();
			});
			carregaMonitor();
		});
		
		function carregaMonitor() {				
		    $.ajax({
		        url: "monitor/all",
		        dataType: 'json',
		        async: false,
		        success: function(data){
		            var content = "";
		            content += '<div class="mesures_monitor_sistema">';
			            content += '<table class="table-monitor-titol table table-striped table-bordered dataTable">';
			            content += '<thead><tr>';
			            content += '<th>Sistema</th>';
			            content += '</thead></tr>';
			            for (var i = 0; i < data.sistema.length; i++) {
			            	content += '<tr class="monitor_fila">';
			            	content += "<td>"+data.sistema[i]+"</td>";
			            	content += '</tr>';
			            }
			            content += '</table>';
		            content +=  '</div>';
		            
		            content +=  '<div id="mesures_monitor">' +
			                        '<table class="table-monitor table table-striped table-bordered dataTable">' +
			                        '<thead><tr>' +
			                        '<th class="monitor_hilo"><spring:message code="expedient.monitor.hilo"/></th>' +
			                        '<th class="min_width"><spring:message code="expedient.monitor.cputime"/></th>' +
			                        '<th class="mid_width"><spring:message code="expedient.monitor.estado"/></th>' +
			                        '<th class="min_width"><spring:message code="expedient.monitor.espera"/></th>' +
			                        '<th class="min_width"><spring:message code="expedient.monitor.blockedtime"/></th>' +
			                        '</thead></tr>';
			            for (var i = 0; i < data.hilo.length; i++) {
			                content +=  '<tr class="monitor_fila">' +
			                            '<td class="monitor_hilo">' + data.hilo[i] + '</td>' +
			                            '<td class="min_width">' + data.cputime[i] + '</td>' +
			                            '<td>' + data.estado[i] + '</td>' +
			                            '<td>' + data.espera[i] + '</td>' +
			                            '<td>' + data.blockedtime[i] + '</td>' +
			                            '</tr>';
			            }
			            content +=  '</table>' +
		                        '</div>';
		           
		            $("#monitor_contens").html(content);
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
	</script>
</head>
<body>
	<div id="monitor_contens">
		<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
	</div>
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.tancar"/></button>
		<button type="button" class="btn btn-primary" name="refrescar" value="refrescar"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comuns.refrescar"/></button>
	</div>
</body>
</html>
