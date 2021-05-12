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
	<table	id="area-sense-configurar"
			data-toggle="datatable"
			data-url="area/sense/configurar/datatable"
			data-paging-enabled="true"
			data-info-type="search"
			data-ordering="true"
			data-default-order="0"
			data-rowhref-template="#rowhrefTemplateSenseConfigurar"
			data-rowhref-toggle="modal" 
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"></th>
				<th data-col-name="codi" data-orderable="false"><spring:message code="area.llistat.columna.codi"/></th>
				<th data-col-name="numValors" data-template="#cellSenseConfigurarTemplate" data-orderable="false" width="13%">
					<script id="cellSenseConfigurarTemplate" type="text/x-jsrender">
						<a data-toggle="modal" href="area/{{:codi}}/new" class="btn btn-primary"><span class="fa fa-bars"></span>&nbsp;<spring:message code="area.helium.llistat.configurar"/></a>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="rowhrefTemplateSenseConfigurar" type="text/x-jsrender">area/{{:codi}}/new</script>
</body>
</html>