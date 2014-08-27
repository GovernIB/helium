<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="tasca.llistat.titol"/></title>
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
	$("#mostrarTasquesPersonalsCheck").click(function() {
		$("input[name=mostrarTasquesPersonals]").val(!$("#mostrarTasquesPersonalsCheck").hasClass('active'));
		$('#tascaConsultaCommand').submit();
	});
	$("#mostrarTasquesGrupCheck").click(function() {
		$("input[name=mostrarTasquesGrup]").val(!$("#mostrarTasquesGrupCheck").hasClass('active'));
		$('#tascaConsultaCommand').submit();
	});
});
</script>
</head>
<body>

	<form:form action="" method="post" cssClass="well formbox" commandName="tascaConsultaCommand">
		<form:hidden path="filtreDesplegat"/>
		<div id="filtresCollapsable" class="collapse<c:if test="${true or tascaConsultaCommand.filtreDesplegat}"> in</c:if>">
			<div class="row">
				<div class="col-md-2">
					<hel:inputText name="tasca" textKey="tasca.llistat.filtre.camp.titol" placeholderKey="tasca.llistat.filtre.camp.titol" inline="true"/>
				</div>
				<div class="col-md-4">
					<hel:inputText name="expedient" textKey="tasca.llistat.filtre.camp.expedient" placeholderKey="tasca.llistat.filtre.camp.expedient" inline="true"/>
				</div>
				<div class="col-md-3">
					<hel:inputSelect name="prioritat" textKey="tasca.llistat.filtre.camp.prioritat" placeholderKey="tasca.llistat.filtre.camp.prioritat" optionItems="${prioritats}" optionValueAttribute="valor" optionTextAttribute="codi" inline="true"/>
				</div>
				<div class="col-md-3">
					<hel:inputSelect name="expedientTipusId" textKey="tasca.llistat.filtre.camp.tipexp" placeholderKey="tasca.llistat.filtre.camp.tipexp" optionItems="${expedientTipusAccessibles}" optionValueAttribute="id" optionTextAttribute="nom"  disabled="${not empty expedientTipusActual}" inline="true"/>
				</div>
			</div>
			<div class="row">
				<div class="col-md-4">
					<label><spring:message code="tasca.llistat.filtre.camp.datcre"/></label>
					<div class="row">
						<div class="col-md-6">
							<hel:inputDate name="dataCreacioInicial" textKey="tasca.llistat.filtre.camp.datcre.ini" placeholder="dd/mm/yyyy" inline="true"/>
						</div>
						<div class="col-md-6">
							<hel:inputDate name="dataCreacioFinal" textKey="tasca.llistat.filtre.camp.datcre.fin" placeholder="dd/mm/yyyy" inline="true"/>
						</div>
					</div>
				</div>
				<div class="col-md-4">
					<label><spring:message code="tasca.llistat.filtre.camp.datlim"/></label>
					<div class="row">
						<div class="col-md-6">
							<hel:inputDate name="dataLimitInicial" textKey="tasca.llistat.filtre.camp.datlim.ini" placeholder="dd/mm/yyyy" inline="true"/>
						</div>
						<div class="col-md-6">
							<hel:inputDate name="dataLimitFinal" textKey="tasca.llistat.filtre.camp.datlim.fin" placeholder="dd/mm/yyyy" inline="true"/>
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
					<button id="mostrarTasquesPersonalsCheck" title="<spring:message code="tasca.llistat.filtre.camp.mostrar.usuari"/>" class="btn btn-default<c:if test="${tascaConsultaCommand.mostrarTasquesPersonals}"> active</c:if>" data-toggle="button"><span class="fa fa-user"></span></button>
					<button id="mostrarTasquesGrupCheck" title="<spring:message code="tasca.llistat.filtre.camp.mostrar.grup"/>" class="btn btn-default<c:if test="${tascaConsultaCommand.mostrarTasquesGrup}"> active</c:if>" data-toggle="button"><span class="fa fa-users"></span></button>
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

	<table id="taulaDades" class="table table-striped table-bordered table-hover" data-rdt-filtre-form-id="tascaConsultaCommand" data-rdt-seleccionable="false" <c:if test="${not empty preferenciesUsuari.numElementosPagina}">data-rdt-display-length-default="${preferenciesUsuari.numElementosPagina}"</c:if>>
		<thead>
			<tr>
				<th data-rdt-property="titol" data-rdt-template="cellPersonalGroupTemplate" data-rdt-visible="true" >
					<spring:message code="tasca.llistat.columna.titol"/>
					<script id="cellPersonalGroupTemplate" type="text/x-jsrender">
						{{:titol}}
						{{if responsables != null && !agafada}}
							<span class="fa fa-users"></span>
						{{/if}}
						<div class="pull-right">
						{{if cancelada}}
							<span class="label label-danger" title="<spring:message code="enum.tasca.etiqueta.CA"/>">CA</span>
						{{/if}}
						{{if suspesa}}
							<span class="label label-info" title="<spring:message code="enum.tasca.etiqueta.SU"/>">SU</span>
						{{/if}}
						{{if oberta}}
							<span class="label label-warning" title="<spring:message code="enum.tasca.etiqueta.PD"/>">PD</span>
						{{/if}}
						{{if completed}}
							<span class="label label-success" title="<spring:message code="enum.tasca.etiqueta.FI"/>">FI</span>
						{{/if}}
						{{if agafada}}
							<span class="label label-default" title="<spring:message code="enum.tasca.etiqueta.AG"/>">AG</span>
						{{/if}}
						</div>
					</script>
				</th>
				<th data-rdt-property="expedientIdentificador" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.expedient"/></th>
				<th data-rdt-property="expedientTipusNom" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.tipexp"/></th>
				<th data-rdt-property="dataCreacio" data-rdt-type="datetime" data-rdt-sorting="desc" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.creada"/></th>
				<th data-rdt-property="dataLimit" data-rdt-type="datetime" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.limit"/></th>
				<th data-rdt-property="prioritat" data-rdt-visible="true"><spring:message code="tasca.llistat.columna.prioritat"/></th>
				<th data-rdt-property="id" data-rdt-template="cellAccionsTemplate" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
 						<div class="dropdown"> 
 							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button> 
							<ul class="dropdown-menu"> 
								{{if oberta && !suspesa}}
 									<li><a href="<c:url value="../v3/expedient/{{:expedientId}}/tasca/{{:id}}"/>" data-rdt-link-modal="true"><span class="fa fa-folder-open"></span> <spring:message code="tasca.llistat.accio.tramitar"/></a></li>
									<li><a href="<c:url value="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/delegar"/>" data-rdt-link-modal="true"><span class="fa fa-hand-o-right"></span> <spring:message code="tasca.llistat.accio.delegar"/></a></li>
								{{/if}}
								{{if responsables != null && !agafada && oberta && !suspesa}}
 									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/agafar" data-rdt-link-ajax="true"><span class="fa fa-chain"></span> <spring:message code="tasca.llistat.accio.agafar"/></a></li>
								{{/if}}
								<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/reassignar" data-rdt-link-modal="true"><span class="fa fa-share-square-o"></span>&nbsp;<spring:message code="tasca.llistat.accio.reassignar"/></a></li>
								{{if oberta && !suspesa}}
									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/suspendre" data-rdt-link-confirm="<spring:message code="tasca.llistat.confirmacio.suspendre"/>"><span class="fa fa-pause"></span> <spring:message code="tasca.llistat.accio.suspendre"/></a></li>
								{{/if}}
								{{if suspesa}}
									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/reprendre" data-rdt-link-confirm="<spring:message code="tasca.llistat.confirmacio.reprendre"/>"><span class="fa fa-play"></span> <spring:message code="tasca.llistat.accio.reprendre"/></a></li>
								{{/if}}
								{{if !cancelada}}
									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/cancelar" data-rdt-link-confirm="<spring:message code="tasca.llistat.confirmacio.cancelar"/>"><span class="fa fa-times"></span> <spring:message code="tasca.llistat.accio.cancelar"/></a></li>
								{{/if}}
								{{if agafada && oberta}}
									<li><a href="../v3/expedient/{{:expedientId}}/tasca/{{:id}}/alliberar" data-rdt-link-confirm="<spring:message code="tasca.llistat.confirmacio.alliberar"/>"><span class="fa fa-chain-broken"></span> <spring:message code="tasca.llistat.accio.alliberar"/></a></li>
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

</body>
</html>
