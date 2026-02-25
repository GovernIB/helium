<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<c:choose>
		<c:when test="${permisUO == null}">
			<title><spring:message code="expedient.tipus.permis.titol"/></title>
			<meta name="title" content="<spring:message code="expedient.tipus.permis.titol"/>"/>
		</c:when>
		<c:otherwise> 
		<title><spring:message code="expedient.tipus.permis.UO.titol"/></title>
			<meta name="title" content="<spring:message code="expedient.tipus.permis.UO.titol"/>"/>
	 	</c:otherwise>
	</c:choose>
	<meta name="screen" content="expedients tipus"/>
	<meta name="subtitle" content="${expedientTipus.nom}"/>
	<meta name="title-icon-class" content="fa fa-key"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<hel:modalHead/>
</head>
<body>
	<div class="text-right" data-toggle="botons-titol">
		<a class="btn btn-default" href="permis${permisUO}/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.permis.accio.nou"/></a>
	</div>
	<table	id="expedientTipus"
			data-toggle="datatable"
			data-url="permis${permisUO}/datatable"
			data-paging-enabled="true"
			data-ordering="false"
			data-default-order="1"
			data-ajax-request-type="POST"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="read" data-visible="false"/>
				<th data-col-name="write" data-visible="false"/>
				<th data-col-name="create" data-visible="false"/>
				<th data-col-name="delete" data-visible="false"/>
				<th data-col-name="administration" data-visible="false"/>
				<th data-col-name="cancel" data-visible="false"/>
				<th data-col-name="stop" data-visible="false"/>
				<th data-col-name="relate" data-visible="false"/>
				<th data-col-name="dataManagement" data-visible="false"/>
				<th data-col-name="docManagement" data-visible="false"/>
				<th data-col-name="termManagement" data-visible="false"/>
				<th data-col-name="taskManagement" data-visible="false"/>
				<th data-col-name="taskSupervision" data-visible="false"/>
				<th data-col-name="taskAssign" data-visible="false"/>
				<th data-col-name="logRead" data-visible="false"/>
				<th data-col-name="logManage" data-visible="false"/>
				<th data-col-name="tokenRead" data-visible="false"/>
				<th data-col-name="tokenManage" data-visible="false"/>
				<th data-col-name="designAdmin" data-visible="false"/>
				<th data-col-name="designDeleg" data-visible="false"/>
				<th data-col-name="scriptExe" data-visible="false"/>
				<th data-col-name="undoEnd" data-visible="false"/>
				<th data-col-name="defprocUpdate" data-visible="false"/>
				<th data-col-name="design" data-visible="false"/>
				<th data-col-name="supervision" data-visible="false"/>
				<th data-col-name="manage" data-visible="false"/>
				<th data-col-name="reassignment" data-visible="false"/>
				<th data-col-name="unitatOrganitzativaCodi" data-visible="false"/>
				<th data-col-name="principalNom" width="20%"><spring:message code="expedient.tipus.permis.columna.principal"/></th>
				<th data-col-name="principalTipus" width="20%"><spring:message code="expedient.tipus.permis.columna.tipus"/></th>
				
				<c:if test="${permisUO != null}">
					<th data-col-name="unitatOrganitzativaCodiNom" width="30%" data-template="#cellUnitatOrganitzativaTemplate" data-orderable="false">
					<spring:message code="expedient.tipus.permis.columna.unitat.organitzativa"/>
					<script id="cellUnitatOrganitzativaTemplate" type="text/x-jsrender">
						{{:unitatOrganitzativaCodiNom}}
					</script>
					<input type="hidden" id="unitatOrganitzativaCodi" name="unitatOrganitzativaCodi">
					</th>
				</c:if>
				<th data-col-name="id" data-template="#cellPermisosTemplate" data-orderable="false" width="50%">
					<spring:message code="expedient.tipus.permis.columna.permisos"/>
					<script id="cellPermisosTemplate" type="text/x-jsrender">
						{{if administration}}<span class="label label-primary"><spring:message code="permis.ADMINISTRATION"/></span>{{/if}}
						{{if read}}<span class="label label-default"><spring:message code="permis.READ"/></span>{{/if}}
						{{if write}}<span class="label label-default"><spring:message code="permis.WRITE"/></span>{{/if}}
						{{if create}}<span class="label label-default"><spring:message code="permis.CREATE"/></span>{{/if}}
						{{if delete}}<span class="label label-default"><spring:message code="permis.DELETE"/></span>{{/if}}
						{{if cancel}}<span class="label label-default"><spring:message code="permis.CANCEL"/></span>{{/if}}
						{{if stop}}<span class="label label-default"><spring:message code="permis.STOP"/></span>{{/if}}
						{{if relate}}<span class="label label-default"><spring:message code="permis.RELATE"/></span>{{/if}}
						{{if dataManagement}}<span class="label label-default"><spring:message code="permis.DATA_MANAGEMENT"/></span>{{/if}}
						{{if docManagement}}<span class="label label-default"><spring:message code="permis.DOC_MANAGEMENT"/></span>{{/if}}
						{{if termManagement}}<span class="label label-default"><spring:message code="permis.TERM_MANAGEMENT"/></span>{{/if}}
						{{if taskManagement}}<span class="label label-default"><spring:message code="permis.TASK_MANAGEMENT"/></span>{{/if}}
						{{if taskSupervision}}<span class="label label-default"><spring:message code="permis.TASK_SUPERVISION"/></span>{{/if}}
						{{if taskAssign}}<span class="label label-default"><spring:message code="permis.TASK_ASSIGN"/></span>{{/if}}
						{{if logRead}}<span class="label label-default"><spring:message code="permis.LOG_READ"/></span>{{/if}}
						{{if logManage}}<span class="label label-default"><spring:message code="permis.LOG_MANAGE"/></span>{{/if}}
						{{if tokenRead}}<span class="label label-default"><spring:message code="permis.TOKEN_READ"/></span>{{/if}}
						{{if tokenManage}}<span class="label label-default"><spring:message code="permis.TOKEN_MANAGE"/></span>{{/if}}
						{{if designAdmin}}<span class="label label-default"><spring:message code="permis.DESIGN_ADMIN"/></span>{{/if}}
						{{if designDeleg}}<span class="label label-default"><spring:message code="permis.DESIGN_DELEG"/></span>{{/if}}
						{{if scriptExe}}<span class="label label-default"><spring:message code="permis.SCRIPT_EXE"/></span>{{/if}}
						{{if undoEnd}}<span class="label label-default"><spring:message code="permis.UNDO_END"/></span>{{/if}}
						{{if defprocUpdate}}<span class="label label-default"><spring:message code="permis.DEFPROC_UPDATE"/></span>{{/if}}
						{{if design}}<span class="label label-default"><spring:message code="permis.DESIGN"/></span>{{/if}}
						{{if supervision}}<span class="label label-default"><spring:message code="permis.SUPERVISION"/></span>{{/if}}
						{{if manage}}<span class="label label-default"><spring:message code="permis.MANAGE"/></span>{{/if}}
						{{if reassignment}}<span class="label label-default"><spring:message code="permis.REASSIGNMENT"/></span>{{/if}}
					</script>
				</th>
				<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a data-toggle="modal" href="permis${permisUO}/{{:id}}?unitatOrganitzativaCodi={{:unitatOrganitzativaCodi}}"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
								<li><a href="permis${permisUO}/{{:id}}/delete?unitatOrganitzativaCodi={{:unitatOrganitzativaCodi}}" data-rdt-link-ajax="true" data-confirm="<spring:message code="expedient.tipus.permis.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
							</ul>
						</div>
					</script>
				</th>
			</tr>
		</thead>
	</table>
	<a href="<c:url value="/v3/expedientTipus"/>" class="btn btn-default pull-right"><span class="fa fa-arrow-left"></span> <spring:message code="comu.boto.tornar"/></a>
</body>
</html>
