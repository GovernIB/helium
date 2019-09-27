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

		<form:form cssClass="form-horizontal" enctype="multipart/form-data" method="post" commandName="expedientTipusIntegracioFormsCommand">
			<div style="height: 400px">        
				<input type="hidden" id="id" name="id" value="${expedientTipusIntegracioFormsCommand.id}"/>
				<hel:inputCheckbox name="actiu" textKey="expedient.tipus.integracio.forms.activar"/>
				<div id="inputs_integracioForms" style="display:${expedientTipusIntegracioFormsCommand.actiu? 'inline' : 'none'}">
					<hel:inputText required="true" name="url" textKey="expedient.tipus.integracio.forms.url" />
					<hel:inputText name="usuari" textKey="expedient.tipus.integracio.forms.usuari"  placeholderKey="expedient.tipus.integracio.forms.noaut"/>
					<hel:inputText name="contrasenya" textKey="expedient.tipus.integracio.forms.contrasenya" placeholderKey="expedient.tipus.integracio.forms.noaut" />
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
	$('#actiu','#expedientTipusIntegracioFormsCommand').change(function() {
		if ($(this).is(':checked')) {
			$('#inputs_integracioForms').show();
		} else {
			$('#inputs_integracioForms').hide();
		}
	})
	$('#expedientTipusIntegracioFormsCommand').submit(function(e){
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
	        		   $('#expedientTipusIntegracioFormsCommand').webutilMostrarErrorsCamps(ajaxResponse.errorsCamps);
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
// ]]>
</script>			
