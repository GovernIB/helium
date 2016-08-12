<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="consulta.llistat.titol"/></title>
	<meta name="title" content="<spring:message code='consulta.llistat.titol'/>"/>
	<meta name="screen" content="consultes">
	<meta name="title-icon-class" content="fa fa-cube"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>
</head>
<body>
	<table	id="consulta"
			data-toggle="datatable"
			data-url="consulta/datatable"
			data-paging-enabled="true"
			data-info-type="search"
			data-ordering="true"
			data-default-order="1"
			data-rowhref-template="#rowhrefTemplate"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"/>
				<th data-col-name="expedientTipus.id" data-visible="false"/>
				<th data-col-name="codi" width="20%"><spring:message code="consulta.llistat.columna.codi"/></th>
				<th data-col-name="nom"><spring:message code="consulta.llistat.columna.titol"/></th>
				<th data-col-name="expedientTipus.nom"><spring:message code="consulta.llistat.columna.expedientTipusNom"/></th>
				<th data-col-name="varsFiltreCount" data-template="#cellVarsFiltreTemplate" data-orderable="false" width="13%">
					<script id="cellVarsFiltreTemplate" type="text/x-jsrender">
							<a href="../v3/expedientTipus/{{:expedientTipus.id}}/consulta/{{:id}}/varFiltre" data-toggle="modal" data-callback="callbackModalConsultes()" class="btn btn-default"><spring:message code="expedient.tipus.consulta.llistat.accio.variables.filtre"/>&nbsp;<span class="badge">{{:varsFiltreCount}}</span></a>
						</script>
				</th>
				<th data-col-name="varsInformeCount" data-template="#cellVarsInformeTemplate" data-orderable="false" width="13%">
					<script id="cellVarsInformeTemplate" type="text/x-jsrender">
							<a href="../v3/expedientTipus/{{:expedientTipus.id}}/consulta/{{:id}}/varInforme" data-toggle="modal" data-callback="callbackModalConsultes()" class="btn btn-default"><spring:message code="expedient.tipus.consulta.llistat.accio.variables.informe"/>&nbsp;<span class="badge">{{:varsInformeCount}}</span></a>
						</script>
				</th>
				<th data-col-name="parametresCount" data-template="#cellParametresTemplate" data-orderable="false" width="13%">
					<script id="cellParametresTemplate" type="text/x-jsrender">
							<a href="../v3/expedientTipus/{{:expedientTipus.id}}/consulta/{{:id}}/parametre" data-toggle="modal" data-callback="callbackModalConsultes()" class="btn btn-default"><spring:message code="expedient.tipus.consulta.llistat.accio.parametres"/>&nbsp;<span class="badge">{{:parametresCount}}</span></a>
					</script>
				</th>
				<th data-col-name="ocultarActiu" data-template="#cellExpedientTipusConsultaActiuTemplate">
				<spring:message code="expedient.tipus.consulta.llistat.columna.actiu"/>
					<script id="cellExpedientTipusConsultaActiuTemplate" type="text/x-jsrender">
						{{if ocultarActiu }}
							<spring:message code="comu.false"></spring:message>
						{{else}}
							<spring:message code="comu.true"></spring:message>
						{{/if}}
						</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsConsultaTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsConsultaTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a data-toggle="modal" data-callback="callbackModalConsultes()" href="../v3/expedientTipus/{{:expedientTipus.id}}/consulta/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
								<li><a href="expedientTipus/{{:expedientTipus.id}}/consulta/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.consulta.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>

			</tr>
		</thead>
	</table>
	<script id="rowhrefTemplate" type="text/x-jsrender">consulta/{{:id}}</script>

<script type="text/javascript">
// <![CDATA[            

function callbackModalConsultes() {
	webutilRefreshMissatges();
	refrescaTaula();
}

function refrescaTaula() {
	$('#expedientTipusRedireccio').webutilDatatable('refresh');
}            

// ]]>
</script>			
	
</body>
</html>
