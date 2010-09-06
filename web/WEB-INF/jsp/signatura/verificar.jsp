<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Verificació de signatures</title>
	<link href="<c:url value="/css/reset.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/common.css"/>" rel="stylesheet" type="text/css"/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.js"/>"></script>
</head>
<body>

	<c:set var="validesAll" value="${true}"/>
	<c:set var="classeDiv" value="missatgesDocumentGris"/>
	<c:forEach var="signatura" items="${signatures}">
		<c:if test="${not signatura.valida}"><c:set var="validesAll" value="${false}"/><c:set var="classeDiv" value="missatgesDocumentVermell"/></c:if>
	</c:forEach>
	<c:if test="${not validesAll}">
		<div id="errors" class="missatgesError">
			<p>Aquest document té signatures no vàlides</p>
		</div>
		<c:remove var="missatgesError" scope="session"/>
	</c:if>

	<h3 class="titol-tab titol-firmes-tasca">
		${document.documentNom}
		<c:set var="instanciaProcesActual" value="${instanciaProces}" scope="request"/>
		<c:set var="documentActual" value="${document}" scope="request"/>
		<c:set var="ocultarSignatura" value="${true}" scope="request"/>
		<c:import url="../common/iconesConsultaDocument.jsp"/>
	</h3><br/>

	<c:forEach var="signatura" items="${signatures}" varStatus="status">
		<div class="<c:choose><c:when test="${signatura.valida}">missatgesDocumentGris</c:when><c:otherwise>missatgesDocumentVermell</c:otherwise></c:choose>">
			<h4 class="titol-missatge">
				<c:choose>
					<c:when test="${signatura.valida}"><img src="<c:url value="/img/tick.png"/>" alt="Signatura vàlida" title="Signatura vàlida" border="0"/></c:when>
					<c:otherwise><img src="<c:url value="/img/exclamation.png"/>" alt="Signatura NO vàlida" title="Signatura NO vàlida" border="0"/></c:otherwise>
				</c:choose>
				Signatari ${status.index + 1}: ${signatura.infoCertificat.fullName}
			</h4>
			<dl class="form-info">
				<dt>NIF:</dt><dd>${signatura.infoCertificat.nif}</dd>
				<dt>Email:</dt><dd>${signatura.infoCertificat.email}</dd>
				<c:set var="signaturaBytes" value="${signatura.signatura}"/>
				<dt>Signatura:</dt><dd>
					<form><textarea rows="10" cols="64"><%=new String((byte[])pageContext.getAttribute("signaturaBytes"))%></textarea></form>
				</dd>
			</dl><br/>
		</div>
	</c:forEach>

</body>
</html>
