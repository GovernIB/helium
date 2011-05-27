<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<ul id="tabnav">
	<li<c:if test="${param.tabActiu == 'configurats'}"> class="active"</c:if>><a href="<c:url value="/carrec/jbpmConfigurats.html"></c:url>"><fmt:message key='common.tabscarrecs.configurats' /></a></li>
	<li<c:if test="${param.tabActiu == 'buits'}"> class="active"</c:if>><a href="<c:url value="/carrec/jbpmBuits.html"></c:url>"><fmt:message key='common.tabscarrecs.sense_conf' /></a></li>
</ul>
