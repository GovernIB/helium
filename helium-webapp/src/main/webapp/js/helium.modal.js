(function($) {
	$.fn.heliumModal = function(options) {
		return this.filter("div").each(function() {
			var settings = $.extend({
				refrescarTaula: false,
				refrescarAlertes: true,
				refrescarPagina: false,
				adjustWidth: false,
				adjustHeight: true,
				maximize: false,
				buttonContainerId: "modal-botons",
				buttonCloseClass: "modal-tancar"
			}, options);
			var ajaxErrorFunction = function (jqXHR, exception) {
				if (jqXHR.status === 0) {
	                alert('Not connected.\n Verify Network.');
	            } else if (jqXHR.status == 404) {
	                alert('Requested page not found. [404]');
	            } else if (jqXHR.status == 500) {
	                alert('Internal Server Error [500].');
	            } else if (exception === 'parsererror') {
	                alert('Requested JSON parse failed.');
	            } else if (exception === 'timeout') {
	                alert('Time out error.');
	            } else if (exception === 'abort') {
	                alert('Ajax request aborted.');
	            } else {
	                alert('Uncaught Error.\n' + jqXHR.responseText);
	            }
			};
			$(this).html(
					'<div class="modal fade" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">' +
					'	<div class="modal-dialog ' + ((settings.maximize) ? ' modal-maximize' : '') + ' modal-lg">' +
					'		<div class="modal-content">' +
					'			<div class="modal-header">' +
					'				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
					'				<h4 class="modal-title"></h4>' +
					'			</div>' +
					'			<div class="modal-body">' +
					'				<iframe frameborder="0" height="100' + ((settings.maximize) ? '%' : '') + '" width="99.6%"></iframe>' +
					'			</div>' +
					'			<div class="modal-footer">' +
					'			</div>' +
					'		</div>' +
					'	</div>' +
					'</div>');
			var modalUrl = settings.modalUrl;
			if (modalUrl.indexOf("../") != -1)
				modalUrl = modalUrl.substr(0, modalUrl.lastIndexOf("../") + "../".length) + "modal/" + modalUrl.substr(modalUrl.lastIndexOf("../") + "../".length);
			else
				modalUrl = "modal/" + modalUrl;
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
					$('.modal-footer *', $(this).parent().parent()).remove();
					var iframe = this;
					$('#' + settings.buttonContainerId + ' .btn', $(this).contents()).each(function(index) {
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
						$('.modal-footer', $(iframe).parent().parent()).append(clon);
					});
					$('#' + settings.buttonContainerId, $(this).contents()).hide();
					// Ajustar tamany
					if (settings.adjustHeight) {
						var contentHeight = $(this).contents().find("html").height();
						$(this).height(contentHeight + 'px');
					}
					if (settings.adjustWidth) {
						var contentWidth = $(this).contents().find("html").width();
						var modalobj = $(this).parent().parent().parent();
						modalobj.css('width', contentWidth + 'px');
					}
					if (settings.maximize) {
						var contentHeight = $(this).contents().find("html").height();
						//var contentHeight = this.contentWindow.document.body.offsetHeight;
						var modalobj = $(this).parent().parent().parent();
						var taraModal = $('.modal-header', modalobj).height() + $('.modal-footer', modalobj).height();
						var maxBodyHeight = $(document).height() - taraModal - 100;
						if (contentHeight > maxBodyHeight) {
							$(this).height((contentHeight - 14) + 'px');
							$('.modal-body', modalobj).css('max-height', maxBodyHeight + 20 + 'px');
						} else {
							$(this).height(contentHeight + 'px');
							$('.modal-body', modalobj).css('max-height', contentHeight + 30 + 'px');
						}
					}
				});
			});
			modalobj.on('hide.bs.modal', function () {
				if (!$(this).data('modal-cancel')) {
					$(this).removeData('modal-cancel');
					if (settings.modalCloseFunction) {
						settings.modalCloseFunction();
					} else {
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
						if (settings.refrescarPagina) {
							window.parent.location.reload();
						}
						if (settings.refrescarAlertes && settings.alertesRefreshUrl) {
							$.ajax({
								url: settings.alertesRefreshUrl,
								async: false,
								timeout: 20000,
								success: function (data) {
									$('#contingut-alertes *').remove();
									$('#contingut-alertes').append(data);
								},
								error: ajaxErrorFunction
						    });
						}
					}
				}
			});
			modalobj.modal({show:true});
		});
	};
	$.fn.heliumEvalLink = function(options) {
		return this.filter("a").each(function() {
			var settings = $.extend({
				refrescarAlertes: true,
				refrescarPagina: false,
				adjustWidth: false,
				adjustHeight: true,
				buttonContainerId: "modal-botons",
				buttonCloseClass: "modal-tancar"
			}, options);
			$(this).on('click', function() {
				var ajaxErrorFunction = function (jqXHR, exception) {
					if (jqXHR.status === 0) {
		                alert('Not connected.\n Verify Network.');
		            } else if (jqXHR.status == 404) {
		                alert('Requested page not found. [404]');
		            } else if (jqXHR.status == 500) {
		                alert('Internal Server Error [500].');
		            } else if (exception === 'parsererror') {
		                alert('Requested JSON parse failed.');
		            } else if (exception === 'timeout') {
		                alert('Time out error.');
		            } else if (exception === 'abort') {
		                alert('Ajax request aborted.');
		            } else {
		                alert('Uncaught Error.\n' + jqXHR.responseText);
		            }
				};
				var confirmat = true;
				if ($(this).data('rdt-link-confirm'))
					confirmat = confirm($(this).data('rdt-link-confirm'));
				var ajax = ($(this).data("rdt-link-ajax") != undefined) ? $(this).data("rdt-link-ajax") === true : false;
				var modal = ($(this).data("rdt-link-modal") != undefined) ? $(this).data("rdt-link-modal") === true : false;
				var refrescarPaginaFunction = function() {
					//alert('REF. PAGINA');
					window.parent.location.reload();
				};
				var refrescarAlertesFunction = function() {
					//alert('REF. ALERTES');
					$.ajax({
						url: settings.alertesRefreshUrl,
						async: false,
						timeout: 20000,
						success: function (data) {
							$('#contingut-alertes *').remove();
							$('#contingut-alertes').append(data);
						},
						error: ajaxErrorFunction
				    });
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
							success: function() {
								if (settings.ajaxRefrescarPagina)
									refrescarPaginaFunction();
								if (settings.ajaxRefrescarAlertes && settings.alertesRefreshUrl)
									refrescarAlertesFunction();
							},
							error: ajaxErrorFunction
					    });
						return false;
					} else if (modal) {
						var modalCloseFunction = function() {
							if (settings.refrescarPagina)
								refrescarPaginaFunction();
							if (settings.refrescarAlertes && settings.alertesRefreshUrl)
								refrescarAlertesFunction();
						};
						var modalDivId = $(this).attr('id') + "_modal";
						if ($('#' + modalDivId).length == 0)
							$('body').append('<div id="' + modalDivId + '"></div>');
						$('#' + modalDivId).heliumModal({
							modalUrl: $(this).attr("href"),
							maximize: $(this).data("rdt-link-modal-maximize"),
							modalCloseFunction: modalCloseFunction,
							refrescarPagina: settings.refrescarPagina,
							refrescarAlertes: settings.refrescarAlertes
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

function modalTancarIRefrescar(iframe) {
	$(iframe).parent().parent().parent().parent().data('modal-cancel', 'true');
	$(iframe).parent().parent().parent().parent().modal('hide');
	window.parent.location.reload();
}