<c:set var="token" value="${dada}"/>
<c:if test="${index == 0}">
<table id="token_${token.id}" class="table tableTokens table-bordered">
	<thead>
		<tr>
			<th><spring:message code="expedient.token.id"/></th>
			<th><spring:message code="expedient.token.nom"/></th>
			<th><spring:message code="expedient.token.pare"/></th>
			<th><spring:message code="expedient.token.root"/></th>
			<th><spring:message code="expedient.token.node"/></th>
			<th><spring:message code="expedient.token.entrada"/></th>
			<th><spring:message code="expedient.token.creat"/></th>
			<th><spring:message code="expedient.token.finalitzat"/></th>
			<th><spring:message code="expedient.token.flags"/></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
</c:if>
		<tr>
		    <td>${token.id}</td>
			<td>${token.fullName}</td>
			<td>${token.parentTokenFullName}</td>
			<td>
				<c:choose>
					<c:when test="${token.root}">Si</c:when>
					<c:otherwise>No</c:otherwise>
				</c:choose>
			</td>
			<td>${token.nodeName}</td>
			<td>
				<fmt:formatDate value="${token.nodeEnter}" pattern="dd/MM/yyyy HH:mm"/>
			</td>
			<td>
				<fmt:formatDate value="${token.start}" pattern="dd/MM/yyyy HH:mm"/>
			</td>
			<td>
				<fmt:formatDate value="${token.end}" pattern="dd/MM/yyyy HH:mm"/>
			</td>
			<td>
				<c:if test="${token.ableToReactivateParent}">R</c:if>
				<c:if test="${token.terminationImplicit}">F</c:if>
				<c:if test="${token.suspended}">S</c:if>
			</td>
			<td class="token_options">
				<c:if test="${expedient.permisTokenManage}">
					<c:choose>
						<c:when test="${empty token.end}">
							<a  class="icon retroces" 
								data-rdt-link-confirm="<spring:message code="expedient.token.confirmar.retrocedir" />"
								data-rdt-link-modal="true" 
								data-rdt-link-modal-min-height="180"
								href='<c:url value="../../v3/expedient/${expedientId}/proces/${procesId}/token/${token.id}/retrocedir"/>' 
								data-rdt-link-callback="recargarPanelToken(${procesId});">
								<i class="fa fa-reply" alt="<spring:message code="expedient.token.accio.retrocedir"/>" title="<spring:message code="expedient.token.accio.retrocedir"/>" border="0"/>
							</a>	
							<a  class="icon" 
								data-rdt-link-confirm="<spring:message code="expedient.token.confirmar.desactivar" />"
								data-rdt-link-ajax=true
								href='<c:url value="../../v3/expedient/${expedientId}/proces/${procesId}/token/${token.id}/activar"/>' 
								data-rdt-link-callback="recargarPanelToken(${procesId});">
								<i class="fa fa-pause" alt="<spring:message code="expedient.token.accio.desactivar"/>" title="<spring:message code="expedient.token.accio.desactivar"/>" border="0"/>
							</a>	
						</c:when>
						<c:otherwise>
							<a  class="icon" 
								data-rdt-link-confirm="<spring:message code="expedient.token.confirmar.activar" />"
								data-rdt-link-ajax=true
								href='<c:url value="../../v3/expedient/${expedientId}/proces/${procesId}/token/${token.id}/activar"/>' 
								data-rdt-link-callback="recargarPanelToken(${procesId});">
								<i class="fa fa-play" alt="<spring:message code="expedient.token.accio.activar"/>" title="<spring:message code="expedient.token.accio.activar"/>" border="0"/>
							</a>
						</c:otherwise>
					</c:choose>
				</c:if>
			</td>
		</tr>
		
<c:if test="${index == (paramCount-1)}">
	</tbody>
</table>
</c:if>