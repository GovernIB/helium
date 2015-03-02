<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<table class="table table-bordered" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0">
	<thead>
		<tr>
			<th><spring:message code="tasca.llistat.columna.titol"/></th>
			<th><spring:message code="tasca.llistat.columna.expedient"/></th>
			<th><spring:message code="tasca.llistat.columna.tipexp"/></th>
			<th><spring:message code="tasca.llistat.columna.creada"/></th>
			<th><spring:message code="tasca.llistat.columna.limit"/></th>
		</tr>
	</thead>
	<c:forEach var="tasca" items="${tasques}">
		<tbody>
			<tr>
				<td>
					${tasca.titol}
					<c:if test="${not tasca.agafada && not empty tasca.responsables}">
						<span class="fa fa-users" title="<spring:message code="enum.tasca.etiqueta.grup"/>"></span>
					</c:if>
					<div class="pull-right">
						<c:if test="${tasca.cancelled}">
							<span class="label label-danger" title="<spring:message code="enum.tasca.etiqueta.CA"/>">CA</span>
						</c:if>
						<c:if test="${tasca.suspended}">
							<span class="label label-info" title="<spring:message code="enum.tasca.etiqueta.SU"/>">SU</span>
						</c:if>
						<c:if test="${tasca.open}">
							<span class="label label-warning" title="<spring:message code="enum.tasca.etiqueta.PD"/>">PD</span>
						</c:if>
						<c:if test="${tasca.completed}">
							<span class="label label-success" title="<spring:message code="enum.tasca.etiqueta.FI"/>">FI</span>
						</c:if>
						<c:if test="${tasca.agafada}">
							<span class="label label-default" title="<spring:message code="enum.tasca.etiqueta.AG"/>">AG</span>
						</c:if>
						<c:if test="${not tasca.completed and tasca.tascaTramitacioMassiva}">
							<span class="label label-default" title="<spring:message code="tasca.llistat.accio.tramitar_massivament"/>"><i class="fa fa-files-o"></i></span>
						</c:if>
					</div>
				</td>
				<td>${tasca.expedientIdentificador}</td>
				<td>${tasca.expedientTipusNom}</td>
				<td><fmt:formatDate value="${tasca.createTime}" pattern="dd/MM/yyyy HH:mm"/></td>
				<td><fmt:formatDate value="${tasca.dueDate}" pattern="dd/MM/yyyy"/></td>								
			</tr>
		</tbody>
	</c:forEach>				       		
</table>