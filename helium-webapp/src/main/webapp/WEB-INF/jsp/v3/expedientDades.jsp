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
		<c:forEach begin="0" end="${fn:length(agrupacions)}" varStatus="agrupacioStatus">
			<c:set var="agrupacio" value="${agrupacions[agrupacioStatus.index]}"/>
			<c:set var="count" value="${0}"/>
			<c:forEach var="dada" items="${dades}">
				<c:if test="${dada.agrupacioId == agrupacio.id}"><c:set var="count" value="${count + 1}"/></c:if>
			</c:forEach>
			<c:if test="${count > 0}">
				<c:choose>
					<c:when test="${not empty agrupacio}">
						<c:import url="import/expedientDadesTaula.jsp">
							<c:param name="id" value="${agrupacio.id}"/>
							<c:param name="dadesAttribute" value="dades"/>
							<c:param name="titol" value="${agrupacio.nom}"/>
							<c:param name="numColumnes" value="${numColumnes}"/>
							<c:param name="count" value="${count}"/>
							<c:param name="condicioCamp" value="agrupacioId"/>
							<c:param name="condicioValor" value="${agrupacio.id}"/>
							<c:param name="desplegat" value="${agrupacioFirst}"/>
							<c:param name="desplegadorClass" value="agrupacio-desplegador"/>
						</c:import>
					</c:when>
					<c:otherwise>
						<c:import url="import/expedientDadesTaula.jsp">
							<c:param name="dadesAttribute" value="dades"/>
							<c:param name="numColumnes" value="${numColumnes}"/>
							<c:param name="count" value="${count}"/>
							<c:param name="condicioCamp" value="agrupacioId"/>
							<c:param name="condicioEmpty" value="${true}"/>
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
		<div class="well well-small">Aquest expedient no conté cap dada</div>
	</c:otherwise>
</c:choose>