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


<c:choose>
	<c:when test="${not empty expedientTipus}">

		<div class="botons-titol text-right">
			<a id="nou_camp" class="btn btn-default" href="${expedientTipus.id}/document/new" data-toggle="modal" data-datatable-id="expedientTipusDocument"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.camp.llistat.accio.nova"/></a>
		</div>
		<table	id="expedientTipusDocument"
				data-toggle="datatable"
				data-url="${expedientTipus.id}/document/datatable"
				data-paging-enabled="true"
				data-info-type="search"
				data-ordering="true"
				data-default-order="1"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="codi" width="20%"><spring:message code="expedient.tipus.document.llistat.columna.codi"/></th>
					<th data-col-name="nom"><spring:message code="expedient.tipus.document.llistat.columna.nom"/></th>
					<th data-col-name="plantilla" data-template="#plantillaTemplate">
						<spring:message code="expedient.tipus.document.llistat.columna.plantilla"/>
						<script id="plantillaTemplate" type="text/x-jsrender">
							{{if estat}}
								<span class="label label-success">SI</span>
							{{else}}
								<span class="label label-default">NO</span>
							{{/if}}
						</script>
					</th>
					<th data-col-name="arxiuNom"><spring:message code="expedient.tipus.document.llistat.columna.arxiu"/></th>
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
	
	// Quan es repinta la taula actualitza els enllaços
// 	$('#expedientTipusVariable').on('draw.dt', function() {
// 		// Botons per agrupar o desagrupar
// 		$(".ajax-link").click(function(e) {
// 			var getUrl = $(this).attr('href');
// 			$.ajax({
// 				type: 'GET',
// 				url: getUrl,
// 				async: true,
// 				success: function(result) {
// 					if (result) {
// 						refrescaTaula();
// 					}
// 				}
// 			});
// 			e.stopImmediatePropagation();
// 			return false;
// 		});
// 	});		
});

// function refrescaTaula() {
// 	var agrupacioId = $("#agrupacions").val();
// 	if (agrupacioId != "") {
// 		$('#expedientTipusVariable').webutilDatatable('refresh-url', '${expedientTipus.id}/variable/datatable?agrupacioId='+agrupacioId);		
// 	} else {
// 		$('#expedientTipusVariable').webutilDatatable('refresh-url', '${expedientTipus.id}/variable/datatable');
// 	}
// }
// ]]>
</script>			
