
function webutilContextPath() {
	return '/helium';
}
function webutilModalTancarPath() {
	return webutilContextPath() + '/modal/tancar';
}
function webutilRefreshMissatges() {
	jQuery.ajaxSetup({async:false});
	$('#contingut-alertes').load(webutilContextPath() + "/nodeco/v3/missatges");
	jQuery.ajaxSetup({async:true});	
}

function webutilEsborrarAlertes(divAlertes) {
	if (!divAlertes) {		
		divAlertes = '#contingut-alertes';
	}
	$(divAlertes).empty();
}

function webutilAlertaSuccess(missatge, divAlertes) {
	if (!divAlertes) {
		divAlertes = '#contingut-alertes';		
	}
	var $div = $('<div>', {'class': 'alert alert-success'});
	$div.html('<span class="fa fa-check"></span>&nbsp;<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>');
	$div.append(missatge);	
	$(divAlertes).append($div);
	return $div;
}

function webutilAlertaWarning(missatge, divAlertes) {
	if (!divAlertes) {
		divAlertes = '#contingut-alertes';		
	}
	var $div = $('<div>', {'class': 'alert alert-warning'});
	$div.html('<span class="fa fa-warning"></span>&nbsp;<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>');
	$div.append(missatge);	
	$(divAlertes).append($div);
	return $div;
}

function webutilAlertaError(missatge, divAlertes) {
	if (!divAlertes) {
		divAlertes = '#contingut-alertes';		
	}
	var $div = $('<div>', {'class': 'alert alert-danger'});
	$div.html('<span class="fa fa-warning"></span>&nbsp;<button type="button" class="close-alertes" data-dismiss="alert" aria-hidden="true"><span class="fa fa-times"></span></button>');
	$div.append(missatge);	
	$(divAlertes).append($div);
	return $div;
}

function webutilModalAdjustHeight(iframe) {
	var $iframe = (iframe) ? $(iframe) : $(window.frameElement);
	var modalobj = $iframe.parent().parent().parent();
	var taraModal = $('.modal-header', modalobj).outerHeight() + $('.modal-footer', modalobj).outerHeight();
	var maxBodyHeight = $(window.top).height() - taraModal - 62;
	var htmlHeight = (iframe) ? $(iframe).contents().find("html").height() : document.documentElement.scrollHeight;
	if (htmlHeight > maxBodyHeight) {
		$iframe.height(maxBodyHeight + 'px');
		$('.modal-body', modalobj).css('height', (maxBodyHeight+5) + 'px');
		$iframe.contents().find("body").css('height', maxBodyHeight + 'px');
	} else {
		$iframe.parent().css('height', htmlHeight + 'px');
		$iframe.css('min-height', htmlHeight + 'px');
		$iframe.closest('div.modal-body').height((htmlHeight+5) + 'px');
	}
}

function webutilUrlAmbPrefix(url, prefix) {
	var absolutePath;
	if (url.indexOf('/') != 0)
		absolutePath = window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/') + 1) + url;
	else
		absolutePath = url;
	var prefixSenseBarra = prefix;
	if (prefixSenseBarra.indexOf('/') == 0)
		prefixSenseBarra = prefixSenseBarra.substring(1);
	if (prefixSenseBarra.indexOf('/') == prefixSenseBarra.length - 1)
		prefixSenseBarra = prefixSenseBarra.substring(0, prefixSenseBarra.length - 1);
	return absolutePath.substring(0, webutilContextPath().length) + '/' + prefixSenseBarra + absolutePath.substring(webutilContextPath().length);
}

/** Retorna true si ha estat l'usuari que ha apretat la tecla ESC o 
 * ha navegat cap a una altra pàgina.
 */
function userAborted(xhr) {
	return !xhr.getAllResponseHeaders();
}

