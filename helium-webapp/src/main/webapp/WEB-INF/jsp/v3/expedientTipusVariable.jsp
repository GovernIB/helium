<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>


<c:choose>
	<c:when test="${not empty expedientTipus}">

		<div class="row well well-small">
			<div class="col-sm-10">
				<hel:inputSelect required="false" emptyOption="true" name="agrupacions" textKey="expedient.tipus.camp.llistat.agrupacio.seleccionada" placeholderKey="expedient.tipus.camp.llistat.agrupacio.seleccionada" optionItems="${agrupacions}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			</div>
			<div class="col-sm-2">
				<div id="agrupacionsAccions" class="dropdown">
					<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
					<ul class="dropdown-menu">
						<li><a href="${expedientTipus.id}/agrupacio/new" data-toggle="modal" data-callback="callbackModalAgrupacions()"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.camp.llistat.agrupacio.boto.crear"/></a></li>
						<li style="display: none;"><a id="agrupacioUpdate" href="${expedientTipus.id}/agrupacio/update" data-toggle="modal" data-callback="callbackModalAgrupacions()"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.camp.llistat.agrupacio.boto.modificar"/></a></li>
						<li style="display: none;"><a id="agrupacioDelete" href="${expedientTipus.id}/agrupacio/delete" data-confirm="<spring:message code="expedient.tipus.camp.llistat.agrupacio.boto.esborrar.confirm"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.tipus.camp.llistat.agrupacio.boto.esborrar"/></a></li>
						<li class="divider"></li>
						<li><a href="${expedientTipus.id}/agrupacio" data-toggle="modal" data-callback="refrescarAgrupacions()"><span class="fa fa-arrows-v"></span>&nbsp;<spring:message code="expedient.tipus.camp.llistat.agrupacio.boto.ordenar"/></a></li>
					</ul>
				</div>
			</div>
		</div>

		<div class="botons-titol text-right">
			<a id="nou_camp" class="btn btn-default" href="${expedientTipus.id}/variable/new" data-toggle="modal" data-callback="callbackModalVariables()" data-datatable-id="expedientTipusVariable"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.camp.llistat.accio.nova"/></a>
		</div>
		<table	id="expedientTipusVariable"
				data-rowId="id"
				data-toggle="datatable"
				data-url="${expedientTipus.id}/variable/datatable"
				data-paging-enabled="true"
				data-info-type="search"
				data-ordering="true"
				data-default-order="3"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="agrupacio" data-visible="false"/>
					<th data-col-name="ordre" width="5%"><spring:message code="expedient.tipus.camp.llistat.columna.ordre"/></th>
					<th data-col-name="codi" width="20%"><spring:message code="expedient.tipus.camp.llistat.columna.codi"/></th>
					<th data-col-name="etiqueta"><spring:message code="expedient.tipus.camp.llistat.columna.etiqueta"/></th>
					<th data-col-name="tipus"><spring:message code="expedient.tipus.camp.llistat.columna.tipus"/></th>
					<th data-col-name="multiple"><spring:message code="expedient.tipus.camp.llistat.columna.multiple"/></th>
					<th data-col-name="validacioCount" data-template="#cellValidacionsTemplate" data-orderable="false" width="13%">
						<script id="cellValidacionsTemplate" type="text/x-jsrender">
						<a href="${expedientTipus.id}/variable/{{:id}}/validacio" data-toggle="modal" data-callback="callbackModalVariables()" class="btn btn-default"><spring:message code="expedient.tipus.camp.llistat.accio.validacions"/>&nbsp;<span class="badge">{{:validacioCount}}</span></a>
					</script>
					</th>
					<th data-col-name="id" data-template="#cellAccionsVariableTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsVariableTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a data-toggle="modal" data-callback="callbackModalVariables()" href="${expedientTipus.id}/variable/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
								<li><a href="${expedientTipus.id}/variable/{{:id}}/delete" class="ajax-link" data-confirm="<spring:message code="expedient.tipus.camp.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
								<li class="divider"></li>
								<li id="accioAgrupacions">
									{{if agrupacio == null}}
										<span class="fa fa-plus" style="margin-left:10px"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.agrupar"/>
										<br/>						
									{{else}}
										<a href="${expedientTipus.id}/variable/{{:id}}/desagrupar"
												class="ajax-link"><span class="fa fa-minus" data-rdt-link-ajax="true"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.desagrupar"/>
									{{/if}}																		
								</li>
							</ul>
						</div>
					</script>
					</th>
				</tr>
			</thead>
		</table>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
