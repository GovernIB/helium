<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title><spring:message code="expedient.accio.script.titol"/></title>
	<hel:modalHead/>
<script type="text/javascript">
	function confirmar(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm('<spring:message code="expedient.accio.script.confirmacio"/>');
	}
</script>
</head>
<body>
	<form:form id="scriptCommand" name="scriptCommand" action="scriptCommand" method="post" modelAttribute="expedientEinesScriptCommand" onsubmit="return confirmar(event)">
		<input type="hidden" name="id" value="${param.id}"/>
		
		<div class="form-group">
		    <label for="scriptProcessId"><spring:message code="expedient.log.objecte.PROCES"/></label>
			<select id="scriptProcessId" name="scriptProcessId">
				<c:forEach var="proces" items="${processos}">
					<option value="${proces.id}" <c:if test="${proces.instanciaProcesPareId == null}"> selected</c:if>>
						<c:choose>
						 	<c:when test="${proces.instanciaProcesPareId == null}">
						 		<spring:message code="common.tabsexp.proc_princip" />	
						 	</c:when>
						 	<c:otherwise>
						 		${proces.titol}
						 	</c:otherwise>
						</c:choose>
					</option>
				</c:forEach>
			</select>
		</div>									
	
		<div class="form-group">
		    <label for="exampleInputEmail1"><spring:message code="expedient.eines.script"/></label>
			<hel:inputTextarea required="true" name="script" textKey="expedient.accio.script.camp.script" placeholderKey="expedient.accio.script.camp.script" inline="true"/>
		</div>
			
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel">
				<spring:message code="comu.boto.cancelar"/>
			</button>
			<button type="submit" class="btn btn-primary"><span class="fa fa-cog"></span>&nbsp;<spring:message code="expedient.accio.script.boto.executar"/></button>
		</div>
	</form:form>
</body>
</html>