<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
	<head>
		<title>Expediente</title>
		<c:import url="../utils/modalHead.jsp">
			<c:param name="titol" value="Aturar la tramitació de l'expedient"/>
			<c:param name="buttonContainerId" value="botons"/>
		</c:import>
	</head>
	<body>		
		<form:form id="aturarExpedient" name="aturarExpedient" action="aturarExpedient" method="post" commandName="aturarCommand" onsubmit="return confirmar(event)">
			<div class="modal-body">
				<div class="span1">
					Motiu
				</div>
				<textarea id="motiu" name="motiu" autofocus="autofocus" class="span99per"></textarea>
			</div>
			<div id="botons" class="well">
				<button type="submit" class="btn btn-primary"><spring:message code="comuns.guardar"/></button>
				<button type="button" class="btn btn-tancar"><spring:message code="comuns.cancelar"/></button>
			</div>
		</form:form>
		<script>
			function confirmar(e) {
				var e = e || window.event;
				e.cancelBubble = true;
				if ($("#motiu").val() === "") {
					$('.contingut-alertes').append(
							'<div class="alert alert-error">'+
								'<button class="close" data-dismiss="alert">×</button>' +								
								'<spring:message code="comuns.camp_oblig"/>'+
								': <spring:message code="expedient.eines.motiu"/>'+
							'</div>'
					);
					window.parent.modalAjustarTamany(window.frameElement,$('html').height());
					return false;
				}
				if (e.stopPropagation) e.stopPropagation();
				return confirm('<spring:message code="expedient.eines.confirm_aturar"/>');
			}
		</script>
	</body>
</html>
