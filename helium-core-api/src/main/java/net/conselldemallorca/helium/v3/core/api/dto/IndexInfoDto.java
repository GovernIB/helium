package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * Classe dto per retornar informació sobre l'índex de lucene
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class IndexInfoDto implements Serializable{
	
	private static final long serialVersionUID = -9205810035704823501L;
	
	// valors en bytes
	
	private long espaiTotal = 0;
    private long espaiLliure = 0;
	private long[] midaSegments = {};
    private long midaTotal = 0;
    private long espaiNecessari = 0; // Camp calculat com a mida de l'índex (midaTotal) + marge de seguretat

    // valors en String
	private String espaiTotalStr = "";
    private String espaiLliureStr = "";
	private String[] midaSegmentsStr = {};
    private String midaTotalStr = "";
    private String espaiNecessariStr = "";
    
   
    // Informació de diagnòstic
    private Date data = new Date();
    private String dataStr = "";
    private boolean correcte = true;
    private String error = null;
    private String alerta = null;
}
