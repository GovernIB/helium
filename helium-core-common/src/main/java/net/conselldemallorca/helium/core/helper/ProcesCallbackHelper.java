package net.conselldemallorca.helium.core.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import edu.emory.mathcs.backport.java.util.Collections;

@Component
public class ProcesCallbackHelper {
	
	private List<Integer> idsDocumentsProcessant = new ArrayList<Integer>();
	
	public void afegirDocument (Integer idDocument) {
			this.idsDocumentsProcessant.add(idDocument);
	}
	
	public boolean eliminarDocument(Integer idDocument) {
		if (this.idsDocumentsProcessant.contains(idDocument))
			return this.idsDocumentsProcessant.removeAll(Collections.singleton(idDocument));
		return false;
	}
	
	public boolean isDocumentEnProces(Integer idDocucument) {
		return this.idsDocumentsProcessant.contains(idDocucument);
	}
}
