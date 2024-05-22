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

	<table	id="consultesPinbalDatatable"
			data-toggle="datatable"
			data-url="<c:url value="/v3/expedient/${expedientId}/pinbal/datatable"/>"
			data-paging-enabled="true"
			data-ordering="true"
			data-default-order="0"
			data-default-dir="desc"
			data-info-type="search"			
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>				
				<th data-col-name="dataPeticio" width="13%" data-converter="datetime"><spring:message code="consultes.pinbal.camp.dataPeticio"/></th>
				<th data-col-name="usuari" width="10%"><spring:message code="consultes.pinbal.camp.usuari"/></th>
				<th data-col-name="procediment" width="15%"><spring:message code="consultes.pinbal.camp.procediment"/></th>
				<th data-col-name="estat" data-template="#cellEstatTemplate" width="10%"><spring:message code="consultes.pinbal.camp.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat=='PENDENT'}}<span class="fa fa-clock-o" title="{{:dataPrevistaFormat}}"></span> Pendent{{/if}}
						{{if estat=='TRAMITADA'}}<span class="fa fa-check"></span> Tramitada{{/if}}
						{{if estat=='ERROR'}}<span class="fa fa-exclamation-triangle" title="{{:errorMsg}} {{:errorProcessament}}"></span> Error{{/if}}
						{{if estat=='ERROR_PROCESSANT'}}<span class="fa fa-exclamation-triangle" title="{{:errorMsg}} {{:errorProcessament}}"></span> Error processant{{/if}}
					</script>
				</th>
				<th data-col-name="document.nom" data-template="#cellDocTemplate" width="10%"><spring:message code="consultes.pinbal.camp.document"/>
					<script id="cellDocTemplate" type="text/x-jsrender">
						<a href="<c:url value="/v3/expedient/{{:expedientId}}/document/{{:documentId}}/descarregar"/>" target="_blank">{{:document.nom}}</a>
					</script>
				</th>				
				<th data-col-name="expedientId" data-visible="false"></th>
				<th data-col-name="documentId" data-visible="false"></th>
				<th data-col-name="dataPrevistaFormat" data-visible="false"></th>
				<th data-col-name="errorMsg" data-visible="false"></th>
				<th data-col-name="errorProcessament" data-visible="false"></th>
				<th data-col-name="id" width="5%" data-template="#cellAccionsTemplate" data-orderable="false">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown navbar-right">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/v3/consultesPinbal/{{:id}}/info"/>" data-toggle="modal" class="consultar-expedient"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="consultes.pinbal.boto.info"/></a></li>
								{{if estat=='PENDENT'}}<li><a href="<c:url value="/v3/consultesPinbal/{{:id}}/actualitzarEstat"/>" data-toggle="ajax"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="consultes.pinbal.boto.update"/></a></li>{{/if}}
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>

<script type="text/javascript">
// <![CDATA[			
$(document).ready(function() {});
//]]>
</script>

