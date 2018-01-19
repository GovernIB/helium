<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:set var="ambInfoPropiaText"><spring:message code="expedient.tipus.form.camp.ambInfoPropia"/></c:set>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<c:choose>
	<c:when test="${not empty expedientTipus && empty definicioProcesId && not empty expedientTipus || not empty definicioProcesId}">

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

		<table	id="expedientTipusTermini"
				data-toggle="datatable"
				data-url="${baseUrl}/termini/datatable"
				data-paging-enabled="true"
				data-info-type="search+button"
				data-ordering="true"
				data-default-order="1"
				data-rowhref-toggle="modal"
				data-rowhref-template="#rowhrefTemplateTerminis" 
				data-botons-template="#tableButtonsTerminiTemplate"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="codi" width="20%" data-template="#cellExpedientTipusTerminiCodiTemplate">
					<spring:message code="comuns.codi"/>
						<script id="cellExpedientTipusTerminiCodiTemplate" type="text/x-jsrender">
								{{if heretat }}
									<span class="dada-heretada">{{:codi}}</span> 
									<span class="label label-primary" title="<spring:message code="expedient.tipus.termini.llistat.codi.heretat"/>">R</span>
								{{else}}
									{{:codi}}
									{{if sobreescriu }}
										<span class="label label-warning" title="<spring:message code="expedient.tipus.termini.llistat.codi.sobreescriu"/>">S</span>
									{{/if}}
								{{/if}}
						</script>
					</th>
					<th data-col-name="nom"><spring:message code="comuns.nom"/></th>
					<th data-col-name="durada"><spring:message code="defproc.termllist.durada"/></th>
					<th data-col-name="id" data-template="#cellTerminiTemplate" data-orderable="false" width="10%">
						<script id="cellTerminiTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								{{if heretat}}
									<li><a data-toggle="modal" href="${baseUrl}/termini/{{:id}}/update"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.visualitzar"/></a></li>
								{{else}}
									<li><a data-toggle="modal" data-callback="callbackModalTerminis()" href="${baseUrl}/termini/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
									<li><a href="${baseUrl}/termini/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.camp.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
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

		<script id="rowhrefTemplateTerminis" type="text/x-jsrender">${baseUrl}/termini/{{:id}}/update</script>	

		<script id="tableButtonsTerminiTemplate" type="text/x-jsrender">
			<div class="botons-titol text-right">
				<a id="nou_camp" class="btn btn-default" href="${baseUrl}/termini/new" data-toggle="modal" data-callback="callbackModalTerminis()" data-datatable-id="expedientTipusTermini"><span class="fa fa-plus"></span>&nbsp;<spring:message code="defproc.termllist.nou_term"/></a>
			</div>
		</script>
		<script id="rowhrefTemplate" type="text/x-jsrender">${baseUrl}/termini/update/{{:id}}</script>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>
<script>
$(document).ready(function() {
	$('#expedientTipusTermini').on('draw.dt', function() {
		// Refresca els missatges
		webutilRefreshMissatges();		
	});
});

function callbackModalTerminis() {
	refrescaTaula();
}

function refrescaTaula() {
	$('#expedientTipusTermini').webutilDatatable('refresh');
}
</script>