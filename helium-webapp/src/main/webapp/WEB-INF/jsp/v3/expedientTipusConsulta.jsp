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

		<table	id="expedientTipusConsulta"
				data-rowId="id"
				data-toggle="datatable"
				data-url="${expedientTipus.id}/consulta/datatable"
				data-paging-enabled="true"
				data-info-type="search+button"
				data-ordering="true"
				data-default-order="8"
				data-botons-template="#tableButtonsConsultaTemplate"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="expedientTipus.id" data-visible="false"/>
					<th data-col-name="codi" width="20%"><spring:message code="expedient.tipus.consulta.llistat.columna.codi"/></th>
					<th data-col-name="nom"><spring:message code="expedient.tipus.consulta.llistat.columna.titol"/></th>
					<th data-col-name="varsFiltreCount" data-template="#cellVarsFiltreTemplate" data-orderable="false" width="13%">
						<script id="cellVarsFiltreTemplate" type="text/x-jsrender">
							<a href="${expedientTipus.id}/consulta/{{:id}}/varFiltre" data-toggle="modal" data-callback="callbackModalConsultes()" class="btn btn-default"><spring:message code="expedient.tipus.consulta.llistat.accio.variables.filtre"/>&nbsp;<span class="badge">{{:varsFiltreCount}}</span></a>
						</script>
					</th>
					<th data-col-name="varsInformeCount" data-template="#cellVarsInformeTemplate" data-orderable="false" width="13%">
						<script id="cellVarsInformeTemplate" type="text/x-jsrender">
							<a href="${expedientTipus.id}/consulta/{{:id}}/varInforme" data-toggle="modal" data-callback="callbackModalConsultes()" class="btn btn-default"><spring:message code="expedient.tipus.consulta.llistat.accio.variables.informe"/>&nbsp;<span class="badge">{{:varsInformeCount}}</span></a>
						</script>
					</th>
					<th data-col-name="parametresCount" data-template="#cellParametresTemplate" data-orderable="false" width="13%">
						<script id="cellParametresTemplate" type="text/x-jsrender">
							<a href="${expedientTipus.id}/consulta/{{:id}}/parametre" data-toggle="modal" data-callback="callbackModalConsultes()" class="btn btn-default"><spring:message code="expedient.tipus.consulta.llistat.accio.parametres"/>&nbsp;<span class="badge">{{:parametresCount}}</span></a>
					</script>
					</th>
					<th data-col-name="ocultarActiu" data-template="#cellExpedientTipusConsultaActiuTemplate">
					<spring:message code="expedient.tipus.consulta.llistat.columna.actiu"/>
						<script id="cellExpedientTipusConsultaActiuTemplate" type="text/x-jsrender">
						{{if ocultarActiu }}
							<spring:message code="comu.false"></spring:message>
						{{else}}
							<spring:message code="comu.true"></spring:message>
						{{/if}}
						</script>
					</th>
					<th data-col-name="ordre"><spring:message code="expedient.tipus.consulta.llistat.columna.ordre"/></th>
					<th data-col-name="id" data-template="#cellAccionsConsultaTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsConsultaTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a data-toggle="modal" data-callback="callbackModalConsultes()" href="${expedientTipus.id}/consulta/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
								<li><a href="${expedientTipus.id}/consulta/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.consulta.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
							</ul>
						</div>
					</script>
					</th>
				</tr>
			</thead>
		</table>
		<script id="tableButtonsConsultaTemplate" type="text/x-jsrender">
			<div class="botons-titol text-right">
				<a id="nou_camp" class="btn btn-default" href="${expedientTipus.id}/consulta/new" data-toggle="modal" data-callback="callbackModalConsultes()" data-datatable-id="expedientTipusConsulta"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.consulta.llistat.accio.nova"/></a>
			</div>
		</script>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
// <![CDATA[            
$(document).ready(function() {
	// Quan es repinta la taula aplica la reordenació
	$('#expedientTipusConsulta').on('draw.dt', function() {
		// Posa la taula com a ordenable
		$(this).tableDnD({
	    	onDragClass: "drag",
	    	onDrop: function(table, row) {	        	
	        	var pos = row.rowIndex - 1;
	        	var id= obtenirId(pos);
	        	canviarPosicioConsulta(id,pos);
	    	},
	    	onDragStart: function(table, row) {
	    			filaMovem = row.rowIndex-1;
			}
	    });
	    $("tr", this).hover(function() {
	        $(this.cells[0]).addClass('showDragHandle');
	    }, function() {
	        $(this.cells[0]).removeClass('showDragHandle');
	    });	
	  });
});

function callbackModalConsultes() {
	webutilRefreshMissatges();
	refrescaTaula();
}

function refrescaTaula() {
	$('#expedientTipusRedireccio').webutilDatatable('refresh');
}

function canviarPosicioConsulta( id, pos) {
  	// Canvia la ordenació sempre amb ordre ascendent
	$('#expedientTipusConsulta').DataTable().order([8, 'asc']);
	var getUrl = '<c:url value="/v3/expedientTipus/${expedientTipusId}/consulta/"/>'+id+'/moure/'+pos;
	$.ajax({
		type: 'GET',
		url: getUrl,
		async: true,
		success: function(result) {
			$('#expedientTipusConsulta').webutilDatatable('refresh');
		},
		error: function(e) {
			console.log("Error canviant l'ordre: " + e);
			$('#expedientTipusConsulta').webutilDatatable('refresh');
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
	id = $("#expedientTipusConsulta tr:eq("+fila+")").attr("id");	
	id2 = id.split("_");
	return id2[1] ;
}

// ]]>
</script>			
