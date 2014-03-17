<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="hel"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<c:if test="${dada.campTipus == 'STRING'}">
	<div class="controls">
		<c:if test="${!dada.readOnly && !tasca.validada}">	
			<input type="text" id="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" name="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" value="${dada.text}" class="span11" <c:if test="${dada.required}"> data-required="true"</c:if>/>
			<div class="formHint">${dada.observacions}</div>
		</c:if>
		
		<c:if test="${dada.readOnly || tasca.validada}">
			<label class="control-label-value"><c:out value="${dada.varValor}"/></label>
		</c:if>	
	</div>
</c:if>
<c:if test="${dada.campTipus == 'TEXTAREA'}">
	<div class="controls">
		<c:if test="${!dada.readOnly && !tasca.validada}">	
			<textarea id="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" name="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" class="span11" <c:if test="${dada.required}"> data-required="true"</c:if>>${dada.text}</textarea>
			<div class="formHint">${dada.observacions}</div>
		</c:if>
		
		<c:if test="${dada.readOnly || tasca.validada}">
			<label class="control-label-value"><c:out value="${dada.text}"/></label>
		</c:if>
	</div>
</c:if>
<c:if test="${dada.campTipus == 'SUGGEST'}">
	<div class="controls">
		<c:if test="${!dada.readOnly && !tasca.validada}">	
			<c:set var="extraParams">campCodi:'${dada.varCodi}',valors:function(){return canvisSelectValorsAddicionals}</c:set>
			<c:import url="../common/formElement.jsp">
				<c:param name="property">${dada.varCodi}</c:param>
				<c:param name="required">${dada.required}</c:param>
				<c:param name="type" value="suggest"/>
				<c:param name="label">${dada.campEtiqueta}</c:param>
				<c:param name="comment">${dada.observacions}</c:param>
				<c:param name="suggestUrl"><c:url value="/v3/domini/consultaExpedient"/></c:param>
				<c:param name="suggestExtraParams">${extraParams},tipus:'suggest'</c:param>
				<c:param name="suggestText"><c:if test="${not empty tasca.valorsDomini[dada.varCodi]}">${tasca.valorsDomini[dada.varCodi].valor}</c:if></c:param>
				<c:param name="iterateOn"><c:if test="${dada.campMultiple}">${command[dada.varCodi]}</c:if></c:param>
				<c:param name="multipleIcons"><c:if test="${dada.campMultiple}">true</c:if></c:param>
				<c:param name="multipleSuggestText">${valorsPerSuggest[dada.varCodi]}</c:param>
			</c:import>	
			<div class="formHint">${dada.observacions}</div>
		</c:if>
		
		<c:if test="${dada.readOnly || tasca.validada}">
			<label class="control-label-value"><c:out value="${dada.text}"/></label>
		</c:if>	
	</div>
</c:if>
<c:if test="${dada.campTipus == 'TERMINI'}">
	<div class="controls multiField">
		<c:if test="${!dada.readOnly && !tasca.validada}">	
			<ul class="alternate alt_termini">
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
			<div class="formHint">${dada.observacions}</div>
		</c:if>
		
		<input id="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" name="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" type="hidden" value="">
		
		<c:if test="${(dada.readOnly || tasca.validada) && not empty dada.varValor}">
			<c:out value="${dada.varValor.anys}/${dada.varValor.mesos}/${dada.varValor.dies}"/>
		</c:if>	
	</div>
</c:if>
<c:if test="${dada.campTipus == 'ACCIO'}">
	<c:if test="${!dada.readOnly && !tasca.validada}">	
		<div class="controls">
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
			<script>
				$("#${dada.varCodi}").keyfilter(/^[-+]?[0-9]*$/);
			</script>
			<div class="formHint">${dada.observacions}</div>
		</div>
	</c:if>
</c:if>
<c:if test="${dada.campTipus == 'INTEGER'}">
	<div class="controls">
		<c:if test="${!dada.readOnly && !tasca.validada}">	
			<input type="number" id="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" name="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" value="${dada.text}" class="span11" style="text-align:right" <c:if test="${dada.required}"> data-required="true"</c:if>/>
			<script>
				$("#${dada.varCodi}").keyfilter(/^[-+]?[0-9]*$/);
			</script>	
			<div class="formHint">${dada.observacions}</div>
		</c:if>
		<c:if test="${dada.readOnly || tasca.validada}">
			<label class="control-label-value"><c:out value="${dada.text}"/></label>
		</c:if>	
	</div>
