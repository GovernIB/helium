<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<ul id="tabnav">
	<li<c:if test="${param.tabActiu == 'export'}"> class="active"</c:if>><a href="<c:url value="/deploy/export.html"></c:url>">Exportació</a></li>
	<li<c:if test="${param.tabActiu == 'jbpm'}"> class="active"</c:if>><a href="<c:url value="/deploy/jbpm.html"></c:url>">jBPM</a></li>
</ul>
