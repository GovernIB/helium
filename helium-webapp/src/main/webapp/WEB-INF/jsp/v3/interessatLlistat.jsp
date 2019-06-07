<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="interessat.llistat.titol"/></title>
	<meta name="title" content="<spring:message code="interessat.llistat.titol"/>"/>
	<meta name="screen" content="interessat">
	<meta name="title-icon-class" content="fa fa-cubes"/>
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
	<div class="text-right" style="margin-bottom: 10px;">
		<a class="btn btn-default" href="${expedientId}/interessat/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="interessat.llistat.accio.nou"/></a>
	</div>
	<table	id="interessat"
			data-toggle="datatable"
			data-url="${expedientId}/interessat/datatable"
			data-paging-enabled="true"
			data-info-type="search"
			data-ordering="true"
			data-default-order="1"
			data-rowhref-template="#rowhrefTemplate"
			data-rowhref-toggle="modal" 
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="codi" width="20%"><spring:message code="interessat.llistat.columna.codi"/></th>
				<th data-col-name="nom"><spring:message code="interessat.llistat.columna.nom"/></th>
				<th data-col-name="nif"><spring:message code="interessat.llistat.columna.nif"/></th>
				<th data-col-name="llinatge1"><spring:message code="interessat.llistat.columna.llinatge1"/></th>
				<th data-col-name="llinatge2"><spring:message code="interessat.llistat.columna.llinatge2"/></th>
				<th data-col-name="tipus"><spring:message code="interessat.llistat.columna.tipus"/></th>
				<th data-col-name="email"><spring:message code="interessat.llistat.columna.email"/></th>
				<th data-col-name="telefon"><spring:message code="interessat.llistat.columna.telefon"/></th>
				
 				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%"> 
 					<script id="cellAccionsTemplate" type="text/x-jsrender"> 
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li><a href="${expedientId}/interessat/{{:id}}/update" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
									<li><a href="${expedientId}/interessat/{{:id}}/delete" data-rdt-link-ajax="true" data-confirm="<spring:message code="interessat.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								</ul>
							</div>
					</script>
 				</th> 
			</tr>
		</thead>
	</table>
	<script id="rowhrefTemplate" type="text/x-jsrender">${expedientId}/interessat/{{:id}}/update</script>
</body>
</html>
