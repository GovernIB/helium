<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>


<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>

<c:choose>
	<c:when test="${not empty expedientTipus}">

		<table	id="expedientTipusRecurs"
				data-rowId="id"
				data-toggle="datatable"
				data-url="${expedientTipus.id}/recurs/datatable"
				data-paging-enabled="true"
				data-info-type="search+button"
				data-ordering="true"
				data-default-order="1"
			  	data-botons-template="#tableButtonsRecursosTemplate"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="nom"><spring:message code="expedient.tipus.recurs.llistat.columna.nom"/></th>
					<th data-col-name="classe" data-template="#cellExpedientTipusRecursClasseTemplate">
						<spring:message code="expedient.tipus.recurs.llistat.columna.classe"/>
						<script id="cellExpedientTipusRecursClasseTemplate" type="text/x-jsrender">
							{{if classe}}
								<spring:message code="comu.check"></spring:message>
							{{/if}}
						</script>
					</th>
					<th data-col-name="handler" data-template="#cellExpedientTipusRecursHandlerTemplate">
						<spring:message code="expedient.tipus.recurs.llistat.columna.handler"/>
						<script id="cellExpedientTipusRecursHandlerTemplate" type="text/x-jsrender">
							{{if handler}}
								<spring:message code="comu.check"></spring:message>
							{{/if}}
						</script>
					</th>
					<th data-col-name="dataCreacio" data-converter="date"><spring:message code="expedient.tipus.recurs.llistat.columna.dataCreacio"/></th>
					<th data-col-name="id" data-template="#cellAccionsRecursTemplate" data-orderable="false"  width="50px">
						<script id="cellAccionsRecursTemplate" type="text/x-jsrender">
							<div class="dropdown navbar-right">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li>
										<a href='<c:url value="/expedientTipus${expedientTipus.id}/recurs/{{:id}}/descarregar"/>?nom={:nom}' >
											<span class="fa fa-download"></span>&nbsp;<spring:message code="definico.proces.recurs.descarregar"/>
										</a>
									</li>
									<li>
										<a 	href='<c:url value="/expedientTipus${expedientTipus.id}/recurs/{{:id}}/delete"/>'
											data-toggle="ajax"
											class="consultar-expedient"
											data-confirm="<spring:message code="expedient.tipus.recurs.llistat.accio.esborrar.confirmacio"/>">
											<span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.tipus.recurs.llistat.accio.esborrar"/></a>
									</li>
								</ul>
							</div>
						</script>
					</th>
				</tr>
			</thead>
		</table>

		<script id="tableButtonsRecursosTemplate" type="text/x-jsrender">
			<div class="botons-titol text-right">
				<a id="descarregar_par" class="btn btn-primary" href='<c:url value="/expedientTipus/${expedientTipus.id}/recurs/par"></c:url>' style="margin-left: 10px;"><span class="fa fa-arrow-down"></span>&nbsp;<spring:message code="definico.proces.recurs.descarregar.par"/></a>
				<a id="nou_recurs" class="btn btn-default" href="<c:url value="/modal/expedientTipus/${expedientTipus.id}/recursos/new"/>" data-toggle="modal" data-datatable-id="expedientTipusRecurs"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.recurs.llistat.accio.add"/></a>
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
	$('#expedientTipusRecurs').on('draw.dt', function() {
		// Refresca els missatges
		webutilRefreshMissatges();
	});
});

function callbackModalRecursos() {
	refrescaTaula();
}

function refrescaTaula() {
	$('#expedientTipusRecurs').webutilDatatable('refresh');
}
// ]]>
</script>
