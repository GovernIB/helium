<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<script type="text/javascript">
// <![CDATA[            
$(document).ready(function() {
	$('#actiu', '#expedientTipusMetadadesNtiCommand').change(function() {
		if ($(this).is(':checked')) {
			$('#organo').prop('readonly', false);
			$('#clasificacion').prop('readonly', false);
			$('#serieDocumental').prop('readonly', false);
			$('#arxiuActiu').prop('disabled', false);
		} else {
			$('#organo').prop('readonly', true);
			$('#clasificacion').prop('readonly', true);
			$('#serieDocumental').prop('readonly', true);
			$('arxiuActiu').prop('readonly', true);
			$('#arxiuActiu').prop('disabled', true);
		}
	});
	$('#expedientTipusMetadadesNtiCommand').submit(function(e) {
		$('#accioGuardarProcessant').show();
		webutilRefreshMissatges();
		$(this).webutilNetejarErrorsCamps();
		var url = $(this).attr('action'); 
		$.ajax({
			type: "POST",
			url: "/helium/v3/expedientTipus/${expedientTipus.id}/metadadesNti",
			data: $(this).serialize(),
			success: function(ajaxResponse) {
				if (ajaxResponse.estatError) {
					$('#expedientTipusMetadadesNtiCommand').webutilMostrarErrorsCamps(ajaxResponse.errorsCamps);
				}
			},
			complete: function() {
				webutilRefreshMissatges();
				$('#accioGuardarProcessant').hide();
			}
		});
		e.preventDefault();
		return false;
	});
});
// ]]>
</script>
<c:choose>
	<c:when test="${not empty expedientTipus}">
		<form:form cssClass="form-horizontal" enctype="multipart/form-data" method="post" commandName="expedientTipusMetadadesNtiCommand">
			<div style="height: 400px">        
				<hel:inputCheckbox name="actiu" textKey="expedient.tipus.metadades.nti.actiu"/>
				<div id="inputs_metadadesNti">
					<hel:inputText name="organo" textKey="expedient.tipus.metadades.nti.organo" readonly="${not expedientTipusMetadadesNtiCommand.actiu}"/>
					<hel:inputText name="clasificacion" textKey="expedient.tipus.metadades.nti.clasificacion" readonly="${not expedientTipusMetadadesNtiCommand.actiu}"/>
					<hel:inputText name="serieDocumental" textKey="expedient.tipus.metadades.nti.serie.documental" readonly="${not expedientTipusMetadadesNtiCommand.actiu}"/>
					<hel:inputCheckbox name="arxiuActiu" textKey="expedient.tipus.metadades.nti.arxiu.actiu" disabled="${not expedientTipusMetadadesNtiCommand.actiu}"/>
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
