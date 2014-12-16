<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="net.conselldemallorca.helium.webapp.v3.helper.TascaFormValidatorHelper"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<style type="text/css">
	.btn-file {position: relative; overflow: hidden;}
	.btn-file input[type=file] {position: absolute; top: 0; right: 0; min-width: 100%; min-height: 100%; font-size: 100px; text-align: right; filter: alpha(opacity = 0); opacity: 0; outline: none; background: white; cursor: inherit; display: block;}
	.form-group {width: 100%;display: inline-flex;}
	.fila_reducida {width: 100%;}
	.col-xs-1 {width: auto;padding-left: 0px;}				
	.col-xs-4 {width: 20%;}		
	.col-xs-8 {width: 77%;}
	.col-xs-8 .form-group {margin-left: 0px;margin-right: 0px;}
	.col-xs-8 .form-group .col-xs-4 {padding-left: 0px;width: 15%;}
	.col-xs-8 .form-group .col-xs-8 {width: 85%;padding-left: 15px;padding-right: 0px;}
	.col-xs-11 {padding-left: 0px;}				
	.select2-container {width: 100% !important;}
	.arxiu {margin-left: 0%; margin-top: 10px;}
	h4.titol-missatge {width: 100%;}
	h4.titol-missatge a{margin-left: 0px;}
	a.icon {margin-left: 10px !important;}
	.comentari {padding-top: 30px;}
	.comentari label {font-weight: bold;}
	.modal-botons {padding-bottom: 30px;padding-top: 15px;}
	.form-horizontal .control-label {padding-top: 0px;}
	.obligatori {background-position: right 8px;}
	.inlineLabels.col.first {padding-top: 10px;padding-bottom: 10px;}
	.inlineLabels .ctrlHolder {padding-top: 10px;}
	.iconos {display: inline;}
	.no-disponible {padding-top:  30px;}
</style>
<c:set var="sourceUrl" value="${globalProperties['app.base.url']}/document/arxiuPerSignar.html"/>
<script src="https://www.java.com/js/deployJava.js"></script>
<script type="text/javascript">
//<![CDATA[
var attributes = {
		id: 'signaturaApplet',
		code: 'net.conselldemallorca.helium.applet.signatura.SignaturaCaibApplet',
		archive: '<c:url value="/signatura/caib/helium-applet.jar"/>',
		width: 1,
		height: 1};
var parameters = {};
deployJava.runApplet(
		attributes,
		parameters,
		'1.5');

function obtenirCertificats(selectId, contentType) {
	if (typeof(signaturaApplet.findCertificats) != "undefined") {
		var certs = signaturaApplet.findCertificats(contentType);
		if (!certs) {
			alert("<spring:message code="tasca.signa.alert.certerr"/>");
			$(".modal-botons button").hide();
		} else if (certs.length == 0) {
			$("#l"+selectId).append(": <spring:message code="tasca.signa.alert.nocert"/>");
			$(".modal-botons button").hide();
		} else {
			$.each(certs, function (i, item) {
			    $('#'+selectId).append($('<option>', { 
			        value: item,
			        text : item 
			    }));
			});
			$("#"+selectId).show();
			$("#"+selectId).select2({
				width:'resolve',
			    allowClear: true,
			    minimumResultsForSearch: 10
			});
			
		}
	} else {
		setTimeout("obtenirCertificats('"+selectId+"', '"+contentType+"')", 1000);
	}
}

function signarCaib(token, form, contentType) {
	var cert = form.certs.value;
	if (cert == null || cert.length == 0) {
		alert("<spring:message code="tasca.signa.alert.nosel"/>");
	} else {
		// Comprobar fichero
		$.get("${sourceUrl}?token=" + token)
		.done(function(data) {
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
 					    $.ajax({
 				            type: 'POST',
 				            url: $(form).attr('action'),
 				            data: $(form).serialize(),
 				            success: function(data) {
 				            	if (data) {
 				            		$("#firmar"+$(form).find('#docId').val()).hide();
 				            		$("#iconos"+$(form).find('#docId').val()).load('<c:url value="/nodeco/v3/expedient/${tasca.expedientId}/tasca/${tasca.id}/icones/'+$(form).find('#docId').val()+'"/>');
 				            	}
 				            	
 				            	// Refrescar alertas
 				            	$.ajax({
 									url: "<c:url value="/nodeco/v3/missatges"/>",
 									async: false,
 									timeout: 20000,
 									success: function (data) {
 										$('#contingut-alertes *').remove();
 										$('#contingut-alertes').append(data);
 									}
 								});
 				            }
 				        });
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
		})
		.fail(function(xhr, status, error) {
			alert("<spring:message code='tasca.signa.alert.no.document'/>: " + xhr.responseText);
		});
	}
}
// ]]>
</script>

<c:forEach var="document" items="${signatures}">
	<div class="well well-small">
		<div class="form-horizontal form-tasca">
			<div class="inlineLabels">
				<h4 class="titol-missatge">
					<label class="control-label col-xs-1 <c:if test="${document.required}">obligatori</c:if>">${document.documentNom}</label>
					<c:choose>
						<c:when test="${not empty document.tokenSignatura}"><c:url value="/v3/expedient/document/arxiuMostrar" var="downloadUrl"><c:param name="token" value="${document.tokenSignatura}"/></c:url>
							<a class="icon" id="downloadUrl${document.id}" href="${downloadUrl}">
								<i class="fa fa-download"></i>
							</a>
							<div id="iconos${document.id}" class="iconos"></div>
							<div id="firmar${document.id}">
								<c:if test="${not document.signat}">						
									<form:form id="form${document.id}" action="signarAmbToken" cssClass="uniForm" method="POST" onsubmit="return false;">
										<input type="hidden" id="docId" name="docId" value="${document.id}"/>
										<input type="hidden" id="taskId" name="taskId" value="${tasca.id}"/>
										<input type="hidden" id="token" name="token" value="${document.tokenSignatura}"/>
										<div class="inlineLabels col first">
											<div class="ctrlHolder">
												<label id="lcerts${document.id}" for="certs${document.id}"><spring:message code="tasca.signa.camp.cert"/></label>
												<select style="display: none" id="certs${document.id}" name="certs"></select>
											</div>
											<div class="ctrlHolder">
												<label for="passwd${document.id}"><spring:message code="tasca.signa.camp.passwd"/></label>
												<input type="password" id="passwd${document.id}" name="passwd" class="form-control"/>
											</div>
										</div>
										<div id="modal-botons${document.id}" class="modal-botons">
											<button class="pull-right btn btn-primary right" onclick="signarCaib('${document.tokenSignatura}', this.form, '1');"><spring:message code="tasca.signa.signar"/></button>
										</div>
										<script>
										// <![CDATA[
											$(document).ready( function() {								
												obtenirCertificats("certs${document.id}", "1");
											});
										//]]>
										</script>
									</form:form>
								</c:if>
							</div>
							<script>
							// <![CDATA[
								$(document).ready( function() {
									$("#iconos${document.id}").load('<c:url value="/nodeco/v3/expedient/${tasca.expedientId}/tasca/${tasca.id}/icones/${document.id}"/>');
								});
							//]]>
							</script>
						</c:when>
						<c:otherwise>
							<div class="no-disponible">Document no disponible</div>
						</c:otherwise>
					</c:choose>
				</h4>
			</div>
		</div>
	</div>
</c:forEach>
