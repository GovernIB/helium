<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="expedient.document.notificar"/></title>
	<hel:modalHead/>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>	
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
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
<style type="text/css">
#s2id_interessatsIds, #s2id_serveiTipusEnum {
	width: 100% !important;
}
</style>
<script>
$(document).ready(function() {

    $('#entregaPostalActiva').on('change', function() { 
        if (this.checked) {
            $('#entregaPostal').removeClass('hidden');
        } else {
            $('#entregaPostal').addClass('hidden');
        }

    })
});
</script>



</head>
<body>		
	<form:form cssClass="form-horizontal form-tasca" action="notificar"  method="post" modelAttribute="documentNotificacioCommand">
		

		<hel:inputSelect required="true" name="interessatsIds" multiple="true" textKey="expedient.document.notificar.form.camp.titulars" placeholderKey="expedient.document.notificar.form.camp.titulars.placeholder" comment="expedient.document.notificar.form.camp.titulars.info" optionItems="${interessats}" optionValueAttribute="id" optionTextAttribute="fullInfo"/>
		<hel:inputSelect required="false" name="representantId" multiple="false" emptyOption="true" textKey="expedient.document.notificar.form.camp.representant" placeholderKey="expedient.document.notificar.form.camp.representant.placeholder" comment="expedient.document.notificar.form.camp.representant.info" optionItems="${interessats}" optionValueAttribute="id" optionTextAttribute="fullInfo"/>
		<hel:inputText required="true" name="concepte" textKey="expedient.document.notificar.form.camp.concepte" />
		<hel:inputSelect required="true" name="serveiTipusEnum" optionItems="${serveiTipusEstats}" optionValueAttribute="codi" optionTextAttribute="valor" textKey="expedient.document.notificar.form.camp.serveiTipus"/>
		<hel:inputTextarea name="descripcio" textKey="expedient.document.notificar.form.camp.descripcio"></hel:inputTextarea>
		<hel:inputSelect required="true" name="enviamentTipus" optionItems="${enviamentTipusEstats}" optionValueAttribute="codi" optionTextAttribute="valor" textKey="expedient.document.notificar.form.camp.enviamentTipus"/>
		<hel:inputDate name="enviamentDataProgramada" textKey="expedient.document.notificar.form.camp.dataprogramada" comment="notificacio.form.camp.data.programada.comment"/>
		<hel:inputDate required="true" name="caducitat" textKey="expedient.document.notificar.form.camp.datacaducitat" comment="notificacio.form.camp.data.caducitat.comment"/>
		<hel:inputNumber name="retard" textKey="expedient.document.notificar.form.camp.retard" comment="notificacio.form.camp.idioma"/>
		
		<hel:inputText name="grupCodi" textKey="expedient.document.notificar.form.camp.grupCodi" />

		<hel:inputSelect name="idioma" optionItems="${idiomes}" emptyOption="true" textKey="notificacio.form.camp.idioma" placeholderKey="anotacio.llistat.filtre.camp.estat" optionValueAttribute="codi" optionTextAttribute="valor"/>
		
		<hel:inputCheckbox name="entregaPostalActiva" textKey="expedient.document.notificar.form.camp.entregaPostalActiva" />
		
<!-- 		<div id="entregaPostal" class="hidden"> -->
<%-- 			<hel:inputSelect required="true" name="entregaPostalViaTipus" optionItems="${entregaPostalViaTipusEstats}" optionValueAttribute="valor" optionTextAttribute="codi" textKey="expedient.document.notificar.form.camp.entregaPostalViaTipus" /> --%>
<%-- 			<hel:inputSelect required="true" name="entregaPostalTipus" optionItems="${entregaPostalTipusEstats}" optionValueAttribute="valor" optionTextAttribute="codi" textKey="expedient.document.notificar.form.camp.entregaPostalTipus" /> --%>
			
			
<%-- 			<hel:inputText required="true" name="entregaPostalViaNom" textKey="expedient.document.notificar.form.camp.entregaPostalViaNom" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalNumeroCasa" textKey="expedient.document.notificar.form.camp.entregaPostalNumeroCasa" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalNumeroQualificador" textKey="expedient.document.notificar.form.camp.entregaPostalNumeroQualificador" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalPuntKm" textKey="expedient.document.notificar.form.camp.entregaPostalPuntKm" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalApartatCorreus" textKey="expedient.document.notificar.form.camp.entregaPostalApartatCorreus" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalPortal" textKey="expedient.document.notificar.form.camp.entregaPostalPortal" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalEscala" textKey="expedient.document.notificar.form.camp.entregaPostalEscala" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalPlanta" textKey="expedient.document.notificar.form.camp.entregaPostalPlanta" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalPorta" textKey="expedient.document.notificar.form.camp.entregaPostalPorta" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalBloc" textKey="expedient.document.notificar.form.camp.entregaPostalBloc" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalComplement" textKey="expedient.document.notificar.form.camp.entregaPostalComplement" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalCodiPostal" textKey="expedient.document.notificar.form.camp.entregaPostalCodiPostal" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalPoblacio" textKey="expedient.document.notificar.form.camp.entregaPostalPoblacio" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalMunicipiCodi" textKey="expedient.document.notificar.form.camp.entregaPostalMunicipiCodi" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalProvinciaCodi" textKey="expedient.document.notificar.form.camp.entregaPostalProvinciaCodi" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalPaisCodi" textKey="expedient.document.notificar.form.camp.entregaPostalPaisCodi" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalLinea1" textKey="expedient.document.notificar.form.camp.entregaPostalLinea1" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalLinea2" textKey="expedient.document.notificar.form.camp.entregaPostalLinea2" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalCie" textKey="expedient.document.notificar.form.camp.entregaPostalCie" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalFormatSobre" textKey="expedient.document.notificar.form.camp.entregaPostalFormatSobre" /> --%>
<%-- 			<hel:inputText required="true" name="entregaPostalFormatFulla" textKey="expedient.document.notificar.form.camp.entregaPostalFormatFulla" /> --%>
<!-- 		</div> -->
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
			<button class="btn btn-primary right" type="submit" name="accio" value="notificar"><spring:message code='comuns.notificar' /></button>
		</div>
	</form:form>
</body>
</html>