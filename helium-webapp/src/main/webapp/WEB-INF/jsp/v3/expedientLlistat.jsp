<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><fmt:message key="index.inici"/></title>
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
				$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.INICIAT%>">Iniciat</option>');
				for (var i = 0; i < data.length; i++) {
					$('#estatText').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
				}
				$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.FINALITZAT%>">Finalitzat</option>');
			})
			.fail(function() {
				alert("Error al refrescar els estats");
			});
		} else {
			$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.INICIAT%>">Iniciat</option>');
			$('#estatText').append('<option value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientDto.EstatTipusDto.FINALITZAT%>">Finalitzat</option>');
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
				<hel:inputText name="numero" text="Número" placeholder="Número" inline="true"/>
			</div>
			<div class="col-md-4">
				<hel:inputText name="titol" text="Títol" placeholder="Títol" inline="true"/>
			</div>
			<div class="col-md-3">
				<hel:inputSelect name="expedientTipusId" text="Tipus d'expedient" placeholder="Tipus d'expedient" optionItems="${expedientTipusAccessibles}" optionValueAttribute="id" optionTextAttribute="nom" disabled="${not empty expedientTipusActual}" inline="true"/>
			</div>
			<div class="col-md-3">
				<hel:inputSelect name="estatText" text="Estat" placeholder="Estat" optionItems="${estats}" optionValueAttribute="id" optionTextAttribute="nom" inline="true"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-4">
				<label>Data inici</label>
				<div class="row">
					<div class="col-md-6">
						<hel:inputDate name="dataIniciInicial" text="Data inici inicial" placeholder="dd/mm/yyyy" inline="true"/>
					</div>
					<div class="col-md-6">
						<hel:inputDate name="dataIniciFinal" text="Data inici final" placeholder="dd/mm/yyyy" inline="true"/>
					</div>
				</div>
			</div>
			<div class="col-md-4">
				<label>Data fi</label>
				<div class="row">
					<div class="col-md-6">
						<hel:inputDate name="dataFiInicial" text="Data fi inicial" placeholder="dd/mm/yyyy" inline="true"/>
					</div>
					<div class="col-md-6">
						<hel:inputDate name="dataFiFinal" text="Data fi final" placeholder="dd/mm/yyyy" inline="true"/>
					</div>
				</div>
			</div>
			<c:choose>
				<c:when test="${globalProperties['app.georef.actiu']}">
					<c:choose>
						<c:when test="${globalProperties['app.georef.tipus']=='ref'}">
							<div class="col-md-4">
								<label>Geoposició</label>
								<hel:inputText name="geoReferencia" text="Codi georeferencial" placeholder="Codi georeferencial" inline="true"/>
							</div>
						</c:when>
						<c:otherwise>
							<div class="col-md-4">
								<label>Geoposició</label>
								<div class="row">
									<div class="col-md-6">
										<hel:inputText name="geoPosX" text="Coordenada X" placeholder="Coordenada X" inline="true"/>
									</div>
									<div class="col-md-6">
										<hel:inputText name="geoPosY" text="Coordenada Y" placeholder="Coordenada Y" inline="true"/>
									</div>
								</div>
							</div>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<div class="col-md-4"></div>
				</c:otherwise>
			</c:choose>
		</div>
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
	</form:form>
	<table id="taulaDades" class="table table-striped table-bordered table-hover" data-rdt-button-template="tableButtonsTemplate" data-rdt-filtre-form-id="expedientConsultaCommand" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0" <c:if test="${not empty preferenciesUsuari.numElementosPagina}">data-rdt-display-length-default="${preferenciesUsuari.numElementosPagina}"</c:if>>
		<thead>
			<tr>
				<th data-rdt-property="id" width="4%" data-rdt-sortable="false"></th>
				<th data-rdt-property="identificador" data-rdt-visible="true">Expedient</th>
				<th data-rdt-property="dataInici" data-rdt-type="datetime" data-rdt-sorting="desc" data-rdt-visible="true">Iniciat el</th>
				<th data-rdt-property="dataFi" data-rdt-type="datetime" data-rdt-visible="true">Finalitzat el</th>
				<th data-rdt-property="tipus.nom" data-rdt-visible="true">Tipus</th>
				<th data-rdt-property="estat.nom" data-rdt-template="cellEstatTemplate" data-rdt-visible="true">
					Estat
					<script id="cellEstatTemplate" type="text/x-jsrender">
					{{if dataFi}}Finalitzat{{else estat_nom}}{{:estat_nom}}{{else}}Iniciat{{/if}}
					</script>
				</th>
				<th data-rdt-property="aturat" data-rdt-visible="false">Aturat</th>
				<th data-rdt-property="anulat" data-rdt-visible="false">Anulat</th>
				<th data-rdt-property="id" data-rdt-template="cellAccionsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;Accions&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="expedient/{{:id}}" class="obrir-expedient"><span class="fa fa-folder-open"></span>&nbsp;Obrir</a></li>
								<li><a href="../v3/expedient/{{:id}}/stop" data-rdt-link-modal="true"><span class="fa fa-stop"></span>&nbsp;<spring:message code='comuns.aturar'/></a></li>
								<li><a href="../v3/expedient/{{:id}}/cancelar" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code='expedient.consulta.confirm.anular'/>"><span class="fa fa-times"></span>&nbsp;Anul·lar</a></li>
								<li><a href="../v3/expedient/{{:id}}/delete" data-rdt-link-ajax="true" data-rdt-link-confirm="<spring:message code='expedient.consulta.confirm.esborrar'/>"><span class="fa fa-trash-o"></span>&nbsp;Esborrar</a></li>
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
			<a id="iniciar-modal" class="btn btn-default" href="<c:url value="../v3/expedient/iniciar"/>" data-iniciar-modal="true"><span class="fa fa-plus"></span>&nbsp;Nou expedient</a>
		</div>
<script type="text/javascript">
	// <![CDATA[
		$('#iniciar-modal').click(function() {
			if ($(this).data('iniciar-modal')) {
				$('#expedient-iniciar-modal').heliumModal({
					modalUrl: $(this).attr('href'),
					refrescarTaula: false,
					refrescarAlertes: true,
					refrescarPagina: false,
					adjustWidth: false,
					adjustHeight: true,
					maximize: true,
					alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
					valignTop: true,
					buttonContainerId: 'formButtons'
				});
				return false;
			} else { 
				return true;
			}
		});
	//]]>
	</script>
	</script>

	<div id="expedient-iniciar-modal"></div>
	<div id="expedient-aturar-modal"></div>
	
</body>
</html>
