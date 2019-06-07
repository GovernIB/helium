<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="expedient.info.etiqueta.notificacions"/></title>
	<hel:modalHead/>
</head>
<body>


<c:choose>
	<c:when test="${not empty notificacions}">
		<table id="notificacions_${expedient.id}" class="table tableNotificacions table-bordered">
			<thead>
				<tr>
					<th><spring:message code="expedient.notificacio.data_enviament"/></th>
					<th><spring:message code="expedient.notificacio.concepte"/></th>
					<th><spring:message code="expedient.notificacio.estat"/></th>
					<th><spring:message code="expedient.notificacio.error"/></th>
					<th><spring:message code="expedient.notificacio.titular"/></th>
					<th><spring:message code="expedient.notificacio.destinatari"/></th>
					<th><spring:message code="expedient.notificacio.justificant"/></th>
<!-- 					<th></th> -->
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${notificacions}" var="notificacio">
					<tr>
						<td><fmt:formatDate value="${notificacio.enviatData}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate></td>
					    <td>${notificacio.concepte}</td>
						<td>
							<c:choose>
							<c:when test="${notificacio.estat == 'PENDENT'}">
								<span class="label label-warning"><spring:message code="expedient.notificacio.estat.pendent"/></span>
							</c:when>
							<c:when test="${notificacio.estat == 'ENVIADA'}">
								<span class="label label-warning"><spring:message code="expedient.notificacio.estat.enviat"/></span>
							</c:when>
							<c:when test="${notificacio.estat == 'PROCESSADA'}">
								<span class="label label-success"><spring:message code="expedient.notificacio.estat.processat_ok"/></span>
							</c:when>
							<c:when test="${notificacio.estat == 'REBUTJADA'}">
								<span class="label label-danger"><spring:message code="expedient.notificacio.estat.rebutjat"/></span>
							</c:when>
							<c:when test="${notificacio.estat == 'CANCELADA'}">
								<span class="label label-danger"><spring:message code="expedient.notificacio.estat.cancelat"/></span>
							</c:when>
							<c:otherwise>
								<span class="label label-default">${notificacio.estat}</span>
							</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:if test="${notificacio.error}">
								<button type="button" class="btn btn-lg btn-danger" data-toggle="popover" title="Error" data-content="${notificacio.errorDescripcio}"><spring:message code="expedient.notificacio.estat.processat_error"/></button>
							</c:if>
						</td>

					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.notificacio.expedient.cap' /></div>
	</c:otherwise>
</c:choose>
<script type="text/javascript">
	// <![CDATA[
		$("#notificacioInfo,#notificacioError").heliumEvalLink({
			refrescarPagina: false,
			refrescarAlertes: false ,
			refrescarTaula: false
		});
		$('[data-toggle="tooltip"]').tooltip();
		$('[data-toggle="popover"]').popover();
	//]]>
	</script>
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel"><spring:message code="comu.boto.tancar"/></button>
	</div>
</body>
</html>
