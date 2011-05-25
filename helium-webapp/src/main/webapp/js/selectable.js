
function initSelectable(maxcols) {
	if (!maxcols) maxcols = 1000;
	if (!document.getElementsByTagName) return;
    var tables = document.getElementsByTagName("table");
    for (var t = 0; t < tables.length; t++) {
        var table = tables[t];
        if (table.className.indexOf("selectable") != -1) {
	        var previousClass = null;
		    var tbody = table.getElementsByTagName("tbody")[0];
		    if (tbody == null) {
		        var rows = table.getElementsByTagName("tr");
		    } else {
		        var rows = tbody.getElementsByTagName("tr");
		    }
		    for (var i = 0; i < rows.length; i++) {		    
		        for (var j = 0; j < rows[i].childNodes.length; j++) {
		        	var enllasat = false;
		        	if (j >= maxcols) break;
		        	for (var k = 0; k < rows[i].childNodes[j].childNodes.length; k++) {
		        		if (rows[i].childNodes[j].childNodes[k].nodeName == 'A') {
		        			if (rows[i].childNodes[j].childNodes[k].onclick != null)
		        				rows[i].onclick = rows[i].childNodes[j].childNodes[k].onclick;
		        			else
		        				eval("rows[i].onclick = function() {document.location = '" + rows[i].childNodes[j].childNodes[k].href + "'}");
		        			rows[i].onmouseover = function() { previousClass=this.className;this.className='over ' + this.className };
		    		        rows[i].onmouseout = function() { this.className=previousClass };
		        			enllasat = true;
		        			break;
		        		}
	    	    	}
	    	    	if (enllasat) break;
		        }
		    }
		}
    }
}
