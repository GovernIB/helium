<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:if test="${empty campCodi}">
	<c:choose>
		<c:when test="${not dada.campMultiple}">
			<c:choose>
				<c:when test="${empty dada_multiple}">
					<c:set var="campCodi">${dada.varCodi}</c:set>
				</c:when>
				<c:otherwise>
					<c:set var="campCodi">${dada_multiple}.${membre.varCodi}</c:set>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<c:set var="campCodi">${dada_multiple}[${membre.varCodi}]</c:set>
		</c:otherwise>
	</c:choose>
</c:if>
<c:if test="${empty campInline}"><c:set var="campInline" value="${inline}"/></c:if>
<c:set var="obligatorio"><c:if test="${dada.required}"> data-required="true"</c:if></c:set>

<c:if test="${!dada.readOnly && !tasca.validada && dada.campTipus != 'REGISTRE'}">
	<c:choose>
		<c:when test="${not dada.campMultiple}">
			<c:set var="campErrors"><form:errors path="${campCodi}"/></c:set>
			<div class="form-group<c:if test="${inline}"> condensed</c:if><c:if test="${not empty campErrors}"> has-error</c:if>">
				<label for="${dada.varCodi}" class="control-label<c:choose><c:when test='${inline}'> sr-only</c:when><c:otherwise> col-xs-3<c:if test="${dada.required}"> obligatori</c:if></c:otherwise></c:choose>">${dada.campEtiqueta} - ${dada.campTipus}</label>
				<div class="controls<c:if test='${not inline}'> col-xs-9</c:if>">
	
	
