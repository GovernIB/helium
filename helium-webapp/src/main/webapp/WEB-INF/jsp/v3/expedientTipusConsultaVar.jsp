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
	
	<form:form id="consulta-var-form" cssClass="well" action="${baseUrl}/new" enctype="multipart/form-data" method="post" commandName="expedientTipusConsultaVarCommand">
				<input type="hidden" name="expedientTipusId" id="inputExpedientTipusId" value="${expedientTipusConsultaVarCommand.expedientTipusId}"/>
				<input type="hidden" name="consultaId" id="inputConsultaId" value="${expedientTipusConsultaVarCommand.consultaId}"/>
				<input type="hidden" name="tipus" id="inputTipus" value="${expedientTipusConsultaVarCommand.tipus}"/>
		<div class="row">
			<div class="col-sm-10">
				<hel:inputSelect required="true" emptyOption="true" name="campId" textKey="expedient.tipus.consulta.vars.form.variable" placeholderKey="expedient.tipus.consulta.vars.form.variable.placeholder" optionItems="${variables}" optionValueAttribute="codi" optionTextAttribute="valor"/>
			</div>
			<div class="col-sm-2 right" id="modal-botons">
				<button id="btnCreate" class="btn btn-primary right" type="submit" name="accio" value="crear">
					<span class="fa fa-plus"></span> <spring:message code='expedient.tipus.consulta.vars.llistat.accio.afegir' />
				</button>
			</div>
		</div>
	</form:form>
	
	<form id="consultaVarFiltre">
		<input type=hidden name="tipus" value="${tipus}"/>
	</form>
	
	<div style="height: 500px;">
		<table	id="consultaVar"
				data-toggle="datatable"
				data-url="${baseUrl}/datatable"
				data-paging-enabled="false"
				data-ordering="true"
				data-default-order="3"
				data-filtre="#consultaVarFiltre"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="campCodi"><spring:message code="expedient.tipus.consulta.vars.llistat.columna.variable"/></th>
					<th data-col-name="campTipus"><spring:message code="expedient.tipus.consulta.vars.llistat.columna.tipus"/></th>
					<th data-col-name="ordre"><spring:message code="expedient.tipus.consulta.vars.llistat.columna.ordre"/></th>
					<th data-col-name="id" width="100px" data-template="#cellConsultaVarTemplate" data-orderable="false" width="10%">
						<script id="cellConsultaVarTemplate" type="text/x-jsrender">
							<a href="${baseUrl}/{{:id}}/delete" class="btn btn-default ajax-delete" data-confirm="<spring:message code="expedient.tipus.consulta.vars.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a>
						</script>
					</th>
				</tr>
			</thead>
		</table>
	</div>
	
	<script type="text/javascript">
	// <![CDATA[
	            
	$(document).ready(function() {
		
		// Quan es repinta la taula aplica la reordenació
		$('#consultaVar').on('draw.dt', function() {
			// Posa la taula com a ordenable
			$("#consultaVar").tableDnD({
		    	onDragClass: "drag",
		    	onDrop: function(table, row) {	        	
		        	var pos = row.rowIndex - 1;
		        	var id= obtenirId(pos);
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
		  });			
	});

	
	function canviarPosicioConsultaVar( id, pos) {
	  	// Canvia la ordenació sempre amb ordre ascendent
		$('#consultaVar').DataTable().order([3, 'asc']);
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
		var getUrl = '<c:url value="/v3/expedientTipus/${expedientTipusId}/consulta/${consulta.id}/var/select?tipus=${tipus}"/>';
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
		$('#consultaVar').webutilDatatable('refresh');
	}	
	// ]]>
	</script>	
</body>
		
