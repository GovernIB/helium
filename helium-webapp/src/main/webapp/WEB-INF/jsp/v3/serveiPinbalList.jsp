<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<html>
<head>
	<title><spring:message code="decorator.menu.serveisPinbal"/></title>
	<meta name="title" content="<spring:message code='decorator.menu.serveisPinbal'/>"/>
	<meta name="screen" content="serveisPinbal">
	<meta name="title-icon-class" content="fa fa-exchange fa-rotate-90"/>
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
			data-url="serveisPinbal/datatable"
			data-paging-enabled="false"
			data-info-type="search"
			data-rowhref-toggle="modal"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="codi" width="18%"><spring:message code="serveisPinbal.col.codi"/></th>
				<th data-col-name="nom"  width="50%"><spring:message code="serveisPinbal.col.nom"/></th>
				<th data-col-name="documentsRestringits_str" width="27%"><spring:message code="serveisPinbal.col.docsRestr"/></th>
				<th data-col-name="id" width="5%" data-template="#cellAccionsTemplate" data-orderable="false">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown navbar-right">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/v3/serveisPinbal/{{:id}}"/>" data-toggle="modal" class="consultar-expedient"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="serveisPinbal.boto.modificar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
</body>