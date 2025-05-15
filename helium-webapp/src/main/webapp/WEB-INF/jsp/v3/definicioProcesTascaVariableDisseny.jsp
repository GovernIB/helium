<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>


<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="definicio.proces.tasca.variable.disseny.titol" arguments="${tasca.nom}"/></c:set>
<c:set var="baseModalUrl"><c:url value="/modal/v3/${basicUrl}"></c:url></c:set>

<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>
	<link href="<c:url value="/css/tascaForm.css"/>" rel="stylesheet"/>
	
	<script src="//code.jquery.com/ui/1.11.3/jquery-ui.js"></script>
	<link rel="stylesheet" href="//code.jquery.com/ui/1.11.3/themes/smoothness/jquery-ui.css">
	
	<hel:modalHead/>
	
	<style>
	
	.b-radius-r {
		border-radius: 0 0.5em 0.5em 0;
	}
	
	.b-radius-l {
		border-radius: 0.5em 0 0 0.5em;
	}
	
	.floating-form {
		position: fixed;
		left: 0;
		width: 100%;
		padding: 0 1em;
		z-index: 9999;
		display: flex;
		justify-content: center;
	}
	
	.floating-form form, .floating-form button {
		cursor: grab;
	}
	
	.floating-form > .well {
		width: 100%;
	}
	
	.vars {
		min-height: 50px;
		width: 100%;
	}
	
	.variable {
		min-height: 95px;
		display: flex;
		justify-content: space-between;
		cursor: move;
		padding: 0;
		position: relative;
		padding: 0.5em;
	}
	
	.camp {
		border: 2px solid #f1f1f1;
		border-radius: 0.5em;
	}
	
	.var-spacing ~ .camp {
		border-radius: 0 0.5em 0.5em 0;
	}
	
	.camp:has(+.var-spacing) {
		border-radius: 0.5em 0 0 0.5em;
	}
	
	
	.variable-label {
		flex: auto;
		text-align: center;
		/*cursor: move;*/
	}
	
	.bg-white {
		background-color: #fff;
	}
	
	.bg-secondary {
		background-color: #f5f5f5;
	}
	
	.row-options {
		display: none;
		flex-wrap: nowrap;
		flex-direction: row;
		column-gap: 2px;
		position: absolute;
		width: 100%;
		justify-content: space-between;
		z-index: 999;
		align-items: center;
		bottom: -120px;
		width: 500px;
		
	}
	
	.row-options-body {
		background: #f5f5f5;
		border-left: 1px solid #ccc;
		border-right: 1px solid #ccc;
		border-radius: 4px;
		box-shadow: 0px 11px 17px -4px rgba(0,0,0,0.75);
		-webkit-box-shadow: 0px 11px 17px -4px rgba(0,0,0,0.75);
		-moz-box-shadow: 0px 11px 17px -4px rgba(0,0,0,0.75);
	}
	
	.row-options-header {
		width: 100%;
		height: 20px;
		background: transparent !important;
		border-left: none !important;
		border-right: none !important;
		border-bottom: 1px solid #ccc;
	}
	
	.row-options-header:after {
		content: '';
		width: 0;
		height: 0;
		top: 100;
		position: absolute;
		border-left: 20px solid transparent;
		border-right: 20px solid transparent;
		border-bottom: 20px solid #ccc;
	}
	
	#vars_r .resize {
		display: none;
	}
	
	.variable:hover .resize {
		display: flex;
		width: 100%;
		justify-content: flex-end;
	}
	
	.resize:hover {
		display: inline;
	}
	
	.resize {
		display: none;
		position: absolute;
		top: 0;
		right: 0;
	}
	
	.prop-check input {
		width: auto;
	}
	
	.flex {
		display: flex;
	}
	
	.flex-end {
		justify-content: flex-end;
	}
	
	.txt-2 {
		font-size: 20px;
		text-align: justify;
		padding: 0.25em;
		background-color: #f1f1f1a1;
		cursor: pointer;
		z-index: 9999;
	}
	
	.var-spacing {
		background-color: #f8f8f8;
		display: flex;
		flex-direction: row;
		justify-content: center;
		align-items: center;
		cursor: pointer;
		color: transparent;
	}
	
	.var-spacing:hover {
		color: #7f7f7f;
	}
	
	.readonly_label {
		display: none;
	}
	
	.var-spacing > i {
		font-size: 30px;
	}
	
	#vars_r {
		margin:0;
	}
	
	#vars_r > .variable {
		width: 33.3333%
	}
	
	#vars_r .var-spacing {
		display: none;
	}
	
	#vars_r .row-options {
		display: none !important;
	}
	
	#vars_r .form-group {
		display: none !important;
	}
	
	#vars_r .readonly_label {
		display: inline-block;
	}
	
	.spinner-backdrop {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		background: rgb(79 79 101 / 39%);
		display: flex;
		flex-direction: row;
		align-content: center;
		justify-content: center;
		align-items: center;
	}
	
	.empty-space {
		height: 60px;
	}
	
	.checkbox {
		min-height: 27px;
	}
	
	.readonly-checkbox {
		border: 2px solid #ccc;
		border-radius: 4px;
	}
	
	.m-0 {
		margin: 0;
	}
	
	.pt-4 {
		padding-top: 1em;
	}
	
	</style>
	
	<script type="text/javascript">
	
	var getAllUrl = '${baseUrl}/variable/all';
	var resize = null;
	var variables = [];
	var selectedId;
	
	var mostrarAgrupacions = ${tasca.mostrarAgrupacions};
	
	$(function() {
		
		refrescaVariables();
		
		$('#goBack').on('click', function() {
			document.location = '${baseModalUrl}/variable';
		});
		
		$('body').on('click', '.var-spacing', function(ev) {
			$('#spinner').show();
			var el = $(this);
			var id = el.data('variable-id');
			var variableEl = $(id);
			var buitCols = variableEl.data('buit-cols');
			
			buitCols -= buitCols > 0? 1 : -1;
			
			variableEl.data('buit-cols', buitCols);
			addSpaceElement(variableEl.data('id'), buitCols);
			canviarValorTascaVariable(variableEl.data('id'), buitCols, 'buitCols', function() {
				refrescaVariables(function() {
					$('#spinner').hide();
				});
			});
		});
		
		$('#tasca-camp-form').submit(function(ev) {
			ev.preventDefault();
			submitForm($(this));
		});
	});
	
	function submitForm(form) {
		var actionUrl = form.attr('action');

		$.ajax({
			type: "POST",
			url: actionUrl,
			data: form.serialize(),
			complete: function(data)
			{
				document.location.reload();
			}
		});
	}
	
	function subCol(e) {
		var varId = $(e.srcElement).parent().data('var-id');
		var el = $('#'+varId);
		var ampleCols = el.data('ample-cols');
		
		if(ampleCols == 1) return;
		
		$('#spinner').show();
		var classes = el.attr('class').split(' ');
		var colClass = classes.find(c => c.startsWith('col-xs-'));
		if(colClass)
			el.removeClass(colClass);
		
		nouAmple = ampleCols - 1;
		el.addClass('col-xs-' + nouAmple);
		el.data('ample-cols', nouAmple);
		
		var buitCols = el.data('buit-cols');
		var totalCols = (nouAmple + buitCols);
		if(totalCols > 12) {
			buitCols = buitCols-(totalCols - 12)
			buitCols = buitCols >= 0? buitCols : 0;
		}
		
		el.data('buit-cols', buitCols);
		addSpaceElement(el.data('id'), buitCols);
		
		canviarValorTascaVariable(el.data('id'), nouAmple, 'ampleCols', function() {
			refrescaVariables(function() {
				$('#spinner').hide();
			});
		});
	}
	
	function sumCol(e) {
		var varId = $(e.srcElement).parent().data('var-id');
		var el = $('#'+varId);
		var ampleCols = el.data('ample-cols');
		
		if(ampleCols == 12) return;
		$('#spinner').show();
		
		var classes = el.attr('class').split(' ');
		var colClass = classes.find(c => c.startsWith('col-xs-'));
		if(colClass)
			el.removeClass(colClass);
		
		nouAmple = ampleCols + 1;
		el.addClass('col-xs-' + nouAmple);
		el.data('ample-cols', nouAmple);
		
		var buitCols = el.data('buit-cols');
		var totalCols = (nouAmple + buitCols);
		if(totalCols > 12) {
			buitCols = buitCols-(totalCols - 12)
			buitCols = buitCols >= 0? buitCols : 0;
		}
		el.data('buit-cols', buitCols);
		addSpaceElement(el.data('id'), buitCols);
		
		canviarValorTascaVariable(el.data('id'), nouAmple, 'ampleCols', function() {
			refrescaVariables(function() {
				$('#spinner').hide();
			});
		});
	}
	
	function refrescaVariables(callback) {
		$.ajax({
			type: 'GET',
			url: getAllUrl,
			async: true,
			success: function(data) {
				variables = data.sort((a, b) => a.order - b.order);
				drawVariables(data);
			},
			error: function(e) {
				console.log("Error obtenint variables: " + e);
			},
			complete: function() {
				callback&&callback();
			}
		});
	}
	
	function dragstartHandler(ev) {
		var el = $(ev.target).closest('.variable');
		if(!el.length)
			el = $(ev.target).closest('#tasca-camp-form');
		ev.dataTransfer.setData("draggedId", el.attr('id'));
	}

	function dragoverHandler(ev) {
		ev.preventDefault();
	}
	
	var dropedNew = false;
	function dropHandler(ev) {
		$('#spinner').show();
		ev.preventDefault();
		var draggedId = '#' + ev.dataTransfer.getData("draggedId");
		var dropedOnEl = $(ev.target);
		var draggedEl = $(draggedId);
		
		if(dropedNew) {
			$('#spinner').hide();
			return;
		}
		
		if(draggedId.includes('tasca-camp-form')) {
			dropedNew = true;
			var order = null;
			if(dropedOnEl.closest('.vars').attr('id') == 'vars_r') {
				$('#readOnly').prop('checked', 'checked');
				

			} else {
				if(dropedOnEl.attr('class').includes('empty-space')) {
					order = 1 + $('#var_' + dropedOnEl.data('id')).data('order');
				} else {
					order = $('#var_' + dropedOnEl.closest('.variable').data('id')).data('order');
				}
				$('#order').val(order);
			}
			submitForm(draggedEl);
			$('#spinner').hide();
			return;
		}
		
		var readOnly = draggedEl.data('readonly');
		if (dropedOnEl.attr('id') == 'vars_r'){
			draggedEl.data('readonly', true);
			dropedOnEl.append(draggedEl);
			readOnly = true;
		} else if(dropedOnEl.closest('.vars').attr('id') == 'vars_r') {
			draggedEl.data('readonly', true);
			dropedOnEl.closest('.variable').before(draggedEl);
			readOnly = true;
		} else {
			if(draggedEl.data('readonly')) {
				draggedEl.data('readonly', false);
				readOnly = false;
			}
			
			if(dropedOnEl.attr('class').includes('empty-space')) {
				$('#var_' + dropedOnEl.data('id')).after(draggedEl);
			} else {
				dropedOnEl.closest('.variable').before(draggedEl);
			}
		} 
		
		canviarValorTascaVariable(draggedEl.data('id'), readOnly, 'readOnly', function() {
			refreshOrder(draggedId, function() {
				refrescaVariables(function() {
					$('#spinner').hide();
					processing = false;
				});
			});
		});
	}
	
	function onClickEvent(ev) {
		if($(ev.target).parents('.resize').length)
			return;
		
		var el = $(ev.target).closest('.variable');
		var currentSelected = el.data('id');
		$('.row-options').hide();
		if(selectedId == currentSelected) {
			selectedId = null;
			return;
		}
		showEditionDialog(currentSelected, el);
	}
	
	function showEditionDialog(currentSelected, el) {
		
		selectedId = currentSelected;
		var resizeEl = el.children('.row-options');
		resizeEl.show();
		var marginAdjust = 100;
		var parentElement = el.closest('.well');
		
		var navPosition = $(parentElement).position();
		var navWidth = $(parentElement).width();
		var navRight = navPosition.left+navWidth;
		
		var position = el.position();
		var thisWidth = resizeEl.width();
		var thisRight = position.left+thisWidth-marginAdjust;
		
		if (thisRight > navWidth) resizeEl.css('margin-left', navWidth-thisRight);
	}
	
	function drawVariables() {
		$('.variable').remove();
		$('.empty-space').remove();
		$('.agrupacio').remove();
		
		if(mostrarAgrupacions) {
			var agrupacions = variables.reduce((reducer, v) => {
				if(v.camp.agrupacio&&!reducer.find(a => a.id == v.camp.agrupacio.id)){
					reducer.push(v.camp.agrupacio);
				}
				return reducer}, []).sort((a, b) => a.ordre - b.ordre);
			
			for(a of agrupacions) {
				var agrupacioEl = '<div class="agrupacio">' +
				'<fieldset><legend>'+ a.nom +'</legend></fieldset>' +
				'<div id="vars_a_'+ a.id +'" class="vars" ondrop="dropHandler(event)" ondragover="dragoverHandler(event)" >' +
				'</div></div>';
				$('#vars_w_container').append(agrupacioEl);
			}
		}
		
		var currentRow = $('<div class="row"><div>');
		var totalColsRow = 0;
		for(v of variables) {
			var agrupacioId = v.camp.agrupacio? v.camp.agrupacio.id : 0;
			
			var variableElement = '<div id="var_' + v.id + '" data-id="' + v.id + '" data-order="' + v.order + '"' +
							' data-readonly="' + v.readOnly + '"' +
							' data-buit-cols="' + v.buitCols + '"' +
							' data-ample-cols="'+ v.ampleCols +'"' +
							' data-agrupacio-id="'+ agrupacioId +'"' +
							' class="variable col-xs-' + v.ampleCols + '">' +
								'<div id="camp_'+ v.id +'" class="col-xs-12 p-2 camp" onclick="onClickEvent(event)" ' +
								' draggable="true" ondrop="dropHandler(event)" ondragover="dragoverHandler(event)" ondragstart="dragstartHandler(event)">' +
									'<b class="readonly_label p-2">' + v.camp.etiqueta + '</b>' +
									makeField(v.camp.tipus, v.camp.etiqueta, v.required) +
									'<div class="resize" data-var-id="var_' + v.id + '">' +
										'<i title="' + '<spring:message code="definicio.proces.tasca.variable.columna.reduir.ampleCols"/>' + '" class="txt-2 fa fa-minus" onclick="subCol(event)"/>' +
										'<i title="' + '<spring:message code="definicio.proces.tasca.variable.columna.afegir.ampleCols"/>' + '" class="txt-2 fa fa-plus" onclick="sumCol(event)"/>' +
										'<i title="' + '<spring:message code="definicio.proces.tasca.variable.columna.afegir.buitCols"/>' + '" class="txt-2 fa fa-arrows-h" onclick="addSpace(event)"/>' +
									'</div>' +
								' </div>' +
								'<div class="row-options" data-var-id="var_' + v.id + '">' +
									'<div class="row-options-header"></div>' +
									'<div class="row-options-body col-xs-12">' +
										'<div class="flex col-xs-12 p-4 pt-4">' +
											'<div class="col-sm-4 p-1">' +
												'<label for="readFrom_'+ v.id +'" class="checkbox inputCheckbox m-0">' +
													'<spring:message code="definicio.proces.tasca.variable.columna.readFrom" />' +
													'<input class="prop-check" type="checkbox" id="readFrom_'+ v.id +'" ' + (v.readFrom&&'checked') + ' data-variableid="'+ v.id +'" data-propietat="readFrom"/>' +
												'</label>' +
											'</div>' +
											'<div class="col-sm-4 p-1">' +
												'<label for="writeTo_'+ v.id +'" class="checkbox inputCheckbox m-0">' +
													'<spring:message code="definicio.proces.tasca.variable.columna.writeTo" />' +
													'<input class="prop-check" type="checkbox" id="writeTo_'+ v.id +'" ' + (v.writeTo&&'checked') + ' data-variableid="'+ v.id +'" data-propietat="writeTo"/>' +
												'</label>' +
											'</div>' +
											'<div class="col-sm-4 p-1">' +
												'<label for="required_'+ v.id +'" class="checkbox inputCheckbox m-0">' +
													'<spring:message code="definicio.proces.tasca.variable.columna.required" />' +
													'<input class="prop-check" type="checkbox" id="required_'+ v.id + '" ' + (v.required&&'checked') + ' data-variableid="'+ v.id +'" data-propietat="required"/>' +
												'</label>' +
											'</div>' +
										'</div>' +
										
										'<div class="col-xs-12 p-4">' +
										'	<div class="col-xs-4 p-1">' +
										'		<div class="form-group mb-0">' +
										'			<label for="ampleCols" class="m-0">' +
										'				<spring:message code="definicio.proces.tasca.variable.columna.ampleCols" />' +
										'			</label>' +
										'			<input id="ampleCols_'+v.id+'" class="form-control ample" type="number" name="ampleCols" value="'+v.ampleCols+'" data-propietat="required"/>' +
										'		</div>' +
										'	</div>' +
										'	<div class="col-xs-4 p-1">' +
										'		<div class="form-group mb-0">' +
										'			<label for="buitCols" class="m-0">' +
										'				<spring:message code="definicio.proces.tasca.variable.columna.buitCols" />' +
										'			</label>' +
										'			<input id="buitCols_'+v.id+'" class="form-control ample" type="number" name="buitCols" value="'+v.buitCols+'" data-propietat="required"/>' +
										'		</div>' +
										'	</div>' +
										'	<div class="col-xs-4 p-1">' +
										'		<div class="form-group mb-0">' +
										'			<label for="buitCols" class="m-0" style="color: transparent;font-size: small;"><spring:message code="comu.boto.esborrar"/></label>' +
										'			<span onclick="deleteCamp('+ v.id +')" class="btn btn-default" data-confirm="<spring:message code="definicio.proces.tasca.variable.confirmacio.esborrar"/>">' +
										'				<span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/>' +
										'			</span>' +
										'		</div>' +
										'	</div>' +
										'</div>' +
									'</div>' +
								'</div>' +
							'</div>';
			
			if(v.readOnly) {
				$('#vars_r').append(variableElement);
			} else {
				var cols = Math.abs(v.buitCols) + v.ampleCols;
				
				if(totalColsRow + cols > 12) {
					currentRow = $('<div class="row"><div>');
					totalColsRow = 0;
				}
				
				if(totalColsRow == 0) {
					
					if(mostrarAgrupacions && v.camp.agrupacio) {
						$('#vars_a_'+v.camp.agrupacio.id).append(currentRow);
					} else {
						$('#vars_w').append(currentRow);
					}
				}
				totalColsRow += cols;
				currentRow.append(variableElement);
			}
		}
		drawSpaces();
		
		$('.prop-check').change(function() {
			$('#spinner').show();
			updateCheckbox(this, function() {
				refrescaVariables(function () {
					$('#spinner').hide();
				});
			});
		});
		
		$('.ample').on('input', function() {
			$('#spinner').show();
			var el = $(this);
			var variableEl = el.closest('.variable');
			var id = el.attr('id');
			var nouAmple = el.val();
			switch(el.attr('name')){
			case 'ampleCols':
				if(nouAmple > 12) nouAmple = 12;
				if(nouAmple < 1) nouAmple = 1;
				canviarValorTascaVariable(variableEl.data('id'), nouAmple, 'ampleCols', function() {
					refrescaVariables(function() {
						$('#spinner').hide();
						$('#'+id).select();
					});
				});
				break;
			case 'buitCols':
				var buitCols = variableEl.data('buit-cols');
				var ampleCols = variableEl.data('ample-cols');
				
				if((buitCols + ampleCols) >= 12) {
					el.val(buitCols);
					nouAmple = 0;
				}
				
				canviarValorTascaVariable(variableEl.data('id'), nouAmple, 'buitCols', function() {
					refrescaVariables(function() {
						$('#spinner').hide();
						$('#'+id).select();
					});
				});
				break;
			}
		});
		
		if(selectedId) {
			var el = $('#var_'+selectedId);
			var currentSelected = el.data('id');
			showEditionDialog(currentSelected, el);
		}
	}
	
	function addSpace(e) {
		$('#spinner').show();
		var varId = $(e.srcElement).parent().data('var-id');
		var el = $('#'+varId);
		var buitCols = el.data('buit-cols');
		var ampleCols = el.data('ample-cols');
		
		if((Math.abs(buitCols) + ampleCols) >= 12) {
			$('#spinner').hide();
			return;
		}
		
		buitCols += buitCols >= 0 ? 1 : -1;
		
		el.data('buit-cols', buitCols);
		canviarValorTascaVariable(el.data('id'), buitCols, 'buitCols', function() {
			refrescaVariables(function() {
				$('#spinner').hide();
			});
		});
		$('.row-options').hide();
	}
	
	function drawSpaces() {
		$('.var-spacing').remove();
		for(v of variables) {
			if(Math.abs(v.buitCols)) {
				addSpaceElement(v.id, v.buitCols);
			}
		}
		
		var rowCols = 0;
		var wvars = variables.filter(v => !v.readOnly);
		if(mostrarAgrupacions) {
			wvars = wvars.sort((a, b) => (a.camp.agrupacio? a.camp.agrupacio.ordre:-1) - (b.camp.agrupacio? b.camp.agrupacio.ordre:-1));
		}
		var lastIndex = wvars.length - 1;
		for(i in wvars) {
			var v = wvars[i];
			
			if(mostrarAgrupacions && (i) > 0) {
				var prevVarible = wvars[i-1];
				if(prevVarible && (v.camp.agrupacio?v.camp.agrupacio.id : 0) != (prevVarible.camp.agrupacio?prevVarible.camp.agrupacio.id : 0)) {
					rowCols = 0;
				}
			}
			
			var cols = Math.abs(v.buitCols) + v.ampleCols;
			var totalCols = (rowCols + cols);
			if(totalCols > 12) {
				var remainingSpace = 12 - rowCols;
				var prevVar = wvars[i-1];
				
				$('#var_' + prevVar.id).after('<div ondrop="dropHandler(event)" ondragover="dragoverHandler(event)" data-id="'+ prevVar.id +'" class="empty-space col-xs-'+ remainingSpace +'"></div>');
				rowCols = cols == 12? 0 : cols;
			} else {
				rowCols += cols;
			}
			
			if(lastIndex == i && totalCols < 12 ) {
				var remainingSpace = 12 - totalCols;
				$('#var_' + v.id).after('<div ondrop="dropHandler(event)" ondragover="dragoverHandler(event)" data-id="'+ v.id +'" class="empty-space col-xs-'+ remainingSpace +'"></div>');
			}
		}
	}
	
	function addSpaceElement(variableId, cols) {
		var elVariableId = '#var_' + variableId;
		var elSpaceId = 'space_' + variableId;
		
		var spaceElement = $('#'+elSpaceId);
		
		var variableElement = $(elVariableId);
		var campElement = $('#camp_' + variableId);
		
		var ampleCols = variableElement.data('ample-cols');
		var colClass = variableElement.attr('class').split(' ').find(c => c.startsWith('col-xs-'));
		if(colClass)
			variableElement.removeClass(colClass);
		
		var campColClass = campElement.attr('class').split(' ').find(c => c.startsWith('col-xs-'));
		if(campColClass)
			campElement.removeClass(campColClass);
		
		var totalCols = (Math.abs(cols) + ampleCols)
		var campCols = Math.round((ampleCols/totalCols)*12)||1;
		var spaceCols = Math.round((Math.abs(cols)/totalCols)*12)||1;
		
		variableElement.addClass('col-xs-' + totalCols);
		campElement.addClass('col-xs-' + campCols);
		
		if(cols == 0) {
			spaceElement.remove();
		} else if(spaceElement.length) {
			var colClass = spaceElement.prop('class').split(' ').find(c => c.startsWith('col-xs-'));
			spaceElement.removeClass(colClass);
			spaceElement.addClass('col-xs-' + spaceCols);
		} else {
			if(cols > 0) {
				campElement.after('<div class="var-spacing b-radius-r col-xs-'+ spaceCols +'" id="'+ elSpaceId +'" data-variable-id="'+ elVariableId +'">buit[' + cols + ']</div>');
			} else {
				campElement.before('<div class="var-spacing b-radius-l col-xs-'+ spaceCols +'" id="'+ elSpaceId +'" data-variable-id="'+ elVariableId +'">buit[' + cols + ']</div>');
			}
		}
	}
	
	function refreshOrder(id, callback) {
		var orders = [];
		var wVars = $('#vars_w_container .variable');
		var currentOrder;
		var checkNext = false;
		var agrupacioId = 0;
		var campElement = $(id);
		var campId = campElement.data('id');
		
		wVars.each(function(i, e) {
			var el = $(e);
			if(mostrarAgrupacions && checkNext && agrupacioId == el.data('agrupacio-id')) {
				// Si la agrupació esta activa
				// S'ha de comprovar el seguent camp de la mateixa agrupació
				// per poder posar devant l'actual
				var order = el.data('order');
				var campElementOrder = campElement.data('order');
				
				if(order > campElementOrder) {
					currentOrder = order > 0? order - 1 : 0;
				} else {
					currentOrder = order > 0? order : 0;
				}
				checkNext = false;
			}
			
			if(campId == el.data('id')) {
				currentOrder = i;
				checkNext = true;
				agrupacioId = el.data('agrupacio-id');
			}
		});
		var wVarsLen = wVars.length;
		$('#vars_r>.variable').each(function(i, e) {
			if(campId == $(e).data('id')) {
				currentOrder = wVarsLen+i;
			}
		});
		
		canviarPosicioTascaVariable(campId, currentOrder, callback);
	}
	
	/* Actualitza un valor del camp de la tasca. */
	function updateCheckbox(checkbox, callback) {
		var variableId = $(checkbox).data('variableid');
		var propietat = $(checkbox).data('propietat');
		var getUrl = '${baseUrl}/variable/'+variableId+'/'+propietat;
		var spin = $("#accioUpdateProcessant")
			.clone()
			.show()
			.insertAfter(checkbox);
		$(checkbox).hide();
		$.ajax({
			type: 'POST',
			url: getUrl,
			data: {
				valor : $(checkbox).is(':checked')
			},
			async: true,
			success: function(result) {
			},
			error: function(error) {
				console.log('Error:'+error);
			},
			complete: function() {
				webutilRefreshMissatges();
				callback&&callback();
			}
		});
	}
	
	function canviarPosicioTascaVariable(id, pos, callback) {
		// Canvia la ordenació sempre amb ordre ascendent
		$('#tascaVariable').DataTable().order([6, 'asc']);
		var _getUrl = '${baseUrl}/variable/'+id+'/moure/'+pos;
		$.ajax({
			type: 'GET',
			url: _getUrl,
			async: true,
			success: function(result) {
				$('#tascaVariable').webutilDatatable('refresh');
			},
			error: function(e) {
				console.log("Error canviant l'ordre: " + e);
				$('#tascaVariable').webutilDatatable('refresh');
			},
			complete: function() {
				webutilRefreshMissatges();
				callback&&callback();
			}
		});	
	}
	
	function deleteCamp(id) {
		$('#spinner').show();
		var _getUrl = '${baseUrl}/variable/'+id+'/delete';
		$.ajax({
			type: 'GET',
			url: _getUrl,
			async: true,
			success: function(result) {
			},
			error: function(error) {
				console.log('Error:'+error);
			},
			complete: function() {
				document.location.reload();
				/*
				webutilRefreshMissatges();
				if(selectedId == id) {
					selectedId = null;
				}
				
				refrescaVariables(function() {
					$('#spinner').hide();
				});
				*/
			}
		});
	}
	

	function canviarValorTascaVariable(id, valor, propietat, callback) {
		var _getUrl = '${baseUrl}/variable/'+id+'/' + propietat;
		$.ajax({
			type: 'POST',
			url: _getUrl,
			data: {
				valor : valor
			},
			async: true,
			success: function(result) {
			},
			error: function(error) {
				console.log('Error:'+error);
			},
			complete: function() {
				webutilRefreshMissatges();
				callback&&callback();
			}
		});
	}
	
	function makeField(tipus, etiqueta, required) {
		switch(tipus) {
		case 'ACCIO':
			return '<div class="form-group p-2">' +
						'<label for="interessats_helium_accion" class="control-label " style="width: 130px; float: left; padding-right: 11px;text-align: right;">'+ etiqueta +'</label>' +
						'<div class="controls  like-cols   no-obligatori" style="width: calc(100% - 130px);">' +
						'	<button class="btn btn-primary pull-lef btn_accio tasca-boto" name="accio" value="accio">' +
							'<spring:message code="expedient.info.accio.executar"/>' +
						'	</button>' +
						'</div>' +
					'</div>';
		case 'STRING':
		case 'INTEGER':
		case 'FLOAT':
		case 'SELECCIO':
		case 'DATE':
		case 'PRICE':
		case 'SUGGEST':
		case 'TEXTAREA':
			return '<div class="form-group p-2">' +
						'<label class="control-label '+ (required?'obligatori': '') +'" style="width: 130px; float: left; padding-right: 11px;text-align: right;">'+ etiqueta +'</label>' +
						'<div class="controls like-cols" style="width: calc(100% - 130px);">' +
							'<span class="form-control">' +
						'</div>' +
					'</div>';
		case 'BOOLEAN':
			return '<div class="form-group p-2">'+
						'<label class="control-label '+ (required?'obligatori': '') +'" style="width: 130px; float: left; padding-right: 11px;text-align: right;">'+ etiqueta +'</label>'+
						'<div class="controls like-cols '+ (required?'obligatori': 'no-obligatori') +'" style="width: calc(100% - 130px);">'+
						'<span class="checkbox readonly-checkbox" style="max-width: 27px;">'+
						'</div>'+
					'</div>';
		case 'REGISTRE':
					return '<div class="form-group registre p-2">' +
					'	<label class="control-label" style="width: 130px; float: left; padding-right: 11px;text-align: right;">' + etiqueta + '</label>' +
					'	<div class="controls registre like-cols" style="width: calc(100% - 130px);">' +
					'		<div class="registre_taula">' +
					'			<table class="table table-bordered table-condensed">' +
					'				<thead>' +
					'					<tr>' +
					'						<th>' +
					'							<label class="col-xs-12">' + etiqueta + '</label>' +
					'						</th>' +
					'					</tr>' +
					'				</thead>' +
					'				<tbody>' +
					'				</tbody>' +
					'			</table>' +
					'		</div>' +
					'	</div>' +
					'</div>';
		default:
			return  '<div class="form-group p-2">' +
						'<label class="control-label" style="width: 130px; float: left; padding-right: 11px;text-align: right;">'+ etiqueta +'</label>' +
					'</div>';
		}
	}
	
	</script>
	
