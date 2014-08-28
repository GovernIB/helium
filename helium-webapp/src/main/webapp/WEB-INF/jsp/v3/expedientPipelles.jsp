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
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
<style>
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
.formRelacioDelete {float: right;}
</style>
<script>
	$(document).ready(function() {
		$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
			e.target // activated tab
			e.relatedTarget // previous tab
			var targetHref = $(e.target).attr('href');
			var loaded = $(targetHref).data('loaded')
			if (!loaded) {
				$(targetHref).load(
					$(targetHref).data('href'),
					function() {
						$(this).data('loaded', 'true');
					}
				);
			}
		})
		<c:choose>
			<c:when test="${not empty pipellaActiva}">$('#expedient-pipelles li#pipella-${pipellaActiva} a').click();</c:when>
			<c:otherwise>$('#expedient-pipelles li:first a').click();</c:otherwise>
		</c:choose>

		$('#definicioProcesJbpmId').on('change', function () {
			if (confirm("<spring:message code='expedient.eines.confirm_canviar_versio_proces' />")) {
				$.ajax({
				    url:'${expedient.id}/updateDefinicioProces/' + $(this).val(),
				    type:'GET',
				    dataType: 'json',
				    success: function(data) {
				        $("#canviDefinicioProcesJbpm").toggleClass('hide');
				        $.ajax({
							url: '<c:url value="/nodeco/v3/missatges"/>',
							async: false,
							timeout: 20000,
							success: function (data) {
								if (data.contains("alert alert-info")) {
									$("#desc_def_proc").text(data);
								}
								$('#contingut-alertes *').remove();
								$('#contingut-alertes').append(data);
							}
					    });
				    },
				  	error: function(XMLHttpRequest, textStatus, errorThrown) {
					}
				});
			}
		});
		
		/*$("#pipella-dades").click(function() {
			$('#contingut-carregant').hide();
			if (!$('#contingut-dades').data('carregat')) {
				$('#contingut-carregant').show();
				$('#contingut-dades').load(
						'<c:url value="/nodeco/v3/expedient/${expedient.id}/dades"/>',
						function() {
							$("i.agrupacio-desplegador").click(function() {
								var taula = $(this).parent().parent().parent().parent().parent();
								$('tbody', taula).toggleClass('hide');
								$(this).removeClass('icon-chevron-up');
								$(this).removeClass('icon-chevron-down');
								if ($('tbody', taula).hasClass('hide'))
									$(this).addClass('icon-chevron-down');
								else
									$(this).addClass('icon-chevron-up');
							});
							$('#contingut-carregant').hide();
						});
				$('#contingut-dades').data('carregat', 'true');
			}
			$('#contingut-contenidor .contingut').hide();
			$('#contingut-dades').show();
			$('#pipelles-expedient .pipella').removeClass('active');
			$('#pipella-dades').addClass('active');
			return false;
		});
		$("#pipella-documents").click(function() {
			$('#contingut-carregant').hide();
			if (!$('#contingut-documents').data('carregat')) {
				$('#contingut-carregant').show();
				$('#contingut-documents').load(
						'<c:url value="/nodeco/v3/expedient/${expedient.id}/documents"/>',
						function() {
							$('#contingut-carregant').hide();
						});
				$('#contingut-documents').data('carregat', 'true');
			}
			$('#contingut-contenidor .contingut').hide();
			$('#contingut-documents').show();
			$('#pipelles-expedient .pipella').removeClass('active');
			$('#pipella-documents').addClass('active');
			return false;
		});
		$("#pipella-terminis").click(function() {
			$('#contingut-carregant').hide();
			if (!$('#contingut-terminis').data('carregat')) {
				$('#contingut-carregant').show();
				$('#contingut-terminis').load(
						'<c:url value="/nodeco/v3/expedient/${expedient.id}/terminis"/>',
						function() {
							$('#contingut-carregant').hide();
						});
				$('#contingut-terminis').data('carregat', 'true');
			}
			$('#contingut-contenidor .contingut').hide();
			$('#contingut-terminis').show();
			$('#pipelles-expedient .pipella').removeClass('active');
			$('#pipella-terminis').addClass('active');
			return false;
		});
		$("#pipella-tasques").click(function() {
			$('#contingut-carregant').hide();
			if (!$('#contingut-tasques').data('carregat')) {
				$('#contingut-carregant').show();
				$('#contingut-tasques').load(
						'<c:url value="/nodeco/v3/expedient/${expedient.id}/tasques"/>',
						function() {
							$('#contingut-carregant').hide();
						});
				$('#contingut-tasques').data('carregat', 'true');
			}
			$('#contingut-contenidor .contingut').hide();
			$('#contingut-tasques').show();
			$('#pipelles-expedient .pipella').removeClass('active');
			$('#pipella-tasques').addClass('active');
			return false;
		});
		$("#pipella-registre").click(function() {
			$('#contingut-carregant').hide();
			if (!$('#contingut-registre').data('carregat')) {
				$('#contingut-carregant').show();
				$('#contingut-registre').load(
						'<c:url value="/nodeco/v3/expedient/${expedient.id}/registre"/>',
						function() {
							$('#contingut-carregant').hide();
						});
				$('#contingut-registre').data('carregat', 'true');
			}
			$('#contingut-contenidor .contingut').hide();
			$('#contingut-registre').show();
			$('#pipelles-expedient .pipella').removeClass('active');
			$('#pipella-registre').addClass('active');
			return false;
		});
		$("#pipella-cronograma").click(function() {
			$('#contingut-carregant').hide();
			if (!$('#contingut-cronograma').data('carregat')) {
				$('#contingut-carregant').show();
				$('#contingut-cronograma').load(
						'<c:url value="/nodeco/v3/expedient/${expedient.id}/timeline"/>',
						function() {
							$('#contingut-carregant').hide();
						});
				$('#contingut-cronograma').data('carregat', 'true');
			}
			$('#contingut-contenidor .contingut').hide();
			$('#contingut-cronograma').show();
			$('#pipelles-expedient .pipella').removeClass('active');
			$('#pipella-cronograma').addClass('active');
			return false;
		});
		$("#pipella-${pipellaActiva}").click();*/
	});

	function confirmarEsborrarRelacio(e, idExpedient) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		if (confirm("<spring:message code='expedient.info.confirm.relacio.esborrar'/>")) {
			$('#' + idExpedient + '_formRelacioDelete').submit();
		}
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
						<span class="fa fa-pencil" onclick="$('#canviDefinicioProcesJbpm').toggleClass('hide');" style="cursor: pointer"></span>
						<%-- 				
						<div id="imgDefinicioProcesJbpm" class="hide">
							<img src="<c:url value="/v3/expedient/${expedientId}/imatgeDefProces"/>"/>
						</div> 
						--%>
					</dd>
					<div id="canviDefinicioProcesJbpm" class="hide">
						<hel:inputSelect inline="true" name="definicioProcesJbpmId" optionItems="${definicioProces.jbpmIdsAmbDescripcio}" optionValueAttribute="jbpmId" optionTextAttribute="descripcio"/>
					</div>
				</dl>
				<%--div class="buttonList">
					<button class="btn btn-primary span12" type="button">Modificar informació</button>
					<button class="btn btn-primary span12" type="button">Descarregar expedient</button>
					<button class="btn btn-primary span12" type="button">Aturar tramitació de l’expedient</button>
				</div>
				<h5>Tasques actives</h5>
				<dl class="dl-horizontal tasc-description">
					<dt><i class=" icon-tasks"></i></dt>
					<dd>
						<a href="#">Cel·lebració de la sessió de la Junta de Govern</a><br>
						<small>Data límit: 12/08/2011</small> 
						<div class="progress progress-danger progress-striped marTop6">
							<div class="bar" style="width: 80%">Queden 3 dies de termini</div>
						</div>
					</dd>
				</dl--%>
				<c:if test="${not empty relacionats}">
					<h4 id="expedient-info-relacionats"><spring:message code="expedient.info.relacionats"/></h4>
					<ul class="list-unstyled">
						<c:forEach var="expedientRelacionat" items="${relacionats}">
							<li>
								<span class="fa fa-file-text-o"></span>&nbsp;
								<a href="${expedientRelacionat.id}">${expedientRelacionat.identificador}</a>
