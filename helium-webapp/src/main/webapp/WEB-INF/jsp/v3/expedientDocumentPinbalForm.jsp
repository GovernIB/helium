<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/helium" prefix="hel"%>
<c:set var="idioma"><%=org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).getLanguage()%></c:set>

<c:choose>
	<c:when test="${empty document}">
		<c:set var="titol"><spring:message code="expedient.titol.nou_documentPinbal"/></c:set>
		<c:set var="formAction">new</c:set>
	</c:when>
	<c:otherwise>
		<c:set var="titol"><spring:message code="expedient.document.modificar"/></c:set>
		<c:set var="formAction">update</c:set>
	</c:otherwise>
</c:choose>
<c:if test="${not empty expedientDocumentPinbalDto.documentNom}">
	<c:set var="titol">${titol}: ${expedientDocumentPinbalDto.documentNom}</c:set>
</c:if>

<html>
<head>

	<title>${titol}</title>
	<hel:modalHead/>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.keyfilter-1.8.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery.price_format.1.8.min.js"/>"></script>
	<script type="text/javascript" src="<c:url value="/js/jquery/jquery.maskedinput.js"/>"></script>
	<link href="<c:url value="/css/select2.css"/>" rel="stylesheet"/>
	<link href="<c:url value="/css/select2-bootstrap.css"/>" rel="stylesheet"/>
	<script src="<c:url value="/js/select2.min.js"/>"></script>
	<script src="<c:url value="/js/select2-locales/select2_locale_${idioma}.js"/>"></script>
	<script src="<c:url value="/js/moment.js"/>"></script>
	<script src="<c:url value="/js/moment-with-locales.min.js"/>"></script>
	<script src="<c:url value="/js/bootstrap-datetimepicker.js"/>"></script>
	<script src="<c:url value="/js/webutil.common.js"/>"></script>
	<link href="<c:url value="/css/bootstrap-datetimepicker.min.css"/>" rel="stylesheet">

<script type="text/javascript">
var codiDocumentSeleccionat = '${codiDocumentSeleccionat != null ? codiDocumentSeleccionat : ""}';

$(document).ready(function() {
    if (codiDocumentSeleccionat) {
        let optionEncontrada = $('#documentId option').filter(function() {
            return $(this).text().trim() === codiDocumentSeleccionat;
        }).first();

        if (optionEncontrada.length) {
            $('#documentId').val(optionEncontrada.val());
        } else {
            console.warn("No se encontró opción para codiDocumentSeleccionat:", codiDocumentSeleccionat);
        }
    }

    // Al canviar el tipus de document Pinbal, refrescar els titulars
    $('#documentId').on('change', function() {
        const varDocumentId = $(this).val();
        $.get('<c:url value="/v3/expedient/${expedientDocumentPinbalDto.expedientId}/documentPinbal/' + varDocumentId + '/info"/>')
        .done(function(data) {
            <c:if test="${expedientDocumentPinbalDto.commandValidat==false}">
            if (data.finalitat) { $("#finalitat").val(data.finalitat); }
            </c:if>
            if (data.codiServei) { $("#codiServei").val(data.codiServei); }
            if (data.documentNom) { $("#documentNom").val(data.documentNom); }
            if (data.documentCodi) { $("#documentCodi").val(data.documentCodi); }

            // Ocultam totes les dades específiques, tant les del form con les de fora del form
            $('#datos-especificos').children().each(function () {
                if (this.nodeName=="DIV") {
                    $(this).hide();
                    $(this).appendTo("#dadesForaDelForm");
                }
            });
            $('#dadesForaDelForm').children().each(function () {
                if (this.nodeName=="DIV") {
                    $(this).hide();
                }
            });
            
          	// El div de dades específiques correspnent al servei selecciona, es coloca al form i es fa visible.
            if ($('#div' + data.codiServei).length>0) {
                $('#div' + data.codiServei).appendTo("#datos-especificos");
                $('#div' + data.codiServei).show();
                $('#datos-especificos').show();
                $('legend').show();
            } else {
                $('#datos-especificos').hide();
                $('legend').hide();
            }

// 			botoConsultar.removeAttr("disabled");
// 			botoConsultar.removeClass("disabled");
            webutilModalAdjustHeight();
        })
        .fail(function() {
            alert('Error al recuperar les dades del document: ' + varDocumentId);
        });
    });

    $('#provinciaNaixament').on('change', function() {
        var selectMuni = $('#municipiNaixament');
        $(selectMuni).empty();
      	// Al canviar el tipus de document Pinbal, refrescar els titulars
        $.get('<c:url value="/v3/expedient/getMunicipisPerProvincia/' + $(this).val() + '"/>')
        .done(function(data) {
            if (data) {
                data.forEach((elemento) => {
                    $(selectMuni).append(new Option(elemento.valor, elemento.codi));
                });
                $(selectMuni).trigger('change');
            }
        })
        .fail(function() {
            alert('Error al recuperar els Municipis Per Provincia: ' + $('#provinciaNaixament').val());
        });
    });

    
    $('#paisNaixament').on('change', function() {
    	// Han seleccionat espanya
        if ($(this).val() == '724') { // España
            $("#poblacioNaixament").parent().parent().hide();
            $("#provinciaNaixament").parent().parent().show();
            $("#municipiNaixament").parent().parent().show();
        } else {
            $("#poblacioNaixament").parent().parent().show();
            $("#provinciaNaixament").parent().parent().hide();
            $("#municipiNaixament").parent().parent().hide();
        }
    });

    $('#documentId').trigger('change');
    $('#paisNaixament').trigger('change');
});
</script>