</head>
<body>
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
		<button id="goBack" type="button" class="btn btn-primary"><spring:message code="comu.boto.tornar"/></button>
	</div>
	
	<div style="height: 100px">
		<div class="floating-form" title="Arrossegar per afegir el camp">
			<form:form id="tasca-camp-form" cssClass="well" action="${baseModalUrl}/variable/new" enctype="multipart/form-data" method="post" commandName="definicioProcesTascaVariableCommand" draggable="true" ondragstart="dragstartHandler(event)">
				<input type="hidden" name="tascaId" id="inputTascaId" value="${definicioProcesTascaVariableCommand.tascaId}"/>
				<input type="hidden" name="order" id="order" value="${definicioProcesTascaVariableCommand.order}"/>
				<div class="row">
					<div class="col-sm-4">
						<hel:inputSelect inline="true" required="true" emptyOption="true" name="campId" textKey="definicio.proces.tasca.variable.form.variable" placeholderKey="definicio.proces.tasca.variable.form.variable.placeholder" optionItems="${variables}" optionValueAttribute="codi" optionTextAttribute="valor"/>
					</div>
					<div class="col-sm-6">
						<div class="row">
							<div class="col-sm-3">
								<hel:inputCheckbox inline="true" name="readFrom" textKey="definicio.proces.tasca.variable.columna.readFrom" />
							</div>
							<div class="col-sm-3">
								<hel:inputCheckbox inline="true" name="writeTo" textKey="definicio.proces.tasca.variable.columna.writeTo" />
							</div>
							<div class="col-sm-3">
								<hel:inputCheckbox inline="true" name="required" textKey="definicio.proces.tasca.variable.columna.required" />
							</div>
							<div class="col-sm-3">
								<hel:inputCheckbox inline="true" name="readOnly" textKey="definicio.proces.tasca.variable.columna.readOnly" />
							</div>
						</div>
					</div>
					<div class="col-sm-2 right">
						<button id="btnCreate" class="btn btn-primary right" name="accio" value="crear">
							<span class="fa fa-plus"></span> <spring:message code='definicio.proces.tasca.variable.accio.afegir' />
						</button>
					</div>
				</div>
			</form:form>
		</div>
	</div>
	<div style="height: 500px;">
		<div class="well p-0">
			<div id="grup-default-titol" class="panel-heading clicable grup tauladades" data-toggle="collapse" data-target="#grup-default-dades" aria-expanded="true">
				<spring:message code="definicio.proces.tasca.variable.disseny.refdata" />
			</div>
			<div id="vars_r" class="vars row bg-white" ondrop="dropHandler(event)" ondragover="dragoverHandler(event)" >
			</div>
		</div>
		
		<div id="vars_w_container" class="well bg-white">
			<div id="vars_w" class="vars" ondrop="dropHandler(event)" ondragover="dragoverHandler(event)" >
			</div>
		</div>
		
		<span id="accioUpdateProcessant" style="display: none;">
			<span class="fa fa-spinner fa-spin fa-fw" title="<spring:message code="comu.processant"/>"></span><span class="sr-only">&hellip;</span>
		</span>
	</div>
	<div id="spinner" style="display: none;">
		<div class="spinner-backdrop">
			<span id="accioUpdateProcessant">
				<span class="fa fa-spinner fa-spin fa-fw" style="font-size: 150px;" title="<spring:message code="comu.processant"/>"></span><span class="sr-only">&hellip;</span>
			</span>
		</div>
	</div>
	
</body>
		
