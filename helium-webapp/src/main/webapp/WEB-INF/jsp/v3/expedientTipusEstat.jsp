<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>

<c:url var="urlDatatable" value="/v3/expedientTipus/${expedientTipus.id}/estats/datatable"/>

<c:choose>
	<c:when test="${not empty expedientTipus}">

		<table	id="expedientTipusEstat"
				data-toggle="datatable"
				data-url="${urlDatatable}"
				data-paging-enabled="true"
				data-info-type="search+button"
				data-rowhref-toggle="modal"
				data-rowhref-template="#rowhrefTemplateEstats" 
				data-botons-template="#tableButtonsEstatTemplate"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="codi" data-orderable="false" width="20%" data-template="#cellExpedientTipusEstatCodiTemplate">
					<spring:message code="expedient.tipus.camp.llistat.columna.codi"/>
						<script id="cellExpedientTipusEstatCodiTemplate" type="text/x-jsrender">
								{{if heretat }}
									<span class="dada-heretada">{{:codi}}</span> 
									<span class="label label-primary" title="<spring:message code="expedient.tipus.estat.llistat.codi.heretat"/>">R</span>
								{{else}}
									{{:codi}}
									{{if sobreescriu }}
										<span class="label label-warning" title="<spring:message code="expedient.tipus.estat.llistat.codi.sobreescriu"/>">S</span>
									{{/if}}
								{{/if}}
						</script>
					</th>
					<th data-col-name="nom" data-orderable="false"><spring:message code="comuns.nom"/></th>
					<th data-col-name="id" data-template="#cellEstatsTemplate" data-orderable="false" width="10%">
						<script id="cellEstatsTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								{{if heretat}}
									<li><a data-toggle="modal" href="${expedientTipus.id}/estat/{{:id}}/update"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.visualitzar"/></a></li>
								{{else}}
									<li><a data-toggle="modal" data-callback="callbackModalEstats()" href="${expedientTipus.id}/estat/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
									<li><a href="${expedientTipus.id}/estat/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.estat.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
								{{/if}}
							</ul>
						</div>
					</script>
					</th>
					<th data-col-name="sobreescriu" data-visible="false"/>
					<th data-col-name="heretat" data-visible="false"/>
				</tr>
			</thead>
		</table>
		<script id="tableButtonsEstatTemplate" type="text/x-jsrender">
			<div class="botons-titol text-right">
				<a id="nou_camp" class="btn btn-default" href="${expedientTipus.id}/estat/new" data-toggle="modal" data-callback="callbackModalEstats()" data-datatable-id="expedientTipusEstat"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.estat.nou"/></a>
				<a id="importar_dades" class="btn btn-info" href="${expedientTipus.id}/estat/importar" data-toggle="modal" data-callback="callbackModalEstats()" data-datatable-id="expedientTipusEstat"><span class="fa fa-sign-in"></span>&nbsp;<spring:message code="comu.boto.importar.dades"/></a>
			</div>
		</script>
		<script id="rowhrefTemplateEstats" type="text/x-jsrender">${expedientTipus.id}/estat/{{:id}}/update</script>
				
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>
<script>
$(document).ready(function() {
	$('#expedientTipusEstat').on('draw.dt', function() {
		// Posa la taula com a ordenable
		$("#expedientTipusEstat").tableDnD({
	    	onDragClass: "drag",
	    	onDrop: function(table, row) {	     
	    		var pos = row.rowIndex - 1;
	        	var id= obtenirId(pos);
	        	if (pos != filaMovem) {
	        		canviarPosicioEstat(id,pos);
	    			$('tr').off('click');
	    			$('td').off('click');
	        	}
	    	},
	    	onDragStart: function(table, row) {
	    			filaMovem = row.rowIndex-1;
			}
	    });
		$("#expedientTipusEstat tr").hover(function() {
	        $(this.cells[0]).addClass('showDragHandle');
	    }, function() {
	        $(this.cells[0]).removeClass('showDragHandle');
	    });
	});
});

function callbackModalEstats() {
	// Refresca els missatges
	webutilRefreshMissatges();
}

function canviarPosicioEstat(id, pos) {
  	// Canvia la ordenació sempre amb ordre ascendent
	$('#campValidacio').DataTable().order([3, 'asc']);
	var getUrl = '<c:url value="/v3/expedientTipus/${expedientTipusId}/estat/"/>'+id+'/moure/'+pos;
	$.ajax({
		type: 'GET',
		url: getUrl,
		async: true,
		success: function(result) {
			$('#expedientTipusEstat').webutilDatatable('refresh');
		},
		error: function(e) {
			console.log("Error canviant l'ordre: " + e);
			$('#expedientTipusEstat').webutilDatatable('refresh');
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
	id = $("#expedientTipusEstat tr:eq("+fila+")").attr("id");	
	id2 = id.split("_");
	return id2[1] ;
}

</script>