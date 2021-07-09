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

var $ADMINISTRACIO = '<%=es.caib.helium.logic.intf.dto.InteressatTipusEnumDto.ADMINISTRACIO%>';
var $FISICA = '<%=es.caib.helium.logic.intf.dto.InteressatTipusEnumDto.FISICA%>';
var $JURIDICA = '<%=es.caib.helium.logic.intf.dto.InteressatTipusEnumDto.JURIDICA%>';

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

$(document).ready(function() {

	
 	$('#tipus').on('change', function() {
		ajustarTipus(this.value);
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

	
	$('input[type=checkbox][name=entregaPostal]').on('change', function() {
		if($(this).prop("checked") == true){
			$('#entrgePostalForm').removeClass('hidden');
			webutilModalAdjustHeight();
		} else {
			$('#entrgePostalForm').addClass('hidden');		
		}
	});	

	$('input[type=checkbox][name=entregaPostal').trigger('change');

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
			<div class="col-xs-12">
				<hel:inputText required="true" name="codi" textKey="interessat.form.camp.codi" labelSize="2" />
			</div>
		</div>
		<hel:inputSelect required="true" name="tipus"
			optionItems="${interessatTipusEstats}" optionValueAttribute="valor"
			optionTextAttribute="codi" textKey="interessat.form.camp.tipus" labelSize="2" />
		<div class="row">
			<div class="col-xs-6">
				<hel:inputText required="true" name="nif" textKey="interessat.form.camp.nif" />
			</div>
			<div class="col-xs-6">
				<hel:inputText required="false" name="dir3Codi" textKey="interessat.form.camp.dir3codi" />
			</div>
		</div>
		<div class="row">
			<div class="col-xs-12">
				<hel:inputText required="true" name="nom" textKey="interessat.form.camp.nom" labelSize="2" />
			</div>
		</div>
		<div class="row">
			<div class="col-xs-6">
			<c:choose>
				<c:when test="${interessatCommand.tipus=='FISICA'}">
					<hel:inputText name="llinatge1" textKey="interessat.form.camp.llinatge1" required="true"/>
				</c:when>
				<c:otherwise>
					<hel:inputText name="llinatge1" textKey="interessat.form.camp.llinatge1"/>			
				</c:otherwise>
			</c:choose>
			</div>
			<div class="col-xs-6">
				<hel:inputText name="llinatge2" textKey="interessat.form.camp.llinatge2" />
			</div>
		</div>
		<div class="row">
			<div class="col-xs-6">
				<hel:inputText name="email" textKey="interessat.form.camp.email" />		
			</div>
			<div class="col-xs-6">
				<hel:inputText name="telefon" textKey="interessat.form.camp.telefon" />
			</div>
		</div>
		<div class="row">
			<div class="col-xs-8" style="float: right;">
				<hel:inputCheckbox name="entregaPostal" textKey="interessat.form.camp.entregaPostal" labelSize="11"></hel:inputCheckbox>
			</div>
		</div>
		<div id="entrgePostalForm" class="hidden">
			<div class="hidden">
				<hel:inputSelect required="true" name="entregaTipus"
					optionItems="${NotificaDomiciliConcretTipus}" optionValueAttribute="valor"
					optionTextAttribute="codi" textKey="interessat.form.camp.entregatipus" labelSize="2"/>
			</div>
			<hel:inputSelect required="true" name="entregaTipus"
				optionItems="${NotificaDomiciliConcretTipus}" optionValueAttribute="valor"
				optionTextAttribute="codi" textKey="interessat.form.camp.entregatipus" labelSize="2" disabled="true"/>
			<hel:inputText name="codiPostal" textKey="interessat.form.camp.codipostal" labelSize="2" required="true"/>
			<div class="row">
				<div class="col-xs-6">
					<hel:inputTextarea name="linia1" textKey="interessat.form.camp.linia1"  required="true"/>
				</div>
				<div class="col-xs-6">
					<hel:inputTextarea name="linia2" textKey="interessat.form.camp.linia2" required="true"/>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-8" style="float: right;">
				<hel:inputCheckbox name="entregaDeh" textKey="interessat.form.camp.entregadeh" labelSize="11"></hel:inputCheckbox>
			</div>
		</div>		
		<div class="row" class="hidden">		
			<div class="col-xs-8" style="float: right;" id="entregaDehObligatDiv">
				<hel:inputCheckbox name="entregaDehObligat" textKey="interessat.form.camp.entregadehobligat" labelSize="11"></hel:inputCheckbox>
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
