package es.caib.helium.camunda.model.modeler;

import lombok.Data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class LinkableDto {

    protected List<AtomLink> links = new ArrayList<>();

    public void addLink(AtomLink link) {
        this.links.add(link);
    }

    public void addReflexiveLink(URI linkUri, String method, String relation) {
        AtomLink link = generateLink(linkUri, method, relation);
        this.links.add(link);
    }

    public AtomLink generateLink(URI linkUri, String method, String relation) {
        AtomLink link = new AtomLink(relation, linkUri.toString(), method);
        return link;
    }
}
