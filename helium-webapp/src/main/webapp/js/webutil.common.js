
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

function webutilModalAdjustHeight() {
	debugger;
	$html = $(document.documentElement);
	$iframe = $(window.frameElement);
	var modalobj = $iframe.parent().parent().parent();
	var taraModal = $('.modal-header', modalobj).outerHeight() + $('.modal-footer', modalobj).outerHeight();
	var maxBodyHeight = $(window.top).height() - taraModal - 70;
	var pixelsCorreccio = 15;
	var bodyHeight = $(this).contents().find("body").outerHeight() + pixelsCorreccio;
	$(iframe).height(bodyHeight + 'px');
	var modalBodyHeight = (bodyHeight > maxBodyHeight) ? (maxBodyHeight + 5) : (bodyHeight + 5);
	$('div.modal-body', modalobj).height(modalBodyHeight + 'px');
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

/** Funció per descarregar un arxiu i refrescar missatges */
function webutilDownloadAndRefresh(arxiuUrl, event) {

	// Fa la petició a la url de l'arxiu
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
			webutilRefreshMissatges();
		});
	// Atura els events de l'enllaç
	if (event != null) {
		event.preventDefault();
		event.stopPropagation();
	}
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
		alert(message);
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
				timeout: 10000,
				success: function() {
					webutilRefreshMissatges();
					if ($element.closest('.dataTables_wrapper')) {
						var $dataTable = $('table.dataTable', $element.closest('.dataTables_wrapper'));
						$dataTable.webutilDatatable('refresh');
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
