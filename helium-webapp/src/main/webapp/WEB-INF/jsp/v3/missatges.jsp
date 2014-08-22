<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:forEach var="attributeName" items="${pageContext.request.attributeNames}">
	<c:if test="${not fn:contains(attributeName, '.') && fn:contains(attributeName, 'ommand')}">
		<spring:hasBindErrors name="${attributeName}">
			<c:if test="${not empty errors.globalErrors}">
				<c:forEach var="error" items="${errors.globalErrors}">
					<div class="alert alert-danger">
						<button class="close" data-dismiss="alert">×</button>
						<spring:message message="${error}"/>
					</div>
				</c:forEach>
			</c:if>
		</spring:hasBindErrors>
	</c:if>
</c:forEach>

<%pageContext.setAttribute(
		"sessionErrors",
		session.getAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_ERROR));%>
<c:forEach var="text" items="${sessionErrors}">
	<div class="alert alert-danger">
		<button class="close" data-dismiss="alert">×</button>
		${text}
	</div>
</c:forEach>
<%session.removeAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_ERROR);%>

<%pageContext.setAttribute(
		"sessionWarnings",
		session.getAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_WARNING));%>
<c:forEach var="text" items="${sessionWarnings}">
	<div class="alert alert-block">
		<button class="close" data-dismiss="alert">×</button>
		${text}
	</div>
</c:forEach>
<%session.removeAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_WARNING);%>

<%pageContext.setAttribute(
		"sessionSuccesses",
		session.getAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_SUCCESS));%>
<c:forEach var="text" items="${sessionSuccesses}">
	<div class="alert alert-success">
		<button class="close" data-dismiss="alert">×</button>
		${text}
	</div>
</c:forEach>
<%session.removeAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_SUCCESS);%>

<%pageContext.setAttribute(
		"sessionInfos",
		session.getAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_INFO));%>
<c:forEach var="text" items="${sessionInfos}">
	<div class="alert alert-info">
		<button class="close" data-dismiss="alert">×</button>
		${text}
	</div>
</c:forEach>
<%session.removeAttribute(net.conselldemallorca.helium.webapp.v3.helper.MissatgesHelper.SESSION_ATTRIBUTE_INFO);%>