<%-- 								<security:accesscontrollist domainObject="${expedientRelacionat.tipus}" hasPermission="16,8"> --%>
									<form method="POST" class="formRelacioDelete" id="${expedientId}_formRelacioDelete" action="${expedientId}/relacioDelete" >
										<input type="hidden" id="expedientIdOrigen" name="expedientIdOrigen" value="${expedientId}"/>
										<input type="hidden" id="expedientIdDesti" name="expedientIdDesti" value="${expedientRelacionat.id}"/>
										<span class="fa fa-trash-o" style="cursor: pointer" onclick="return confirmarEsborrarRelacio(event, '${expedientId}')"></span>
									</form>
<%-- 								</security:accesscontrollist> --%>
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
				<div id="expedient-info-accio" class="dropdown">
					<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="<c:url value="/v3/expedient/${expedientId}/imatgeProces"/>"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></a>
					<ul class="dropdown-menu">
						<li><a data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/modificar"/>"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.info.accio.modificar"/></a></li>
						<li><a data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/relacionats"/>"><span class="fa fa-link"></span>&nbsp;<spring:message code="expedient.info.accio.relacionar"/></a></li>
						<li><a data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/suspend"/>"><span class="fa fa-stop"></span>&nbsp;<spring:message code="expedient.info.accio.aturar"/></a></li>
						<li><a data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/cancel"/>"><span class="fa fa-times"></span>&nbsp;<spring:message code='comuns.anular'/></a></li>
						<li><a data-rdt-link-modal="true" href="<c:url value="../../v3/expedient/${expedientId}/execucions"/>"><span class="fa fa-cog"></span>&nbsp;<spring:message code="expedient.info.accio.script"/></a></li>
						<c:if test="${not empty accions}">
							<c:set var="tePermisAccions" value="${false}"/>
							<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
								<c:set var="tePermisAccions" value="${true}"/>
							</security:accesscontrollist>
							<c:if test="${hiHaAccionsPubliques || tePermisAccions}">
								<li class="divider"></li>
								<c:forEach var="accio" items="${accions}">
									<li><a href="../../v3/expedient/${expedient.id}/accio?accioId=${accio.id}"><span class="fa fa-bolt"></span> ${accio.nom}</a></li>
								</c:forEach>
							</c:if>
						</c:if>
					</ul>
				</div>
			</div>
		</div>
		<div id="expedient-pipelles" class="col-md-9">
			<ul class="nav nav-tabs" role="tablist">
				<li id="pipella-tasques"><a href="#contingut-tasques" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.tasques"/></a></li>
				<li id="pipella-dades"><a href="#contingut-dades" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.dades"/></a></li>
				<li id="pipella-documents"><a href="#contingut-documents" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.documents"/></a></li>
				<li id="pipella-terminis"><a href="#contingut-terminis" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.terminis"/></a></li>
				<li id="pipella-registre"><a href="#contingut-registre" role="tab" data-toggle="tab"><spring:message code="expedient.info.pipella.registre"/></a></li>
				<li id="pipella-cronograma"><a href="#contingut-cronograma" role="tab" data-toggle="tab">Cronograma</a></li>
			</ul>
			<div class="tab-content">
				<div id="contingut-tasques" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/tasca"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-dades" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/dada"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-documents" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/document"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-terminis" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/terminis"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-registre" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/registre"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-cronograma" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/timeline"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
			</div>
		</div>
	</div>
	
	<script type="text/javascript">
	// <![CDATA[
		$("#definicioProcesJbpmId").select2({
		    allowClear: true,
		    minimumResultsForSearch: 10
		});
		$("#expedient-info-accio a").heliumEvalLink({
			refrescarAlertes: true,
			refrescarPagina: false
		});
	//]]>
	</script>
</body>
</html>
