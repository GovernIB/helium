<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:choose>
	<c:when test="${adjuntar}"><
		<c:set var="titol"><spring:message code="expedient.document.adjuntar"/></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.document.modificar"/></c:set>
	</c:otherwise>
</c:choose>
<c:url value="/v3/expedient/document/firma/validate" var="validateFirmaUrl"/>
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
	.tab-pane {
		min-height: 300px;
		margin-top: 25px;
	}
</style>
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
	// Errors en les pipelles
	$('.tab-pane').each(function() {
		if ($('.has-error', this).length > 0) {
			$('a[href="#' + $(this).attr('id') + '"]').append(' <span class="fa fa-exclamation-triangle text-danger"/>');
		}
	});
	$('#arxiuNom').on('click', function() {
		$('input[name=arxiu]').click();
	});
	$('#firmaNom').on('click', function() {
		$('input[name=firma]').click();
	});
	
	$('.input-group:has( > #arxiuNom)').on('click', showLoader);
	$('.input-group:has( > #firmaNom)').on('click', showLoader);
	$('input[name=arxiu]').on('cancel', hideLoader);
	$('input[name=firma]').on('cancel', hideLoader);
	
	$('input[type=checkbox][name=ambFirma]').on('change', function() {
		if($(this).prop("checked") == true){
			$('#input-firma').removeClass('hidden');
			$('input[tpe=radio][name=tipusFirma]:checked').change();
		} else {
			$('#input-firma').addClass('hidden');
		}
	});
	$('input[type=radio][name=tipusFirma]').change(function() {
		if ($(this).val() == 'SEPARAT') {
			$('#input-firma-arxiu').removeClass('hidden');
		} else {
			$('#input-firma-arxiu').addClass('hidden');
		}
	});
	
	$('input[name=arxiu]').on('change', function(e) { validateFirma('arxiu')});
	$('input[name=firma]').on('change', function(e) { validateFirma('firma')});
	
	$('input[type=checkbox][name=ambFirma]').change();
	$('input[type=radio][name=tipusFirma]:checked').change();
	
	$("#arxiu").change(function (){
		$('#arxiuNom').val($(this).val().split('\\').pop());
	});
	$("#firma").change(function (){
		$('#firmaNom').val($(this).val().split('\\').pop());
	});
}); 


