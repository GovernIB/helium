<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="excepcio.list.titol"/></title>
	<meta name="title" content="<spring:message code='excepcio.list.titol'/>"/>
	<meta name="screen" content="darreresExcepcions">
	<meta name="title-icon-class" content="fa fa-exclamation-triangle"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
</head>
<body>
	<table	id="excepcionsDatatable"
			data-toggle="datatable"
			data-url="excepcions/datatable"
			data-paging-enabled="false"
			data-info-type="search"
			data-rowhref-toggle="modal"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="data"		width="9%" data-orderable="false" data-converter="datetime"><spring:message code="excepcio.list.columna.data"/></th>
				<th data-col-name="peticio"		width="17%" data-orderable="false"><spring:message code="excepcio.list.columna.peticio"/></th>
				<th data-col-name="params" 		width="20%" data-orderable="false"><spring:message code="excepcio.list.columna.params"/></th>
				<th data-col-name="objectClass"	width="17%" data-orderable="false"><spring:message code="excepcio.list.columna.objecte.class"/></th>				
				<th data-col-name="message"		width="27%" data-orderable="false"><spring:message code="excepcio.list.columna.message"/></th>
				<th data-col-name="index"		width="10%" data-orderable="false" data-template="#cellAccionsTemplate" align="center">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="excepcions/{{:index}}" class="btn btn-default" data-toggle="modal"><span class="fa fa-info-circle"></span>&nbsp;&nbsp;<spring:message code="comu.boto.detalls"/></a>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>