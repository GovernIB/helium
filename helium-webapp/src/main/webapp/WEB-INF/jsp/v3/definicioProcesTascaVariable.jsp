<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>


<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="definicio.proces.tasca.variable.titol" arguments="${tasca.nom}"/></c:set>
<c:set var="baseUrl"><c:url value="/modal/v3/definicioProces/${jbpmKey}/${definicioProcesId}/tasca/${tasca.id}/variable"></c:url></c:set>

<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>

	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>

	<hel:modalHead/>
</head>
<body>			
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
	</div>
	
	<form:form id="tasca-camp-form" cssClass="well" action="${baseUrl}/new" enctype="multipart/form-data" method="post" commandName="definicioProcesTascaVariableCommand">
		<input type="hidden" name="tascaId" id="inputTascaId" value="${definicioProcesTascaVariableCommand.tascaId}"/>
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
			<div class="col-sm-2 right" id="modal-botons">
				<button id="btnCreate" class="btn btn-primary right" type="submit" name="accio" value="crear">
					<span class="fa fa-plus"></span> <spring:message code='definicio.proces.tasca.variable.accio.afegir' />
				</button>
			</div>
		</div>
	</form:form>
		
	<div style="height: 500px;">
		<table	id="tascaVariable"
				data-toggle="datatable"
				data-url="${baseUrl}/datatable"
				data-paging-enabled="false"
				data-ordering="true"
				data-info-type="search"
				data-default-order="6"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="camp.codi" data-template="#celldefinicioProcesTascaVariable" width="30%">
						<spring:message code="definicio.proces.tasca.variable.columna.variable"/>
						<script id="celldefinicioProcesTascaVariable" type="text/x-jsrender">
							{{:camp.codi}} / {{:camp.etiqueta}}
							{{if camp.heretat }}
								<span class="label label-primary" title="<spring:message code="expedient.tipus.camp.llistat.codi.heretat"/>">R</span>
							{{/if}}
							{{if camp.sobreescriu }}
								<span class="label label-warning" title="<spring:message code="expedient.tipus.camp.llistat.codi.sobreescriu"/>">S</span>
							{{/if}}
							{{if camp.expedientTipus != null}}
								<span class="label label-info pull-right" title="Tipus Expedient">TE</span>
							{{else}}
								<span class="label label-warning pull-right" title="Definició de Procés">DP</span>
							{{/if}}							
						</script>
					</th>
					<th data-col-name="readFrom" data-template="#celldefinicioProcesTascaVariableReadFromTemplate">
					<spring:message code="definicio.proces.tasca.variable.columna.readFrom"/>
						<script id="celldefinicioProcesTascaVariableReadFromTemplate" type="text/x-jsrender">
						<div style="text-align: center;">
						<input type="checkbox" id="readFrom_{{:id}}" {{:readFrom ? 'checked' : ''}} data-variableid="{{:id}}" data-propietat="readFrom" />
						</div>
						</script>
					</th>
					<th data-col-name="writeTo" data-template="#celldefinicioProcesTascaVariableWriteToTemplate">
					<spring:message code="definicio.proces.tasca.variable.columna.writeTo"/>
						<script id="celldefinicioProcesTascaVariableWriteToTemplate" type="text/x-jsrender">
						<div style="text-align: center;">
						<input type="checkbox" id="writeTo_{{:id}}" {{:writeTo ? 'checked' : ''}} data-variableid="{{:id}}" data-propietat="writeTo"/>
						</div>
						</script>
					</th>
					<th data-col-name="required" data-template="#celldefinicioProcesTascaVariableRequiredTemplate">
					<spring:message code="definicio.proces.tasca.variable.columna.required"/>
						<script id="celldefinicioProcesTascaVariableRequiredTemplate" type="text/x-jsrender">
						<div style="text-align: center;">
						<input type="checkbox" id="required_{{:id}}" {{:required ? 'checked' : ''}} data-variableid="{{:id}}" data-propietat="required"/>
						</div>
						</script>
					</th>
					<th data-col-name="readOnly" data-template="#celldefinicioProcesTascaVariableReadOnlyTemplate">
					<spring:message code="definicio.proces.tasca.variable.columna.readOnly"/>
						<script id="celldefinicioProcesTascaVariableReadOnlyTemplate" type="text/x-jsrender">
						<div style="text-align: center;">
						<input type="checkbox"  id="readOnly_{{:id}}"{{:readOnly ? 'checked' : ''}} data-variableid="{{:id}}" data-propietat="readOnly"/>
						</div>
						</script>
					</th>
					
					<th data-col-name="order" data-visible="false"><spring:message code="definicio.proces.tasca.variable.columna.ordre"/></th>
					
					<th data-col-name="ampleCols" data-template="#celldefinicioProcesTascaVariableAmpleColsTemplate">
						<spring:message code="definicio.proces.tasca.variable.columna.ampleCols"/>
						<script id="celldefinicioProcesTascaVariableAmpleColsTemplate" type="text/x-jsrender">
							<div class="form-group">
            					<input id="ampleCols_{{:id}}" data-variableid="{{:id}}" data-propietat="ampleCols" type="number" class="form-control" name="name" value="{{:ampleCols}}" style="width:100%;"/>
							</div>
						</script>
					</th>
					<th data-col-name="buitCols" data-template="#celldefinicioProcesTascaVariableBuitColsTemplate">
						<spring:message code="definicio.proces.tasca.variable.columna.buitCols"/>
						<script id="celldefinicioProcesTascaVariableBuitColsTemplate" type="text/x-jsrender">
							<div class="form-group">
            					<input id="buitCols_{{:id}}" data-variableid="{{:id}}" data-propietat="buitCols" type="number" class="form-control" name="name" value="{{:buitCols}}" style="width:100%;"/>
							</div>
						</script>
					</th>
					<th data-col-name="id" width="100px" data-template="#cellTascaVariableDeleteTemplate" data-orderable="false" width="10%">
						<script id="cellTascaVariableDeleteTemplate" type="text/x-jsrender">
							<a href="${baseUrl}/{{:id}}/delete" class="btn btn-default ajax-delete" data-confirm="<spring:message code="definicio.proces.tasca.variable.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar"/></a>
						</script>
					</th>
				</tr>
			</thead>
		</table>
		<span id="accioUpdateProcessant" style="display: none;">
			<span class="fa fa-spinner fa-spin fa-fw" title="<spring:message code="comu.processant"/>"></span><span class="sr-only">&hellip;</span>
		</span>
	</div>
	
	<script type="text/javascript">
	// <![CDATA[

	// Llistat d'identificadors de camps heretats
	var campsHeretatsIds =  ${campsHeretatsIds};
	//Llistat d'identificadors de camps que sobreescriuen
	var campsSobreescriuenIds =  ${campsSobreescriuenIds};

	// Funció per donar format als itemps de la select d'agrupacions depenent de la herència
	function formatCampSelectHerencia(item) {
		var res;
	    if(item.id && campsHeretatsIds.indexOf(parseInt(item.id)) >= 0)
			res = item.text + " <span class='label label-primary'>R</span>";
		else if(item.id && campsSobreescriuenIds.indexOf(parseInt(item.id)) >= 0)
			res = item.text + " <span class='label label-warning'>S</span>";
		else 
			res = item.text;
	    return res;
	  }
	
	$(document).ready(function() {
		

		// Afegeix format si l'item de la agrupació està heretat
		$('#campId').select2({
	        formatResult: formatCampSelectHerencia,
	        formatSelection: formatCampSelectHerencia
	    });

		// Quan es repinta la taula aplica la reordenació
		$('#tascaVariable').on('draw.dt', function() {
			// Posa la taula com a ordenable
			$("#tascaVariable").tableDnD({
		    	onDragClass: "drag",
		    	onDrop: function(table, row) {	        	
		        	var pos = row.rowIndex - 1;
		        	var id= obtenirId(pos);
		        	if (pos != filaMovem)
		        		canviarPosicioTascaVariable(id,pos);
		    	},
		    	onDragStart: function(table, row) {
		    			filaMovem = row.rowIndex-1;
				}
		    });			
		    $("#tascaVariable tr").hover(function() {
		        $(this.cells[0]).addClass('showDragHandle');
		    }, function() {
		        $(this.cells[0]).removeClass('showDragHandle');
		    });	
		    // Si es modifica un checbox, s'actualitza el registre
		    $("input[type=checkbox]", this).change(function() {
		    	updateCheckbox(this);
		    });
		 	// Si es modifica un camp numèric, s'actualitza el registre
		    $("input[type=number]", this).change(function() {
		    	updateNumeric(this);
		    });
		    // botons d'esborrar
			$(".ajax-delete", this).click(function(e) {
				var getUrl = $(this).attr('href');
				$.ajax({
					type: 'GET',
					url: getUrl,
					async: true,
					success: function(result) {
						if (result) {
							refrescaVariables();
							refrescaTaula();
						}
					},
					error: function(error) {
						console.log('Error:'+error);
					},
					complete: function() {
						webutilRefreshMissatges();
					}
				});
				e.stopImmediatePropagation();
				return false;				
			});
		  });			
	});
	
	/* Actualitza un valor del camp de la tasca. */
	function updateCheckbox(checkbox) {
		var variableId = $(checkbox).data('variableid');
		var propietat = $(checkbox).data('propietat');
		var getUrl = '<c:url value="/v3/definicioProces/${jbpmKey}/${definicioProcesId}/tasca/${tasca.id}/variable/"/>'+variableId+'/'+propietat;
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
				$(spin).remove();
				$(checkbox).show();
				
			}
		});
	}
	/* Defineix l'ample de columnes i el buit per tal que no es passin de mida */
	function definirAmpleBuit(ample, buit) {
		ample = parseInt(ample);
		buit = parseInt(buit);
		var absAmple = Math.abs(ample);
		var absBuit = Math.abs(buit);
		var totalCols = absAmple + absBuit;
		if (totalCols > 12) {
			var diff = totalCols - 12;
			if (buit >= 0)
				buit -= diff;
			else
				buit += diff;
		}
		return buit;
	}
	
	/* Actualitza un valor numèric del camp de la tasca. */
	function updateNumeric(input) {
		var variableId = $(input).data('variableid');
		var propietat = $(input).data('propietat');
		var getUrl = '<c:url value="/v3/definicioProces/${jbpmKey}/${definicioProcesId}/tasca/${tasca.id}/variable/"/>'+variableId+'/'+propietat;
		var spin = $("#accioUpdateProcessant")
			.clone()
			.show()
			.insertAfter(input);
		
		var valor = $(input).val();
		if (propietat == 'ampleCols') {
			if (valor == undefined || valor == '' || valor > 12)
				valor = 12;
			else if (valor <= 0) 
				valor = 1;
			
			$("#buitCols_" + variableId).val(definirAmpleBuit(valor, $("#buitCols_" + variableId).val()));
		} else {
			if (valor == undefined || valor == '')
				valor = 0;
			else if (valor > 12) 
				valor = 12;
			else if (valor < -12)
				valor = -12;
			
			valor = definirAmpleBuit($("#ampleCols_" + variableId).val(), valor);
			
		}
		
		$(input).hide();
		$.ajax({
			type: 'POST',
			url: getUrl,
			data: {
				valor : valor
			},
			async: true,
			success: function(result) {
				$(input).val(valor);
			},
			error: function(error) {
				console.log('Error:'+error);
			},
			complete: function() {
				webutilRefreshMissatges();
				$(spin).remove();
				$(input).show();
			}
		});
	}

	
	function canviarPosicioTascaVariable( id, pos) {
	  	// Canvia la ordenació sempre amb ordre ascendent
		$('#tascaVariable').DataTable().order([6, 'asc']);
		var getUrl = '<c:url value="/v3/definicioProces/${jbpmKey}/${definicioProcesId}/tasca/${tasca.id}/variable/"/>'+id+'/moure/'+pos;
		$.ajax({
			type: 'GET',
			url: getUrl,
			async: true,
			success: function(result) {
				$('#tascaVariable').webutilDatatable('refresh');
			},
			error: function(e) {
				console.log("Error canviant l'ordre: " + e);
				$('#tascaVariable').webutilDatatable('refresh');
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
		id = $("#tascaVariable tr:eq("+fila+")").attr("id");	
		id2 = id.split("_");
		return id2[1] ;
	}
		
	function refrescaVariables() {
		var getUrl = '<c:url value="/v3/definicioProces/${jbpmKey}/${definicioProcesId}/tasca/${tasca.id}/variable/select"/>';
		$.ajax({
			type: 'GET',
			url: getUrl,
			async: true,
			success: function(data) {
				$("#campId option").each(function(){
				    $(this).remove();
				});
				$("#campId").append($("<option/>"));
				for (i = 0; i < data.length; i++) {
					$("#campId").append($("<option/>", {value: data[i].codi, text: data[i].valor}));
				}
				$("#campId").val('').change();
			},
			error: function(e) {
				console.log("Error obtenint variables: " + e);
			}
		});
	}	
	function refrescaTaula() {
		$('#tascaVariable').webutilDatatable('refresh');
	}	
	// ]]>
	</script>	
</body>
		