function validateFirma(tipus) {
	var formData = new FormData($('#documentExpedientCommand')[0]);
	$('#firmaAlert').hide();
	$('#firmaError').hide();
	if(tipus == 'arxiu' && $(ambFirma).prop('checked')) {
		$(ambFirma).trigger('click');
		$('#clearFirmes').val(false);
	}
	$.ajax({
		url: '${validateFirmaUrl}',
		type: 'POST',
		data: formData,
		async: false,
		success: function (data) {
			if((data.firmat && data.valid && !$(ambFirma).prop('checked')) || 
			   ($(ambFirma).prop('checked') && !data.firmat && !data.valid)) {
				$(ambFirma).trigger('click');
			}
			
			if(tipus == 'arxiu') {
				$('input[name="tipusFirma"][value="ADJUNT"]').prop('disabled', !data.valid);
				if((!data.firmat || !data.valid) && $('input[name="tipusFirma"]').val() == 'ADJUNT') {
					$('input[name="tipusFirma"][value="ADJUNT"]').parent().removeClass('active');
					$('input[name="tipusFirma"][value="SEPARAT"]').trigger('click').parent().addClass('active');					
				} 
				if(data.firmat && data.valid) {
					$('input[name="tipusFirma"][value="ADJUNT"]').trigger('click').parent().addClass('active');
					$('input[name="tipusFirma"][value="SEPARAT"]').parent().removeClass('active');
				}
			}
			
			if(data.alert)
				$('#firmaAlert').html('<i class="fa fa-exclamation-triangle pr-2" />' + data.alert).show();
			
			if(data.error)
				$('#firmaError').html(data.error).show();
			
			if(tipus == 'arxiu' && !data.firmat)
				$('#clearFirmes').val(true);
			
			if(tipus == 'firma' && !data.firmat) {
				esFirmaValida = false;
			} else {
				$('button[name="accio"]', window.parent.document).removeAttr('disabled');
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
	$('button[name="accio"]', window.parent.document).attr('disabled', true);
	$('.div-dades-carregant', window.parent.document).show();
}

function hideLoader() {
	$('.div-dades-carregant', window.parent.document).hide();
}
		
// ]]>
</script>
</head>
<body>
	<form:form cssClass="form-horizontal form-tasca" action="documentMasForm" enctype="multipart/form-data" method="post" commandName="documentExpedientCommand">

		<input id="inici" name="inici" value="${inici}" type="hidden"/>
		<input id="correu" name="correu" value="${correu}" type="hidden"/>
		<input id="adjuntar" name="adjuntar" value="${adjuntar}" type="hidden"/>
		<form:hidden path="docId"/>
		<form:hidden path="codi"/>
		<form:hidden path="expedientId"/>
		<form:hidden path="validarArxius"/>

		<c:if test="${metadades}">
			<div>
				<ul class="nav nav-tabs" role="tablist">
					<li id="pipella-general" class="active"><a href="#dades-generals" role="tab" data-toggle="tab"><spring:message code="expedient.document.pipella.general"/></a></li>
					<li id="pipella-nti"><a href="#dades-nti" role="tab" data-toggle="tab"><spring:message code="expedient.document.pipella.nti"/></a></li>
				</ul>
			</div>
		</c:if>

		<div class="tab-content">
			<div id="dades-generals" class="tab-pane in active">	
		
				<div id="titolArxiu">
				<c:choose>
					<c:when test="${documentExpedientCommand.docId == null}">
						<hel:inputText required="true" name="nom" textKey="expedient.document.titol" disabled="${documentExpedientCommand.docId != null}"/>						
					</c:when>
					<c:otherwise>
						<h4 class="titol-missatge">
							${documentExpedientCommand.nom}
						</h4>
					</c:otherwise>
				</c:choose>
				</div>
				<hel:inputDate required="true" name="data" textKey="expedient.document.data" placeholder="dd/mm/aaaa"/>
				<c:set var="campErrors"><form:errors path="arxiu"/></c:set>
				<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
					<label class="control-label col-xs-4 obligatori" for="arxiu"><spring:message code="expedient.document.arxiu"/></label>
			        <div class="col-xs-8 arxiu">
			            <div class="input-group">
			            	<input type="text" id="arxiuNom" name="arxiuNom" class="form-control" placeholder="${ document.arxiuNom }"/>
			                <span class="input-group-btn">
			                    <span class="btn btn-default btn-file">
			                        <spring:message code='expedient.document.arxiu' />… <input type="file" id="arxiu" name="arxiu"/>
			                    </span>
			                </span>
			            </div>
						<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="arxiu"/></p></c:if>
					</div>
				</div>
				
				<div class="alert alert-warning" id="firmaAlert" style="display: none;"></div>
				
				<hel:inputCheckbox name="ambFirma" textKey="expedient.document.form.camp.amb.firma"></hel:inputCheckbox>
					<div id="input-firma" class="hidden">
						<hel:inputRadio name="tipusFirma" textKey="expedient.document.form.camp.tipus.firma" botons="true" optionItems="${tipusFirmaOptions}" optionValueAttribute="value" optionTextKeyAttribute="text"/>
						<div id="input-firma-arxiu" class="hidden">
						<c:set var="campErrors"><form:errors path="firma"/></c:set>
							<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
								<label class="control-label col-xs-4 obligatori" for="firma"><spring:message code="expedient.document.form.camp.firma"/></label>
						        <div class="col-xs-8 firma">
						            <div class="input-group">
						            	<input type="text" id="firmaNom" name="firmaNom" class="form-control"/>
						                <span class="input-group-btn">
						                    <span class="btn btn-default btn-file">
						                        <spring:message code='expedient.document.arxiu' />… <input type="file" id="firma" name="firma"/>
						                    </span>
						                </span>
						            </div>
									<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="firma"/></p></c:if>
								</div>
							</div>
						</div>
					</div>					
				</div>
				<c:if test="${expedient.ntiActiu}">
					<div id="dades-nti" class="tab-pane">
						<hel:inputSelect name="ntiOrigen" textKey="document.metadades.nti.origen" optionItems="${ntiOrigen}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true" comment="expedient.tipus.document.form.camp.nti.origen.comentari"/>
						<hel:inputSelect name="ntiEstadoElaboracion" textKey="document.metadades.nti.estado.elaboracion" optionItems="${ntiEstadoElaboracion}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true" comment="expedient.tipus.document.form.camp.nti.estado.elaboracion.comentari"/>
						<hel:inputSelect name="ntiTipoDocumental" textKey="document.metadades.nti.tipo.documental" optionItems="${ntiTipoDocumental}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true" comment="expedient.tipus.document.form.camp.nti.tipo.documental.comentari"/>
						<hel:inputText name="ntiIdOrigen" textKey="document.metadades.nti.iddoc.origen"/>
					</div>
				</c:if>
			</div>

		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" style="float: none;" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
<c:choose>
	<c:when test="${adjuntar}">
			<button class="btn btn-primary right" type="submit" style="float: none;" name="accio" value="document_adjuntar">
				<spring:message code='comuns.adjuntar' />
			</button>
	</c:when>
	<c:otherwise>
			<button class="btn btn-primary right" type="submit" style="float: none;" name="accio" value="document_modificar">
				<spring:message code='comuns.modificar' />
			</button>
	</c:otherwise>
</c:choose>
		</div>
	</form:form>
</body>
</html>