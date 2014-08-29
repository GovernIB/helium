<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:if test="${empty campCodi}"><c:set var="campCodi">${dada.varCodi}</c:set></c:if>
<c:if test="${empty campInline}"><c:set var="campInline" value="${false}"/></c:if>
<c:set var="obligatorio"><c:if test="${dada.required}"> data-required="true"</c:if></c:set>

<c:if test="${!dada.readOnly && !tasca.validada && dada.campTipus != 'REGISTRE'}">
	<c:choose>
		<c:when test="${not dada.campMultiple}">	
	
			<c:set var="campErrors"><form:errors path="${campCodi}"/></c:set>
			<div class="form-group<c:if test="${inline}"> condensed</c:if><c:if test="${not empty campErrors}"> has-error</c:if>">
				<label for="${dada.varCodi}" class="control-label<c:choose><c:when test='${inline}'> sr-only</c:when><c:otherwise> col-xs-3<c:if test="${dada.required}"> obligatori</c:if></c:otherwise></c:choose>">${dada.campEtiqueta}</label>
				<div class="controls input-group<c:if test='${not inline}'> col-xs-9</c:if>">
	
	
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
						<div class="col-xs-4 tercpre">
							<label for="${campCodi}.anys" class="blockLabel"><spring:message code='common.camptasca.anys' /></label>
							<div class="">
								<form:select path="${campCodi}.anys" id="${campCodi}.anys" cssClass="termini">
									<c:forEach var="index" begin="0" end="12">
										<option value="${index}"<c:if test="${command[campCodi].anys==index}"> selected="selected"</c:if>>${index}</option>
									</c:forEach>
								</form:select>
							</div>
						</div>
						<div class="col-xs-4 tercmig">
							<label for="${campCodi}.mesos" class="blockLabel"><spring:message code='common.camptasca.mesos' /></label>
							<div class="">
								<form:select path="${campCodi}.mesos" id="${campCodi}.mesos" cssClass="termini">
									<c:forEach var="index" begin="0" end="12">
										<option value="${index}"<c:if test="${command[campCodi].mesos==index}"> selected="selected"</c:if>>${index}</option>
									</c:forEach>
								</form:select>
							</div>								
						</div> 
						<div class="col-xs-4 tercpost">
							<label for="${campCodi}.dies" class="blockLabel"><spring:message code='common.camptasca.dies' /></label>
							<div class="">
								<form:input path="${campCodi}.dies" id="${campCodi}.dies" cssClass="textInput termini termdia"/>
							</div>
						</div>
					</c:if>
					
	<%-- BOOLEAN ------------------------------------------------------------------------------------%>					
					<c:if test="${dada.campTipus == 'BOOLEAN'}">
						<form:checkbox path="${campCodi}" id="${campCodi}" data-required="${dada.required}"/>
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
						<c:set var="urlConsultaInicial"><c:url value='/v3/domini/consulta/inicial'/>/${tasca.id}}/${dada.campId}"</c:set>
						<c:set var="urlConsultaLlistat"><c:url value='/v3/domini/consulta/'/>/${tasca.id}}/${dada.campId}"</c:set>
						<form:input path="${campCodi}" cssClass="form-control suggest" id="${campCodi}" data-campId="${dada.campId}"/>
						<script>
