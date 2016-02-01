<c:set var="log" value="${dada}"/>
<c:set var="permisReg" value="${expedient.permisAdministration || expedient.permisSupervision || tipus_retroces == 0}" />
<c:if test="${index == 0}">
<table id="log_${log.id}" class="table tableLogs table-bordered">
	<thead>
		<tr>
			<th><spring:message code="expedient.document.data"/></th>
			<th><spring:message code="expedient.editar.responsable"/></th>
			<c:if test="${permisReg}" >
				<th><spring:message code="expedient.log.objecte"/></th>
			</c:if>
			<th>
				<c:if test="${permisReg}">
					<spring:message code="expedient.log.accio"/>
				</c:if>
				<c:if test="${empty tipus_retroces || tipus_retroces == 0}">
					<spring:message code="expedient.log.objecte.TASCA"/>
				</c:if>
			</th>
			<c:if test="${permisReg}">
				<th><spring:message code="expedient.log.info"/></th>
				<th><spring:message code="expedient.lot.token"/></th>
			</c:if>
			<th></th>
		</tr>
	</thead>
	<tbody>
</c:if>
	<tr>
		<td>
			<fmt:formatDate value="${log.data}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate>
		</td>
		<td>
			${log.usuari}
		</td>
		<c:if test="${permisReg}">
			<td>
				<c:choose>
					<c:when test="${log.targetTasca}"><spring:message code="expedient.log.objecte.TASCA"/><c:if test="${tipus_retroces == 0}">: ${tasques[log.targetId].tascaNom}</c:if></c:when>
					<c:when test="${log.targetProces}"><spring:message code="expedient.log.objecte.PROCES"/>: ${log.targetId}</c:when>
					<c:when test="${log.targetExpedient}"><spring:message code="expedient.log.objecte.EXPEDIENT"/></c:when>
					<c:otherwise>???: ${log.targetId}</c:otherwise>
				</c:choose>
			</td>
		</c:if>
		<td>
			<c:choose>
				<c:when test="${log.targetTasca and tipus_retroces != 0}">
					${tasques[log.targetId].tascaNom}
					<span class="right">
						<a data-rdt-link-modal="true" class="icon a-modal-registre" href="<c:url value="../../v3/expedient/logAccionsTasca?id=${expedient.id}&targetId=${log.targetId}"/>" ><i  class="fa fa-search"></i></a>
					</span>
				</c:when>
				<c:otherwise>
					<spring:message code="expedient.log.accio.${log.accioTipus}"/>
				</c:otherwise>
			</c:choose>
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
						<a data-rdt-link-modal="true" href="../../v3/expedient/scriptForm/${log.id}" class="icon a-modal-registre scriptLink_${log.id}"><i class="fa fa-search"></i></a>
					</c:when>
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
						<a data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/logRetrocedit?id=${expedient.id}&logId=${log.id}"/>" class="icon a-modal-registre"><i class="fa fa-search"></i></a>
					</c:when>
					<c:otherwise></c:otherwise>
				</c:choose>
			</td>
			<td>
				${log.tokenName}
			</td>
		</c:if>
		<td>
			<c:choose>
				<c:when test="${log.accioTipus == 'PROCES_SCRIPT_EXECUTAR'}"></c:when>
				<c:when test="${log.accioTipus == 'PROCES_LLAMAR_SUBPROCES'}"></c:when>
				<c:when test="${log.estat == 'NORMAL' && numBloquejos == 0}">										
					<c:if test="${expedient.permisAdministration || expedient.permisSupervision}">
						<a  class="icon retroces" 
							data-rdt-link-confirm="<spring:message code='expedient.log.confirm.retrocedir'/>"
							data-rdt-link-ajax=true
							href='<c:url value="../../v3/expedient/retrocedir">
								<c:param name="id" value="${expedient.id}"/>
								<c:param name="logId" value="${log.id}"/>
								<c:param name="tipus_retroces" value="${tipus_retroces}"/>
								<c:param name="retorn" value="r"/>
								</c:url>' 
							data-rdt-link-callback="recargarPanelesLog(${tipus_retroces});">
							<i class="fa fa-reply" alt="<spring:message code="expedient.log.retrocedir"/>" title="<spring:message code="expedient.log.retrocedir"/>" border="0"/>
						</a>
					</c:if>
				</c:when>				
				<c:otherwise></c:otherwise>
			</c:choose>
			<c:if test="${numBloquejos gt 0}">B</c:if>
		</td>
		<c:if test="${log.estat == 'BLOCAR'}"><c:set var="numBloquejos" value="${numBloquejos - 1}"/></c:if>
	</tr>
<c:if test="${index == (paramCount-1)}">
	</tbody>
</table>
</c:if>