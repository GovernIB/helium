<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<div id="footerSlideContainer">
	<div class="footerSlideContent">
	    <div class="page-title-massiva">
			<h2><span><fmt:message key='expedient.massiva.titol' /></span></h2>
		</div>
		<div class="footerSlideText">
			<table id="pbar_total_massive_table_bars">
				<%
					List<Long> listaMassiva = (List<Long>) session.getAttribute( "consultaExpedientsIdsMassiusActives" );
					List<String> listaMassivaText = (List<String>) session.getAttribute( "consultaExpedientsTextsMassiusActives" );
					
					for (int j=0;(listaMassiva != null) && (j<listaMassiva.size());j++) {		
				    	%>
				    		<tr id="pbar_tr_<%=listaMassiva.get(j)%>">
				    			<td class="td_texto" style="width: 25%">
				    				<%=listaMassivaText.get(j)%>
				    			</td>
				    			<td class="td_barra" style="width: 70%">
				    				<div class="pbar" id="pbar_<%=listaMassiva.get(j)%>"></div>
				    			</td>
				    			<td style="width: 5%">
				    				<img id="pbar_total_massive_icon" src="/helium/img/magnifier.png" onClick="mostrarDetalleExpediente('<%=listaMassiva.get(j)%>')" style="padding-left: 0px;cursor: pointer"/>
				    			</td>
				    		</tr>
				    		<script>
				    			if ($.isFunction($.fn.progressbar)) {
				    				$("#pbar_<%=listaMassiva.get(j)%>").progressbar({ value: 0 });
				    				$("#pbar_tr_<%=listaMassiva.get(j)%>").hide();
				    			}
				    			numBarras++;
				    		</script>
				    	<%
					}
				%>
			</table>
		</div>
	</div>
</div>

<div class="dialog-form-massive-bar" id="footerSlideContainerDetail" title="<fmt:message key='expedient.massiva.proces' />">
	<table id="pbar_total_massive_detail">
		<tr>
			<td>
				<div id="div_progressBarMassiveDetail"/>
			</td>
		</tr>
	</table>
</div>
	
<script>
	$( "#footerSlideContainerDetail" ).dialog({
		autoOpen: false,
		height: 'auto',
		width: 525,
		modal: true,
		resizable:false,
		close: function() {
			ocultarDetalleExpediente();
		}
	});
</script>

<div id="pbar_total_massive_div">
	<table id="pbar_total_massive_table" style="display: none">
		<tr>
			<td style="width: 25%">
				<div id="pbar_total_massive_text"><fmt:message key='expedient.massiva.proces' /></div>
			</td>
			<td style="width: 70%">
				<div id="pbar_total_massive"></div>
			</td>
			<td style="width: 5%;">
				<img id="pbar_total_massive_icon" src="/helium/img/magnifier.png"/>			
			</td>
		</tr>
	</table>
</div>

<script>
	if ($.isFunction($.fn.progressbar)) {
	<% if ((listaMassiva != null) && (listaMassiva.size() > 0))  { %>
		$("#pbar_total_massive").progressbar({ value: 0 });
		
		var porcentajeTotal = 0;	
		function pbar_total_massive(){
		     $.post("/helium/expedient/refreshBarExpedientMassiveAct.html",
			     function(data){
			     	 var value = $( "#pbar_total_massive" ).progressbar( "option", "value" );
			     	 if (value < data) {
			     	 	$( "#pbar_total_massive" ).progressbar( "option", "value", data );
			     	 	porcentajeTotal = data;
			     	 }
			     	 
			     	 if (value == 0 && data == 0) {
			     	 	$("#pbar_total_massive_table").hide();
			     	 } else {
			     	 	$("#pbar_total_massive_table").show();
			     	 	
			     	 	$(".pbar").width($("#pbar_total_massive").width());
				    	$(".pbar").height($("#pbar_total_massive").height());
			     	 }
			     	 <%
						for (int j=0;(listaMassiva != null) && (j<listaMassiva.size());j++) {
					    	%>pbar("<%=listaMassiva.get(j)%>");<%
						}
					 %>
					 
					 if (porcentajeTotal == 100) {
					 	if (interval_pbar_total_massive != null) {
			     	 		window.clearInterval(interval_pbar_total_massive);
			     	 	}
			     	 }
				 }
			 );
		}
						
		function pbar(barra){
		    $.post("/helium/expedient/refreshBarExpedientMassiveAct.html",
		    { idExp: barra },
		     function(data){
		     	 var value = $( "#pbar_"+barra ).progressbar( "option", "value" );
		     	 if (value < data) {
		     	 	$( "#pbar_"+barra ).progressbar( "option", "value", data );
		     	 	$( "#pbar_tr_"+barra ).show();
		     	 }
			});
		}   	 
     	
     	if (interval_pbar_total_massive == null) {
			interval_pbar_total_massive = setInterval("pbar_total_massive()", delayBar);
		}
	<% } %>
	}
</script>
