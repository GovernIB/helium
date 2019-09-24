<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:set var="titol"><spring:message code="expedient.tipus.domini.form.provar"/></c:set>

<html>
<head>
	<title>${titol}</title>
	<hel:modalHead/>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
</head>
<body>		
	<form id="testForm" class="form-horizontal" action="/helium/v3/domini/${dominiId}/test">
	<div class="form-group">
		<label class="control-label col-md-2">Codi Domini</label>
		<div class="col-md-6">
			<input type="text" name="codiDomini" id="codiDomini" class="form-control">
		</div>
	</div>
		<table class="table" id="paramsWS">
			<tr>
				<th><spring:message code="expedient.tipus.domini.form.parametres"/></th>
				<th><spring:message code="expedient.nova.data.codi"/></th>
				<th><spring:message code="monitor.domini.columna.tipus"/></th>
				<th><spring:message code="expedient.nova.data.valor"/></th>
				<th></th>
			</tr>
		</table>
		<button class="btn btn-primary" id="afegirParam" title="<spring:message code="expedient.tipus.domini.form.parametres.afegir"/>"><i class="fa fa-plus"></i></button>
	</form>	
		<div class="col">
			<button class="btn btn-primary" style="float:right;margin-bottom: .5em;" onclick="enviar()">
				<span id="spinnerIcon" style="visibility:hidden;" class="fa fa-spinner fa-spin"></span>
				<spring:message code="expedient.tipus.domini.form.provar"/>
			</button>
		</div>
	<div class="row">
	<br>
		<div class="col-xs-12">
		<par id="error"></par>
		<ul class="nav nav-tabs" id="domini-tabs">
  			<li class="active" id="grid"><a href="#">GRID</a></li>
 			<li id="json"><a href="#">JSON</a></li>
 		</ul>
			<div id="res-grid" style="margin:10px;">
				<table class="table"></table>
			</div>
			<pre id="res-json" style="margin:10px;"></pre>
		</div>
	</div>
		
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>	
	<script src="<c:url value="/js/helium.modal.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/dominiDwrService.js"/>"></script>
	<script>
		var cp = 0;
		function afegirParam(){
			$("#paramsWS").append(
					"<tr id='"+cp+"'><td>\n" +
					"<label for ='etiqueta_"+cp+"'>Parametre_" + cp + "</label>\n" +
					"</td>" +
					"<td>\n" +
					"<input type='text' name='codi' id='codi_" + cp + "' class='form-control'  />\n"+
					"</td>" +
					"<td>\n" +
					"<select class='form-control' id='tipusParam_"+cp+"' >"+
					"<option value='string' selected='selected'>string</option>" +
					"<option value='int'>integer</option>" +
					"<option value='float'>float</option>" +
					"<option value='boolean'>boolean</option>" +
					"<option value='date'>date</option>" +
					"<option value='price'>price</option>" +
					"</select>" +
					"</td>" +
					"<td>\n" +
					"<input type='text' name='par' id='par_" + cp + "' class='form-control' />\n" +
					"</td>" + 
					"<td><span class='btn btn-link p-1'  onclick='esborrarParam("+cp+")'><i class='text-danger fa fa-times fa-2x'></i><span>" +
					"</tr>");
			cp++;
		}
		
		function esborrarParam(cp){
			$("#"+cp).remove();
		}
		
		function res(response){
			$("#res-json").html(JSON.stringify(response.objecte, null, " "));
			var data = {};
			response.objecte.forEach(function(element, index) {
				element.columnes.forEach(function(e, i) {
					if(data[e.codi] == undefined)
						data[e.codi] = [];
					data[e.codi].push(e.valor);
				});
			});
			console.log(data);
			var cols = [];
			var length = 0;
			var th = "";
			for(var key in data){
				length = data[key].length >= length? data[key].length : length;
				th += "<th>" + key + "</th>";
				cols.push(key);
			}
				$("#res-grid table").html("<tr>" + th + "</tr>");
			for(var i = 0; i < length; i++){
				var td = "";
				cols.forEach(function(element, index) {
					var val = (data[element][i] == undefined)?"":data[element][i];
					td += "<td>" + val + "</td>";
				});
			$("#res-grid table").append("<tr>"+td+"</tr>");
			} 
		}
		
		function enviar(){
			$("#error").html("");
			var data = {
						'codi': [],
						'tipusParam': [],
						'par': []};
			$(".form-control").each(function (index, element){
				if($(element).attr("id") == "codiDomini"){
					data["codiDomini"] = $(element).val();
				}else if($(element).attr("id").startsWith("codi")){
					data["codi"].push($(element).val());
				}else if($(element).attr("id").startsWith("tipusParam")){
					data["tipusParam"].push($(element).val());
				}else if($(element).attr("id").startsWith("par")){
					data["par"].push($(element).val());
				}
            });
			$('#spinnerIcon').css('visibility', 'visible');
			$('#res-grid table').empty();
			$('#res-json').empty();
			
			$.ajax($("#testForm").attr("action"), {
				  type: "POST",
				  data: JSON.stringify(data),
				  success: function (response){
					  res(response);
		            },
		          error: function (response) {
		        	  $("#error").html('<div class="alert alert-danger"><strong>' + response.statusText + '</strong>' + response.responseJSON.objecte + '</div>' );
		          	},
				  contentType:"application/json",
				  complete: function() {
					  $('#spinnerIcon').css('visibility', 'hidden');
				  }
			});
		}
		
		$("#domini-tabs li").click(function(){
			if($(this).attr("id") == "grid"){
				$(this).addClass("active");
				$("#json").removeClass("active");
				$("#res-grid").show();
				$("#res-json").hide();
			}else if($(this).attr("id") == "json"){
				$(this).addClass("active");
				$("#grid").removeClass("active");
				$("#res-json").show();
				$("#res-grid").hide();
			}
		});
		
		$("#afegirParam").click(function(event){
			event.preventDefault();
			afegirParam();
		});
		
		$(document).ready(function () {
			afegirParam();
			$("#res-json").hide();
		});
	
	</script>
	
	
</body>
</html>