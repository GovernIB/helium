<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="expedient.tipus.enumeracio.form.titol.llistat"/></title>
	<hel:modalHead/>
	<script src="/helium/webjars/datatables.net/1.10.10/js/jquery.dataTables.min.js"></script>
	<script src="/helium/webjars/datatables.net-bs/1.10.10/js/dataTables.bootstrap.min.js"></script>
	<link href="/helium/webjars/datatables.net-bs/1.10.10/css/dataTables.bootstrap.min.css" rel="stylesheet"></link>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<script src="/helium/js/jsrender.min.js"></script>
	
	<script type="text/javascript">

		var dataTableURL = "";
	
		var msg_confirmaBorrar = "<spring:message code='expedient.tipus.enumeracio.valors.llistat.confirm.esborra'/>";
		var msg_borratKO = "<spring:message code='expedient.tipus.enumeracio.valors.llistat.esborrat.ko'/>";
	
		function netejaErrorCamp(campId) {
			$("camp."+campId+".container").removeClass("has-error");
			$("pError_"+campId).hide();
			$(campId+".errors").html("");
		}
		
		function netejaForm() {
			
			netejaErrorCamp("codi");
			netejaErrorCamp("nom");
			
			$("#codi").val("");
			$("#nom").val("");
			$("#accio").val("nou");
			$("#id").val("");
			$("#expeId").val("");
			$("#enumId").val("");
		}
	
		function guardaDadesValorEnumerat() {
			debugger;
			var str = $("#formNouValorEnum").serialize();
			var v_url = $("#formNouValorEnum").attr("action")+"/guardar"; //"/helium/v3/expedientTipus/"+$("#expeId").val()+"/enumeracio/"+$("#enumId").val()+"/valor/guarda";
			
			$.ajax({
				url: v_url,
				method: "POST",
				data:str,
				//contentType: "text/plain"
			}).done(function(res) {
				
				debugger;
				var data = jQuery.parseJSON(res);
				console.log(data);
				
				if (data!="OK") {
					if (data.codi) { mostrarErrorCamp("codi", data.codi); }
					if (data.nom) { mostrarErrorCamp("nom", data.nom); }
				}else{
					$("#formNouValorEnum").hide();
					netejaForm();
					$('table#expedientEnumeracioValor').webutilDatatable('refresh-url', dataTableURL);
				}
			});
		}
		
		function carregaDadesValorEnumerat(expedientTipusId, enumId, id) {

			var v_url = "/helium/v3/expedientTipus/"+expedientTipusId+"/enumeracio/"+enumId+"/valor/"+id+"/get";
			
			$.ajax({
				url: v_url,
				method: "GET",
			}).done(function(res) {
				var data = jQuery.parseJSON(res);
				$("#accio").val("modificar");
				$("#id").val(id);
				$("#expeId").val(expedientTipusId);
				$("#enumId").val(enumId);
				$("#codi").val(data.codi);
				$("#nom").val(data.nom);
			});
		}
		
		function esborraValorEnumerat(expedientTipusId, enumId, id) {

			if (confirm(msg_confirmaBorrar)) {
			
				var v_url = "/helium/v3/expedientTipus/"+expedientTipusId+"/enumeracio/"+enumId+"/valor/"+id+"/delete";
				
				$.ajax({
					url: v_url,
					method: "GET",
				}).done(function(res) {
					debugger;
					if ("OK"==res) {
						$('table#expedientEnumeracioValor').webutilDatatable('refresh-url', dataTableURL);
					}else{
						alert(msg_borratKO+": "+res);
					}
				});
			}
		}
		
		function mostrarErrorCamp(campId, msgErr) {
			$("camp."+campId+".container").addClass("has-error");
			$("pError_"+campId).show();
			$(campId+".errors").html(msgErr);
		}
		
		$(document).ready( function() {
			
			dataTableURL = $('table#expedientEnumeracioValor').attr("data-url");
			
			$("#formNouValorEnum").hide();
			
			$("#botoNouValorEnum").on("click", function (e, settings) {
				$("#botoNouValorEnum").hide();
				netejaForm();
				$("#formNouValorEnumBtnNou").show();
				$("#formNouValorEnumBtnMod").hide();
				$("#formNouValorEnum").show();
				$("#accio").val("nou");
			});
			
			$("#formNouValorEnumBtnCan").on("click", function (e, settings) {
				netejaForm();
				$("#formNouValorEnum").hide();
				$("#botoNouValorEnum").show();
			});

			$('table#expedientEnumeracioValor').on('draw.dt', function (e, settings) {
				var alturaContent = document.body.clientHeight;				
				var alturaMaxima = screen.height-280; //$(window.top).height();
// 				if (alturaContent>alturaMaxima-100) {
// 					alturaContent = alturaMaxima-100;
// 				}
				window.frameElement.style.height=alturaMaxima+"px";
				window.frameElement.parentElement.style.height=alturaMaxima+"px";
			});
		}); 
	</script>

