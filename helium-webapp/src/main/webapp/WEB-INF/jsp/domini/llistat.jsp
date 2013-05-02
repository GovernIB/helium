<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<html>
<head>
	<title><fmt:message key='domini.llistat.dominis' /></title>
	<meta name="titolcmp" content="<fmt:message key='comuns.disseny' />" />
	<script type="text/javascript" src="<c:url value="/js/selectable.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/dwr/interface/dominiDwrService.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.DOMWindow.js"/>"></script>
    <link href="<c:url value="/css/displaytag.css"/>" rel="stylesheet" type="text/css"/>
    <c:import url="../common/formIncludes.jsp"/>
    
<%-- 

inici nou formulari modal

--%>   
    
    <link media="all" type="text/css" href="http://code.jquery.com/ui/1.8.21/themes/base/jquery-ui.css" rel="stylesheet">
	<link media="all" type="text/css" href="http://static.jquery.com/ui/css/demo-docs-theme/ui.theme.css" rel="stylesheet">
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type="text/javascript" src="http://code.jquery.com/ui/1.8.21/jquery-ui.min.js"></script>
    
  
    <style >
/* 		body { font-size: 62.5%; } */
/* 		label, input { display:block; } */
/* 		input.text { margin-bottom:12px; width:95%; padding: .4em; } */
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

					if ( validar() == true) {
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
	</script>
     
<script type="text/javascript">
// <![CDATA[
function confirmar(e) {
	var e = e || window.event;
	e.cancelBubble = true;
	if (e.stopPropagation) e.stopPropagation();
	return confirm("<fmt:message key='domini.llistat.confirmacio' />");
}

// ]]>
</script>

<%String idDominio = request.getParameter("id");%>

<style>
        .black_overlay{
            display: hide;
            position: absolute;
            top: 0%;
            left: 0%;
            width: 100%;
            height: 100%;
            background-color: black;
            z-index:1001;
            -moz-opacity: 0.8;
            opacity:.80;
            filter: alpha(opacity=80);
        }
        .white_content {
            display: hide;
            position: absolute;
            top: 25%;
            left: 25%;
            width: 300px;
            height: 250px;
            padding: 16px;
            border: 16px solid orange;
            background-color: white;
            z-index:1002;
            overflow: auto;
        }
    </style>


</head>
<body>

<div class="demo">

	<div id="dialog-form" title="Paràmetres">
		<p class="validateTips"></p>
	
		<form>
		<fieldset>
			<div id="params"></div>
		</fieldset>
		</form>
	</div>

</div>



<div class="demo">

<div id="dialog-form-WS" title="Paràmetres">
	<p class="validateTips"></p>

	<form>
	<div id="flotante"  align="right" style="display:none;color:red;font-size:10px">
		<p>El format de la data ha de ser dd/mm/aaaa</p>	
	</div>
	<div align="left">
		<label id="">Afegir &nbsp</label><img src="<c:url value="/img/add.png"/>" alt="afegir" title="afegir" border="0" onclick="afegirParam()"/>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp<label for='nomCodiDomini'>Codi consulta</label>
		<em>
		<img border="0" title="Camp obligatori" alt="Camp obligatori" src="/helium/img/bullet_red.png">
		</em>
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



	<input id="idDominio" type="hidden" value="<%=idDominio%>">
	<display:table name="llistat" id="registre" requestURI="" class="displaytag selectable" >
		<display:column property="codi" titleKey="comuns.codi" sortable="true" url="/domini/form.html" paramId="id" paramProperty="id"/>
		<display:column property="nom" titleKey="comuns.nom" sortable="true"/>
		<display:column property="tipus" titleKey="comuns.tipus"/>
		<display:column titleKey="domini.form.provar">
						
			<img src="<c:url value="/img/gear.png"/>" alt="provar" title="provar" border="0" class="provar" data-id="${registre.id}" />
			<input type="hidden" name="id" value="${registre.id}"/>
			<input type="hidden" id="sql_${registre.id}" name="sql_${registre.id}" value="${registre.sql}"/>
			<input type="hidden" id="url_${registre.id}" name="url_${registre.id}" value="${registre.url}"/>
			<input type="hidden" id="tipus_${registre.id}" name="tipus_${registre.id}" value="${registre.tipus}"/>
 		</display:column>
		<display:column >
			<a href="<c:url value="/domini/delete.html"><c:param name="id" value="${registre.id}"/></c:url>" onclick="return confirmar(event)"><img src="<c:url value="/img/cross.png"/>" alt="<fmt:message key='comuns.esborrar' />" title="<fmt:message key='comuns.esborrar' />" border="0"/></a>
		</display:column>
	</display:table>
	<script type="text/javascript">initSelectable();</script>
		
	<form action="<c:url value="/domini/form.html"/>">
		<button type="submit" class="submitButton"><fmt:message key='domini.llistat.nou' /></button>
	</form>


	
<script type="text/javascript">

	var parametres = 0;
	var cadena = "";
	var domini = 0;
	var keyString = new Array();
	var cp=0;
	var params = new Array();
	var ws;
	
	
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
		if($.browser.msie){
			provar(this.getAttribute('id'));
		}
		else{
			provar(this.dataset['id']);
		}
		
		cp=0;
		event.stopPropagation();
	});
	
	$(".provarWS").click(function(event){
		if($.browser.msie){
			domini = this.getAttribute('id');
		}else{
			domini = this.dataset['id'];
		}
		$("#dialog-form-WS").dialog("open");
		event.stopPropagation();
	});

	function replaceParams(sql,parametres){
		var params = new Array(parametres);
		var j=0;
		for (var i=0;i<parametres;i++){
			params[j++] = keyString[i];
			params[j++] = $("#par_" + i).val();
		}
		consultaDomini(domini,params);
	}
	
	function preparaParams(domini){
		params=[];
		var j=0;
		for (var i=0;i<cp;i++){
			if($("#tipusParam_"+(i)).val()!=null){
				params[j++] = $("#tipusParam_"+(i)).val();
				params[j++] = $("#par_"+i).val();
				params[j++] = $("#codi_"+i).val();
			}
		}
		consultaWSParams(domini, $("#codiDomini").val(), params);
	}
	
	
	
	
	function esborrarParam(cp){

		$("#"+cp).remove();
		if(cp>0) cp--;
	}
	
	function afegirParam(){
		$("#flotante").hide();
		$("#paramsWS").append(
				"<div id='"+cp+"' class='ctrlHolder' id='capa_"+cp+"'>\n" +
				"<label for ='etiqueta_"+cp+"'>Parametre </label>\n" +
				"<input type='text' name='codi_" + cp + "' id='codi_" + cp + "' style='margin-left:10px' class='class='text ui-widget-content ui-corner-all'  />\n"+
				"<select id='tipusParam_"+cp+"' onChange=afegirAvisFormat("+cp+")>"+
				"<option value=''>.....</option>" +
				"<option value='string'>string</option>" +
				"<option value='int'>integer</option>" +
				"<option value='float'>float</option>" +
				"<option value='boolean'>boolean</option>" +
				"<option value='date'>date</option>" +
				"<option value='price'>price</option>" +
				"</select>"+
				"<input type='text' name='par_" + cp + "' id='par_" + cp + "' style='margin-left:10px' class='text ui-widget-content ui-corner-all' />\n" +
				"<img id='imatge_"+cp+"' src='<c:url value='/img/delete.png'/>' alt='esborrar' title='esborrar' border='0' onclick='esborrarParam("+cp+")'/>" +
				"</div>");
				
		cp++;
	}
	function afegirAvisFormat(cp){
		if($("#tipusParam_"+cp).val() == "date"){
		$("#flotante").fadeIn('slow');
		}else{$("#flotante").hide();}
	}

	

	function provar(dominiId){
		domini = dominiId;
		if($('#tipus_'+domini).val() == "CONSULTA_SQL"){
			cadena = $("#sql_" + dominiId).val();
			if(cadena.indexOf(":")!=-1){
				var paramsContent = cadena.split(" ");
				var contador=0;
				var count=0;
				for(var i=0;i<paramsContent.length;i++){
					count = paramsContent[i].indexOf(':');
					if(count!=-1){
						keyString[contador]= paramsContent[i].substr(count+1,paramsContent[i].length-1);
						contador++;
					}
				}

				parametres = cadena.split(":").length - 1;
				$("#params").empty();
				$("#params").append("<p class='aclaracio'>Url=  " + cadena + "</p>");
				$("#params").append("<br/>");
				
				for (var i=0;i<parametres;i++){
					$("#params").append(
							"<div class='ctrlHolder'>\n" +
							"<label for='par_" + i + "'>Paràmetre_" + i + "</label>\n" +
							"<input type='text' name='par_" + i + "' id='par_" + i + "' class='text ui-widget-content ui-corner-all' />\n" +
							"</div>");
				
				}
				
				$("#dialog-form").dialog("open");			
			
			}
			else{consulta(domini);}
		}
		else{
			cadena= $("#url_"+domini).val();
			$("#paramsWS").empty();
			$("#paramsWS").append("<p class='aclaracio'>URL:  " + cadena + "</p>"+"<br>");
// 			$("#paramsWS").append(
			
// 			"<div><table id='capçalera'><tr><td></td><td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbspCodi</td><td>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbspTipus</td><td>Valor</td></tr></table></div>"
// 			);
			$("#flotante").hide();
			$("#dialog-form-WS").dialog("open");
		}
	}//fi provar

	
	//consulta al domini per id del domini
	function consulta(dominiId){
		dominiDwrService.ping(dominiId,
				{
					callback: function(data) {
						var domini = data;
						alert(domini);
					},
					async: false
				});
	}
	
	
	//consulta al domini sql per id del domini i amb paràmetres
	function consultaDomini(dominiId,params){
		dominiDwrService.pingParams(dominiId,params,
				{
					callback: function(data) {
						var domin = data;
						if(domin!="" || domin.indexOf("incorrecte")!=-1){
							alert(domin);
						}
						else{
							alert("El domini no retorna dades");
						}
						
					},
					async: false
				});
	}
	
	
	//consulta al domini WS amb paràmetres i idConsulta
	function consultaWSParams(dominiId, idConsulta,  params){
		
		dominiDwrService.pingWSParams(dominiId, idConsulta, params,
			{
				callback: function(data) {
					var dominiD = data;
					if(dominiD!="" || dominiD.indexOf("incorrecte")!=-1){
						alert(dominiD);
					}
					else{
						alert("El domini no retorna dades");
					}
				},
				async: false
			});
	}
	
	//consulta al domini WS amb idConsulta
	function consultaWS(dominiId, idConsulta){
		
		dominiDwrService.pingWS(dominiId, idConsulta,
			{
				callback: function(data) {
					alert(data);
					var dominiD = data;
					if(dominiD!="" || dominiD.indexOf("incorrecte")!=-1){
						alert(dominiD);
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
