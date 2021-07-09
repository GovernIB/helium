<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:choose>
	<c:when test="${not empty notificacions}">
		<table id="notificacions_${expedient.id}" class="table tableNotificacions table-bordered">
			<thead>
				<tr>
					<th><spring:message code="expedient.notificacio.data_enviament"/></th>
					<th><spring:message code="expedient.notificacio.assumpte"/></th>
					<th><spring:message code="expedient.notificacio.estat"/></th>
					<th><spring:message code="expedient.notificacio.interessat"/></th>
					<th><spring:message code="expedient.notificacio.document"/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${notificacions}" var="notificacio">
					<tr>
						<td><fmt:formatDate value="${notificacio.dataEnviament}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate></td>
					    <td>${notificacio.assumpte}</td>
						<td>
							<c:choose>
							<c:when test="${notificacio.estat == 'ENVIAT'}">
								<span class="label label-warning"><spring:message code="expedient.notificacio.estat.enviat"/></span>
							</c:when>
							<c:when test="${notificacio.estat == 'PROCESSAT_OK'}">
								<span class="label label-success"><spring:message code="expedient.notificacio.estat.processat_ok"/></span>
							</c:when>
							<c:when test="${notificacio.estat == 'PROCESSAT_REBUTJAT'}">
								<a id="notificacioError" data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedient.id}/notificacio/${notificacio.id}/error"/>" data-toggle="tooltip" title="<spring:message code="expedient.notificacio.mostrar_error"/>"><span class="label label-danger"><spring:message code="expedient.notificacio.estat.processat_rebutjat"/></span></a>
							</c:when>
							<c:when test="${notificacio.estat == 'PROCESSAT_ERROR'}">
								<a id="notificacioError" data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedient.id}/notificacio/${notificacio.id}/error"/>" data-toggle="tooltip" title="<spring:message code="expedient.notificacio.mostrar_error"/>"><span class="label label-danger"><spring:message code="expedient.notificacio.estat.processat_error"/></span></a>
							</c:when>
							<c:otherwise>
								<span class="label label-default">${notificacio.estat}</span>
							</c:otherwise>
							</c:choose>
						</td>
						<td>${notificacio.interessatNom} ${notificacio.interessatLlinatge1} ${notificacio.interessatLlinatge2}</td>
						<td>${notificacio.document.documentNom}</td>
						<td style="width:120px">
							<div id="dropdown-menu-accions" class="dropdown pull-right">
	 							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu dropdown-menu-right">
									<li><a id="notificacioInfo" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true" href="<c:url value="../../v3/expedient/${expedient.id}/notificacio/${notificacio.id}/info"/>"><span class="fa fa-file"></span> <spring:message code="expedient.notificacio.info"/></a></li>
									<c:if test="${not empty notificacio.error && (notificacio.estat == 'PROCESSAT_REBUTJAT' || notificacio.estat == 'PROCESSAT_ERROR')}">
										<li><a href="<c:url value="/v3/expedient/${expedient.id}/notificacio/${notificacio.id}/processar"/>"><span class="fa fa-refresh"></span> <spring:message code="expedient.notificacio.tornar_processar"/></a></li>
									</c:if>
	 							</ul>
 							</div>
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
	//]]>
	</script>
