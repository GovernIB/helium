<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%-- SENSE US --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>

<style type="text/css">
	#tasca-document .well.well-small {margin: 0 0 15px;}
	#tasca-document .form-tasca .modal-botons {padding-bottom: 25px;}
	.documentTramitacio h4.titol-missatge {width: 100%;display: inline-table;}
	.documentTramitacio h4.titol-missatge a{margin-left: 10px;}
	.documentTramitacio .col-xs-1 {width: auto;padding-left: 0px;}
	.documentTramitacio .titol-missatge label {padding-top: 0px;}
	.documentTramitacio .titol-missatge .obligatori {background-position: right 8px;}
	.documentTramitacio .btn-file {position: relative; overflow: hidden;}
	.documentTramitacio .btn-file input[type=file] {position: absolute; top: 0; right: 0; min-width: 100%; min-height: 100%; font-size: 100px; text-align: right; filter: alpha(opacity = 0); opacity: 0; outline: none; background: white; cursor: inherit; display: block;}
	.documentTramitacio .col-xs-4 {width: 15%;}
	.documentTramitacio .col-xs-10 {width: 85%;}	
	.documentTramitacio .col-xs-10.arxiu {padding-right: 0px;}			
	.documentTramitacio .div_timer .input-group {padding-left: 15px;}
	.documentTramitacio .div_timer .input-group-addon {width: 5% !important;}
	.documentTramitacio .comentari {padding-top: 30px;}
</style>
<c:if test="${not tasca.validada}">
	<div class="alert alert-warning">	
		<button type="button" class="close" data-dismiss="alert" aria-label="<spring:message code="comu.boto.tancar"/>"><span aria-hidden="true">&times;</span></button>
		<p>
			<span class="fa fa-warning"></span>
			<spring:message code="tasca.doc.no_es_podran"/>
		</p>
	</div>
</c:if>
<c:if test="${not tasca.documentsComplet}">
	<div class="alert alert-warning alert-valid">	
		<button type="button" class="close" data-dismiss="alert" aria-label="<spring:message code="comu.boto.tancar"/>"><span aria-hidden="true">&times;</span></button>
		<p>
			<span class="fa fa-warning"></span>
			<spring:message code="tasca.tramitacio.documents.no.complet"/>
		</p>
	</div>
