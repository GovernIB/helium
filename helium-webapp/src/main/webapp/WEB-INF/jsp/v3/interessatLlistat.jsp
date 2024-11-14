<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>


	<table	id="interessat"
			data-toggle="datatable"
			data-url="${expedientId}/interessat/datatable"
			data-paging-enabled="true"
			data-info-type="search+button"
			data-ordering="true"
			data-default-order="0"
			data-botons-template="#tableButtonsInteressatTemplate"
			data-row-info="true"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="codi" width="10%"><spring:message code="interessat.llistat.columna.codi"/></th>
				<th data-col-name="tipusNom"><spring:message code="interessat.llistat.columna.tipus"/></th>
				<th data-col-name="documentIdent"><spring:message code="interessat.form.camp.document.identificatiu"/></th>
				<th data-col-name="fullNom" width="30%"><spring:message code="interessat.form.camp.nom.rao.social"/></th>
				<th data-col-name="dir3Codi"><spring:message code="interessat.llistat.columna.dir3Codi"/></th>
				<th data-col-name="email"><spring:message code="interessat.llistat.columna.email"/></th>
				<th data-col-name="telefon"><spring:message code="interessat.llistat.columna.telefon"/></th>
				<th data-col-name="representantFullNom"><spring:message code="expedient.document.notificar.form.camp.representant"/></th>
				<th data-col-name="existeixenRepresentantsExpedient" data-visible="false"></th>
				<th data-col-name="es_representant" data-visible="false"></th>
				<th data-col-name="teRepresentant" data-visible="false"></th>
				<th data-col-name="representant_id" data-visible="false"></th>
				<!--<th data-col-name="representant" data-visible="false"></th> -->
 				<th data-col-name="id" data-template="#cellInteressatAccionsTemplate" data-orderable="false" width="10%"> 
 					<script id="cellInteressatAccionsTemplate" type="text/x-jsrender"> 
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									{{if !es_representant}}
										<li><a href="${expedientId}/interessat/{{:id}}/update?{{:es_representant}}" data-ajax="true" data-callback="callbackModalInteressats()" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
										<li><a href="${expedientId}/interessat/{{:id}}/delete?{{:es_representant}}" data-ajax="true" data-callback="callbackModalInteressats()" data-confirm="<spring:message code="interessat.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
									{{/if}}
									{{if !es_representant && !teRepresentant}}
										<li class="divider" role="separator"></li>
										<li><a href="${expedientId}/interessat/{{:id}}/representant/new" data-ajax="true" data-callback="callbackModalInteressats()" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="interessat.llistat.accio.nou.representant"/></a></li>
										{{if existeixenRepresentantsExpedient}}
											<li><a href="${expedientId}/interessat/{{:id}}/representant/search" data-ajax="true" data-callback="callbackModalInteressats()" data-toggle="modal"><span class="fa fa-magnifying-glass"></span>&nbsp;<spring:message code="interessat.llistat.accio.cercar.representant"/></a></li>
										{{/if}}
									{{/if}}
									{{if !es_representant && teRepresentant}}
										<li class="divider" role="separator"></li>
										<li><a href="${expedientId}/interessat/{{:representant_id}}/update?es_representant=true" data-ajax="true" data-callback="callbackModalInteressats()" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="interessat.llistat.accio.modificar.representant"/></a></li>
										<li><a href="${expedientId}/interessat/{{:representant_id}}/deleteRepresentant?interessatId={{:id}}" data-ajax="true" data-callback="callbackModalInteressats()" data-confirm="<spring:message code="interessat.llistat.confirmacio.esborrar.representant"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="interessat.llistat.accio.esborrar.representant"/></a></li>																		
									{{/if}}
									</ul>
							</div>
					</script>
 				</th>
			</tr>
		</thead>
	</table>

	<script id="tableButtonsInteressatTemplate" type="text/x-jsrender">
		<div class="botons-titol text-right">
			<a id="nou_interessat" class="btn btn-default" href="${expedientId}/interessat/new" data-toggle="modal" data-ajax="true" data-callback="callbackModalInteressats()"><span class="fa fa-plus"></span>&nbsp;<spring:message code="interessat.llistat.accio.nou"/></a>
		</div>
	</script>

<script type="text/javascript">
// <![CDATA[			
$(document).ready(function() {	
	$(".desplegable").click(function(){
		alert("entra");
		$(this).find("span").toggleClass("fa-caret-up");
		$(this).find("span").toggleClass("fa-caret-down");
	});
	$('.desplegable').click(function(){
		alert("entra");
		$(this).find("span").toggleClass("fa-caret-up");
		$(this).find("span").toggleClass("fa-caret-down");
	});
	
	$('#interessat').on('rowinfo.dataTable', function (e, td, rowData) {
		$(td).addClass('text-center');
		$(td).append('<span class="fa fa-spinner fa-spin"/>');
		var getUrl = "<c:url value="/nodeco/v3/expedient/${expedientId}/interessat/"/>" + rowData.id + "/detall";
		$.get(getUrl).done(function (data) {
			$(td).removeClass('text-center');
			$(td).empty();
			$(td).append(data);
		});
	});
});

function refrescaTaulaInteressats() {
	$('#interessat').webutilDatatable('refresh-url', '${expedientId}/interessat/datatable');
}


function callbackModalInteressats() {
	webutilRefreshMissatges();
	refrescaTaulaInteressats();
}

//]]>
</script>

