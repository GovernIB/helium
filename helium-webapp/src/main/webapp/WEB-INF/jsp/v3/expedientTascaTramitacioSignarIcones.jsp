<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<script src="<c:url value="/js/helium.modal.js"/>"></script>

<c:if test="${document.signat}">
	<c:choose>
		<c:when test="${not empty document.urlVerificacioCustodia}"><a href="${document.urlVerificacioCustodia}" onclick="return verificarSignatura(this)"><img src="<c:url value="/img/rosette.png"/>" alt="<spring:message code='common.icon.verif_signa' />" title="<spring:message code='common.icon.verif_signa' />" border="0"/></a></c:when>
		<c:otherwise>
			<a data-rdt-link-modal="true" class="icon" href="
				<c:url value="../../../../../v3/expedient/${tasca.expedientId}/tasca/${tasca.id}/verificarSignatura">
					<c:param name="docId" value="${document.id}"/>
				</c:url>"> 
				<i class="fa fa-certificate" alt="<spring:message code='common.icon.verif_signa' />" title="<spring:message code='common.icon.verif_signa' />"></i>
			</a>			
					
			<script type="text/javascript">
				// <![CDATA[
					$('.icon').heliumEvalLink({
						refrescarAlertes: true,
						refrescarPagina: false
					});
				//]]>
			</script>
		</c:otherwise>
	</c:choose>	
</c:if>
<%--
<c:if test="${document.registrat}">
	<img src="<c:url value="/img/book_open.png"/>" alt="<spring:message code="common.icon.registrat"/>" title="<spring:message code="common.icon.registrat"/>" border="0" style="cursor:pointer" onclick="infoRegistre(${document.id})"/>
	<div id="registre_${document.id}" style="display:none">
		<dl class="form-info">
			<dt><spring:message code="common.icon.oficina"/></dt><dd>${document.registreOficinaNom}&nbsp;</dd>
			<dt><spring:message code="common.icon.data"/></dt><dd><fmt:formatDate value="${document.registreData}" pattern="dd/MM/yyyy HH:mm"/>&nbsp;</dd>
			<dt><spring:message code="comuns.tipus"/></dt><dd><c:choose><c:when test="${document.registreEntrada}"><spring:message code="common.icon.entrada"/></c:when><c:otherwise><spring:message code="common.icon.sortida"/></c:otherwise></c:choose>&nbsp;</dd>
			<dt><spring:message code="common.icon.numero"/></dt><dd>${document.registreNumero}&nbsp;</dd>
		</dl>
	</div>
</c:if>
--%>
