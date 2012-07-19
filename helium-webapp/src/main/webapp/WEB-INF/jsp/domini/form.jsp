<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><c:choose><c:when test="${empty command.id}"><fmt:message key="domini.form.crear_nou"/></c:when><c:otherwise><fmt:message key="domini.form.modificar"/></c:otherwise></c:choose></title>
	<meta name="titolcmp" content="<fmt:message key="comuns.disseny"/>" />
	<c:import url="../common/formIncludes.jsp"/>
	
	<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/dominiDwrService.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.js"/>"></script>

	<link media="all" type="text/css" href="http://code.jquery.com/ui/1.8.21/themes/base/jquery-ui.css" rel="stylesheet">
	<link media="all" type="text/css" href="http://static.jquery.com/ui/css/demo-docs-theme/ui.theme.css" rel="stylesheet">
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type="text/javascript" src="http://code.jquery.com/ui/1.8.21/jquery-ui.min.js"></script>


<style >
	fieldset { padding:0; border:0; margin-top:25px; }
	h1 { font-size: 1.2em; margin: .6em 0; }
	div#users-contain { width: 350px; margin: 20px 0; }
	div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
	div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
	.ui-dialog .ui-state-error { padding: .3em; }
	.validateTips { border: 1px solid transparent; padding: 0.3em; }
	.dialog-form{
        position:fixed; 
	    font-family:arial;
    	font-size:1em;
    	border:0.05em solid black;
    	overflow:false;
    	background-color:#fff;
}


	table td, div#users-contain table th { 
		border: 0px #eee; 
		margin-left:15px;
		padding: 25px; 
		text-align: right; 
		}
</style>



<script>

var parametres = 0;
var cadena = "";
var keyString = new Array();
var cp=0;
var params = new Array();
var ws;
var domini = 0;
var sql=""; 
var url="";
var sq;
var ur;

$(document).ready(function () {
	sq= $("#sql0").val();
	ur= $("#url0").val();
});


$(function() {
	
	$( "#dialog:ui-dialog" ).dialog( "destroy" );
	
	var name = $( "#parametre_${registre.id} }" ),
		allFields = $( [] ).add( name ),
		tips = $( ".validateTips" );

	function updateTips( t ) {
		tips
			.text( t )
			.addClass( "ui-state-highlight" );
		setTimeout(function() {
			tips.removeClass( "ui-state-highlight", 1500 );
		}, 500 );
	}


	$( "#dialog-form" ).dialog({
		autoOpen: false,
		height: 300,
		width: 525,
		modal: true,
		resizable:false,
		buttons: {
			"Acceptar": function() {
				allFields.removeClass( "ui-state-error" );
				//var bValid = parametres.length;
				if ( validar() == true ) {
					replaceParams(cadena,parametres);
					$( this ).dialog( "close" );
				}else{alert("S'han d'introduir els paràmetres requerits");}
			},
			Cancel: function() {
				$( this ).dialog( "close" );
			}
		},
		close: function() {
			allFields.val( "" ).removeClass( "ui-state-error" );
		}
	});
	
	$( "#dialog-form-WS" ).dialog({
		autoOpen: false,
		height: 300,
		width: 725,
		modal: true,
		resizable:true,
		buttons: {
			"Acceptar": function() {
				allFields.removeClass( "ui-state-error" );
				var bValid = $("#codiDomini").val();
				if ( bValid !="" ) {
					preparaParams(domini);
					$("#codiDomini").val("");
					$( this ).dialog( "close" );
				}else{alert("S'ha d'emplenar Codi Consulta");}
			},
			"Cancel·lar": function() {
				$( this ).dialog( "close" );
			}
		},
		close: function() {
			allFields.val( "" ).removeClass( "ui-state-error" );
		}
	});
	
	
	
});


// <![CDATA[
function disable(blocid) {
	$("#" + blocid).find("input,select,textarea").attr("disabled", "disabled");
}
function enable(blocid) {
	$("#" + blocid).find("input,select,textarea").removeAttr("disabled");
}
function canviTipus(input) {
	if (input.value == "CONSULTA_WS") {
		enable("camps_ws");
		disable("camps_sql");
	} else if (input.value == "CONSULTA_SQL") {
		enable("camps_sql");
		disable("camps_ws");
	} else {
		disable("camps_ws");
		disable("camps_sql");
	}
}
function canviTipusAuth(input) {
	if (input.value == "NONE") {
		disable("camps_usupass");
	} else {
		enable("camps_usupass");
	}
}

