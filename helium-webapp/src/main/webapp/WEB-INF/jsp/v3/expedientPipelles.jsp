<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="security"%>

<html>
<head>
	<title>Informació de l'expedient</title>
	<meta name="capsaleraTipus" content="expedient"/>
	<meta name="tabActiu" content="dades"/>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
<style>
#info-carregant {
	margin-top: 4em;
	text-align: center;
}
</style>
<script>
	$(document).ready(function() {
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
		$("#pipella-${pipellaActiva}").click();
	});
	
	function confirmarEsborrarRelacio(e, idExpedient) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		if (confirm("<spring:message code='expedient.info.confirm.relacio.esborrar'/>")) {
			$('#' + idExpedient + '_formRelacioDelete').submit();
		}
	}

	function confirmarCanviVersio(e) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		if (confirm("<fmt:message key='expedient.eines.confirm_canviar_versio_proces' />")) {
			$('#' + idExpedient + '_formCanviVersio').submit();
		}
	}
</script>
</head>
<body>
	<div class="span3 mainMenu">
		<div class="thumbnail">
			<h4>
				<i class="icon-folder-open"></i> Informació
			</h4>
			<dl class="expedient-description">
				<c:if test="${expedient.tipus.teNumero}">
					<dd><em><small>Número</small></em></dd>
					<dt>${expedient.numero} <a href="#"><i class="icon-pencil"></i></a></dt>
				</c:if>
				<c:if test="${expedient.tipus.teTitol}">
					<dd><em><small>Títol</small></em></dd>
					<dt>${expedient.titol} <a href="#"><i class="icon-pencil"></i></a></dt>
				</c:if>
				<dd><em><small>Tipus</small></em></dd>
				<dt>${expedient.tipus.nom} <a href="#"><i class="icon-pencil"></i></a></dt>
				<dd><em><small>Iniciat el:</small></em></dd>
				<dt><fmt:formatDate value="${expedient.dataInici}" pattern="dd/MM/yyyy HH:mm"/>  <a href="#"><i class="icon-pencil"></i></a></dt>
				<dd><em><small>Estat</small></em></dd>
				<dt>
					<c:choose>
						<c:when test="${not empty expedient.estat}">${expedient.estat.nom}</c:when>
							<c:when test="${not empty expedient.dataFi}">Finalitzat</c:when>
						<c:otherwise>Iniciat</c:otherwise>
					</c:choose>					
				</dt>
				<dd><em><small>Definició de procés</small></em></dd>
				<dt>	
					<i class="icon-picture"></i> <a href="<c:url value="${expedient.id}/imatgeProces"/>" role="button" data-toggle="modal"><c:out value="${definicioProcesDescripcio}"/></a> <a href="#"><i class="icon-pencil"></i></a>
				</dt>
