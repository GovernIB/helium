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
	<meta name="title" content="${fn:escapeXml(expedient.identificador)}"/>
	<meta name="title-icon-class" content="fa fa-folder-open"/>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script src="<c:url value="/js/expdoc-viewer.js"/>"></script>

	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script src="<c:url value="/js/helium3Tasca.js"/>"></script>
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
.right-btn {
	float: right;
	margin-top: -4px;
}
dd.proces {
	margin-bottom: -5px !important;
}
dt.subproc {
	padding-left: 15px;
}
dd.subproc {
	padding-left: 15px;
	font-size: 14px !important;
}
.etiqueta-nti-arxiu {
	font-size: 12px;
	padding-top: 4px;
	float: right;
	margin-left: 2px;
}
.sup-count {
	position: relative;
	padding: 2px 5px;
	background-color: red;
	font-size: 11px;
	top: -9px;
	left: -10px;
}
.error-triangle {
	color: red;
    font-size: 18px;
    top: 4px;
    position: relative;	
}
</style>

<script type="text/javascript">
	$(document).ready(function() {		
		$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
			var targetHref = $(e.target).attr('href');
			var loaded = $(targetHref).data('loaded')
			//if (!loaded) {	//Condició per carregar només un cop cada pipella
			if (true) {			//Condició per carregar cada vegada les pipelles
				carregaTab(targetHref);
			}
		})
		// Actualitza la pipella activa a la query per poder refrescar
		$('#expedient-pipelles>.nav-tabs>li').click(function() {
			let url = new URL(window.location);
			if ($(this).attr('id') != 'pipella-dades') {
				url.searchParams.set('pipellaActiva', $(this).attr('id').replace('pipella-',''));
			} else {
				url.searchParams.delete('pipellaActiva');
			}
			window.history.pushState({}, '', url);
		});
		
		<c:choose>
			<c:when test="${not empty pipellaActiva}">$('#expedient-pipelles li#pipella-${pipellaActiva} a').click();</c:when>
			<c:otherwise>$('#expedient-pipelles li:first a').click();</c:otherwise>
		</c:choose>
		/* Per defecte, si no s'especifica al fitxer de properties
		   tendrem un interval que executa una funció cada 10 segons per a refrescar les
		   ícones d'estat de les tasques en segon pla */
		<c:set var="refrescaSegonPla" value="${globalProperties['app.segonpla.refrescar.auto'] == 'false' ? false : true}"/>
		<c:set var="refrescaSegonPlaPeriode" value="${globalProperties['app.segonpla.refrescar.auto.periode'] != null ? globalProperties['app.segonpla.refrescar.auto.periode'] : 10}"/>
		<c:if test="${refrescaSegonPla}">
			setInterval(refrescaEstatSegonPla, (${refrescaSegonPlaPeriode} * 1000));
		</c:if>
		<c:if test="${expedient.reindexarData != null}">
		setTimeout( function(){ 
				refrescarEstatExpedient();
		 	}  , 5000 );
		</c:if>
	});
	function refrescaEstatSegonPla() {
		var tasquesSegonPlaIds = [];
		$('span.segon-pla-icona').each(function (index, value) {
			var id = $(value).attr('id').split('spi-')[1]; 
		 	tasquesSegonPlaIds.push(id);	
		});
		if (tasquesSegonPlaIds.length > 0) {
			$.ajax({
			    url: "../tasca/actualitzaEstatsSegonPla",
			    data: {"tasquesSegonPlaIds": tasquesSegonPlaIds},
			    type: "POST",
			    success: function(data) {
				    //recorrem de nou les icones de les tasques per 
				    //actualitzar-ne l'estat
				    if (data != undefined) {
					    $.each(tasquesSegonPlaIds, function(ind,val) {
						    var canviar = false;
						    var previousClass = "";
						    var iconContent = "";
						    var tascaEstat = data[val];
						    if (tascaEstat != undefined) {
						    	if (!tascaEstat['completada'] && (tascaEstat['error'] != undefined || tascaEstat['marcadaFinalitzar'] != undefined || tascaEstat['iniciFinalitzacio'] != undefined)) {
									if (tascaEstat['error'] != undefined){
										iconContent = '<i class="fa fa-exclamation-circle fa-lg error" title="<spring:message code="error.finalitzar.tasca"/>: ' + tascaEstat['error'] + '"></i>';
										previousClass = "error";
									}
									if (tascaEstat['error'] == undefined && tascaEstat['marcadaFinalitzar'] != undefined && tascaEstat['iniciFinalitzacio'] == undefined) {
										iconContent = '<i class="fa fa-clock-o fa-lg programada" title="<spring:message code="enum.tasca.etiqueta.marcada.finalitzar"/> ' + (moment(new Date(tascaEstat['marcadaFinalitzar'])).format("DD/MM/YYYY HH:mm:ss")) + '"></i>';
										previousClass = "programada";
									}
									if (tascaEstat['error'] == undefined && tascaEstat['marcadaFinalitzar'] != undefined && tascaEstat['iniciFinalitzacio'] != undefined) {
										iconContent = '<i class="fa fa-circle-o-notch fa-spin fa-lg executant" title="<spring:message code="enum.tasca.etiqueta.execucio"/> ' + (moment(new Date(tascaEstat['iniciFinalitzacio'])).format("DD/MM/YYYY HH:mm:ss")) + '"></i>';
										previousClass = "executant";
									}
						    	} else if (tascaEstat['completada']) {
						    		iconContent = '<i class="fa fa-check-circle-o fa-lg"></i>';
						    		//refrescam el datatable
						    		carregaTab("#contingut-tasques");
						    		refrescarEstatExpedient();
							    }				    	
							} else {
								iconContent = '<i class="fa fa-check-circle-o fa-lg"></i>';
								//refrescam el datatable
								carregaTab("#contingut-tasques");
							}

							if ($('#spi-' + val).length > 0 && !$('#spi-' + val + ' > i').hasClass(previousClass)) {
								$('#spi-' + val).html(iconContent);
								$('#spi-' + val + ' > i').tooltip('hide')
						          .attr('data-original-title', $('#spi-' + val).attr('title'))
						          .tooltip('fixTitle');
							}
						});
				    }
				}
			});
		}
	}
	function carregaTab(targetHref) {
		//mostrem cada cop l'icona de càrrega
		$(targetHref).html('<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>'); 
		///////////////
		
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
	function reestructura (proces, correcte) {
		if (correcte) {
			panell = $('#panel_' + proces);
			desplegats = panell.find(".collapse.in");
			var ambOcults = "";
			if ($("#boto-ocults > span").length)
				ambOcults = $("#boto-ocults > span").hasClass("fa-check-square-o");
			panell.load('<c:url value="/nodeco/v3/expedient/${expedientId}/proces/"/>' + proces + '/dada', {"ambOcults": ambOcults}, updatePanell);
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
	function confirmarMigrarArxiu(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		return confirm("<spring:message code='expedient.accio.migrararxiu.confirmacio' />");
	}
	/** Refresca l'estat i la data de fi de l'expedient quan hi ha accions sobre les tasques. */
	function refrescarEstatExpedient() {		
		var getUrl = '<c:url value="/v3/expedient/${expedientId}/consultaEstat"/>';
		$.ajax({
			type: 'GET',
			url: getUrl,
			async: true,
			success: function(data) {
				// dataFi
				if (data.dataFi != null) {
					$('#expedientDataFi').show();
				} else {
					$('#expedientDataFi').hide();
				}
				$('#expedientDataFi').find('dd').html(data.dataFi);
				// estat
				$('#expedientEstat').html(data.estat);
				// Reindexació
				if(data.reindexarData || data.reindexarError) {
					let title = "";
					if (data.reindexarData) {
						title = "<spring:message code='expedient.consulta.reindexacio.asincrona.amb.data'/> " + data.reindexarData + ". ";
						// Programa un refresc de l'estat
						setTimeout( function(){ 
							refrescarEstatExpedient();
					 	}  , 5000 );
					}
					if (data.reindexarError) {
						title ="<spring:message code='expedient.consulta.reindexacio.error.full'/>.";
						$('#reindexarEstat span').addClass('text-danger');
					} else {
						$('#reindexarEstat span').removeClass('text-danger');
					}
					$('#reindexarEstat span').attr('title', title);
					$('#reindexarEstat').show();
				} else {
					$('#reindexarEstat').hide();
				}
			},
			error: function(e) {
				console.log("Error refrescant l'estat de l'expedient: " + e);
			}
		});
	}

</script>
</head>
<body>

	<div id="botonsEstats" class="row" style="margin-bottom: 5px;">
		<!-- Botons per retrocedir d'estat -->
		<c:if test="${estatsRetrocedir != null }">
			<c:forEach var="estat" items="${estatsRetrocedir}">
				<a href="<c:url value="/v3/expedient/${expedientId}/estat/${estat.id}/canviar" />" class="btn btn-warning"  style="float: left; margin-left: 15px;">
					<b>&lt;</b> <spring:message code="expedient.info.estat.retrocedir" arguments="${estat.nom}"></spring:message></a>					
			</c:forEach>
		</c:if>
	
		<!-- Botons per avançar d'estat -->
		<c:if test="${estatsAvancar != null }">
			<c:forEach var="estat" items="${estatsAvancar}">
				<a href="<c:url value="/v3/expedient/${expedientId}/estat/${estat.id}/canviar" />" class="btn btn-success" style="float: right; margin-right: 15px;">
					<spring:message code="expedient.info.estat.avancar" arguments="${estat.nom}"></spring:message> <b>&gt;</b></a>
			</c:forEach>
		</c:if>
	</div>

	<div class="row">
		<div class="col-md-3">
			<div id="expedient-info" class="well">
				<h3>
					<spring:message code="expedient.info.informacio"/>
					<c:if test="${expedient.ntiActiu and expedient.permisRead}">
						<a	href="<c:url value="../../v3/expedient/${expedient.id}/metadadesNti"/>"
							data-rdt-link-modal="true"
							data-rdt-link-modal-min-height="500"
							id="nti">
							<span class="label label-info etiqueta-nti-arxiu">
								<c:choose>
									<c:when test="${not expedient.arxiuActiu}"><spring:message code="expedient.info.etiqueta.nti"/></c:when>
									<c:otherwise><spring:message code="expedient.info.etiqueta.arxiu"/></c:otherwise>
								</c:choose>
							</span>
						</a>
					</c:if>
				</h3>
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
					<dd>${expedient.tipus.nom}
						<c:if test="${expedient.tipus.manualAjudaNom != null}">
							<c:set var="arxiuUrl">../../v3/expedient/${expedient.tipus.id}/documentDownload"</c:set>
								<a id="descarregarZip"
									href="<c:url value="${arxiuUrl}"/>" class="fa fa-book" title="<spring:message code="expedient.tipus.form.camp.manual.ajuda.descarregar"/>">
								</a>
						</c:if>	
					</dd>
					<c:if test="${expedient.unitatOrganitzativa != null}">
						<dt><spring:message code='expedient.info.camp.unitat.organitzativa' /></dt>
						<dd>${expedient.unitatOrganitzativa.codiAndNom}</dd>
					</c:if>
					<c:if test="${not empty expedient.registreNumero}">
						<dt><spring:message code='expedient.info.camp.registre.numero' /></dt>
						<dd>${expedient.registreNumero}</dd>
					</c:if>
					<c:if test="${not empty expedient.registreData}">
						<dt><spring:message code='expedient.info.camp.registre.data' /></dt>
						<dd><fmt:formatDate value="${expedient.registreData}" pattern="dd/MM/yyyy"/></dd>
					</c:if>					
					<dt><spring:message code="expedient.info.camp.data.inici"/></dt>
					<dd><fmt:formatDate value="${expedient.dataInici}" pattern="dd/MM/yyyy HH:mm"/></dd>
					<c:choose>
						<c:when test="${expedient.iniciadorTipus == 'SISTRA'}">
							<dt><spring:message code='expedient.info.camp.tramit.origen' /></dt>
							<dd>${expedient.bantelEntradaNum}</dd>
						</c:when>
						<c:otherwise>
							<c:if test="${not empty expedient.iniciadorPersona}">
								<dt><spring:message code='expedient.info.iniciat.per' /></dt>
								<dd>${expedient.iniciadorPersona.nomSencer}</dd>
							</c:if>
						</c:otherwise>
					</c:choose>
					<block id="expedientDataFi" style="${not empty expedient.dataFi ? 'display:block' : 'display:none'}">
						<dt><spring:message code="expedient.info.camp.data.fi"/></dt>
						<dd><fmt:formatDate value="${expedient.dataFi}" pattern="dd/MM/yyyy HH:mm"/></dd>
					</block>
					<dt><spring:message code="expedient.info.camp.estat"/></dt>
					<dd>
						<span id="expedientEstat">
							<c:choose>
								<c:when test="${not empty expedient.dataFi}"><spring:message code="comu.estat.finalitzat"/></c:when>
								<c:when test="${not empty expedient.estat}">${expedient.estat.nom}</c:when>
								<c:otherwise><spring:message code="comu.estat.iniciat"/></c:otherwise>
							</c:choose>
						</span>
						<!--  Icones d'estat  -->
						<span class=" pull-right">
							<c:if test="${expedient.ambErrors}"><span class="fa fa-exclamation-triangle error-triangle" title="<spring:message code="expedient.consulta.errors"/>"></span></c:if>
							<c:if test="${expedient.aturat}"><span class="label label-danger" title="${expedient.infoAturat}">AT</span></c:if>
							<c:if test="${expedient.anulat}"><span class="label label-warning" title="${expedient.comentariAnulat}">AN</span></c:if>
							<c:if test="${not empty expedient.alertesTotals}">
									<span class="fa fa-envelope alert-envelope show-modal-alertes" title="<spring:message code="expedient.consulta.alertes.totals"/>"></span>
									<c:if test="${not empty expedient.alertesPendents or true}">
										<span class="badge sup-count">${expedient.alertesPendents}</span>
									</c:if>
							</c:if>
							<!-- per marcar l'expedient amb error de sincronització -->
							<a id ="reindexarEstat" style="display:${(not empty expedient.reindexarData || expedient.reindexarError) ? 'inline' : 'none'};" data-toggle="modal" data-maximized="true" href="<c:url value="/v3/expedient/lucene/${expedientId}"/>">
								<span class="fa fa-refresh <c:if test='${expedient.reindexarError}'>text-danger</c:if> pull-right"
									title="<c:if test='${expedient.reindexarData != null}'> <spring:message code='expedient.consulta.reindexacio.asincrona.data' arguments='${expedient.reindexarData}'/>. </c:if>
										   <c:if test='${expedient.reindexarError}'> <spring:message code='expedient.consulta.reindexacio.error.full'/>. </c:if>"></span>
						    </a>
						</span>
					</dd>
					<c:if test="${not empty expedient.responsablePersona}">
						<dt><spring:message code='expedient.info.camp.responsable' /></dt>
						<dd>${expedient.responsablePersona.nomSencer}</dd>
					</c:if>		
				<c:if test="${expedient.tipus.restringirPerGrup }">
						<dt><spring:message code="expedient.info.camp.codi.grup"/></dt>
						<dd>
							<c:choose>
								<c:when test="${not empty expedient.grupCodi}">
									<span class="fa fa-users"></span> ${expedient.grupCodi}
								</c:when>
								<c:otherwise>
									-
								</c:otherwise>
							</c:choose>
						</dd>	
				</c:if>								
					<c:if test="${!perEstats}">
						<dt><spring:message code="expedient.info.camp.defproc"/></dt>
						<dd class="proces">	
							<span class="fa fa-picture-o" onclick="$('#imgDefinicioProcesJbpm').toggle();" style="display: none !important; cursor: pointer"></span>
							&nbsp;<label id="desc_def_proc"><c:out value="${definicioProces.etiqueta}"/></label>&nbsp;
	<%-- 						<c:if test="${expedient.permisWrite}"><span class="fa fa-pencil edita" onclick="$('#canviDefinicioProcesJbpm').toggleClass('hide');" style="cursor: pointer"></span></c:if> --%>
							<c:if test="${expedient.permisDefprocUpdate}"><a id="canviversio" data-rdt-link-modal-min-height="300" data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/canviVersio"/>"><span class="fa fa-pencil edita"></span></a></c:if>
							<%-- 				
							<div id="imgDefinicioProcesJbpm" class="hide">
								<img src="<c:url value="/v3/expedient/${expedientId}/imatgeDefProces"/>"/>
							</div> 
							--%>
							<c:if test="${not empty subprocessos}">
								<dt class="subproc"><spring:message code="expedient.info.camp.defproc.subprocessos"/></dt>
								<c:forEach var="subproces" items="${subprocessos}">
									<dd class="subproc">
										${subproces}
									</dd>
								</c:forEach>
							</c:if>
						</dd>
					</c:if>
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
									<c:if test="${expedient.permisRelate}"><span class="fa fa-trash-o edita" style="cursor: pointer" onclick="return confirmarEsborrarRelacio(event, '${expedientId}')"></span></c:if>
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
				<c:if test="${expedient.permisWrite 
							|| expedient.permisStop 
							|| expedient.permisCancel 
							|| expedient.permisDelete
							|| expedient.permisRelate
							|| expedient.permisScriptExe
							|| expedient.permisUndoEnd
							|| expedient.permisLogManage }">
					<div id="expedient-info-accio" class="dropdown">
						<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="<c:url value="/v3/expedient/${expedientId}/imatgeProces"/>"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.eines"/>&nbsp;<span class="caret"></span></a>
						<ul class="dropdown-menu">

							<c:if test="${expedient.permisStop}">
								<c:choose>
									<c:when test="${not expedient.aturat}">
											<li><a data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/aturar"/>"><span class="fa fa-pause"></span>&nbsp;<spring:message code="expedient.info.accio.aturar"/></a></li>
									</c:when>
									<c:otherwise>
										<li><a data-rdt-link-confirm="<spring:message code="expedient.eines.confirm_reprendre_tramitacio"/>" href="<c:url value="../../v3/expedient/${expedientId}/reprendre"/>"><span class="fa fa-play"></span>&nbsp;<spring:message code="expedient.info.accio.reprendre"/></a></li>
									</c:otherwise>
								</c:choose>
							</c:if>								
							
							<c:if test="${expedient.permisCancel}">
								<c:choose>
									<c:when test="${not expedient.anulat}">
										<li><a data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/anular"/>"><span class="fa fa-times"></span>&nbsp;<spring:message code="expedient.info.accio.anular"/></a></li>
									</c:when>
									<c:otherwise>
										<li><a data-rdt-link-confirm="<spring:message code="expedient.consulta.confirm.desanular"/>" href="<c:url value="../../v3/expedient/${expedientId}/activar"/>"><span class="fa fa-check"></span>&nbsp;<spring:message code="expedient.info.accio.activar"/></a></li>
									</c:otherwise>
								</c:choose>
							</c:if>								

							<c:if test="${expedient.permisDelete}">
								<li><a href="<c:url value="../../v3/expedient/${expedientId}/delete"/>" data-rdt-link-ajax="false" data-rdt-link-confirm="<spring:message code="expedient.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
							</c:if>
							
							<li class="divider"></li>
							<c:if test="${expedient.permisWrite}">
								<c:if test="${empty expedient.dataFi}">
									<li><a data-rdt-link-confirm="<spring:message code="expedient.eines.finalitzar"/>" href="<c:url value="../../v3/expedient/${expedientId}/finalitzar"/>"><span class="fa fa-power-off"></span>&nbsp;<spring:message code="expedient.info.accio.finalitzar"/></a></li>
								</c:if>
							</c:if>
							<c:if test="${expedient.permisWrite}">
								<li><a data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/modificar"/>"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.info.accio.modificar"/></a></li>
							</c:if>
							<c:if test="${expedient.permisRelate}">
								<li><a data-rdt-link-modal-min-height="190" data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/relacionats"/>"><span class="fa fa-link"></span>&nbsp;<spring:message code="expedient.info.accio.relacionar"/></a></li>
							</c:if>
							<c:if test="${expedient.permisScriptExe}">
								<li><a data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/execucions"/>"><span class="fa fa-cog"></span>&nbsp;<spring:message code="expedient.info.accio.script"/></a></li>
							</c:if>
							<c:if test="${expedient.permisUndoEnd}">
								<c:if test="${not empty expedient.dataFi}">
									<li><a data-rdt-link-confirm="<spring:message code="expedient.consulta.confirm.desfinalitzar"/>" href="<c:url value="../../v3/expedient/${expedientId}/desfinalitzar"/>"><span class="fa fa-reply"></span>&nbsp;<spring:message code="expedient.info.accio.desfinalitzar"/></a></li>
								</c:if>
							</c:if>
							<c:if test="${expedient.permisWrite}">
								<li><a data-rdt-link-confirm="<spring:message code="expedient.accio.reindexa.confirmacio"/>" href="<c:url value="../../v3/expedient/${expedientId}/reindexa"/>"><span class="fa fa-refresh"></span>&nbsp;<spring:message code="expedient.info.accio.reindexa"/></a></li>
							</c:if>
							<c:if test="${expedient.permisLogManage}">
								<li><a href="<c:url value="../../v3/expedient/${expedientId}/buidalog"/>" onclick="return confirmarBuidarLogExpedient(event)"><span class="fa fa-eraser"></span>&nbsp;<spring:message code="expedient.info.accio.buidarlog"/></a></li>
							</c:if>
							<c:if test="${expedient.permisAdministration and expedient.tipus.arxiuActiu and empty expedient.arxiuUuid}">
								<li><a href="<c:url value="../../v3/expedient/${expedientId}/migrarArxiu"/>" onclick="return confirmarMigrarArxiu(event)"><span class="fa fa-suitcase"></span>&nbsp;<spring:message code="expedient.info.accio.migrararxiu"/></a></li>
							</c:if>
							
							<c:if test="${perEstats == true }">
								<li class="divider"></li>
								<c:forEach var="estat" items="${estatsRetrocedir}">
									<li><a href="<c:url value="/v3/expedient/${expedientId}/estat/${estat.id}/canviar" />">
										<b>&lt;&lt;</b> <spring:message code="expedient.info.estat.retrocedir" arguments="${estat.nom}"></spring:message></a></li>
								</c:forEach>
							
								<c:forEach var="estat" items="${estatsAvancar}">
									<li><a href="<c:url value="/v3/expedient/${expedientId}/estat/${estat.id}/canviar" />">
										<b>&gt;&gt;</b> <spring:message code="expedient.info.estat.avancar" arguments="${estat.nom}"></spring:message></a></li>
								</c:forEach>
							</c:if>
						</ul>
					</div>
				</c:if>
			</div>
		</div>
		<div id="expedient-pipelles" class="col-md-9">
			<c:if test="${not empty expedient.comentari}">
				<div class="alert alert-info" role="alert">
					<span class="fa fa-info-circle"></span>
					<strong><spring:message code="expedient.info.comentari"/>:</strong>
					${expedient.comentari}
				</div>
			</c:if>
			<c:if test="${(expedient.ambErrors)}">
				<div class="alert alert-danger" role="alert">
					<span class="fa fa-exclamation-triangle"></span>
					<strong><spring:message code="expedient.info.errors"/>: </strong><spring:message code="expedient.consulta.errors"/>
					<a class="btn btn-default btn-sm right-btn error-btn" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true" href="<c:url value="../../v3/expedient/${expedientId}/errors"/>"><spring:message code="expedient.boto.veure_errors"/></a>
				</div>
			</c:if>
			<c:if test="${not empty expedient.infoAturat}">
				<div class="alert alert-danger" role="alert">
					<span class="fa fa-pause"></span>
					<strong><spring:message code="expedient.info.aturat"/>:</strong>
					${expedient.infoAturat}
				</div>
			</c:if>
			<c:if test="${expedient.anulat}">
				<div class="alert alert-warning" role="alert">
					<span class="fa fa-times"></span>
					<strong><spring:message code="expedient.info.anulat"/>:</strong>
					${expedient.comentariAnulat}
				</div>
			</c:if>
			<c:if test="${not empty expedient.alertesTotals}">
				<div class="alert alert-warning" role="alert">
					<span class="fa fa-envelope"></span>
					<strong><spring:message code="expedient.info.alertes"/>: </strong><spring:message code="expedient.info.alertes.text"/>
					<c:if test="${not empty expedient.alertesPendents}">
						<strong>${expedient.alertesPendents} <spring:message code="expedient.info.alertes.complement"/></strong>
					</c:if>
					<a class="btn btn-default btn-sm right-btn alert-btn" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true" href="<c:url value="../../v3/expedient/${expedientId}/alertes"/>"><spring:message code="expedient.boto.veure_alertes"/></a>
				</div>
			</c:if>
			
			<c:if test="${expedient.reindexarError}">
				<div class="alert alert-danger" role="alert">
					<span class="fa fa-refresh text-danger"></span>
					<strong><spring:message code="expedient.info.reindexacio"/>: </strong>
					<c:if test="${expedient.reindexarData != null}"> <spring:message code="expedient.consulta.reindexacio.asincrona.data" arguments="${expedient.reindexarData}"/>. </c:if>
					<spring:message code="expedient.consulta.reindexacio.error.full"/>.					
					<a class="btn btn-default btn-sm right-btn alert-btn" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true" href="<c:url value='../../v3/expedient/lucene/${expedientId}'/>"><spring:message code="expedient.boto.veure_dades"/></a>
				</div>
			</c:if>

			
			<ul class="nav nav-tabs" role="tablist">
				<li id="pipella-dades"><a href="#contingut-dades" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.dades"/></a></li>
				<li id="pipella-documents"><a href="#contingut-documents" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.documents"/></a></li>
				<li id="pipella-interessats"><a href="#contingut-interessats" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.interessats"/></a></li>
				<li id="pipella-cronograma"><a href="#contingut-cronograma" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.cronograma"/></a></li>
				<li id="pipella-terminis"><a href="#contingut-terminis" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.terminis"/></a></li>
				<c:if test="${not perEstats}">
					<li id="pipella-tasques"><a href="#contingut-tasques" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.tasques"/></a></li>
				</c:if>
				<c:if test="${expedient.permisTokenRead and not perEstats}">
					<li id="pipella-tokens"><a href="#contingut-tokens" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.tokens"/></a></li>
				</c:if>
				<c:if test="${expedient.permisLogRead and not perEstats}">
					<li id="pipella-registre"><a href="#contingut-registre" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.registre"/></a></li>
				</c:if>
				<c:if test="${perEstats}">
					<li id="pipella-estats"><a href="#contingut-estats" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.estats"/></a></li>
				</c:if>
				<c:if test="${numAccions > 0}">
					<li id="pipella-accions"><a href="#contingut-accions" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.accions"/></a></li>
				</c:if>
				<c:if test="${expedient.tipus.notibActiu}">
					<li id="pipella-notificacions"><a href="#contingut-notificacions" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.notificacions"/></a></li>
				</c:if>
				<c:if test="${expedient.tipus.distribucioActiu}">
					<li id="pipella-anotacions"><a href="#contingut-anotacions" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.anotacions"/></a></li>
				</c:if>
			</ul>
			<div class="tab-content">
				<div id="contingut-dades" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/dada"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-documents" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/document"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-interessats" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/interessat"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>				
				<div id="contingut-cronograma" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/timeline"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-terminis" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/termini"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<c:if test="${not perEstats}">
					<div id="contingut-tasques" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/tasca"/>">
						<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
					</div>
				</c:if>
				<c:if test="${expedient.permisTokenRead and not perEstats}">
					<div id="contingut-tokens" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/token"/>">
						<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
					</div>
				</c:if>
				<c:if test="${expedient.permisLogRead and not perEstats}">
					<div id="contingut-registre" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/registre"/>">
						<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
					</div>
				</c:if>
				<c:if test="${perEstats}">
					<div id="contingut-estats" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/estat"/>">
						<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
					</div>
				</c:if>
				<c:if test="${numAccions > 0}">
					<div id="contingut-accions" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/accio"/>">
						<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
					</div>
				</c:if>
				<c:if test="${expedient.tipus.notibActiu}">
					<div id="contingut-notificacions" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/notificacions"/>">
						<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
					</div>
				</c:if>
				<c:if test="${expedient.tipus.distribucioActiu}">
					<div id="contingut-anotacions" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/anotacio"/>">
						<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
					</div>
				</c:if>
			</div>
		</div>
	</div>
	

	<script type="text/javascript">
	// <![CDATA[
	    $("#canviversio").heliumEvalLink({
			refrescarAlertes: true,
			refrescarPagina: true
		});
		$("#expedient-info-accio a").heliumEvalLink({
			refrescarAlertes: true,
			refrescarPagina: false
		});
		$('#nti').heliumEvalLink({
			refrescarAlertes: true,
			refrescarPagina: false
		});
		$(".alert-btn").heliumEvalLink({
			refrescarPagina: true,
			refrescarAlertes: true ,
			refrescarTaula: false
		});
		$(".error-btn").heliumEvalLink({
			refrescarPagina: false,
			refrescarAlertes: false ,
			refrescarTaula: false
		});
		$("#contingut-documents").heliumEvalLink({
			refrescarAlertes: true,
			refrescarPagina: true
		});
	//]]>
	</script>
</body>
</html>
