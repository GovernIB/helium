<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<script src="<c:url value="/js/helium3Tasca.js"/>"></script>

	<form:form id="command" commandName="addVariableCommand" action="" cssClass="form-horizontal form-tasca" method="post">
		<input type="hidden" id="procesId" name="procesId" value="${procesId}">

		<c:set var="command" value="${addVariableCommand}"/>
<%-- 		<hel:inputSelect name="varCodi" --%>
<%-- 			emptyOption="true" --%>
<%-- 			placeholder="Selecciona una variable" --%>
<%-- 			optionItems="${camps}" --%>
<%-- 			optionValueAttribute="codi" --%>
<%-- 			optionTextAttribute="etiqueta" --%>
<%-- 			textKey="expedient.dada.variable"/> --%>
		
		<c:set var="campErrors"><form:errors path="varCodi"/></c:set>	
		<div id="selCamp" class="form-group">
			<label class="control-label col-xs-3 obligatori" for="varCodi"><spring:message code="expedient.dada.variable"/></label>
			<div id="selCamp_controls" class="col-xs-9 controls">
				<form:select path="varCodi" cssClass="form-control" id="varCodi">
					<option value=""></option>
					<optgroup label="<spring:message code='expedient.nova.data.nova'/>">
						<option value="String"><spring:message code="expedient.nova.data.string"/></option>
					</optgroup>
					<optgroup label="<spring:message code='expedient.nova.data.existent'/>">
						<c:forEach var="opt" items="${camps}">
							<form:option value="${opt.codi}">${opt.etiqueta}</form:option>
						</c:forEach>
					</optgroup>
				</form:select>
				<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="varCodi"/></p></c:if>
			</div>
		</div>

		<div id="nova" class="hide">
			<c:set var="campErrors"><form:errors path="codi"/></c:set>
			<div class="form-group <c:if test="${not empty campErrors}"> has-error</c:if>">
				<label for="codi" class="control-label col-xs-3 obligatori">codi</label>
				<div class="controls col-xs-9">
					<form:input path="codi" cssClass="form-control" id="codi" />
					<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="codi"/></p></c:if>
				</div>
			</div>
			<c:set var="campErrors"><form:errors path="valor"/></c:set>
			<div class="form-group <c:if test="${not empty campErrors}"> has-error</c:if>">
				<label for="valor" class="control-label col-xs-3">valor</label>
				<div class="controls col-xs-9">
					<form:input path="valor" cssClass="form-control" id="valor" />
					<c:if test="${not empty campErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="valor"/></p></c:if>
				</div>
			</div>
<%-- 			<hel:inputText name="codi" text="codi"/> --%>
<%-- 			<hel:inputText name="valor" text="Valor"/> --%>
		</div>
		<div id="valordada" class="<c:if test="${empty dada}">hide</c:if>">
		<!-- Aquí va el formulari per a introduïr les dades de la variable seleccionada. Aquest formulari es carregarà via Ajax  -->
