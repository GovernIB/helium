// Canviar entre obert i tancat del detall del document (Tancar també detalls d'altres documents)
const toggleDocDetails = (tr) => {
	
	let rowId = $(tr).attr('id');	
    // Ocultam tots els detalls que es trobin actualment oberts (en principi només n'hi pot haver un)
    $("#expedientDocuments").find('tr').each((index, row) => {
        // Comprovam si el detall del document actual està obert
        detallActualVisible = $(row).next().hasClass('doc-details') && $(row).next().is(":visible");
    	if ($(row).attr('id') == rowId && !detallActualVisible ) {
            showDocDetail(row);
    	} else {
            closeDocDetail(row);    		
    	}
    });
}

// Tancar el detall d'un document
const closeDocDetail = (detail) => {
    // Per tancar el detall dels documents ho feim amb un efecte d'acordiò de 500ms
    if ($(detail).next().hasClass('doc-details')) {
        slideRowUp($(detail).next(), 500);
    }
}

// Mostrar el detall d'un document
const showDocDetail = (row) => {
    // Si ja s'havia carregat el detall, el tornem a obrir amb un efecte d'acordió de 500ms
    if ($(row).next().hasClass('doc-details')) {
        slideRowDown($(row).next(), 500);

    // Si encara no s'havia carregat el detall del document, l'hem de crear
    } else {
        createDocDetail(row);
    }
}

// Crea la fila amb el detall del document
const createDocDetail = (row) => {
    // Obtenim l'identificador del document
    const documentId = $(row).find(".accionsDocument").first().data("documentId");
    const documentNom = escape($(row).find("td:eq(1)").text().trim());

    // Feim una crida ajax per obtenir els detalls del document
    $.ajax({
        url: urlDescarregaBase + documentId + "/detall/",
        beforeSend: function(xhr) {
            // Abans d'obtenir els detalls mostram un fila amb un spinner per indicar que s'està carregant
            addRowLoadingDetail(row, documentId);
        }
    }).done(function(data) {
        // Al rebre els detalls afegim la fila amb els detalls
        addRowDetail(row, documentId, data);
    }).fail(function(jqXHR, exception) {
        modalAjaxErrorFunction(jqXHR, exception);
    });




    // const documentArxivat = $(element).find(".accionsDocument").first().data("arxivat");
    // showViewer(e, documentId, documentNom);

}

// Afegim dues files per a mantenir els colors de parells i senars de la taula.
// En aquest mètode mostram l'spinner de càrrega
const addRowLoadingDetail = (row, id) => {
    $(row).after('<tr class="doc-details"><td class="td-doc-detalls" colspan="6"><div class="wrapping-slide"><span class="fa fa-circle-o-notch fa-spin" style="display: table; margin: 0 auto;"></span></div></td></tr>');
    slideRowDown($(row).next(), 200);
}

// Substituim el contingut de la fila de l'spinner de càrrega amb els detalls del document
const addRowDetail = (row, id, data) => {
    // Ocultam la fila
    slideRowUp($(row).next(),100, () => {
        // Subtituim el contingut de la fina
        $('.wrapping-slide', $(row).next()).html(data);
        // Mostram la fila
        slideRowDown($(row).next(), 500);
    });
}

// Ocultam una fila d'una taula amb un efecte d'slide.
const slideRowUp = (row, duration, funcio) => {
    $('.wrapping-slide', $(row)).slideUp(duration, function(){
    	$(row).hide();
        if (funcio != undefined) {
            funcio();
        }
    });
}

// Visualitzan una fila d'una taula amb un efecte d'slide
const slideRowDown = (row, duration, funcio) => {
	$(row).show();
    $('.wrapping-slide', $(row)).slideDown(duration, () => {
        if (funcio != undefined) {
            funcio();
        }
    });
}


// Visualitzam / Ocultam el cos del card
const toggleCard = (cardHeader) => {
    $(cardHeader).next().slideToggle(350, toggleCardCaret(cardHeader));
}

// Canvia el caret entre up i down
const toggleCardCaret = (cardHeader) => {
    let caretUp = $(cardHeader).find('.fa-caret-up').length;
    if (caretUp) {
        $(cardHeader).find('.fa-caret-up').removeClass('fa-caret-up').addClass('fa-caret-down');
    } else {
        $(cardHeader).find('.fa-caret-down').removeClass('fa-caret-down').addClass('fa-caret-up');
    }
}

const toggleViewer = (viewer) => {
    if (!$(viewer).is(':visible')) {
        // let documentId = $(viewer).data('documentid');
        // var urlDescarrega = urlDescarregaBase + documentId + '/returnFitxer';
        $(viewer).slideDown(500, toggleCardCaret($(viewer).prev()));
        if ($(viewer).data('loaded') == undefined) {
            showDocument(viewer);
        }
    } else {
        $(viewer).slideUp(500, toggleCardCaret($(viewer).prev()));
    }

}

const showDocument = (viewer) => {

    let documentId = $(viewer).data('documentid');
    let arxiuUrl = urlDescarregaBase + documentId + '/returnFitxer';
    let contenidor = $(viewer).find('iframe');

    $(viewer).prepend('<span class="fa fa-circle-o-notch fa-spin"></span>');
    contenidor.addClass('rmodal_loading');

    // Fa la petició a l'url de l'arxiu
    $.ajax({
        type: 'GET',
        url: arxiuUrl,
        responseType: 'arraybuffer',
        success: function(json) {

            if (json.error) {
                $(viewer).before('<div class="viewer-padding"><div class="alert alert-danger">' + msgViewer['error'] + ': ' + json.errorMsg + '</div></div>');
            } else if (json.warning) {
                $(viewer).before('<div class="viewer-padding"><div class="alert alert-warning">' + msgViewer['warning'] + '</div></div>');
            } else {
                response = json.data;
                let blob = base64toBlob(response.contingut, response.contentType);
                let file = new File([blob], response.contentType, {type: response.contentType});
                let link = URL.createObjectURL(file);

                var viewerUrl = urlViewer + '?file=' + encodeURIComponent(link);
                contenidor.attr('src', viewerUrl);
                $(viewer).data('loaded', "true");
            }
            $(viewer).find('.fa-spin').remove();

        },
        error: function(xhr, ajaxOptions, thrownError) {
            $(viewer).find('.fa-spin').remove();
            alert(thrownError);
        }
    });
}

function base64toBlob(b64Data, contentType) {
    var contentType = contentType || '';
    var sliceSize = 512;
    var byteCharacters = atob(b64Data);
    var byteArrays = [];
    for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
        var slice = byteCharacters.slice(offset, offset + sliceSize);
        var byteNumbers = new Array(slice.length);
        for (var i=0; i<slice.length; i++) {
            byteNumbers[i] = slice.charCodeAt(i);
        }
        var byteArray = new Uint8Array(byteNumbers);
        byteArrays.push(byteArray);
    }
    var blob = new Blob(byteArrays, {type: contentType});
    return blob;
}

function escape(htmlStr) {
    return htmlStr.replace(/&/g, "&amp;")
        .replace(/\</g, "&lt;")
        .replace(/\>/g, "&gt;")
        .replace(/\"/g, "&quot;")
        .replace(/\'/g, "&#39;");
}