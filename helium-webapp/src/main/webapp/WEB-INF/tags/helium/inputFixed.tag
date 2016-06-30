<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<c:if test="${empty labelSize}"><c:set var="labelSize" value="${4}"/></c:if>
<c:set var="campClassRequired"><c:if test="${required}">obligatori</c:if></c:set>
<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
	<label class="control-label col-xs-${labelSize} ${campClassRequired}" for="${campPath}">
		<c:choose>
			<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
			<c:when test="${not empty text}">${text}</c:when>
			<c:otherwise>${campPath}</c:otherwise>
		</c:choose>
	</label>
	<div class="controls col-xs-${12 - labelSize}">
		<p class="form-control-static"><jsp:doBody/></p>
	</div>
</div>