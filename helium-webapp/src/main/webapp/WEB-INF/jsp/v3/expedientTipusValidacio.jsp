<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>


<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="expedient.tipus.campValidacio.llistat.titol" arguments="${camp.etiqueta}"/></c:set>
<c:set var="baseUrl"><c:url value="/modal/v3/${basicUrl}/variable/${camp.id}/validacio"></c:url></c:set>

<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
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
	
	<form:form id="validacio-form" cssClass="form-horizontal" action="#" enctype="multipart/form-data" method="post" commandName="validacioCommand" 
		style='${mostraCreate || mostraUpdate ? "":"display:none;"}'>
		<div class="inlineLabels">        
			<input type="hidden" name="id" id="inputValidacioId" value="${validacioCommand.id}"/>
			<hel:inputText required="true" name="expressio" textKey="expedient.tipus.campValidacio.form.camp.expressio" />
			<hel:inputTextarea required="true" name="missatge" textKey="expedient.tipus.campValidacio.form.camp.missatge" />
		</div>

		<div id="modal-botons" class="well">
			<button id="btnCancelar" name="submit" value="cancel" class="btn btn-default"><spring:message code="comu.boto.cancelar"/></button>
			<button id="btnCreate" style='${mostraCreate ? "":"display:none;"}' class="btn btn-primary right" type="submit" name="accio" value="crear">
				<span class="fa fa-plus"></span> <spring:message code='comu.boto.crear' />
			</button>
			<button id="btnUpdate" style='${mostraUpdate ? "":"display:none;"}' class="btn btn-primary right" type="submit" name="accio" value="modificar">
				<span class="fa fa-pencil"></span> <spring:message code='comu.boto.modificar' />
			</button>	
		</div>

	</form:form>
	
	<div class="botons-titol text-right">
		<c:if test="${!heretat}">
			<button id="btnNew" class="btn btn-default" style='${mostraCreate || mostraUpdate ? "display:none;" : ""}'><span class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.campValidacio.llistat.accio.crear"/></button>
		</c:if>
	</div>	
	<div style="height: 500px;">
		<table	id="campValidacio"
				data-toggle="datatable"
				data-url="${baseUrl}/datatable"
				data-paging-enabled="false"
				data-ordering="true"
				data-default-order="3"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="expressio"><spring:message code="expedient.tipus.campValidacio.llistat.columna.expressio"/></th>
					<th data-col-name="missatge"><spring:message code="expedient.tipus.campValidacio.llistat.columna.missatge"/></th>
					<th data-col-name="ordre"><spring:message code="expedient.tipus.campValidacio.llistat.columna.ordre"/></th>
					<th data-col-name="id" data-template="#cellValidacioTemplate" data-orderable="false" width="10%">
						<script id="cellValidacioTemplate" type="text/x-jsrender">
						<c:if test="${!heretat}">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li><a href="${baseUrl}/{{:id}}/update" class="validacioUpdate" data-validacioid="{{:id}}"><span class="fa fa-pencil"></span>&nbsp;<spring:message code="expedient.tipus.info.accio.modificar"/></a></li>
									<li><a href="${baseUrl}/{{:id}}/delete" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.campValidacio.llistat.confirmacio.esborrar"/>"><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="expedient.llistat.accio.esborrar"/></a></li>
								</ul>
							</div>
						</c:if>
					</script>
					</th>
				</tr>
			</thead>
		</table>
	</div>
	
	<script type="text/javascript">
	// <![CDATA[
	            
	$(document).ready(function() {
		// Accions del formulari
		$('#btnCancelar').click(function(e){
			e.preventDefault();
			cancelaFormulari();
		})
		$('#btnNew').click(function(){
			mostraFormulariNew();
		})
		
		// Quan es repinta la taula aplica la reordenació
		$('#campValidacio').on('draw.dt', function() {
			/* <c:if test="${!heretat}"> */
			// Posa la taula com a ordenable
			$("#campValidacio").tableDnD({
		    	onDragClass: "drag",
		    	onDrop: function(table, row) {	        	
		        	var pos = row.rowIndex - 1;
		        	var id= obtenirId(pos);
		        	if (pos != filaMovem)
		        		canviarPosicioValidacio(id,pos);
		    	},
		    	onDragStart: function(table, row) {
		    			filaMovem = row.rowIndex-1;
				}
		    });
			/* </c:if> */
		    // Modifica l'enllaç update per carregar adaptar la vista a l'update
		    $(".validacioUpdate").click(function(){
				var validacioId = $(this).data('validacioid');
				mostraFormulariUpdate(validacioId);
				return false;
		    });
		  });			
	});
	
	function cancelaFormulari() {
		$('#validacio-form').hide(300);
		$('#btnNew').show();
		$('#validacio-form').attr('action','');
	}
	
	function mostraFormulariNew() {
		$('#btnNew').hide();
		$('#btnCreate').show();
		$('#btnUpdate').hide();
		resetFormulari();
		$('#validacio-form').attr('action','${baseUrl}/new');
	}
	
	function mostraFormulariUpdate(id) {
		$('#btnNew').hide();
		$('#btnCreate').hide();
		$('#btnUpdate').show();
		resetFormulari();
		$('#validacio-form').attr('action','${baseUrl}/'+id+'/update');
		// Copia els valors
		$("#inputValidacioId").val(id);
		$("#expressio").val($("#campValidacio tr[id='row_"+id+"'] td:nth-child(1)").text());
		$("#missatge").val($("#campValidacio tr[id='row_"+id+"'] td:nth-child(2)").text());
	}
	
	function canviarPosicioValidacio( id, pos) {
	  	// Canvia la ordenació sempre amb ordre ascendent
		$('#campValidacio').DataTable().order([3, 'asc']);
		var getUrl = '<c:url value="/v3/${basicUrl}/variable/${camp.id}/validacio/"/>'+id+'/moure/'+pos;
		$.ajax({
			type: 'GET',
			url: getUrl,
			async: true,
			success: function(result) {
				$('#campValidacio').webutilDatatable('refresh');
			},
			error: function(e) {
				console.log("Error canviant l'ordre: " + e);
				$('#campValidacio').webutilDatatable('refresh');
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
		id = $("#campValidacio tr:eq("+fila+")").attr("id");	
		id2 = id.split("_");
		return id2[1] ;
	}
	
	function resetFormulari() {
		$('#validacio-form').trigger('reset').show(300);
		$('#validacio-form .help-block').remove();
		$('#validacio-form .has-error').removeClass('has-error');
	}
	// ]]>
	</script>	
</body>
		
