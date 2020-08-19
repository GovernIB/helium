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
				order: [[1, 'asc'],[2, 'asc']]
			});
		});
		
		// ]]>
	</script>			


	<!-- per marcar l'expedient amb error de sincronització -->
	
	<c:if test="${not empty expedient.reindexarData || expedient.reindexarError}">
		<div class="alert <c:if test='${expedient.reindexarError}'>alert-danger</c:if>">
			<span class="fa fa-refresh"></span>
			<c:if test="${expedient.reindexarData != null}"> <spring:message code="expedient.lucene.reindexacio.asincrona.data" arguments="${expedient.reindexarData}"/>. </c:if>
			<c:if test="${expedient.reindexarError}"> <spring:message code="expedient.lucene.reindexacio.error"/>. </c:if>
		</div>
	</c:if>

	<table id="dades-taula"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th rowspan="2">Error</th>
				<th rowspan="2" title="Expedient, tipus d'expedient o definició de procés">Tipus</th>
				<th rowspan="2">Etiqueta</th>
				<th rowspan="2">Agrupació</th>
				<th colspan="3">Dades Lucene</th>
			</tr>
			<tr>
				<th>Dada</th>
				<th>Valor indexat</th>
				<th>Valor mostrar</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${dades}" var="dada" varStatus="procesosStatus">
			<tr class="row_checkbox <c:if test='${dada.error}'>danger</c:if>">
				<td align="center">
					<c:if test="${dada.error}">
						<div class="pull-right">
							<span class="fa fa-warning text-danger" 
							title="${dada.errorDescripcio}"></span>
						</div>
					</c:if>					
				</td>
				<td align="center">
					<c:choose>
					    <c:when test="${dada.tipus == 'EX' }">
							<span class="label label-success" title="Propietat expedient">EX</span>
					    </c:when>    
					    <c:when test="${dada.tipus == 'TE' }">
							<span class="label label-info" title="Tipus Expedient">TE</span>
					    </c:when>    
					    <c:when test="${dada.tipus == 'DP' }">
							<span class="label label-warning" title='Definició de Procés "${dada.dadaIndexada.definicioProcesCodi }"'>DP</span>
					    </c:when>    
					    <c:otherwise>
							<span class="label label-default">${dada.tipus}</span>
					    </c:otherwise>
					</c:choose>	
				</td>
				<td>${dada.dadaIndexada.etiqueta}</td>
				<td>Agrupació</td>
				<td>${dada.dadaIndexada.campCodi}</td>
				<td>
					${dada.dadaIndexada.valorIndex}
				</td>
				<td>${dada.dadaIndexada.valorMostrar}</td>
			</tr>
			</c:forEach>			
		</tbody>
	</table>

</body>
</html>
