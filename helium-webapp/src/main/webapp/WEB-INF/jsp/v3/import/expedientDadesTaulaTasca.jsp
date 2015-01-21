<c:set var="tasca" value="${dada}"/>

<c:if test="${hiHaPendents gt 0 and tasca.oberta}">
	<c:if test="${contHiHaPendents == 0}">
	<td class="dadesTaulaTasca"><tr class="dadesTaulaTascaTr"><td class="dadesTaulaTascaTd" colspan="3">
	<table id="tasques-pendents-meves" class="dataTable table table-bordered table-hover">
		<thead>
			<tr>
				<th><spring:message code="expedient.tasca.columna.tasca"/></th>
				<th><spring:message code="expedient.tasca.columna.asignada_a"/></th>
				<th><spring:message code="expedient.tasca.columna.datcre"/></th>
				<th><spring:message code="expedient.tasca.columna.datlim"/></th>
				<th></th>
			</tr>
		</thead>
		<tbody>
	</c:if>	
	<c:set var="contHiHaPendents" value="${contHiHaPendents + 1}"/>	
	<tr>
		<td>
			${tasca.titol}
			<c:set var="ocultarAgafar" value="false"/>
			<c:choose>
				<c:when test="${not empty tasca.responsables && not tasca.agafada}">
					<span class="fa fa-users"></span>
					<c:forEach var="responsable" items="${tasca.responsables}" varStatus="status">
						<c:if test="${responsable.codi != dadesPersona.codi}">
							<c:set var="ocultarAgafar" value="true"/>
						</c:if>
					</c:forEach>
				</c:when>
				<c:when test="${tasca.responsableCodi != dadesPersona.codi}">
					<c:set var="ocultarOpciones" value="true"/>	
				</c:when>
			</c:choose>
			<div class="pull-right">
				<c:if test="${tasca.cancelada}">
					<span class="label label-danger" title="<spring:message code="enum.tasca.etiqueta.CA"/>">CA</span>
				</c:if>
				<c:if test="${tasca.suspesa}">
					<span class="label label-info" title="<spring:message code="enum.tasca.etiqueta.SU"/>">SU</span>
				</c:if>
				<c:if test="${tasca.oberta}">
					<span class="label label-warning" title="<spring:message code="enum.tasca.etiqueta.PD"/>">PD</span>
				</c:if>
				<c:if test="${tasca.completed}">
					<span class="label label-success" title="<spring:message code="enum.tasca.etiqueta.FI"/>">FI</span>
				</c:if>
				<c:if test="${tasca.agafada}">
					<span class="label label-default" title="<spring:message code="enum.tasca.etiqueta.AG"/>">AG</span>
				</c:if>
				<c:if test="${tasca.tramitacioMassiva}">
					<span class="label label-default" title="<spring:message code="tasca.llistat.accio.tramitar_massivament"/>"><i class="fa fa-files-o"></i></span>
				</c:if>
			</div>
		</td>
		<td><c:if test="${not empty tasca.responsable}">${tasca.responsable.nomSencer}</c:if></td>
		<td><fmt:formatDate value="${tasca.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></td>
		<td><fmt:formatDate value="${tasca.dataLimit}" pattern="dd/MM/yyyy"/></td>		 
		<td>
			<div class="btn-group">
				<a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/> <span class="caret"></span></a>
				<ul class="dropdown-menu">
					<c:if test="${tasca.oberta and not tasca.suspesa}">
						<c:if test="${tasca.responsableCodi == dadesPersona.codi}">
							<li><a href="../../v3/expedient/${expedient.id}/tasca/${tasca.id}" class="icon" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true"><span class="fa fa-folder-open"></span> <spring:message code="tasca.llistat.accio.tramitar"/></a></li>
							<c:if test="${tasca.tramitacioMassiva}">
								<li><a href="../../v3/tasca/${tasca.id}/massiva"><span class="fa fa-files-o"></span> <spring:message code="tasca.llistat.accio.tramitar_massivament"/></a></li>
							</c:if>
								<li><a href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/delegar"/>" class="icon" data-rdt-link-modal="true"><span class="fa fa-hand-o-right"></span> <spring:message code="tasca.llistat.accio.delegar"/></a></li>
						</c:if>
						<c:if test="${not empty tasca.responsables && not tasca.agafada && not ocultarAgafar}">
							<li><a href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/tascaAgafar"/>"><span class="fa fa-chain"></span> Agafar</a></li>
						</c:if>
						<c:if test="${expedient.permisSupervision}"><li><a href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/suspendre"/>" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.suspendre"/>" data-rdt-link-modal="true"><span class="fa fa-pause"></span> <spring:message code="tasca.llistat.accio.suspendre"/></a></li></c:if>
					</c:if>
					<c:if test="${tasca.oberta}">
						<c:if test="${expedient.permisReassignment}"><li><a href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/reassignar"/>" class="icon" data-rdt-link-modal="true"><span class="fa fa-share-square-o"></span> <spring:message code="tasca.llistat.accio.reassignar"/></a></li></c:if>
					</c:if>
					<c:if test="${tasca.suspesa}">
						<c:if test="${expedient.permisSupervision}"><li><a href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/reprendre"/>" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.reprendre"/>"><span class="fa fa-play"></span> <spring:message code="tasca.llistat.accio.reprendre"/></a></li></c:if>
					</c:if>
					<c:if test="${not tasca.cancelada}">
						<c:if test="${expedient.permisSupervision}"><li><a href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/cancelar"/>" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.cancelar"/>"><span class="fa fa-times"></span> <spring:message code="tasca.llistat.accio.cancelar"/></a></li></c:if>
					</c:if>
					<c:if test="${not empty tasca.responsables && tasca.responsableCodi == dadesPersona.codi and tasca.oberta}">
						<c:if test="${expedient.permisSupervision}"><li><a href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/tascaAlliberar"/>" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.alliberar"/>"><span class="fa fa-chain-broken"></span> <spring:message code="tasca.llistat.accio.alliberar"/></a></li></c:if>
					</c:if>													
				</ul>
			</div>
		</td>
	</tr>
	<c:if test="${contHiHaPendents == hiHaPendents}">
		</tbody>
	</table>
	
	<script type="text/javascript">
	// <![CDATA[			
	$(document).ready(function() {
		<c:if test="${procesId != inicialProcesInstanceId}">
			$('#${procesId}-titol-tasques').click();
			var icona = $('#${procesId}-titol-tasques').find('.icona-collapse');
			icona.toggleClass('fa-chevron-down');
			icona.toggleClass('fa-chevron-up');
		</c:if>
	});
	//]]>
	</script>
	</td></tr></td>
	</c:if>
