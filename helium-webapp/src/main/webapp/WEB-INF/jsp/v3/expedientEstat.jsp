<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<!-- Contingut de la pipella "Estat" per expedients basats en estats. Mostra els logs de
tipus de canvi d'estat ordenat per dada. -->
<style>
#spinner {display: none;}
</style>

<script type="text/javascript">
//<![CDATA[
           $(document).ready(function(){
        	   	$('#btnDetall').click(function(){
        	   		$("#spinner").show();
        	   		$('#registre_estats').hide();
        	   		$('#contingut-estats').load('<c:url value="/nodeco/v3/expedient/${expedient.id}/estat?detall='+$('#detall').val()+'"/>');
        	   	});
        	   	$('[data-toggle="modal"]', $('#taulaLogs_${expedient.id}')).each(function() {
        			if (!$(this).attr('data-modal-eval')) {
        				$(this).webutilModal();
        				$(this).attr('data-modal-eval', 'true');
        			}
        		});
           });
//]]>
</script>

<c:if test="${expedient.permisLogRead}">
	<div style="padding-top: 5px;padding-bottom: 10px;">
		<div style="text-align: right;">
			<button id="btnDetall" type="button" class="btn btn-default">
					<c:choose>
						<c:when test="${detall == false}">
							<input type="hidden" id="detall" name="detall" value="true"/>
							<spring:message code="expedient.log.tipus.detall"/>
						</c:when>
						<c:otherwise>
							<input type="hidden" id="detall" name="detall" value="false"/>
							<spring:message code="expedient.log.tipus.estat"/>
						</c:otherwise>
					</c:choose>
			</button>
		</div>
	</div>
</c:if>

	<div id="spinner"><div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div></div>

	<div id="registre_estats">
		<c:if test="${detall == false }">
			<!-- Taula d'estats -->
			<table id="taulaEstats_${expedient.id}" class="table tableNotificacions table-bordered">
			<thead>
				<tr>		
					<th><spring:message code="expedient.estat.data"/></th>
					<th><spring:message code="expedient.estat.estat"/></th>
					<th><spring:message code="expedient.estat.responsable"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="log" items="${logs}">
				<tr id="tr_log_${log.id}">
					<td><fmt:formatDate value="${log.data}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate></td>
					<td>${log.accioParams}</td>
					<td>${log.usuari}</td>
				</tr>
				</c:forEach>
			</tbody>
			</table>	
		</c:if>
		<c:if test="${detall == true }">
			<!-- Taula detallada dels logs -->
			<c:set var="permisReg" value="${expedient.permisLogManage}" />
			
			<table id="taulaLogs_${expedient.id}" class="table tableLogs table-bordered">
				<thead>
					<tr>
						<th><spring:message code="expedient.document.data"/></th>
						<th><spring:message code="expedient.editar.responsable"/></th>
						<th><spring:message code="expedient.log.accio"/></th>
						<c:if test="${permisReg}">
							<th><spring:message code="expedient.log.info"/></th>
						</c:if>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="log" items="${logs}">
					<tr id="tr_log_${log.id}">
						<td>
							<fmt:formatDate value="${log.data}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate>
						</td>
						<td>
							${log.usuari}
						</td>
						<td>
							<spring:message code="expedient.log.accio.${log.accioTipus}" text="${log.accioTipus}"/>
						</td>
						<c:if test="${permisReg}">
							<td>
								<c:choose>
									<c:when test="${log.accioTipus == 'PROCES_LLAMAR_SUBPROCES'}"><spring:message code="expedient.log.info.nom"/>: ${registre.accioParams}</c:when>
									<c:when test="${log.accioTipus == 'PROCES_VARIABLE_CREAR'}"><spring:message code="expedient.log.info.variable"/>: ${log.accioParams}</c:when>
									<c:when test="${log.accioTipus == 'PROCES_VARIABLE_MODIFICAR'}"><spring:message code="expedient.log.info.variable"/>: ${log.accioParams}</c:when>
									<c:when test="${log.accioTipus == 'PROCES_VARIABLE_ESBORRAR'}"><spring:message code="expedient.log.info.variable"/>: ${log.accioParams}</c:when>
									<c:when test="${log.accioTipus == 'PROCES_DOCUMENT_AFEGIR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
									<c:when test="${log.accioTipus == 'PROCES_DOCUMENT_MODIFICAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
									<c:when test="${log.accioTipus == 'PROCES_DOCUMENT_ESBORRAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
									<c:when test="${log.accioTipus == 'PROCES_DOCUMENT_ADJUNTAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
									<c:when test="${log.accioTipus == 'PROCES_SCRIPT_EXECUTAR'}">
										<a data-toggle="modal" href="<c:url value="/v3/expedient/scriptForm/${log.id}"/>" class="icon a-modal-registre scriptLink_${log.id}"><i class="fa fa-search"></i></a>
									</c:when>
									<c:when test="${log.accioTipus == 'TASCA_REASSIGNAR'}"><spring:message code="expedient.log.info.abans"/>: ${fn:split(log.accioParams, "::")[0]}, <spring:message code="expedient.log.info.despres"/>: ${fn:split(log.accioParams, "::")[1]}</c:when>
									<c:when test="${log.accioTipus == 'TASCA_ACCIO_EXECUTAR'}"><spring:message code="expedient.log.info.accio"/>: ${log.accioParams}</c:when>
									<c:when test="${log.accioTipus == 'TASCA_DOCUMENT_AFEGIR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
									<c:when test="${log.accioTipus == 'TASCA_DOCUMENT_MODIFICAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
									<c:when test="${log.accioTipus == 'TASCA_DOCUMENT_ESBORRAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
									<c:when test="${log.accioTipus == 'TASCA_DOCUMENT_SIGNAR'}"><spring:message code="expedient.log.info.document"/>: ${log.accioParams}</c:when>
									<c:when test="${log.accioTipus == 'TASCA_COMPLETAR'}"><c:if test="${not empty log.accioParams}"><spring:message code="expedient.log.info.opcio"/>: ${log.accioParams}</c:if></c:when>
									<c:when test="${log.accioTipus == 'TASCA_MARCAR_FINALITZAR'}"><c:if test="${not empty log.accioParams}"><spring:message code="expedient.log.info.opcio"/>: ${log.accioParams}</c:if></c:when>
									<c:when test="${log.accioTipus == 'EXPEDIENT_ATURAR'}"><spring:message code="expedient.log.info.missatges"/>: ${log.accioParams}</c:when>
									<c:when test="${log.accioTipus == 'EXPEDIENT_ACCIO'}"><spring:message code="expedient.log.info.accio"/>: ${log.accioParams}</c:when>
									<c:when test="${log.accioTipus == 'ANOTACIO_RELACIONAR'}">
										${log.accioParams}
									</c:when>
									<c:otherwise>
										${log.accioParams}
									</c:otherwise>
								</c:choose>
							</td>
							<td>
								${log.tokenName}
							</td>
						</c:if>
					</tr>
					</c:forEach>
				</tbody>
			</table>		
		</c:if>

	</div>
	