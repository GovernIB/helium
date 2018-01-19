<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="potDissenyarExpedientTipusAdmin" value="${potAdministrarEntorn 
														or potDissenyarEntorn 
														or expedientTipus.permisAdministration 
														or expedientTipus.permisDesignAdmin}"/>
<c:set var="potDissenyarExpedientTipusDelegat" value="${potAdministrarEntorn 
														or potDissenyarEntorn 
														or expedientTipus.permisDesignAdmin 
														or expedientTipus.permisAdministration 
														or expedientTipus.permisDesignDeleg}"/>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>

<style type="text/css">
	#expedientTipus-info dt {
		color: #999;
		font-style: italic;
		font-weight: normal;
		float: left;
		width: 20em;
		margin: 0 0 0 0;
		padding: .2em .5em;
	}
	#expedientTipus-info dd {
		font-size: medium;
		font-weight: bold;
		margin: 0 0 0 0;
		padding: .2em .5em;
	}
	#expedientTipus-info-accio {
		margin-top: 1em;
	}
</style>
<c:choose>
	<c:when test="${not empty expedientTipus}">		
		<form class="well">
			<div id="expedientTipus-info" class="row">
				<div class="col-md-12">
					<dl>
						<dt><spring:message code="expedient.tipus.info.camp.codi"/></dt>
						<dd>${expedientTipus.codi}</dd>
						<dt><spring:message code="expedient.tipus.info.camp.titol"/></dt>
						<dd>${expedientTipus.nom}</dd>				
						<dt><spring:message code="expedient.tipus.info.camp.ambInfoPropia"/></dt>
						<dd>
							<spring:message code="comu.${expedientTipus.ambInfoPropia}"></spring:message>
							<c:if test="${expedientTipus.heretable}">
								, <spring:message code="expedient.tipus.info.camp.ambInfoPropia.heretable"/>
							</c:if>
							<c:if test="${not empty expedientTipusPare}">
								, 
								<a href="../expedientTipus/${expedientTipusPare.id}">
									<span class="label label-primary" 
										title="<spring:message code="expedient.tipus.info.camp.ambInfoPropia.expedientTipusPare" 
										arguments="${expedientTipusPare.codi},${expedientTipusPare.nom}" htmlEscape="true"/>">R</span>
								</a>
							</c:if>
						</dd>
						<dt><spring:message code="expedient.tipus.info.camp.amb.titol"/></dt>
						<dd><spring:message code="comu.${expedientTipus.teTitol}"></spring:message></dd>
						<dt><spring:message code="expedient.tipus.info.camp.demana.titol"/></dt>
						<dd><spring:message code="comu.${expedientTipus.demanaTitol}"></spring:message></dd>
						<dt><spring:message code="expedient.tipus.info.camp.amb.numero"/></dt>
						<dd><spring:message code="comu.${expedientTipus.teNumero}"></spring:message></dd>
						<dt><spring:message code="expedient.tipus.info.camp.demana.numero"/></dt>
						<dd><spring:message code="comu.${expedientTipus.demanaNumero}"></spring:message></dd>
						<dt><spring:message code="expedient.tipus.info.camp.permet.retroaccio"/></dt>
						<dd><spring:message code="comu.${expedientTipus.ambRetroaccio}"></spring:message></dd>
						<dt><spring:message code="expedient.tipus.info.camp.permet.reindexacioAsincrona"/></dt>
						<dd><spring:message code="comu.${expedientTipus.reindexacioAsincrona}"></spring:message></dd>
						<c:if test="${not empty expedientTipus.responsableDefecteCodi}">
							<dt><spring:message code="expedient.tipus.info.camp.reponsable.defecte"></spring:message></dt>
							<dd>
								${responsableDefecte.nomSencer != null ? responsableDefecte.nomSencer : responsableDefecte.codi}
								<c:if test="${errorResonsableNoTrobat}">
									<p class="help-block has-error" style="color: rgb(169, 68, 66)">
										<span class="fa fa-exclamation-triangle"></span> 
										<spring:message code="expedient.tipus.info.camp.reponsable.defecte.error"></spring:message>
									</p>
								</c:if>
							</dd>
						</c:if>
						<c:if test="${not empty definicioProcesInicial}">
							<dt><spring:message code="expedient.tipus.info.camp.definicio.proces.inicial"/></dt>
							<dd>${definicioProcesInicial.jbpmKey}</dd>
						</c:if>
					</dl>
					<c:if test="${potDissenyarExpedientTipusDelegat}">
						<div id="expedientTipus-info-accio" class="dropdown">
							<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="<c:url value="/v3/expedientTipus/${expedientTipus.id}}/imatgeProces"/>"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.eines"/>&nbsp;<span class="caret"></span></a>
							<ul class="dropdown-menu">
								<c:if test="${ potDissenyarExpedientTipusAdmin }">
									<li><a data-toggle="modal" href="<c:url value="/v3/expedientTipus/${expedientTipus.id}/update"/>"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
									<li><a data-toggle="modal" href="<c:url value="/v3/expedientTipus/${expedientTipus.id}/exportar"/>"><span class="fa fa-sign-out"></span>&nbsp;<spring:message code="comu.filtre.exportar"/></a></li>
									<li><a data-toggle="modal" href="<c:url value="/v3/expedientTipus/importar?expedientTipusId=${expedientTipus.id}"/>"><span class="fa fa-sign-in"></span>&nbsp;<spring:message code="comu.importar"/></a></li>
								</c:if>
								<c:if test="${ potDissenyarExpedientTipusDelegat}">
									<li><a href="<c:url value="/v3/expedientTipus/${expedientTipus.id}/propagarPlantilles"/>" title="<spring:message code="exptipus.info.propagar.plantilles.info"/>" ><span class="fa fa-cog"></span>&nbsp;<spring:message code="exptipus.info.propagar.plantilles"/></a></li>
									<li><a href="<c:url value="/v3/expedientTipus/${expedientTipus.id}/propagarConsultes"/>" title="<spring:message code="exptipus.info.propagar.consultes.info"/>" ><span class="fa fa-cog"></span>&nbsp;<spring:message code="exptipus.info.propagar.consultes"/></a></li>
								</c:if>
								<c:if test="${potDissenyarEntorn or potAdministrarEntorn}">
									<li class="divider"></li>
									<li><a href="<c:url value="/v3/expedientTipus/${expedientTipus.id}/delete"/>" data-confirm="<spring:message code="expedient.tipus.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
								</c:if>
							</ul>
						</div>
					</c:if>
				</div>
			</div>
		</form>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
