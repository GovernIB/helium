<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${empty expedientTipusCommand.id}"><
		<c:set var="titol"><spring:message code="expedient.tipus.form.titol.nou"/></c:set>
		<c:set var="formAction">new</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.tipus.form.titol.modificar"/></c:set>
		<c:set var="formAction">update</c:set>
	</c:otherwise>
</c:choose>

<html>
<head>
	<title>${titol}</title>
	
	
</head>
<body>		
	<form:form cssClass="form-horizontal" action="${formAction}" enctype="multipart/form-data" method="post" commandName="expedientTipusCommand">
		<div class="inlineLabels">
        
			<script type="text/javascript">
				// <![CDATA[
				var esborrarMsg = "<spring:message code='expedient.tipus.form.boto.esborrar' />";
				
				$(document).ready( function() {
					
					$('#reiniciarCadaAny').change(function() {
						canviReiniciar();
					})
					
					$('#ambInfoPropiaNota').insertAfter('#ambInfoPropia');
					
				}); 

				function add() {
					var nFiles = $("#seqs > tbody > tr").length;
					var classe = "odd";
					if (nFiles % 2 == 1) classe = "even"; 
					var nouDiv =	"<tr class='" + classe + "'>\n" +
									"	<td><input type='text' style='text-align:right; width: 100%;' class='form-control' value='' name='sequenciesAny' id='seqany_" + nFiles + "'></td>\n" +
									"	<td><input type='text' style='text-align:right; width: 100%;' class='form-control' value='' name='sequenciesValor' id='seqseq_" + nFiles + "'></td>\n" +
									"	<td style='width:16px'><a onclick='removeSeq(" + nFiles +")' href='javascript:void(0)'><img border='0' src='/helium/img/cross.png'></a></td>\n" +
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
					if ($("#reiniciarCadaAny").is(':checked')) {
						$("#seqUnica").css("display", "none");
						$("#seqMultiple").css("display", "inline");
					} else {
						$("#seqUnica").css("display", "inline");
						$("#seqMultiple").css("display", "none");
					}
				}				
				
				// ]]>
			</script>			
			<hel:inputText required="true" name="codi" textKey="expedient.tipus.form.camp.codi" disabled="${! empty expedientTipusCommand.id}"/>
			<hel:inputText required="true" name="nom" textKey="expedient.tipus.form.camp.titol" />
			<hel:inputCheckbox name="ambInfoPropia" textKey="expedient.tipus.form.camp.ambInfoPropia" />
			<p id="ambInfoPropiaNota" class="help-block"><spring:message code="expedient.tipus.form.camp.ambInfoPropia.nota"></spring:message></p>			
			<hel:inputCheckbox name="teTitol" textKey="expedient.tipus.form.camp.teTitol" />
			<hel:inputCheckbox name="demanaTitol" textKey="expedient.tipus.form.camp.demanaTitol" />
			<hel:inputCheckbox name="teNumero" textKey="expedient.tipus.form.camp.teNumero" />
			<hel:inputCheckbox name="demanaNumero" textKey="expedient.tipus.form.camp.demanaNumero" />
			<hel:inputText name="expressioNumero" textKey="expedient.tipus.form.camp.expressioNumero" />
			<hel:inputCheckbox name="reiniciarCadaAny" textKey="expedient.tipus.form.camp.reiniciarCadaAny" />
			<div id="seqUnica" <c:if test="${expedientTipusCommand.reiniciarCadaAny}">style="display:none;"</c:if>>
				<hel:inputText name="sequencia" textKey="expedient.tipus.form.camp.sequencia" />
			</div>			
			<div 	id="seqMultiple" 
					class="form-group"
					<c:if test="${not expedientTipusCommand.reiniciarCadaAny}">style="display:none;"</c:if>>
					<label class="control-label col-xs-4" for="sequenciesAny"><spring:message code="expedient.tipus.form.camp.reiniciarCadaAny.seq_actuals"></spring:message></label>
					<div class="controls col-xs-8">					
			 			<div class="multiField" style="overflow:auto;">
							<% 
								String[] classes = {"odd", "even"}; 
								int i = 0; 
							%>
							<table id="seqs" width="100%" style="border-collapse: separate; border-spacing: 5px;">
								<thead>
									<tr>
										<th width="45%"><spring:message code="expedient.tipus.form.camp.reiniciarCadaAny.any"/></th>
										<th width="45%"><spring:message code="expedient.tipus.form.camp.reiniciarCadaAny.sequencia"/></th>
										<th width="10%"></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="any" items="${expedientTipusCommand.sequenciesAny}" varStatus="status">
									<tr class="<%=classes[i%2]%>">
										<td><input type="text" class="form-control" style="text-align:right; width: 100%;" value="${expedientTipusCommand.sequenciesAny[status.index]}" name="sequenciesAny" id="seqany_<%=i%>"></td>
										<td><input type="text" class="form-control" style="text-align:right; width: 100%;" value="${expedientTipusCommand.sequenciesValor[status.index]}" name="sequenciesValor" id="seqseq_<%=i%>"></td>
										<td style="width:16px"><a onclick="removeSeq(<%=i++%>)" href="javascript:void(0)"><img border="0" title="<spring:message code='expedient.tipus.form.boto.esborrar' />" alt="<spring:message code='expedient.tipus.form.boto.esborrar' />" src="/helium/img/cross.png"></a></td>
									</tr>
									</c:forEach>
								</tbody>
							</table>
							<button onclick="add()" class="btn btn-primary submitButton" type="button">
								<span class="fa fa-plus"></span> <spring:message code="expedient.tipus.form.boto.afegir"/>
							</button>							
						</div>

					</div>
			</div>						
			</div>
			<hel:inputSuggest inline="false" name="responsableDefecteCodi" urlConsultaInicial="/helium/v3/tasca/persona/suggestInici" urlConsultaLlistat="/helium/v3/tasca/persona/suggest" textKey="expedient.tipus.form.camp.responsableDefecteCodi" placeholderKey="expedient.tipus.form.camp.responsableDefecteCodi"/>
			<hel:inputCheckbox name="restringirPerGrup" textKey="expedient.tipus.form.camp.restringirPerGrup" />
			<hel:inputCheckbox name="seleccionarAny" textKey="expedient.tipus.form.camp.seleccionarAny" />
			<c:if test="${potDissenyar}">
				<hel:inputCheckbox name="ambRetroaccio" textKey="expedient.tipus.form.camp.ambRetroaccio" />
				<hel:inputCheckbox name="reindexacioAsincrona" textKey="expedient.tipus.form.camp.reindexacioAsincrona" />
			</c:if>
		</div>
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default" data-modal-cancel="true">
				<spring:message code="comu.boto.cancelar"/>
			</button>
			<button type="submit" class="btn btn-success right">
				<span class="fa fa-save"></span> <spring:message code="comu.boto.guardar"/>
			</button>
		</div>
	</form:form>
</body>
</html>