/** Funció per descarregar un arxiu i refrescar missatges. També és possible passar una funció de callback com a paràmetre */
function webutilDownloadAndRefresh(arxiuUrl, event, callbackFunction) {

	// Fa la petició a la url de l'arxiu
	$('html').css('cursor', 'wait');
	$.get( arxiuUrl, { responseType: 'arraybuffer' })
        .success(function (response, status, xhr) {
        	// estableix el nom de la descàrrega i el tipus
			var filename = "";
            var disposition = xhr.getResponseHeader('Content-Disposition');
            if (disposition && disposition.indexOf('attachment') !== -1) {
                var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                var matches = filenameRegex.exec(disposition);
                if (matches != null && matches[1]) { 
                  filename = matches[1].replace(/['"]/g, '');
                }
            }
            // Crea un enllaç per obrir la descàrrega
            var blob = new Blob([response], { type: disposition });
            var link=document.createElement('a');
            link.href=window.URL.createObjectURL(blob);
            link.download= filename;
            (document.body || document.documentElement).appendChild(link);
            link.click();
		}).always(function(){
			$('html').css('cursor', 'default');
			if (callbackFunction)
				try {
					callbackFunction();
				} catch(e) {
					console.error("Error executant la funció de callback " + callbackFunction + ": " + e);
				}
			webutilRefreshMissatges();
		});
	// Atura els events de l'enllaç
	if (event != null) {
		event.preventDefault();
		event.stopPropagation();
	}
}

function webutilBase64DownloadAndRefresh(arxiuUrl, event, callbackFunction) {

	$("#overlay").show();
	// Fa la petició a la url de l'arxiu
	$.get( arxiuUrl, { responseType: 'arraybuffer' })
		.success(function (data, status, xhr) {
			// estableix el nom de la descàrrega i el tipus
			var b64Data = data;
			var contentType = xhr.getResponseHeader("Content-Type");
			var disposition = xhr.getResponseHeader('Content-Disposition');
			var filename = disposition.substring(disposition.lastIndexOf("=") + 1) || "download";

			// Processam les dades rebudes
			var sliceSize = 512;
			var byteCharacters = window.atob(b64Data);
			var byteArrays = [];

			for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
				var slice = byteCharacters.slice(offset, offset + sliceSize);
				var byteNumbers = new Array(slice.length);
				for (var i = 0; i < slice.length; i++) {
					byteNumbers[i] = slice.charCodeAt(i);
				}
				var byteArray = new Uint8Array(byteNumbers);
				byteArrays.push(byteArray);
			}
			// Crea un enllaç per obrir la descàrrega
			var blob = new Blob(byteArrays, { type: contentType });
			var link=document.createElement('a');
			link.href=window.URL.createObjectURL(blob);
			link.download= filename;
			(document.body || document.documentElement).appendChild(link);
			link.click();
		}).always(function(){
		if (callbackFunction)
			try {
				callbackFunction();
			} catch(e) {
				console.error("Error executant la funció de callback " + callbackFunction + ": " + e);
			}
		webutilRefreshMissatges();
		$("#overlay").hide();
	});
	// Atura els events de l'enllaç
	if (event != null) {
		event.preventDefault();
		event.stopPropagation();
	}
}

/** Funció per deshabilitar els camps d'un formulari. Els de text tornen readonly y la resta disabled */
function webutilDisableInputs(formulari) {
	$(':input', formulari).attr('readonly', true);
	$('select', formulari).attr('disabled', true);
	$('input[type=checkbox]', formulari).attr('disabled', 'disabled');
}
/** Funció per tornar a habilitar els camps d'un formulari. */
function webutilEnableInputs(formulari) {
	$(':input', formulari).removeAttr('readonly');
	$('select', formulari).removeAttr('disabled');
	$('input[type=checkbox]', formulari).removeAttr('disabled');
}


$(document).ajaxError(function(event, jqxhr, ajaxSettings, thrownError) {
	var message = "Error AJAX: [" + jqxhr.status + "] " + thrownError;
	/*var statusErrorMap = {
			'400': "Server understood the request, but request content was invalid.",
			'401': "Unauthorized access.",
			'403': "Forbidden resource can't be accessed.",
			'500': "Internal server error.",
			'503': "Service unavailable."
	};
	if (jqxhr.status) {
		message = statusErrorMap[jqxhr.status];
		if (!message) {
			message = "Unknown Error: (" + jqxhr.status + ", " + thrownError + ")";
		}
	} else if (thrownError == 'parsererror') {
		message = "Error.\nParsing JSON Request failed.";
	} else if (thrownError == 'timeout') {
		message = "Request Time out.";
	} else if (thrownError == 'abort') {
		message = "Request was aborted by the server";
	} else {
		message = "Unknown Error: (" + jqxhr.status + ", " + thrownError + ")";
	}*/
	if (!userAborted(jqxhr))
		console.error(message);
	else
		console.warn(message + ": User cancels request");
});

(function($) {

	$.fn.webutilClonarElementAmbInputs = function(clonId, clonValor) {
		var $contingutOrigen = $(this);
		$contingutOrigen.webutilDestroyInputComponents();
		var $contingutClonat = $contingutOrigen.clone(true);
		$contingutOrigen.webutilEvalInputComponents();
		$contingutClonat.webutilNetejarInputs();
		$contingutClonat.webutilNetejarErrorsCamps();
		var $inputClonat = ($contingutClonat.is(':input')) ? $contingutClonat : $(':input', $contingutClonat);
		$inputClonat.attr('id', clonId);
		if ($inputClonat.attr('type') == 'checkbox') {
			$inputClonat.prop('checked', (clonValor == true));
		} else {
			$inputClonat.css('width', '100%');
			//$contingutClonat.limitEvalInputComponents();
			$inputClonat.val(clonValor);
		}
		return $contingutClonat;
	};
	$.fn.webutilDestroyInputComponents = function() {
		/*$(this).limitSelect2Destroy();
		$(this).limitDatepickerDestroy();*/
	};
	$.fn.webutilEvalInputComponents = function() {
		/*$(this).limitSelect2Eval();
		$(this).limitDatepickerEval();*/
	};

	$.fn.webutilMostrarErrorsCamps = function(errors) {
		var focused = false;
		for (var i = 0; i < errors.length; i++) {
			var $input = $(':input[name="' + errors[i].camp + '"]', this);
			$input.attr('title', errors[i].missatge);
			$input.closest('.form-group').addClass('has-error has-feedback');
			$input.closest('.form-group').append('<span class="fa fa-warning form-control-feedback" aria-hidden="true" style="top:6px"/>');
			if (!focused) {
				$input.focus();
				focused = true;
			}
		}
	}
	$.fn.webutilNetejarErrorsCamps = function() {
		$(':input', this).each(function() {
			$(this).attr('title', '');
			$(this).closest('.form-group').removeClass('has-error has-feedback');
			$('span.form-control-feedback', $(this).closest('.form-group')).remove();
		});
	}

	$.fn.webutilNetejarInputs = function(options) {
		$(this).find('input:text, input:password, input:file, select, textarea').val('');
		$(this).find('input:radio, input:checkbox').removeAttr('checked').removeAttr('selected');
		$(this).find('select.select2-hidden-accessible').val(null).trigger("change");
	}

	$.fn.webutilConfirm = function() {
		$(this).click(function(e) {
			if (confirm($(this).data('confirm'))) {
				return true
			} else {
				e.stopImmediatePropagation();
				return false;
			}
		});
	}
	$.fn.webutilConfirmEval = function() {
		$('[data-confirm]', this).each(function() {
			if (!$(this).attr('data-confirm-eval')) {
				$(this).webutilConfirm();
				$(this).attr('data-confirm-eval', 'true');
			}
		});
	}

	$.fn.webutilAjax = function() {
		$(this).on('click', function() {
			var $element = $(this);
			$.ajax({
				type: "GET",
				url: webutilUrlAmbPrefix($element.attr("href"), '/ajax'),
				timeout: 30000,
				success: function() {
					webutilRefreshMissatges();
					// if((!$element.hasAttribute('data-reload-table') || $element.data('reloadTable') == "true") &&
					if ($element.closest('.dataTables_wrapper')) {
						var $dataTable = $('table.dataTable', $element.closest('.dataTables_wrapper'));
						$dataTable.webutilDatatable('refresh');
					// }
					// if ($element.hasAttribute('data-callback')) {
					// 	let callback = $element.data('callback');
					// 	var sep = callback.indexOf('(');
					// 	if ( sep != -1) {
					// 		callbackFunctionName = callback.substring(0, sep);
					// 		callbackFunctionParams = callback.substring(sep + 1, callback.lastIndexOf(')')).split(",");
					// 		// modalExecuteFunctionByName(callbackFunctionName, window, callbackFunctionParams, data);
					// 		let i = 0;
					// 		let args = [];
					// 		for(i = 0; i < callbackFunctionParams; i++)
					// 			args[i] = callbackFunctionParams[i].trim();
					// 		args[i] = data;
					// 		let namespaces = callbackFunctionName.split('.');
					// 		let func = namespaces.pop();
					// 		let context = window;
					// 		for(i = 0; i < namespaces; i++)
					// 			context = context[namespaces[i]];
					// 		if (context[func] === undefined)
					// 			return false
					// 		return context[func].apply(this, args);
					// 	}
					}
				}
		    });
			return false;
		});
	}
	$.fn.webutilAjaxEval = function() {
		$('[data-toggle="ajax"]', $(this)).each(function() {
			if (!$(this).attr('data-ajax-eval')) {
				$(this).webutilAjax();
				$(this).attr('data-ajax-eval', 'true');
			}
		});
	}

	$.fn.webutilBotonsTitol = function() {
		var $heading = $('.panel-heading', $(this).closest('.panel'))
		if ($heading) {
			$(this).css('float', 'right');
			$(this).css('width', '1000px');
			$(this).css('margin-top', '3px');
			$(this).appendTo($heading.find('#capcalera-botons'));
		}
	}
	$.fn.webutilBotonsTitolEval = function() {
		$('[data-toggle="botons-titol"]', $(this)).each(function() {
			if (!$(this).attr('data-botons-titol-eval')) {
				$(this).webutilBotonsTitol();
				$(this).attr('data-botons-titol-eval', 'true');
			}
		});
	}

	$(document).ready(function() {
		$('[data-confirm]', $(this)).each(function() {
			if (!$(this).attr('data-confirm-eval')) {
				$(this).webutilConfirm();
				$(this).attr('data-confirm-eval', 'true');
			}
		});
		$('[data-toggle="ajax"]', $(this)).each(function() {
			if (!$(this).attr('data-ajax-eval')) {
				$(this).webutilAjax();
				$(this).attr('data-ajax-eval', 'true');
			}
		});
		$('[data-toggle="botons-titol"]', $(this)).each(function() {
			if (!$(this).attr('data-botons-titol-eval')) {
				$(this).webutilBotonsTitol();
				$(this).attr('data-botons-titol-eval', 'true');
			}
		});
	});

}(jQuery));

jQuery.fn.highlight = function(pat) {
	function innerHighlight(node, pat) {
		var skip = 0;
		if (node.nodeType == 3) {
			var pos = node.data.toUpperCase().indexOf(pat);
			if (pos >= 0) {
				var spannode = document.createElement('span');
				spannode.className = 'highlight';
				var middlebit = node.splitText(pos);
				var endbit = middlebit.splitText(pat.length);
				var middleclone = middlebit.cloneNode(true);
				spannode.appendChild(middleclone);
				middlebit.parentNode.replaceChild(spannode, middlebit);
				skip = 1;
			}
		}
		else if (node.nodeType == 1 && node.childNodes && !/(script|style)/i.test(node.tagName)) {
			for (var i = 0; i < node.childNodes.length; ++i) {
				i += innerHighlight(node.childNodes[i], pat);
			}
		}
		return skip;
	}
	return this.each(function() {
		innerHighlight(this, pat.toUpperCase());
	});
};

jQuery.fn.removeHighlight = function() {
	function newNormalize(node) {
		for (var i = 0, children = node.childNodes, nodeCount = children.length; i < nodeCount; i++) {
			var child = children[i];
			if (child.nodeType == 1) {
				newNormalize(child);
				continue;
			}
			if (child.nodeType != 3) { continue; }
			var next = child.nextSibling;
			if (next == null || next.nodeType != 3) { continue; }
			var combined_text = child.nodeValue + next.nodeValue;
			new_node = node.ownerDocument.createTextNode(combined_text);
			node.insertBefore(new_node, child);
			node.removeChild(child);
			node.removeChild(next);
			i--;
			nodeCount--;
		}
	}

	return this.find("span.highlight").each(function() {
		var thisParent = this.parentNode;
		thisParent.replaceChild(this.firstChild, this);
		newNormalize(thisParent);
	}).end();
};