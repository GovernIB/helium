package net.conselldemallorca.helium.core.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.index.SegmentInfos;

import net.conselldemallorca.helium.core.util.StringUtilsHelium;
import net.conselldemallorca.helium.v3.core.api.dto.IndexInfoDto;

/** Classe per agrupar funcions comunes en el càlcul de mesures de directoris i segments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumLuceneUtils {
	
	/**
     * Marge de seguretat 10 GB.
     */
    public static final long MARGE_SEGURETAT_BYTES = 10L * 1024L * 1024L * 1024L;


	/** Suma els bytes dels segments. */
	public static long[] getMidaSegments(SegmentInfos segmentInfos) throws IOException{
	    // Sumar la mida dels segments
	    long[] midaSegments = new long[segmentInfos.size()];
	    for (int i = 0; i < segmentInfos.size(); i++) {
	        SegmentInfo segmentInfo =
	            segmentInfos.info(i);
	        midaSegments[i] = segmentInfo.sizeInBytes();
	    }
	    return midaSegments;
	}
	
	public static IndexInfoDto comprovarIndex(File indexDirectory, SegmentInfos segmentInfos) throws Exception {
		IndexInfoDto indexInfo = new IndexInfoDto();
		
		long espaiTotal = indexDirectory.getTotalSpace();
		indexInfo.setEspaiTotal(espaiTotal);
		indexInfo.setEspaiTotalStr(StringUtilsHelium.formatBytes(espaiTotal));
		
	    long espaiLliure = indexDirectory.getUsableSpace();
	    indexInfo.setEspaiLliure(espaiLliure);
	    indexInfo.setEspaiLliureStr(StringUtilsHelium.formatBytes(espaiLliure));
	    
		long[] midaSegments = getMidaSegments(segmentInfos);
		String[] midaSegmentsStr = new String[midaSegments.length];
	    long midaTotal = 0;
		for (int i = 0; i < midaSegments.length; i++) {
			midaSegmentsStr[i] = StringUtilsHelium.formatBytes(midaSegments[i]);
	        midaTotal += midaSegments[i];
		}
		indexInfo.setMidaSegments(midaSegments);
		indexInfo.setMidaSegmentsStr(midaSegmentsStr);
		indexInfo.setMidaTotal(midaTotal);
		indexInfo.setMidaTotalStr(StringUtilsHelium.formatBytes(midaTotal));
		
		// Comprovacio de si es troba en nivell d'avís o d'error
		long espaiNecessari = midaTotal + MARGE_SEGURETAT_BYTES;
		indexInfo.setEspaiNecessari(espaiNecessari);
		indexInfo.setEspaiNecessariStr(StringUtilsHelium.formatBytes(espaiNecessari));
		long espaiAlerta = espaiNecessari * 2;
		
		if (espaiLliure < espaiNecessari) {
			// ERROR, es necessita més espai
			indexInfo.setError("Espai lliure crític. Queden només " + (indexInfo.getEspaiLliureStr()) + 
						" lliures d'espai a la unitat de l'índex per una mida estimada de " + indexInfo.getEspaiNecessariStr() + 
						". Amb aquest espai no es realitzaran merges per evitar problemes. Cal incrementar l'espai lliure.");
			indexInfo.setCorrecte(false);
		} else if (espaiLliure < espaiAlerta) {
			// ALERTA per falta d'espai
			indexInfo.setAlerta("Queden " + indexInfo.getEspaiLliureStr() + " d'espai lliure a la unitat de l'índex. Si baixa dels " + indexInfo.getEspaiNecessariStr() + " es passarà a un estat d'error i no es faran més merges dels índexos.");
			indexInfo.setCorrecte(true);
		} else {
		    // NORMAL
			indexInfo.setCorrecte(true);
		}
		return indexInfo;
	}
}
