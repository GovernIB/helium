<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>

<c:url value="/v3/expedient/document/firma/validate" var="validateFirmaUrl"/>

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
	.documentTramitacio .checkbox label {padding-left: 0px;}
	.documentTramitacio input[type='checkbox'] {position: relative; margin-left: 0px;}
</style>
<script type="text/javascript">
	function callbackModalPortafibTasca(tascaId) {
		//Refrescar la pipella de documents: https://stackoverflow.com/questions/15113157/reloading-current-active-tab-in-twitter-bootstrap
		var $link = $('li.active a[data-toggle="tab"]');
	    $link.parent().removeClass('active');
	    var tabLink = $link.attr('href');
	    $('#tasca-pipelles a[href="' + tabLink + '"]').tab('show');
	}
</script>
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
				
		<c:choose>
			<c:when test="${isModal}">
				<c:url var="tascaDocumentAction" value="/modal/v3/tasca/${tasca.id}/document/${document.id}/adjuntar"/>
				<c:url var="documentGenerarAction" value='/modal/v3/tasca/${tasca.id}/document/${document.documentCodi}/generar'/>
				<c:url var="documentBorrarAction" value="/modal/v3/tasca/${tasca.id}/document/${document.documentCodi}/esborrar"/>
			</c:when>
			<c:otherwise>
				<c:url var="tascaDocumentAction" value="/v3/tasca/${tasca.id}/document/${document.id}/adjuntar"/>
				<c:url var="documentGenerarAction" value='/v3/tasca/${tasca.id}/document/${document.documentCodi}/generar'/>
				<c:url var="documentBorrarAction" value="/v3/tasca/${tasca.id}/document/${document.documentCodi}/esborrar"/>
			</c:otherwise>
		</c:choose>
	
		<form id="documentsForm" class="form-horizontal form-tasca" action="${tascaDocumentAction}" enctype="multipart/form-data" method="post" data-document-id="${document.id}">
			<input type="hidden" id="docId${document.id}" name="docId" value="${document.id}"/>
			<input type="hidden" id="clearFirmes_${document.id}" name="clearFirmes" value="false"/>
			<div class="inlineLabels">
				<h4 class="titol-missatge">
					<label class="control-label col-xs-1 <c:if test="${document.required}">obligatori</c:if>">${document.documentNom}</label>
		 			<c:if test="${not empty document.arxiuNom and document.arxiuContingutDefinit and tasca.validada and empty document.tokenSignatura}">
						<a 	class="icon generar_plantilla"
							id="plantilla${document.id}" 
							href="${documentGenerarAction}"
							<c:if test="${document.plantilla and document.adjuntarAuto}">
							</c:if>
							title="<spring:message code='expedient.massiva.tasca.doc.generar' />">
							<i class="fa fa-file-text-o"></i>
						</a>
		 			</c:if>
		 			
		 			<c:if test="${not empty document.tokenSignatura and document.signat}">
		 				<c:choose>
							<c:when test="${not empty document.urlVerificacioCustodia}">
								<a class="icon signature" href="${document.urlVerificacioCustodia}" target="_blank"><span class="fa fa-certificate" title="<spring:message code="expedient.document.signat"/>"></span></a>
							</c:when>								
							<c:when test="${not empty document.signaturaUrlVerificacio}">
								<c:choose>
									<c:when test="${document.ntiCsv != null}">
										<!--  Url del ConCSV per l'Arxiu -->
										<a class="icon signature" href="${document.signaturaUrlVerificacio}" target="_blank"><span class="fa fa-certificate" title="<spring:message code="expedient.document.signat"/>"></span></a>
									</c:when>
									<c:otherwise>
										<!-- S'ha de consultar el CSV -->
										<a class="icon signature" href="<c:url value='/v3/expedient/${tasca.expedientId}/proces/${tasca.processInstanceId}/document/${document.documentStoreId}/signatura/verificarCsv'/>" target="_blank"><span class="fa fa-certificate" title="<spring:message code="expedient.document.signat"/>"></span></a>
									</c:otherwise>
								</c:choose>
							</c:when>
		 				</c:choose>
					</c:if>
					<c:if test="${document.registrat}">
						<a 	data-rdt-link-modal="true" 
							class="icon registre" 
							href="<c:url value='/modal/v3/expedient/${tasca.expedientId}/proces/${tasca.processInstanceId}/document/${document.documentStoreId}/registre/verificar'/>">
							<span class="fa fa-book" title="<spring:message code='expedient.document.registrat' />"></span>
						</a>
					</c:if>
		 			
					<a title="<spring:message code='comuns.descarregar' />" class="icon <c:if test="${empty document.tokenSignatura}">hide</c:if>" id="downloadUrl${document.id}" href="<c:url value='/v3/tasca/${tasca.id}/document/${document.documentCodi}/descarregar'/>">
						<i class="fa fa-download"></i>
					</a>
					<c:if test="${!bloquejarEdicioTasca}">
						<a 	class="icon <c:if test="${empty document.tokenSignatura or not tasca.validada}">hide</c:if>" 
							id="removeUrl${document.id}" 
							href="${documentBorrarAction}"
							data-rdt-link-confirm="<spring:message code='expedient.document.confirm_esborrar_proces' />"
							title="<spring:message code='expedient.massiva.tasca.doc.borrar' />">
							<i class="fa fa-trash-o"></i>
						</a>
					</c:if>

		 			<c:if test="${document.portafirmesActiu && !document.signat && !bloquejarEdicioTasca && tasca.validada && ((not empty document.tokenSignatura))}">
		 			
		 				<c:if test="${(document.psignaActual!=null) && (document.psignaActual.firmaEnProces || document.psignaActual.error)}">
							<c:choose>
								<c:when test="${document.psignaActual.error}">
									<c:set var="iconPendentSignaturaBtn" value="fa-exclamation-triangle"/>
									<c:choose>
										<c:when test="${document.psignaActual.rebutjadaProcessada}">
											<c:set var="titlePendentSignaturaBtn" value="expedient.document.rebutjat.psigna.error"/>
										</c:when>
										<c:otherwise>
											<c:set var="titlePendentSignaturaBtn" value="expedient.document.pendent.psigna.error"/>
										</c:otherwise>
									</c:choose>
								</c:when>
								<c:otherwise>
											<c:set var="titlePendentSignaturaBtn" value="expedient.document.pendent.psigna"/>
											<c:set var="iconPendentSignaturaBtn" value="fa fa-clock-o"/>
								</c:otherwise>
							</c:choose>

							<a 	href="<c:url value='/modal/v3/tasca/${tasca.id}/proces/${tasca.processInstanceId}/document/${document.documentStoreId}/pendentSignatura'/>"
								data-rdt-link-modal="true" 
								data-rdt-link-modal-maximize="false"
								data-rdt-link-modal-min-height="400"
								data-rdt-link-callback="callbackModalPortafibTasca(${tasca.id});"
								class="icon enviarPortasignatures">
									<span class="icon fa ${iconPendentSignaturaBtn} psigna-info" title="<spring:message code='${titlePendentSignaturaBtn}' />"></span>
							</a>
		 				</c:if>
		 			
	 					<c:if test="${(document.psignaActual==null) || document.psignaActual.reintentarFirma}">
	 						<!-- S'ha de pintar el sobre: si no hi ha firma, o si la darrera vigent s'ha cancelat o rebutjat -->
							<a 	href="<c:url value='/modal/v3/tasca/${tasca.id}/proces/${tasca.processInstanceId}/document/${document.documentStoreId}/enviarPortasignatures'/>"
								data-rdt-link-modal="true" 
								data-rdt-link-modal-maximize="false"
								data-rdt-link-modal-min-height="400"
								data-rdt-link-callback="callbackModalPortafibTasca(${tasca.id});"									
								class="icon enviarPortasignatures"
								title="<spring:message code='expedient.document.enviar.portasignatures' />">
								<span class="fa fa-envelope-o" /></span>
							</a>
	 					</c:if>

		 			</c:if>

					<div id="hideData${document.id}" class="comentari small <c:if test="${empty document.tokenSignatura}">hide</c:if>">
						<p><label><spring:message code='tasca.doc.adjunt.arxiu' /></label>: <label id="docNom${document.id}">${document.arxiuNom}</label></p>
						<p><label><spring:message code='tasca.doc.adjunt.adjuntat.el' /></label>: <label id="docDataAdj${document.id}"><fmt:formatDate value="${document.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></label></p>
						<p><label><spring:message code='tasca.doc.adjunt.data.document' /></label>: <label id="docData${document.id}"><fmt:formatDate value="${document.dataDocument}" pattern="dd/MM/yyyy"/></label></p>
					</div>
				</h4>
			</div>
			<c:if test="${tasca.validada and !bloquejarEdicioTasca}">
			
				<div id="amagarFile${document.id}" class="form-group <c:if test="${not empty document.tokenSignatura}">hide</c:if>">
					
					<div class="form-group">
						<label class="control-label col-xs-4" for="contingut${document.id}"><spring:message code='expedient.document.arxiu' /></label>
						
				        <div class="col-xs-8 arxiu">
				            <div class="input-group">
				                <input id="contingut${document.id}" name="contingut" class="form-control input-file" />
				                <span class="input-group-btn">
				                    <span class="btn btn-default btn-file">
				                        <spring:message code="expedient.document.arxiu"/>… <input type="file" id="arxiu${document.id}" name="arxiu" <c:if test="${not empty document.extensionsPermeses}">accept="${document.extensionsPermeses}"</c:if>>
				                    </span>
				                </span>
				            </div>
						</div>
					</div>
										
					<div class="form-group">
						<label class="control-label col-xs-4" for="ambFirma${document.id}"><spring:message code="expedient.document.form.camp.amb.firma"></spring:message></label>
						<div class="controls col-xs-8">
							<div class="checkbox">
					  			<label>
									<input id="ambFirma${document.id}" name="ambFirma" class="span12" type="checkbox" value="true">
								</label>
							</div>
						</div>
					</div>
					
					<div id="input-firma${document.id}" class="hidden">
						
						<div class="form-group">
							<label class="control-label col-xs-4" for="tipusFirma${document.id}">
								<spring:message code="expedient.document.form.camp.tipus.firma"></spring:message>
							</label>
							<div class="controls col-xs-8 btn-group" data-toggle="buttons">
									<label class="btn btn-default active">
												<input id="tipusFirma1${document.id}" name="tipusFirma" type="radio" value="ADJUNT" checked="checked"> Adjunta
									</label>
									<label class="btn btn-default">
												<input id="tipusFirma2${document.id}" name="tipusFirma" type="radio" value="SEPARAT"> Separada
									</label>
							</div>
						</div>

						<div id="input-firma-arxiu${document.id}" class="hidden">
							<div class="form-group">
								<label class="control-label col-xs-4 obligatori" for="contingutFirma${document.id}"><spring:message code="expedient.document.form.camp.firma"/></label>
						        <div class="col-xs-8 firma">
						            <div class="input-group">
						                <input id="contingutFirma${document.id}" name="contingutFirma" class="form-control input-file" />
						                <span class="input-group-btn">
						                    <span class="btn btn-default btn-file">
						                        <spring:message code="expedient.document.arxiu"/>… <input type="file" id="firma${document.id}" name="firma" <c:if test="${not empty document.extensionsPermeses}">accept="${document.extensionsPermeses}"</c:if>>
						                    </span>
						                </span>
						            </div>
								</div>
							</div>
						</div>
					</div>					
					
	        	</div>
				<div id="div_timer${document.id}" class="div_timer form-group <c:if test="${not empty document.tokenSignatura}">hide</c:if>">
			    	<div class="<c:if test="${not empty campErrors}"> has-error</c:if>">
						<label class="control-label col-xs-4" for="data${document.id}"><spring:message code='tasca.doc.adjunt.data.document' /></label>
						<div class="input-group col-xs-8">
							<input class="form-control date" placeholder="dd/mm/aaaa" id="data${document.id}" name="data"/>
							<span class="input-group-addon btn_date"><span class="fa fa-calendar"></span></span>
						</div>
					</div>
				</div>
				<div id="modal-botons${document.id}" class="modal-botons <c:if test="${not empty document.tokenSignatura}">hide</c:if>">
					<button type="submit" class="guardar btn btn-primary pull-right"><span class="fa fa-floppy-o"></span>&nbsp;<spring:message code="comuns.guardar"/></button>
				</div>
			</c:if>
		</form>
	</div>
