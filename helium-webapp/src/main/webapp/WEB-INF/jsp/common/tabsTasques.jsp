<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<ul id="tabnav">
	<li<c:if test="${param.tabActiu == 'persona'}"> class="active"</c:if>><a href="<c:url value="/tasca/personaLlistat.html"></c:url>"><fmt:message key='common.tabstasques.personals' /> (${personaLlistatAll})</a></li>
	<li<c:if test="${param.tabActiu == 'grup'}"> class="active"</c:if>><a href="<c:url value="/tasca/grupLlistat.html"></c:url>"><fmt:message key='common.tabstasques.grup' /> (${grupLlistatAll})</a></li>
</ul>
