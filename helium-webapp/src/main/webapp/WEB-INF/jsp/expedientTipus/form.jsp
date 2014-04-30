<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
	<title><c:choose><c:when test="${empty command.id}"><fmt:message key="expedient.tipus.form.crear_nou"/></c:when><c:otherwise><fmt:message key="expedient.tipus.form.crear_nou"/></c:otherwise></c:choose></title>
	<meta name="titolcmp" content="<fmt:message key="comuns.disseny"/>"/>
	<c:import url="../common/formIncludes.jsp"/>
	
	<script type="text/javascript">
		var borrar = "<fmt:message key="comuns.esborrar"/>";
		function add() {
			var nFiles = $("#seqs > tbody > tr").length;
			var classe = "odd";
			if (nFiles % 2 == 1) classe = "even"; 
			var nouDiv =	"<tr class='" + classe + "'>\n" +
							"	<td><input type='text' style='text-align:right; width: 100%;' class='textInput' value='' name='seqany' id='seqany_" + nFiles + "'></td>\n" +
							"	<td><input type='text' style='text-align:right; width: 100%;' class='textInput' value='' name='seqseq' id='seqseq_" + nFiles + "'></td>\n" +
							"	<td style='width:16px'><a onclick='removeSeq(" + nFiles +")' href='javascript:void(0)'><img border='0' title='" + borrar + "' alt='" + borrar + "' src='/helium/img/cross.png'></a></td>\n" +
							"</tr>\n";
			$("#seqs").append(nouDiv);
    	}
		
		function removeSeq(pos) {
			$("#seqs > tbody > tr").eq(pos).remove();
			var i = 0;
			$("#seqs > tbody > tr").each(function() {
				if (i % 2 == 0) {
					$(this).attr('class', 'odd');
				} else {
					$(this).attr('class', 'even');
				}
				$(this).find("td:nth-child(3) > a").attr("onclick", "removeSeq(" + i + ")");
				i++;
			});
		}
		
		function canviReiniciar() {
			if ($("#reiniciarCadaAny0").is(':checked')) {
				$("#seqUnica").css("display", "none");
				$("#seqMultiple").css("display", "");
			} else {
				$("#seqUnica").css("display", "");
				$("#seqMultiple").css("display", "none");
			}
		}
		
		function validateSeqAny() {
			var valid = true;
			$("#seqs > tbody > tr").each(function() {
				
				var any = parseInt($(this).find("td:first-child").find("input").val(), 10);
				var seq = parseInt($(this).find("td:nth-child(2)").find("input").val(), 10);
				
				if (isNaN(any) || isNaN(seq)) {
					if (!$("#seqMultiple").hasClass('error')) {
						$("#seqMultiple").addClass("error");
						$("#seqMultiple").prepend("<p class='errorField'><strong><span id='sequencia.errors'><fmt:message key='error.seq.any'/></span></strong></p>");
					}
					valid = false;
				}
			});
			return valid;
		}
		
		$(document).ready(function() {
		    $("form").submit(function(e) {
		        if (e.originalEvent.type == "submit") {
		            if (validateSeqAny()) {
		                return true;
		            } else {
		                e.preventDefault();
		                return false;
		            }
		        }
		    });
		   
		    return;
		});
