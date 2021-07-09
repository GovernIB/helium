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


<c:choose>
	<c:when test="${not empty expedientTipus}">

		<form:form cssClass="form-horizontal" enctype="multipart/form-data" method="post" commandName="expedientTipusIntegracioTramitsCommand">
			<div style="height: 400px">        
				<input type="hidden" id="id" name="id" value="${expedientTipusIntegracioTramitsCommand.id}"/>
				<hel:inputCheckbox name="actiu" textKey="expedient.tipus.integracio.tramits.activar"/>
				<div id="inputs_integracioTramits" style="display:${expedientTipusIntegracioTramitsCommand.actiu? 'inline' : 'none'}">
					<hel:inputText required="true" name="tramitCodi" textKey="expedient.tipus.integracio.tramits.tramitCodi" />
					<!-- Botons per obrir els formularis dels mapejos -->
					<div class="form-group">
						<label class="control-label col-xs-4" for="mapeigVariables">
							<spring:message code="expedient.tipus.integracio.tramits.mapeig.variables"></spring:message>
						</label>
						<div class="col-xs-8">
							<a id="mapeigVariables" href="${expedientTipus.id}/integracioTramits/mapeig/${tipusMapeigVariable}" data-toggle="modal" data-callback="callbackModalMapeig()" class="btn btn-default">
								<spring:message code="expedient.tipus.integracio.tramits.variables"/>&nbsp;<span class="badge">${variablesCount}</span>
							</a>
						</div>				
					</div>		
					<div class="form-group">
						<label class="control-label col-xs-4" for="mapeigDocuments">
							<spring:message code="expedient.tipus.integracio.tramits.mapeig.documents"></spring:message>
						</label>
						<div class="col-xs-8">
							<a id="mapeigDocuments" href="${expedientTipus.id}/integracioTramits/mapeig/${tipusMapeigDocument}" data-toggle="modal" data-callback="callbackModalMapeig()" class="btn btn-default">
								<spring:message code="expedient.tipus.integracio.tramits.documents"/>&nbsp;<span class="badge">${documentsCount}</span>
							</a>
						</div>				
					</div>					
					<div class="form-group">
						<label class="control-label col-xs-4" for="mapeigAdjunts">
							<spring:message code="expedient.tipus.integracio.tramits.mapeig.adjunts"></spring:message>
						</label>
						<div class="col-xs-8">
							<a id="mapeigAdjunts" href="${expedientTipus.id}/integracioTramits/mapeig/${tipusMapeigAdjunt}" data-toggle="modal" data-callback="callbackModalMapeig()" class="btn btn-default">
								<spring:message code="expedient.tipus.integracio.tramits.adjunts"/>&nbsp;<span class="badge">${adjuntsCount}</span>
							</a>
						</div>				
					</div>					
				</div>
				
				<hr>
				
				<%-- 
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
				--%>
				
				
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
	$('#actiu', '#expedientTipusIntegracioTramitsCommand').change(function() {
		if ($(this).is(':checked')) {
			$('#inputs_integracioTramits').show();
		} else {
			$('#inputs_integracioTramits').hide();
		}
	});
	$('#notificacionsActivades', '#expedientTipusIntegracioTramitsCommand').change(function() {
		if ($(this).is(':checked')) {
			$('#inputs_integracioNotificacios').show();
		} else {
			$('#inputs_integracioNotificacios').hide();
		}
	});
	$('#expedientTipusIntegracioTramitsCommand').submit(function(e){
		$('#contingut-alertes').empty();
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

function callbackModalMapeig() {
	webutilRefreshMissatges();
	//alert('refrescar pestanya pels comptadors');
	carregaTab('#contingut-integracio-tramits');
}

// ]]>
</script>			