<!-- 				<dt>					 -->
<!-- 					<select class="span11" id="definicioProcesJbpmId" name="definicioProcesJbpmId"> -->
<%-- 						<option value="">&lt;&lt; <spring:message code="js.helforms.selec_valor" /> &gt;&gt;</option> --%>
<%-- 						<c:forEach var="definicioProcesJbpm" items="${definicionsProces}"> --%>
<%-- 							<option <c:if test="${definicioProcesJbpmId == definicioProcesJbpm.jbpmId}">selected="selected"</c:if> value="${definicioProcesJbpm.jbpmId}"><c:out value="${definicioProcesJbpm.descripcio}"/></option> --%>
<%-- 						</c:forEach> --%>
<!-- 					</select> -->
<!-- 				</dt> -->
			</dl>
			<div id="expedientAccio" class="btn-group">
				<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><i class="icon-cog icon-white"></i> Accions <span class="caret"></span></a>
				<ul class="dropdown-menu">
					<li><a data-modificar-modal="true" href="<c:url value="/v3/expedient/${expedientId}/modificar"/>"><i class="icon-pencil"></i>&nbsp;Modificar informació</a></li>
					<li><a data-aturar-modal="true" href="<c:url value="/v3/expedient/${expedientId}/stop"/>"><i class="icon-stop"></i>&nbsp;Aturar tramitació</a></li>
					<li><a data-exec-modal="true" href="<c:url value="/v3/expedient/${expedientId}/execucions"/>"><i class="icon-cog"></i>&nbsp;Executar nou script</a></li>
					<li><a data-relacionar-modal="true" href="<c:url value="/v3/expedient/${expedientId}/relacionats"/>"><i class="icon-cog"></i>&nbsp;Relacionar</a></li>
					<c:if test="${not empty accions}">
						<c:set var="tePermisAccions" value="${false}"/>
						<security:accesscontrollist domainObject="${expedient.tipus}" hasPermission="16,2">
							<c:set var="tePermisAccions" value="${true}"/>
						</security:accesscontrollist>
						<c:if test="${hiHaAccionsPubliques || tePermisAccions}">
							<li class="divider"></li>
							<c:forEach var="accio" items="${accions}">
								<li><a href="${expedient.id}/accio?accioId=${accio.id}"><i class="icon-fire"></i> ${accio.nom}</a></li>
							</c:forEach>
						</c:if>
					</c:if>
				</ul>
			</div>

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
				<h5>Persones participants</h5>
				<dl class="dl-horizontal tasc-description">
					<c:forEach var="participant" items="${participants}">
						<dt><i class=" icon-user"></i></dt>
						<dd>${participant.nomSencer}</dd>
					</c:forEach>
				</dl>
			</c:if>
			<c:if test="${not empty relacionats}">
				<h5>Expedients relacionats</h5>
				<dl class="dl-horizontal tasc-description">
					<c:forEach var="expedientRelacionat" items="${relacionats}">
						<dt><i class=" icon-folder-open"></i></dt>
						<dd>
							<a href="${expedientRelacionat.id}">${expedientRelacionat.identificador}</a>
							<security:accesscontrollist domainObject="${expedientRelacionat.tipus}" hasPermission="16,8">
								<form method="POST" class="formRelacioDelete" id="${expedientId}_formRelacioDelete" action="${expedientId}/relacioDelete" >
									<input type="hidden" id="expedientIdOrigen" name="expedientIdOrigen" value="${expedientId}"/>
									<input type="hidden" id="expedientIdDesti" name="expedientIdDesti" value="${expedientRelacionat.id}"/>
									<i class="icon-trash" style="cursor: pointer" onclick="return confirmarEsborrarRelacio(event, '${expedientId}')"></i>
								</form>
							</security:accesscontrollist>
						</dd>
					</c:forEach>
				</dl>
			</c:if>
		</div>
	</div>
		
	<div id="expedient-modificar-modal"></div>
	<div id="expedient-aturar-modal"></div>
	<div id="expedient-exec-modal"></div>
	<div id="expedient-relacionar-modal"></div>
	
	<script type="text/javascript">
	// <![CDATA[
		$('#expedientAccio a').click(function() {
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
					adjustHeight: true,
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

	<div id="contingut-contenidor" class="span9">
		<%--div class="btn-group" data-toggle="buttons-radio">
			<a id="dades-btn" class="btn" href="#"><i class="icon-list-alt"></i> Dades</a>
			<a id="documents-btn" class="btn" href="#"><i class="icon-file"></i> Documents</a>
		</div--%>
		<div id="contingut-carregant" class="hide"><p style="margin-top: 2em; text-align: center"><i class="icon-spinner icon-2x icon-spin"></i></p></div>
		<div id="contingut-dades" class="contingut hide"></div>
		<div id="contingut-documents" class="contingut hide"></div>
		<div id="contingut-tasques" class="contingut hide"></div>
		<div id="contingut-registre" class="contingut hide"></div>
		<div id="contingut-cronograma" class="contingut hide"></div>
	</div>
	<div class="clearfix"></div>

</body>
</html>
