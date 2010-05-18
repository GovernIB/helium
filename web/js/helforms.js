
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

function initSelect(selectId, valor, url, extraParams) {
	var valorActual = $("select#" + selectId).val();
	$("select#" + selectId).html(
			'<option>Carregant...</option>');
    $.getJSON(
    		url,
    		extraParams,
    		function(j) {
			    var options = '';
			    options += '<option value="">&lt;&lt; Seleccioni un valor &gt;&gt;</option>';
		        for (var i = 0; i < j.length; i++) {
		        	if (j[i].valor == valor)
		        		options += '<option value="' + j[i].valor + '" selected="selected">' + j[i].text + '</option>';
		        	else
		        		options += '<option value="' + j[i].valor + '">' + j[i].text + '</option>';
		        }
		        $("select#" + selectId).html(options);
		        if (canvisSelectInicialitzat)
		        	$("select#" + selectId).val(valorActual);
			});
}

var canvisSelectValorsAddicionals;
var canvisSelectInicialitzat = false;
var canvisSelectTasca = new Array();
function canviSelectTasca(selectId, camp) {
	var trobat = false;
	var valor = $("select#" + selectId).val();
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
	var sels = $("#" + selectId).parents("form").find("select");
	for (i = 0; i < sels.length; i++) {
		if (sels[i].id != selectId)
			eval("initSelect_" + sels[i].id + "()");
	}
	canvisSelectInicialitzat = true;
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
