<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
	<head>
		<title><spring:message code='expedient.eines.execucio_scripts' /></title>
		<hel:modalHead/>
		<style>
			body {background-image: none; padding-top: 0px;}
		</style>
	</head>
	<body>
		
		<form:form id="scriptCommand" name="scriptCommand" action="scriptCommand" method="post" commandName="expedientEinesScriptCommand" onsubmit="return confirmar(event)">
			<input type="hidden" name="id" value="${param.id}"/>
		
			<hel:inputTextarea required="true" name="script" placeholder="Script" inline="true"/>
				
			<div id="modal-botons" class="well">
				<button type="button" class="modal-tancar btn" name="submit" value="cancel">
					<spring:message code='comuns.cancelar' />
				</button>
				<button type="submit" class="btn btn-primary"><spring:message code='comuns.executar' /></button>
			</div>
		</form:form>
		
		<script type="text/javascript">
			function confirmar(e) {
				var e = e || window.event;
				e.cancelBubble = true;
				if (e.stopPropagation) e.stopPropagation();
				return confirm('<spring:message code="expedient.eines.confirm_executar_script_proces"/>');
			}
		</script>
	</body>
</html>