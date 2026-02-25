<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="monitor.integracio.titol"/></title>
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
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
	$('table.accions-integracio').on('rowinfo.dataTable', function(e, $info, rowData) {
		var accioId = rowData['DT_Id'];
		if (rowData['parametres']) {
			var panelParamHeader = '<spring:message code="monitor.integracio.info.parametres"/>';
			var panelParamBody = '<ul>';
			for (var i = 0; i < rowData['parametres'].length; i++) {
				var param = rowData['parametres'][i];
				panelParamBody += '<li>' + param.nom + ': ' + param.valor + '</li>';
			}
			panelParamBody += '</ul>';
			$info.append('<div class="panel panel-default"><div class="panel-heading"><a href="#param-' + accioId + '" data-toggle="collapse">' + panelParamHeader + '</a></div><div id="param-' + accioId + '" class="panel-body collapse">' + panelParamBody + '</div></div>');
		}
		if (rowData['estat'] == 'ERROR') {
			var panelErrorHeader = rowData['excepcioMessage'];
			var panelErrorBody = '<textarea class="form-control" style="width:100%" rows="10">' + rowData['excepcioStacktrace'] + '</textarea>';
			$info.append('<div class="panel panel-default"><div class="panel-heading"><a href="#error-' + accioId + '" data-toggle="collapse">' + panelErrorHeader + '</a></div><div id="error-' + accioId + '" class="panel-body collapse">' + panelErrorBody + '</div></div>');
		}
	});
});
</script>
</head>
<body>
	<ul id="pipelles" class="nav nav-tabs" role="tablist">
	<c:forEach var="integracio" items="${integracions}">
		<li role="presentation">
			<a href="#${integracio.codi}" aria-controls="${integracio.codi}" role="tab" data-toggle="tab">
				${integracio.descripcio}
				<c:if test="${integracio.numErrors gt 0}"><span class="badge" style="background-color: #d9534f;">${integracio.numErrors}</span></c:if>
			</a>
		</li>
	</c:forEach>
	</ul>
	<div class="tab-content">
		<c:forEach var="integracio" items="${integracions}">
			<div role="tabpanel" class="tab-pane" id="${integracio.codi}">
				<br/>
				<table id="accions-${integracio.codi}" data-url="monitorIntegracio/${integracio.codi}/datatable" data-paging-enabled="false" data-ordering="false" data-row-info="true" class="table table-striped table-bordered table-hover accions-integracio" style="width:100%">
				<thead>
					<tr>
						<th data-col-name="data" data-converter="datetime" width="15%"><spring:message code="monitor.integracio.columna.data"/></th>
						<th data-col-name="descripcio" width="50%"><spring:message code="monitor.integracio.columna.descripcio"/></th>
						<th data-col-name="tempsResposta" width="15%" data-template="#tempsRespostaTemplate">
							<spring:message code="monitor.integracio.columna.temps.resposta"/>
							<script id="tempsRespostaTemplate" type="text/x-jsrender">{{:tempsResposta}}ms</script>
						</th>
						<th data-col-name="tipus" width="10%"><spring:message code="monitor.integracio.columna.tipus"/></th>
						<th data-col-name="estat" width="10%" data-template="#estatTemplate">
							<spring:message code="monitor.integracio.columna.estat"/>
							<script id="estatTemplate" type="text/x-jsrender">
								{{if estat == 'OK'}}
									<span class="label label-success"><span class="fa fa-check"/> {{:estat}}</span>
								{{else}}
									<span class="label label-danger" title="{{:errorDescripcio}}"><span class="fa fa-warning"/> {{:estat}}</span>
								{{/if}}
							</script>
						</th>
						<th data-col-name="parametres" data-visible="false"></th>
						<th data-col-name="errorDescripcio" data-visible="false"></th>
						<th data-col-name="excepcioMessage" data-visible="false"></th>
						<th data-col-name="excepcioStacktrace" data-visible="false"></th>
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
