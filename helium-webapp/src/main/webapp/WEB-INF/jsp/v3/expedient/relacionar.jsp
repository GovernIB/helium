<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<c:import url="../common/formIncludes.jsp"/>
<jsp:include page="../import/helforms.jsp" />

<form:form id="relacionarExpediente" name="relacionarExpedient" action="${expedientId}/relacionarExpediente" method="post" commandName="relacionarCommand" cssClass="uniForm">
	<div class="inlineLabels">
		<div class="control-group">
			<label class="control-label"><spring:message code='expedient.info.relacionar.amb'/></label>
			<div class="controls">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="expedientIdDesti"/>
					<c:param name="type" value="suggest"/>
					<c:param name="label"></c:param>
					<c:param name="suggestUrl"><c:url value="${expedientId}/expedient/suggest"/></c:param>
				</c:import>
			</div>
		</div>
	</div>
	<div id="botons" class="well">
		<button type="submit" class="btn btn-primary">
			<spring:message code="expedient.info.relacionar"/>
		</button>
	</div>
</form:form>

<c:if test="${not empty relacionats}">
	<br/>
	<h4 class="titol-missatge"><spring:message code="expedient.info.relacionats"/></h4>
	<c:set var="relacionats" value="${relacionats}" scope="request"/>
	
	<table class="table table-bordered">
		<thead>
			<tr>
				<th>Expedient</th>
				<th>Iniciat el</th>						
				<th>Tipus</th>
				<th>Estat</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="expedient" items="${relacionats}">
				<tr>
					<c:if test="${expedient.anulat}"><c:set var="filaStyle" value="text-decoration:line-through"/></c:if>
					<td style="${filaStyle}">
						${expedient.identificador}
					</td>
					<td style="${filaStyle}">
						<fmt:formatDate value="${expedient.dataInici}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate>
					</td>
					<td style="${filaStyle}">
						${expedient.tipus.nom}
					</td>
				<td style="${filaStyle}">
					<c:if test="${expedient.aturat}"><img src="<c:url value="/img/stop.png"/>" alt="Aturat" title="Aturat" border="0"/></c:if>
					<c:choose>
						<c:when test="${empty expedient.dataFi}">
							<c:choose><c:when test="${empty expedient.estat}"><spring:message code='expedient.consulta.iniciat' /></c:when><c:otherwise>${expedient.estat.nom}</c:otherwise></c:choose>
						</c:when>
						<c:otherwise><spring:message code='expedient.consulta.finalitzat' /></c:otherwise>
					</c:choose>
				</td>				
				<td style="${filaStyle}">
					<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,8">
						<form method="POST" id="formRelacioDelete" name="formRelacioDelete" action="${expedientId}/relacioDelete">
							<input type="hidden" id="expedientIdOrigen" name="expedientIdOrigen" value="${expedientId}"/>
							<input type="hidden" id="expedientIdDesti" name="expedientIdDesti" value="${expedient.id}"/>
							
							<img onclick="return confirmarEsborrarRelacio(event)" src="<c:url value="/img/cross.png"/>" alt="<spring:message code='comuns.esborrar' />" title="<spring:message code='comuns.esborrar' />" border="0" style="cursor:pointer;"/>
						</form>
					</security:accesscontrollist>
				</td>
			</c:forEach>
		</tbody>
	</table>
</c:if>

<script type="text/javascript">
// <![CDATA[
function confirmarEsborrarRelacio(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	if (confirm("<spring:message code='expedient.info.confirm.relacio.esborrar'/>")) {
		$('#formRelacioDelete').submit();
	}
}
// ]]>
</script>