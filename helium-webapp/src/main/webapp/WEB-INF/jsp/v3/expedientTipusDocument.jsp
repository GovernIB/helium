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

		<table	id="expedientTipusDocument"
				data-toggle="datatable"
				data-url="${expedientTipus.id}/document/datatable"
				data-paging-enabled="true"
				data-info-type="search+button"
				data-ordering="true"
				data-default-order="1"
				data-botons-template="#tableButtonsDocumentsTemplate"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="codi" width="20%"><spring:message code="expedient.tipus.document.llistat.columna.codi"/></th>
					<th data-col-name="nom"><spring:message code="expedient.tipus.document.llistat.columna.nom"/></th>
					<th data-col-name="arxiuNom" data-template="#arxiuTemplate">
					<spring:message code="expedient.tipus.document.llistat.columna.arxiu"/>
						<script id="arxiuTemplate" type="text/x-jsrender">
							{{if arxiuNom != null }}
								<span class="label label-success">SI</span>
								{{if plantilla }}
									<span class="label label-info" title="plantilla">P</span>
								{{/if}}
							{{/if}}
						</script>
					</th>
					<th data-col-name="id" data-template="#cellAccionsDocTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsDocTemplate" type="text/x-jsrender">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									{{if arxiuNom != null }}
										<li><a href="${expedientTipus.id}/document/{{:id}}/download" ><span class="fa fa-file"></span>&nbsp;Descarregar</a></li>
									{{/if}}
									<li><a data-toggle="modal" data-callback="callbackModalDocuments()" href="${expedientTipus.id}/document/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
									<li><a href="${expedientTipus.id}/document/{{:id}}/delete" class="ajax-link" data-confirm="<spring:message code="expedient.tipus.document.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
								</ul>
							</div>
						</script>
					</th>
					<th data-col-name="plantilla" data-visible="false"/>
				</tr>
			</thead>
		</table>
		<script id="tableButtonsDocumentsTemplate" type="text/x-jsrender">
			<div class="botons-titol text-right">
				<a id="nou_document" class="btn btn-default" href="${expedientTipus.id}/document/new" data-toggle="modal" data-callback="callbackModalDocuments()()" data-datatable-id="expedientTipusDocument"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.document.llistat.accio.nova"/></a>
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

	$('#expedientTipusDocument').on('draw.dt', function() {
		// Botons per agrupar o desagrupar
		$(".ajax-link").click(function(e) {
			var getUrl = $(this).attr('href');
			$.ajax({
				type: 'GET',
				url: getUrl,
				async: true,
				success: function(result) {
					if (result) {
						refrescaTaulaDocuments();
					}
					webutilRefreshMissatges();
				},
				error: function(error) {
					webutilRefreshMissatges();
					console.log('Error:'+error);
				}
			});
			e.stopImmediatePropagation();
			return false;
		});
	});
});

function refrescaTaulaDocuments() {
	$('#expedientTipusDocument').webutilDatatable('refresh-url', '${expedientTipus.id}/document/datatable');
}

function callbackModalDocuments() {
	webutilRefreshMissatges();
	refrescaTaulaDocuments();
}

// ]]>
</script>			
