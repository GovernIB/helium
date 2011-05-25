<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<h3 class="titol-tab">
Definició de procés actual:
<c:choose>
	<c:when test="${param.tabActiu == 'info'}"><c:set var="formUrl" value="/definicioProces/info.html"/></c:when>
	<c:when test="${param.tabActiu == 'tasques'}"><c:set var="formUrl" value="/definicioProces/tascaLlistat.html"/></c:when>
	<c:when test="${param.tabActiu == 'camps'}"><c:set var="formUrl" value="/definicioProces/campLlistat.html"/></c:when>
	<c:when test="${param.tabActiu == 'documents'}"><c:set var="formUrl" value="/definicioProces/documentLlistat.html"/></c:when>
	<c:when test="${param.tabActiu == 'terminis'}"><c:set var="formUrl" value="/definicioProces/terminiLlistat.html"/></c:when>
	<c:when test="${param.tabActiu == 'agrupacions'}"><c:set var="formUrl" value="/definicioProces/campAgrupacioLlistat.html"/></c:when>
	<c:when test="${param.tabActiu == 'accions'}"><c:set var="formUrl" value="/definicioProces/accioLlistat.html"/></c:when>
	<c:when test="${param.tabActiu == 'recursos'}"><c:set var="formUrl" value="/definicioProces/recursLlistat.html"/></c:when>
</c:choose>
<form action="<c:url value="${formUrl}"/>" style="display:inline">
	<select name="definicioProcesId" onchange="this.form.submit()">
		<c:forEach var="id" items="${definicioProces.idsWithSameKey}" varStatus="status">
			<option value="${id}"<c:if test="${param.definicioProcesId == id}">selected="selected"</c:if>>${definicioProces.idsMostrarWithSameKey[status.index]}</option>
		</c:forEach>
	</select>
</form>
</h3>
<ul id="tabnav">
	<li<c:if test="${param.tabActiu == 'info'}"> class="active"</c:if>><a href="<c:url value="/definicioProces/info.html"><c:param name="definicioProcesId" value="${definicioProces.id}"/></c:url>">Detalls</a></li>
	<li<c:if test="${param.tabActiu == 'tasques'}"> class="active"</c:if>><a href="<c:url value="/definicioProces/tascaLlistat.html"><c:param name="definicioProcesId" value="${definicioProces.id}"/></c:url>">Tasques</a></li>
	<li<c:if test="${param.tabActiu == 'camps'}"> class="active"</c:if>><a href="<c:url value="/definicioProces/campLlistat.html"><c:param name="definicioProcesId" value="${definicioProces.id}"/></c:url>">Variables</a></li>
	<li<c:if test="${param.tabActiu == 'documents'}"> class="active"</c:if>><a href="<c:url value="/definicioProces/documentLlistat.html"><c:param name="definicioProcesId" value="${definicioProces.id}"/></c:url>">Documents</a></li>
	<li<c:if test="${param.tabActiu == 'terminis'}"> class="active"</c:if>><a href="<c:url value="/definicioProces/terminiLlistat.html"><c:param name="definicioProcesId" value="${definicioProces.id}"/></c:url>">Terminis</a></li>
	<li<c:if test="${param.tabActiu == 'agrupacions'}"> class="active"</c:if>><a href="<c:url value="/definicioProces/campAgrupacioLlistat.html"><c:param name="definicioProcesId" value="${definicioProces.id}"/></c:url>">Agrupacions</a></li>
	<li<c:if test="${param.tabActiu == 'accions'}"> class="active"</c:if>><a href="<c:url value="/definicioProces/accioLlistat.html"><c:param name="definicioProcesId" value="${definicioProces.id}"/></c:url>">Accions</a></li>
	<li<c:if test="${param.tabActiu == 'recursos'}"> class="active"</c:if>><a href="<c:url value="/definicioProces/recursLlistat.html"><c:param name="definicioProcesId" value="${definicioProces.id}"/></c:url>">Recursos</a></li>
</ul>
