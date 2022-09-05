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
	$('#pinbalActiu', '#expedientTipusIntegracioPinbalCommand').change(function() {
		if ($(this).is(':checked')) {
			$('#pinbalNifCif').prop('readonly', false);
			$('#pinbalActiu').prop('disabled', false);
		} else {
			$('#pinbalNifCif').prop('readonly', true);
			$('pinbalActiu').prop('readonly', true);
		}
	});
	$('#expedientTipusIntegracioPinbalCommand').submit(function(e) {
		$('#accioGuardarProcessant').show();
		webutilRefreshMissatges();
		$(this).webutilNetejarErrorsCamps();
		var url = $(this).attr('action'); 
		$.ajax({
			type: "POST",
			url: "/helium/v3/expedientTipus/${expedientTipus.id}/integracioPinbal",
			data: $(this).serialize(),
			success: function(ajaxResponse) {
				if (ajaxResponse.estatError) {
					$('#expedientTipusIntegracioPinbalCommand').webutilMostrarErrorsCamps(ajaxResponse.errorsCamps);
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
		<form:form cssClass="form-horizontal" enctype="multipart/form-data" method="post" commandName="expedientTipusIntegracioPinbalCommand">
			<div style="height: 400px">        
				<hel:inputCheckbox name="pinbalActiu" textKey="expedient.tipus.intergracio.pinbal.actiu"/>
				<div id="inputs_integracioPinbal">
					<hel:inputText name="pinbalNifCif" required="true" textKey="expedient.tipus.integracio.pinbal.pinbalNifCif" readonly="${not expedientTipusIntegracioPinbalCommand.pinbalActiu}"/>
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
