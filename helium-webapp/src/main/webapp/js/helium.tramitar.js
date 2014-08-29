function verificarSignatura(element) {
	var amplada = 800;
	var alcada = 600;
	$('<iframe id="verificacio" src="' + element.href + '"/>').dialog({
		title : "<spring:message code='tasca.form.verif_signa' />",
		autoOpen : true,
		modal : true,
		autoResize : true,
		width : parseInt(amplada),
		height : parseInt(alcada)
	}).width(amplada - 30).height(alcada - 30);
	return false;
}

function infoRegistre(docId) {
	var amplada = 600;
	var alcada = 200;
	$('<div>' + $("#registre_" + docId).html() + '</div>').dialog({
		title : "<spring:message code='tasca.form.info_reg' />",
		autoOpen : true,
		modal : true,
		width : parseInt(amplada),
		height : parseInt(alcada)
	}).width(amplada - 30).height(alcada - 30);
	return false;
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

function esborrarRegistre(e, campId, index) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation)
		e.stopPropagation();
	$('form#command').append(
			'<input type="hidden" name="registreEsborrarId" value="' + campId
					+ '"/>');
	$('form#command').append(
			'<input type="hidden" name="registreEsborrarIndex" value="' + index
					+ '"/>');
	refresh();
	return false;
}

function refresh() {
	$('form#command :button[name="submit"]').attr("name", "sbmt");
	$('form#command').submit();
}

function canviTermini(input) {
	var campId = input.id.substring(0, input.id.lastIndexOf("_"));
	var anys = document.getElementById(campId + "_anys").value;
	var mesos = document.getElementById(campId + "_mesos").value;
	var dies = document.getElementById(campId + "_dies").value;
	if (anys != "0" && mesos != "0" && dies != "") {
		if (dies == "") {
			dies = "0";
			$("#" + campId + "_dies").val(dies);
		}
		$(campId).val(anys + "/" + mesos + "/" + dies);
	} else {
		$(campId).val("");
	}
}

$(document).ready(function() {
	$("i.agrupacio-desplegador").parent().parent().click(function() {
		var taula = $(this).parent().parent().parent();
		var tbody = $('tbody', taula).first();
		var i = $(this).find("i").first();
		tbody.toggleClass('hide');
		i.removeClass('icon-chevron-up');
		i.removeClass('icon-chevron-down');
		if (tbody.hasClass('hide'))
			i.addClass('icon-chevron-down');
		else
			i.addClass('icon-chevron-up');
	});

	$(".eliminarFila").click(function() {
		if ($(this).closest('table').find('tr').index() < 2) {
			var newTr = $(this).closest('tr');
			limpiarFila(newTr);

			$(this).closest('table').addClass("hide");

			if ($(this).closest('table').hasClass("togle")) {
				$('#button_add_' + $(this).closest('table').attr('id')).show();
			}
		} else {
			$(this).closest('tr').remove();
		}
	});
});

function addField(idTable) {
	tabla = $('#' + idTable);
	if (tabla.hasClass("hide")) {
		tabla.removeClass("hide");

		if (tabla.hasClass("togle")) {
			$('#button_add_' + idTable).hide();
		}
	} else {
		tr = $('tr:last', tabla);
		var newTr = tr.clone();
		limpiarFila(newTr);
		newTr.find(':input').each(
				function(indice, valor) {
					if (this.getAttribute("id") != null) {
						var id = this.getAttribute("id");
						var id_lim = id.substr(0, id.indexOf("["));
						var id_fin = id.substr(id.lastIndexOf("["), id
								.lastIndexOf("]"));
						var i = 1;
						while (document.getElementById(id_lim + "[" + i + "]"
								+ id_fin)) {
							i = i + 1;
						}
						this
								.setAttribute("id", id_lim + "[" + i + "]"
										+ id_fin);
						this.setAttribute("name", id_lim + "[" + i + "]"
								+ id_fin);
					}
				});
		newTr.appendTo(tabla);
		newTr.find('button.eliminarFila').click(function() {
			if (newTr.index() < 2) {
				limpiarFila(newTr);
				tabla.addClass("hide");
				if (tabla.hasClass("togle")) {
					$('#button_add_' + tabla.attr('id')).show();
				}
			} else {
				newTr.remove();
			}
		});
	}
}

function limpiarFila(tr) {
	tr.find(':input').each(function() {
		switch (this.type) {
//		case 'password':
//		case 'text':
//		case 'textarea':
//		case 'file':
//		case 'select-one':
//		case 'select-multiple':
//			$(this).val('');
//			break;
		case 'checkbox':
		case 'radio':
			this.checked = false;
			break;
		default:
			$(this).val('');
			break;
		}
	});
}
var submitAction;
function saveAction(element, action) {
	submitAction = action;
	if ($.browser.msie && $.browser.version.substr(0, 1) <= 7) {
		element.innerHTML = action;
		var $submits = document.getElementsByName("submit");
		for (var i = 0; i < $submits.length; i++) {
			if ($submits[i] != element) {
				$submits[i].name = $submits[i].name + i;
			}
		}
	}
}
function saveAction(element, action, url) {
	submitAction = action;
	if ($.browser.msie && $.browser.version.substr(0, 1) <= 7) {
		element.innerHTML = action;
		var $submits = document.getElementsByName("submit");
		for (var i = 0; i < $submits.length; i++) {
			if ($submits[i] != element) {
				$submits[i].name = $submits[i].name + i;
			}
		}
	}
	if (url != null) {
		$(element).get(0).closest("form").attr('action', url);
		alert($(element).get(0).closest("form").attr('action'));
	}
}