// <![CDATA[            
$(document).ready(function() {
		
	// Botons de modificar i eliminar agrupacions
	$('#agrupacioDelete').click(function(e) {
		var getUrl = $(this).attr('href');
		$.ajax({
			type: 'GET',
			url: getUrl,
			async: true,
			success: function(result) {
				webutilRefreshMissatges();
				if (result) {
					refrescarAgrupacions();
				}
			},
			error: function(e) {
				debugger;
				var msg = 'Error esborrant la agrupació'; 
				alert(msg);
				console.log(msg+': '+e);
			}
		});
		e.stopImmediatePropagation();
		e.preventDefault();
		return false;
	});
	
	// Canvi en la selecció de les agrupacions
	$('#agrupacions').change(function() {
		var agrupacioId = $(this).val();
		if (agrupacioId != "") {
			$('#nou_camp').attr('href', '${expedientTipus.id}/variable/new?agrupacioId=' + agrupacioId);
			$('#agrupacioUpdate').attr('href', '${expedientTipus.id}/agrupacio/' + agrupacioId + '/update');
			$('#agrupacioDelete').attr('href', '${expedientTipus.id}/agrupacio/' + agrupacioId + '/delete');
			$('#agrupacioUpdate,#agrupacioDelete').closest('li').show();			
			// Mostra la columna d'ordre
			$('#expedientTipusVariable').DataTable().order([2, 'asc']);
			$('#expedientTipusVariable').DataTable().column(2).visible(true);
		} else {
			$('#nou_camp').attr('href', '${expedientTipus.id}/variable/new');
			$('#agrupacioUpdate,#agrupacioDelete').closest('li').hide();
			// Amaga la columna d'ordre
			$('#expedientTipusVariable').DataTable().order([3, 'asc']);
			$('#expedientTipusVariable').DataTable().column(2).visible(false);
		}
		refrescaTaula();
	});	
	// Amaga la columna d'ordre
	$('#expedientTipusVariable').DataTable().column(2).visible(false);

	// Quan es repinta la taula actualitza els enllaços
	$('#expedientTipusVariable').on('draw.dt', function() {
		// Afegeix les opcions per a agrupar de les agrupacions existents
		if ($("#agrupacions").val() == "") {
			$("tr", this).each(function(){
				if ($(this).find("#accioAgrupacions").length > 0) {	
					var campId = $(this).attr('id').replace('row_','');
					$agrupacions = $(this).find("#accioAgrupacions");
					$("#agrupacions option").each(function(){
						if ($(this).val() != "")
							$agrupacions.append("<a href='${expedientTipus.id}/variable/"+campId+"/agrupar/"+$(this).val()+"' class='ajax-link'>"+$(this).text()+"</a>");
					});
				}
			});	
		}
		// Botons per agrupar o desagrupar
		$(".ajax-link").click(function(e) {
			var getUrl = $(this).attr('href');
			$.ajax({
				type: 'GET',
				url: getUrl,
				async: true,
				success: function(result) {
					if (result) {
						refrescaTaula();
					}
					webutilRefreshMissatges();
				},
				errlr: function(error) {
					webutilRefreshMissatges();
					console.log('Error:'+error);
				}
			});
			e.stopImmediatePropagation();
			return false;
		});
		if ($('#agrupacions').val() != "") {
			// Posa la taula com a ordenable
			$("#expedientTipusVariable").tableDnD({
		    	onDragClass: "drag",
		    	onDrop: function(table, row) {	        	
		        	var pos = row.rowIndex - 1;
		        	var id= obtenirId(pos);
		        	canviarPosicioVariable(id,pos);
		    	},
		    	onDragStart: function(table, row) {
		    			filaMovem = row.rowIndex-1;
				}
		    });
		    $("#expedientTipusVariable tr").hover(function() {
		        $(this.cells[0]).addClass('showDragHandle');
		    }, function() {
		        $(this.cells[0]).removeClass('showDragHandle');
		    });	
		  	// Canvia la ordenació
			$('#expedientTipusVariable').DataTable().order([2, 'asc']);
		}
	  });		
});

function canviarPosicioVariable( id, pos) {
	var getUrl = '${expedientTipus.id}/variable/'+id+'/moure/'+pos;
	$.ajax({
		type: 'GET',
		url: getUrl,
		async: true,
		success: function(result) {
			refrescaTaula();
		},
		error: function(e) {
			console.log("Error canviant l'ordre: " + e);
			refrescaTaula();
		}
	});	
}

function obtenirId(pos){
	if(filaMovem==pos){
		var fila = filaMovem + 1;
	}
	else{
	
		if( filaMovem < pos){	//baixam elements
			var fila = filaMovem + (pos-filaMovem)+1;
		}else{					//pujam elements
			var fila = filaMovem - (filaMovem-pos)+1;
		}
	}
	id = $("#expedientTipusVariable tr:eq("+fila+")").attr("id");	
	id2 = id.split("_");
	return id2[1] ;
}

function refrescaTaula() {
	var agrupacioId = $("#agrupacions").val();
	if (agrupacioId != "" && agrupacioId != null) {
		$('#expedientTipusVariable').webutilDatatable('refresh-url', '${expedientTipus.id}/variable/datatable?agrupacioId='+agrupacioId);		
	} else {
		$('#expedientTipusVariable').webutilDatatable('refresh-url', '${expedientTipus.id}/variable/datatable');
	}
}

function refrescarAgrupacions() {
	$("#agrupacionsAccions").removeClass('open');
	var getUrl = '${expedientTipus.id}/agrupacio/select';
	var vActual = $("#agrupacions").val();
	$.ajax({
		type: 'GET',
		url: getUrl,
		async: true,
		success: function(data) {
			$("#agrupacions option").each(function(){
			    $(this).remove();
			});
			$("#agrupacions").append($("<option/>"));
			for (i = 0; i < data.length; i++) {
				$("#agrupacions").append($("<option/>", {value: data[i].codi, text: data[i].valor}));
			}
			$("#agrupacions").val(vActual).change();
		},
		error: function(e) {
			console.log("Error obtenint agrupacions: " + e);
			refrescaTaula();
		}
	});
}

function callbackModalVariables() {
	webutilRefreshMissatges();
	refrescaTaula();
}

function callbackModalAgrupacions() {
	webutilRefreshMissatges();
	refrescarAgrupacions();
}

// ]]>
</script>			
