package es.caib.helium.logic.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

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
