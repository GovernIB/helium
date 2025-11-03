package net.conselldemallorca.helium.core.model.hibernate;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Immutable
@Table(name = "hel_acl_sid")
public class AclSid implements Serializable {

    private static final long serialVersionUID = 4136892277872762955L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "principal", nullable = false, length = 1)
    private boolean principal;

    @Column(name = "sid", nullable = false, length = 100)
    private String sid;

    @OneToMany(mappedBy = "ownerSid", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<AclObjectIdentity> objectIdentities;

    @OneToMany(mappedBy = "aclSid", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<AclEntry> aclEntries;

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public List<AclObjectIdentity> getObjectIdentities() {
        return objectIdentities;
    }

    public void setObjectIdentities(List<AclObjectIdentity> objectIdentities) {
        this.objectIdentities = objectIdentities;
    }

    public List<AclEntry> getAclEntries() {
        return aclEntries;
    }

    public void setAclEntries(List<AclEntry> aclEntries) {
        this.aclEntries = aclEntries;
    }
}

