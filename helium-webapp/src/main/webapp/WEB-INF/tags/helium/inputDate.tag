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
<%@ attribute name="comment" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<c:if test="${empty labelSize}"><c:set var="labelSize" value="${4}"/></c:if>
<c:if test="${empty labelSize}"><c:set var="emptyLabelSize" value=""/></c:if>
<c:set var="campClassRequired"><c:if test="${required}">obligatori</c:if></c:set>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose></c:set>
<c:set var="campPlaceholder"><c:choose><c:when test="${not empty placeholderKey}"><spring:message code="${placeholderKey}"/></c:when><c:otherwise>${placeholder}</c:otherwise></c:choose></c:set>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:choose>
	<c:when test="${not inline}">
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<label class="control-label col-xs-${labelSize} ${campClassRequired}" for="${campPath}">${campLabelText}</label>
			<div class="col-xs-${12 - labelSize}">
				<div class="input-group" style="width:100%">
					<div class="input-group date">
						<form:input path="${campPath}" cssClass="form-control datetimepicker date" data-format="dd/MM/yyyy" id="${campPath}" disabled="${disabled}"/>
						<span class="input-group-addon btn_date" style="width:1%"><span class="fa fa-calendar"></span></span>
					</div>
				</div>
				<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
				<c:if test="${not empty comment}">
					<p class="comment col-xs-${12 - emptyLabelSize} col-xs-offset-${emptyLabelSize}"><spring:message code="${comment}"/></p>
				</c:if>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<label class="sr-only ${campClassRequired}" for="${campPath}">${campLabelText}</label>
			<div class="input-group">
				<div class="input-group date">
					<form:input path="${campPath}" cssClass="form-control datetimepicker date" data-format="dd/MM/yyyy" id="${campPath}" placeholder="${campPlaceholder}" disabled="${disabled}"/>
					<span class="input-group-addon btn_date" style="width:auto"><span class="fa fa-calendar"></span></span>
				</div>
			</div>
			<c:if test="${not empty comment}">
				<p class="comment col-xs-${12 - emptyLabelSize} col-xs-offset-${emptyLabelSize}"><spring:message code="${comment}"/></p>
			</c:if>	
		</div>
	</c:otherwise>
</c:choose>
<script>$("#${campPath}").mask("99/99/9999");</script>
<script>
$(document).ready(function() {
	$('.datetimepicker').datetimepicker({
		locale: moment.locale('${idioma}'),
		format: 'DD/MM/YYYY'
    }).on('dp.show', function() {
		var iframe = $('.modal-body iframe', window.parent.document);
		var divField = $('.modal-body iframe', window.parent.document).contents().find('body>div');
		iframe.height((iframe.height() + 200) + 'px');
		divField.height((divField.height() + 200) + 'px');
	}).on('dp.hide', function() {
		var iframe = $('.modal-body iframe', window.parent.document);
		var divField = $('.modal-body iframe', window.parent.document).contents().find('body>div');
		var height = $('html').height();
		iframe.height((iframe.height() - 200) + 'px');
		divField.height((divField.height() - 200) + 'px');
	});
	$(".btn_date").click(function(){
		$(this).prev(".date").trigger("focus");
	});
});
</script>