<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/distribucio" prefix="dis"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


<%
	pageContext.setAttribute(
			"isRolActualAdministrador",
			es.caib.distribucio.war.helper.RolHelper.isRolActualAdministrador(request));
	pageContext.setAttribute(
			"isRolActualAdminLectura",
			es.caib.distribucio.war.helper.RolHelper.isRolActualAdminLectura(request));
%>


<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<dis:blocIconaContingutNoms/>
<html>
<head>
	<title><spring:message code="anotacions.admin.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/webjars/select2/4.0.6-rc.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<c:if test="${requestLocale == 'en'}">
		<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/select2.min.js"/>"></script> 
	</c:if>
	<script src="<c:url value="/js/select2-locales/select2_${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_ca.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.6-rc.1/dist/js/i18n/${requestLocale}.js"/>"></script>
	<link href="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/css/bootstrap-datepicker.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/js/bootstrap-datepicker.min.js"/>"></script>
	<script src="<c:url value="/webjars/bootstrap-datepicker/1.6.1/dist/locales/bootstrap-datepicker.${requestLocale}.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<script src="<c:url value="/webjars/datatables.net-select/1.1.2/js/dataTables.select.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-select-bs/1.1.2/css/select.bootstrap.min.css"/>" rel="stylesheet"></link>

<style>
table.dataTable tbody > tr.selected, table.dataTable tbody > tr > .selected {
	background-color: #fcf8e3;
	color: #666666;
}
table.dataTable thead > tr.selectable > :first-child, table.dataTable tbody > tr.selectable > :first-child {
	cursor: pointer;
}
table.dataTable tbody tr.selected a, table.dataTable tbody th.selected a, table.dataTable tbody td.selected a  {
    color: #333;
}

div.extracteColumn {
    word-wrap: break-word;
    overflow-wrap: break-word;
    overflow-wrap: anywhere;
}
	
</style>
	
<script>
var mostrarInactives = '${registreFiltreCommand.mostrarInactives}' === 'true';
var bustiesInactives = [];
var tipusDocumentacioFisica = '${tipusDocumentacio}';

//Funció per donar format als items de la select de bústies segons si estan actives o no
function formatSelectBustia(item) {
	if (bustiesInactives.includes(item.id))
		return $("<span>" + item.text + " <span class='fa fa-exclamation-triangle text-warning' title=\"<spring:message code='bustia.list.avis.bustia.inactiva'/>\"></span></span>");
	else
		return item.text;
}

function formatSelectTipusDocumentacio(item) {
	if (item.text == '<spring:message code="registre.tipus.doc.fisica.enum.PAPER"/>'){
		return $("<span> <span class='fa fa-archive text-danger'> </span> " + item.text + "</span>");
	}else if (item.text == '<spring:message code="registre.tipus.doc.fisica.enum.DIGIT_PAPER"/>'){
		return $("<span> <span class='fa fa-file-code-o text-warning'> </span> <span class='fa fa-archive text-warning'> </span> " + item.text + "</span>");
	}else if (item.text == '<spring:message code="registre.tipus.doc.fisica.enum.DIGIT"/>'){
		return $("<span> <span class='fa fa-file-code-o text-success'> </span> " + item.text + "</span>");
	}else {
		return '<spring:message code="bustia.list.filtre.tipusDocFisica"/>';
	}
}

function formatSelectUnitat(item) {
	if (!item.id) {
	    return item.text;
	}
	if (item.data && item.data.estat=="V"){
		return item.text;
	} else {
		return $("<span>" + item.text + " <span class='fa fa-exclamation-triangle text-warning' title=\"<spring:message code='unitat.filtre.avis.obsoleta'/>\"></span></span>");
	}
}

$(document).ready(function() {	
	$("#reintents .select2").css("width", "29.5rem");
	$("input:visible:enabled:not([readonly]),textarea:visible:enabled:not([readonly]),select:visible:enabled:not([readonly])").first().focus();
	
	$('#unitatId').on('change', function (e) {
		$('#mostrarInactives').change();
	});
	
	$('#netejarFiltre').click(function(e) {
		$('#bustia').val('');
		$('#procesEstatSimple').val('PENDENT').change();
		$('#mostrarInactives').val(false).change();
		$('#mostrarInactivesBtn').removeClass('active');
		$('#tipusDocFisica').val('').change();
		$('#nomesAmbErrorsBtn').removeClass('active');
		$('#nomesAmbErrors').val(false);
		$('#estat').val(null).trigger('change');
		$('#enviatPerEmail').val(null).change();
		$('#nomesAmbEsborranysBtn').removeClass('active');
		$('#nomesAmbEsborranys').val(false);
		$('#sobreescriure').val(null).trigger('change');
		$('#reintents').val(null).trigger('change');
	});

	$('#nomesAmbErrorsBtn').click(function() {
		nomesAmbErrors = !$(this).hasClass('active');
		// Modifica el formulari
		$('#nomesAmbErrors').val(nomesAmbErrors);
	})

	$('#nomesAmbEsborranysBtn').click(function() {
		nomesAmbEsborranys = !$(this).hasClass('active');
		// Modifica el formulari
		$('#nomesAmbEsborranys').val(nomesAmbEsborranys);
	})

	var selectButtonsInitialized = false;
	
	$('#taulaDades').on( 'draw.dt', function () {
	
		if (!selectButtonsInitialized) {
			selectButtonsInitialized = true;
			$('#seleccioAll').on('click', function(e) {
				$.get(
						"registreAdmin/select",
						function(data) {
							$("#seleccioCount").html(data);
							$('#taulaDades').webutilDatatable('refresh');
						}
				);
				return false;
			});
			$('#seleccioNone').on('click', function() {
				$.get(
						"registreAdmin/deselect",
						function(data) {
							$("#seleccioCount").html(data);
							$('#taulaDades').webutilDatatable('select-none');
						}
				);
				return false;
			});	
		}
		
		$("tr", this).each(function(){
			if ($(this).find("#detall-button").length > 0) {
				var pageInfo = $('#taulaDades').dataTable().api().table().page.info();
				var registreTotal = pageInfo.recordsTotal;
				var registreNumero = $(this).data('rowIndex');
				// Afegeix els paràmetres a l'enllaç dels detalls
				var url = new URL(window.location);
				var params = url.searchParams;
				params.set("registreNumero", registreNumero);
				params.set("registreTotal", registreTotal);
				var sort = $('#taulaDades').dataTable().fnSettings().aaSorting
				if (sort.length > 0) {
					params.set("ordreColumn", $($('#taulaDades').dataTable().api().column(sort[0][0]).header()).data('colName'))
					params.set("ordreDir", sort[0][1]);						
				}			
				var $a = $($(this).find("#detall-button"));
				$a.attr('href', $a.attr('href') + '?' + params.toString());
				// Afegeix els paràmetres a l'enllaç de la fila
				$(this).data('href', $(this).data('href') + '?' + params.toString());
			}
		});
		
	} ).on('selectionchange.dataTable', function (e, accio, ids) {
		$.get(
				"registreAdmin/" + accio,
				{ids: ids},
				function(data) {
					$("#seleccioCount").html(data);
				}
		);
	});
	
	$('#mostrarInactivesBtn').click(function() {
		mostrarInactives = !$(this).hasClass('active');
		// Modifica el formulari
		$('#mostrarInactives').val(mostrarInactives).change();
		$(this).blur();
	});
	$('#mostrarInactives').change(function() {
		$('#bustia').prop('disabled', true);
		var actual = $('#bustia').val();
		$('#bustia').select2('val', '', true);
		$('#bustia option[value!=""]').remove();
		var baseUrl = "<c:url value='/registreAdmin/busties'/>?mostrarInactives=" + $(this).val();
		if ($('#unitatId').val())
			baseUrl += "&unitatId=" + $('#unitatId').val();
		$.get(baseUrl)
			.done(function(data) {
				bustiesInactives = [];
				for (var i = 0; i < data.length; i++) {
					$('#bustia').append('<option value="' + data[i].id + '">' + data[i].nom + '</option>');
					if (!data[i].activa) {
						bustiesInactives.push(data[i].id.toString());
					}
				}
				$('#bustia').removeAttr('disabled');
				$('#bustia').val(actual).change();
			})
			.fail(function() {
				alert("<spring:message code="error.jquery.ajax"/>");
			});
	});
	$('#mostrarInactives').change();
	
	$('#showModalProcesEstatButton').click(function(e) {
		$('#modalProcesEstat').modal();
		e.stopPropagation();
	});	
	
	$('#numero').focus();
	
	$('form').submit(function() {
		$.get(
				"registreAdmin/deselect",
				function(data) {
					$("#seleccioCount").html(data);
					$('#taulaDades').webutilDatatable('select-none');
				}
		);
		return false;
	});

});

</script>
</head>
<body>
	<form:form action="" method="post" cssClass="well" commandName="registreFiltreCommand">
		
		<button id="filtrar" type="submit" name="accio" value="filtrar" class="btn btn-primary" style="display:none"></button>
		
		<div class="row">
			<div class="col-md-2">
				<dis:inputText name="numero" inline="true" placeholderKey="bustia.list.filtre.numero"/>
			</div>
			<div class="col-md-2">
				<dis:inputText name="titol" inline="true" placeholderKey="bustia.list.filtre.titol"/>
			</div>
			
			<div class="col-md-3">
				<dis:inputText name="numeroOrigen" inline="true" placeholderKey="bustia.list.filtre.origen.num"/>
			</div>
			<div class="col-md-3">
				<c:url value="/userajax/remitent" var="urlConsultaInicial"/>
				<c:url value="/userajax/remitent" var="urlConsultaLlistat"/>
				<dis:inputSuggest 
					name="remitent" 
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					placeholderKey="bustia.list.filtre.remitent"
					suggestValue="nom"
					suggestText="codiAndNom"
					inline="true"/>
			</div>
			<div class="col-md-2">
				<dis:inputText name="interessat" inline="true" placeholderKey="bustia.list.filtre.interessat"/>
			</div>
		</div>
		<div class="row">
			<div class="col-md-2">
				<dis:inputDate name="dataRecepcioInici" inline="true" placeholderKey="bustia.list.filtre.data.rec.inical"/>
			</div>
			<div class="col-md-2">
				<dis:inputDate name="dataRecepcioFi" inline="true" placeholderKey="bustia.list.filtre.data.rec.final"/>
			</div>
			<div class="col-md-3">
			
				<c:url value="/unitatajax/unitat" var="urlConsultaInicial"/>
				<c:url value="/unitatajax/nomesUnitatsAmbBusties" var="urlConsultaLlistat"/>
				<dis:inputSuggest 
					name="unitatId"
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					inline="true" 
					placeholderKey="contingut.admin.filtre.uo"
					suggestValue="id"
					suggestText="codiAndNom" 
					optionTemplateFunction="formatSelectUnitat" />
			</div>
			<div class="col-md-3">			
				<div class="row">
					<div class="col-md-10">
						<dis:inputSelect 
							name="bustia" 
							optionItems="${replacedByJquery}" 
							optionValueAttribute="id" 
							optionTextAttribute="nom" 
							emptyOption="true" 
							placeholderKey="bustia.list.filtre.bustia" 
							inline="true"
							optionMinimumResultsForSearch="0" 
							optionTemplateFunction="formatSelectBustia" />
					</div>
					<div class="col-md-2" style="padding-left: 0;">
						<button id="mostrarInactivesBtn" style="width: 45px;" title="<spring:message code="bustia.list.filtre.mostrarInactives"/>" class="btn btn-default btn-sm<c:if test="${registreFiltreCommand.mostrarInactives}"> active</c:if>" data-toggle="button">
							<span class="fa-stack" aria-hidden="true">
								<i class="fa fa-inbox fa-stack-1x"></i>
		    	    			<i class="fa fa-ban fa-stack-2x"></i>
		   					</span>
						</button>
						<dis:inputHidden name="mostrarInactives"/>
					</div>
				</div>			
			</div>			
			<div class="col-md-2">
				<dis:inputSelect name="enviatPerEmail" netejar="false" optionEnum="RegistreEnviatPerEmailEnumDto" placeholderKey="bustia.list.filtre.back.email" emptyOption="true" inline="true"/>
			</div>
		</div>
		<div class="row">			
			<div class="col-md-2">
				<div class="row">
					<div class="col-sm-9">
						<dis:inputSelect 
							name="tipusDocFisica" 
							netejar="false" 
							optionItems="${tipusDocumentacio}" 
							optionValueAttribute="value" 
							optionTextKeyAttribute="text" 
							placeholderKey="bustia.list.filtre.tipusDocFisica" 
							emptyOption="true" 
							inline="true" 
							optionTemplateFunction="formatSelectTipusDocumentacio"/>
					</div>
					<div class="col-sm-3" style="padding-left: 0;">
						<button id="nomesAmbEsborranysBtn" style="width: 45px;" title="<spring:message code="contingut.admin.filtre.nomesAmbEsborranys"/>" class="btn btn-default <c:if test="${registreFiltreCommand.nomesAmbEsborranys}">active</c:if>" data-toggle="button"><span class="fa fa-warning"></span></button>
						<dis:inputHidden name="nomesAmbEsborranys"/>
					</div>
				</div>
			</div>
			<div class="col-md-2">
				<dis:inputSelect name="backCodi" placeholderKey="bustia.list.filtre.back.codi" optionItems="${backoffices}" emptyOption="true" optionValueAttribute="codi" optionTextAttribute="nom" inline="true"/>
			</div>
			<div class="col-md-3">
				<dis:inputSelect name="procesEstatSimple"  netejar="false" optionEnum="RegistreProcesEstatSimpleEnumDto" placeholderKey="bustia.list.filtre.estat" emptyOption="true" inline="true"/>			
			</div>
			<div class="col-md-3">
				<div class="row">
					<div class="col-md-10">
						<dis:inputSelect name="estat" inline="true" netejar="false" optionEnum="RegistreProcesEstatEnum" placeholderKey="contingut.admin.filtre.estat.especific" emptyOption="true"/>
					</div>
					<div class="col-md-2" style="padding-left: 0;">
						<button id="nomesAmbErrorsBtn" style="width: 45px;" title="<spring:message code="contingut.admin.filtre.nomesAmbErrors"/>" class="btn btn-default <c:if test="${registreFiltreCommand.nomesAmbErrors}">active</c:if>" data-toggle="button"><span class="fa fa-warning"></span></button>
						<dis:inputHidden name="nomesAmbErrors"/>
					</div>
				</div>
			</div>
			<div class="col-md-2">
				<dis:inputSelect name="sobreescriure" netejar="false" optionEnum="RegistreMarcatPerSobreescriureEnumDto" placeholderKey="registre.admin.list.filtre.sobreescriure" emptyOption="true" inline="true"/>
			</div>		
		</div>	
		<div class="row">	
			<div class="col-md-2"></div>
			<div class="col-md-2">
				<dis:inputSelect 
					name="nombreAnnexes" 
					netejar="true" 
					optionEnum="RegistreNombreAnnexesEnumDto" 
					placeholderKey="registre.filtre.camp.formulari" 
					emptyOption="true" 
					inline="true"/>
			</div>
			<div class="col-md-3">			
				<c:url value="/procedimentajax/procediment" var="urlConsultaInicial"/>
				<c:url value="/procedimentajax/procediments" var="urlConsultaLlistat"/>
				<dis:inputSuggest 
					name="procedimentCodi"
					urlConsultaInicial="${urlConsultaInicial}" 
					urlConsultaLlistat="${urlConsultaLlistat}" 
					inline="true" 
					placeholderKey="registre.admin.list.filtre.procediment"
					suggestValue="codiSia"
					suggestText="codiNom" />
			</div>
			<div class="col-md-3">
				<dis:inputSelect id="reintents" name="reintents" netejar="true" optionEnum="RegistreFiltreReintentsEnumDto" placeholderKey="registre.admin.list.filtre.reintents" emptyOption="true" inline="true"/>		
			</div>
			<div class="col-md-2">
				<button id="netejarFiltre" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.boto.netejar"/></button>
				<button id="filtrar" type="submit" name="accio" value="filtrar" class="ml-2 btn btn-primary"><span class="fa fa-filter"></span> <spring:message code="comu.boto.filtrar"/></button>
			</div>	
		</div>
	</form:form>
	
	<c:set var="rol" value="admin"/>
	<script id="botonsTemplate" type="text/x-jsrender">

	
		<div class="text-right">
		
			<div class="btn-group">
				<button id="seleccioAll" title="<spring:message code="bustia.pendent.contingut.seleccio.tots"/>" class="btn btn-default"><span class="fa fa-check-square-o"></span></button>
				<button id="seleccioNone" title="<spring:message code="bustia.pendent.contingut.seleccio.cap"/>" class="btn btn-default"><span class="fa fa-square-o"></span></button>

				<button class="btn btn-default" data-toggle="dropdown"><span id="seleccioCount" class="badge">${fn:length(seleccio)}</span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
				<ul class="dropdown-menu dropdown-left-medium">
				  <c:if test="${isRolActualAdministrador}">
					<li><a href="registreAdmin/reintentarProcessamentMultiple" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
						<span class="fa fa-cog"></span> <spring:message code="registre.detalls.accio.reintentar"/></span>
					</a></li>
					<li><a href="registreAdmin/reintentarEnviamentBackofficeMultiple" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
						<span class="fa fa-cog"></span> <spring:message code="registre.detalls.accio.reintentarEnviamentBackoffice"/></span>
					</a></li>
					<li><a href="registreAdmin/marcarSobreescriureMultiple" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
						<span class="fa fa-history"></span> <spring:message code="registre.admin.list.accio.marcar.sobreescriure"/></span>
					</a></li>
					<li><a href="registreComun/marcarPendentMultiple/${rol}" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
						<span class="fa fa-undo"></span> <spring:message code="registre.user.accio.marcar.pendent"/> ...
					</a></li>
				  </c:if>	
					<li><a href="registreComun/exportar/${rol}?format=ods">
						<span class="fa fa-download"></span> <spring:message code="registre.user.accio.grup.exportar.filtre.anotacio.ods"/>
					</a></li>	
					<li><a href="registreComun/exportar/${rol}?format=csv">
						<span class="fa fa-download"></span> <spring:message code="registre.user.accio.grup.exportar.filtre.anotacio.csv"/>
					</a></li>	
					
					<c:if test="${isRolActualAdministrador}">
						<li class="divider"></li>
						<li class="divider"></li>

						<li><a href="registreComun/classificarMultiple/${rol}" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
							<span class="fa fa-inbox"></span> <spring:message code="bustia.pendent.accio.classificar"/>
						</a></li>
						<li><a href="registreComun/registreReenviarMultiple/${rol}" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
							<span class="fa fa-send"></span> <spring:message code="bustia.pendent.accio.reenviar"/>
						</a></li>
						<li><a href="registreComun/marcarProcessatMultiple/${rol}" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
							<span class="fa fa-check-circle-o"></span> <spring:message code="bustia.pendent.accio.marcar.processat"/>
						</a></li>
						<!--<li><a href="registreComun/marcarPendentMultiple/${rol}" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
							<span class="fa fa-undo"></span> <spring:message code="registre.user.accio.marcar.pendent"/>
						</a></li>-->
						<li><a href="registreComun/enviarViaEmailMultiple/${rol}" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
							<span class="fa fa-envelope"></span> <spring:message code="bustia.pendent.accio.enviarViaEmail"/>
						</a></li>
						<li><a href="registreComun/enviarIProcessarMultiple/${rol}" aria-haspopup="true" aria-expanded="false" data-toggle="modal" data-maximized="true">
							<span class="fa fa-envelope"></span>+<span class="fa fa-check-circle-o"></span>
							<spring:message code="bustia.pendent.accio.enviarIProcessar"/>
						</a></li>					
				  </c:if>			
				</ul>
			</div>
		</div>
	</script>	

	<script id="rowhrefTemplate" type="text/x-jsrender">./registreAdmin/{{:id}}/detall</script>
	<table
		id="taulaDades"
		data-refresh-tancar="true"
		data-toggle="datatable"
		data-url="<c:url value="/registreAdmin/datatable"/>"
		data-filter="#registreFiltreCommand"
		data-botons-template="#botonsTemplate"
		data-selection-enabled="true"
		data-default-order="14"
		data-default-dir="desc"
		class="table table-bordered table-striped"
		data-rowhref-template="#rowhrefTemplate" 
		data-rowhref-toggle="modal"
		data-rowhref-maximized="true">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"></th>
				<th data-col-name="error" data-visible="false"></th>
				<th data-col-name="alerta" data-visible="false"></th>
				<th data-col-name="enviatPerEmail" data-visible="false"></th>
				<th data-col-name="enviamentsPerEmail" data-visible="false"></th>
				<th data-col-name="procesEstatSimple"  data-visible="false">
				<th data-col-name="procesError" data-visible="false">#</th>
				<th data-col-name="sobreescriure" data-visible="false">
				<th data-col-name="arxiuTancat" data-visible="false">
				<th data-col-name="numero" width="10%"><spring:message code="bustia.pendent.columna.numero"/></th>			
				<th data-col-name="extracte" width="25%" style="min-width:150px;" data-template="#extracteTemplate">
					<spring:message code="bustia.pendent.columna.titol"/>
					<script id="extracteTemplate" type="text/x-jsrender">
						<div class="extracteColumn">
							{{:extracte}}
						</div>
					</script>
				</th>						
				<th data-col-name="documentacioFisicaCodi" data-orderable="true" width="3%"  data-template="#docFisTemplate">
				<spring:message code="bustia.pendent.columna.tipusDocFisica"/>
					<script id="docFisTemplate" type="text/x-jsrender">
						<center>
						{{if documentacioFisicaCodi == '1'}}
							<span class="fa fa-archive" style="color: #D9534F;" title="<spring:message code="registre.tipus.doc.fisica.enum.PAPER"/>"/>
						{{else documentacioFisicaCodi == '2'}}
							<span class="fa fa-file-code-o" style="color: #F0AD4E;" title="<spring:message code="registre.tipus.doc.fisica.enum.DIGIT_PAPER"/>"/>
							<span class="fa fa-archive" style="color: #F0AD4E;" title="<spring:message code="registre.tipus.doc.fisica.enum.DIGIT_PAPER"/>"/>
						{{else documentacioFisicaCodi == '3'}}
							<span class="fa fa-file-code-o" style="color: #5CB85C;" title="<spring:message code="registre.tipus.doc.fisica.enum.DIGIT"/>"/>
						{{/if}}
						</center>
					</script>
				</th>						
				<th data-col-name="numeroOrigen" width="5%"><spring:message code="bustia.list.filtre.origen.num"/></th>
				
				<th data-col-name="darrerMovimentUsuari" width="15%" data-orderable="false" data-template="#darrerMovimentTemplate">
					<spring:message code="bustia.pendent.columna.remitent"/>
					<script id="darrerMovimentTemplate" type="text/x-jsrender">
						{{if darrerMovimentUsuari}}
	 						{{if darrerMovimentOrigenUoAndBustia}}
 								<div align="left">
									/<span class="fa fa-sitemap" title="{{:darrerMovimentOrigenUoAndBustia}}"></span>/<span class="fa fa-inbox" title="{{:darrerMovimentOrigenUoAndBustia}}"></span>
								</div>
 								<div align="left">
									{{:darrerMovimentUsuari.nom}}
								</div>
 							{{else}}
 								<span class="fa fa-home" title=""></span>
 								{{:oficinaDescripcio}}<br/>({{:darrerMovimentUsuari.nom}})
	 						{{/if}}
 						{{else}}
							<span class="fa fa-ban" title="<spring:message code="bustia.pendent.columna.remitent.buit"/>"></span>
 						{{/if}}
					</script>
				</th>
				
				<th data-col-name="data" data-converter="datetime" ><spring:message code="bustia.pendent.columna.data"/></th>
				<th data-col-name="procesEstat" data-orderable="true" width="10%"  data-template="#estatTemplate">
					<spring:message code="bustia.pendent.columna.estat"/> <span class="fa fa-list" id="showModalProcesEstatButton" title="<spring:message code="bustia.user.proces.estat.legend"/>" style="cursor:over; opacity: 0.5"></span>
					<script id="estatTemplate" type="text/x-jsrender">
						{{if sobreescriure}}
							<span class="fa fa-history" title="<spring:message code="registre.admin.list.icon.marcat.sobreescriure"/>"></span>
						{{/if}}
						{{if enviatPerEmail}}
							<span class="fa fa-envelope" title="<spring:message code="contingut.registre.enviatPerEmail"/>:
							{{for enviamentsPerEmail}} {{>}} 
							{{/for}}"></span>
						{{/if}}
						{{if procesEstat == 'ARXIU_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.ARXIU_PENDENT"/>

							<span {{if reintentsEsgotat}} style="color: #a94442" {{else}} style="color: #8a6d3b" {{/if}} title="<spring:message code="contingut.registre.reintents.msg.seHanRealizat"/> {{:procesIntents}} <spring:message code="contingut.registre.reintents.msg.intentsDeUnMaximDe"/> {{:maxReintents}} <spring:message code="contingut.registre.reintents.msg.deGuardarAnnexosAlArxiu"/>">
								(<spring:message code="contingut.registre.reintents.msg.reintent"/> {{:procesIntents}}/{{:maxReintents}})
							</span>
						{{else procesEstat == 'REGLA_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.REGLA_PENDENT"/>
						{{else procesEstat == 'BUSTIA_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.BUSTIA_PENDENT"/>
						{{else procesEstat == 'BUSTIA_PROCESSADA'}}
							<spring:message code="registre.proces.estat.enum.BUSTIA_PROCESSADA"/>
						{{else procesEstat == 'BACK_PENDENT'}}
							<spring:message code="registre.proces.estat.enum.BACK_PENDENT"/>
						{{else procesEstat == 'BACK_COMUNICADA'}}
							<spring:message code="registre.proces.estat.enum.BACK_COMUNICADA"/>
						{{else procesEstat == 'BACK_REBUDA'}}
							<spring:message code="registre.proces.estat.enum.BACK_REBUDA"/>
						{{else procesEstat == 'BACK_PROCESSADA'}}
							<spring:message code="registre.proces.estat.enum.BACK_PROCESSADA"/>
						{{else procesEstat == 'BACK_REBUTJADA'}}
							<spring:message code="registre.proces.estat.enum.BACK_REBUTJADA"/>
						{{else procesEstat == 'BACK_ERROR'}}
							<spring:message code="registre.proces.estat.enum.BACK_ERROR"/>		
							
							<span {{if reintentsEsgotat}} style="color: #a94442" {{else}} style="color: #8a6d3b" {{/if}} title="<spring:message code="contingut.registre.reintents.msg.seHanRealizat"/> {{:procesIntents}} <spring:message code="contingut.registre.reintents.msg.intentsDeUnMaximDe"/> {{:maxReintents}} <spring:message code="contingut.registre.reintents.msg.deGuardarAnnexosAlArxiu"/>">
								(<spring:message code="contingut.registre.reintents.msg.reintent"/> {{:procesIntents}}/ {{:maxReintents}})
							</span>					
						{{/if}}
						{{if annexosEstatEsborrany > 0}}
							<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="registre.admin.list.icon.annexos.estat.esborrany"/>"></span>
						{{/if}}
					</script>

				</th>
				<th data-col-name="procesError" data-orderable="false" data-template="#procesErrorTemplate">
					<spring:message code="bustia.pendent.columna.avisos"/>
					<script id="procesErrorTemplate" type="text/x-jsrender">
						<center>
						{{if alerta}}
							<span class="fa fa-sticky-note-o text-warning" title="<spring:message code="contingut.errors.registre.regles.segonpla"/>"></span>
						{{/if}}
						{{if procesError != null}}
							{{if procesEstat == 'ARXIU_PENDENT'}}
								<span class="fa fa-warning text-danger" title="<spring:message code="registre.proces.estat.enum.ARXIU_PENDENT.error"/>"></span>
							{{else procesEstat == 'REGLA_PENDENT'}}
								<span class="fa fa-warning text-danger" title="<spring:message code="registre.proces.estat.enum.REGLA_PENDENT.error"/>"></span>
							{{else procesEstat == 'BACK_PENDENT'}}
								<span class="fa fa-warning text-danger" title="<spring:message code="registre.proces.estat.enum.BACK_PENDENT.error"/>"></span>
							{{else procesEstat == 'BACK_ERROR'}}
								<span class="fa fa-warning text-danger" title="<spring:message code="registre.proces.estat.enum.BACK_ERROR.error"/>"></span>
							{{else}}
								<span class="fa fa-warning text-danger" title="<spring:message code="registre.proces.estat.enum.default"/>"></span>
							{{/if}}
						{{/if}}
						</center>
					</script>
				</th>
				<th data-col-name="bustiaActiva" data-visible="false"></th>
				<th data-col-name="path" data-template="#cellPathTemplate" width="15%" data-orderable="false">
					<spring:message code="bustia.pendent.columna.localitzacio"/>
					<script id="cellPathTemplate" type="text/x-jsrender">
						{{if path}}
							{{for path}}/
								{{if bustia}}{{if #getIndex() == 0}}<span class="fa ${iconaUnitat}" title="<spring:message code="contingut.icona.unitat"/>"></span>{{else}}<span class="fa ${iconaBustia}" title="<spring:message code="contingut.icona.bustia"/>"></span>{{/if}}{{/if}}
								{{:nom}}
							{{/for}}
							{{if !bustiaActiva}}
								<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="bustia.list.avis.bustia.inactiva"/>"></span>
							{{/if}}
						{{else}}
							<span class="fa fa-exclamation-triangle text-warning" title="<spring:message code="bustia.list.avis.bustia.inactiva"/>"></span>
						{{/if}}
					</script>
				</th>
				<th data-col-name="interessatsResum" data-orderable="false">
					<spring:message code="bustia.pendent.columna.interessats"/>
				</th>				
				<th data-col-name="numComentaris" data-orderable="false" data-template="#cellPermisosTemplate" width="5%">
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						<a id="link-comentaris" href="./contingut/{{:id}}/comentaris/?isVistaMoviments=false" data-toggle="modal" data-refresh-tancar="true" data-modal-id="comentaris{{:id}}" class="btn btn-default"><span class="fa fa-lg fa-comments"></span>&nbsp;<span class="badge">{{:numComentaris}}</span></a>
					</script>
				</th>


				<th data-col-name="backCodi" data-orderable="true"><spring:message code="contingut.admin.columna.backoffice"/></th>
				
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button id="btn-accions" class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu dropdown-left-high">
								<li><a data-refresh-tancar="true" id="detall-button" href="registreAdmin/{{:id}}/detall" data-toggle="modal" data-maximized="true"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="contingut.admin.boto.detalls"/></a></li>

								<li><a data-refresh-tancar="true" href="./contingut/{{:id}}/log" data-toggle="modal" data-maximized="true"><span class="fa fa-list"></span>&nbsp;<spring:message code="comu.boto.historial"/></a></li>
								{{if alerta}}
									<li><a data-refresh-tancar="true" href="./registreUser/pendent/{{:id}}/alertes" data-toggle="modal"><span class="fa fa-sticky-note-o"></span>&nbsp;<spring:message code="bustia.pendent.accio.llistat.alertes"/></a></li>
								{{/if}}
								<c:if test="${isRolActualAdministrador}">
								<li role="separator" class="divider"></li>
								<li{{if procesEstat == 'ARXIU_PENDENT'}} class="disabled" {{/if}}><a data-refresh-tancar="true" {{if procesEstat != 'ARXIU_PENDENT'}} href="./registreUser/classificar/{{:id}}" {{/if}}  data-toggle="modal"><span class="fa fa-inbox"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.classificar"/> ...</a></li>
								<li role="separator" class="divider"></li>
								<li {{if procesEstat == 'ARXIU_PENDENT' && !reintentsEsgotat}} class="disabled" {{/if}}><a data-refresh-tancar="true" {{if !(procesEstat == 'ARXIU_PENDENT' && !reintentsEsgotat)}} href="./registreUser/enviarViaEmail/{{:id}}?isVistaMoviments=false" {{/if}} data-toggle="modal"><span class="fa fa-envelope"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.enviarViaEmail"/>...</a></li>
								<li><a data-refresh-tancar="true" href="./registreUser/pendent/{{:id}}/reenviar" data-toggle="modal" data-maximized="true"><span class="fa fa-send"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.reenviar"/>...</a></li>
								{{if procesEstatSimple == 'PENDENT'}}
									<li {{if !(procesEstat == 'BUSTIA_PENDENT' || (procesEstat == 'ARXIU_PENDENT' && reintentsEsgotat))}} class="disabled" {{/if}}><a data-refresh-tancar="true" {{if procesEstat == 'BUSTIA_PENDENT' || (procesEstat == 'ARXIU_PENDENT' && reintentsEsgotat)}} href="./registreUser/pendent/{{:id}}/marcarProcessat" {{/if}} data-toggle="modal"><span class="fa fa-check-circle-o"></span>&nbsp;&nbsp;<spring:message code="bustia.pendent.accio.marcar.processat"/>...</a></li>
									{{if !sobreescriure && !arxiuTancat}}
										<li><a data-refresh-tancar="true" href="./registreAdmin/{{:id}}/marcarSobreescriure"><span class="fa fa-history"></span>&nbsp;&nbsp;<spring:message code="registre.admin.list.accio.marcar.sobreescriure"/>...</a></li>
									{{/if}}	
								{{/if}}			
								{{if procesEstat == 'BUSTIA_PROCESSADA'}}
									<li ><a data-refresh-tancar="true" href="./registreUser/{{:id}}/marcarPendent" data-toggle="modal"><span class="fa fa-undo"></span>&nbsp;&nbsp;<spring:message code="registre.user.accio.marcar.pendent"/>...</a></li>
								{{/if}}
								</c:if>
								<li>
									<a data-refresh-tancar="true" href="<c:url value="/contingut/registre/{{:id}}/descarregarZip"/>">
										<span class="fa fa-download"></span> <spring:message code="registre.annex.descarregar.zip"/>
									</a>
								</li>
							</ul>
						</div>

					</script>
				</th>
				<th data-col-name="reintentsEsgotat" data-visible="false"></th>
				<th data-col-name="procesIntents" data-visible="false"></th>
				<th data-col-name="maxReintents" data-visible="false"></th>
				<th data-col-name="darrerMovimentOrigenUoAndBustia" data-visible="false" data-orderable="false"></th>
				<th data-col-name="oficinaDescripcio" data-visible="false" data-orderable="false"></th>
				<th data-col-name="annexosEstatEsborrany" data-visible="false" data-orderable="false"></th>
			</tr>
		</thead>
	</table>
	
	
	
	<!-- Modal pels estats del processament -->
	<div id="modalProcesEstat" class="modal fade">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" aria-label="<spring:message code="comu.boto.tancar"/>"><span aria-hidden="true">&times;</span></button>
					<h4 class="modal-title"><span class="fa fa-list"></span> <spring:message code="bustia.user.proces.estat.legend"></spring:message></h4>
				</div>
				<div class="modal-body">
					<ul>
						<c:set var="enumValues" value="<%=es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum.values()%>"/>
						<c:forEach items="${enumValues}" var="enumValue">
						  	<li>
						  		<strong><spring:message code="registre.proces.estat.enum.${enumValue}"/></strong> :
						  		<br/>
						  		<span><spring:message code="registre.proces.estat.enum.${enumValue}.info"/></span>
						  	</li>
						</c:forEach>
					</ul>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></button>
				</div>
			</div>
		</div>
	</div>	
		
</body>