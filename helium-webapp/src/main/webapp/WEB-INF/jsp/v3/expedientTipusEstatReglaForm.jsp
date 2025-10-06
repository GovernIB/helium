<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<c:choose>
	<c:when test="${empty estatReglaCommand.id}"><c:set var="titol"><spring:message code="expedient.tipus.regla.form.titol.nou"/></c:set></c:when>
	<c:otherwise><c:set var="titol"><spring:message code="expedient.tipus.regla.form.titol.modificar"/></c:set></c:otherwise>
</c:choose>
<html>
<head>
	<title>${titol}</title>
	<meta name="title" content="${titol}"/>
	<link href="<c:url value="/webjars/select2/4.0.1/dist/css/select2.min.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/webjars/select2-bootstrap-theme/0.1.0-beta.4/dist/select2-bootstrap.min.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/select2.min.js"/>"></script>
	<script src="<c:url value="/webjars/select2/4.0.1/dist/js/i18n/${idioma}.js"/>"></script>
	<script>
		$(document).ready(function() {
			configQuiValor("${estatReglaCommand.qui}");

			$("#queValor").select2({
				theme: "bootstrap",
				placeholder: "<spring:message code="expedient.tipus.regla.form.camp.que.valors"/>",
				minimumResultsForSearch: 5
			});

			$("#qui").change((e) => {
				updateQui($(e.currentTarget).val());
			});
			$("#que").change((e) => {
				updateQue($(e.currentTarget).val());
			});

			let qui = $("#qui").val();
			if (qui === 'TOTHOM' || qui === '') {
				$("#quiValor").prop("disabled", true);
			}

			let que = $("#que").val();
			if (que === 'TOT' || que === 'DADES' || que === 'DOCUMENTS' || que === 'TERMINIS' || que === '') {
				$("#queValor").prop("disabled", true);
			}
			
			const $accio = $("#accio");
			const valoresObligatorios = ["REQUERIR", "REQUERIR_ENTRAR"];
			
		    const checkAccio = () => {
				const val = $accio.val();
		    	if (valoresObligatorios.includes($accio.val())) {
		            $("#accioWarning").show();
		        } else {
		            $("#accioWarning").hide();
		        }
		    };

		    $accio.change(checkAccio);
		    checkAccio(); // executem en carregar la pàgina (per si està en edició)
		});

		const updateQui = (qui) => {
			$("#quiValor").val(null).trigger('change');
			$("#quiValor option").each(function(){
				$(this).remove();
			});
			$("#quiValor").select2("destroy");
			configQuiValor(qui);

			if (qui === 'TOTHOM' || qui === '') {
				$("#quiValor").prop("disabled", true);
			} else {
				$("#quiValor").prop("disabled", false);
			}
		}
		const configQuiValor = (qui) => {
			if (qui !== 'USUARI') {
				$("#quiValor").select2({
					theme: "bootstrap",
					placeholder: "<spring:message code="expedient.tipus.regla.form.camp.qui.valors"/>",
					minimumResultsForSearch: 5,
					tags: true,
					tokenSeparators: [',', ' ']
				});
			} else {
				$("#quiValor").select2({
					theme: "bootstrap",
					placeholder: "<spring:message code="expedient.tipus.regla.form.camp.qui.valors"/>",
					minimumInputLength: 3,
					ajax: {
						url: (params) => { return "<c:url value="/v3/expedientTipus/persona/suggest/"/>" + params.term; },
						dataType: 'json',
						processResults: (data) => {
							let results = [];
							data.forEach(d => results.push({id: d.codi + " | " + d.nom, text: d.codi + " | " + d.nom}));
							return { results: results };
						}
					}
				});
			}
		}
		const updateQue = (que) => {
			if (que === 'TOT' || que === 'DADES' || que === 'DOCUMENTS' || que === 'TERMINIS' || que === '') {
				$("#queValor").val(null).trigger('change');
				$("#queValor").prop("disabled", true);
			} else {
				$("#queValor").prop("disabled", false);
				ompleQueVariables(que);
			}
		}
		const ompleQueVariables = (que) => {
			let queValorPare = $("#queValor").parent();
			valorsLoadingStart(queValorPare);

			let getUrl = '';
			if (que === 'DADA') {
				getUrl = '<c:url value="/v3/expedientTipus/${estatReglaCommand.expedientTipusId}/var/select"/>';
			} else if (que === 'DOCUMENT') {
				getUrl = '<c:url value="/v3/expedientTipus/${estatReglaCommand.expedientTipusId}/doc/select"/>';
			} else if (que === 'TERMINI') {
				getUrl = '<c:url value="/v3/expedientTipus/${estatReglaCommand.expedientTipusId}/term/select"/>';
			} else if (que === 'AGRUPACIO') {
				getUrl = '<c:url value="/v3/expedientTipus/${estatReglaCommand.expedientTipusId}/agrup/select"/>';
			} else {
				return;
			}

			$.ajax({
				type: 'GET',
				url: getUrl,
				dataType: "json",
				async: true,
				success: function(data) {
					$("#queValor option").each(function(){
						$(this).remove();
					});
					for (let i = 0; i < data.length; i++) {
						$("#queValor").append($("<option>" + data[i].codi + " | " + data[i].valor + "</option>"));
					}
					$("#queValor").val('').change();
					valorsLoadingStop(queValorPare);
				},
				error: function(e) {
					console.log("Error obtenint dades: " + e);
					valorsLoadingStop(queValorPare);
				}
			});
		}
		const valorsLoadingStart = (el) => {
			$("<span class='fa fa-circle-o-notch fa-spin valors-load'></span>").appendTo($(".select2-container", el));
		}
		const valorsLoadingStop = (el) => {
			el.find(".valors-load").remove();
		}
		
		
	</script>
	<style>
		.text-left { text-align: left !important; }
		.select2-container { width: 100% !important; }
		.valors-load { position: absolute; font-size: 20px; right: 10px; top: 8px; color: cornflowerblue; }
	</style>
	<hel:modalHead/>
