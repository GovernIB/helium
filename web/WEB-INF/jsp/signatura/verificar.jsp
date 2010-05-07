<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Verificació de signatures</title>
</head>
<body>

	<h3 class="titol-tab titol-documents-tasca">
		${doccamp.nom}
		<c:set var="instanciaProcesActual" value="${instanciaProces}" scope="request"/>
		<c:set var="documentActual" value="${document}" scope="request"/>
		<c:set var="codiDocumentActual" value="${doccamp.codi}" scope="request"/>
		<c:import url="../common/iconesConsultaDocument.jsp"/>
	</h3><br/>

	<c:forEach var="signatura" items="${signatures}">
		<div class="missatgesDocumentGris">
			<h4 class="titol-missatge">
				<img src="<c:url value="/img/tick.png"/>" alt="Verificat" title="Verificat" border="0"/>
				${signatura.certPersona}
			</h4>
			<dl class="form-info">
				<dt>DNI/NIF:</dt><dd>${signatura.certNif}</dd>
				<dt>Email:</dt><dd>${signatura.certEmail}</dd>
			</dl><br/>
		</div>
	</c:forEach>

</body>
</html>
