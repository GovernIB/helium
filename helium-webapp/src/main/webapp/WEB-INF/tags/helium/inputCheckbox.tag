<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="comment" required="false" rtexprvalue="true"%>
<%@ attribute name="info" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="labelOffset" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="readonly" required="false" rtexprvalue="true"%>
<%@ attribute name="checkLabelClass" required="false" rtexprvalue="true"%>
<%@ attribute name="labelClass" required="false" rtexprvalue="true"%>
<c:if test="${empty labelSize}"><c:set var="labelSize" value="${4}"/></c:if>
<c:if test="${empty labelOffset}"><c:set var="labelOffset" value="${0}"/></c:if>
<c:set var="inputSize" value="${12 - labelSize - labelOffset}"/>

<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:choose>
	<c:when test="${not inline}">
<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if> inputCheckbox">
	<label class="control-label col-xs-${labelSize} col-xs-offset-${labelOffset} checkbox ${labelClass}" for="${campPath}">
		<c:choose>
			<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
			<c:when test="${not empty text}">${text}</c:when>
			<c:otherwise>${campPath}</c:otherwise>
		</c:choose>
		<c:if test="${not empty info}">
			<span class="fa fa-info-circle text-info checkbox_hiddenInfo" data-placement="auto" title="<spring:message code="${info}"/>"></span>
		</c:if>		
	</label>
	<div class="controls col-xs-${inputSize}">
		<div class="checkbox">
  			<label class="${checkLabelClass}">
				<form:checkbox path="${campPath}" cssClass="span12" id="${campPath}" readonly="${readonly}" disabled="${disabled}"/><i></i>
			</label>
		</div>
		<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
	</div>
	<c:if test="${not empty comment}">
		<p class="comment col-xs-${inputSize} col-xs-offset-${labelOffset}"><spring:message code="${comment}"/></p>
	</c:if>
</div>
	</c:when>
	<c:otherwise>
		<label class="checkbox inputCheckbox ${checkLabelClass}">
			<form:checkbox path="${campPath}" id="${campPath}" readonly="${readonly}" disabled="${disabled}"/><i></i>
			<c:choose>
				<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
				<c:when test="${not empty text}">${text}</c:when>
				<c:otherwise>${campPath}</c:otherwise>
			</c:choose>
			<c:if test="${not empty info}">
				<span class="fa fa-info-circle text-info checkbox_hiddenInfo" data-placement="auto" title="<spring:message code="${info}"/>"></span>
			</c:if>
		</label>
	</c:otherwise>
</c:choose>
