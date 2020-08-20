<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:set var="titol"><spring:message code="expedient.lucene.titol" arguments="${fn:escapeXml(expedient.identificador)}"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
</head>
<body>

	<script type="text/javascript">
		// <![CDATA[
		
		$(document).ready( function() {					
			$('table').DataTable({
				paging: false,
				language: {
					url: webutilContextPath() + '/js/datatables/i18n/datatables.' + solveLanguage() + '.json'
				},
				order: [[4, 'desc'],[0, 'asc'],[1, 'asc']]
			});
		});
		
		// ]]>
	</script>			


	<!-- per marcar l'expedient amb error de sincronització -->
	
	<c:if test="${not empty expedient.reindexarData}">
		<div class="alert alert-warning">
			<span class="fa fa-refresh"></span>
			<spring:message code="expedient.lucene.reindexacio.asincrona.data" arguments="${expedient.reindexarData}"/>
		</div>
	</c:if>
	<c:if test="${expedient.reindexarError}">
		<div class="alert alert-danger">
			<span class="fa fa-refresh"></span>
			<spring:message code="expedient.lucene.reindexacio.error"/>.
		</div>
	</c:if>

	<table id="dades-taula"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th rowspan="2" title="Expedient, tipus d'expedient o definició de procés">Tipus</th>
				<th rowspan="2">Codi</th>
				<th rowspan="2">Etiqueta</th>
				<th rowspan="2">Agrupació</th>
				<th rowspan="2">Error</th>
				<th colspan="2">Dades indexades</th>
				<th colspan="2">Dades expedient</th>
			</tr>
			<tr>
				<th>Valor indexat</th>
				<th>Valor a mostrar</th>
				<th>Valor expedient</th>
				<th>Multiple</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${dades}" var="dada" varStatus="procesosStatus">
			<tr class="row_checkbox <c:if test='${not empty dada.errorReindexacio}'>danger</c:if>">
				<td align="center">
					<c:choose>
					    <c:when test="${dada.tipus == 'EX' }">
							<span class="label label-success" title="Propietat expedient">EX</span>
					    </c:when>    
					    <c:when test="${dada.tipus == 'TE' }">
							<span class="label label-info" title="Tipus Expedient">TE</span>
					    </c:when>    
					    <c:when test="${dada.tipus == 'DP' }">
							<span class="label label-warning" title='Definició de Procés "${dada.definicioProces }"'>DP</span>
					    </c:when>    
					    <c:otherwise>
							<span class="label label-default">${dada.tipus}</span>
					    </c:otherwise>
					</c:choose>	
				</td>
				<td>${dada.codi}</td>
				<td>${dada.etiqueta}</td>
				<td>${dada.agrupacio}</td>
				<td align="center">
					<c:if test="${not empty dada.errorReindexacio}">
						<span class="fa fa-warning text-danger" title="${dada.errorReindexacio}"></span>
					</c:if>					
					<c:if test="${dada.valorDiferent}">
						<span class="fa fa-warning text-warning" title="El valor indexat difereix del valor de l'expedient"></span>
					</c:if>					
				</td>
				<td>
					${dada.valorIndex}
				</td>
				<td class="<c:if test='${dada.valorDiferent}'>danger</c:if>">
					${dada.valorMostrar}
				</td>
				<td class="<c:if test='${dada.valorDiferent}'>danger</c:if>">
					${dada.valorJbpm}
				</td>
				<td align="center">
					<c:if test="${dada.multiple}">&check;</c:if>
				</td>
			</tr>
			</c:forEach>			
		</tbody>
	</table>
	
	<c:if test="${expedient.permisWrite}">
		<a data-rdt-link-confirm="<spring:message code="expedient.accio.reindexa.confirmacio"/>" href="<c:url value="/v3/expedient/lucene/${expedient.id}/reindexa"/>"></a>
	</c:if>

	<form:form cssClass="form-horizontal form-tasca" id="reindexarForm" name="reindexarForm" action="${expedient.id}/reindexa" method="get" >
		<div id="modal-botons">
			<c:if test="${expedient.permisWrite}">
				<button type="submit" class="btn btn-warning" id="submit" name="submit" value="submit">
					<span class="fa fa-refresh"></span>&nbsp;<spring:message code="expedient.info.accio.reindexa"/>
				</button>
			</c:if>
			<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel" data-modal-cancel="true">
				<spring:message code="comu.boto.tancar"/>
			</button>			
		</div>
	</form:form> 		
	
	
	

</body>
</html>
