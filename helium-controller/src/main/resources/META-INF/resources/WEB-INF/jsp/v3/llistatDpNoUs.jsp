<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="entorn.llistat.netejar.titol"/></title>
	<meta name="title" content="<spring:message code="entorn.llistat.netejar.titol"/>"/>
	<meta name="screen" content="expedients tipus"/>
	<meta name="subtitle" content="${expedientTipus.nom}"/>
	<meta name="title-icon-class" content="fa fa-trash"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.0/js/dataTables.select.min.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>
</head>
<body>
	<table	id="definicioProcesNoUs"
			data-toggle="datatable"
			data-url="netejarDp"
			data-paging-enabled="true"
			data-ordering="true"
			data-default-order="0"
			data-selection-enabled="true"
			data-selection-url="selectionDp/selection"
			data-selection-counter="#eliminacioMassivaCount"
			data-botons-template="#tableButtonsDpNoUsTemplate"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"/>
				<th data-col-name="jbpmKey"><spring:message code="expedient.tipus.definicioProces.llistat.columna.nom"/></th>
				<th data-col-name="versio"><spring:message code="expedient.tipus.definicioProces.llistat.columna.versio"/></th>
				<th data-col-name="dataCreacio" data-converter="datetime"><spring:message code="expedient.tipus.definicioProces.llistat.columna.dataVersio"/></th>
			
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
				<spring:message code="expedient.info.relacionats"/>
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<a href="../../expedientTipus/${expedientTipus.id}/afectatsDp/{{:id}}" class="btn btn-default" title="<spring:message code="expedient.info.relacionats"/>"><span class="fa fa-search"></span>&nbsp;<spring:message code="decorator.menu.expedients"/>&nbsp;</a>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="tableButtonsDpNoUsTemplate" type="text/x-jsrender">
		<div style="text-align:right">
			<div id="btnTramitacio" class="btn-group">
				<a class="btn btn-default" href="../../expedientTipus/${expedientTipus.id}/selectionDp/all" data-toggle="ajax" title="<spring:message code="expedient.llistat.accio.seleccio.tots"/>"><span class="fa fa-check-square-o"></span></a>
				<a class="btn btn-default" href="../../expedientTipus/${expedientTipus.id}/selectionDp/clear" data-toggle="ajax" title="<spring:message code="expedient.llistat.accio.seleccio.netejar"/>"><span class="fa fa-square-o"></span></a>
				<a class="btn btn-default" href="../../expedientTipus/${expedientTipus.id}/netejarSelectedDp"><spring:message code="entorn.llistat.netejar.seleccionats"/>&nbsp;<span id="eliminacioMassivaCount" class="badge">&nbsp;</span></a>
			</div>
		</div>
	</script>
	<a href="<c:url value="/v3/expedientTipus"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span> <spring:message code="comu.boto.tornar"/></a>
	
</body>
</html>