<%-- VARIABLES SENZILLES ----------------------------------------------------------------------------%>
<%---------------------------------------------------------------------------------------------------%>
					
	<%-- STRING -------------------------------------------------------------------------------------%>
					<c:if test="${dada.campTipus == 'STRING'}">
						<form:input path="${campCodi}" cssClass="form-control" id="${campCodi}" data-required="${dada.required}" />
					</c:if>
					
	<%-- TEXTAREA -----------------------------------------------------------------------------------%>
					<c:if test="${dada.campTipus == 'TEXTAREA'}">
						<form:textarea path="${campCodi}" cssClass="form-control" id="${campCodi}" data-required="${dada.required}" />
					</c:if>
					
	<%-- INTEGER ------------------------------------------------------------------------------------%>
					<c:if test="${dada.campTipus == 'INTEGER'}">
						<form:input path="${campCodi}" cssClass="form-control text-right enter" id="${campCodi}" data-required="${dada.required}"/>
					</c:if>

	<%-- FLOAT --------------------------------------------------------------------------------------%>
					<c:if test="${dada.campTipus == 'FLOAT'}">
						<form:input path="${campCodi}" cssClass="form-control text-right float" id="${campCodi}" data-required="${dada.required}"/>
					</c:if>

	<%-- PRICE --------------------------------------------------------------------------------------%>
					<c:if test="${dada.campTipus == 'PRICE'}">
						<form:input path="${campCodi}" cssClass="form-control text-right price" id="${campCodi}" data-required="${dada.required}"/>
					</c:if>

	<%-- DATE ---------------------------------------------------------------------------------------%>		
					<c:if test="${dada.campTipus == 'DATE'}">
						<div class="input-group">
							<form:input path="${campCodi}" id="${campCodi}" cssClass="date form-control" placeholder="dd/mm/yyyy" data-required="${dada.required}"/>
							<span class="input-group-addon btn_date"><span class="glyphicon glyphicon-calendar"></span></span>
						</div>
					</c:if>
					
	<%-- TERMINI ------------------------------------------------------------------------------------%>					
					<c:if test="${dada.campTipus == 'TERMINI'}">
						<div class="form-group">
							<div class="col-xs-4 tercpre">
								<label class="control-label col-xs-4" for="${campCodi}_anys"><spring:message code="common.camptasca.anys"/></label>
								<div class="col-xs-8">
									<form:select  itemLabel="valor" itemValue="codi" items="${listTerminis}" path="${campCodi}.anys" id="${campCodi}_anys" cssClass="termini" />
								</div>
							</div>
							<div class="col-xs-4 tercmig">
			 					<label class="control-label col-xs-4" for="${campCodi}_mesos"><spring:message code="common.camptasca.mesos"/></label>
			 					<div class="col-xs-8">
			 						<form:select  itemLabel="valor" itemValue="codi" items="${listTerminis}" path="${campCodi}.mesos" id="${campCodi}_mesos" cssClass="termini" />
			 					</div>
			 				</div>
			 				<div class="col-xs-4 tercpost">
			 					<label class="control-label col-xs-4" for="${campCodi}_dies"><spring:message code="common.camptasca.dies"/></label>
			 					<div class="col-xs-8">
			 						<hel:inputText inline="true" name="${campCodi}.dies" textKey="common.camptasca.dies" placeholderKey="common.camptasca.dies"/>
			 					</div>
			 				</div>
			 			</div>
		 				<script>
							$(document).ready(function() {
								$("#${campCodi}_anys").select2({
								    width: 'resolve',
								    allowClear: true,
								    minimumResultsForSearch: 10
								});
								$("#${campCodi}_mesos").select2({
								    width: 'resolve',
								    allowClear: true,
								    minimumResultsForSearch: 10
								});
							});
						</script>
					</c:if>
					
	<%-- BOOLEAN ------------------------------------------------------------------------------------%>					
					<c:if test="${dada.campTipus == 'BOOLEAN'}">
						<form:checkbox path="${campCodi}" id="${campCodi}" data-required="${dada.required}" cssClass="checkbox"/>
					</c:if>
					
	<%-- ACCIO --------------------------------------------------------------------------------------%>					
					<c:if test="${dada.campTipus == 'ACCIO'}">
						<button class="btn pull-lef btn_accio" name="submit" type="submit" value="submit" data-action="${dada.jbpmAction}" 
							onclick="saveAction(this, 'submit');
								return accioCampExecutar(this, '${dada.jbpmAction}')
						">
							<spring:message code="common.camptasca.executar" />
						</button>
					</c:if>
					
	<%-- SUGGEST ------------------------------------------------------------------------------------%>
					<c:if test="${dada.campTipus == 'SUGGEST'}">
						<c:set var="urlConsultaInicial"><c:url value='/v3/domini/consulta/inicial'/>/${tasca.id}/${dada.campId}</c:set>
						<c:set var="urlConsultaLlistat"><c:url value='/v3/domini/consulta'/>/${tasca.id}/${dada.campId}</c:set>
						<hel:inputSuggest inline="true" name="${campCodi}" urlConsultaInicial="${urlConsultaInicial}" urlConsultaLlistat="${urlConsultaLlistat}"/>
					</c:if>
					
	<%-- SELECCIO ------------------------------------------------------------------------------------%>					
					<c:if test="${dada.campTipus == 'SELECCIO'}">
						<form:input path="${campCodi}" id="${campCodi}" cssClass="seleccio"/>
						<script>
							$(document).ready(function() {
								$("#${campCodi}").select2({
								    width: 'resolve',
								    placeholder: "<spring:message code="js.helforms.selec_valor" />",
								    allowClear: true,
								    minimumResultsForSearch: 10,
								    ajax: {
								        url: function (value) {
								        	return "camp/${dada.campId}/valorsSeleccio/" + value;
								        },
								        dataType: 'json',
								        results: function (data, page) {
								        	var results = [];
								        	for (var i = 0; i < data.length; i++) {
								        		results.push({id: data[i].codi, text: data[i].nom});
								        	}
								            return {results: results};
								        }
								    },
								    initSelection: function(element, callback) {
								    	if ($(element).val()) {
									    	$.ajax("camp/${dada.campId}/valorsSeleccio/inicial/" + $(element).val(), {
								                dataType: "json"
								            }).done(function(data) {
								            	callback({id: data.codi, text: data.nom});
								            });
								    	}
								    },
								}).on('select2-open', function() {
									var iframe = $('.modal-body iframe', window.parent.document);
									var height = $('html').height() + $(".select2-drop").height() - 60;
									iframe.height(height + 'px');
								}).on('select2-close', function() {
									var iframe = $('.modal-body iframe', window.parent.document);
									var height = $('html').height();
									iframe.height(height + 'px');
								});
							});
						</script>
					</c:if>
					
<%-- Fi VARIABLES SENZILLES -------------------------------------------------------------------------%>
<%---------------------------------------------------------------------------------------------------%>
					
					<c:if test="${not inline and not empty dada.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p></c:if>
					<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campPath}"/></p></c:if>				
				</div>	<%-- Fi div controls--%>
			</div>	<%-- Fi div form-group--%>
			<c:if test="${not inline}"><div class="clearForm"></div></c:if>
		</c:when>
		<c:otherwise>
		
