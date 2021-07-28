<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>


<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="expedient.tipus.consulta.vars.llistat.titol" arguments="${tipus},${consulta.nom}"/></c:set>
<c:set var="baseUrl"><c:url value="/modal/v3/expedientTipus/${expedientTipusId}/consulta/${consulta.id}/var"></c:url></c:set>

<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.19/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.19/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.19/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
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
	
	<form:form id="consulta-var-form" cssClass="form-horizontal" action="${baseUrl}/new" enctype="multipart/form-data" method="post" modelAttribute="expedientTipusConsultaVarCommand">
				<input type="hidden" name="expedientTipusId" id="inputExpedientTipusId" value="${expedientTipusConsultaVarCommand.expedientTipusId}"/>
				<input type="hidden" name="consultaId" id="inputConsultaId" value="${expedientTipusConsultaVarCommand.consultaId}"/>
				<input type="hidden" name="tipus" id="inputTipus" value="${expedientTipusConsultaVarCommand.tipus}"/>
		<div class="well well-sm"  >
			<div class="row">
				<div class="col-sm-5">
					<div class="form-group">
						<label class="control-label col-xs-3 obligatori" for="origen">Origen</label>
						<div class="controls col-xs-9">
							<select title="Variable" id="origen" name="origen" class="form-control select2-offscreen">
								<option value="-2" ${expedientTipusConsultaVarCommand.origen == -2? "selected='selected'" : ""}><spring:message code="expedient.tipus.consulta.vars.origen.expedient"/></option>
								<option value="-1" ${expedientTipusConsultaVarCommand.origen == -1? "selected='selected'" : ""}><spring:message code="expedient.tipus.consulta.vars.origen.tipus.expedient"/></option>
								<optgroup label="<spring:message code='expedient.tipus.consulta.vars.origen.definicions.proces'/>">
									<c:forEach items="${definicionsProces}" var="definicioProces">
										<option value="${definicioProces.id}" ${expedientTipusConsultaVarCommand.origen == definicioProces.id? "selected='selected'" : ""} >${definicioProces.jbpmKey} v.${definicioProces.versio}</option>
									</c:forEach>									
								</optgroup>
							</select>
						</div>
					</div>
				</div>
				<div class="col-sm-7">
					<hel:inputSelect required="true" name="campCodi" multiple="true" textKey="expedient.tipus.consulta.vars.form.variable" placeholderKey="expedient.tipus.consulta.vars.form.variable.placeholder" optionItems="${variables}" optionValueAttribute="codi" optionTextAttribute="valor" labelSize="2"/>
				</div>
			</div>
			<div id="modal-botons" style="text-align:right">
				<button id="btnCreate" class="btn btn-primary right" type="submit" name="accio" value="crear">
					<span class="fa fa-plus"></span> <spring:message code='expedient.tipus.consulta.vars.llistat.accio.afegir' />
				</button>
			</div>
		</div>
	</form:form>
	
	<form id="consultaVarFiltre">
		<input type=hidden name="tipus" value="${tipus}"/>
	</form>
	<div class="row">
		<div class="col-sm-12">
			<a class="btn btn-primary right" style="float: right;" title="<spring:message code='consulta.camps.mostrarxml' />" alt="<spring:message code='consulta.camps.mostrarxml' />" href="<c:url value="/v3/expedientTipus/${expedientTipusId}/consulta/reportDownload"><c:param name="consultaId" value="${consulta.id}"/></c:url>"><img src="<c:url value="/img/page_white_code.png"/>" border="0"/></a>
		</div>
	</div>
	<div style="height: 500px;">
		<table	id="consultaVar"
				data-toggle="datatable"
				data-url="${baseUrl}/datatable"
				data-paging-enabled="false"
				data-ordering="true"
				data-default-order="5"
				data-filtre="#consultaVarFiltre"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="codiEtiqueta" data-orderable="false"><spring:message code="expedient.tipus.consulta.vars.llistat.columna.variable"/></th>
					<th data-col-name="campTipus" data-orderable="false"><spring:message code="expedient.tipus.consulta.vars.llistat.columna.tipus"/></th>
					<th data-col-name="defprocVersio" data-visible="false"/>
					<th data-col-name="defprocJbpmKey" data-orderable="true" data-template="#cellConsultaVarCodiEtiquetaTemplate">
						<spring:message code="expedient.tipus.consulta.vars.llistat.columna.defincioProces"/>
						<script id="cellConsultaVarCodiEtiquetaTemplate" type="text/x-jsrender">
							{{if defprocVersio >= 0}}
								{{:defprocJbpmKey}} v.{{:defprocVersio}}
							{{/if}}
						</script>
					</th>
					<th data-col-name="ordre"><spring:message code="expedient.tipus.consulta.vars.llistat.columna.ordre"/></th>
					<th data-col-name="ampleCols" data-template="#cellConsultaVarAmpleColsTemplate">
						<spring:message code="expedient.tipus.consulta.vars.llistat.columna.ampleCols"/>
						<script id="cellConsultaVarAmpleColsTemplate" type="text/x-jsrender">
							<div class="form-group">
            					<input id="ampleCols_{{:id}}" data-variableid="{{:id}}" data-propietat="ampleCols" type="number" class="form-control" name="name" value="{{:ampleCols}}" style="width:100%;"/>
							</div>
						</script>
					</th>
					<th data-col-name="buitCols" data-template="#cellConsultaVarBuitColsTemplate">
						<spring:message code="expedient.tipus.consulta.vars.llistat.columna.buitCols"/>
						<script id="cellConsultaVarBuitColsTemplate" type="text/x-jsrender">
							<div class="form-group">
            					<input id="buitCols_{{:id}}" data-variableid="{{:id}}" data-propietat="buitCols" type="number" class="form-control" name="name" value="{{:buitCols}}" style="width:100%;"/>
							</div>
						</script>
					</th>
					<th data-col-name="id" width="100px" data-template="#cellConsultaVarTemplate" data-orderable="false" width="10%">
						<script id="cellConsultaVarTemplate" type="text/x-jsrender">
							<a href="${baseUrl}/{{:id}}/delete" class="btn btn-default ajax-delete" data-confirm="<spring:message code="expedient.tipus.consulta.vars.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a>
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
	            
	$(document).ready(function() {
		
		// Quan es repinta la taula aplica la reordenació
		$('#consultaVar').on('draw.dt', function() {
			$('#origen').select2();
			// Posa la taula com a ordenable
			$("#consultaVar").tableDnD({
		    	onDragClass: "drag",
		    	onDrop: function(table, row) {	        	
		        	var pos = row.rowIndex - 1;
		        	var id= obtenirId(pos);
		        	if (pos != filaMovem)
		        		canviarPosicioConsultaVar(id,pos);
		    	},
		    	onDragStart: function(table, row) {
		    			filaMovem = row.rowIndex-1;
				}
		    });
		    $("#consultaVar tr").hover(function() {
		        $(this.cells[0]).addClass('showDragHandle');
		    }, function() {
		        $(this.cells[0]).removeClass('showDragHandle');
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
						webutilRefreshMissatges();
					},
					error: function(error) {
						webutilRefreshMissatges();
						console.log('Error:'+error);
					}
				});
				e.stopImmediatePropagation();
				return false;				
			});	
			// Si es modifica un camp numèric, s'actualitza el registre
		    $("input[type=number]", this).change(function() {
		    	updateNumeric(this);
		    });
		  });		
		// Quan es canvia la selecció de l'origen s'actualitzen les variables
		$('#origen').change(function() {
			$('#campCodi').select2('data', null);
			refrescaVariables();
		});
	});

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
		var getUrl = '<c:url value="/v3/expedientTipus/${expedientTipusId}/consulta/${consulta.id}/var/"/>'+variableId+'/'+propietat;
		var spin = $("#accioUpdateProcessant")
			.clone()
			.show()
			.insertAfter(input);
		
		var valor = $(input).val();
		if (propietat == 'ampleCols') {
			if (valor == undefined || valor == '' || valor > 12)
				valor = 12;
			else if (valor <= 0) 
				valor = 0;
			
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
	
	function canviarPosicioConsultaVar( id, pos) {
	  	// Canvia la ordenació sempre amb ordre ascendent
		$('#consultaVar').DataTable().order([5, 'asc']);
		var getUrl = '<c:url value="/v3/expedientTipus/${expedientTipusId}/consulta/${consulta.id}/var/"/>'+id+'/moure/'+pos;
		$.ajax({
			type: 'GET',
			url: getUrl,
			async: true,
			success: function(result) {
				$('#consultaVar').webutilDatatable('refresh');
			},
			error: function(e) {
				console.log("Error canviant l'ordre: " + e);
				$('#consultaVar').webutilDatatable('refresh');
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
		id = $("#consultaVar tr:eq("+fila+")").attr("id");	
		id2 = id.split("_");
		return id2[1] ;
	}
		
	function refrescaVariables() {
		var getUrl = '<c:url value="/v3/expedientTipus/${expedientTipusId}/consulta/${consulta.id}/var/select"/>';
		$.ajax({
			type: 'GET',
			url: getUrl,
			dataType: "json",
			data: { 
					tipus: '${tipus}' , 
					origen: $('#origen').val() },
			async: true,
			success: function(data) {
				$("#campCodi option").each(function(){
				    $(this).remove();
				});
				for (i = 0; i < data.length; i++) {
					$("#campCodi").append($("<option/>", {value: data[i].codi, text: data[i].valor}));
				}
				$("#campCodi").val('').change();
			},
			error: function(e) {
				console.log("Error obtenint variables: " + e);
			}
		});
	}	
	function refrescaTaula() {
		$('#consultaVar').webutilDatatable('refresh');
	}	
	// ]]>
	</script>	
</body>
		
