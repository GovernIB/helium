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
			<hel:inputText required="true" name="codi" textKey="expedient.tipus.accio.form.accio.codi" />
			<hel:inputText required="true" name="nom" textKey="expedient.tipus.accio.form.accio.nom" />
			<hel:inputTextarea name="descripcio" textKey="expedient.tipus.accio.form.accio.descripcio" />
			<hel:inputCheckbox name="publica" textKey="expedient.tipus.accio.form.accio.publica" />
			<hel:inputCheckbox name="oculta" textKey="expedient.tipus.accio.form.accio.oculta" />
			<hel:inputText name="rols" textKey="expedient.tipus.accio.form.accio.rols" />
			<hel:inputSelect required="true" name="tipus" textKey="expedient.tipus.accio.form.accio.tipus" placeholderKey="expedient.tipus.accio.form.accio.tipus" optionItems="${tipus}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			<div id="rowHandler">
				<c:if test="${not empty expedientTipusAccioCommand.expedientTipusId}">
					<hel:inputSelect required="true" name="defprocJbpmKey" textKey="expedient.tipus.accio.form.accio.defprocJbpmKey" emptyOption="true" placeholderKey="expedient.tipus.accio.form.accio.defprocJbpmKey.placeholder" optionItems="${definicionsProces}" />
					<hel:inputSelect required="true" name="jbpmAction" textKey="expedient.tipus.accio.form.accio.jbpmAction" emptyOption="true" placeholderKey="expedient.tipus.accio.form.accio.jbpmAction" optionItems="${accions}" optionValueAttribute="codi" optionTextAttribute="valor"/>
				</c:if>
				<c:if test="${not empty expedientTipusAccioCommand.definicioProcesId}">
					<input type="hidden" name="defprocJbpmKey" value="${expedientTipusAccioCommand.defprocJbpmKey}" />
					<hel:inputSelect required="true" name="jbpmAction" textKey="expedient.tipus.accio.form.accio.jbpmAction" optionItems="${handlers}" />
				</c:if>
			</div>
			<div id="rowHandlerPredefinit">
				
				<hel:inputSelect required="true" name="predefinitClasse" textKey="expedient.tipus.accio.form.accio.handlerPredefinit" emptyOption="true" placeholderKey="expedient.tipus.accio.form.accio.handlerPredefinit.placeholder" />

				<fieldset>
					<legend><spring:message code="expedient.tipus.accio.form.legend.handler.predefinit"></spring:message></legend>					
					
					<div id="mapejosHandlerPredefinit">
						<!-- Div per posar els paràmetres -->
					</div>
					
				</fieldset>
			</div>
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
	
	 	var handlersPredefinitsJson = ${handlersPredefinitsJson };
	 	var predefinitDades = ${dadesPredefinidesJson}

		$(document).ready(function() {			
   			//<c:if test="${heretat}">
			webutilDisableInputs($('#expedientTipusAccioCommand'));
			//</c:if>
			
			// Canvi del tipus d'acció
			$('#tipus').change(function(){
				$('#rowHandler').hide();
				$('#rowHandlerPredefinit').hide();
				$('#rowScript').hide();
				webutilDisableInputs('#rowHandler,#rowHandlerPredefinit,#rowScript');
				switch($(this).val()) {
					case 'HANDLER':
						webutilEnableInputs('#rowHandler');
						$('#rowHandler').show();
					break;
					case 'HANDLER_PREDEFINIT':
						webutilEnableInputs('#rowHandlerPredefinit');
						$('#rowHandlerPredefinit').show();
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
			});
			
			$('#predefinitClasse').change(function() {
				carregarParametresHandlerPredefinit();
			});
			
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
				
		var handlersPredefinititsGrups = new Map([]);
		<c:forEach items="${handlersPredefinititsGrups}" var="handlersPredefinititsGrup">
		handlersPredefinititsGrups["${handlersPredefinititsGrup.codi}"] = "${handlersPredefinititsGrup.valor}";
  		</c:forEach>
		
		// Carrega el select de handlers predefinits agrupats
		function carregarHandlersPredefinits() { 
			var valor = "${expedientTipusAccioCommand.predefinitClasse}";
			var mapAgrupacions = new Map();
			for (i = 0; i < handlersPredefinitsJson.length; i++) {
				// Recupera agrupació
				let group = mapAgrupacions[handlersPredefinitsJson[i].agrupacio];
				if (group == null) {
					group = $("<optgroup/>", {label: handlersPredefinititsGrups[handlersPredefinitsJson[i].agrupacio]})
					mapAgrupacions[handlersPredefinitsJson[i].agrupacio] = group;
					$("#predefinitClasse").append(group);
				}
				// Afegeix la nova opció a l'agrupació
				group.append($("<option/>", {value: handlersPredefinitsJson[i].classe, text: handlersPredefinitsJson[i].nom}));
			}
			$("#predefinitClasse").val('${command.predefinitClasse}').val(valor).change();
		}
		
		// A partir del handler predefinit carrega els paràmetres
		function carregarParametresHandlerPredefinit() {
			$('#mapejosHandlerPredefinit').empty();
			var handlerPredefinit = $("#predefinitClasse").val();
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
			// Pinta els paràmtres
			if (handlerInfo != null) {
				if (handlerInfo.parametres.length > 0) {
					for (i=0; i < handlerInfo.parametres.length; i++) {
						afegirControlsParametre(handlerInfo, handlerInfo.parametres[i]);
					}
				} else {
					$('#mapejosHandlerPredefinit').append('<p>(<spring:message code="expedient.tipus.accio.form.accio.handlerPredefinit.sense.parametres"/>)</p>');
				}
			}
		}
				
		function afegirControlsParametre(handlerInfo, parametre) {
			// clona la plantilla
			var $parametres = $('#handlerParametreTemplate').clone(true);
			$parametres.attr('id', 'handlerParametre_' + parametre.codi);
			
			$label = $('.paramLabel', $parametres)
			$label.text(parametre.nom);
			$label.attr('for', 'predefinitDades[' + parametre.param + ']');
			
			if (parametre.obligatori) {
				$label.addClass('obligatori');
			}
			
			$menuSelect = $('.menuSelect', $parametres);
			$('option[value="text"]', $menuSelect).attr('id', parametre.param);
			$('option[value="var"]', $menuSelect).attr('id', parametre.varParam);
			$menuSelect.select2({
				minimumResultsForSearch: -1 
			});
	
			$inputText = $('.param', $parametres);
			if (parametre.param != null) {
				$inputText.attr('id', parametre.param);
				$inputText.attr('name','predefinitDades[' + parametre.param + ']');
				$inputText.val(predefinitDades[parametre.param]);
								
			} else {
				$inputText.remove();
	            $menuSelect.find('option[value="text"]').attr('disabled', 'disabled');
				$menuSelect.val('var');
			}
				
			$selectVarParam = $('.varParam', $parametres);
			if (parametre.varParam != null) {
				$selectVarParam.select2({
				    width: '100%',
				    allowClear: true
				});
				$selectVarParam.attr('id',parametre.varParam);
				$selectVarParam.attr('name','predefinitDades[' + parametre.varParam + ']');
				$selectVarParam.val(predefinitDades[parametre.varParam]);								
			} else {
				$selectVarParam.remove();
	            $menuSelect.find('option[value="var"]').attr('disabled', 'disabled');
				$menuSelect.val('text');
			}
			
			$menuSelect.change(function(){
				menuSelectChange($(this));
			}).change();
			
			$('#mapejosHandlerPredefinit').append($parametres);
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
				<input id="[parametre.param]" name="predefinitDades[parametre.param]" class="form-control param" type="text" value="predefinitDades[parametre.param]">
				<select  id="[parametre.varParam] name="predefinitDades[parametre.varParam]" class="varParam">
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
	</div>
</body>
</html>