</c:if>

<c:if test="${hiHaNoPendents gt 0 and not tasca.oberta}">
	<c:if test="${contHiHaNoPendents == 0}">
	<td class="dadesTaulaTasca" colspan="5"><tr class="dadesTaulaTascaTr"><td class="dadesTaulaTascaTd td-finalitzats" colspan="5">
	<div class="panel panel-default">
	<div id="${procesId}-titol-tasques-finalitzats" class="panel-heading clicable grup tauladades" data-toggle="collapse" data-target="#${procesId}-tasques-finalitzats">
		<spring:message code="expedient.tasca.grup.finalitzades"/>
		<span class="badge">${hiHaNoPendents}</span>
		<div class="pull-right"><span class="icona-collapse fa fa-chevron-down"></span></div>
	</div>
	<div id="${procesId}-tasques-finalitzats" class="clear collapse panel-body-grup">
	<table class="table table-bordered">
		<thead>
			<tr>
				<th><spring:message code="expedient.tasca.columna.tasca"/></th>
				<th><spring:message code="expedient.tasca.columna.responsable"/></th>
				<th><spring:message code="expedient.tasca.columna.datcre"/></th>
				<th><spring:message code="expedient.tasca.columna.datfi"/></th>
			</tr>
		</thead>
		<tbody>
	</c:if>
	<c:set var="contHiHaNoPendents" value="${contHiHaNoPendents + 1}"/>	
	<tr>
		<td>${tasca.titol}</td>
		<td>
			<c:choose>
				<c:when test="${not empty tasca.responsable}">${tasca.responsable.nomSencer}</c:when>
				<c:when test="${not empty tasca.responsables}">
					<c:forEach var="responsable" items="${tasca.responsables}" varStatus="status">
						${responsable.nomSencer}<c:if test="${not status.last}">, </c:if>
					</c:forEach>
				</c:when>
				<c:otherwise></c:otherwise>
			</c:choose>
		</td>
		<td><fmt:formatDate value="${tasca.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></td>
		<td><fmt:formatDate value="${tasca.dataFi}" pattern="dd/MM/yyyy HH:mm"/></td>
	</tr>
	<c:if test="${contHiHaNoPendents == hiHaNoPendents}">
		</tbody>
	</table>
	</div></div>

	<script type="text/javascript">
	// <![CDATA[			
	$(document).ready(function() {
		$('#${procesId}-tasques-finalitzats').on('shown.bs.collapse', function() {
			$('#${procesId}-titol-tasques-finalitzats .icona-collapse').toggleClass('fa-chevron-down');
			$('#${procesId}-titol-tasques-finalitzats .icona-collapse').toggleClass('fa-chevron-up');
		});
		$('#${procesId}-tasques-finalitzats').on('hidden.bs.collapse', function() {
			$('#${procesId}-titol-tasques-finalitzats .icona-collapse').toggleClass('fa-chevron-down');
			$('#${procesId}-titol-tasques-finalitzats .icona-collapse').toggleClass('fa-chevron-up');
		});
	});
	//]]>
	</script></td></tr></td>
	</c:if>
</c:if>

<c:if test="${not (hiHaPendents gt 0 and tasca.oberta) and contHiHaPendents == 0}">
	<script type="text/javascript">
	// <![CDATA[			
	$(document).ready(function() {
		$('#${procesId}-titol-tasques').click();
	});
	//]]>
	</script>
</c:if>
