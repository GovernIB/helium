<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="expedient.token.retrocedir"/> ${token.fullName}</title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
	
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<style type="text/css">
		.btn-file {position: relative; overflow: hidden;}
		.btn-file input[type=file] {position: absolute; top: 0; right: 0; min-width: 100%; min-height: 100%; font-size: 100px; text-align: right; filter: alpha(opacity = 0); opacity: 0; outline: none; background: white; cursor: inherit; display: block;}
		.col-xs-4 {width: 20%;}		
		.col-xs-8 {width: 80%;}
		#s2id_estatId {width: 100% !important;}
	</style>
</head>
<body>		
	<form:form cssClass="form-horizontal form-tasca" action="tokenRetrocedir" method="post" commandName="tokenExpedientCommand">
		<div class="inlineLabels">
<%-- 			<input type="hidden" id="processInstanceId" name="processInstanceId" value="${processInstanceId}"/> --%>
			<hel:inputSelect required="true" name="nodeRetrocedir" optionItems="${arrivingNodeNames}" emptyOption="true" emptyOptionTextKey="expedient.token.node.buit" textKey="expedient.token.node.retrocedir" placeholderKey="expedient.token.node.retrocedir"/>
			<hel:inputCheckbox inline="false" name="cancelar" textKey="expedient.token.node.cancelar"/>
			
		</div>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
			<button class="btn btn-primary right" type="submit" name="accio" value="token_retrocedir">
				<spring:message code='expedient.token.accio.retrocedir' />
			</button>
		</div>
	</form:form>
</body>
</html>