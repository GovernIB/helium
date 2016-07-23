<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${empty expedientTipusTerminiCommand.id}"><
		<c:set var="titol"><spring:message code="expedient.tipus.termini.form.titol.nou"/></c:set>
		<c:set var="formAction">new</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.tipus.termini.form.titol.modificar"/></c:set>
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
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="expedientTipusTerminiCommand">
		<div>
        
			<script type="text/javascript">
				// <![CDATA[
				// ]]>
			</script>
			
			<input type="hidden" name="id" value="${expedientTipusTerminiCommand.id}"/>
			<hel:inputText required="true" name="codi" textKey="expedient.tipus.termini.form.camp.codi" />
			<hel:inputText required="true" name="nom" textKey="expedient.tipus.termini.form.camp.nom" />
			<hel:inputText required="true" name="desripcio" textKey="expedient.tipus.termini.form.camp.descripcio" />
			<hel:inputCheckbox name="duradaPredefinida" textKey="expedient.tipus.termini.form.camp.duradaPredefinida" />
			<hel:inputTermini required="true" anys="anys" mesos="mesos" dies="dies"/>
			<hel:inputCheckbox name="laborable" textKey="expedient.tipus.termini.form.camp.laborable" />
			<hel:inputCheckbox name="manual" textKey="expedient.tipus.termini.form.camp.manual" />
			<hel:inputText required="true" name="diesPrevisAvis" textKey="expedient.tipus.termini.form.camp.diesPrevisAvis" />
<%-- 			<c:param name="comment"><fmt:message key='defproc.termform.es_generara' /></c:param> --%>
			<hel:inputCheckbox name="alertaPrevia" textKey="expedient.tipus.termini.form.camp.alertaPrevia" />
			<hel:inputCheckbox name="alertaFinal" textKey="expedient.tipus.termini.form.camp.alertaFinal" />
			<hel:inputCheckbox name="alertaCompletat" textKey="expedient.tipus.termini.form.camp.alertaCompletat" />
		</div>
		
		<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<c:choose>
				<c:when test="${empty expedientTipusTerminiCommand.id}">
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