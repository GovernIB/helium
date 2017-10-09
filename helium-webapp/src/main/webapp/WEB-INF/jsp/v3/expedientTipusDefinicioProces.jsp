<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="potDissenyarExpedientTipusAdmin" value="${potAdministrarEntorn 
														or potDissenyarEntorn 
														or expedientTipus.permisAdministration 
														or expedientTipus.permisDesignAdmin}"/>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>

<c:choose>
	<c:when test="${not empty expedientTipus}">

		<div class="botons-titol text-right">
		</div>
		<table	id="expedientTipusDefinicioProces"
				data-rowId="id"
				data-toggle="datatable"
				data-url="${expedientTipus.id}/definicionsProces/datatable"
				data-paging-enabled="true"
				data-info-type="search+button"
				data-ordering="true"
				data-botons-template="#tableExpedientTipusDefinicioProcesButtonsTemplate"
				data-default-order="1"
				data-rowhref-template="#rowhrefTemplate"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="jbpmKey"><spring:message code="expedient.tipus.definicioProces.llistat.columna.nom"/></th>
					<th data-col-name="versioCount" data-orderable="false"><spring:message code="expedient.tipus.definicioProces.llistat.columna.versions"/></th>
					<th data-col-name="dataCreacio" data-converter="datetime"><spring:message code="expedient.tipus.definicioProces.llistat.columna.dataDarreraVersio"/></th>
					<th data-col-name="versio" data-orderable="true"><spring:message code="definicio.proces.llistat.columna.versio"/></th>
					<th data-col-name="jbpmKey"><spring:message code="expedient.tipus.definicioProces.llistat.columna.inicial"/></th>
					<th data-col-name="jbpmKey" data-template="#cellexpedientTipusDefinicioProcesInicialTemplate">
					<spring:message code="expedient.tipus.definicioProces.llistat.columna.inicial"/>
						<script id="cellexpedientTipusDefinicioProcesInicialTemplate" type="text/x-jsrender">
						{{if ~inicial(jbpmKey) }}
							<spring:message code="comu.check"></spring:message>
						{{/if}}
						</script>
					</th>
					<th data-col-name="expedientTipus.id" data-template="#cellexpedientTipusDefinicioProcesGlobalTemplate">
					<spring:message code="expedient.tipus.definicioProces.llistat.columna.global"/>
						<script id="cellexpedientTipusDefinicioProcesGlobalTemplate" type="text/x-jsrender">
						{{if expedientTipus == null }}
							<spring:message code="comu.check"></spring:message>
						{{/if}}
						</script>
					</th>
					<th data-template="#cellexpedientTipusDefinicioProcesAccionsTemplate" data-orderable="false" width="10%">
						<script id="cellexpedientTipusDefinicioProcesAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="../definicioProces/{{:jbpmKey}}" class="consultar-expedient"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.tipus.definicioProces.llistat.definicioProces.dissenyar"/></a></li>
								{{if ${potDissenyarExpedientTipusAdmin} }}
									<li><a class="btn-inicial" data-jbpmkey="{{:jbpmKey}}" href="${expedientTipus.id}/definicionsProces/{{:id}}/inicial"><span class="fa fa-flag-checkered"></span>&nbsp;<spring:message code="expedient.tipus.definicioProces.llistat.definicioProces.inicial"/></a></li>
									<li><a data-toggle="modal" data-callback="callbackModaldefinicionsProces()" href="${expedientTipus.id}/definicionsProces/{{:id}}/incorporar"><span class="fa fa-download"></span>&nbsp;<spring:message code="expedient.tipus.definicioProces.llistat.definicioProces.incorporar"/></a></li>
									<li><a data-toggle="modal" href="../definicioProces/{{:jbpmKey}}/exportar"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.filtre.exportar"/></a></li>
									<li><a data-toggle="modal" href="../definicioProces/importar?definicioProcesId={{:id}}"><span class="fa fa-sign-in"></span>&nbsp;<spring:message code="comu.importar"/></a></li>
									<li><a class="btn-delete" href="../definicioProces/{{:jbpmKey}}/delete" data-confirm="<spring:message code="expedient.tipus.definicioProces.llistat.definicioProces.esborrar.confirmacio"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.tipus.definicioProces.llistat.definicioProces.esborrar"/></a></li>
								{{/if}}
							</ul>
						</div>
						</script>
					</th>
				</tr>
			</thead>
		</table>
		<script id="rowhrefTemplate" type="text/x-jsrender">../definicioProces/{{:jbpmKey}}</script>
		<script id="tableExpedientTipusDefinicioProcesButtonsTemplate" type="text/x-jsrender">
			{{if ${potDissenyarExpedientTipusAdmin} }}
				<div class="botons-titol text-right">
					<a class="btn btn-default" data-toggle="modal" data-callback="callbackModaldefinicionsProces()" data-datatable-id="expedientTipusDefinicioProces" href="../definicioProces/importar?expedientTipusId=${expedientTipus.id}">
						<span class="fa fa-sign-in"></span> <spring:message code="comu.importar"/></a>
					<a class="btn btn-default" href="../definicioProces/desplegar?expedientTipusId=${expedientTipus.id}" data-toggle="modal" data-callback="callbackModaldefinicionsProces()">
						<span class="fa fa-download"></span>&nbsp;<spring:message code="comu.filtre.desplegar"/></a>
				</div>
			{{/if}}
		</script>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
