<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="charSearch" value='"' />
<c:set var="charReplace" value='\\"' />
<c:choose>
	<c:when test="${!heretat}">
		<c:choose>
			<c:when test="${empty expedientTipusDocumentCommand.id}"><
				<c:set var="titol"><spring:message code="expedient.tipus.document.form.titol.nou"/></c:set>
				<c:set var="formAction">new</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="titol"><spring:message code="expedient.tipus.document.form.titol.modificar"/></c:set>
				<c:set var="formAction">update</c:set>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.tipus.document.form.titol.visualitzar"/></c:set>
		<c:set var="formAction">none</c:set>		
	</c:otherwise>
</c:choose>
<!-- URL pels fluxos per tipus d'expedient o definició de proces -->
<c:set var="fluxosBaseUrl">
	<c:choose>
		<c:when test="${not empty expedientTipusDocumentCommand.definicioProcesId}">
			/v3/definicioProces/${expedientTipusDocumentCommand.definicioProcesId}
		</c:when>
		<c:otherwise>
			/v3/expedientTipus/${expedientTipusDocumentCommand.expedientTipusId}
		</c:otherwise>
	</c:choose>
</c:set>

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
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
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
.portafirmesFluxId_btn_edicio:hover {
	cursor: pointer;
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
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="expedientTipusDocumentCommand">
		<div>			
			<input type="hidden" name="id" value="${expedientTipusDocumentCommand.id}"/>
			<input type="hidden" name="eliminarContingut" id="eliminarContingut" value="false"/>
			<hel:inputText required="true" name="codi" textKey="expedient.tipus.document.form.camp.codi" />
			<hel:inputText required="true" name="nom" textKey="expedient.tipus.document.form.camp.nom" />
			<hel:inputTextarea name="descripcio" textKey="expedient.tipus.document.form.camp.descripcio" />
			<!-- Resol la url de descàrrega per tipus d'expedient o definicions de procés -->
			<c:choose>
				<c:when test="${not empty expedientTipusDocumentCommand.definicioProcesId}">
					<c:set var="arxiuUrl">/v3/definicioProces/${jbmpKey}/${expedientTipusDocumentCommand.definicioProcesId}/document/${expedientTipusDocumentCommand.id}/download</c:set>
				</c:when>
				<c:otherwise>
					<c:set var="arxiuUrl">/v3/expedientTipus/${expedientTipusDocumentCommand.expedientTipusId}/document/${expedientTipusDocumentCommand.id}/download</c:set>
				</c:otherwise>
			</c:choose>
 			<hel:inputFile 
	 			name="arxiuContingut" 
	 			required="false" 
	 			textKey="expedient.tipus.document.form.camp.arxiu"
	 			fileName="arxiuNom"
	 			fileUrl="${arxiuUrl}"
	 			fileExists="${not empty expedientTipusDocumentCommand.arxiuContingut}" />		
			<hel:inputCheckbox name="plantilla" textKey="expedient.tipus.document.form.camp.plantilla" />
			<hel:inputText name="convertirExtensio" textKey="expedient.tipus.document.form.camp.gen_ext" />
			<hel:inputCheckbox name="adjuntarAuto" textKey="expedient.tipus.document.form.camp.adj_auto" />
			<hel:inputCheckbox name="generarNomesTasca" textKey="expedient.tipus.document.form.camp.generar.nomes.tasca" info="expedient.tipus.document.form.camp.generar.nomes.tasca.info"/>
			<hel:inputCheckbox name="notificable" textKey="expedient.tipus.document.form.camp.notificable" />
			<hel:inputSelect name="campId" textKey="expedient.tipus.document.form.camp.camp_data" required="false" emptyOption="true" placeholderKey="expedient.tipus.document.form.camp.camp_data.buit" optionItems="${camps}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			<hel:inputText name="extensionsPermeses" textKey="expedient.tipus.document.form.camp.ext_perm" comment="expedient.tipus.document.form.camp.ext_perm.comment"/>
			<hel:inputText name="contentType" textKey="expedient.tipus.document.form.camp.ctype" comment="expedient.tipus.document.form.camp.ctype.comment" />
			<hel:inputText name="custodiaCodi" textKey="expedient.tipus.document.form.camp.codi_custodia" comment="expedient.tipus.document.form.camp.codi_custodia.comment" />
			<hel:inputCheckbox name="ignored" textKey="expedient.tipus.document.form.camp.ignored" comment="expedient.tipus.document.form.camp.ignored.comment"/>
			
			<!-- Portasignatures -->
			<fieldset>
				<legend><spring:message code="expedient.tipus.document.form.legend.enviament.portasignatures"></spring:message></legend>
				<hel:inputText name="tipusDocPortasignatures" textKey="expedient.tipus.document.form.camp.tipus_doc" comment="expedient.tipus.document.form.camp.tipus_doc.comment" />

				<hel:inputSelect name="portafirmesFluxTipus" 
						textKey="expedient.tipus.document.form.camp.portafirmes.flux.tipus" 
						info="expedient.tipus.document.form.camp.portafirmes.flux.tipus.info" 
						optionItems="${fluxtipEnumOptions}" 
						optionValueAttribute="value" 
						optionTextKeyAttribute="text" 
						disabled="${bloquejarCamps}" required="false" emptyOption="true"/>

				<div id="flux_portafib" class="flux_portafib">
					
					<div id="divAlertesFlux"></div>
					
					<hel:inputSelect name="portafirmesFluxId" textKey="expedient.tipus.document.form.camp.id.flux.firma" emptyOption="true" botons="true"  
						icon="fa fa-external-link" iconAddicional="fa fa-trash-o" buttonMsg="${buttonTitle}"
						placeholderKey="expedient.tipus.document.form.camp.id.flux.firma.buit"  required="true"/>
				</div>
				
				<div id="flux_simple" class="flux_simple">
					<hel:inputSuggest 
							inline="false" 
							name="portafirmesResponsables" 
							urlConsultaInicial="/helium/v3/personaCarrec/suggestInici" 
							urlConsultaLlistat="/helium/v3/expedient/persona/suggest" 
							textKey="expedient.document.enviar.portasignatures.camp.responsables" 
							placeholderKey="expedient.document.enviar.portasignatures.camp.responsables" 
							multiple="true"
							required="true"/>
					
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
							disabled="${bloquejarCamps}" required="true"/>
				</div>
				
				<div id="div_portafirmesActiu" class="div_portafirmesActiu">
					<hel:inputCheckbox name="portafirmesActiu" textKey="expedient.tipus.document.form.camp.portafirmes.actiu" info="expedient.tipus.document.form.camp.portafirmes.actiu.info" />
				</div>
				
			</fieldset>
			
			<!-- Metadades NTI -->
			<fieldset>
				<legend><spring:message code="expedient.tipus.document.form.legend.metadades.nti"></spring:message></legend>
				<hel:inputSelect name="ntiOrigen" textKey="expedient.tipus.document.form.camp.nti.origen" optionItems="${ntiOrigen}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true" comment="expedient.tipus.document.form.camp.nti.origen.comentari"/>
				<hel:inputSelect name="ntiEstadoElaboracion" textKey="expedient.tipus.document.form.camp.nti.estado.elaboracion" optionItems="${ntiEstadoElaboracion}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true" comment="expedient.tipus.document.form.camp.nti.estado.elaboracion.comentari"/>
				<hel:inputSelect name="ntiTipoDocumental" textKey="expedient.tipus.document.form.camp.nti.tipo.documental" optionItems="${ntiTipoDocumental}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true" comment="expedient.tipus.document.form.camp.nti.tipo.documental.comentari"/>
			</fieldset>
		</div>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<c:if test="${!heretat}">
				<c:choose>
					<c:when test="${empty expedientTipusDocumentCommand.id}">
						<button class="btn btn-primary right" type="submit" name="accio" value="crear">
							<span class="fa fa-plus"></span> <spring:message code='comu.boto.crear' />
						</button>
					</c:when>
					<c:otherwise>
						<button class="btn btn-primary right" type="submit" name="accio" value="modificar">
							<span class="fa fa-pencil"></span> <spring:message code='comu.boto.modificar' />
						</button>
					</c:otherwise>
				</c:choose>
			</c:if>
		</div>
	<script type="text/javascript">
		// <![CDATA[
		            
		$(document).ready(function() {

			if (window.frameElement != null) {
				let currentHeight = window.frameElement.contentWindow.document.body.scrollHeight;
				localStorage.setItem("currentIframeHeight", currentHeight);
			}
			
   			//<c:if test="${heretat}">
			webutilDisableInputs($('#expedientTipusDocumentCommand'));
			//</c:if>
			
			$("#portafirmesFluxTipus").on('change', function() {
				if($(this).val() == 'SIMPLE') {
					$('.flux_portafib').hide();
					$('.flux_simple').show();
					$('.div_portafirmesActiu').show();
				} else if ( $(this).val() == 'FLUX') {
					$('.flux_portafib').show();
					$('.flux_simple').hide();
					$('.div_portafirmesActiu').show();
				} else {
					$('.flux_portafib').hide();
					$('.flux_simple').hide();
					$('.div_portafirmesActiu').hide();
					$('#portafirmesActiu').prop( "checked", false );
				}
			}).change();

			
			$(".portafirmesFluxId_btn_edicio").on('click', function() {
				var metaDocumentNom = "${fn:replace(expedientTipusDocumentCommand.nom, charSearch, charReplace)}";
				webutilEsborrarAlertes('#divAlertesFlux');
				$.ajax({
					type: 'GET',
					dataType: "json",
					data: {nom: metaDocumentNom, plantillaId: $("#portafirmesFluxId").val()},
					url: "<c:url value="${fluxosBaseUrl}/document/iniciarTransaccio"/>",
					success: function(transaccioResponse) {
						if (transaccioResponse != null && !transaccioResponse.error) {
							localStorage.setItem('transaccioId', transaccioResponse.idTransaccio);
							$('#expedientTipusDocumentCommand').addClass("hidden");
							var fluxIframe = '<div class="iframe_container">' + 
												'<iframe onload="removeLoading()" id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + transaccioResponse.urlRedireccio + '"></iframe>' + 
								  			 '</div>';
							$('.flux_container').html(fluxIframe);	
							adjustModalPerFlux();
							$body = $("body");
							$body.addClass("loading");
						} else if (transaccioResponse != null && transaccioResponse.error) {
							webutilAlertaWarning(transaccioResponse.errorDescripcio, '#divAlertesFlux');
							webutilModalAdjustHeight();
						}
					},
					error: function(error) {
						if (error != null && error.responseJSON != null) {
							let currentIframe = window.frameElement;
							webutilModalAdjustHeight();
						}
					}
				});
			});

			// Càrregal de les opcions del desplegable de fluxos
			$.ajax({
					type: 'GET',
					dataType: "json",
					url: "<c:url value="${fluxosBaseUrl}/document/flux/plantilles"/>",
					success: function(data) {
								var plantillaActual = "${portafirmesFluxSeleccionat}";
								var selPlantilles = $("#portafirmesFluxId");
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
									selPlantilles.change();
									$(".portafirmesFluxId_btn_edicio").attr("title", "<spring:message code="expedient.tipus.document.form.camp.portafirmes.flux.editar"/>");
								}
					},
					error: function (error) {
								var selPlantilles = $("#portafirmesFluxId");
								selPlantilles.empty();
								selPlantilles.append("<option value=\"\"></option>");
								var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
								selPlantilles.select2(select2Options);
					}
			});
			

			$(".portafirmesFluxId_btn_addicional").on('click', function () {
				
				if (confirm("<spring:message code="expedient.tipus.document.form.camp.portafirmes.flux.esborrar.confirm"/>")) {

					var portafirmesFluxId = $("#portafirmesFluxId").val();
					webutilEsborrarAlertes('#divAlertesFlux');
					$.ajax({
							type: 'GET',
							dataType: "json",
								url: "<c:url value="${fluxosBaseUrl}/document/flux/esborrar/"/>" + portafirmesFluxId,
									success: function(esborrat) {
										if (esborrat) {
											webutilAlertaSuccess("<spring:message code='expedient.tipus.document.form.camp.portafirmes.flux.esborrar.ok'/>", '#divAlertesFlux');
											$("#portafirmesFluxId option[value='" + portafirmesFluxId + "']").remove();
											$("#portafirmesFluxId").val('').change();
										} else {
											webutilAlertaError("<spring:message code='expedient.tipus.document.form.camp.portafirmes.flux.esborrar.ko'/>", '#divAlertesFlux');
										}
										webutilModalAdjustHeight();
									},
									error: function (error) {
										webutilAlertaError("<spring:message code='expedient.tipus.document.form.camp.portafirmes.flux.esborrar.ko'/>", '#divAlertesFlux');
										webutilModalAdjustHeight();		
									}
						});
					}
			});

			$("#portafirmesFluxId").on('change', function () {
				var portafirmesFluxId = $(this).val();
				if(portafirmesFluxId != null && portafirmesFluxId != '') {
						$(".portafirmesFluxId_btn_edicio").attr("title", "<spring:message code="expedient.tipus.document.form.camp.portafirmes.flux.editar"/>");
						$(".portafirmesFluxId_btn_addicional").removeClass("disabled");
					} else {
						$(".portafirmesFluxId_btn_edicio").attr("title", "<spring:message code="expedient.tipus.document.form.camp.portafirmes.flux.iniciar"/>");
						$(".portafirmesFluxId_btn_addicional").addClass("disabled");
					}
			});
									
			$('.modal-cancel').on('click', function(){
					localStorage.removeItem('transaccioId');
			});
			
			// Posa les alertes del fux darrera el selector del flux
			$('#divAlertesFlux').insertAfter($('#portafirmesFluxId'));

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
								
		});

		function adjustModalPerFlux() {
					var $iframe = $(window.frameElement);
					$iframe.css('height', '100%');
					$iframe.parent().css('height', '600px');
					$iframe.closest('div.modal-content').css('height',  'auto');
					$iframe.closest('div.modal-dialog').css({
						'height':'auto',
						'height': '100%',
						'margin': '3% auto',
						'padding': '0'
					});
					$iframe.closest('div.modal-lg').css('width', '95%');
					$iframe.parent().next().addClass('hidden');
		}

		function removeLoading() {
						$body = $("body");
						$body.removeClass("loading");
		}
		
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
		}
		
		// Afegeix el càrrec a la llista de responsables si no existeix o la treu en cas que existeixi.
		function seleccionarCarrec(carrec) {
			let responsables;
			debugger;
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
		
		
		// ]]>
	</script>			
		
	</form:form>
	<div class="flux_container"></div>
	
</body>
</html>