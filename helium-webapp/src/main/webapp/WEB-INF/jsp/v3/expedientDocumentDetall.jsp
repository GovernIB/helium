<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>

<%--<link href="<c:url value="/css/exp-doc.css"/>" rel="stylesheet"/>--%>


<script type="text/javascript">
	// <![CDATA[
	$(document).ready( function() {

	});
	// ]]>
</script>


	<!-- Tabuladors -->		
	
	<ul class="nav nav-tabs nav-justified" role="tablist">
		<li id="pipella-documentDetall-registre-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-registre-${detall.documentStoreId}" role="button" data-toggle="tab" class="active">
				<span class="fa fa-book <c:choose><c:when test="${detall.registrat}">fa-success</c:when><c:otherwise>fa-default</c:otherwise></c:choose>"/>
				<spring:message code="expedient.document.detall.pipella.registre"/>
			</a>
		</li>
		<li id="pipella-documentDetall-signatura-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-signatura-${detall.documentStoreId}" role="tab" data-toggle="tab">
				<span class="fa fa-certificate <c:choose><c:when test="${detall.signat}">fa-success</c:when><c:otherwise>fa-default</c:otherwise></c:choose>"/>
				<spring:message code="expedient.document.detall.pipella.signatura"/>
			</a>
		</li>
		<li id="pipella-documentDetall-portasignatura-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-portasignatura-${detall.documentStoreId}" role="tab" data-toggle="tab">
				<span class="fa fa-clock-o <c:choose><c:when test="${detall.psignaPendent}">fa-success</c:when><c:otherwise>fa-default</c:otherwise></c:choose>"/>
				<spring:message code="expedient.document.detall.pipella.portasignatura"/>
			</a>
		</li>
		<li id="pipella-documentDetall-notificacions-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-notificacions-${detall.documentStoreId}" role="tab" data-toggle="tab">
				<span class="fa fa-paper-plane-o <c:choose><c:when test="${detall.notificat}">fa-success</c:when><c:otherwise>fa-default</c:otherwise></c:choose>"/>
				<spring:message code="expedient.document.detall.pipella.notificacions"/>
			</a>
		</li>
		<li id="pipella-documentDetall-arxiu-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-arxiu-${detall.documentStoreId}" role="tab" data-toggle="tab">
				<span class="fa fa-bookmark <c:choose><c:when test="${detall.nti || detall.arxiu}">fa-success</c:when><c:otherwise>fa-default</c:otherwise></c:choose>"/>
				<c:choose>
					<c:when test="${detall.arxiu}">Arxiu</c:when>
					<c:when test="${detall.nti}">NTI</span></p></c:when>
					<c:otherwise>NTI / Arxiu</c:otherwise>
				</c:choose>
			</a>
		</li>
		<li id="pipella-documentDetall-anotacions-${detall.documentStoreId}">
			<a href="#contingut-documentDetall-anotacions-${detall.documentStoreId}" role="tab" data-toggle="tab">
				<span class="fa fa-file-text <c:choose><c:when test="${detall.deAnotacio}">fa-success</c:when><c:otherwise>fa-default</c:otherwise></c:choose>"/>
				<spring:message code="expedient.document.detall.pipella.anotacions"/>
			</a>
		</li>
	
		<li id="pipella-documentDetall-previsualitzacio-${detall.documentStoreId}" role="button">
			<a href="#contingut-documentDetall-previsualitzacio-${detall.documentStoreId}" class="previs-icon" role="tab" data-toggle="tab">
				<span class="fa fa-eye fa-inverse<c:choose><c:when test="${detall.extensio == 'pdf' or detall.extensio == 'pdt' or detall.extensio == 'docx'}">fa-success</c:when><c:otherwise>fa-default</c:otherwise></c:choose>"/>
				<spring:message code="expedient.document.previsualitzacio"/>
			</a>
		</li>
					
	</ul>
	
	
	<div class="tab-content">
	
		<%-- REGISTRE => detall.registreDetall --%>
		
		<div id="contingut-documentDetall-registre-${detall.documentStoreId}" class="tab-pane">
			<c:set var="registre" value="${detall.registreDetall}"/>
			<ul class="list-group">
				<%-- Número de registre--%>
				<li class="list-group-item d-flex justify-content-between border-0">
					<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.info.numero_registre"/></div>
					<div class="d-flex flex-column text-sm">${registre != null ? registre.registreNumero : '--'}</div>
				</li>
				<%-- Data de registre--%>
				<li class="list-group-item d-flex justify-content-between border-0">
					<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.info.camp.registre.data"/></div>
					<div class="d-flex flex-column text-sm">${registre != null ? '<fmt:formatDate value="${registre.registreData}" pattern="dd/MM/yyyy HH:mm"/>' : '--'}</div>
				</li>
				<%-- Oficina de registre--%>
				<li class="list-group-item d-flex justify-content-between border-0">
					<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.info.camp.registre.oficina"/></div>
					<div class="d-flex flex-column text-sm">${registre != null ? registre.registreOficinaNom : '--'}</div>
				</li>
				<%-- Tipus de registre--%>
				<li class="list-group-item d-flex justify-content-between border-0">
					<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.info.camp.registre.tipus"/></div>
					<div class="d-flex flex-column text-sm">${registre != null ? (registre.registreEntrada ? '<spring:message code="expedient.info.camp.registre.tipus.entrada"/>' : '<spring:message code="expedient.info.camp.registre.tipus.sortida"/>') : '--'}</div>
				</li>
			</ul>
	
			<hr class="dark horizontal my-0"/>
			<c:choose>
				<c:when test="${detall.registrat}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">REGISTRAT</span></p></c:when>
				<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO REGISTRAT</span></p></c:otherwise>
			</c:choose>
	
		</div>
					
			
		<%-- SIGNATURA => detall.signaturaValidacioDetall --%>
		
		<div id="contingut-documentDetall-signatura-${detall.documentStoreId}" class="tab-pane">
			<c:set var="signatura" value="${detall.signaturaValidacioDetall}"/>
			<ul class="list-group">
				<%-- --%>
				<li class="list-group-item d-flex justify-content-between border-0">
					<div class="d-flex flex-column text-dark font-weight-bold text-sm">Verificació de la signatura</div>
					<div class="d-flex flex-column text-sm"><c:choose><c:when test="${signatura != null and not empty signatura.urlVerificacio}"><a href="${signatura.urlVerificacio}" target="blank"><span class="fa fa-certificate"/> <span class="fa fa-external-link"></span></a></c:when><c:otherwise>--</c:otherwise></c:choose></div>
				</li>
				<c:if test="${not empty signatura.tokenSignatura}">
					<c:url value="/v3/expedient/document/arxiuMostrar" var="downloadUrl"><c:param name="token" value="${signatura.tokenSignatura}"/></c:url>
					<li class="list-group-item d-flex justify-content-between border-0">
						<div class="d-flex flex-column text-dark font-weight-bold text-sm">Verificació de la signatura</div>
						<div class="d-flex flex-column text-sm"><c:choose><c:when test="${signatura != null and not empty signatura.urlVerificacio}"><a href="${signatura.urlVerificacio}"><span class="fa fa-external-link"></span></a></c:when><c:otherwise>--</c:otherwise></c:choose></div>
					</li>
				</c:if>
				<c:if test="not empty signatura.signatures">
					<li class="list-group-item d-flex justify-content-between border-0">
						<div class="d-flex flex-column text-dark font-weight-bold text-sm">Firmes</div>
						<div class="d-flex flex-column text-sm"></div>
					</li>
					<li class="list-group-item d-flex justify-content-between border-0">
						<div class="d-flex flex-column text-dark font-weight-bold text-sm">Responsable</div>
						<div class="d-flex flex-column text-dark font-weight-bold text-sm">NIF</div>
						<div class="d-flex flex-column text-dark font-weight-bold text-sm">Estat</div>
					</li>
					<c:forEach var="firma" items="${signatura.signatures}" varStatus="status">
						<li class="list-group-item d-flex justify-content-between border-0">
							<div class="d-flex flex-column text-sm">${firma.nomResponsable}</div>
							<div class="d-flex flex-column text-sm">${firma.nifResponsable}</div>
							<div class="d-flex flex-column text-sm">
								<c:choose>
									<c:when test="${firma.estatOk}"><span class="fa fa-check text-success"></span></c:when>
									<c:otherwise><span class="fa fa-times text-danger"></span></c:otherwise>
								</c:choose>
							</div>
						</li>
					</c:forEach>
				</c:if>
			</ul>
			<hr class="dark horizontal my-0">
			<div class="card-footer p-3">
				<c:choose>
					<c:when test="${detall.signat}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">SIGNAT</span></p></c:when>
					<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO SIGNAT</span></p></c:otherwise>
				</c:choose>
			</div>
		</div>
		
	
		<%-- PSIGNA => detall.signaturaValidacioDetall --%>
		<div id="contingut-documentDetall-portasignatura-${detall.documentStoreId}" class="tab-pane">
			<c:set var="psigna" value="${detall.psignaDetall}"/>
			<c:if test="${not empty psigna}">
				<ul class="list-group">
					<li class="list-group-item d-flex justify-content-between border-0">
						<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="common.icones.doc.psigna.id"/></div>
						<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty psigna.documentId}">${psigna.documentId}</c:when><c:otherwise>--</c:otherwise></c:choose></div>
					</li>
					<li class="list-group-item d-flex justify-content-between border-0">
						<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="common.icones.doc.psigna.data.enviat"/></div>
						<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty psigna.dataEnviat}"><fmt:formatDate value="${psigna.dataEnviat}" pattern="dd/MM/yyyy HH:mm"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
					</li>
					<li class="list-group-item d-flex justify-content-between border-0">
						<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="common.icones.doc.psigna.estat"/></div>
						<div class="d-flex flex-column text-sm">
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
						</div>
					</li>
					<c:if test="${not empty psigna.motiuRebuig}">
						<li class="list-group-item d-flex justify-content-between border-0">
							<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="common.icones.doc.psigna.motiu.rebuig"/></div>
							<div class="d-flex flex-column text-sm">${psigna.motiuRebuig != null ? psigna.motiuRebuig : '--'}</div>
						</li>
					</c:if>
					<c:if test="${not empty psigna.dataProcessamentPrimer}">
						<li class="list-group-item d-flex justify-content-between border-0">
							<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="common.icones.doc.psigna.data.proces.primer"/></div>
							<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty psigna.dataProcessamentPrimer}"><fmt:formatDate value="${psigna.dataProcessamentPrimer}" pattern="dd/MM/yyyy HH:mm"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
						</li>
					</c:if>
					<c:if test="${not empty psigna.dataProcessamentDarrer}">
						<li class="list-group-item d-flex justify-content-between border-0">
							<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="common.icones.doc.psigna.data.proces.darrer"/></div>
							<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty psigna.dataProcessamentDarrer}"><fmt:formatDate value="${psigna.dataProcessamentDarrer}" pattern="dd/MM/yyyy HH:mm"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
						</li>
					</c:if>
				</ul>
	
	<%--						<c:if test="${psigna.error && psigna.estat != 'PROCESSAT'}">--%>
	<%--							<c:if test="${expedient.permisDocManagement}">--%>
	<%--								<form id="form_psigna_${document.id}" action="<c:url value='../../v3/expedient/${expedientId}/proces/${document.processInstanceId}/document/${document.id}/psignaReintentar'/>">--%>
	<%--									<input type="hidden" name="id" value="${document.processInstanceId}"/>--%>
	<%--									<input type="hidden" name="psignaId" value="${psigna.documentId}"/>--%>
	<%--								</form>--%>
	<%--							</c:if>--%>
	<%--						</c:if>--%>
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
			<ul class="list-group">
				<li class="list-group-item d-flex justify-content-between border-0">
					<div class="d-flex flex-column text-dark font-weight-bold text-sm">Destinatari</div>
					<div class="d-flex flex-column text-dark font-weight-bold text-sm">Estat</div>
				</li>
				<c:forEach var="notificacio" items="${notificacions}" varStatus="status">
					<c:forEach var="enviament" items="${notificacio.enviaments}" varStatus="statuse">
						<li class="list-group-item d-flex justify-content-between border-0">
							<div class="d-flex flex-column text-sm">
								<span class="label label-default">${notificacio.enviamentTipus == "NOTIFICACIO" ? 'N' : 'C'}</span>
								<span>
									<h6>${enviament.titular.nomSencer}</h6>
									<p>${notificacio.enviatData}</p>
								</span>
							</div>
							<div class="d-flex flex-column text-sm">
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
												enviament.estat == 'EXPIRADA'}"><c:set var="ecolor" value="Danger"/></c:when>
								</c:choose>
								<span class="label label-${ecolor}">${enviament.estat}</span>
							</div>
						</li>
					</c:forEach>
				</c:forEach>
			</ul>
			
			<hr class="dark horizontal my-0"/>
			<div class="card-footer p-3">
				<c:choose>
					<c:when test="${detall.notificat}"><p class="mb-0">Document <span class="text-success text-sm font-weight-bolder">NOTIFICAT</span></p></c:when>
					<c:otherwise><p class="mb-0">Document <span class="text-danger text-sm font-weight-bolder">NO NOTIFICAT</span></p></c:otherwise>
				</c:choose>
			</div>
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
							<ul class="list-group">
								<%-- --%>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.version"/></div>
									<div class="d-flex flex-column text-sm">${ntiDetall.ntiVersion != null ? ntiDetall.ntiVersion : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.identificador"/></div>
									<div class="d-flex flex-column text-sm">${ntiDetall.ntiIdentificador != null ? ntiDetall.ntiIdentificador : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.organo"/></div>
									<div class="d-flex flex-column text-sm">${ntiDetall.ntiOrgano != null ? ntiDetall.ntiOrgano : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.fecha.captura"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty detall.dataCreacio}"><fmt:formatDate value="${detall.dataCreacio}" pattern="dd/MM/yyyy HH:mm"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.origen"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty ntiDetall.ntiOrigen}"><spring:message code="nti.document.origen.${ntiDetall.ntiOrigen}"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.estado.elaboracion"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty ntiDetall.ntiEstadoElaboracion}"><spring:message code="nti.document.estado.elaboracion.${ntiDetall.ntiEstadoElaboracion}"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.nombre.formato"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty ntiDetall.ntiNombreFormato}"><spring:message code="nti.document.format.${ntiDetall.ntiNombreFormato}"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.tipo.documental"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty ntiDetall.ntiTipoDocumental}"><spring:message code="nti.document.tipo.documental.${ntiDetall.ntiTipoDocumental}"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.iddoc.origen"/></div>
									<div class="d-flex flex-column text-sm">${ntiDetall.ntiIdDocumentoOrigen != null ? ntiDetall.ntiIdDocumentoOrigen : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.tipo.firma"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty ntiDetall.ntiTipoFirma}">${ntiDetall.ntiTipoFirma}<c:if test="${not empty detall.ntiCsv}">, CSV</c:if></c:when><c:otherwise>--</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.csv"/></div>
									<div class="d-flex flex-column text-sm">${ntiDetall.ntiCsv != null ? ntiDetall.ntiCsv : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="document.metadades.nti.defgen.csv"/></div>
									<div class="d-flex flex-column text-sm">${ntiDetall.ntiDefinicionGenCsv != null ? ntiDetall.ntiDefinicionGenCsv : '--'}</div>
								</li>
							</ul>
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
							<ul class="list-group">
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.identificador"/></div>
									<div class="d-flex flex-column text-sm">${arxiuDetall.identificador != null ? arxiuDetall.identificador : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.nom"/></div>
									<div class="d-flex flex-column text-sm">${arxiuDetall.nom != null ? arxiuDetall.nom : '--'}</div>
								</li>
								<c:if test="${not empty arxiuDetall.serieDocumental}">
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.serie.doc"/></div>
										<div class="d-flex flex-column text-sm">${arxiuDetall.serieDocumental != null ? arxiuDetall.serieDocumental : '--'}</div>
									</li>
								</c:if>
							</ul>

							<c:if test="${not empty arxiuDetall.contingutTipusMime or not empty arxiuDetall.contingutArxiuNom}">
							<hr class="dark horizontal my-0">

							<div class="text-dark font-weight-bold"><spring:message code="expedient.metadades.nti.grup.contingut"/></div>
							<ul class="list-group">
								<c:if test="${not empty arxiuDetall.contingutTipusMime}">
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.contingut.tipus.mime"/></div>
									<div class="d-flex flex-column text-sm">${arxiuDetall.contingutTipusMime != null ? arxiuDetall.contingutTipusMime : '--'}</div>
								</li>
								</c:if>
								<c:if test="${not empty arxiuDetall.contingutArxiuNom}">
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.contingut.arxiu.nom"/></div>
									<div class="d-flex flex-column text-sm">${arxiuDetall.contingutArxiuNom != null ? arxiuDetall.contingutArxiuNom : '--'}</div>
								</li>
								</c:if>
							</ul>
							</c:if>

							<c:if test="${not empty arxiuDetall.eniIdentificador}">
								<hr class="dark horizontal my-0">

								<div class="text-dark font-weight-bold"><spring:message code="expedient.metadades.nti.grup.metadades"/></div>
								<ul class="list-group">
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.versio"/></div>
										<div class="d-flex flex-column text-sm">${arxiuDetall.eniVersio != null ? arxiuDetall.eniVersio : '--'}</div>
									</li>
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.identificador"/></div>
										<div class="d-flex flex-column text-sm">${arxiuDetall.eniIdentificador != null ? arxiuDetall.eniIdentificador : '--'}</div>
									</li>
									<c:if test="${not empty arxiuDetall.eniOrgans}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.organs"/></div>
											<div class="d-flex flex-column text-sm">
												<c:if test="${empty arxiuDetall.eniOrgans}">--</c:if>
												<c:forEach var="organ" items="${arxiuDetall.eniOrgans}" varStatus="status">
													${organ}<c:if test="${not status.last}">,</c:if>
												</c:forEach>
											</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniDataObertura}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.data.obertura"/></div>
											<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty arxiuDetall.eniDataObertura}"><fmt:formatDate value="${arxiuDetall.eniDataObertura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniClassificacio}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.classificacio"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniClassificacio != null ? arxiuDetall.eniClassificacio : '--'}</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniEstat}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.estat"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniEstat != null ? arxiuDetall.eniEstat : '--'}</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniDataCaptura}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.data.captura"/></div>
											<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty arxiuDetall.eniDataCaptura}"><fmt:formatDate value="${arxiuDetall.eniDataCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniOrigen}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.origen"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniOrigen != null ? arxiuDetall.eniOrigen : '--'}</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniEstatElaboracio}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.estat.elab"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniEstatElaboracio != null ? arxiuDetall.eniEstatElaboracio : '--'}</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniTipusDocumental}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.tipus.doc"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniTipusDocumental != null ? arxiuDetall.eniTipusDocumental : '--'}</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniFormat}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.format.nom"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniFormat != null ? arxiuDetall.eniFormat : '--'}</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniExtensio}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.format.ext"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniExtensio != null ? arxiuDetall.eniExtensio : '--'}</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniInteressats}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.interessats"/></div>
											<div class="d-flex flex-column text-sm">
												<c:if test="${empty arxiuDetall.eniInteressats}">--</c:if>
												<c:forEach var="interessat" items="${arxiuDetall.eniInteressats}" varStatus="status">
													${interessat}<c:if test="${not status.last}">,</c:if>
												</c:forEach>
											</div>
										</li>
									</c:if>
									<c:if test="${not empty arxiuDetall.eniDocumentOrigenId}">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.doc.orig.id"/></div>
											<div class="d-flex flex-column text-sm">${arxiuDetall.eniDocumentOrigenId != null ? arxiuDetall.eniDocumentOrigenId : '--'}</div>
										</li>
									</c:if>
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.firma.csv"/></div>
										<div class="d-flex flex-column text-sm">${arxiuDetall.eniCsv != null ? arxiuDetall.eniCsv : '--'}</div>
									</li>
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.eni.firma.csvdef"/></div>
										<div class="d-flex flex-column text-sm">${arxiuDetall.eniCsvDef != null ? arxiuDetall.eniCsvDef : '--'}</div>
									</li>
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.arxiuEstat"/></div>
										<div class="d-flex flex-column text-sm">${arxiuDetall.arxiuEstat != null ? arxiuDetall.arxiuEstat : "--"}</div>
									</li>
								</ul>
							</c:if>
						</div>
						
						<%-- Fills --%>
						<c:if test="${not empty arxiuDetall.fills}">
						<div id="documentDetall_fills_${detall.documentStoreId}" class="tab-pane">
							<ul class="list-group">
								<c:forEach var="fill" items="${arxiuDetall.fills}" varStatus="status">
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-sm">${fill.tipus}</div>
										<div class="d-flex flex-column text-sm">${fill.nom}</div>
									</li>
								</c:forEach>
							</ul>
						</div>
						</c:if>
	
						<%-- Firmes --%>
						<c:if test="${not empty arxiuDetall.firmes}">
						<div id="documentDetall_firmes_${detall.documentStoreId}" class="tab-pane">
							<ul class="list-group">
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.firma.tipus"/></div>
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.firma.perfil"/></div>
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="expedient.metadades.nti.camp.firma.contingut"/></div>
								</li>
								<c:forEach var="firma" items="${arxiuDetall.firmes}" varStatus="status">
									<li class="list-group-item d-flex justify-content-between border-0">
										<div class="d-flex flex-column text-sm">${firma.tipus}</div>
										<div class="d-flex flex-column text-sm">${firma.perfil}</div>
										<div class="d-flex flex-column text-sm">
											<c:choose>
												<c:when test="${firma.tipus == 'CSV'}">${firma.contingutComString}</c:when>
												<c:when test="${firma.tipus == 'XADES_DET' or firma.tipus == 'CADES_DET'}">
													<a href="<c:url value='/v3/expedient/${expedientId}/document/${detall.documentStoreId}/firma/${status.index}/descarregar'></c:url>" class="btn btn-default btn-sm pull-right">
														<span class="fa fa-download"  title="<spring:message code="comu.boto.descarregar"/>"></span>
													</a>
												</c:when>
												<c:otherwise></c:otherwise>
											</c:choose>
										</div>
									</li>
								</c:forEach>
							</ul>
						</div>
						</c:if>
	
						<%-- Metadades Arxiu --%>
						<c:if test="${not empty arxiuDetall.metadadesAddicionals}">
						<div id="documentDetall_metadades_${detall.documentStoreId}" class="tab-pane">
							<ul class="list-group">
							<c:forEach var="metadada" items="${arxiuDetall.metadadesAddicionals}" varStatus="status">
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm">${metadada.key}</div>
									<div class="d-flex flex-column text-sm">${metadada.value}</div>
								</li>
							</c:forEach>
							</ul>
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
						<li role="presentation" class="active"><a class="pill-link" href="#informacio_${anotacio.id}" aria-controls="nti" role="tab" data-toggle="tab"><spring:message code="anotacio.detalls.pipella.informacio"/></a></li>
						<li role="presentation"><a class="pill-link" href="#interessats_${anotacio.id}" aria-controls="info" role="tab" data-toggle="tab"><spring:message code="anotacio.detalls.pipella.interessats"/></a></li>
						<li role="presentation"><a class="pill-link" href="#annexos_${anotacio.id}" aria-controls="fills" role="tab" data-toggle="tab">
							<c:choose>
								<c:when test="${anotacio.errorAnnexos || anotacio.annexosInvalids}"><span class="fa fa-warning text-danger"></span></c:when>
								<c:when test="${anotacio.annexosEsborranys}"><span class="fa fa-warning text-warning"></span></c:when>
							</c:choose>
							<spring:message code="anotacio.detalls.pipella.annexos"/></a></li>
						<c:if test="${not empty anotacio.distibucioErrorNotificacio}"><li role="presentation"><a class="pill-link" href="#error_${anotacio.id}" aria-controls="firmes" role="tab" data-toggle="tab"><spring:message code="anotacio.detalls.pipella.error"/></a></li></c:if>
					</ul>
	
					<%-- Informació --%>
					<div class="tab-content">
						<div id="informacio_${anotacio.id}" class="tab-pane in active">
							<ul class="list-group">
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.tipus"/></div>
									<div class="d-flex flex-column text-sm"><spring:message code="anotacio.detalls.entrada"/></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.numero"/></div>
									<div class="d-flex flex-column text-sm">${anotacio.identificador != null ? anotacio.identificador : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.data"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty anotacio.data}"><fmt:formatDate value="${anotacio.data}" pattern="dd/MM/yyyy HH:mm:ss"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.estat"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty anotacio.estat}"><spring:message code="enum.anotacio.estat.${anotacio.estat}"/></c:when><c:otherwise>'--'</c:otherwise></c:choose></div>
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
								</li>
							</ul>
	
							<hr class="dark horizontal my-0">
	
							<div class="d-flex flex-column text-sm"><h6><spring:message code="anotacio.detalls.titol.obligatories"/></h6></div>
							<ul class="list-group">
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.oficina"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${anotacio.oficinaCodi != null}">${anotacio.oficinaDescripcio} (${anotacio.oficinaCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.llibre"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${anotacio.llibreCodi != null}">${anotacio.llibreDescripcio} (${anotacio.llibreCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.extracte"/></div>
									<div class="d-flex flex-column text-sm">${anotacio.extracte != null ? anotacio.extracte : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.docfis"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${anotacio.docFisicaCodi != null}">${anotacio.docFisicaCodi} - ${anotacio.docFisicaDescripcio})</c:when><c:otherwise>'--'</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.desti"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${anotacio.destiCodi != null}">${anotacio.destiDescripcio} (${anotacio.destiCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.assumpte.tipus"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${anotacio.assumpteTipusDescripcio != null}">${anotacio.destiDescripcio} (${anotacio.assumpteTipusCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.idioma"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${anotacio.idiomaDescripcio != null}">${anotacio.destiDescripcio} (${anotacio.idiomaCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></div>
								</li>
							</ul>
	
							<hr class="dark horizontal my-0">
	
							<div class="d-flex flex-column text-sm"><h6><spring:message code="anotacio.detalls.titol.opcionals"/></h6></div>
							<ul class="list-group">
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.procediment.codi"/></div>
									<div class="d-flex flex-column text-sm">${anotacio.procedimentCodi != null ? anotacio.procedimentCodi : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.assumpte.codi"/></div>
									<div class="d-flex flex-column text-sm">${anotacio.assumpteCodiCodi != null ? anotacio.assumpteCodiCodi : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.refext"/></div>
									<div class="d-flex flex-column text-sm">${anotacio.refExterna != null ? anotacio.refExterna : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.numexp"/></div>
									<div class="d-flex flex-column text-sm">${anotacio.expedientNumero != null ? anotacio.expedientNumero : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.transport.tipus"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${anotacio.transportTipusCodi != null}">${anotacio.transportTipusDescripcio} (${anotacio.transportTipusCodi})</c:when><c:otherwise>'--'</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.transport.num"/></div>
									<div class="d-flex flex-column text-sm">${anotacio.transportNumero != null ? anotacio.transportNumero : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.origen.num"/></div>
									<div class="d-flex flex-column text-sm">${anotacio.origenRegistreNumero != null ? anotacio.origenRegistreNumero : '--'}</div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.origen.data"/></div>
									<div class="d-flex flex-column text-sm"><c:choose><c:when test="${not empty anotacio.origenData}"><fmt:formatDate value="${anotacio.origenData}" pattern="dd/MM/yyyy HH:mm:ss"/></c:when><c:otherwise>--</c:otherwise></c:choose></div>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.observacions"/></div>
									<div class="d-flex flex-column text-sm">${anotacio.observacions != null ? anotacio.observacions : '--'}</div>
								</li>
							</ul>
	
						</div>
	
						<%-- Interessats --%>
						<div id="interessats_${anotacio.id}" class="tab-pane">
							<c:choose>
								<c:when test="${not empty anotacio.interessats}">
									<ul class="list-group">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.interessat.tipus"/></div>
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.interessat.document"/></div>
											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.interessat.nom"/></div>
										</li>
										<c:forEach var="interessat" items="${anotacio.interessats}" varStatus="status">
											<li class="list-group-item d-flex justify-content-between border-0">
												<div class="d-flex flex-column text-sm"><spring:message code="anotacio.interessat.tipus.enum.${interessat.tipus}"/></div>
												<div class="d-flex flex-column text-sm">${interessat.documentTipus}: ${interessat.documentNumero}</div>
												<div class="d-flex flex-column flex-grow-1 text-sm">
													<c:choose>
														<c:when test="${interessat.tipus == 'PERSONA_FISICA'}">${interessat.nom} ${interessat.llinatge1} ${interessat.llinatge2}</c:when>
														<c:otherwise>${interessat.raoSocial}</c:otherwise>
													</c:choose>
													<button type="button" class="btn btn-default desplegable fr10" href="#detalls_${status.index}" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}"><span class="fa fa-caret-down"></span></button>
												</div>
											</li>
											<li id="detalls_${status.index}" class="collapse detall list-group-item d-flex justify-content-between border-0">
												<ul class="list-group">
													<li class="list-group-item d-flex justify-content-between border-0">
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.pais"/></div>
														<div class="d-flex flex-column text-sm">${interessat.pais}</div>
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.provincia"/></div>
														<div class="d-flex flex-column text-sm">${interessat.provincia}</div>
													</li>
													<li class="list-group-item d-flex justify-content-between border-0">
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.municipi"/></div>
														<div class="d-flex flex-column text-sm">${interessat.municipi}</div>
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.adresa"/></div>
														<div class="d-flex flex-column text-sm">${interessat.adresa}</div>
													</li>
													<li class="list-group-item d-flex justify-content-between border-0">
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.codiPostal"/></div>
														<div class="d-flex flex-column text-sm">${interessat.cp}</div>
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"></div>
														<div class="d-flex flex-column text-sm"></div>
													</li>
													<li class="list-group-item d-flex justify-content-between border-0">
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.email"/></div>
														<div class="d-flex flex-column text-sm">${interessat.email}</div>
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.telefon"/></div>
														<div class="d-flex flex-column text-sm">${interessat.telefon}</div>
													</li>
													<li class="list-group-item d-flex justify-content-between border-0">
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.canalPreferent"/></div>
														<div class="d-flex flex-column text-sm"><c:if test="${not empty interessat.canal}"><spring:message code="anotacio.interessat.detalls.camp.canalPreferent.${interessat.canal}"/></c:if></div>
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.observacions"/></div>
														<div class="d-flex flex-column text-sm">${interessat.observacions}</div>
													</li>
												</ul>
												<c:if test="${not empty interessat.representant}">
													<c:set var="representant" value="${interessat.representant}"/>
													<h6><spring:message code="anotacio.interessat.detalls.camp.representant"/></h6>
													<ul class="list-group">
														<li class="list-group-item d-flex justify-content-between border-0">
															<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.interessat.tipus"/></div>
															<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.interessat.document"/></div>
															<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.camp.interessat.nom"/></div>
														</li>
														<li class="list-group-item d-flex justify-content-between border-0">
															<div class="d-flex flex-column text-sm"><spring:message code="anotacio.interessat.tipus.enum.${representant.tipus}"/></div>
															<div class="d-flex flex-column text-sm">${representant.documentTipus}: ${representant.documentNumero}</div>
															<div class="d-flex flex-column flex-grow-1 text-sm">
																<c:choose>
																	<c:when test="${representant.tipus == 'PERSONA_FISICA'}">${representant.nom} ${representant.llinatge1} ${representant.llinatge2}</c:when>
																	<c:otherwise>${representant.raoSocial}</c:otherwise>
																</c:choose>
																<c:if test="${representant.tipus != 'ADMINISTRACIO'}">
																	<button type="button" class="btn btn-default desplegable fr10" href="#detalls_${status.index}_rep" data-toggle="collapse" aria-expanded="false" aria-controls="detalls_${status.index}_rep"><span class="fa fa-caret-down"></span></button>
																</c:if>
															</div>
														</li>
														<li id="detalls_${status.index}_rep" class="collapse detall list-group-item d-flex justify-content-between border-0">
															<ul class="list-group">
																<li class="list-group-item d-flex justify-content-between border-0">
																	<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.pais"/></div>
																	<div class="d-flex flex-column text-sm">${representant.pais}</div>
																	<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.provincia"/></div>
																	<div class="d-flex flex-column text-sm">${representant.provincia}</div>
																</li>
																<li class="list-group-item d-flex justify-content-between border-0">
																	<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.municipi"/></div>
																	<div class="d-flex flex-column text-sm">${representant.municipi}</div>
																	<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.adresa"/></div>
																	<div class="d-flex flex-column text-sm">${representant.adresa}</div>
																</li>
																<li class="list-group-item d-flex justify-content-between border-0">
																	<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.codiPostal"/></div>
																	<div class="d-flex flex-column text-sm">${representant.cp}</div>
																	<div class="d-flex flex-column text-dark font-weight-bold text-sm"></div>
																	<div class="d-flex flex-column text-sm"></div>
																</li>
																<li class="list-group-item d-flex justify-content-between border-0">
																	<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.email"/></div>
																	<div class="d-flex flex-column text-sm">${representant.email}</div>
																	<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.telefon"/></div>
																	<div class="d-flex flex-column text-sm">${representant.telefon}</div>
																</li>
																<li class="list-group-item d-flex justify-content-between border-0">
																	<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.canalPreferent"/></div>
																	<div class="d-flex flex-column text-sm"><c:if test="${not empty representant.canal}"><spring:message code="anotacio.interessat.detalls.camp.canalPreferent.${representant.canal}"/></c:if></div>
																	<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.detalls.camp.observacions"/></div>
																	<div class="d-flex flex-column text-sm">${representant.observacions}</div>
																</li>
															</ul>
														</li>
													</ul>
												</c:if>
											</li>
										</c:forEach>
									</ul>
							</c:when>
							<c:otherwise>
								<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.interessat.buit"/></div>
							</c:otherwise>
							</c:choose>
						</div>
	
						<%-- Annexos --%>
						<div id="annexos_${anotacio.id}" class="tab-pane">
							<c:choose>
								<c:when test="${not empty anotacio.annexos}">
									<ul class="list-group">
										<li class="list-group-item d-flex justify-content-between border-0">
											<div class="d-flex flex-column text-sm"><spring:message code="anotacio.detalls.camp.interessat.tipus"/></div>
										</li>
										<c:forEach var="annex" items="${anotacio.annexos}" varStatus="status">
	<%--												<script type="text/javascript">--%>
	<%--													$(document).ready(function() {--%>
	<%--														$("#collapse-registre-firmes-<c:out value='${annex.id}'/>").on('show.bs.collapse', function(data){--%>
	<%--															if (!$(this).data("loaded")) {--%>
	<%--																var annexId = $(this).data("annexId");--%>
	<%--																$(this).append("<div style='text-align: center; margin-bottom: 60px; margin-top: 60px;''><span class='fa fa-circle-o-notch fa-spin fa-3x' title='<spring:message code="anotacio.annex.detalls.annex.firmes.consultant" />'/></div>");--%>
	<%--																$(this).load('<c:url value="/nodeco/v3/anotacio/${anotacio.id}/annex/"/>' + ${annex.id} + '/firmaInfo');--%>
	<%--																$(this).data("loaded", true);--%>
	<%--															}--%>
	<%--														});--%>
	<%--													});--%>
	<%--												</script>--%>
											<li class="list-group-item d-flex justify-content-between border-0">
												<div class="d-flex flex-column text-sm">
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
													<button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-annex-${status.index}"><span class="fa fa-chevron-down"></span></button>
												</div>
											</li>
											<li id="collapse-annex-${status.index}" class="collapse collapse-annex detall list-group-item d-flex justify-content-between border-0" data-registre-id="${anotacio.id}"  data-fitxer-arxiu-uuid="${annex.uuid}">
												<ul class="list-group">
													<li class="list-group-item d-flex justify-content-between border-0">
	<%--														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.annex.detalls.annex.processament.error"/></div>--%>
	<%--														<div class="d-flex flex-column text-sm">--%>
	<%--															<a href="<c:url value="/v3/anotacio/${anotacio.id}/annex/${annex.id}/reintentar"/>"--%>
	<%--												  				 class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span>--%>
	<%--																<spring:message code="anotacio.annex.detalls.annex.accio.reintentar" /></a>--%>
	<%--														</div>--%>
													</li>
													<li class="list-group-item d-flex justify-content-between border-0">
														<div class="d-flex flex-column text-sm"><pre>${annex.error}</pre></div>
													</li>
													<li class="list-group-item d-flex justify-content-between border-0">
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.annex.detalls.camp.eni.data.captura"/></div>
														<div class="d-flex flex-column text-sm"><c:if test="${not empty annex.ntiFechaCaptura}"><fmt:formatDate value="${annex.ntiFechaCaptura}" pattern="dd/MM/yyyy HH:mm:ss"/></c:if></div>
													</li>
													<li class="list-group-item d-flex justify-content-between border-0">
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.annex.detalls.camp.eni.origen"/></div>
														<div class="d-flex flex-column text-sm"><c:if test="${not empty annex.ntiOrigen}">${annex.ntiOrigen}</c:if></div>
													</li>
													<li class="list-group-item d-flex justify-content-between border-0">
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.annex.detalls.camp.eni.tipus.documental"/></div>
														<div class="d-flex flex-column text-sm"><c:if test="${not empty annex.ntiTipoDocumental}"><spring:message code="anotacio.annex.detalls.camp.ntiTipusDocument.${annex.ntiTipoDocumental}"/></c:if></div>
													</li>
													<li class="list-group-item d-flex justify-content-between border-0">
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.annex.detalls.camp.sicres.tipus.document"/></div>
														<div class="d-flex flex-column text-sm"><c:if test="${not empty annex.sicresTipoDocumento}"><spring:message code="anotacio.annex.detalls.camp.sicresTipusDocument.${annex.sicresTipoDocumento}"/></c:if></div>
													</li>
													<li class="list-group-item d-flex justify-content-between border-0">
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.annex.detalls.camp.arxiu.uuid"/></div>
														<div class="d-flex flex-column text-sm">
															${annex.uuid}
															<c:if test="${annex.uuid == null }">
																<span class="fa fa-warning text-warning" title="<spring:message code="anotacio.annex.detalls.camp.arxiu.uuid.buit.avis"/>"></span>
															</c:if>
														</div>
													</li>
													<c:if test="${not empty annex.observacions}">
														<li class="list-group-item d-flex justify-content-between border-0">
															<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.annex.detalls.camp.observacions"/></div>
															<div class="d-flex flex-column text-sm">${annex.observacions}</div>
														</li>
													</c:if>
													<li class="list-group-item d-flex justify-content-between border-0">
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.annex.detalls.camp.fitxer"/></div>
														<div class="d-flex flex-column text-sm">
															${annex.nom}
															<a href='<c:url value="/v3/anotacio/${anotacio.id}/annex/${annex.id}/descarregar"></c:url>'
															   class="btn btn-default btn-sm pull-right">
																<span class="fa fa-download" title="<spring:message code="anotacio.annex.detalls.camp.fitxer.descarregar"/>"></span>
															</a>
														</div>
													</li>
													<li class="list-group-item d-flex justify-content-between border-0">
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.annex.detalls.camp.estat"/></div>
														<div class="d-flex flex-column text-sm">
															${annex.estat}
															<c:if test="${annex.error != null}"><span class="fa fa-exclamation-triangle text-danger" title="${annex.error}"></span></c:if>
														</div>
													</li>
													<li class="list-group-item d-flex justify-content-between border-0">
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.annex.detalls.camp.estat.arxiu"/></div>
														<div class="d-flex flex-column text-sm">
															${annex.arxiuEstat}
															<c:if test="${annex.arxiuEstat == 'ESBORRANY'}"><span class="fa fa-exclamation-triangle text-warning" title="<spring:message code='anotacio.annex.detalls.camp.estat.arxiu.esborrany.avis'></spring:message>"></span></c:if>
														</div>
													</li>
													<li class="list-group-item d-flex justify-content-between border-0">
														<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.annex.detalls.camp.valid"/></div>
														<div class="d-flex flex-column text-sm">
															<c:choose>
																<c:when test="${annex.documentValid }"><spring:message code="enum.si"></spring:message></c:when>
																<c:when test="${!annex.documentValid }"><span class="fa fa-exclamation-triangle text-danger"></span> <spring:message code="enum.no"></spring:message>: ${annex.documentError}</c:when>
															</c:choose>
														</div>
													</li>
	<%--															<li class="list-group-item d-flex justify-content-between border-0">--%>
	<%--																<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.annex.detalls.camp.firmes"/></div>--%>
	<%--																<div class="d-flex flex-column text-sm"><button class="btn btn-default btn-xs pull-right" data-toggle="collapse" data-target="#collapse-registre-firmes-${annex.id}"><span class="fa fa-chevron-down"></span></button></div>--%>
	<%--															</li>--%>
	<%--															<li id="collapse-registre-firmes-${annex.id}" class="collapse collapse-annex collapse-registre-firmes list-group-item d-flex justify-content-between border-0">--%>
	<%--															</li>--%>
												</ul>
											</li>
										</c:forEach>
									</ul>
								</c:when>
								<c:otherwise>
									<ul class="list-group">
										<li class="list-group-item d-flex justify-content-between border-0">
										<c:choose>
											<c:when test="${not empty annexosErrorMsg}">
												<div class="d-flex flex-column text-danger font-weight-bold text-sm">${annexosErrorMsg}</div>
											</c:when>
											<c:otherwise>
												<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.annex.buit"/></div>
											</c:otherwise>
										</c:choose>
										</li>
									</ul>
								</c:otherwise>
							</c:choose>
	
						</div>
	
						<%-- Error --%>
						<div id="error_${anotacio.id}" class="tab-pane">
							<ul class="list-group">
								<li class="list-group-item d-flex justify-content-between border-0">
	<%--											<div class="d-flex flex-column text-dark font-weight-bold text-sm"><spring:message code="anotacio.detalls.errorNotifacio"/></div>--%>
	<%--											<div class="d-flex flex-column text-sm">--%>
	<%--												<a href="<c:url value="/v3/anotacio/${anotacio.id}/reintentarNotificar"/>"--%>
	<%--												   class="btn btn-xs btn-default pull-right"><span class="fa fa-refresh"></span>--%>
	<%--													<spring:message code="anotacio.detalls.annex.accio.reintentar" /></a>--%>
	<%--											</div>--%>
								</li>
								<li class="list-group-item d-flex justify-content-between border-0">
									<div class="d-flex flex-column text-sm"><pre>${anotacio.distibucioErrorNotificacio}</pre></div>
								</li>
							</ul>
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
						<span class="fa fa-download fa-inverse fa-1x"></span>&nbsp;<spring:message code="expedient.document.descarregar"/></a></li>
						<span>Descarregar</span>
				</c:otherwise>
			</c:choose>			
		
		</div>			
	</div>