<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="errorTitol"><spring:message code="error.jsp.titol.http"/> ${errorObject.status}</c:set>
<c:choose>
	<c:when test="${errorObject.notFound}">
		<c:set var="errorTitol"><spring:message code="error.jsp.titol.not.found"/></c:set>
	</c:when>
	<c:when test="${errorObject.sistemaExtern}">
		<c:set var="errorTitol"><spring:message code="error.jsp.titol.sistema.ext"/></c:set>
	</c:when>
	<c:when test="${errorObject.accessDenied}">
		<c:set var="errorTitol"><spring:message code="error.jsp.titol.access.denied"/></c:set>
	</c:when>
</c:choose>
<html>
<head>
	<title>${errorTitol}</title>
	<meta name="title-icon-class" content="fa fa-warning"/>
	<hel:modalHead/>
<style>
pre {
	overflow: auto;
	word-wrap: normal;
	white-space: pre;
}
</style>
</head>
<body>
	<table class="table table-bordered" style="width:100%">
	<tbody>
		<c:choose>
			<c:when test="${errorObject.notFound}">
				<tr>
					<td width="20%"><strong><spring:message code="error.jsp.tipus.element"/></strong></td>
					<td>${errorObject.throwable.objectClass.canonicalName}</td>
				</tr>
				<tr>
					<td><strong><spring:message code="error.jsp.id"/></strong></td>
					<td>${errorObject.throwable.objectId}</td>
				</tr>
			</c:when>
			<c:when test="${errorObject.sistemaExtern}">
				<tr>
					<td><strong><spring:message code="error.jsp.sistema.ext"/></strong></td>
					<td><spring:message code="sistema.extern.codi.${errorObject.throwable.sistemaExternCodi}"/></td>
				</tr>
				<tr>
					<td width="20%"><strong><spring:message code="error.jsp.missatge"/></strong></td>
					<td>${errorObject.message}</td>
				</tr>
			</c:when>
			<c:when test="${errorObject.accessDenied}">
				<tr>
					<td><strong><spring:message code="error.jsp.resource"/></strong></td>
					<td>${requestScope['javax.servlet.forward.request_uri']}</td>
				</tr>
				<tr>
					<td width="20%"><strong><spring:message code="error.jsp.source"/></strong></td>
					<td>${errorObject.accessDeniedSource}</td>
				</tr>
			</c:when>
			<c:otherwise>
				<tr>
					<td width="20%"><strong><spring:message code="error.jsp.missatge"/></strong></td>
					<td>${errorObject.message}</td>
				</tr>
			</c:otherwise>
		</c:choose>
		<c:if test="${not empty errorObject.stackTrace}">
			<tr>
				<td>
					<strong>
						<button class="btn btn-default" type="button" data-toggle="collapse" data-target="#trasaCollapse" aria-expanded="false" aria-controls="trasaCollapse">
							<spring:message code="error.jsp.trasa"/>
						</button>
					</strong>
				</td>
				<td>
					<pre class="collapse" id="trasaCollapse"><code>${errorObject.stackTrace}</code></pre>
				</td>
			</tr>
		</c:if>
	</tbody>
	</table>
</body>
</html>
