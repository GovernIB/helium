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
<c:if test="${globalProperties['app.signatura.tipus'] == 'afirma'}">
<script type="text/javascript">
//<![CDATA[
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
//]]>
</script>
</c:if>
<c:if test="${globalProperties['app.signatura.tipus'] == 'caib'}">
<script src="https://www.java.com/js/deployJava.js"></script>
<script>
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
</script>
<script type="text/javascript">
//<![CDATA[
var certsCarregats = false;
function obtenirCertificats(selectId, contentType) {
	try {
		signaturaApplet.findCertificats(contentType)
		var certs = signaturaApplet.findCertificats(contentType);
		if (!certs)
			alert("<fmt:message key="tasca.signa.alert.certerr"/>");
		var options = '';
		for (var i = 0; i < certs.length; i++)
			options += '<option value="' + certs[i] + '">' + certs[i] + '</option>';
		$("select#" + selectId).html(options);
		certsCarregats = true;
	} catch (e) {
		setTimeout("obtenirCertificats('" + selectId + "', '" + contentType + "')", 1000);
	}
}
function signarCaib(token, form, contentType) {
	if (!certsCarregats) {
		obtenirCertificats(form.cert.id, contentType);
	} else {
		var cert = form.cert.value;
		if (cert == null || cert.length == 0) {
			alert("<fmt:message key="tasca.signa.alert.nosel"/>");
		} else {
			var signaturaB64 = signaturaApplet.signaturaPdf(
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
// ]]>
</script>
</c:if>
</head>
<body>

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
				<label>${firma.document.nom}</label>&nbsp;&nbsp;
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
							<script>obtenirCertificats("certs${documentActual.id}", "1");</script>
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