</head>
<body>
	<form:form cssClass="form-horizontal form-tasca" action="${formAction}" enctype="multipart/form-data" method="post" commandName="expedientDocumentPinbalDto">
		
		<hel:inputHidden name="codiServei"/>
		<hel:inputHidden name="documentNom"/>
		<hel:inputHidden name="documentCodi"/>
		<hel:inputHidden name="processInstanceId"/>
		
		<hel:inputSelect 
			name="documentId"
			required="true"
			emptyOption="false"
			optionItems="${documentsPinbal}"
			textKey="expedient.nou.document.existent"
			optionValueAttribute="id"
			optionTextAttribute="documentNom"/>
		<hel:inputSelect 
			required="true" 
			name="interessatId" 
			textKey="expedient.notificacio.titular" 
			optionItems="${interessats}" 
			optionValueAttribute="id" 
			optionTextAttribute="fullInfo"/>
		<hel:inputSelect 
			name="consentiment"
			required="true"
			textKey="consultes.pinbal.camp.consentiment"
			optionItems="${consentimentList}"
			optionValueAttribute="codi"
			optionTextAttribute="valor"/>
			
		<hel:inputTextarea name="finalitat" required="true" textKey="serveisPinbal.document.form.pinbalFinalitat"/>
		
		<div class="tab-content">
			
			<fieldset id="datos-especificos" class="ocult">
			
				<legend><spring:message code="contingut.pinbal.form.datos.especificos"></spring:message></legend>
			
				<div id="divSVDCCAACPASWS01" class="ocult">
					<hel:inputSelect name="comunitatAutonomaCodi" textKey="contingut.pinbal.form.camp.comunitat.autonoma" optionItems="${comunitats}" optionValueAttribute="codi" optionTextAttribute="valor"/>
					<hel:inputSelect name="provinciaCodi" textKey="contingut.pinbal.form.camp.provincia" optionItems="${provincies}" optionValueAttribute="codi" optionTextAttribute="valor"/>
				</div>
				
				<div id="divSVDSCDDWS01" class="ocult">
					<hel:inputSelect name="comunitatAutonomaCodi" textKey="contingut.pinbal.form.camp.comunitat.autonoma" optionItems="${comunitats}" optionValueAttribute="codi" optionTextAttribute="valor"/>
					<hel:inputSelect name="provinciaCodi" textKey="contingut.pinbal.form.camp.provincia" optionItems="${provincies}" optionValueAttribute="codi" optionTextAttribute="valor"/>
					<hel:inputDate name="dataConsulta" placeholder="dd/MM/aaaa" textKey="contingut.pinbal.form.camp.data.consulta" />
					<hel:inputDate name="dataNaixement" placeholder="dd/MM/aaaa" textKey="contingut.pinbal.form.camp.data.naixement" />
					<hel:inputCheckbox name="consentimentTipusDiscapacitat" textKey="contingut.pinbal.form.camp.consentiment.tipus.discapacitat"/>					
				</div>
				
				<div id="divSCDCPAJU" class="ocult">
					<hel:inputSelect name="provinciaCodi" required="true" textKey="contingut.pinbal.form.camp.provincia" optionItems="${provincies}" optionValueAttribute="codi" optionTextAttribute="valor"/>
					<hel:inputSelect name="municipiCodi"  required="true" textKey="contingut.pinbal.form.camp.municipi"  optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="valor"/>
				</div>

				<div id="divSVDSCTFNWS01" class="ocult">
					<hel:inputSelect name="comunitatAutonomaCodi" textKey="contingut.pinbal.form.camp.comunitat.autonoma" optionItems="${comunitats}" optionValueAttribute="codi" optionTextAttribute="valor"/>
					<hel:inputDate name="dataConsulta" placeholder="dd/MM/aaaa" textKey="contingut.pinbal.form.camp.data.consulta" />
					<hel:inputDate name="dataNaixement" placeholder="dd/MM/aaaa" textKey="contingut.pinbal.form.camp.data.naixement"/>
					<hel:inputText name="numeroTitol" textKey="contingut.pinbal.form.camp.numero.titol"/>
				</div>
			
				<div id="divSVDCCAACPCWS01" class="ocult">
					<hel:inputSelect name="comunitatAutonomaCodi" textKey="contingut.pinbal.form.camp.comunitat.autonoma" optionItems="${comunitats}" optionValueAttribute="codi" optionTextAttribute="valor"/>
					<hel:inputSelect name="provinciaCodi" textKey="contingut.pinbal.form.camp.provincia" optionItems="${provincies}" optionValueAttribute="codi" optionTextAttribute="valor"/>
				</div>
				
				<div id="divSVDDELSEXWS01" class="ocult">
					<hel:inputSelect name="codiNacionalitat" required="true" textKey="contingut.pinbal.form.camp.pais.nacionalitat" optionItems="${paisos}" optionValueAttribute="codi" optionTextAttribute="valor"/>
					<hel:inputSelect name="paisNaixament" required="true" textKey="contingut.pinbal.form.camp.pais.naixament" optionItems="${paisos}" optionValueAttribute="codi" optionTextAttribute="valor"/>
				  	<hel:inputSelect name="provinciaNaixament" textKey="contingut.pinbal.form.camp.provincia.naixament" optionItems="${provincies}" optionValueAttribute="codi" optionTextAttribute="valor" comment="contingut.pinbal.form.camp.provincia.naixament.comment"/>
				  	<hel:inputText	 name="poblacioNaixament" textKey="contingut.pinbal.form.camp.poblacio.naixament" comment="contingut.pinbal.form.camp.poblacio.naixament.comment"/>
				  	<hel:inputSelect name="municipiNaixament" textKey="contingut.pinbal.form.camp.municipi.naixament" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true" comment="contingut.pinbal.form.camp.codi.poblacio.naixament.comment"/>					
					<hel:inputDate	 name="dataNaixement" required="true" placeholder="dd/MM/aaaa" textKey="contingut.pinbal.form.camp.data.naixement" comment="contingut.pinbal.form.delsex.camp.any"/>					
				  	<hel:inputSelect name="sexe" textKey="contingut.pinbal.form.camp.sexe" optionItems="${sexes}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true"/>
					<hel:inputText name="nomPare" textKey="contingut.pinbal.form.camp.nom.pare" comment="contingut.pinbal.form.camp.nom.pare.comment"/>
					<hel:inputText name="nomMare" textKey="contingut.pinbal.form.camp.nom.mare" comment="contingut.pinbal.form.camp.nom.mare.comment"/>
					<hel:inputText name="telefon" textKey="interessat.llistat.columna.telefon"/>
					<hel:inputText name="email" textKey="interessat.llistat.columna.email"/>
				</div>
				
				<div id="divSCDHPAJU" class="ocult">
					<hel:inputSelect name="provinciaCodi" required="true" textKey="contingut.pinbal.form.camp.provincia" optionItems="${provincies}" optionValueAttribute="codi" optionTextAttribute="valor"/>
					<hel:inputSelect name="municipiCodi" required="true" textKey="contingut.pinbal.form.camp.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="valor"/>
					<hel:inputNumber name="nombreAnysHistoric" textKey="contingut.pinbal.form.camp.data.nombre.anys.historic"/>
				</div>

				<div id="divNIVRENTI" class="ocult">
					<hel:inputNumber name="exercici" required="true" textKey="contingut.pinbal.form.camp.data.exercici"/>
				</div>
				
				<div id="divSVDDGPRESIDENCIALEGALDOCWS01" class="ocult">
					<hel:inputText name="numeroSoporte" textKey="contingut.pinbal.form.camp.numero.soporte" comment="contingut.pinbal.form.camp.tipus.numero.soporte.passaport.comment"/>
					<hel:inputSelect name="tipusPassaport" optionItems="${tipusPassaportsList}" optionValueAttribute="codi" optionTextAttribute="valor" textKey="contingut.pinbal.form.camp.tipus.passaport" comment="contingut.pinbal.form.camp.tipus.passaport.comment" emptyOption="true"/>
					<hel:inputDate name="dataCaducidad" placeholder="dd/MM/aaaa" textKey="contingut.pinbal.form.camp.data.caducidad"/>
					<hel:inputSelect name="codiNacionalitat" textKey="contingut.pinbal.form.camp.pais.nacionalitat" optionItems="${paisos}" emptyOption="true" optionValueAttribute="codi" optionTextAttribute="valor"/>
					<hel:inputDate name="dataExpedicion" placeholder="dd/MM/aaaa" textKey="contingut.pinbal.form.camp.data.expedicion"/>
				</div>
				
				<div id="divSVDRRCCNACIMIENTOWS01" class="ocult">
					<legend style="font-size: medium;padding-left: 50px;"><spring:message code="contingut.pinbal.form.legend.dadesRegistrals"/></legend>
					<hel:inputText name="registreCivil" textKey="contingut.pinbal.form.camp.registreCivil"/>
					<hel:inputText name="tom" textKey="contingut.pinbal.form.camp.tom"/>
					<hel:inputText name="pagina" textKey="contingut.pinbal.form.camp.pagina"/>
					<legend style="font-size: medium;padding-left: 50px;"><spring:message code="contingut.pinbal.form.legend.fetRegistral"/></legend>
					<hel:inputDate name="dataRegistre" placeholder="dd/MM/aaaa" textKey="contingut.pinbal.form.camp.data"/>
					<hel:inputSelect name="municipiRegistre" textKey="contingut.pinbal.form.camp.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true"/>			
					<legend style="font-size: medium;padding-left: 50px;"><spring:message code="contingut.pinbal.form.legend.naixement"/></legend>		
					<hel:inputDate name="dataNaixement" placeholder="dd/MM/aaaa" textKey="contingut.pinbal.form.camp.data"/>		
				  	<hel:inputSelect name="municipiNaixament" textKey="contingut.pinbal.form.camp.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true"/>		
				  	<legend style="font-size: medium;padding-left: 50px;"><spring:message code="contingut.pinbal.form.legend.dadesAdicionals"/></legend>	
				  	<hel:inputCheckbox name="ausenciaSegundoApellido" textKey="contingut.pinbal.form.camp.ausenciaSegundoApellido"/>
				  	<hel:inputSelect name="sexe" textKey="contingut.pinbal.form.camp.sexe" optionItems="${sexes}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true"/>
				  	<hel:inputText name="nomPare" textKey="contingut.pinbal.form.camp.nom.pare"/>
					<hel:inputText name="nomMare" textKey="contingut.pinbal.form.camp.nom.mare"/>	
				</div>				
				
				<div id="divSVDRRCCMATRIMONIOWS01" class="ocult">
					<legend style="font-size: medium;padding-left: 50px;"><spring:message code="contingut.pinbal.form.legend.dadesRegistrals"/></legend>
					<hel:inputText name="registreCivil" textKey="contingut.pinbal.form.camp.registreCivil"/>
					<hel:inputText name="tom" textKey="contingut.pinbal.form.camp.tom"/>
					<hel:inputText name="pagina" textKey="contingut.pinbal.form.camp.pagina"/>
					<legend style="font-size: medium;padding-left: 50px;"><spring:message code="contingut.pinbal.form.legend.fetRegistral"/></legend>
					<hel:inputDate name="dataRegistre" placeholder="dd/MM/aaaa" textKey="contingut.pinbal.form.camp.data"/>
					<hel:inputSelect name="municipiRegistre" textKey="contingut.pinbal.form.camp.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true"/>			
					<legend style="font-size: medium;padding-left: 50px;"><spring:message code="contingut.pinbal.form.legend.naixement"/></legend>		
					<hel:inputDate name="dataNaixement" placeholder="dd/MM/aaaa" textKey="contingut.pinbal.form.camp.data"/>		
				  	<hel:inputSelect name="municipiNaixament" textKey="contingut.pinbal.form.camp.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true"/>		
				  	<legend style="font-size: medium;padding-left: 50px;"><spring:message code="contingut.pinbal.form.legend.dadesAdicionals"/></legend>	
				  	<hel:inputCheckbox name="ausenciaSegundoApellido" textKey="contingut.pinbal.form.camp.ausenciaSegundoApellido"/>
				  	<hel:inputSelect name="sexe" textKey="contingut.pinbal.form.camp.sexe" optionItems="${sexes}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true"/>
				  	<hel:inputText name="nomPare" textKey="contingut.pinbal.form.camp.nom.pare"/>
					<hel:inputText name="nomMare" textKey="contingut.pinbal.form.camp.nom.mare"/>	
				</div>
				
				<div id="divSVDRRCCDEFUNCIONWS01" class="ocult">
				
					<legend style="font-size: medium;padding-left: 50px;"><spring:message code="contingut.pinbal.form.legend.dadesRegistrals"/></legend>
					
					<hel:inputText name="registreCivil" textKey="contingut.pinbal.form.camp.registreCivil"/>
					<hel:inputText name="tom" textKey="contingut.pinbal.form.camp.tom"/>
					<hel:inputText name="pagina" textKey="contingut.pinbal.form.camp.pagina"/>
					
					<legend style="font-size: medium;padding-left: 50px;"><spring:message code="contingut.pinbal.form.legend.fetRegistral"/></legend>
					<hel:inputDate name="dataRegistre" placeholder="dd/MM/aaaa" textKey="contingut.pinbal.form.camp.data"/>
					<hel:inputSelect name="municipiRegistre" textKey="contingut.pinbal.form.camp.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true"/>			
					
					<legend style="font-size: medium;padding-left: 50px;"><spring:message code="contingut.pinbal.form.legend.naixement"/></legend>		
					<hel:inputDate name="dataNaixement" placeholder="dd/MM/aaaa" textKey="contingut.pinbal.form.camp.data"/>		
				  	<hel:inputSelect name="municipiNaixament" textKey="contingut.pinbal.form.camp.municipi" optionItems="${municipis}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true"/>		
				  	
				  	<legend style="font-size: medium;padding-left: 50px;"><spring:message code="contingut.pinbal.form.legend.dadesAdicionals"/></legend>	
				  	<hel:inputCheckbox name="ausenciaSegundoApellido" textKey="contingut.pinbal.form.camp.ausenciaSegundoApellido"/>
				  	<hel:inputSelect name="sexe" textKey="contingut.pinbal.form.camp.sexe" optionItems="${sexes}" optionValueAttribute="codi" optionTextAttribute="valor" emptyOption="true"/>
				  	<hel:inputText name="nomPare" textKey="contingut.pinbal.form.camp.nom.pare"/>
					<hel:inputText name="nomMare" textKey="contingut.pinbal.form.camp.nom.mare"/>	
				</div>		
				
				<div id="divSVDBECAWS01" class="ocult">
					<hel:inputNumber name="curs" textKey="contingut.pinbal.form.camp.curs" comment="contingut.pinbal.form.camp.curs.comment"/>
				</div>
																
			</fieldset>
			
		</div>		
		
		<div id="modal-botons" class="well">
			<button type="button" class="btn btn-default modal-tancar" name="submit" value="cancel" data-modal-cancel="true"><spring:message code="comu.boto.cancelar"/></button>
			<button id="botoPerConsultar" class="btn btn-primary right" type="submit" name="accio" value="notificar"><spring:message code='consultes.pinbal.boto.consultar' /></button>
		</div>		
		
	</form:form>
	
	<div id="dadesForaDelForm"></div>
	
</body>
</html>