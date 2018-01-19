<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="ambInfoPropiaText"><spring:message code="expedient.tipus.form.camp.ambInfoPropia"/></c:set>

<script src="<c:url value="/js/webutil.common.js"/>"></script>
<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
<script src="<c:url value="/js/webutil.modal.js"/>"></script>

<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>

<c:choose>
	<c:when test="${not empty expedientTipus || not empty definicioProcesId}">
		
		<c:if test="${not empty expedientTipus && empty definicioProcesId && !expedientTipus.ambInfoPropia}">
			<div class="alert alert-warning">
				<span class="fa fa-exclamation-triangle"></span>
				<spring:message code="expedient.tipus.ambInfoPropia.avis" arguments="${ambInfoPropiaText}"></spring:message>
			</div>
		</c:if>
		<c:if test="${not empty definicioProces && not empty definicioProces.expedientTipus && definicioProces.expedientTipus.ambInfoPropia}">
			<div class="alert alert-warning">
				<span class="fa fa-exclamation-triangle"></span>
				<spring:message code="definicio.proces.ambInfoPropia.avis" arguments="${ambInfoPropiaText}"></spring:message>
			</div>
		</c:if>
		
		<form class="well">
			<div class="row">
				<div class="col-sm-10">
					<hel:inputSelect required="false" emptyOption="false" name="agrupacions" textKey="expedient.tipus.camp.llistat.agrupacio.seleccionada" optionItems="${agrupacions}" optionValueAttribute="codi" optionTextAttribute="valor"/>
				</div>
				<div class="col-sm-2 text-right">
					<div id="agrupacionsAccions" class="dropdown" style="margin-right: -10px;">
						<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
						<ul class="dropdown-menu">
							<li><a href="${baseUrl}/agrupacio/new" data-toggle="modal" data-callback="callbackModalAgrupacions()"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.camp.llistat.agrupacio.boto.crear"/></a></li>
							<li style="display: none;"><a id="agrupacioUpdate" href="${baseUrl}/agrupacio/update" data-toggle="modal" data-callback="callbackModalAgrupacions()"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.camp.llistat.agrupacio.boto.modificar"/></a></li>
							<li style="display: none;"><a id="agrupacioDelete" href="${baseUrl}/agrupacio/delete" data-confirm="<spring:message code="expedient.tipus.camp.llistat.agrupacio.boto.esborrar.confirm"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.tipus.camp.llistat.agrupacio.boto.esborrar"/></a></li>
							<li class="divider"></li>
							<li><a href="${baseUrl}/agrupacio" data-toggle="modal" data-callback="refrescarAgrupacions()"><span class="fa fa-arrows-v"></span>&nbsp;<spring:message code="expedient.tipus.camp.llistat.agrupacio.boto.ordenar"/></a></li>
						</ul>
					</div>
				</div>
			</div>
		</form>

		<table	id="expedientTipusVariable"
				data-rowId="id"
				data-toggle="datatable"
				data-url="${baseUrl}/variable/datatable"
				data-paging-enabled="true"
				data-info-type="search+button"
				data-ordering="true"
				data-default-order="3"
				data-length-menu='[[10,25,50,-1],["10","25","50","<spring:message code="comu.totes"/>"]]'
				data-rowhref-toggle="modal"
				data-rowhref-template="#rowhrefTemplateVariables" 
				data-botons-template="#tableButtonsVariableTemplate"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="agrupacio" data-visible="false"/>
					<th data-col-name="ordre" width="5%"><spring:message code="expedient.tipus.camp.llistat.columna.ordre"/></th>
					<th data-col-name="codi" width="20%" data-template="#cellExpedientTipusVariableCodiTemplate">
					<spring:message code="expedient.tipus.camp.llistat.columna.codi"/>
						<script id="cellExpedientTipusVariableCodiTemplate" type="text/x-jsrender">
								{{if heretat }}
									<span class="dada-heretada">{{:codi}}</span> 
									<span class="label label-primary" title="<spring:message code="expedient.tipus.camp.llistat.codi.heretat"/>">R</span>
								{{else}}
									{{:codi}}
									{{if sobreescriu }}
										<span class="label label-warning" title="<spring:message code="expedient.tipus.camp.llistat.codi.sobreescriu"/>">S</span>
									{{/if}}
								{{/if}}
						</script>
					</th>
					<th data-col-name="etiqueta"><spring:message code="expedient.tipus.camp.llistat.columna.etiqueta"/></th>
					<th data-col-name="tipus"><spring:message code="expedient.tipus.camp.llistat.columna.tipus"/></th>
					<th data-col-name="multiple" data-template="#cellexpedientTipusVariableMultibleTemplate">
					<spring:message code="expedient.tipus.camp.llistat.columna.multiple"/>
						<script id="cellexpedientTipusVariableMultibleTemplate" type="text/x-jsrender">
						{{if multiple }}
							<spring:message code="comu.check"></spring:message>
						{{/if}}
						</script>
					</th>
					<th data-col-name="validacioCount" data-template="#cellValidacionsTemplate" data-orderable="false" width="13%">
						<script id="cellValidacionsTemplate" type="text/x-jsrender">
						<a href="${baseUrl}/variable/{{:id}}/validacio" data-toggle="modal" data-callback="callbackModalVariables()" class="btn btn-default"><spring:message code="expedient.tipus.camp.llistat.accio.validacions"/>&nbsp;<span class="badge">{{:validacioCount}}</span></a>
					</script>
					</th>
					<th data-col-name="campRegistreCount" data-template="#cellMembresTemplate" data-orderable="false" width="13%">
						<script id="cellMembresTemplate" type="text/x-jsrender">
						{{if tipus == "REGISTRE" }}
							<a href="${baseUrl}/variable/{{:id}}/campRegistre" data-toggle="modal" data-callback="callbackModalVariables()" class="btn btn-default"><spring:message code="expedient.tipus.camp.llistat.accio.campsRegistre"/>&nbsp;<span class="badge">{{:campRegistreCount}}</span></a>
						{{/if}}
					</script>
					</th>
					<th data-col-name="id" data-template="#cellAccionsVariableTemplate" data-orderable="false" width="10%">
						<script id="cellAccionsVariableTemplate" type="text/x-jsrender">
						<div class="dropdown">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								{{if heretat}}
									<li><a data-toggle="modal" href="${baseUrl}/variable/{{:id}}/update"><span class="fa fa-search"></span>&nbsp;<spring:message code="comu.boto.visualitzar"/></a></li>
								{{else}}
									<li><a data-toggle="modal" data-callback="callbackModalVariables()" href="${baseUrl}/variable/{{:id}}/update"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
									<li><a href="${baseUrl}/variable/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.camp.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
									<li class="divider"></li>
									<li id="accioAgrupacions">
										{{if agrupacio == null}}
											<span class="fa fa-plus" style="margin-left:10px"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.agrupar"/>
											<br/>						
										{{else}}
											<a href="${baseUrl}/variable/{{:id}}/desagrupar"
												data-toggle="ajax" ><span class="fa fa-minus"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.desagrupar"/>
										{{/if}}																		
									</li>
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

		<script id="rowhrefTemplateVariables" type="text/x-jsrender">${baseUrl}/variable/{{:id}}/update</script>	

		<script id="tableButtonsVariableTemplate" type="text/x-jsrender">
			<div class="botons-titol text-right">
				<a id="nou_camp" class="btn btn-default" href="${baseUrl}/variable/new" data-toggle="modal" data-callback="callbackModalVariables()" data-datatable-id="expedientTipusVariable"><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.camp.llistat.accio.nova"/></a>
			</div>
		</script>
	</c:when>
	<c:otherwise>
		<div class="well well-small"><spring:message code='expedient.dada.expedient.cap'/></div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
