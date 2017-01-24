<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%-- SENSE US --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
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
<c:set var="sourceUrl" value="${globalProperties['app.base.url']}/v3/expedient/document/arxiuPerSignar"/>
<c:forEach var="document" items="${signatures}">
	<div class="signarTramitacio well well-small">
		<div class="form-horizontal form-tasca">
			<div class="inlineLabels">
				<h4 class="titol-missatge">
					<label class="control-label col-xs-1 <c:if test="${document.required}">obligatori</c:if>">${document.documentNom}</label>
					<c:choose>
						<c:when test="${not empty document.tokenSignatura}">
							<c:url value="/v3/expedient/document/arxiuMostrar" var="downloadUrl"><c:param name="token" value="${document.tokenSignatura}"/></c:url>
							<a title="<spring:message code='comuns.descarregar' />" class="icon" id="downloadUrl${document.id}" href="${downloadUrl}">
								<i class="fa fa-download"></i>
							</a>
							
							<c:if test="${document.signat}">																					
								<a 	data-rdt-link-modal="true" 
									<c:if test="${not empty document.urlVerificacioCustodia}">data-rdt-link-modal-min-height="400"</c:if>
									class="icon signature" 
									href="<c:url value='/modal/v3/tasca/${tasca.id}/verificarSignatura/${document.documentStoreId}/${document.documentCodi}'/>?urlVerificacioCustodia=${document.urlVerificacioCustodia}">
									<span class="fa fa-certificate" title="<spring:message code='expedient.document.signat' />"></span>
								</a>
							</c:if>
							<c:if test="${document.registrat}">
								<a 	data-rdt-link-modal="true" 
									class="icon registre" 
									href="<c:url value='/modal/v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.documentStoreId}/registre/verificar'/>">
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
										<form:form id="form${document.id}" action="${globalProperties['app.base.url']}/modal/v3/tasca/${tasca.id}/signarAmbToken" cssClass="${stils}" method="POST" onsubmit="return false;">
											<input type="text" id="docId${document.id}" name="docId" value="${document.id}"/>
											<input type="text" id="taskId${document.id}" name="taskId" value="${tasca.id}"/>
											<input type="text" id="token${document.id}" name="token" value="${document.tokenSignatura}"/>
											
											<input type="text" id="data0'" name="data" value="safs234j2io4j234jo"/>
											<input type="submit" value="Submit">
											
											<div class="form-group">
												<label class="control-label col-xs-4" id="lcerts${document.id}" for="certs${document.id}"><spring:message code="tasca.signa.camp.cert"/></label>
												<div class="col-xs-10">
									            	<select id="certs${document.id}" name="certs">
														<option value=""><spring:message code="expedient.document.signat.carregant"/></option>
													</select>
										       </div>
								        	</div>
								        	
											<div class="form-group">
												<label class="control-label col-xs-4" for="passwd${document.id}"><spring:message code="tasca.signa.camp.passwd"/></label>
												<div class="col-xs-10">
									           		<input type="password" id="passwd${document.id}" name="passwd" class="form-control"/>
												</div>
								        	</div>
								        	
											<div class="modal-botons-firma">
												<button id="modal-botons${document.id}" class="hide pull-right btn btn-primary right boto-applet" onclick="signarCaib('${document.tokenSignatura}', this.form, '1');"><spring:message code="tasca.signa.signar"/></button>
  												<c:if test="${numPluginsPassarela > 0}">
  													<button id="applet-tancar${document.id}" class="pull-right btn btn-default right" data-formid="form${document.id}" data-botonsid="botons${document.id}"><spring:message code="comu.boto.tancar"/></button>
  												</c:if>
											</div>
										</form:form>
											
										<!-- Modal Passarel·la -->
										<div id="modalPassarela${document.id}" class="modal fade" role="dialog">
  											<div class="modal-dialog modal-lg">

											    <!-- Modal content-->
											    <div class="modal-content">
      												<div class="modal-header">
        												<button type="button" class="close" data-dismiss="modal">&times;</button>
        												<h4 class="modal-title"><spring:message code="tasca.signa.signar.passarela"/></h4>
      												</div>
													<iframe id="passIframe" src="<c:url value="/modal/v3/tasca/${tasca.id}/document/${document.id}/firmaPassarela"/>" height="380px" width="100%" style="border: 0;"></iframe>
													<button type="button" id="dismiss" class="btn btn-default hide" data-dismiss="modal"><spring:message code="comu.boto.tancar"/></button>
    											</div>
    											<script type="text/javascript">
    												$('#dismiss, #modalPassarela${document.id} button.close').click(function() {
    													window.location.href = '<c:url value="/modal/v3/tasca/${tasca.id}/signatura"/>';
    												});
    											</script>
  											</div>
										</div>
										
										<c:if test="${numPluginsPassarela > 0}">
											<div id="botons${document.id}" class="modal-botons-firma">
												<button type="button" class="btn btn-default" data-toggle="modal" data-target="#modalPassarela${document.id}"><spring:message code="tasca.signa.signar.passarela"/></button>
												<button type="button" class="btn btn-default applet" data-formid="form${document.id}" id="bapplet${document.id}"><spring:message code="tasca.signa.signar.applet"/></button>
											</div>
										</c:if>

									</c:if>
								</div>
							</c:if>
							
							<script type="text/javascript">
								$(document).ready( function() {
									
									$.get("${sourceUrl}?token=${document.tokenSignatura}")
									.done(function(data) {})
									.fail(function(xhr, status, error) {
										$('#contingut-alertes').append(
												"<div id='errors' class='alert alert-danger'>" +
													"<button class='close' data-dismiss='alert'>×</button>" +
													"<p><spring:message code='tasca.signa.alert.no.document'/>: " + xhr.responseText.match(/.*<h1.*>([\s\S]*)<\/h1>.*/) + "</p>" +
												"</div>");
										$("#modal-botons${document.id}").addClass('hide');
									});
									
									$('#dismissap').click(function() {
										window.location.href = '<c:url value="/modal/v3/tasca/${tasca.id}/signatura"/>';
									});
									$('#bapplet${document.id}').click(function() {
										$("#" + $(this).data("formid")).removeClass('hide');
										$(this).parent().addClass("hide");
									});
									$("#applet-tancar${document.id}").click(function() {
										$("#" + $(this).data("formid")).addClass('hide');
										$("#" + $(this).data("botonsid")).removeClass('hide');
									});
								});
							</script>
							
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
<div id="applet"></div>

