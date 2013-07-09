<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="sourceUrl" value="${globalProperties['app.base.url']}/document/arxiuPerSignar.html"/>
<c:set var="targetUrl" value="${globalProperties['app.base.url']}/signatura/signarAmbTokenCaib.html"/>

<html>
<head>
	<title>${tasca.nomLimitat}</title>
	<meta name="titolcmp" content="<fmt:message key='comuns.tasques' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<c:if test="${globalProperties['app.signatura.tipus'] == 'afirma'}">
	<script type="text/javascript" src="<c:url value="/signatura/aFirma/common-js/deployJava.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/signatura/aFirma/common-js/firma.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/signatura/aFirma/common-js/instalador.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/signatura/aFirma/common-js/time.js"/>"></script>
</c:if>
<script type="text/javascript" src="<c:url value="/js/jquery/ui/ui.core.js"/>"></script>
<script  type="text/javascript" src="<c:url value="/js/jquery/ui/jquery-ui-1.7.2.custom.js"/>"></script>
<script type="text/javascript">
// <![CDATA[
function confirmarEsborrarSignatura(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='tasca.signa.confirmacio' />");
}
function verificarSignatura(element) {
	var amplada = 800;
	var alcada = 600;
	$('<iframe id="verificacio" src="' + element.href + '"/>').dialog({
		title: "<fmt:message key='tasca.signa.verificacio' />",
		autoOpen: true,
		modal: true,
		autoResize: true,
		width: parseInt(amplada),
		height: parseInt(alcada)
	}).width(amplada - 30).height(alcada - 30);
	return false;
}
function infoRegistre(docId) {
	var amplada = 600;
	var alcada = 200;
	$('<div>' + $("#registre_" + docId).html() + '</div>').dialog({
		title: "<fmt:message key='tasca.signa.informacio' />",
		autoOpen: true,
		modal: true,
		width: parseInt(amplada),
		height: parseInt(alcada)
	}).width(amplada - 30).height(alcada - 30);
	return false;
}
<c:if test="${globalProperties['app.signatura.tipus'] == 'afirma'}">
var baseDownloadURL = "../signatura/aFirma";
var base = "../signatura/aFirma";
var signatureAlgorithm = "${globalProperties['app.signatura.afirma.signature.algorithm']}";
var signatureFormat = "${globalProperties['app.signatura.afirma.signature.format']}";
var showErrors = 'false';
var certFilter = "${globalProperties['app.signatura.afirma.cert.filter']}";
var installDirectory = "${globalProperties['app.signatura.afirma.install.directory']}";
var oldVersionsAction = ${globalProperties['app.signatura.afirma.old.versions.action']};
var showMozillaSmartCardWarning = 'false';
var showExpiratedCertificates = "${globalProperties['app.signatura.afirma.show.expired.certificates']}";
var defaultBuild = "${globalProperties['app.signatura.afirma.default.build']}";
function signarAFirma(form, token) {
	initialize();
	configuraFirma();
	clienteFirma.setFileUri("${sourceUrl}?token=" + token);
	firmar();
	if (!clienteFirma.isError()) {
		form.data.value = clienteFirma.getSignatureBase64Encoded();
		form.submit();
	} else {
		alert("<fmt:message key='tasca.signa.no_sa_pogut' />: " + clienteFirma.getErrorMessage());
	}
}
</c:if>
<c:if test="${globalProperties['app.signatura.tipus'] == 'caib'}">
var appletCaib;
var certsCarregats = false;
function initApplet(
		id,
		code,
		archive,
		width,
		height,
		contentType) {
	if ($.browser.msie) {
		document.write(
				'<object id="' + id + '" width="' + width + '" height="' + height + '"' +
				'        classid="clsid:CAFEEFAC-0015-0000-FFFF-ABCDEFFEDCBA">' +
				'    <param name="code" value="' + code + '"/>' +
				'    <param name="archive" value="' + archive + '"/>' +
				'</object>');
	} else {
		document.write(
				'<embed id="' + id + '" width="' + width + '" height="' + height + '" ' +
				'        type="application/x-java-applet;version=1.5" ' +
				'        code="' + code + '" ' +
				'        archive="' + archive + '" ' +
				'        cache_option="no"/>');
	}
	return document.getElementById(id);
}
function initSignaturaCaib(selectId, contentType) {
	appletCaib = initApplet(
			"signaturaCaibApplet",
			"net.conselldemallorca.helium.integracio.plugins.signatura.applet.SignaturaCaibApplet",
			"../signatura/caib/signatura-applet-caib.jar,../signatura/caib/signaturacaib.core-3.1.0-api-unsigned.jar",
			1,
			1,
			contentType);
}
function obtenirCertificats(selectId, contentType) {
	var certs = appletCaib.findCertificats(contentType);
	if (!certs)
		alert("<fmt:message key="tasca.signa.alert.certerr"/>");
	var options = '';
	for (var i = 0; i < certs.length; i++)
		options += '<option value="' + certs[i] + '">' + certs[i] + '</option>';
	$("select#" + selectId).html(options);
	certsCarregats = true;
}
function signarCaib(token, form, contentType) {
	if (!certsCarregats) {
		obtenirCertificats(form.cert.id, contentType);
	} else {
		var cert = form.cert.value;
		if (cert == null || cert.length == 0) {
			alert("<fmt:message key="tasca.signa.alert.nosel"/>");
		} else {
			var signaturaB64 = appletCaib.signaturaPdf(
					"${sourceUrl}?token=" + token,
					cert,
					form.passwd.value,
					contentType);
			if (signaturaB64 == null) {
				alert("<fmt:message key="tasca.signa.alert.error"/>");
			} else {
				form.data.value = signaturaB64;
				form.submit();
			}
		}
	}
}
</c:if>
function mostrarOcultar(img, objid) {
	var obj = document.getElementById(objid);
	if (obj.style.display=="none") {
		$('#' + objid).slideDown();
		//obj.style.display = "block";
		img.src = '<c:url value="/img/magnifier_zoom_out.png"/>';
	} else {
		$('#' + objid).slideUp();
		//obj.style.display = "none";
		img.src = '<c:url value="/img/magnifier_zoom_in.png"/>';
	}
}
// ]]>
</script>
</head>
<body>

