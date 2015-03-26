<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<html>
<head>
	<title><spring:message code="expedient.info.titol"/></title>
	<meta name="title" content="${expedient.identificador}"/>
	<meta name="title-icon-class" content="fa fa-folder-open"/>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
<style type="text/css">
	#expedient-info h3 {
		font-weight: bold;
		margin-top: 0;
		border-bottom: 1px solid #e3e3e3;
		padding-bottom: .2em;
	}
	#expedient-info h4 {
		font-weight: bold;
		margin-top: 0;
		border-bottom: 1px solid #e3e3e3;
		padding-bottom: .2em;
		margin-bottom: 0.4em;
	}
	#expedient-info dt {
		color: #999;
		font-size: small;
		font-style: italic;
		font-weight: normal;
	}
	#expedient-info dd {
		font-size: medium;
		font-weight: bold;
		margin-bottom: 0.4em;
	}
	#expedient-info-participants, #expedient-info-relacionats {
		padding-bottom: .2em !important;
		margin-bottom: .6em !important;
	}
	#expedient-info ul.interessats {
		padding-left: 1em !important;
	}
	#expedient-info-accio {
		margin-top: 1em;
	}
	#expedient-pipelles .tab-pane {
		margin-top: .6em;
	}
	.contingut-carregant {
		margin-top: 4em;
		text-align: center;
	}
	.edita {
		color: #428bca
	}
	.edita:hover {
		color: #3071a9
	}
	.formRelacioDelete {float: right;}
</style>
<script type="text/javascript">
	$(document).ready(function() {		
		$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
			var targetHref = $(e.target).attr('href');
			var loaded = $(targetHref).data('loaded')
			if (!loaded) {
				$(targetHref).load(
					$(targetHref).data('href'),
					function (responseText, textStatus, jqXHR) {
						if (textStatus == 'error') {
							modalAjaxErrorFunction(jqXHR, textStatus);
						} else {
							$(this).data('loaded', 'true');
						}
					}
				);
			}
		})
		<c:choose>
			<c:when test="${not empty pipellaActiva}">$('#expedient-pipelles li#pipella-${pipellaActiva} a').click();</c:when>
			<c:otherwise>$('#expedient-pipelles li:first a').click();</c:otherwise>
		</c:choose>
		$('#definicioProcesVersio').on('change', function () {
			if (confirm("<spring:message code='expedient.eines.confirm_canviar_versio_proces' />")) {
				$.ajax({
				    url:'${expedient.id}/updateDefinicioProces/' + $(this).val(),
				    type:'GET',
				    dataType: 'json',
				    success: function(data) {
				        $("#canviDefinicioProcesJbpm").toggleClass('hide');
				        $("#desc_def_proc").text($("#definicioProcesVersio option:selected").text());
						refrescarAlertas();
				    },
				    error :function(jqXHR, exception) {
						modalAjaxErrorFunction(jqXHR, exception);
					}
				});
			}
		});
		$('#definicioProcesVersio').select2({
		    width: '100%',
		    allowClear: true,
		    minimumResultsForSearch: 10
		});
	});

	function reestructura (proces, correcte) {
		if (correcte) {
			panell = $('#panel_' + proces);
			desplegats = panell.find(".collapse.in");
			var ambOcults = "";
			if ($("#ambOcults").length)
				ambOcults = $("#ambOcults").prop('checked');
			panell.load('<c:url value="/nodeco/v3/expedient/${expedientId}/dades/"/>' + proces, {"ambOcults": ambOcults}, updatePanell);
		}
	};
	
	function refrescarAlertas() {
		$.ajax({
			url: "<c:url value="/nodeco/v3/missatges"/>",
			async: false,
			timeout: 20000,
			success: function (data) {
				$('#contingut-alertes').html(data);
			}
		});
	}
		
	function confirmarEsborrarRelacio(e, idExpedient) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		if (confirm("<spring:message code='expedient.info.confirm.relacio.esborrar'/>")) {
			$('#' + idExpedient + '_formRelacioDelete').submit();
		}
	}
	function confirmarBuidarLogExpedient(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm("<spring:message code='expedient.accio.buidarlog.confirmacio' />");
	}
