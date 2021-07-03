package es.caib.helium.logic.intf.dto;

public class CampTipusIgnored {

    private CampTipusDto tipus;
    private Boolean ignored;

    public CampTipusDto getTipus() {
        return tipus;
    }
    public void setTipus(CampTipusDto tipus) {
        this.tipus = tipus;
    }
    public Boolean getIgnored() {
        return ignored;
    }
    public void setIgnored(Boolean ignored) {
        this.ignored = ignored;
    }
}
