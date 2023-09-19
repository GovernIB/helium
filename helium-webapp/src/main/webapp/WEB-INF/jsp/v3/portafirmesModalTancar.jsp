<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="fluxid">${fluxId}</c:set>

<html>
<head>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script>
let fluxIframe = window.frameElement;

if (fluxIframe) {
	const idTransaccioFlux = "${fluxId}";
	const fluxErrorDesc = "${FluxError}";
	const fluxSuccesDesc = "${FluxCreat}";
	const fluxCreatedNom = "${FluxNom}";
	const $modalFlux = $(fluxIframe.parentElement.parentElement).prev();
	var alerta;
	if (idTransaccioFlux != null && idTransaccioFlux != '') {
		var newOption = "<option value=\"" + idTransaccioFlux + "\" selected>" + fluxCreatedNom + "</option>";
		$portafirmesFluxId = $modalFlux.find('#portafirmesFluxId');
		$portafirmesFluxId.append(newOption).val(idTransaccioFlux);
		$portafirmesFluxId.val(idTransaccioFlux);
		// No actualitza el text seleccionat, hactualitzem el div del select2
		$modalFlux.find('#s2id_portafirmesFluxId').find('.select2-chosen').html(fluxCreatedNom);
		$portafirmesFluxId.change();
	} else if (fluxErrorDesc != null && fluxErrorDesc != '') 		
	{
		alerta = fluxErrorDesc;
		//desactivar selecció si s'ha creat un nou flux
		if (localStorage.getItem('transaccioId') == null && localStorage.getItem('transaccioId') == '') {
			$modalFlux.find('#portafirmesFluxId').attr('disabled', false);
		}

		$modalFlux.find(".portafirmesEnviarFluxId_btn_addicional").find('i').addClass('fa-eye').removeClass('fa-eye-slash');
	}
	if (fluxSuccesDesc != null && fluxSuccesDesc != '') {

		// Flux creat des de la modal d'enviament de l'usuari, informa el camp ocult per poder enviar fent referència al nou camp
		$modalFlux.find('#portafirmesNouFluxId').val(idTransaccioFlux);
		
		$modalFlux.find('#portafirmesEnviarFluxId').empty();
		$modalFlux.find('#portafirmesEnviarFluxId').attr('disabled', true);
		//desactivar botó de visualitzar
		$modalFlux.find('.portafirmesEnviarFluxId_btn_addicional').attr('disabled', true);
		alerta = fluxSuccesDesc;
		if ((fluxCreatedNom != null && fluxCreatedNom != '')) {
			var $comentari = $modalFlux.find('.comentari');
			$comentari = $modalFlux.find('.comentari');
			$comentari.text('');
			//if flux success text exists
			$modalFlux.find('#portafirmesEnviarFluxId').closest('.form-group').prev('p').remove();
			$modalFlux.find('#portafirmesEnviarFluxId').closest('form').find('.success-label').removeClass('hidden');
			$modalFlux.find('#portafirmesEnviarFluxId').closest('.form-group').before('<p class="success col-xs-8"></p>');
			
			var $success =  $modalFlux.find('.success');
			var text = '<spring:message code='expedient.tipus.document.form.camp.portafirmes.flux.seleccionat'/>';
			$success.html(text + " <span>" + fluxCreatedNom + "</span>");
			$success.css('color', '#3c763d');
			$success.find('span').css('font-weight', 'bold');
		}
	}
	$modalFlux.removeClass('hidden');
	$modalFlux.find('.alert').remove();
	if (alerta) {
		webutilAlertaWarning(alerta, '#divAlertesFlux');
	}
	
	//Adjust modal width/height
	adjustModalPerFluxRemove(fluxCreatedNom);
	$(fluxIframe.parentElement).trigger('remove');
}

function adjustModalPerFluxRemove(fluxCreatedNom) { 
	let $iframe = $(window.parent.frameElement);
	let height = localStorage.getItem('currentIframeHeight');
	$iframe.css('height', (height =! null) ? height : '50vh');
	$iframe.parent().css('height', 'auto');
	$iframe.closest('div.modal-content').css('height',  '');
	$iframe.closest('div.modal-dialog').css({
		'height':'',
		'margin': '30px auto',
		'padding': '0'
	});
	$iframe.closest('div.modal-lg').css('width', '900px');
	$iframe.parent().next().removeClass('hidden');
	if ($iframe.parent().next().find('button').hasClass('disabled') && (fluxCreatedNom != null && fluxCreatedNom != '')) {
		$iframe.parent().next().find('button').removeClass("disabled");
	}
	
	localStorage.removeItem('currentIframeHeight');
	webutilModalAdjustHeight($iframe);
}

</script>
</head>
</html>
