<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<ul id="tabnav">
	<li<c:if test="${param.tabActiu == 'info'}"> class="active"</c:if>><a href="<c:url value="/expedientTipus/info.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>">Informació</a></li>
	<li<c:if test="${param.tabActiu == 'estats'}"> class="active"</c:if>><a href="<c:url value="/expedientTipus/estats.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>">Estats</a></li>
	<li<c:if test="${param.tabActiu == 'defprocs'}"> class="active"</c:if>><a href="<c:url value="/expedientTipus/definicioProcesLlistat.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>">Definicions de procés</a></li>
	<li<c:if test="${param.tabActiu == 'sistra'}"> class="active"</c:if>><a href="<c:url value="/expedientTipus/sistra.html"><c:param name="expedientTipusId" value="${expedientTipus.id}"/></c:url>">Integració amb tramits</a></li>
</ul>
