<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" buffer="16kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><spring:message code='expedient.iniciar.iniciar_expedient' />: ${expedientTipus.nom}</title>
	<meta name="capsaleraTipus" content="llistat"/>
	<script type="text/javascript" src="<c:url value="/js/jquery.keyfilter.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
<script type="text/javascript">
	// <![CDATA[
	$(document).ready(function() {
		$("select").select2({
			allowClear: true
		});
	});
		
	var accioInici;
	function confirmar(e) {
		if (!e) var e = window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		if ("cancel" == submitAction || "guardar" == submitAction) {
			return true;
		}
		<c:choose>
			<c:when test="${not ((expedientTipus.teNumero and expedientTipus.demanaNumero) or (expedientTipus.teTitol and expedientTipus.demanaTitol))}">return confirm("<spring:message code='expedient.iniciar.confirm_iniciar' />");</c:when>
			<c:otherwise>return true</c:otherwise>
		</c:choose>
	}
	function editarRegistre(campId, campCodi, campEtiqueta, numCamps, index) {
		var amplada = 686;
		var alcada = 64 * numCamps + 80;
		var url = "iniciarRegistre.html?id=${expedientTipus.id}&registreId=" + campId;
		if (index != null)
			url = url + "&index=" + index;
		$('<iframe id="' + campCodi + '" src="' + url + '" frameborder="0" marginheight="0" marginwidth="0"/>').dialog({
			title: campEtiqueta,
			autoOpen: true,
			modal: true,
			autoResize: true,
			width: parseInt(amplada),
			height: parseInt(alcada)
		}).width(amplada - 30).height(alcada - 30);
		return false;
	}
	function esborrarRegistre(e, campId, index) {
		var e = e || window.event;
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		$('form#command').append('<input type="hidden" name="registreEsborrarId" value="' + campId + '"/>');
		$('form#command').append('<input type="hidden" name="registreEsborrarIndex" value="' + index + '"/>');
		refresh();
		return false;
	}
	function refresh() {
		$('button[name="submit"]', $('form#command')).remove();
		submitAction = "guardar";
		$('form#command').submit();
	}
    function verificarSignatura(element) {
		var amplada = 800;
		var alcada = 600;
		$('<iframe id="verificacio" src="' + element.href + '"/>').dialog({
			title: "<spring:message code='tasca.form.verif_signa' />",
			autoOpen: true,
			modal: true,
			autoResize: true,
			width: parseInt(amplada),
			height: parseInt(alcada)
		}).width(amplada - 30).height(alcada - 30);
		return false;
	}
	function infoRegistre(docId) {
		var amplada = 600;
		var alcada = 200;
		$('<div>' + $("#registre_" + docId).html() + '</div>').dialog({
			title: "<spring:message code='tasca.form.info_reg' />",
			autoOpen: true,
			modal: true,
			width: parseInt(amplada),
			height: parseInt(alcada)
		}).width(amplada - 30).height(alcada - 30);
		return false;
	}
	function canviTermini(input) {
		var campId = input.id.substring(0, input.id.lastIndexOf("_"));
		var anys = document.getElementById(campId + "_anys").value;
		var mesos = document.getElementById(campId + "_mesos").value;
		var dies = document.getElementById(campId + "_dies").value;
		if (!anys.empty() && !mesos.empty() && !dies.empty()) {
			$(campId).val(anys + "/" + mesos + "/" + dies);
		} else {
			$(campId).val("");
		}
	}
	function editarRegistre(campId, campCodi, campEtiqueta, numCamps, index) {
		var amplada = 686;
		var alcada = 64 * numCamps + 80;
		var url = "registre.html?id=${tasca.id}&registreId=" + campId;
		if (index != null)
			url = url + "&index=" + index;
		$('<iframe id="' + campCodi + '" src="' + url + '" frameborder="0" marginheight="0" marginwidth="0"/>').dialog({
			title: campEtiqueta,
			autoOpen: true,
			modal: true,
			autoResize: true,
			width: parseInt(amplada),
			height: parseInt(alcada)
		}).width(amplada - 30).height(alcada - 30);
		return false;
	}
	
	function refresh() {
		$('form#command :button[name="submit"]').attr("name", "sbmt");
		$('form#command').submit();
	}

	function campOnFocus(camp) {
		$('form#command :input[name="helCampFocus"]').val("" + $(window).scrollTop() + "#${param.id}");
	}

	function addField(idTable){   
		tabla = $('#'+idTable);
		if(tabla.hasClass( "hide" )){
			tabla.removeClass( "hide" );

			if(tabla.hasClass( "togle" )){
				$('#button_add_'+idTable).hide();
			}
		} else {
	    	tr = $('tr:last', tabla);
	    	var newTr = tr.clone();
	    	limpiarFila(newTr);
    		newTr.find(':input').each(function(indice,valor) {
		    	if (this.getAttribute("id") != null) {
	    			var id = this.getAttribute("id");
	    			var id_lim = id.substr(0, id.indexOf("["));
	    			var id_fin = id.substr(id.lastIndexOf("["), id.lastIndexOf("]"));
			    	var i = 1;
	    			while (document.getElementById(id_lim+"["+i+"]"+id_fin)) {
		    			i = i+1;
		    		}
		    		this.setAttribute("id", id_lim+"["+i+"]"+id_fin);
		    		this.setAttribute("name", id_lim+"["+i+"]"+id_fin);
	    		}
	    	});
	    	newTr.appendTo(tabla);
		}
	}

	function accioCampExecutar(elem, field) {
		if (confirm("<spring:message code='js.helforms.confirmacio' />")) {
			var fieldField = document.getElementById("helAccioCamp");
			if (fieldField == null) {
				newField = document.createElement('input');
				newField.setAttribute("id", "helAccioCamp");
				newField.setAttribute("name", "helAccioCamp");
				newField.setAttribute("type", "hidden");
				newField.setAttribute("value", field);
				elem.form.appendChild(newField);
			}
			return true;
		}
		return false;
	}

	 $(".eliminarFila").live('click', function (){
	    if($(this).closest('table').find('tr').index() < 2) {
		    var newTr = $(this).closest('tr');
	    	limpiarFila(newTr);
	    	
	    	$(this).closest('table').addClass( "hide" );

			if($(this).closest('table').hasClass( "togle" )){
				$('#button_add_'+$(this).closest('table').attr('id')).show();
			}
		} else {
        	$(this).closest('tr').remove();
		}
    });

	function limpiarFila(tr) {
		tr.find(':input').each(function() {
		    switch(this.type) {
		        case 'password':
		        case 'text':
		        case 'textarea':
		        case 'file':
		        case 'select-one':
		        case 'select-multiple':
		            $(this).val('');
		            break;
		        case 'checkbox':
		        case 'radio':
		            this.checked = false;
		    }
		});
	}
	
	var submitAction;
	function saveAction(element, action) {
		submitAction = action;
		if ($.browser.msie && $.browser.version.substr(0,1) <= 7) {
			element.innerHTML = action;
			var $submits = document.getElementsByName("submit");
			for (var i = 0; i < $submits.length; i++) {
			    if ($submits[i] != element) {
			        $submits[i].name = $submits[i].name + i;
			    }
			}
		}
	}
