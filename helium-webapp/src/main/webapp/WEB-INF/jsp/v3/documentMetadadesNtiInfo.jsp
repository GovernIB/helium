<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="expedient.tipus.metadades.nti.titol"/></title>
	<hel:modalHead/>
</head>
<body>

	<form:form cssClass="form-horizontal" enctype="multipart/form-data" method="post" commandName="documentStore">
	
		<hel:inputText name="ntiVersio" textKey="document.metadades.nti.versio" disabled="true"/>
		<hel:inputText name="ntiIdentificador" textKey="document.metadades.nti.identificador" disabled="true"/>
		<hel:inputText name="ntiOrgan" textKey="document.metadades.nti.organisme" disabled="true"/>
		<hel:inputText name="ntiOrigen" textKey="document.metadades.nti.origen" disabled="true"/>
		<hel:inputText name="ntiEstatElaboracio" textKey="document.metadades.nti.estat.elaboracio" disabled="true"/>
		<hel:inputText name="ntiNomFormat" textKey="document.metadades.nti.nom.format" disabled="true"/>
		<hel:inputText name="ntiTipusDocumental" textKey="document.metadades.nti.tipus.documental" disabled="true"/>
		<hel:inputText name="dataCreacio" textKey="document.metadades.nti.data.captura" disabled="true"/>
		
		<hel:inputText name="ntiTipoFirma" textKey="document.metadades.nti.tipus.firma" disabled="true"/>
		<hel:inputText name="ntiValorCsv" textKey="document.metadades.nti.valor.csv" disabled="true"/>
		<hel:inputText name="ntiDefGenCsv" textKey="document.metadades.nti.definicio.generacio.csv" disabled="true"/>
		
		<hel:inputText name="ntiIdDocOrigen" textKey="document.metadades.nti.identificador.doc.origen" disabled="true"/>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.tancar"/></button>
		</div>
	
	</form:form>
	
</body>
</html>
