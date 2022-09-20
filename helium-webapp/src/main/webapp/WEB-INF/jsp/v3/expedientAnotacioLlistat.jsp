<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>


	<table	id="anotacio"
			data-toggle="datatable"
			data-url="${expedientId}/anotacio/datatable"
			data-paging-enabled="true"
			data-info-type="search"
			data-ordering="true"
			data-default-order="1"
			data-rowhref-template="#rowhrefTemplateAnotacions"
			data-rowhref-toggle="modal" 
			data-rowhref-maximized="true"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="errorAnnexos" data-visible="false"></th>
				<th data-col-name="data" data-converter="datetime"><spring:message code="expedient.anotacio.llistat.columna.data"/></th>
				<th data-col-name="identificador" data-template="#cellAnotacioIdentificadorTemplate" width="20%">
					<spring:message code="expedient.anotacio.llistat.columna.numero"/>
 					<script id="cellAnotacioIdentificadorTemplate" type="text/x-jsrender"> 
						{{:identificador}}
						{{if errorAnnexos}}
							<div class="pull-right">
								<span class="fa fa-exclamation-triangle text-danger" 
								title="<spring:message code="expedient.anotacio.llistat.error.annexos"/>"></span>
							</div>
						{{/if}}
						{{if annexosInvalids}}
							<div class="pull-right">
								<span class="fa fa-exclamation-triangle text-danger" 
								title="<spring:message code="expedient.anotacio.llistat.annexos.invalids"/>"></span>
							</div>
						{{/if}}
						{{if annexosEsborranys}}
							<div class="pull-right">
								<span class="fa fa-exclamation-triangle text-warning"
								title="<spring:message code="expedient.anotacio.llistat.annexos.esborranys"/>"></span>
							</div>
						{{/if}}
					</script>
				</th>
				<th data-col-name="extracte"><spring:message code="expedient.anotacio.llistat.columna.extracte"/></th>
 				<th data-col-name="id" data-template="#cellAnotacioAccionsTemplate" data-orderable="false" width="10%"> 
 					<script id="cellAnotacioAccionsTemplate" type="text/x-jsrender"> 
						<a href="<c:url value="/v3/anotacio/{{:id}}"/>" class="btn btn-default" data-toggle="modal" data-maximized="true"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="comu.boto.detalls"/></a>
					</script>
 				</th> 
				<th data-col-name="annexosInvalids" data-visible="false"></th>
				<th data-col-name="annexosEsborranys" data-visible="false"></th>
			</tr>
		</thead>
	</table>
	<script id="rowhrefTemplateAnotacions" type="text/x-jsrender"><c:url value="/v3/anotacio/{{:id}}"/></script>

<script type="text/javascript">
// <![CDATA[			
$(document).ready(function() {				
});
//]]>
</script>
