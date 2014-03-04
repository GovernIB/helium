<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
<head>
	<title><fmt:message key="index.inici" /></title>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<meta name="capsaleraTipus" content="llistat"/>
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<c:import url="common/formIncludes.jsp"/>
<script>
	<c:if test="${expedientInformeCommand.consultaRealitzada}">
	var seleccioInicialitzada = false;
	var seleccioArray = [];
	var seleccioClass = "info";
 	var oTable;
	
	function obtenirSeleccioActual() {
// 		if (!seleccioInicialitzada) {
// 			$.ajax({
// 				url: "expedient/deseleccionar/-1",
// 				success: function (data) {
// 					seleccioArray = data;
// 				},
// 				async: false
// 			});
// 			seleccioInicialitzada = true;
// 		}
// 		return seleccioArray;
	}
	function modificarSeleccioIActualitzar(valor, afegir) {
// 		var url = (afegir) ? "expedient/seleccionar/" : "expedient/deseleccionar/";
// 		$.get(
// 			url + valor,
// 			function (data) {
// 				seleccioArray = data;
// 				actualitzarVistaSeleccio();
// 			});
	}
	function actualitzarVistaSeleccio() {
		$('#dades-carregant').hide();
		var seleccio = obtenirSeleccioActual();
		$('#tramitacioMassivaCount').html(seleccio.length);
		var totsSeleccionats = true;
		$("tbody tr", taula).each(function (index, row) {
			var checkbox = $("td:eq(0) input[type=checkbox]", row);
			var value = checkbox.attr("value");
			var index = seleccio.indexOf(parseInt(value));
			if (index == -1) {
				$(row).removeClass(seleccioClass);
				checkbox.removeAttr("checked");
				totsSeleccionats = false;
			} else {
				$(row).addClass(seleccioClass);
				checkbox.attr("checked", "checked");
			}
		});
		if (totsSeleccionats)
			$("thead input[type=checkbox]", taula).attr("checked", "checked");
		else
			$("thead input[type=checkbox]", taula).removeAttr("checked");
	}
	
	function formatDate(fechaSinFormato) {
		var date = fechaSinFormato.split(" ");
		var fecha = date[0].split("-");
		var hora = date[1].split(":");
		fechaFormateada = (fecha[2]+"/"+fecha[1]+"/"+fecha[0]+" " +hora[0]+":" +hora[1]);
		return fechaFormateada;
	}
	</c:if>
	
	function confirmarEsborrar(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm("<spring:message code='expedient.consulta.confirm.esborrar'/>");
	}
	
	function rowCallback(nRow, aData, iDisplayIndex, iDisplayIndexFull) {
		var numColumnes = $("td", nRow).size();
		var expedientId = aData[0];
		$("#btn_exportar").show();
		$("td:eq(" + (numColumnes - 1) + ")", nRow).html(
			'<div class="btn-group">' +
				'<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#">' + 
				 	'<i class="icon-cog icon-white"></i> Accions <span class="caret"></span>' + 
				 '</a>' + 
				 '<ul class="dropdown-menu">' + 
				 	'<li>' + 
				 		'<a href="/helium/v3/expedient/' + expedientId + '"><i class="icon-folder-open"></i> Obrir</a>' + 
				 	'</li>' + 
				 	'<li>' + 
				 		'<a href="/helium/v3/expedient/' + expedientId + '/stop"><i class="icon-stop"></i> Aturar</a>' + 
				 	'</li>' + 
				 	'<li>' + 
				 		'<a href="/helium/v3/expedient/' + expedientId + '/suspend"><i class="icon-remove"></i> Anular</a>' + 
				 	'</li>' + 
				 	'<li>' + 
				 	'<a href="<c:url value="/helium/v3/expedient/' + expedientId + '/delete"></c:url>" onclick="return confirmarEsborrar(event)">' +
			 			'<i class="icon-trash"></i> Esborrar' +
				 		'</a>' +
				 	'</li>' + 
				 '</ul>' + 
			'</div>');
// 		if (aData[6] === 'true')
// 			$("td:eq(1)", nRow).append(' <span class="label" title="Aturat">AT</span>');
// 		if (aData[7] === 'true')
// 			$("td:eq(1)", nRow).append('  <span class="label" title="Anulat">AN</span>');					
// 		if (!aData[5]) {
// 			if (aData[3])
// 				$("td:eq(" + (numColumnes - 2) + ")", nRow).html('Finalitzat');
// 			else
// 				$("td:eq(" + (numColumnes - 2) + ")", nRow).html('Iniciat');
// 		}
		
// 		$("td:eq(" + (numColumnes - 4) + ")", nRow).html(formatDate(aData[2]));
		
// 		if (!aData[5] && aData[3]) $("td:eq(" + (numColumnes - 2) + ")", nRow).html('Finalitzat');
		if ($(taula).dataTable().fnSettings().aoColumns[0].bVisible) {
			var seleccio = obtenirSeleccioActual();
			var seleccionat = seleccio.indexOf(parseInt(aData[0])) != -1;
			if (seleccionat) {
				$(nRow).addClass(seleccioClass);
				$("td:eq(0)", nRow).html('<input type="checkbox" value="' + aData[0] + '" checked="checked"/>');
			} else {
				$("td:eq(0)", nRow).html('<input type="checkbox" value="' + aData[0] + '"/>');
			}
		}
	}
	function serverParamsCallback(aoData) {
		$("#formConsultar input").each(function (index) {
			aoData.push({ "name": $(this).attr("name"), "value": $(this).attr("value")} );	
		});		
		$("#formConsultar select").each(function (index) {
			aoData.push({ "name": $(this).attr("name"), "value": $(this).attr("value")} );	
		});		
		aoData.push({ "name": "netejar", "value": $("#netejar").val()} );
		$("#netejar").val("false");
	}
	function drawCallback(oSettings) {
		oTable = $(taula).dataTable();
		// Gestiona els clics als checkboxes de cada fila
		$("tbody", taula).delegate(
			"input[type=checkbox]",
			"click",
			function () {
				modificarSeleccioIActualitzar(
						$(this).attr("value"),
						$(this).is(":checked"));
			}
		);
		
		// Gestiona el clic al checkbox de la capçalera
		$("thead input[type=checkbox]", taula).click(function() {
			var checked = $(this).is(":checked");
			var ids = [];
			$("tbody tr", taula).each(function (index, row) {
				ids.push($("td:eq(0) input[type=checkbox]", row).attr("value"));
			});
			modificarSeleccioIActualitzar(
					ids,
					checked);
		});
	}
	function initComplete() {
		
	}
	
	$(document).ready(
		function() {
			$("#bconsultar").click(function() {
				$(taula).dataTable().fnDraw();
			});
			$("#bnetejar").click(function() {
				$("#formConsultar")[0].reset();
				$(taula).dataTable().fnDraw();
			});
			$("#nomesPendentsCheck").click(function() {
				$("input[name=nomesPendents]").val(!$("#nomesPendentsCheck").hasClass('active'));
				$(taula).dataTable().fnDraw();
			});
			$("#nomesAlertesCheck").click(function() {
				$("input[name=nomesAlertes]").val(!$("#nomesAlertesCheck").hasClass('active'));
				$(taula).dataTable().fnDraw();
			});
			$("#mostrarAnulatsCheck").click(function() {
				$("input[name=mostrarAnulats]").val(!$("#mostrarAnulatsCheck").hasClass('active'));
				$(taula).dataTable().fnDraw();
			});
			$("#tramitacioMassivaActivar").click(function() {
				$("#tramitacioMassivaActivar").parent().addClass('hide');
				$("#tramitacioMassivaDesactivar").parent().removeClass('hide');
				$("#tramitacioMassivaBtn").removeClass('disabled');
				 $(taula).dataTable().fnSetColumnVis(0, true);
				$("input[name=tramitacioMassivaActivada]").val("true");
			});
			$("#tramitacioMassivaDesactivar").click(function() {
				$("#tramitacioMassivaDesactivar").parent().addClass('hide');
				$("#tramitacioMassivaActivar").parent().removeClass('hide');
				$("#tramitacioMassivaBtn").addClass('disabled');
				 $(taula).dataTable().fnSetColumnVis(0, false);
				$("input[name=tramitacioMassivaActivada]").val("false");
			});
			$('#filtresCollapsable').on('hide', function () {
				$('#filtresCollapse i').attr("class", "icon-chevron-down");
				$("input[name=filtreDesplegat]").val("false");
			});
			$('#filtresCollapsable').on('show', function () {
				$('#filtresCollapse i').attr("class", "icon-chevron-up");
				$("input[name=filtreDesplegat]").val("true");
			});
			$('select[name=expedientTipusId]').on('change', function () {
				$("select[name=estatText] option").each(function (index, option) {
		    		if (index > 1 && $("select[name=estatText] option").size() > 3)
						$(option).remove();
				});
				if ($(this).val()) {
					$.ajax({
					    url:'expedient/estatsPerTipus/' + $(this).val(),
					    type:'GET',
					    dataType: 'json',
					    success: function(json) {
					        $.each(json, function(i, value) {
					        	$('<option>').text(value.nom).attr('value', value.id).insertAfter($("select[name=estatText] option:eq(" + (i + 1) + ")"));
					        });
					    }
					});
				}
			});
			$('.datepicker').datepicker({language: 'ca', autoclose: true});
		}
	);
