<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title>${tasca.nom}</title>
	<meta name="titolcmp" content="Tasques">
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<c:import url="../common/formIncludes.jsp"/>
<script type="text/javascript">
// <![CDATA[
function confirmarEsborrarSignatura(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu esborrar la signatura d'aquest document?");
}
// ]]>
</script>
</head>
<body>

	<c:import url="../common/tabsTasca.jsp">
		<c:param name="tabActiu" value="firmes"/>
	</c:import>

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
					<c:set var="tokenActual" value="${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignatura}" scope="request"/>
					<c:import url="../common/iconesConsultaDocument.jsp"/>
					<c:if test="${tasca.varsDocumentsPerSignar[firma.document.codi].signatEnTasca}">
						<img src="<c:url value="/img/tick.png"/>" alt="Ok" title="Ok" border="0"/>
					</c:if>
					&nbsp;&nbsp;
					<c:if test="${not tasca.varsDocumentsPerSignar[firma.document.codi].signatEnTasca}">
						<div>
							<object classid="clsid:CAFEEFAC-0015-0000-FFFF-ABCDEFFEDCBA" width="294" height="110" align="baseline" codebase="http://java.sun.com/update/1.5.0/jinstall-1_5_0_12-windows-i586.cab" >
								<param name="code" value="${globalProperties['app.signatura.applet.code']}">
								<param name="archive" value="${globalProperties['app.signatura.applet.archive']}">
								<param name="baseUrl" value="${globalProperties['app.base.url']}"/>
								<param name="token" value="${tasca.varsDocumentsPerSignar[firma.document.codi].tokenSignatura}"/>
								<param name="signaturaParams" value="${tasca.varsDocumentsPerSignar[firma.document.codi].contentType}"/>
								<PARAM NAME="MAYSCRIPT" VALUE="true">
								<comment>
									<embed width="294" height="110" align="baseline" 
										code="${globalProperties['app.signatura.applet.code']}"
										archive="${globalProperties['app.signatura.applet.archive']}"
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
					</c:if>
				</c:if>
			</h4>
		</div>
	</c:forEach>

	<p class="aclaracio">Els documents marcats amb <img src="<c:url value="/img/bullet_red.png"/>" alt="Document obligatori" title="Document obligatori" border="0"/> són de signatura obligatòria</p>

</body>
</html>