// <![CDATA[            

// Llistat d'identificadors d'agrupacions heretades
var agrupacionsHeretadesIds =  ${agrupacionsHeretadesIds};
//Llistat d'identificadors d'agrupacions que sobreescriuen
var agrupacionsSobreescriuenIds =  ${agrupacionsSobreescriuenIds};

// Funció per donar format als itemps de la select d'agrupacions depenent de la herència
function formatAgrupacioSelectHerencia(item) {
	var res;
    if(item.id && agrupacionsHeretadesIds.indexOf(parseInt(item.id)) >= 0)
		res = item.text + " <span class='label label-primary'>R</span>";
	else if(item.id && agrupacionsSobreescriuenIds.indexOf(parseInt(item.id)) >= 0)
		res = item.text + " <span class='label label-warning'>S</span>";
	else 
		res = item.text;
    return res;
  }
   
$(document).ready(function() {

	// Posa el text "Totes" pel botó de selecció de totes les variables
	$('#expedientTipusVariable_length button[value="-1"]').text('<spring:message code="comu.totes"/>');

	// Botons de modificar i eliminar agrupacions
	$('#agrupacioDelete').click(function(e) {
		var getUrl = $(this).attr('href');
		$.ajax({
			type: 'GET',
			url: getUrl,
			async: true,
			success: function(result) {
				if (result) {
					refrescarAgrupacions();
				}
			},
			error: function(e) {
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
		if (agrupacioId >= 0 ) {
			$('#nou_camp').attr('href', '${baseUrl}/variable/new?agrupacioId=' + agrupacioId);
			if (agrupacionsHeretadesIds.indexOf(parseInt(agrupacioId)) < 0) {
				$('#agrupacioUpdate').attr('href', '${baseUrl}/agrupacio/' + agrupacioId + '/update');
				$('#agrupacioDelete').attr('href', '${baseUrl}/agrupacio/' + agrupacioId + '/delete');
				$('#agrupacioUpdate,#agrupacioDelete').closest('li').show();
			} else {
				$('#agrupacioUpdate,#agrupacioDelete').closest('li').hide();
			}
			// Mostra la columna d'ordre
			$('#expedientTipusVariable').DataTable().order([2, 'asc']);
			$('#expedientTipusVariable').DataTable().column(2).visible(true);
		} else {
			$('#nou_camp').attr('href', '${baseUrl}/variable/new');
			$('#agrupacioUpdate,#agrupacioDelete').closest('li').hide();
			// Amaga la columna d'ordre
			$('#expedientTipusVariable').DataTable().order([3, 'asc']);
			$('#expedientTipusVariable').DataTable().column(2).visible(false);
		}
		refrescaTaula();
	});
	// Afegeix format si l'item de la agrupació està heretat
	$('#agrupacions').select2({
        formatResult: formatAgrupacioSelectHerencia,
        formatSelection: formatAgrupacioSelectHerencia
    });
	
	// Amaga la columna d'ordre
	$('#expedientTipusVariable').DataTable().column(2).visible(false);
	
	// Quan es repinta la taula actualitza els enllaços
	$('#expedientTipusVariable').on('draw.dt', function() {
		// Afegeix les opcions per a agrupar de les agrupacions existents
		if ($("#agrupacions").val() <  0) {
			$("tr", this).each(function(){
				if ($(this).find("#accioAgrupacions").length > 0) {	
					var campId = $(this).attr('id').replace('row_','');
					$agrupacions = $(this).find("#accioAgrupacions");
					$("#agrupacions option").each(function(){
						if ($(this).val() > 0 
								&& (agrupacionsHeretadesIds.indexOf(parseInt($(this).val())) < 0))
							$agrupacions.append("<a href='${baseUrl}/variable/"+campId+"/agrupar/"+$(this).val()+"' data-toggle='ajax'>"+$(this).text()+"</a>");
					});
				}
			});
			$('[data-toggle="ajax"]', $(this)).each(function() {
				if (!$(this).attr('data-ajax-eval')) {
					$(this).webutilAjax();
					$(this).attr('data-ajax-eval', 'true');
				}
			});			
		}

		var agrupacioId = $("#agrupacions").val();
		if (agrupacioId >= 0 
				&& agrupacionsHeretadesIds.indexOf(parseInt(agrupacioId)) < 0) {
			// Posa la taula com a ordenable
			$("#expedientTipusVariable").tableDnD({
		    	onDragClass: "drag",
		    	onDrop: function(table, row) {	      
		        	var pos = row.rowIndex - 1;
		        	var id= obtenirId(pos);
		        	if (pos != filaMovem) {
		        		canviarPosicioVariable(id,pos);
		    			$('tr').off('click');
		    			$('td').off('click');
		        	}
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
		// Refresca els missatges
		webutilRefreshMissatges();
	  });		
});

function canviarPosicioVariable( id, pos) {
	var getUrl = '${baseUrl}/variable/'+id+'/moure/'+pos;
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
	$('#expedientTipusVariable').webutilDatatable('refresh-url', '${baseUrl}/variable/datatable?agrupacioId='+agrupacioId);		
}

function refrescarAgrupacions() {
	$("#agrupacionsAccions").removeClass('open');
	var getUrl = '${baseUrl}/agrupacio/select';
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
	refrescaTaula();
}

function callbackModalAgrupacions() {
	refrescarAgrupacions();
}

// ]]>
</script>			
