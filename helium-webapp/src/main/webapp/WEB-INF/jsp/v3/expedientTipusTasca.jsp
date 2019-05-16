<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="ambInfoPropiaText"><spring:message code="expedient.tipus.form.camp.ambInfoPropia"/></c:set>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>

<c:choose>
	<c:when test="${not empty expedientTipus || not empty definicioProcesId}">
		
		<c:if test="${not empty expedientTipus && empty definicioProcesId && !expedientTipus.ambInfoPropia}">
			<div class="alert alert-warning">
				<span class="fa fa-exclamation-triangle"></span>
				<spring:message code="expedient.tipus.ambInfoPropia.avis" arguments="${ambInfoPropiaText}"></spring:message>
			</div>
		</c:if>
		<c:if test="${not empty definicioProces && not empty definicioProces.expedientTipus && definicioProces.expedientTipus.ambInfoPropia}">
			<div class="alert alert-warning">
				<span class="fa fa-exclamation-triangle"></span>
				<spring:message code="definicio.proces.ambInfoPropia.avis" arguments="${ambInfoPropiaText}"></spring:message>
			</div>
		</c:if>
		
		<form class="well">
			<div class="row">
				<div class="col-sm-6">
					<hel:inputSelect required="false" emptyOption="true" name="definicions" textKey="expedient.tipus.tasca.llistat.definicio.seleccionada" labelSize="4" optionItems="${definicions}" optionValueAttribute="codi" optionTextAttribute="valor"/>
				</div>
				<div class="col-sm-6">
					<hel:inputSelect required="false" emptyOption="true" name="versions" textKey="expedient.tipus.tasca.llistat.versio.seleccionada" optionItems="${versions}" optionValueAttribute="codi" optionTextAttribute="valor"/>
				</div>
			</div>
		</form>

		<table	id="expedientTipusTasca"
				data-rowId="id"
				data-toggle="datatable"
				data-url="${baseUrl}/tasca/datatable"
				data-paging-enabled="true"
				data-info-type="search+button"
				data-ordering="true"
				data-default-order="2"
				data-length-menu='[[10,25,50,-1],["10","25","50","<spring:message code="comu.totes"/>"]]'
				data-rowhref-toggle="modal"
				data-rowhref-template="#rowhrefTemplateTasques" 
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="jbpmName" width="20%" data-template="#cellExpedientTipusTascaJbpmKeyTemplate">
					<spring:message code="expedient.tipus.camp.llistat.columna.codi"/>
						<script id="cellExpedientTipusTascaJbpmKeyTemplate" type="text/x-jsrender">
								{{if heretat }}
									<span class="dada-heretada">{{:jbpmName}}</span> 
									<span class="label label-primary herencia" title="<spring:message code="expedient.tipus.tasca.llistat.codi.heretat"/>">R</span>
								{{else}}
									{{:jbpmName}}
									{{if sobreescriu }}
										<span class="label label-warning herencia" title="<spring:message code="expedient.tipus.tasca.llistat.codi.sobreescriu"/>">S</span>
									{{/if}}
								{{/if}}
						</script>
					</th>
					<th data-col-name="nom"><spring:message code="definicio.proces.tasca.llistat.columna.titol"/></th>
					<th data-col-name="inicial" width="20px" data-orderable="false" data-template="#cellDefinicioProcesInicialTemplate">
					<spring:message code="definicio.proces.tasca.llistat.columna.inicial"/>
						<script id="cellDefinicioProcesInicialTemplate" type="text/x-jsrender">
						{{if inicial }}
							<div style="text-align: center;">
								<spring:message code="comu.check"></spring:message>
							</div>
						{{/if}}
						</script>
					</th>
					<th data-col-name="campsCount" data-template="#cellFirmesTemplate" data-orderable="false" width="13%">
						<script id="cellFirmesTemplate" type="text/x-jsrender">
							<a href="${baseUrl}/tasca/{{:id}}/variable" data-maximized="true" data-toggle="modal" data-callback="callbackModalTasques()" class="btn btn-default"><spring:message code="definicio.proces.tasca.llistat.accio.variables"/>&nbsp;<span class="badge">{{:campsCount}}</span></a>
						</script>
					</th>
					<th data-col-name="documentsCount" data-template="#cellDocumentsTemplate" data-orderable="false" width="13%">
						<script id="cellDocumentsTemplate" type="text/x-jsrender">
							<a href="${baseUrl}/tasca/{{:id}}/document" data-toggle="modal" data-callback="callbackModalTasques()" class="btn btn-default"><spring:message code="definicio.proces.tasca.llistat.accio.documents"/>&nbsp;<span class="badge">{{:documentsCount}}</span></a>
						</script>
					</th>
					<th data-col-name="firmesCount" data-template="#cellSignaturesTemplate" data-orderable="false" width="13%">
						<script id="cellSignaturesTemplate" type="text/x-jsrender">
							<a href="${baseUrl}/tasca/{{:id}}/firma" data-toggle="modal" data-callback="callbackModalTasques()" class="btn btn-default"><spring:message code="definicio.proces.tasca.llistat.accio.signatures"/>&nbsp;<span class="badge">{{:firmesCount}}</span></a>
						</script>
					</th>
					<th data-col-name="id" data-template="#cellAccionsTascaTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsTascaTemplate" type="text/x-jsrender">
							{{if heretat }}
								<a class="btn btn-default" data-toggle="modal" data-callback="callbackModalTasques()" href="${baseUrl}/tasca/{{:id}}/update"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.visualitzar"/></a>
							{{else}}
								<a class="btn btn-default" data-toggle="modal" data-callback="callbackModalTasques()" href="${baseUrl}/tasca/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a>
							{{/if}}
					</script>
					</th>
					<th data-col-name="heretat" data-visible="false"/>
				</tr>
			</thead>
		</table>
		<script id="rowhrefTemplateTasques" type="text/x-jsrender">${baseUrl}/tasca/{{:id}}/update</script>	
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
// <![CDATA[            
// Llistat d'identificadors de definicions heretades
var definicionsHeretadesIds =  ${definicionsHeretadesIds};
//Llistat d'identificadors de definicions que sobreescriuen
var definicionsSobreescriuenIds =  ${definicionsSobreescriuenIds};
// Definició de procés inicial
var definicioInical = "${definicioInical}";