</script>
</head>
<body>

	<div class="row">
		<div class="col-md-3">
			<div id="expedient-info" class="well">
				<h3><spring:message code="expedient.info.informacio"/></h3>
				<dl>
					<c:if test="${expedient.tipus.teNumero}">
						<dt><spring:message code="expedient.info.camp.numero"/></dt>
						<dd>${expedient.numero}</dd>
					</c:if>
					<c:if test="${expedient.tipus.teTitol}">
						<dt><spring:message code="expedient.info.camp.titol"/></dt>
						<dd>${expedient.titol}</dd>
					</c:if>
					<dt><spring:message code="expedient.info.camp.tipus"/></dt>
					<dd>${expedient.tipus.nom}</dd>
					<dt><spring:message code="expedient.info.camp.data.inici"/></dt>
					<dd><fmt:formatDate value="${expedient.dataInici}" pattern="dd/MM/yyyy HH:mm"/></dd>
					<c:if test="${not empty expedient.dataFi}">
						<dt><spring:message code="expedient.info.camp.data.fi"/></dt>
						<dd><fmt:formatDate value="${expedient.dataFi}" pattern="dd/MM/yyyy HH:mm"/></dd>
					</c:if>
					<dt><spring:message code="expedient.info.camp.estat"/></dt>
					<dd>
						<c:choose>
							<c:when test="${not empty expedient.dataFi}"><spring:message code="comu.estat.finalitzat"/></c:when>
							<c:when test="${not empty expedient.estat}">${expedient.estat.nom}</c:when>
							<c:otherwise><spring:message code="comu.estat.iniciat"/></c:otherwise>
						</c:choose>					
					</dd>
					<dt><spring:message code="expedient.info.camp.defproc"/></dt>
					<dd>	
						<span class="fa fa-picture-o" onclick="$('#imgDefinicioProcesJbpm').toggle();" style="display: none !important; cursor: pointer"></span>
						&nbsp;<label id="desc_def_proc"><c:out value="${definicioProces.etiqueta}"/></label>&nbsp;
						<c:if test="${expedient.permisWrite}"><span class="fa fa-pencil edita" onclick="$('#canviDefinicioProcesJbpm').toggleClass('hide');" style="cursor: pointer"></span></c:if>
						<%-- 				
						<div id="imgDefinicioProcesJbpm" class="hide">
							<img src="<c:url value="/v3/expedient/${expedientId}/imatgeDefProces"/>"/>
						</div> 
						--%>
					</dd>
					<div id="canviDefinicioProcesJbpm" class="hide">
						<select id="definicioProcesVersio">
							<c:forEach var="opt" items="${definicioProces.listVersioAmbEtiqueta}">
								<option value="${opt.versio}" <c:if test="${opt.versio == definicioProces.versio}"> selected</c:if>>${opt.etiqueta}</option>
							</c:forEach>
						</select>
					</div>
				</dl>
				<c:if test="${not empty relacionats}">
					<h4 id="expedient-info-relacionats"><spring:message code="expedient.info.relacionats"/></h4>
					<ul class="list-unstyled">
						<c:forEach var="expedientRelacionat" items="${relacionats}">
							<li>
								<span class="fa fa-folder"></span>&nbsp;
								<a href="${expedientRelacionat.id}">${expedientRelacionat.identificador}</a>
								<form method="POST" class="formRelacioDelete" id="${expedientId}_formRelacioDelete" action="${expedientId}/relacioDelete" >
									<input type="hidden" id="expedientIdOrigen" name="expedientIdOrigen" value="${expedientId}"/>
									<input type="hidden" id="expedientIdDesti" name="expedientIdDesti" value="${expedientRelacionat.id}"/>
									<c:if test="${expedient.permisWrite}"><span class="fa fa-trash-o edita" style="cursor: pointer" onclick="return confirmarEsborrarRelacio(event, '${expedientId}')"></span></c:if>
								</form>
							</li>
						</c:forEach>
					</ul>
				</c:if>
				<c:if test="${not empty participants}">
					<h4 id="expedient-info-participants"><spring:message code="expedient.info.participants"/></h4>
					<ul class="list-unstyled">
						<c:forEach var="participant" items="${participants}">
							<li><span class="fa fa-user"></span>&nbsp;${participant.nomSencer}</li>
						</c:forEach>
					</ul>
				</c:if>
				<c:if test="${expedient.permisWrite}">
					<div id="expedient-info-accio" class="dropdown">
						<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="<c:url value="/v3/expedient/${expedientId}/imatgeProces"/>"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.eines"/>&nbsp;<span class="caret"></span></a>
						<ul class="dropdown-menu">
							<c:if test="${expedient.permisWrite or expedient.permisAdministration}">
								<c:choose>
									<c:when test="${not expedient.aturat}">
										<li><a data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/aturar"/>"><span class="fa fa-pause"></span>&nbsp;<spring:message code="expedient.info.accio.aturar"/></a></li>
									</c:when>
									<c:otherwise>
										<li><a data-rdt-link-confirm="<spring:message code="expedient.eines.confirm_reprendre_tramitacio"/>" href="<c:url value="../../v3/expedient/${expedientId}/reprendre"/>"><span class="fa fa-play"></span>&nbsp;<spring:message code="expedient.info.accio.reprendre"/></a></li>
									</c:otherwise>
								</c:choose>
							</c:if>
							<c:if test="${expedient.permisWrite or expedient.permisAdministration}">
								<c:choose>
									<c:when test="${not expedient.anulat}">
										<li><a data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/anular"/>"><span class="fa fa-times"></span>&nbsp;<spring:message code="expedient.info.accio.anular"/></a></li>
									</c:when>
									<c:otherwise>
										<li><a data-rdt-link-confirm="<spring:message code="expedient.consulta.confirm.desanular"/>" href="<c:url value="../../v3/expedient/${expedientId}/activar"/>"><span class="fa fa-check"></span>&nbsp;<spring:message code="expedient.info.accio.activar"/></a></li>
									</c:otherwise>
								</c:choose>
							</c:if>
							<c:if test="${expedient.permisWrite or expedient.permisAdministration}">
								<c:if test="${not empty expedient.dataFi}">
									<li><a data-rdt-link-confirm="<spring:message code="expedient.consulta.confirm.desfinalitzar"/>" href="<c:url value="../../v3/expedient/${expedientId}/desfinalitzar"/>"><span class="fa fa-reply"></span>&nbsp;<spring:message code="expedient.info.accio.desfinalitzar"/></a></li>
								</c:if>
							</c:if>
							<c:if test="${expedient.permisDelete}">
								<li><a href="<c:url value="../../v3/expedient/${expedientId}/delete"/>" data-rdt-link-confirm="<spring:message code="expedient.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
							</c:if>
							<li class="divider"></li>
							<c:if test="${expedient.permisWrite or expedient.permisAdministration}">
								<li><a data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/modificar"/>"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.info.accio.modificar"/></a></li>
							</c:if>
							<c:if test="${expedient.permisWrite or expedient.permisAdministration}">
								<li><a data-rdt-link-modal-min-height="190" data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/relacionats"/>"><span class="fa fa-link"></span>&nbsp;<spring:message code="expedient.info.accio.relacionar"/></a></li>
							</c:if>
							<c:if test="${expedient.permisWrite or expedient.permisAdministration}">
							<li><a data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/execucions"/>"><span class="fa fa-cog"></span>&nbsp;<spring:message code="expedient.info.accio.script"/></a></li>
							</c:if>
							<c:if test="${expedient.permisWrite or expedient.permisAdministration}">
								<li><a data-rdt-link-confirm="<spring:message code="expedient.accio.reindexa.confirmacio"/>" href="<c:url value="../../v3/expedient/${expedientId}/reindexa"/>"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="expedient.info.accio.reindexa"/></a></li>
							</c:if>
							<c:if test="${expedient.permisAdministration}">
								<li><a href="<c:url value="../../v3/expedient/${expedientId}/buidalog"/>" onclick="return confirmarBuidarLogExpedient(event)"><span class="fa fa-eraser"></span>&nbsp;<spring:message code="expedient.info.accio.buidarlog"/></a></li>
							</c:if>
						</ul>
					</div>
				</c:if>
			</div>
		</div>
		<div id="expedient-pipelles" class="col-md-9">
			<c:if test="${not empty expedient.infoAturat}">
				<div class="alert alert-danger" role="alert">
					<span class="fa fa-pause"></span>
					<strong><spring:message code="expedient.info.aturat"/>:</strong>
					${expedient.infoAturat}
				</div>
			</c:if>
			<c:if test="${not empty expedient.comentari}">
				<div class="alert alert-info" role="alert">
					<span class="fa fa-info-circle"></span>
					<strong><spring:message code="expedient.info.comentari"/>:</strong>
					${expedient.comentari}
				</div>
			</c:if>
			<ul class="nav nav-tabs" role="tablist">
				<li id="pipella-dades"><a href="#contingut-dades" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.dades"/></a></li>
				<li id="pipella-documents"><a href="#contingut-documents" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.documents"/></a></li>
				<li id="pipella-cronograma"><a href="#contingut-cronograma" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.cronograma"/></a></li>
				<li id="pipella-terminis"><a href="#contingut-terminis" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.terminis"/></a></li>
				<li id="pipella-tasques"><a href="#contingut-tasques" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.tasques"/></a></li>
				<li id="pipella-tokens"><a href="#contingut-tokens" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.tokens"/></a></li>
				<li id="pipella-registre"><a href="#contingut-registre" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.registre"/></a></li>
				<li id="pipella-accions"><a href="#contingut-accions" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.accions"/></a></li>
			</ul>
			<div class="tab-content">
				<div id="contingut-dades" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/dada"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-documents" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/document"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-tasques" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/tasca"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-cronograma" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/timeline"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-terminis" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/terminis"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-tokens" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/tokens"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-registre" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/registre"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-accions" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/accions"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
	// <![CDATA[
		$("#expedient-info-accio a").heliumEvalLink({
			refrescarAlertes: true,
			refrescarPagina: false
		});
	//]]>
	</script>
</body>
</html>