<!-- Scripts per a signatura amb applet -->

<script type="text/javascript">
$(document).ready(function() {
	docWriteWrapper($('#applet'), function () {
		var attributes = {
				id: 'signaturaApplet',
				code: 'net.conselldemallorca.helium.applet.signatura.SignaturaMockApplet',
				archive: '<c:url value="/signatura/caib/helium-applet.jar"/>',
				width: 1,
				height: 1};
		if (typeof(deployJava) != "undefined") {
			deployJava.runApplet(
					attributes,
					{},
					'1.5');
			obtenirCertificats();
		} 
	});
	
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

function docWriteWrapper(jq, func) {
    var oldwrite = document.write, content = '';
    document.write = function(text) {
        content += text;
    }
    func();
    document.write = oldwrite;
    jq.html(content);
}

function obtenirCertificats() {
	
	try {
		if (typeof(signaturaApplet) != "undefined") {
		 	if (typeof(signaturaApplet.findCertificats) != "undefined") {
				var certs = signaturaApplet.findCertificats(1);
				if (!certs) {
					alert("<spring:message code='tasca.signa.alert.certerr'/>");
					$(".boto-applet").hide();
				} else {
					$('select[name=certs]').empty();
					if (certs.length == 0) {
						$('select[name=certs]').append($('<option>', { 
					        value: -1,
					        text : "<spring:message code='tasca.signa.alert.nocert'/>" 
					    }));
						$(".boto-applet").hide();
					} else {
						$.each(certs, function (i, item) {
							$('select[name=certs]').append($('<option>', { 
						        value: item,
						        text : item 
						    }));
						});
						$(".boto-applet").removeClass('hide');
					}
					$('select[name=certs]').select2({
						width:'resolve',
					    allowClear: true,
					    minimumResultsForSearch: 10
					});
				}
		 	} else {
		 		setTimeout("obtenirCertificats()", 1000);
		 	}
		} else {
			setTimeout("obtenirCertificats()", 1000);
	 	}
	} catch (e) {
		setTimeout("obtenirCertificats()", 1000);
	}
}
function signarCaib(token, form, contentType) {
	var cert = form.certs.value;
	if (cert == null || cert.length == 0) {
		alert("<spring:message code='tasca.signa.alert.nosel'/>");
	} else {
		try {
			var signaturaB64 = signaturaApplet.signaturaPdf(
					"${sourceUrl}?token=" + token,
					cert,
					form.passwd.value,
					contentType);
			if (signaturaB64 == null) {
				$('#contingut-alertes').append(
					"<div id='errors' class='alert alert-danger'>" +
						"<button class='close' data-dismiss='alert'>×</button>" +
						"<p><spring:message code='tasca.signa.alert.error'/></p>" +
					"</div>");
			} else {
				if (signaturaB64.length > 0) {
					for (var i = 0; i < signaturaB64.length; i++) {
						$(form).append( '<input type="hidden" id="data'+i+'" name="data" value="'+signaturaB64[i]+'"/>' );
					}
					$(form).removeAttr('onsubmit');
					$(form).submit();
				} else {
					$('#contingut-alertes').append(
							"<div id='errors' class='alert alert-danger'>" +
								"<button class='close' data-dismiss='alert'>×</button>" +
								"<p><spring:message code='tasca.signa.alert.no.document.signar'/>: ${sourceUrl}?token=" + token + "</p>" +
							"</div>");
				}
			}
		} catch (e) {
			$('#contingut-alertes').append(
					"<div id='errors' class='alert alert-danger'>" +
						"<button class='close' data-dismiss='alert'>×</button>" +
						"<p>" + e +"</p>" +
					"</div>");
		}
	}
}
</script>
