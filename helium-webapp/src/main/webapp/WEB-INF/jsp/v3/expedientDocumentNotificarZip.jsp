<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="expedient.document.notificar.zip.generar.document"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>	
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
<script type="text/javascript">
</script>
</head>

<body>
		<c:set var="formAction">
			<c:url value="/v3/expedient/${expedientId}/document/${document.id}/notificarZip"/>
		</c:set>
		<form:form 	cssClass="form-horizontal form-tasca" action="${formAction}" enctype="multipart/form-data" method="post" commandName="documentExpedientNotificarZipCommand">
			<div>
				<hel:inputText required="true" name="titol" textKey="expedient.document.notificat.zip.titol"/>
				<hel:inputSelect required="true" name="annexos" multiple="true" textKey="expedient.document.notificar.zip.documents" placeholderKey="expedient.document.notificar.zip.documents.placeholder" optionItems="${annexos}" optionValueAttribute="id" optionTextAttribute="documentNom" labelSize="4"/>	
			</div>
			
			<!-- Metadades NTI -->
			<fieldset>
				<legend><spring:message code="expedient.tipus.document.form.legend.metadades.nti"></spring:message></legend>
				<hel:inputSelect name="ntiOrigen" textKey="expedient.tipus.document.form.camp.nti.origen" optionItems="${ntiOrigen}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="false" comment="expedient.tipus.document.form.camp.nti.origen.comentari"/>
				<hel:inputSelect name="ntiEstadoElaboracion" textKey="expedient.tipus.document.form.camp.nti.estado.elaboracion" optionItems="${ntiEstadoElaboracion}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="false" comment="expedient.tipus.document.form.camp.nti.estado.elaboracion.comentari"/>
				<hel:inputSelect name="ntiTipoDocumental" textKey="expedient.tipus.document.form.camp.nti.tipo.documental" optionItems="${ntiTipoDocumental}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="false" comment="expedient.tipus.document.form.camp.nti.tipo.documental.comentari"/>
			</fieldset>
			<div id="modal-botons" class="well">
					<button type="submit" class="btn btn-success" name="accio" value="zip_generar"><span class="fa fa-pencil-square-o"></span> <spring:message code="expedient.document.notificar.zip.afegir"/></button>
					<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			</div>
		</form:form>

	
</body>
</html>