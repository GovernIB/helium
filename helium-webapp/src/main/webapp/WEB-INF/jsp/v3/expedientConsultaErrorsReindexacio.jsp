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
<c:choose>
	<c:when test="${error != null }">
		 <span class="fa fa-exclamation-triangle text-danger"></span> &nbsp; ${error }
	</c:when>
	<c:otherwise>
		<spring:message code="comu.darrera.actualitzacio"></spring:message>: ${dataComprovacio}
		<br/>
		<table id="taulaExpedients" class="table table-striped table-bordered" data-rdt-seleccionable="true" data-rdt-seleccionable-columna="0">
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
					<td>
						<a href="<c:url value="/v3/expedient/${expedient.id}"/>" target="blank">${expedient.identificadorLimitat}</a>
						
							<c:if test="${expedient.reindexarData != null || expedient.reindexarError}">
								<div class="pull-right">
									<span class="fa fa-refresh ${expedient.reindexarError ? 'text-danger' : ''}" 
									title="<c:if test="${expedient.reindexarData != null}"><spring:message code="expedient.consulta.reindexacio.asincrona"/></c:if>
										   <c:if test="${expedient.reindexarError}"><spring:message code="expedient.consulta.reindexacio.error.full"/></c:if>"></span>
								</div>
							</c:if>
					</td>
					<td><fmt:formatDate value="${expedient.dataInici}" pattern="dd/MM/yyyy HH:mm"/></td>
					<td>${expedient.estatNom}</td>
				</tr>
			</c:forEach>	
			</tbody>			       		
		</table>
	</c:otherwise>
</c:choose>
