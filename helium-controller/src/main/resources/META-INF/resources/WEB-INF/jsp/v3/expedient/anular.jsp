<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="expedient.accio.anular.titol"/></title>
	<hel:modalHead/>
	<style type="text/css">
		.col-xs-4 {width: 8.33333%;}
		.col-xs-8 {width: 91.6667%;}
		.obligatori{background-position: right 6px;}
	</style>
<script>
	function confirmar(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm('<spring:message code="expedient.accio.anular.confirmacio"/>');
	}
</script>
</head>
<body>		
	<form:form action="anular" method="post" commandName="expedientEinesCancelCommand" onsubmit="return confirmar(event)">
		<hel:inputTextarea required="true" name="motiu" textKey="expedient.accio.anular.camp.motiu" placeholderKey="expedient.accio.anular.camp.motiu"/>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
			<button type="submit" class="btn btn-primary"><span class="fa fa-times"></span>&nbsp;<spring:message code="expedient.accio.anular.boto.anular"/></button>
		</div>
	</form:form>
</body>
</html>
