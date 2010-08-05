<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:hasBindErrors name="command">
	<c:if test="${not empty errors.globalErrors}">
		<div id="errors" class="missatgesError">
			<c:forEach var="error" items="${errors.globalErrors}">
				<p><c:out value="${error}" escapeXml="false"/></p>
			</c:forEach>
		</div>
	</c:if>
</spring:hasBindErrors>
<c:if test="${not empty missatgesError}">
	<div id="errors" class="missatgesError">
		<c:forEach var="error" items="${missatgesError}">
			<p><c:out value="${error}" escapeXml="false"/></p>
		</c:forEach>
	</div>
	<c:remove var="missatgesError" scope="session"/>
</c:if>
<c:if test="${not empty missatgesInfo}">
	<div id="infos" class="missatgesOk">
		<c:forEach var="info" items="${missatgesInfo}">
			<p><c:out value="${info}" escapeXml="false"/></p>
		</c:forEach>
	</div>
	<script type="text/javascript">setTimeout(function(){$("#infos").fadeOut(1000)}, 2000);</script>
	<c:remove var="missatgesInfo" scope="session"/>
</c:if>
