<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://displaytag.sf.net/el" prefix="display" %>

<div id="footerSlideContainer">
	<div id="footerSlideContent">
		<div id="footerSlideText">
			<table id="pbar_total_massive_table_bars">
				<%
					List<Long> listaMassiva = (List<Long>) session.getAttribute( "consultaExpedientsIdsMassiusActives" );
					List<String> listaMassivaText = (List<String>) session.getAttribute( "consultaExpedientsTextsMassiusActives" );
					
					for (int j=0;(listaMassiva != null) && (j<listaMassiva.size());j++) {		
				    	%>
				    		<tr id="pbar_tr_<%=listaMassiva.get(j)%>">
				    			<td class="td_texto">
				    				<%=listaMassivaText.get(j)%>
				    			</td>
				    			<td class="td_barra">
				    				<div class="pbar" id="pbar_<%=listaMassiva.get(j)%>"></div>
				    			</td>
				    			<td>
				    				<img id="pbar_total_massive_icon" src="/helium/img/magnifier.png" onClick="mostrarDetalleExpediente('<%=listaMassiva.get(j)%>')" style="cursor: pointer"/>
				    			</td>
				    		</tr>
				    		<script>
				    			if ($.isFunction($.fn.progressbar)) {
				    				$("#pbar_<%=listaMassiva.get(j)%>").progressbar({ value: 0 });
				    				$("#pbar_tr_<%=listaMassiva.get(j)%>").hide();
				    			}
				    		</script>
				    	<%
					}
				%>
			</table>
			<table id="pbar_total_massive_detail" style="display: none">
				<tr>
					<td>
						<div id="div_progressBarMassiveDetail"/>
					</td>
				</tr>
				<tr>
					<td style="text-align: right; padding-right: 8px">
						<button onClick="$('#pbar_total_massive_detail').hide();$('#pbar_total_massive_table_bars').show();" value="submit" name="submit" class="submitButton" type="button"><fmt:message key='comuns.tornar' /></button>
					</td>
				</tr>
			</table>
		</div>
	</div>
</div>

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
		var timeRefresh = 1000;
		$("#pbar_total_massive").progressbar({ value: 0 });
		
		var interval_pbar_total_massive; 
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
			     	 }
			     	 <%
						for (int j=0;(listaMassiva != null) && (j<listaMassiva.size());j++) {
					    	%>pbar("<%=listaMassiva.get(j)%>");<%
						}
					 %>
					 
					 if (porcentajeTotal == 100) {
			     	 	 window.clearInterval(interval_pbar_total_massive);
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
     	
		interval_pbar_total_massive = setInterval("pbar_total_massive()", timeRefresh);
	<% } %>
	}
</script>
