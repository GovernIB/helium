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
			data-default-order="2"
			data-default-dir="desc"
			data-info-type="search"			
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="procediment" width="15%"><spring:message code="consultes.pinbal.camp.procediment"/></th>
				<th data-col-name="usuari" width="10%"><spring:message code="consultes.pinbal.camp.usuari"/></th>
				<th data-col-name="dataPeticio" width="13%" data-converter="datetime"><spring:message code="consultes.pinbal.camp.dataPeticio"/></th>
				<th data-col-name="asincrona" width="10%" data-converter="boolean"><spring:message code="consultes.pinbal.camp.asincrona"/></th>
				<th data-col-name="dataPrevista" width="13%" data-converter="datetime"><spring:message code="consultes.pinbal.camp.dataPrevista"/></th>
				<th data-col-name="estat" width="10%"><spring:message code="consultes.pinbal.camp.estat"/></th>
				<th data-col-name="errorMsg" width="24%"><spring:message code="consultes.pinbal.camp.error"/></th>
				<th data-col-name="expedientId" data-visible="false"></th>
				<th data-col-name="documentId" data-visible="false"></th>
				<th data-col-name="id" width="5%" data-template="#cellAccionsTemplate" data-orderable="false">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="<c:url value="/v3/expedient/{{:expedientId}}/document/{{:documentId}}/descarregar"/>" style="padding-left: 30%;">
							<span class="fa fa-file fa-2x no-doc" title="Descarregar justificant"></span>
						</a>
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

