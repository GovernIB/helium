<td id="cela-${procesId}-${dada.varCodi}"<c:if test="${dadaTipusRegistre}"> colspan="${paramNumColumnes}"</c:if><c:if test="${dada.campOcult}"> class="campOcult"</c:if><c:if test="${not empty dada.error}"> style="background-color:#f2dede"</c:if>>
	<address class=var_dades>
		<c:if test="${dada.campOcult}"><span class="fa fa-eye-slash"></span></c:if>
		<label class="<c:if test="${dada.required}">obligatori</c:if>">${dada.campEtiqueta}</label><br/>
		<c:choose>
			<c:when test="${dadaTipusRegistre}">
				<c:set var="registreFilesTaula" value="${dada.dadesRegistrePerTaula}"/>
				<table class="table table-bordered table-condensed marTop6">
					<thead>
						<tr>
							<c:forEach var="dadaFila0" items="${registreFilesTaula[0].registreDades}">
								<th>${dadaFila0.campEtiqueta}</th>
							</c:forEach>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="registreFila" items="${registreFilesTaula}">
							<tr>
								<c:forEach var="registreDada" items="${registreFila.registreDades}">
									<td>${registreDada.text}</td>
								</c:forEach>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<c:if test="${not empty dada.varValor}">
					<c:choose>
						<c:when test="${not empty dada.error}"><strong><i class="icon-warning-sign" title="${dada.error}"></i></strong></c:when>
						<c:otherwise><strong>${dada.textMultiple}</strong></c:otherwise>
					</c:choose>
				</c:if>
			</c:otherwise>
		</c:choose>
	</address>
	<c:if test="${isAdmin}">
	<div class=var_botons>
		<a 	class="var-edit" 
			data-rdt-link-modal=true
			href='<c:url value="../../v3/expedient/${expedientId}/dades/${procesId}/edit/${dada.varCodi}"/>' 
			data-rdt-link-callback="reestructura(${procesId});"
			data-rdt-link-modal-min-height="300">
			<span class="fa fa-pencil"></span>
		</a>
		<a 	class="var-delete" 
			data-rdt-link-confirm="<spring:message code='expedient.info.confirm.dada.esborrar'/>"
			data-rdt-link-ajax=true
			href='<c:url value="../../v3/expedient/${expedientId}/dades/${procesId}/delete/${dada.varCodi}"/>' 
			data-rdt-link-callback="reestructura(${procesId});">
			<span class="fa fa-trash-o"></span>
		</a>
	</div>
	</c:if>
</td>