// Funció per donar format als itemps de la select de definicions depenent de la herència
function formatDefinicioSelectHerencia(item) {
	var res;
    if(item.id && definicionsHeretadesIds.indexOf(parseInt(item.id)) >= 0)
		res = item.text + " <span class='label label-primary'>R</span>";
	else if(item.id && definicionsSobreescriuenIds.indexOf(parseInt(item.id)) >= 0)
		res = item.text + " <span class='label label-warning'>S</span>";
	else 
		res = item.text;
    return res;
  }
   
$(document).ready(function() {

	// Posa el text "Totes" pel botó de selecció de totes les variables
	$('#expedientTipusTasca_length button[value="-1"]').text('<spring:message code="comu.totes"/>');
	
	// Canvi en la selecció de les definicions
	$('#definicions').change(function() {
		refrescaVersions();
	});
	// Quan se selecciona una versió de la definicó de procés
	$('#versions').change(function() {
		refrescaTaula();
	});

	// Afegeix format si l'item de la agrupació està heretat
	$('#definicions').select2({
        formatResult: formatDefinicioSelectHerencia,
        formatSelection: formatDefinicioSelectHerencia
    });
		
	// Quan es repinta la taula actualitza els enllaços
	$('#expedientTipusTasca').on('draw.dt', function() {
		// Refresca els missatges
		webutilRefreshMissatges();
  	});		
	
	// Carrega la definició inicial en la selecció
	if (definicioInical)
		$("#definicions").val(definicioInical).change();
});


function refrescaTaula() {
	var definicioProcesId = $("#versions").val();
	$('#expedientTipusTasca').webutilDatatable('refresh-url', '${baseUrl}/tasca/datatable?definicioProcesId='+definicioProcesId);		
}

function callbackModalTasques() {
	refrescaTaula();
}

function refrescaVersions() {
	var definicioProcesId = $("#definicions").val();
	var getUrl = '${baseUrl}/definicio/' + definicioProcesId + '/versions/select';
	$.ajax({
		type: 'GET',
		url: getUrl,
		async: true,
		success: function(data) {
			$("#versions option").each(function(){
			    $(this).remove();
			});
			$("#versions").append($("<option/>"));
			for (i = 0; i < data.length; i++) {
				$("#versions").append($("<option/>", {value: data[i].codi, text: data[i].valor}));
			}
			$("#versions").val(definicioProcesId).change();
		},
		error: function(e) {
			console.log("Error obtenint les versions de les definicions de procés per l' id " + definicioProcesId + ": " + e);
		}
	});
}	



// ]]>
</script>			
