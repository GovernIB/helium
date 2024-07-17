<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:choose>
	<c:when test="${empty interessatCommand.id}">
		<c:set var="titol"><spring:message code="interessat.form.titol.nou"/></c:set>
		<c:set var="formAction">new</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="interessat.form.titol.modificar"/></c:set>
		<c:set var="formAction">update</c:set>
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
#s2id_tipus, #s2id_entregaTipus {
	width: 100% !important;
}
</style>
<script>

var $ADMINISTRACIO = '<%=net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto.ADMINISTRACIO%>';
var $FISICA = '<%=net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto.FISICA%>';
var $JURIDICA = '<%=net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto.JURIDICA%>';

function ajustarTipus(tipus) {
  	if (tipus == $ADMINISTRACIO) {
 		$("label[for='dir3Codi']").addClass('obligatori');
 	} else{
  		$("label[for='dir3Codi']").removeClass('obligatori');
  	}
  	if (tipus == $FISICA) {
 		$("label[for='llinatge1']").addClass('obligatori');
 	} else{
  		$("label[for='llinatge1']").removeClass('obligatori');
  	}
}

function adaptarSuggest(tipus) {
	//alert(tipus);
	if (tipus == '')
		$('.cercadors').addClass('hidden');
	if (tipus == 'ADMINISTRACIO'){
		$('.cercadors').removeClass('hidden');
		$('.visibilitatTipus').addClass('hidden');
		$('.suggestUnitatsOrganitzatives').removeClass('hidden');
		$('.suggestPersonesFisiques').addClass('hidden');
		$('.suggestPersonesJuridiques').addClass('hidden');

	} else if (tipus == 'FISICA'){
		$('.cercadors').removeClass('hidden');
		$('.visibilitatTipus').addClass('hidden');
		$('.suggestPersonesFisiques').removeClass('hidden');
		$('.suggestUnitatsOrganitzatives').addClass('hidden');
		$('.suggestPersonesJuridiques').addClass('hidden');

	} else if (tipus == 'JURIDICA'){
		$('.cercadors').removeClass('hidden');
		$('.visibilitatTipus').addClass('hidden');
		$('.suggestPersonesJuridiques').removeClass('hidden');
		$('.suggestPersonesFisiques').addClass('hidden');
		$('.suggestUnitatsOrganitzatives').addClass('hidden');	
	}
}
	
function adaptarVisibilitat(tipus){
		 if (tipus == 'FISICA'){
			 $('.visibilitatTipus').removeClass('hidden');
			 $('.personajuridica').addClass('hidden');	
			 $('.personafisica').removeClass('hidden');
		} else if (tipus == 'JURIDICA'){
			 $('.visibilitatTipus').removeClass('hidden');
			 $('.personafisica').addClass('hidden');
			 $('.personajuridica').removeClass('hidden');	
		}
	/* if (accio == 'GUARDAR') {
		$('.guardar').removeClass('hidden');				
	} else if (accio == 'CREAR') {
		$('.crear').removeClass('hidden');
	} else if (accio == 'INCORPORAR') {
		$('.incorporar').removeClass('hidden');
	} */
}

function carregaDadesInteressat(interessatDto) {
	alert(interessatDto);
}

$(document).ready(function() {

	//adaptarVisibilitat('');

 	/* $('#tipus').on('change', function() {
		ajustarTipus(this.value);
	}); */
 	
	$("#nifPersonaFisica").on('change', function() {
		debugger;
		adaptarVisibilitat($FISICA);
		if (this.value && this.value!='') {
			$.ajax({
				url: "<c:url value="/v3/expedient/${expedientId}/interessat"/>"+"/"+this.value,
			    type:'GET',
			    dataType: 'json',
			    async: true,
			    success: function(json) {
			    	carregaDadesInteressat(json);
			    },
			    error: function(jqXHR, textStatus, errorThrown) {
			    	console.log("Error al actualitzar la llista d'estats: [" + textStatus + "] " + errorThrown);
			    }
			});
		} else {
			carregaDadesInteressat(null);
		}
	});
	
 	$('input[type=radio][name=tipus]').on('change', function() {
 		adaptarSuggest(this.value);
 		//adaptarVisibilitat($(this).val());
		webutilModalAdjustHeight();
	});
	
	$('[id=nova_fisica]').on('click', function() {
 		adaptarVisibilitat($FISICA);
 		//adaptarVisibilitat($(this).val());
		webutilModalAdjustHeight();
	});	
	$('[id=nova_juridica]').on('click', function() {
 		adaptarVisibilitat($JURIDICA);
 		//adaptarVisibilitat($(this).val());
		webutilModalAdjustHeight();
	});	
 	
	$('input[type=checkbox][name=entregaDeh]').on('change', function() {
		if($(this).prop("checked") == true){
			$('#entregaDehObligatDiv').removeClass('hidden');
			
			$("label[for='email']").addClass('obligatori');
		} else {
			$('#entregaDehObligatDiv').addClass('hidden');
			$("label[for='email']").removeClass('obligatori');				
		}
	});			
	
	$('input[type=checkbox][name=entregaDehObligat]').on('change', function() {
		if($(this).prop("checked") == true){
			$('#entregaDeh').attr('disabled', 'disabled');
		} else {
			$('#entregaDeh').removeAttr('disabled');
		}
	});		

	
	/*$('input[type=checkbox][name=entregaPostal]').on('change', function() {
		if($(this).prop("checked") == true){
			$('#entrgePostalForm').removeClass('hidden');
			webutilModalAdjustHeight();
		} else {
			$('#entrgePostalForm').addClass('hidden');		
		}
	});	

	$('input[type=checkbox][name=entregaPostal').trigger('change');*/

	var select2Options = {theme: 'bootstrap'};
	$('select[name=tipus').select2("destroy");
	$('select[name=tipus').select2(select2Options);
	
	var select2Options = {theme: 'bootstrap'};
	$('select[name=entregaTipus').select2("destroy");
	$('select[name=entregaTipus').select2(select2Options);
		
	$('input[type=checkbox][name=entregaDeh').trigger('change');
	$('input[type=checkbox][name=entregaDehObligat').trigger('change');	


	$('#tipus').change();

});
</script>