<%-- VARIABLES MULTIPLES ----------------------------------------------------------------------------%>
<%---------------------------------------------------------------------------------------------------%>
				
			<c:set var="mida" value="${fn:length(command[campCodi])}"/>
			<c:set var="campLabelText" value="${dada.campEtiqueta}"/>
			<div class="multiple">
			<c:forEach var="i" begin="0" end="${mida - 1}">
				<c:set var="campErrors"><form:errors path="${campCodi}[${i}]"/></c:set>
				<div class="form-group<c:if test="${inline}"> condensed</c:if><c:if test="${not empty campErrors}"> has-error</c:if>">
					<label for="${campCodi}[${i}]" class="control-label<c:choose><c:when test='${inline}'> sr-only</c:when><c:otherwise> col-xs-3<c:if test="${dada.required}"> obligatori</c:if></c:otherwise></c:choose>">${campLabelText}</label>
					<div class="controls input-group<c:if test='${not inline}'> col-xs-9</c:if>">
					<c:if test="${mida > 0}">
					
	<%-- STRING -----------------------------------------------------------------------------------------%>
						<c:if test="${dada.campTipus == 'STRING'}">
							<form:input path="${campCodi}[${i}]" cssClass="form-control" id="${campCodi}[${i}]" data-required="${dada.required}"/>
						</c:if>

	<%-- TEXTAREA ---------------------------------------------------------------------------------------%>			
						<c:if test="${dada.campTipus == 'TEXTAREA'}">
							<form:textarea path="${campCodi}[${i}]" cssClass="form-control" id="${campCodi}[${i}]" data-required="${dada.required}" />
						</c:if>

	<%-- INTEGER ---------------------------------------------------------------------------------------%>
					<c:if test="${dada.campTipus == 'INTEGER'}">
						<form:input path="${campCodi}[${i}]" type="number" cssClass="form-control text-right" id="${campCodi}[${i}]" data-required="${dada.required}"/>
					</c:if>

	<%-- FLOAT ---------------------------------------------------------------------------------------%>
					<c:if test="${dada.campTipus == 'FLOAT'}">
						<form:input path="${campCodi}[${i}]" type="number" cssClass="form-control text-right" id="${campCodi}[${i}]" data-required="${dada.required}"/>
					</c:if>

	<%-- PRICE --------------------------------------------------------------------------------------%>
					<c:if test="${dada.campTipus == 'PRICE'}">
						<form:input path="${campCodi}[${i}]" cssClass="form-control text-right price" id="${campCodi}[${i}]" data-required="${dada.required}"/>
					</c:if>
	
	<%-- DATE ---------------------------------------------------------------------------------------%>		
					<c:if test="${dada.campTipus == 'DATE'}">
						<div class="input-append">
							<span class="input-group-addon btn_date_pre"><span class="glyphicon glyphicon-calendar"></span></span>
							<form:input path="${campCodi}[${i}]" id="${campCodi}[${i}]" cssClass="date form-control" placeholder="dd/mm/yyyy" data-required="${dada.required}"/>
						</div>
					</c:if>
					
	<%-- TERMINI ------------------------------------------------------------------------------------%>			
					<c:if test="${dada.campTipus == 'TERMINI'}">
						<div class="form-group">
							<div class="col-xs-4 tercpre">
								<label class="control-label col-xs-4" for="${campCodi}_anys"><spring:message code="common.camptasca.anys"/></label>
								<div class="col-xs-8">
									<form:select  itemLabel="valor" itemValue="codi" items="${listTerminis}" path="${campCodi}[${i}].anys" id="${campCodi}_anys" cssClass="termini" />
								</div>
							</div>
							<div class="col-xs-4 tercmig">
			 					<label class="control-label col-xs-4" for="${campCodi}_mesos"><spring:message code="common.camptasca.mesos"/></label>
			 					<div class="col-xs-8">
			 						<form:select  itemLabel="valor" itemValue="codi" items="${listTerminis}" path="${campCodi}[${i}].mesos" id="${campCodi}_mesos" cssClass="termini" />
			 					</div>
			 				</div>
			 				<div class="col-xs-4 tercpost">
			 					<label class="control-label col-xs-4" for="${campCodi}_dies"><spring:message code="common.camptasca.dies"/></label>
			 					<div class="col-xs-8">
			 						<hel:inputText inline="true" name="${campCodi}[${i}].dies" textKey="common.camptasca.dies" placeholderKey="common.camptasca.dies"/>
			 					</div>
			 				</div>
			 			</div>
		 				<script>
							$(document).ready(function() {
								$("#${campCodi}_anys").select2({
								    width: 'resolve',
								    allowClear: true,
								    minimumResultsForSearch: 10
								});
								$("#${campCodi}_mesos").select2({
								    width: 'resolve',
								    allowClear: true,
								    minimumResultsForSearch: 10
								});
							});
						</script>
					</c:if>		
					
	<%-- BOOLEAN ------------------------------------------------------------------------------------%>					
					<c:if test="${dada.campTipus == 'BOOLEAN'}">
						<form:checkbox path="${campCodi}[${i}]" id="${campCodi}[${i}]" data-required="${dada.required}"/>
					</c:if>
					
	<%-- SELECCIO ------------------------------------------------------------------------------------%>					
					<c:if test="${dada.campTipus == 'SELECCIO'}">
						<form:input path="${campCodi}[${i}]" id="${campCodi}[${i}]" cssClass="seleccio"/>
						<script>
							$(document).ready(function() {
								$("#${campCodi}[${i}]").select2({
								    width: 'resolve',
								    placeholder: "<spring:message code="js.helforms.selec_valor" />",
								    allowClear: true,
								    minimumResultsForSearch: 10,
								    ajax: {
								        url: function (value) {
								        	return "camp/${dada.campId}/valorsSeleccio/" + value;
								        },
								        dataType: 'json',
								        results: function (data, page) {
								        	var results = [];
								        	for (var i = 0; i < data.length; i++) {
								        		results.push({id: data[i].codi, text: data[i].nom});
								        	}
								            return {results: results};
								        }
								    },
								    initSelection: function(element, callback) {
								    	if ($(element).val()) {
									    	$.ajax("camp/${dada.campId}/valorsSeleccio/inicial/" + $(element).val(), {
								                dataType: "json"
								            }).done(function(data) {
								            	callback({id: data.codi, text: data.nom});
								            });
								    	}
								    },
								}).on('select2-open', function() {
									var iframe = $('.modal-body iframe', window.parent.document);
									var height = $('html').height() + $(".select2-drop").height() - 60;
									iframe.height(height + 'px');
								}).on('select2-close', function() {
									var iframe = $('.modal-body iframe', window.parent.document);
									var height = $('html').height();
									iframe.height(height + 'px');
								});
							});
						</script>
					</c:if>
					
	<%-- SUGGEST ------------------------------------------------------------------------------------%>
					<c:if test="${dada.campTipus == 'SUGGEST'}">
						<c:set var="urlConsultaInicial"><c:url value='/v3/domini/consulta/inicial'/>/${tasca.id}/${dada.campId}</c:set>
						<c:set var="urlConsultaLlistat"><c:url value='/v3/domini/consulta'/>/${tasca.id}/${dada.campId}</c:set>
						<hel:inputSuggest inline="true" name="${campCodi}[${i}]" urlConsultaInicial="${urlConsultaInicial}" urlConsultaLlistat="${urlConsultaLlistat}"/>
					</c:if>
					
	
