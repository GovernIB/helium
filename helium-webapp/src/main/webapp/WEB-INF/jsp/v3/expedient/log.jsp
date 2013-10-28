<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript" src="<c:url value="/js/jquery/ui/ui.core.js"/>"></script>
<script  type="text/javascript" src="<c:url value="/js/jquery/ui/jquery-ui-1.7.2.custom.js"/>"></script>

<script type="text/javascript">
	function confirmarRetrocedir(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm("<spring:message code="expedient.log.confirm.retrocedir"/>");
	}
</script>

<c:set var="numBloquejos" value="${0}"/>
<c:forEach var="log" items="${logs}">
	<c:if test="${log.estat == 'BLOCAR'}"><c:set var="numBloquejos" value="${numBloquejos + 1}"/></c:if>
</c:forEach>

<%-- <form action="<c:url value='/v3/expedient/${expedient.id}/registre'/>"> --%>
<!-- 	<div class="buttonHolder"> -->
<!-- 		<button type="submit" class="btn btn-primary dropdown-toggle">	 -->
<%-- 			<c:if test="${param.tipus_retroces == 0}"> --%>
<%-- 				<spring:message code="expedient.log.tipus.tasca"/> --%>
<%-- 			</c:if> --%>
<%-- 			<c:if test="${param.tipus_retroces != 0}"> --%>
<!-- 				<input type="hidden" id="tipus_retroces" name="tipus_retroces" value="0"/> -->
<%-- 				<spring:message code="expedient.log.tipus.detall"/> --%>
<%-- 			</c:if> --%>
<!-- 		</button> -->
<!-- 	</div> -->
<%-- </form> --%>

<c:choose>
	<c:when test="${not empty logs}">
		<table class="table table-bordered">
			<thead>
				<tr>
					<th><spring:message code="expedient.document.data"/></th>
					<th><spring:message code="expedient.editar.responsable"/></th>						
					<th><spring:message code="expedient.log.objecte"/></th>
					<th><spring:message code="expedient.log.accio"/></th>
					<th><spring:message code="expedient.log.info"/></th>
					<th><spring:message code="expedient.lot.token"/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="log" items="${logs}">
					<tr>
						<c:set var="cellStyle" value=""/>
						<c:if test="${log.estat == 'RETROCEDIT' or log.estat == 'RETROCEDIT_TASQUES'}">
							<c:set var="cellStyle" value="text-decoration:line-through"/>
						</c:if>
						<td style="${cellStyle}">
							<fmt:formatDate value="${log.data}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate>
						</td>
						<td style="${cellStyle}">
							${log.usuari}
						</td>
						<td style="${cellStyle}">
							<c:choose>
								<c:when test="${log.targetTasca}"><spring:message code="expedient.log.objecte.TASCA"/><c:if test="${param.tipus_retroces == 0}">: ${tasques[log.targetId].nom}</c:if></c:when>
								<c:when test="${log.targetProces}"><spring:message code="expedient.log.objecte.PROCES"/>: ${log.targetId}</c:when>
								<c:when test="${log.targetExpedient}"><spring:message code="expedient.log.objecte.EXPEDIENT"/></c:when>
								<c:otherwise>???: ${log.targetId}</c:otherwise>
							</c:choose>
						</td>
						<td style="${cellStyle}">
							<c:choose>
								<c:when test="${log.targetTasca and param.tipus_retroces != 0}">
									${tasques[log.targetId].nom}
									<span class="right">
										<c:import url="../utils/modalDefinir.jsp">
											<c:param name="sAjaxSource" value="/helium/v3/expedient/logAccionsTasca?id=${expedient.id}&targetId=${log.targetId}"/>
											<c:param name="modalId" value="logAccionsTasca"/>
											<c:param name="refrescarAlertes" value="true"/>
											<c:param name="refrescarPagina" value="false"/>							
											<c:param name="refrescarTaula" value="false"/>							
											<c:param name="refrescarTaulaId" value="false"/>
											<c:param name="icon" value=""/>
											<c:param name="img" value="/img/magnifier.png"/>
											<c:param name="texto" value=""/>
										</c:import>
									</span>
								</c:when>
								<c:otherwise>
									<spring:message code="expedient.log.accio.${log.accioTipus}"/>
								</c:otherwise>
							</c:choose>
						</td>
						<td style="${cellStyle}">
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
								<c:when test="${log.accioTipus == 'EXPEDIENT_ATURAR'}"><spring:message code="expedient.log.info.missatges"/>: ${log.accioParams}</c:when>
								<c:when test="${log.accioTipus == 'EXPEDIENT_ACCIO'}"><spring:message code="expedient.log.info.accio"/>: ${log.accioParams}</c:when>
								<c:when test="${log.accioTipus == 'EXPEDIENT_RETROCEDIR' or log.accioTipus == 'EXPEDIENT_RETROCEDIR_TASQUES'}">
									<c:import url="../utils/modalDefinir.jsp">
										<c:param name="sAjaxSource" value="/helium/v3/expedient/logRetrocedit?id=${expedient.id}&logId=${log.id}"/>
										<c:param name="modalId" value="logRetrocedit"/>
										<c:param name="refrescarAlertes" value="true"/>
										<c:param name="refrescarPagina" value="false"/>							
										<c:param name="refrescarTaula" value="false"/>							
										<c:param name="refrescarTaulaId" value="false"/>
										<c:param name="icon" value=""/>
										<c:param name="img" value="/img/magnifier.png"/>
										<c:param name="texto" value=""/>
									</c:import>								
								</c:when>
								<c:otherwise></c:otherwise>
							</c:choose>
						</td>
						<td style="${cellStyle}">
							${log.tokenName}
						</td>
						<td style="${cellStyle}">
							<c:if test="${log.estat == 'NORMAL' && numBloquejos == 0}">
								<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="128,16">
									<a href="<c:url value="/v3/expedient/retrocedir">
									<c:param name="id" value="${expedient.id}"/>
									<c:param name="logId" value="${log.id}"/>
									<c:param name="tipus_retroces" value="${param.tipus_retroces}"/>
									<c:param name="retorn" value="r"/>
									</c:url>" onclick="return confirmarRetrocedir(event)" class="retroces"><img src="<c:url value="/img/arrow_undo.png"/>" alt="<spring:message code="expedient.log.retrocedir"/>" title="<spring:message code="expedient.log.retrocedir"/>" border="0"/></a>
								</security:accesscontrollist>
							</c:if>
							<c:if test="${numBloquejos gt 0}">B</c:if>
						</td>
						<c:if test="${log.estat == 'BLOCAR'}">Hola<c:set var="numBloquejos" value="${numBloquejos - 1}"/></c:if>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="well well-small">No hi ha logs per a mostrar</div>
	</c:otherwise>
</c:choose>
