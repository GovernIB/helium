$(function() {
	var open = false;
	$('#pbar_total_massive_table').click(function() {
		if(open === false) {
			$('#footerSlideContainer').show();
			$(this).css('backgroundPosition', 'bottom left');
			open = true;
		} else {
			$('#footerSlideContainer').hide();
			$(this).css('backgroundPosition', 'top left');
			open = false;
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
    		$('#pbar_total_massive_table_bars').hide();
    		$('#pbar_total_massive_detail').hide();
    		
    		$('#div_progressBarMassiveDetail').html(data);
    		
    		$('#div_progressBarMassiveDetail #main #header').hide();
    		$('#div_progressBarMassiveDetail #footer').hide();

    		$('#div_progressBarMassiveDetail  #main #content').attr("id", "contenido");

    		$('#pbar_total_massive_detail').show();
    		
    		// Refrescamos los datos
    		//refreshTable();
	});
}

function cancelarExpedientMassiveAct(url,id) {
	$.post(url,
    { idExp: id },
     function(data){});
}

function refreshTable(){
	var delay = 1000;
	var auto = setInterval(function ()
	{
          $('#div_progressBarMassiveDetail').load('/WEB-INF/jsp/decorators/progressBarMassiveDetail.jsp');
    }, delay); // refresh every 5000 milliseconds
}
