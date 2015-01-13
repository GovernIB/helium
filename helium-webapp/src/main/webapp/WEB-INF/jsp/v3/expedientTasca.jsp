<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:choose>
	<c:when test="${not empty tasques}">
		<c:set var="hiHaPendents" value="${false}"/>
		<c:set var="hiHaNoPendents" value="${false}"/>
		<c:forEach var="tasca" items="${tasques}">
			<c:if test="${tasca.oberta}"><c:set var="hiHaPendents" value="${true}"/></c:if>
			<c:if test="${not tasca.oberta}"><c:set var="hiHaNoPendents" value="${true}"/></c:if>
		</c:forEach>
		<c:if test="${hiHaPendents}">
			<c:set var="cont" value="0"/>
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
					<c:forEach var="tasca" items="${tasques}">
						<c:if test="${tasca.oberta}">
							<tr <c:if test="${tasca.responsableCodi == dadesPersona.codi and not tasca.suspesa}"> style="cursor: pointer"</c:if>>
								<td>
									${tasca.titol}
									<c:if test="${not empty tasca.responsables && not tasca.agafada}">
										<span class="fa fa-users"></span>
									</c:if>
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
													<li><a href="../../v3/expedient/${expedient.id}/tasca/${tasca.id}" data-rdt-link-modal="true" data-rdt-link-modal-maximize="true"><span class="fa fa-folder-open"></span> <spring:message code="tasca.llistat.accio.tramitar"/></a></li>
													<c:if test="${tasca.tramitacioMassiva}">
														<li><a href="../../v3/tasca/${tasca.id}/massiva"><span class="fa fa-files-o"></span> <spring:message code="tasca.llistat.accio.tramitar_massivament"/></a></li>
													</c:if>
				 									<li><a href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/delegar"/>" data-rdt-link-modal="true"><span class="fa fa-hand-o-right"></span> <spring:message code="tasca.llistat.accio.delegar"/></a></li>
												</c:if>
												<c:if test="${not empty tasca.responsables && not tasca.agafada}">
													<li><a href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/tascaAgafar"/>"><span class="fa fa-chain"></span> Agafar</a></li>
												</c:if>
												<c:if test="${expedient.permisReassignment}"><li><a href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/reassignar"/>" data-rdt-link-modal="true"><span class="fa fa-share-square-o"></span> <spring:message code="tasca.llistat.accio.reassignar"/></a></li></c:if>
												<c:if test="${expedient.permisSupervision}"><li><a href="<c:url value="../../v3/expedient/${expedient.id}/tasca/${tasca.id}/suspendre"/>" data-rdt-link-confirm="<spring:message code="expedient.tasca.confirmacio.suspendre"/>" data-rdt-link-modal="true"><span class="fa fa-pause"></span> <spring:message code="tasca.llistat.accio.suspendre"/></a></li></c:if>
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
						</c:if>
					</c:forEach>
				</tbody>
			</table>

			<script type="text/javascript">
				$("#tasques-pendents-meves a").heliumEvalLink({
					refrescarAlertes: true,
					refrescarPagina: false
				});
			</script>
		</c:if>
		<c:if test="${hiHaNoPendents}">
			<div class="well well-sm">
				<h4><spring:message code="expedient.tasca.grup.finalitzades"/></h4>
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
						<c:forEach var="tasca" items="${tasques}">
							<c:if test="${not tasca.oberta}">
								<tr>
									<td>${tasca.titol}</td>
									<td>
										<c:choose>
											<c:when test="${not empty tasca.responsable}"><!--A-->${tasca.responsable.nomSencer}</c:when>
											<c:when test="${not empty tasca.responsables}"><!--P-->
												<c:forEach var="responsable" items="${tasca.responsables}" varStatus="status">
													${responsable.nomSencer}<c:if test="${not status.last}">, </c:if>
												</c:forEach>
											</c:when>
											<c:otherwise><!--O--></c:otherwise>
										</c:choose>
									</td>
									<td><fmt:formatDate value="${tasca.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></td>
									<td><fmt:formatDate value="${tasca.dataFi}" pattern="dd/MM/yyyy HH:mm"/></td>
								</tr>
							</c:if>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</c:if>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code="expedient.tasca.nohiha"/></div>
	</c:otherwise>
</c:choose>