function preparaParams(domini){
	var j=0;
	for (var i=0;i<cp;i++){
		
		params[j++] = $("#tipusParam_"+(i)).val();
		params[j++] = $("#par_"+i).val();
		params[j++] = $("#codi_"+i).val();
	}
	consultaDomini(domini,params);
}

function validar(){
	
	var s=0;
	for(s = 0;s<parametres;s++){
		if($("#par_"+s).val() ==""){
			return false;
		}
	}
	return true;
}


$(".provar").click(function(event){
	provar(this.dataset['id']);
	cp=0;
	event.stopPropagation();
});

$(".provarWS").click(function(event){
	domini = this.dataset['id'];
	$("#dialog-form-WS").dialog("open");
	event.stopPropagation();
});


function esborrarParam(){
	$("#paramsWS div:last-child").remove();
	if(cp>0) cp--;
}

function afegirParam(){

	$("#paramsWS").append(
			"<div class='ctrlHolder'>\n" +
			"<label for ='etiqueta_"+cp+"'>Parametre_" + cp + "</label>\n" +
			"<input type='text' name='codi_" + cp + "' id='codi_" + cp + "' style='margin-left:10px' class='class='text ui-widget-content ui-corner-all'  />\n"+
			"<select id='tipusParam_"+cp+"'>"+
			"<option value=''>.....</option>" +
			"<option value='string'>string</option>" +
			"<option value='int'>integer</option>" +
			"<option value='float'>float</option>" +
			"<option value='boolean'>boolean</option>" +
			"<option value='date'>date</option>" +
			"<option value='price'>price</option>" +
			"</select>"+
			"<input type='text' name='par_" + cp + "' id='par_" + cp + "' style='margin-left:10px' class='text ui-widget-content ui-corner-all' />\n" +
			"<img src='<c:url value='/img/delete.png'/>' alt='esborrar' title='esborrar' border='0' onclick='esborrarParam()'/>" +
			"</div>");
			
	cp++;
}


// ]]>
</script>
</head>
<body>


<!-- finestra per demanar paràmetres -->
<div id="dialog-form" title="Paràmetres">
	<p class="validateTips"></p>

	<form>
	<fieldset>
		<div id="params"></div>
	</fieldset>
	</form>
</div>
<div class="demo">

<div id="dialog-form-WS" title="Paràmetres">
	<p class="validateTips"></p>

	<form>
	<div align="left">
		<label id="">Afegir &nbsp</label><img src="<c:url value="/img/add.png"/>" alt="afegir" title="afegir" border="0" onclick="afegirParam()"/>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<label for='nomCodiDomini'>Codi consulta</label>
		<input type='text' name='codiDomini' id='codiDomini' class='text ui-widget-content ui-corner-all'>
		<br>
		<br>
	</div>
	<fieldset>
		<div id="paramsWS"></div>
	</fieldset>
	</form>
</div>

</div>



