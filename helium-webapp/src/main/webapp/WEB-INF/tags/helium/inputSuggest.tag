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
<%@ attribute name="urlConsultaLlistat" required="true" rtexprvalue="true"%>
<%@ attribute name="urlConsultaInicial" required="true" rtexprvalue="true"%>
<%@ attribute name="inline" required="false" rtexprvalue="true"%>
<%@ attribute name="disabled" required="false" rtexprvalue="true"%>
<%@ attribute name="id" required="false" rtexprvalue="true"%>
<%@ attribute name="multiple" required="false" rtexprvalue="true"%>
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<c:if test="${empty labelSize}"><c:set var="labelSize" value="${4}"/></c:if>
<c:set var="campPath" value="${name}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campClassRequired"><c:if test="${required}">obligatori</c:if></c:set>
<c:choose>
	<c:when test="${not empty placeholderKey}"><c:set var="placeholderText"><spring:message code="${placeholderKey}"/></c:set></c:when>
	<c:otherwise><c:set var="placeholderText" value="${placeholder}"/></c:otherwise>
</c:choose>
<c:choose>
	<c:when test="${not inline}">
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<label class="control-label col-xs-${labelSize} ${campClassRequired}" for="${campPath}">
				<c:choose>
					<c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when>
					<c:when test="${not empty text}">${text}</c:when>
					<c:otherwise>${campPath}</c:otherwise>
				</c:choose>
			</label>
			<div class="controls col-xs-${12 - labelSize}">
				<form:input path="${campPath}" cssClass="form-control suggest" id="${campPath}" disabled="${disabled}" styleClass="width: 100%"  data-url-llistat="${urlConsultaLlistat}" data-url-inicial="${urlConsultaInicial}" />
      				<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<form:input path="${campPath}" cssClass="form-control suggest" id="${campPath}" disabled="${disabled}"  data-url-llistat="${urlConsultaLlistat}" data-url-inicial="${urlConsultaInicial}"/>
	</c:otherwise>
</c:choose>
<script type="text/javascript">
// <![CDATA[

var multiple = "true" == "${multiple == true}";

$(document).ready(function() {
	
	debugger;
	$("[id='${campPath}']").select2({
	    minimumInputLength: 3,
	    width: '100%',
	    placeholder: '${placeholderText}',
	    allowClear: true,
	    //<c:if test="${multiple == true}">
	    tags: true,
	    tokenSeparators: [','],
	    //</c:if>
	    ajax: {
	        url: function (value) {
	        	debugger;
	        	return $(this).data('urlLlistat') + "/" + value;
	        },
	        dataType: 'json',
	        results: function (data, page) {
	        	debugger;
	        	var results = [];
	        	for (var i = 0; i < data.length; i++) {
	        		results.push({id: data[i].codi, text: data[i].nom});
	        	}
	            return {results: results};
	        }
	    },
	    initSelection: function(element, callback) {
	    	if ($(element).val()) {
		    	$.ajax($(element).data('urlInicial') + "/" + $(element).val(), {
	                dataType: "json"
	            }).done(function(data) {
	            	var valors_inicials = [];
	     			if (data) {
	     				if (Array.isArray(data)) {
	     					for (i = 0; i < data.length; i++) {
		    	            	valors_inicials.push({id: data[i].codi, text: data[i].nom});
	     					}
	     				} else {
	    	            	valors_inicials.push({id: data.codi, text: data.nom});
	     				}
	     				if (multiple) {
	    	            	callback(valors_inicials);
	     				} else {
	    	            	callback(valors_inicials[0]);	     					
	     				}
	     			}
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

// ]]>
</script>	

</script>