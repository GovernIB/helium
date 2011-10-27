<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>${tasca.nomLimitat}</title>
	<meta name="titolcmp" content="Tasques"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<c:if test="${globalProperties['app.signatura.tipus'] == 'afirma'}">
	<script type="text/javascript" language="javascript" src="<c:url value="/signatura/aFirma/common-js/deployJava.js"/>"></script>
	<script type="text/javascript" language="javascript" src="<c:url value="/signatura/aFirma/common-js/firma.js"/>"></script>
	<script type="text/javascript" language="javascript" src="<c:url value="/signatura/aFirma/common-js/instalador.js"/>"></script>
	<script type="text/javascript" language="javascript" src="<c:url value="/signatura/aFirma/common-js/time.js"/>"></script>
</c:if>
<script type="text/javascript" src="<c:url value="/js/jquery/ui/ui.core.js"/>"></script>
<script  type="text/javascript" src="<c:url value="/js/jquery/ui/jquery-ui-1.7.2.custom.js"/>"></script>
<script type="text/javascript">
// <![CDATA[
function confirmarEsborrarSignatura(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar la signatura d'aquest document?");
}
function verificarSignatura(element) {
	var amplada = 800;
	var alcada = 600;
	$('<iframe id="verificacio" src="' + element.href + '"/>').dialog({
		title: "Verificació de signatures",
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
		title: "Informació de registre",
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
	clienteFirma.setFileUri("${globalProperties['app.base.url']}/document/arxiuPerSignar.html?token=" + token);
	firmar();
	if (!clienteFirma.isError()) {
		form.data.value = clienteFirma.getSignatureBase64Encoded();
		form.submit();
	} else {
		alert("No s'ha pogut signar el document: " + clienteFirma.getErrorMessage());
	}
}
</c:if>
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsTasca.jsp">
		<c:param name="tabActiu" value="firmes"/>
	</c:import>

	<c:if test="${globalProperties['app.signatura.tipus'] == 'afirma'}"><script type="text/javascript">cargarAppletFirma(base + '/${globalProperties['app.signatura.afirma.default.build']}');</script></c:if>

	<c:if test="${not tasca.documentsComplet}">
		<div class="missatgesWarn">
			<p>No es podran signar documents si falten documents obligatoris per adjuntar</p>
		</div>
	</c:if>
	<c:if test="${not tasca.signaturesComplet}">
		<div class="missatgesWarn">
			<p>Hi ha documents amb signatura obligatòria que encara no han estat signats</p>
		</div>
	</c:if>

	<c:import url="../common/tascaReadOnly.jsp"/>

	<h3 class="titol-tab titol-firmes-tasca">
		Documents per signar
	</h3>

	<c:forEach var="firma" items="${tasca.signatures}">
		<div class="missatgesDocumentGris">
			<h4 class="titol-missatge">
				<c:if test="${firma.required}"><img src="<c:url value="/img/bullet_red.png"/>" alt="Firma obligatòria" title="Firma obligatòria" border="0"/></c:if>
				${firma.document.nom}&nbsp;&nbsp;
				<c:if test="${tasca.documentsComplet}">
					<c:set var="tascaActual" value="${tasca}" scope="request"/>
					<c:set var="documentActual" value="${tasca.varsDocumentsPerSignar[firma.document.codi]}" scope="request"/>
					<c:set var="codiDocumentActual" value="${firma.document.codi}" scope="request"/>
					<c:if test="${not tasca.varsDocumentsPerSignar[firma.document.codi].signatEnTasca}">
						<c:set var="tokenActual" value="${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignatura}" scope="request"/>
					</c:if>
					<c:import url="../common/iconesConsultaDocument.jsp"/>
					<c:choose>
						<c:when test="${tasca.varsDocumentsPerSignar[firma.document.codi].signatEnTasca}">
							&nbsp;&nbsp;
						</c:when>
						<c:otherwise>
							&nbsp;&nbsp;
							<c:choose>
								<c:when test="${globalProperties['app.signatura.tipus'] == 'caib'}">
									<div>
										<object classid="clsid:CAFEEFAC-0015-0000-FFFF-ABCDEFFEDCBA" width="294" height="110" align="baseline" codebase="http://java.sun.com/update/1.5.0/jinstall-1_5_0_12-windows-i586.cab" >
											<param name="code" value="net.conselldemallorca.helium.integracio.plugins.signatura.applet.SignaturaAppletCaib">
											<param name="archive" value="../signatura/caib/signatura-applet-caib.jar,../signatura/caib/signaturacaib.core-3.1.0-api-unsigned.jar,../signatura/caib/swing-layout-1.0.3.jar">
											<param name="baseUrl" value="${globalProperties['app.base.url']}"/>
											<param name="token" value="${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignatura}"/>
											<param name="signaturaParams" value="${tasca.varsDocumentsPerSignar[firma.document.codi].contentType}"/>
											<PARAM NAME="MAYSCRIPT" VALUE="true">
											<comment>
												<embed width="294" height="110" align="baseline" 
													code="net.conselldemallorca.helium.integracio.plugins.signatura.applet.SignaturaAppletCaib"
													archive="../signatura/caib/signatura-applet-caib.jar,../signatura/caib/signaturacaib.core-3.1.0-api-unsigned.jar,../signatura/caib/swing-layout-1.0.3.jar"
													baseUrl="${globalProperties['app.base.url']}"
													token="${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignatura}"
													signaturaParams="${tasca.varsDocumentsPerSignar[firma.document.codi].contentType}"
													MAYSCRIPT="true"
													type="application/x-java-applet;version=1.5"
													pluginspage="http://java.sun.com/j2se/1.5.0/download.html"
													cache_option="No" />
													<noembed>
														No te suport per applets Java 2 SDK, Standard Edition v 1.5 ! !
													</noembed>
												</embed>
											</comment>
										</object>
									</div>
								</c:when>
								<c:when test="${globalProperties['app.signatura.tipus'] == 'afirma'}">
									<form:form action="../signatura/signarAmbTokenAFirma.html" cssClass="uniForm" cssStyle="display:inline" onsubmit="return false">
										<input type="hidden" name="taskId" value="${tasca.id}"/>
										<input type="hidden" name="token" value="${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignaturaMultiple}"/>
										<input type="hidden" name="data"/>
										<button class="submitButton" onclick="signarAFirma(this.form, '${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignatura}');return false">Signar</button>
									</form:form>
								</c:when>
								<c:otherwise>
									[Tipus de signatura no suportat]
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
				</c:if>
			</h4>
		</div>
	</c:forEach>

	<p class="aclaracio">Els documents marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Document obligatori" title="Document obligatori" border="0"/> són de signatura obligatòria</p>

	<br/><c:import url="../common/tramitacioTasca.jsp">
		<c:param name="pipella" value="signatures"/>
	</c:import>

</body>
</html>
