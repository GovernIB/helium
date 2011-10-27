<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test="${empty tokenActual}"><a href="<c:url value="/document/arxiuMostrar.html"><c:param name="token" value="${documentActual.tokenSignatura}"/></c:url>"><img src="<c:url value="/img/page_white_put.png"/>" alt="Descarregar" title="Descarregar" border="0"/></a></c:when>
	<c:otherwise><a href="<c:url value="/document/arxiuPerSignar.html"><c:param name="token" value="${tokenActual}"/><c:param name="noe" value="true"/></c:url>"><img src="<c:url value="/img/page_white_put.png"/>" alt="Descarregar" title="Descarregar" border="0"/></a></c:otherwise>
</c:choose>
<c:if test="${documentActual.signat and empty ocultarSignatura}">
	<a href="<c:url value="/signatura/verificar.html"><c:param name="token" value="${documentActual.tokenSignatura}"/></c:url>" onclick="return verificarSignatura(this)"><img src="<c:url value="/img/rosette.png"/>" alt="Document signat" title="Document signat" border="0"/></a>
</c:if>
<c:if test="${documentActual.registrat}">
	<img src="<c:url value="/img/book_open.png"/>" alt="Registrat" title="Registrat" border="0" style="cursor:pointer" onclick="infoRegistre(${documentActual.id})"/>
	<div id="registre_${documentActual.id}" style="display:none">
		<dl class="form-info">
			<dt>Oficina</dt><dd>${documentActual.registreOficinaNom}&nbsp;</dd>
			<dt>Data</dt><dd><fmt:formatDate value="${documentActual.registreData}" pattern="dd/MM/yyyy HH:mm"/>&nbsp;</dd>
			<dt>Tipus</dt><dd><c:choose><c:when test="${documentActual.registreEntrada}">Entrada</c:when><c:otherwise>Sortida</c:otherwise></c:choose>&nbsp;</dd>
			<dt>Número</dt><dd>${documentActual.registreNumero}<c:if test="${not empty documentActual.registreAny}">/${documentActual.registreAny}</c:if>&nbsp;</dd>
		</dl>
	</div>
</c:if>