</head>
<body>

	<form:form cssClass="form-horizontal" style="display: none;" id="formNouValorEnum" action="${formAction}" enctype="multipart/form-data" method="post" commandName="expedientTipusEnumeracioValorCommand">

		<div class="well text-right">
		
			<input type="hidden" id="accio"		name="accio" 	value="nou"/>
			<input type="hidden" id="id" 		name="id" 		value="${expedientTipusEnumeracioValorCommand.id}"/>
			<input type="hidden" id="expeId" 	name="expeId" 	value="${expedientTipus.id}"/>
			<input type="hidden" id="enumId" 	name="enumId" 	value="${enumeracio.id}"/>
			
			<div class="form-group" id="camp.codi.container">
				<label class="control-label col-xs-4 obligatori" for="codi">
					<spring:message code='expedient.tipus.enumeracio.valors.form.camp.codi' />
				</label>
				<div class="col-xs-8">
					<input id="codi" name="codi" class="form-control" type="text" value="">
					<p class="help-block" style="display:none;" id="pError_codi">
						<span class="fa fa-exclamation-triangle"></span>&nbsp;
						<span id="codi.errors"></span>
					</p>
				</div>
			</div>
			
			<div class="form-group" id="camp.nom.container">
				<label class="control-label col-xs-4 obligatori" for="codi">
					<spring:message code='expedient.tipus.enumeracio.valors.form.camp.nom' />
				</label>
				<div class="col-xs-8">
					<input id="nom" name="nom" class="form-control" type="text" value="">
					<p class="help-block" style="display:none;" id="pError_nom">
						<span class="fa fa-exclamation-triangle"></span>&nbsp;
						<span id="nom.errors"></span>
					</p>
				</div>
			</div>				

			<div class="row text-right">
				<div class="col-xs-12">
					<button id="formNouValorEnumBtnCan" type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
					<button id="formNouValorEnumBtnNou" class="btn btn-primary right" type="button" onclick="guardaDadesValorEnumerat();">
						<span class="fa fa-plus"></span> <spring:message code='comu.boto.crear' />
					</button>
					<button id="formNouValorEnumBtnMod" class="btn btn-primary right" type="button" onclick="guardaDadesValorEnumerat();">
						<span class="fa fa-pencil"></span> <spring:message code='comu.boto.modificar' />
					</button>
				</div>
			</div>
		</div>

	</form:form>

	<div class="botons-titol text-right" id="botoNouValorEnum">
		<a	id="nou_camp" class="btn btn-default" href="#" data-datatable-id="expedientEnumeracio">
			<span class="fa fa-plus"></span>&nbsp;
			<spring:message code="expedient.tipus.enumeracio.llistat.accio.nova"/>
		</a>
	</div>

	<c:choose>
		<c:when test="${not empty expedientTipus}">
			
			<table	id="expedientEnumeracioValor"
					data-toggle="datatable"
					data-url="/helium/v3/expedientTipus/${expedientTipus.id}/enumeracio/${enumeracio.id}/valors/datatable"
					data-paging-enabled="true"
					data-info-type="search"
					data-ordering="true"
					data-scrollMinHeight="500px"
					data-default-order="2"
					class="table table-striped table-bordered table-hover">
				<thead>
					<tr>
						<th data-col-name="id" data-visible="false"/>
						<th data-col-name="codi" width="20%"><spring:message code="expedient.tipus.enumeracio.valors.llistat.columna.codi"/></th>
						<th data-col-name="nom"><spring:message code="expedient.tipus.enumeracio.valors.llistat.columna.titol"/></th>
						<th data-col-name="ordre"><spring:message code="expedient.tipus.enumeracio.valors.llistat.columna.ordre"/></th>
						<th data-col-name="id" data-template="#cellAccionsTemplate" data-orderable="false" width="10%">
							<script id="cellAccionsTemplate" type="text/x-jsrender">
							<div class="dropdown">
								<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
								<ul class="dropdown-menu">
									<li><a href="JavaScript:;" onclick="carregaDadesValorEnumerat('${expedientTipus.id}', '${enumeracio.id}', '{{:id}}');"><span class="fa fa-pencil" ></span>&nbsp;<spring:message code="comu.boto.modificar"/></a></li>
									<li><a href="JavaScript:;" onclick="esborraValorEnumerat('${expedientTipus.id}', '${enumeracio.id}', '{{:id}}');" ><span class="fa fa-trash-o"></span>&nbsp;<spring:message code="comu.boto.esborrar" /></a></li>
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

	<div id="modal-botons" class="well">
		<button type="button" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.tornar"/></button>
	</div>

</body>
</html>