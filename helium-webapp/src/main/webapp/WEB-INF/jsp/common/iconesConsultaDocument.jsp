<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test="${empty tokenActual}"><a href="<c:url value="/document/arxiuMostrar.html"><c:param name="token" value="${documentActual.tokenSignatura}"/></c:url>"><img src="<c:url value="/img/page_white_put.png"/>" alt="<fmt:message key='comuns.descarregar' />" title="<fmt:message key='comuns.descarregar' />" border="0"/></a></c:when>
	<c:otherwise><a href="<c:url value="/document/arxiuPerSignar.html"><c:param name="token" value="${tokenActual}"/><c:param name="noe" value="true"/></c:url>"><img src="<c:url value="/img/page_white_put.png"/>" alt="Descarregar" title="Descarregar" border="0"/></a></c:otherwise>
</c:choose>
<c:if test="${documentActual.signat and empty ocultarSignatura}">
	<c:choose>
		<c:when test="${not empty documentActual.urlVerificacioCustodia}"><a href="${documentActual.urlVerificacioCustodia}" onclick="return verificarSignatura(this)"><img src="<c:url value="/img/rosette.png"/>" alt="<fmt:message key='common.icon.verif_signa' />" title="<fmt:message key='common.icon.verif_signa' />" border="0"/></a></c:when>
		<c:otherwise><a href="<c:url value="/signatura/verificar.html"><c:param name="token" value="${documentActual.tokenSignatura}"/></c:url>" onclick="return verificarSignatura(this)"><img src="<c:url value="/img/rosette.png"/>" alt="<fmt:message key='common.icon.verif_signa' />" title="<fmt:message key='common.icon.verif_signa' />" border="0"/></a></c:otherwise>
	</c:choose>
</c:if>
<c:if test="${documentActual.registrat}">
	<img src="<c:url value="/img/book_open.png"/>" alt="<fmt:message key='common.icon.registrat' />" title="<fmt:message key='common.icon.registrat' />" border="0" style="cursor:pointer" onclick="infoRegistre(${documentActual.id})"/>
	<div id="registre_${documentActual.id}" style="display:none">
		<dl class="form-info">
			<dt><fmt:message key='common.icon.oficina' /></dt><dd>${documentActual.registreOficinaNom}&nbsp;</dd>
			<dt><fmt:message key='common.icon.data' /></dt><dd><fmt:formatDate value="${documentActual.registreData}" pattern="dd/MM/yyyy HH:mm"/>&nbsp;</dd>
			<dt><fmt:message key='comuns.tipus' /></dt><dd><c:choose><c:when test="${documentActual.registreEntrada}"><fmt:message key='common.icon.entrada' /></c:when><c:otherwise><fmt:message key='common.icon.sortida' /></c:otherwise></c:choose>&nbsp;</dd>
			<dt><fmt:message key='common.icon.numero' /></dt><dd>${documentActual.registreNumero}/${documentActual.registreAny}&nbsp;</dd>
		</dl>
	</div>
</c:if>
