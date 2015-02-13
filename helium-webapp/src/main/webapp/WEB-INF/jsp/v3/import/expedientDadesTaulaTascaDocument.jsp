<c:set var="document" value="${dada}"/>
<td id="cela-${expedientId}-${document.id}">									
	<c:choose>
		<c:when test="${not empty document.error}">
			<span class="fa fa-warning fa-2x" title="${document.error}"></span>
		</c:when>
		<c:otherwise>
			<table id="document_${document.id}" class="table-condensed marTop6 tableDocuments">
				<thead>
					<tr>
						<td class="right">
							<c:if test="${not empty document.id}">
								<table class="marTop6 tableDocuments">
									<thead>
										<tr>
											<td class="tableDocumentsTd">												
								 				<c:url value="/v3/expedient/document/arxiuMostrar" var="downloadUrl"><c:param name="token" value="${document.tokenSignatura}"/></c:url>
												<c:if test="${not empty document.tokenSignatura}">
													<a title="<spring:message code='comuns.descarregar' />" id="downloadUrl" href="${downloadUrl}">
														<i class="fa fa-download"></i>
													</a>
												</c:if>												
												<c:if test="${document.signat}">																					
													<a 	data-rdt-link-modal="true" 
														<c:if test="${not empty document.urlVerificacioCustodia}">data-rdt-link-modal-min-height="400"</c:if>
														class="icon signature" 
														href="<c:url value='../../v3/expedient/${expedientId}/verificarSignatura/${document.id}/${document.documentCodi}'/>?urlVerificacioCustodia=${document.urlVerificacioCustodia}">
														<span class="fa fa-2x fa-certificate" title="<spring:message code='expedient.document.signat' />"></span>
													</a>
												</c:if>
												
												<c:if test="${document.registrat}">
													<a 	data-rdt-link-modal="true" 
														class="icon registre" 
														href="<c:url value='../../v3/expedient/${expedientId}/verificarRegistre/${document.id}/${document.documentCodi}'/>">
														<span class="fa fa-book fa-2x" title="<spring:message code='expedient.document.registrat' />"></span>
													</a>
												</c:if>											
											</td>
										</tr>
										<tr>
											<td>
												<fmt:formatDate value="${document.dataDocument}" pattern="dd/MM/yyyy HH:mm"/>
											</td>
										</tr>
									</thead>
								</table>
							</c:if>
						</td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td colspan="2">
							<strong class="nom_document">${document.documentNom}</strong><br/>
						</td>
					</tr>
				</tbody>
			</table>
		</c:otherwise>
	</c:choose>
</td>