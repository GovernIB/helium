<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<%--<c:set var="baseModalUrl"><c:url value="/modal/v3/expedientTipus/${expedientTipus.id}/estat/${estat.id}"></c:url></c:set>--%>

<html>
<head>
	<title><spring:message code="expedient.tipus.estat.permis.titol"/></title>
	<meta name="title" content="<spring:message code="expedient.tipus.estat.permis.titol"/>"/>
	<meta name="screen" content="permisos estats"/>
	<meta name="subtitle" content="${expedientTipus.nom}:${estat.nom}"/>
	<meta name="title-icon-class" content="fa fa-key"/>
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
		<a class="btn btn-default" href="permis/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.permis.accio.nou"/></a>
	</div>
	<table	id="estatpermis"
			data-toggle="datatable"
			data-url="permis/datatable"
			data-paging-enabled="false"
			data-ordering="false"
			data-default-order="1"
			data-ajax-request-type="GET"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="read" data-visible="false"/>
				<th data-col-name="write" data-visible="false"/>
				<th data-col-name="dataManagement" data-visible="false"/>
				<th data-col-name="docManagement" data-visible="false"/>
				<th data-col-name="principalNom" width="20%"><spring:message code="expedient.tipus.permis.columna.principal"/></th>
				<th data-col-name="principalTipus" width="20%"><spring:message code="expedient.tipus.permis.columna.tipus"/></th>
				<th data-col-name="id" data-template="#cellPermisosTemplate" data-orderable="false" width="50%">
					<spring:message code="expedient.tipus.permis.columna.permisos"/>
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						{{if read}}<span class="label label-default"><spring:message code="permis.READ"/></span>{{/if}}
						{{if write}}<span class="label label-default"><spring:message code="permis.WRITE"/></span>{{/if}}
						{{if dataManagement}}<span class="label label-default"><spring:message code="permis.DATA_MANAGEMENT"/></span>{{/if}}
						{{if docManagement}}<span class="label label-default"><spring:message code="permis.DOC_MANAGEMENT"/></span>{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a data-toggle="modal" href="permis/{{:id}}"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="permis/{{:id}}/delete" data-rdt-link-ajax="true" data-confirm="<spring:message code="expedient.tipus.permis.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<a href="<c:url value="/v3/expedientTipus/${expedientTipus.id}?pipellaActiva=estats"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span> <spring:message code="comu.boto.tornar"/></a>
</body>
</html>
