<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%
	java.util.Map<String, String> campsMap = new java.util.HashMap<String, String>();
%>
<c:if test="${not empty tasca.camps}">
	<c:forEach var="camp" items="${tasca.camps}">
		<c:set var="campTascaActual" value="${camp}" scope="request"/>
		<c:import url="../common/campTasca.jsp"/>
<%
	campsMap.put(
			(String)request.getAttribute("codiActual"),
			(String)pageContext.getAttribute("htmlCamp"));
	request.setAttribute("campsMap", campsMap);
%>
	</c:forEach>
	<c:if test="${empty param.display || param.display == 'true'}">
		<div class="inlineLabels">
			<c:forEach var="index" begin="0" end="${fn:length(tasca.camps) - 1}">
				<c:forEach var="camp" items="${tasca.camps}">
					<c:if test="${camp.order == index}">
						${campsMap[camp.camp.codi]}
					</c:if>
				</c:forEach>
			</c:forEach>
		</div>
	</c:if>
</c:if>
