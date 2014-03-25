(function($) {
	$.fn.ripeaModal = function(options) {
		return this.filter("div").each(function() {
			var settings = $.extend({
				refrescarTaula: false,
				refrescarAlertes: true,
				refrescarPagina: false,
				adjustWidth: false,
				adjustHeight: true,
				buttonContainerId: "modal-botons",
				buttonCloseClass: "modal-tancar"
			}, options);
			$(this).html(
					'<div class="modal fade" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">' +
					'	<div class="modal-dialog">' +
					'		<div class="modal-content">' +
					'			<div class="modal-header">' +
					'				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
					'				<h4 class="modal-title"></h4>' +
					'			</div>' +
					'			<div class="modal-body">' +
					'				<iframe frameborder="0" height="100" width="99.6%"></iframe>' +
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
						var height = $(this).contents().find("html").height();
						$(this).height(height + 'px');
					}
					if (settings.adjustWidth) {
						var width = $(this).contents().find("html").width();
						var modalobj = $(this).parent().parent().parent();
						modalobj.css('width', width + 'px');
					}
				});
			});
			modalobj.on('hide.bs.modal', function () {
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
			});
			modalobj.modal({show:true});
		});
	};
}(jQuery));