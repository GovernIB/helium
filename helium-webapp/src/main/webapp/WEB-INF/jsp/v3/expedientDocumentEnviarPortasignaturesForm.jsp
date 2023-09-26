<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="charSearch" value='"' />
<c:set var="charReplace" value='\\"' />
<c:choose>
	<c:when test="${document.adjunt}">
		<c:set var="titol"><spring:message code="expedient.document.enviar.portasignatures.titol.adjunt" arguments="${document.adjuntTitol}"/></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.document.enviar.portasignatures.titol.document" arguments="${document.documentNom}"/></c:set>
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
	<script src="<c:url value="/js/webutil.common.js"/>"></script>

<style type="text/css">
.rmodal {
    display:    none;
    position:   fixed;
    z-index:    1000;
    top:        0;
    left:       0;
    height:     100%;
    width:      100%;
    background: rgba( 255, 255, 255, .8 ) 
                url('<c:url value="/img/loading.gif"/>') 
                50% 50% 
                no-repeat;
}
.rmodal_carrecs {
    display:    none;
    position:   absolute;
    z-index:    1000;
    top:        0;
    left:       0;
    height:     100%;
    width:      100%;
    background: rgba( 255, 255, 255, .8 ) 
                url('<c:url value="/img/loading.gif"/>') 
                50% 50% 
                no-repeat;
}
body.loading {
    overflow: hidden;   
}
body.loading .rmodal {
    display: block;
}

.ui-dialog {
	z-index: 1000;
}
.modal-dialog {
	width: 100%;
	height: 100%;
	margin: 0;
	padding: 0;
}

.modal-content {
	height: auto;
	min-height: 100%;
}

.iframe_container {
	position: relative;
	width: 100%;
	height: 97vh;
	padding-bottom: 0;
}

