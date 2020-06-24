(function($) {
	$.fn.heliumModal = function(options) {
		return this.filter("div").each(function() {
			var settings = $.extend({
				refrescarTaula: false,
				refrescarAlertes: true,
				refrescarPagina: false,
				adjustWidth: false,
				adjustHeight: true,
				minHeight: false,
				maximize: false,
				callback: false,
				ajax: false,
				buttonContainerId: "modal-botons",
				buttonCloseClass: "modal-tancar",
				dataTable: null
			}, options);
			$(this).html(
					'<div class="modal fade" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">' +
					'	<div class="modal-dialog ' + ((settings.maximize) ? ' modal-maximize' : '') + ' modal-lg">' +
					'		<div class="modal-content">' +
					'			<div class="modal-header">' +
					'				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
					'				<h4 class="modal-title"></h4>' +
					'			</div>' +
					'			<div class="modal-body" style="padding:0">' +
					'				<iframe frameborder="0" height="100' + ((settings.maximize) ? '%' : '') + '" width="100%" scrolling="auto"></iframe>' +
					'			</div>' +
					'			<div class="modal-footer">' +
					'			</div>' +
					'		</div>' +
					'	</div>' +
					'</div>');
			var modalUrl = settings.modalUrl;
			if (modalUrl.lastIndexOf("http") !== 0) {
				if (modalUrl.indexOf("../") != -1)
					modalUrl = modalUrl.substr(0, modalUrl.lastIndexOf("../") + "../".length) + "modal/" + modalUrl.substr(modalUrl.lastIndexOf("../") + "../".length);
				else
					modalUrl = "modal/" + modalUrl;
			}
			var modalobj = $('div.modal', this);
			/*if (settings.maximize)
				modalobj.css('top', '1%');*/
			modalobj.on('show.bs.modal', function () {
				$('iframe', modalobj).empty();
				$('iframe', modalobj).attr(
						"src",
						modalUrl);
				$('iframe', modalobj).load(function() {
					// Copiar el titol de la modal
					var titol = $(this).contents().find("title").html();
					$('.modal-header h4', $(this).parent().parent()).html(titol);
					// Copiar botons
					/*$('.modal-footer *', $(this).parent().parent()).remove();
					var iframe = this;
					$('#' + settings.buttonContainerId + ' .btn', $(this).contents()).each(function(index) {
						var element = $(this);
						if (!element.parent().hasClass("outcomes")) {
							var clon = element.clone();
							if (clon.hasClass(settings.buttonCloseClass)) {
								clon.on('click', function () {
									$(iframe).parent().parent().parent().parent().data('modal-cancel', 'true');
									$(iframe).parent().parent().parent().parent().modal('hide');
									return false;
								});
							} else {
								clon.on('click', function () {
									element.click();
									return false;
								});
							}
							$('.modal-footer', $(iframe).parent().parent()).append(clon);
						}
					});*/
					modalRefrescarElements(
							this,
							settings);
					
					/*$('#' + settings.buttonContainerId + ' .outcomes', $(this).contents()).each(function(index) {
						var outcomes = $(this);
						var dropup = $("<div>", {
							"class": "btn-group dropup outcomes"
						});
						var dropup_button = $("<button type='button' class='btn btn-primary dropdown-toggle' data-toggle='dropdown'>" +
								"<span class='fa fa-thumbs-o-up'></span>&nbsp;" + outcomes.data("textfi") + "&nbsp;<span class='caret'></span>" +
								"</button>");
						dropup.append(dropup_button);
						var dropup_ul = $("<ul role='menu' class='dropdown-menu dropdown-menu-right'>");
						dropup.append(dropup_ul);
						outcomes.find("button").each(function(){
							var boto = $(this);
							var dropup_li = $("<li>");
							var dropup_li_a = $("<a>", {
								href: "javascript:void(0);",
								text: boto.val()
							});
							dropup_li_a.on('click', function() {
								boto.click();
								return false;
							});
							dropup_li.append(dropup_li_a);
							dropup_ul.append(dropup_li);
						});
						$('.modal-footer', $(iframe).parent().parent()).append(dropup);
					});*/
					// Ajustar tamany
					/*if (settings.adjustHeight) {
						var contentHeight = $(this).contents().find("html").outerHeight();
						$(this).height(contentHeight + 'px');
					}
					if (settings.adjustWidth) {
						var contentWidth = $(this).contents().find("html").outerWidth();
						var modalobj = $(this).parent().parent().parent();
						modalobj.css('width', contentWidth + 'px');
					}
					if (settings.maximize) {
						var contentHeight = $(this).contents().find("html").outerHeight();
						var modalobj = $(this).parent().parent().parent();
						var taraModal = $('.modal-header', modalobj).outerHeight() + $('.modal-footer', modalobj).outerHeight();
						var maxBodyHeight = $(window).height() - taraModal - 30;
						$(this).height(maxBodyHeight + 'px');
						$('.modal-body', modalobj).css('height', maxBodyHeight + 'px');
					}
					if (settings.minHeight) {
						modalAdjustHeight($('iframe', modalobj), settings.minHeight);
					}*/
				});
			});
			modalobj.on('hide.bs.modal', function () {
				/*if (!$(this).data('modal-cancel')) {
					$(this).removeData('modal-cancel');
					if (settings.modalCloseFunction) {
//						settings.modalCloseFunction();
					} else {
						if (settings.refrescarPagina) {
							window.parent.location.reload();
						}
						if (settings.refrescarTaula && settings.dataTable) {
							var taula = settings.dataTable;
							if (!(taula === null)) {
								var refrescat = false;
								$('.dataTables_paginate li', taula.parent()).each(function() {
									if ($(this).hasClass('active')) {
										$('a', this).click();
										refrescat = true;
									}
								});
								if (!refrescat)
									taula.dataTable().fnDraw();
							}
						}
						
					}
				}*/
				if (settings.refrescarPagina) {
					window.parent.location.reload();
				} else {
					if (settings.refrescarAlertes && settings.alertesRefreshUrl) {
						$.ajax({
							url: settings.alertesRefreshUrl,
							async: false,
							timeout: 20000,
							success: function (data) {
								$('#contingut-alertes *').remove();
								$('#contingut-alertes').append(data);
							},
							error: modalAjaxErrorFunction
					    });
					}
					if (settings.refrescarTaula && settings.dataTable) {
						var taula = settings.dataTable;
						if (!(taula === null)) {
							var refrescat = false;
							$('.dataTables_paginate li', taula.parent()).each(function() {
								if ($(this).hasClass('active')) {
									$('a', this).click();
									refrescat = true;
								}
							});
							if (!refrescat) {
								taula.dataTable().fnDraw();
							}
						}
					}
				}
				if (settings.callback) {
					var scb = settings.callback;
					var sep = scb.indexOf('(');
					if ( sep != -1) {
						callbackFunctionName = scb.substring(0, sep);
						callbackFunctionParams = scb.substring(sep + 1, scb.lastIndexOf(')')).split(",");
						modalExecuteFunctionByName(callbackFunctionName, window, callbackFunctionParams, true);
					}
				}
			});
			modalobj.modal({show:true});
		});
	};
	
	$.fn.heliumEvalLink = function(options) {
		return this.filter("a").each(function() {
			var settings = $.extend({
				refrescarTaula: false,
				refrescarAlertes: true,
				refrescarPagina: false,
				adjustWidth: false,
				adjustHeight: true,
				minHeight: false,
				maximize: false,
				callback: false,
				ajax: false,
				buttonContainerId: "modal-botons",
				buttonCloseClass: "modal-tancar",
				dataTable: null
			}, options);
			$(this).on('click', function() {
				var confirmat = true;
				if ($(this).data('rdt-link-confirm'))
					confirmat = confirm($(this).data('rdt-link-confirm'));
				var ajax = ($(this).data("rdt-link-ajax") != undefined) ? $(this).data("rdt-link-ajax") === true : false;
				var modal = ($(this).data("rdt-link-modal") != undefined) ? $(this).data("rdt-link-modal") === true : false;
				var callback = ($(this).data("rdt-link-callback") != undefined) ? $(this).data("rdt-link-callback") : false;
				var refrescarPaginaFunction = function() {
					window.parent.location.reload();
				};
				var refrescarAlertesFunction = function() {
					$.ajax({
						url: settings.alertesRefreshUrl,
						async: false,
						timeout: 20000,
						success: function (data) {
							$('#contingut-alertes *').remove();
							$('#contingut-alertes').append(data);
						},
						error: modalAjaxErrorFunction
				    });
				};
				var executeCallbackFunction = function(data) {
					var scb = callback;
					var sep = scb.indexOf('(');
					if ( sep != -1) {
						callbackFunctionName = scb.substring(0, sep);
						callbackFunctionParams = scb.substring(sep + 1, scb.lastIndexOf(')')).split(",");
						modalExecuteFunctionByName(callbackFunctionName, window, callbackFunctionParams, data);
					}
				};
				if (confirmat) {
					if (ajax) {
						var ajaxUrl = $(this).attr("href");
						if (ajaxUrl.indexOf("../") != -1)
							ajaxUrl = ajaxUrl.substr(0, ajaxUrl.lastIndexOf("../") + "../".length) + "ajax/" + ajaxUrl.substr(ajaxUrl.lastIndexOf("../") + "../".length);
						else
							ajaxUrl = "ajax/" + ajaxUrl;
						$.ajax({
							type: "GET",
							url: ajaxUrl,
							async: false,
							timeout: 20000,
							success: function( data ) {
								if (settings.refrescarPagina)
									refrescarPaginaFunction();
								if (settings.refrescarAlertes && settings.alertesRefreshUrl)
									refrescarAlertesFunction();
								if (callback)
									executeCallbackFunction(data);
							},
							error: modalAjaxErrorFunction,
							complete: $(this).tooltip('hide')
					    });
						return false;
					} else if (modal) {
						/*var modalCloseFunction = function() {
							if (settings.refrescarPagina)
								refrescarPaginaFunction();
//							if (settings.refrescarAlertes && settings.alertesRefreshUrl)
//								refrescarAlertesFunction();
//							if (callback)
//								executeCallbackFunction(true);
						};*/
						var modalDivId = $(this).attr('id') + "_modal";
						if ($('#' + modalDivId).length == 0)
							$('body').append('<div id="' + modalDivId + '"></div>');
						$('#' + modalDivId).heliumModal({
							modalUrl: $(this).attr("href"),
							maximize: $(this).data("rdt-link-modal-maximize"),
							minHeight:  $(this).data("rdt-link-modal-min-height"),
							callback:  $(this).data("rdt-link-callback"),
							ajax:  $(this).data("rdt-link-ajax"),
							dataTable: settings.dataTable,
							refrescarTaula: settings.refrescarTaula,
							refrescarPagina: settings.refrescarPagina,
							refrescarAlertes: settings.refrescarAlertes,
							alertesRefreshUrl: settings.alertesRefreshUrl
						});
						return false;
					} else {
						return true;
					}
				} else {			
					return false;
				}
			});
		});
	};
}(jQuery));

