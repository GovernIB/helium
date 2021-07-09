$.fn.filtrarTaula = function(table){
	
	if(typeof String.prototype.trim !== 'function') {
		String.prototype.trim = function() {
			return this.replace(/^\s+|\s+$/g, ''); 
		};
	}
	
	var $item = this;
	var $rows = $(table).children("tbody").find("tr");
	$item.bind("keydown keyup keypress", function() {
		var filter = $item.val().toLowerCase();
		$rows.each(function(){
			var show = false;
			$(this).find("td").each(function(){
				var text = $(this).text().trim().toLowerCase();
				if (text.indexOf(filter) >= 0) { show = true; }
			});
			if (show) { $(this).show(); }
			else { $(this).hide(); }
		});
	});
};