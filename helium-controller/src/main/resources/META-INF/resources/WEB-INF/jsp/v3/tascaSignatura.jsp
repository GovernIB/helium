<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%-- SENSE US --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<style type="text/css">
	#tasca-signatura .well.well-small {margin: 0 0 15px;}
	#tasca-signatura .form-tasca .modal-botons {padding-bottom: 25px;}
	.signarTramitacio .col-xs-1 {width: auto;padding-left: 0px;}	
	.signarTramitacio h4.titol-missatge {width: 100%;display: inline-table;}
	.signarTramitacio .titol-missatge label {padding-top: 0px;}
	.signarTramitacio .titol-missatge .obligatori {background-position: right 8px;}
	.signarTramitacio h4.titol-missatge {width: 100%;display: inline-table;}
	.signarTramitacio form {padding-top: 25px;}
	.signarTramitacio .col-xs-4 {width: 20%;}
	.signarTramitacio .col-xs-10 {width: 80%;padding-right: 0px;}		
	.signarTramitacio .inlineLabels a {margin-left: 10px;}
	.signarTramitacio .select2-container a {margin-left: 0px;}
	.signarTramitacio .select2-container {width: 100% !important;}
	.signarTramitacio .ctrlHolder {padding-bottom: 10px;}
	.modal-botons-firma {padding-top: 15px; padding-bottom: 10px; margin-bottom: 15px; text-align: right;}
	.modal-botons-firma button {margin-left: 5px;}
</style>
<c:if test="${not tasca.documentsComplet}">
	<div class="alert alert-warning">	
		<button type="button" class="close" data-dismiss="alert" aria-label="<spring:message code="comu.boto.tancar"/>"><span aria-hidden="true">&times;</span></button>
		<p>
			<span class="fa fa-warning"></span>
			<spring:message code="tasca.signa.no_es_podran"/>
		</p>
	</div>
</c:if>
<c:if test="${not tasca.signaturesComplet}">
	<div class="alert alert-warning alert-valid">
		<button type="button" class="close" data-dismiss="alert" aria-label="<spring:message code="comu.boto.tancar"/>"><span aria-hidden="true">&times;</span></button>
		<p>
			<span class="fa fa-warning"></span>
			<spring:message code="tasca.tramitacio.firmes.no.complet"/>
		</p>
	</div>