function modalTancar(iframe, refrescar) {
	$(iframe).parent().parent().parent().parent().data('modal-cancel', 'true');
	$(iframe).parent().parent().parent().parent().modal('hide');
	if (refrescar)	
		window.parent.location.reload();
	//else if ('function' === typeof webutilRefreshMissatges)
	//	webutilRefreshMissatges();
}

function modalAdjustHeight(iframe, height) {
	$(iframe).parent().css('height', height + 15 + 'px');
	$(iframe).css('min-height', height + 'px');
}

function modalAdjustMinHeight(iframe, height) {
	altPare = $(iframe).parent().css('min-height');
	if (altPare == undefined)
		altPare = 0;
	else
		altPare = parseInt(altPare.substring(0, altPare.length - 2));
	if (height > altPare)
		$(iframe).parent().css('min-height', height + 15 + 'px');
	$(iframe).css('min-height', height + 'px');
}

function modalRefrescarElements(
		iframe,
		settings) {
	// Refrescar titol
	var titol = $(iframe).contents().find("title").html();
	$('.modal-header h4', $(iframe).parent().parent()).html(titol);
	// Refrescar botons
	$('.modal-footer *', $(iframe).parent().parent()).remove();
	$('#' + settings.buttonContainerId + ' .btn', $(iframe).contents()).each(function(index) {
		var element = $(this);
		var clon = element.clone();
		if (clon.hasClass(settings.buttonCloseClass)) {
			clon.on('click', function () {
				$(iframe).parent().parent().parent().parent().data('modal-cancel', 'true');
				$(iframe).parent().parent().parent().parent().modal('hide');
				return false;
			});
		} else {
			clon.on('click', function () {
				element.click();
				return false;
			});
		}
		if ($('.modal-footer [value="'+element.attr('id')+'"]', $(iframe).parent().parent()).length == 0)
			$('.modal-footer', $(iframe).parent().parent()).append(clon);
	});
	$('#' + settings.buttonContainerId, $(iframe).contents()).hide();
	// Ajustar tamany
	var contentHeight = $(iframe).contents().find("html").outerHeight();
	var modalobj = $(iframe).parent().parent().parent();
	var taraModal = $('.modal-header', modalobj).outerHeight() + $('.modal-footer', modalobj).outerHeight();
	var maxBodyHeight = $(window.top).height() - taraModal - 30;
	if (settings.adjustHeight) {
		$(iframe).height(contentHeight + 'px');
		$(iframe).parent().css('height', Math.min(contentHeight + 15, maxBodyHeight - 35) + 'px');
		$(iframe).parent().css('max-height', maxBodyHeight + 'px');
	}
	if (settings.adjustWidth) {
		var contentWidth = $(iframe).contents().find("html").outerWidth();
		modalobj.css('width', contentWidth + 'px');
	}
	if (settings.maximize) {
		/*if (contentHeight > maxBodyHeight) {
			// Si el contingut és més alt que la finestra
			$(this).height(maxBodyHeight + 'px');
			$('.modal-body', modalobj).css('height', maxBodyHeight + 'px');
		} else {
			// Si el contingut cap a dins la finestra
			$(this).height(contentHeight + 'px');
			$('.modal-body', modalobj).css('height', contentHeight + 'px');
		}*/
		$(iframe).height(maxBodyHeight + 'px');
		$(iframe).css('margin-bottom', '-5px');
		$(iframe).parent().css('max-height', (maxBodyHeight) + 'px');
		$('.modal-body', modalobj).css('height', (maxBodyHeight) + 'px');
	}
	if (settings.minHeight) {
		modalAdjustMinHeight($(iframe), settings.minHeight);
	}
	// Elimina els tool tips que hagin pogut quedar
	$('div.tooltip').remove();
}

function modalAjaxErrorFunction(jqXHR, exception) {
	if (jqXHR.status === 0) {
        alert('Not connected.\n Verify network.');
    } else if (jqXHR.status == 404) {
        alert('Requested page not found [404].');
    } else if (jqXHR.status == 500) {
        alert('Internal server error [500].');
    } else if (exception === 'parsererror') {
        alert('Requested JSON parse failed.');
    } else if (exception === 'timeout') {
        alert('Timeout error.');
    } else if (exception === 'abort') {
        alert('Ajax request aborted.');
    } else {
        alert('Unknown error:\n' + jqXHR.responseText);
    }
}

function modalExecuteFunctionByName(functionName, context /*, args */) {
	var argsarr = [].slice.call(arguments).splice(2);
	var args = argsarr[0];
	for (var i = 0; i < args.length; i++)
		args[i] = args[i].trim();
	args.push(argsarr[1]);
	var namespaces = functionName.split(".");
	var func = namespaces.pop();
	for(var i = 0; i < namespaces.length; i++) {
		context = context[namespaces[i]];
	}
	if (context[func] === undefined)
		return false;
	return context[func].apply(this, args);
}