// <![CDATA[            

$(document).ready(function() {	
	
	var jbpmProcessDefinitionKey = "${expedientTipus.jbpmProcessDefinitionKey}";
	
	$('#expedientTipusDefinicioProces').on('draw.dt', function() {
		// Mira si la definicio de proces coincideix amb la del tipus d'expedient
		$("tr", this).each(function(){
			if ($(this).find("td").length > 0) {
				$jbpmKey = $(this).find("td:nth-child(4)");
				if ($jbpmKey.html() == jbpmProcessDefinitionKey) 
					$jbpmKey.html("<spring:message code='comu.true'></spring:message>");
				else
					$jbpmKey.html("<spring:message code='comu.false'></spring:message>");
			}
		});		    	
		// Botó per marcar com a inicial una definicó de procés
		$("#expedientTipusDefinicioProces a.btn-inicial").click(function(e) {
			var getUrl = $(this).attr('href');
			var jbpmKey = $(this).data('jbpmkey');
			$.ajax({
				type: 'GET',
				url: getUrl,
				async: true,
				success: function(result) {
					if (result) {
						jbpmProcessDefinitionKey = jbpmKey;
						refrescaTaula();
					}
				},
				error: function(error) {
					console.log('Error:'+error);
				},
				complete: function() {
					webutilRefreshMissatges();
				}
			});
			e.stopImmediatePropagation();
			return false;
		});

		// Botó per esborrar una definicó de procés
		$("#expedientTipusDefinicioProces a.btn-delete").click(function(e) {
			var getUrl = $(this).attr('href');
			$.ajax({
				type: 'GET',
				url: getUrl,
				async: true,
				success: function(result) {
					if (result) {
						refrescaTaula();
					}
				},
				error: function(error) {
					console.log('Error:'+error);
				},
				complete: function() {
					webutilRefreshMissatges();
				}
			});
			e.stopImmediatePropagation();
			return false;
		});
	});
});

function callbackModaldefinicionsProces() {
	webutilRefreshMissatges();
	refrescaTaula();
}

function refrescaTaula() {
	$('#expedientTipusDefinicioProces').webutilDatatable('refresh');
}
// ]]>
</script>			
