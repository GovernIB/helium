<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code="expedient.tipus.llistat.titol"/></title>
	<meta name="title" content="<spring:message code='expedient.tipus.llistat.titol'/>"/>
	<meta name="screen" content="expedients tipus">
	<meta name="title-icon-class" content="fa fa-folder"/>
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
	<table id="expedientTipus" 
			data-url="expedientTipus/datatable" 
			data-toggle="datatable" 
			data-paging-enabled="true" 
			data-ordering="true" 
			data-info-type-desapareix-boto-nou="search" 
			class="table table-striped table-bordered table-hover" 
			data-botons-template="#tableButtonsTemplate" 
			data-default-order="1">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false" />
				<th data-col-name="codi" width="15%"><spring:message code="expedient.tipus.llistat.columna.codi"/></th>
				<th data-col-name="nom" ><spring:message code="expedient.tipus.llistat.columna.titol"/></th>
				<th data-rdt-property="id" data-template="#cellAccionsTemplate" data-rdt-context="true" data-rdt-visible="true" data-rdt-sortable="false" data-rdt-nowrap="true" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown navbar-right">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="expedientTipus/{{:id}}" class="consultar-expedient"><span class="fa fa-folder-open"></span>&nbsp;<spring:message code="expedient.llistat.accio.consultar"/></a></li>
								<li><a data-toggle="modal" href="expedientTipus/{{:id}}/modificar"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
								<li><a href="expedientTipus/{{:id}}/delete" data-rdt-link-ajax="true" data-confirm="<spring:message code="expedient.tipus.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="tableButtonsTemplate" type="text/x-jsrender">
		<p style="text-align:right"><a class="btn btn-default" href="expedientTipus/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.llistat.accio.nou"/></a></p>
	</script>
</body>
</html>