.iframe_content {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
}
#fluxModal {
	margin: 1%;
}
.portafirmesEnviarFluxId_btn_edicio:hover {
	cursor: pointer;
}
.flux_disabled {
	pointer-events: none;
	cursor: not-allowed;
}
.flux_disabled:hover {
	cursor: not-allowed;
}
.carrec-selected {
	font-weight: bold;
	background-color: #1a3d5c;
	border-radius: 2px;
}
div[class^="carrec_"] > a {
	color: black;
}
.carrec-selected > a {
	color: #FFF !important;
}
div[class^="carrec_"] {
	padding: 1%;
	margin: 2px;
}
div[class^="carrec_"]:hover {
	background-color: #1a3d5c;;
}
div[class^="carrec_"]:hover a{
	color: #FFF;
}
div[class^="carrec_"] > a:hover {
	text-decoration: none;
	cursor: pointer;
}
div.dropdown-menu {
	left: auto;
	right: 0;
	padding: 1%;
	width: 70%;
}
div.dropdown-menu.loading {
    overflow: hidden;   
    height: 100px;
}
div.dropdown-menu.loading .rmodal_carrecs {
    display: block;
}
</style>	
</head>
<body>
	<c:if test="${potFirmar}">
		<c:set var="formAction">
			<c:url value="/v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/enviarPortasignatures"/>
		</c:set>

		<form:form 	cssClass="form-horizontal content" action="${formAction}" enctype="multipart/form-data" method="post" commandName="documentExpedientEnviarPortasignaturesCommand">
			<div>
				<input type="hidden" name="id" value="${documentExpedientEnviarPortasignaturesCommand.id}"/>
				<hel:inputText required="true" name="motiu" textKey="expedient.document.enviar.portasignatures.camp.motiu"/>
				<hel:inputSelect name="portafirmesPrioritatTipus" textKey="expedient.document.enviar.portasignatures.camp.prioritat" 
							optionItems="${portafirmesPrioritatEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" 
							disabled="${bloquejarCamps}"/>
				<hel:inputSelect required="true" name="annexos" multiple="true" textKey="expedient.document.enviar.portasignatures.camp.annexos" placeholderKey="expedient.document.enviar.portasignatures.camp.annexos.placeholder" optionItems="${annexos}" optionValueAttribute="id" optionTextAttribute="documentNom" labelSize="4"/>
				<c:if test="${documentExpedientEnviarPortasignaturesCommand.portafirmesActiu}">
					<c:if test="${documentExpedientEnviarPortasignaturesCommand.portafirmesFluxTipus eq 'FLUX'}">
						<label class="control-label success-label hidden col-xs-4"></label>
						<hel:inputSelect name="portafirmesEnviarFluxId" textKey="expedient.tipus.document.form.camp.id.flux.firma" emptyOption="true" botons="true"  
							icon="fa fa-external-link" iconAddicional="fa fa-eye" buttonMsg="${buttonTitle}"
							placeholderKey="expedient.tipus.document.form.camp.id.flux.firma.buit" />

						<!-- Camp hidden on s'informarà des de portafirmesModalTancar en el cas de crear-se un flux temporal per part de l'usuari. -->
						<hel:inputHidden name="portafirmesNouFluxId"/>
												
						<label class="control-label col-xs-4"></label>
						<c:if test="${!nouFluxDeFirma}">
							<p class="comment col-xs-8"><spring:message code="expedient.document.enviar.portasignatures.camp.flux.definit.comentari" /></p>
						</c:if>
					</c:if>
					<c:if test="${documentExpedientEnviarPortasignaturesCommand.portafirmesFluxTipus eq 'SIMPLE'}">	
						<hel:inputSuggest 
							inline="false" 
							name="portafirmesResponsables" 
							urlConsultaInicial="/helium/v3/personaCarrec/suggestInici" 
							urlConsultaLlistat="/helium/v3/expedient/persona/suggest" 
							textKey="expedient.document.enviar.portasignatures.camp.responsables" 
							placeholderKey="expedient.document.enviar.portasignatures.camp.responsables" 
							multiple="true"/>

					<!-- Botó i desplegable de responsables -->
					<div class="form-group">
						<label class="col-xs-4"><span id="portafirmesCarrecsSpin" class="fa fa-refresh fa-spin" style="display:none; float: right;"></span></label>
						<div class="col-xs-8">
							<table border="0" width="100%">
								<tr>
									<td>
										<a class="btn btn-default btn-sm portafirmesCarrecsBtn" onclick="toggleCarrecs()" title="<spring:message code='expedient.document.enviar.portasignatures.camp.carrecs.info'/>"><i class="fa fa-star"></i></a>
									</td>
									<td style="width:100%;">
										<div id="portafirmesCarrecsSelectDiv" class="" style="display: none;">
											<select id="portafirmesCarrecsSelect" style="width: 100%;">
												<option value"">&nbsp;</option>
											</select>
										</div>
									</td>
								</tr>
							</table>		
						</div>
					</div>
			
						<hel:inputSelect name="portafirmesSequenciaTipus" textKey="expedient.tipus.document.form.camp.portafirmes.sequencia.firma" 
							optionItems="${portafirmesSequenciaTipusEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" 
							disabled="${bloquejarCamps}"/>
					</c:if>
							
				</c:if>
			</div>
			
			<div id="modal-botons" class="well">
				<c:if test="${potFirmar}">
					<button type="submit" class="btn btn-success"><span class="fa fa-envelope-o"></span> <spring:message code="expedient.document.enviar.portasignatures.boto.enviar.portasignatures"/></button>
				</c:if>			
				<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
			</div>
			
			<div style="height: 20px;"></div>
			
		</form:form>

		<div class="flux_container"></div>
		<div class="rmodal"></div>

	</c:if>
	
	<c:if test="${!potFirmar}">
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.tancar"/></button>
		</div>
	</c:if>
	
	<script type="text/javascript">
		// <![CDATA[
		$(document).ready(function() {
   			//<c:if test="${heretat}">
			webutilDisableInputs($('#documentExpedientEnviarPortasignaturesCommand'));
			//</c:if>

			$(".portafirmesFluxId_btn_edicio").attr("title", "<spring:message code="expedient.tipus.document.form.camp.portafirmes.flux.iniciar"/>");

			// //crear nou flux
			$(".portafirmesEnviarFluxId_btn_edicio").on('click', function() {
				let documentNom = "${fn:replace(documentExpedientEnviarPortasignaturesCommand.nom, charSearch, charReplace)}";
				crearFlux(documentNom);
			});
			
			$(".portafirmesEnviarFluxId_btn_edicio").attr("title", "<spring:message code="expedient.tipus.document.form.camp.portafirmes.flux.iniciar"/>");
			$(".portafirmesEnviarFluxId_btn_addicional").attr("title", "<spring:message code="expedient.document.enviar.portasignatures.camp.flux.show"/>");
						
			//mostrar flux actual
			$(".portafirmesEnviarFluxId_btn_addicional").on('click', function() {
				$(this).find('i').toggleClass('fa-eye fa-eye-slash');
				if ($(this).find('i').hasClass('fa-eye-slash')) {
					var portafirmesEnviarFluxId = $("#portafirmesEnviarFluxId").val();
					$(".portafirmesEnviarFluxId_btn_addicional").attr("title", "<spring:message code="expedient.document.enviar.portasignatures.camp.flux.hide"/>");
					recuperarFluxSeleccionat(portafirmesEnviarFluxId);
				} else {
					$(".portafirmesEnviarFluxId_btn_addicional").attr("title", "<spring:message code="expedient.document.enviar.portasignatures.camp.flux.show"/>");
					amagarFluxSeleccionat();
				}
			});	
			
			$("#portafirmesEnviarFluxId").on('change', function () {
				var portafirmesEnviarFluxId = $(this).val();
				if(portafirmesEnviarFluxId != null && portafirmesEnviarFluxId != '') {
					$(".portafirmesEnviarFluxId_btn_addicional").removeClass('disabled');
					// si el flux és visible el recarrega o actualitza
					if ($(".portafirmesEnviarFluxId_btn_addicional").find('i').hasClass('fa-eye-slash')) {
						recuperarFluxSeleccionat(portafirmesEnviarFluxId);						
					}
				} else {
					$(".portafirmesEnviarFluxId_btn_addicional").addClass('disabled');
					// Buida el contenidor
					$('.flux_container').html('');
				}
			}).change();

			$('.modal-cancel').on('click', function(){
					localStorage.removeItem('transaccioId');
			});
						
			$(".portafirmesResponsables_btn").attr("title", "<spring:message code="expedient.tipus.document.form.camp.portafirmes.carrecs"/>");
			
			$("#portafirmesResponsables").on('select2:unselecting', function (e) {
					var optionRemoved = e.params.args.data.id;
					$("#portafirmesResponsables option[value='" + optionRemoved + "']").remove();
			});
			
			// Obtenir plantilles
			obtenirPlantilles();
		});
					
		var mostrarCarrecs = false;
		var carrecsCarregats = false;
					
		function toggleCarrecs() {
			mostrarCarrecs = !mostrarCarrecs;
			if (mostrarCarrecs) {
				if (!carrecsCarregats) {
					recuperarCarrecs();
					carrecsCarregats = true;
				} else {
					$('#portafirmesCarrecsSelectDiv').show();
				}
			} else {
				$('#portafirmesCarrecsSelectDiv').hide();
			}
		}
					
		function recuperarCarrecs() {
			$('#portafirmesCarrecsSpin').show();
			$.ajax({
				type: 'GET',
				dataType: "json",
				url: "<c:url value="/v3/portasig/carrecs"/>",
				success: function(carrecs) {
					if (carrecs) {
						$.each(carrecs, function(i, carrec) {
							var persona = '';
							if (carrec.usuariPersonaNom) {
								persona = ' (' + carrec.usuariPersonaNom + ' - ' + carrec.usuariPersonaNif + ' - ' + carrec.usuariPersonaId + ')';
							}
							var nomCarrec = carrec.carrecName + persona;
							var newOption = new Option(nomCarrec, 'CARREC[' + carrec.carrecId + ']', false, false);
							$('#portafirmesCarrecsSelect').append(newOption).trigger('change');

						});
					}
					$('#portafirmesCarrecsSelect').trigger('change');
				},
				error: function (error) {
					webutilAlertaWarning("Hi ha hagut un problema recuperant els càrrecs " + error.statusText, '#divAlertesFlux');
				},
				statusCode: {
			        500: function(error) {
						webutilAlertaWarning("Hi ha hagut un problema recuperant els càrrecs " + error.statusText, '#divAlertesFlux');
			        }
			   	}, 
			   	complete: function() {
					$('#portafirmesCarrecsSelectDiv').show();
					$('#portafirmesCarrecsSpin').hide();
			   	}
			});

			// Recupera els càrrecs i quan se seleccionen s'afegeixen a la select de responsables.
			$('#portafirmesCarrecsSelect').select2({
				    width: 'resolve',
				    theme: "bootstrap",
				    placeholder: "<spring:message code='expedient.document.enviar.portasignatures.camp.carrecs.placeholder'/>",
				    allowClear: true,
				    minimumResultsForSearch: 3
			}).on('change', function() {
				if ($(this).val() != '') {
					seleccionarCarrec($(this).val());
				}
			});

		}

		// Afegeix el càrrec a la llista de responsables si no existeix o la treu en cas que existeixi.
		function seleccionarCarrec(carrec) {
			let responsables;
			if ($('#portafirmesResponsables').val() != '') {
				responsables = $('#portafirmesResponsables').val().split(',');
			} else {
				responsables = [];
			}
			if (responsables.includes(carrec)) {
				responsables.splice( responsables.indexOf(carrec), 1);
			} else {
				responsables.push(carrec);				
			}
			$('#portafirmesResponsables').val(responsables).change();
		}
						
			function adjustModalPerFlux(amagar) {
				let $iframe = $(window.frameElement);
				$iframe.css('height', '100%');
				$iframe.css('min-height', amagar ? '370px' : '100%');
				$iframe.find('body').css('min-height', amagar ? '370px' : '100%');
				$iframe.parent().css('height', '600px');
				$iframe.closest('div.modal-content').css('height',  'auto');
				$iframe.closest('div.modal-dialog').css({
					//'height':'auto',
					'height': '100%',
					'margin': '3% auto',
					'padding': '0'
				});
				if (amagar) {
					$iframe.closest('div.modal-lg').css('width', '95%');
					$iframe.parent().next().addClass('hidden');
				}
			}

			function recuperarFluxSeleccionat(portafirmesEnviarFluxId) {
				
				if (portafirmesEnviarFluxId != null && portafirmesEnviarFluxId != '') {
					$.ajax({
						type: 'GET',
						contentType: "application/json; charset=utf-8",
						dataType: "json",
						data: {plantillaId: portafirmesEnviarFluxId},
						url: "<c:url value="/modal/v3/expedient/portafirmes/flux/mostrar"/>",
						success: function(transaccioResponse, textStatus, XmlHttpRequest) {
							if (transaccioResponse != null && !transaccioResponse.error) {
								mostrarFluxSeleccionat(transaccioResponse.urlRedireccio);
							} else if (transaccioResponse != null && transaccioResponse.error) {
								let currentIframe = window.frameElement;
								var alertDiv = '<div class="alert alert-danger" role="alert">' + 
													'<a class="close" data-dismiss="alert">×</a><span>' + transaccioResponse.errorDescripcio + '</span>' + 
												'</div>';
								$('form').prev().find('.alert').remove();
								$('form').prev().prepend(alertDiv);
								webutilModalAdjustHeight();
							}
							$body = $("body");
							$body.addClass("loading");
						},
						error: function(error) {
							if (error != null && error.responseText != null) {
								let currentIframe = window.frameElement;
								var alertDiv = '<div class="alert alert-danger" role="alert">' + 
													'<a class="close" data-dismiss="alert">×</a><span>' + error.responseText + '</span>' + 
												'</div>';
								$('form').prev().find('.alert').remove();
								$('form').prev().prepend(alertDiv);
								webutilModalAdjustHeight();
							}
						}
					});
				} else {
					alert("No s'ha seleccionat cap flux");
				}
			}

			function mostrarFluxSeleccionat(urlPlantilla) {
				adjustModalPerFlux(false);
				var plantilla = '<hr>' + 
								'<div class="iframe_container">' +
									'<iframe onload="removeLoading()" id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + urlPlantilla + '"></iframe>' +
								'</div>';
				$('.flux_container').html(plantilla);	
			}

			function amagarFluxSeleccionat() {
				$('.flux_container').empty();
				let $iframe = $(window.frameElement);
				$iframe.removeAttr('style').css('height', "375px");
				$iframe.parent().removeAttr('style').css('height', "375px");
				$iframe.find('body').removeAttr('style').css('height', "375px");
			}

			function removeLoading() {
						$body = $("body");
						$body.removeClass("loading");
			}

			function crearFlux(documentNom) {
				$.ajax({
					type: 'GET',
					contentType: "application/json; charset=utf-8",
					dataType: "json",
					data: {nom: documentNom},
					url: "<c:url value="/v3/expedient/${expedientId}/document/portafirmesFlux/iniciarTransaccio"/>",
					success: function(transaccioResponse, textStatus, XmlHttpRequest) {
						if (transaccioResponse != null && !transaccioResponse.error) {
							localStorage.setItem('transaccioId', transaccioResponse.idTransaccio);
							$('.content').addClass("hidden");
							var fluxIframe = '<div class="iframe_container">' +
									'<iframe onload="removeLoading()" id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + transaccioResponse.urlRedireccio + '"></iframe>' +
									'</div>';
							$('.flux_container').html(fluxIframe);
							adjustModalPerFlux(true);
							$body = $("body");
							$body.addClass("loading");
						} else if (transaccioResponse != null && transaccioResponse.error) {
							let currentIframe = window.frameElement;
							var alertDiv = '<div class="alert alert-danger" role="alert">' +
									'<a class="close" data-dismiss="alert">×</a><span>' + transaccioResponse.errorDescripcio + '</span>' +
									'</div>';
							$('form').prev().find('.alert').remove();
							$('form').prev().prepend(alertDiv);
							webutilModalAdjustHeight();
						}
					},
					error: function(error) {
						if (error != null && error.responseText != null) {
							let currentIframe = window.frameElement;
							var alertDiv = '<div class="alert alert-danger" role="alert">' +
									'<a class="close" data-dismiss="alert">×</a><span>' + error.responseText + '</span>' +
									'</div>';
							$('form').prev().find('.alert').remove();
							$('form').prev().prepend(alertDiv);
							webutilModalAdjustHeight();
						}
					}
				});
			}
			
			function obtenirPlantilles() {

				// Càrregal de les opcions del desplegable de fluxos
				$.ajax({
					type: 'GET',
					dataType: "json",
					url: "<c:url value="/v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${documentExpedientEnviarPortasignaturesCommand.id}/flux/plantilles"/>",
						success: function(data) {
							var plantillaActual = "${portafirmesFluxSeleccionat}";
							var selPlantilles = $("#portafirmesEnviarFluxId");
							selPlantilles.empty();
							selPlantilles.append("<option value=\"\"></option>");
							selPlantilles.append("<option value=\"\"></option>");
							if (data) {
								var items = [];
								$.each(data, function(i, val) {
									items.push({
										"id": val.fluxId,
										"text": val.nom
									});
									selPlantilles.append("<option value=\"" + val.fluxId + "\">" + val.nom + "</option>");
								});
							}
							var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
							selPlantilles.select2(select2Options);
							if (plantillaActual != '') {
								selPlantilles.val(plantillaActual);
							}
							selPlantilles.change();
						},
						error: function (error) {
							var selPlantilles = $("#portafirmesEnviarFluxId");
							selPlantilles.empty();
							selPlantilles.append("<option value=\"\"></option>");
							var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
							selPlantilles.select2(select2Options);
						}
				});
			}
				
		// ]]>
	</script>			
		
</body>
</html>