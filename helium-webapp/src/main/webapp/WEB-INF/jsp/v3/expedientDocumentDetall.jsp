<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<%--<link href="<c:url value="/css/exp-doc.css"/>" rel="stylesheet"/>--%>

<style>
.dl-horizontal dt::after {
  content: ": ";
}
</style>

<script type="text/javascript">
	// <![CDATA[
	$(document).ready( function() {
		// Clica el primer tab amb text-success
		$('#nav-tabs-document-${detall.documentStoreId} li a[role=tab]').children('span').each( function(){
			if ($(this).hasClass('text-success')) {
				$(this).parent('a[role=tab]').click();
				return false;
			}
		});
	});
	// ]]>
</script>

	<!-- Tabuladors -->		
	
	<ul id="nav-tabs-document-${detall.documentStoreId}" class="nav nav-tabs" role="tablist">
		<li id="pipella-documentDetall-arxiu-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-arxiu-${detall.documentStoreId}" role="tab" data-toggle="tab">
				<span class="fa fa-bookmark <c:choose><c:when test="${detall.nti || detall.arxiu}">text-success</c:when><c:otherwise>text-default</c:otherwise></c:choose>">
				<c:choose>
					<c:when test="${detall.arxiu}">Arxiu</c:when>
					<c:when test="${detall.nti}">NTI</c:when>
					<c:otherwise>NTI / Arxiu</c:otherwise>
				</c:choose>
				</span>
			</a>
		</li>
		<li id="pipella-documentDetall-registre-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-registre-${detall.documentStoreId}" role="button" data-toggle="tab">
				<span class="fa fa-book <c:choose><c:when test="${detall.registrat}">text-success</c:when><c:otherwise>text-default</c:otherwise></c:choose>"></span>
				<spring:message code="expedient.document.detall.pipella.registre"/>
			</a>
		</li>
		<li id="pipella-documentDetall-signatura-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-signatura-${detall.documentStoreId}" role="tab" data-toggle="tab">
				<span class="fa fa-certificate <c:choose><c:when test="${detall.signat}">text-success</c:when><c:otherwise>text-default</c:otherwise></c:choose>"></span>
				<spring:message code="expedient.document.detall.pipella.signatura"/>
			</a>
		</li>
		<li id="pipella-documentDetall-portasignatura-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-portasignatura-${detall.documentStoreId}" role="tab" data-toggle="tab">
				<span class="fa fa-clock-o <c:choose><c:when test="${detall.psignaPendent}">text-success</c:when><c:otherwise>text-default</c:otherwise></c:choose>"></span>
				<spring:message code="expedient.document.detall.pipella.portasignatura"/>
			</a>
		</li>
		<li id="pipella-documentDetall-notificacions-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-notificacions-${detall.documentStoreId}" role="tab" data-toggle="tab">
				<span class="fa fa-paper-plane-o <c:choose><c:when test="${detall.notificat}">text-success</c:when><c:otherwise>text-default</c:otherwise></c:choose>"></span>
				<spring:message code="expedient.document.detall.pipella.notificacions"/>
			</a>
		</li>
		<li id="pipella-documentDetall-anotacions-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-anotacions-${detall.documentStoreId}" role="tab" data-toggle="tab">
				<span class="fa fa-file-text <c:choose><c:when test="${detall.deAnotacio}">text-success</c:when><c:otherwise>text-default</c:otherwise></c:choose>"></span>
				<spring:message code="expedient.document.detall.pipella.anotacions"/>
			</a>
		</li>
	
		<c:if test="${detall.extensio == 'pdf' or detall.extensio == 'pdt' or detall.extensio == 'docx'}">
			<li id="pipella-documentDetall-previsualitzacio-${detall.documentStoreId}" class="pull-right" role="button">	
				<a href="#contingut-documentDetall-previsualitzacio-${detall.documentStoreId}" class="previs-icon" role="tab" data-toggle="tab">
					<span class="fa fa-eye"></span>
					<spring:message code="expedient.document.previsualitzacio"/>
				</a>
			</li>			
		</c:if>
					
	</ul>
	
	
	<div class="tab-content">
	
		<%-- REGISTRE => detall.registreDetall --%>
		
		<div id="contingut-documentDetall-registre-${detall.documentStoreId}" class="tab-pane">
			<c:set var="registre" value="${detall.registreDetall}"/>
			<dl class="dl-horizontal">
				<%-- Número de registre--%>
					<dt><spring:message code="expedient.info.numero_registre"/></dt>
					<dd>${registre != null ? registre.registreNumero : '--'}</dd>
				<%-- Data de registre--%>
					<dt><spring:message code="expedient.info.camp.registre.data"/></dt>
					<dd>${registre != null ? '<fmt:formatDate value="${registre.registreData}" pattern="dd/MM/yyyy HH:mm"/>' : '--'}</dd>
				<%-- Oficina de registre--%>
					<dt><spring:message code="expedient.info.camp.registre.oficina"/></dt>
					<dd>${registre != null ? registre.registreOficinaNom : '--'}</dd>
				<%-- Tipus de registre--%>
					<dt><spring:message code="expedient.info.camp.registre.tipus"/></dt>
					<dd>${registre != null ? (registre.registreEntrada ? '<spring:message code="expedient.info.camp.registre.tipus.entrada"/>' : '<spring:message code="expedient.info.camp.registre.tipus.sortida"/>') : '--'}</dd>
			</dl>
	
			<hr class="dark horizontal my-0"/>
			<c:choose>
				<c:when test="${detall.registrat}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">REGISTRAT</span></p></c:when>
				<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO REGISTRAT</span></p></c:otherwise>
			</c:choose>
		</div>
					
			
		<%-- SIGNATURA => detall.signaturaValidacioDetall --%>
		
		<div id="contingut-documentDetall-signatura-${detall.documentStoreId}" class="tab-pane">
			<c:set var="signatura" value="${detall.signaturaValidacioDetall}"/>
			<dl class="dl-horizontal">
				<dt>Verificació de la signatura</dt>
				<dd><c:choose>
					<c:when test="${signatura != null and not empty signatura.urlVerificacio}">
						<a href="${signatura.urlVerificacio}" target="blank"><span class="fa fa-certificate"></span> <span class="fa fa-external-link"></span></a>
					</c:when>
					<c:otherwise>--</c:otherwise>
					</c:choose>
				</dd>
				
				<c:if test="${not empty signatura.tokenSignatura}">
					<c:url value="/v3/expedient/document/arxiuMostrar" var="downloadUrl"><c:param name="token" value="${signatura.tokenSignatura}"/></c:url>
						<dt>Verificació de la signatura</dt>
						<dd><c:choose><c:when test="${signatura != null and not empty signatura.urlVerificacio}"><a href="${signatura.urlVerificacio}"><span class="fa fa-external-link"></span></a></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
				</c:if>
				<c:if test="${not empty signatura.signatures}">
				
					<table class="table table-bordered">
					<tbody>
						<tr>
							<td>Responsable</td>
							<td>NIF</td>
							<td>Estat</td>
						</tr>
					<tbody>
						<c:forEach var="firma" items="${signatura.signatures}" varStatus="status">
							<tr>
								<td>${firma.nomResponsable}</td>
								<td>${firma.nifResponsable}</td>
								<td>
									<c:choose>
										<c:when test="${firma.estatOk}"><span class="fa fa-check text-success"></span></c:when>
										<c:otherwise><span class="fa fa-times text-danger"></span></c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
					</tbody>
					</table>
					
				</c:if>
			</dl>
			<hr class="dark horizontal my-0"/>
			<c:choose>
				<c:when test="${detall.signat}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">SIGNAT</span></p></c:when>
				<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO SIGNAT</span></p></c:otherwise>
			</c:choose>
		</div>
		
	
		<%-- PSIGNA => detall.signaturaValidacioDetall --%>
		<div id="contingut-documentDetall-portasignatura-${detall.documentStoreId}" class="tab-pane">
			<c:set var="psigna" value="${detall.psignaDetall}"/>
			<c:if test="${not empty psigna}">
				<dl class="dl-horizontal">
					
						<dt><spring:message code="common.icones.doc.psigna.id"/></dt>
						<dd><c:choose><c:when test="${not empty psigna.documentId}">${psigna.documentId}</c:when><c:otherwise>--</c:otherwise></c:choose></dd>
					
					
						<dt><spring:message code="common.icones.doc.psigna.data.enviat"/></dt>
						<dd><c:choose><c:when test="${not empty psigna.dataEnviat}"><fmt:formatDate value="${psigna.dataEnviat}" pattern="dd/MM/yyyy HH:mm"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
					
					
						<dt><spring:message code="common.icones.doc.psigna.estat"/></dt>
						<dd>
							<c:choose>
								<c:when test="${psigna.estat == 'PROCESSAT' && psigna.error}">
									<span>REBUTJAT</span>
								</c:when>
								<c:when test="${psigna.estat != 'PROCESSAT' && psigna.error && not empty psigna.errorProcessant}">
									<span>PENDENT</span>
								</c:when>
								<c:otherwise>
									<span>${psigna.estat}</span>
								</c:otherwise>
							</c:choose>
						</dd>
					
					<c:if test="${not empty psigna.motiuRebuig}">
						
							<dt><spring:message code="common.icones.doc.psigna.motiu.rebuig"/></dt>
							<dd>${psigna.motiuRebuig != null ? psigna.motiuRebuig : '--'}</dd>
						
					</c:if>
					<c:if test="${not empty psigna.dataProcessamentPrimer}">
						
							<dt><spring:message code="common.icones.doc.psigna.data.proces.primer"/></dt>
							<dd><c:choose><c:when test="${not empty psigna.dataProcessamentPrimer}"><fmt:formatDate value="${psigna.dataProcessamentPrimer}" pattern="dd/MM/yyyy HH:mm"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
						
					</c:if>
					<c:if test="${not empty psigna.dataProcessamentDarrer}">
						
							<dt><spring:message code="common.icones.doc.psigna.data.proces.darrer"/></dt>
							<dd><c:choose><c:when test="${not empty psigna.dataProcessamentDarrer}"><fmt:formatDate value="${psigna.dataProcessamentDarrer}" pattern="dd/MM/yyyy HH:mm"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
						
					</c:if>
				</dl>
			</c:if>
			<hr class="dark horizontal my-0"/>
			<c:choose>
				<c:when test="${detall.psignaPendent}">
					<c:choose>
						<c:when test="${psigna.error and psigna.estat != 'PROCESSAT'}">
							<p class="mb-0">Document amb error: <span class="text-dander text-sm font-weight-bolder"><spring:message code="expedient.document.pendent.psigna.error" /></span></p>
						</c:when>
						<c:when test="${psigna.error and psigna.estat == 'PROCESSAT'}">
							<p class="mb-0">Document amb error: <span class="text-danger text-sm font-weight-bolder"><spring:message code="expedient.document.rebutjat.psigna.error" /></span></p>
						</c:when>
						<c:otherwise>
							<p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">PENDENT de Portasignatures</span></p>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO PENDENT de Portasignatures</span></p></c:otherwise>
			</c:choose>
		</div>
	
		<%-- NOTIFICACIONS => detall.notificacions --%>
		<div id="contingut-documentDetall-notificacions-${detall.documentStoreId}" class="tab-pane">
			<c:set var="notificacions" value="${detall.notificacions}"/>
				
				<table class="table table-bordered">
				<thead>
					<tr>
						<th>Destinatari</th>
						<th>Estat notificació</th>
						<th>Estat enviament</th>
					</tr>
				</thead>
				<tbody>

					<c:forEach var="notificacio" items="${notificacions}" varStatus="status">
						<c:forEach var="enviament" items="${notificacio.enviaments}" varStatus="statuse">
							<tr>
								<td>
									<span class="label label-default">${notificacio.enviamentTipus == "NOTIFICACIO" ? 'N' : 'C'}</span>
										<b>${enviament.titular.nomSencer}</b>
										<p class="comment">${notificacio.enviatData}</p>
								</td>
								<td>
									<span title="<spring:message code="notificacio.etst.enum.${notificacio.estat}.info"/>">
										<c:choose>
										<c:when test="${notificacio.estat == 'PENDENT'}">
											<span class="fa fa-clock-o"></span>
										</c:when>
										<c:when test="${notificacio.estat == 'ENVIADA'}">
											<span class="fa fa-send-o"></span>
											<span class="label label-warning"></span>
										</c:when>
										<c:when test="${notificacio.estat == 'REGISTRADA'}">
											<span class="fa fa-file-o"></span>
										</c:when>
										<c:when test="${notificacio.estat == 'FINALITZADA'}">
											<span class="fa fa-check"></span>
										</c:when>							
										<c:when test="${notificacio.estat == 'PROCESSADA'}">
											<span class="fa fa-check-circle"></span>
										</c:when>
										</c:choose>
										<spring:message code="notificacio.etst.enum.${notificacio.estat}"/>
									</span>
								</td>
								<td>
									<c:set var="ecolor" value="default"/>
									<c:choose>
										<c:when test="${enviament.estat == 'LLEGIDA' or
													enviament.estat == 'NOTIFICADA'}"><c:set var="ecolor" value="success"/></c:when>
										<c:when test="${enviament.estat == 'ABSENT' or
													enviament.estat == 'DESCONEGUT' or
													enviament.estat == 'ADRESA_INCORRECTA' or
													enviament.estat == 'MORT' or
													enviament.estat == 'EXTRAVIADA' or
													enviament.estat == 'SENSE_INFORMACIO' or
													enviament.estat == 'ERROR_ENTREGA' or
													enviament.estat == 'EXPIRADA'}"><c:set var="ecolor" value="danger"/></c:when>
										<c:otherwise><c:set var="ecolor" value="default"/></c:otherwise>
									</c:choose>
									<span class="label label-${ecolor}">${enviament.estat != null? enviament.estat : '-'}</span>
								</td>
							</tr>
						</c:forEach>
					</c:forEach>
				</tbody>
				</table>
			
			<hr class="dark horizontal my-0"/>
			<c:choose>
				<c:when test="${detall.notificat}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">NOTIFICAT</span></p></c:when>
				<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO NOTIFICAT</span></p></c:otherwise>
			</c:choose>
		</div>
	
	
		<%-- ARXIU/NTI => detall.arxiuDetall --%>
		<div id="contingut-documentDetall-arxiu-${detall.documentStoreId}" class="tab-pane">
			
			<c:set var="arxiuDetall" value="${detall.arxiuDetall}"/>
			<c:set var="ntiDetall" value="${detall.ntiDetall}"/>
			<c:if test="${not empty detall.arxiuDetall}">
				<ul class="nav nav-pills" role="tablist">
					<li role="presentation" class="active"><a class="pill-link" href="#documentDetall_nti_${detall.documentStoreId}" aria-controls="nti" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.nti"/></a></li>
					<li role="presentation"><a class="pill-link" href="#documentDetall_info_${detall.documentStoreId}" aria-controls="info" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.info"/></a></li>
					<c:if test="${not empty arxiuDetall.fills}"><li role="presentation"><a class="pill-link" href="#documentDetall_fills_${detall.documentStoreId}" aria-controls="fills" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.fills"/> <span class="badge badge-default">${fn:length(arxiuDetall.fills)}</span></a></li></c:if>
					<c:if test="${not empty arxiuDetall.firmes}"><li role="presentation"><a class="pill-link" href="#documentDetall_firmes_${detall.documentStoreId}" aria-controls="firmes" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.firmes"/> <span class="badge badge-default">${fn:length(arxiuDetall.firmes)}</span></a></li></c:if>
					<c:if test="${not empty arxiuDetall.metadadesAddicionals}"><li role="presentation"><a class="pill-link" href="#documentDetall_metadades_${detall.documentStoreId}" aria-controls="metadades" role="tab" data-toggle="tab"><spring:message code="expedient.metadades.nti.tab.metadades"/></a></li></c:if>
				</ul>
			</c:if>

				<div class="tab-content">
					<%-- Metadades NTI --%>
					<c:if test="${not empty detall.ntiDetall }">
					
						<div id="documentDetall_nti_${detall.documentStoreId}" class="tab-pane in active">
							<dl class="dl-horizontal">
								<%-- --%>
								
									<dt><spring:message code="document.metadades.nti.version"/></dt>
									<dd>${ntiDetall.ntiVersion != null ? ntiDetall.ntiVersion : '--'}</dd>
								
								
									<dt><spring:message code="document.metadades.nti.identificador"/></dt>
									<dd>${ntiDetall.ntiIdentificador != null ? ntiDetall.ntiIdentificador : '--'}</dd>
								
								
									<dt><spring:message code="document.metadades.nti.organo"/></dt>
									<dd>${ntiDetall.ntiOrgano != null ? ntiDetall.ntiOrgano : '--'}</dd>
								
								
									<dt><spring:message code="document.metadades.nti.fecha.captura"/></dt>
									<dd><c:choose><c:when test="${not empty detall.dataCreacio}"><fmt:formatDate value="${detall.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="document.metadades.nti.origen"/></dt>
									<dd><c:choose><c:when test="${not empty ntiDetall.ntiOrigen}"><spring:message code="nti.document.origen.${ntiDetall.ntiOrigen}"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="document.metadades.nti.estado.elaboracion"/></dt>
									<dd><c:choose><c:when test="${not empty ntiDetall.ntiEstadoElaboracion}"><spring:message code="nti.document.estado.elaboracion.${ntiDetall.ntiEstadoElaboracion}"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="document.metadades.nti.nombre.formato"/></dt>
									<dd><c:choose><c:when test="${not empty ntiDetall.ntiNombreFormato}"><spring:message code="nti.document.format.${ntiDetall.ntiNombreFormato}"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="document.metadades.nti.tipo.documental"/></dt>
									<dd><c:choose><c:when test="${not empty ntiDetall.ntiTipoDocumental}"><spring:message code="nti.document.tipo.documental.${ntiDetall.ntiTipoDocumental}"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="document.metadades.nti.iddoc.origen"/></dt>
									<dd>${ntiDetall.ntiIdDocumentoOrigen != null ? ntiDetall.ntiIdDocumentoOrigen : '--'}</dd>
								
								
									<dt><spring:message code="document.metadades.nti.tipo.firma"/></dt>
									<dd><c:choose><c:when test="${not empty ntiDetall.ntiTipoFirma}">${ntiDetall.ntiTipoFirma}<c:if test="${not empty detall.ntiCsv}">, CSV</c:if></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="document.metadades.nti.csv"/></dt>
									<dd>${ntiDetall.ntiCsv != null ? ntiDetall.ntiCsv : '--'}</dd>
								
								
									<dt><spring:message code="document.metadades.nti.defgen.csv"/></dt>
									<dd>${ntiDetall.ntiDefinicionGenCsv != null ? ntiDetall.ntiDefinicionGenCsv : '--'}</dd>
								
							</dl>
							<c:if test="${detall.errorMetadadesNti}">
								<div class="row alert alert-danger" style="margin: 0px; margin-bottom: 10px;">
									<div class="col-sm-10">
										<p><spring:message code="expedient.metadades.nti.dades.error.info"/></p>
									</div>
		<%--					TODO:--%>
		<%--									<div class="col-sm-2">--%>
		<%--										<form  action="<c:url value="/v3/expedient/${expedient.id}/metadadesNti/arreglar"/>" method="post">--%>
		<%--											<button id="arreglarNtiButton" type="submit" class="btn btn-default" title="<spring:message code='expedient.metadades.nti.dades.error.arreglar.info'></spring:message>">--%>
		<%--												<span class="fa fa-cog"></span>--%>
		<%--												<spring:message code="expedient.metadades.nti.dades.error.arreglar"></spring:message>--%>
		<%--											</button>--%>
		<%--										<form>--%>
		<%--									</div>--%>
								</div>
							</c:if>
						</div>
					</c:if>

					<c:if test="${not empty detall.arxiuDetall}">

						<%-- Informació arxiu --%>
						<div id="documentDetall_info_${detall.documentStoreId}" class="tab-pane">
							<dl class="dl-horizontal">
								
									<dt><spring:message code="expedient.metadades.nti.camp.identificador"/></dt>
									<dd>${arxiuDetall.identificador != null ? arxiuDetall.identificador : '--'}</dd>
								
								
									<dt><spring:message code="expedient.metadades.nti.camp.nom"/></dt>
									<dd>${arxiuDetall.nom != null ? arxiuDetall.nom : '--'}</dd>
								
								<c:if test="${not empty arxiuDetall.serieDocumental}">
									
										<dt><spring:message code="expedient.metadades.nti.camp.serie.doc"/></dt>
										<dd>${arxiuDetall.serieDocumental != null ? arxiuDetall.serieDocumental : '--'}</dd>
									
								</c:if>
							</dl>

							<c:if test="${not empty arxiuDetall.contingutTipusMime or not empty arxiuDetall.contingutArxiuNom}">
							<hr class="dark horizontal my-0">

							<div class="text-dark font-weight-bold"><spring:message code="expedient.metadades.nti.grup.contingut"/></div>
							<dl class="dl-horizontal">
								<c:if test="${not empty arxiuDetall.contingutTipusMime}">
								
									<dt><spring:message code="expedient.metadades.nti.camp.contingut.tipus.mime"/></dt>
									<dd>${arxiuDetall.contingutTipusMime != null ? arxiuDetall.contingutTipusMime : '--'}</dd>
								
								</c:if>
								<c:if test="${not empty arxiuDetall.contingutArxiuNom}">
								
									<dt><spring:message code="expedient.metadades.nti.camp.contingut.arxiu.nom"/></dt>
									<dd>${arxiuDetall.contingutArxiuNom != null ? arxiuDetall.contingutArxiuNom : '--'}</dd>
								
								</c:if>
							</dl>
							</c:if>

							<c:if test="${not empty arxiuDetall.eniIdentificador}">
								<hr class="dark horizontal my-0">

								<div class="text-dark font-weight-bold"><spring:message code="expedient.metadades.nti.grup.metadades"/></div>
								<dl class="dl-horizontal">
									
										<dt><spring:message code="expedient.metadades.nti.camp.eni.versio"/></dt>
										<dd>${arxiuDetall.eniVersio != null ? arxiuDetall.eniVersio : '--'}</dd>
									
									
										<dt><spring:message code="expedient.metadades.nti.camp.eni.identificador"/></dt>
										<dd>${arxiuDetall.eniIdentificador != null ? arxiuDetall.eniIdentificador : '--'}</dd>
									
									<c:if test="${not empty arxiuDetall.eniOrgans}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.organs"/></dt>
											<dd>
												<c:if test="${empty arxiuDetall.eniOrgans}">--</c:if>
												<c:forEach var="organ" items="${arxiuDetall.eniOrgans}" varStatus="status">
													${organ}<c:if test="${not status.last}">,</c:if>
												</c:forEach>
											</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniDataObertura}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.data.obertura"/></dt>
											<dd><c:choose><c:when test="${not empty arxiuDetall.eniDataObertura}"><fmt:formatDate value="${arxiuDetall.eniDataObertura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniClassificacio}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.classificacio"/></dt>
											<dd>${arxiuDetall.eniClassificacio != null ? arxiuDetall.eniClassificacio : '--'}</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniEstat}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.estat"/></dt>
											<dd>${arxiuDetall.eniEstat != null ? arxiuDetall.eniEstat : '--'}</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniDataCaptura}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.data.captura"/></dt>
											<dd><c:choose><c:when test="${not empty arxiuDetall.eniDataCaptura}"><fmt:formatDate value="${arxiuDetall.eniDataCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniOrigen}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.origen"/></dt>
											<dd>${arxiuDetall.eniOrigen != null ? arxiuDetall.eniOrigen : '--'}</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniEstatElaboracio}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.estat.elab"/></dt>
											<dd>${arxiuDetall.eniEstatElaboracio != null ? arxiuDetall.eniEstatElaboracio : '--'}</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniTipusDocumental}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.tipus.doc"/></dt>
											<dd>${arxiuDetall.eniTipusDocumental != null ? arxiuDetall.eniTipusDocumental : '--'}</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniFormat}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.format.nom"/></dt>
											<dd>${arxiuDetall.eniFormat != null ? arxiuDetall.eniFormat : '--'}</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniExtensio}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.format.ext"/></dt>
											<dd>${arxiuDetall.eniExtensio != null ? arxiuDetall.eniExtensio : '--'}</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniInteressats}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.interessats"/></dt>
											<dd>
												<c:if test="${empty arxiuDetall.eniInteressats}">--</c:if>
												<c:forEach var="interessat" items="${arxiuDetall.eniInteressats}" varStatus="status">
													${interessat}<c:if test="${not status.last}">,</c:if>
												</c:forEach>
											</dd>
										
									</c:if>
									<c:if test="${not empty arxiuDetall.eniDocumentOrigenId}">
										
											<dt><spring:message code="expedient.metadades.nti.camp.eni.doc.orig.id"/></dt>
											<dd>${arxiuDetall.eniDocumentOrigenId != null ? arxiuDetall.eniDocumentOrigenId : '--'}</dd>
										
									</c:if>
									
										<dt><spring:message code="expedient.metadades.nti.camp.eni.firma.csv"/></dt>
										<dd>${arxiuDetall.eniCsv != null ? arxiuDetall.eniCsv : '--'}</dd>
									
									
										<dt><spring:message code="expedient.metadades.nti.camp.eni.firma.csvdef"/></dt>
										<dd>${arxiuDetall.eniCsvDef != null ? arxiuDetall.eniCsvDef : '--'}</dd>
									
									
										<dt><spring:message code="expedient.metadades.nti.camp.arxiuEstat"/></dt>
										<dd>${arxiuDetall.arxiuEstat != null ? arxiuDetall.arxiuEstat : "--"}</dd>
									
								</dl>
							</c:if>
						</div>
						
						<%-- Fills --%>
						<c:if test="${not empty arxiuDetall.fills}">
						<div id="documentDetall_fills_${detall.documentStoreId}" class="tab-pane">
							<dl class="dl-horizontal">
								<c:forEach var="fill" items="${arxiuDetall.fills}" varStatus="status">
									
										<dt>${fill.tipus}</dt>
										<dd>${fill.nom}</dd>
									
								</c:forEach>
							</dl>
						</div>
						</c:if>
	
						<%-- Firmes --%>
						<c:if test="${not empty arxiuDetall.firmes}">
						<div id="documentDetall_firmes_${detall.documentStoreId}" class="tab-pane">
							<table class="table table-bordered">
								<thead>
									<tr>
										<th><spring:message code="expedient.metadades.nti.camp.firma.tipus"/></th>
										<th><spring:message code="expedient.metadades.nti.camp.firma.perfil"/></th>
										<th><spring:message code="expedient.metadades.nti.camp.firma.contingut"/></th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="${arxiuDetall.firmes}" var="firma" varStatus="status">
										<tr>
											<td>${firma.tipus}</td>
											<td>${firma.perfil}</td>
											<td>
												<c:choose>
													<c:when test="${firma.tipus == 'CSV'}">${firma.contingutComString}</c:when>
													<c:when test="${firma.tipus == 'XADES_DET' or firma.tipus == 'CADES_DET'}">
														<a href="<c:url value='/v3/expedient/${expedientId}/document/${detall.documentStoreId}/firma/${status.index}/descarregar'></c:url>" class="btn btn-default btn-sm pull-right">
															<span class="fa fa-download"  title="<spring:message code="comu.boto.descarregar"/>"></span>
														</a>
													</c:when>
													<c:otherwise></c:otherwise>
												</c:choose>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
								
						</div>
						</c:if>
	
						<%-- Metadades Arxiu --%>
						<c:if test="${not empty arxiuDetall.metadadesAddicionals}">
						<div id="documentDetall_metadades_${detall.documentStoreId}" class="tab-pane">
							<dl class="dl-horizontal">
							<c:forEach var="metadada" items="${arxiuDetall.metadadesAddicionals}" varStatus="status">
								
									<dt>${metadada.key}</dt>
									<dd>${metadada.value}</dd>
								
							</c:forEach>
							</dl>
						</div>
						</c:if>
						
					</c:if>
					
				</div>		
						
			<hr class="dark horizontal my-0"/>
			<c:choose>
				<c:when test="${detall.arxiu}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">ARXIVAT</span></p></c:when>
				<c:when test="${detall.nti}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">CUSTODIAT</span></p></c:when>
				<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO ARXIVAT ni CUSTODIAT</span></p></c:otherwise>
			</c:choose>				
		</div>
		
	
		<%-- ANOTACIO => detall.anotacio --%>
		<div id="contingut-documentDetall-anotacions-${detall.documentStoreId}" class="tab-pane">
			<c:set var="anotacio" value="${detall.anotacio}"/>
			<c:choose>
				<c:when test="${not empty anotacio.annexos}">
					<ul class="nav nav-pills" role="tablist">
						<li role="presentation" class="active"><a class="pill-link" href="#document_${detall.documentStoreId}_antotacio_${anotacio.id}_informacio" aria-controls="nti" role="tab" data-toggle="tab"><spring:message code="anotacio.detalls.pipella.informacio"/></a></li>
						<li role="presentation"><a class="pill-link" href="#document_${detall.documentStoreId}_antotacio_${anotacio.id}_interessats" aria-controls="info" role="tab" data-toggle="tab"><spring:message code="anotacio.detalls.pipella.interessats"/> <span class="badge">${fn:length(anotacio.interessats)}</span></a></li>
						<li role="presentation"><a class="pill-link" href="#document_${detall.documentStoreId}_antotacio_${anotacio.id}_annexos" aria-controls="fills" role="tab" data-toggle="tab">
							<c:choose>
								<c:when test="${anotacio.errorAnnexos || anotacio.annexosInvalids}"><span class="fa fa-warning text-danger"></span></c:when>
								<c:when test="${anotacio.annexosEsborranys}"><span class="fa fa-warning text-warning"></span></c:when>
							</c:choose>
							<spring:message code="anotacio.detalls.pipella.annexos"/> <span class="badge">${fn:length(anotacio.annexos)}</span></a></li>
						<c:if test="${not empty anotacio.distibucioErrorNotificacio}"><li role="presentation"><a class="pill-link" href="#error_${anotacio.id}" aria-controls="firmes" role="tab" data-toggle="tab"><spring:message code="anotacio.detalls.pipella.error"/></a></li></c:if>
					</ul>
	
					<%-- Informació --%>
					<div class="tab-content">
					
						<div id="document_${detall.documentStoreId}_antotacio_${anotacio.id}_informacio" class="tab-pane in active">
							<dl class="dl-horizontal">
								
									<dt><spring:message code="anotacio.detalls.camp.tipus"/></dt>
									<dd><spring:message code="anotacio.detalls.entrada"/></dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.numero"/></dt>
									<dd>${anotacio.identificador != null ? anotacio.identificador : '--'}</dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.data"/></dt>
									<dd><c:choose><c:when test="${not empty anotacio.data}"><fmt:formatDate value="${anotacio.data}" pattern="dd/MM/yyyy HH:mm:ss"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
								
								
									<dt><spring:message code="anotacio.detalls.camp.estat"/></dt>
									<dd><c:choose><c:when test="${not empty anotacio.estat}"><spring:message code="enum.anotacio.estat.${anotacio.estat}"/></c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
									<c:choose>
										<c:when test="${anotacio.estat == 'ERROR_PROCESSANT'}">
											<div class="d-flex flex-column text-sm text-danger">
												<span class="fa fa-exclamation-triangle"></span>
												<spring:message code="anotacio.detalls.errorProcessament" arguments="${anotacio.errorProcessament}"/>
											</div>
										</c:when>
										<c:when test="${anotacio.estat == 'COMUNICADA'}">
											<div class="d-flex flex-column text-sm text-warning" <c:if test="${anotacio.consultaError != null}">title="${anotacio.consultaError}"</c:if>>
												<span class="fa fa-exclamation-triangle"></span>
												<spring:message code="anotacio.detalls.consulta" arguments="${anotacio.consultaIntents},${ maxConsultaIntents}"/>
											</div>
											<span class="pull-right" id="psignaEstat">PENDENT</span>
										</c:when>
									</c:choose>
								
							</dl>
	
							<hr class="dark horizontal my-0">
	
							<div class="row">
							
								<div class="col-sm-6">
							
									<b><spring:message code="anotacio.detalls.titol.obligatories"/></b>
									
									<dl class="dl-horizontal">
								
										<dt><spring:message code="anotacio.detalls.camp.oficina"/></dt>
										<dd><c:choose><c:when test="${anotacio.oficinaCodi != null}">${anotacio.oficinaDescripcio} (${anotacio.oficinaCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
									
									
										<dt><spring:message code="anotacio.detalls.camp.llibre"/></dt>
										<dd><c:choose><c:when test="${anotacio.llibreCodi != null}">${anotacio.llibreDescripcio} (${anotacio.llibreCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
									
									
										<dt><spring:message code="anotacio.detalls.camp.extracte"/></dt>
										<dd>${anotacio.extracte != null ? anotacio.extracte : '--'}</dd>
									
									
										<dt><spring:message code="anotacio.detalls.camp.docfis"/></dt>
										<dd><c:choose><c:when test="${anotacio.docFisicaCodi != null}">${anotacio.docFisicaCodi} - ${anotacio.docFisicaDescripcio})</c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
									
									
										<dt><spring:message code="anotacio.detalls.camp.desti"/></dt>
										<dd><c:choose><c:when test="${anotacio.destiCodi != null}">${anotacio.destiDescripcio} (${anotacio.destiCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
									
									
										<dt><spring:message code="anotacio.detalls.camp.assumpte.tipus"/></dt>
										<dd><c:choose><c:when test="${anotacio.assumpteTipusDescripcio != null}">${anotacio.destiDescripcio} (${anotacio.assumpteTipusCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
									
									
										<dt><spring:message code="anotacio.detalls.camp.idioma"/></dt>
										<dd><c:choose><c:when test="${anotacio.idiomaDescripcio != null}">${anotacio.destiDescripcio} (${anotacio.idiomaCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
									
								</dl>
										
								</div>
								
								<div class="col-sm-6">
								
									<b><spring:message code="anotacio.detalls.titol.opcionals"/></b>

									<dl class="dl-horizontal">
										
											<dt><spring:message code="anotacio.detalls.camp.procediment.codi"/></dt>
											<dd>${anotacio.procedimentCodi != null ? anotacio.procedimentCodi : '--'}</dd>
										
										
											<dt><spring:message code="anotacio.detalls.camp.assumpte.codi"/></dt>
											<dd>${anotacio.assumpteCodiCodi != null ? anotacio.assumpteCodiCodi : '--'}</dd>
										
										
											<dt><spring:message code="anotacio.detalls.camp.refext"/></dt>
											<dd>${anotacio.refExterna != null ? anotacio.refExterna : '--'}</dd>
										
										
											<dt><spring:message code="anotacio.detalls.camp.numexp"/></dt>
											<dd>${anotacio.expedientNumero != null ? anotacio.expedientNumero : '--'}</dd>
										
										
											<dt><spring:message code="anotacio.detalls.camp.transport.tipus"/></dt>
											<dd><c:choose><c:when test="${anotacio.transportTipusCodi != null}">${anotacio.transportTipusDescripcio} (${anotacio.transportTipusCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></dd>
										
										
											<dt><spring:message code="anotacio.detalls.camp.transport.num"/></dt>
											<dd>${anotacio.transportNumero != null ? anotacio.transportNumero : '--'}</dd>
										
										
											<dt><spring:message code="anotacio.detalls.camp.origen.num"/></dt>
											<dd>${anotacio.origenRegistreNumero != null ? anotacio.origenRegistreNumero : '--'}</dd>
										
										
											<dt><spring:message code="anotacio.detalls.camp.origen.data"/></dt>
											<dd><c:choose><c:when test="${not empty anotacio.origenData}"><fmt:formatDate value="${anotacio.origenData}" pattern="dd/MM/yyyy HH:mm:ss"/></c:when><c:otherwise>--</c:otherwise></c:choose></dd>
										
										
											<dt><spring:message code="anotacio.detalls.camp.observacions"/></dt>
											<dd>${anotacio.observacions != null ? anotacio.observacions : '--'}</dd>
										
									</dl>
									
								</div>
																
							</div>
	
							<hr class="dark horizontal my-0">
						</div>
	
						<%-- Interessats --%>
						<div id="document_${detall.documentStoreId}_antotacio_${anotacio.id}_interessats" class="tab-pane">
							<c:choose>
								<c:when test="${not empty anotacio.interessats}">
									<table class="table table-bordered">
										<thead>
											<tr>
												<th><spring:message code="anotacio.detalls.camp.interessat.tipus"/></th>
												<th><spring:message code="anotacio.detalls.camp.interessat.document"/></th>
												<th><spring:message code="anotacio.detalls.camp.interessat.nom"/></th>
												<th style="width: 50px;"></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="interessat" items="${anotacio.interessats}" varStatus="status">
												<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
												
													<td><spring:message code="anotacio.interessat.tipus.enum.${interessat.tipus}"/></td>
													<td>${interessat.documentTipus}: ${interessat.documentNumero}</td>
													<td>
														<c:choose>
															<c:when test="${interessat.tipus == 'PERSONA_FISICA'}">${interessat.nom} ${interessat.llinatge1} ${interessat.llinatge2}</c:when>
															<c:otherwise>${interessat.raoSocial}</c:otherwise>
														</c:choose>
													</td>
													<td>
														<c:if test="${interessat.tipus != 'ADMINISTRACIO'}">
															<button type="button" class="btn btn-default desplegable fr10" href="#document_${detall.documentStoreId}_antotacio_${anotacio.id}_detalls_${status.index}" data-toggle="collapse" aria-expanded="false" aria-controls="document_${detall.documentStoreId}_antotacio_${anotacio.id}_detalls_${status.index}"><span class="fa fa-caret-down"></span></button>
														</c:if>
													</td>
												</tr>
												<!-- Detall interessats -->
												<tr class="collapse detall" id="document_${detall.documentStoreId}_antotacio_${anotacio.id}_detalls_${status.index}">
													<td colspan="4">
														<div class="row">
															<div class="col-xs-6">
																<dl class="dl-horizontal">
																	<dt><spring:message code="anotacio.interessat.detalls.camp.pais"/></dt><dd>${interessat.pais}</dd>
																	<dt><spring:message code="anotacio.interessat.detalls.camp.provincia"/></dt><dd>${interessat.provincia}</dd>											
																	<dt><spring:message code="anotacio.interessat.detalls.camp.municipi"/></dt><dd>${interessat.municipi}</dd>
																	<dt><spring:message code="anotacio.interessat.detalls.camp.adresa"/></dt><dd>${interessat.adresa}</dd>
																	<dt><spring:message code="anotacio.interessat.detalls.camp.codiPostal"/></dt><dd>${interessat.cp}</dd>
																</dl>
															</div>
															<div class="col-xs-6">
																<dl class="dl-horizontal">
																	<dt><spring:message code="anotacio.interessat.detalls.camp.email"/></dt><dd>${interessat.email}</dd>
																	<dt><spring:message code="anotacio.interessat.detalls.camp.telefon"/></dt><dd>${interessat.telefon}</dd>
																	<dt><spring:message code="anotacio.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty interessat.canal}"><spring:message code="anotacio.interessat.detalls.camp.canalPreferent.${interessat.canal}"/></c:if></dd>
																	<dt><spring:message code="anotacio.interessat.detalls.camp.observacions"/></dt><dd>${interessat.observacions}</dd>
																</dl>
															</div>
															
															<!-- NOU APARTAT REPRESENTANT -->
															<c:if test="${not empty interessat.representant}">
																<c:set var="representant" value="${interessat.representant}"/>
																<div class="col-xs-12">
																	<table class="table table-bordered">
																		<thead>
																			<tr><th colspan="4"><spring:message code="anotacio.interessat.detalls.camp.representant"/></th></tr>
																			<tr>
																				<th style="width: 150px;"><spring:message code="anotacio.detalls.camp.interessat.tipus"/></th>
																				<th style="width: 150px;"><spring:message code="anotacio.detalls.camp.interessat.document"/></th>
																				<th><spring:message code="anotacio.detalls.camp.interessat.nom"/></th>
																				<th style="width: 50px;"></th>
																			</tr>
																		</thead>
																		<tbody>
																			<tr <c:if test="${status.index%2 == 0}">class="odd"</c:if>>
																				<td>
																					<spring:message code="anotacio.interessat.tipus.enum.${representant.tipus}"/>
																				</td>
																				<td>${representant.documentTipus}: ${representant.documentNumero}</td>
																				<c:choose>
																					<c:when test="${representant.tipus == 'PERSONA_FISICA'}">
																						<td>${representant.nom} ${representant.llinatge1} ${representant.llinatge2}</td>
																					</c:when>
																					<c:otherwise>
																						<td>${representant.raoSocial}</td>
																					</c:otherwise>
																				</c:choose>
																				<td>
																					<c:if test="${representant.tipus != 'ADMINISTRACIO'}">
																						<button type="button" class="btn btn-default desplegable" href="#document_${detall.documentStoreId}_antotacio_${anotacio.id}_detalls_${status.index}_representant" data-toggle="collapse" aria-expanded="false" aria-controls="document_${detall.documentStoreId}_antotacio_${anotacio.id}_detalls_${status.index}_representant">
																							<span class="fa fa-caret-down"></span>
																						</button>
																					</c:if>
																				</td>
																			</tr>
																			<tr class="collapse detall" id="document_${detall.documentStoreId}_antotacio_${anotacio.id}_detalls_${status.index}_representant">
																				<td colspan="4">
																					<div class="row">
																						<div class="col-xs-6">
																							<dl class="dl-horizontal">
																								<dt><spring:message code="anotacio.interessat.detalls.camp.pais"/></dt><dd>${representant.pais}</dd>
																								<dt><spring:message code="anotacio.interessat.detalls.camp.provincia"/></dt><dd>${representant.provincia}</dd>											
																								<dt><spring:message code="anotacio.interessat.detalls.camp.municipi"/></dt><dd>${representant.municipi}</dd>
																								<dt><spring:message code="anotacio.interessat.detalls.camp.adresa"/></dt><dd>${representant.adresa}</dd>
																								<dt><spring:message code="anotacio.interessat.detalls.camp.codiPostal"/></dt><dd>${representant.cp}</dd>
																							</dl>
																						</div>
																						<div class="col-xs-6">
																							<dl class="dl-horizontal">
																								<dt><spring:message code="anotacio.interessat.detalls.camp.email"/></dt><dd>${representant.email}</dd>
																								<dt><spring:message code="anotacio.interessat.detalls.camp.telefon"/></dt><dd>${representant.telefon}</dd>
																								<dt><spring:message code="anotacio.interessat.detalls.camp.canalPreferent"/></dt><dd><c:if test="${not empty representant.canal}"><spring:message code="anotacio.interessat.detalls.camp.canalPreferent.${representant.canal}"/></c:if></dd>
																								<dt><spring:message code="anotacio.interessat.detalls.camp.observacions"/></dt><dd>${representant.observacions}</dd>
																							</dl>
																						</div>
																					</div>
																				</td>						
																			</tr>
																		</tbody>
																	</table>
																</div>
															</c:if>
														</div>
													</td>						
												</tr>
												<!-- Detall interessats -->												
											</c:forEach>											
										</tbody>
									</table>
							</c:when>
							<c:otherwise>
								<dt><spring:message code="anotacio.interessat.buit"/></dt>
							</c:otherwise>
							</c:choose>
						</div>
	
						<%-- Annexos --%>
						<div id="document_${detall.documentStoreId}_antotacio_${anotacio.id}_annexos" class="tab-pane">
							<c:choose>
								<c:when test="${not empty anotacio.annexos}">
									<c:forEach var="annex" items="${anotacio.annexos}" varStatus="status">

										<script type="text/javascript">
											$(document).ready(function() {
											    $("#collapse-registre-firmes-annex-${status.index}-anotacio-${anotacio.id}-document-${detall.documentStoreId}").on('show.bs.collapse', function(data){  	
												    if (!$(this).data("loaded")) {
												        var annexId = $(this).data("annexId");
												        $(this).append("<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x' title='<spring:message code="anotacio.annex.detalls.annex.firmes.consultant" />'/></div>");
												        $(this).load('<c:url value="/nodeco/v3/anotacio/${anotacio.id}/annex/"/>' + ${annex.id} + '/firmaInfo');
												        $(this).data("loaded", true);
												    }
											    });
										 	});
										</script>
									
										<div class="panel panel-default">
											<div class="panel-heading">
												<h3 class="panel-title">
													<span class="fa fa-file"></span>
													${annex.titol}
													<c:if test="${annex.error != null }">
														<span class="fa fa-warning text-danger" title="<spring:message code="anotacio.annex.detalls.annex.error" arguments="${annex.error}"/>"></span>
													</c:if>
													<c:if test="${!annex.documentValid}">
														<span class="fa fa-warning text-danger" title="<spring:message code="anotacio.annex.detalls.annex.invalid" arguments="${annex.documentError}"/>"></span>
													</c:if>
													<c:if test="${annex.arxiuEstat == 'ESBORRANY'}">
														<span class="fa fa-warning text-warning" title="<spring:message code="anotacio.annex.detalls.annex.esborrany"/>"></span>
													</c:if>
													<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-annex-${status.index}-anotacio-${anotacio.id}-document-${detall.documentStoreId}"><span class="fa fa-chevron-down"></span></button>
												</h3>
											</div>
				 							<div id="collapse-annex-${status.index}-anotacio-${anotacio.id}-document-${detall.documentStoreId}" class="panel-collapse collapse collapse-annex" role="tabpanel" aria-labelledby="dadesAnnex${status.index}-anotacio-${anotacio.id}-document-${detall.documentStoreId}" data-registre-id="${anotacio.id}"  data-fitxer-arxiu-uuid="${annex.uuid}">
								
												<div>
													<c:if test="${annex.estat == 'PENDENT' && not empty annex.error}">
													
														<div class="alert well-sm alert-danger alert-dismissable" style="margin-bottom: 0px;">
															<span class="fa fa-exclamation-triangle"></span>
															<spring:message code="anotacio.annex.detalls.annex.processament.error" />
															<a href="<c:url value="/v3/anotacio/${anotacio.id}/annex/${annex.id}/reintentar"/>"
																class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span>
																<spring:message code="anotacio.annex.detalls.annex.accio.reintentar" /></a>
														</div>
														<pre style="height: 200px; background-color: white; margin-bottom: 0px;">${annex.error}</pre>
													</c:if>
												</div>
												<table class="table table-bordered">
												<tbody>														
													<tr>
														<td><strong><spring:message code="anotacio.annex.detalls.camp.eni.data.captura"/></strong></td>
														<td><c:if test="${not empty annex.ntiFechaCaptura}"><fmt:formatDate value="${annex.ntiFechaCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if></td>
													</tr>
													<tr>
														<td><strong><spring:message code="anotacio.annex.detalls.camp.eni.origen"/></strong></td>
														<td><c:if test="${not empty annex.ntiOrigen}">${annex.ntiOrigen}</c:if></td>
													</tr>
													<tr>
														<td><strong><spring:message code="anotacio.annex.detalls.camp.eni.tipus.documental"/></strong></td>
														<td><c:if test="${not empty annex.ntiTipoDocumental}"><spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.${annex.ntiTipoDocumental}"/></c:if></td>
													</tr>
													<tr>
														<td><strong><spring:message code="anotacio.annex.detalls.camp.sicres.tipus.document"/></strong></td>
														<td><c:if test="${not empty annex.sicresTipoDocumento}"><spring:message code="anotacio.annex.detalls.camp.sicresTipusDocument.${annex.sicresTipoDocumento}"/></c:if></td>
													</tr>
													<tr>
														<td><strong><spring:message code="anotacio.annex.detalls.camp.arxiu.uuid"/></strong></td>
														<td>
															${annex.uuid}
															<c:if test="${annex.uuid == null }">
																<span class="fa fa-warning text-warning" title="<spring:message code="anotacio.annex.detalls.camp.arxiu.uuid.buit.avis"/>"></span>
															</c:if>
														</td>
													</tr>
												
													<c:if test="${not empty annex.observacions}">
														<tr>
															<td><strong><spring:message code="anotacio.annex.detalls.camp.observacions"/></strong></td>
															<td>${annex.observacions}</td>
														</tr>
													</c:if>
													<tr>
														<td><strong><spring:message code="anotacio.annex.detalls.camp.fitxer"/></strong></td>
														<td>
															${annex.nom}
															<a href='<c:url value="/v3/anotacio/${anotacio.id}/annex/${annex.id}/descarregar"></c:url>' 
															class="btn btn-default btn-sm pull-right">
																<span class="fa fa-download" title="<spring:message code="anotacio.annex.detalls.camp.fitxer.descarregar"/>"></span>
															</a>
														</td>
													</tr>
													<tr>
														<td><strong><spring:message code="anotacio.annex.detalls.camp.estat"/></strong></td>
														<td>
															${annex.estat}
															<c:if test="${annex.error != null}">
															<span 
																class="fa fa-exclamation-triangle text-danger" 
																title="${annex.error}"></span>
															</c:if>
														</td>
													</tr>
													<tr>
														<td><strong><spring:message code="anotacio.annex.detalls.camp.estat.arxiu"/></strong></td>
														<td>
															${annex.arxiuEstat}
															<c:if test="${annex.arxiuEstat == 'ESBORRANY'}">
															<span 
																class="fa fa-exclamation-triangle text-warning" 
																title="<spring:message code='anotacio.annex.detalls.camp.estat.arxiu.esborrany.avis'></spring:message>"></span>
															</c:if>
														</td>
													</tr>
													<tr>
														<td><strong><spring:message code="anotacio.annex.detalls.camp.valid"/></strong></td>
														<td>
															<c:choose>
																<c:when test="${annex.documentValid }">
																	<spring:message code="enum.si"></spring:message>
																</c:when>
																<c:when test="${!annex.documentValid }">
																	<span 
																		class="fa fa-exclamation-triangle text-danger"></span>
																	<spring:message code="enum.no"></spring:message>
																	: ${annex.documentError}
																</c:when>
															</c:choose>
														</td>
													</tr>
													<tr>
														<td colspan="2">
															<div class="panel panel-default">
																<div class="panel-heading">
																	<h3 class="panel-title">
																		<span class="fa fa-certificate"></span>
																		<spring:message code="anotacio.annex.detalls.camp.firmes"/>
																		<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-registre-firmes-annex-${status.index}-anotacio-${anotacio.id}-document-${detall.documentStoreId}"><span class="fa fa-chevron-down"></span></button>
																	</h3>
																</div>
																<div id="collapse-registre-firmes-annex-${status.index}-anotacio-${anotacio.id}-document-${detall.documentStoreId}" class="panel-collapse collapse collapse-annex collapse-registre-firmes" role="tabpanel" data-annex-id="${annex.id}"> 
																</div> 
															</div>
														</td>
													</tr>
												
												</table>
				 							</div> 
										</div>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${not empty annexosErrorMsg}">
											<div class="row col-xs-12">
												<div class="alert alert-danger">
													${annexosErrorMsg}
												</div>
											</div>						
										</c:when>
										<c:otherwise>
											<div class="row col-xs-12">
												<div class="well">
													<spring:message code="anotacio.annex.buit"/>
												</div>
											</div>
										</c:otherwise>
									</c:choose>				
								</c:otherwise>
							</c:choose>
						</div>
	
						<%-- Error --%>
						<div id="error_${anotacio.id}" class="tab-pane">
							<dl class="dl-horizontal">
								
	<%--											<dt><spring:message code="anotacio.detalls.errorNotifacio"/></dt>--%>
	<%--											<dl>--%>
	<%--												<a href="<c:url value="/v3/anotacio/${anotacio.id}/reintentarNotificar"/>"--%>
	<%--												   class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span>--%>
	<%--													<spring:message code="anotacio.detalls.annex.accio.reintentar" /></a>--%>
	<%--											</dl>--%>
								
								
									<dt><spring:message code="anotacio.detalls.errorNotifacio"/></dt>
									<dd><pre>${anotacio.distibucioErrorNotificacio}</pre></dd>
							</dl>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<span class="text-dark font-weight-bold">Sense ANOTACIÓ</span>
				</c:otherwise>
			</c:choose>
			<hr class="dark horizontal my-0"/>
			<div class="card-footer p-3">
				<c:choose>
					<c:when test="${detall.deAnotacio}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">PROVÉ d'una ANOTACIÓ</span></p></c:when>
					<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO PROVÉ d'una ANOTACIÓ</span></p></c:otherwise>
				</c:choose>
			</div>
		</div>
		
		<%-- PREVISUALITZACIO--%>
		<div id="contingut-documentDetall-previsualitzacio-${detall.documentStoreId}" class="tab-pane">
		
			<%-- Depenent del format del document serà previsualització o descàrrega--%>
			<c:choose>
				<c:when test="${detall.extensio == 'pdf' or detall.extensio == 'pdt' or detall.extensio == 'docx'}">
					<div class="viewer mtop-4" data-documentid="${detall.documentStoreId}" style="display: none; width: 100%;">
						<iframe class="viewer-iframe" width="100%" height="540" frameBorder="0" style="padding: 15px;"></iframe>
					</div>
				</c:when>
				<c:otherwise>
					<a href="${expedientId}/document/${detall.id}/descarregar" class="position-absolute bg-gradient-info shadow-info text-white font-weight-bold pointer">
						<span class="fa fa-download fa-inverse fa-1x"></span>&nbsp;<spring:message code="expedient.document.descarregar"/></a>
				</c:otherwise>
			</c:choose>			
		
		</div>			
	</div>