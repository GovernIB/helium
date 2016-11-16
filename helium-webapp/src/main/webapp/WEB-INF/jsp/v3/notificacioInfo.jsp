<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<html>
<head>
	<title>
		<spring:message code="expedient.notificacio.expedient"/>:
		<c:if test="${expedient.tipus.teNumero}">
			${expedient.numero}
		</c:if>
		<c:if test="${expedient.tipus.teNumero and expedient.tipus.teTitol}">
			-
		</c:if>
		<c:if test="${expedient.tipus.teTitol}">
			${expedient.titol}
		</c:if>
	</title>
	<hel:modalHead/>
	<style type="text/css">
		.var_notificacions label {
			font-style: italic;
    		font-weigth: normal;
    		color: #999;
    		font-size: small;
		}
		.tableDocuments .extensionIcon {
			color: white !important;
			float: left;
			font-size: 12px;
			font-weight: bold;
			margin-left: 12px;
			margin-top: -20px;
			position: relative;
		}
		.contingut-tabs-marge {
			margin-top: 15px;
		}
	</style>
<script type="text/javascript">

</script>
</head>
<body>


<!-- Nav tabs -->
  <ul class="nav nav-tabs" role="tablist">
    <li role="presentation" class="active"><a href="#dades_generals" aria-controls="home" role="tab" data-toggle="tab"><spring:message code="expedient.notificacio.dades_generals"/></a></li>
    <li role="presentation"><a href="#dades_interessat" aria-controls="profile" role="tab" data-toggle="tab"><spring:message code="expedient.notificacio.interessat"/></a></li>
    <li role="presentation"><a href="#documents_annexos" aria-controls="profile" role="tab" data-toggle="tab"><spring:message code="expedient.notificacio.document_annexos"/></a></li>
  </ul>

  <!-- Tab panes -->
  <div class="tab-content contingut-tabs-marge">
    <div role="tabpanel" class="tab-pane active" id="dades_generals">
    	<div class="panel panel-primary">
	  <div class="panel-heading">
	    <h3 class="panel-title"><spring:message code="expedient.notificacio"/>: ${notificacio.registreNumero}</h3>
	  </div>
	  <div class="panel-body">
	  	<table class="table table-bordered">
	    	<tbody>
	    		<tr>
		    		<td colspan="2">
		    			<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.assumpte"/></label><br>
							<strong>${notificacio.assumpte}</strong>
						</address>
		    		</td>
	    			<td>
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.data_enviament"/></label><br>
							<strong><fmt:formatDate value="${notificacio.dataEnviament}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate></strong>
						</address>
	    			</td>
	    			<td>
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.data_recepcio"/></label><br>
							<strong><fmt:formatDate value="${notificacio.dataRecepcio}" pattern="dd/MM/yyyy HH:mm:ss"></fmt:formatDate></strong>
						</address>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>
		    			<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.estat"/>:</label><br>
							<c:choose>
								<c:when test="${notificacio.estat == 'ENVIAT'}">
									<span class="label label-warning"><spring:message code="expedient.notificacio.estat.enviat"/></span>
								</c:when>
								<c:when test="${notificacio.estat == 'PROCESSAT_OK'}">
									<span class="label label-success"><spring:message code="expedient.notificacio.estat.processat_ok"/></span>
								</c:when>
								<c:when test="${notificacio.estat == 'PROCESSAT_REBUTJAT'}">
									<span class="label label-danger"><spring:message code="expedient.notificacio.estat.processat_rebutjat"/></span>
								</c:when>
								<c:when test="${notificacio.estat == 'PROCESSAT_ERROR'}">
									<span class="label label-danger"><spring:message code="expedient.notificacio.estat.processat_error"/></span>
								</c:when>
								<c:otherwise>
									<span class="label label-default">${notificacio.estat}</span>
								</c:otherwise>
							</c:choose>
						</address>
	    			</td>
	    			<td>
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.organ_codi"/></label><br>
							<strong>${notificacio.organCodi}</strong>
						</address>
	    			</td>
	    			<td>
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.oficina_codi"/></label><br>
							<strong>${notificacio.oficinaCodi}</strong>
						</address>
	    			</td>
	    			<td>
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.unitat_administrativa"/></label><br>
							<strong>${notificacio.unitatAdministrativa}</strong>
						</address>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.avis_titol"/></label><br>
							<strong>${notificacio.avisTitol}</strong>
						</address>
	    			</td>
	    			<td colspan="2">
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.avis_text"/></label><br>
							<strong>${notificacio.avisText}</strong>
						</address>
	    			</td>
	    			<td>
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.avis_text_sms"/></label><br>
							<strong>${notificacio.avisTextSms}</strong>
						</address>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.ofici_titol"/></label><br>
							<strong>${notificacio.oficiTitol}</strong>
						</address>
	    			</td>
	    			<td colspan="3">
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.ofici_titol"/></label><br>
							<strong>${notificacio.oficiText}</strong>
						</address>
	    			</td>
	    		</tr>
	    		
	    		<c:if test="${not empty notificacio.error}">
	    		<tr>
	    			<td colspan="4">
	    				<address class="var_notificacions">
							<label><spring:message code="error.error"/></label><br>
							<p><c:out value="${notificacio.error}"/><p>
						</address>
	    			</td>
	    		</tr>
	    		</c:if>
	    	</tbody>
	  	</table>
	  </div>
	</div>
    </div>
    
    <div role="tabpanel" class="tab-pane" id="dades_interessat">
    	<div class="panel panel-primary">
	  <div class="panel-heading">
	    <h3 class="panel-title"><spring:message code="expedient.notificacio.interessat"/></h3>
	  </div>
	  <div class="panel-body">
	  	<table class="table table-bordered">
	    	<tbody>
	    		<tr>
	    			<td>
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.interessat.document_num"/></label><br>
							<strong>${notificacio.interessatDocumentNum}</strong>
						</address>
	    			</td>
	    			<td>
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.interessat.nom_llinatges"/></label><br>
							<strong>${notificacio.interessatNom} ${notificacio.interessatLlinatge1} ${notificacio.interessatLlinatge2}</strong>
						</address>
	    			</td>
	    			<td>
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.interessat.email"/></label><br>
							<strong>${notificacio.interessatEmail}</strong>
						</address>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.interessat.pais"/></label><br>
							<strong>${notificacio.interessatPaisCodi}</strong>
						</address>
	    			</td>
	    			<td>
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.interessat.provincia"/></label><br>
							<strong>${notificacio.interessatProvinciaCodi}</strong>
						</address>
	    			</td>
	    			<td>
	    				<address class="var_notificacions">
							<label><spring:message code="expedient.notificacio.interessat.municipi"/></label><br>
							<strong>${notificacio.interessatMunicipiCodi}</strong>
						</address>
	    			</td>
	    		</tr>
	    	</tbody>
	  	</table>
	  </div>
	</div>
    </div>
    
    
    <div role="tabpanel" class="tab-pane" id="documents_annexos">
	    <div class="panel panel-primary">
		  <div class="panel-heading">
		    <h3 class="panel-title"><spring:message code="expedient.notificacio.document_annexos"/></h3>
		  </div>
		  <div class="panel-body">
		  	<table class="table table-bordered">
		    	<tbody>
		    		<tr>
		    			<td id="cela-${expedient.id}-${notificacio.document.id}">									
							<table id="document_${notificacio.document.id}" class="table-condensed marTop6 tableDocuments">
								<thead>
									<tr>
										<td class="left" style="max-width: 60px;">
											<a href="<c:url value="/v3/expedient/${expedientId}/notificacio/${notificacio.id}/document/${notificacio.document.id}/descarregar"/>">
												<span class="fa fa-file fa-4x" title="Descarregar document"></span>
												<span class="extensionIcon">
													${fn:toUpperCase(notificacio.document.arxiuExtensio)}
												</span>
											</a>
										</td>
										<td class="right">
											<c:if test="${not empty notificacio.document.id}">
												<table class="marTop6 tableDocuments">
													<thead>
														<tr>
															<td class="tableDocumentsTd">
															</td>
														</tr>
														<tr>
															<td>
																<spring:message code='expedient.document.data' /> <fmt:formatDate value="${notificacio.document.dataDocument}" pattern="dd/MM/yyyy"/>
															</td>
														</tr>
														<c:if test="${not empty notificacio.document.dataCreacio}">
															<tr>
																<td>
																	<spring:message code='expedient.document.adjuntat' /> <fmt:formatDate value="${notificacio.document.dataCreacio}" pattern="dd/MM/yyyy hh:mm"/>
																</td>
															</tr>
														</c:if>
													</thead>
												</table>
											</c:if>
										</td>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td colspan="2">
											<strong class="nom_document">
												${notificacio.document.documentNom}
											</strong><br/>
										</td>
									</tr>
								</tbody>
							</table>
						</td>
						<c:forEach var="annex" items="${notificacio.annexos}">
							<td id="cela-${expedient.id}-${annex.id}">									
							<table id="document_${annex.id}" class="table-condensed marTop6 tableDocuments">
								<thead>
									<tr>
										<td class="left" style="max-width: 60px;">
											<a href="<c:url value="/v3/expedient/${expedientId}/notificacio/${notificacio.id}/document/${annex.id}/descarregar"/>">
												<span class="fa fa-file fa-4x" title="Descarregar document"></span>
												<span class="extensionIcon">
													${fn:toUpperCase(annex.arxiuExtensio)}
												</span>
											</a>
										</td>
										<td class="right">
											<c:if test="${not empty annex.id}">
												<table class="marTop6 tableDocuments">
													<thead>
														<tr>
															<td class="tableDocumentsTd">
															</td>
														</tr>
														<tr>
															<td>
																<spring:message code='expedient.document.data' /> <fmt:formatDate value="${annex.dataDocument}" pattern="dd/MM/yyyy"/>
															</td>
														</tr>
														<c:if test="${not empty annex.dataCreacio}">
															<tr>
																<td>
																	<spring:message code='expedient.document.adjuntat' /> <fmt:formatDate value="${annex.dataCreacio}" pattern="dd/MM/yyyy hh:mm"/>
																</td>
															</tr>
														</c:if>
													</thead>
												</table>
											</c:if>
										</td>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td colspan="2">
											<strong class="nom_document">
												${annex.documentNom}
											</strong><br/>
										</td>
									</tr>
								</tbody>
							</table>
						</td>
						</c:forEach>
		    		</tr>
		    	</tbody>
		  	</table>
		  </div>
		</div>
    </div>
  </div>






	
	
	
</body>
</html>