<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="expedient.monitor.integracio.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>
<script>
$(document).ready(function() {
	$('#pipelles li').on('show.bs.tab', function(e) {
		var integracioCodi = $('a', this).attr('href').substring(1);
		$('table#accions-' + integracioCodi).webutilDatatable();
	});
	$('#pipelles li:first a').click();
});
</script>
</head>
<body>
	<ul id="pipelles" class="nav nav-tabs" role="tablist">
	<c:forEach var="integracio" items="${integracions}">
		<li role="presentation">
			<a href="#${integracio.codi}" aria-controls="${integracio.codi}" role="tab" data-toggle="tab">${integracio.descripcio}</a>
		</li>
	</c:forEach>
	</ul>
	<div class="tab-content">
		<c:forEach var="integracio" items="${integracions}">
			<div role="tabpanel" class="tab-pane" id="${integracio.codi}">
				<br/>
				<table id="accions-${integracio.codi}" data-url="monitorIntegracio/${integracio.codi}/datatable" data-paging-enabled="false" data-ordering="false" class="table table-striped table-bordered table-hover" style="width:100%">
				<thead>
					<tr>
						<th data-col-name="data" width="15%">Data</th>
						<th data-col-name="descripcio" width="40%">Descripció</th>
						<th data-col-name="tipus" width="25%">Tipus</th>
					</tr>
				</thead>
				</table>
			</div>
		</c:forEach>
	</div>
	<div id="modal-botons" class="well hide">
		<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
	</div>
</body>
</html>
