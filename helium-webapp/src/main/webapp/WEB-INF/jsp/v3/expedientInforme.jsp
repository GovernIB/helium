<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
<head>
	<title><fmt:message key="index.inici" /></title>
	<meta name="capsaleraTipus" content="llistat"/>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.ca.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/helium.datatable.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
<script>
$(document).ready(function() {
	$("#taulaDades").heliumDataTable({
		ajaxSourceUrl: "<c:url value="/v3/informe/${expedientInformeCommand.expedientTipusId}/${expedientInformeCommand.consultaId}/datatable"/>",
		localeUrl: "<c:url value="/js/dataTables-locales/dataTables_locale_ca.txt"/>",
		alertesRefreshUrl: "<c:url value="/nodeco/util/alertes"/>",
		rowClickCallback: function(row) {
			$('a.obrir-expedient', $(row))[0].click();
			/*var url = $('ul a:first', $(row)).attr("href");
			var idExpedient = url.substr("expedient/".length, url.length);
			if ($("tr.info-" + idExpedient, $(row).parent()).length) {
				return;
			}
			var numCols = $(row).children('td').length;
			$(".tr-pendents").each(function( index ) {
				$(this).fadeOut();
				$(this).remove();
			});
			$(row).after("<tr id='contingut-carregant' class='tr-pendents hide'>"+
					"<td colspan='" + (numCols - 1)+ "'>"+
						"<div><p style='margin-top: 2em; text-align: center'><i class='icon-spinner icon-2x icon-spin'></i></p></div>"+
					"</td></tr>");
			$.ajax({
				"url": "/helium/nodecorar/v3/expedient/" + idExpedient + "/tasquesPendents",
				"success": function (data) {								
					$(row).after("<tr class='tr-pendents info-" + idExpedient + "'>"+
							"<td colspan='" + (numCols - 1)+ "'>" + data + "</td>").fadeIn();
				},
			  	"error": function(XMLHttpRequest, textStatus, errorThrown) {
				}
			});*/
		},
		seleccioCallback: function(seleccio) {
			$('#tramitacioMassivaCount').html(seleccio.length);
		}
	});
	$("#expedientInformeCommand button[value='netejar']").click(function() {
		$('#expedientInformeCommand')[0].reset();
	});
	$("#nomesPendentsCheck").click(function() {
		$("input[name=nomesPendents]").val(!$("#nomesPendentsCheck").hasClass('active'));
		$('#expedientInformeCommand').submit();
	});
	$("#nomesAlertesCheck").click(function() {
		$("input[name=nomesAlertes]").val(!$("#nomesAlertesCheck").hasClass('active'));
		$('#expedientInformeCommand').submit();
	});
	$("#mostrarAnulatsCheck").click(function() {
		$("input[name=mostrarAnulats]").val(!$("#mostrarAnulatsCheck").hasClass('active'));
		$('#expedientInformeCommand').submit();
	});
	$('#filtresCollapsable').on('hide', function () {
		$('#filtresCollapse i').attr("class", "icon-chevron-down");
		$("input[name=filtreDesplegat]").val("false");
	});
	$('#filtresCollapsable').on('show', function () {
		$('#filtresCollapse i').attr("class", "icon-chevron-up");
		$("input[name=filtreDesplegat]").val("true");
	});
	$('#tramitacioMassivaSelTots').click(function() {
		$.ajax({
		    url:'${expedientInformeCommand.expedientTipusId}/${expedientInformeCommand.consultaId}/seleccionarTots',
		    type:'POST',
		    dataType: 'json',
			async: false,
			success: function(data) {
				$("td input.rdt-seleccio[type=checkbox]", $("#taulaDades")).each(function(index) {
					$(this).removeAttr('checked');
					for (var i = 0; i < data.length; i++) {
						if (data[i] == $(this).val()) {
							$(this).attr('checked', 'checked');
							break;
						}
					}
					seleccio = data;
				});
				$('#tramitacioMassivaCount').html(seleccio.length);
			},
			timeout: 20000,
			error: function (xhr, textStatus, errorThrown) {
				alert('Error al canviar la selecció');
			}
		});
	});
	$('.datepicker').datepicker({language: 'ca', autoclose: true});
});
</script>
</head>
<body>
	<jsp:include page="import/helforms.jsp" />
	<input type="hidden" id="netejar" value="false"/>
	<form:form id="expedientInformeCommand" name="expedientInformeCommand" action="" method="post" cssClass="well_mod form-horizontal form-tasca" commandName="expedientInformeCommand">
		<input type="hidden" id="expedientTipusId" name="expedientTipusId" value="${expedientTipusId}"/>
		<div class="page-header">
			Informes de <c:forEach var="expedientTipus" items="${expedientTipusAccessibles}"><c:if test="${expedientTipus.id == expedientTipusId}">${expedientTipus.nom}</c:if></c:forEach>
			<c:if test='${not empty consulta}'><form:hidden path="filtreDesplegat"/></c:if>
		</div>
		<div id="filtresCollapsable" class="collapse<c:if test="${true or (not empty consulta && expedientInformeCommand.filtreDesplegat)}"> in</c:if>">
			<div class="row-fluid">
				<div class="span12">
					<c:if test="${not empty consulta}">
						<c:set var="campPath" value="consultaId"/>
					</c:if>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<form:select id="consultaId" name="consultaId" path="${campPath}" cssClass="span12" onchange="this.form.submit()">
						<option value=""><spring:message code='expedient.consulta.select.consula'/></option>
						<form:options items="${consultes}" itemLabel="nom" itemValue="id"/>
					</form:select>
				</div>
			</div>
		</div>
		<c:forEach var="camp" items="${campsFiltre}">			
			<div class="control-group fila_reducida">
				<label class="control-label" for="${camp.varCodi}">${camp.campEtiqueta}</label>
				<div class="controls">
					<c:set var="campActual" value="${camp}" scope="request"/>
					<c:set var="readonly" value="${false}" scope="request"/>
					<c:set var="required" value="${false}" scope="request"/>
					<c:import url="campsFiltre.jsp"/>
				</div>
			</div>
		</c:forEach>
		<c:if test='${not empty consulta}'>
			<hr/>
			<div class="row-fluid" >
				<div class="span6">
					<form:hidden path="nomesPendents"/>
					<form:hidden path="nomesAlertes"/>
					<form:hidden path="mostrarAnulats"/>
					<form:hidden path="tramitacioMassivaActivada"/>
					<div class="btn-group">
						<a id="nomesPendentsCheck" href="javascript:void(0)" title="Només amb tasques pendents" class="hide btn<c:if test="${expedientInformeCommand.nomesPendents}"> active</c:if>" data-toggle="button"><i class="icon-time"></i></a>
						<a id="nomesAlertesCheck" href="javascript:void(0)" title="Només amb alertes" class="hide btn<c:if test="${expedientInformeCommand.nomesAlertes}"> active</c:if>" data-toggle="button"><i class="icon-warning-sign"></i></a>
						<a id="mostrarAnulatsCheck" href="javascript:void(0)" title="Mostrar anulats" class="hide btn<c:if test="${expedientInformeCommand.mostrarAnulats}"> active</c:if>" data-toggle="button"><i class="icon-remove"></i></a>
					</div>
				</div>
				<div class="span6">
					<button type="submit" name="accio" value="consultar" class="btn btn-primary pull-right">Consultar</button>
					<button type="submit" name="accio" value="netejar" class="btn pull-right" style="margin-right:.6em">Netejar</button>
					<div class="btn-group pull-right">
						<button class="tramitacioMassiva btn">Tramitació massiva <span id="tramitacioMassivaCount" class="badge">&nbsp;</span></button>
						<button class="tramitacioMassiva btn dropdown-toggle" style="margin-right:.6em" data-toggle="dropdown"><span class="caret"></span></button>
						<ul class="tramitacioMassiva dropdown-menu" style="right: auto;">
							<li><a id="tramitacioMassivaSelTots" href="#"><i class="icon-ok-circle"></i> seleccionar tots</a></li>
						</ul>
					</div>
				</div>
			</div>
		</c:if>
	</form:form>
	
	<script>	
		$( '[data-required="true"]' )
			.closest(".control-group")
			.children("label")
			.prepend("<i class='icon-asterisk'></i> ");
	</script>
		
	<c:if test='${not empty consulta}'>
		<table id="taulaDades" class="table table-striped table-bordered table-hover" data-rdt-filtre-form-id="expedientInformeCommand" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0" <c:if test="${not empty preferenciesUsuari.numElementosPagina}">data-rdt-display-length-default="${preferenciesUsuari.numElementosPagina}"</c:if>>
			<thead>
				<tr>
					<th data-rdt-property="expedient.id" width="4%" data-rdt-sortable="false"></th>
					<th data-rdt-property="expedient.identificador" data-sorting="desc" data-visible=true>Expedient</th>
					<c:forEach var="camp" items="${campsInforme}">
						<th data-rdt-property="dadesExpedient.${camp.varCodi}.valorMostrar" data-visible=true >${camp.campEtiqueta}</th>
					</c:forEach>
					<th data-rdt-property="expedient.id" data-rdt-template="cellAccionsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><i class="icon-cog icon-white"></i>&nbsp;Accions&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a class="obrir-expedient" href="../expedient/{{:id}}"><i class="icon-folder-open"></i>&nbsp;Obrir</a></li>
								<li><a href="../expedient/{{:id}}/suspend"><i class="icon-stop"></i>&nbsp;Aturar</a></li>
								<li><a href="../expedient/{{:id}}/cancel" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code='expedient.consulta.confirm.anular'/>"><i class="icon-remove"></i>&nbsp;Anul·lar</a></li>
								<li><a href="../entitat/{{:id}}/delete" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code='expedient.consulta.confirm.esborrar'/>"><i class="icon-trash"></i>&nbsp;Esborrar</a></li>
							</ul>
						</div>
					</script>
					</th>
				</tr>
			</thead>
		</table>
		<div id="btn_exportar" class="btn-toolbar pull-left btn_under_taulaDades">
			<a href="${expedientInformeCommand.expedientTipusId}/${consulta.id}/exportar_excel" class="btn btn-default">
				<span class="icon-download-alt"></span>&nbsp;<spring:message code="comuns.descarregar"/>
			</a>
			<c:if test="${not empty consulta.informeNom}">
				<a href="${expedientInformeCommand.expedientTipusId}/${consulta.id}/mostrar_informe" class="btn btn-default">
					<span class="icon-download-alt"></span>&nbsp;<spring:message code="expedient.consulta.informe"/>
				</a>
			</c:if>
		</div>
	</c:if>

</body>
</html>