// ]]>
</script>
<c:if test="${not empty tasca.formExtern}">
	<script type="text/javascript" src="<c:url value="/dwr/interface/formulariExternDwrService.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
<script type="text/javascript">
// <![CDATA[
	function clickFormExtern(form) {
		formulariExternDwrService.dadesIniciFormulariInicial(
				form.id.value,
				'${command.expedientTipusId}',
				<c:choose><c:when test="${not empty command.definicioProcesId}">'${command.definicioProcesId}'</c:when><c:otherwise>null</c:otherwise></c:choose>,
				{
					callback: function(retval) {
						if (retval) {
							$('<iframe id="formExtern" src="' + retval[0] + '"/>').dialog({
								title: '<spring:message code="tasca.form.dades_form" />',
				                autoOpen: true,
				                modal: true,
				                autoResize: true,
				                width: parseInt(retval[1]),
				                height: parseInt(retval[2]),
				                close: function() {
									form.submit();
								}
				            }).width(parseInt(retval[1]) - 30).height(parseInt(retval[2]) - 30);
						} else {
							alert("<spring:message code='tasca.form.error_ini' />");
						}
					},
					async: false
				});
		return false;
	}

	$(document).ready(function() {
		$("select").select2({
			allowClear: true
		});
	});
	
	function confirmar(form) {
		$("table").each(function(){
			if ($(this).hasClass("hide")) {
				$(this).remove();
			}
		});
		return true;
	}
    function verificarSignatura(element) {
		var amplada = 800;
		var alcada = 600;
		$('<iframe id="verificacio" src="' + element.href + '"/>').dialog({
			title: "<spring:message code='tasca.form.verif_signa' />",
			autoOpen: true,
			modal: true,
			autoResize: true,
			width: parseInt(amplada),
			height: parseInt(alcada)
		}).width(amplada - 30).height(alcada - 30);
		return false;
	}
	function infoRegistre(docId) {
		var amplada = 600;
		var alcada = 200;
		$('<div>' + $("#registre_" + docId).html() + '</div>').dialog({
			title: "<spring:message code='tasca.form.info_reg' />",
			autoOpen: true,
			modal: true,
			width: parseInt(amplada),
			height: parseInt(alcada)
		}).width(amplada - 30).height(alcada - 30);
		return false;
	}
	function canviTermini(input) {
		var campId = input.id.substring(0, input.id.lastIndexOf("_"));
		var anys = document.getElementById(campId + "_anys").value;
		var mesos = document.getElementById(campId + "_mesos").value;
		var dies = document.getElementById(campId + "_dies").value;
		if (!anys.empty() && !mesos.empty() && !dies.empty()) {
			$(campId).val(anys + "/" + mesos + "/" + dies);
		} else {
			$(campId).val("");
		}
	}
	function editarRegistre(campId, campCodi, campEtiqueta, numCamps, index) {
		var amplada = 686;
		var alcada = 64 * numCamps + 80;
		var url = "registre.html?id=${tasca.id}&registreId=" + campId;
		if (index != null)
			url = url + "&index=" + index;
		$('<iframe id="' + campCodi + '" src="' + url + '" frameborder="0" marginheight="0" marginwidth="0"/>').dialog({
			title: campEtiqueta,
			autoOpen: true,
			modal: true,
			autoResize: true,
			width: parseInt(amplada),
			height: parseInt(alcada)
		}).width(amplada - 30).height(alcada - 30);
		return false;
	}
	
	function refresh() {
		$('form#command :button[name="submit"]').attr("name", "sbmt");
		$('form#command').submit();
	}

	function campOnFocus(camp) {
		$('form#command :input[name="helCampFocus"]').val("" + $(window).scrollTop() + "#${param.id}");
	}

	function addField(idTable){   
		tabla = $('#'+idTable);
		if(tabla.hasClass( "hide" )){
			tabla.removeClass( "hide" );

			if(tabla.hasClass( "togle" )){
				$('#button_add_'+idTable).hide();
			}
		} else {
	    	tr = $('tr:last', tabla);
	    	var newTr = tr.clone();
	    	limpiarFila(newTr);
    		newTr.find(':input').each(function(indice,valor) {
		    	if (this.getAttribute("id") != null) {
	    			var id = this.getAttribute("id");
	    			var id_lim = id.substr(0, id.indexOf("["));
	    			var id_fin = id.substr(id.lastIndexOf("["), id.lastIndexOf("]"));
			    	var i = 1;
	    			while (document.getElementById(id_lim+"["+i+"]"+id_fin)) {
		    			i = i+1;
		    		}
		    		this.setAttribute("id", id_lim+"["+i+"]"+id_fin);
		    		this.setAttribute("name", id_lim+"["+i+"]"+id_fin);
	    		}
	    	});
	    	newTr.appendTo(tabla);
		}
	}

	function accioCampExecutar(elem, field) {
		if (confirm("<spring:message code='js.helforms.confirmacio' />")) {
			var fieldField = document.getElementById("helAccioCamp");
			if (fieldField == null) {
				newField = document.createElement('input');
				newField.setAttribute("id", "helAccioCamp");
				newField.setAttribute("name", "helAccioCamp");
				newField.setAttribute("type", "hidden");
				newField.setAttribute("value", field);
				elem.form.appendChild(newField);
			}
			return true;
		}
		return false;
	}

	 $(".eliminarFila").live('click', function (){
	    if($(this).closest('table').find('tr').index() < 2) {
		    var newTr = $(this).closest('tr');
	    	limpiarFila(newTr);
	    	
	    	$(this).closest('table').addClass( "hide" );

			if($(this).closest('table').hasClass( "togle" )){
				$('#button_add_'+$(this).closest('table').attr('id')).show();
			}
		} else {
        	$(this).closest('tr').remove();
		}
    });

	function limpiarFila(tr) {
		tr.find(':input').each(function() {
		    switch(this.type) {
		        case 'password':
		        case 'text':
		        case 'textarea':
		        case 'file':
		        case 'select-one':
		        case 'select-multiple':
		            $(this).val('');
		            break;
		        case 'checkbox':
		        case 'radio':
		            this.checked = false;
		    }
		});
	}
	
	var submitAction;
	function saveAction(element, action) {
		submitAction = action;
		if ($.browser.msie && $.browser.version.substr(0,1) <= 7) {
			element.innerHTML = action;
			var $submits = document.getElementsByName("submit");
			for (var i = 0; i < $submits.length; i++) {
			    if ($submits[i] != element) {
			        $submits[i].name = $submits[i].name + i;
			    }
			}
		}
	}
