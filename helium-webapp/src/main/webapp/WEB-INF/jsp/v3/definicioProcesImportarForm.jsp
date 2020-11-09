<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${not empty command.id}"><
		<c:set var="titol"><spring:message code="definicio.proces.importar.form.titol.definicio" arguments="${definicioProces.jbpmKey}, ${definicioProces.etiqueta}, ${definicioProces.versio}"/></c:set>
	</c:when>
	<c:when test="${not empty command.expedientTipusId}"><
		<c:set var="titol"><spring:message code="definicio.proces.importar.form.titol.tipus" arguments="${expedientTipus.codi}, ${expedientTipus.nom}"/></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="definicio.proces.importar.form.titol.entorn" arguments="${entorn.codi}, ${entorn.nom}"/></c:set>
	</c:otherwise>
</c:choose>

<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<style type="text/css">
		.panel {
			margin-bottom: 0px;
		}
		.panel-heading {
			padding : 5px 5px;
		}
	</style>
</head>
<body>		
	<form:form id="importar-form" cssClass="form-horizontal" action="importar" enctype="multipart/form-data" method="post" commandName="command" style="min-height: 500px;">

		<div class="inlineLabels">
		
			<script type="text/javascript">
				// <![CDATA[
				            
				// Opcions comunes per totes les taules del document
				$.extend( true, $.fn.dataTable.defaults, {
				    columnDefs: [ {
				    	targets: 0,
				    	orderable: false
				    } ],
					order: [[ 1, "asc" ]],
					paging : false,
				    searching: false,
				    oLanguage: {
					      sInfo: ""
				    }
				});				
				
				$(document).ready( function() {
					// Quan se selecciona un fitxer es mostra el botó de carregar el contingut.
					$('#file').change(function() {
						window.parent.$('button[type="submit"]').attr('disabled', 'disabled');
						$('progress').hide();
						$('#importarOpcions').empty();
						if ($(this).val() != "") 
							$('#carregarButton').show().click();	
					});
					$('#carregarButton').click(function(e){
						importarUpload();
					})						
					// Submit del formulari per Ajax
					$('button[type="submit"]').click(function(e){
						e.preventDefault();
						e.stopPropagation();
						importarFormPostSubmit();
						return false;
					})
				}); 	
				
				/** Funció que fa el post del formulari d'importació per ajax per carregar 
				la part del contingut de les opcions mantenint la selecció del fitxer. */
				function importarFormPostSubmit() {
					
					var $form = $('#importar-form');
					// Neteja els errors en fer submit						
					$('.has-error').removeClass('has-error');
					$('p.help-block').remove();
					// Adequa el botó de submit posat el el parent per webutil.modal.js
					window.parent.$('button[type="submit"]').attr('disabled', 'disabled');
					//window.parent.$('#importarProcessant').css('visibility', 'visible');
					window.parent.$('#importarIcon').hide();
					window.parent.$('#importarProcessant').show();
					// Completa les dades del formulari amb el contingut del fitxer per enviar dins del commmand
					var formData = new FormData($form[0]);
					// Fa el post per ajax per mantenir la selecció del fitxer
					var url = $form.attr('action'); 
				    $.ajax({
				           	type: "POST",
					        url: '<c:url value="/nodeco/v3/definicioProces/importar"/>',  
				           	data: formData,
				            processData: false,
				            contentType: false,
					        xhr: function() {  // Custom XMLHttpRequest
					            var myXhr = $.ajaxSettings.xhr();
					            if(myXhr.upload){ // Check if upload property exists
					                myXhr.upload.addEventListener('progress',progressHandlingFunction, false); // For handling the progress of the upload
					            }
					            return myXhr;
					        },
					        beforeSend: function(){
								$('progress').show();
								$('#carregant').show();
					        },
				           	success: function(data)
				           	{
					        	$('#importarOpcions').html(data);
					        	if (Boolean($('#importacioFinalitzada').val())) {
						        	if (typeof $('#redireccioUrl').val() != 'undefined')
						        		window.parent.location = $('#redireccioUrl').val();					        		
					        	} else {
									webutilRefreshMissatges();
									actualitzarOpcions();
					        	}
				           	},
							complete: function(){
								window.parent.$('button[type="submit"]').removeAttr('disabled');
								//window.parent.$('#importarProcessant').css('visibility', 'hidden');
								window.parent.$('#importarProcessant').hide();
								window.parent.$('#importarIcon').show();
								$('progress').hide();
								$('#carregant').hide();
							},
					        error: function(err){
								webutilRefreshMissatges();
					        	console.log('Error ' + err.status + ': ' + err.statusText)
					        }
					});
				}

			    
				/* Quan es carrega el contingut de les opcions es lliguen els events ajax. */
				function actualitzarOpcions() {					
					$('table').DataTable();
					// Event per seleccionar o des seleccionar totes les entrades
					$('.checkAll').change(function(){
						$(this).closest('.dataTables_wrapper').find('.check').prop('checked', $(this).is(':checked'));
						updateMarcador($(this).closest('.agrupacio'));
					});
					$('.tots').click(function(e) {
						$(this).closest('.agrupacio').find('.checkAll').prop('checked', false).change();
						e.stopPropagation();
					});
					$('.algun, .cap').click(function(e) {
						$(this).closest('.agrupacio').find('.checkAll').prop('checked', true).change();
						e.stopPropagation();
					});
					// marca el checkbox si es clica sobre la línia de la taula
					$('.row_checkbox').click(function(e) {
						if (e.target.type != 'checkbox'){
							var $checkbox =$('.check', this);
							$checkbox.prop('checked', !$checkbox.is(':checked')).change();
						  }						
					});
					// si canvia un check comprova què fer amb el checkAll
					$('.check').change(function() {
						updateMarcador($(this).closest('.agrupacio'))
					});					
					// Per mostrar o ocultar el contingut de les taules
					$('.agrupacio').on('show.bs.collapse', function () {
						$(this).find('.fa-chevron-down').hide();
						$(this).find('.fa-chevron-up').show();
					});
					$('.agrupacio').on('hidden.bs.collapse', function () {
						$(this).find('.fa-chevron-up').hide();
						$(this).find('.fa-chevron-down').show();
					});
					// actualitza els comptadors
					$('.agrupacio').each(function(){
						if ($(this).find('.taula').length > 0)
							updateMarcador($(this));
					});		
					// Expandeix els panells que continguin errors
					$('.has-error').closest('.agrupacio').find('.clicable').click();

					// Aplica la funció select2 als controls select
					$('select.form-control').select2({
					    minimumResultsForSearch: -1
					});
					// Deshabilita la selecció de versió
					$('#definicions-taula').find('.check').change(function() {
						$(this).closest('tr').find('select').attr('disabled', !$(this).is(':checked'));
					})
				} 				
				
				// Actualitza la icona del marcador de selecció del costat del títol de la agrupació
				function updateMarcador( $agrupacio ) {
					// amaga els marcadors
					$agrupacio.find('.marcador').hide();
					var total = $agrupacio.find('.check').length;
					var marcats = $agrupacio.find('.check:checked').length;
					
					if (total == marcats && marcats > 0)
						$agrupacio.find('.tots').show();
					else if(marcats == 0)
						$agrupacio.find('.cap').show();
					else
						$agrupacio.find('.algun').show();
					$agrupacio.find('.marcats').text(marcats);
					$agrupacio.find('.total').text(total);
					// botó de checkall					
					$agrupacio.find('.checkAll').prop('checked', total == marcats);
				}	
				
				/** Carrega el fitxer al servidor i omple el contingut per seleccionar què importar. */
				function importarUpload() {
					
					var formData = new FormData();
		            formData.append('file', $('#file')[0].files[0]);		            
					formData.append("entornId", $('#entornId').val());
					formData.append("expedientTipusId", $('#expedientTipusId').val());
					formData.append("id", $('#id').val());
					
				    $.ajax({
				        url: '<c:url value="/nodeco/v3/definicioProces/importar/upload"/>',  
				        type: 'POST',
				        // Form data
				        data: formData,
				        xhr: function() {  // Custom XMLHttpRequest
				            var myXhr = $.ajaxSettings.xhr();
				            if(myXhr.upload){ // Check if upload property exists
				                myXhr.upload.addEventListener('progress',progressHandlingFunction, false); // For handling the progress of the upload
				            }
				            return myXhr;
				        },
				        //Ajax events
				        beforeSend: function(){
				        	$('#importarOpcions').empty();
							$('#carregarButton').hide();
							$('progress').show();
							$('#carregant').show();
				        },
				        success: function(data){
				        	$('#importarOpcions').html(data);
							window.parent.$('button[type="submit"]').removeAttr('disabled');
							actualitzarOpcions();
				        },
				        error: function(err){
				        	console.log('Error ' + err.status + ': ' + err.statusText)
				        },
				        complete: function() {
				        	$('#carregarButton').show();
							$('progress').hide();
							$('#carregant').hide();
							webutilRefreshMissatges();
				        },
				        //Options to tell jQuery not to process data or worry about content-type.
				        cache: false,
				        contentType: false,
				        processData: false
				    });
				}
				
				/** Funció que va incrementant la barra de progrés de la càrrega dels fitxers. */
				function progressHandlingFunction(e){
				    if(e.lengthComputable){
				        $('progress').attr({value:e.loaded,max:e.total});
				    }
				}
				// ]]>
			</script>			
		</div>
		
		<input type="hidden" name="entornId" id="entornId" value="${command.entornId}" />
		<input type="hidden" name="expedientTipusId" id="expedientTipusId" value="${command.expedientTipusId}" />
		<input type="hidden" name="id" id="id" value="${command.id}" />

		<c:if test="${command.id != null}">
			<hel:inputCheckbox name="sobreEscriure" textKey="definicio.proces.importar.form.opcions.sobreEscriure" labelSize="6" />
		</c:if>
		
		<h4>1 <spring:message code="definicio.proces.importar.form.carregar"/></h4>
		<div class="row">
			<div class="col-sm-6">
				<input type="file" name="file" id="file" />
			</div>
			<div class="col-sm-6">
				<button id="carregarButton" type="button" class="btn btn-default" style="display: none;">
					<span class="fa fa-upload"></span>
					<spring:message code="definicio.proces.importar.form.boto.carregar"/>
				</button>
				<progress style="display: none;"></progress>
			</div>
		</div>
		
		<h4>2 <spring:message code="definicio.proces.importar.form.seleccionar"/></h4>
		<div id="carregant" style="display: none; width: 10%; text-align: center;">
			<span class="fa fa-spinner fa-pulse fa-2x fa-fw"></span>
			<span class="sr-only"><spring:message code="comu.processant"/>...</span>
		</div>
		<div id="importarOpcions">
		</div>
		

		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true">
				<spring:message code="comu.boto.cancelar"/>
			</button>
			<button id="importarButton" type="submit" class="btn btn-success right">
				<span id="importarIcon" class="fa fa-sign-in fa-fw"></span> 
				<span id="importarProcessant" style="display:none;">
					<span class="fa fa-spinner fa-spin fa-fw" title="<spring:message code="comu.processant"/>..."></span><span class="sr-only">&hellip;</span>
				</span>			
				<spring:message code="comu.importar"/>
			</button>
		</div>
	</form:form>
</body>
</html>