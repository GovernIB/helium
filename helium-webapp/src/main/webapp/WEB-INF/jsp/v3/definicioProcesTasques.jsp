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
	<c:when test="${not empty definicioProcesId}">		
	
		<table	id="definicioProcesTasques"
				data-rowId="id"
				data-toggle="datatable"
				data-url="${jbpmKey}/${definicioProcesId}/tasca/datatable"
				data-paging-enabled="true"
				data-info-type="search"
				data-ordering="true"
				data-default-order="1"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="jbpmName" width="20%"><spring:message code="definicio.proces.tasca.llistat.columna.codi"/></th>
					<th data-col-name="nom"><spring:message code="definicio.proces.tasca.llistat.columna.titol"/></th>
					<th data-col-name="campsCount" data-template="#cellFirmesTemplate" data-orderable="false" width="13%">
						<script id="cellFirmesTemplate" type="text/x-jsrender">
							<a href="${jbpmKey}/${definicioProcesId}/tasca/{{:id}}/variable" data-toggle="modal" data-callback="callbackModalTasques()" class="btn btn-default"><spring:message code="definicio.proces.tasca.llistat.accio.variables"/>&nbsp;<span class="badge">{{:campsCount}}</span></a>
						</script>
					</th>
					<th data-col-name="documentsCount" data-template="#cellDocumentsTemplate" data-orderable="false" width="13%">
						<script id="cellDocumentsTemplate" type="text/x-jsrender">
							<a href="${jbpmKey}/${definicioProcesId}/tasca/{{:id}}/document" data-toggle="modal" data-callback="callbackModalTasques()" class="btn btn-default"><spring:message code="definicio.proces.tasca.llistat.accio.documents"/>&nbsp;<span class="badge">{{:documentsCount}}</span></a>
						</script>
					</th>
					<th data-col-name="firmesCount" data-template="#cellSignaturesTemplate" data-orderable="false" width="13%">
						<script id="cellSignaturesTemplate" type="text/x-jsrender">
							<a href="${jbpmKey}/${definicioProcesId}/tasca/{{:id}}/firma" data-toggle="modal" data-callback="callbackModalTasques()" class="btn btn-default"><spring:message code="definicio.proces.tasca.llistat.accio.signatures"/>&nbsp;<span class="badge">{{:firmesCount}}</span></a>
						</script>
					</th>
					<th data-col-name="id" data-template="#cellAccionsTascaTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsTascaTemplate" type="text/x-jsrender">
							<a class="btn btn-default" data-toggle="modal" data-callback="callbackModalTasques()" href="${jbpmKey}/${definicioProcesId}/tasca/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a>
						</script>
					</th>
				</tr>
			</thead>
		</table>
		<script id="rowhrefTemplate" type="text/x-jsrender">${jbpmKey}/${definicioProcesId}/tasca/{{:id}}/update</script>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='definicio.proces.detall.cap'/></div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
// <![CDATA[            
$(document).ready(function() {
});

function callbackModalTasques() {
	webutilRefreshMissatges();
	refrescaTaula();
}

function refrescaTaula() {
	$('#definicioProcesTasques').webutilDatatable('refresh');
}

// ]]>
</script>			
