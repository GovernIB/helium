<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="defproc.llistat.llistar.afectats.titol"/></title>
	<meta name="title" content="<spring:message code="defproc.llistat.llistar.afectats.titol"
       arguments="${definicioProces.jbpmKey};${definicioProces.versio}"
       htmlEscape="false"
       argumentSeparator=";"/>"/>
	<meta name="screen" content="expedients afectats dp no us"/>
	<meta name="title-icon-class" content="fa fa-search"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/webjars/datatables.net-select/1.1.0/js/dataTables.select.min.js"/>"></script>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>
</head>
<body>
	<table	id="afectatsDp"
			data-toggle="datatable"
			data-url="../afectatsDp/${definicioProces.id}"
			data-paging-enabled="true"
			data-ordering="true"
			data-default-order="0"
			data-selection-enabled="true"
			data-selection-url="../afectatsDp/${definicioProces.id}/selection"
			data-selection-counter="#eliminacioMassivaCount"
			data-rowhref-template="#rowhrefTemplate"
			data-botons-template="#tableButtonsAfectatsDp"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"/>
				<th data-col-name="identificador" data-template="#identificador">
					<spring:message code="expedient.llistat.columna.expedient"/>
					<script id="identificador" type="text/x-jsrender">
						{{:identificador}}
					</script>
				</th>
				<th data-col-name="dataInici" data-converter="datetime"><spring:message code="expedient.info.iniciat_el"/></th>
			</tr>
		</thead>
	</table>
	<script id="tableButtonsAfectatsDp" type="text/x-jsrender">
		<div style="text-align:right">
			<div id="btnTramitacio" class="btn-group">
				<a class="btn btn-default" href="../../../expedientTipus/${expedientTipus.id}/afectatsDp/${definicioProces.id}/all" data-toggle="ajax" title="<spring:message code="expedient.llistat.accio.seleccio.tots"/>"><span class="fa fa-check-square-o"></span></a>
				<a class="btn btn-default" href="../../../expedientTipus/${expedientTipus.id}/afectatsDp/${definicioProces.id}/clear" data-toggle="ajax" title="<spring:message code="expedient.llistat.accio.seleccio.netejar"/>"><span class="fa fa-square-o"></span></a>
				<a class="btn btn-default" href="../../../expedientTipus/${expedientTipus.id}/eliminarLogs/${definicioProces.id}"><spring:message code="defproc.llistat.llistar.afectats.buidar.logs"/>&nbsp;<span id="eliminacioMassivaCount" class="badge">&nbsp;</span></a>
			</div>
		</div>
	</script>
	<script id="rowhrefTemplate" type="text/x-jsrender">../../../expedient/{{:id}}</script>
	<a href="<c:url value="/v3/expedientTipus/${expedientTipus.id}/netejarDp"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span> <spring:message code="comu.boto.tornar"/></a>
</body>
</html>