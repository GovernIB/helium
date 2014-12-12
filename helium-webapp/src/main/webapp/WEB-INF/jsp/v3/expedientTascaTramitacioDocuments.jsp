<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<style type="text/css">
	.btn-file {position: relative; overflow: hidden;}
	.btn-file input[type=file] {position: absolute; top: 0; right: 0; min-width: 100%; min-height: 100%; font-size: 100px; text-align: right; filter: alpha(opacity = 0); opacity: 0; outline: none; background: white; cursor: inherit; display: block;}
	.form-group {width: 100%;display: inline-flex;}
	.fila_reducida {width: 100%;}
	.col-xs-1 {width: auto;padding-left: 0px;}				
	.col-xs-4 {width: 20%;}		
	.col-xs-8 {width: 77%;}
	.col-xs-8 .form-group {margin-left: 0px;margin-right: 0px;}
	.col-xs-8 .form-group .col-xs-4 {padding-left: 0px;width: 15%;}
	.col-xs-8 .form-group .col-xs-8 {width: 85%;padding-left: 15px;padding-right: 0px;}
	.col-xs-11 {padding-left: 0px;}				
	#s2id_estatId {width: 100% !important;}
	.arxiu {margin-left: 0%; margin-top: 10px;}
	h4.titol-missatge {width: 100%;}
	h4.titol-missatge a{margin-left: 10px;}
	a.icon {margin-left: 10px !important;}
	.comentari {padding-top: 30px;}
	.comentari label {font-weight: bold;}
	.modal-botons {padding-bottom: 30px;}
	.form-horizontal .control-label {padding-top: 0px;}
	.obligatori {background-position: right 8px;}
</style>
<c:forEach var="document" items="${documents}">
	<div class="well well-small">
		<form class="form-horizontal form-tasca" action="documentAdjuntar" enctype="multipart/form-data" method="post">
			<input type="hidden" id="inici" name="inici" value="${command.inici}"/>
			<input type="hidden" id="correu" name="correu" value="${command.correu}"/>
			<input type="hidden" id="docId" name="docId" value="${document.id}"/>
			<div class="inlineLabels">
				<h4 class="titol-missatge">
					<label class="control-label col-xs-1 <c:if test="${document.required}">obligatori</c:if>">${document.documentNom}</label>
		 			<c:if test="${document.plantilla || not empty document.arxiuNom}"> 
		 				<a class="icon" href="
		 				<c:url value="documentGenerar">
		 					<c:param name="docId" value="${document.id}"/>
							<c:param name="inici" value="${command.inici}"/>
							<c:param name="correu" value="${command.correu}"/>
		 				</c:url>">
		 					<i class="fa fa-file-text-o"></i>
		 				</a>
		 			</c:if>
	 				<c:if test="${not empty document.tokenSignatura}">
						<c:url value="/v3/expedient/document/arxiuMostrar" var="downloadUrl"><c:param name="token" value="${document.tokenSignatura}"/></c:url>
						<a class="icon" id="downloadUrl${document.id}" href="${downloadUrl}">
							<i class="fa fa-download"></i>
						</a>
						<a class="icon" id="removeUrl${document.id}" href="
							<c:url value="documentEsborrar">
								<c:param name="docId" value="${document.id}"/>
								<c:param name="inici" value="${command.inici}"/>
								<c:param name="correu" value="${command.correu}"/>
							</c:url>"
							 onclick="return confirmarAmagarFile(event, '${document.id}')">
							 <i class="fa fa-times"></i>
						</a>
						<div id="hideData${document.id}" class="comentari small">
							<p><label>Arxiu</label>: ${document.arxiuNom}</p>
							<p><label>Adjuntat el</label>: <fmt:formatDate value="${document.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></p>
							<p><label>Data del document</label>: <fmt:formatDate value="${document.dataDocument}" pattern="dd/MM/yyyy"/></p>
						</div>
					</c:if>
				</h4>
				<div class="form-group">
					<div class="col-xs-11 arxiu">
			            <div id="amagarFile${document.id}" class="input-group <c:if test="${not empty document.tokenSignatura}">hide</c:if>">
			                <span class="input-group-btn">
			                    <span class="btn btn-primary btn-file">
			                        <spring:message code='expedient.document.arxiu' />… <input type="file" name="arxiu">
			                    </span>
			                </span>
			               <input id="contingut" name="contingut" readonly="readonly" class="form-control" />
			            </div>
					</div>
				</div>
				<div id="div_timer" class="control-group left control-group-mid <c:if test="${not empty document.tokenSignatura}">hide</c:if>"">
			    	<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
						<label class="control-label col-xs-1" for="data${document.id}">Data del document</label>
						<div class="input-group">
							<input class="form-control datepicker" id="data" name="data"/>
							<span class="input-group-addon" style="width:auto"><span class="fa fa-calendar"></span></span>
						</div>
					</div>
		            <script type="text/javascript">
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
				</div>
			</div>
			<div id="modal-botons${document.id}" class="modal-botons <c:if test="${not empty document.tokenSignatura}">hide</c:if>">
				<button class="pull-right btn btn-primary right" type="submit" name="accio" value="document_guardar">
					<spring:message code='comuns.guardar' />
				</button>
			</div>
		</form>
	</div>
</c:forEach>
	        
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
	
	function confirmarAmagarFile(e, docId) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
// 		var conf = confirm("<spring:message code='expedient.eines.confirm_reassignar_expedients' />");
// // 		if (conf) {
// 			$("#amagarFile"+docId).removeClass("hide");
// 			$("#modal-botons"+docId).removeClass("hide");
// 			$("#downloadUrl"+docId).hide();
// 			$("#hideData"+docId).hide();
// 			$("#removeUrl"+docId).hide();
// 		}
// 		return confirm;
	}
	// ]]>
</script>
