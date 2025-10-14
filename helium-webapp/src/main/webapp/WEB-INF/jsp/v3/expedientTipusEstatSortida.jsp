<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>


<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="expedient.tipus.estat.sortida.titol" arguments="${estat.codi},${estat.nom}"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>
	
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>

	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.tablednd.js"/>"></script>

	<hel:modalHead/>
</head>
<body>			
	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tancar"/></button>
	</div>

	<script type="text/javascript">
	// <![CDATA[
	           
     function refrescarTaulaDades(taulaId) {
		try {
			$(taulaId).dataTable().fnDraw();
		}catch(e) {
			// no hi ha cap taula de dades amb aquest id
		}
	}
     
	$(document).ready(function() {
		$('#sortida').select2({
		    width: '100%',
		    theme: "bootstrap",
		    minimumResultsForSearch: 10
		});
		
		$('#btnAddSortida').click(function() {
			
			$('#sortida').select2("enable", false);
			$('#btnAddSortida').addClass('disabled');
			$('#spinSortida', this).removeClass("fa-plus").addClass("fa-cog").addClass("fa-spin");	
			
			var estatSortidaId = $('#sortida').val();
			$.ajax({
			    url:"<c:url value="/v3/expedientTipus/${expedientTipus.id}/estat/${estat.id}/sortida/add/"></c:url>" + estatSortidaId,
			    type:'POST',
			    dataType: 'json',
			    cache: false,
			    success: function(data) {
			    	refrescarTaulaDades('#estatSortidaDataTable'); 
			    },
			    error: function(jqXHR, textStatus, errorThrown) {
			    	console.log("Error afegint l'estat de sortida a l'estat: [" + textStatus + "] " + errorThrown);
			    },
			    complete: function() {
			    	webutilRefreshMissatges();
					$('#sortida').select2("enable", true);
					$('#btnAddSortida').removeClass('disabled');
					$('#spinSortida', $('#btnAddSortida')).removeClass("fa-cog").addClass("fa-plus").removeClass("fa-spin");
			    }
			});
		});
		
		
		// Quan es repinta la taula aplica la reordenació
		//$('#estatSortidaDataTable').on('draw.dt', function() {
		//	/* <c:if test="${!heretat}"> */
		//	// Posa la taula com a ordenable
		//	$("#estatSortidaDataTable").tableDnD({
		//    	onDragClass: "drag",
		//    	onDrop: function(table, row) {	        	
		//        	var pos = row.rowIndex - 1;
		//        	var id= obtenirId('#estatSortidaDataTable', pos);
		//        	if (pos != filaMovem)
		//        		canviarPosicioValidacio("#estatSortidaDataTable", id,pos);
		//    	},
		//    	onDragStart: function(table, row) {
		//    			filaMovem = row.rowIndex-1;
		//		}
		//    });
		//	/* </c:if> */
		//  });		
		
	});
	
		function canviarPosicioValidacio(taulaId, id, pos) {
		  	// Canvia la ordenació sempre amb ordre ascendent
			$('#estatSortidaDataTable').DataTable().order([1, 'asc']);
			var getUrl = '<c:url value="/v3/expedientTipus/${expedientTipus.id}/estat/${estat.id}/sortida/"></c:url>' + id + '/moure/' + pos;
			$.ajax({
				type: 'GET',
				url: getUrl,
				async: true,
				success: function(result) {
				},
				error: function(e) {
					console.log("Error canviant l'ordre: " + e);
				},
				complete: function() {
			    	refrescarTaulaDades(taulaId); 
					webutilRefreshMissatges();
					$(spin).remove();
					$(input).show();
				}
			});	
		}

		function obtenirId(taulaId, pos){
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
			id = $(taulaId + " tr:eq("+fila+")").attr("id");	
			id2 = id.split("_");
			return id2[1] ;
		}
	
	
	// ]]>
	</script>
	
	<fieldset>
	

		<table	id="estatSortidaDataTable"
				data-toggle="datatable"
				data-url="<c:url value="/v3/expedientTipus/${expedientTipus.id}/estat/${estat.id}/sortida/datatable"></c:url>"
				data-paging-enabled="false"
				data-default-order="1"
				data-ordering="false"
				class="table table-striped table-bordered table-hover">
			<thead>
				<tr>
					<th data-col-name="id" data-visible="false"/>
					<th data-col-name="codi"><spring:message code="expedient.tipus.estat.sortida.llistat.columna.codi"/></th>
					<th data-col-name="nom"><spring:message code="expedient.tipus.estat.sortida.llistat.columna.nom"/></th>
					<th data-col-name="id" data-template="#cellEstatAccionsSortidaTemplate" data-orderable="false" width="10%">
						<script id="cellEstatAccionsSortidaTemplate" type="text/x-jsrender">
						<c:if test="${!heretat}">
					    	<a href="<c:url value="/v3/expedientTipus/${expedientTipus.id}/estat/${estat.id}/sortida/"></c:url>{{:id}}/delete"  class="btn btn-danger" data-toggle="ajax" data-confirm="<spring:message code="expedient.tipus.estat.sortida.llistat.accio.esborrar.confirmacio"/>"><span class="fa fa-trash-o"></span></a>
						</c:if>
					</script>
					</th>
				</tr>
			</thead>
		</table>
		
		<c:if test="${!heretat}">
			<div class="row botons-titol text-left" width="100%">
				<div class="col-sm-11">
					<select id="sortida">
						<c:forEach items="${estatsSortida}" var="sortida">
							<option value="${sortida.id}">${sortida.codi} - ${sortida.nom}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-sm-1">
					<button id="btnAddSortida" class="btn btn-primary" style='${mostraCreate || mostraUpdate ? "display:none;" : ""}'><span id="spinSortida" class="fa fa-plus"></span>&nbsp;<spring:message code="expedient.tipus.estat.sortida.llistat.accio.afegir"/></button>
				</div>
			</div>	
		</c:if>		
	</fieldset>

</body>
		