</c:if>
<c:set var="sourceUrl" value="${globalProperties['es.caib.helium.base.url']}/v3/expedient/document/arxiuPerSignar"/>
<c:forEach var="document" items="${signatures}">
	<div class="signarTramitacio well well-small">
		<div class="form-horizontal form-tasca">
			<span class="hide">numPluginsPassarela: ${numPluginsPassarela}</span>
			<div class="inlineLabels">
				<h4 class="titol-missatge">
					<label class="control-label col-xs-1 <c:if test="${document.required}">obligatori</c:if>">${document.documentNom}</label>
					<c:choose>
						<c:when test="${not empty document.tokenSignatura}">
							<c:choose>
								<c:when test="${not document.signat}">
									<c:url value="/v3/tasca/${tasca.id}/document/${document.documentCodi}/descarregar" var="downloadUrl"></c:url>
									<a title="<spring:message code="comuns.descarregar"/>" class="icon" id="downloadUrl${document.id}" href="${downloadUrl}">
										<i class="fa fa-download"></i>
									</a>
								</c:when>
								<c:otherwise>
									<a class="icon signature" href="<c:url value="/v3/expedient/${tasca.expedientId}/proces/${tasca.processInstanceId}/document/${document.documentStoreId}/descarregar"/>"><span class="fa fa-download" title="<spring:message code="comuns.descarregar"/>"></span></a>
									<c:if test="${not empty document.urlVerificacioCustodia}">
										<a class="icon signature" href="${document.urlVerificacioCustodia}" target="_blank"><span class="fa fa-certificate" title="<spring:message code="expedient.document.signat"/>"></span></a>
									</c:if>								
									<c:if test="${not empty document.signaturaUrlVerificacio}">
										<c:choose>
											<c:when test="${document.ntiCsv != null}">
												<!--  Url del ConCSV per l'Arxiu -->
												<a class="icon signature" href="${document.signaturaUrlVerificacio}" target="_blank"><span class="fa fa-certificate" title="<spring:message code="expedient.document.signat"/>"></span></a>
											</c:when>
											<c:otherwise>
												<!-- S'ha de consultar el CSV -->
												<a class="icon signature" href="<c:url value='/v3/expedient/${tasca.expedientId}/proces/${tasca.processInstanceId}/document/${document.documentStoreId}/signatura/verificarCsv'/>" target="_blank"><span class="fa fa-certificate" title="<spring:message code="expedient.document.signat"/>"></span></a>
											</c:otherwise>
										</c:choose>
									</c:if>
								</c:otherwise>
							</c:choose>
							<c:if test="${document.registrat}">
								<a 	data-rdt-link-modal="true" 
									class="icon registre" 
									href="<c:url value='/modal/v3/expedient/${tasca.expedientId}/proces/${tasca.processInstanceId}/document/${document.documentStoreId}/registre/verificar'/>">
									<span class="fa fa-book" title="<spring:message code='expedient.document.registrat' />"></span>
								</a>
							</c:if>
							</h4>
							<c:if test="${!bloquejarEdicioTasca}">
								<div id="firmar${document.id}">
									<c:if test="${not document.signat}">
										<c:choose>
											<c:when test="${numPluginsPassarela > 0}"><c:set var="stils" value="uniForm hide"/></c:when>
											<c:otherwise><c:set var="stils" value="uniForm"/></c:otherwise>
										</c:choose>
										
										<c:if test="${numPluginsPassarela > 0}">
											<div id="botons${document.id}" class="modal-botons-firma">
												<button type="button" onclick="obrirFinestraFirma('<c:url value="/modal/v3/tasca/${tasca.id}/document/${document.id}/firmaPassarela"/>');" class="btn btn-default"><spring:message code="tasca.signa.signar.passarela"/></button>
											</div>
										</c:if>
										<c:if test="${numPluginsPassarela == 0}">
											<div class="alert alert-warning">
												<span class="fa fa-warning"/> No hi ha plugins de passarel·la de firma disponibles per signar
											</div>
										</c:if>
									</c:if>
								</div>
							</c:if>
														
						</c:when>
						<c:otherwise>
							</h4>
							<div class="no-disponible"><spring:message code="expedient.document.no_disponible"/></div>
						</c:otherwise>
					</c:choose>
			</div>
		</div>
	</div>
</c:forEach>

<!-- Scripts per a signatura amb applet -->

<script type="text/javascript">

var finestraFirma;

function refreshSignatures() {
	window.location.href = '<c:url value="/modal/v3/tasca/${tasca.id}/signatura"/>';
}

function obrirFinestraFirma(url) {
	// Obre la nova finestra per firmar
	finestraFirma = window.open(url, 'Firma passarel.la', 'location=0,status=0,scrollbars=0,resizable=0,directories=0,toolbar=0,titlebar=0,width=800,height=450,top=200,left=200');
	
	// Comprova periòdicament si la finestra s'ha tancat
	var timer = setInterval(function() { 
	    if(finestraFirma.closed) {
	        clearInterval(timer);
	        refreshSignatures();
	    }
	}, 1000);
}


$(document).ready(function() {
	
	$(document).on('show.bs.modal', '.modal', function (event) {
        var zIndex = 1040 + (10 * $('.modal:visible').length);
        $(this).css('z-index', zIndex);
        setTimeout(function() {
            $('.modal-backdrop').not('.modal-stack').css('z-index', zIndex - 1).addClass('modal-stack');
        }, 0);
    });
	
	$('.icon.signature, .icon.registre').heliumEvalLink({
		refrescarAlertes: true,
		refrescarPagina: false,
		alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>"
	});
});


</script>