// ]]>
</script>
</c:if>
</head>
<body>

	<h3 class="titol-tab titol-dades-tasca">${tasca.nom}</h3>

	<c:if test="${not empty tasca.formExtern}">
		<form action="iniciarPasForm" onclick="return clickFormExtern(this)">
			<input type="hidden" name="id" value="${tasca.id}"/>
			<input type="hidden" name="expedientTipusId" value="${command.expedientTipusId}"/>
			<button type="submit" class="submitButton"><spring:message code='tasca.form.obrir_form' /></button>
		</form><br/>
	</c:if>

	<form:form action="iniciarPasForm" id="command" name="command" cssClass="form-horizontal form-tasca" onsubmit="return confirmar(event)" method="post" commandName="command">
		<input type="hidden" id="id" name="id" value="${tasca.id}"/>
		<input type="hidden" name="entornId" value="${entornId}"/>
		<input type="hidden" name="expedientTipusId" value="${expedientTipus.id}"/>
		<input type="hidden" name="definicioProcesId" value="${definicioProcesId}"/>
		<c:if test="${(empty tasca.formExtern) or (not empty tasca.formExtern and tasca.validada)}">
			<c:if test="${not empty tasca.camps}">
				<c:forEach var="dada" items="${dades}" varStatus="varStatusMain">
					<c:if test="${not dada.readOnly}">
						<div class="control-group fila_reducida <c:if test='${dada.readOnly || tasca.validada}'>fila_reducida</c:if>">
							<label class="control-label" for="${dada.varCodi}">${dada.campEtiqueta} - ${dada.campTipus}</label>
							
							<c:set var="dada" value="${dada}"/>
							<%@ include file="../campsTasca.jsp" %>
							<%@ include file="../campsTascaRegistre.jsp" %>
						</div>
					</c:if>
				</c:forEach>
			</c:if>
		</c:if>
		<br/>
		<div style="clear: both"></div>
		<div class="pull-right">
			<button type="button" class="btn" name="submit" value="cancel" onclick="location='iniciar'">
				<spring:message code='comuns.cancelar' />
			</button>				
			<button type="submit" class="btn btn-primary" name="submit" value="submit" onclick="accioInici=this.value">
				<spring:message code='comuns.iniciar' />
			</button>
		</div>
	</form:form>
	<p class="aclaracio"><spring:message code='comuns.camps_marcats' /> <i class='icon-asterisk'></i> <spring:message code='comuns.son_oblig' /></p>

	<script>	
		$( '[data-required="true"]' )
			.closest(".control-group")
			.children("label")
			.prepend("<i class='icon-asterisk'></i> ");
	</script>
</body>
</html>
