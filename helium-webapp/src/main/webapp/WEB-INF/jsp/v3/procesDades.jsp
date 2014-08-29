<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="numColumnes" value="${3}"/>
<c:choose>
	<c:when test="${not empty dades}">
		<c:set var="agrupacioFirst" value="${true}"/>
			<c:forEach items="${dades}" var="dadesAgrupacioEntry" varStatus="agrupacioStatus">
				<c:set var="agrupacio" value="${dadesAgrupacioEntry.key}"/>
				<c:set var="count" value="${fn:length(dadesAgrupacioEntry.value)}"/>
				<c:set var="dadesAgrupacio" value="${dadesAgrupacioEntry.value}" scope="request"/>
				<c:if test="${count > 0}">
					<c:choose>
						<c:when test="${not empty agrupacio}">
							<c:import url="import/expedientDadesTaula.jsp">
								<c:param name="id" value="${agrupacio.id}"/>
								<c:param name="dadesAttribute" value="dadesAgrupacio"/>
								<c:param name="titol" value="${agrupacio.nom}"/>
								<c:param name="numColumnes" value="${numColumnes}"/>
								<c:param name="count" value="${count}"/>
								<c:param name="desplegat" value="${agrupacioFirst}"/>
								<c:param name="desplegadorClass" value="agrupacio-desplegador"/>
							</c:import>
						</c:when>
						<c:otherwise>
							<c:import url="import/expedientDadesTaula.jsp">
								<c:param name="dadesAttribute" value="dadesAgrupacio"/>
								<c:param name="titol" value="${agrupacio.nom}"/>
								<c:param name="numColumnes" value="${numColumnes}"/>
								<c:param name="count" value="${count}"/>
								<c:param name="desplegat" value="${true}"/>
								<c:param name="mostrarCapsalera" value="${false}"/>
							</c:import>
						</c:otherwise>
					</c:choose>
				</c:if>
				<c:set var="agrupacioFirst" value="${false}"/>
			</c:forEach>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.proces.cap' /></div>
	</c:otherwise>
</c:choose>