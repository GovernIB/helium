<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ page import="java.net.URLEncoder"%>

<html>
<head>
	<title><spring:message code='tasca.pendents.completar' /></title>
	<hel:modalHead/>
</head>
<body>
	<c:choose>
		<c:when test="${not empty tasques}">
			<table class="table table-bordered">
				<tr class="table-pendents header">
					<td class="datacol"><spring:message code="tasca.llistat.columna.tipexp"/></td>
					<td class="datacol"><spring:message code="tasca.llistat.columna.expedient"/></td>
					<td class="datacol"><spring:message code="expedient.tasca.columna.id"/></td>
					<td class="maxcols"><spring:message code="expedient.tasca.columna.tasca"/></td>
					<td class="datacol"><spring:message code="expedient.tasca.iniciat.el"/></td>
					<td class="datacol"><spring:message code="expedient.tasca.temps.execucio"/></td>
				</tr>
				<c:forEach var="tasca" items="${tasques}" varStatus="index">
					<tr>
						<td>${tasca.tipusExpedient}</td>
						<td>${tasca.expedient}</td>
						<td>${tasca.tascaId}</td>
						<td>${tasca.tasca}</td>
						<td><fmt:formatDate value="${tasca.inici}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
						<td><fmt:formatNumber type="number" value="${tasca.tempsExecucio}" pattern="#,##0.00;-#,##0.00" /> s</td> 
					</tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
			<div class="well-small"><spring:message code="expedient.tasca.pendents"/></div>
		</c:otherwise>
	</c:choose>
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.tancar"/></button>
	</div>
</body>
</html>
