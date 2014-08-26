<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="expedient.llistat.titol"/></title>
	<meta name="capsaleraTipus" content="llistat"/>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/datepicker-locales/bootstrap-datepicker.${idioma}.js"/>"></script>
	<script src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/DT_bootstrap.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/jquery.dataTables.js"/>"></script>
	<script src="<c:url value="/js/DT_bootstrap.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/helium.datatable.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
<script>
$(document).ready(function() {
	$("#taulaDades").heliumDataTable({
		ajaxSourceUrl: "<c:url value="/v3/expedient/datatable"/>",
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
	$("#nomesPendentsCheck").click(function() {
		$("input[name=nomesPendents]").val(!$("#nomesPendentsCheck").hasClass('active'));
		$('#expedientConsultaCommand').submit();
	});
	$("#nomesAlertesCheck").click(function() {
		$("input[name=nomesAlertes]").val(!$("#nomesAlertesCheck").hasClass('active'));
		$('#expedientConsultaCommand').submit();
	});
	$("#mostrarAnulatsCheck").click(function() {
		$("input[name=mostrarAnulats]").val(!$("#mostrarAnulatsCheck").hasClass('active'));
		$('#expedientConsultaCommand').submit();
	});
	$('#expedientTipusId').on('change', function() {
		var tipus = $(this).val();
		$('#estatText').select2('val', '', true);
		$('#estatText option[value!=""]').remove();
		if ($(this).val()) {
			$.get('expedient/estatsPerTipus/' + $(this).val())
			.done(function(data) {
				$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.INICIAT%>"><spring:message code="comu.estat.iniciat"/></option>');
				for (var i = 0; i < data.length; i++) {
					$('#estatText').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
				}
				$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.FINALITZAT%>"><spring:message code="comu.estat.finalitzat"/></option>');
			})
			.fail(function() {
				alert("<spring:message code="expedient.llistat.estats.ajax.error"/>");
			});
		} else {
			$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.INICIAT%>"><spring:message code="comu.estat.iniciat"/></option>');
			$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.FINALITZAT%>"><spring:message code="comu.estat.finalitzat"/></option>');
		}
	});
	$('#expedientTipusId').trigger('change');
});
</script>
</head>
<body>
	<input type="hidden" id="netejar" value="false"/>
	<form:form action="" method="post" cssClass="well" commandName="expedientConsultaCommand">
		<div class="row">
			<div class="col-md-2">
				<hel:inputText name="numero" textKey="expedient.llistat.filtre.camp.numero" placeholderKey="expedient.llistat.filtre.camp.numero" inline="true"/>
			</div>
			<div class="col-md-4">
				<hel:inputText name="titol" textKey="expedient.llistat.filtre.camp.titol" placeholderKey="expedient.llistat.filtre.camp.titol" inline="true"/>
			</div>
			<div class="col-md-3">
				<hel:inputSelect name="expedientTipusId" textKey="expedient.llistat.filtre.camp.expedient.tipus" placeholderKey="expedient.llistat.filtre.camp.expedient.tipus" optionItems="${expedientTipusAccessibles}" optionValueAttribute="id" optionTextAttribute="nom" disabled="${not empty expedientTipusActual}" inline="true"/>
			</div>
			<div class="col-md-3">
				<hel:inputSelect name="estatText" textKey="expedient.llistat.filtre.camp.estat" placeholderKey="expedient.llistat.filtre.camp.estat" optionItems="${estats}" optionValueAttribute="id" optionTextAttribute="nom" inline="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-4">
				<label><spring:message code="expedient.llistat.filtre.camp.data.inici"/></label>
				<div class="row">
					<div class="col-md-6">
						<hel:inputDate name="dataIniciInicial" textKey="expedient.llistat.filtre.camp.data.inici.1" placeholder="dd/mm/yyyy" inline="true"/>
					</div>
					<div class="col-md-6">
						<hel:inputDate name="dataIniciFinal" textKey="expedient.llistat.filtre.camp.data.inici.2" placeholder="dd/mm/yyyy" inline="true"/>
					</div>
				</div>
			</div>
			<div class="col-md-4">
				<label><spring:message code="expedient.llistat.filtre.camp.data.fi"/></label>
				<div class="row">
					<div class="col-md-6">
						<hel:inputDate name="dataFiInicial" textKey="expedient.llistat.filtre.camp.data.fi.1" placeholder="dd/mm/yyyy" inline="true"/>
					</div>
					<div class="col-md-6">
						<hel:inputDate name="dataFiFinal" textKey="expedient.llistat.filtre.camp.data.fi.2" placeholder="dd/mm/yyyy" inline="true"/>
					</div>
				</div>
			</div>
			<div class="col-md-4">
				<c:if test="${globalProperties['app.georef.actiu']}">
					<label><spring:message code="expedient.llistat.filtre.camp.geopos"/></label>
					<c:choose>
						<c:when test="${globalProperties['app.georef.tipus']=='ref'}">
							<hel:inputText name="geoReferencia" textKey="expedient.llistat.filtre.camp.georef" placeholderKey="expedient.llistat.filtre.camp.georef" inline="true"/>
						</c:when>
						<c:otherwise>
							<div class="row">
								<div class="col-md-6">
									<hel:inputText name="geoPosX" textKey="expedient.llistat.filtre.camp.coordx" placeholderKey="expedient.llistat.filtre.camp.coordx" inline="true"/>
								</div>
								<div class="col-md-6">
									<hel:inputText name="geoPosY" textKey="expedient.llistat.filtre.camp.coordy" placeholderKey="expedient.llistat.filtre.camp.coordy" inline="true"/>
								</div>
							</div>
						</c:otherwise>
					</c:choose>
				</c:if>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6">
				<form:hidden path="nomesPendents"/>
				<form:hidden path="nomesAlertes"/>
				<form:hidden path="mostrarAnulats"/>
				<div class="btn-group">
					<button id="nomesPendentsCheck" title="<spring:message code="expedient.llistat.filtre.camp.tasques"/>" class="btn btn-default<c:if test="${expedientConsultaCommand.nomesPendents || preferenciesUsuari.filtroTareasActivas}"> active</c:if>" data-toggle="buttons"><span class="fa fa-clock-o"></span></button>
					<button id="nomesAlertesCheck" title="<spring:message code="expedient.llistat.filtre.camp.alertes"/>" class="hide btn btn-default<c:if test="${expedientConsultaCommand.nomesAlertes}"> active</c:if>" data-toggle="buttons"><span class="fa fa-warning"></span></button>
					<button id="mostrarAnulatsCheck" title="<spring:message code="expedient.llistat.filtre.camp.anulats"/>" class="btn btn-default<c:if test="${expedientConsultaCommand.mostrarAnulats}"> active</c:if>" data-toggle="buttons"><span class="fa fa-times"></span></button>
				</div>
			</div>
			<div class="col-md-6">
				<div class="pull-right">
					<input type="hidden" name="consultaRealitzada" value="true"/>
					<button type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
					<button type="submit" name="accio" value="consultar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	<table id="taulaDades" class="table table-striped table-bordered table-hover" data-rdt-button-template="tableButtonsTemplate" data-rdt-filtre-form-id="expedientConsultaCommand" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0" <c:if test="${not empty preferenciesUsuari.numElementosPagina}">data-rdt-display-length-default="${preferenciesUsuari.numElementosPagina}"</c:if>>
		<thead>
			<tr>
				<th data-rdt-property="id" width="4%" data-rdt-sortable="false"></th>
				<th data-rdt-property="identificador" data-rdt-visible="true"><spring:message code="expedient.llistat.columna.expedient"/></th>
				<th data-rdt-property="dataInici" data-rdt-type="datetime" data-rdt-sorting="desc" data-rdt-visible="true"><spring:message code="expedient.llistat.columna.iniciat"/></th>
				<th data-rdt-property="dataFi" data-rdt-type="datetime" data-rdt-visible="true"><spring:message code="expedient.llistat.columna.finalitzat"/></th>
				<th data-rdt-property="tipus.nom" data-rdt-visible="true"><spring:message code="expedient.llistat.columna.tipus"/></th>
				<th data-rdt-property="estat.nom" data-rdt-template="cellEstatTemplate" data-rdt-visible="true">
					<spring:message code="expedient.llistat.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
					{{if dataFi}}<spring:message code="comu.estat.iniciat"/>{{else estat_nom}}{{:estat_nom}}{{else}}<spring:message code="comu.estat.finalitzat"/>{{/if}}
					</script>
				</th>
				<th data-rdt-property="aturat" data-rdt-visible="false"></th>
				<th data-rdt-property="anulat" data-rdt-visible="false"></th>
				<th data-rdt-property="id" data-rdt-template="cellAccionsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="expedient/{{:id}}" class="obrir-expedient"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.llistat.accio.obrir"/></a></li>
								<li><a href="../v3/expedient/{{:id}}/suspend"><span class="fa fa-stop"></span>&nbsp;<spring:message code="expedient.llistat.accio.aturar"/></a></li>
								<li><a href="../v3/expedient/{{:id}}/cancel" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code="expedient.llistat.confirmacio.anular"/>"><span class="fa fa-times"></span>&nbsp;<spring:message code="expedient.llistat.accio.anular"/></a></li>
								<li><a href="../v3/expedient/{{:id}}/delete" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code="expedient.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
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
				<a class="btn btn-default" href="../v3/expedient/seleccioTots" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.tots"/>"><span class="fa fa-check-square-o"></span></a>
				<a class="btn btn-default" href="../v3/expedient/seleccioNetejar" data-rdt-link-ajax="true" title="<spring:message code="expedient.llistat.accio.seleccio.netejar"/>"><span class="fa fa-square-o"></span></a>
				<a class="btn btn-default" href="#"><spring:message code="expedient.llistat.accio.massiva"/>&nbsp;<span id="tramitacioMassivaCount" class="badge">&nbsp;</span></a>
			</div>
			<a id="iniciar-modal" class="btn btn-default" href="<c:url value="../v3/expedient/iniciar"/>" data-iniciar-modal="true"><span class="fa fa-plus"></span>&nbsp;Nou expedient</a>
		</div>
	</script>

	<div id="expedient-iniciar-modal"></div>
	<div id="expedient-aturar-modal"></div>
	
</body>
</html>
