<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<script type="text/javascript">
// <![CDATA[
function confirmarFinalitzar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='common.tram.confirmacio' />");
}
// ]]>
</script>
<c:choose>
	<c:when test="${not empty tasca.camps and not tasca.validada}">
		<c:set var="tramitacioClass" value="missatgesVermell"/>
		<c:set var="tramitacioOnSubmit" value="alert('<fmt:message key='common.tram.form_no_validat' />');return false"/>
	</c:when>
	<c:when test="${not empty tasca.documents and tasca.documentsNotReadOnly and not tasca.documentsComplet}">
		<c:set var="tramitacioClass" value="missatgesVermell"/>
		<c:set var="tramitacioOnSubmit" value="alert('<fmt:message key='common.tram.docs_sense_adj' />');return false"/>
	</c:when>
	<c:when test="${not empty tasca.signatures and not tasca.signaturesComplet}">
		<c:set var="tramitacioClass" value="missatgesVermell"/>
		<c:set var="tramitacioOnSubmit" value="alert('<fmt:message key='common.tram.docs_sense_sign' />');return false"/>
	</c:when>
	<c:otherwise>
		<c:set var="tramitacioClass" value="missatgesGris"/>
		<c:set var="tramitacioOnSubmit" value="return confirmarFinalitzar(event)"/>
	</c:otherwise>
</c:choose>
<c:if test="${not tasca.delegada or not tasca.delegacioOriginal}">
	<div class="missatgesGrisFort">
		<h4 class="titol-missatge"><fmt:message key='common.tram.finalitzar' /></h4>
		<c:set var="outcomes"><c:forEach var="outcome" items="${tasca.outcomes}" varStatus="status"><c:choose><c:when test="${not empty outcome}">${outcome}</c:when><c:otherwise><fmt:message key='common.tram.outcome.finalitzar' /></c:otherwise></c:choose><c:if test="${not status.last}">,</c:if></c:forEach></c:set>
		<form action="completar.html" method="post" class="uniForm" onsubmit="${tramitacioOnSubmit}">
			<input type="hidden" name="id" value="${tasca.id}"/>
			<c:if test="${not empty param.pipella}"><input type="hidden" name="pipella" value="${param.pipella}"/></c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">${outcomes}</c:param>
				<c:param name="titles">${outcomes}</c:param>
			</c:import>
		</form>
	</div>
</c:if>