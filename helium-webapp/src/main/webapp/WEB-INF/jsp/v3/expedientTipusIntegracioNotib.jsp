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

<script type="text/javascript">
$(document).ready(function() {
	$('#notibActiu', '#expedientTipusIntegracioNotibCommand').change(function() {
		if ($(this).is(':checked')) {
			$('#inputs-notib').show();
		} else {
			$('#inputs-notib').hide();
		}
	});
	$('#expedientTipusIntegracioNotibCommand').submit(function(e){
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
	        		   $('#expedientTipusIntegracioNotibCommand').webutilMostrarErrorsCamps(ajaxResponse.errorsCamps);
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
</script>


<c:choose>
	<c:when test="${not empty expedientTipus}">

		<form:form cssClass="form-horizontal" enctype="multipart/form-data" method="post" commandName="expedientTipusIntegracioNotibCommand">
			<div>				
				<hel:inputCheckbox name="notibActiu" textKey="expedient.tipus.integracio.tramits.notificacions.activar"/>
				<div id="inputs-notib" style="display:${expedientTipusIntegracioNotibCommand.notibActiu? 'inline' : 'none'}">

					<hel:inputText required="true" name="notibEmisor" textKey="expedient.tipus.integracio.notib.organisme.emisor" readonly="${expedientTipusIntegracioNotibCommand.ntiActiu}"/>
					<hel:inputText required="true" name="notibCodiProcediment" textKey="expedient.tipus.integracio.notib.sia.codiprocediment"  readonly="${expedientTipusIntegracioNotibCommand.ntiActiu}"/>
					
					<hel:inputText required="true" name="notibSeuUnitatAdministrativa" textKey="expedient.tipus.integracio.notib.exp.unitatorganitzativa" />
					<hel:inputText required="true" name="notibSeuCodiProcediment" textKey="expedient.tipus.integracio.notib.sistra.codiprocediment" />
					<hel:inputText required="true" name="notibSeuOficina" textKey="expedient.tipus.integracio.notib.reg.oficina" />
					<hel:inputText required="true" name="notibSeuLlibre" textKey="expedient.tipus.integracio.notib.reg.llibre" />
					<hel:inputText required="true" name="notibSeuOrgan" textKey="expedient.tipus.integracio.notib.reg.organ" />
					
					<hel:inputSelect name="notibSeuIdioma" textKey="expedient.tipus.integracio.notib.idioma" placeholderKey="expedient.tipus.integracio.notib.idioma" optionItems="${seuIdioma}" optionTextAttribute="codi" optionValueAttribute="valor"/>
					
					<hel:inputText required="false" name="notibAvisTitol" textKey="expedient.tipus.integracio.notib.avis.titol" />
					<hel:inputTextarea required="false" name="notibAvisText" textKey="expedient.tipus.integracio.notib.avis.text" />
					<hel:inputText required="false" name="notibAvisTextSms" textKey="expedient.tipus.integracio.notib.avis.textmobil" />
					
					<hel:inputText required="false" name="notibOficiTitol" textKey="expedient.tipus.integracio.notib.ofici.titol" />
					<hel:inputTextarea required="false" name="notibOficiText" textKey="expedient.tipus.integracio.notib.ofici.text" />
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
