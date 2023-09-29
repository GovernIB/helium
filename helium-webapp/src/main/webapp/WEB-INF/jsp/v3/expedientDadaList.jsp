<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
<script src="<c:url value="/js/datatables/js/dataTables.rowGroup.js"/>"></script>
<script src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
<script src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
<script src="<c:url value="/js/jsrender.min.js"/>"></script>
<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/helium3Tasca.js"/>"></script>

<script type="application/javascript">
	let expedientId = "${expedientId}";
	let currentEditedDada;
	let currentEditedCela;
	let mouseDownCela;

	$(document).keyup((event) => {
		if (event.key === "Escape" || event.key === "Esc") {
			console.log("Pressed Escape key");
			finishEditing();
		} else if (event.key === "Enter") {
			console.log("Pressed Enter key");
			if (currentEditedCela) {
				event.preventDefault();
				submitEditForm(currentEditedCela.find('form'));
			}
		}
	});

	$(document).keydown((event) => {
		if (event.key === "Tab") {
			// Si estem editant una dada, evitam que el tab surti fora del formulari
			if (!currentEditedDada)
				return true;

			const tabbable = $()
					.add(currentEditedCela.find("button, input:not([type='hidden']), select, textarea"))
					.add(currentEditedCela.find("[href]"))
					.add(currentEditedCela.find("[tabindex]:not([tabindex='-1'])"));
			const target = $(event.target);
			if (target.closest('.varValor').length) {
				if (event.shiftKey) {
					if (target.is(tabbable.first())) {
						event.preventDefault();
						tabbable.last().focus();
					}
				} else {
					if (target.is(tabbable.last())) {
						event.preventDefault();
						tabbable.first().focus();
					}
				}
			} else {
				event.preventDefault();
				if (event.shiftKey) {
					tabbable.last().focus();
				} else {
					tabbable.first().focus();
				}
			}
		}
	});

	// Mirar el click. El canvi de focus amb tabulador el mantenim sempre dins el formulari
	$(document).on('mousedown', (event) => {
		let desti = $(event.target);
		mouseDownCela = desti.closest('.varValor');

		if (!currentEditedDada) {
			return true;
		}
		let exitForm = !desti.closest('.varValor').length || desti.closest('.varValor').find('.varVal').data('codi') !== currentEditedDada;
		if (exitForm) {
			console.log("Focus out of " + currentEditedDada + " form");
			submitEditForm(currentEditedCela.find('form'));
		}
		return true;
	});


	$(document).ready(() => {
		<%-- Al recarregar la taula... --%>
		$("#expedientDades").on("draw.dt", () => {
			<%-- Si la primera dada és inexistent fem la vora superior de 3px, ja que amb els 2px per defecte no es veu --%>
			let firstTr = $("#expedientDades tbody tr:first-child");
			if (firstTr.hasClass("no-data")) {
				firstTr.css("border-top", "dashed 3px #BBB");
			}
			$("td>span.fa-lock").closest('tr').addClass('dada-bloquejada');
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

		$("#expedientDades").on('click', (event) => {
			console.log("Clic en taula");
			if (!mouseDownCela)
				return true;

			console.log('Click on cela de dada');
			const cela = mouseDownCela;
			// let cela = $(event.currentTarget).closest('.varValor');
			if (cela.find('.varEdit').length > 0) {
				console.log("Clic no retorna cela");
				return true;
			}

			const dadaValor = cela.find('.varVal');
			const dadaCodi = dadaValor.data('codi');
			const editable = dadaValor.hasClass('editable');
			console.log('Editing dada ' + dadaCodi);

			currentEditedDada = dadaCodi;
			currentEditedCela = cela

			if (editable) {
				console.log('Obtenint formulari');
				dadaValor.addClass('ocult');
				let dadaEdit = $('<span class="varEdit"><span class="fa fa-circle-o-notch fa-spin"></span></span>');
				let botonsEdit = $('<span class="btnEdit"><button type="button" class="btn btn-success btn-ok"><span class="fa fa-check"></span></button><button type="button" class="btn btn-danger btn-cancel"><span class="fa fa-times"></span></button></span>');
				beginEditing(cela);
				cela.append(dadaEdit);
				$.get("<c:url value="/nodeco/v3/expedient/${expedient.id}/dada/"/>" + dadaCodi + "/edit", function(data) {
					dadaEdit.html(data);
					cela.append(botonsEdit);
					postEditForm(dadaEdit);
				}).fail(function(jqXHR, textStatus, errorThrown) {
					console.log('Error obtenint formulari', textStatus);
					webutilRefreshMissatges();
					dadaEdit.remove();
					dadaValor.removeClass('ocult');
				});

			}

		});

		$("#expedientDades").on('submit', 'form', (event) => {
			console.log('Submit form!!', event);
			event.preventDefault();
			// submitEditForm($(event.target));
		})

		$("#expedientDades").on('click', '.btn-cancel', (event) => {
			event.stopPropagation();
			finishEditing();
		});

		$("#expedientDades").on('click', '.btn-ok', (event) => {
			event.stopPropagation();
			submitEditForm(currentEditedCela.find('form'));
		});


	});

	const beginEditing = (cela) => {
		let accio = cela.next().children().first();
		if (accio.is("div")) {
			accio.children().first().prop("disabled", true).addClass('isDisabled');
		} else {
			accio.prop("disabled", true).addClass('isDisabled');
		}
	}

	const finishEditing = () => {
		$('.varEdit').remove();
		$('.btnEdit').remove();
		$('#overlay').hide();
		console.log("Overlay OFF - Finish edit");
		$('.varVal').removeClass('ocult');
		$('.isDisabled').prop("disabled", false).removeClass('isDisabled');
		currentEditedDada = undefined;
		currentEditedCela = undefined;
	}

	const postEditForm = (dadaEdit) => {
		dadaEdit.find('#modal-botons').remove();
		dadaEdit.find('.form-group > label').addClass('ocult');
		dadaEdit.find('.col-xs-9').removeClass('col-xs-9');
		dadaEdit.find('th>label').removeClass('col-xs-3');
		dadaEdit.find('.tercpre, .tercmig, .tercpost').addClass('col-xs-4');
		loadFormAccions(dadaEdit.find("form"));
		dadaEdit.find('input:text, input:checkbox, textarea').first().focus();
	}

	const postSubmitForm = (dadaEdit, dadaCodi) => {
		debugger
		console.log("Post submit");
		let fila = dadaEdit.closest('tr');
		if (fila.hasClass('no-data')) {
			// Canviar id de fila, i afegir botó d'accions
			fila.attr('id', 'row_' + dadaCodi);
			fila.removeClass('no-data');
			let celaAccions = fila.find('td').last();
			let botoAccions = $('<div data-document-id="' + dadaCodi + '" class="dropdown accionsDocument">' +
					'<button class="btn btn-primary" data-toggle="dropdown" style="width:100%;" aria-expanded="false"><span class="fa fa-cog"></span>&nbsp;Accions&nbsp;<span class="caret"></span></button>' +
					'<ul class="dropdown-menu dropdown-menu-right">' +
					'<li class="ld-editar"><a data-toggle="modal" href="' + expedientId + '/dada/' + dadaCodi + '/update"><span class="fa fa-pencil fa-fw"></span>&nbsp;Modificar</a></li>' +
					'<li class="ld-borrar"><a href="' + expedientId + '/dada/' + dadaCodi + '/delete" data-toggle="ajax" data-confirm="Estau segur que voleu esborrar aquest document?"><span class="fa fa-trash-o fa-fw"></span>&nbsp;Esborrar</a></li>' +
					'</ul>' +
					'</div>');
			celaAccions.empty();
			celaAccions.append(botoAccions);
			$(botoAccions).find('button').dropdown();
			$(botoAccions).webutilModalEval();
			$(botoAccions).webutilAjaxEval();
			$(botoAccions).webutilConfirmEval();
		}
	}

	const submitEditForm = (fomulari) => {
		console.log('Submitting form');
		$("#overlay").show();
		console.log("Overlay ON");
		const cela = fomulari.closest('.varValor');
		const dadaValor = cela.find('.varVal');
		const dadaEdit = cela.find('.varEdit');
		const dadaCodi = dadaValor.data('codi');

		$.ajax({
			type: 'POST',
			url: "<c:url value="/nodeco/v3/expedient/${expedient.id}/proces/${expedient.processInstanceId}/dada/"/>" + dadaCodi + "/edit",
			data: fomulari.serialize(),
			dataType: "html",
			async: true,
			success: function(data) {
				if (data.indexOf('form id="command"') == -1) {
					dadaValor.html(data);
					postSubmitForm(dadaEdit, dadaCodi);
					finishEditing();
				} else {
					dadaEdit.html(data);
					postEditForm(dadaEdit);
				}
				$('#overlay').hide();
				console.log("Overlay OFF - Success");
			},
			error: function(e) {
				console.log("Error desant dades: ", e);
				webutilRefreshMissatges();
				let errorAlert = $('<div class="alert alert-danger alert-inline" role="alert"><span class="fa fa-exclamation-triangle"></span> S\'ha produït un error al intentar desar la dada \'' + dadaCodi + '\': ' + e.statusText + '</div>');
				cela.prepend(errorAlert);
				finishEditing();
				$('#overlay').hide();
				console.log("Overlay OFF - Error");
				setTimeout(function () {errorAlert.hide('slow', function(){ errorAlert.remove(); });}, 5000);
			}
		});
		console.log('Finish submitting form');
	}

	const loadFormAccions = (formulari) => {
		let action = $(formulari).attr('action');
		$(formulari).attr('action', cleanAction($(formulari).attr('action')));
		// Ajustaments per a cada tipus de camp
		$(formulari).find(".price").priceFormat({prefix: '', centsSeparator: ',', thousandsSeparator: '.', allowNegative: true});
		$(formulari).find(".date").mask("99/99/9999").datepicker({language: 'ca', autoclose: true, dateFormat: "dd/mm/yy"});
		$(formulari).find(".btn_date").click(function(){
			$(this).prev(".date").trigger("focus");
		});
		$(formulari).find(".termini").each(function(){
			$(this).select2({width: 'resolve', allowClear: true, minimumResultsForSearch: 10});
		});
		$(formulari).find(".termdia").keyfilter(/^[-+]?[0-9]*$/);
		$(formulari).find(".enter").keyfilter(/^[-+]?[0-9]*$/);
		$(formulari).find(".float").keyfilter(/^[-+]?[0-9]*[.]?[0-9]*$/);
		$(formulari).find(".suggest").each(function() {
			initSuggest(this);
		});
		$(formulari).find(".seleccio").each(function() {
			initSeleccio(this);
		});
		// Camp múltiple: afegir
		$(formulari).on("click", ".btn_multiple", function() {
			addMultiple(this);
		});
		$(formulari).find(".validada, .formext").each(function(index){
			validado(true);
		});
		// Camp múltiple: eliminar
		$(formulari).on("click", ".btn_eliminar", function() {
			delMultiple(this);
		});
		// Eliminar fila
		$(formulari).on("click", ".eliminarFila", function() {
			delFila(this);
		});

		// Funcionalitats concretes
		$(formulari).on("click", ".btn_date", function(){
			$(this).closest(".date").focus();
		});
		$(formulari).on("click", ".btn_date_pre", function(){
			$(this).next().focus();
		});
		$(formulari).on("change", ".checkboxmul", function() {
			if($(this).is(":checked")) {
				$(this).prev().val(true);
			} else {
				$(this).prev().val(false);
			}
		});
		$(formulari).find(".btn_accio").click(function() {
			return executeAction(this);
		});
		$(formulari).find('#boto-formext').click(function() {
			return openFormExtern(this);
		});
	}

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
	.camp-bloquejat {font-size: 24px; color: #AAAAAA;}
	.varEdit .form-group {margin-left: 0px !important; width: 100% !important;}
	.varEdit .form-control {width: 100%;}
	.varEdit .multiple label.control-label {display: none;}
	.varEdit .multiple input-group-multiple {display: none;}
	.varEdit .multiple input.camp-multiple {width: calc(100% - 41px) !important;}
	.varEdit .multiple textarea {width: calc(100% - 41px) !important;}
	.varEdit tr.multiple textarea {width: 100% !important;}
	.varEdit tr.multiple input {width: 100% !important;}
	.varEdit .multiple .input-group {width: calc(100% - 41px);}
	.varEdit .multiple .termgrup {width: calc(100% - 41px) !important;}
	.varEdit .multiple .seleccio {width: calc(100% - 41px) !important;}
	.varEdit .multiple .suggest {width: calc(100% - 41px) !important; float: left;}
	.varEdit .multiple .input-group input {width: 100% !important;}
	.varEdit .multiple .tercpost>input {width: 100% !important;}
	.varEdit .multiple .multiple_camp > .controls.multiple_camp {position: relative;}
	.varEdit .multiple .multiple_camp > .controls.multiple_camp > .termgrup+.btn_eliminar {position: absolute; bottom: 4px; right: 0px;}
	.varEdit div.inputcheck {display: inline;}
	.varEdit .input-group {width: 100%;}
	.varEdit .input-group-multiple {min-height: 34px;}
	.varEdit input.checkbox {max-width: 22px; width: 22px;}
	.varEdit input.checkbox:focus {min-width: 27px;}
	.varEdit button.btn_eliminar {float: right;}
	.varEdit .input-group-addon:last-child {width: 40px !important;}
	.varEdit .termgrup .label-term {display: block !important;}
	.varEdit .termgrup ~ .termgrup .label-term {display: none;}
	.varEdit label {font-size: 12px;}
	.varEdit .label-term {text-align: left;}
	.varEdit .tercpre {padding-left: 0px;}
	.varEdit .tercmig {padding-left: 7px; padding-right: 7px;}
	.varEdit .tercpost {padding-right: 0px;}
	.varEdit .termini {width: 100% !important;}
	.varEdit table {margin-bottom: 0px;}
	.varEdit {width: calc(100% - 20px); float: left;}
	.varEdit button.btn_afegir.btn_multiple {margin-top: 4px;}
	.btnEdit {float: left; width: 20px;}
	.btnEdit .btn {margin: 1px; font-size: 8px; float: right;}
	.btnEdit .btn-ok {padding: 1px 2px;}
	.btnEdit .btn-cancel {padding: 1px 3px;}
	.varValor {position: relative; cursor: pointer;}
	.dada-bloquejada > .varValor {cursor: not-allowed !important;}
	.alert-inline {font-size: 12px; padding: 2px 8px !important;}
	.overlay {z-index: 16777271; position: absolute; top: 0; right: 0; bottom: 0; left: 0; background: rgba(0,0,0,.2); cursor: progress; text-align: center;}
	.overlay > span {margin-top: 15px;}
	.isDisabled {pointer-events: none; opacity: 0.5;}
	#overlay {position: fixed; display: none; width: 100%; height: 100%; top: 0; left: 0; right: 0; bottom: 0; background-color: rgba(0,0,0,0.2); z-index: 200; cursor: pointer;}
	#ov_spin {position: absolute; top: 50%; left: 50%; font-size: 40px; transform: translate(-50%,-50%); -ms-transform: translate(-50%,-50%);}
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
		<th data-col-name="valor" data-orderable="false" width="70%" data-template="#cellDadaValorTemplate" data-class="varValor">
			<spring:message code="expedient.nova.data.valor"/>
			<script id="cellDadaValorTemplate" type="text/x-jsrender">
				<span id="varVal-{{:campCodi}}" class="varVal {{if editable}}editable{{/if}}" data-codi={{:campCodi}}>
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
				</span>
			</script>
		</th>
		<th data-col-name="id" data-template="#cellDadaAccionsTemplate" data-orderable="false" width="5%">
			<script id="cellDadaAccionsTemplate" type="text/x-jsrender">
			{{if editable}}
				{{if id == null}}
					<a class="btn btn-default" href="${expedient.id}/dada/{{:campCodi}}/new?ocultarVar=true" data-toggle="modal" data-adjust-height="false" data-height="350"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.boto.nova_dada"/></a>
				{{else}}
					<div data-document-id="{{:id}}" <%--data-arxivat="{{:arxivat}}" data-psigna="{{psignaInfo}}"--%> class="dropdown accionsDocument">
						<button class="btn btn-primary" data-toggle="dropdown" {{if error}}disabled="disabled"{{/if}} style="width:100%;"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
						<ul class="dropdown-menu dropdown-menu-right">
							{{if !error}}
								<li><a data-toggle="modal" href="${expedient.id}/dada/{{:id}}/update"><span class="fa fa-pencil fa-fw"></span>&nbsp;<spring:message code="comuns.modificar"/></a></li>
								<li><a href="${expedient.id}/dada/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.llistat.document.confirm_esborrar"/>"><span class="fa fa-trash-o fa-fw"></span>&nbsp;<spring:message code="comuns.esborrar"/></a></li>
							{{/if}}
						</ul>
					</div>
				{{/if}}
			{{else}}
				<span class="fa fa-lock pull-right camp-bloquejat" title="Camp bloquejat en aquest estat"></span>
			{{/if}}
			</script>
		</th>
	</tr>
	</thead>
</table>
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
		<a id="descarregarDades" href="<c:url value="/v3/expedient/${expedient.id}/dades/descarregar"/>" class="btn btn-default" title="<spring:message code="expedient.dada.descarregar"/>">
			<span class="fa fa-download"></span> <spring:message code="comu.boto.descarregar"/>
		</a>
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

<div id="overlay"><span id="ov_spin" class="fa fa-circle-o-notch fa-2x fa-spin"></span></div>