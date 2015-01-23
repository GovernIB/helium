<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code="expedient.accio.modificar.titol"/></title>	
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/helium.tramitar.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
	<style type="text/css">
		.form-group {width: 100%;}
		.fila_reducida {width: 100%;}		
		.col-xs-4 {width: 20%;}		
		.col-xs-8 {width: 77%;}
		.col-xs-8 .form-group {margin-left: 0px;margin-right: 0px;}
		.col-xs-8 .form-group .col-xs-4 {padding-left: 0px;width: 15%;}
		.col-xs-8 .form-group .col-xs-8 {width: 85%;padding-left: 15px;padding-right: 0px;}
		#s2id_estatId {width: 100% !important;}
	</style>
</head>
<body>
	<form:form cssClass="form-horizontal form-tasca" id="modificarInformacio" name="modificarInformacio" action="modificar" method="post" commandName="expedientEditarCommand">
		<c:if test="${expedient.tipus.teNumero}">
			<div class="control-group fila_reducida">
				<hel:inputText required="true" name="numero" textKey="expedient.consulta.numero" placeholderKey="expedient.consulta.numero"/>
			</div>
		</c:if>
		<c:if test="${expedient.tipus.teTitol}">
			<div class="control-group fila_reducida">
				<hel:inputText required="true" name="titol" textKey="expedient.consulta.titol" placeholderKey="expedient.consulta.titol"/>
			</div>
		</c:if>
		<div class="control-group fila_reducida">
			<hel:inputDate name="dataInici" textKey="expedient.consulta.datainici" placeholderKey="expedient.consulta.datainici" placeholder="dd/MM/yyyy"/>
		</div>
		<div class="control-group fila_reducida">
			<hel:inputSuggest name="responsableCodi" urlConsultaInicial="persona/suggestInici" urlConsultaLlistat="persona/suggest" textKey="expedient.editar.responsable" placeholderKey="expedient.editar.responsable"/>
		</div>
		<div class="control-group fila_reducida">
			<hel:inputTextarea name="comentari" textKey="expedient.editar.comentari" placeholderKey="expedient.editar.comentari"/>
		</div>
		<div class="control-group fila_reducida">
			<hel:inputSelect name="estatId" textKey="expedient.consulta.select.estat" placeholderKey="expedient.consulta.select.estat" optionItems="${estats}" optionValueAttribute="id" optionTextAttribute="nom"/>
		</div>
		<div class="control-group fila_reducida">
			<c:choose>
				<c:when test="${globalProperties['app.georef.actiu']}">
					<c:choose>
						<c:when test="${globalProperties['app.georef.tipus']=='ref'}">
							<hel:inputText name="geoReferencia" textKey="comuns.georeferencia.codi" placeholderKey="comuns.georeferencia.codi"/>
						</c:when>
						<c:otherwise>
							<div class="form-group">
								<label class="control-label col-xs-4"><spring:message code='comuns.georeferencia.codi' /></label>
								<div class="controls col-xs-8">
									<hel:inputText name="geoPosX" textKey="comuns.georeferencia.coordenadaX" placeholderKey="comuns.georeferencia.coordenadaX"/>
								</div>
								<div class="controls col-xs-8" style="margin-left: 20%;">
									<hel:inputText name="geoPosY" textKey="comuns.georeferencia.coordenadaY" placeholderKey="comuns.georeferencia.coordenadaY"/>
								</div>
							</div>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<div class="span4"></div>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="control-group fila_reducida">
			<hel:inputText name="grupCodi" textKey="expedient.editar.grup_codi" placeholderKey="expedient.editar.grup_codi"/>
		</div>
		<div id="modal-botons">
			<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel">
				<spring:message code="comu.boto.cancelar"/>
			</button>
			<button type="submit" class="btn btn-primary" id="submit" name="submit" value="submit">
				<span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.accio.modificar.boto.modificar"/>
			</button>
		</div>
	</form:form>
 		
</body>
</html>