<c:if test="${globalProperties['app.signatura.tipus'] == 'caib'}">
<script>initSignaturaCaib('certs${documentActual.id}', '1')</script>
</c:if>

	<h3 class="titol-tab titol-expedient">${tasca.expedient.identificadorLimitat}</h3>

	<c:import url="../common/tabsTasca.jsp">
		<c:param name="tabActiu" value="firmes"/>
	</c:import>

	<c:if test="${globalProperties['app.signatura.tipus'] == 'afirma'}"><script type="text/javascript">cargarAppletFirma(base + '/${globalProperties['app.signatura.afirma.default.build']}');</script></c:if>

	<c:if test="${not empty seleccioMassiva}">
		<div class="missatgesWarn">
			<p><fmt:message key='tasca.signa.massiu.no_es_podran' /></p>
		</div>
	</c:if>
	<c:if test="${not tasca.documentsComplet}">
		<div class="missatgesWarn">
			<p><fmt:message key='tasca.signa.no_es_podran' /></p>
		</div>
	</c:if>
	<c:if test="${not tasca.signaturesComplet}">
		<div class="missatgesWarn">
			<p><fmt:message key='tasca.signa.hi_ha_docs' /></p>
		</div>
	</c:if>

	<c:import url="../common/tascaReadOnly.jsp"/>

	<c:forEach var="firma" items="${tasca.signatures}">
		<div class="missatgesDocumentGris">
			<h4 class="titol-missatge">
				<c:if test="${firma.required}"><img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='tasca.signa.signa_oblig' />" title="<fmt:message key='tasca.signa.signa_oblig' />" border="0"/></c:if>
				${firma.document.nom}&nbsp;&nbsp;
				<c:if test="${tasca.documentsComplet}">
					<c:set var="tascaActual" value="${tasca}" scope="request"/>
					<c:set var="documentActual" value="${tasca.varsDocumentsPerSignar[firma.document.codi]}" scope="request"/>
					<c:set var="codiDocumentActual" value="${firma.document.codi}" scope="request"/>
					<c:if test="${not tasca.varsDocumentsPerSignar[firma.document.codi].signatEnTasca}">
						<c:set var="tokenActual" value="${documentActual.tokenSignatura}" scope="request"/>
					</c:if>
					<c:import url="../common/iconesConsultaDocument.jsp"/>
				</c:if>
			</h4>
			<c:choose>
				<c:when test="${tasca.varsDocumentsPerSignar[firma.document.codi].signatEnTasca}">
					&nbsp;&nbsp;
				</c:when>
				<c:otherwise>
					&nbsp;&nbsp;
					<c:choose>
						<c:when test="${globalProperties['app.signatura.tipus'] == 'caib'}">
							<%-- div>
								<object classid="clsid:CAFEEFAC-0015-0000-FFFF-ABCDEFFEDCBA" width="294" height="110" align="baseline" codebase="http://java.sun.com/update/1.5.0/jinstall-1_5_0_12-windows-i586.cab" >
									<param name="code" value="net.conselldemallorca.helium.integracio.plugins.signatura.applet.SignaturaAppletCaib">
									<param name="archive" value="../signatura/caib/signatura-applet-caib.jar,../signatura/caib/signaturacaib.core-3.1.0-api-unsigned.jar,../signatura/caib/swing-layout-1.0.3.jar">
									<param name="token" value="${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignaturaMultiple}"/>
									<param name="sourceUrl" value="${sourceUrl}?token=${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignaturaUrlEncoded}"/>
									<param name="targetUrl" value="${targetUrl}?token=${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignaturaMultipleUrlEncoded}"/>
									<param name="signaturaParams" value="${tasca.varsDocumentsPerSignar[firma.document.codi].contentType}"/>
									<PARAM NAME="MAYSCRIPT" VALUE="true">
									<comment>
										<embed width="294" height="110" align="baseline" 
											code="net.conselldemallorca.helium.integracio.plugins.signatura.applet.SignaturaAppletCaib"
											archive="../signatura/caib/signatura-applet-caib.jar,../signatura/caib/signaturacaib.core-3.1.0-api-unsigned.jar,../signatura/caib/swing-layout-1.0.3.jar"
											token="${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignaturaMultiple}"
											sourceUrl="${sourceUrl}?token=${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignaturaUrlEncoded}"
											targetUrl="${targetUrl}?token=${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignaturaMultipleUrlEncoded}"
											signaturaParams="${tasca.varsDocumentsPerSignar[firma.document.codi].contentType}"
											MAYSCRIPT="true"
											type="application/x-java-applet;version=1.5"
											pluginspage="http://java.sun.com/j2se/1.5.0/download.html"
											cache_option="No" />
											<noembed>
												<fmt:message key='tasca.signa.no_te_suport' />
											</noembed>
										</embed>
									</comment>
								</object>
							</div--%>
							<form:form action="../signatura/signarAmbToken.html" cssClass="uniForm" onsubmit="return false">
								<input type="hidden" name="taskId" value="${tasca.id}"/>
								<input type="hidden" name="token" value="${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignaturaMultiple}"/>
								<input type="hidden" name="data"/>
								<div class="inlineLabels col first">
									<div class="ctrlHolder">
										<label for="certs${documentActual.id}"><fmt:message key="tasca.signa.camp.cert"/></label>
										<select id="certs${documentActual.id}" name="cert">
											<option><fmt:message key="tasca.signa.obtenint.certs"/></option>
										</select>
									</div>
									<div class="ctrlHolder">
										<label for="passwd${documentActual.id}"><fmt:message key="tasca.signa.camp.passwd"/></label>
										<input type="password" id="passwd${documentActual.id}" name="passwd" class="textInput"/>
									</div>
									<div class="buttonHolder">
										<button class="submitButton" <c:if test="${not empty seleccioMassiva}">disabled </c:if>onclick="signarCaib('${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignaturaUrlEncoded}', this.form, '1');return false"><fmt:message key="tasca.signa.signar"/></button>
									</div>
								</div>
							</form:form>
							<script>setTimeout("obtenirCertificats('certs${documentActual.id}', '1')", 1000);</script>
						</c:when>
						<c:when test="${globalProperties['app.signatura.tipus'] == 'afirma'}">
							<form:form action="../signatura/signarAmbToken.html" cssClass="uniForm" cssStyle="display:inline" onsubmit="return false">
								<input type="hidden" name="taskId" value="${tasca.id}"/>
								<input type="hidden" name="token" value="${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignaturaMultiple}"/>
								<input type="hidden" name="data"/>
								<button class="submitButton" <c:if test="${not empty seleccioMassiva}">disabled </c:if>onclick="signarAFirma(this.form, '${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignatura}');return false"><fmt:message key="tasca.signa.signar"/></button>
							</form:form>
						</c:when>
						<c:otherwise>
							[<fmt:message key='tasca.signa.tipus_no_sup' />]
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</div>
	</c:forEach>

	<p class="aclaracio"><fmt:message key='tasca.signa.docs_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='tasca.signa.signa_oblig' />" title="<fmt:message key='tasca.signa.signa_oblig' />" border="0"/> <fmt:message key='tasca.signa.son_sign_oblig' /></p>

	<br/><c:import url="../common/tramitacioTasca.jsp">
		<c:param name="pipella" value="signatures"/>
	</c:import>


</html>