<!-- FI finestra per demanar paràmetres -->

	<input type="hidden" name="dominiId" value="${registre.id}"/>
	<form:form action="form.html" cssClass="uniForm">
		<div class="inlineLabels col first">
			<h3><fmt:message key="domini.form.dades_dom"/></h3>
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="codi"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key="comuns.codi"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key="comuns.nom"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="tipus"/>
				<c:param name="required" value="true"/>
				<c:param name="type" value="select"/>
				<c:param name="items" value="tipusDomini"/>
				<c:param name="itemBuit">&lt;&lt; <fmt:message key="domini.form.selec_tipus"/> &gt;&gt;</c:param>
				<c:param name="label"><fmt:message key="comuns.tipus"/></c:param>
				<c:param name="onchange">canviTipus(this)</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="cacheSegons"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key="domini.form.temps_cache"/></c:param>
				<c:param name="comment"><fmt:message key="domini.form.en_segons"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="descripcio"/>
				<c:param name="type" value="textarea"/>
				<c:param name="label"><fmt:message key="comuns.descripcio"/></c:param>
			</c:import>
		</div>
		<div class="inlineLabels col last">
			<h3><fmt:message key="domini.form.dades_ws"/></h3>
			<div id="camps_ws">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="url"/>
					<c:param name="type" value="textarea"/>
					<c:param name="label"><fmt:message key="domini.form.url"/></c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="tipusAuth"/>
					<c:param name="type" value="select"/>
					<c:param name="items" value="tipusAuth"/>
					<c:param name="label"><fmt:message key="domini.form.tipus.auth"/></c:param>
					<c:param name="onchange">canviTipusAuth(this)</c:param>
				</c:import>
				<div id="camps_usupass">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="origenCredencials"/>
						<c:param name="type" value="select"/>
						<c:param name="items" value="origenCredencials"/>
						<c:param name="label"><fmt:message key="domini.form.origen.creds"/></c:param>
					</c:import>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="usuari"/>
						<c:param name="label"><fmt:message key="domini.form.usuari"/></c:param>
					</c:import>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="contrasenya"/>
						<c:param name="label"><fmt:message key="domini.form.contrasenya"/></c:param>
					</c:import>
				</div>
			</div>
			<h3><fmt:message key="domini.form.dades_sql"/></h3>
			<div id="camps_sql">
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="jndiDatasource"/>
					<c:param name="label"><fmt:message key="domini.form.jndi"/></c:param>
				</c:import>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="sql"/>
					<c:param name="type" value="textarea"/>
					<c:param name="label"><fmt:message key="domini.form.sql"/></c:param>
				</c:import>
				<div class="buttonHolder">
				<input type="hidden" name="pingD" value="">
				<button type="button" onclick="provar(${registre.id})">Provar</button>
				</div>
			</div>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}"><fmt:message key="comuns.crear"/>,<fmt:message key="comuns.cancelar"/></c:when><c:otherwise><fmt:message key="comuns.modificar"/>,<fmt:message key="comuns.cancelar"/></c:otherwise></c:choose></c:param>
		</c:import>
	</form:form>
	
	<p class="aclaracio"><fmt:message key="comuns.camps_marcats"/> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key="comuns.camp_oblig"/>" title="<fmt:message key="comuns.camp_oblig"/>" border="0"/> <fmt:message key="comuns.son_oblig"/></p>

	<script type="text/javascript">$(document).ready(canviTipus(document.getElementById("tipus0")));</script>
	<script type="text/javascript">$(document).ready(canviTipusAuth(document.getElementById("tipusAuth0")));</script>
	<script type="text/javascript">
	
	function replaceParams(sql,parametres){
		var params = new Array(parametres);
		
		var j=0;
		for (var i=0;i<parametres;i++){
			params[j++] = keyString[i];
			params[j++] = $("#par_" + i).val();
		}
		consultaAmbParams($("#id").val(),sql, params);
	}


	function consultaAmbParams(dominiId, sql, params){
		
		dominiDwrService.pingParams(dominiId, params,
			{
				callback: function(data) {
					var domini = data;
					if(domini!="" || data.indexOf("incorrecte")!=-1){
						alert(domini);
					}
					else{
						alert("El domini no retorna dades");
					}
				},
				async: false
			});
	}

	var keyString = new Array();

	
	function provar(idDomini){
		if(sq==$("#sql0").val() && ur==$("#url0").val()){	
		
		domini = $('#id').val();
		if($('#tipus0 option:selected').val() == "CONSULTA_SQL"){
			sql = $('#sql0').val();
			cadena = sql;
			if(cadena.indexOf(":")!=-1){
				var paramsContent = cadena.split(" ");
				var contador=0;
				var count=0;
				for(var i=0;i<paramsContent.length;i++){
					count = paramsContent[i].indexOf(':');
					if(count!=-1){
						keyString[contador]= paramsContent[i].substr((count+1),paramsContent[i].length -1);
						contador++;
					}
				}

				parametres = cadena.split(":").length - 1;
				$("#params").empty();
				$("#params").append("<p class='text'>" + cadena + "</p>");
				$("#params").append("<br/>");
				
				for (var i=0;i<parametres;i++){
					$("#params").append(
							"<div class='ctrlHolder'>\n" +
							"<label for='par_" + i + "'>Paràmetre_" + i + "</label>\n" +
							"<input type='text' name='par_" + i + "' id='par_" + i + "' class='text ui-widget-content ui-corner-all' />\n" +
							"</div>");
				}
				$("#dialog-form").dialog("open");
			} else {
				consulta(domini);
			};
		}
		else{
			cadena= $("#url0").val();
			$("#paramsWS").empty();
			$("#paramsWS").append("<p class='aclaracio'>URL:  " + cadena + "</p>"+"<br>");
			$("#dialog-form-WS").dialog("open");
			
		}
		
		}
		else{alert("Heu de desar els canvis al domini abans de provar");}
	}
	
	function consultaDomini(dominiId,params){
		dominiDwrService.pingParams(dominiId,params,
				{
					callback: function(data) {
						var domin = data;
						alert(domin);
					},
					async: false
				});
	}
	
	
	function consulta(dominiId){
		dominiDwrService.ping(dominiId,
		{
			callback: function(data) {
			var domini = data;
			if(domini!="" || domini.indexOf("incorrecte")!=-1){
				alert(domini);
			}
			else{
				alert("El domini no retorna dades");			
			}
		},
		async: false
		});
	}
</script>



</body>
</html>
