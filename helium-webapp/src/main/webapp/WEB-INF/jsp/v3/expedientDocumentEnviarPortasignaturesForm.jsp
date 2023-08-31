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
		<form:form 	cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="documentExpedientEnviarPortasignaturesCommand">
			<div>
				<input type="hidden" name="id" value="${documentExpedientEnviarPortasignaturesCommand.id}"/>
				<hel:inputText required="true" name="motiu" textKey="expedient.document.enviar.portasignatures.camp.motiu"/>
				<hel:inputSelect name="portafirmesPrioritatTipus" textKey="expedient.document.enviar.portasignatures.camp.prioritat" 
							optionItems="${portafirmesPrioritatEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" 
							disabled="${bloquejarCamps}"/>
				<hel:inputSelect required="true" name="annexos" multiple="true" textKey="expedient.document.enviar.portasignatures.camp.annexos" placeholderKey="expedient.document.enviar.portasignatures.camp.annexos.placeholder" optionItems="${annexos}" optionValueAttribute="id" optionTextAttribute="documentNom" labelSize="4"/>
				<c:if test="${documentExpedientEnviarPortasignaturesCommand.portafirmesActiu}">
					<c:if test="${documentExpedientEnviarPortasignaturesCommand.portafirmesFluxTipus eq 'FLUX'}">
						<hel:inputSelect name="portafirmesFluxId" textKey="expedient.tipus.document.form.camp.id.flux.firma" emptyOption="true" botons="true"  
						icon="fa fa-external-link" iconAddicional="fa fa-trash-o" buttonMsg="${buttonTitle}"
						placeholderKey="expedient.tipus.document.form.camp.id.flux.firma.buit" />
						
						<c:if test="${urlFluxFirmes!=null and not empty urlFluxFirmes}">
							<div class="blocks_container">
								<iframe width="100%" height="500px" frameborder="0" allowtransparency="true" src="${urlFluxFirmes}"></iframe>
							</div>
						</c:if>
					</c:if>
					<c:if test="${documentExpedientEnviarPortasignaturesCommand.portafirmesFluxTipus eq 'SIMPLE'}">	
						<hel:inputSuggest 
							inline="false" 
							name="portafirmesResponsables" 
							urlConsultaInicial="/helium/v3/expedient/persona/suggestInici" 
							urlConsultaLlistat="/helium/v3/expedient/persona/suggest" 
							textKey="expedient.tipus.form.camp.responsableDefecteCodi" 
							placeholderKey="expedient.tipus.form.camp.responsableDefecteCodi" 
							multiple="true"/>
			
						<hel:inputSelect name="portafirmesSequenciaTipus" textKey="expedient.tipus.document.form.camp.portafirmes.sequencia.firma" 
							optionItems="${portafirmesSequenciaTipusEnumOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" 
							disabled="${bloquejarCamps}"/>
					</c:if>
							
				</c:if>
			</div>
			
			<div id="modal-botons" class="well">
				<c:if test="${potFirmar}">
					<button type="submit" class="btn btn-success"><span class="fa fa-pencil-square-o"></span> <spring:message code="expedient.document.enviar.portasignatures.boto.enviar.portasignatures"/></button>
				</c:if>			
				<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
			</div>
		</form:form>
	<div class="flux_container"></div>
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
		});
		$(".portafirmesFluxId_btn_edicio").on('click', function(){
			var metaDocumentNom = "${fn:replace(documentExpedientEnviarPortasignaturesCommand.nom, charSearch, charReplace)}";
			alert(metaDocumentNom);
			$.ajax({
				type: 'GET',
				dataType: "json",
				data: {nom: metaDocumentNom, plantillaId: $("#portafirmesFluxId").val()},
				url: "<c:url value="/v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${documentExpedientEnviarPortasignaturesCommand.id}/enviarPortasignatures/iniciarTransaccio"/>",
				success: function(transaccioResponse) {
					if (transaccioResponse != null && !transaccioResponse.error) {
						localStorage.setItem('transaccioId', transaccioResponse.idTransaccio);
						$('#documentExpedientEnviarPortasignaturesCommand').addClass("hidden");
						alert(transaccioResponse.urlRedireccio);
						var fluxIframe = '<div class="iframe_container">' + 
											'<iframe onload="removeLoading()" id="fluxIframe" class="iframe_content" width="100%" height="100%" frameborder="0" allowtransparency="true" src="' + transaccioResponse.urlRedireccio + '"></iframe>' + 
							  			 '</div>';
						$('.flux_container').html(fluxIframe);	
						adjustModalPerFlux();
						$body = $("body");
						$body.addClass("loading");
					} else if (transaccioResponse != null && transaccioResponse.error) {
						let currentIframe = window.frameElement;
						var alertDiv = '<div class="alert alert-danger" role="alert">' + 
											'<a class="close" data-dismiss="alert">×</a>' + 
											'<span>' + transaccioResponse.errorDescripcio + '</span>' +
									   '</div>';
						$('form').prev().find('.alert').remove();
						$('form').prev().prepend(alertDiv);
						webutilModalAdjustHeight();
					}
				},
				error: function(error) {
					if (error != null && error.responseJSON != null) {
						let currentIframe = window.frameElement;
						var alertDiv = '<div class="alert alert-danger" role="alert">' + 
											'<a class="close" data-dismiss="alert">×</a>' + 
											'<span>' + error.responseJSON.message + '</span>' + 
									   '</div>';
						$('form').prev().find('.alert').remove();
						$('form').prev().prepend(alertDiv);
						webutilModalAdjustHeight();
					}
				}
			});
		});
		
		$.ajax({
				type: 'GET',
				dataType: "json",
				url: "<c:url value="/v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${documentExpedientEnviarPortasignaturesCommand.id}/flux/plantilles"/>",
					success: function(data) {
								var plantillaActual = "${portafirmesFluxSeleccionat}";
								var selPlantilles = $("#portafirmesFluxId");
								selPlantilles.empty();
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
								var successAlert = "<div class='alert alert-success' role='alert'>" +
														"<a class='close' data-dismiss='alert'>×</a>" + 
														"<span><spring:message code='expedient.tipus.document.form.camp.portafirmes.flux.esborrar.ok'/></span>" +
												   "</div>";
								var errorAlert = "<div class='alert alert-danger' role='alert'>" + 
													 "<a class='close' data-dismiss='alert'>×</a>" + 
													 "<span><spring:message code='expedient.tipus.document.form.camp.portafirmes.flux.esborrar.ko'/></span>" + 
												 "</div>";
			$.ajax({
					type: 'GET',
					dataType: "json",
						url: "<c:url value="/v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${documentExpedientEnviarPortasignaturesCommand.id}/flux/esborrar/"/>" + portafirmesFluxId,
							success: function(esborrat) {
								if (esborrat) {
											$('form').prev().find('.alert').remove();
											$('form').prev().prepend(successAlert);
											$("#portafirmesFluxId option[value='" + portafirmesFluxId + "']").remove();
										} else {
											$('form').prev().find('.alert').remove();
											$('form').prev().prepend(errorAlert);
										}
										webutilModalAdjustHeight();
									},
									error: function (error) {
										$('form').prev().find('.alert').remove();
										$('form').prev().prepend(errorAlert);
										webutilModalAdjustHeight();		
									}
								});
							}
						});
			$("#portafirmesFluxId").on('change', function () {
						var portafirmesFluxId = $(this).val();
						if(portafirmesFluxId != null && portafirmesFluxId != '') {
								$(".portafirmesFluxId_btn_edicio").attr("title", "<spring:message code="expedient.tipus.document.form.camp.portafirmes.flux.editar"/>");
								$(".portafirmesFluxId_btn_addicional").removeClass("flux_disabled");
							} else {
								$(".portafirmesFluxId_btn_edicio").attr("title", "<spring:message code="expedient.tipus.document.form.camp.portafirmes.flux.iniciar"/>");
								$(".portafirmesFluxId_btn_addicional").addClass("flux_disabled");
							}
						});
						
			$("#portafirmesFluxId").trigger('change');

			$('.modal-cancel').on('click', function(){
					localStorage.removeItem('transaccioId');
			});
						
			$(".portafirmesResponsables_btn").attr("title", "<spring:message code="expedient.tipus.document.form.camp.portafirmes.carrecs"/>");
			
			$("#portafirmesResponsables").on('select2:unselecting', function (e) {
					var optionRemoved = e.params.args.data.id;
					$("#portafirmesResponsables option[value='" + optionRemoved + "']").remove();
			});
						
						
			function toggleCarrecs() {
					var dropdown = $(".portafirmesResponsables_btn").parent().find('.dropdown-menu');
					if (dropdown.length === 0) {
						$(".portafirmesResponsables_btn").parent().append(recuperarCarrecs());
							$(".portafirmesResponsables_btn").parent().find('.dropdown-menu').toggle();
							
					} else {
						dropdown.toggle();
					}
			}
						
			function recuperarCarrecs() {
					var llistatCarrecs = "<div class='loading dropdown-menu'>";
					$.ajax({
								type: 'GET',
								dataType: "json",
								//url: "<c:url value="/v3/expedientTipus/${expedientTipusDocumentCommand.expedientTipusId}/document/${expedientTipusDocumentCommand.id}/carrecs"/>",
								url: "<c:url value="/v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${documentExpedientEnviarPortasignaturesCommand.id}/carrecs"/>",
								success: function(carrecs) {
									var dropdown = $(".portafirmesResponsables_btn").parent().find('.dropdown-menu');
									dropdown.removeClass('loading');
									if (carrecs) {
										llistatCarrecs += '<div class="carrecsList">';
										$.each(carrecs, function(i, carrec) {
											var persona = '';
											if (carrec.usuariPersonaNom) {
												persona = ' (' + carrec.usuariPersonaNom + ' - ' + carrec.usuariPersonaNif + ' - ' + carrec.usuariPersonaId + ')';
											}
											var nomCarrec = carrec.carrecName + persona;
											llistatCarrecs += "<div class='carrec_" + carrec.carrecId + "'><a onclick='seleccionarCarrec(" + JSON.stringify(carrec) + ")'>" + nomCarrec + "</a></div>";	
											
											$('#portafirmesResponsables option').each(function(i, responsable) {
												if (responsable.value === carrec.carrecId) {
													llistatCarrecs = llistatCarrecs.replace('carrec_' + carrec.carrecId, 'carrec_' + carrec.carrecId + ' carrec-selected');
												}
											});
										});
									}
									dropdown.append(llistatCarrecs);
								},
								error: function (error) {
									var dropdown = $(".portafirmesResponsables_btn").parent().find('.dropdown-menu');
									dropdown.removeClass('loading');
									dropdown.empty();
									dropdown.append("Hi ha hagut un problema recuperant els càrrecs " + error.statusText);
								},
								statusCode: {
							        500: function(error) {
							        	var dropdown = $(".portafirmesResponsables_btn").parent().find('.dropdown-menu');
										dropdown.removeClass('loading');
							        	dropdown.empty();
										dropdown.append("Hi ha hagut un problema recuperant els càrrecs: " + error.statusText);
							        }
							   	}
							});
							llistatCarrecs += "<div class='rmodal_carrecs'></div></div>";
							return llistatCarrecs;
						}

				function seleccionarCarrec(carrec) {
							if ($('.carrec_' + carrec.carrecId).hasClass('carrec-selected')) {
								$("#portafirmesResponsables option[value='" + carrec.carrecId + "']").remove();
								$('.carrec_' + carrec.carrecId).removeClass('carrec-selected');
							} else {
								var persona = '';
								if (carrec.usuariPersonaNif) {
									persona = ' (' + carrec.usuariPersonaNif + ')';
								}
								var nomCarrec = carrec.carrecName + persona;
								var items = [];
								items.push({
									"id": carrec.carrecId,
									"text": nomCarrec
								});
							    var newOption = new Option(items[0].text, items[0].id, true, true);
							    $("#portafirmesResponsables").append(newOption).trigger('change');

								$('.carrec_' + carrec.carrecId).addClass('carrec-selected');
							}
						}
							
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
		// ]]>
	</script>			
		
</body>
</html>