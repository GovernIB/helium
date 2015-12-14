<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="optionItems" required="false" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="optionValueAttribute" required="false" rtexprvalue="true"%>
<%@ attribute name="optionTextAttribute" required="false" rtexprvalue="true"%>
<%@ attribute name="optionTextKeyAttribute" required="false" rtexprvalue="true"%>
<%@ attribute name="emptyOption" required="false" rtexprvalue="true"%>
<%@ attribute name="emptyOptionText" required="false" rtexprvalue="true"%>
<%@ attribute name="emptyOptionTextKey" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:choose>
	<c:when test="${not inline}">
<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
	<label class="control-label col-xs-4" for="${campPath}">
		<c:choose>
			<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
			<c:when test="${not empty text}">${text}</c:when>
			<c:otherwise>${campPath}</c:otherwise>
		</c:choose>
	</label>
	<div class="controls col-xs-8">
		<div class="radio">
  			<label>
  				<form:radiobutton path="${campPath}" cssClass="span12" id="${campPath}"/>
  			</label>
		</div>
		<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
	</div>
</div>
	</c:when>
	<c:otherwise>
		<label class="checkbox">
			<form:radiobutton path="${campPath}" id="${campPath}"/>
			<c:choose>
				<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
				<c:when test="${not empty text}">${text}</c:when>
				<c:otherwise>${campPath}</c:otherwise>
			</c:choose>
		</label>
	</c:otherwise>
</c:choose>