<%-- Fi VARIABLES MULTIPLES -------------------------------------------------------------------------%>
						<span class="input-group-addon btn_eliminar"><span class="glyphicon glyphicon-remove"></span></span>
						<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${campCodi}[${i}]"/></p></c:if>
						</c:if>	
					</div>
				</div>
				<c:if test="${i == 0}"><c:set var="campLabelText" value=""/></c:if>
			</c:forEach>
			<div class="form-group">
				<c:if test="${not inline}"><div class="col-xs-3"></div><div class="col-xs-9 input-group"></c:if>
					<c:if test="${not inline and not empty dada.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p></c:if>
					<button id="button_add_var_mult_${campCodi}" type="button" class="btn pull-left btn_afegir btn_multiple"><spring:message code='comuns.afegir' /></button>
				<c:if test="${not inline}"></div></c:if>
			</div>
			</div>		
		</c:otherwise>
	</c:choose>
</c:if>

<c:if test="${dada.readOnly || tasca.validada}">
	<div class="controls">
		<label class="control-label-value"><c:out value="${dada.text}"/></label>
	</div>
	<c:if test="${dada.campTipus == 'TERMINI' && not empty dada.varValor}">
		<c:out value="${dada.varValor.anys}/${dada.varValor.mesos}/${dada.varValor.dies}"/>
	</c:if>
</c:if>
	
<c:set var="campCodi" value=""/>
<c:set var="campInline" value="${false}"/>