</script>
<c:url value="/v3/informe/${expedientInformeCommand.expedientTipusId}/datatable" var="dataTableAjaxSourceUrl"/>
<hel:dataTable tableId="taulaDades" paginate="true" ajaxSourceUrl="${dataTableAjaxSourceUrl}" rowCallback="rowCallback" serverParamsCallback="serverParamsCallback" ajaxRefrescarTaula="true" ajaxRefrescarAlertes="true" drawCallback="drawCallback" initComplete="initComplete" hoverRow="false"/>
</head>
<body>
	<jsp:include page="import/helforms.jsp" />
	<input type="hidden" id="netejar" value="false"/>
	<form:form action="" method="post" cssClass="well formbox" commandName="expedientInformeCommand">
		<div class="page-header">
			Informes de <c:forEach var="expedientTipus" items="${expedientTipusAccessibles}"><c:if test="${expedientTipus.id == expedientInformeCommand.expedientTipusId}">${expedientTipus.nom}</c:if></c:forEach>
			<form:hidden path="filtreDesplegat"/>
		</div>
		<div id="filtresCollapsable" class="collapse in">
			<div class="row-fluid">
				<div class="span12">
					<c:set var="campPath" value="consultaId"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<form:select path="${campPath}" cssClass="span12" onchange="$('#expedientInformeCommand').submit()">
						<option value=""><spring:message code='expedient.consulta.select.consula'/></option>
						<form:options items="${consultes}" itemLabel="nom" itemValue="id"/>
					</form:select>
				</div>
			</div>
		</div>
	</form:form>					
		
	<form:form id="formConsultar" name="formConsultar" action="consultar" method="post" cssClass="well_mod form-horizontal form-tasca" commandName="commandFiltre">
		<input id="consultaId" type="hidden" value="${consulta.id}" name="consultaId">
		<c:forEach var="camp" items="${campsFiltre}">
			<div class="control-group fila_reducida">
				<label class="control-label" for="${camp.codi}">${camp.etiqueta}</label>
				<div class="controls">
					<c:set var="campActual" value="${camp}" scope="request"/>
					<c:set var="readonly" value="${false}" scope="request"/>
					<c:set var="required" value="${false}" scope="request"/>
					<c:import url="campsFiltre.jsp"/>
				</div>
			</div>
		</c:forEach>
		<script type="text/javascript">initSelectable();</script>
		
		<div class="row-fluid  botonera <c:if test='${empty consulta}'>hide</c:if>">
			<div class="span6">
				<input type="hidden" id="nomesPendents" name="nomesPendents" value="${expedientInformeCommand.nomesPendents}"/>
				<input type="hidden" id="nomesAlertes" name="nomesAlertes" value="${expedientInformeCommand.nomesAlertes}"/>
				<input type="hidden" id="mostrarAnulats" name="mostrarAnulats" value="${expedientInformeCommand.mostrarAnulats}"/>
				<input type="hidden" id="tramitacioMassivaActivada" name="tramitacioMassivaActivada" value="${expedientInformeCommand.tramitacioMassivaActivada}"/>

				<div class="btn-group">
