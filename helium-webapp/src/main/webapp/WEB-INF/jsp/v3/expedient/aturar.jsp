<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
	<head>
		<title><spring:message code='expedient.eines.aturar_tramitacio' /></title>
		<hel:modalHead/>
		<style>
			.col-xs-4 {width: auto;}
			.col-xs-8 {width: 90%;}
		</style>
	</head>
	<body>		
		<form:form id="aturarExpedient" name="aturarExpedient" action="aturarExpedient" method="post" commandName="expedientEinesAturarCommand" onsubmit="return confirmar(event)">
			<hel:inputTextarea required="true" name="motiu" textKey="expedient.eines.motiu" placeholderKey="expedient.eines.motiu"/>
		
			<div id="formButtons" class="well">
				<button type="button" class="modal-tancar btn" name="submit" value="cancel">
					<spring:message code='comuns.cancelar' />
				</button>
				<button type="submit" class="btn btn-primary"><spring:message code="comuns.guardar"/></button>
			</div>
		</form:form>
		<script>
			function confirmar(e) {
				var e = e || window.event;
				e.cancelBubble = true;
				if (e.stopPropagation) e.stopPropagation();
				return confirm('<spring:message code="expedient.eines.confirm_aturar"/>');
			}
		</script>
	</body>
</html>
