/**
 * 
 */
package net.conselldemallorca.helium.core.security.acl;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.ForeignKey;

/**
 * Taula acl_entry
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_acl_entry")
public class AclEntry implements Serializable {

	private Long id;
	private int mask;
	private int aceOrder;
	private boolean granting;
	private boolean auditSuccess; 
	private boolean auditFailure;

	private AclSid sid;
	private AclObjectIdentity identity;



	public AclEntry() {}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_acl_entry")
	@TableGenerator(name="gen_acl_entry", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="mask", nullable=false)
	public int getMask() {
		return mask;
	}
	public void setMask(int mask) {
		this.mask = mask;
	}

	@Column(name="ace_order", nullable=false)
	public int getAceOrder() {
		return aceOrder;
	}
	public void setAceOrder(int aceOrder) {
		this.aceOrder = aceOrder;
	}

	@Column(name="granting", nullable=false)
	public boolean isGranting() {
		return granting;
	}
	public void setGranting(boolean granting) {
		this.granting = granting;
	}

	@Column(name="audit_success", nullable=false)
	public boolean isAuditSuccess() {
		return auditSuccess;
	}
	public void setAuditSuccess(boolean auditSuccess) {
		this.auditSuccess = auditSuccess;
	}

	@Column(name="audit_failure", nullable=false)
	public boolean isAuditFailure() {
		return auditFailure;
	}
	public void setAuditFailure(boolean auditFailure) {
		this.auditFailure = auditFailure;
	}

	@ManyToOne
	@JoinColumn(name="sid")
	@ForeignKey(name="hel_aclsid_aclentry_fk")
	public AclSid getSid() {
		return sid;
	}
	public void setSid(AclSid sid) {
		this.sid = sid;
	}

	@ManyToOne
	@JoinColumn(name="acl_object_identity")
	@ForeignKey(name="hel_aclobjid_aclentry_fk")
	public AclObjectIdentity getIdentity() {
		return identity;
	}
	public void setIdentity(AclObjectIdentity identity) {
		this.identity = identity;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + aceOrder;
		result = prime
				* result
				+ ((identity == null) ? 0 : identity
						.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AclEntry other = (AclEntry) obj;
		if (aceOrder != other.aceOrder)
			return false;
		if (identity == null) {
			if (other.identity != null)
				return false;
		} else if (!identity.equals(other.identity))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
