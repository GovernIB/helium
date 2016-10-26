<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${not empty notificacions}">
		<table id="notificacions_${expedient.id}" class="table tableNotificacions table-bordered">
			<thead>
				<tr>
					<th><spring:message code="expedient.notificacio.data"/></th>
					<th><spring:message code="expedient.notificacio.assumpte"/></th>
					<th><spring:message code="expedient.notificacio.estat"/></th>
					<th><spring:message code="expedient.notificacio.interessat"/></th>
					<th><spring:message code="expedient.notificacio.document"/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${notificacions}" var="notificacio">
					<tr>
					    <td>${notificacio.interessatNif}</td>
						<td>${notificacio.interessatNif}</td>
						<td>${notificacio.interessatNif}</td>
						<td>${notificacio.interessatNif}</td>
						<td>${notificacio.interessatNif}</td>
						<td>
							<button class="btn btn-primary">algo</button>	
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.notificacio.expedient.cap' /></div>
	</c:otherwise>
</c:choose>
