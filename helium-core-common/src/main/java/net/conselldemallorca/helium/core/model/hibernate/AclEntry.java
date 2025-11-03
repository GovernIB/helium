package net.conselldemallorca.helium.core.model.hibernate;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Immutable
@Table(name = "hel_acl_entry", uniqueConstraints = @UniqueConstraint(columnNames = {"acl_object_identity", "ace_order"}))
public class AclEntry implements Serializable {

    private static final long serialVersionUID = 5024839895363737383L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "acl_object_identity", nullable = false)
    private AclObjectIdentity aclObjectIdentity;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "sid", nullable = false)
    private AclSid aclSid;

    @Column(name = "ace_order", nullable = false, length = 11)
    private long aceOrder;

    @Column(name = "mask", nullable = false, length = 11)
    private long mask;

    @Column(name = "granting", nullable = false, length = 1)
    private boolean granting;

    @Column(name = "audit_success", nullable = false, length = 1)
    private boolean auditSuccess;

    @Column(name = "audit_failure", nullable = false, length = 1)
    private boolean auditFailure;

    public AclObjectIdentity getAclObjectIdentity() {
        return aclObjectIdentity;
    }

    public void setAclObjectIdentity(AclObjectIdentity aclObjectIdentity) {
        this.aclObjectIdentity = aclObjectIdentity;
    }

    public AclSid getAclSid() {
        return aclSid;
    }

    public void setAclSid(AclSid aclSid) {
        this.aclSid = aclSid;
    }

    public long getAceOrder() {
        return aceOrder;
    }

    public void setAceOrder(long aceOrder) {
        this.aceOrder = aceOrder;
    }

    public long getMask() {
        return mask;
    }

    public void setMask(long mask) {
        this.mask = mask;
    }

    public boolean isGranting() {
        return granting;
    }

    public void setGranting(boolean granting) {
        this.granting = granting;
    }

    public boolean isAuditSuccess() {
        return auditSuccess;
    }

    public void setAuditSuccess(boolean auditSuccess) {
        this.auditSuccess = auditSuccess;
    }

    public boolean isAuditFailure() {
        return auditFailure;
    }

    public void setAuditFailure(boolean auditFailure) {
        this.auditFailure = auditFailure;
    }
}