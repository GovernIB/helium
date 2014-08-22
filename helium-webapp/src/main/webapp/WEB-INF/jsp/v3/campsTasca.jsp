<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<c:if test="${not empty errors && errors[dada.varCodi] != ''}">
	<c:set var="campErrors">${errors[dada.varCodi]}</c:set>
</c:if>
<c:if test="${not empty dada_multiple}">
	<c:set var="campCodi">${dada_multiple}[${dada.varCodi}]</c:set>
</c:if>
<c:if test="${empty dada_multiple}">
	<c:set var="campCodi">${dada.varCodi}</c:set>
</c:if>
<c:set var="obligatorio"><c:if test="${dada.required}"> data-required="true"</c:if></c:set>

<c:if test="${!dada.readOnly && !tasca.validada}">
	<div class="controls<c:if test="${not empty campErrors}"> error</c:if>">
		<c:if test="${dada.campTipus == 'STRING'}">
			<input placeholder="${dada.campEtiqueta}" type="text" id="${campCodi}" name="${campCodi}" value="${dada.text}" class="form-control span11" ${obligatorio}/>
		</c:if>
		<c:if test="${dada.campTipus == 'TEXTAREA'}">
			<textarea placeholder="${dada.campEtiqueta}" id="${campCodi}" name="${campCodi}" class="form-control span11" ${obligatorio}>${dada.text}</textarea>
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
					<label for="${dada.varCodi}_anys" class="blockLabel">									
						<label><spring:message code='common.camptasca.anys' /></label>
						<select class="span9" id="${dada.varCodi}_anys" name="${dada.varCodi}_anys" onchange="canviTermini(this)">
							<c:forEach var="index" begin="0" end="12">
								<option value="${index}"<c:if test="${dada.varValor.anys==index}"> selected="selected"</c:if>>${index}</option>
							</c:forEach>
						</select>
					</label>
				</li>
				<li>									
					<label for="${dada.varCodi}_mesos" class="blockLabel">
						<span><spring:message code='common.camptasca.mesos' /></label>
						<select class="span9" id="${dada.varCodi}_mesos" name="${dada.varCodi}_mesos" onchange="canviTermini(this)">
							<c:forEach var="index" begin="0" end="12">
								<option value="${index}"<c:if test="${dada.varValor.mesos==index}"> selected="selected"</c:if>>${index}</option>
							</c:forEach>
						</select>
					</label>
				</li>
				<li>	
					<c:if test="${empty dies or dies == ''}"><c:set var="dies" value="0"/></c:if>
					<label for="${dada.varCodi}_dies" class="blockLabel">
						<label><spring:message code='common.camptasca.dies' /></label>
						<input type="text" class="span9" id="${dada.varCodi}_dies" name="${dada.varCodi}_dies" value="${dada.varValor.dies}" class="textInput" onchange="canviTermini(this)"/>
						<script>
							$("#${dada.varCodi}_dies").keyfilter(/^[-+]?[0-9]*$/);
						</script>
					</label>
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
			<input placeholder="${dada.campEtiqueta}" type="number" id="${campCodi}" name="${campCodi}" value="${dada.text}" class="form-control span11" style="text-align:right" ${obligatorio}/>
		</c:if>
		<c:if test="${dada.campTipus == 'FLOAT'}">
			<input placeholder="${dada.campEtiqueta}" type="number" id="${campCodi}" name="${campCodi}" value="${dada.text}" class="form-control span11" style="text-align:right" ${obligatorio}/>
		</c:if>
		<c:if test="${dada.campTipus == 'PRICE'}">
			<input placeholder="${dada.campEtiqueta}" type="text" id="${campCodi}" name="${campCodi}" value="${dada.text}" class="form-control span11" style="text-align:right" ${obligatorio}/>
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
			<div class="span5 input-append date datepicker">
				<input placeholder="${dada.campEtiqueta}" type="text" id="${campCodi}" name="${campCodi}" value="${dada.text}" class="date_${varStatusMain.index} span3" placeholder="dd/mm/yyyy" ${obligatorio}/>
				<span class="add-on" onclick="$('.date_${varStatusMain.index}').focus()"><i class="icon-calendar"></i></span>
			</div>
			<script>
				$(".date_${varStatusMain.index}").mask("99/99/9999");
				$(".date_${varStatusMain.index}").datepicker({language: 'ca', autoclose: true});
			</script>
		</c:if>
		<c:if test="${dada.campTipus == 'BOOLEAN'}">
			<input type="checkbox" id="${campCodi}" name="${campCodi}" <c:if test="${dada.varValor}"> checked="checked"</c:if> ${obligatorio}/>
		</c:if>
		<c:if test="${dada.campTipus == 'SELECCIO'}">
			<select id="${campCodi}" name="${campCodi}" class="form-control span11"><option value="" <c:if test="${dada.varValor == '' || empty dada.varValor}"> selected="selected"</c:if>>&lt;&lt; <spring:message code="js.helforms.selec_valor" /> &gt;&gt;</option></select>
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
		<div class="formHint">${dada.observacions}</div>
		<span>${campErrors}</span>
	</div>
</c:if>	

<c:if test="${dada.readOnly || tasca.validada}">
	<div class="controls">
		<label class="control-label-value"><c:out value="${dada.text}"/></label>
	</div>
	<c:if test="${dada.campTipus == 'TERMINI' && not empty dada.varValor}">
		<c:out value="${dada.varValor.anys}/${dada.varValor.mesos}/${dada.varValor.dies}"/>
	</c:if>
</c:if>
	