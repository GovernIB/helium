<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<html>
<head>
	<title><spring:message code='expedient.nova.data.nova'/></title>
	<hel:modalHead/>
</head>
<body>		
	<div id="formulari">
	<c:import url="procesDadaNova.jsp"/>
	</div>
	
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.cancelar"/></button>
		<button class="btn btn-primary right" type="submit" name="accio" value="desar_variable">
			<spring:message code="comuns.guardar"/>
		</button>
	</div>
</body>
</html>
