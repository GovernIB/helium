<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="es.caib.helium.back.helper.TascaFormValidatorHelper"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<form:form id="command" modelAttribute="command" action="" cssClass="form-horizontal form-tasca" method="post">

	<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>	
	<c:set var="dadaActual" value="${dada}"/>

	<%-- Primer registre, que utilitzam per a definir la capÃ§alera de la taula --%>
	<c:choose>
		<c:when test="${dadaActual.campMultiple}">
				<c:set var="registre" value="${dadaActual.multipleDades[0].registreDades}"/>
		</c:when>
		<c:otherwise>
				<c:set var="registre" value="${dadaActual.registreDades}"/>
		</c:otherwise>
	</c:choose>

	<%-- TAULA MÚLTIPLE -------------------------------------------------------------------------------------------%>
	<table>
		<tr class="multiple">
					<c:forEach var="membre" items="${registre}" varStatus="status">
				<td>
					<c:set var="inline" value="${true}"/>
					<c:set var="dada" value="${membre}"/>
					<c:set var="campCodi" value="${dadaActual.varCodi}[0].${membre.varCodi}"/>
					<%@ include file="campsTasca.jsp" %>
					<c:set var="campCodi" value=""/>
				</td>
			</c:forEach>
			
			<td class="opciones">
				<button 
					class="btn fa fa-times eliminarFila" 
					type="button" 
					value="<spring:message code='comuns.esborrar' />" 
					title="<spring:message code='comuns.esborrar' />">
				</button>
			</td>
		</tr>
	</table>

</form:form>