<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${empty document}">
		<c:set var="titol"><spring:message code="expedient.document.afegir"/></c:set>
		<c:set var="formAction">new</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.document.modificar"/></c:set>
		<c:set var="formAction">update</c:set>
	</c:otherwise>
</c:choose>
<c:if test="${not empty documentExpedientCommand.nom}">
	<c:set var="titol">${titol}: ${documentExpedientCommand.nom}</c:set>
</c:if>

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
	.titol-missatge {margin-left: 3px; padding-top: 10px; padding-bottom: 10px;}
	.titol-missatge label {padding-right: 10px;}
	.nav-tabs li.disabled a {pointer-events: none;}
	.tab-pane {min-height: 300px; margin-top: 25px;}
	.candau {color: #666666;}
	.select2-result-label:has(> span.candau) {cursor: not-allowed;}
</style>
<script type="text/javascript">
// <![CDATA[
	var dadesNti = [];
<c:if test="${not empty documentsNoUtilitzats}">
	<c:forEach items="${documentsNoUtilitzats}" var="d">
	dadesNti['${d.codi}'] = new Object();
	dadesNti['${d.codi}'].ntiOrigen = '${d.ntiOrigen}';
	dadesNti['${d.codi}'].ntiEstadoElaboracion = '${d.ntiEstadoElaboracion}';
	dadesNti['${d.codi}'].ntiTipoDocumental = '${d.ntiTipoDocumental}';
	dadesNti['${d.codi}'].plantilla = ${d.plantilla};
dadesNti['${d.codi}'].generarNomesTasca = ${d.generarNomesTasca};
	</c:forEach>
</c:if>

var esFirmaValida = true;

$(document).ready( function() {

	$('#arxiuNom').on('click', function() {
		$('input[name=arxiu]').click();
	});
	
	$('input[name=arxiu]').on('change', () => validateFirma('arxiu'));
	$('input[name=firma]').on('change', () => validateFirma('firma'));
	
	$('.input-group:has( > #arxiuNom)').on('click', showLoader);
	$('.input-group:has( > #firmaNom)').on('click', showLoader);
	$('input[name=arxiu]').on('cancel', hideLoader);
	$('input[name=firma]').on('cancel', hideLoader);
	
	$('#firmaNom').on('click', function() {
		$('input[name=firma]').click();
	});

	$('label[for=generarPlantilla]').append($('#generarPlantillaBtn'))
	$('#generarPlantilla').change(function() {
		if ($(this).prop('checked') == true) {
			$('#arxiuNom').attr('disabled', 'disabled');
			$('#ambFirma').attr('disabled', 'disabled');
			$('#firmaNom').attr('disabled', 'disabled');
		} else {
			$('#arxiuNom').removeAttr('disabled');
			$('#ambFirma').removeAttr('disabled');
			$('#firmaNom').removeAttr('disabled');
		}
	}).change();

	<c:choose>
	<c:when test="${ambDocument}">
		$('#selDocument').hide();
		$('#generarPlantilla').closest('.form-group').hide();
		$('#generarPlantilla').prop('checked', false).change();
		let generarPlantilla = ${documentExpedientCommand.generarPlantilla};
		if (generarPlantilla) {
			// Per documents tipus plantlilla mostra un enllaç a la generació de documents
			var href = '<c:url value="/modal/v3/expedient/${expedientId}/proces/${document != null? document.processInstanceId : processInstanceId}/document/${documentExpedientCommand.codi}/generar"/>';
			console.log(href);
			$('#generarPlantillaBtn').attr('href', href);
			$('#generarPlantilla').closest('.form-group').show();
		}
	</c:when>
	<c:otherwise>
		$('#documentCodi').on('change', function() {
			var valor = $(this).val();
			if (valor == '##adjuntar_arxiu##') {
				$("#titolArxiu").show();
			} else {
				$("#titolArxiu").hide();
				$('#pipella-general a').click();
			}
		}).click();

		function formatDisabled(opt) {
			let originalOpt = opt.element;
			let text = opt.text;
			if ($(originalOpt).data('locked') == true) {
				return text + ' <span class="fa fa-lock pull-right candau">';
			} else {
				return text;
			}
		}

		// Carrega dades nti per defecte
		$('#documentCodi')
			.select2({
				language: "${idioma}",
				formatResult: formatDisabled})
			.change(function() {
				var documentCodi = $(this).val();
				$('#generarPlantilla').closest('.form-group').hide();
				$('#generarPlantilla').prop('checked', false).change();

				if (dadesNti[documentCodi]) {
					$('#ntiOrigen').val(dadesNti[documentCodi].ntiOrigen).change();
					$('#ntiEstadoElaboracion').val(dadesNti[documentCodi].ntiEstadoElaboracion).change();
					$('#ntiTipoDocumental').val(dadesNti[documentCodi].ntiTipoDocumental).change();
				if (dadesNti[documentCodi].plantilla && !dadesNti[documentCodi].generarNomesTasca) {
						// Per documents tipus plantlilla mostra un enllaç a la generació de documents
						var href = '<c:url value="/modal/v3/expedient/${expedientId}/proces/${document != null? document.processInstanceId : processInstanceId}/document/{documentCodi}/generar"/>';
						href = href.replace("{documentCodi}", $('#documentCodi').val());
						console.log(href);
						$('#generarPlantillaBtn').attr('href', href);
						$('#generarPlantilla').closest('.form-group').show();
					}
				} else {
					$('#ntiOrigen,#ntiEstadoElaboracion,ntiTipoDocumental').val('').change();
				}
			})
			.change();
	</c:otherwise>
	</c:choose>

	// Errors en les pipelles
	$('.tab-pane').each(function() {
		if ($('.has-error', this).length > 0) {
			$('a[href="#' + $(this).attr('id') + '"]').append(' <span class="fa fa-exclamation-triangle text-danger"/>');
		}
	});

	$('input[type=checkbox][name=ambFirma]').on('change', function() {
		if($(this).prop("checked") == true){
			$('#input-firma').removeClass('hidden');
			$('input[tpe=radio][name=tipusFirma]:checked').change();
		} else {
			$('#input-firma').addClass('hidden');
			if(!esFirmaValida) {
				esFirmaValida = true;
				$('#firma').val(null);
				$('#firmaNom').val(null);
				$('input[type=radio][name=tipusFirma][value=ADJUNT]').click();
				$('button[name="accio"]', window.parent.document).removeAttr('disabled');
			}
		}
	});
	
	$('input[type=radio][name=tipusFirma]').change(function() {
		if ($(this).val() == 'SEPARAT') {
			$('#input-firma-arxiu').removeClass('hidden');
		} else {
			$('#input-firma-arxiu').addClass('hidden');
		}
	});

	$('input[type=checkbox][name=ambFirma]').change();
	$('input[type=radio][name=tipusFirma]:checked').change();

	$("#arxiu").change(function (){
		$('#arxiuNom').val($(this).val().split('\\').pop());
	});
	$("#firma").change(function (){
		$('#firmaNom').val($(this).val().split('\\').pop());
	});
});

function mostrarAmagarFile() {
	$("#amagarFile").removeClass("hide");
	$("#downloadUrl").hide();
	$("#removeUrl").hide();
	$("#modificarArxiu").val(true);
}

function validateFirma(tipus) {
	
	if(!${isArxiuActiu}) {
		hideLoader();
		$('button[name="accio"]', window.parent.document).removeAttr('disabled');
		return;
	}
	
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
	<c:url value="/v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/descarregar" var="downloadUrl"/>
	<form:form cssClass="form-horizontal form-tasca" action="${formAction}" enctype="multipart/form-data" method="post" commandName="documentExpedientCommand">
		<div class="inlineLabels">
			<form:hidden path="codi"/>
			<form:hidden path="docId"/>
			<form:hidden path="expedientId"/>
			<form:hidden path="clearFirmes"/>
			<form:hidden path="validarArxius"/>
			<c:if test="${ambDocument}"><form:hidden path="documentCodi"/></c:if>
			<input type="hidden" id="processInstanceId" name="processInstanceId" value="${document.processInstanceId}"/>
			<input type="hidden" id="modificarArxiu" name="modificarArxiu" value="false"/>
<c:if test="${not ambDocument}">
	<c:choose>
		<c:when test="${empty document}">
			<div id="selDocument" class="form-group">
				<label class="control-label col-xs-5 obligatori" for="documentCodi">Document</label>
				<div id="elDocument_controls" class="col-xs-7">
					<form:select path="documentCodi" cssClass="form-control" id="documentCodi">
						<optgroup label="<spring:message code='expedient.document.adjuntar.document'/>">
							<option value="##adjuntar_arxiu##"><spring:message code="expedient.document.adjuntar.document"/></option>
						</optgroup>
						<optgroup label="<spring:message code='expedient.nou.document.existent'/>">
							<c:forEach var="opt" items="${documentsNoUtilitzats}">
								<c:if test="${opt.visible}">
									<c:set var="bloquejat" value="${!opt.editable}"/>
									<form:option value="${opt.codi}" disabled="${bloquejat}" data-locked="${bloquejat}">${opt.documentNom}</form:option>
								</c:if>
							</c:forEach>
						</optgroup>
					</form:select>
					<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="documentCodi"/></p></c:if>
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<h4 class="titol-missatge">
				<label><c:choose><c:when test="${document.adjunt}">${document.adjuntTitol}</c:when><c:otherwise>${document.documentNom}</c:otherwise></c:choose></label>
 				<c:if test="${empty document.signaturaPortasignaturesId && not document.signat}">
					<a title="<spring:message code='comuns.descarregar' />" id="downloadUrl" href="${downloadUrl}">
						<i class="fa fa-download"></i>
					</a>
				</c:if>
			</h4>
		</c:otherwise>
	</c:choose>
</c:if>
			<div class="alert alert-danger" id="firmaError" style="display: none;"></div>
			
			<c:if test="${expedient.ntiActiu}">
				<div>
					<ul class="nav nav-tabs" role="tablist">
						<li id="pipella-general" class="active"><a href="#dades-generals" role="tab" data-toggle="tab"><spring:message code="expedient.document.pipella.general"/></a></li>
						<li id="pipella-nti"><a href="#dades-nti" role="tab" data-toggle="tab"><spring:message code="expedient.document.pipella.nti"/></a></li>
					</ul>
				</div>
			</c:if>

			<div class="tab-content">
				<div id="dades-generals" class="tab-pane in active">
					<div id="titolArxiu" style="display: ${document.adjunt ? "inline" : "none"}">
						<hel:inputText required="true" name="nom" textKey="expedient.document.titol" placeholderKey="expedient.document.titol"/>
					</div>
					<hel:inputDate required="true" name="data" textKey="expedient.document.data" placeholder="dd/mm/aaaa"/>

					<c:if test="${empty document || document.plantilla}">
						<hel:inputCheckbox name="generarPlantilla" textKey="expedient.document.form.camp.generar.plantilla" info="expedient.document.form.camp.generar.plantilla.info"></hel:inputCheckbox>
						<div style="display: none;">
							<a 	id="generarPlantillaBtn"
								class="icon"
								style="font-weight: bold;"
								title="<spring:message code='expedient.document.form.camp.generar.descarregar'/>"
								href="<c:url value="/modal/v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.documentCodi}/generar"/>">
			 					<i class="fa fa-file-text-o fa-sm"></i>
			 				</a>
						</div>
					</c:if>

					<c:set var="campErrors"><form:errors path="arxiu"/></c:set>
					<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>">
						<label class="control-label col-xs-4 obligatori" for="arxiu"><spring:message code="expedient.document.arxiu"/></label>
				        <div class="col-xs-8 arxiu">
				            <div class="input-group">
				            	<input type="text" id="arxiuNom" name="arxiuNom" class="form-control" placeholder="${document.arxiuNom}"/>
				                <span class="input-group-btn">
				                    <span class="btn btn-default btn-file">
				                        <spring:message code='expedient.document.arxiu' />… <input type="file" id="arxiu" name="arxiu"/>
				                    </span>
				                </span>
				            </div>
							<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="arxiu"/></p></c:if>
						</div>
					</div>

					<hel:inputCheckbox name="ambFirma" textKey="expedient.document.form.camp.amb.firma"></hel:inputCheckbox>
					
					<div class="alert alert-warning" id="firmaAlert" style="display: none;"></div>
					
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
					<div id="dades-nti" class="tab-pane" style="witdth:100%">
						<hel:inputSelect name="ntiOrigen" textKey="document.metadades.nti.origen" optionItems="${ntiOrigen}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true" comment="expedient.tipus.document.form.camp.nti.origen.comentari"/>
						<hel:inputSelect name="ntiEstadoElaboracion" textKey="document.metadades.nti.estado.elaboracion" optionItems="${ntiEstadoElaboracion}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true" comment="expedient.tipus.document.form.camp.nti.estado.elaboracion.comentari"/>
						<hel:inputSelect name="ntiTipoDocumental" textKey="document.metadades.nti.tipo.documental" optionItems="${ntiTipoDocumental}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true" comment="expedient.tipus.document.form.camp.nti.tipo.documental.comentari"/>
						<hel:inputText name="ntiIdOrigen" textKey="document.metadades.nti.iddoc.origen"/>
					</div>
				</c:if>
			</div>
		</div>
		<c:if test="${modificarArxiu}">
<script type="text/javascript">mostrarAmagarFile();</script>
		</c:if>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" data-modal-cancel="true" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
<c:choose>
	<c:when test="${empty document}">
			<button class="btn btn-primary right" type="submit" name="accio" value="document_adjuntar">
				<spring:message code='comuns.afegir' />
			</button>
	</c:when>
	<c:otherwise>
			<button class="btn btn-primary right" type="submit" name="accio" value="document_modificar">
				<spring:message code='comuns.modificar' />
			</button>
	</c:otherwise>
</c:choose>
		</div>
	</form:form>
</body>
</html>