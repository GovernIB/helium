<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="numColumnes" value="${3}"/>
<c:set var="hiHaDadesReadOnly" value="${false}"/>
<c:set var="countReadOnly" value="${0}"/>
<c:forEach var="dada" items="${dades}">
	<c:if test="${dada.readOnly}"><c:set var="countReadOnly" value="${countReadOnly + 1}"/><c:set var="hiHaDadesReadOnly" value="${true}"/></c:if>
</c:forEach>
<c:if test="${hiHaDadesReadOnly}">
	<c:import url="import/expedientDadesTaula.jsp">
		<c:param name="dadesAttribute" value="dades"/>
		<c:param name="titol" value="Dades de referència"/>
		<c:param name="numColumnes" value="${3}"/>
		<c:param name="count" value="${countReadOnly}"/>
		<c:param name="condicioCamp" value="readOnly"/>
	</c:import>
</c:if>
<html>
<head>
	<title>Tasca</title>
	<script type="text/javascript" src="<c:url value="/js/jquery.keyfilter.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<link href="<c:url value="/css/datepicker.css"/>" rel="stylesheet">
	<script src="<c:url value="/js/bootstrap-datepicker.js"/>"></script>
	<script src="<c:url value="/js/locales/bootstrap-datepicker.ca.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.maskedinput.js"/>"></script>
</head>
<body>
<ul class="nav nav-tabs">
	<li class="active"><a href="#home" data-toggle="tab">1. Dades</a></li>
	<li class="disabled"><a href="#profile" data-toggle="tab">2. Documents</a></li>
	<li class="disabled"><a href="#settings" data-toggle="tab">3. Signatures</a></li>
</ul>
<form class="form-horizontal form-tasca">
	<c:forEach var="dada" items="${dades}">
		<c:if test="${not dada.readOnly}">
			<div class="control-group">
				<label class="control-label" for="${dada.varCodi}">${dada.campEtiqueta} (${dada.campTipus})</label>
				<c:choose>
					<c:when test="${dada.campTipus == 'INTEGER'}">
						<div class="controls">
							<input type="text" id="${dada.varCodi}" value="${dada.text}" class="input-xlarge" style="text-align:right"/>
							<script>
								$("#${dada.varCodi}").keyfilter(/^[-+]?[0-9]*$/);
							</script>
						</div>
					</c:when>
					<c:when test="${dada.campTipus == 'FLOAT'}">
						<div class="controls">
							<input type="text" id="${dada.varCodi}" value="${dada.text}" class="input-xlarge" style="text-align:right"/>
							<script>
								$("#${dada.varCodi}").keyfilter(/^[-+]?[,0-9]*$/);
							</script>
						</div>
					</c:when>
					<c:when test="${dada.campTipus == 'PRICE'}">
						<div class="controls">
							<input type="text" id="${dada.varCodi}" value="${dada.text}" class="input-xlarge" style="text-align:right" value="-12345"/>
							<script>
								$("#${dada.varCodi}").priceFormat({
									prefix: '',
									centsSeparator: ',',
								    thousandsSeparator: '.',
								    allowNegative: false
								});
							</script>
						</div>
					</c:when>
					<c:when test="${dada.campTipus == 'DATE'}">
						<div class="controls">
							<div class="span5 input-append date datepicker">
								<input type="text" id="${dada.varCodi}" value="${dada.text}" class="input-xlarge" placeholder="dd/mm/yyyy"/>
								<span class="add-on"><i class="icon-calendar"></i></span>
							</div>
							<script>
								$("#${dada.varCodi}").mask("99/99/9999");
								$("#${dada.varCodi}").datepicker({language: 'ca', autoclose: true});
							</script>
						</div>
					</c:when>
					<c:when test="${dada.campTipus == 'BOOLEAN'}">
						<div class="controls">
							<input type="checkbox" id="${dada.varCodi}"<c:if test="${dada.varValor}"> checked="checked"</c:if>/>
						</div>
					</c:when>
					<c:when test="${dada.campTipus == 'SELECCIO'}">
						<div class="controls">
							<select id="${dada.varCodi}" class="input-xlarge"></select>
							<script>
								$.ajax({
								    url: 'camp/${dada.campId}/valorsSeleccio',
								    type: 'GET',
								    dataType: 'json',
								    success: function(json) {
								        $.each(json, function(i, value) {
								        	if (value.codi == '${dada.varValor}')
								        		$('select#${dada.varCodi}').append($('<option>').text(value.text).attr('value', value.codi).attr('selected', 'selected'));
								        	else
								        		$('select#${dada.varCodi}').append($('<option>').text(value.text).attr('value', value.codi));
								        });
								    }
								});
							</script>
						</div>
					</c:when>
					<c:otherwise>
						<div class="controls">
							<input type="text" id="${dada.varCodi}" value="${dada.text}" class="input-xlarge"/>
						</div>
					</c:otherwise>
				</c:choose>
			</div>
		</c:if>
	</c:forEach>
	<%--div class="form-actions">
		<c:forEach var="transicio" items="${tasca.transicions}">
			<button class="btn btn-primary">${transicio}</button>
		</c:forEach>
		<c:if test="${tasca.transicioPerDefecte}">
			<button class="btn btn-primary">Finalitzar</button>
		</c:if>
	</div--%>
</form>
<script>
	window.parent.canviTitolModal("${tasca.titol}");
	var transicions = new Array();
	var texts = new Array();
	var accions = new Array();
	<c:forEach var="transicio" items="${tasca.transicions}">transicions.push('${transicio}');texts.push('${transicio}');</c:forEach>
	<c:if test="${tasca.transicioPerDefecte}">transicions.push('default');texts.push('Finalitzar');</c:if>
	window.parent.substituirBotonsPeuModal(transicions, texts);
	function test(codi) {
		return confirm('Estau segur que voleu donar aquesta tasca per finalitzada?');
	}
</script>
</body>
