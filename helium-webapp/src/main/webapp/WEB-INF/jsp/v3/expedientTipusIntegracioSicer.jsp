<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<script src="<c:url value="/js/webutil.common.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>


<c:choose>
	<c:when test="${not empty expedientTipus}">

		<form:form cssClass="form-horizontal" enctype="multipart/form-data" method="post" commandName="expedientTipusIntegracioSicerCommand">
			<div>        
				<input type="hidden" id="id" name="id" value="${expedientTipusIntegracioSicerCommand.id}"/>
				
				<hel:inputCheckbox name="sicerIntegracioActiva" textKey="expedient.tipus.integracio.sicer.activar"/>
				<div id="inputs_integracioSicer" style="display:${expedientTipusIntegracioSicerCommand.sicerIntegracioActiva? 'inline' : 'none'}">
					<hel:inputText required="false" name="sicerProducteCodi" textKey="expedient.tipus.integracio.sicer.sicerProducteCodi" />
					<hel:inputText required="false" name="sicerClientCodi" textKey="expedient.tipus.integracio.sicer.sicerClientCodi" />
					<hel:inputText required="false" name="sicerPuntAdmissioCodi" textKey="expedient.tipus.integracio.sicer.sicerPuntAdmissioCodi" />
					<hel:inputText required="false" name="sicerNomLlinatges" textKey="expedient.tipus.integracio.sicer.sicerNomLlinatges" />
					<hel:inputText required="false" name="sicerDireccio" textKey="expedient.tipus.integracio.sicer.sicerDireccio" />
					<hel:inputText required="false" name="sicerPoblacio" textKey="expedient.tipus.integracio.sicer.sicerPoblacio" />
					<hel:inputText required="false" name="sicerCodiPostal" textKey="expedient.tipus.integracio.sicer.sicerCodiPostal" />
					<hel:inputText required="false" name="sicerSftpUser" textKey="expedient.tipus.integracio.sicer.sicerSftpUser" />
					<hel:inputText required="false" name="sicerSftpPassword" textKey="expedient.tipus.integracio.sicer.sicerSftpPassword" />
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
	$('#sicerIntegracioActiva', '#expedientTipusIntegracioSicerCommand').change(function() {
		if ($(this).is(':checked')) {
			$('#inputs_integracioSicer').show();
		} else {
			$('#inputs_integracioSicer').hide();
		}
	});
	$('#expedientTipusIntegracioSicerCommand').submit(function(e){
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
	        		   $('#expedientTipusIntegracioSicerCommand').webutilMostrarErrorsCamps(ajaxResponse.errorsCamps);
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
