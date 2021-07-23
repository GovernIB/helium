<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="definicio.proces.llistat.titol"/></title>
	<meta name="title" content="<spring:message code='definicio.proces.llistat.titol'/>"/>
	<meta name="screen" content="definicions procés">
	<meta name="title-icon-class" content="fa fa-cube"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<c:if test="${potDissenyarEntorn}">
			<a class="btn btn-default" href="definicioProces/importar" data-toggle="modal"><span class="fa fa-sign-in"></span>&nbsp;<spring:message code="comu.importar"/></a>
			<a class="btn btn-default" href="definicioProces/desplegar" data-toggle="modal" data-callback="callbackModaldefinicionsProces()"><span class="fa fa-download"></span>&nbsp;<spring:message code="comu.filtre.desplegar"/></a>
		</c:if>
	</div>
	<table	id="definicioProces"
			data-toggle="datatable"
			data-url="definicioProces/datatable"
			data-paging-enabled="true"
			data-info-type="search"
			data-ordering="true"
			data-default-order="1"
			data-rowhref-template="#rowhrefTemplate"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"/>
				<th data-col-name="jbpmKey" width="20%"><spring:message code="definicio.proces.llistat.columna.jbpmKey"/></th>
				<th data-col-name="versioCount" data-orderable="false"><spring:message code="definicio.proces.llistat.columna.versions"/></th>
				<th data-col-name="dataCreacio" data-converter="datetime"><spring:message code="definicio.proces.llistat.columna.dataDarreraVersio"/></th>
				<th data-col-name="versio" data-orderable="true"><spring:message code="definicio.proces.llistat.columna.versio"/></th>
				<th data-col-name="expedientTipus.codi"><spring:message code="definicio.proces.llistat.columna.expedientTipus"/></th>
				<th data-col-name="jbpmKey" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu pull-right">
								<li><a href="definicioProces/{{:jbpmKey}}/{{:id}}" class="consultar-expedient"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code="definicio.proces.llistat.accio.dissenyar"/></a></li>
								<li><a data-toggle="modal" href="definicioProces/{{:jbpmKey}}/exportar?definicioProcesId={{:id}}"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.filtre.exportar"/></a></li>
								<li><a data-toggle="modal" href="definicioProces/importar?definicioProcesId={{:id}}"><span class="fa fa-sign-in"></span>&nbsp;<spring:message code="comu.importar"/></a></li>
								<li><a href="definicioProces/{{:jbpmKey}}/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="definicio.proces.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="definicio.proces.llistat.accio.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="rowhrefTemplate" type="text/x-jsrender">definicioProces/{{:jbpmKey}}/{{:id}}</script>	

	<script>
		function callbackModaldefinicionsProces() {
			//webutilRefreshMissatges();
			//refrescaTaula();
			location.reload();
		}
	</script>

</body>
</html>