// <![CDATA[            
// Valor jbpmKey de la DP inicial del TE
var jbpmProcessDefinitionKey = "${expedientTipus.jbpmProcessDefinitionKey}";

// Funció per al template de la columna inicial per comparar amb la variable javascript anterior
function inicialFunction(jbpmKey) {
	return jbpmProcessDefinitionKey == jbpmKey;
}

$(document).ready(function() {	
	
	// Afegeix la funció javascript pel template de la columna inicial
	var templateHelpers = {inicial: inicialFunction};
	$.views.helpers(templateHelpers);
		
	// Events quan la taula es pinta
	$('#expedientTipusDefinicioProces').on('draw.dt', function() {
		// Botó per marcar com a inicial una definicó de procés
		$("#expedientTipusDefinicioProces a.btn-inicial").click(function(e) {
			var getUrl = $(this).attr('href');
			var jbpmKey = $(this).data('jbpmkey');
			$.ajax({
				type: 'GET',
				url: getUrl,
				async: true,
				success: function(result) {
					if (result) {
						jbpmProcessDefinitionKey = jbpmKey;
						refrescaTaula();
					}
				},
				error: function(error) {
					console.log('Error:'+error);
				},
				complete: function() {
					webutilRefreshMissatges();
				}
			});
			e.stopImmediatePropagation();
			return false;
		});
		// Botó per incorporar la informació de la definició de procés
		$("#expedientTipusDefinicioProces a.btn-incorporar").click(function(e) {
			var $a = $(this);
			var getUrl = $a.attr('href');
			$.ajax({
				type: 'GET',
				url: getUrl,
				async: true,
				error: function(error) {
					console.log('Error:'+error);
				},
				complete: function() {
					webutilRefreshMissatges();
					$a.closest('div .dropdown').removeClass('open');
				}
			});
			e.stopImmediatePropagation();
			return false;
		});
		// Botó per esborrar la darrera versió de la definició de procés amb un timeout de 20 segons
		// <li><a class="btn-delete" href="../definicioProces/{{:jbpmKey}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.definicioProces.llistat.definicioProces.esborrar.confirmacio"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.tipus.definicioProces.llistat.definicioProces.esborrar"/></a></li>
		$("#expedientTipusDefinicioProces a.btn-delete").click(function(e) {
			var getUrl = $(this).attr('href');
			$.ajax({
				type: 'GET',
				url: getUrl,
				async: true,
				timeout: 60000,
				success: function(result) {
					if (result) {
						refrescaTaula();
					}
				},
				error: function(error) {
					console.log('Error:'+error);
				},
				complete: function() {
					webutilRefreshMissatges();
				}
			});
			e.stopImmediatePropagation();
			return false;
		});
	});
});

function callbackModaldefinicionsProces() {
	webutilRefreshMissatges();
	refrescaTaula();
}

function refrescaTaula() {
	$('#expedientTipusDefinicioProces').webutilDatatable('refresh');
}

// ]]>
</script>			
