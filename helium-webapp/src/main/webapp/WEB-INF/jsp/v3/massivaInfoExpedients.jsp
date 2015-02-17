<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<table class="table table-striped table-bordered" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0">
	<thead>
		<tr>
			<th><spring:message code="expedient.llistat.columna.expedient"/></th>
			<th><spring:message code="expedient.llistat.columna.tipus"/></th>
			<th><spring:message code="expedient.llistat.columna.iniciat"/></th>
			<th><spring:message code="expedient.llistat.columna.estat"/></th>
		</tr>
	</thead>
	<tbody>
	<c:forEach var="expedient" items="${expedients}">
		<tr>
			<td>${expedient.identificadorLimitat}</td>
			<td>${expedient.tipus.nom}</td>
			<td><fmt:formatDate value="${expedient.dataInici}" pattern="dd/MM/yyyy HH:mm"/></td>
			<td>${expedient.estatNom}</td>
		</tr>
	</c:forEach>	
	</tbody>			       		
</table>