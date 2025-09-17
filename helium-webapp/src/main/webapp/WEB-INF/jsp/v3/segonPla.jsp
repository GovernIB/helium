<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>


<c:set var="titol"><spring:message code="decorator.menu.administracio.tasques.segonPla"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#tasquesTable').on('draw.dt', function (e, settings) {
				webutilModalAdjustHeight();
			});
		});
		function actualitzarTaula() {
			$('#tasquesTable').webutilDatatable('refresh');
		}
		function reiniciarTasca(codi) {
			if (!codi || codi==null) codi='totes';
			$.ajax({
				type: 'GET',
				url: '<c:url value="/v3/tasca-segon-pla/restart/' + codi + '"/>',
				async: true,
				success: function(data) {
				        webutilRefreshMissatges();
				        setTimeout(actualitzarTaula, 1000);
				},
				error: function() {
					console.error("Error en la petició AJAX reiniciant la tasca.");
				}
			});
		}

        

	</script>
</head>

<body>

	<table 
		id="tasquesTable" 
		data-toggle="datatable"
		data-url="<c:url value="/v3/tasca-segon-pla/datatable"/>" 
		data-search-enabled="false"
		data-botons-template="#tasquesTableBotoTots"
		data-save-state="true"
		data-default-order="0"
		data-default-dir="asc"
		data-paging-enabled="false"
		class="table table-striped table-bordered" 
		style="width:100%">
		<thead>
			<tr>
				<th data-col-name="codi" data-orderable="false" data-template="#cellTascaCodiTemplate" width="30%">
					<spring:message code="administracio.tasquesSegonPla.tasca"/>
					<script id="cellTascaCodiTemplate" type="text/x-jsrender">
					{{if codi == 'comprovarAnotacionsPendents'}}
						<spring:message code="administracio.tasquesSegonPla.codi.comprovarAnotacionsPendents"/>
					{{else codi == 'comprovarExecucionsMassives'}}
						<spring:message code="administracio.tasquesSegonPla.codi.comprovarExecucionsMassives"/>
					{{else codi == 'comprovarReindexacioAsincrona'}}
						<spring:message code="administracio.tasquesSegonPla.codi.comprovarReindexacioAsincrona"/>
					{{else codi == 'actualitzarUnitatsIProcediments'}}
						<spring:message code="administracio.tasquesSegonPla.codi.actualitzarUnitatsIProcediments"/>
					{{else codi == 'updatePeticionsAsincronesPinbal'}}
						<spring:message code="administracio.tasquesSegonPla.codi.updatePeticionsAsincronesPinbal"/>
					{{else codi == 'comprovarEmailAnotacionsNoAgrupats'}}
						<spring:message code="administracio.tasquesSegonPla.codi.comprovarEmailAnotacionsNoAgrupats"/>
					{{else codi == 'comprovarEmailAnotacionsAgrupats'}}
						<spring:message code="administracio.tasquesSegonPla.codi.comprovarEmailAnotacionsAgrupats"/>
					{{else codi == 'processarAnotacionsAutomatiques'}}
						<spring:message code="administracio.tasquesSegonPla.codi.processarAnotacionsAutomatiques"/>
					{{else codi == 'migrarExpedientsArxiu'}}
						<spring:message code="administracio.tasquesSegonPla.codi.migrarExpedientsArxiu"/>
					{{/if}}
					</script>
				</th>
				<th data-col-name="estat" data-orderable="false" data-template="#cellTascaEstatTemplate" width="15%">
					<%-- <spring:message code="administracio.tasquesSegonPla.estat"/> --%>
					<script id="cellTascaEstatTemplate" type="text/x-jsrender">
					{{if estat == 'EN_EXECUCIO'}}
						<spring:message code="administracio.tasquesSegonPla.estat.EN_EXECUCIO"/>
					{{else estat == 'EN_ESPERA'}}
						<spring:message code="administracio.tasquesSegonPla.estat.EN_ESPERA"/>
					{{else estat == 'ERROR'}}
						<spring:message code="administracio.tasquesSegonPla.estat.ERROR"/>
					{{/if}}
					</script>
				</th>
				<th data-col-name="dataInici" data-converter="datetime" data-orderable="false" width="15%"><spring:message code="administracio.tasquesSegonPla.darrer.inici"/></th>
				<th data-col-name="dataFi" data-converter="datetime" data-orderable="false" width="15%"><spring:message code="administracio.tasquesSegonPla.fi.execucio"/></th>
				<th data-col-name="properaExecucio" data-converter="datetime" data-orderable="false" width="15%"><spring:message code="administracio.tasquesSegonPla.propera.execucio"/></th>
				<th data-col-name="codi" data-orderable="false" data-template="#cellAccionsTemplate" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<a href="Javascript:reiniciarTasca('{{:codi}}');" class="btn btn-warning"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="administracio.tasquesSegonPla.boto.reiniciar"/></a>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="tasquesTableBotoTots" type="text/x-jsrender">
	<div style="float: right;">
		<a href="Javascript:actualitzarTaula();" class="btn btn-success" style="margin-right: 45px;"><span class="fa fa-list"></span>&nbsp;<spring:message code="administracio.tasquesSegonPla.boto.refresh.taula"/></a>
		<a href="Javascript:reiniciarTasca();" class="btn btn-warning" style="margin-right: 45px;"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="administracio.tasquesSegonPla.boto.reiniciar.totes"/></a>
	</div>
	</script>
	<div id="modal-botons">
		<a href="#" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></a>
	</div>
</body>
</html>