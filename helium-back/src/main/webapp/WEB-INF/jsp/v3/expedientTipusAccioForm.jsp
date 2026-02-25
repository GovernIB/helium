<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${!heretat}">
		<c:choose>
			<c:when test="${empty expedientTipusAccioCommand.id}"><
				<c:set var="titol"><spring:message code="expedient.tipus.accio.form.titol.nova"/></c:set>
				<c:set var="formAction">new</c:set>
			</c:when>
			<c:otherwise>
				<c:set var="titol"><spring:message code="expedient.tipus.accio.form.titol.modificar"/></c:set>
				<c:set var="formAction">update</c:set>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.tipus.accio.form.titol.visualitzar"/></c:set>
		<c:set var="formAction">none</c:set>		
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
</head>
<body>	
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="expedientTipusAccioCommand">
		<div>        
			<input type="hidden" name="id" value="${expedientTipusAccioCommand.id}"/>
			<input type="hidden" name="perEstats" value="${expedientTipusAccioCommand.perEstats}"/>
			<hel:inputText required="true" name="codi" textKey="expedient.tipus.accio.form.accio.codi" />
			<hel:inputText required="true" name="nom" textKey="expedient.tipus.accio.form.accio.nom" />
			<hel:inputTextarea name="descripcio" textKey="expedient.tipus.accio.form.accio.descripcio" />
			<hel:inputCheckbox name="publica" textKey="expedient.tipus.accio.form.accio.publica" />
			<hel:inputCheckbox name="oculta" textKey="expedient.tipus.accio.form.accio.oculta" />
			<hel:inputText name="rols" textKey="expedient.tipus.accio.form.accio.rols" />
			<hel:inputSelect required="true" name="tipus" textKey="expedient.tipus.accio.form.accio.tipus" placeholderKey="expedient.tipus.accio.form.accio.tipus" optionItems="${tipus}" optionValueAttribute="codi" optionTextAttribute="valor"/>

			<div id="rowDefProc">
				<hel:inputSelect required="true" name="defprocJbpmKey" textKey="expedient.tipus.accio.form.accio.defprocJbpmKey" emptyOption="true" placeholderKey="expedient.tipus.accio.form.accio.defprocJbpmKey.placeholder" optionItems="${definicionsProces}" />
			</div>
			<div id="rowAccio">
				<c:if test="${not empty expedientTipusAccioCommand.expedientTipusId}">
					<hel:inputSelect required="true" name="jbpmAction" textKey="expedient.tipus.accio.form.accio.jbpmAction" emptyOption="true" placeholderKey="expedient.tipus.accio.form.accio.jbpmAction" optionItems="${accions}" optionValueAttribute="codi" optionTextAttribute="valor"/>
				</c:if>
			</div>
			<div id="rowHandlerPropi">
				<c:if test="${not empty expedientTipusAccioCommand.expedientTipusId}">
					<hel:inputSelect required="true" name="handlerPropi" textKey="expedient.tipus.accio.form.accio.handlerPropi" emptyOption="true" placeholderKey="expedient.tipus.accio.form.accio.handlerPropi" optionItems="${handlersPropis}" optionValueAttribute="codi" optionTextAttribute="valor"/>
				</c:if>
			</div>
			<div id="rowHandlerPredefinit">
				<hel:inputSelect required="true" name="handlerPredefinit" textKey="expedient.tipus.accio.form.accio.handlerPredefinit" emptyOption="true" placeholderKey="expedient.tipus.accio.form.accio.handlerPredefinit.placeholder" />
				<span id='handlerPredefinitDescripcio' class="text-muted"></span>
			</div>
			<fieldset id="fmapejos">
				<legend><spring:message code="expedient.tipus.accio.form.legend.handler.predefinit"></spring:message></legend>
				<div id="mapejosHandler">
					<!-- Div per posar els paràmetres -->
				</div>
			</fieldset>
			<div id="rowScript">
				<hel:inputTextarea name="script" required="true" textKey="expedient.tipus.accio.form.accio.script" />
			</div>
		</div>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<c:if test="${!heretat}">
				<c:choose>
					<c:when test="${empty expedientTipusAccioCommand.id}">
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

	 	var handlersPredefinitsJson = ${handlersPredefinitsJson != null? handlersPredefinitsJson : "[]" };
	 	var handlerDades = ${dadesHandlerJson != null? dadesHandlerJson : "[]"};
		<c:forEach items="${expedientTipusAccioCommand.handlerDades}" var="handlerDada">
		handlerDades["${handlerDada.key}"] = "${handlerDada.value}";
		</c:forEach>

		$(document).ready(function() {
   			//<c:if test="${heretat}">
			webutilDisableInputs($('#expedientTipusAccioCommand'));
			//</c:if>
			// Canvi del tipus d'acció
			$('#tipus').change(function(){
				$('#rowDefProc').hide();
				$('#rowAccio').hide();
				$('#rowHandlerPropi').hide();
				$('#rowHandlerPredefinit').hide();
				$('#rowScript').hide();
				$('#fmapejos').hide();
				webutilDisableInputs('#rowAccio,#rowHandlerPropi,#rowHandlerPredefinit,#rowScript');
				switch($(this).val()) {
					case 'ACCIO':
						webutilEnableInputs('#rowDefProc');
						webutilEnableInputs('#rowAccio');						
						$('#rowDefProc').show();
						$('#rowAccio').show();
					break;
					case 'HANDLER_PROPI':
						webutilEnableInputs('#rowDefProc');
						webutilEnableInputs('#rowHandlerPropi');
						//<c:if test="${not perEstats}">
						$('#rowDefProc').show();
						//</c:if>
						$('#rowHandlerPropi').show();
						$('#fmapejos').show();
						$('#handlerPropi').change();
					break;
					case 'HANDLER_PREDEFINIT':
						webutilEnableInputs('#rowHandlerPredefinit');
						$('#rowHandlerPredefinit').show();
						$('#fmapejos').show();
						$('#handlerPredefinit').change();
						break;
					case 'SCRIPT':
						webutilEnableInputs('#rowScript');
						$('#rowScript').show();
						break;
				}
			}).change();
			
			// Canvi en la selecció de la definicion
			$('#defprocJbpmKey').change(function() {
				refrescaAccions();
				refrescaHandlersPropis();
			});
			
			
			$('#handlerPredefinit').after($("#handlerPredefinitDescripcio"));
			$('#handlerPredefinit').change(function() {
				carregarParametresHandlerPredefinit();
			}).after($("#handlerPredefinitDescripcio"));

			//<c:if test="${perEstats}">
			$("#rowDefProc").hide();
			//</c:if>

			$('#jbpmAction').val('${expedientTipusAccioCommand.jbpmAction}');
			
			$('#handlerPropi').change(function() {
				carregarParametresHandler();
			}).change();
			
			carregarHandlersPredefinits();			
		});
		

		function refrescaAccions() {
			var definicioProcesId = $("#defprocJbpmKey").val();
			var jbpmActionActual = $('#jbpmAction').val();
			if (definicioProcesId != "") {
				var getUrl = '<c:url value="/v3/expedientTipus/${expedientTipusAccioCommand.expedientTipusId}/definicio/"/>' + definicioProcesId + '/accions/select';
				$.ajax({
					type: 'GET',
					url: getUrl,
					data: {jbpmActionActual: jbpmActionActual},
					async: true,
					success: function(data) {
						$("#jbpmAction option").each(function(){
						    $(this).remove();
						});
						$("#jbpmAction").append($("<option/>"));
						for (i = 0; i < data.length; i++) {
							$("#jbpmAction").append($("<option/>", {value: data[i].codi, text: data[i].valor}));
						}
						$("#jbpmAction").val(definicioProcesId).change();
					},
					error: function(e) {
						console.log("Error obtenint les accions de les definicions de procés per l' id " + definicioProcesId + ": " + e);
					}
				});
			} else {
				$('#jbpmAction').val('').change();
			}
		}
		
		function refrescaHandlersPropis() {
			var definicioProcesId = $("#defprocJbpmKey").val();
			var handlerPropiActual = $('#handlerPropi').val();
			if (definicioProcesId != "") {
				var getUrl = '<c:url value="/v3/expedientTipus/${expedientTipusAccioCommand.expedientTipusId}/definicio/"/>' + definicioProcesId + '/handlersPropis/select';
				$.ajax({
					type: 'GET',
					url: getUrl,
					data: {handlerPropiActual: handlerPropiActual},
					async: true,
					success: function(data) {
						$("#handlerPropi option").each(function(){
						    $(this).remove();
						});
						$("#handlerPropi").append($("<option/>"));
						for (i = 0; i < data.length; i++) {
							$("#handlerPropi").append($("<option/>", {value: data[i].codi, text: data[i].valor}));
						}
						$("#handlerPropi").val(definicioProcesId).change();
					},
					error: function(e) {
						console.log("Error obtenint els handlers propis de les definicions de procés per l' id " + definicioProcesId + ": " + e);
					}
				});
			} else {
				$('#handlerPropi').val('').change();
			}
		}
				
		var handlersPredefinititsGrups = new Map([]);
		<c:forEach items="${handlersPredefinititsGrups}" var="handlersPredefinititsGrup">
		handlersPredefinititsGrups["${handlersPredefinititsGrup.codi}"] = "${handlersPredefinititsGrup.valor}";
  		</c:forEach>
		
		// Carrega el select de handlers predefinits agrupats
		function carregarHandlersPredefinits() { 
			var valor = "${expedientTipusAccioCommand.handlerPredefinit}";
			var mapAgrupacions = new Map();
			for (i = 0; i < handlersPredefinitsJson.length; i++) {
				// Recupera agrupació
				let group = mapAgrupacions[handlersPredefinitsJson[i].agrupacio];
				if (group == null) {
					group = $("<optgroup/>", {label: handlersPredefinititsGrups[handlersPredefinitsJson[i].agrupacio]})
					mapAgrupacions[handlersPredefinitsJson[i].agrupacio] = group;
					$("#handlerPredefinit").append(group);
				}
				// Afegeix la nova opció a l'agrupació
				group.append($("<option/>", {value: handlersPredefinitsJson[i].classe, text: handlersPredefinitsJson[i].nom}));
			}
			$("#handlerPredefinit").val('${command.handlerPredefinit}').val(valor).change();
		}
		
		// A partir del handler predefinit carrega els paràmetres
		function carregarParametresHandlerPredefinit() {

			$('#mapejosHandler').empty();

			$("#handlerPredefinitDescripcio").html("")
			var handlerPredefinit = $("#handlerPredefinit").val();
			if (handlerPredefinit === '') {
				return;
			}
			// Troba la informació del handler
			var handlerInfo = null;
			for (i=0; i<handlersPredefinitsJson.length; i++) {
				if (handlerPredefinit === handlersPredefinitsJson[i].classe) {
					handlerInfo = handlersPredefinitsJson[i];
				}
			}
			$("#handlerPredefinitDescripcio").html(handlerInfo.descripcio)
			// Pinta els paràmtres
			if (handlerInfo != null) {
				if (handlerInfo.parametres.length > 0) {
					for (i=0; i < handlerInfo.parametres.length; i++) {
						afegirControlsParametre(handlerInfo, handlerInfo.parametres[i]);
					}
				} else {
					$('#mapejosHandler').append('<p>(<spring:message code="expedient.tipus.accio.form.accio.handlerPredefinit.sense.parametres"/>)</p>');
				}
			}
		}

		function afegirControlsParametre(handlerInfo, parametre) {
			debugger;
			// clona la plantilla
			var $parametres = $('#handlerParametreTemplate').clone(true);
			$parametres.attr('id', 'handlerParametre_' + parametre.codi);

			$label = $('.paramLabel', $parametres)
			$label.text(parametre.nom);
			$label.attr('for', 'handlerDades[' + parametre.param + ']');

			if (parametre.obligatori) {
				$label.addClass('obligatori');
			}

			$menuSelect = $('.menuSelect', $parametres);
			//$('option[value="text"]', $menuSelect).attr('id', parametre.param);
			//$('option[value="var"]', $menuSelect).attr('id', parametre.varParam);
			$menuSelect.select2({
				minimumResultsForSearch: -1
			});

			$inputText = $('.param', $parametres);
			if (parametre.param != null) {
				$inputText.attr('id', parametre.param);
				$inputText.attr('name','handlerDades[' + parametre.param + ']');
				$inputText.attr("placeholder", parametre.paramDesc);
				$inputText.attr("title", parametre.paramDesc);
				if (handlerDades[parametre.param]) {
					$inputText.val(handlerDades[parametre.param]);
					$menuSelect.val('text');
				} else {
					$inputText.val('');
				}
			} else {
				$inputText.remove();
				$menuSelect.find('option[value="text"]').remove();
				$menuSelect.val('var');
			}

			$selectVarParam = $('.varParam', $parametres);
			if (parametre.varParam != null) {
				$selectVarParam.attr("placeholder", parametre.varParamDesc);
				$selectVarParam.attr("title", parametre.varParamDesc);
				$selectVarParam.select2({
					width: '100%',
					allowClear: true
				});
				$selectVarParam.attr('id',parametre.varParam);
				$selectVarParam.attr('name','handlerDades[' + parametre.varParam + ']');
				if (handlerDades[parametre.varParam]) {
					$selectVarParam.val(handlerDades[parametre.varParam]).change();
					$menuSelect.val('var');
				} else {
					$selectVarParam.val('').change();
				}
			} else {
				$selectVarParam.remove();
				$menuSelect.find('option[value="var"]').remove();
				$menuSelect.val('text');
			}

			$menuSelect.change(function(){
				menuSelectChange($(this));
			}).change();

			$('#mapejosHandler').append($parametres);
		}

		function menuSelectChange($menuSelect) {

			$parametres = $menuSelect.closest('.handlerParametre');
			$label = $('.paramLabel', $parametres)
			$inputText = $('.param', $parametres);
			$selectVarParam = $('.varParam', $parametres);

			if ($menuSelect.val() == 'text') {
				if ($inputText) {
					$inputText.show().removeAttr('disabled');
				}
				if ($selectVarParam) {
					$selectVarParam.hide().attr('disabled','disabled');
				};
			} else {
				if ($selectVarParam) {
					$selectVarParam.show().removeAttr('disabled');
				}
				if ($inputText) {
					$inputText.hide().attr('disabled','disabled');
				}
			}
		}

		function carregarParametresHandler() {
			$('#mapejosHandler').empty();
			// Troba la informació del handler
			var handlerPropiActual = $('#handlerPropi').val();
			if (handlerPropiActual == null || handlerPropiActual == '') {
				return;
			}
			var getUrl = '<c:url value="/v3/expedientTipus/${expedientTipusAccioCommand.expedientTipusId}/accio/params"/>';
			$.ajax({
				type: 'GET',
				url: getUrl,
				data: {handler: handlerPropiActual},
				async: true,
				success: function(data) {
					// Pinta els paràmetres
					if (data != null && data.length > 0) {
						for (i = 0; i < data.length; i++) {
							afegirParametresHandler(data[i].codi);
						}
					} else {
						$('#mapejosHandler').append('<p>(<spring:message code="expedient.tipus.accio.form.accio.handlerPredefinit.sense.parametres"/>)</p>');
					}
				},
				error: function(e) {
					console.log("Error obtenint els paràmetres del handler " + handlerPropiActual + ": " + e);
				}
			}).done(function() {
				// Netejem l'array amb els valors que han arribat del controlador
				handlerDades = [];
			});
		}

		function afegirParametresHandler(parametre) {
			var $parametres = $('#handlerParametreTemplate2').clone(true);
			$parametres.attr('id', 'handlerParametre_' + parametre);
			let codi = 'handlerDades[' + parametre + ']';

			$label = $('.paramLabel', $parametres)
			$label.text(parametre);
			$label.attr('for', codi);

			$inputText = $('.param', $parametres);
			$inputText.attr('id', parametre);
			$inputText.attr('name',codi);
			$inputText.val(handlerDades[parametre]);
			$('#mapejosHandler').append($parametres);
		}
				
		// ]]>
	</script>			

	</form:form>
	
	<!-- Plantilla no visible per paràmetres -->
	<div style="display: none;">
	
		<div id="handlerParametreTemplate" class="form-group handlerParametre">
		
			<label class="control-label col-xs-3 paramLabel" for="[codi]">
				[parametre.nom]
			</label>
			
			<div class="col-xs-2">
				<select class="menuSelect">
					<option value="text">Text</option>
					<option value="var">Var</option>
				</select>
			</div>
			
			<div class="col-xs-7">
				<input id="[parametre.param]" name="handlerDades[parametre.param]" class="form-control param" type="text" value="handlerDades[parametre.param]">
				<select  id="[parametre.varParam] name="handlerDades[parametre.varParam]" class="varParam">
					<option/>
				<c:forEach items="${variables}" var="variable">
					<option data-agrupacio="${variable.agrupacio.codi }" data-agrupacio-nom="${variable.agrupacio.nom }" value="${variable.codi}">
						${variable.codi} / ${variable.etiqueta}
						<c:if test="${variable.heretat }">
							<span class='label label-primary'>R</span>
						</c:if>
						<c:if test="${variable.sobreescriu }">
							<span class='label label-warning'>S</span>
						</c:if>									
					</option>
				</c:forEach>
				</select>
			</div>						
		</div>

		<div id="handlerParametreTemplate2" class="form-group handlerParametre">
			<label class="control-label col-xs-4 paramLabel" for="[parametre.param]">[parametre.param]</label>
			<div class="col-xs-8">
				<input id="[parametre.param]" name="handlerDades[parametre.param]" class="form-control param" type="text" value="handlerDades[parametre.param]">
			</div>
		</div>

	</div>
</body>
</html>