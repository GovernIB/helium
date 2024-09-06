<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:choose>
	<c:when test="${empty interessatCommand.id}">
		<c:if test="${!es_representant}">
			<c:set var="titol"><spring:message code="interessat.form.titol.nou"/></c:set>
		</c:if>
		<c:if test="${es_representant}">
			<c:set var="titol"><spring:message code="interessat.form.titol.nou.representant"/>
			</c:set>
		</c:if>
		<c:set var="formAction">new</c:set>
	</c:when>
	<c:otherwise>
		<c:if test="${!es_representant}">
			<c:set var="titol">
				<spring:message code="interessat.form.titol.modificar"/>
				${tipus}
			</c:set>
		</c:if>
		<c:if test="${es_representant}">
			<c:set var="titol">
				<spring:message code="interessat.form.titol.modificar.representant"/>
				${tipus}
			</c:set>
		</c:if>
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

	
function adaptarVisibilitat(tipus){
		//alert(tipus);
		//netejar();
		var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
		let nif="NIF";
		let cif="CIF";
		let passaport="PASSAPORT";
		let document_identificatiu_estrangers="DOCUMENT_IDENTIFICATIU_ESTRANGERS";
		let altres_de_persona_fisica="ALTRES_DE_PERSONA_FISICA";
		let codi_origen="CODI_ORIGEN";

		 if (tipus == 'FISICA'){
			 $('.visibilitatCodi').removeClass('hidden');
			 $('.personajuridica').addClass('hidden');
			 $('.administracio').addClass('hidden');
			 $('.personafisica').removeClass('hidden');
			 $('#tipusDocIdent option[value="'+codi_origen+'"]').prop('disabled',true); 
		} else if (tipus == 'JURIDICA'){
			 $('.visibilitatCodi').removeClass('hidden');
			 $('.personafisica').addClass('hidden');
			 $('.administracio').addClass('hidden');
			 $('.personajuridica').removeClass('hidden');	
			 //$('#tipusDocIdent').prop("readonly", true);
			 $('#tipusDocIdent option[value="'+codi_origen+'"]').prop('disabled',true); 
			 $('#tipusDocIdent option[value="'+altres_de_persona_fisica+'"]').prop('disabled',true); 
			 $('#tipusDocIdent option[value="'+passaport+'"]').prop('disabled',true); 
			 
		}else if (tipus == 'ADMINISTRACIO'){
			 $('.visibilitatCodi').removeClass('hidden');
			 $('.personafisica').addClass('hidden');
			 $('.personajuridica').addClass('hidden');
			 $('.administracio').removeClass('hidden');
	 	 	/*  $('#tipusDocIdent').val("CODI_ORIGEN"); */
			 $('#tipusDocIdent').prop("readonly", true);
		}
		 $('#tipusDocIdent').change();
		 $('#tipusDocIdent').select2("destroy");
	 	 $('#tipusDocIdent').select2(select2Options);
}

function netejar(){
		$('#pais').val("");
		$('#pais').val("724");
		$('#pais').change();
		$('#provincia').val("");
		$('#provincia').change();
		$('#municipi').val("");
		$('#municipi').change();
		$('#codiPostal').val("");
		$('#direccio').val("");
		$('#documentIdent').val("");
		$('#tipusDocIdent').val("NIF");
}

