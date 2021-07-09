package es.caib.helium.logic.intf.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentConversioDto {
    String nom;
    byte[] contingut;
    String extensio;
    String mediaType;
    String extensioOrignal;
}
