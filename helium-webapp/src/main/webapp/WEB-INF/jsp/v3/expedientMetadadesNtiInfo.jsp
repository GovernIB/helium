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

	<form:form cssClass="form-horizontal form-tasca" enctype="multipart/form-data" method="post" commandName="expedient">
		
		<hel:inputText name="ntiVersio" textKey="expedient.metadades.nti.versio" disabled="true"/>
		<hel:inputText name="ntiIdentificador" textKey="expedient.metadades.nti.identificador" disabled="true"/>	
		<hel:inputText name="ntiOrgan" textKey="expedient.metadades.nti.organisme" disabled="true"/>
		<hel:inputText name="dataInici" textKey="expedient.metadades.nti.data.apertura" disabled="true"/>
		<hel:inputText name="ntiClasificacio" textKey="expedient.metadades.nti.classificacio" disabled="true"/>
		<hel:inputText name="estat.nom" textKey="expedient.metadades.nti.estat" disabled="true"/>
		<hel:inputText name="interessat" textKey="expedient.metadades.nti.interesat" disabled="true"/>
		<hel:inputText name="ntiTipoFirma" textKey="expedient.metadades.nti.tipus.firma" disabled="true"/>
		<hel:inputText name="ntiValorCsv" textKey="expedient.metadades.nti.valor.csv" disabled="true"/>
		<hel:inputText name="ntiDefGenCsv" textKey="expedient.metadades.nti.definicio.generacio.csv" disabled="true"/>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.tancar"/></button>
		</div>
	
	</form:form>
	
</body>
</html>
