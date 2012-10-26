<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<script type="text/javascript">

function findValue(li, input) {
	var codi = input.id.substring(input.id.indexOf("_") + 1);
	if (!!li.extra ) var sValue = li.extra[0];
	else var sValue = li.selectValue;
	$("#suggest_" + codi).attr("disabled", "disabled");
	$("#suggest_" + codi + "_info").css("display", "none");
	$("#suggest_" + codi + "_delete").css("display", "");
	$("#" + codi).val(li.extra[0]);
}

function initSuggest(codi, url, callback, extraParams) {
	$("#suggest_" + codi).autocomplete(
			url, {
				delay:10,
				minChars:2,
				mustMatch: false,
				matchContains: true,
				onItemSelect: callback,
				extraParams: extraParams,
				onFindValue: callback});
	if ($("#" + codi).val() == null || $("#" + codi).val() == '') {
		$("#suggest_" + codi + "_info").css("display", "");
		$("#suggest_" + codi + "_delete").css("display", "none");
		$("#suggest_" + codi).attr("disabled", "");
	} else {
		$("#suggest_" + codi + "_info").css("display", "none");
		$("#suggest_" + codi + "_delete").css("display", "");
		$("#suggest_" + codi).attr("disabled", "disabled");
	}
	$("#suggest_" + codi + "_delete").click(function(){
		$("#" + codi).val(null);
		$("#suggest_" + codi).val("");
		$("#suggest_" + codi + "_info").css("display", "");
		$("#suggest_" + codi + "_delete").css("display", "none");
		$("#suggest_" + codi).attr("disabled", "");
	});
}

var selectDominiParams = new Array();
var consultaActiva = new Array();
function initSelect(selectId, valor, url, extraParams, dominiParams) {
	$.blockUI({
		message: '<fmt:message key="js.helforms.carreg_dades" />' ,
		css: {
        	border: 'none', 
        	padding: '15px', 
        	backgroundColor: '#000', 
        	'-webkit-border-radius': '10px', 
        	'-moz-border-radius': '10px', 
        	opacity: .5, 
        	color: '#fff'}
	});
	selectDominiParams[selectId] = dominiParams;
	consultaActiva.push(selectId);
	var valorActual = $("select#" + selectId).val();
	$("select#" + selectId).html(
			'<option><fmt:message key="js.helforms.carregant" /></option>');
    $.getJSON(
    		url,
    		extraParams,
    		function(j) {
			    var options = '';
			    options += '<option value="">&lt;&lt; <fmt:message key="js.helforms.selec_valor" /> &gt;&gt;</option>';
		        for (var i = 0; i < j.length; i++) {
		        	if (j[i].valor == valor)
		        		options += '<option value="' + j[i].valor + '" selected="selected">' + j[i].text + '</option>';
		        	else
		        		options += '<option value="' + j[i].valor + '">' + j[i].text + '</option>';
		        }
		        for (i = 0; i < consultaActiva.length; i++) {
		        	if (consultaActiva[i] == selectId) {
		        		consultaActiva.splice(i, 1);
		        		break;
		        	}
		        }
		        //if (!$.browser.msie || parseInt(jQuery.browser.version) > 7) {
			        if (consultaActiva.length == 0)
			        	$.unblockUI({ message: null });
		        //}
		        $("select#" + selectId).html(options);
		        if (canvisSelectInicialitzat)
		        	$("select#" + selectId).val(valorActual);
			});
}

var canvisSelectValorsAddicionals;
var canvisSelectInicialitzat = false;
var canvisSelectTasca = new Array();
function canviSelectTasca(selectId, camp, prefix, tipus) {
	if(tipus!="string"){
		valor = $("select#" + selectId).val();
	}else{valor = $("#"+selectId).val();}
	var campSensePrefix = camp;
	if (prefix) campSensePrefix = camp.substring(prefix.length);
	updateValorAddicionalSelect(campSensePrefix, valor);
	var sels = $("#" + selectId).parents("form").find("select");
	for (i = 0; i < sels.length; i++) {
		if (sels[i].id != selectId && selectDominiParams[sels[i].id] != null && selectDominiParams[sels[i].id].indexOf(campSensePrefix) != -1) {
			try { eval("initSelect_" + sels[i].id + "()"); }
			catch (ex) {
				alert(ex);
			}
		}
	}
	canvisSelectInicialitzat = true;
}
function updateValorAddicionalSelect(camp, valor) {
	var trobat = false;
	for (i = 0; i < canvisSelectTasca.length; i++) {
		if (canvisSelectTasca[i][0] == camp) {
			canvisSelectTasca[i][1] = valor;
			trobat = true;
			break;
		}
	}
	if (!trobat)
		canvisSelectTasca[canvisSelectTasca.length] = new Array(camp, valor);
	var str = "";
	for (i = 0; i < canvisSelectTasca.length; i++) {
		str = str + canvisSelectTasca[i][0] + ":" + canvisSelectTasca[i][1];
		if (i < canvisSelectTasca.length - 1)
			str = str + ",";
	}
	canvisSelectValorsAddicionals = str;
}

function multipleRemove(elem, index, field) {
	var fieldIndex = document.getElementById("helMultipleIndex");
	if (fieldIndex == null) {
		newField = document.createElement('input');
		newField.setAttribute("id", "helMultipleIndex");
		newField.setAttribute("name", "helMultipleIndex");
		newField.setAttribute("type", "hidden");
		newField.setAttribute("value", index);
		elem.form.appendChild(newField);
	}
	var fieldField = document.getElementById("helMultipleField");
	if (fieldField == null) {
		newField = document.createElement('input');
		newField.setAttribute("id", "helMultipleField");
		newField.setAttribute("name", "helMultipleField");
		newField.setAttribute("type", "hidden");
		newField.setAttribute("value", field);
		elem.form.appendChild(newField);
	}
	return true;
}

function multipleAdd(elem, field) {
	var fieldField = document.getElementById("helMultipleField");
	if (fieldField == null) {
		newField = document.createElement('input');
		newField.setAttribute("id", "helMultipleField");
		newField.setAttribute("name", "helMultipleField");
		newField.setAttribute("type", "hidden");
		newField.setAttribute("value", field);
		elem.form.appendChild(newField);
	}
	return true;
}

function accioCampExecutar(elem, field) {
	if (confirm("<fmt:message key='js.helforms.confirmacio' />")) {
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

function canviTermini(input) {
	var campId = input.id.substring(0, input.id.indexOf("_"));
	var anys = document.getElementById(campId + "_anys").value;
	var mesos = document.getElementById(campId + "_mesos").value;
	var dies = document.getElementById(campId + "_dies").value;
	document.getElementById(campId).value = anys + "/" + mesos + "/" + dies;
}

</script>