<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ attribute name="name" required="true" rtexprvalue="true"%>
<%@ attribute name="required" required="false" rtexprvalue="true"%>
<%@ attribute name="text" required="false" rtexprvalue="true"%>
<%@ attribute name="textKey" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholder" required="false" rtexprvalue="true"%>
<%@ attribute name="placeholderKey" required="false" rtexprvalue="true"%>
<%@ attribute name="urlConsultaLlistat" required="true" rtexprvalue="true"%>
<%@ attribute name="urlConsultaInicial" required="true" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:choose>
	<c:when test="${not empty placeholderKey}"><c:set var="placeholderText"><spring:message code="${placeholderKey}"/></c:set></c:when>
	<c:otherwise><c:set var="placeholderText" value="${placeholder}"/></c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not inline}">
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<label class="control-label col-xs-4" for="${campPath}">
				<c:choose>
					<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
					<c:when test="${not empty text}">${text}</c:when>
					<c:otherwise>${campPath}</c:otherwise>
				</c:choose>
				<c:if test="${required}">*</c:if>
			</label>
			<div class="controls col-xs-8">
				<form:input path="${campPath}" cssClass="form-control" id="${campPath}" disabled="${disabled}" styleClass="width: 100%"/>
				<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<form:input path="${campPath}" cssClass="form-control" id="${campPath}" disabled="${disabled}"/> 
	</c:otherwise>
</c:choose>
<script>
$(document).ready(function() {
	$("#${campPath}").select2({
	    minimumInputLength: 3,
	    width: '100%',
	    placeholder: '${placeholderText}',
	    allowClear: true,
	    ajax: {
	        url: function (value) {
	        	return "${urlConsultaLlistat}/" + value;
	        },
	        dataType: 'json',
	        results: function (data, page) {
	        	var results = [];
	        	for (var i = 0; i < data.length; i++) {
	        		results.push({id: data[i].codi, text: data[i].nom});
	        	}
	            return {results: results};
	        }
	    },
	    initSelection: function(element, callback) {
	    	if ($(element).val()) {
		    	$.ajax("${urlConsultaInicial}/" + $(element).val(), {
	                dataType: "json"
	            }).done(function(data) {
	            	callback({id: data.codi, text: data.nom});
	            });
	    	}
	    },
	}).on('select2-open', function() {
		var iframe = $('.modal-body iframe', window.parent.document);
		var height = $('html').height() + $(".select2-drop").height() - 60;
		iframe.height(height + 'px');
	}).on('select2-close', function() {
		var iframe = $('.modal-body iframe', window.parent.document);
		var height = $('html').height();
		iframe.height(height + 'px');
	});
});
</script>