</head>
<body>
	<form:form action="" method="post" cssClass="form-horizontal" commandName="estatReglaCommand">
		<form:hidden path="id"/>
		<form:hidden path="expedientTipusId"/>
		<form:hidden path="estatId"/>

		<div class="row">
			<div class="col-xs-12">
				<hel:inputText name="nom" textKey="expedient.tipus.regla.form.camp.nom"  labelSize="2" required="true"/>
			</div>
		</div>
		<hr/>
		<div class="row">
			<div class="col-xs-4">
				<hel:inputSelect name="qui" textKey="expedient.tipus.regla.form.camp.qui" emptyOption="true" optionItems="${quiOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" labelSize="6" required="true"/>
			</div>
			<div class="col-xs-8">
				<div id="notUser">
					<c:set var="quiErrors"><form:errors path="quiValor"/></c:set>
					<div class="form-group<c:if test="${not empty quiErrors}"> has-error</c:if>">
						<div class="controls col-xs-12">
							<form:select path="quiValor" cssClass="form-control" id="quiValor" multiple="multiple">
								<form:options items="${estatReglaCommand.quiValor}"/>
							</form:select>
							<c:if test="${not empty quiErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="quiValor"/></p></c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-4">
				<hel:inputSelect name="que" textKey="expedient.tipus.regla.form.camp.que" emptyOption="true" optionItems="${queOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" labelSize="6" required="true"/>
			</div>
			<div class="col-xs-8">
				<c:set var="queErrors"><form:errors path="queValor"/></c:set>
				<div class="form-group<c:if test="${not empty queErrors}"> has-error</c:if>">
					<div class="controls col-xs-12">
						<form:select path="queValor" cssClass="form-control" id="queValor" multiple="multiple">
							<form:options items="${valorsQue}"/>
						</form:select>
						<c:if test="${not empty queErrors}"><p class="help-block"><span class="fa fa-exclamation-triangle"></span>&nbsp;<form:errors path="queValor"/></p></c:if>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-12">
				<hel:inputSelect name="accio" textKey="expedient.tipus.regla.form.camp.accio" emptyOption="true" optionItems="${accioOptions}" optionValueAttribute="value" optionTextKeyAttribute="text" labelSize="2" required="true"/>
				<div id="accioWarning" style="margin-top:-10px; display:none;">
					<label class="control-label col-xs-2 hiddenInfoContainer"></label>
					<div class="col-xs-10">
						<span class="text-primary">
							<span class="fa fa-info-circle" style="margin-right: 8px;"></span>
							<spring:message code="expedient.tipus.regla.form.camp.accio.warning"/>
						</span>
					</div>
            		
        		</div>
			</div>
		</div>
		<div style="min-height: 150px;"></div>
		<div id="modal-botons">
			<button type="submit" class="btn btn-success"><span class="fa fa-save"></span>&nbsp;<spring:message code="comu.boto.guardar"/></button>
			<a href="<c:url value="/v3/expedientTipus/${expedientTipus.id}/permis"/>" class="btn btn-default" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></a>
		</div>
	</form:form>
</body>
</html>
