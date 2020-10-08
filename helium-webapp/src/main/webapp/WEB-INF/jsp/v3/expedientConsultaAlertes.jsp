<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

	<c:if test="${errorsReindexacio > 0}">
		<div class="alert alert-warning ">
			<div id="alertaErrorReindexacio" data-toggle="collapse" data-target="#collapseErrorsReindexacio" title="<spring:message code="expedient.consulta.alerta.errorsReindexacio.tip"/>" style="cursor: pointer;">
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
		</div>
		
		<script type="text/javascript">
			var carregat = false;
			$('#alertaErrorReindexacio').click(function(){
				var icona = $(this).find('.icona-collapse');
				icona.toggleClass('fa-chevron-down');
				icona.toggleClass('fa-chevron-up');
				if (!carregat) {
					$('#collapseErrorsReindexacio').load('<c:url value="/nodeco/v3/expedient/consulta/${consultaId}/expedientsErrorsReindexacio"/>');
					carregat = true;
				}
			});
		</script>
	</c:if>

	<c:if test="${pendentsReindexacio > 0}">
		<div class="alert alert-warning">
			<span class="fa fa-refresh"></span>
			<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>
			Actualment hi ha ${pendentsReindexacio} expedients pendents de reindexació que podrien afectar al resultat de la consulta.
		</div>
	</c:if>