<%-- 					<a id="nomesPendentsCheck" href="javascript:void(0)" title="Només amb tasques pendents" class="btn<c:if test="${expedientInformeCommand.nomesPendents}"> active</c:if>" data-toggle="button"><i class="icon-time"></i></a> --%>
<%-- 					<a id="nomesAlertesCheck" href="javascript:void(0)" title="Només amb alertes" class="hide btn<c:if test="${expedientInformeCommand.nomesAlertes}"> active</c:if>" data-toggle="button"><i class="icon-warning-sign"></i></a> --%>
<%-- 					<a id="mostrarAnulatsCheck" href="javascript:void(0)" title="Mostrar anulats" class="btn<c:if test="${expedientInformeCommand.mostrarAnulats}"> active</c:if>" data-toggle="button"><i class="icon-remove"></i></a> --%>
				</div>
	
				<div class="hide btn-group">
					<button id="tramitacioMassivaBtn" class="btn<c:if test="${not expedientInformeCommand.tramitacioMassivaActivada}"> disabled</c:if>">Tramitació massiva <span id="tramitacioMassivaCount" class="badge">&nbsp;</span></button>
					<button class="btn dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>
					<ul class="dropdown-menu">
						<li<c:if test="${expedientInformeCommand.tramitacioMassivaActivada}"> class="hide"</c:if>><a id="tramitacioMassivaActivar" href="#"><i class="icon-ok-circle"></i> Activar</a></li>
						<li<c:if test="${not expedientInformeCommand.tramitacioMassivaActivada}"> class="hide"</c:if>><a id="tramitacioMassivaDesactivar" href="#"><i class="icon-ban-circle"></i> Desactivar</a></li>
					</ul>
				</div>
			</div>
			<div class="span6">
				<input type="hidden" name="consultaRealitzada" value="true"/>
				<button id="bconsultar" type="button" class="btn btn-primary pull-right">Consultar</button>
				<button id="bnetejar" type="button" class="btn pull-right" style="margin-right:.6em">Netejar</button>
			</div>
		</div>
	</form:form>
	
	<script>	
		$( '[data-required="true"]' )
			.closest(".control-group")
			.children("label")
			.prepend("<i class='icon-asterisk'></i> ");
	</script>
	
	<c:if test="${expedientInformeCommand.consultaRealitzada}">
		<table id="taulaDades" class="table table-striped table-bordered">
			<thead>
				<tr>
					<th data-property="id" width="4%" <c:if test="${not expedientInformeCommand.tramitacioMassivaActivada}">data-visible=false</c:if>><input type="checkbox"/></th>
					<th data-property="identificador" data-sorting="desc" data-visible=true>Expedient</th>
					<c:forEach var="camp" items="${campsInforme}">
						<th data-property="${camp.codi}" data-visible=true>${camp.etiqueta}</th>
					</c:forEach>
					<th data-property="id" data-sortable="false" width="10%" data-visible=true></th>
				</tr>
			</thead>
		</table>
		<div id="btn_exportar" class="btn-toolbar pull-left btn_under_taulaDades hide">
			<a href="${expedientInformeCommand.expedientTipusId}/exportar_excel" class="btn btn-default">
				<span class="icon-download-alt"></span>&nbsp;<spring:message code="comuns.descarregar"/>
			</a>
			<c:if test="${not empty consulta.informeNom}">
				<a href="${expedientInformeCommand.expedientTipusId}/mostrar_informe" class="btn btn-default">
					<span class="icon-download-alt"></span>&nbsp;<spring:message code="expedient.consulta.informe"/>
				</a>
			</c:if>
		</div>
	</c:if>

</body>
</html>
