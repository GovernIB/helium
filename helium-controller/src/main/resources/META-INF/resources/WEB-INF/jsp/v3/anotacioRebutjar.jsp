<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code="anotacio.form.rebutjar.titol" arguments="${anotacio.identificador}"/></title>
	<hel:modalHead />
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
</head>
<body>
    <c:set var="formAction"><rip:modalUrl value="/expedientPeticio/rebutjar"/></c:set>
	<form:form cssClass="form-horizontal" enctype="multipart/form-data" method="post" commandName="anotacioRebutjarCommand">
		<hel:inputHidden name="anotacioId"/>
		<hel:inputTextarea required="true" name="observacions" textKey="anotacio.form.rebutjar.camp.observacions" placeholderKey="anotacio.form.rebutjar.camp.observacions"/>
		<div id="modal-botons" class="well">
			<button type="submit" class="btn btn-primary"><span class="fa fa-times"></span>&nbsp;<spring:message code="anotacio.form.rebutjar.accio.rebutjar"/></button>
			<button type="button" class="btn btn-default modal-tancar" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
		</div>
	</form:form>
</body>
</html>
