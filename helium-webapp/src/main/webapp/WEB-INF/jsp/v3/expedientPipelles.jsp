<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Informació de l'expedient</title>
	<meta name="title" content="${expedient.identificador}"/>
	<meta name="title-icon-class" content="fa fa-folder-open"/>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
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
		$('#expedient-pipelles a:first').click();

		/*$('select[name=definicioProcesJbpmId]').on('change', function () {
			if (confirm("<spring:message code='expedient.eines.confirm_canviar_versio_proces' />")) {
				$.ajax({
				    url:'${expedient.id}/' + $(this).val(),
				    type:'GET',
				    dataType: 'json',
				    success: function(data) {
				        $("#imatgeproces").text(data);
				        $("#definicioProcesJbpmId").toggle();
				        $.ajax({
							url: '<c:url value="/nodecorar/v3/missatges"/>',
							async: false,
							timeout: 20000,
							success: function (data) {
								$('.contingut-alertes *').remove();
								$('.contingut-alertes').append(data);
							}
					    });
				    },
				  	"error": function(XMLHttpRequest, textStatus, errorThrown) {
					}
				});
			}
		});
		
		$("#pipella-dades").click(function() {
			$('#contingut-carregant').hide();
			if (!$('#contingut-dades').data('carregat')) {
				$('#contingut-carregant').show();
				$('#contingut-dades').load(
						'<c:url value="/nodecorar/v3/expedient/${expedient.id}/dades"/>',
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
						'<c:url value="/nodecorar/v3/expedient/${expedient.id}/documents"/>',
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
						'<c:url value="/nodecorar/v3/expedient/${expedient.id}/terminis"/>',
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
						'<c:url value="/nodecorar/v3/expedient/${expedient.id}/tasques"/>',
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
						'<c:url value="/nodecorar/v3/expedient/${expedient.id}/registre"/>',
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
						'<c:url value="/nodecorar/v3/expedient/${expedient.id}/timeline"/>',
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
				<h3>Informació</h3>
				<dl>
					<c:if test="${expedient.tipus.teNumero}">
						<dt>Número</dt>
						<dd>${expedient.numero}</dd>
					</c:if>
					<c:if test="${expedient.tipus.teTitol}">
						<dt>Títol</dt>
						<dd>${expedient.titol}</dd>
					</c:if>
					<dt>Tipus</dt>
					<dd>${expedient.tipus.nom}</dd>
					<dt>Iniciat el:</dt>
					<dd><fmt:formatDate value="${expedient.dataInici}" pattern="dd/MM/yyyy HH:mm"/>&nbsp;</dd>
					<dt>Estat</dt>
					<dd>
						<c:choose>
							<c:when test="${not empty expedient.estat}">${expedient.estat.nom}</c:when>
								<c:when test="${not empty expedient.dataFi}">Finalitzat</c:when>
							<c:otherwise>Iniciat</c:otherwise>
						</c:choose>					
					</dd>
					<dt>Definició de procés</dt>
					<dd>	
						<span class="fa fa-picture-o" onclick="$('#imgDefinicioProcesJbpm').toggle();" style="display: none !important; cursor: pointer"></span> <c:out value="${definicioProcesDescripcio}"/>&nbsp;
						<%--span class="fa fa-pencil" onclick="$('#definicioProcesJbpmId').toggle();" style="cursor: pointer"></span--%>
						<div id="imgDefinicioProcesJbpm" class="hide">
							<img src="<c:url value="/v3/expedient/${expedientId}/imatgeDefProces"/>"/>
						</div>
					</dd>
					<%--select class="span12 hide" id="definicioProcesJbpmId" name="definicioProcesJbpmId">
						<c:forEach var="definicioProcesJbpm" items="${definicionsProces}">
							<option <c:if test="${definicioProcesJbpmId == definicioProcesJbpm.jbpmId}">selected="selected"</c:if> value="${definicioProcesJbpm.jbpmId}"><c:out value="${definicioProcesJbpm.descripcio}"/></option>
						</c:forEach>
					</select--%>
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
				<c:if test="${not empty participants}">
					<h4 id="expedient-info-participants">Persones participants</h4>
					<ul class="list-unstyled">
						<c:forEach var="participant" items="${participants}">
							<li><span class="fa fa-user"></span>&nbsp;${participant.nomSencer}</li>
						</c:forEach>
					</ul>
				</c:if>
				<c:if test="${not empty relacionats}">
					<h4 id="expedient-info-relacionats">Expedients relacionats</h4>
					<ul class="list-unstyled">
						<c:forEach var="expedientRelacionat" items="${relacionats}">
							<li>
								<span class="fa fa-user"></span>&nbsp;
								<a href="${expedientRelacionat.id}">${expedientRelacionat.identificador}</a>
								<security:accesscontrollist domainObject="${expedientRelacionat.tipus}" hasPermission="16,8">
									<form method="POST" class="formRelacioDelete" id="${expedientId}_formRelacioDelete" action="${expedientId}/relacioDelete" >
										<input type="hidden" id="expedientIdOrigen" name="expedientIdOrigen" value="${expedientId}"/>
										<input type="hidden" id="expedientIdDesti" name="expedientIdDesti" value="${expedientRelacionat.id}"/>
										<span class="fa fa-trash-o" style="cursor: pointer" onclick="return confirmarEsborrarRelacio(event, '${expedientId}')"></span>
									</form>
								</security:accesscontrollist>
							</li>
						</c:forEach>
					</ul>
				</c:if>
				<div id="expedient-info-accio" class="dropdown">
					<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="<c:url value="/v3/expedient/${expedientId}/imatgeProces"/>"><span class="fa fa-cog"></span> Accions <span class="caret"></span></a>
					<ul class="dropdown-menu">
						<li><a data-modificar-modal="true" href="<c:url value="/v3/expedient/${expedientId}/modificar"/>"><span class="fa fa-pencil"></span>&nbsp;Modificar informació</a></li>
						<li><a data-aturar-modal="true" href="<c:url value="/v3/expedient/${expedientId}/stop"/>"><span class="fa fa-stop"></span>&nbsp;Aturar tramitació</a></li>
						<li><a data-exec-modal="true" href="<c:url value="/v3/expedient/${expedientId}/execucions"/>"><span class="fa fa-cog"></span>&nbsp;Executar script</a></li>
						<li><a data-relacionar-modal="true" href="<c:url value="/v3/expedient/${expedientId}/relacionats"/>"><span class="fa fa-link"></span>&nbsp;Relacionar</a></li>
						<c:if test="${not empty accions}">
							<c:set var="tePermisAccions" value="${false}"/>
							<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
								<c:set var="tePermisAccions" value="${true}"/>
							</security:accesscontrollist>
							<c:if test="${hiHaAccionsPubliques || tePermisAccions}">
								<li class="divider"></li>
								<c:forEach var="accio" items="${accions}">
									<li><a href="${expedient.id}/accio?accioId=${accio.id}"><span class="fa fa-bolt"></span> ${accio.nom}</a></li>
								</c:forEach>
							</c:if>
						</c:if>
					</ul>
				</div>
			</div>
		</div>
		<div id="expedient-pipelles" class="col-md-9">
			<ul class="nav nav-tabs" role="tablist">
				<li><a href="#contingut-tasques" role="tab" data-toggle="tab">Tasques</a></li>
				<li><a href="#contingut-dades" role="tab" data-toggle="tab">Dades</a></li>
				<li><a href="#contingut-documents" role="tab" data-toggle="tab">Documents</a></li>
				<li><a href="#contingut-terminis" role="tab" data-toggle="tab">Terminis</a></li>
				<li><a href="#contingut-registre" role="tab" data-toggle="tab">Registre</a></li>
				<li><a href="#contingut-cronograma" role="tab" data-toggle="tab">Cronograma</a></li>
			</ul>
			<div class="tab-content">
				<div id="contingut-tasques" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/tasques"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-dades" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/dades"/>">
					<div class="contingut-carregant"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
				</div>
				<div id="contingut-documents" class="tab-pane" data-href="<c:url value="/nodeco/v3/expedient/${expedient.id}/documents"/>">
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
		
	<div id="expedient-modificar-modal"></div>	
	<div id="expedient-aturar-modal"></div>
	<div id="expedient-exec-modal"></div>
	<div id="expedient-relacionar-modal"></div>
	
	<script type="text/javascript">
	// <![CDATA[
		$('#expedient-info-accio a').click(function() {
			if ($(this).data('modificar-modal')) {
				$('#expedient-modificar-modal').heliumModal({
					modalUrl: $(this).attr('href'),
					refrescarTaula: false,
					refrescarAlertes: true,
					refrescarPagina: false,
					adjustWidth: false,
					adjustHeight: true,
					maximize: true,
					alertesRefreshUrl: "<c:url value="/nodecorar/v3/missatges"/>",
					valignTop: true,
					buttonContainerId: 'formButtons'
				});
				return false;
			} else if ($(this).data('aturar-modal')) {
				$('#expedient-aturar-modal').heliumModal({
					modalUrl: $(this).attr('href'),
					refrescarTaula: false,
					refrescarAlertes: true,
					refrescarPagina: false,
					adjustWidth: false,
					adjustHeight: true,
					maximize: true,
					alertesRefreshUrl: "<c:url value="/nodecorar/v3/missatges"/>",
					valignTop: true,
					buttonContainerId: 'formButtons'
				});
				return false;
			} else if ($(this).data('exec-modal')) {
				$('#expedient-exec-modal').heliumModal({
					modalUrl: $(this).attr('href'),
					refrescarTaula: false,
					refrescarAlertes: true,
					refrescarPagina: false,
					adjustWidth: false,
					adjustHeight: true,
					maximize: true,
					alertesRefreshUrl: "<c:url value="/nodecorar/v3/missatges"/>",
					valignTop: true,
					buttonContainerId: 'formButtons'
				});
				return false;
			} else if ($(this).data('relacionar-modal')) {
				$('#expedient-relacionar-modal').heliumModal({
					modalUrl: $(this).attr('href'),
					refrescarTaula: false,
					refrescarAlertes: true,
					refrescarPagina: false,
					adjustWidth: false,
					adjustHeight: false,
					maximize: true,
					alertesRefreshUrl: "<c:url value="/nodecorar/v3/missatges"/>",
					valignTop: true,
					buttonContainerId: 'formButtons'
				});
				return false;
			} else { 
				return true;
			}
		});
	//]]>
	</script>
</body>
</html>