</head>
<body>
	<form:form cssClass="form-horizontal" action="${formAction}"  method="post" commandName="interessatCommand">
		<form:hidden id="id" path="id"/>
		
		<div class="row">
			<div class="col-xs-10">
			<hel:inputRadio 
				name="tipus"
				labelSize="3" 
				textKey="interessat.form.camp.tipus" 
				optionItems="${interessatTipusOptions}" 
				optionValueAttribute="value" 
				optionTextKeyAttribute="text"/>
			</div>
		</div>
	<div class="hidden cercadors well">
			<div class="row hidden suggestPersonesFisiques">
				<div class="col-xs-9">
				<hel:inputSuggest 
					id="suggestPersonesFisiques"
					inline="false" 
					name="nifPersonaFisica" 
					labelSize="3"
					urlConsultaInicial="persona/suggestInici" 
					urlConsultaLlistat="/helium/v3/expedient/persona/suggest"
					textKey="interessat.form.camp.suggest.persona.fisica" 
					placeholderKey="interessat.form.camp.suggest.cercar.persona.fisica"/>
				</div>
				<button type="button" class="btn btn-default" data-modal-cancel="true">
					<spring:message code="interessat.form.camp.boto.cercar"/>
				</button>
				<button type="button" id="nova_fisica" class="btn btn-default">
					<span class="fa fa-solid fa-user-plus"></span>
					<spring:message code="interessat.form.camp.boto.nova"/>
				</button>
			</div>
		
			<div class="row hidden suggestPersonesJuridiques">
				<div class="col-xs-9">
				<hel:inputSuggest 
					id="suggestPersonesJuridiques"
					inline="false" 
					name="cifPersonaJuridica" 
					labelSize="3"
					urlConsultaInicial="persona/suggestInici" 
					urlConsultaLlistat="/helium/v3/expedient/persona/suggest" 
					textKey="interessat.form.camp.suggest.persona.juridica" 
					placeholderKey="interessat.form.camp.suggest.cercar.persona.juridica"/>
				</div>
				<button type="button" class="btn btn-default" data-modal-cancel="true">
					<spring:message code="interessat.form.camp.boto.cercar"/>	
				</button>
				<button type="button" id="nova_juridica" class="btn btn-default">
					<span class="fa fa-solid fa-user-plus"></span>
					<spring:message code="interessat.form.camp.boto.nova"/>
				</button>
			</div>
		
		
			<div class="row hidden suggestUnitatsOrganitzatives">
				<div class="col-xs-9">
				<hel:inputSuggest 
						id="suggestUnitatsOrganitzatives"
						name="cifOrganGestor" 
						inline="false"
						labelSize="3" 
						urlConsultaInicial="/helium/v3/unitatOrganitzativa/suggestInici" 
						urlConsultaLlistat="/helium/v3/unitatOrganitzativa/suggest" 
						textKey="interessat.form.camp.suggest.administracio.organ" 
						placeholderKey="interessat.form.camp.suggest.cercar.administracio.organ"/>	
				</div>	
				<button type="button" class="btn btn-default" data-modal-cancel="true">
					<spring:message code="interessat.form.camp.boto.cercar"/>
				</button>
				
			</div>
	</div>
		
	
	<div class="hidden visibilitatTipus">	
		<!--<div class="row">
			<div class="col-xs-12">
				<hel:inputText required="true" name="codi" textKey="interessat.form.camp.codi" labelSize="2" />
			</div>
		</div>-->
	<div class="hidden personafisica personajuridica">
		
		<div class="row hidden personafisica nomillinatges">
			<div class="col-xs-4">
				<hel:inputText required="true" name="nom" textKey="interessat.form.camp.nom" labelSize="3" />
			</div>
			<div class="col-xs-4">
				<hel:inputText name="llinatge1" textKey="interessat.form.camp.llinatge1" labelSize="3" required="true"/>
			</div>
			<div class="col-xs-4">
				<hel:inputText name="llinatge2" textKey="interessat.form.camp.llinatge2" labelSize="3" required="false"/>
			</div>	
		</div>
		
		
		
		<div class="row hidden personajuridica raoSocial">
			<div class="col-xs-12">
				<hel:inputText name="raoSocial" textKey="interessat.form.camp.raosocial" labelSize="2" required="true"/>
			</div>	
		</div>
			
		<div class="row hidden personajuridica personafisica tipusdocument">
			<div class="col-xs-6">
				<hel:inputSelect 
					required="true" 
					name="tipusDocIdent"
					optionItems="${interessatTipusDocuments}" 
					optionValueAttribute="valor"
					optionTextAttribute="codi" 
					textKey="interessat.form.camp.tipus.document" 
					labelSize="3" 
					inline="false"/>
			</div>
			<div class="col-xs-6">
				<hel:inputText required="true" name="documentIdent" textKey="interessat.form.camp.document.identificatiu" labelSize="4" inline="false"/>
			</div>
			
		</div>
		
		
		<div class="row emailTelefon personafisica personajuridica">
			<div class="col-xs-6">
				<hel:inputText name="email" textKey="interessat.form.camp.email" labelSize="3"/>		
			</div>
			<div class="col-xs-6">
				<hel:inputText name="telefon" textKey="interessat.form.camp.telefon" />
			</div>
		</div>
		<div class="row paisProvincia personafisica personajuridica">
			<div class="col-xs-6">
				<hel:inputText name="pais" textKey="interessat.form.camp.pais" labelSize="3"/>		
			</div>
			<div class="col-xs-6">
				<hel:inputText name="provincia" textKey="interessat.form.camp.provincia" />
			</div>
		</div>
		<div class="row localitatCodipostal personafisica personajuridica">
			<div class="col-xs-6">
				<hel:inputText name="municipi" textKey="interessat.form.camp.localitat" labelSize="3"/>		
			</div>
			<div class="col-xs-6">
				<hel:inputText name="codiPostal" textKey="interessat.form.camp.codipostal" labelSize="3" required="true"/>	
			</div>
		</div>
		<div class="row">
			<div class="hidden" style="float: right;">
				<hel:inputCheckbox name="entregaPostal" textKey="interessat.form.camp.entregaPostal" labelSize="11"></hel:inputCheckbox>
			</div>
		</div>
		<div id="entrgePostalForm" class="row direccioObservacions">
			<!--<div class=" col-xs-6">
				<hel:inputSelect required="true" name="entregaTipus"
					optionItems="${NotificaDomiciliConcretTipus}" optionValueAttribute="valor"
					optionTextAttribute="codi" textKey="interessat.form.camp.entregatipus" labelSize="2"/>
			</div>
			<div class=" col-xs-6">
				<hel:inputSelect required="true" name="entregaTipus"
				optionItems="${NotificaDomiciliConcretTipus}" optionValueAttribute="valor"
				optionTextAttribute="codi" textKey="interessat.form.camp.entregatipus" labelSize="2" disabled="true"/>
			</div>-->
			
			<div class="col-xs-6 personafisica personajuridica hidden">
				<hel:inputTextarea name="direccio" textKey="interessat.form.camp.direccio" labelSize="3" /> 					
			</div>
			<div class="col-xs-6">
				<hel:inputTextarea name="observacions" textKey="interessat.form.camp.observacions" labelSize="3" /> 					
			</div>
			<!--  <div class="row">
				<div class="col-xs-6">
					<hel:inputTextarea name="linia1" textKey="interessat.form.camp.linia1"  required="true"/>
				</div>
				<div class="col-xs-6">
					<hel:inputTextarea name="linia2" textKey="interessat.form.camp.linia2" required="true"/>
				</div>
			</div>-->
		</div>
		<div class="row hidden">
			<div class="col-xs-8" style="float: right;">
				<hel:inputCheckbox name="entregaDeh" textKey="interessat.form.camp.entregadeh" labelSize="11"></hel:inputCheckbox>
			</div>
		</div>		
		<div class="row" class="hidden">		
			<div class="col-xs-8" style="float: right;" id="entregaDehObligatDiv">
				<hel:inputCheckbox name="entregaDehObligat" textKey="interessat.form.camp.entregadehobligat" labelSize="11"></hel:inputCheckbox>
			</div>
			<div class="col-xs-6 hidden">
				<hel:inputText required="false" name="dir3Codi" textKey="interessat.form.camp.dir3codi" />
			</div>
		</div>	
	</div>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true">
				<spring:message code="comu.boto.cancelar"/>
			</button>
			<button type="submit" class="btn btn-success right">
				<span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/>
			</button>
		</div>
	</form:form>
</body>
</html>
