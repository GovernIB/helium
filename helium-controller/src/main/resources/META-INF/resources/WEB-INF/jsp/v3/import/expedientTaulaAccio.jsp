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
			<a  class="btn_accio btn btn-primary" 
				href='<c:url value="/v3/expedient/${expedient.id}/proces/${procesId}/accio/${accio.id}/executar"/>'>
				<span class="fa fa-bolt"></span> <spring:message code="expedient.info.accio.executar"/>
			</a>
		</td>
	</tr>		
<c:if test="${index == (paramCount-1)}">
	</tbody>
</table>
</c:if>
