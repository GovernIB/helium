<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<style type="text/css">
	.documentTramitacio .btn-file {position: relative; overflow: hidden;}
	.documentTramitacio .btn-file input[type=file] {position: absolute; top: 0; right: 0; min-width: 100%; min-height: 100%; font-size: 100px; text-align: right; filter: alpha(opacity = 0); opacity: 0; outline: none; background: white; cursor: inherit; display: block;}
	.documentTramitacio .form-group {width: 100%;display: inline-flex;}
	.documentTramitacio .fila_reducida {width: 100%;}
	.documentTramitacio .col-xs-1 {width: auto;padding-left: 0px;}				
	.documentTramitacio .col-xs-4 {width: 20%;}		
	.documentTramitacio .col-xs-8 {width: 77%;}
	.documentTramitacio .col-xs-8 .form-group {margin-left: 0px;margin-right: 0px;}
	.documentTramitacio .col-xs-8 .form-group .col-xs-4 {padding-left: 0px;width: 15%;}
	.documentTramitacio .col-xs-8 .form-group .col-xs-8 {width: 85%;padding-left: 15px;padding-right: 0px;}
	.documentTramitacio .col-xs-11 {padding-left: 0px;width: 100%;}				
	.documentTramitacio #s2id_estatId {width: 100% !important;}
	.documentTramitacio .arxiu {margin-left: 0%; margin-top: 10px;}
	.documentTramitacio h4.titol-missatge {width: 100%;}
	.documentTramitacio h4.titol-missatge a{margin-left: 10px;}
	.documentTramitacio a.icon {margin-left: 10px !important;}
	.documentTramitacio .comentari {padding-top: 30px;}
	.documentTramitacio .comentari label {font-weight: bold;}
	.documentTramitacio .modal-botons {padding-bottom: 30px;}
	.documentTramitacio .form-horizontal .control-label {padding-top: 0px;}
	.documentTramitacio .form-group {padding-right: 0px;}
	.documentTramitacio .form-group .col-xs-11 {padding-right: 0px;}
	.documentTramitacio .obligatori {background-position: right 8px;}
