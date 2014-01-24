<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
	<head>
		<title><spring:message code="expedient.log.accions.llistat"/></title>
		<c:import url="../utils/modalHead.jsp">
			<c:param name="titol" value="Accions de la tasca"/>
			<c:param name="buttonContainerId" value="botons"/>
		</c:import>
	</head>
	<body>
		<c:choose>
			<c:when test="${not empty logs}">
				<table id="table-accions-tascas" class="table table-bordered">
					<thead>
						<tr>
							<th><spring:message code="expedient.document.data"/></th>
							<th><spring:message code="expedient.editar.responsable"/></th>						
							<th><spring:message code="expedient.log.objecte"/></th>
							<th><spring:message code="expedient.log.accio"/></th>
							<th><spring:message code="expedient.log.info"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="log" items="${logs}">
							<tr>
								<td>
									<fmt:formatDate value="${log.data}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate>
								</td>
								<td>
									${log.usuari}
								</td>
								<td>
									<c:choose>
										<c:when test="${log.targetTasca}"><spring:message code="expedient.log.objecte.TASCA"/><c:if test="${param.tipus_retroces == 0}">: ${tasques[log.targetId].nom}</c:if></c:when>
										<c:when test="${log.targetProces}"><spring:message code="expedient.log.objecte.PROCES"/>: ${log.targetId}</c:when>
										<c:when test="${log.targetExpedient}"><spring:message code="expedient.log.objecte.EXPEDIENT"/></c:when>
										<c:otherwise>???: ${log.targetId}</c:otherwise>
									</c:choose>
								</td>
								<td>							
									<spring:message code="expedient.log.accio.${log.accioTipus}"/>
								</td>
								<td>
									<c:choose>
										<c:when test="${log.accioTipus == 'PROCES_VARIABLE_CREAR'}"><spring:message code="expedient.log.info.variable"/>: ${log.accioParams}</c:when>
										<c:when test="${log.accioTipus == 'PROCES_VARIABLE_MODIFICAR'}"><spring:message code="expedient.log.info.variable"/>: ${log.accioParams}</c:when>
										<c:when test="${log.accioTipus == 'PROCES_VARIABLE_ESBORRAR'}"><spring:message code="expedient.log.info.variable"/>: ${log.accioParams}</c:when>
										<c:when test="${log.accioTipus == 'PROCES_DOCUMENT_AFEGIR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
										<c:when test="${log.accioTipus == 'PROCES_DOCUMENT_MODIFICAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
										<c:when test="${log.accioTipus == 'PROCES_DOCUMENT_ESBORRAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
										<c:when test="${log.accioTipus == 'PROCES_DOCUMENT_ADJUNTAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
										<c:when test="${log.accioTipus == 'PROCES_SCRIPT_EXECUTAR'}"><spring:message code="expedient.log.info.accio"/>: ${log.accioParams}</c:when>
										<c:when test="${log.accioTipus == 'TASCA_REASSIGNAR'}"><spring:message code="expedient.log.info.abans"/>: ${fn:split(log.accioParams, "::")[0]}, <spring:message code="expedient.log.info.despres"/>: ${fn:split(log.accioParams, "::")[1]}</c:when>
										<c:when test="${log.accioTipus == 'TASCA_ACCIO_EXECUTAR'}"><spring:message code="expedient.log.info.accio"/>: ${log.accioParams}</c:when>
										<c:when test="${log.accioTipus == 'TASCA_DOCUMENT_AFEGIR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
										<c:when test="${log.accioTipus == 'TASCA_DOCUMENT_MODIFICAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
										<c:when test="${log.accioTipus == 'TASCA_DOCUMENT_ESBORRAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
										<c:when test="${log.accioTipus == 'TASCA_DOCUMENT_SIGNAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
										<c:when test="${log.accioTipus == 'TASCA_COMPLETAR'}"><c:if test="${not empty log.accioParams}"><spring:message code="expedient.log.info.opcio"/>: ${log.accioParams}</c:if></c:when>
										<c:when test="${log.accioTipus == 'EXPEDIENT_ATURAR'}"><spring:message code="expedient.log.info.missatge"/>: ${log.accioParams}</c:when>
										<c:when test="${log.accioTipus == 'EXPEDIENT_ACCIO'}"><spring:message code="expedient.log.info.accio"/>: ${log.accioParams}</c:when>
										<c:otherwise></c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<div class="well well-small">No hi ha logs per a mostrar</div>
			</c:otherwise>
		</c:choose>
		<div id="botons" class="well">
			<button type="button" class="btn btn-tancar"><spring:message code="comuns.cancelar"/></button>
		</div>
		<script type="text/javascript">
			window.parent.modalAjustarTamany(window.frameElement,$('html').height());
		</script>
	</body>
</html>