<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
	<title>Expedient</title>
	<script type="text/javascript" src="<c:url value="/js/jquery.keyfilter.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
</head>
<body>

<form:form id="aturarExpedient" name="aturarExpedient" action="aturarExpedient" method="post" commandName="aturarCommand">
	<div class="modal-body">
		<div class="span1">
			Motiu
		</div>
		<textarea id="motiu" name="motiu" autofocus="autofocus" class="span9"></textarea>
	</div>
</form:form>
<script>
	window.parent.canviTitolModal("Aturar la tramitació de l'expedient");
	var transicions = new Array();
	var texts = new Array();
	transicions.push('aturarExpedient');texts.push('Aturar');
	window.parent.substituirBotonsPeuModal(transicions, texts);
	function test(codi) {
		if (confirm('Estau segur que voleu donar aquesta tasca per finalitzada?')) {
			$("#aturarExpedient").submit();
			$("#tramitacio-modal").hide();
		}
	}
</script>
</body>
