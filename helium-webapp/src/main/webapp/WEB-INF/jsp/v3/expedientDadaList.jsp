<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
<%--<script src="<c:url value="/webjars/datatables.net-select/1.1.0/js/dataTables.select.min.js"/>"></script>--%>
<%--<script src="<c:url value="/webjars/datatables.net-rowgroup/1.0.3/js/dataTables.rowGroup.min.js"/>"></script>--%>
<script src="<c:url value="/js/datatables/js/dataTables.rowGroup.js"/>"></script>
<script src="<c:url value="/js/jsrender.min.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<%--<script src="<c:url value="/js/webutil.modal.js"/>"></script>--%>

<script type="application/javascript">
	$(document).ready(() => {
		<%-- Al recarregar la taula... --%>
		$("#expedientDades").on("draw.dt", () => {
			<%-- Si la primera dada és inexistent fem la vora superior de 3px, ja que amb els 2px per defecte no es veu --%>
			let firstTr = $("#expedientDades tbody tr:first-child");
			if (firstTr.hasClass("no-data")) {
				firstTr.css("border-top", "dashed 3px #BBB");
			}
		});
		$("#expedientDades").on("rowgroup-draw.dt", () => {
			filtraDades();
		});


		$('body').on('change', '#boto-totes', () => {
			opcionsVisualitzacioChanged('boto-totes');
		});
		$('body').on('change', '#boto-ocults', () => {
			filtraDades();
		});
		$('body').on('change', '#boto-pendents', () => {
			filtraDades();
		});

		$('body').on('mouseup', 'button.modal-tancar', (event) => {
			$(event.currentTarget).closest('.modal-content').find('.close').trigger('click');
		})

		$('body').on('keypress', "#searchDades", function (keyData) {
			if (keyData.which == 13) { //execute on keyenter
				filtraDades();
			}
		});

		// Botó per tornar a dalt: scroll top
		$('.btn-top').on('click', () => {
			$([document.documentElement, document.body]).animate({
				scrollTop: 0
			}, 200);
		});
		$(document).scroll(() => {
			var scrollTop = $(document).scrollTop();
			var opacity = 0.4 + scrollTop / 1500;
			if (opacity > 0.9)
				opacity = 0.9;
			$('.btn-top').css({
				opacity: opacity
			});
		});
	});

	const opcionsVisualitzacioChanged = () => {
		let serverParams = {};
		serverParams['totes'] = $("#boto-totes").parent().hasClass("active");
		$('#expedientDades').webutilDatatable('refresh', serverParams);
	}

	const filtraDades = () => {
		const filtre = $("#searchDades").val().toLowerCase();
		const mostrarOcults = $("#boto-ocults").parent().hasClass("active");
		const mostrarPendents = $("#boto-pendents").parent().hasClass("active");

		$("#expedientDades tbody").removeHighlight();
		$("#expedientDades tr").show();

		if (filtre != '')
			$("#expedientDades tbody").highlight(filtre);

		let hideAgrupacio = true;
		let visibles = 0;
		$($("#expedientDades tbody>tr").not(".datatable-dades-carregant,#expedientDades_noDades").get().reverse()).each((index, fila) => {
			if ($(fila).hasClass('group')) {
				if (hideAgrupacio && filtre != '' && $(fila).find('td:eq(0)').text().toLowerCase().includes(filtre)) {
					let varsAgrupacio = $(fila).nextUntil('.group');
					varsAgrupacio.each((idx, row) => {
						if (visualitzarFila(row, '', mostrarOcults, mostrarPendents)) {
							hideAgrupacio = false;
							visibles++;
							$(row).show();
						}
					})
				}
				$(fila).toggle(!hideAgrupacio);
				hideAgrupacio = true;
			} else {
				if (visualitzarFila(fila, filtre, mostrarOcults, mostrarPendents)) {
					hideAgrupacio = false;
					visibles++;
					$(fila).show();
				} else {
					$(fila).hide();
				}
			}
		});
		if (visibles > 0) {
			$("#expedientDades_info").text("Mostrant 1 a " + visibles + " de " + visibles + " resultats");
			$('#expedientDades_noDades').hide();
		} else {
			$("#expedientDades_info").text("Mostrant 0 resultats");
			if($('#expedientDades_noDades').length){
				$('#expedientDades_noDades').show();
			}else{
				$('<tr id="expedientDades_noDades"><td colspan="4">Sense dades</td></tr>').appendTo("#expedientDades tbody");
			}
		}
		$(".datatable-dades-carregant").hide();
	}

	const visualitzarFila = (fila, filtre, mostrarOcults, mostrarPendents) => {
		let mostrar = true;
		if (!mostrarPendents) {
			if ($(fila).hasClass("no-data")) {
				mostrar = false;
			}
		}
		if (mostrar && !mostrarOcults) {
			if ($(fila).find(".fa-eye-slash").length) {
				mostrar = false;
			}
		}
		if (mostrar && filtre != '') {
			if (!$(fila).find('td:eq(1)').text().toLowerCase().includes(filtre) && !$(fila).find('td:eq(2)').text().toLowerCase().includes(filtre)) {
				mostrar = false;
			}
		}
		return mostrar;
	}
