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
<%@ attribute name="labelSize" required="false" rtexprvalue="true"%>
<%@ attribute name="fileName" required="true" rtexprvalue="true"%>
<%@ attribute name="fileExists" required="true" rtexprvalue="true"%>
<%@ attribute name="fileUrl" required="false" rtexprvalue="true"%>
<c:if test="${empty labelSize}"><c:set var="labelSize" value="${4}"/></c:if>
<c:set var="campPath" value="${name}"/>
<c:set var="campClassRequired"><c:if test="${required}">obligatori</c:if></c:set>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>
<c:set var="campLabelText"><c:choose><c:when test="${not empty textKey}"><spring:message code="${textKey}"/></c:when><c:when test="${not empty text}">${text}</c:when><c:otherwise>${campPath}</c:otherwise></c:choose></c:set>
<c:set var="campPlaceholder"><c:choose><c:when test="${not empty placeholderKey}"><spring:message code="${placeholderKey}"/></c:when><c:otherwise>${placeholder}</c:otherwise></c:choose></c:set>

<style type="text/css">
	.btn-file {position: relative; overflow: hidden;}
	.btn-file-mid {position: relative; overflow: hidden; border-radius: 0px;background-color: #eee;}
	.btn-file input[type=file] {position: absolute; top: 0; right: 0; min-width: 100%; min-height: 100%; font-size: 100px; text-align: right; filter: alpha(opacity = 0); opacity: 0; outline: none; background: white; cursor: inherit; display: block;}
	.no-left-radius{border-top-left-radius: 0px; border-top-right-radius: 4px; border-bottom-right-radius: 4px; border-bottom-left-radius: 0px;}
</style>

<input id="${campPath}_deleted" name="${campPath}_deleted" type="hidden" value="false"/>

<c:choose>
	<c:when test="${not inline}">
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
			<label class="control-label col-xs-${labelSize} ${campClassRequired}" for="${campPath}">${campLabelText}</label>
			<div class="col-xs-${12 - labelSize}">
	            <div class="input-group" id ="${campPath}Group">
	            	<form:input path="${fileName}" cssClass="form-control" />
	                <c:choose>
						<c:when test="${not fileExists}">
			                <span class="input-group-btn">
			                    <span class="btn btn-default btn-file">
			                        <spring:message code='comu.arxiu' />&hellip; <input type="file" id="${campPath}_multipartFile" name="${campPath}_multipartFile">
			                    </span>
			                </span>
			            </c:when>
						<c:otherwise>
							<span class="input-group-btn" id="btnTrash" title="Eliminar document">
								<span id="esborrarContingut_${campPath}" class="btn btn-default btn-file-mid">
									<span class="fa fa-trash"></span>
								</span>
							</span>
							<span class="input-group-btn" id="btnDownload" title="Descarregar">
								<a href="<c:url value="${fileUrl}" />" >
									<span class="btn btn-default btn-file no-left-radius">
										<span class="fa fa-file"></span>
									</span>
								</a>
							</span>
						</c:otherwise>
					</c:choose>
	            </div>
				<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
    		<label class="sr-only ${campClassRequired}" for="${campPath}">${campLabelText}</label>
    		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
				<div class="form-control" data-trigger="fileinput"><i class="glyphicon glyphicon-file fileinput-exists"></i> <span class="fileinput-filename"></span></div>
				<span class="input-group-addon btn btn-default btn-file" style="width:auto"><span class="fileinput-new">Seleccionar</span><span class="fileinput-exists">Canviar</span>
				<input type="file" id="${campPath}_multipartFile" name="${campPath}_multipartFile"></span>
				<a href="#" class="input-group-addon btn btn-default fileinput-exists" style="width:auto" data-dismiss="fileinput">Netejar</a>
			</div>
  		</div>
	</c:otherwise>
</c:choose>


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
		$('#${fileName}').on('click', function() {
			$('input[name=${campPath}_multipartFile]').click();
		});
		$('#esborrarContingut_${campPath}').on('click', function() {
			$('#${campPath}_deleted').val(true);
			$('#${fileName}').val('');
			$('#btnTrash').remove();
			$('#btnDownload').remove();
			$('#${campPath}Group').append('<span class="input-group-btn">' +
                    '<span class="btn btn-default btn-file">' +
                       '<spring:message code='comu.arxiu' />&hellip; <input type="file" id="${campPath}_multipartFile" name="${campPath}_multipartFile">' +
                   '</span>' +
               '</span>');
			
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
	}); 
	// ]]>
</script>	