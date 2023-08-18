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

		<form:form cssClass="form-horizontal" enctype="multipart/form-data" method="post" commandName="expedientTipusIntegracioDistribucioCommand">
			<div>
				<input type="hidden" id="id" name="id" value="${expedientTipusIntegracioDistribucioCommand.id}"/>
				<hel:inputCheckbox name="actiu" textKey="expedient.tipus.integracio.distribucio.activar"/>
				<div id="inputs_integracioDistribucio" style="display:${expedientTipusIntegracioDistribucioCommand.actiu? 'inline' : 'none'}">
					<hel:inputText name="codiProcediment" textKey="expedient.tipus.integracio.distribucio.codiProcediment" />
					<div class="col-xs-4">
						<a id="addRuleBtn" class="btn btn-primary">
							<span id="accioAddRuleSpin" class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.crearRegla"/>
						</a>
					</div>
					<hel:inputText name="codiAssumpte" textKey="expedient.tipus.integracio.distribucio.codiAssumpte" />
					<hel:inputCheckbox name="procesAuto" textKey="expedient.tipus.integracio.distribucio.procesAuto" info="expedient.tipus.integracio.distribucio.procesAuto.comment"/>
					<hel:inputCheckbox name="sistra" textKey="expedient.tipus.integracio.distribucio.sistra" info="expedient.tipus.integracio.distribucio.sistra.comment"/>
					<hel:inputSelect name="presencial" textKey="expedient.tipus.integracio.distribucio.presencial" optionItems="${sino}" optionTextAttribute="text" optionValueAttribute="value"  />
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
	
	// Redimensiona controls
	$("input[name='codiProcediment']").parent('div').removeClass('col-xs-8').addClass('col-xs-4').parent(".form-group").append($('#addRuleBtn').parent());
	$("input[name='codiAssumpte']").parent('div').removeClass('col-xs-8').addClass('col-xs-4');
	$("select[name='presencial']").parent('div').removeClass('col-xs-8').addClass('col-xs-4');
	$('#s2id_presencial').css('width', '100%');

	$('#actiu','#expedientTipusIntegracioDistribucioCommand').change(function() {
		if ($(this).is(':checked')) {
			$('#inputs_integracioDistribucio').show();
		} else {
			$('#inputs_integracioDistribucio').hide();
		}
	})
	$('#expedientTipusIntegracioDistribucioCommand').submit(function(e){
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
	        		   $('#expedientTipusIntegracioDistribucioCommand').webutilMostrarErrorsCamps(ajaxResponse.errorsCamps);
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
	
	$('#addRuleBtn').click(function(e){
		$('#contingut-alertes').empty();
		$('#accioAddRuleSpin').addClass('fa-spin');
		$(this).webutilNetejarErrorsCamps();
		var url = "<c:url value="/v3/expedientTipus/${expedientTipus.id}/integracioDistribucio/addRule"></c:url>";
	    $.ajax({
	           type: "POST",
	           url: url,
	           data: {
	        	   		codiProcediment: $('#codiProcediment').val(),
				   		presencial: $('#presencial').val()
	        	   	  },
	           success: function(ajaxResponse)
	           {
	        	   if (ajaxResponse.estatError) {
	        		   $('#guardarRegla').webutilMostrarErrorsCamps(ajaxResponse.errorsCamps);
	        	   }
	           },
				complete: function(){
					webutilRefreshMissatges();
					$('#accioAddRuleSpin').removeClass('fa-spin');
				}
	         });
		e.preventDefault();
		return false;
	})
	
});
// ]]>
</script>			
