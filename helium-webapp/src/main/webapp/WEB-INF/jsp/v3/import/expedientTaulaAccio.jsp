<c:set var="accio" value="${dada}"/>
<c:if test="${index == 0}">
<table id="accio_${accio.id}" class="table tableaccio table-bordered">
	<thead>
		<tr>
			<th><spring:message code="expedient.accio.nom"/></th>
			<th width="10%"></th>
		</tr>
	</thead>
	<tbody>
</c:if>		
	<tr>
		<td>${accio.nom}</td>
		<td id="accio_options_${accio.id}">			
			<a 	class="btn btn-primary" 
				data-rdt-link-confirm="<spring:message code='expedient.info.confirm.accio.executar'/>"
				data-rdt-link-ajax=true
				href='<c:url value="/ajax/v3/expedient/${expedient.id}/accio/${accio.id}"/>'
				data-rdt-link-callback="recargarPanel(${procesId});">
				<span class="fa fa-bolt"></span> <spring:message code="expedient.info.accio.executar"/>
			</a>
			<script type="text/javascript">
				// <![CDATA[
					$(document).ready(function() {
						$('#accio_options_${accio.id} > a').heliumEvalLink({
							refrescarAlertes: true,
							refrescarPagina: false,
							alertesRefreshUrl: "<c:url value="/nodeco/v3/missatges"/>"
						});
					});
				//]]>
			</script>	
		</td>
	</tr>		
<c:if test="${index == (paramCount-1)}">
	</tbody>
</table>
</c:if>
