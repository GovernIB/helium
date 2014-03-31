<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
	<head>
		<title>Execució d'scripts</title>
	</head>
	<body>		
		<script type="text/javascript">
			function confirmar(e) {
				var e = e || window.event;
				e.cancelBubble = true;
				if ($("#script").val() === "") {
					$('.contingut-alertes').append(
							"<div class='alert alert-error'>"+
								"<button class='close' data-dismiss='alert'>×</button>" +								
								"<spring:message code='comuns.camp_oblig'/>"+
								": <spring:message code='error.executar.script'/>"+
							"</div>"
					);
					window.parent.modalAjustarTamany(window.frameElement,$('html').height());
					return false;
				}
				if (e.stopPropagation) e.stopPropagation();
				return confirm('<spring:message code="expedient.eines.confirm_executar_script_proces"/>');
			}
		</script>
		
		<form:form id="scriptCommand" name="scriptCommand" action="scriptCommand" method="post" commandName="scriptCommand" onsubmit="return confirmar(event)">
			<div class="inlineLabels">
				<input type="hidden" name="id" value="${param.id}"/>
				<textarea id=script name="script" autofocus="autofocus" class="span99per"></textarea>
			</div>
			<div id="formButtons" class="well">
				<button type="submit" class="btn btn-primary"><spring:message code='comuns.executar' /></button>
			</div>
		</form:form>
	</body>
</html>