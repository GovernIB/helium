var interval_pbar_total_massive; 
var interval_pbar_total_massiveDetail;
var delayBarDetail = 1000;
var delayBar = 1000;
var numBarras = 0;
var mostrarDetalle = true;

function ocultarDetalleExpediente() {
	if (interval_pbar_total_massiveDetail != null) {
 		window.clearInterval(interval_pbar_total_massiveDetail);
 		interval_pbar_total_massiveDetail = null;
 	}
}

$(function() {
	$('#pbar_total_massive_table').click(function() {
		if (numBarras > 1) {
			$('#footerSlideContainer').slideToggle();
		} else {
			if (mostrarDetalle) {
				mostrarDetalle = false;
				var idBarra = $('.pbar').attr('id');
				mostrarDetalleExpediente(idBarra.substr(5, idBarra.length-1));
			}
		}
	});	
});

/**
 * Muestra la barra de progreso de los detalles de un expediente y sus opciones
 */
function mostrarDetalleExpediente(id) {
	$.post("/helium/expedient/refreshBarExpedientMassiveDetailAct.html",
    { idExp: id },
    function(data){
    	 	// Recibimos los datos    		
    		$('#div_progressBarMassiveDetail').html(data);    		
    		$('#div_progressBarMassiveDetail #main #header').hide();
    		$('#div_progressBarMassiveDetail #footer').hide();
    		$('#div_progressBarMassiveDetail #main #push').hide();
    		$('#div_progressBarMassiveDetail  #main #content').attr("id", "contenido");
    		$('#footerSlideContainerDetail').dialog("open");
    		
    		// Refrescamos los datos
    		if (interval_pbar_total_massiveDetail == null) {
    			refreshTable(id);
    		}
    		mostrarDetalle = true;
	});
}

function cancelarExpedientMassiveAct(url,id) {
	$.post(url, { idExp: id }, function(data){});
}

function refreshTable(id) {
	interval_pbar_total_massiveDetail = setInterval(function ()
	{
			$.post("/helium/expedient/refreshRegistreExpedientMassiveDetailAct.html",
			{ idExp: id },
		    function(data){
				// Recibimos los datos
	    		$('#registrosDetalles').html(data);
			});
    }, delayBarDetail);
}
