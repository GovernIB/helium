<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<script src="<c:url value="/js/helium.modal.js"/>"></script>

<c:if test="${document.signat}">
	<a 	id="verificar${document.id}"
		data-rdt-link-modal="true" 
		<c:if test="${not empty document.urlVerificacioCustodia}">data-rdt-link-modal-min-height="400"</c:if>
		class="icon signature" 
		href="<c:url value='../../../../../v3/expedient/${tasca.expedientId}/verificarSignatura/${document.documentStoreId}/${document.documentCodi}'/>?urlVerificacioCustodia=${document.urlVerificacioCustodia}">
		<span class="fa fa-certificate" title="<spring:message code='expedient.document.signat' />"></span>
	</a>
			
	<script type="text/javascript">
		// <![CDATA[
			$('#verificar${document.id}').heliumEvalLink({
				refrescarAlertes: true,
				refrescarPagina: false,
				alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>"
			});
		//]]>
	</script>
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