</script>
<style>
	#expedientDades {border-collapse: collapse !important; margin-bottom: 15px !important;}
	#expedientDades tbody tr td {vertical-align: middle;}
	.table-bordered>tbody>tr>td {max-width: 155px;}
	.no-data {color: #bbb; border: dashed 2px; cursor: default !important;}
	.btn-top {position: fixed; z-index: 1000; right: 15px; bottom: 50px; background-color: #FFF; padding: 0 5px 0 5px; border-radius: 5px; cursor: pointer; opacity: 0.1;}
	.nodoc-icon {margin-right: 12px;}
	.adjustIcon {top:-2px;}
	.text-center {text-align: center !important;}
	.list-group {margin-bottom: 0px !important;}
	.obligatori {background-position: right 4px !important; padding-right: 10px !important;}
	.d-flex {display: flex !important;}
	.justify-content-between {justify-content: space-between !important;}
	.text-dark {color: #344767 !important;}
	.font-weight-bold {font-weight: 600 !important;}
	.text-sm {font-size: 0.875em !important;}
	tr.group {font-weight: bold; background-color: #F0F0F0 !important;}
	tr:not(.no-data):has(.fa-eye-slash) {color: #999;}
	.dada-oculta {color: indianred; top: -14px; right: -4px; position: relative;}
	.highlight {background-color: #fff34d; color: black; padding:1px 2px;}
</style>

<c:url var="urlDatatable" value="/v3/expedient/${expedient.id}/dada/datatable"/>

<table	id="expedientDades"
		  data-toggle="datatable"
		  data-url="${urlDatatable}"
		  data-ajax-request-type="POST"
		  data-paging-enabled="false"
		  data-ordering="true"
		  data-default-order="8"
		  data-info-type="button"
		  data-rowcolid-nullclass="no-data"
		  data-selection-enabled="false"
<%--		  data-fixed-order="1"--%>
		  data-group="agrupacioNom"
<%--		  data-selection-url="${expedient.id}/dada/selection"--%>
<%--		  data-selection-counter="#descarregarCount"--%>
		  data-botons-template="#tableButtonsDadesTemplate"
		  class="table table-striped table-bordered table-hover">
	<thead>
	<tr>
		<th data-col-name="id" data-visible="false"/>
		<th data-col-name="agrupacioNom" data-visible="false"/>
		<th data-col-name="campId" data-visible="false"/>
		<th data-col-name="campCodi" data-visible="false"/>
		<th data-col-name="obligatori" data-visible="false"/>
		<th data-col-name="editable" data-visible="false"/>
		<th data-col-name="ocult" data-visible="false"/>
		<th data-col-name="tipus" data-orderable="true" width="1%" data-template="#cellDadaTipusTemplate" data-class="text-center">
			<script id="cellDadaTipusTemplate" type="text/x-jsrender">
				{{if tipus == null}}
					<span class="fa fa-2x fa-file-o nodoc-icon"></span>
				{{else}}
					{{if tipus == "STRING"}}<span class="fa-stack fa-1x"><span class="fa fa-square-o fa-stack-2x"></span><span class="adjustIcon fa fa-stack-1x">T</span></span>{{/if}}
					{{if tipus == "INTEGER"}}<span class="fa-stack fa-1x"><span class="fa fa-square-o fa-stack-2x"></span><span class="adjustIcon fa fa-stack-1x">99</span></span>{{/if}}
					{{if tipus == "FLOAT"}}<span class="fa-stack fa-1x"><span class="fa fa-square-o fa-stack-2x"></span><span class="adjustIcon fa fa-stack-1x">9,9</span></span>{{/if}}
					{{if tipus == "BOOLEAN"}}<span class="fa fa-2x fa-check-square-o"></span>{{/if}}
					{{if tipus == "TEXTAREA"}}<span class="fa-stack fa-1x"><span class="fa fa-square-o fa-stack-2x"></span><span class="adjustIcon fa fa-stack-1x fa-bars"></span></span>{{/if}}
					{{if tipus == "DATE"}}<span class="fa fa-2x fa-calendar" style="font-size:1.6em;"></span>{{/if}}
					{{if tipus == "PRICE"}}<span class="fa fa-2x fa-eur"></span>{{/if}}
					{{if tipus == "TERMINI"}}<span class="fa fa-2x fa-clock-o"></span>{{/if}}
					{{if tipus == "SELECCIO"}}<span class="fa fa-2x fa-chevron-down"></span>{{/if}}
					{{if tipus == "SUGGEST"}}<span class="fa fa-2x fa-chevron-circle-down"></span>{{/if}}
					{{if tipus == "REGISTRE"}}<span class="fa fa-2x fa-table" style="font-size:1.6em;"></span>{{/if}}
					{{if tipus == "ACCIO"}}<span class="fa fa-2x fa-bolt"></span>{{/if}}
				{{/if}}
			</script>
		</th>
		<th data-col-name="nom" data-orderable="true" width="20%" data-template="#cellDadaNomTemplate">
			<spring:message code="expedient.tipus.document.llistat.columna.nom"/>
			<script id="cellDadaNomTemplate" type="text/x-jsrender">
				<span {{if obligatori}}class="obligatori"{{/if}}>{{:nom}}</span>
				{{if ocult}}
					<span class="fa fa-eye-slash pull-right dada-oculta" title="Dada oculta"></span>
				{{/if}}
			</script>
		</th>
		<th data-col-name="valor" data-orderable="false" width="70%"  data-template="#cellDadaValorTemplate">
			<spring:message code="expedient.nova.data.valor"/>
			<script id="cellDadaValorTemplate" type="text/x-jsrender">
				{{if valor.registre}}
					<ul class="list-group registre">
						<li class="list-group-item d-flex justify-content-between border-0">
							{{for valor.valorHeader}}
								{{props #data}}
									<div class="d-flex flex-column text-dark font-weight-bold text-sm {{if prop}}obligatori{{/if}}">{{>key}}</div>
								{{/props}}
							{{/for}}
						</li>
						{{for valor.valorBody}}
							<li class="list-group-item d-flex justify-content-between border-0">
								{{for #data}}
									<div class="d-flex flex-column text-sm">{{>#data}}</div>
								{{/for}}
							</li>
						{{/for}}
					</ul>
				{{else}}
					{{if valor.multiple}}
						<ul class="list-group multiple">
							{{for valor.valorMultiple}}
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-sm">{{>#data}}</div>
								</li>
							{{/for}}
						</ul>
					{{else}}
						{{:valor.valorSimple}}
					{{/if}}
				{{/if}}
			</script>
		</th>
		<th data-col-name="id" data-template="#cellDadaAccionsTemplate" data-orderable="false" width="5%">
			<script id="cellDadaAccionsTemplate" type="text/x-jsrender">
			{{if id == null}}
				<a class="btn btn-default" href="${expedient.id}/dada/{{:campCodi}}/new" data-toggle="modal"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.boto.nova_dada"/></a>
			{{else}}
				<div data-document-id="{{:id}}" <%--data-arxivat="{{:arxivat}}" data-psigna="{{psignaInfo}}"--%> class="dropdown accionsDocument">
					<button class="btn btn-primary" data-toggle="dropdown" {{if error || !editable}}disabled="disabled"{{/if}} style="width:100%;"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
					<ul class="dropdown-menu dropdown-menu-right">
						{{if !error && editable}}
							{{if editable}}<li><a data-toggle="modal" href="${expedient.id}/dada/{{:id}}/update"><span class="fa fa-pencil fa-fw"></span>&nbsp;<spring:message code="comuns.modificar"/></a></li>{{/if}}
							{{if editable}}<li><a href="${expedient.id}/dada/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.llistat.document.confirm_esborrar"/>"><span class="fa fa-trash-o fa-fw"></span>&nbsp;<spring:message code="comuns.esborrar"/></a></li>{{/if}}
						{{/if}}
					</ul>
				</div>
			{{/if}}
			</script>
		</th>
	</tr>
	</thead>
</table>
<div class="btn-top">
	<span class="fa fa-arrow-up"></span>
</div>
<script id="tableButtonsDadesTemplate" type="text/x-jsrender">
	<div class="botons-titol text-right">
		<span style="padding-left: 5px">
			<span class="fa fa-search" style="position: absolute;float: left;padding-left: 10px;padding-top: 10px;"></span>
			<input id="searchDades" class="form-control" placeholder="<spring:message code="expedient.dada.filtrar"/>" autocomplete="off" spellcheck="false" autocorrect="off" tabindex="1" style="padding-left: 35px;">
		</span>
		<div class="btn-group" data-toggle="buttons">
			<c:if test="${expedient.permisAdministration}">
				<label class="btn btn-default" title="<spring:message code='expedient.dada.mostrar.totes'/>">
					<input type="checkbox" id="boto-totes" autocomplete="off"><span class="fa fa-globe"></span>
				</label>
			</c:if>
			<label class="btn btn-default" title="<spring:message code='expedient.dada.mostrar.ocultes'/>">
				<input type="checkbox" id="boto-ocults" autocomplete="off"><span class="fa fa-eye-slash"></span>
			</label>
			<label class="btn btn-default active" title="<spring:message code='expedient.dada.mostrar.pendents'/>">
				<input type="checkbox" id="boto-pendents" autocomplete="off" checked><span class="fa fa-plus"></span>
			</label>
		</div>
		<a id="nova_dada" class="btn btn-default" href="${expedient.id}/dada/new" data-toggle="modal" data-adjust-height="false" data-height="350" data-datatable-id="expedientDades"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.boto.nova_dada"/></a>
	</div>
</script>
<script id="rowhrefTemplateDades" type="text/x-jsrender">
	{{if id == null}}
		${expedient.id}/dada/{{:codi}}/new
	{{else}}
		${expedient.id}/dada/{{:id}}/update
	{{/if}}
</script>
