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

		<div class="botons-titol text-right">
			<a id="nova_accio" class="btn btn-default" href="${expedientTipus.id}/accio/new" data-toggle="modal" data-callback="callbackModalAccions()" data-datatable-id="expedientTipusAccio"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.accio.llistat.accio.nova"/></a>
		</div>
		<table	id="expedientTipusAccio"
				data-rowId="id"
				data-toggle="datatable"
				data-url="${expedientTipus.id}/accio/datatable"
				data-paging-enabled="true"
				data-info-type="search"
				data-ordering="true"
				data-default-order="3"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="codi" width="20%"><spring:message code="expedient.tipus.accio.llistat.columna.codi"/></th>
					<th data-col-name="nom"><spring:message code="expedient.tipus.accio.llistat.columna.nom"/></th>
					<th data-col-name="jbpmAction"><spring:message code="expedient.tipus.accio.llistat.columna.jbpmAction"/></th>
					<th data-col-name="id" data-template="#cellAccionsAccioTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsAccioTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a data-toggle="modal" data-callback="callbackModalAccions()" href="${expedientTipus.id}/accio/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
								<li><a href="${expedientTipus.id}/accio/{{:id}}/delete" class="ajax-link" data-confirm="<spring:message code="expedient.tipus.accio.llistat.accio.esborrar.confirmacio"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
							</ul>
						</div>
					</script>
					</th>
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

	$('#expedientTipusAccio').on('draw.dt', function() {
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
	$('#expedientTipusAccio').webutilDatatable('refresh');
}
// ]]>
</script>			
