<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="expedient.document.adjuntar"/></title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/helium.tramitar.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
</script>
<style type="text/css">
	.btn-file {position: relative; overflow: hidden;}
	.btn-file input[type=file] {position: absolute; top: 0; right: 0; min-width: 100%; min-height: 100%; font-size: 100px; text-align: right; filter: alpha(opacity = 0); opacity: 0; outline: none; background: white; cursor: inherit; display: block;}
	.col-xs-4 {width: 7%;}		
	.col-xs-8 {width: 93%;}
	#s2id_estatId {width: 100% !important;}
</style>
</head>
<body>		
	<form:form cssClass="form-horizontal form-tasca" action="documentModificarMas" enctype="multipart/form-data" method="post" commandName="documentExpedientCommand">
		<div class="inlineLabels">
			<input id="inici" name="inici" value="${inici}" type="hidden"/>
			<input id="correu" name="correu" value="${correu}" type="hidden"/>
			<hel:inputText required="true" name="nom" textKey="expedient.document.titol" placeholderKey="expedient.document.titol"/>
			<div id="amagarFile" class="form-group">
				<label class="control-label col-xs-4 obligatori" for="nom"><spring:message code='expedient.document.arxiu' /></label>
		        <div class="col-xs-8 arxiu">					
		            <div class="input-group">
		                <form:input path="contingut" readonly="readonly" cssClass="form-control" />
		                <span class="input-group-btn">
		                    <span class="btn btn-default btn-file">
		                        <spring:message code='expedient.document.arxiu' />… <input type="file" name="arxiu">
		                    </span>
		                </span>
		            </div>
				</div>
        	</div>
			<script type="text/javascript">
				// <![CDATA[
				$(document).on('change', '.btn-file :file', function() {
					var input = $(this),
					numFiles = input.get(0).files ? input.get(0).files.length : 1,
					label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
					input.trigger('fileselect', [numFiles, label]);
				});
				
				$(document).ready( function() {
					$('.btn-file :file').on('fileselect', function(event, numFiles, label) {
						var input = $(this).parents('.input-group').find(':text'),
						log = numFiles > 1 ? numFiles + ' files selected' : label;
						if( input.length ) {
							input.val(log);
						} else {
							if( log )
								alert(log);
						}
					});
				}); 
				// ]]>
			</script>
			
			<hel:inputDate required="true" name="data" textKey="expedient.document.data" placeholder="dd/mm/yyyy"/>
		</div>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" style="float: none;" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
			<button class="btn btn-primary right" type="submit" style="float: none;" name="accio" value="document_adjuntar">
				<spring:message code='comuns.adjuntar' />
			</button>
		</div>
	</form:form>
</body>
</html>