// TODO:						
						$(document).ready(function() {
							$("#${campCodi}").select2({
							    minimumInputLength: 2,
							    width: '100%',
							    allowClear: true,
							    ajax: {
							        url: function (value) {
							        	return "<c:url value='/v3/domini/consulta'/>/${tasca.id}/${dada.campId}/" + value;
							        },
							        dataType: 'json',
							        results: function (data, page) {
							        	var results = [];
							        	for (var i = 0; i < data.length; i++) {
							        		results.push({id: data[i].codi, text: data[i].valor});
							        	}
							            return {results: results};
							        }
							    },
							    initSelection: function(element, callback) {
							    	if ($(element).val()) {
								    	$.ajax("<c:url value='/v3/domini/consulta/inicial'/>/${tasca.id}/${dada.campId}/" + $(element).val(), {
							                dataType: "json"
							            }).done(function(data) {
							            	callback({id: data.codi, text: data.valor});
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
					
	<%-- SELECCIO ------------------------------------------------------------------------------------%>					
					<c:if test="${dada.campTipus == 'SELECCIO'}">
						<form:input path="${campCodi}" id="${campCodi}"/>

						<script>
// TODO:
							$(document).ready(function() {
								$("#${campCodi}").select2({
								    width: 'resolve',
								    placeholder: "<spring:message code="js.helforms.selec_valor" />",
								    allowClear: true,
								    minimumResultsForSearch: 10,
								    ajax: {
								    	url: 'camp/${dada.campId}/valorsSeleccio',
								    	dataType: 'json',
								    	results: function(data, page) {
								    		var results = [];
								        	for (var i = 0; i < data.length; i++) {
								        		results.push({id: data[i].codi, text: data[i].valor});
								        	}
								            return {results: results};
								    	}
								    }
								});
								
// 								$("#${campCodi}").on('select2-open', function() {
// 									var iframe = $('.modal-body iframe', window.parent.document);
// 									var height = $('html').height() + 30;
// 									iframe.height(height + 'px');
// 								});
// 								$("#${campPath}").on('select2-close', function() {
// 									var iframe = $('.modal-body iframe', window.parent.document);
// 									var height = $('html').height();
// 									iframe.height(height + 'px');
// 								});
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
						<div class="col-xs-4 tercpre">
							<label for="${campCodi}[${i}].anys" class="blockLabel"><spring:message code='common.camptasca.anys' /></label>
							<div class="">
								<form:select path="${campCodi}[${i}].anys" id="${campCodi}[${i}].anys" cssClass="termini">
									<c:forEach var="index" begin="0" end="12">
										<option value="${index}"<c:if test="${command[campCodi][i].anys==index}"> selected="selected"</c:if>>${index}</option>
									</c:forEach>
								</form:select>
							</div>
						</div>
						<div class="col-xs-4 tercmig">
							<label for="${campCodi}[${i}].mesos" class="blockLabel"><spring:message code='common.camptasca.mesos' /></label>
							<div class="">
								<form:select path="${campCodi}[${i}].mesos" id="${campCodi}[${i}].mesos" cssClass="termini">
									<c:forEach var="index" begin="0" end="12">
										<option value="${index}"<c:if test="${command[campCodi][i].mesos==index}"> selected="selected"</c:if>>${index}</option>
									</c:forEach>
								</form:select>
							</div>								
						</div> 
						<div class="col-xs-4 tercpost">
							<label for="${campCodi}[${i}].dies" class="blockLabel"><spring:message code='common.camptasca.dies' /></label>
							<div class="">
								<form:input path="${campCodi}[${i}].dies" id="${campCodi}[${i}].dies" cssClass="textInput termini termdia"/>
							</div>
						</div>
					</c:if>		
					
	<%-- BOOLEAN ------------------------------------------------------------------------------------%>					
					<c:if test="${dada.campTipus == 'BOOLEAN'}">
						<form:checkbox path="${campCodi}[${i}]" id="${campCodi}[${i}]" data-required="${dada.required}"/>
					</c:if>
					
					
					
	<%-- SUGGEST ------------------------------------------------------------------------------------%>
					<c:if test="${dada.campTipus == 'SUGGEST'}">
						<c:set var="urlConsultaInicial"><c:url value='/v3/domini/consulta/inicial'/>/${tasca.id}}/${dada.campId}"</c:set>
						<c:set var="urlConsultaLlistat"><c:url value='/v3/domini/consulta/'/>/${tasca.id}}/${dada.campId}"</c:set>
						<form:input path="${campCodi}[${i}]" cssClass="form-control suggest" id="${campCodi}[${i}]" data-campId="${dada.campId}"/>
					</c:if>
					
	<%-- SELECCIO ------------------------------------------------------------------------------------%>					
					<c:if test="${dada.campTipus == 'SELECCIO'}">
						<form:input path="${campCodi}[${i}]" id="${campCodi}[${i}]"/>
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



<%---------------------------------------------------------------------------------------------------------- --%>

<%--
<c:if test="${not empty errors && errors[dada.varCodi] != ''}">
	<c:set var="campErrors">${errors[dada.varCodi]}</c:set>
</c:if>
<c:if test="${not empty dada_multiple}">
	<c:set var="campCodi">${dada_multiple}[${dada.varCodi}]</c:set>
</c:if>
<c:if test="${empty dada_multiple}">
	<c:set var="campCodi">${dada.varCodi}</c:set>
</c:if>

<c:if test="${!dada.readOnly && !tasca.validada && dada.campTipus != 'REGISTRE'}">
	<c:if test="${addFormGroup}">
	<div class="form-group<c:if test="${registre}"> condensed</c:if><c:if test="${not empty campErrors}"> has-error</c:if>">
		<label for="${dada.varCodi}" class="control-label<c:if test='${registre}'> sr-only</c:if>">${dada.campEtiqueta} - ${dada.campTipus}<c:if test="${dada.required}"> <span class=""></span></c:if></label>
		<div class="controls">
	</c:if>
		<c:if test="${dada.campTipus == 'STRING'}">
			<input placeholder="${dada.campEtiqueta}" type="text" id="${campCodi}" name="${campCodi}" value="${dada.text}" ${obligatorio}/>
		</c:if>
		<c:if test="${dada.campTipus == 'TEXTAREA'}">
			<textarea placeholder="${dada.campEtiqueta}" id="${campCodi}" name="${campCodi}" ${obligatorio}>${dada.text}</textarea>
		</c:if>
		<c:if test="${dada.campTipus == 'SUGGEST'}">
			<c:set var="extraParams">taskId:'${tasca.tascaId}',campId:'${dada.campId}',valors:function(){return canvisSelectValorsAddicionals}</c:set>
			<c:import url="../common/formElement.jsp">
				<c:param name="property">${campCodi}</c:param>
				<c:param name="required">${dada.required}</c:param>
				<c:param name="type" value="suggest"/>
				<c:param name="label">${dada.campEtiqueta}</c:param>
				<c:param name="comment">${dada.observacions}</c:param>
				<c:param name="suggestUrl"><c:url value="/v3/domini/consulta"/></c:param>
				<c:param name="suggestExtraParams">${extraParams},tipus:'suggest'</c:param>
				<c:param name="suggestText">${campCodi}</c:param>
				<c:param name="iterateOn"><c:if test="${dada.campMultiple}">${dada.multipleDades}</c:if></c:param>
				<c:param name="multipleIcons"><c:if test="${dada.campMultiple}">true</c:if></c:param>
				<c:param name="multipleSuggestText"></c:param>
			</c:import>
		</c:if>
		<c:if test="${dada.campTipus == 'TERMINI'}">
			<ul class="multiField alternate alt_termini">
				<li>
					<label for="${dada.varCodi}_anys" class="blockLabel"><spring:message code='common.camptasca.anys' /></label>
					<select id="${dada.varCodi}_anys" name="${dada.varCodi}_anys" onchange="canviTermini(this)">
						<c:forEach var="index" begin="0" end="12">
							<option value="${index}"<c:if test="${dada.varValor.anys==index}"> selected="selected"</c:if>>${index}</option>
						</c:forEach>
					</select>
				</li>
				<li>									
					<label for="${dada.varCodi}_mesos" class="blockLabel"><spring:message code='common.camptasca.mesos' /></label>
					<select id="${dada.varCodi}_mesos" name="${dada.varCodi}_mesos" onchange="canviTermini(this)">
						<c:forEach var="index" begin="0" end="12">
							<option value="${index}"<c:if test="${dada.varValor.mesos==index}"> selected="selected"</c:if>>${index}</option>
						</c:forEach>
					</select>
				</li>
				<li>	
					<c:if test="${empty dies or dies == ''}"><c:set var="dies" value="0"/></c:if>
					<label for="${dada.varCodi}_dies" class="blockLabel"><spring:message code='common.camptasca.dies' /></label>
					<div class="">
						<input type="text" id="${dada.varCodi}_dies" name="${dada.varCodi}_dies" value="${dada.varValor.dies}" class="textInput" onchange="canviTermini(this)"/>
					</div>
					<script>
						$("#${dada.varCodi}_dies").keyfilter(/^[-+]?[0-9]*$/);
					</script>
				</li>
			</ul>
		</c:if>
		<c:if test="${dada.campTipus == 'ACCIO'}">
			<button 
				class="btn pull-lef" 
				name="submit" 
				type="submit" 
				value="submit" 
				onclick="
					saveAction(this, 'submit');
					return accioCampExecutar(this, '${dada.jbpmAction}')
					">
				<spring:message code="common.camptasca.executar" />
			</button>
		</c:if>
		<c:if test="${dada.campTipus == 'INTEGER'}">
			<input placeholder="${dada.campEtiqueta}" type="number" id="${campCodi}" name="${campCodi}" value="${dada.text}" style="text-align:right" ${obligatorio}/>
		</c:if>
		<c:if test="${dada.campTipus == 'FLOAT'}">
			<input placeholder="${dada.campEtiqueta}" type="number" id="${campCodi}" name="${campCodi}" value="${dada.text}" style="text-align:right" ${obligatorio}/>
		</c:if>
		<c:if test="${dada.campTipus == 'PRICE'}">
			<input placeholder="${dada.campEtiqueta}" type="text" id="${campCodi}" name="${campCodi}" value="${dada.text}" style="text-align:right" ${obligatorio}/>
			<script>
				$("#${dada.varCodi}").priceFormat({
					prefix: '',
					centsSeparator: ',',
				    thousandsSeparator: '.',
				    allowNegative: false
				});
			</script>
		</c:if>
		<c:if test="${dada.campTipus == 'DATE'}">
			<div class="input-append">
				<input placeholder="${dada.campEtiqueta}" type="text" id="${campCodi}" name="${campCodi}" value="${dada.text}" class="date_${varStatusMain.index} form-control" placeholder="dd/mm/yyyy" ${obligatorio}/>
				<span class="add-on" onclick="$('.date_${varStatusMain.index}').focus()"><i class="icon-calendar"></i></span>
			</div>
			<script>
				var datep = $(".date_${varStatusMain.index}");
				datep.mask("99/99/9999");
				datep.datepicker({language: 'ca', autoclose: true});
			</script>
		</c:if>
		<c:if test="${dada.campTipus == 'BOOLEAN'}">
			<input type="checkbox" id="${campCodi}" name="${campCodi}" <c:if test="${dada.varValor}"> checked="checked"</c:if> ${obligatorio}/>
		</c:if>
		<c:if test="${dada.campTipus == 'SELECCIO'}">
			<select id="${campCodi}" name="${campCodi}" ><option value="" <c:if test="${dada.varValor == '' || empty dada.varValor}"> selected="selected"</c:if>>&lt;&lt; <spring:message code="js.helforms.selec_valor" /> &gt;&gt;</option></select>
			<script>
		       	$.ajax({
				    url: 'camp/${dada.campId}/valorsSeleccio',
				    type: 'GET',
				    dataType: 'json',
				    success: function(json) {
				    	$.each(json, function(i, value) {
					    	var option = new Option(value.text,value.codi);	
					    	if(value.codi == '${dada.varValor}') {
					    		option.setAttribute("selected","selected");
						    }
					    	document.getElementById('${campCodi}').options.add(option);
				        });
				    }
				});
			</script>
		</c:if>
		<c:if test="${not empty dada.observacions && !registre}">
			<p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p>
		</c:if>
		<span class="campErrors">${campErrors}</span>
	<c:if test="${addFormGroup}">
		</div>
	</div>
	</c:if>
	<c:if test="${!registre}"><div class="clearForm"></div></c:if>
</c:if>	
--%>

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