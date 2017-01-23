<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>

<c:url var="urlDatatable" value="/v3/expedientTipus/${expedientTipus.id}/integracioTramits/datatable"/>

<c:choose>
	<c:when test="${not empty expedientTipus}">

		<table	id="expedientTipusTramits"
				data-toggle="datatable"
				data-url="${urlDatatable}"
				data-paging-enabled="true"
				data-info-type="search+button"
				data-botons-template="#tableButtonsTramitsTemplate"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="sistraTramitCodi" width="20%" data-orderable="true"><spring:message code="expedient.tipus.integracio.tramits.tramitCodi"/></th>
					<th data-col-name="tipus" data-template="#cellExpedientTipusTramitTipus">
						<spring:message code="expedient.tipus.integracio.tramits.tipus.operacio"/>
						<script id="cellExpedientTipusTramitTipus" type="text/x-jsrender">
						{{if tipus=="INICIAR_EXPEDIENT" }}
							<spring:message code="expedient.tipus.integracio.tramits.tipus.INICIAR_EXPEDIENT"></spring:message>
						{{/if}}
						{{if tipus=="EXECUTAR_ACCIO" }}
							<spring:message code="expedient.tipus.integracio.tramits.tipus.EXECUTAR_ACCIO"></spring:message>
						{{/if}}
						</script>
					</th>
					<th data-col-name="accio" data-template="#cellTramitAccio" data-orderable="true"><spring:message code="expedient.tipus.integracio.tramits.titol.accio"/>
						<script id="cellTramitAccio" type="text/x-jsrender">
						{{if accio!=null}}
							{{:accio.nom}}
						{{/if}}
						</script>
					</th>
					<th data-col-name="numVariables" data-template="#cellTramitVariablesTemplate" data-orderable="false" width="13%">
						<script id="cellTramitVariablesTemplate" type="text/x-jsrender">
							<a data-toggle="modal" data-callback="callbackModalEnumerats()" href="${expedientTipus.id}/integracioTramits/{{:id}}/mapeig/${tipusMapeigVariable}" class="btn btn-default"><spring:message code="expedient.tipus.integracio.tramits.variables"/>&nbsp;<span class="badge">{{:numVariables}}</span></a>
						</script>
					</th>
					<th data-col-name="numDocuments" data-template="#cellTramitDocumentsTemplate" data-orderable="false" width="13%">
						<script id="cellTramitDocumentsTemplate" type="text/x-jsrender">
							<a data-toggle="modal" data-callback="callbackModalEnumerats()" href="${expedientTipus.id}/integracioTramits/{{:id}}/mapeig/${tipusMapeigDocument}" class="btn btn-default"><spring:message code="expedient.tipus.integracio.tramits.documents"/>&nbsp;<span class="badge">{{:numDocuments}}</span></a>
						</script>
					</th>
					<th data-col-name="numAdjunts" data-template="#cellTramitAdjuntsTemplate" data-orderable="false" width="13%">
						<script id="cellTramitAdjuntsTemplate" type="text/x-jsrender">
							<a data-toggle="modal" data-callback="callbackModalEnumerats()" href="${expedientTipus.id}/integracioTramits/{{:id}}/mapeig/${tipusMapeigAdjunt}" class="btn btn-default"><spring:message code="expedient.tipus.integracio.tramits.adjunts"/>&nbsp;<span class="badge">{{:numAdjunts}}</span></a>
						</script>
					</th>
					
					
					<th data-col-name="id" data-template="#cellTramitsTemplate" data-orderable="false" width="10%">
						<script id="cellTramitsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a data-toggle="modal" data-callback="callbackModalTramits()" href="${expedientTipus.id}/integracioTramit/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
								<li><a href="${expedientTipus.id}/integracioTramit/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.integracio.tramits.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
							</ul>
						</div>
					</script>
					</th>
				</tr>
			</thead>
		</table>
		<script id="tableButtonsTramitsTemplate" type="text/x-jsrender">
			<div class="botons-titol text-right">
				<a id="nou_tramit" class="btn btn-default" href="${expedientTipus.id}/integracioTramit/new" data-toggle="modal" data-callback="callbackModalTramits()" data-datatable-id="expedientTipusTramits"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.integracio.tramits.titol.nou"/></a>
			</div>
		</script>
		<script id="rowhrefTemplate" type="text/x-jsrender">${expedientTipus.id}/integracioTramit/update/{{:id}}</script>
				
		<hr>
		
		<form:form cssClass="form-horizontal" enctype="multipart/form-data" method="post" commandName="expedientTipusIntegracioTramitsCommand">
			<div>        
				<input type="hidden" id="id" name="id" value="${expedientTipusIntegracioTramitsCommand.id}"/>
				
				<hel:inputCheckbox name="notificacionsActivades" textKey="expedient.tipus.integracio.tramits.notificacions.activar"/>
				<div id="inputs_integracioNotificacios" style="display:${expedientTipusIntegracioTramitsCommand.notificacionsActivades? 'inline' : 'none'}">
					<hel:inputText required="true" name="notificacioOrganCodi" textKey="expedient.tipus.integracio.notificacio.organCodi" />
					<hel:inputText required="true" name="notificacioOficinaCodi" textKey="expedient.tipus.integracio.notificacio.oficinaCodi" />
					<hel:inputText required="true" name="notificacioUnitatAdministrativa" textKey="expedient.tipus.integracio.notificacio.unitatAdministrativa" />
					<hel:inputText required="true" name="notificacioCodiProcediment" textKey="expedient.tipus.integracio.notificacio.codiProcediment" />
					<hel:inputText required="false" name="notificacioAvisTitol" textKey="expedient.tipus.integracio.notificacio.avisTitol" />
					<hel:inputTextarea required="false" name="notificacioAvisText" textKey="expedient.tipus.integracio.notificacio.avisText" />
					<hel:inputText required="false" name="notificacioAvisTextSms" textKey="expedient.tipus.integracio.notificacio.avisSmsText" />
					<hel:inputText required="false" name="notificacioOficiTitol" textKey="expedient.tipus.integracio.notificacio.oficiTitol" />
					<hel:inputTextarea required="false" name="notificacioOficiText" textKey="expedient.tipus.integracio.notificacio.oficiText" />
				</div>
				
				
				
			</div>
			
			<div id="modal-botons" class="well" style="text-align: right;">
				<span id="accioGuardarProcessant" style="display:none;">
					<span class="fa fa-spinner fa-spin fa-fw" title="<spring:message code="comu.processant"/>..."></span><span class="sr-only">&hellip;</span>
				</span>			
				<button class="btn btn-primary right" type="submit" name="accio" value="guardar">
					<span class="fa fa-save"></span> <spring:message code='comu.boto.guardar' />
				</button>
			</div>
				
		</form:form>
				
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
// <![CDATA[    
            
