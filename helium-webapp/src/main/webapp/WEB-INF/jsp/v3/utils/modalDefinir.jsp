<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="modalId" value="${param.modalId}"/>
<c:set var="sAjaxSource" value="${param.sAjaxSource}"/>
<c:set var="refrescarAlertes" value="${param.refrescarAlertes}"/>
<c:set var="refrescarTaula" value="${param.refrescarTaula}"/>
<c:set var="refrescarTaulaId" value="${param.refrescarTaulaId}"/>
<c:set var="refrescarPagina" value="${param.refrescarPagina}"/>
<c:set var="icon" value="${param.icon}"/>
<c:set var="img" value="${param.img}"/>
<c:set var="texto" value="${param.texto}"/>

<a id="a-modal-${modalId}" href="${sAjaxSource}">	
	<i class="${icon}"></i>
	<img src="<c:url value="${img}"/>" alt="${texto}" title="${texto}" border="0"/>
</a>

<script>
	$(document).ready(
		function() {
			$('#a-modal-${modalId}').click(function(e) {
				e.preventDefault();
				modalInit("${modalId}");
			});
		}
	);
	function modalInit(modalId) {
		var modal = $('.modal-hel');
		var href = $('#a-modal-' + modalId).attr("href");
		$(modal).on('show', function () {
			$('iframe', modal).attr(
					"src",
					href);
		});
		$(modal).modal({show:true});
	}
	function modalCanviarTitol(iframe, titol) {
		$('.modal-header h3', $(iframe).parent().parent()).html(titol);
	}
	function modalCopiarBotons(iframe, elements) {
		$('.modal-footer *', $(iframe).parent().parent()).remove();
		elements.each(function(index) {
			var element = $(this);
			var clon = element.clone();
			if (clon.hasClass('btn-tancar')) {
				clon.on('click', function () {
					$(iframe).parent().parent().modal('hide');
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
	}
	function modalTancarIRefrescar(iframe) {
		$(iframe).parent().parent().modal('hide');
		<c:if test="${refrescarTaula == 'true'}">
			var refrescat = false;
			$('.dataTables_paginate li', $('#${refrescarTaulaId}').parent()).each(function() {
				if ($(this).hasClass('active')) {
					$('a', this).click();
					refrescat = true;
				}
			});
			if (!refrescat)
				$("#${refrescarTaulaId}").dataTable().fnDraw();
		</c:if>
		<c:if test="${refrescarPagina == 'true'}">
			window.parent.location.reload();
		</c:if>
		<c:if test="${refrescarAlertes == 'true'}">
			$.ajax({
				"url": "<c:url value="/nodecorar/v3/missatges"/>",
				"success": function (data) {
					$('.contingut-alertes *').remove();
					$('.contingut-alertes').append(data);
				}
		    });
		</c:if>
	}
	function modalAjustarTamany(iframe, height) {
		$(iframe).height(height + 'px');
    }
</script>
