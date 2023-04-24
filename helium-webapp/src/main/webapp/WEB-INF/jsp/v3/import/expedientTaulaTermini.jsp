<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="termini" value="${dada}"/>
<c:if test="${index == 0}">
<table id="termini_${termini.id}" class="table tableTerminis table-bordered">
	<thead>
		<tr>
			<th><spring:message code="expedient.termini.nom"/></th>
			<th><spring:message code="expedient.termini.durada"/></th>
			<th><spring:message code="expedient.termini.iniciat.el"/></th>
			<th><spring:message code="expedient.termini.aturat.el"/></th>
			<th><spring:message code="expedient.termini.data.de.fi"/></th>
			<th><spring:message code="expedient.termini.estat"/></th>
			<c:if test="${expedient.permisTermManagement}">
				<th></th>
			</c:if>
		</tr>
	</thead>
	<tbody>
</c:if>
<c:if test="${termini.visible}">
		<c:set var="iniciat" value=""/>
		<c:forEach var="ini" items="${iniciats_termini}">
			<c:if test="${termini.id == ini.termini.id and empty ini.dataCancelacio}">
				<c:set var="iniciat" value="${ini}"/>
			</c:if>
		</c:forEach>
		<tr>
			<td><span class="<c:if test="${termini.obligatori}">obligatori</c:if>">${termini.nom}</span></td>
			<td>
				<c:choose>
					<c:when test="${not empty iniciat}">
						${iniciat.durada}
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${termini.duradaPredefinida}">
								${termini.durada}
							</c:when>
							<c:otherwise>
								Sense especificar
							</c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</td>
			<td>
				<c:if test="${not empty iniciat}"><fmt:formatDate value="${iniciat.dataInici}" pattern="dd/MM/yyyy"/></c:if>
			</td>
			<td>
				<c:if test="${not empty iniciat and not empty iniciat.dataAturada}"><fmt:formatDate value="${iniciat.dataAturada}" pattern="dd/MM/yyyy"/></c:if>
			</td>
			<td>
				<c:if test="${not empty iniciat}"><fmt:formatDate value="${iniciat.dataFi}" pattern="dd/MM/yyyy"/></c:if>
			</td>
			<td>
				<c:choose>
					<c:when test="${empty iniciat}">
						<c:set var="trobat" value="${false}"/>
						<c:forEach var="ini" items="${iniciats_termini}">
							<c:if test="${termini.id == ini.termini.id and not empty ini.dataCancelacio}">
								<spring:message code="expedient.termini.estat.cancelat"/>
								<c:set var="trobat" value="${true}"/>
							</c:if>
						</c:forEach>
						<c:if test="${not trobat}"><spring:message code="expedient.termini.estat.pendent"/></c:if>
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${not empty iniciat.dataAturada}"><spring:message code="expedient.termini.estat.aturat"/></c:when>
							<c:otherwise><spring:message code="expedient.termini.estat.actiu"/></c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>
			</td>
			<c:if test="${expedient.permisTermManagement}">
				<td class="termini_options">
					<c:if test="${termini.editable}">
					<c:choose>
						<c:when test="${not termini.manual or not empty iniciat}">
							<c:choose>
								<c:when test="${empty iniciat}">
									<i class="fa fa-play" alt="<spring:message code="expedient.termini.accio.iniciar"/>" title="<spring:message code="expedient.termini.accio.iniciar"/>" border="0"/>
								</c:when>
								<c:when test="${not termini.manual or (not empty iniciat and empty iniciat.dataAturada)}">
									<i class="fa fa-play" alt="<spring:message code="expedient.termini.accio.iniciar"/>" title="<spring:message code="expedient.termini.accio.iniciar"/>" border="0"/>
								</c:when>
								<c:otherwise>
									<a  class="icon" 
										data-rdt-link-confirm="<spring:message code="expedient.termini.confirmar.continuar" />"
										data-rdt-link-ajax=true
										href='<c:url value="../../v3/expedient/${expedientId}/proces/${procesId}/termini/${iniciat.id}/reprendre"/>' 
										data-rdt-link-callback="recargarPanelTermini(${procesId});">
										<i class="fa fa-play" alt="<spring:message code="expedient.termini.accio.continuar"/>" title="<spring:message code="expedient.termini.accio.continuar"/>" border="0"/>
									</a>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<a  class="icon" 
								data-rdt-link-confirm="<spring:message code="expedient.termini.confirmar.iniciar" />"
								data-rdt-link-ajax=true
								href='<c:url value="../../v3/expedient/${expedientId}/proces/${procesId}/termini/${termini.id}/iniciar"/>' 
								data-rdt-link-callback="recargarPanelTermini(${procesId});">
								<i class="fa fa-play" alt="<spring:message code="expedient.termini.accio.iniciar"/>" title="<spring:message code="expedient.termini.accio.iniciar"/>" border="0"/>
							</a>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${empty iniciat}">
							<i class="fa fa-pause" alt="<spring:message code="expedient.termini.accio.aturar"/>" title="<spring:message code="expedient.termini.accio.aturar"/>" border="0"/>
						</c:when>
						<c:when test="${not termini.manual or (not empty iniciat and not empty iniciat.dataAturada)}">
							<i class="fa fa-pause" alt="<spring:message code="expedient.termini.accio.aturar"/>" title="<spring:message code="expedient.termini.accio.aturar"/>" border="0"/>
						</c:when>
						<c:otherwise>
							<a  class="icon" 
								data-rdt-link-confirm="<spring:message code="expedient.termini.confirmar.aturar" />"
								data-rdt-link-ajax=true
								href='<c:url value="../../v3/expedient/${expedientId}/proces/${procesId}/termini/${iniciat.id}/suspendre"/>' 
								data-rdt-link-callback="recargarPanelTermini(${procesId});">
								<i class="fa fa-pause" alt="<spring:message code="expedient.termini.accio.aturar"/>" title="<spring:message code="expedient.termini.accio.aturar"/>" border="0"/>
							</a>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${empty iniciat}">
							<i class="fa fa-stop" alt="<spring:message code="expedient.termini.accio.cancelar"/>" title="<spring:message code="expedient.termini.accio.cancelar"/>" border="0"/>
						</c:when>
						<c:otherwise>
							<a  class="icon" 
								data-rdt-link-confirm="<spring:message code='expedient.termini.confirmar.cancelar' />"
								data-rdt-link-ajax=true
								href='<c:url value="../../v3/expedient/${expedientId}/proces/${procesId}/termini/${iniciat.id}/cancelar"/>' 
								data-rdt-link-callback="recargarPanelTermini(${procesId});">
								<i class="fa fa-stop" alt="<spring:message code="expedient.termini.accio.cancelar"/>" title="<spring:message code="expedient.termini.accio.cancelar"/>" border="0"/>
							</a>
							<a class="icon" 
								data-rdt-link-callback="recargarPanelTermini(${procesId});" 
								data-rdt-link-modal="true" 
								href="<c:url value="../../v3/expedient/${expedientId}/proces/${procesId}/termini/${iniciat.id}/modificar"/>"><i class="fa fa-pencil-square-o" alt="<spring:message code="expedient.termini.accio.modificar"/>" title="<spring:message code="expedient.termini.accio.modificar"/>" border="0"/></a>
						</c:otherwise>
					</c:choose>
					</c:if>
					<c:if test="${!termini.editable}">
						<span class="fa fa-lock pull-right term-bloquejat" style="font-size: 24px; color: #AAAAAA;" title="Termini bloquejat en aquest estat"></span>
					</c:if>
				</td>
			</c:if>
		</tr>
</c:if>
<c:if test="${index == (paramCount-1)}">
	</tbody>
</table>
</c:if>