<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="processInstanceId"><c:choose><c:when test="${not empty tascaActual}">${tascaActual.processInstanceId}</c:when><c:otherwise>${instanciaProcesActual.id}</c:otherwise></c:choose></c:set>
<c:choose>
	<c:when test="${documentActual.signat}"><a href="<c:url value="/expedient/documentConsultar.html"><c:param name="processInstanceId" value="${processInstanceId}"/><c:param name="docId" value="${documentActual.id}"/></c:url>"><img src="<c:url value="/img/rosette.png"/>" alt="Descarregar" title="Descarregar" border="0"/></a></c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${empty tokenActual}"><a href="<c:url value="/expedient/documentConsultar.html"><c:param name="processInstanceId" value="${processInstanceId}"/><c:param name="docId" value="${documentActual.id}"/></c:url>"><img src="<c:url value="/img/page_white_put.png"/>" alt="Descarregar" title="Descarregar" border="0"/></a></c:when>
			<c:otherwise><a href="<c:url value="/signatura/descarregarAmbToken.html"><c:param name="token" value="${tokenActual}"/><c:param name="noe" value="true"/></c:url>"><img src="<c:url value="/img/page_white_put.png"/>" alt="Descarregar" title="Descarregar" border="0"/></a></c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
