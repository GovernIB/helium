<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholder" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholderKey" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose><c:if test="${required}">*</c:if></c:set>
<c:set var="campPlaceholder"><c:choose><c:when test="${not empty placeholderKey}"><spring:message code="${placeholderKey}"/></c:when><c:otherwise>${placeholder}</c:otherwise></c:choose></c:set>
<c:choose>
	<c:when test="${not inline}">
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<label class="control-label col-xs-4" for="${campPath}">${campLabelText}</label>
			<div class="col-xs-8">
				<form:input path="${campPath}" cssClass="form-control" id="${campPath}" disabled="${disabled}"/>
				<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
    		<label class="sr-only" for="${campPath}">${campLabelText}</label>
    		<form:input path="${campPath}" cssClass="form-control" id="${campPath}" placeholder="${campPlaceholder}" disabled="${disabled}"/>
  		</div>
	</c:otherwise>
</c:choose>
