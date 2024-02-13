<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<meta name="decorator" content="senseCapNiPeus"/>

<!-- Pàgian per pintar la informació de la unitat organitzativa en cas que no estigui vigent. Es mostra en
 la pipella de metades NTI del tipus d'expedient en seleccionar un òrgan. -->
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
						<!-- Tipus de transició -->
						<c:if test="${unitatOrganitzativaTipusTransicio != null }">
							<label class="col-xs-4 text-right">
								<spring:message code="expedient.tipus.metadades.nti.unitat.organitzativa.transicio.tipus"></spring:message>													
							</label>
							<div class="col-xs-8">
								<ul style="padding-left: 17px;">
									<li><spring:message code="enum.tipus.transcicio.${unitatOrganitzativaTipusTransicio}"></spring:message></li>
								</ul>
							</div>
						</c:if>
						
						<!-- Noves unitats -->
						<label class="col-xs-4 text-right">
							<spring:message code="expedient.tipus.metadades.nti.unitat.oranitzativa.noves"></spring:message>
							
						</label>
						<div class="col-xs-8">
							<c:choose>
								<c:when test="${unitatOrganitzativa.lastHistoricosUnitats != null 
													&& ! empty unitatOrganitzativa.lastHistoricosUnitats}">
									<ul style="padding-left: 17px;" id="lastHistoricosUnitats">
										<c:forEach var="novaUo" items="${unitatOrganitzativa.lastHistoricosUnitats }">
											<li>${novaUo.codi} - ${novaUo.nom}</li>
										</c:forEach>
									</ul>
								</c:when>
								<c:otherwise>
									(No hi ha transició cap a noves unitats organitzatives)
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

</c:if>

