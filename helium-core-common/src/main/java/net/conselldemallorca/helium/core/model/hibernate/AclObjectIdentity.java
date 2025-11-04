package net.conselldemallorca.helium.core.model.hibernate;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Immutable
@Table(name = "HEL_ACL_OBJECT_IDENTITY", 
	uniqueConstraints = @UniqueConstraint(name = "HEL_ACL_OBJ_ID_CLASS_IDENT_UQ", columnNames = {
    "OBJECT_ID_CLASS", "OBJECT_ID_IDENTITY"}))
public class AclObjectIdentity implements Serializable {


	private static final long serialVersionUID = 3046766577730456407L;

	@Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "object_id_class", nullable = false)
    private AclClass aclClass;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "parent_object", nullable = true)
    private AclObjectIdentity parentObject;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "owner_sid", nullable = true)
    private AclSid ownerSid;

    @Column(name = "object_id_identity", nullable = false)
    private long objectIdIdentity;

    @Column(name = "entries_inheriting", nullable = false, length = 1)
    private boolean entriesInheriting;

    @OneToMany(mappedBy = "aclObjectIdentity", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<AclEntry> aclEntries;

    public AclClass getAclClass() {
        return aclClass;
    }

    public void setAclClass(AclClass aclClass) {
        this.aclClass = aclClass;
    }

    public AclObjectIdentity getParentObject() {
        return parentObject;
    }

    public void setParentObject(AclObjectIdentity parentObject) {
        this.parentObject = parentObject;
    }

    public AclSid getOwnerSid() {
        return ownerSid;
    }

    public void setOwnerSid(AclSid ownerSid) {
        this.ownerSid = ownerSid;
    }

    public long getObjectIdIdentity() {
        return objectIdIdentity;
    }

    public void setObjectIdIdentity(long objectIdIdentity) {
        this.objectIdIdentity = objectIdIdentity;
    }

    public boolean isEntriesInheriting() {
        return entriesInheriting;
    }

    public void setEntriesInheriting(boolean entriesInheriting) {
        this.entriesInheriting = entriesInheriting;
    }

    public List<AclEntry> getAclEntries() {
        return aclEntries;
    }

    public void setAclEntries(List<AclEntry> aclEntries) {
        this.aclEntries = aclEntries;
    }
}