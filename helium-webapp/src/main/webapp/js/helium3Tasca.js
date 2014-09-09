/**
 * Funcions a executar al montar un formulari de tasca a la versió 3
 */
$(function(){
	// Formats
	$(".price").priceFormat({
		prefix: '',
		centsSeparator: ',',
	    thousandsSeparator: '.',
	    allowNegative: false
	});
	$(".date").mask("99/99/9999").datepicker({language: 'ca', autoclose: true});
	$(".termdia").keyfilter(/^[-+]?[0-9]*$/);
	$(".enter").keyfilter(/^[-+]?[0-9]*$/);
	$(".float").keyfilter(/^[-+]?[0-9]*[.]?[0-9]*$/);
	
	// Afegir múltiple
	$("#command").on("click", ".btn_multiple", function(){
		var previousInput = $(this).closest('.form-group').prev();
		var newInput = previousInput.clone();
		$('input', newInput).each(function(){
			var input = $(this);
			input.val('');
			if (input.attr("name") != null) {
				var name = input.attr("name");
				var name_pre = name.substr(0, name.lastIndexOf("["));
				var name_post = name.substr(name.lastIndexOf("]") + 2);
				var index = parseInt(name.substr(name.lastIndexOf("[") + 1, name.lastIndexOf("]"))) + 1;
				input.attr({ 
					"id" : name_pre + "[" + index + "]" + name_post, 
					"name" : name_pre + "[" + index + "]" + name_post});
			}
			$('label', newInput).text('');
			if ($('label', newInput).hasClass('sr-only') && !$('label', newInput).closest('.input-group-multiple').hasClass('pad-left-col-xs-3')) {
				$('label', newInput).closest('.input-group-multiple').addClass('pad-left-col-xs-3');
			}
		});
		previousInput.after(newInput);
		
		// Camp de tipus price
		newInput.find(".price").priceFormat({
				prefix: '',
				centsSeparator: ',',
			    thousandsSeparator: '.',
			    allowNegative: false
			});
		// Camp de tipus date
		newInput.find(".date").mask("99/99/9999").datepicker({language: 'ca', autoclose: true});
		// Camp de tipus termini
		newInput.find(".termdia").keyfilter(/^[-+]?[0-9]*$/);
		// Camp de tipus enter
		newInput.find(".enter").keyfilter(/^[-+]?[0-9]*$/);
		// Camp de tipus float
		newInput.find(".float").keyfilter(/^[-+]?[0-9]*[.]?[0-9]*$/);		
	});
	
	// Eliminar múltiple
	$("#command").on("click", ".btn_eliminar", function(){
		var multiple = $(this).closest('.multiple');
		var formgroup = $(this).closest('.form-group');
		if (multiple.find(".form-group").size() > 2) {
			var label = multiple.find(".formgroup:first").find(".control-label").val();
			formgroup.remove();
			multiple.find(".form-group:first").find(".control-label").val(label);
			multiple.find(".form-group").each(function(index){
				$('input', this).each(function(){
					var input = $(this);
					if (input.attr("name") != null) {
						var name = input.attr("name");
						var name_pre = name.substr(0, name.lastIndexOf("["));
						var name_post = name.substr(name.lastIndexOf("]") + 2);
						input.attr({ 
							"id" : name_pre + "[" + index + "]" + name_post, 
							"name" : name_pre + "[" + index + "]" + name_post});
					}
				});
			});
		} else {
			formgroup.find("input").each(function(){
				switch (this.type) {
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
	});

	// Eliminar fila
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
	
	// Executar accions
	$("#command").on("click", ".btn_accio", function(){
		var accio = $(this).data(action);
		accioCampExecutar(this, accio);
	});
	
	// Funcionalitats concretes
	$("#command").on("click", ".btn_date", function(){
		$(this).closest(".date").focus();
	});
	$("#command").on("click", ".btn_date_pre", function(){
		$(this).next().focus();
	});
//	$("#command").on("change", ".termini", function(){
//		canviTermini(this);
//	});
});

function addField(idTable) {
	// TODO No se tiene en cuenta si una variable múltiple está dentro de una de registro
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
					var id_fin = id.substr(id.lastIndexOf("]")+1);
					var i = 1;
					while (document.getElementById(id_lim + "[" + i + "]" + id_fin)) {
						i = i + 1;
					}
					this.setAttribute("id", id_lim + "[" + i + "]" + id_fin);
					this.setAttribute("name", id_lim + "[" + i + "]" + id_fin);
				}
			});
		newTr.appendTo(tabla);
		newTr.find('button.btn_eliminar').click(function() {
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
	
function confirmar(form) {
	$("table").each(function(){
		if ($(this).hasClass("hide")) {
			$(this).remove();
		}
	});
	return true;
}
//function reorder(multiple) {
//	multiple.find
//}

//function canviTermini(input) {
//	var campId = input.id.substring(0, input.id.lastIndexOf("_"));
//	var anys = document.getElementById(campId + "_anys").value;
//	var mesos = document.getElementById(campId + "_mesos").value;
//	var dies = document.getElementById(campId + "_dies").value;
//	if (anys != "0" && mesos != "0" && dies != "") {
//		if (dies == "") {
//			dies = "0";
//			$("#" + campId + "_dies").val(dies);
//		}
//		$(campId).val(anys + "/" + mesos + "/" + dies);
//	} else {
//		$(campId).val("");
//	}
//}

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