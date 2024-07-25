<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="serveisPinbal.form.modificar"/></title>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<hel:modalHead/>
</head>
<body>
	<c:set var="formAction">update</c:set>
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="serveiPinbalDto">
		<form:hidden id="id" path="id"/>
		<div class="row">
			<div class="col-sm-11">
				<hel:inputText name="codi" textKey="serveisPinbal.col.codi" disabled="true"/>
				<hel:inputTextarea name="nom" textKey="serveisPinbal.col.nom"/>
				<hel:inputCheckbox name="pinbalServeiDocPermesDni" textKey="serveisPinbal.col.pinbalServeiDocPermesDni"/>
				<hel:inputCheckbox name="pinbalServeiDocPermesNif" textKey="serveisPinbal.col.pinbalServeiDocPermesNif"/>
				<hel:inputCheckbox name="pinbalServeiDocPermesCif" textKey="serveisPinbal.col.pinbalServeiDocPermesCif"/>
				<hel:inputCheckbox name="pinbalServeiDocPermesNie" textKey="serveisPinbal.col.pinbalServeiDocPermesNie"/>
				<hel:inputCheckbox name="pinbalServeiDocPermesPas" textKey="serveisPinbal.col.pinbalServeiDocPermesPas"/>
			</div>
		</div>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true">
				<spring:message code="comu.boto.cancelar"/>
			</button>
			<button type="submit" class="btn btn-success right">
				<span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/>
			</button>
		</div>
	</form:form>
</body>
</html>
