<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
<script src="<c:url value="/js/select2.min.js"/>"></script>
<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
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
		alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
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
				"url": "/helium/nodeco/v3/expedient/" + idExpedient + "/tasquesPendents",
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
});
</script>
</head>
<body>	
	<input type="hidden" id="netejar" value="false"/>
	<form:form id="expedientInformeCommand" name="expedientInformeCommand" action="" method="post" cssClass="well_mod form-horizontal form-tasca" commandName="expedientInformeCommand">
		<input type="hidden" id="expedientTipusId" name="expedientTipusId" value="${expedientTipusId}"/>
		<div class="page-header">
			Informes de <c:forEach var="expedientTipus" items="${expedientTipusAccessibles}"><c:if test="${expedientTipus.id == expedientTipusId}">${expedientTipus.nom}</c:if></c:forEach>
		</div>
		<c:choose>
			<c:when test="${empty consulta}">
				<div id="filtresCollapsable">
					<hel:inputSelect name="consultaId" textKey="expedient.consulta.select.consula" placeholderKey="expedient.consulta.select.consula" optionItems="${consultes}" optionValueAttribute="id" optionTextAttribute="nom"/>
					<div class="row">
						<div class="pull-right">
							<button type="submit" name="accio" value="consultar" class="btn btn-primary">Consultar</button>
						</div>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<input type="hidden" id="consultaId" name="consultaId" value="${consultaId}"/>
			</c:otherwise>
		</c:choose>
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
			<div class="row">
			<div class="col-md-6">
				<form:hidden path="nomesPendents"/>
				<form:hidden path="nomesAlertes"/>
				<form:hidden path="mostrarAnulats"/>
				<form:hidden path="tramitacioMassivaActivada"/>
				<div class="btn-group">
					<a id="nomesPendentsCheck" href="javascript:void(0)" title="Només amb tasques pendents" class="btn btn-default<c:if test="${expedientConsultaCommand.nomesPendents || preferenciesUsuari.filtroTareasActivas}"> active</c:if>" data-toggle="buttons"><span class="fa fa-clock-o"></span></a>
					<a id="nomesAlertesCheck" href="javascript:void(0)" title="Només amb alertes" class="hide btn btn-default<c:if test="${expedientConsultaCommand.nomesAlertes}"> active</c:if>" data-toggle="buttons"><span class="fa fa-warning"></span></a>
					<a id="mostrarAnulatsCheck" href="javascript:void(0)" title="Mostrar anul·lats" class="btn btn-default<c:if test="${expedientConsultaCommand.mostrarAnulats}"> active</c:if>" data-toggle="buttons"><span class="fa fa-times"></span></a>
				</div>
			</div>
			<div class="col-md-6">
				<div class="pull-right">
					<input type="hidden" name="consultaRealitzada" value="true"/>
					<button type="submit" name="accio" value="netejar" class="btn btn-default">Netejar</button>
					<button type="submit" name="accio" value="consultar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;Filtrar</button>
				</div>
			</div>
		</div>
		</c:if>
	</form:form>
		
	<c:if test='${not empty consulta}'>
		<table id="taulaDades" class="table table-striped table-bordered table-hover" data-rdt-button-template="tableButtonsTemplate" data-rdt-filtre-form-id="expedientInformeCommand" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0" <c:if test="${not empty preferenciesUsuari.numElementosPagina}">data-rdt-display-length-default="${preferenciesUsuari.numElementosPagina}"</c:if>>
			<thead>
				<tr>
					<th data-rdt-property="expedient.id" width="4%" data-rdt-sortable="false"></th>
					<th data-rdt-property="expedient.identificador" data-rdt-sorting="desc" data-visible=true>Expedient</th>
					<c:forEach var="camp" items="${campsInforme}">
						<th data-rdt-property="dadesExpedient.${camp.varCodi}.valorMostrar" data-visible=true >${camp.campEtiqueta}</th>
					</c:forEach>
					<th data-rdt-property="expedient.id" data-rdt-template="cellAccionsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;Accions&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="expedient/{{:id}}" class="obrir-expedient"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code='comuns.obrir'/></a></li>
								<li><a href="<c:url value="../v3/expedient/{{:id}}/stop"/>" data-rdt-link-modal="true"><span class="fa fa-stop"></span>&nbsp;<spring:message code='comuns.aturar'/></a></li>
								<li><a href="<c:url value="../v3/expedient/{{:id}}/anular"/>" data-rdt-link-modal="true"><span class="fa fa-times"></span>&nbsp;<spring:message code='comuns.anular'/></a></li>
								<li><a href="<c:url value="../v3/expedient/{{:id}}/delete"/>" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code='expedient.consulta.confirm.esborrar'/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code='comuns.esborrar'/></a></li>
							</ul>
						</div>
					</script>
					</th>
				</tr>
			</thead>
		</table>
		<script id="tableButtonsTemplate" type="text/x-jsrender">
			<div style="text-align:right">
				<div class="btn-group">
					<a class="btn btn-default" href="../v3/expedient/seleccioTots" data-rdt-link-ajax="true" title="Seleccionar tots"><span class="fa fa-check-square-o"></span></a>
					<a class="btn btn-default" href="../v3/expedient/seleccioNetejar" data-rdt-link-ajax="true" title="Netejar selecció"><span class="fa fa-square-o"></span></a>
					<a class="btn btn-default" href="#">Tramitació massiva <span id="tramitacioMassivaCount" class="badge">&nbsp;</span></a>
				</div>
			</div>
		</script>
		<div id="btn_exportar" class="btn-toolbar pull-left btn_under_taulaDades">
			<a href="${consulta.expedientTipus.id}/${consulta.id}/exportar_excel" class="btn btn-default">
				<span class="icon-download-alt"></span>&nbsp;<spring:message code="comuns.descarregar"/>
			</a>
			<c:if test="${not empty consulta.informeNom}">
				<a href="${consulta.expedientTipus.id}/${consulta.id}/mostrar_informe" class="btn btn-default">
					<span class="icon-download-alt"></span>&nbsp;<spring:message code="expedient.consulta.informe"/>
				</a>
			</c:if>
		</div>
	</c:if>

</body>
</html>
