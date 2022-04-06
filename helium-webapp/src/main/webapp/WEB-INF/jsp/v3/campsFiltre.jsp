<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="campPath" value="${campActual.varCodi}"/>
<c:set var="campErrors"><form:errors path="${campPath}"/></c:set>

<div class="form-group<c:if test="${not empty campErrors}"> has-error</c:if>" style="margin-left: 0px !important;">

 	<c:if test="${campActual.campTipus != 'DATE'}">
	 	<c:if test="${campActual.varCodi != 'expedient$id'}">
			<label 	for="${campPath}" class="control-label top-label"
					style="width: 100%; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;"
					title="${campActual.campEtiqueta}">
				${campActual.campEtiqueta}
			</label>
		</c:if>
		<c:if test="${campActual.varCodi == 'expedient$id'}">
			<label 	for="${campPath}[0]" class="control-label top-label"
						style="width: 100%; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;"
						title="${campActual.campEtiqueta}">
						${campActual.campEtiqueta}
			</label>
		</c:if>
	</c:if>

	<div class="controls consulta-tipus-controls">
		<c:choose>
			<c:when test="${campActual.campTipus == 'STRING'}">
				
					<c:if test="${campActual.varCodi != 'expedient$id'}">
						<label class="control-label col-xs-4 hide" for="${campPath}[0]"><spring:message code="common.campfiltre.entre"/></label>
						<div class="col-xs-13">
							<form:input path="${campPath}" cssClass="form-control" id="${campPath}" data-required="false" />
						</div>
					</c:if>
					
				<c:if test="${campActual.varCodi == 'expedient$id'}">
						<c:set var="urlConsultaInicial" value="/helium/v3/expedient/consulta/${consulta.id}/suggest/expedient/inici"/>
						<c:set var="urlConsultaLlistat" value="/helium/v3/expedient/consulta/${consulta.id}/suggest/expedient/llista/${consulta.expedientTipus.id}"/>
						<div class="col-md-5 p-0">
								<hel:inputSuggest inline="true" 
									name="${campPath}[0]" 
									urlConsultaInicial="${urlConsultaInicial}" 
									urlConsultaLlistat="${urlConsultaLlistat}" 
									placeholder="Des de" 
									text="Des de"/>
						</div>
						<div class="col-md-5 p-0">
								<hel:inputSuggest inline="true" 
									name="${campPath}[1]" 
									urlConsultaInicial="${urlConsultaInicial}" 
									urlConsultaLlistat="${urlConsultaLlistat}" 
									placeholder="Fins a" 
									text="Fins a"/>
						</div>			
				</c:if>				
			</c:when>
			<c:when test="${campActual.campTipus == 'TEXTAREA'}">
					<label class="control-label col-xs-4 hide" for="${campPath}[0]"><spring:message code="common.campfiltre.entre"/></label>
					<div class="col-xs-13">
						<form:textarea path="${campPath}" cssClass="form-control" id="${campPath}" data-required="false" />
					</div>
			</c:when>
			<c:when test="${campActual.campTipus == 'INTEGER'}">
					<label class="control-label col-xs-4 hide" for="${campPath}[0]"><spring:message code="common.campfiltre.entre"/></label>
					<div class="col-xs-13">
						<form:input path="${campPath}[0]" cssClass="form-control text-right enter" id="${campPath}[0]" data-required="false"/>
					</div>
				</div>
				<div class="col-xs-6">
					<label class="control-label col-xs-4 hide" for="${campPath}[1]"><spring:message code="common.campfiltre.i"/></label>
					<div class="col-xs-13">
						<form:input path="${campPath}[1]" cssClass="form-control text-right enter" id="${campPath}[1]" data-required="false"/>
					</div>
				<!-- </div> -->
			</c:when>
			<c:when test="${campActual.campTipus == 'FLOAT'}">
				<div class="col-xs-6 p-0 pr-15">
					<label class="control-label col-xs-4 hide" for="${campPath}[0]"><spring:message code="common.campfiltre.entre"/></label>
					<div class="col-xs-13">
						<form:input path="${campPath}[0]" cssClass="form-control text-right float" id="${campPath}[0]" data-required="false"/>
					</div>
				</div>
				<div class="col-xs-6 p-0 pl-15">
					<label class="control-label col-xs-4 hide" for="${campPath}[1]"><spring:message code="common.campfiltre.i"/></label>
					<div class="col-xs-13">
						<form:input path="${campPath}[1]" cssClass="form-control text-right float" id="${campPath}[1]" data-required="false"/>
					</div>
				</div>
			</c:when>
			<c:when test="${campActual.campTipus == 'PRICE'}">
				<div class="col-xs-6 p-0 pr-15">
					<label class="control-label col-xs-4 hide" for="${campPath}[0]"><spring:message code="common.campfiltre.entre"/></label>
					<div class="col-xs-13">
						<form:input path="${campPath}[0]" cssClass="form-control text-right price" id="${campPath}[0]" data-required="false"/>
					</div>
				</div>
				<div class="col-xs-6 p-0 pl-15">
					<label class="control-label col-xs-4 hide" for="${campPath}[1]"><spring:message code="common.campfiltre.i"/></label>
					<div class="col-xs-13">
						<form:input path="${campPath}[1]" cssClass="form-control text-right price" id="${campPath}[1]" data-required="false"/>
					</div>
				</div>
			</c:when>
			<c:when test="${campActual.campTipus == 'DATE'}">
			<div class="col-md-12 p-0">
				<label 	for="${campPath}" class="control-label top-label"
				style="width: 100%; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;"
				title="${campActual.campEtiqueta}">
					${campActual.campEtiqueta}
				</label>
				<div class="col-xs-6 p-0 pr-15">
					<label class="control-label col-xs-4 hide" for="${campPath}[0]"><spring:message code="common.campfiltre.entre"/></label>
					<div class="col-xs-13">
						<div class="input-group">
							<form:input path="${campPath}[0]" id="${campPath}[0]" cssClass="date form-control" placeholder="dd/mm/aaaa" data-required="false"/>
							<span class="input-group-addon btn_date"><span class="fa fa-calendar"></span></span>
						</div>
					</div>
				</div>
				<div class="col-xs-6 p-0 pl-15">
					<label class="control-label col-xs-4 hide" for="${campPath}[1]"><spring:message code="common.campfiltre.i"/></label>
					<div class="col-xs-13">
						<div class="input-group">
							<form:input path="${campPath}[1]" id="${campPath}[1]" cssClass="date form-control" placeholder="dd/mm/aaaa" data-required="false"/>
							<span class="input-group-addon btn_date"><span class="fa fa-calendar"></span></span>
						</div>
					</div>
				</div>
				</div>
			</c:when>
			<c:when test="${campActual.campTipus == 'BOOLEAN'}">
				<hel:inputSelect inline="true" name="${campPath}" emptyOption="true" optionItems="${valorsBoolea}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			</c:when>
			<c:when test="${campActual.campTipus == 'SELECCIO'}">
				<c:set var="EXPEDIENT_ESTAT" value="<%=net.conselldemallorca.helium.v3.core.api.dto.ExpedientCamps.EXPEDIENT_CAMP_ESTAT_JSP%>"></c:set>
				<div class="col-xs-13">
				<c:choose>
					<c:when test="${campPath == EXPEDIENT_ESTAT}">
						<hel:inputSelect inline="true" name="${campPath}" text="${campActual.campEtiqueta}" emptyOption="true" optionItems="${estats}" optionValueAttribute="codi" optionTextAttribute="nom"/>
					</c:when>
					<c:otherwise>
						<hel:inputSelect inline="true" name="${campPath}" text="${campActual.campEtiqueta}" emptyOption="true" optionItems="${campActual.varValor}" optionValueAttribute="codi" optionTextAttribute="valor"/>
					</c:otherwise>
				</c:choose>
				</div>
			</c:when>
			<c:when test="${campActual.campTipus == 'TERMINI'}">
					<div class="col-xs-4 tercpre">
						<label class="control-label col-xs-4" for="${campPath}_anys"><spring:message code="common.camptasca.anys"/></label>
						<div class="col-xs-8">
							<form:select  itemLabel="valor" itemValue="codi" items="${listTerminis}" path="${campPath}.anys" id="${campPath}_anys" cssClass="termini" />
						</div>
					</div>
					<div class="col-xs-4 tercmig">
	 					<label class="control-label col-xs-4" for="${campPath}_mesos"><spring:message code="common.camptasca.mesos"/></label>
	 					<div class="col-xs-8">
	 						<form:select  itemLabel="valor" itemValue="codi" items="${listTerminis}" path="${campPath}.mesos" id="${campPath}_mesos" cssClass="termini" />
	 					</div>
	 				</div>
	 				<div class="col-xs-4 tercpost">
	 					<label class="control-label col-xs-4" for="${campPath}_dies"><spring:message code="common.camptasca.dies"/></label>
	 					<div class="col-xs-8">
	 						<hel:inputText inline="true" name="${campPath}.dies" textKey="common.camptasca.dies" placeholderKey="common.camptasca.dies"/>
	 					</div>
	 				</div>
 				<script type="text/javascript">
					$(document).ready(function() {
						$("#${campPath}_anys").select2({
						    width: 'resolve',
						    allowClear: true,
						    minimumResultsForSearch: 10
						});
						$("#${campPath}_mesos").select2({
						    width: 'resolve',
						    allowClear: true,
						    minimumResultsForSearch: 10
						});
					});
				</script>
			</c:when>			
			<c:when test="${campActual.campTipus == 'SUGGEST'}">
					<c:set var="urlConsultaInicial" value="domini/consulta/inicial/${campActual.campId}"/>
					<c:set var="urlConsultaLlistat" value="domini/consulta/${campActual.campId}"/>
					<hel:inputSuggest inline="true" name="${campPath}" urlConsultaInicial="${urlConsultaInicial}" urlConsultaLlistat="${urlConsultaLlistat}" placeholder="${campActual.campEtiqueta}" text="${campActual.campEtiqueta}"/>
			</c:when>
			<c:otherwise>
				¿¿¿No contemplada???
			</c:otherwise>
		</c:choose>
	</div>
</div>
