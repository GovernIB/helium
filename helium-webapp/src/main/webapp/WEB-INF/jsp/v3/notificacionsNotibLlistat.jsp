<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>
<html>
<head>
	<title><spring:message code="notificacio.llistat.titol"/></title>
	<meta name="title" content="<spring:message code='notificacio.llistat.titol'/>"/>
	<meta name="screen" content="notificacionsNotib">
	<meta name="title-icon-class" content="fa fa-envelope"/>
	<script src="<c:url value="/webjars/datatables.net/1.10.13/js/jquery.dataTables.min.js"/>"></script>
	<script src="<c:url value="/webjars/datatables.net-bs/1.10.13/js/dataTables.bootstrap.min.js"/>"></script>
	<script src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>	
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">
	<link href="<c:url value="/webjars/datatables.net-bs/1.10.13/css/dataTables.bootstrap.min.css"/>" rel="stylesheet"></link>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_ca.js"/>"></script>	
	<script src="<c:url value="/js/jsrender.min.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<script src="<c:url value="/js/webutil.datatable.js"/>"></script>
	<script src="<c:url value="/js/webutil.modal.js"/>"></script>	
</head>
<body>
	<form:form action="" method="post" cssClass="well" commandName="notificacioFiltreCommand">
	
	<div class="row">
		<div class="col-md-3">
			<hel:inputSelect name="expedientTipusId" textKey="notificacio.llistat.filtre.camp.expedientTipus"
				optionItems="${expedientsTipus}" optionValueAttribute="codi" emptyOption="true"
				inline="true" placeholderKey="notificacio.llistat.filtre.camp.expedientTipus" optionTextAttribute="valor" />
		</div>
		 <div class="col-md-3">							
			<hel:inputText name="expedientNumero" textKey="notificacio.llistat.filtre.camp.expedient" placeholderKey="notificacio.llistat.filtre.camp.expedient" inline="true"/>
		</div>		
		<div class="col-md-2">
			<hel:inputDate name="dataInicial" textKey="notificacio.llistat.filtre.camp.dataInicial" placeholderKey="consultes.potafib.camp.filtreDesde" inline="true"/>
		</div>	
		<div class="col-md-2">
			<hel:inputDate name="dataFinal" textKey="notificacio.llistat.filtre.camp.dataFinal" placeholderKey="consultes.potafib.camp.filtreFins" inline="true"/>
		</div>	
		<div class="col-md-2">
			<hel:inputText name="interessat" textKey="notificacio.llistat.filtre.camp.interessat" placeholderKey="notificacio.llistat.filtre.camp.interessat" inline="true"/>
		</div>
	</div>
		
	<div class="row">
		<div class="col-md-2">
			<hel:inputSelect inline="true" name="tipus" optionItems="${tipusEnviaments}"
			 emptyOption="true" 
			 textKey="notificacio.llistat.filtre.camp.tipusEnviament" 
			 placeholderKey="notificacio.llistat.filtre.camp.tipusEnviament" 
			 optionValueAttribute="codi" 
			 optionTextAttribute="valor"/>
		</div>
		<div class="col-md-2">							
			<hel:inputText name="concepte" textKey="notificacio.llistat.filtre.camp.concepte" placeholderKey="notificacio.llistat.filtre.camp.concepte" inline="true"/>
		</div>
		<div class="col-md-2">
			<hel:inputSelect inline="true" name="estat" optionItems="${estats}" emptyOption="true" textKey="notificacio.llistat.filtre.camp.estat" placeholderKey="notificacio.llistat.filtre.camp.estat" optionValueAttribute="codi" optionTextAttribute="valor"/>
		</div>		
		<div class="col-md-2">
			<hel:inputText name="nomDocument" textKey="notificacio.llistat.filtre.camp.document.nom" placeholderKey="notificacio.llistat.filtre.camp.document.nom" inline="true"/>
		</div>	
		<div class="col-md-2">
			<hel:inputSuggest 
					name="unitatOrganitzativaCodi" 
					urlConsultaInicial="/helium/v3/unitatOrganitzativa/suggestInici" 
					urlConsultaLlistat="/helium/v3/unitatOrganitzativa/suggest" 
					placeholderKey="notificacio.llistat.filtre.camp.organ.emissor"
					inline="true"
					/>	
		</div>	

		<div class="col-md-2 pull-right">
			<div class="pull-right">
					<button id="consultarHidden" type="submit" name="accio" value="consultar" class="btn btn-primary hidden"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
					<button id="netejar" type="submit" name="accio" value="netejar" class="btn btn-default"><spring:message code="comu.filtre.netejar"/></button>
					<button id="consultar" type="submit" name="accio" value="consultar" class="btn btn-primary"><span class="fa fa-filter"></span>&nbsp;<spring:message code="comu.filtre.filtrar"/></button>
			</div>		
		</div>		
	</div>
	
	</form:form>
	
	<table	id="notificacionsNotib"
			data-toggle="datatable"
			data-url="notificacionsNotib/datatable"
			data-filter="#notificacioFiltreCommand"
			data-paging-enabled="true"
			data-ordering="true"
			data-default-order="2"
			data-default-dir="desc"	
			data-info-type="button"
			class="table table-striped table-bordered table-hover">
		<thead>
			<tr>
				<th data-col-name="expedientTipusNom" data-template="#cellTipusExpTemplate" width="10%">
					<spring:message code="notificacio.llistat.filtre.camp.expedientTipus"/>
					<script id="cellTipusExpTemplate" type="text/x-jsrender">
						{{:expedientTipusCodi}} <span class="fa fa-info-circle" title="{{:expedientTipusCodi}} - {{:expedientTipusNom}}
