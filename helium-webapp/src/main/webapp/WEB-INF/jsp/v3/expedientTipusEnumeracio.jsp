<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<c:choose>
	<c:when test="${not empty expedientTipus}">

		<div class="botons-titol text-right">
			<a	id="nou_camp" class="btn btn-default" 
				href="${expedientTipus.id}/enumeracio/new" 
				data-toggle="modal" 
				data-datatable-id="expedientEnumeracio">
					<span class="fa fa-plus"></span>&nbsp;
					<spring:message code="expedient.tipus.enumeracio.llistat.accio.nova"/>
			</a>
		</div>
		
		<table	id="expedientEnumeracio"
				data-toggle="datatable"
				data-url="${expedientTipus.id}/enumeracio/datatable"
				data-paging-enabled="true"
				data-info-type="search"
				data-ordering="true"
				data-default-order="2"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="numValors" data-visible="false"/>
					<th data-col-name="codi" width="20%"><spring:message code="expedient.tipus.enumeracio.llistat.columna.codi"/></th>
					<th data-col-name="nom"><spring:message code="expedient.tipus.enumeracio.llistat.columna.titol"/></th>
					<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsTemplate" type="text/x-jsrender">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li><a data-toggle="modal" href="${expedientTipus.id}/enumeracio/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
									<li><a href="${expedientTipus.id}/enumeracio/{{:id}}/delete" data-rdt-link-ajax="true" data-confirm="<spring:message code="expedient.tipus.enumeracio.llistat.confirm.esborra"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a></li>
									<li><a data-toggle="modal" href="${expedientTipus.id}/enumeracio/{{:id}}/valors"><span class="fa fa-bars"></span>&nbsp;<spring:message code="expedient.tipus.enumeracio.llistat.boto.valors"/>&nbsp;({{:numValors}})</a></li>
								</ul>
							</div>
						</script>
					</th>
				</tr>
			</thead>
		</table>

		
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>