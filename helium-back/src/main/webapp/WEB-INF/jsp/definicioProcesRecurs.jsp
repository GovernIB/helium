<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="idioma">ca</c:set>


<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>

<c:choose>
	<c:when test="${not empty definicioProces}">
		<table id="recursos-datatable"
				data-botons-template="#tableButtonsVariableTemplate"
			   	class="table table-bordered">
			<thead>
				<tr>
					<th><spring:message code="definico.proces.recurs.columna.nom"></spring:message></th>
<!--					<th><spring:message code="definico.proces.recurs.columna.contingut"></spring:message></th>-->
					<th width="50px" data-orderable="false"><spring:message code="comu.boto.accions"></spring:message></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${recursos}" var="recurs">
					<tr>
						<td>${recurs}</td>
						<td style="text-align: center;" width="50px">
							<c:if test="${recursosEsborrables.contains(recurs)}">
								<c:url var="esborrarUrl" value="/definicioProces/${jbpmKey}/${definicioProcesId}/recurs/delete">
									<c:param name="nom" value="${recurs}" />
								</c:url>
								<div class="dropdown navbar-right">
									<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
									<ul class="dropdown-menu">
										<li>
											<a href='<c:url value="/definicioProces/${jbpmKey}/${definicioProcesId}/recurs/descarregar"><c:param name="nom" value="${recurs}" /></c:url>' >
												<span class="fa fa-download"></span>&nbsp;<spring:message code="definico.proces.recurs.descarregar"/>
											</a>
										</li>
										<li>
											<a href="${esborrarUrl}" class="recurs-esborrar" data-confirm="<spring:message code="definico.proces.recurs.esborrar.confirmacio"/>">
												<span class="fa fa-trash-o"></span>&nbsp;<spring:message code="definico.proces.recurs.esborrar" />
											</a>
										</li>
									</ul>
								</div>
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<a id="nou_recurs" class="btn btn-default" href="<c:url value="/modal/definicioProces/${jbpmKey}/${definicioProcesId}/recurs/new"/>" data-toggle="modal" data-datatable-id="recursos-datatable" style="margin-left: 10px;"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.recurs.llistat.accio.add"/></a>
		<a id="descarregar_par" class="btn btn-primary" href='<c:url value="/definicioProces/${jbpmKey}/${definicioProcesId}/recurs/par"></c:url>' style="margin-left: 10px;"><span class="fa fa-arrow-down"></span>&nbsp;<spring:message code="definico.proces.recurs.descarregar.par"/></a>

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
			$('#recursos-datatable_filter').append($('#nou_recurs'));
		}
	});
	// Esborrat d'un recurs de la taula de recursos (sense data-toggle="ajax" perque
	// la taula no es una webutil datatable i no es pot refrescar, es recarrega la pagina).
	$('.recurs-esborrar').click(function(e) {
		e.preventDefault();
		var url = $(this).attr('href');
		$.get(url).always(function() {
			location.reload();
		});
	});
});

// Recarrega la pestanya en tancar el modal d'upload (la taula no es una
// webutil datatable i no es pot refrescar). Ha de ser global perque
// el modal l'invoca amb eval.
function callbackModalRecursos() {
	location.reload();
}

// ]]>
</script>
