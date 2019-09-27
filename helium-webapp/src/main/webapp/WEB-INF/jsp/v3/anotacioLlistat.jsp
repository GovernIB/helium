<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="anotacio.llistat.titol"/></title>
	<meta name="title" content="<spring:message code='anotacio.llistat.titol'/>"/>
	<meta name="screen" content="anotacions">
	<meta name="title-icon-class" content="fa fa-book"/>
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
	<form:form action="" method="post" cssClass="well" commandName="anotacioFiltreCommand">
		<div class="row">
			<div class="col-md-2">
				<hel:inputText name="extracte" textKey="anotacio.llistat.filtre.camp.extracte" placeholderKey="anotacio.llistat.filtre.camp.extracte" inline="true"/>
			</div>
			<div class="col-md-6">
				<div class="pull-right">
					<button id="netejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
					<button id="consultar" type="submit" name="accio" value="consultar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
				</div>
			</div>
		</div>
	</form:form>
	<table	id="anotacio"
			data-toggle="datatable"
			data-url="<c:url value="anotacio/datatable"/>"
			data-paging-enabled="true"
			data-ordering="true"
			data-default-order="0"
			data-default-desc="asc"
			data-rowhref-template="#rowhrefTemplate"
			data-rowhref-toggle="modal" 
			class="table table-striped table-bordered table-hover"
			style="width:100%">			
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"/>
				<th data-col-name="extracte"><spring:message code="anotacio.llistat.columna.extracte"/></th>
				<th data-col-name="expedientNumero"><spring:message code="anotacio.llistat.columna.extracte"/></th>
				<th data-col-name="id" data-template="#cellAnotacioAccioTemplate" data-orderable="false" width="10%">
					<script id="cellAnotacioAccioTemplate" type="text/x-jsrender">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
								</ul>
							</div>
						</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="rowhrefTemplate" type="text/x-jsrender">anotacio/{{:id}}</script>	
	
	<script type="text/javascript">
	// <![CDATA[
	            
	$(document).ready(function() {
	});
	
	// ]]>
	</script>	

</body>
</html>