<%-- 			<c:catch var="exception">${dada.campTipus}</c:catch> --%>
			<c:if test="${empty dada}">
				<div class='carregant'><span class='fa fa-circle-o-notch fa-spin fa-3x'></span></div>
			</c:if>
			<c:if test="${not empty dada}">
				<c:set var="inline" value="${false}"/>
				<c:set var="isRegistre" value="${false}"/>
				<c:set var="isMultiple" value="${false}"/>
				
				<c:choose>
					<c:when test="${dada.campTipus != 'REGISTRE'}">
						<c:choose>
							<c:when test="${dada.campMultiple}">
								<c:set var="campErrorsMultiple"><form:errors path="${dada.varCodi}"/></c:set>
								<div class="multiple<c:if test="${not empty campErrorsMultiple}"> has-error</c:if>">
									<label for="${dada.varCodi}" class="control-label col-xs-3">${dada.campEtiqueta}</label>
									<c:forEach var="membre" items="${command[dada.varCodi]}" varStatus="varStatusCab">
										<c:set var="inline" value="${true}"/>
										<c:set var="campCodi" value="${dada.varCodi}[${varStatusCab.index}]"/>
										<c:set var="campNom" value="${dada.varCodi}"/>
										<c:set var="campIndex" value="${varStatusCab.index}"/>
										<div class="col-xs-9 input-group-multiple <c:if test="${varStatusCab.index != 0}">pad-left-col-xs-3</c:if>">
											<c:set var="isMultiple" value="${true}"/>
											<%@ include file="campsTasca.jsp" %>
											<c:set var="isMultiple" value="${false}"/>
										</div>
									</c:forEach>
									<c:if test="${empty dada.multipleDades}">
										<c:set var="inline" value="${true}"/>
										<c:set var="campCodi" value="${dada.varCodi}[0]"/>
										<c:set var="campNom" value="${dada.varCodi}"/>
										<c:set var="campIndex" value="0"/>
										<div class="col-xs-9 input-group-multiple">
											<c:set var="isMultiple" value="${true}"/>
											<%@ include file="campsTasca.jsp" %>
											<c:set var="isMultiple" value="${false}"/>
										</div>
									</c:if>
									<div class="form-group">
										<div class="col-xs-9 pad-left-col-xs-3">
											<c:if test="${not empty dada.observacions}"><p class="help-block"><span class="label label-info">Nota</span> ${dada.observacions}</p></c:if>
											<button id="button_add_var_mult_${campCodi}" type="button" class="btn btn-default pull-left btn_afegir btn_multiple"><spring:message code='comuns.afegir' /></button>
											<div class="clear"></div>
											<c:if test="${not empty campErrorsMultiple}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="${dada.varCodi}"/></p></c:if>
										
									
								
							</c:when>
							<c:otherwise>
								<c:set var="campCodi" value="${dada.varCodi}"/>
								<c:set var="campNom" value="${dada.varCodi}"/>
								<%@ include file="campsTasca.jsp" %>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<%@ include file="campsTascaRegistre.jsp" %>
					</c:otherwise>
				</c:choose>
			</c:if>
		</div>
		
		<script>
		var codi = "${varCodi}";
		var procesId = "${procesId}";
		$(document).ready(function() {
			$("#varCodi").select2({
			    width: 'resolve',
			    placeholder: 'Selecciona una opcio', //'<spring:message code="expedient.nova.data.selecciona"/>',
			    allowClear: true,
			    minimumResultsForSearch: 6
			});
			$("#varCodi").on('select2-open', function() {
				var iframe = $('.modal-body iframe', window.parent.document);
				var height = $('html').height() + 30;
				iframe.height(height + 'px');
			});
			$("#varCodi").on('select2-close', function() {
				var iframe = $('.modal-body iframe', window.parent.document);
				var height = $('html').height();
				iframe.height(height + 'px');
			});
			$("#varCodi").on("change", function(e) {
				var ruta = document.URL; 
				ruta = ruta.substring(0, ruta.indexOf("novaDada") + 8) + "/" + procesId + "/" + e.val;
// 				alert(ruta);
				$("#command").attr('action', ruta);
				if (e.val == "") {
					$("#nova").addClass("hide");
					$("#valordada").addClass("hide");
					$("#command").attr('action', ruta + "Buit");
				} else  if (e.val == "String") {
					$("#valordada").addClass("hide");
					$("#nova").removeClass("hide");
				} else {
					$("#valordada").removeClass("hide");
					$("#nova").addClass("hide");
					if (e.val != codi)
						$("#formulari").load('<c:url value="/nodeco/v3/expedient/${expedientId}/novaDada/${procesId}/"/>' + e.val);
				}
				codi = e.val;
			});
			<c:if test="${varCodi}">$("#varCodi").select2("val", "${varCodi}");</c:if>
			$("button:submit").click(function(){
				if ($("#varCodi").val() == "") {
					$("#selCamp").addClass("has-error");
					if ($("#selCamp_error").length == 0) {
						$("#selCamp_controls").append("<p id='selCamp_error' class='help-block'><span class='fa fa-exclamation-triangle'></span>&nbsp;<spring:message code='expedient.nova.data.camp.variable.buit'/></p>");
					}
				} else {
					$("#command").submit();
				}
			});
		});
		</script>
	</form:form>