</c:if>
<c:if test="${dada.campTipus == 'FLOAT'}">
	<div class="controls">
		<c:if test="${!dada.readOnly && !tasca.validada}">	
			<input type="number" id="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" name="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" value="${dada.text}" class="span11" style="text-align:right" <c:if test="${dada.required}"> data-required="true"</c:if>/>
			<script>
				$("#${dada.varCodi}").keyfilter(/^[-+]?[,0-9]*$/);
			</script>	
			<div class="formHint">${dada.observacions}</div>
		</c:if>
		<c:if test="${dada.readOnly || tasca.validada}">
			<label class="control-label-value"><c:out value="${dada.text}"/></label>
		</c:if>	
	</div>
</c:if>
<c:if test="${dada.campTipus == 'PRICE'}">
	<div class="controls">
		<c:if test="${!dada.readOnly && !tasca.validada}">	
			<input type="number" id="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" name="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" value="${dada.text}" class="span11" style="text-align:right" <c:if test="${dada.required}"> data-required="true"</c:if>/>
			<script>
				$("#${dada.varCodi}").priceFormat({
					prefix: '',
					centsSeparator: ',',
				    thousandsSeparator: '.',
				    allowNegative: false
				});
			</script>	
			<div class="formHint">${dada.observacions}</div>
		</c:if>
		<c:if test="${dada.readOnly || tasca.validada}">
			<label class="control-label-value"><c:out value="${dada.text}"/></label>
		</c:if>	
	</div>
</c:if>
<c:if test="${dada.campTipus == 'DATE'}">
	<div class="controls">
		<c:if test="${!dada.readOnly && !tasca.validada}">	
			<div class="span5 input-append date datepicker">
				<input type="text" id="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" name="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" value="${dada.text}" class="date_${varStatusMain.index} span3" placeholder="dd/mm/yyyy" <c:if test="${dada.required}"> data-required="true"</c:if>/>
				<span class="add-on" onclick="$('.date_${varStatusMain.index}').focus()"><i class="icon-calendar"></i></span>
			</div>
			<script>
				$(".date_${varStatusMain.index}").mask("99/99/9999");
				$(".date_${varStatusMain.index}").datepicker({language: 'ca', autoclose: true});
			</script>
			<div class="formHint">${dada.observacions}</div>	
		</c:if>
		<c:if test="${dada.readOnly || tasca.validada}">
			<label class="control-label-value"><c:out value="${dada.text}"/></label>
		</c:if>	
	</div>
</c:if>
<c:if test="${dada.campTipus == 'BOOLEAN'}">
	<div class="controls">
		<c:if test="${!dada.readOnly && !tasca.validada}">
			<input type="checkbox" id="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" name="<c:if test="${not empty dada_multiple}">${dada_multiple}[${dada.varCodi}]</c:if><c:if test="${empty dada_multiple}">${dada.varCodi}</c:if>" <c:if test="${dada.varValor}"> checked="checked"</c:if> <c:if test="${dada.required}"> data-required="true"</c:if>/>
			<div class="formHint">${dada.observacions}</div>	
		</c:if>
		
		<c:if test="${dada.readOnly || tasca.validada}">
			<label class="control-label-value"><c:out value="${dada.text}"/></label>
		</c:if>
	</div>	
</c:if>
<c:if test="${dada.campTipus == 'SELECCIO'}">
	<div class="controls">
		<c:if test="${!dada.readOnly && !tasca.validada}">
			<c:if test='${not empty dada_multiple}'>
				<c:set var="idSelect" value="${dada_multiple}[${dada.varCodi}]"/>
			</c:if>
			<c:if test='${empty dada_multiple}'>
				<c:set var="idSelect" value="${dada.varCodi}"/>
			</c:if>
			<select id="${idSelect}" name="${idSelect}" class="span11"><option value="" <c:if test="${dada.varValor == '' || empty dada.varValor}"> selected="selected"</c:if>>&lt;&lt; <spring:message code="js.helforms.selec_valor" /> &gt;&gt;</option></select>
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
					    	document.getElementById('${idSelect}').options.add(option);
				        });
				    }
				});
			</script>
			<div class="formHint">${dada.observacions}</div>
		</c:if>
		<c:if test="${dada.readOnly || tasca.validada}">
			<label class="control-label-value"><c:out value="${dada.text}"/></label>
		</c:if>	
	</div>
</c:if>
