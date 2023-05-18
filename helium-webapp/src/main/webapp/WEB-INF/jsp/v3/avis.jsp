<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${empty avisCommand.id}"><
		<c:set var="titol"><spring:message code="avis.form.titol.nou"/></c:set>
		<c:set var="formAction">new</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="avis.form.titol.modificar"/></c:set>
		<c:set var="formAction">update</c:set>
	</c:otherwise>
</c:choose>

<html>
<head>
	<title><spring:message code="avis.llistat.titol"/></title>
	<meta name="screen" content="avisos">
	<meta name="title" content="<spring:message code='avis.llistat.titol'/>"/>
	<meta name="title-icon-class" content="fa fa-cubes"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<c:if test="${dadesPersona.admin}">
			<a class="btn btn-default" href="avis/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="avis.form.titol.nou"/></a>
		</c:if>
	</div>
	
	<table	id=""
			data-toggle="datatable"
			data-url="avis/datatable"
			data-paging-enabled="true"
			data-info-type="search"
			data-ordering="true"
			data-default-order="1"
			data-rowhref-template="#rowhrefTemplate"
			data-rowhref-toggle="modal" 
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="assumpte" width="50%"><spring:message code="avis.assumpte"/></th>
				<th data-col-name="dataInici" data-converter="date"><spring:message code="avis.dataInici"/></th>
				<th data-col-name="dataFinal" data-converter="date"><spring:message code="avis.dataFi"/></th>
				<th data-col-name="avisNivell"><spring:message code="avis.avisNivell"/></th>
				<th data-col-name="actiu" data-template="#cellActivaTemplate">
					<spring:message code="avis.list.columna.activa"/>
					<script id="cellActivaTemplate" type="text/x-jsrender">
						{{if actiu}}<span class="fa fa-check"></span>{{/if}}
					</script>
				</th>
		
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<c:if test="${dadesPersona.admin}">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li><a href="avis/{{:id}}/update" data-toggle="modal" data-callback="callbackModalAvis()"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
									{{if !actiu}}
										<li><a href="avis/{{:id}}/enable"><span class="fa fa-check"></span>&nbsp;&nbsp;<spring:message code="avis.boto.activar"/></a></li>
									{{else}}
										<li><a href="avis/{{:id}}/disable"><span class="fa fa-times"></span>&nbsp;&nbsp;<spring:message code="avis.boto.desactivar"/></a></li>
									{{/if}}
									<li><a href="avis/{{:id}}/delete" data-rdt-link-ajax="true" data-confirm="<spring:message code="avis.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
								</ul>
							</div>
						</c:if>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<script id="rowhrefTemplate" type="text/x-jsrender">avis/{{:id}}/update</script>
	<script type="text/javascript">
		// <![CDATA[
		$(document).ready(function() {
			
			$('#avis').on('draw.dt', function() {
				// Refresca els missatges
				webutilRefreshMissatges();		
			});
		});

		function callbackModalAvis() {
			refrescaTaula();
		}

		function refrescaTaula() {
			$('#avis').webutilDatatable('refresh');
		}
		// ]]>
	</script>		
	
</body>
</html>
