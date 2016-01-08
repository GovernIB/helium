(function($) {

	$.webutilModal = function(element, options) {
		var defaults = {
			adjustHeight: true,
			maximized: false,
			elementBotons: "#modal-botons",
			elementForm: "#modal-form",
			elementTancarData: "modal-cancel"
		}
		var contextPath = '/helium/';
		var $element = $(element), element = element;
		var plugin = this;
		plugin.settings = {}
		plugin.serverParams = [];
		plugin.init = function() {
			plugin.settings = $.extend(defaults, $element.data(), options);
			$element.click(function(event) {
				var elementPerEvaluar = $element;
				if (elementPerEvaluar.prop("tagName") == 'TR' && event.target.tagName != 'TD') {
					elementPerEvaluar = $(event.target.tagName);
				}
				var obrirNovaFinestra = false;
				if ((elementPerEvaluar.attr('href') || elementPerEvaluar.data('href')) && elementPerEvaluar.data('toggle') == 'modal') {
					obrirNovaFinestra = true;
				}
				if (obrirNovaFinestra) {
					var href = elementPerEvaluar.attr('href');
					if (!href)
						href = elementPerEvaluar.data('href');
					if (event.which != 2) {
						var dataTableId;
						if (elementPerEvaluar.closest('.dataTables_wrapper')) {
							dataTableId = $('table.dataTable', elementPerEvaluar.closest('.dataTables_wrapper')).attr('id');
						}
						var modalDivId = (elementPerEvaluar.data("modal-id")) ? elementPerEvaluar.data("modal-id") : "modal_" + (new Date().getTime());
						var modalData = '';
						if (plugin.settings.maximized) {
							modalData += ' data-maximized="true"';
						}
						if ($('#' + modalDivId).length == 0 ) {
							$('body').append(
								'<div id="' + modalDivId + '"' + modalData + '>' +
								'	<div class="modal" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">' +
								'		<div class="modal-dialog modal-lg">' +
								'			<div class="modal-content">' +
								'				<div class="modal-header">' +
								'					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
								'					<h4 class="modal-title"></h4>' +
								'				</div>' +
								'				<div class="modal-body" style="padding:0">' +
								'					<iframe frameborder="0" height="100" width="100%"></iframe>' +
								'				</div>' +
								'				<div class="modal-footer"></div>' +
								'			</div>' +
								'		</div>' +
								'	</div>' +
								'</div>');						
							elementPerEvaluar.data("modal-id", modalDivId);
							$('#' + modalDivId).webutilModalShow({
								contentUrl: urlAmbPrefix(href, '/modal'),
								dataTableId: dataTableId
							});
						} else {
							$('#' + modalDivId).webutilModalShow({
								dataTableId: dataTableId
							});
						}
					} else {
						window.open(href, '_blank');
					}
					return false;
				}
			});
		}
		// Mètodes públics
		// Mètodes privats
		var urlAmbPrefix = function(url, prefix) {
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
			return absolutePath.substring(0, contextPath.length) + prefixSenseBarra + '/' + absolutePath.substring(contextPath.length);
		}
		// Inicialització del plugin
        plugin.init();
	}

	$.fn.webutilModalShow = function(options) {
		return this.filter("div").each(function() {
			var settings = $.extend({
				adjustHeight: true,
				maximized: false,
				elementBotons: "#modal-botons",
				elementForm: "#modal-form",
				elementTancarData: "modal-cancel"
			}, $(this).data(), options);
			var modalobj = $('div.modal', this);
			if (!modalobj.data('modal-configurada')) {
				var iframe = $('iframe', modalobj);
				modalobj.on('show.bs.modal', function () {
					iframe.empty();
					if (settings.height)
						iframe.css('height', '' + settings.height + 'px');
					iframe.attr("src", settings.contentUrl);
					iframe.load(function() {
						// Copiar el titol de la modal
						var titol = $(this).contents().find("title").html();
						$('.modal-header h4', $(this).parent().parent()).html(titol);
						// Copiar botons
						var dataBotons = $('body', $(iframe).contents()).data('modal-botons');
						var modalBotons = (dataBotons) ? $(dataBotons, $(iframe).contents()) : $(settings.elementBotons, $(iframe).contents());
						if (modalBotons.length) {
							$('.modal-footer *', $(this).parent().parent()).remove();
							$('.btn', modalBotons).each(function(index) {
								var element = $(this);
								var clon = element.clone();
								if (element.data(settings.elementTancarData)) {
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
							modalBotons.hide();
						}
						// Evaluar URL del formulari
						var dataForm = $('body', $(iframe).contents()).data('modal-form');
						var modalForm = (dataForm) ? $(dataForm, $(iframe).contents()) : $(settings.elementForm, $(iframe).contents());
						if (modalForm.length) {
							modalForm.attr('action', urlAmbPrefix(modalForm.attr('action'), '/modal'));
						}
						if (settings.maximized) {
							// Maximitzar
							$('.modal-dialog', modalobj).css('width', '98%');
							$('.modal-dialog', modalobj).css('top', '10px');
							$('.modal-dialog', modalobj).css('margin', 'auto');
							iframe.attr('height', '100%');
							var contentHeight = $(iframe).contents().find("html").outerHeight();
							var modalobj = $(iframe).parent().parent().parent();
							var taraModal = $('.modal-header', modalobj).outerHeight() + $('.modal-footer', modalobj).outerHeight();
							var maxBodyHeight = $(window.top).height() - taraModal - 20;
							$(iframe).height(maxBodyHeight + 'px');
							$('.modal-body', modalobj).css('height', maxBodyHeight + 'px');
							$(iframe).contents().find("body").css('height', maxBodyHeight + 'px');
						} else if (settings.adjustHeight) {
							// Ajustar alçada
							var height = $(this).contents().find("html").height();
							$(iframe).parent().css('height', height + 17 + 'px');
							$(iframe).css('min-height', height + 'px');
							$(this).height(height + 17 + 'px');
						}
					});
				});
				modalobj.on('hidden.bs.modal', function () {
					if (settings.dataTableId) {
						$('#' + settings.dataTableId).webutilDatatableRefresh();
					}
				});
				iframe.on('load', function () {
					var pathname = this.contentDocument.location.pathname;
					var suffix = "/modal/tancar";
					if (pathname.indexOf(suffix, this.length - suffix.length) !== -1) {
						// Crida l'event de tancament de finestra modal
						$(document).trigger(
								'webutilmodal.close',
								[String(modalobj.context.id)]); // data-modal-id
						
						$('button.close', $(this).closest('.modal-dialog')).trigger('click');
						$('section.content-alertes').load("/fragments/alertes");
					}
				});
				modalobj.data('modal-configurada', true);
			}
			$('.modal-body iframe *', modalobj).remove();
			$('.modal-footer *', modalobj).remove();
			modalobj.modal('show');
		});
	};

	$.fn.webutilModal = function(options) {
		var pluginName = 'webutilModal';
        return this.each(function() {
            if (undefined == $(this).data(pluginName)) {
                var plugin = new $.webutilModal(this, options);
                $(this).data(pluginName, plugin);
            } else if (options && typeof options !== 'object') {
            	
            }
        });
    }

	$(document).ready(function() {
		$('[data-toggle="modal"]', $(this)).each(function() {
			if (!$(this).attr('data-modal-eval')) {
				$(this).webutilModal();
				$(this).attr('data-modal-eval', 'true');
			}
		});
	});
}(jQuery));