$(document).ready(function() {
	$('#notificacionsActivades', '#expedientTipusIntegracioTramitsCommand').change(function() {
		if ($(this).is(':checked')) {
			$('#inputs_integracioNotificacios').show();
		} else {
			$('#inputs_integracioNotificacios').hide();
		}
	});
	$('#expedientTipusIntegracioTramitsCommand').submit(function(e){
		$('#accioGuardarProcessant').show();
		$(this).webutilNetejarErrorsCamps();
		var url = $(this).attr('action'); 
	    $.ajax({
	           type: "POST",
	           url: url,
	           data: $(this).serialize(),
	           success: function(ajaxResponse)
	           {
	        	   if (ajaxResponse.estatError) {
	        		   $('#expedientTipusIntegracioTramitsCommand').webutilMostrarErrorsCamps(ajaxResponse.errorsCamps);
	        	   }
	           },
				complete: function(){
					webutilRefreshMissatges();
					$('#accioGuardarProcessant').hide();
				}
	         });
		e.preventDefault();
		return false;
	})
});

function refrescaTaula() {
	$('#expedientTipusTramits').webutilDatatable('refresh');
}

function callbackModalTramits() {
	webutilRefreshMissatges();
	refrescaTaula();
}

function callbackModalMapeig() {
	webutilRefreshMissatges();
	//alert('refrescar pestanya pels comptadors');
	carregaTab('#contingut-integracio-tramits');
}

// ]]>
</script>			
