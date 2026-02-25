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

		<table	id="expedientTipusRedireccio"
				data-rowId="id"
				data-toggle="datatable"
				data-url="${expedientTipus.id}/redireccio/datatable"
				data-paging-enabled="true"
				data-info-type="search+button"
				data-ordering="true"
				data-default-order="1"
				data-rowhref-toggle="modal"
				data-rowhref-template="#rowhrefTemplateRedireccions" 
				data-botons-template="#tableButtonsRedireccionsTemplate"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="usuariOrigen"><spring:message code="expedient.tipus.redireccio.llistat.columna.usuariOrigen"/></th>
					<th data-col-name="usuariDesti"><spring:message code="expedient.tipus.redireccio.llistat.columna.usuariDesti"/></th>
					<th data-col-name="dataInici" data-converter="date"><spring:message code="expedient.tipus.redireccio.llistat.columna.dataInici"/></th>
					<th data-col-name="dataFi" data-converter="date"><spring:message code="expedient.tipus.redireccio.llistat.columna.dataFi"/></th>
					<th data-col-name="id" data-template="#cellAccionsRedireccioTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsRedireccioTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a data-toggle="modal" data-callback="callbackModalRedireccions()" href="${expedientTipus.id}/redireccio/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
								<li><a href="${expedientTipus.id}/redireccio/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.redireccio.llistat.accio.esborrar.confirmacio"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.tipus.redireccio.llistat.accio.esborrar"/></a></li>
							</ul>
						</div>
					</script>
					</th>
				</tr>
			</thead>
		</table>
		<script id="rowhrefTemplateRedireccions" type="text/x-jsrender">${expedientTipus.id}/redireccio/{{:id}}/update</script>
		<script id="tableButtonsRedireccionsTemplate" type="text/x-jsrender">
			<div class="botons-titol text-right">
				<a id="nova_redireccio" class="btn btn-default" href="${expedientTipus.id}/redireccio/new" data-toggle="modal" data-callback="callbackModalRedireccions()" data-datatable-id="expedientTipusRedireccio"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.redireccio.llistat.accio.nova"/></a>
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
	$('#expedientTipusRedireccio').on('draw.dt', function() {
		// Refresca els missatges
		webutilRefreshMissatges();		
	});
});

function callbackModalRedireccions() {
	refrescaTaula();
}

function refrescaTaula() {
	$('#expedientTipusRedireccio').webutilDatatable('refresh');
}
// ]]>
</script>			
