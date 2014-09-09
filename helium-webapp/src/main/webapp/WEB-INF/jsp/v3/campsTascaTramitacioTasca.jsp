<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:choose>
	<c:when test="${not empty documents and not tasca.documentsComplet}">
		<c:set var="missatgeAlertaFinalitzar"><spring:message code="common.tram.docs_sense_adj"/></c:set>
	</c:when>
	<c:otherwise>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-primary" name="submit" value="finalitzar">
				<spring:message code='common.tram.outcome.finalitzar' />
			</button>
		</div>
	</c:otherwise>
</c:choose>

<c:if test="${false}">
<!-- 
<c:choose>
	<c:when test="${not empty documents and tasca.documentsNotReadOnly and not tasca.documentsComplet}">
		<c:set var="missatgeAlertaFinalitzar"><spring:message code="common.tram.docs_sense_adj"/></c:set>
	</c:when>
	<c:when test="${not empty tasca.signatures and not tasca.signaturesComplet}">
		<c:set var="missatgeAlertaFinalitzar"><spring:message code="common.tram.docs_sense_sign"/></c:set>
	</c:when>
</c:choose>

<c:choose>
	<c:when test="${not tasca.validada}">
		<script type="text/javascript">
			// <![CDATA[
			function confirmarFinalitzar(e) {
				var e = e || window.event;
				e.cancelBubble = true;
				if (e.stopPropagation) e.stopPropagation();
				<c:choose>
					<c:when test="${not empty missatgeAlertaFinalitzar}">
						alert('${missatgeAlertaFinalitzar}');
					</c:when>
					<c:otherwise>
						if (confirm("<spring:message code="common.tram.confirmacio"/>")) {
							$('#formFinalitzar').attr('action','${expedientId}/tasca/${tasca.id}/form');
							$('#command button[name=submit]').remove();
							$('#command').append("<input type=\"hidden\" name=\"submitar\" value=\"validate\"/>");
							$('#command').submit();
						}
					</c:otherwise>
				</c:choose>
				return false;
			}
			<c:if test="${empty missatgeAlertaFinalitzar}">
				$(document).ready(function() {
					$('#formFinalitzar button').click(function() {
						$('#helFinalitzarAmbOutcome').val(this.value);
					});
				});
			</c:if>
			// ]]>
		</script>
	</c:when>
	<c:otherwise>
<script type="text/javascript">
// <![CDATA[
function confirmarFinalitzar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	<c:choose>
		<c:when test="${not empty missatgeAlertaFinalitzar}">
			alert('${missatgeAlertaFinalitzar}');
			return false;
		</c:when>
		<c:otherwise>
			return confirm("<spring:message code="common.tram.confirmacio"/>");
		</c:otherwise>
	</c:choose>
}
// ]]>
</script>
	</c:otherwise>
</c:choose>

<c:if test="${not tasca.delegada or not tasca.delegacioOriginal}">
	<c:set var="outcomes">
		<c:forEach var="outcome" items="${tasca.outcomes}" varStatus="status">
			<c:choose>
				<c:when test="${not empty outcome}">${outcome}</c:when>
				<c:otherwise>
					<spring:message code="common.tram.outcome.finalitzar"/>
				</c:otherwise>
			</c:choose>
			<c:if test="${not status.last}">,</c:if>
		</c:forEach>
	</c:set>
	<form onsubmit="return confirmarFinalitzar(event)" id="formFinalitzar" name="formFinalitzar" action="${expedientId}/tasca/${tasca.id}/completar" method="post">
		<input type="hidden" name="id" value="${tasca.id}"/>
		<input type="hidden" name="readOnly" value="${hiHaCampsReadOnly}"/>
		<input type="hidden" id="helFinalitzarAmbOutcome" name="helFinalitzarAmbOutcome" value="@@@"/>
		<div class="buttonHolder">
			<c:set var="buttonValues" value="${fn:split(outcomes,',')}"/>
			<c:set var="buttonTitles" value="${fn:split(outcomes,',')}"/>
			<c:forEach var="value" items="${buttonValues}" varStatus="status">
				<c:choose>
					<c:when test="${value == 'reset'}">
						<button type="reset" class="btn btn-primary" onclick="${param.onclick}">${buttonTitles[status.index]}</button>
					</c:when>
					<c:otherwise>
						<button type="submit" class="btn btn-primary" name="submit" value="${buttonValues[status.index]}" onclick="saveAction(this, '${buttonValues[status.index]}');$('#helFinalitzarAmbOutcome').val(this.value);">${buttonTitles[status.index]}</button></c:otherwise>
				</c:choose>
			</c:forEach>
		</div>
	</form>
</c:if>
 -->
 </c:if>