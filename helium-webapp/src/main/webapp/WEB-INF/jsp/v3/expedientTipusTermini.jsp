<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<c:url var="urlDatatable" value="/v3/expedientTipus/${expedientTipus.id}/terminis/datatable"/>

<c:choose>
	<c:when test="${not empty expedientTipus}">

		<table	id="expedientTipusTermini"
				data-toggle="datatable"
				data-url="${urlDatatable}"
				data-paging-enabled="true"
				data-info-type="search+button"
				data-ordering="true"
				data-default-order="1"
				data-botons-template="#tableButtonsTerminiTemplate"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="codi" width="20%"><spring:message code="comuns.codi"/></th>
					<th data-col-name="nom"><spring:message code="comuns.nom"/></th>
					<th data-col-name="durada"><spring:message code="defproc.termllist.durada"/></th>
					<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a data-toggle="modal" data-callback="callbackModalAccions()" href="${expedientTipus.id}/termini/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
								<li><a href="${expedientTipus.id}/termini/{{:id}}/delete" class="ajax-link" data-confirm="<spring:message code="expedient.tipus.camp.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
							</ul>
						</div>
					</script>
					</th>
				</tr>
			</thead>
		</table>
		<script id="tableButtonsTerminiTemplate" type="text/x-jsrender">
			<div class="botons-titol text-right">
				<a id="nou_camp" class="btn btn-default" href="${expedientTipus.id}/termini/new" data-toggle="modal" data-callback="callbackModalAccions()" data-datatable-id="expedientTipusTermini"><span class="fa fa-plus"></span>&nbsp;<spring:message code="defproc.termllist.nou_term"/></a>
			</div>
		</script>
		<script id="rowhrefTemplate" type="text/x-jsrender">${expedientTipus.id}/termini/update/{{:id}}</script>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>
<script>
$(document).ready(function() {
	$('#expedientTipusTermini').on('draw.dt', function() {
		// Botons per agrupar o desagrupar
		$(".ajax-link").click(function(e) {
			var getUrl = $(this).attr('href');
			$.ajax({
				type: 'GET',
				url: getUrl,
				async: true,
				success: function(result) {
					if (result) {
						refrescaTaula();
					}
					webutilRefreshMissatges();
				},
				errlr: function(error) {
					webutilRefreshMissatges();
					console.log('Error:'+error);
				}
			});
			e.stopImmediatePropagation();
			return false;
		});
	});
});

function callbackModalAccions() {
	webutilRefreshMissatges();
	refrescaTaula();
}

function refrescaTaula() {
	$('#expedientTipusTermini').webutilDatatable('refresh');
}
</script>