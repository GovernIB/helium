/**
 * Funcions a executar al montar un formulari de tasca a la versió 3
 */
function desplegableObtenirParams(input, value) {
	var dataObj = {};
	if (value) {
		dataObj['q'] = value;
	}
	var regPrefix = '';
	if (input.attr('id').indexOf('.') != -1) 
		regPrefix = input.attr('id').substring(0, input.attr('id').indexOf('.'));
	var params = '';
	$('input', input.parents('form')).each(function() {
		if ($(this).val()) {
			var attrId = $(this).attr('id');
			if (attrId) {
				if (attrId.indexOf('.') == -1) {
					params += attrId + ':' + $(this).val() + ',';
				} else if (regPrefix.length > 0 && attrId.indexOf(regPrefix) == 0) {
					params += (attrId.substring(attrId.indexOf('.') + 1)) + ':' + $(this).val() + ',';
				}
			}
		}
	});
	if (params.length > 0)
		params = params.substring(0, params.length);
	if (params.length > 0)
		dataObj['valors'] = params;
	return dataObj;
}
function desplegableInitSeleccio(url, element, callback) {
	var registreCampId = $(element).closest('table').data('registre-id');
	var registreIndex = $(element).closest('tr').index();
	var ajaxData;
	if (registreCampId) {
		ajaxData = {
            	registreCampId: registreCampId,
            	registreIndex: registreIndex
            };
	}
	if ($(element).val()) {
		$.ajax(url, {
            dataType: "json",
            data: ajaxData
        }).done(function(data) {
        	if (data.length > 0)
        		callback({id: data[0].codi, text: data[0].nom});
        });
	}
}
function initSuggest(element) {
	var input = $(element);
	input.select2({
	    minimumInputLength: 3,
	    width: '100%',
	    allowClear: true,
	    ajax: {
	        url: function(value) {
	        	return input.data("urlconsultallistat");
	        },
	        dataType: 'json',
	        data: function (value) {
	        	return desplegableObtenirParams(input, value);
	        },
	        results: function(data, page) {
	        	var results = [];
	        	for (var i = 0; i < data.length; i++) {
	        		results.push({id: data[i].codi, text: data[i].nom});
	        	}
	            return {results: results};
	        }
	    },
	    initSelection: function(element, callback) {
	    	var ajaxUrl = encodeURI(input.data("urlconsultainicial") + "/" + $(element).val());
	    	desplegableInitSeleccio(ajaxUrl, element, callback);
	    },
	}).on('change', function() {
		var campName = $(this).attr('name');
		$('input.suggest,input.seleccio', input.parents('form')).each(function() {
			if ($(this).data('campparams') == campName) {
				$(this).select2("val", "");
			}
		});
	});
}
var opcionsSeleccio = [];
function initSeleccio(element) {
	var input = $(element);
	input.select2({
		width: '100%',
		placeholder: input.data("placeholder"),
		allowClear: true,
		data: function () {
			var carregant = input.data('select2-refresh') == 'true';
			var refrescar = input.data('select2-refresh') != 'false';
			if (refrescar) {
				$.ajax({
					url: input.data("urlconsultallistat"),
					data: desplegableObtenirParams(input),
					dataType: 'json',
					success: function(data) {
						resposta = [];
						for (i = 0; i < data.length; i++) {
							resposta[i] = {
								id: data[i].codi,
								text: data[i].nom
							};
						}
						opcionsSeleccio[input.attr('id')] = resposta;
						input.data('select2-refresh', 'false');
						var select2 = input.data('select2');
						if (select2.opened()) {
							input.select2("close");
							input.select2("open");
						}
					},
					async: true
				});
			}
			return {results: opcionsSeleccio[input.attr('id')]};
		},
		/*ajax: {
	        url: function(value) {
	        	return input.data("urlconsultallistat");
	        },
	        dataType: 'json',
	        data: function () {
	        	return desplegableObtenirParams(input);
	        },
	        results: function(data) {
	        	var results = [];
	        	for (var i = 0; i < data.length; i++) {
	        		results.push({id: data[i].codi, text: data[i].nom});
	        	}
	            return {results: results};
	        }
	    },*/
	    initSelection: function (element, callback) {
	    	var ajaxUrl = encodeURI(input.data("urlconsultainicial") + "/" + $(element).val());
	    	desplegableInitSeleccio(ajaxUrl, element, callback);
	    },
	}).on('change', function () {
		var regPrefix = '';
		if (input.attr('id').indexOf('.') != -1) 
			regPrefix = input.attr('id').substring(0, input.attr('id').indexOf('.'));
		var campName = (regPrefix) ? $(this).attr('name').substring(regPrefix.length + 1) : $(this).attr('name');
		console.log("regPrefix: " + regPrefix + ", campName: " + campName);
		$('input.suggest,input.seleccio', input.parents('form')).each(function() {
			var thisPrefix = '';
			if ($(this).attr('id').indexOf('.') != -1) 
				thisPrefix = $(this).attr('id').substring(0, $(this).attr('id').indexOf('.'));
			if (thisPrefix == regPrefix) {
				console.log("verificant: " + campName + ", " + $(this).data('campparams'));
				var campParams;
				if ($(this).data('campparams'))
					campParams = $(this).data('campparams').split(',');
				if ($.inArray(campName, campParams) != -1) {
					$(this).select2("val", "");
					$(this).removeData('select2-refresh');
					console.log("removeData: " + $(this).attr("id"))
				}
			}
		});
	});
}

