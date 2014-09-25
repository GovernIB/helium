<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Expedient: ${expedient.identificadorLimitat}</title>
	<meta name="titolcmp" content="<spring:message code='comuns.consultes' />" />
	<link href="<c:url value="/css/tabs.css"/>" rel="stylesheet" type="text/css"/>
	<link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
<script type="text/javascript">
// <![CDATA[
function confirmarIniciar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu iniciar aquest termini?");
}
function confirmarAturar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu aturar aquest termini?");
}
function confirmarContinuar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu continuar aquest termini?");
}
function confirmarCancelar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("Estau segur que voleu calcel·lar aquest termini?");
}
// ]]>
</script>
</head>
<body>
	<c:choose>
		<c:when test="${not empty terminis}">
			<table class="dataTable table table-bordered">
				<thead>
					<tr>
						<th><spring:message code="expedient.termini.nom"/></th>
						<th><spring:message code="expedient.termini.durada"/></th>
						<th><spring:message code="expedient.termini.iniciat.el"/></th>
						<th><spring:message code="expedient.termini.aturat.el"/></th>
						<th><spring:message code="expedient.termini.data.de.fi"/></th>
						<th><spring:message code="expedient.termini.estat"/></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="registre" items="${terminis}">
						<c:set var="iniciat" value=""/>
						<c:forEach var="ini" items="${iniciats}">
							<c:if test="${registre.id == ini.termini.id and empty ini.dataCancelacio}">
								<c:set var="iniciat" value="${ini}"/>
							</c:if>
						</c:forEach>
						<tr>
							<td>${registre.nom}</td>
							<td>
								<c:choose>
									<c:when test="${not empty iniciat}">${iniciat.durada}</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${registre.duradaPredefinida}">${registre.durada}</c:when>
											<c:otherwise>Sense especificar</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</td>
							<td>
								<c:if test="${not empty iniciat}"><fmt:formatDate value="${iniciat.dataInici}" pattern="dd/MM/yyyy"/></c:if>
							</td>
							<td>
								<c:if test="${not empty iniciat and not empty iniciat.dataAturada}"><fmt:formatDate value="${iniciat.dataAturada}" pattern="dd/MM/yyyy"/></c:if>
							</td>
							<td>
								<c:if test="${not empty iniciat}"><fmt:formatDate value="${iniciat.dataFi}" pattern="dd/MM/yyyy"/></c:if>
							</td>
							<td>
								<c:choose>
									<c:when test="${empty iniciat}">
										<c:set var="trobat" value="${false}"/>
										<c:forEach var="ini" items="${iniciats}">
											<c:if test="${registre.id == ini.termini.id and not empty ini.dataCancelacio}">
												<spring:message code="expedient.termini.estat.cancelat"/>
												<c:set var="trobat" value="${true}"/>
											</c:if>
										</c:forEach>
										<c:if test="${not trobat}"><spring:message code="expedient.termini.estat.pendent"/></c:if>
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${not empty iniciat.dataAturada}"><spring:message code="expedient.termini.estat.aturat"/></c:when>
											<c:otherwise><spring:message code="expedient.termini.estat.actiu"/></c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</td>
							<td>
								<c:choose>
									<c:when test="${not registre.manual or not empty iniciat}">
										<c:choose>
											<c:when test="${not registre.manual or empty iniciat.dataAturada}">
												<img src="<c:url value="/img/control_play.png"/>" alt="<spring:message code="expedient.termini.accio.iniciar"/>" title="<spring:message code="expedient.termini.accio.iniciar"/>" border="0"/>
											</c:when>
											<c:otherwise>
												<a href="<c:url value="${expedientId}/${iniciat.id}/terminiContinuar"/>" onclick="return confirmarContinuar(event)"><img src="<c:url value="/img/control_play_blue.png"/>" alt="<spring:message code="expedient.termini.accio.continuar"/>" title="<spring:message code="expedient.termini.accio.continuar"/>" border="0"/></a>
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<a href="<c:url value="${expedientId}/${expedient.processInstanceId}/${registre.id}/terminiIniciar"/>" onclick="return confirmarIniciar(event)"><img src="<c:url value="/img/control_play_blue.png"/>" alt="<spring:message code="expedient.termini.accio.iniciar"/>" title="<spring:message code="expedient.termini.accio.iniciar"/>" border="0"/></a>
									</c:otherwise>
								</c:choose>
								
								<c:choose>
									<c:when test="${not registre.manual or empty iniciat or not empty iniciat.dataAturada}">
										<img src="<c:url value="/img/control_pause.png"/>" alt="<spring:message code="expedient.termini.accio.aturar"/>" title="<spring:message code="expedient.termini.accio.aturar"/>" border="0"/>
									</c:when>
									<c:otherwise>
										<a href="<c:url value="${expedientId}/${iniciat.id}/terminiPausar"/>" onclick="return confirmarAturar(event)"><img src="<c:url value="/img/control_pause_blue.png"/>" alt="<spring:message code="expedient.termini.accio.aturar"/>" title="<spring:message code="expedient.termini.accio.aturar"/>" border="0"/></a>
									</c:otherwise>
								</c:choose>
								
								<c:choose>
									<c:when test="${empty iniciat}">
										<img src="<c:url value="/img/control_stop.png"/>" alt="<spring:message code="expedient.termini.accio.cancelar"/>" title="<spring:message code="expedient.termini.accio.cancelar"/>" border="0"/>
									</c:when>
									<c:otherwise>
										<a href="<c:url value="${expedientId}/${iniciat.id}/terminiCancelar"/>" onclick="return confirmarCancelar(event)"><img src="<c:url value="/img/control_stop_blue.png"/>" alt="<spring:message code="expedient.termini.accio.cancelar"/>" title="<spring:message code="expedient.termini.accio.cancelar"/>" border="0"/></a>
									</c:otherwise>
								</c:choose>
								
								<c:if test="${not empty iniciat}">
									<a class="a-modal-termini" href="<c:url value="${expedientId}/${iniciat.id}/terminiModificar"/>"><i class="fa fa-pencil-square-o" alt="<spring:message code="expedient.termini.accio.modificar"/>" title="<spring:message code="expedient.termini.accio.modificar"/>" border="0"/></a>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:when>
		<c:otherwise>
			<div class="well well-small">No hi ha terminis per a mostrar</div>
		</c:otherwise>
	</c:choose>

	<div id="expedient-termini-modal"></div>
	
	<script type="text/javascript">
	// <![CDATA[
		$('.a-modal-termini').click(function() {
			$('#expedient-termini-modal').heliumModal({
				modalUrl: $(this).attr('href'),
				refrescarTaula: true,
				refrescarAlertes: true,
				refrescarPagina: false,
				adjustWidth: false,
				adjustHeight: true,
				maximize: true,
				valignTop: true,
				buttonContainerId: 'formButtons'
			});
			return false;
		});
	//]]>
	</script>
		
</body>
</html>