</script>
</head>
<body>

	<form:form action="form.html" cssClass="uniForm">
		<div class="inlineLabels">
			<c:if test="${not empty command.id}"><form:hidden path="id"/></c:if>
			<c:choose>
				<c:when test="${empty command.id}">
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="codi"/>
						<c:param name="required" value="true"/>
						<c:param name="label"><fmt:message key="expedient.tipus.form.codi"/></c:param>
					</c:import>
				</c:when>
				<c:otherwise>
					<c:import url="../common/formElement.jsp">
						<c:param name="property" value="codi"/>
						<c:param name="type" value="static"/>
						<c:param name="required" value="true"/>
						<c:param name="label"><fmt:message key="expedient.tipus.form.codi"/></c:param>
					</c:import>
				</c:otherwise>
			</c:choose>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="nom"/>
				<c:param name="required" value="true"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.titol"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="teTitol"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.amb_titol"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="demanaTitol"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.demana_titol"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="teNumero"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.amb_num"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="demanaNumero"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.demana_num"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="expressioNumero"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.expressio"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="reiniciarCadaAny"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.seq_reiniciar"/></c:param>
				<c:param name="onchange" value="canviReiniciar()"/>
			</c:import>
			<div id="seqUnica" <c:if test="${command.reiniciarCadaAny}">style="display:none;"</c:if>>
				<c:import url="../common/formElement.jsp">
					<c:param name="property" value="sequencia"/>
					<c:param name="label"><fmt:message key="expedient.tipus.form.seq_actual"/></c:param>
				</c:import>
			</div>
			<div id="seqMultiple" class="ctrlHolder" <c:if test="${not command.reiniciarCadaAny}">style="display:none;"</c:if>>
	 			<p class="label"><fmt:message key="expedient.tipus.form.seq_actuals"/></p>
	 			<div class="multiField" style="overflow:auto;">
					<% 
						String[] classes = {"odd", "even"}; 
						int i = 0; 
					%>
					<table id="seqs" class="displaytag">
						<thead>
							<tr>
								<th><fmt:message key="expedient.tipus.form.any"/></th>
								<th><fmt:message key="expedient.tipus.form.sequencia"/></th>
								<th></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="any" items="${command.sequenciaAny}">
							<tr class="<%=classes[i%2]%>">
								<td><input type="text" style="text-align:right; width: 100%;" class="textInput" value="${any.key}" name="seqany" id="seqany_<%=i%>"></td>
								<td><input type="text" style="text-align:right; width: 100%;" class="textInput" value="${any.value.sequencia}" name="seqseq" id="seqseq_<%=i%>"></td>
								<td style="width:16px"><a onclick="removeSeq(<%=i++%>)" href="javascript:void(0)"><img border="0" title="Esborrar" alt="Esborrar" src="/helium/img/cross.png"></a></td>
							</tr>
							</c:forEach>
						</tbody>
					</table>
					<button onclick="add()" class="submitButton" type="button" style="font-size:11px;margin-top: 2px"><fmt:message key="comuns.afegir"/></button>
				</div>
			</div>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="responsableDefecteCodi"/>
				<c:param name="type" value="suggest"/>
				<c:param name="label">Responsable per defecte</c:param>
				<c:param name="suggestUrl"><c:url value="/persona/suggest.html"/></c:param>
				<c:param name="suggestText">${responsableDefecte.nomSencer}</c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="restringirPerGrup"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.restringir_grup"/></c:param>
			</c:import>
			<c:import url="../common/formElement.jsp">
				<c:param name="property" value="seleccionarAny"/>
				<c:param name="type" value="checkbox"/>
				<c:param name="label"><fmt:message key="expedient.tipus.form.seleccionar_any"/></c:param>
			</c:import>
		</div>
		<c:import url="../common/formElement.jsp">
			<c:param name="type" value="buttons"/>
			<c:param name="values">submit,cancel</c:param>
			<c:param name="titles"><c:choose><c:when test="${empty command.id}"><fmt:message key='comuns.crear' />,<fmt:message key='comuns.cancelar' /></c:when><c:otherwise><fmt:message key='comuns.modificar' />,<fmt:message key='comuns.cancelar' /></c:otherwise></c:choose></c:param>
<%-- 			<c:param name="onclick" value="validateSeqAny(event)"/> --%>
		</c:import>
	</form:form>

	<p class="aclaracio"><fmt:message key='comuns.camps_marcats' /> <img src="<c:url value="/img/bullet_red.png"/>" alt="<fmt:message key='comuns.camp_oblig' />" title="<fmt:message key='comuns.camp_oblig' />" border="0"/> <fmt:message key='comuns.son_oblig' /></p>

</body>
</html>
