<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<c:if test="${fn:length(arbreProcessos) > 1}">
<h3 class="titol-tab">
<fmt:message key='common.tabsexp.proc_actual' />:
<c:choose>
	<c:when test="${param.tabActiu == 'info'}"><c:set var="formUrl" value="/expedient/info.html"/></c:when>
	<c:when test="${param.tabActiu == 'dades'}"><c:set var="formUrl" value="/expedient/dades.html"/></c:when>
	<c:when test="${param.tabActiu == 'documents'}"><c:set var="formUrl" value="/expedient/documents.html"/></c:when>
	<c:when test="${param.tabActiu == 'timeline'}"><c:set var="formUrl" value="/expedient/timeline.html"/></c:when>
	<c:when test="${param.tabActiu == 'terminis'}"><c:set var="formUrl" value="/expedient/terminis.html"/></c:when>
	<c:when test="${param.tabActiu == 'tasques'}"><c:set var="formUrl" value="/expedient/tasques.html"/></c:when>
	<c:when test="${param.tabActiu == 'registre'}"><c:set var="formUrl" value="/expedient/registre.html"/></c:when>
	<c:when test="${param.tabActiu == 'tokens'}"><c:set var="formUrl" value="/expedient/tokens.html"/></c:when>
	<c:when test="${param.tabActiu == 'eines'}"><c:set var="formUrl" value="/expedient/eines.html"/></c:when>
</c:choose>
<form action="<c:url value="${formUrl}"/>" style="display:inline">
	<select name="id" onchange="this.form.submit()">
		<c:forEach var="proces" items="${arbreProcessos}" varStatus="varStatus">
			<c:choose>
				<c:when test="${varStatus.index == 0}">
					<c:set var="nivell" value="${0}"/>
				</c:when>
				<c:when test="${proces.instanciaProcesPareId == arbreProcessos[varStatus.index - 1].id}">
					<c:set var="nivell" value="${nivell + 1}"/>
					<c:set var="pareActualId" value="${proces.instanciaProcesPareId}"/>
				</c:when>
				<c:when test="${proces.instanciaProcesPareId == pareActualId}">
				</c:when>
				<c:otherwise>
				</c:otherwise>
			</c:choose>
			<option value="${proces.id}"<c:if test="${param.id == proces.id}"> selected="selected"</c:if>>
				<c:choose>
					<c:when test="${proces.id == expedient.processInstanceId}">&lt;&lt; <fmt:message key='common.tabsexp.proc_princip' /> &gt;&gt;</c:when>
					<c:otherwise>
						<c:forEach begin="0" end="${nivell}">&nbsp;&nbsp;</c:forEach>${proces.titol}
					</c:otherwise>
				</c:choose>
			</option>
		</c:forEach>
	</select>
</form>
</h3>
</c:if>

<ul id="tabnav">
	<li<c:if test="${param.tabActiu == 'info'}"> class="active"</c:if>><a href="<c:url value="/expedient/info.html"><c:param name="id" value="${param.id}"/></c:url>"><fmt:message key='common.tabsexp.expedient' /></a></li>
	<li<c:if test="${param.tabActiu == 'dades'}"> class="active"</c:if>><a href="<c:url value="/expedient/dades.html"><c:param name="id" value="${param.id}"/></c:url>"><fmt:message key='comuns.dades' /></a></li>
	<li<c:if test="${param.tabActiu == 'documents'}"> class="active"</c:if>><a href="<c:url value="/expedient/documents.html"><c:param name="id" value="${param.id}"/></c:url>"><fmt:message key='comuns.documents' /></a></li>
	<li<c:if test="${param.tabActiu == 'timeline'}"> class="active"</c:if>><a href="<c:url value="/expedient/timeline.html"><c:param name="id" value="${param.id}"/></c:url>"><fmt:message key='common.tabsexp.cronograma' /></a></li>
	<li<c:if test="${param.tabActiu == 'terminis'}"> class="active"</c:if>><a href="<c:url value="/expedient/terminis.html"><c:param name="id" value="${param.id}"/></c:url>"><fmt:message key='comuns.terminis' /></a></li>
	<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="1,16,128">
		<li<c:if test="${param.tabActiu == 'tasques'}"> class="active"</c:if>><a href="<c:url value="/expedient/tasques.html"><c:param name="id" value="${param.id}"/></c:url>"><fmt:message key='comuns.tasques' /></a></li>
	</security:accesscontrollist>
	<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16">
		<li<c:if test="${param.tabActiu == 'tokens'}"> class="active"</c:if>><a href="<c:url value="/expedient/tokens.html"><c:param name="id" value="${param.id}"/></c:url>"><fmt:message key='common.tabsexp.tokens' /></a></li>
	</security:accesscontrollist>
	<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="128,16">
		<li<c:if test="${param.tabActiu == 'registre'}"> class="active"</c:if>><a href="<c:url value="/expedient/registre.html"><c:param name="id" value="${param.id}"/></c:url>"><fmt:message key='common.tabsexp.registre' /></a></li>
	</security:accesscontrollist>
	<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="2,16">
		<li<c:if test="${param.tabActiu == 'eines'}"> class="active"</c:if>><a href="<c:url value="/expedient/eines.html"><c:param name="id" value="${param.id}"/></c:url>"><fmt:message key='common.tabsexp.eines' /></a></li>
	</security:accesscontrollist>
</ul>

<c:if test="${expedient.aturat}">
	<div class="missatgesAturat">
		<p><b><fmt:message key='common.tabsexp.exp_aturat' /></b>: ${expedient.infoAturat}</p>
	</div>
</c:if>
<c:if test="${not empty expedient.comentari}">
	<div class="missatgesComment">
		<p><b><fmt:message key='common.tabsexp.comentari' /></b>: ${expedient.comentari}</p>
	</div>
</c:if>
