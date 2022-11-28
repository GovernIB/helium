package net.conselldemallorca.helium.v3.core.api.dto.regles;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;


@Data
@Builder
public class VariableFact {

    // Regla
    private QuiEnum qui;
    private Set<String> quiValors;
    private QueEnum que;
    private Set<String> queValors;
    private AccioEnum accio;

    // Variable
    private TipusVarEnum tipus;
    private String varCodi;
    private String agrupacioCodi;

    // Dades auxiliars per el càlcul de la regla
    private boolean aplicaReglaQui = false;
    private boolean aplicaReglaQue = false;
    private String usuariCodi;
    private Set<String> usuariRols;
    private Set<String> usuariCarrecIds;

    // Camps que defineixen com mostrar la variable
    private boolean visible = true;
    private boolean editable = true;
    private boolean obligatori = false;

}

/*
TODO: Abans d'aplicar regles s'ha d'emplenar el VariableFact per a cada usuari:

String usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();

Set<String> usuariRols = new HashSet<String>();
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
for (GrantedAuthority ga: auth.getAuthorities())
    rols.add(ga.getAuthority());

// Si alguna regla és per càrrec
Set<Long> usuariCarrecIds = new HashSet<String>();
List<Carrec> carrecs = dominiHelper.getCarrecsAmbPersonaCodi(usuariCodi);
for(Carrec carrec: carrecs)
     usuariCarrecIds.add(carrec.getId());

 */
