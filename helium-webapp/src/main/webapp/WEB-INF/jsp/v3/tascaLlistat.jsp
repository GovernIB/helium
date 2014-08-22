<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><fmt:message key="index.inici" /></title>
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
		ajaxSourceUrl: "<c:url value="/v3/tasca/datatable"/>",
		localeUrl: "<c:url value="/js/dataTables-locales/dataTables_locale_ca.txt"/>",
		alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>"
	});
	$("#tascaConsultaCommand button[value='netejar']").click(function() {
		$('#tascaConsultaCommand')[0].reset();
	});
	$("#mostrarTasquesPersonalsCheck").click(function() {
		$("input[name=mostrarTasquesPersonals]").val(!$("#mostrarTasquesPersonalsCheck").hasClass('active'));
		$('#tascaConsultaCommand').submit();
	});
	$("#mostrarTasquesGrupCheck").click(function() {
		$("input[name=mostrarTasquesGrup]").val(!$("#mostrarTasquesGrupCheck").hasClass('active'));
		$('#tascaConsultaCommand').submit();
	});
	$('#filtresCollapsable').on('hide', function () {
		$('#filtresCollapse i').attr("class", "icon-chevron-down");
		$("input[name=filtreDesplegat]").val("false");
	});
	$('#filtresCollapsable').on('show', function () {
		$('#filtresCollapse i').attr("class", "icon-chevron-up");
		$("input[name=filtreDesplegat]").val("true");
	});
	$('.datepicker').datepicker({language: 'ca', autoclose: true});
});

