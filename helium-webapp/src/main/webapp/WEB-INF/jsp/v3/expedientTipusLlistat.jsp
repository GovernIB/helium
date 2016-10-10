<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="expedient.tipus.llistat.titol"/></title>
	<meta name="title" content="<spring:message code='expedient.tipus.llistat.titol'/>"/>
	<meta name="screen" content="expedients tipus">
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
	<div class="text-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="expedientTipus/importar" data-toggle="modal"><span class="fa fa-sign-in"></span>&nbsp;<spring:message code="comu.filtre.importar"/></a>
		<a class="btn btn-default" href="expedientTipus/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.llistat.accio.nou"/></a>
	</div>
	<table	id="expedientTipus"
			data-toggle="datatable"
			data-url="expedientTipus/datatable"
			data-paging-enabled="true"
			data-info-type="search"
			data-ordering="true"
			data-default-order="1"
			data-rowhref-template="#rowhrefTemplate"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"/>
				<th data-col-name="codi" width="20%"><spring:message code="expedient.tipus.llistat.columna.codi"/></th>
				<th data-col-name="nom"><spring:message code="expedient.tipus.llistat.columna.titol"/></th>
				<th data-col-name="permisCount" data-template="#cellPermisosTemplate" data-orderable="false" width="13%">
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						<a href="expedientTipus/{{:id}}/permis" data-toggle="" class="btn btn-default"><span class="fa fa-key"></span>&nbsp;<spring:message code="expedient.tipus.llistat.accio.permisos"/>&nbsp;<span class="badge">{{:permisCount}}</span></a>
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu pull-right">
								<li><a href="expedientTipus/{{:id}}" class="consultar-expedient"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.tipus.llistat.accio.dissenyar"/></a></li>
								<li><a data-toggle="modal" href="expedientTipus/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
								<li><a data-toggle="modal" href="expedientTipus/{{:id}}/exportar"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.filtre.exportar"/></a></li>
								<li><a data-toggle="modal" href="expedientTipus/importar?expedientTipusId={{:id}}"><span class="fa fa-sign-in"></span>&nbsp;<spring:message code="comu.filtre.importar"/></a></li>
								<li><a href="expedientTipus/{{:id}}/delete" data-rdt-link-ajax="true" data-confirm="<spring:message code="expedient.tipus.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
								<li><a href="expedientTipus/{{:id}}/netejarDp" class="consultar-expedient"><span class="fa fa-trash"></span>&nbsp;<spring:message code="entorn.llistat.netejar.definicions.proces"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="rowhrefTemplate" type="text/x-jsrender">expedientTipus/{{:id}}</script>

</body>
</html>
