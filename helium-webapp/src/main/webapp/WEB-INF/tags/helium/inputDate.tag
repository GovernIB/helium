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
<c:set var="campClassRequired"><c:if test="${required}">obligatori</c:if></c:set>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose></c:set>
<c:set var="campPlaceholder"><c:choose><c:when test="${not empty placeholderKey}"><spring:message code="${placeholderKey}"/></c:when><c:otherwise>${placeholder}</c:otherwise></c:choose></c:set>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:choose>
	<c:when test="${not inline}">
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<label class="control-label col-xs-4 ${campClassRequired}" for="${campPath}">${campLabelText}</label>
			<div class="col-xs-8">
				<div class="input-group" style="width:100%">
					<form:input path="${campPath}" cssClass="form-control datepicker" id="${campPath}" disabled="${disabled}"/>
					<span class="input-group-addon" style="width:1%"><span class="fa fa-calendar"></span></span>
				</div>
				<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<label class="sr-only ${campClassRequired}" for="${campPath}">${campLabelText}</label>
			<div class="input-group">
				<form:input path="${campPath}" cssClass="form-control datepicker" id="${campPath}" placeholder="${campPlaceholder}" disabled="${disabled}"/>
				<span class="input-group-addon" style="width:auto"><span class="fa fa-calendar"></span></span>
			</div>
		</div>
	</c:otherwise>
</c:choose>
<script>$("#${campPath}").mask("99/99/9999");</script>
<script>
$(document).ready(function() {
	$('.datepicker').datepicker({
		format: 'dd/mm/yyyy',
		weekStart: 1,
		autoclose: true,
		language: '${idioma}'
	}).on('show', function() {
		var iframe = $('.modal-body iframe', window.parent.document);
		var height = $('html').height() + 190;
		iframe.height(height + 'px');
	}).on('hide', function() {
		var iframe = $('.modal-body iframe', window.parent.document);
		var height = $('html').height();
		iframe.height(height + 'px');
	});
});
</script>