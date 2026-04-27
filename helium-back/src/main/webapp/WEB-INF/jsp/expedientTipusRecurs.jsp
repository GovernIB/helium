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
				data-rowhref-toggle="modal"
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
				</tr>
			</thead>
		</table>
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

function callbackModalRedireccions() {
	refrescaTaula();
}

function refrescaTaula() {
	$('#expedientTipusRedireccio').webutilDatatable('refresh');
}
// ]]>
</script>
