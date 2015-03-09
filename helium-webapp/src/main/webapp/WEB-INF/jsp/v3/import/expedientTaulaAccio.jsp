<c:set var="accio" value="${dada}"/>
<c:if test="${index == 0}">
<table id="accio_${accio.id}" class="table tableaccio table-bordered">
	<thead>
		<tr>
			<th><spring:message code="expedient.accio.nom"/></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
</c:if>		
	<tr>
		<td>${accio.nom}</td>
		<td class="accio_options">			
			<button type="button" class="btn btn-default" onclick="<c:url value="/v3/expedient/${expedient.id}/accio/${accio.id}"/>">
				<span class="fa fa-bolt"></span> <spring:message code="expedient.info.accio.executar"/>
			</button>				
		</td>
	</tr>		
<c:if test="${index == (paramCount-1)}">
	</tbody>
</table>
</c:if>
