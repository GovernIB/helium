<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="botons" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="optionItems" required="false" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="optionValueAttribute" required="false" rtexprvalue="true"%>
<%@ attribute name="optionTextAttribute" required="false" rtexprvalue="true"%>
<%@ attribute name="optionTextKeyAttribute" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<c:set var="campPath" value="${name}"/>
<spring:bind path="${campPath}"><c:set var="campValue" value="${status.value}"/></spring:bind>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelSize"><c:choose><c:when test="${not empty labelSize}">${labelSize}</c:when><c:otherwise>4</c:otherwise></c:choose></c:set>
<c:set var="campInputSize">${12 - campLabelSize}</c:set>
<c:choose>
	<c:when test="${not inline}">
<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
	<label class="control-label col-xs-${campLabelSize}" for="${campPath}">
		<c:choose>
			<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
			<c:when test="${not empty text}">${text}</c:when>
			<c:otherwise>${campPath}</c:otherwise>
		</c:choose>
			<c:if test="${required}">*</c:if>
	</label>
	<div class="controls col-xs-${campInputSize} btn-group" data-toggle="buttons">
		
		<c:forEach var="opt" items="${optionItems}">
			<c:set var="labelClass" value="checkbox-inline"/>
			<c:set var="labelClassActive" value=""/>
			<c:if test="${campValue == opt[optionValueAttribute]}"><c:set var="labelClassActive" value=" active"/></c:if>
			<c:if test="${not empty botons}"><c:set var="labelClass" value="btn btn-default${labelClassActive}"/></c:if>
			<label class="${labelClass}">
			<c:choose>
				<c:when test="${not empty optionValueAttribute}">
					<c:choose>
						<c:when test="${not empty optionTextAttribute}"><form:radiobutton path="${campPath}" value="${opt[optionValueAttribute]}"/> ${opt[optionTextAttribute]}</c:when>
						<c:when test="${not empty optionTextKeyAttribute}"><form:radiobutton path="${campPath}" value="${opt[optionValueAttribute]}"/> <spring:message code="${opt[optionTextKeyAttribute]}"/></c:when>
						<c:otherwise><form:radiobutton path="${campPath}" value="${opt[optionValueAttribute]}"/> ${opt[optionValueAttribute]}</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise><form:radiobutton path="${campPath}" value="${opt}"/> ${opt}</c:otherwise>
			</c:choose>
			</label>
		</c:forEach>
		<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
	</div>
</div>
	</c:when>
	<c:otherwise>
		<label class="sr-only">
			<c:choose>
				<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
				<c:when test="${not empty text}">${text}</c:when>
				<c:otherwise>${campPath}</c:otherwise>
			</c:choose>
			<c:if test="${required}">*</c:if>
		</label>
		<c:forEach var="opt" items="${optionItems}">
			<c:set var="labelClass" value="checkbox-inline"/>
			<c:set var="labelClassActive" value=""/>
			<c:if test="${campValue == opt[optionValueAttribute]}"><c:set var="labelClassActive" value=" active"/></c:if>
			<c:if test="${not empty botons}"><c:set var="labelClass" value="btn btn-default${labelClassActive}"/></c:if>
			<label class="${labelClass}">
			<c:choose>
				<c:when test="${not empty optionValueAttribute}">
					<c:choose>
						<c:when test="${not empty optionTextAttribute}"><form:radiobutton path="${campPath}" value="${opt[optionValueAttribute]}"/> ${opt[optionTextAttribute]}</c:when>
						<c:when test="${not empty optionTextKeyAttribute}"><form:radiobutton path="${campPath}" value="${opt[optionValueAttribute]}"/> <spring:message code="${opt[optionTextKeyAttribute]}"/></c:when>
						<c:otherwise><form:radiobutton path="${campPath}" value="${opt[optionValueAttribute]}"/> ${opt[optionValueAttribute]}</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise><form:radiobutton path="${campPath}" value="${opt}"/> ${opt}</c:otherwise>
			</c:choose>
			</label>
		</c:forEach>
	</c:otherwise>
</c:choose>
