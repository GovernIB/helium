<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:choose>
	<c:when test="${empty expedientTipusDocumentCommand.id}"><
		<c:set var="titol"><spring:message code="expedient.tipus.document.form.titol.nou"/></c:set>
		<c:set var="formAction">new</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.tipus.document.form.titol.modificar"/></c:set>
		<c:set var="formAction">update</c:set>
	</c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
</head>
<body>		
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="expedientTipusDocumentCommand">
		<div>			
			<input type="hidden" name="id" value="${expedientTipusDocumentCommand.id}"/>
			<input type="hidden" name="eliminarContingut" id="eliminarContingut" value="false"/>
			<hel:inputText required="true" name="codi" textKey="expedient.tipus.document.form.camp.codi" />
			<hel:inputText required="true" name="nom" textKey="expedient.tipus.document.form.camp.nom" />
			<hel:inputTextarea name="descripcio" textKey="expedient.tipus.document.form.camp.descripcio" />
			<!-- Resol la url de descàrrega per tipus d'expedient o definicions de procés -->
			<c:choose>
				<c:when test="${not empty expedientTipusDocumentCommand.definicioProcesId}">
					<c:set var="arxiuUrl">/v3/definicioProces/${jbmpKey}/${expedientTipusDocumentCommand.definicioProcesId}/document/${expedientTipusDocumentCommand.id}/download</c:set>
				</c:when>
				<c:otherwise>
					<c:set var="arxiuUrl">/v3/expedientTipus/${expedientTipusDocumentCommand.expedientTipusId}/document/${expedientTipusDocumentCommand.id}/download</c:set>
				</c:otherwise>
			</c:choose>
 			<hel:inputFile 
	 			name="arxiuContingut" 
	 			required="false" 
	 			textKey="expedient.tipus.document.form.camp.arxiu"
	 			fileName="arxiuNom"
	 			fileUrl="${arxiuUrl}"
	 			fileExists="${not empty expedientTipusDocumentCommand.arxiuContingut}" />		
			<hel:inputCheckbox name="plantilla" textKey="expedient.tipus.document.form.camp.plantilla" />
			<hel:inputCheckbox name="notificable" textKey="expedient.tipus.document.form.camp.notificable" />
			<hel:inputText name="convertirExtensio" textKey="expedient.tipus.document.form.camp.gen_ext" />
			<hel:inputCheckbox name="adjuntarAuto" textKey="expedient.tipus.document.form.camp.adj_auto" />
			<hel:inputSelect name="campId" textKey="expedient.tipus.document.form.camp.camp_data" required="false" emptyOption="true" placeholderKey="expedient.tipus.document.form.camp.camp_data.buit" optionItems="${camps}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			<hel:inputText name="extensionsPermeses" textKey="expedient.tipus.document.form.camp.ext_perm" comment="expedient.tipus.document.form.camp.ext_perm.comment"/>
			<hel:inputText name="contentType" textKey="expedient.tipus.document.form.camp.ctype" comment="expedient.tipus.document.form.camp.ctype.comment" />
			<hel:inputText name="custodiaCodi" textKey="expedient.tipus.document.form.camp.codi_custodia" comment="expedient.tipus.document.form.camp.codi_custodia.comment" />
			<hel:inputText name="tipusDocPortasignatures" textKey="expedient.tipus.document.form.camp.tipus_doc" comment="expedient.tipus.document.form.camp.tipus_doc.comment" />
			<hel:inputCheckbox name="ignored" textKey="expedient.tipus.document.form.camp.ignored" comment="expedient.tipus.document.form.camp.ignored.comment"/> 			
			<fieldset>
				<legend><spring:message code="expedient.tipus.document.form.legend.metadades.nti"></spring:message></legend>
				<hel:inputSelect name="ntiOrigen" textKey="expedient.tipus.document.form.camp.nti.origen" optionItems="${ntiOrigen}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true" comment="expedient.tipus.document.form.camp.nti.origen.comentari"/>
				<hel:inputSelect name="ntiEstadoElaboracion" textKey="expedient.tipus.document.form.camp.nti.estado.elaboracion" optionItems="${ntiEstadoElaboracion}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true" comment="expedient.tipus.document.form.camp.nti.estado.elaboracion.comentari"/>
				<hel:inputSelect name="ntiTipoDocumental" textKey="expedient.tipus.document.form.camp.nti.tipo.documental" optionItems="${ntiTipoDocumental}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true" comment="expedient.tipus.document.form.camp.nti.tipo.documental.comentari"/>
			</fieldset>
		</div>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<c:choose>
				<c:when test="${empty expedientTipusDocumentCommand.id}">
					<button class="btn btn-primary right" type="submit" name="accio" value="crear">
						<span class="fa fa-plus"></span> <spring:message code='comu.boto.crear' />
					</button>
				</c:when>
				<c:otherwise>
					<button class="btn btn-primary right" type="submit" name="accio" value="modificar">
						<span class="fa fa-pencil"></span> <spring:message code='comu.boto.modificar' />
					</button>
				</c:otherwise>
			</c:choose>
		</div>
	</form:form>
</body>
</html>