(Entorn {{:entornCodi}} - {{:entornNom}})"></span>
					</script>
				</th>
				<th data-col-name="expedient.identificador" data-template="#cellExpTemplate" width="20%"><spring:message code="consultes.pinbal.camp.exp"/>
					<script id="cellExpTemplate" type="text/x-jsrender">
						<a href="<c:url value="/v3/expedient/{{:expedientId}}"/>" target="_blank">{{:expedient.identificador}}</a>
					</script>
				</th>								
				<th data-col-name="enviatData" data-converter="datetime" width="9%">
					<spring:message code="notificacio.llistat.columna.data.enviament"/>
				</th>
				 <th data-col-name="organEmissorCodiAndNom" data-visible="true" width="10%">
				 	<spring:message code="notificacio.llistat.columna.organ.emissor"/>
				</th>
				<th data-col-name="interessatFullNomNif"><spring:message code="notificacio.llistat.filtre.camp.interessat"/></th>
			 	<th data-col-name="tipus" data-template="#cellTipusTemplate" width="10%">
					<spring:message code="notificacio.llistat.columna.tipus.enviament"/>
					<script id="cellTipusTemplate" type="text/x-jsrender">
						{{if tipus=='NOTIFICACIO'}} 
						<spring:message code="notifica.enviament.tipus.enum.NOTIFICACIO"></spring:message>
						{{/if}}	
						{{if tipus=='COMUNICACIO'}} 
						<spring:message code="notifica.enviament.tipus.enum.COMUNICACIO"></spring:message>
						{{/if}}
					</script>
				</th>
				<th data-col-name="concepte" width="10%"><spring:message code="notificacio.llistat.columna.concepte"/></th>				
				<th data-col-name="nomDocument" data-template="#cellDocTemplate" width="10%">
				<spring:message code="notificacio.llistat.filtre.camp.document.nom"/>
					<script id="cellDocTemplate" type="text/x-jsrender">
						<a href="<c:url value="/v3/expedient/{{:expedientId}}/proces/{{:processInstanceId}}/document/{{:documentStoreId}}/descarregar"/>" 
							title="<spring:message code="expedient.notificacio.descarregar.doc"/> ${arxiuNom}"
							target="_blank">{{:nomDocument}}
							<span class="fa fa-download fa-lg"></span>
						</a>
					</script>
				</th>
			  	<th data-col-name="estat" data-template="#cellEstatTemplate" width="8%">
				<spring:message code="notificacio.llistat.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat == 'PENDENT'}}
							<span class="fa fa-clock-o" title="<spring:message code="notificacio.etst.enum.PENDENT.info"/>"></span>
							<spring:message code="notificacio.etst.enum.PENDENT"></spring:message>
						{{else estat == 'ENVIADA'}}
							<span class="fa fa-send-o" title="<spring:message code="notificacio.etst.enum.ENVIADA.info"/>"></span>
							<spring:message code="notificacio.etst.enum.ENVIADA"></spring:message>
						{{else estat == 'REGISTRADA'}}
							<span class="fa fa-file-o" title="<spring:message code="notificacio.etst.enum.REGISTRADA.info"/>"></span>
							<spring:message code="notificacio.etst.enum.REGISTRADA" ></spring:message>
						{{else}}
						{{else estat == 'FINALITZADA'}}
							<span class="fa fa-check" title="<spring:message code="notificacio.etst.enum.FINALITZADA.info"/>"></span>
							<spring:message code="notificacio.etst.enum.FINALITZADA" ></spring:message>
						{{else}}
						{{else estat == 'PROCESSADA'}}
							<span class="fa fa-check-circle title="<spring:message code="notificacio.etst.enum.PROCESSADA.info"/>""></span>
							<spring:message code="notificacio.etst.enum.PROCESSADA" ></spring:message>
						{{else}}
							{{:estat}}
						{{/if}}
					</script>				
				</th>
				<th data-col-name="enviamentDatatEstat" data-template="#cellEnviamentEstatTemplate" width="9%">
				<spring:message code="expedient.notificacio.estat.enviament"/>
					<script id="cellEnviamentEstatTemplate" type="text/x-jsrender">
						{{if enviamentDatatEstat == 'REGISTRADA'}}
								<spring:message code="notificacio.enviament.estat.enum.REGISTRADA"></spring:message>
						{{else enviamentDatatEstat == 'ENVIADA'}}
								<spring:message code="notificacio.enviament.estat.enum.ENVIADA" ></spring:message>
						{{else enviamentDatatEstat == 'FINALITZADA'}}
								<spring:message code="notificacio.enviament.estat.enum.FINALITZADA" ></spring:message>
						{{else enviamentDatatEstat == 'PROCESSADA'}}
								<spring:message code="notificacio.enviament.estat.enum.PROCESSADA" ></spring:message>
						{{else enviamentDatatEstat == 'NOTIB_PENDENT'}}
								<spring:message code="notificacio.enviament.estat.enum.NOTIB_PENDENT" ></spring:message>
						{{else enviamentDatatEstat == 'NOTIB_ENVIADA'}}
								<spring:message code="notificacio.enviament.estat.enum.NOTIB_ENVIADA" ></spring:message>
						{{else enviamentDatatEstat == 'ABSENT'}}
								<spring:message code="notificacio.enviament.estat.enum.ABSENT" ></spring:message>
						{{else enviamentDatatEstat == 'ADRESA_INCORRECTA'}}
								<spring:message code="notificacio.enviament.estat.enum.ADRESA_INCORRECTA" ></spring:message>
						{{else enviamentDatatEstat == 'DESCONEGUT'}}
								<spring:message code="notificacio.enviament.estat.enum.DESCONEGUT" ></spring:message>
						{{else enviamentDatatEstat == 'ENVIADA_CI'}}
								<spring:message code="notificacio.enviament.estat.enum.ENVIADA_CI" ></spring:message>
						{{else enviamentDatatEstat == 'ENVIADA_DEH'}}
								<spring:message code="notificacio.enviament.estat.enum.ENVIADA_DEH" ></spring:message>
						{{else enviamentDatatEstat == 'ENVIAMENT_PROGRAMAT'}}
								<spring:message code="notificacio.enviament.estat.enum.ENVIAMENT_PROGRAMAT" ></spring:message>
						{{else enviamentDatatEstat == 'ENTREGADA_OP'}}
								<spring:message code="notificacio.enviament.estat.enum.ENTREGADA_OP" ></spring:message>
						{{else enviamentDatatEstat == 'ERROR_ENTREGA'}}
								<spring:message code="notificacio.enviament.estat.enum.ERROR_ENTREGA" ></spring:message>
						{{else enviamentDatatEstat == 'EXPIRADA'}}
								<spring:message code="notificacio.enviament.estat.enum.EXPIRADA" ></spring:message>
						{{else enviamentDatatEstat == 'EXTRAVIADA'}}
								<spring:message code="notificacio.enviament.estat.enum.EXTRAVIADA" ></spring:message>
						{{else enviamentDatatEstat == 'LLEGIDA'}}
								<spring:message code="notificacio.enviament.estat.enum.LLEGIDA" ></spring:message>
						{{else enviamentDatatEstat == 'MORT'}}
								<spring:message code="notificacio.enviament.estat.enum.MORT" ></spring:message>
						{{else enviamentDatatEstat == 'NOTIFICADA'}}
								<spring:message code="notificacio.enviament.estat.enum.NOTIFICADA" ></spring:message>
						{{else enviamentDatatEstat == 'PENDENT'}}
								<spring:message code="notificacio.enviament.estat.enum.PENDENT" ></spring:message>
						{{else enviamentDatatEstat == 'PENDENT_ENVIAMENT'}}
								<spring:message code="notificacio.enviament.estat.enum.PENDENT_ENVIAMENT" ></spring:message>
						{{else enviamentDatatEstat == 'PENDENT_SEU'}}
								<spring:message code="notificacio.enviament.estat.enum.PENDENT_SEU" ></spring:message>
						{{else enviamentDatatEstat == 'PENDENT_CIE'}}
								<spring:message code="notificacio.enviament.estat.enum.PENDENT_DEH" ></spring:message>
						{{else enviamentDatatEstat == 'PENDENT_DEH'}}
								<spring:message code="notificacio.enviament.estat.enum.REBUTJADA" ></spring:message>
						{{else enviamentDatatEstat == 'SENSE_INFORMACIO'}}
								<spring:message code="notificacio.enviament.estat.enum.SENSE_INFORMACIO" ></spring:message>
						{{else enviamentDatatEstat == 'NOTIFICADA'}}
								<spring:message code="notificacio.enviament.estat.enum.NOTIFICADA" ></spring:message>
						{{else enviamentDatatEstat == 'ANULADA'}}
								<spring:message code="notificacio.enviament.estat.enum.ANULADA" ></spring:message>
						{{else enviamentDatatEstat == 'ENVIAT_SIR'}}
								<spring:message code="notificacio.enviament.estat.enum.ENVIAT_SIR" ></spring:message>
						{{else enviamentDatatEstat == 'ENVIADA_AMB_ERRORS'}}
								<spring:message code="notificacio.enviament.estat.enum.ENVIADA_AMB_ERRORS" ></spring:message>
						{{else enviamentDatatEstat == 'FINALITZADA_AMB_ERRORS'}}
								<spring:message code="notificacio.enviament.estat.enum.FINALITZADA_AMB_ERRORS" ></spring:message>
						{{else}}
							{{:enviamentDatatEstat}}
						{{/if}}
						
						<!--<br/><span class="text-muted small">{{:enviamentDatatData}}</span>-->

					</script>	
								
				</th>
 
				<th data-col-name="justificantArxiuNom" data-template="#cellJustificantTemplate" width="8%" data-orderable="false">	
					<spring:message code="notificacio.detall.camp.justificant"/>
					<script id="cellJustificantTemplate" type="text/x-jsrender">
						{{if justificantId != null}}
							<a href="<c:url value="/v3/expedient/{{:expedientId}}/proces/{{:processInstanceId}}/document/{{:justificantId}}/descarregar"/>" 
								title="<spring:message code="expedient.notificacio.descarregar.doc"/> ${arxiuNom}"
								target="_blank">
								<span class="fa fa-download fa-lg" title="{{:justificantArxiuNom}}" ></span>	
							</a>
						{{/if}}
					</script>
					
				</th>
				 
				<th data-col-name="id" width="5%" data-template="#cellAccionsTemplate" data-orderable="false">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown navbar-right">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/v3/notificacionsNotib/{{:id}}/info"/>" data-toggle="modal" class="consultar-expedient"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="consultes.pinbal.boto.info"/></a></li>
								<li><a href="<c:url value="/v3/notificacionsNotib/{{:id}}/consultarEstat"/>" 
									class="consultar-expedient"
									title="<spring:message code="expedient.notificacio.consultar.estat.info"/>">
									<span class="fa fa-refresh"></span>&nbsp;<spring:message code="comu.boto.refrescar"/>
									</a>
								</li>

							</ul>
						</div>
					</script>
				</th>				
										
				<th data-col-name="enviamentDatatData" data-visible="false"></th>
				<th data-col-name="documentStoreId" data-visible="false"></th>
				<th data-col-name="justificantId" data-visible="false"></th>
				<th data-col-name="processInstanceId" data-visible="false"></th>
				<th data-col-name="processInstanceId" data-visible="false"></th>
				<th data-col-name="expedientId" data-visible="false"></th>
				<th data-col-name="expedientTipusId" data-visible="false">
				<th data-col-name="expedientTipusNom" data-visible="false">
				<th data-col-name="expedientTipusCodi" data-visible="false">
				<th data-col-name="entornNom" data-visible="false">
				<th data-col-name="entornCodi" data-visible="false">
			</tr>
		</thead>
	</table>	

	
	
	<script id="rowhrefTemplate" type="text/x-jsrender"><c:url value="/v3/notificacionsNotib/{{:id}}/info"/></script>	
	
		
	<script type="text/javascript">
	// <![CDATA[

	   $.views.helpers({
		   formatTemplateDate: function (d) {
			   return moment(new Date(d)).format("DD/MM/YYYY HH:mm:ss");
		    }
		});
		            
	$(document).ready(function() {	
		$("#netejar").click(function() {
			$('#unitatOrganitzativaCodi').val('').change();
			$('#expedientTipusId').val('').change();
			$('#estat').val('').change();
			$('#tipus').val('').change();
			//$('#estat').val("PENDENT").change().change();
		})	
	});
	
	// ]]>
	</script>	

</body>
</html>
