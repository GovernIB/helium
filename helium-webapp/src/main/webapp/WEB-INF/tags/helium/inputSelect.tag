<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholder" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholderKey" required="false" rtexprvalue="true"%>
<%@ attribute name="optionItems" required="false" rtexprvalue="true" type="java.lang.Object"%>
<%@ attribute name="optionValueAttribute" required="false" rtexprvalue="true"%>
<%@ attribute name="optionTextAttribute" required="false" rtexprvalue="true"%>
<%@ attribute name="optionTextKeyAttribute" required="false" rtexprvalue="true"%>
<%@ attribute name="optionNivellAttribute" required="false" rtexprvalue="true"%>
<%@ attribute name="emptyOption" required="false" rtexprvalue="true"%>
<%@ attribute name="emptyOptionText" required="false" rtexprvalue="true"%>
<%@ attribute name="emptyOptionTextKey" required="false" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="readonly" required="false" rtexprvalue="true"%>
<%@ attribute name="comment" required="false" rtexprvalue="true"%>
<%@ attribute name="info" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="multiple" required="false" rtexprvalue="true"%>

<c:if test="${empty labelSize}"><c:set var="labelSize" value="${4}"/></c:if>
<c:set var="campPath" value="${name}"/>
<c:set var="campClassRequired"><c:if test="${required}">obligatori</c:if></c:set>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose></c:set>
<c:set var="campPlaceholder"><c:choose><c:when test="${not empty placeholderKey}"><spring:message code="${placeholderKey}"/></c:when><c:otherwise>${placeholder}</c:otherwise></c:choose></c:set>
<c:choose>
	<c:when test="${not inline}">
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<label class="control-label col-xs-${labelSize} ${campClassRequired} hiddenInfoContainer" for="${campPath}">${campLabelText}
				<c:if test="${not empty info}">
					<span class="fa fa-info-circle text-info hiddenInfo" data-placement="auto" title="<spring:message code="${info}"/>"></span>
				</c:if>
			</label>		
			<div class="controls col-xs-${12 - labelSize}">
				<form:select path="${campPath}" cssClass="form-control" id="${campPath}" disabled="${disabled}" multiple="${multiple}">
					<c:if test="${emptyOption == 'true'}">
						<c:choose>
							<c:when test="${not empty emptyOptionTextKey}"><option value=""><spring:message code="${emptyOptionTextKey}"/></option></c:when>
							<c:when test="${not empty emptyOptionText}"><option value="">${emptyOptionText}</option></c:when>
							<c:otherwise><option value=""></option></c:otherwise>
						</c:choose>
					</c:if>
					<c:choose>
						<c:when test="${not empty optionItems}">
							<c:forEach var="opt" items="${optionItems}">
								<c:set var="nivellTxt"><c:if test="${not empty optionNivellAttribute}"><c:forEach begin="${0}" end="${(opt[optionNivellAttribute])}" varStatus="status"><c:if test="${status.index >= 1}">&nbsp;&nbsp;&nbsp;&nbsp;</c:if></c:forEach></c:if></c:set>
								<c:choose>
									<c:when test="${not empty optionValueAttribute}">
										<c:choose>
											<c:when test="${not empty optionTextAttribute}"><form:option value="${opt[optionValueAttribute]}">${nivellTxt}${opt[optionTextAttribute]}</form:option></c:when>
											<c:when test="${not empty optionTextKeyAttribute}"><form:option value="${opt[optionValueAttribute]}">${nivellTxt}<spring:message code="${opt[optionTextKeyAttribute]}"/></form:option></c:when>
											<c:otherwise><form:option value="${opt[optionValueAttribute]}"/></c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise><form:option value="${opt}"/></c:otherwise>
								</c:choose>
							</c:forEach>
						</c:when>
						<c:otherwise><form:options/></c:otherwise>
					</c:choose>
				</form:select>
				<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
				<c:if test="${not empty comment}">
					<p class="comment"><spring:message code="${comment}"/></p>
				</c:if>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if> mb-0">
    		<label class="sr-only ${campClassRequired}" for="${campPath}">${campLabelText}</label>
			<form:select path="${campPath}" cssClass="form-control" id="${campPath}" disabled="${disabled}">
				<c:if test="${emptyOption == 'true'}">
					<c:choose>
						<c:when test="${not empty emptyOptionTextKey}"><option value=""><spring:message code="${emptyOptionTextKey}"/></option></c:when>
						<c:when test="${not empty emptyOptionText}"><option value="">${emptyOptionText}</option></c:when>
						<c:otherwise><option value=""></option></c:otherwise>
					</c:choose>
				</c:if>
				<c:choose>
					<c:when test="${not empty optionItems}">
						<c:forEach var="opt" items="${optionItems}">
							<c:set var="nivellTxt"><c:if test="${not empty optionNivellAttribute}"><c:forEach begin="${0}" end="${(opt[optionNivellAttribute])}" varStatus="status"><c:if test="${status.index >= 1}">&nbsp;&nbsp;&nbsp;&nbsp;</c:if></c:forEach></c:if></c:set>
							<c:choose>
								<c:when test="${not empty optionValueAttribute}">
									<c:choose>
										<c:when test="${not empty optionTextAttribute}"><form:option value="${opt[optionValueAttribute]}">${nivellTxt}${opt[optionTextAttribute]}</form:option></c:when>
										<c:when test="${not empty optionTextKeyAttribute}"><form:option value="${opt[optionValueAttribute]}">${nivellTxt}<spring:message code="${opt[optionTextKeyAttribute]}"/></form:option></c:when>
										<c:otherwise><form:option value="${opt[optionValueAttribute]}"/></c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise><form:option value="${opt}"/></c:otherwise>
							</c:choose>
						</c:forEach>
					</c:when>
					<c:otherwise><form:options/></c:otherwise>
				</c:choose>
			</form:select>
		</div>
	</c:otherwise>
</c:choose>
<script>
$(document).ready(function() {
	$("#${campPath}").select2({
	    width: 'resolve',
	    theme: "bootstrap",
	    placeholder: "${campPlaceholder}",
	    allowClear: true,
	    minimumResultsForSearch: 10
	});
	$("#${campPath}").on('select2-open', function() {
		/* comentat perquè feia que la pàgina es reposicionés adalt del tot
		var iframe = $('.modal-body iframe', window.parent.document);
		var height = $('html').height() + 30;
		iframe.height(height + 'px');
		*/
	});
	$("#${campPath}").on('select2-close', function() {
		/* comentat perquè feia que la pàgina es reposicionés adalt del tot
		var iframe = $('.modal-body iframe', window.parent.document);
		var height = $('html').height();
		iframe.height(height + 'px');
		*/
	});
});
</script>
