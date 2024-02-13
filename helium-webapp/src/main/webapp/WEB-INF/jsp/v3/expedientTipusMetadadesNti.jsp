<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/jsrender/1.0.0-rc.70/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script type="text/javascript">
// <![CDATA[            
$(document).ready(function() {
	$('#actiu', '#expedientTipusMetadadesNtiCommand').change(function() {
		if ($(this).is(':checked')) {
			$('#input_unitatOrganitzativa').hide();
			$('#clasificacion').prop('readonly', false);
			$('#serieDocumental').prop('readonly', false);
			$('#arxiuActiu').prop('disabled', false);
		} else {
			$('#input_unitatOrganitzativa').show();
			$('#clasificacion').prop('readonly', true);
			$('#serieDocumental').prop('readonly', true);
			$('arxiuActiu').prop('readonly', true);
			$('#arxiuActiu').prop('disabled', true);
		}
		
	});
	
	$('#procedimentComu', '#expedientTipusMetadadesNtiCommand').change(function() {
		if ($(this).is(':checked')) {
			$('#input_unitatOrganitzativa').hide();	
		} else {
			$('#input_unitatOrganitzativa').show();
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

function formatSelectUnitat(item) {
	alert(item);
	if (!item.id) {
	    return item.text;
	}
	if (item.data && item.data.estat=="V"){
		return item.text;
	} else {
		return $("<span>" + item.text + " <span class='fa fa-exclamation-triangle text-warning' title=\"<spring:message code='unitat.organitzativa.avis.obsoleta'/>\"></span></span>");
	}
}

// ]]>
</script>
<c:choose>
	<c:when test="${not empty expedientTipus}">
		<form:form cssClass="form-horizontal" enctype="multipart/form-data" method="post" commandName="expedientTipusMetadadesNtiCommand">
			<div style="height: 400px">        
				<hel:inputCheckbox name="actiu" textKey="expedient.tipus.metadades.nti.actiu"/>
				<div id="inputs_metadadesNti">
					<div id="input_clasificacio">
						<hel:inputSuggest 
							name="clasificacion" 
							urlConsultaInicial="/helium/v3/procediment/suggestInici" 
							urlConsultaLlistat="/helium/v3/procediment/suggest" 
							placeholderKey="expedient.tipus.metadades.nti.clasificacion"
							textKey="expedient.tipus.metadades.nti.clasificacion" 
							required="true"
							disabled="${not expedientTipusMetadadesNtiCommand.actiu}"
							/>	
					</div>	
					<hel:inputCheckbox name="procedimentComu" textKey="expedient.tipus.metadades.nti.procediment.comu"/>
					
					
					<div id="input_unitatOrganitzativa" style="display:${!expedientTipus.procedimentComu ? 'inline' : 'none'}">

						<!-- Informació de la unitat organitzativa si no està vigent. -->
						<c:if test="${unitatOrganitzativaError != null }">
	
							<div class="row">
								<div class="col-sm-4"></div>
								<div class="col-sm-8">
									<div class="panel panel-danger" id="unitatOrganitzativaErrorDiv">
										<div class="panel-heading">
											<span class="fa fa-warning text-danger"></span>
											${unitatOrganitzativaError} 
										</div>
										<div class="panel-body">
											<div class="row">
												<label class="col-xs-4 text-right">Etiqueta</label>
												<div class="col-xs-8">
													<ul style="padding-left: 17px;" id="lastHistoricosUnitats">
														<li>Detalls errors</li>
													</ul>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						
						</c:if>
						<!-- Fi informació de la unitat organitzativa si no està vigent. -->
					
						<hel:inputSuggest 
							name="organo" 
							urlConsultaInicial="/helium/v3/unitatOrganitzativa/suggestInici" 
							urlConsultaLlistat="/helium/v3/unitatOrganitzativa/suggest" 
							textKey="expedient.tipus.metadades.nti.organo" 
							placeholderKey="expedient.tipus.metadades.nti.organo.placeholder" 
							optionTemplateFunction="formatSelectUnitat" 
						/>
					</div>	
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
