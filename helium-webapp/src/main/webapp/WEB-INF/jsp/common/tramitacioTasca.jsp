<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<c:choose>
	<c:when test="${not empty tasca.documents and tasca.documentsNotReadOnly and not tasca.documentsComplet}">
		<c:set var="missatgeAlertaFinalitzar"><fmt:message key="common.tram.docs_sense_adj"/></c:set>
	</c:when>
	<c:when test="${not empty tasca.signatures and not tasca.signaturesComplet}">
		<c:set var="missatgeAlertaFinalitzar"><fmt:message key="common.tram.docs_sense_sign"/></c:set>
	</c:when>
</c:choose>
<c:choose>
	<c:when test="${param.pipella == 'form' and not tasca.validada}">
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
						if (confirm("<fmt:message key="common.tram.confirmacio"/>")) {
							$('#formFinalitzar').attr('action','form.html');
							$('#command button[value=validate]').click();
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
			return confirm("<fmt:message key="common.tram.confirmacio"/>");
		</c:otherwise>
	</c:choose>
}
// ]]>
</script>
	</c:otherwise>
</c:choose>
<c:if test="${not tasca.delegada or not tasca.delegacioOriginal}">
	<div class="missatgesGrisFort">
		<h4 class="titol-missatge"><fmt:message key="common.tram.finalitzar"/></h4>
		<c:set var="outcomes"><c:forEach var="outcome" items="${tasca.outcomes}" varStatus="status"><c:choose><c:when test="${not empty outcome}">${outcome}</c:when><c:otherwise><fmt:message key="common.tram.outcome.finalitzar"/></c:otherwise></c:choose><c:if test="${not status.last}">,</c:if></c:forEach></c:set>
		<form id="formFinalitzar" action="completar.html" method="post" class="uniForm" onsubmit="return confirmarFinalitzar(event)">
			<input type="hidden" name="id" value="${tasca.id}"/>
			<input type="hidden" name="readOnly" value="${hiHaCampsReadOnly}"/>
			<input type="hidden" id="helFinalitzarAmbOutcome" name="helFinalitzarAmbOutcome" value="@@@"/>
			<c:if test="${not empty param.pipella}"><input type="hidden" name="pipella" value="${param.pipella}"/></c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">${outcomes}</c:param>
				<c:param name="titles">${outcomes}</c:param>
			</c:import>
		</form>
	</div>
</c:if>