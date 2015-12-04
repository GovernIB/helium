<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

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
	<img src="<c:url value="/img/book_open.png"/>" alt="<fmt:message key="common.icon.registrat"/>" title="<fmt:message key="common.icon.registrat"/>" border="0" style="cursor:pointer" onclick="infoRegistre(${documentActual.id})"/>
	<div id="registre_${documentActual.id}" style="display:none">
		<dl class="form-info">
			<dt><fmt:message key="common.icon.oficina"/></dt><dd>${documentActual.registreOficinaNom}&nbsp;</dd>
			<dt><fmt:message key="common.icon.data"/></dt><dd><fmt:formatDate value="${documentActual.registreData}" pattern="dd/MM/yyyy HH:mm"/>&nbsp;</dd>
			<dt><fmt:message key="comuns.tipus"/></dt><dd><c:choose><c:when test="${documentActual.registreEntrada}"><fmt:message key="common.icon.entrada"/></c:when><c:otherwise><fmt:message key="common.icon.sortida"/></c:otherwise></c:choose>&nbsp;</dd>
			<dt><fmt:message key="common.icon.numero"/></dt><dd>${documentActual.registreNumero}&nbsp;</dd>
		</dl>
	</div>
</c:if>
<c:if test="${not empty psignaPendentActual}">
	<c:choose>
		<c:when test="${psignaPendentActual.error}"><img src="<c:url value="/img/exclamation.png"/>" alt="<fmt:message key="expedient.document.pendent.psigna.error"/>" title="<fmt:message key="expedient.document.pendent.psigna.error"/>" border="0" style="cursor:pointer" onclick="infoPsigna(${documentActual.id})"/></c:when>
		<c:otherwise><img src="<c:url value="/img/clock_red.png"/>" alt="<fmt:message key="expedient.document.pendent.psigna"/>" title="<fmt:message key="expedient.document.pendent.psigna"/>" border="0" style="cursor:pointer" onclick="infoPsigna(${documentActual.id})"/></c:otherwise>
	</c:choose>
	<div id="psigna_${documentActual.id}" style="display:none">
		<dl class="form-info">
			<dt><fmt:message key="common.icones.doc.psigna.id"/></dt><dd>${psignaPendentActual.documentId}&nbsp;</dd>
			<dt><fmt:message key="common.icones.doc.psigna.data.enviat"/></dt><dd><fmt:formatDate value="${psignaPendentActual.dataEnviat}" pattern="dd/MM/yyyy HH:mm"/>&nbsp;</dd>
			<dt><fmt:message key="common.icones.doc.psigna.estat"/></dt><dd>${psignaPendentActual.estat}&nbsp;</dd>
			<c:if test="${not empty psignaPendentActual.motiuRebuig}">
				<dt><fmt:message key="common.icones.doc.psigna.motiu.rebuig"/></dt><dd>${psignaPendentActual.motiuRebuig}&nbsp;</dd>
			</c:if>
			<c:if test="${not empty psignaPendentActual.dataProcessamentPrimer}">
				<dt><fmt:message key="common.icones.doc.psigna.data.proces.primer"/></dt><dd><fmt:formatDate value="${psignaPendentActual.dataProcessamentPrimer}" pattern="dd/MM/yyyy HH:mm"/>&nbsp;</dd>
			</c:if>
			<c:if test="${not empty psignaPendentActual.dataProcessamentDarrer}">
				<dt><fmt:message key="common.icones.doc.psigna.data.proces.darrer"/></dt><dd><fmt:formatDate value="${psignaPendentActual.dataProcessamentDarrer}" pattern="dd/MM/yyyy HH:mm"/>&nbsp;</dd>
			</c:if>
			<c:if test="${psignaPendentActual.error}">
				<dt><fmt:message key="common.icones.doc.psigna.error.processant"/></dt><dd>${psignaPendentActual.errorProcessant}&nbsp;</dd>
				<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
					<form action="<c:url value="/expedient/documentPsignaReintentar.html"/>">
						<input type="hidden" name="id" value="${instanciaProces.id}"/>
						<input type="hidden" name="psignaId" value="${psignaPendentActual.documentId}"/>
						<button class="submitButtonImage" type="submit">
							<span class="nova-variable"></span><fmt:message key="common.icones.doc.psigna.reintentar"/>
						</button>
					</form>
				</security:accesscontrollist>
			</c:if>
		</dl>
	</div>
</c:if>