$(function() {
	if (typeof String.prototype.endsWith !== 'function') {
	    String.prototype.endsWith = function(suffix) {
	        return this.indexOf(suffix, this.length - suffix.length) !== -1;
	    };
	}
	var action = $("#command").attr('action');
	$("#command").attr('action', cleanAction($("#command").attr('action')));
	// Ajustaments per a cada tipus de camp
	$(".price").priceFormat({
		prefix: '',
		centsSeparator: ',',
	    thousandsSeparator: '.',
	    allowNegative: true
	});
	//$(".price").setMask({mask:'99,999.999.999.999',type:'reverse',defaultValue:'+'});
	$(".date").mask("99/99/9999").datepicker({language: 'ca', autoclose: true, dateFormat: "dd/mm/yy"});
	$(".btn_date").click(function(){
		$(this).prev(".date").trigger("focus");
	});
	$(".termini").each(function(){
		$(this).select2({
		    width: 'resolve',
		    allowClear: true,
		    minimumResultsForSearch: 10
		});
	});
	$(".termdia").keyfilter(/^[-+]?[0-9]*$/);
	$(".enter").keyfilter(/^[-+]?[0-9]*$/);
	$(".float").keyfilter(/^[-+]?[0-9]*[.]?[0-9]*$/);
	$(".suggest").each(function() {
		initSuggest(this);
	});
	$(".seleccio").each(function() {
		initSeleccio(this);
	});
	// Camp múltiple: afegir
	$("#command").on("click", ".btn_multiple", function() {
		var previousInput = $(this).closest('.form-group').prev();
		var newInput = previousInput.clone();
		$('div.select2-container', newInput).remove();
		$('input, textarea, select', newInput).each(function(){
			var input = $(this);
			input.val('');
			input.removeAttr("tabindex");
			input.removeClass("select2-offscreen");
			input.removeAttr("checked");
			if (input.attr("id") != null) {
				var id = input.attr("id");
				var id_pre = id.substr(0, id.lastIndexOf("["));
				var id_post = id.substr(id.lastIndexOf("]") + 1);
				var index = parseInt(id.substr(id.lastIndexOf("[") + 1, id.lastIndexOf("]"))) + 1;
				input.attr({ 
					"id" : id_pre + "[" + index + "]" + id_post
				});
			}
			if (input.attr("name") != null) {
				var nom = input.attr("name");
				if (nom.indexOf("[") > -1) {
					var nom_pre = nom.substr(0, nom.lastIndexOf("["));
					var nom_post = nom.substr(nom.lastIndexOf("]") + 1);
					var index = parseInt(nom.substr(nom.lastIndexOf("[") + 1, nom.lastIndexOf("]"))) + 1;
					input.attr({ 
						"name" : nom_pre + "[" + index + "]" + nom_post
					});
				}
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
			    allowNegative: true
			});
		//newInput.find(".price").setMask({mask:'99,999.999.999.999',type:'reverse',defaultValue:'+'});
		// Camp de tipus date
		newInput.find(".date").mask("99/99/9999").datepicker({language: 'ca', autoclose: true, dateFormat: "dd/mm/yy"});
		newInput.find(".btn_date").click(function(){
			$(this).prev(".date").trigger("focus");
		});
		// Camp de tipus termini
		newInput.find(".termdia").keyfilter(/^[-+]?[0-9]*$/);
		newInput.find(".termini").each(function(){
			$(this).select2({
			    width: 'resolve',
			    allowClear: true,
			    minimumResultsForSearch: 10
			});
		});
		// Camp de tipus sencer
		newInput.find(".enter").keyfilter(/^[-+]?[0-9]*$/);
		// Camp de tipus float
		newInput.find(".float").keyfilter(/^[-+]?[0-9]*[.]?[0-9]*$/);		
		// Camp de tipus suggest
		newInput.find(".suggest").each(function() {
			$(this).val('');
			initSuggest(this);
		});
		// Camp de tipus seleccio
		newInput.find(".seleccio").each(function() {
			$(this).val('');
			initSeleccio(this);
		});
	});
	$(".validada, .formext").each(function(index){
		validado(true);
	});
	// Camp múltiple: eliminar
	$("#command").on("click", ".btn_eliminar", function() {
		var multiple = $(this).closest('.multiple');
		var inputgroupmultiple = $(this).closest('.input-group-multiple');
		if (multiple.find(".input-group-multiple").size() > 1) {
			var label = multiple.find(".input-group-multiple:first").find(".control-label").val();
			inputgroupmultiple.remove();
			multiple.find(".input-group-multiple:first").find(".control-label").val(label);
			multiple.find(".input-group-multiple").each(function(index){
				$('input, textarea, select', this).each(function(){
					var input = $(this);
					if (input.attr("id") != null) {
						var id = input.attr("id");
						var id_pre = id.substr(0, id.lastIndexOf("["));
						var id_post = id.substr(id.lastIndexOf("]") + 1);
						input.attr({"id" : id_pre + "[" + index + "]" + id_post});
					}
					if (input.attr("name") != null) {
						var nom = input.attr("name");
						if (nom.indexOf("[") > -1) {
							var nom_pre = nom.substr(0, nom.lastIndexOf("["));
							var nom_post = nom.substr(nom.lastIndexOf("]") + 1);
							input.attr({"name" : nom_pre + "[" + index + "]" + nom_post});
						}
					}
					if (index == 0 && input.closest('.input-group-multiple').hasClass('pad-left-col-xs-3')) {
						input.closest('.input-group-multiple').removeClass('pad-left-col-xs-3');
					}
				});
			});
		} else {
			inputgroupmultiple.find("input").each(function(){
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
			inputgroupmultiple.find("textarea, select").each(function(){
				$(this).val('');
			});
		}
	});
	// Eliminar fila
	$("#command").on("click", ".eliminarFila", function() {
		var table = $(this).closest('table');
		var tr = $(this).closest('tr');
		if (table.find('tbody tr').size() < 2) {
			limpiarFila(tr);
		} else {
			tr.remove();
			// Renumerar filas
			table.find("tr.multiple").each(function(index){
				$('input, textarea, select', this).each(function(){
					var input = $(this);
					if (input.attr("id") != null) {
						var id = input.attr("id");
						var id_pre = id.substr(0, id.lastIndexOf("["));
						var id_post = id.substr(id.lastIndexOf("]") + 1);
						input.attr({"id" : id_pre + "[" + index + "]" + id_post});
					}
					if (input.attr("name") != null) {
						var nom = input.attr("name");
						if (nom.indexOf("[") > -1) {
							var nom_pre = nom.substr(0, nom.lastIndexOf("["));
							var nom_post = nom.substr(nom.lastIndexOf("]") + 1);
							input.attr({"name" : nom_pre + "[" + index + "]" + nom_post});
						}
					}
				});
			});
		}
	});
	
	// Funcionalitats concretes
	$("#command").on("click", ".btn_date", function(){
		$(this).closest(".date").focus();
	});
	$("#command").on("click", ".btn_date_pre", function(){
		$(this).next().focus();
	});
	$("#command").on("change", ".checkboxmul", function() {
		if($(this).is(":checked")) {
			$(this).prev().val(true);
		} else {
			$(this).prev().val(false);
		}
	});
	$(".btn_accio").click(function() {
		if (confirm($(this).data("confirmacio"))) {
			
			$("#command").attr('action', $("#command").attr('action') + "/accio");
			$("#command").append('<input type="hidden" id="accioCamp" name="accioCamp" value="'+$(this).data("action")+'"/>');
			var boto = $(this);
			setTimeout(function() {
				boto.attr('disabled', true);
				$('button[name="accio"]',parent.document).prop('disabled',true);
				$('button[name="accio"]').prop('disabled',true);
				boto.prepend('<i class="fa fa-circle-o-notch fa-spin"></i>');
			}, 1);
			return true;
		}
		return false;
	});
	$('#boto-formext').click(function() {
		$.ajax({
			url: $(this).attr('href'),
			async: false,
			timeout: 20000,
			success: function (data) {
				var dialogWidth = parseInt(data.width);
				var dialogHeight = parseInt(data.height);
				$('<iframe id="formExtern" src="' + data.url + '"/>').dialog({
					title: 'Formulari extern',
	                autoOpen: true,
	                modal: true,
	                autoResize: false,
	                width: dialogWidth,
	                height: dialogHeight,
	                close: function() {
	                	window.location.href = window.location.href;
	                	//document.location.reload();
					}
	            }).width(dialogWidth - 24);
			},
			error: modalAjaxErrorFunction
	    });
		return false;
	});
});

function validado(validar) {
	$('#command input[type=text]').each(function(){
		$(this).attr("disabled",validar);
	});
	$('#command textarea').each(function(){
		$(this).attr("disabled",validar);
	});
	$('#command select').each(function(){
		$(this).attr("disabled",validar);
	});
	$('#command input[type=checkbox]').each(function(){
		$(this).attr("disabled",validar);
	});
	$('#command span').each(function(){
		$(this).attr("disabled",validar);
		if (validar)
			$(this).unbind("click");
		else
			$(this).bind("click");
	});
	$('#command #guardarValidarTarea button').each(function(){
		$(this).attr("disabled",false);
	});
}

function cleanAction(action) {
	if (typeof action !== 'undefined' && !action.endsWith("/form")) {
		var pos = action.lastIndexOf("/form")
		if (pos != -1)
			return action.substring(0, action.lastIndexOf("/form") + 5);
	}
	return action;
}
function addField(idTable) {
	tabla = $('#' + idTable);
	tr = $('tr:last', tabla);
	var newTr = tr.clone();
	$('.select2-container', newTr).remove();
	limpiarFila(newTr);
	$('input, textarea, select', newTr).each(function(indice, valor){
		var input = $(this);
		input.removeAttr("tabindex");
		input.removeClass("select2-offscreen");
		input.removeAttr("checked");
		if (input.attr("id") != null) {
			var id = input.attr("id");
			var id_pre = id.substr(0, id.lastIndexOf("["));
			var id_post = id.substr(id.lastIndexOf("]") + 1);
			var index = parseInt(id.substr(id.lastIndexOf("[") + 1, id.lastIndexOf("]"))) + 1;
			input.attr({ 
				"id" : id_pre + "[" + index + "]" + id_post
			});
		}
		if (input.attr("name") != null) {
			var nom = input.attr("name");
			if (nom.indexOf("[") > -1) {
				var nom_pre = nom.substr(0, nom.lastIndexOf("["));
				var nom_post = nom.substr(nom.lastIndexOf("]") + 1);
				var index = parseInt(nom.substr(nom.lastIndexOf("[") + 1, nom.lastIndexOf("]"))) + 1;
				input.attr({ 
					"name" : nom_pre + "[" + index + "]" + nom_post
				});
			}
		}
	});
	newTr.appendTo(tabla);
	// Camp de tipus price
	
	newTr.find(".price").priceFormat({
			prefix: '',
			centsSeparator: ',',
		    thousandsSeparator: '.',
		    allowNegative: true
		});
	//newTr.find(".price").setMask({mask:'99,999.999.999.999',type:'reverse',defaultValue:'+'});
	// Camp de tipus date
	newTr.find(".date").mask("99/99/9999").datepicker({language: 'ca', autoclose: true, dateFormat: "dd/mm/yy"});
	newTr.find(".btn_date").click(function(){
		$(this).prev(".date").trigger("focus");
	});
	// Camp de tipus termini
	newTr.find(".termdia").keyfilter(/^[-+]?[0-9]*$/);
	newTr.find(".termini").each(function(){
		$(this).select2({
		    width: 'resolve',
		    allowClear: true,
		    minimumResultsForSearch: 10
		});
	});
	// Camp de tipus enter
	newTr.find(".enter").keyfilter(/^[-+]?[0-9]*$/);
	// Camp de tipus float
	newTr.find(".float").keyfilter(/^[-+]?[0-9]*[.]?[0-9]*$/);		
	// Camp de tipus suggest
	newTr.find(".suggest").each(function() {
		$(this).val('');
		initSuggest(this);
	});
	// Camp de tipus seleccio
	newTr.find(".seleccio").each(function() {
		$(this).val('');
		initSeleccio(this);
	});
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

function limpiarFila(tr) {
	tr.find(':input').each(function() {
		switch (this.type) {
		case 'checkbox':
		case 'radio':
			this.checked = false;
			break;
		default:
			if ($(this).hasClass("select2-offscreen")) {
				$(this).select2("val", "");
			} else {
				$(this).val('');
			}
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

function campOnFocus(camp) {
	$("#command").find("[data-action='" + camp + "']").focus();
}