$(document).ready(function() {
	var organsCarregats = <c:out value="${not empty organs}"/>;
	var munOrgan = '';

	adaptarVisibilitat($("#tipusHiddenId").val());

 	$('input[type=radio][name=tipus]').on('change', function() {
 		//adaptarSuggest(this.value);
 		adaptarVisibilitat($(this).val());
		webutilModalAdjustHeight();
		var tipusInt = 1;
 		if (this.value == '<%=net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto.FISICA%>') {
 			tipusInt = 1;
			$('#tipusDocIdent').val("NIF");
			$('#tipusDocIdent').prop("readonly", false);
		
 		} else if (this.value == '<%=net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto.JURIDICA%>') {
 			tipusInt = 2;
 	 	} else {
 			tipusInt = 3;
 	 		$('#tipusDocIdent').val("CODI_ORIGEN");
			$('#tipusDocIdent').prop("readonly", true);
 	 	}
 		$('#tipusDocIdent').change();
		$('#tipusDocIdent').select2("destroy");
 	 	$('#tipusDocIdent').select2(select2Options);
	});
	
 	
 	$('select#canalNotif').change(function() {
 		//alert($(this).val());
 		if ($(this).val() == '01') { //DIRECCION_POSTAL("01", "Direcció Postal")
 			//alert($(this).val());
 			$('select#pais').prop("required", true);
 			$('select#provincia').prop("required", true);
			$('select#municipi').prop("required", true);
			$('#direccio').prop("required", true);
			$('#codiPostal').prop("required", true);
			$('#email').prop("required", false);
 		} else if ($(this).val() == '02' || $(this).val() == '03') {
 			//alert($(this).val());
 			// DIRECCION_ELECTRONICA_HABILITADA("02", "Direcció electrònica habilitada")
 			//COMPARECENCIA_ELECTRONICA("03", "Compareixença electrònica");
 			$('select#pais').prop("required", false);
 			$('select#provincia').prop("required", false);
			$('select#municipi').prop("required", false);
			$('#direccio').prop("required", false);
			$('#codiPostal').prop("required", false);
			$('#email').prop("required", true);
 		}
 	});

 	
	$('select#pais').change(function() {
 		if ($(this).val() == '724') {
 	 		if ($('select#tipus').val() != '<%=net.conselldemallorca.helium.v3.core.api.dto.InteressatTipusEnumDto.ADMINISTRACIO%>') {
				$('#provincia').change();
 	 			$('#provincia').prop("readonly", false);
				$('#municipi').change();
				$('#municipi').prop("readonly", false);
 	 		} else {
				$('#provincia').change();
 	 			$('#provincia').prop("readonly", true);
				$('#municipi').change();
				$('#municipi').prop("readonly", true);
 	 	 	}
		} else {
			$('#provincia').val("");
 	 		$('#provincia').change();
			$('#provincia').prop("readonly", true);
			$('#municipi').val("");
 	 		$('#municipi').change();
			$('#municipi').prop("readonly", true);
		}
 	});
	
	
	var municipiActual = $('#municipi').val();
 	$('select#provincia').change(function(valor) {
 		if ($(this).val() != '') {
 			$.ajax({
				type: 'GET',
				url: "<c:url value="/v3/expedient/municipis/"/>" + $(this).val(),
				success: function(data) {
					var selMunicipi = $('#municipi');
					selMunicipi.empty();
					selMunicipi.append("<option value=\"\"></option>");
					if (data && data.length > 0) {
						var items = [];
						$.each(data, function(i, val) {
							items.push({
								"id": val.codi,
								"text": val.nom
							});
							selMunicipi.append("<option value=\"" + val.codi + "\">" + val.nom + "</option>");
						});
					}
					var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
					selMunicipi.select2("destroy");
					selMunicipi.select2(select2Options);
					if (munOrgan != '') {
						selMunicipi.val(munOrgan);
						selMunicipi.change();
					}
					
					if (municipiActual)
						selMunicipi.val(municipiActual);
					else
						selMunicipi.val("407");
					selMunicipi.change();
				}
			});
 	 	} else {
 	 		var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
 	 		$('#municipi').select2("destroy");
 	 		$('#municipi').select2(select2Options);
 	 	}
 	});
 	
 	$('select#cifOrganGestor').change(function() {
 	 	 		munOrgan = '';
 	 	 		//alert($(this).val());
 	 	 	 	if ($(this).val() != "") {
 	 	 	 		
 	 	 	 		let optionSelected = $("option:selected", this);
 	 	 	 		var select2Options = {theme: 'bootstrap', minimumResultsForSearch: "6"};
 	 		 		$.ajax({
 	 					type: 'GET',
 	 					url: "<c:url value="/v3/expedient/organ/"/>" + $(this).val(),
 	 					success: function(data) {
 	 						$('#tipusDocIdent').val("CODI_ORIGEN");
 	 						$('#tipusDocIdent').prop("readonly", true);
 	 						$('#tipusDocIdent').change();
 	 			 	 		$('#tipusDocIdent').select2("destroy");
 	 			 	 		$('#tipusDocIdent').select2(select2Options);

 	 						$('#documentIdent').val(data.codi);
 	 						$('#documentIdent').prop("readonly", true);
 	 						$('#pais').val(data.codiPais);
 	 						$('#pais').prop("readonly", true);
 	 						$('#pais').change();
 	 						$('#pais').select2("destroy");
 	 			 	 		$('#pais').select2(select2Options);

 	 						$('#provincia').val(data.codiProvincia);
 	 						$('#provincia').change();
 	 						$('#provincia').prop("readonly", true);
 	 						$('#provincia').select2("destroy");
 	 			 	 		$('#provincia').select2(select2Options);
 	 						
 	 			 	 		$('#municipi').val(data.localitat);
 	 						$('#municipi').prop("readonly", true);
 	 			 	 		munOrgan = data.localitat;
 	 			 	 		$('#municipi').change();
 	 			 	 		
 	 			 	 		$('#codiPostal').val(data.codiPostal);
 	 						$('#codiPostal').prop("readonly", true);

 	 			 	 		$('#direccio').val(data.adressa);
 	 						$('#direccio').prop("readonly", true);

 	 			 	 		//$('#ambOficinaSir').val(optionSelected.hasClass('ambOficinaSir'));
 	 					}
 	 				});
 	 	 	 	} else {
 	 	 	 		netejar();
 	 	 	 	}
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


/* 	var select2Options = {theme: 'bootstrap'};
	$('select[name=tipus').select2("destroy");
	$('select[name=tipus').select2(select2Options); */
	
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
	<form:hidden id="es_representant" path="es_representant"/>
	<div class="tipusInteressats">	
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
		<form:hidden id="tipusHiddenId" path="tipus"/>	
	</div>

	
	<div class="hidden visibilitatCodi">	
		<div class="row" style="margin-right:-14px ; margin-left:-59px">
			<div class="col-xs-12">
				<hel:inputText required="true" name="codi" textKey="interessat.form.camp.codi" labelSize="2" />
			</div>
		</div>
		
			
	<div class="row hidden administracio" style="margin-right:-14px ; margin-left:-59px">
		<div class="col-xs-12">				
			<hel:inputSelect 
				required="true" 
				name="cifOrganGestor"
				optionItems="${organs}" 
				optionValueAttribute="codi"
				optionTextAttribute="denominacio" 
				textKey="interessat.form.camp.suggest.administracio" 
				emptyOption="true"
				labelSize="2" 
				inline="false"/>
		</div>	
	</div>	
	

			<div class="row hidden personafisica nom" style="margin-right:-14px ; margin-left:-59px">
				<div class="col-xs-12" >
					<hel:inputText required="true" name="nom" textKey="interessat.form.camp.nom" labelSize="2" />
				</div>
			</div>
			<div class="row hidden personafisica llinatges">
				<div class="col-xs-6">
					<hel:inputText name="llinatge1" textKey="interessat.form.camp.llinatge1" labelSize="3" required="true"/>
				</div>
				<div class="col-xs-6">
					<hel:inputText name="llinatge2" textKey="interessat.form.camp.llinatge2" labelSize="3" required="false"/>
				</div>	
			</div>
		
			<div class="row hidden personajuridica raoSocial" style="margin-right:-14px ; margin-left:-59px">
				<div class="col-xs-12">
					<hel:inputText name="raoSocial" textKey="interessat.form.camp.raosocial" labelSize="2" required="true"/>
				</div>	
			</div>
			
			<div class="row hidden personajuridica personafisica administracio tipusdocument">
				<div class="col-xs-6">
					<hel:inputSelect 
						readonly="false"
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
					<hel:inputText required="true" name="documentIdent" textKey="interessat.form.camp.document.identificatiu" labelSize="3" inline="false"/>
				</div>
			</div>

		
			<div class="row emailTelefon personafisica personajuridica administracio">
				<div class="col-xs-6">
					<hel:inputText name="email" textKey="interessat.form.camp.email" labelSize="3"/>		
				</div>
				<div class="col-xs-6">
					<hel:inputText name="telefon" textKey="interessat.form.camp.telefon" labelSize="3"/>
				</div>
			</div>
			
			<div class="row paisProvincia personafisica personajuridica administracio">
				<div class="col-xs-6">
					<hel:inputSelect 
						readonly="false"
						emptyOption="true"
						name="pais"
						optionItems="${paisos}" 
						optionValueAttribute="codi"
						optionTextAttribute="nom" 
						textKey="interessat.form.camp.pais" 
						labelSize="3" 
						inline="false"/>
				</div>
				<div class="col-xs-6">
				<hel:inputSelect 
						readonly="false"
						emptyOption="true"
						required="false" 
						name="provincia"
						optionItems="${provincies}" 
						optionValueAttribute="codi"
						optionTextAttribute="nom" 
						textKey="interessat.form.camp.provincia" 
						labelSize="3" 
						inline="false"/>
				</div>
			</div>
			
		<div class="row localitatCodipostal personafisica personajuridica administracio">
			<div class="col-xs-6">
			<hel:inputSelect 
					readonly="false"
					emptyOption="true"
					required="false" 
					name="municipi"
					optionItems="${municipis}" 
					optionValueAttribute="codi"
					optionTextAttribute="nom" 
					textKey="interessat.form.camp.municipi" 
					labelSize="3" 
					inline="false"/>
			</div>
			<div class="col-xs-6">
				<hel:inputText name="codiPostal" textKey="interessat.form.camp.codipostal" labelSize="3" required="false"/>	
			</div>
		</div>
		
		<div class="row">
			<div class="hidden" style="float: right;">
				<hel:inputCheckbox name="entregaPostal" textKey="interessat.form.camp.entregaPostal" labelSize="11"></hel:inputCheckbox>
			</div>
		</div>
		<div id="entrgePostalForm" class="row">
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
			
			<div class="col-xs-6 personafisica personajuridica administracio hidden">
				<hel:inputTextarea name="direccio" textKey="interessat.form.camp.direccio" labelSize="3" /> 					
			</div>
			<div class="col-xs-6">
				<hel:inputSelect 
					name="canalNotif"
					optionItems="${interessatCanalsNotif}" 
					optionValueAttribute="valor"
					optionTextAttribute="codi" 
					textKey="interessat.form.camp.canal.notif" 
					labelSize="3" 
					inline="false"/>
			</div>
		</div>
		
		<div id="direCodi" class="row  personafisica personajuridica hidden"  style="margin-right:-14px ; margin-left:-59px">
			<div class="col-xs-12">
					<hel:inputText required="false" name="codiDire" textKey="interessat.form.camp.codi.dire" labelSize="2" /> 
			</div>
		</div>
		
		<div id="dir3Codi" class="row personafisica personajuridica administracio hidden"  style="margin-right:-14px ; margin-left:-59px">
			<div class="col-xs-12">
					<hel:inputText required="false" name="dir3Codi" textKey="interessat.form.camp.dir3codi" labelSize="2" /> 
			</div>
		</div>
		
		<div id="observacions" class="row"  style="margin-right:-14px ; margin-left:-59px">
			
			<div class="col-xs-12">
				<hel:inputTextarea name="observacions" textKey="interessat.form.camp.observacions" labelSize="2" /> 					
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
		<!--  <div class="row" class="hidden">		
			<div class="col-xs-8" style="float: right;" id="entregaDehObligatDiv">
				<hel:inputCheckbox name="entregaDehObligat" textKey="interessat.form.camp.entregadehobligat" labelSize="11"></hel:inputCheckbox>
			</div>
			<div class="col-xs-6 hidden">
				<hel:inputText required="false" name="dir3Codi" textKey="interessat.form.camp.dir3codi" />
			</div>
		</div>	-->

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
