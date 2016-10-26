/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;


/**
 * Enumeració amb els possibles estats del contingut pendent
 * d'una bústia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum InteressatDocumentTipusEnumDto {
	NIF ("N"),
	CIF ("C"),
	PASSAPORT ("P"),
	DOCUMENT_IDENTIFICATIU_ESTRANGERS ("E"),
	ALTRES_DE_PERSONA_FISICA ("X"),
	CODI_ORIGEN ("O");
	
	private final String label;
	private final String id;
	
	InteressatDocumentTipusEnumDto(String label) {
		this.label = label;
		this.id = this.name();
		
	}
	public String getLabel() {
		return label;
	}
	public String getId() {
		return id;
	}
	public static InteressatDocumentTipusEnumDto getType(String label) {
        if (label == null)
            return null;
         for (InteressatDocumentTipusEnumDto interessatDocumentTipusEnum : InteressatDocumentTipusEnumDto.values()) {
            if (label.equals(interessatDocumentTipusEnum.getLabel())) {
                return interessatDocumentTipusEnum;
            }
        }
        throw new IllegalArgumentException("No matching type in InteressatDocumentTipusEnumDto for code " + label);
    }
}
