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
		<div class="col-md-2">
			<hel:inputSelect inline="true" name="tipusEnviament" optionItems="${tipusEnviament}"
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
			<hel:inputDate name="dataInicial" textKey="notificacio.llistat.filtre.camp.dataInicial" placeholder="dd/mm/aaaa" inline="true"/>
		</div>	
		<div class="col-md-2">
			<hel:inputDate name="dataFinal" textKey="notificacio.llistat.filtre.camp.dataFinal" placeholder="dd/mm/aaaa" inline="true"/>
		</div>	
		<div class="col-md-2">
			<hel:inputText name="interessat" textKey="notificacio.llistat.filtre.camp.interessat" placeholderKey="notificacio.llistat.filtre.camp.interessat" inline="true"/>
		</div>
	</div>
		
	<div class="row">
		 <div class="col-md-2">							
			<hel:inputText name="expedientNumero" textKey="notificacio.llistat.filtre.camp.expedient" placeholderKey="notificacio.llistat.filtre.camp.expedient" inline="true"/>
		</div>	
		<div class="col-md-2">
			<hel:inputText name="nomDocument" textKey="notificacio.llistat.filtre.camp.document.nom" placeholderKey="notificacio.llistat.filtre.camp.document.nom" inline="true"/>
		</div>	
		<div class="col-md-3">
			<hel:inputSuggest 
					name="unitatOrganitzativaCodi" 
					urlConsultaInicial="/helium/v3/unitatOrganitzativa/suggestInici" 
					urlConsultaLlistat="/helium/v3/unitatOrganitzativa/suggest" 
					placeholderKey="notificacio.llistat.filtre.camp.organ.emissor"
					inline="true"
					/>	
		</div>	
		<div class="col-md-3">
			<hel:inputSelect name="expedientTipusId" textKey="notificacio.llistat.filtre.camp.expedientTipus"
				optionItems="${expedientsTipus}" optionValueAttribute="codi" emptyOption="true"
				inline="true" placeholderKey="notificacio.llistat.filtre.camp.expedientTipus" optionTextAttribute="valor" />
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
				<th data-col-name="expedientTipusNom" data-template="#cellTipusExpTemplate" width="8%">
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
				 <th data-col-name="organEmissorCodiAndNom" data-visible="true" width="10%" data-orderable="false">
				 	<spring:message code="notificacio.llistat.columna.organ.emissor"/>
				</th>
			
				<th data-col-name="interessatFullNomNif"><spring:message code="notificacio.llistat.filtre.camp.interessat"/></th>
				  
			 	<th data-col-name="tipus" data-template="#cellTipusTemplate" width="9%">
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
					</script>${documentsDinsZip}
				<!-- 	<c:if test="${not empty documentsDinsZip}">
								<br/><span class="">Conté:</span>
									<ul>
									<c:forEach items="${documentsDinsZip}" var="docContingut">
											<li data-id="${docContingut.id}" data-codi="${docContingut.codiDocument}">
												<a href="<c:url value="/v3/expedient/${expedient.id}/proces/${expedient.processInstanceId}/document/${docContingut.id}/descarregar"/>"
													title="<spring:message code="expedient.notificacio.descarregar.doc"/>">
													<c:choose>
														<c:when test="${docContingut.adjunt}">
															<span class="fa fa-paperclip" title="Document adjunt"></span>
															${docContingut.nom}
														</c:when>
														<c:otherwise>
															${nomsDocuments[docContingut.codiDocument]}
														</c:otherwise>
													</c:choose>
													<span class="fa fa-download fa-lg"></span>
												</a>
											</li>
									</c:forEach>
									</ul>
								</c:if>		
								 -->	
				</th>
			
			  	<th data-col-name="estat" data-template="#cellEstatTemplate" width="9%">
				<spring:message code="notificacio.llistat.columna.estat"/>
					<script id="cellEstatTemplate" type="text/x-jsrender">
						{{if estat == 'PENDENT'}}
							<span class="fa fa-clock-o"></span>
							<spring:message code="notificacio.etst.enum.PENDENT"></spring:message>
						{{else estat == 'ENVIADA'}}
							<span class="fa fa-send-o"></span>
							<spring:message code="notificacio.etst.enum.ENVIADA"></spring:message>
						{{else estat == 'REGISTRADA'}}
							<span class="fa fa-file-o"></span>
							<spring:message code="notificacio.etst.enum.REGISTRADA" ></spring:message>
						{{else}}
						{{else estat == 'FINALITZADA'}}
							<span class="fa fa-check"></span>
							<spring:message code="notificacio.etst.enum.FINALITZADA" ></spring:message>
						{{else}}
						{{else estat == 'PROCESSADA'}}
							<span class="fa fa-check-circle"></span>
							<spring:message code="notificacio.etst.enum.PROCESSADA" ></spring:message>
						{{else}}
							{{:estat}}
						{{/if}}
					</script>				
				</th>
				
				<th data-col-name="destinatariNomILlinatges" width="10%" data-visible="true" data-orderable="false">
				 	<spring:message code="notificacio.llistat.columna.destinatari"/>
				</th>  
				 
				<th data-col-name="id" width="5%" data-template="#cellAccionsTemplate" data-orderable="false">
					<script id="cellAccionsTemplate" type="text/x-jsrender">
						<div class="dropdown navbar-right">
							<button class="btn btn-primary" data-toggle="dropdown"><span class="fa fa-cog"></span>&nbsp;<spring:message code="comu.boto.accions"/>&nbsp;<span class="caret"></span></button>
							<ul class="dropdown-menu">
								<li><a href="<c:url value="/v3/notificacionsNotib/{{:id}}/info"/>" data-toggle="modal" class="consultar-expedient"><span class="fa fa-info-circle"></span>&nbsp;<spring:message code="consultes.pinbal.boto.info"/></a></li>
							</ul>
						</div>
					</script>
				</th>				
				
				<th data-col-name="documentStoreId" data-visible="false"></th>
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
			//$('#estat').val("PENDENT").change().change();
		})	
	});
	
	// ]]>
	</script>	

</body>
</html>
