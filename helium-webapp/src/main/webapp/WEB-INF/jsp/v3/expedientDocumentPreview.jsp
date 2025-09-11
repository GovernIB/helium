<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:set var="titol"><spring:message code="expedient.document.preview" arguments="${documentNom}"/></c:set>
<c:choose>
    <c:when test="${not empty processInstanceId}">
        <c:url var="downloadUrl" value="${pageContext.request.contextPath}/v3/expedient/${expedientId}/proces/${processInstanceId}/document/${documentStoreId}/descarregar" />
    </c:when>
    <c:otherwise>
        <c:url var="downloadUrl" value="${pageContext.request.contextPath}/v3/expedient/${expedientId}/document/${documentStoreId}/descarregar" />
    </c:otherwise>
</c:choose>


<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
<style type="text/css">
	.btn-file {position: relative; overflow: hidden;}
	.btn-file input[type=file] {position: absolute; top: 0; right: 0; min-width: 100%; min-height: 100%; font-size: 100px; text-align: right; filter: alpha(opacity = 0); opacity: 0; outline: none; background: white; cursor: inherit; display: block;}
	.col-xs-4 {width: 20%;}
	.col-xs-8 {width: 80%;}
	#s2id_estatId {width: 100% !important;}
	.titol-missatge {margin-left: 3px; padding-top: 10px; padding-bottom: 10px;}
	.titol-missatge label {padding-right: 10px;}
	.nav-tabs li.disabled a {pointer-events: none;}
	.tab-pane {min-height: 300px; margin-top: 25px;}
	.candau {color: #666666;}
	.select2-result-label:has(> span.candau) {cursor: not-allowed;}
</style>
<script type="text/javascript">
// <![CDATA[
	
	$(function() {
    var expedientId = '${expedientId}';
    var documentId = '${documentStoreId}';
    var processInstanceId = '${processInstanceId}';

    $.ajax({
        type: 'GET',
        url: '/helium/modal/v3/expedient/' + expedientId + '/document/' + documentId + '/returnFitxer',
        success: function(json) {
            if (json.error) {
                $('#viewer').html('<div class="alert alert-danger">' + json.errorMsg + '</div>');
            } else {
                // el PDF viene en base64
                var viewerUrl = 'data:application/pdf;base64,' + json.data.contingut;
                $('#docPreview').attr('data', viewerUrl);
                $('#docLink').attr('href', viewerUrl);
            }
        },
        error: function(xhr, ajaxOptions, thrownError) {
            $('#viewer').html('<div class="alert alert-danger">' + thrownError + '</div>');
        }
	    });
	});
	
	
// ]]>
</script>
</head>
<body>	
	<div id="viewer">
	    <object id="docPreview" type="application/pdf"  style="height: 90vh; width: 100vw;">
	        <p>No es pot mostrar la vista prèvia. <a id="docLink" href=${downloadUrl}>Descarregar</a></p>
	    </object>
	</div>	
</body>
</html>