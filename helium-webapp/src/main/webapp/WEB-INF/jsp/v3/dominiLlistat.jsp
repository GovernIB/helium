<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="domini.llistat.titol"/></title>
	<meta name="title" content="<spring:message code='domini.llistat.titol'/>"/>
	<meta name="screen" content="dominis">
	<meta name="title-icon-class" content="fa fa-cube"/>
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
		<c:if test="${potDissenyarEntorn}">
			<a class="btn btn-default" href="domini/new" data-toggle="modal"  data-datatable-id="domini"><span class="fa fa-plus"></span>&nbsp;<spring:message code="domini.llistat.accio.nou"/></a>
		</c:if>
	</div>
	<table	id="domini"
			data-toggle="datatable"
			data-url="domini/datatable"
			data-paging-enabled="true"
			data-info-type="search"
			data-ordering="true"
			data-default-order="3"
			data-rowhref-template="#rowhrefTemplate"
			data-rowhref-toggle="modal" 
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="id" data-visible="false"/>
				<th data-col-name="tipus" data-visible="false"/>
				<th data-col-name="codi" width="20%"><spring:message code="comuns.codi"/></th>
				<th data-col-name="nom"><spring:message code="comuns.nom"/></th>
					<th data-col-name="tipus" data-template="#cellTipusDomTemplate" data-orderable="false"><spring:message code="comuns.tipus"/>
						<script id="cellTipusDomTemplate" type="text/x-jsrender">
							{{if tipus == 'CONSULTA_SQL'}}
								<spring:message code="expedient.tipus.domini.tipus.sql"/>
							{{else}}
								<spring:message code="expedient.tipus.domini.tipus.ws"/>
							{{/if}}
						</script>
					</th>
				<th data-col-name="id" data-template="#cellEnumTemplate" data-orderable="false" width="10%">
					<script id="cellEnumTemplate" type="text/x-jsrender">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li><a data-toggle="modal" href="domini/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
									<li><a data-toggle="modal" href="domini/{{:id}}/test"><span class="fa fa-cog"></span>&nbsp;<spring:message code="expedient.tipus.domini.form.provar"/></a></li>
									<li><a href="domini/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="domini.llistat.confirm.esborra"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								</ul>
							</div>
						</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="rowhrefTemplate" type="text/x-jsrender">domini/{{:id}}/update</script>	
	
	<script type="text/javascript">
	// <![CDATA[
	            
	$(document).ready(function() {
	});

	// ]]>
	</script>	

</body>
</html>
