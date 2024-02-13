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
						<hel:inputSuggest 
							name="codiProcediment" 
							urlConsultaInicial="/helium/v3/procediment/suggestInici" 
							urlConsultaLlistat="/helium/v3/procediment/suggest" 
							placeholderKey="expedient.tipus.integracio.distribucio.codiProcediment"
							textKey="expedient.tipus.integracio.distribucio.codiProcediment"
							info="expedient.tipus.integracio.distribucio.codiProcediment.comment"
						/>	
					<div class="col-xs-4">
						<!-- Botó per crear regles i altres opcions per consultar o canviar l'estat -->
						<div id="reglaBtn" class="btn-group">
						  <button id="crearReglaBtn" type="button" class="btn btn-primary">
						  	<span id="accioCrearReglaSpin" class="fa fa-cog"></span>&nbsp;<spring:message code="expedient.tipus.integracio.distribucio.crearActualitzarRegla"/>
						  </button>
						  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
						  	<span class="caret"></span>
						    <span class="sr-only">Toggle Dropdown</span>
						  </button>
						  <ul class="dropdown-menu">
						    <li>
								<a id="consultarReglaBtn" data-toggle="modal" href="<c:url value="/v3/expedientTipus/${expedientTipus.id}/integracioDistribucio/consultarRegla?codiProcediment=${expedientTipusIntegracioDistribucioCommand.codiProcediment}"/>">
									<span class="fa fa-search"></span>&nbsp;<spring:message code="expedient.tipus.integracio.distribucio.consultarRegla"/>
								</a>
							</li>
						    <li>
								<a id="canviEstatReglaBtn" href="#">
									<span class="fa fa-check"></span>&nbsp;<spring:message code="expedient.tipus.integracio.distribucio.canviEstatRegla"/>
									<span id="accioCaviEstatReglaSpin" class="fa fa-cog fa-spin text-secondary" style="visibility: hidden; color: gray;"></span>
								</a>
						    </li>
						  </ul>
						</div>
					</div>
					
					<hel:inputSelect name="presencial" textKey="expedient.tipus.integracio.distribucio.presencial" info="expedient.tipus.integracio.distribucio.presencial.comment" optionItems="${sino}" optionTextAttribute="text" optionValueAttribute="value"  />
					<hel:inputText name="codiAssumpte" textKey="expedient.tipus.integracio.distribucio.codiAssumpte" info="expedient.tipus.integracio.distribucio.codiAssumpte.comment"/>
					<hel:inputCheckbox name="procesAuto" textKey="expedient.tipus.integracio.distribucio.procesAuto" info="expedient.tipus.integracio.distribucio.procesAuto.comment"/>
					<hel:inputCheckbox name="sistra" textKey="expedient.tipus.integracio.distribucio.sistra" info="expedient.tipus.integracio.distribucio.sistra.comment"/>
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
	$("#codiProcediment").parent('div').removeClass('col-xs-8').addClass('col-xs-4').parent(".form-group").append($('#reglaBtn').parent());
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
	
	$('#crearReglaBtn').click(function(e){
		$('#contingut-alertes').empty();
		$('#accioCrearReglaSpin').addClass('fa-spin');
		$(this).webutilNetejarErrorsCamps();
		var url = "<c:url value="/v3/expedientTipus/${expedientTipus.id}/integracioDistribucio/crearRegla"></c:url>";
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
					$('#accioCrearReglaSpin').removeClass('fa-spin');
				}
	         });
		e.preventDefault();
		return false;
	})

	$('#canviEstatReglaBtn').click(function(e){
		$('#contingut-alertes').empty();
        $('#accioCaviEstatReglaSpin').css("visibility", "visible");
		$(this).webutilNetejarErrorsCamps();
		var url = "<c:url value="/v3/expedientTipus/${expedientTipus.id}/integracioDistribucio/canviEstatRegla"></c:url>";
	    $.ajax({
	           type: "POST",
	           url: url,
	           data: {
	        	   		codiProcediment: $('#codiProcediment').val()
	        	   	  },
	           success: function(ajaxResponse)
	           {
	        	   if (ajaxResponse.estatError) {
	        		   $('#guardarRegla').webutilMostrarErrorsCamps(ajaxResponse.errorsCamps);
	        	   }
	           },
				complete: function(){
					webutilRefreshMissatges();
		            $('#accioCaviEstatReglaSpin').css("visibility", "hidden");
				}
	         });
		e.preventDefault();
		return false;
	});
	
	$('#codiProcediment').change(function() {
		$('#consultarReglaBtn').attr('href', '<c:url value="/v3/expedientTipus/${expedientTipus.id}/integracioDistribucio/consultarRegla?codiProcediment="/>' + $(this).val());
	})	
});
// ]]>
</script>			
