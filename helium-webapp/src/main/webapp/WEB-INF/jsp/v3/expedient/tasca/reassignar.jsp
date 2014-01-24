<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><spring:message code='alerta.llistat.expedient' />: ${expedient.identificadorLimitat}</title>
	<meta name="capsaleraTipus" content="llistat"/>
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
	
	<c:import url="../../utils/modalHead.jsp">
		<c:param name="titol" value="Reassignar tasca: ${tasca.titol}"/>
		<c:param name="buttonContainerId" value="botons"/>
	</c:import>
</head>
<body>

	<form:form action="reassignar" cssClass="form-horizontal form-tasca">
		<div class="modal-body">
			<input type="hidden" name="tascaId" value="${tasca.id}"/>
	
			<div class="control-group fila_reducida">
				<label class="control-label" for="expression"><spring:message code='expedient.tasca.expresio_assignacio' /></label>
				<div class="controls">
					<input type="text" id="expression" name="expression" class="span11" style="text-align:right" data-required="true" value="${expression}"/>
				</div>
			</div>
			<br/>
			<p class="aclaracio"><spring:message code='comuns.camps_marcats' /> <i class='icon-asterisk'></i> <spring:message code='comuns.son_oblig' /></p>
		</div>
		<div id="botons" class="well">
			<button type="button" class="btn" id="cancelar" name="cancelar" value="cancel" onclick="location='iniciar'">
				<spring:message code='comuns.cancelar' />
			</button>				
			<button type="submit" class="btn btn-primary" id="submit" name="submit" value="submit">
				<spring:message code='comuns.reassignar' />
			</button>
		</div>
	</form:form>
	
	<script>
		$( '[data-required="true"]' )
			.closest(".control-group")
			.children("label")
			.prepend("<i class='icon-asterisk'></i> ");
	</script>
</body>
</html>
