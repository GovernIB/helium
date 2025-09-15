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

	<table	id="signatura"
			data-toggle="datatable"
			data-url="${expedientId}/signatura/datatable"
			data-paging-enabled="true"
			data-info-type="search"
			data-ordering="true"
			data-default-order="1"
			data-rowhref-template="#rowhrefTemplateSignatures"
			data-rowhref-toggle="modal" 
			data-rowhref-maximized="true"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name=documentId width="10%"><spring:message code="consultes.potafib.camp.documentId"/></th>
				<th data-col-name="dataEnviat" width="10%" data-converter="datetime"><spring:message code="consultes.potafib.camp.dataEnviat"/></th>
				<th data-col-name="estat" data-template="#cellEstatTemplate" width="10%"><spring:message code="consultes.pinbal.camp.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat=='BLOQUEJAT'}}<span class="fa fa-clock-o"></span> Bloquejat{{/if}}						
						{{if estat=='PENDENT'}}<span class="fa fa-clock-o"></span> Pendent{{/if}}
						{{if estat=='SIGNAT'}}<span class="fa fa-check"></span> Signat{{/if}}
						{{if estat=='REBUTJAT'}}<span class="fa fa-ban"></span> Rebutjat{{/if}}
						{{if estat=='PROCESSAT'}}<span class="fa fa-check"></span> Processat ({{:transicio}}){{/if}}
						{{if estat=='CANCELAT'}}<span class="fa fa-times"></span> Cancel·lat{{/if}}
						{{if estat=='ERROR'}}<span class="fa fa-exclamation-triangle" title="{{>errorProcessant}}"></span> Error ({{:transicio}}){{/if}}
						{{if estat=='ESBORRAT'}}<span class="fa fa-times"></span> Esborrat{{/if}}					
					</script>
				</th>
				<th data-col-name="documentNom" data-template="#cellDocTemplate" width="15%" data-orderable="false"><spring:message code="consultes.potafib.camp.nomDoc"/>
					<script id="cellDocTemplate" type="text/x-jsrender">
						<a href="<c:url value="/v3/expedient/{{:expedientId}}/document/{{:documentStoreId}}/descarregar"/>" target="_blank">{{:documentNom}}</a>
					</script>
				</th>					
				<th data-col-name="expedientId" data-visible="false"></th>
				<th data-col-name="tipusExpedientCodi" data-visible="false"></th>
				<th data-col-name="transicio" data-visible="false"></th>
				<th data-col-name="entornCodi" data-visible="false"></th>
				<th data-col-name="documentStoreId" data-visible="false"></th>
				<th data-col-name="errorProcessant" data-visible="false"></th>
				<th data-col-name="entornNom" data-visible="false"></th>
				<th data-col-name="id" width="5%" data-template="#cellAccionsTemplate" data-orderable="false">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown navbar-right">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/v3/enviamentsPortafib/{{:id}}/info"/>" data-toggle="modal" class="consultar-expedient"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="consultes.pinbal.boto.info"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="rowhrefTemplateSignatures" type="text/x-jsrender"><c:url value="/v3/anotacio/{{:id}}"/></script>

<script type="text/javascript">
// <![CDATA[			
$(document).ready(function() {				
});
//]]>
</script>

