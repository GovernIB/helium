<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<c:set var="numColumnes" value="${3}"/>
<c:choose>
	<c:when test="${not empty documents}">
		<c:import url="import/expedientDadesTaula.jsp">
			<c:param name="dadesAttribute" value="documents"/>
			<c:param name="mostrarCapsalera" value="${false}"/>
			<c:param name="numColumnes" value="${numColumnes}"/>
		</c:import>
	</c:when>
	<c:otherwise>
		<div class="well well-small">Aquest expedient no conté cap document</div>
	</c:otherwise>
</c:choose>