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

		<form:form cssClass="form-horizontal" enctype="multipart/form-data" method="post" commandName="expedientTipusMetadadesNtiCommand">
			<div style="height: 400px">        
				<hel:inputCheckbox name="ntiActiu" textKey="expedient.tipus.metadades.nti.activar"/>
				<div id="inputs_metadadesNti">
					<hel:inputText name="organisme" textKey="expedient.tipus.metadades.nti.organisme" readonly="${not expedientTipusMetadadesNtiCommand.ntiActiu}"/>
					<hel:inputText name="classificacio" textKey="expedient.tipus.metadades.nti.classificacio" readonly="${not expedientTipusMetadadesNtiCommand.ntiActiu}"/>
					
					<hel:inputSelect required="false" emptyOption="true" name="ntiTipoFirma" textKey="expedient.tipus.metadades.nti.tipus.firma" optionItems="${ntiTipoFirma}" optionValueAttribute="codi" optionTextAttribute="valor"/>
					<hel:inputText name="ntiValorCsv" textKey="expedient.tipus.metadades.nti.valor.csv" readonly="${not (expedientTipusMetadadesNtiCommand.ntiActiu and expedientTipusMetadadesNtiCommand.ntiTipoFirma == 'CSV')}"/>
					<hel:inputText name="ntiDefGenCsv" textKey="expedient.tipus.metadades.nti.definicio.generacio.csv" readonly="${not (expedientTipusMetadadesNtiCommand.ntiActiu and expedientTipusMetadadesNtiCommand.ntiTipoFirma == 'CSV')}"/>
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
	$('#ntiActiu','#expedientTipusMetadadesNtiCommand').change(function() {
		if ($(this).is(':checked')) {
			$('#organisme').prop('readonly', false);
			$('#classificacio').prop('readonly', false);
			
			$('#ntiTipoFirma').prop('readonly', false);
			var data = $("#ntiTipoFirma option:selected").val();
			if(data == 'CSV') {
				$('#ntiValorCsv').prop('readonly', false);
				$('#ntiDefGenCsv').prop('readonly', false);
			}
		} else {
			$('#organisme').prop('readonly', true);
			$('#classificacio').prop('readonly', true);
			
			$('#ntiTipoFirma').prop('readonly', true);
			$('#ntiValorCsv').prop('readonly', true);
			$('#ntiDefGenCsv').prop('readonly', true);
		}
	})
	$('#ntiTipoFirma').on("change", function(e) {
		var data = $("#ntiTipoFirma option:selected").val();
		if(data == 'CSV') {
			$('#ntiValorCsv').prop('readonly', false);
			$('#ntiDefGenCsv').prop('readonly', false);
		} else {
			$('#ntiValorCsv').prop('readonly', true);
			$('#ntiDefGenCsv').prop('readonly', true);
		}
	});
	$('#ntiTipoFirma').select2().on('select2-selecting', function(e) {
	    if(!$('#ntiActiu').is(':checked')) {
	    	e.preventDefault();
	    	$(this).select2('close');
	    }
	});
	
	
	$('#expedientTipusMetadadesNtiCommand').submit(function(e){
		$('#accioGuardarProcessant').show();
		$(this).webutilNetejarErrorsCamps();
		var url = $(this).attr('action'); 
	    $.ajax({
	           type: "POST",
	           url: "/helium/v3/expedientTipus/${expedientTipus.id}/metadadesNti",
	           data: $(this).serialize(),
	           success: function(ajaxResponse)
	           {
	        	   if (ajaxResponse.estatError) {
	        		   $('#expedientTipusMetadadesNtiCommand').webutilMostrarErrorsCamps(ajaxResponse.errorsCamps);
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
