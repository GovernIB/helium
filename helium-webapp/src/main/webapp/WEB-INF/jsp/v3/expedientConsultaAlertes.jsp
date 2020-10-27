<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

	<c:set var="idioma">ca</c:set>
	
	<c:if test="${errorsReindexacio > 0}">
		<div class="alert alert-warning ">
			<div id="alertaErrorsReindexacio" data-toggle="collapse" data-target="#collapseErrorsReindexacio" title="<spring:message code="expedient.consulta.alerta.errorsReindexacio.tip"/>" style="cursor: pointer;">
				<span class="fa fa-refresh text-danger"></span>
				<button type="button" class="close-alertes" aria-hidden="true"><span class="icona-collapse fa fa-chevron-down"></span></button>
				<spring:message code="expedient.consulta.alerta.errorsReindexacio" arguments="${errorsReindexacio}"/>				
			</div>
				<div id="collapseErrorsReindexacio" class="collapse" data-parent="#accordion">
					<div class="card-body" >
						<div class="contingut-carregant text-center"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
					</div>
				</div>		
		</div>
		
	<c:if test="${pendentsReindexacio > 0}">
		<div class="alert alert-warning ">
			<div id="alertaPendentsReindexacio" data-toggle="collapse" data-target="#collapsePendentsReindexacio" title="<spring:message code="expedient.consulta.alerta.pendentsReindexacio.tip"/>" style="cursor: pointer;">
				<span class="fa fa-refresh text-warning"></span>
				<button type="button" class="close-alertes" aria-hidden="true"><span class="icona-collapse fa fa-chevron-down"></span></button>
				<spring:message code="expedient.consulta.alerta.pendentsReindexacio" arguments="${pendentsReindexacio}"/>				
			</div>
				<div id="collapsePendentsReindexacio" class="collapse" data-parent="#accordion">
					<div class="card-body" >
						<div class="contingut-carregant text-center"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
					</div>
				</div>		
		</div>
		
	</c:if>

	<div id="alertaPendentsErrorsCarregant" style="display:none;">
		<div class="contingut-carregant text-center"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>
	</div>
	
		<script type="text/javascript">
			// Errors reindexació
			var errorsCarregats = false;
			$('#alertaErrorsReindexacio').click(function(){
				var icona = $(this).find('.icona-collapse');
				icona.toggleClass('fa-chevron-down');
				icona.toggleClass('fa-chevron-up');
				if (!errorsCarregats) {
					$('#collapseErrorsReindexacio').empty().html($('#alertaPendentsErrorsCarregant').html());
					$('#collapseErrorsReindexacio').load('<c:url value="/nodeco/v3/expedient/consulta/${consultaId}/expedientsErrorsReindexacio"/>', 
						function() {
							$('#collapseErrorsReindexacio').find('#taulaExpedients').DataTable({
								'oLanguage': {
									'sUrl': webutilContextPath() + '/js/datatables/i18n/datatables.ca.json'
								},
								'aaSorting': [[1, 'asc']]
							});
					});
					errorsCarregats = true;
				} else {
					errorsCarregats = false;	
				}
			});
			// Pendents reindexació
			var pendentsCarregats = false;
			$('#alertaPendentsReindexacio').click(function(){
				var icona = $(this).find('.icona-collapse');
				icona.toggleClass('fa-chevron-down');
				icona.toggleClass('fa-chevron-up');
				if (!pendentsCarregats) {
					$('#collapsePendentsReindexacio').empty().html($('#alertaPendentsErrorsCarregant').html());
					$('#collapsePendentsReindexacio').load('<c:url value="/nodeco/v3/expedient/consulta/${consultaId}/expedientsPendentsReindexacio"/>', function() {
						$('#collapsePendentsReindexacio').find('table').dataTable({
							'oLanguage': {
								'sUrl': webutilContextPath() + '/js/datatables/i18n/datatables.ca.json'
							},
							'aaSorting': [[1, 'asc']]
						});
					});
					pendentsCarregats = true;
				} else {
					pendentsCarregats = false;	
				}
			});
		</script>
	</c:if>