function confirmarSuspendre(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu suspendre aquesta tasca?");
}
function confirmarReprendre(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu reprendre aquesta tasca?");
}
function confirmarCancelar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu cancel·lar aquesta tasca? Aquesta acció no es podrà desfer.");
}
function confirmarAlliberar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu alliberar aquesta tasca?");
}
</script>
</head>
<body>

	<div id="tasca-reasignar-modal"></div>
	<div id="tasca-tramitacio-modal"></div>
	
	<input type="hidden" id="netejar" value="false"/>
	<form:form action="" method="post" cssClass="well formbox" commandName="tascaConsultaCommand">
		<form:hidden path="filtreDesplegat"/>
		<div id="filtresCollapsable" class="collapse<c:if test="${true or tascaConsultaCommand.filtreDesplegat}"> in</c:if>">
			<div class="row">
				<div class="col-md-2">
					<hel:inputText name="tasca" text="Tasca" placeholder="Tasca" inline="true"/>
				</div>
				<div class="col-md-4">
					<hel:inputText name="expedient" text="Expedient" placeholder="Expedient" inline="true"/>
				</div>
				<div class="col-md-3">
					<hel:inputSelect name="prioritat" text="Prioritat" placeholder="Prioritat" optionItems="${prioritats}" optionValueAttribute="valor" optionTextAttribute="codi" inline="true"/>
				</div>
				<div class="col-md-3">
					<hel:inputSelect name="expedientTipusId" text="Tipus d'expedient" placeholder="Tipus d'expedient" optionItems="${expedientTipusAccessibles}" optionValueAttribute="id" optionTextAttribute="nom"  disabled="${not empty expedientTipusActual}" inline="true"/>
					<%--c:set var="campPath" value="expedientTipusId"/>
					<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
					<c:choose>
						<c:when test="${not empty expedientTipusActual}">
							<input type="hidden" id="${campPath}" name="${campPath}" value="${expedientTipusActual.id}"/>
							<input type="text" name="${campPath}_actual" value="${expedientTipusActual.nom}" class="span12" disabled="disabled"/>
						</c:when>
						<c:otherwise>
							<form:select path="${campPath}" cssClass="span12">
								<option value="">Tipus d'expedient</option>
								<form:options items="${expedientTipusAccessibles}" itemLabel="nom" itemValue="id"/>
							</form:select>
						</c:otherwise>
					</c:choose--%>
				</div>
			</div>
			<div class="row">
				<div class="col-md-4">
					<label>Data creació</label>
					<div class="row">
						<div class="col-md-6">
							<hel:inputDate name="dataCreacioInicial" text="Data creació inicial" placeholder="dd/mm/yyyy" inline="true"/>
						</div>
						<div class="col-md-6">
							<hel:inputDate name="dataCreacioFinal" text="Data creació final" placeholder="dd/mm/yyyy" inline="true"/>
						</div>
					</div>
				</div>
				<div class="col-md-4">
					<label>Data límit</label>
					<div class="row">
						<div class="col-md-6">
							<hel:inputDate name="dataLimitInicial" text="Data límit inicial" placeholder="dd/mm/yyyy" inline="true"/>
						</div>
						<div class="col-md-6">
							<hel:inputDate name="dataLimitFinal" text="Data límit final" placeholder="dd/mm/yyyy" inline="true"/>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6">
				<form:hidden path="mostrarTasquesPersonals"/>
				<form:hidden path="mostrarTasquesGrup"/>
				<div class="btn-group">
					<a id="mostrarTasquesPersonalsCheck" href="javascript:void(0)" title="Mostrar tareas personales" class="btn btn-default<c:if test="${tascaConsultaCommand.mostrarTasquesPersonals}"> active</c:if>" data-toggle="button"><span class="fa fa-user"></span></a>
					<a id="mostrarTasquesGrupCheck" href="javascript:void(0)" title="Mostrar tareas de grupo" class="btn btn-default<c:if test="${tascaConsultaCommand.mostrarTasquesGrup}"> active</c:if>" data-toggle="button"><span class="fa fa-users"></span></a>
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

	<table id="taulaDades" class="table table-striped table-bordered table-hover" data-rdt-filtre-form-id="tascaConsultaCommand" data-rdt-seleccionable="false" <c:if test="${not empty preferenciesUsuari.numElementosPagina}">data-rdt-display-length-default="${preferenciesUsuari.numElementosPagina}"</c:if>>
		<thead>
			<tr>
				<th data-rdt-property="titol" data-rdt-template="cellPersonalGroupTemplate" data-rdt-visible="true" >
					Tasca
					<script id="cellPersonalGroupTemplate" type="text/x-jsrender">
						{{:titol}}
						{{if responsables != null && !agafada}}
							<span class="fa fa-users"></span>
						{{/if}}
						<div class="pull-right">
						{{if cancelada}}
							<span class="label label-danger" title="Cancel·lada">CA</span>
						{{/if}}
						{{if suspesa}}
							<span class="label label-info" title="Suspesa">SU</span>
						{{/if}}
						{{if oberta}}
							<span class="label label-warning" title="Oberta">OB</span>
						{{/if}}
						{{if completed}}
							<span class="label label-success" title="Finalitzada">FI</span>
						{{/if}}
						{{if agafada}}
							<span class="label label-default" title="Agafada">AG</span>
						{{/if}}
						</div>
					</script>
				</th>
				<th data-rdt-property="expedientIdentificador" data-rdt-visible="true">Expedient</th>
				<th data-rdt-property="dataCreacio" data-rdt-type="datetime" data-rdt-sorting="desc" data-rdt-visible="true">Creada el</th>
				<th data-rdt-property="expedientTipusNom" data-rdt-visible="true">Tipus d'expedient</th>
				<th data-rdt-property="dataLimit" data-rdt-visible="true">Data límit</th>
				<th data-rdt-property="prioritat" data-rdt-visible="true">Prioritat</th>
				<th data-rdt-property="id" data-rdt-template="cellAccionsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
 						<div class="dropdown"> 
 							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;Accions&nbsp;<span class="caret"></span></button> 
							<ul class="dropdown-menu"> 
								{{if oberta && !suspesa}}
 									<li><a rdt-link-modal="true" href="<c:url value="/v3/tasca/{{:expedientId}}/{{:id}}/tramitar"/>" data-tramitar-modal="true"><span class="fa fa-folder-open"></span> Tramitar</a></li>
									<li><a href="<c:url value="/v3/expedient/{{:expedientId}}/tasca/{{:id}}/delegar"/>"><span class="fa fa-hand-o-right"></span> Delegar</a></li>
								{{/if}}
								{{if responsables != null && !agafada && oberta && !suspesa}}
 									<li><a href="<c:url value="/v3/expedient/{{:expedientId}}/tasca/{{:id}}/tascaAgafar"/>"><span class="fa fa-chain"></span> Agafar</a></li>
								{{/if}}
								<li><a rdt-link-modal="true" data-reasignar-modal="true" href="<c:url value="/v3/expedient/{{:expedientId}}/tasca/{{:id}}/reassignar"/>"><span class="fa fa-share-square-o"></span>&nbsp;Reasignar</a></li>
								{{if oberta && !suspesa}}
									<li><a onclick="return confirmarSuspendre(event)" href="<c:url value="/v3/expedient/{{:expedientId}}/tasca/{{:id}}/suspendre"/>"><span class="fa fa-pause"></span> Suspendre</a></li>
								{{/if}}
								{{if suspesa}}
									<li><a onclick="return confirmarReprendre(event)" href="<c:url value="/v3/expedient/{{:expedientId}}/tasca/{{:id}}/reprendre"/>"><span class="fa fa-play"></span> Reprendre</a></li>
								{{/if}}
								{{if !cancelada}}
									<li><a onclick="return confirmarCancelar(event)" href="<c:url value="/v3/expedient/{{:expedientId}}/tasca/{{:id}}/cancelar"/>"><span class="fa fa-times"></span> Cancelar</a></li>
								{{/if}}
								{{if agafada && oberta}}
									<li><a onclick="return confirmarAlliberar(event)" href="<c:url value="/v3/expedient/{{:expedientId}}/tasca/{{:id}}/tascaAlliberar"/>"><span class="fa fa-chain-broken"></span> <spring:message code="tasca.pllistat.alliberar"/></a></li>
								{{/if}} 
 							</ul> 
 						</div>
					</script> 
				</th>
				<th data-rdt-property="agafada" data-rdt-visible="false"></th>
				<th data-rdt-property="cancelada" data-rdt-visible="false"></th>
				<th data-rdt-property="suspesa" data-rdt-visible="false"></th>
				<th data-rdt-property="oberta" data-rdt-visible="false"></th>
				<th data-rdt-property="completed" data-rdt-visible="false"></th>
				<th data-rdt-property="expedientId" data-rdt-visible="false"></th>
				<th data-rdt-property="responsables" data-rdt-visible="false"></th>
			</tr>
		</thead>
	</table>
<script type="text/javascript">
$('#taulaDades a').click(function() {
	if ($(this).data('tramitar-modal')) {
		$('#tasca-tramitacio-modal').heliumModal({
			modalUrl: $(this).attr('href'),
			refrescarTaula: false,
			refrescarAlertes: false,
			refrescarPagina: false,
			adjustWidth: false,
			adjustHeight: true,
			maximize: true,
			alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
			valignTop: true,
			buttonContainerId: 'formFinalitzar',
		});
		return false;
	} else if ($(this).data('reasignar-modal')) {
		$('#tasca-reasignar-modal').heliumModal({
			modalUrl: $(this).attr('href'),
			refrescarTaula: false,
			refrescarAlertes: true,
			refrescarPagina: false,
			adjustWidth: false,
			adjustHeight: true,
			maximize: true,
			alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>",
			valignTop: true,
			buttonContainerId: 'formReasignar'
		});
		return false;
	} else {
		return true;
	}
});
</script>
</body>
</html>
