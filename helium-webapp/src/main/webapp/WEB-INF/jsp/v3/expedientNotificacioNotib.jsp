<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${not empty notificacions}">
		<table id="notificacions_${expedient.id}" class="table tableNotificacions table-bordered">
			<thead>
				<tr>
					<th><spring:message code="expedient.notificacio.data_enviament"/></th>
					<th><spring:message code="expedient.notificacio.concepte"/></th>
					<th><spring:message code="expedient.notificacio.estat.notificacio"/></th>
					<th><spring:message code="expedient.notificacio.estat.enviament"/></th>
					<th><spring:message code="expedient.notificacio.titular"/></th>
					<th><spring:message code="expedient.notificacio.destinatari"/></th>
					<th><spring:message code="expedient.notificacio.document"/></th>
					<th><spring:message code="expedient.notificacio.justificant"/></th>
<!-- 					<th></th> -->
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${notificacions}" var="notificacio">
					<tr>
						<td><fmt:formatDate value="${notificacio.enviatData}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate></td>
					    <td>${notificacio.concepte}</td>
						<td>
							<span title="<spring:message code="notificacio.etst.enum.${notificacio.estat}.info"/>">
								<c:choose>
								<c:when test="${notificacio.estat == 'PENDENT'}">
									<span class="fa fa-clock-o"></span>
								</c:when>
								<c:when test="${notificacio.estat == 'ENVIADA'}">
									<span class="fa fa-send-o"></span>
									<span class="label label-warning"></span>
								</c:when>
								<c:when test="${notificacio.estat == 'REGISTRADA'}">
									<span class="fa fa-file-o"></span>
								</c:when>
								<c:when test="${notificacio.estat == 'FINALITZADA'}">
									<span class="fa fa-check"></span>
								</c:when>							
								<c:when test="${notificacio.estat == 'PROCESSADA'}">
									<span class="fa fa-check-circle"></span>
								</c:when>
								</c:choose>
								<spring:message code="notificacio.etst.enum.${notificacio.estat}"/>
							</span>
						</td>
						<td>
							<c:if test="${notificacio.error }">
								<span class="fa fa-warning text-danger" title="${notificacio.errorDescripcio}"></span>
							</c:if>
							<c:if test="${not empty notificacio.enviaments[0].estat}">
								<spring:message code="notificacio.enviament.estat.enum.${notificacio.enviaments[0].estat}"/>
							</c:if>
							<c:if test="${notificacio.enviaments[0].estatData != null}">
								<br/><span class="text-muted small">
										<fmt:formatDate value="${notificacio.enviaments[0].estatData}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate>
									</span>
							</c:if>
						</td>
						<td>${notificacio.enviaments[0].titular.dni} - ${notificacio.enviaments[0].titular.nomSencer}</td>
						<td>
							<c:if test="${not empty notificacio.enviaments[0].destinataris[0]}">
								${notificacio.enviaments[0].destinataris[0].dni} - ${notificacio.enviaments[0].destinataris[0].nomSencer}
							</c:if>
						</td>
						<td>
							<a href="<c:url value="/v3/expedient/${expedient.id}/proces/${expedient.processInstanceId}/document/${notificacio.documentId}/descarregar"/>">
								${notificacio.documentArxiuNom}
								<span class="fa fa-download fa-lg" title="Descarregar document"></span>
							</a>
						</td>
						<td>
							<c:if test="${not empty notificacio.justificantId}">
								<a href="<c:url value="/v3/expedient/${expedient.id}/proces/${expedient.processInstanceId}/document/${notificacio.justificantId}/descarregar"/>">
									${notificacio.justificantArxiuNom}
									<span class="fa fa-download fa-lg" title="Descarregar document"></span>
								</a>
							</c:if>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.notificacio.expedient.cap' /></div>
	</c:otherwise>
</c:choose>
<script type="text/javascript">
	// <![CDATA[

	//]]>
	</script>