</c:forEach>

<script type="text/javascript">
	// <![CDATA[	
	var esFirmaValida = true;
	$(document).on('change', '.btn-file :file', function() {
		var input = $(this),
		numFiles = input.get(0).files ? input.get(0).files.length : 1,
		label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
		input.trigger('fileselect', [numFiles, label]);
	});
	
	$(document).ready( function() {
		
		$('input[name=arxiu]').on('change', function(e) { validateFirma(this, 'arxiu')});
		$('input[name=firma]').on('change', function(e) { validateFirma(this, 'firma')});
		
		$('.input-group:has( > input[name=contingut])').on('click', showLoader);
		$('.input-group:has( > input[name=contingutFirma])').on('click', showLoader);
		$('input[name=arxiu]').on('cancel', hideLoader);
		$('input[name=firma]').on('cancel', hideLoader);
		
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
		$(".btn_date").click(function(){
			$(this).prev(".date").trigger("focus");
		});
		$('.input-file').click(function() {
			$(this).blur();
			$(this).parent().find('input[type="file"]').click();
		});
		$('.generar_plantilla').click(function(e) {
			
			if (confirm('<spring:message code='expedient.tasca.doc.generar.confirm' />')) {
				try {
					$('.div-dades-carregant', window.parent.document).show();
				} catch(e) {
					console.error('No es troba el .div-dades-carregant: ' + e);				
				}	
			} else {
				e.stopPropagation();
				e.preventDefault();
				return false;
			}
		});
		$('input[type=checkbox][name=ambFirma]').on('change', function() {
			var form= $(this).closest('form')
			var documentId= $(this).closest('form').data('document-id');
			if($(this).prop("checked") == true){
				$('#input-firma' + documentId).removeClass('hidden');
				$('input[type=radio][name=tipusFirma][id*=' + documentId + ']:checked').change();
			} else {
				$('#input-firma' + documentId).addClass('hidden');
				if(!esFirmaValida) {
					esFirmaValida = true;
					$('input[name=contingutFirma]', form).val(null);
					$('input[name=firma]', form).val(null);
					$('input[type=radio][name=tipusFirma][value=ADJUNT]', form).click();
					$('button[type=submit]', form).removeAttr('disabled');
				}
			}
		});
		$('input[type=radio][name=tipusFirma]').change(function() {
			var documentId= $(this).closest('form').data('document-id');
			if ($(this).val() == 'SEPARAT') {
				$('#input-firma-arxiu' + documentId).removeClass('hidden');
			} else {
				$('#input-firma-arxiu' + documentId).addClass('hidden');
			}
		});	
		$('.guardar').click(function(e) {
			$('.div-dades-carregant', window.parent.document).show();	
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
	
	function validateFirma(self, tipus) {
		if(!${isArxiuActiu}) {
			hideLoader();		
			return;
		}
		
		var form = $(self).closest('form');
		var formData = new FormData(form[0]);
		$('#firmaAlert').remove();
		$('#firmaError').remove();
		if(tipus == 'arxiu' && $('input[name=ambFirma]', form).prop('checked')) {
			$('input[name=ambFirma]', form).trigger('click');
			$('input[name=clearFirmes]', form).val(false);
		}
		$.ajax({
			url: '${validateFirmaUrl}',
			type: 'POST',
			data: formData,
			async: false,
			success: function (data) {
				
				if((data.firmat && data.valid && !$('input[name=ambFirma]', form).prop('checked')) || 
					($('input[name=ambFirma]', form).prop('checked') && !data.valid && !data.firmat)) {
					$('input[name=ambFirma]', form).trigger('click');
				}
				
				if(tipus == 'arxiu') {
					if(data.valid) {
						$('input[name="tipusFirma"][value="ADJUNT"]', form).parent().show();
					} else {
						$('input[name="tipusFirma"][value="ADJUNT"]', form).parent().hide();
					}
					if((!data.firmat || !data.valid) && $('input[name="tipusFirma"]', form).val() == 'ADJUNT') {
						$('input[name="tipusFirma"][value="ADJUNT"]', form).parent().removeClass('active');
						$('input[name="tipusFirma"][value="SEPARAT"]', form).trigger('click').parent().addClass('active');					
					} 
					if(data.firmat && data.valid) {
						$('input[name="tipusFirma"][value="ADJUNT"]', form).trigger('click').parent().addClass('active');
						$('input[name="tipusFirma"][value="SEPARAT"]', form).parent().removeClass('active');
					}
				}
				
				if(data.alert) {
					form.prepend('<div id="firmaAlert" class="alert alert-warning"> <i class="fa fa-exclamation-triangle pr-2" />' + data.alert + '</div>');
				}

				if(data.error) {
					form.prepend('<div id="firmaError" class="alert alert-danger">' + data.error + '</div>');
				}
				
				if(tipus == 'arxiu' && !data.firmat)
					$('input[name=clearFirmes]', form).val(true);
				if(tipus == 'firma' && !data.firmat) {
					esFirmaValida = false;
				} else {
					$('button[type=submit]', form).removeAttr('disabled');
				}
				
				hideLoader();
			},
			cache: false,
			contentType: false,
			processData: false
		});
	}
	
	function showLoader() {
		$('input', this).val(undefined);
		$('button[type=submit]', $(this).closest('form')).attr('disabled', true);
		$('.div-dades-carregant', window.parent.document).show();
	}

	function hideLoader() {
		$('.div-dades-carregant', window.parent.document).hide();
	}
	
	// ]]>
</script>
