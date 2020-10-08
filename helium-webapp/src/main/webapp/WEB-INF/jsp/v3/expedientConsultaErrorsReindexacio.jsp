<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!-- Taula d'expedients amb error de reindexació. Carregada per ajax a expedientConsultaAlertes -->
<br/>
<table class="table table-striped table-bordered" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0">
	<thead>
		<tr>
			<th><spring:message code="expedient.llistat.columna.expedient"/></th>
			<th><spring:message code="expedient.llistat.columna.iniciat"/></th>
			<th><spring:message code="expedient.llistat.columna.estat"/></th>
		</tr>
	</thead>
	<tbody>
	<c:forEach var="expedient" items="${expedients}">
		<tr>
			<td><a href="<c:url value="/v3/expedient/${expedient.id}"/>" target="blank">${expedient.identificadorLimitat}</a></td>
			<td><fmt:formatDate value="${expedient.dataInici}" pattern="dd/MM/yyyy HH:mm"/></td>
			<td>${expedient.estatNom}</td>
		</tr>
	</c:forEach>	
	</tbody>			       		
</table>
