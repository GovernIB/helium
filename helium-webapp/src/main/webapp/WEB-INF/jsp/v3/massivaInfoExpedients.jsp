<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%-- <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%> --%>
<%-- <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%> --%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%-- <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%> --%>
<table class="table table-bordered" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0">
	<thead>
		<tr>
			<th><spring:message code="expedient.llistat.columna.expedient"/></th>
			<th><spring:message code="expedient.llistat.columna.tipus"/></th>
			<th><spring:message code="expedient.llistat.columna.iniciat"/></th>
			<th><spring:message code="expedient.llistat.columna.estat"/></th>
		</tr>
	</thead>
	<c:forEach var="expedient" items="${expedients}">
		<tbody>
			<tr>
				<td>${expedient.identificadorLimitat}</td>
				<td>${expedient.tipus.nom}</td>
				<td><fmt:formatDate value="${expedient.dataInici}" pattern="dd/MM/yyyy HH:mm"/></td>
				<td>${expedient.estatNom}</td>
			</tr>
		</tbody>
	</c:forEach>				       		
</table>