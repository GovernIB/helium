<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${not tasca.delegada or not tasca.delegacioOriginal}">
	<div class="missatgesGris">
		<h4 class="titol-missatge">Finalitzar la tasca</h4>
		<c:set var="outcomes"><c:forEach var="outcome" items="${tasca.outcomes}" varStatus="status"><c:choose><c:when test="${not empty outcome}">${outcome}</c:when><c:otherwise>Finalitzar</c:otherwise></c:choose><c:if test="${not status.last}">,</c:if></c:forEach></c:set>
		<form action="completar.html" method="post" class="uniForm" onsubmit="return confirmarFinalitzar(event)">
			<input type="hidden" name="id" value="${tasca.id}"/>
			<c:if test="${not empty param.pipella}"><input type="hidden" name="pipella" value="${param.pipella}"/></c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="type" value="buttons"/>
				<c:param name="values">${outcomes}</c:param>
				<c:param name="titles">${outcomes}</c:param>
			</c:import>
		</form>
	</div>
</c:if>