</style>
<c:url value="/v3/expedient/document/arxiuMostrar" var="downloadUrl"/>
<c:forEach var="document" items="${documents}">
	<div class="documentTramitacio well well-small">
		<form id="form${document.id}" class="form-horizontal form-tasca" action="documentAdjuntar" enctype="multipart/form-data" method="post" onsubmit="return false;">
			<input type="hidden" id="docId${document.id}" name="docId" value="${document.id}"/>
			
			<div class="inlineLabels">
				<h4 class="titol-missatge">
					<label class="control-label col-xs-1 <c:if test="${document.required}">obligatori</c:if>">${document.documentNom}</label>
		 			<c:if test="${document.plantilla}">
						<a 	class="icon" 
							id="plantilla${document.id}" 
							href="<c:url value='../../../../../../v3/expedient/${expedientId}/tasca/${tascaId}/documentGenerar'><c:param name='docId' value='${document.id}'/></c:url>"
							data-rdt-link-confirm="<spring:message code='expedient.tasca.doc.generar.confirm' />"
							data-rdt-link-ajax=true
							title="<spring:message code='expedient.massiva.tasca.doc.generar' />" 
							data-rdt-link-callback="documentGenerar(${document.id},${document.arxiuNom},${document.documentCodi}, ${document.adjuntarAuto});">
							<i class="fa fa-file-text-o"></i>
						</a>
		 			</c:if>
					<a title="<spring:message code='comuns.descarregar' />" class="icon <c:if test="${empty document.tokenSignatura}">hide</c:if>" id="downloadUrl${document.id}" href="<c:url value='/v3/expedient/${expedientId}/document/${document.documentStoreId}/descarregar'/>">
						<i class="fa fa-download"></i>
					</a>
					<a 	class="icon <c:if test="${empty document.tokenSignatura}">hide</c:if>" 
						id="removeUrl${document.id}" 
						href="<c:url value="../../../../../../v3/expedient/${expedientId}/tasca/${tascaId}/documentEsborrar"><c:param name="docId" value="${document.id}"/></c:url>"
						data-rdt-link-confirm="<spring:message code='expedient.document.confirm_esborrar_proces' />"
						data-rdt-link-ajax=true
						title="<spring:message code='expedient.massiva.tasca.doc.borrar' />" 
						data-rdt-link-callback="amagarFile(${document.id});">
						<i class="fa fa-times"></i>
					</a>
					<div id="hideData${document.id}" class="comentari small <c:if test="${empty document.tokenSignatura}">hide</c:if>">
						<p><label>Arxiu</label>: <label id="docNom${document.id}">${document.arxiuNom}</label></p>
						<p><label>Adjuntat el</label>: <label id="docDataAdj${document.id}"><fmt:formatDate value="${document.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></label></p>
						<p><label>Data del document</label>: <label id="docData${document.id}"><fmt:formatDate value="${document.dataDocument}" pattern="dd/MM/yyyy"/></label></p>
					</div>
				</h4>
				<div class="form-group">
					<div class="col-xs-11 arxiu">
			            <div id="amagarFile${document.id}" class="input-group <c:if test="${not empty document.tokenSignatura}">hide</c:if>">
			                <input id="contingut${document.id}" name="contingut" readonly="readonly" class="form-control" />
			                <span class="input-group-btn">
			                    <span class="btn btn-default btn-file">
			                         <spring:message code='expedient.document.arxiu' />… <input type="file" name="arxiu">
			                 	</span>
			                </span>
			            </div>
					</div>
				</div>
				<div id="div_timer${document.id}" class="control-group left control-group-mid <c:if test="${not empty document.tokenSignatura}">hide</c:if>">
			    	<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
						<label class="control-label col-xs-1" for="data${document.id}">Data del document</label>
						<div class="input-group">
							<input class="form-control datepicker" id="data${document.id}" name="data"/>
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
				<button class="pull-right btn btn-primary right" name="accio" onclick="documentGuardar(${document.id});" value="document_guardar">
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
		$('.icon').heliumEvalLink({
			refrescarAlertes: true,
			refrescarPagina: false,
			alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>"
		});	
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
	
	function documentGuardar(docId) {
		$.ajax({
            type: 'POST',
            url: $("#form"+docId).attr('action'),
            data: new FormData($("#form"+docId)[0]),
            cache: false,
            contentType: false,
            processData: false,
            success: function(data) {
            	if (data != "") {
            		$("#amagarFile"+docId).addClass("hide");
        			$("#modal-botons"+docId).addClass("hide");
        			$("#downloadUrl"+docId).attr('href', "<c:url value='/v3/expedient/${expedientId}/document/'/>"+data+"/descarregar");
        			$("#downloadUrl"+docId).removeClass("hide");
        			$("#hideData"+docId).removeClass("hide");
        			$("#removeUrl"+docId).removeClass("hide");
        			$("#div_timer"+docId).addClass("hide");
        			
        			$("#docNom"+docId).text($("#contingut"+docId).val());
        			$("#docDataAdj"+docId).text((new Date()).toLocaleFormat('%d/%m/%Y %H:%M'));
        			if ($("#data"+docId).val() != "")
           				$("#docData"+docId).text($("#data"+docId).val());
           			else
           				$("#docData"+docId).text((new Date()).toLocaleFormat('%d/%m/%Y'));
        		}
            	
    	    	// Refrescar alertas
    	    	$.ajax({
    				url: "<c:url value="/nodeco/v3/missatges"/>",
    				async: false,
    				timeout: 20000,
    				success: function (data) {
    					$('#contingut-alertes *').remove();
    					$('#contingut-alertes').append(data);
    				}
    			});
            }
        });
	}
	
	function documentGenerar(docId, nom, codi, adjuntarAuto, correcte) {		
		if (adjuntarAuto == 'false') {
			window.location.href = "<c:url value='/v3/expedient/${expedientId}/tasca/${tascaId}/document/'/>"+docId+"/"+codi+"/descarregar";
		} else {
       		$("#amagarFile"+docId).addClass("hide");
   			$("#modal-botons"+docId).addClass("hide");
   			$("#downloadUrl"+docId).attr('href', "<c:url value='/v3/expedient/${expedientId}/tasca/${tascaId}/document/'/>"+docId+"/"+codi+"/descarregar");
   			$("#downloadUrl"+docId).removeClass("hide");
   			$("#hideData"+docId).removeClass("hide");
   			$("#removeUrl"+docId).removeClass("hide");
   			$("#div_timer"+docId).addClass("hide");
   			
   			$("#docNom"+docId).text(nom);
   			$("#docDataAdj"+docId).text((new Date()).toLocaleFormat('%d/%m/%Y %H:%M'));
   			if ($("#data"+docId).val() != "")
   				$("#docData"+docId).text($("#data"+docId).val());
   			else
   				$("#docData"+docId).text((new Date()).toLocaleFormat('%d/%m/%Y'));
   		}
	}
	
	function amagarFile(docId, correcte) {
		if (correcte) {
			$("#amagarFile"+docId).removeClass("hide");
			$("#modal-botons"+docId).removeClass("hide");
			$("#downloadUrl"+docId).addClass("hide");
			$("#hideData"+docId).addClass("hide");
			$("#removeUrl"+docId).addClass("hide");
			$("#div_timer"+docId).removeClass("hide");
			$("#form"+docId).find(':input').not(':button, :submit, :reset, :hidden, :checkbox, :radio').val('');
		}
	}
	// ]]>
</script>