</c:if>
<c:forEach var="document" items="${documents}">
	<div class="documentTramitacio well well-small">
		<form id="documentsForm" class="form-horizontal form-tasca" action="${tasca.id}/document/${document.documentCodi}/adjuntar" enctype="multipart/form-data" method="post">
			<input type="hidden" id="docId${document.id}" name="docId" value="${document.id}"/>
			<div class="inlineLabels">
				<h4 class="titol-missatge">
					<label class="control-label col-xs-1 <c:if test="${document.required}">obligatori</c:if>">${document.documentNom}</label>
		 			<c:if test="${document.plantilla and tasca.validada}">
						<a 	class="icon" 
							id="plantilla${document.id}" 
							href="<c:url value='/modal/v3/expedient/${expedientId}/tasca/${tasca.id}/document/${document.documentCodi}/generar'/>"
							<c:if test="${document.adjuntarAuto}">data-rdt-link-confirm="<spring:message code='expedient.tasca.doc.generar.confirm' />"</c:if>
							title="<spring:message code='expedient.massiva.tasca.doc.generar' />">
							<i class="fa fa-file-text-o"></i>
						</a>
		 			</c:if>
					<a title="<spring:message code='comuns.descarregar' />" class="icon <c:if test="${empty document.tokenSignatura}">hide</c:if>" id="downloadUrl${document.id}" href="<c:url value='/v3/expedient/${expedientId}/document/${document.documentStoreId}/descarregar'/>">
						<i class="fa fa-download"></i>
					</a>
					<a 	class="icon <c:if test="${empty document.tokenSignatura or not tasca.validada}">hide</c:if>" 
						id="removeUrl${document.id}" 
						href="<c:url value="/modal/v3/expedient/${expedientId}/tasca/${tasca.id}/document/${document.documentCodi}/esborrar"></c:url>"
						data-rdt-link-confirm="<spring:message code='expedient.document.confirm_esborrar_proces' />"
						title="<spring:message code='expedient.massiva.tasca.doc.borrar' />">
						<i class="fa fa-trash-o"></i>
					</a>
					<div id="hideData${document.id}" class="comentari small <c:if test="${empty document.tokenSignatura}">hide</c:if>">
						<p><label><spring:message code='tasca.doc.adjunt.arxiu' /></label>: <label id="docNom${document.id}">${document.arxiuNom}</label></p>
						<p><label><spring:message code='tasca.doc.adjunt.adjuntat.el' /></label>: <label id="docDataAdj${document.id}"><fmt:formatDate value="${document.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></label></p>
						<p><label><spring:message code='tasca.doc.adjunt.data.document' /></label>: <label id="docData${document.id}"><fmt:formatDate value="${document.dataDocument}" pattern="dd/MM/yyyy"/></label></p>
					</div>
				</h4>
			</div>
			<c:if test="${tasca.validada}">
				<div id="amagarFile${document.id}" class="form-group <c:if test="${not empty document.tokenSignatura}">hide</c:if>">
					<label class="control-label col-xs-4" for="nom"><spring:message code='expedient.document.arxiu' /></label>
			        <div class="col-xs-10 arxiu">
			            <div class="input-group">
			                <input  id="contingut${document.id}" name="contingut" class="form-control" />
			                <span class="input-group-btn">
			                    <span class="btn btn-default btn-file">
			                        <spring:message code="expedient.document.arxiu"/>… <input type="file" id="arxiu${document.id}" name="arxiu" <c:if test="${not empty document.extensionsPermeses}">accept="${document.extensionsPermeses}"</c:if>>
			                    </span>
			                </span>
			            </div>
					</div>
	        	</div>
				<div id="div_timer${document.id}" class="div_timer form-group <c:if test="${not empty document.tokenSignatura}">hide</c:if>">
			    	<div class="<c:if test="${not empty campErrors}"> has-error</c:if>">
						<label class="control-label col-xs-4" for="data${document.id}"><spring:message code='tasca.doc.adjunt.data.document' /></label>
						<div class="input-group col-xs-10">
							<input class="form-control date" placeholder="dd/mm/aaaa" id="data${document.id}" name="data"/>
							<span class="input-group-addon btn_date"><span class="fa fa-calendar"></span></span>
						</div>
					</div>
				</div>
				<div id="modal-botons${document.id}" class="modal-botons <c:if test="${not empty document.tokenSignatura}">hide</c:if>">
					<button type="submit" class="btn btn-primary pull-right"><span class="fa fa-floppy-o"></span>&nbsp;<spring:message code="comuns.guardar"/></button>
				</div>
			</c:if>
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
		$('.documentTramitacio .icon').heliumEvalLink({
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
		$('.date').datepicker({
    		format: 'dd/mm/yyyy',
    		weekStart: 1,
    		autoclose: true,
    		language: '${idioma}'
    	});

	});
	
	function checkFile(docId) {
        var fileExtension = "";
        var fileElementVal = $("#arxiu"+docId).val();
        if (fileElementVal.lastIndexOf(".") > 0) {
            fileExtension = fileElementVal.substring(fileElementVal.lastIndexOf(".") + 1, fileElementVal.length);
        }
        if($("#arxiu"+docId).attr('accept') !== undefined && $("#arxiu"+docId).attr('accept').indexOf(fileExtension) == -1) {    	
            alert("<spring:message code='error.extensio.document.permesa' /> "+$("#arxiu"+docId).attr('accept'));
            return false;
        }
        return true;
    }
	// ]]>
</script>
