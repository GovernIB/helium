<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page import = "java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code='expedient.mesura.temps' /></title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.flot.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.flot.time.min.js"/>"></script>
	
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
			margin-top: 10px;
		 }
		.contingut-carregant {
			text-align: center;
			padding: 8px;
		}
		.temps_chart {
			margin-top: 10px;
			margin-left: 25px;
			margin-bottom: 20px;
			width: 600px;
			height: 400px;
			float: left;
		}
		.temps_legend {
			margin-top: 10px;
			margin-bottom: 20px;
			float: left;
			width: calc(100% - 625px);
		}
		
		.temps_legend td {
			padding: 3px;
		}
		.no-element {
			padding: 8px;
		}
		.first-element {
			width: 35px;
		}
	</style>
	<script type="text/javascript">
		var datasets;
		var fam = "";
	    var previousPoint = null;

	    $(document).ready(function(){
			$("button[name=refrescar]").click(function() {
				carregaMesuresTemps();
			});

			$("button[name=export]").click(function() {
				window.location="mesuresTemps/export";
			});
			carregaMesuresTemps();
		});
	    
		function carregaMesuresTempsFamilia(familia) {
			fam = familia;
			carregaMesuresTemps();
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
	                    },
	                    grid: {
	                        hoverable: true
	                    }
	                });
	        $("#placeholder").bind("plothover", plotHover);
		}
		
		function showTooltip(x, y, contents) {
	        $("<div id='tooltip'>" + contents + "</div>").css({
	            position: "absolute",
	            display: "none",
	            "z-index": 1003,
	            top: y - 20,
	            left: x + 5,
	            border: "1px solid #FFFF80",
	            padding: "2px",
	            "background-color": "#FFFFCA",
	            color: "#000023",
	            opacity: 0.80
	        }).appendTo("body").fadeIn(200);
	    }
	    
	    function plotHover (event, pos, item) {
	        if (item) {
	            if (previousPoint != item.dataIndex) {
	                previousPoint = item.dataIndex;
	                $("#tooltip").remove();
	                y = item.datapoint[1];
	                showTooltip(item.pageX, item.pageY, item.series.label + " (" + y + "ms)");
	            }
	        } else {
	            $("#tooltip").remove();
	            previousPoint = null;           
	        }
	    }
		function carregaMesuresTemps() {
			$('button[name="submit"]',parent.document).prop('disabled',true);
            $('button[name="export"]',parent.document).prop('disabled',true);
            $('button[name="refrescar"]',parent.document).prop('disabled',true);
			
			$('button[name="refrescar"] > span',parent.document).remove();
			$('button[name="refrescar"]',parent.document).prepend('<i class="fa fa-refresh fa-spin"></i>');
	        $.ajax({
	            url: "mesuresTemps/all",
	            dataType: 'json',
	            data: {familia: fam},
	            async: false,
	            success: function(data){
	                var length = 0;
	                var consultaBBDD = (fam == "sql_jbpm" || fam == "sql_helium");
	               
	                var content =     '<ul id="temps_tabnav" class="nav nav-tabs">';
	                if (fam == "") {
	                    content +=    '<li class="active"><a href="javascript:carregaMesuresTempsFamilia(\'\');"><spring:message code="temps.familia.tot"/></a></li>';
	                } else {
	                    content +=    '<li><a href="javascript:carregaMesuresTempsFamilia(\'\');"><spring:message code="temps.familia.tot"/></a></li>';
	                }
	                for (var key in data.familia) {
	                    if (fam == key) {
	                        content +=    '<li class="active"><a href="javascript:carregaMesuresTempsFamilia(\'' + key + '\');">' + data.familia[key] + '</a></li>';
	                    } else {
	                        content +=    '<li><a href="javascript:carregaMesuresTempsFamilia(\'' + key + '\');">' + data.familia[key] + '</a></li>';
	                    }
	                }
	                content +=     '</ul>';
	                if ($.isEmptyObject(data.clau)) {
	                    content += (fam == "" ?
	                            "<div class='no-element'><spring:message code='execucions.mesura.temps.no'/></div>" :
	                            "<div class='no-element'><spring:message code='execucions.mesura.temps.bbdd.no'/></div>");
	                    $("#temps_contens").html(content);
	                } else {
	                   
	                    length = data.clau.length;
	                   
	                    content +=  '<table id="mesures_temps" class="table table-striped table-bordered table-hover dataTable">' +
	                                '<thead>' +
	                                '<th class="first-element"></th>' +
	                                '<th class="temps_col1"><spring:message code="temps.clau"/></th>' +
	                                '<th class="temps_col2"><spring:message code="temps.darrera"/></th>' +
	                                '<th class="temps_col2"><spring:message code="temps.minima"/></th>' +
	                                '<th class="temps_col2"><spring:message code="temps.maxima"/></th>' +
	                                '<th class="temps_col2"><spring:message code="temps.numMesures"/></th>' +
	                                '<th class="temps_col2"><spring:message code="temps.mitja"/></th>' +
	                                '<th class="temps_col2"><spring:message code="temps.periode"/></th>' +
	                                '</thead><tbody>';
	                    for (var i = 0; i < length; i++) {
	                        content +=  '<tr>' +
	                                    '<td class="temps_col0"><input type="checkbox" name="' + data.clau[i] + '" checked="checked"></td>' +
	                                    '<td class="temps_col1">' + data.clau[i] + '</td>' +
	                                    '<td class="temps_col2">' + data.darrera[i] + '</td>' +
	                                    '<td class="temps_col2">' + data.minima[i] + '</td>' +
	                                    '<td class="temps_col2">' + data.maxima[i] + '</td>' +
	                                    '<td class="temps_col2">' + data.numMesures[i] + '</td>' +
	                                    '<td class="temps_col2">' + data.mitja[i] + '</td>' +
	                                    '<td class="temps_col2">' + data.periode[i] + '</td>' +
	                                    '</tr>';
	                    }
	                    var noMostrar = "";
	                    if(consultaBBDD) {
	                        noMostrar += "style='display: none'";
	                    }
	                    content +=     '</tbody></table>' +
	                    			'<div class="temps_chart" id="placeholder" '+noMostrar+'></div>' +
	                                '<div class="temps_legend" id="temps_legend" '+noMostrar+'></div>' +
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
	                        if (!(key == 'Consultas totales de JBPM' || key == 'Consultas totales de Helium')) {
	                            series.push(data.series[key]);
	                        }
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
	                                    },
	                                    grid: {
	                                        hoverable: true
	                                    }
	                                });
	                        $("#placeholder").bind("plothover", plotHover);
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

	            $('button[name="submit"]',parent.document).prop('disabled',false);
	            $('button[name="export"]',parent.document).prop('disabled',false);
	            $('button[name="refrescar"]',parent.document).prop('disabled',false);
	            
	            $('button[name="refrescar"] > i',parent.document).remove();
				$('button[name="refrescar"]',parent.document).prepend('<span class="fa fa-refresh"></span>');
	        });
	    }
	</script>
</head>
<body>
	
	<div id="temps_contens"><div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div></div>
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.tancar"/></button>
		<button type="button" class="btn btn-primary" name="export" value="export"><span class="fa fa-download"></span>&nbsp;<spring:message code="temps.exportar"/></button>
		<button type="button" class="btn btn-primary" name="refrescar" value="refrescar"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="comuns.refrescar"/></button>
	</div>
</body>
</html>
