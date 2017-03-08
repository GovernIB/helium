<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<div id="notificacions_sistra_header" class="panel-heading clicable proces" data-toggle="collapse" data-target="#notificacions_sistra">
	<spring:message code='expedient.notificacio.sistra.titol' />
	<div class="pull-right">
		<span class="icona-collapse fa fa-chevron-up"></span>
	</div>
</div>

<div class="panel panel-default">
<div id="notificacions_sistra" class="collapse in">
<c:choose>
	<c:when test="${not empty notificacionsSistra}">
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
				<c:forEach items="${notificacionsSistra}" var="notificacioSistra">
					<tr>
						<td><fmt:formatDate value="${notificacioSistra.dataRecepcio}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate></td>
					    <td>${notificacioSistra.assumpte}</td>
						<td>
							<c:choose>
							<c:when test="${notificacioSistra.estat == 'ENVIAT'}">
								<span class="label label-warning"><spring:message code="expedient.notificacio.estat.enviat"/></span>
							</c:when>
							<c:when test="${notificacioSistra.estat == 'PROCESSAT_OK'}">
								<span class="label label-success"><spring:message code="expedient.notificacio.estat.processat_ok"/></span>
							</c:when>
							<c:when test="${notificacioSistra.estat == 'PROCESSAT_REBUTJAT'}">
								<a id="notificacioError" data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedient.id}/notificacio/${notificacioSistra.id}/error"/>" data-toggle="tooltip" title="<spring:message code="expedient.notificacio.mostrar_error"/>"><span class="label label-danger"><spring:message code="expedient.notificacio.estat.processat_rebutjat"/></span></a>
							</c:when>
							<c:when test="${notificacioSistra.estat == 'PROCESSAT_ERROR'}">
								<a id="notificacioError" data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedient.id}/notificacio/${notificacioSistra.id}/error"/>" data-toggle="tooltip" title="<spring:message code="expedient.notificacio.mostrar_error"/>"><span class="label label-danger"><spring:message code="expedient.notificacio.estat.processat_error"/></span></a>
							</c:when>
							<c:otherwise>
								<span class="label label-default">${notificacioSistra.estat}</span>
							</c:otherwise>
							</c:choose>
						</td>
						<td>${notificacioSistra.interessatNom} ${notificacioSistra.interessatLlinatge1} ${notificacioSistra.interessatLlinatge2}</td>
						<td>${notificacioSistra.document.documentNom}</td>
						<td style="width:120px">
							<div id="dropdown-menu-accions" class="dropdown pull-right">
	 							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu dropdown-menu-right">
									<li><a class="notificacioInfo" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true" href="<c:url value="../../v3/expedient/${expedient.id}/notificacio/${notificacioSistra.id}/info"/>"><span class="fa fa-file"></span> <spring:message code="expedient.notificacio.info"/></a></li>
									<c:if test="${not empty notificacioSistra.error && (notificacioSistra.estat == 'PROCESSAT_REBUTJAT' || notificacioSistra.estat == 'PROCESSAT_ERROR')}">
										<li><a href="<c:url value="/v3/expedient/${expedient.id}/notificacio/${notificacioSistra.id}/processar"/>"><span class="fa fa-refresh"></span> <spring:message code="expedient.notificacio.tornar_processar"/></a></li>
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
		<div class="well well-small"><spring:message code='expedient.notificacio.expedient.sistra.cap' /></div>
	</c:otherwise>
</c:choose>
</div>
</div>

<div class="panel panel-default">
<div id="notificacions_sicer_header" class="panel-heading clicable proces" data-toggle="collapse" data-target="#notificacions_sicer">
	<spring:message code='expedient.notificacio.sicer.titol' />
	<div class="pull-right">
		<span class="icona-collapse fa fa-chevron-up"></span>
	</div>
</div>
<div id="notificacions_sicer" class="collapse in">
<c:choose>
	<c:when test="${not empty notificacionsSicer}">
		<table id="notificacions_${expedient.id}" class="table tableNotificacions table-bordered">
			<thead>
				<tr>
					<th><spring:message code="expedient.notificacio.data_creacio"/></th>
					<th><spring:message code="expedient.notificacio.data_enviament"/></th>
					<th><spring:message code="expedient.notificacio.assumpte"/></th>
					<th><spring:message code="expedient.notificacio.estat"/></th>
