<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>


<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>

<c:choose>
	<c:when test="${not empty definicioProces}">		
	
		<table id="recursos-datatable"
				data-botons-template="#tableButtonsVariableTemplate" 
			   	class="table table-bordered">
			<thead>
				<tr>
					<th><spring:message code="definico.proces.recurs.columna.nom"></spring:message></th>
					<th><spring:message code="definico.proces.recurs.columna.contingut"></spring:message></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${recursos}" var="recurs">
					<tr>
						<td>${recurs}</td>
						<td style="text-align: center;" width="50px">
							<a href='<c:url value="/v3/definicioProces/${jbpmKey}/${definicioProcesId}/recurs/descarregar"><c:param name="nom" value="${recurs}" /></c:url>' >
								<span class="btn btn-default btn-file no-left-radius"
									  title='<spring:message code="definico.proces.recurs.descarregar"></spring:message>'>
									<span class="fa fa-download"></span>
								</span>
							</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		
		<a id="descarregar_par" class="btn btn-primary" href='<c:url value="/v3/definicioProces/${jbpmKey}/${definicioProcesId}/recurs/par"></c:url>' style="margin-left: 10px;"><span class="fa fa-arrow-down"></span>&nbsp;<spring:message code="definico.proces.recurs.descarregar.par"/></a>
		
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='definicio.proces.detall.cap'/></div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
// <![CDATA[            

$(document).ready(function() {		
	$('#recursos-datatable').DataTable({
		paging:   false,
		language: {
			url: webutilContextPath() + '/js/datatables/i18n/datatables.' + '${idioma}' + '.json'
		}, 
		preDrawCallback : function() {
			$('#recursos-datatable_filter').append($('#descarregar_par'));
		}
	});
});

// ]]>
</script>			
