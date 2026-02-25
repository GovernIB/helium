<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="comuns.area"/></title>
	<meta name="title" content="<spring:message code="area.llistat.titol"/>"/>
	<meta name="screen" content="arees">
	<meta name="title-icon-class" content="fa fa-cubes"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>
</head>
<body>
	<table	id="area-configurades"
			data-toggle="datatable"
			data-url="area/configurades/datatable"
			data-paging-enabled="true"
			data-info-type="search"
			data-ordering="true"
			data-default-order="1"
			data-rowhref-template="#rowhrefTemplate"
			data-rowhref-toggle="modal" 
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="codi" width="20%"><spring:message code="area.llistat.columna.codi"/></th>
				<th data-col-name="nom"><spring:message code="area.llistat.columna.nom"/></th>
				
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<c:if test="${dadesPersona.admin}">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li><a href="area/{{:id}}/update" data-toggle="modal"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="area.helium.llistat.configurar"/></a></li>
									<li><a href="area/{{:id}}/delete" data-rdt-link-ajax="true" data-confirm="<spring:message code="area.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								</ul>
							</div>
						</c:if>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="rowhrefTemplate" type="text/x-jsrender">area/{{:id}}/update</script>
</body>
</html>