<%-- 					<th><spring:message code="expedient.notificacio.interessat"/></th> --%>
					<th><spring:message code="expedient.notificacio.remesa"/></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${notificacionsSicer}" var="notificacioSicer">
					<tr>
						<td><fmt:formatDate value="${notificacioSicer.dataCreacio}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate></td>
						<td>
							<c:if test="${not empty notificacioSicer.remesa}">
								<fmt:formatDate value="${notificacioSicer.remesa.dataEnviament}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate>
							</c:if>
						</td>
					    <td>${notificacioSicer.assumpte}</td>
						<td>
							<c:choose>
								<c:when test="${notificacioSicer.estat == 'PENDENT'}">
									<span class="label label-default"><spring:message code="expedient.notificacio.estat.pendent"/></span>
								</c:when>
								<c:when test="${notificacioSicer.estat == 'ENVIAT'}">
									<span class="label label-warning"><spring:message code="expedient.notificacio.estat.enviat"/></span>
								</c:when>
								<c:when test="${notificacioSicer.estat == 'VALIDAT'}">
									<span class="label label-info"><spring:message code="expedient.notificacio.estat.validat"/></span>
								</c:when>
								<c:when test="${notificacioSicer.estat == 'PROCESSAT_OK'}">
									<span class="label label-success"><spring:message code="expedient.notificacio.estat.processat_ok"/></span>
								</c:when>
								<c:when test="${notificacioSicer.estat == 'PROCESSAT_REBUTJAT'}">
									<a class="notificacioError" data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedient.id}/notificacio/${notificacioSicer.id}/error"/>" data-toggle="tooltip" title="<spring:message code="expedient.notificacio.mostrar_error"/>"><span class="label label-danger"><spring:message code="expedient.notificacio.estat.processat_rebutjat"/></span></a>
								</c:when>
								<c:when test="${notificacioSicer.estat == 'PROCESSAT_ERROR'}">
									<a class="notificacioError" data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedient.id}/notificacio/${notificacioSicer.id}/error"/>" data-toggle="tooltip" title="<spring:message code="expedient.notificacio.mostrar_error"/>"><span class="label label-danger"><spring:message code="expedient.notificacio.estat.processat_error"/></span></a>
								</c:when>
								<c:when test="${notificacioSicer.estat == 'ENVIAT_ERROR'}">
									<a class="notificacioError" data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedient.id}/notificacio/${notificacioSicer.id}/error"/>" data-toggle="tooltip" title="<spring:message code="expedient.notificacio.mostrar_error"/>"><span class="label label-danger"><spring:message code="expedient.notificacio.estat.enviat.error"/></span></a>
								</c:when>
								<c:when test="${notificacioSicer.estat == 'VALIDAT_ERROR'}">
									<a class="notificacioError" data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedient.id}/notificacio/${notificacioSicer.id}/error"/>" data-toggle="tooltip" title="<spring:message code="expedient.notificacio.mostrar_error"/>"><span class="label label-danger"><spring:message code="expedient.notificacio.estat.validat.error"/></span></a>
								</c:when>
								<c:otherwise>
									<span class="label label-default">${notificacioSicer.estat}</span>
								</c:otherwise>
							</c:choose>
						</td>
<%-- 						<td>${expedient.tipus.sicerNomLlinatges}</td> --%>
						<td>
							<c:choose>
								<c:when test="${empty notificacioSicer.remesa}">
									<span class="label label-default"><spring:message code="expedient.notificacio.remesa.no_creada"/></span>
								</c:when>
								<c:otherwise>
									${notificacioSicer.remesa.codi}
								</c:otherwise>
							</c:choose>
						</td>
						<td style="width:120px">
							<div id="dropdown-menu-accions" class="dropdown pull-right">
	 							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu dropdown-menu-right">
									<li><a class="notificacioInfo" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true" href="<c:url value="../../v3/expedient/${expedient.id}/notificacio/${notificacioSicer.id}/info"/>"><span class="fa fa-file"></span> <spring:message code="expedient.notificacio.info"/></a></li>
									<c:if test="${not empty notificacioSicer.remesa && (notificacioSicer.remesa.estat == 'ENVIAT_ERROR' || notificacioSicer.remesa.estat == 'VALIDAT_ERROR')}">
										<li><a href="<c:url value="/v3/expedient/${expedient.id}/notificacio/${notificacioSicer.id}/remesa/${notificacioSicer.remesa.id}/reenviar"/>"><span class="fa fa-share-square-o"></span> <spring:message code="expedient.notificacio.sicer.reenviar"/></a></li>
									</c:if>
									<c:if test="${not empty notificacioSicer.remesa && (notificacioSicer.remesa.estat == 'VALIDAT' || notificacioSicer.remesa.estat == 'ENVIAT')}">
										<li><a href="<c:url value="/v3/expedient/${expedient.id}/notificacio/${notificacioSicer.id}/remesa/${notificacioSicer.remesa.id}/refrescar"/>"><span class="fa fa-refresh"></span> <spring:message code="expedient.notificacio.sicer.refrescar"/></a></li>
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
		<div class="well well-small"><spring:message code='expedient.notificacio.expedient.sicer.cap' /></div>
	</c:otherwise>
</c:choose>
</div>
</div>


<script type="text/javascript">
	// <![CDATA[
	            
         $('.proces').click(function() {
			var icona = $(this).find('.icona-collapse');
			icona.toggleClass('fa-chevron-down');
			icona.toggleClass('fa-chevron-up');
		});
		$(".notificacioInfo,.notificacioError").heliumEvalLink({
			refrescarPagina: false,
			refrescarAlertes: false ,
			refrescarTaula: false
		});
		$('[data-toggle="tooltip"]').tooltip();
	//]]>
	</script>
