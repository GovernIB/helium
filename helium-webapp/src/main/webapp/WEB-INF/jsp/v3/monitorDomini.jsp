<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="expedient.monitor.domini.titol"/></title>
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
		//var $tabContent = $($('a', this).attr('href'));
		var dominiId = $('a', this).attr('href').substring(1);
		$('table#accions-' + dominiId).webutilDatatable();
		
	});
	$('#domini-seleccio-dropdown li a').click(function (e) {
		$('#accions-domini').webutilDatatable('refresh-url', $(this).attr('href'));
		$('#domini-seleccio span:first').text($(this).text());
		$('#domini-seleccio-dropdown').dropdown('toggle')
		return false;
	});
	$('table#accions-domini').on('rowinfo.dataTable', function(e, $info, rowData) {
		var dominiId = rowData['DT_Id'];
		if (rowData['parametres']) {
			var panelParamHeader = 'Paràmetres de la petició';
			var panelParamBody = '<ul>';
			panelParamBody += '</ul>';
			$info.append('<div class="panel panel-default"><div class="panel-heading"><a href="#param-' + dominiId + '" data-toggle="collapse">' + panelParamHeader + '</a></div><div id="param-' + dominiId + '" class="panel-body collapse">' + panelParamBody + '</div></div>');
		}
		if (rowData['estat'] == 'ERROR') {
			var panelErrorHeader = rowData['excepcioMessage'];
			var panelErrorBody = '<textarea class="form-control" style="width:100%" rows="10">' + rowData['excepcioStacktrace'] + '</textarea>';
			$info.append('<div class="panel panel-default"><div class="panel-heading"><a href="#error-' + dominiId + '" data-toggle="collapse">' + panelErrorHeader + '</a></div><div id="error-' + dominiId + '" class="panel-body collapse">' + panelErrorBody + '</div></div>');
		}
	});
});
</script>
</head>
<body>
	<div class="well well-sm">
		<c:forEach var="entorn" items="${entorns}">
			<c:if test="${entorn.id == entornActualId}"><c:set var="entornActualNom" value="${entorn.nom}"/></c:if>
		</c:forEach>
		Entorn:
		<div class="btn-group">
			<button id="entorn-seleccio" class="btn btn-default" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${entornActualNom} <span class="caret"></span></button>
			<ul class="dropdown-menu" aria-labelledby="entorn-seleccio">
				<c:forEach var="entorn" items="${entorns}">
					<li><a href="monitorDomini?entornId=${entorn.id}">${entorn.nom}</a></li>
				</c:forEach>
			</ul>
		</div>
		&nbsp;&nbsp;&nbsp;&nbsp;Domini:
		<div class="btn-group">
			<c:if test="${not empty dominis}"><c:set var="dominiActual" value="${dominis[0]}"/></c:if>
			<button id="domini-seleccio" class="btn btn-default" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span>${dominiActual.codi} - ${dominiActual.nom}</span> <span class="caret"></span></button>
			<ul id="domini-seleccio-dropdown" class="dropdown-menu" aria-labelledby="domini-seleccio">
				<c:forEach var="domini" items="${dominis}">
					<li><a href="monitorDomini/${domini.id}/datatable">${domini.codi} - ${domini.nom}</a></li>
				</c:forEach>
			</ul>
		</div>
	</div>
	<table id="accions-domini"<c:if test="${not empty dominis}"> data-toggle="datatable"</c:if> data-url="monitorDomini/${dominiActual.id}/datatable" data-paging-enabled="false" data-ordering="false" data-row-info="true" class="table table-striped table-bordered" style="width:100%">
	<thead>
		<tr>
			<th data-col-name="data" data-converter="datetime" width="25%">Data</th>
			<th data-col-name="descripcio" width="25%">Descripció</th>
			<th data-col-name="tipus" width="25%">Tipus</th>
			<th data-col-name="estat" width="25%" data-template="#estatTemplate">
				Resultat
				<script id="estatTemplate" type="text/x-jsrender"><span title="{{:errorDescripcio}}">{{:estat}}</span></script>
			</th>
			<th data-col-name="parametres" data-visible="false"></th>
			<th data-col-name="errorDescripcio" data-visible="false"></th>
			<th data-col-name="excepcioMessage" data-visible="false"></th>
			<th data-col-name="excepcioStacktrace" data-visible="false"></th>
		</tr>
	</thead>
	</table>
	<div id="modal-botons" class="well hide">
		<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
	</div>
</body>
</html>
