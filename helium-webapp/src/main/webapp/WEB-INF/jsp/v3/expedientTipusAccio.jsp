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

		<table	id="expedientTipusAccio"
				data-rowId="id"
				data-toggle="datatable"
				data-url="${baseUrl}/accio/datatable"
				data-paging-enabled="true"
				data-info-type="search+button"
				data-ordering="true"
				data-default-order="1"
				data-rowhref-toggle="modal"
				data-rowhref-template="#rowhrefTemplateAccions" 
				data-botons-template="#tableButtonsAccionsTemplate"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="codi" width="20%" data-template="#cellExpedientTipusAccioCodiTemplate">
					<spring:message code="expedient.tipus.accio.llistat.columna.codi"/>
						<script id="cellExpedientTipusAccioCodiTemplate" type="text/x-jsrender">
								{{if heretat }}
									<span class="dada-heretada">{{:codi}}</span> 
									<span class="label label-primary" title="<spring:message code="expedient.tipus.accio.llistat.codi.heretat"/>">R</span>
								{{else}}
									{{:codi}}
									{{if sobreescriu }}
										<span class="label label-warning" title="<spring:message code="expedient.tipus.accio.llistat.codi.sobreescriu"/>">S</span>
									{{/if}}
								{{/if}}
						</script>
					</th>
					<th data-col-name="nom"><spring:message code="expedient.tipus.accio.llistat.columna.nom"/></th>
					<c:if test="${not empty expedientTipus}">
						<!-- Per les accions dels tipus d'expedient es mostra la clau de la definició de procés -->
						<th data-col-name="defprocJbpmKey"><spring:message code="expedient.tipus.accio.llistat.columna.defprocJbpmKey"/></th>
					</c:if>
					<th data-col-name="jbpmAction"><spring:message code="expedient.tipus.accio.llistat.columna.jbpmAction"/></th>
					<th data-col-name="id" data-template="#cellAccionsAccioTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsAccioTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								{{if heretat}}
									<li><a data-toggle="modal" href="${baseUrl}/accio/{{:id}}/update"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.visualitzar"/></a></li>
								{{else}}
									<li><a data-toggle="modal" data-callback="callbackModalAccions()" href="${baseUrl}/accio/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
									<li><a href="${baseUrl}/accio/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.accio.llistat.accio.esborrar.confirmacio"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
								{{/if}}
							</ul>
						</div>
					</script>
					</th>
					<th data-col-name="sobreescriu" data-visible="false"/>
					<th data-col-name="heretat" data-visible="false"/>
				</tr>
			</thead>
		</table>

		<script id="rowhrefTemplateAccions" type="text/x-jsrender">${baseUrl}/accio/{{:id}}/update</script>	

		<script id="tableButtonsAccionsTemplate" type="text/x-jsrender">
			<div class="botons-titol text-right">
				<a id="nova_accio" class="btn btn-default" href="${baseUrl}/accio/new" data-toggle="modal" data-callback="callbackModalAccions()" data-datatable-id="expedientTipusAccio"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.accio.llistat.accio.nova"/></a>
			</div>
		</script>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
// <![CDATA[            
$(document).ready(function() {
	$('#expedientTipusAccio').on('draw.dt', function() {
		// Refresca els missatges
		webutilRefreshMissatges();		
	});
});

function callbackModalAccions() {
	refrescaTaula();
}

function refrescaTaula() {
	$('#expedientTipusAccio').webutilDatatable('refresh');
}
// ]]>
</script>			
