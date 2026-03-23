<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<table>
<tbody>
<tr>
	<td colspan="6">
		<div class="row">
			<div class="col-xs-6">
					<dl class="dl-horizontal">
					<c:choose>
						<c:when test="${interessat.tipus == 'FISICA'}">
							<dt><spring:message code="interessat.form.camp.nom"/></dt><dd>${interessat.nom} ${interessat.llinatge1} ${interessat.llinatge2}</dd>
						</c:when>
						<c:otherwise>
							<dt><spring:message code="interessat.form.camp.rao.social"/></dt><dd>${interessat.raoSocial}</dd>
						</c:otherwise>
					</c:choose>
					<dt><spring:message code="interessat.form.camp.pais"/></dt><dd>${interessat.paisNom}</dd>
					<dt><spring:message code="interessat.form.camp.provincia"/></dt><dd>${interessat.provinciaNom}</dd>											
					<dt><spring:message code="interessat.form.camp.municipi"/></dt><dd>${interessat.municipiNom}</dd>
					<dt><spring:message code="interessat.form.camp.direccio"/></dt><dd>${interessat.direccio}</dd>
					<dt><spring:message code="interessat.form.camp.codipostal"/></dt><dd>${interessat.codiPostal}</dd>
					</dl>
			</div>
			<div class="col-xs-6">
					<dl class="dl-horizontal">
					<dt><spring:message code="interessat.form.camp.document"/></dt><dd>${interessat.documentIdent}</dd>
					<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${interessat.email}</dd>
					<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${interessat.telefon}</dd>
					<dt><spring:message code="interessat.form.camp.canal.notif"/></dt>	
						<dd>
							<c:if test="${not empty interessat.canalNotif}">
								<spring:message code="interessat.tipus.enum.canalnotif.${interessat.canalNotif}"/>
							</c:if>
						</dd>
					<c:if test="${not empty interessat.codiDire}">
						<dt><spring:message code="interessat.form.camp.codi.dire"/></dt><dd>${interessat.codiDire}</dd>
					</c:if>
					<c:if test="${not empty interessat.dir3Codi}">
						<dt><spring:message code="interessat.form.camp.dir3codi"/></dt><dd>${interessat.dir3Codi}</dd>
					</c:if>		
					<dt><spring:message code="interessat.form.camp.observacions"/></dt><dd>${interessat.observacions}</dd>
					</dl>
			</div>
			
			<!-- NOU APARTAT REPRESENTANT -->
			<c:if test="${not empty interessat.representant}">
				<c:set var="representant" value="${interessat.representant}"/>
				<div class="col-xs-12">
					<table class="table table-bordered">
						<thead>
							<tr>
								<th colspan="4"><spring:message code="expedient.document.notificar.form.camp.representant"/></th>
							</tr>
							<tr>
								<th style="width: 150px;"><spring:message code="interessat.form.camp.codi"/></th>
								<th style="width: 150px;"><spring:message code="interessat.llistat.columna.tipus"/></th>
								<th style="width: 150px;"><spring:message code="interessat.form.camp.document.identificatiu"/></th>
								<c:choose>
									<c:when test="${representant.tipus == 'FISICA'}">
										<th><spring:message code="interessat.form.camp.nom"/></th>
									</c:when>
									<c:otherwise>
										<th><spring:message code="interessat.form.camp.rao.social"/></th>
									</c:otherwise>
								</c:choose>
								<th style="width: 50px;"></th>
							</tr>
						</thead>
						<tbody>
							<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
								<td>
									${representant.codi}
								</td>
								<td>
									<spring:message code="interessat.form.tipus.enum.${representant.tipus}"/>
								</td>
								<td>${representant.documentIdent}</td>
								<c:choose>
									<c:when test="${representant.tipus == 'FISICA'}">
										<td>${representant.nom} ${representant.llinatge1} ${representant.llinatge2}</td>
									</c:when>
									<c:otherwise>
										<td>${representant.raoSocial}</td>
									</c:otherwise>
								</c:choose>
								<td>
									<c:if test="${representant.tipus != 'ADMINISTRACIO'}">
										<button type="button" class="btn btn-default desplegable" href="#detalls_${status.index}_rep" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}_rep">
											<span class="fa fa-caret-down"></span>
										</button>
									</c:if>
								</td>
							</tr>
							<tr class="collapse detall" id="detalls_${status.index}_rep">
								<td colspan="4">
									<div class="row">
										<div class="col-xs-6">
											<dl class="dl-horizontal">
												<c:choose>
													<c:when test="${representant.tipus == 'FISICA'}">
														<dt><spring:message code="interessat.form.camp.nom"/></dt><dd>${representant.nom} ${representant.llinatge1} ${representant.llinatge2}</dd>
													</c:when>
													<c:otherwise>
														<dt><spring:message code="interessat.form.camp.rao.social"/></dt><dd>${representant.raoSocial}</dd>
													</c:otherwise>
												</c:choose>
												<dt><spring:message code="interessat.form.camp.pais"/></dt><dd>${representant.paisNom}</dd>
												<dt><spring:message code="interessat.form.camp.provincia"/></dt><dd>${representant.provinciaNom}</dd>											
												<dt><spring:message code="interessat.form.camp.municipi"/></dt><dd>${representant.municipiNom}</dd>
												<dt><spring:message code="interessat.form.camp.direccio"/></dt><dd>${representant.direccio}</dd>
												<dt><spring:message code="interessat.form.camp.codipostal"/></dt><dd>${representant.codiPostal}</dd>
											</dl>
										</div>
										<div class="col-xs-6">
											<dl class="dl-horizontal">
												<dt><spring:message code="interessat.form.camp.document"/></dt><dd>${representant.documentIdent}</dd>
												<dt><spring:message code="interessat.form.camp.email"/></dt><dd>${representant.email}</dd>
												<dt><spring:message code="interessat.form.camp.telefon"/></dt><dd>${representant.telefon}</dd>
												<dt><spring:message code="interessat.form.camp.canal.notif"/></dt>	
													<dd>
														<c:if test="${not empty representant.canalNotif}">
															<spring:message code="interessat.tipus.enum.canalnotif.${representant.canalNotif}"/>
														</c:if>
													</dd>
												<c:if test="${not empty representant.codiDire}">
													<dt><spring:message code="interessat.form.camp.codi.dire"/></dt><dd>${representant.codiDire}</dd>
												</c:if>
												<c:if test="${not empty representant.dir3Codi}">
													<dt><spring:message code="interessat.form.camp.dir3codi"/></dt><dd>${representant.dir3Codi}</dd>
												</c:if>		
												<dt><spring:message code="interessat.form.camp.observacions"/></dt><dd>${representant.observacions}</dd>
											</dl>									
										</div>
									</div>
								</td>						
							</tr>
						</tbody>
					</table>
				</div>
			</c:if>
		</div>
	</td>						
</tr>
</tbody>
</table>
