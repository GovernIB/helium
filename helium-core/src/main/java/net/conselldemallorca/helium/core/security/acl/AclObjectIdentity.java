/**
 * 
 */
package net.conselldemallorca.helium.core.security.acl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.ForeignKey;

/**
 * Taula acl_object_identity
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="hel_acl_object_identity")
public class AclObjectIdentity implements Serializable {

	private Long id;
	private long objectIdentity;
	private boolean entriesInheriting;

	private AclClass objectClass;
	private AclSid ownerSid;
	private AclObjectIdentity parentObject;

	private Set<AclObjectIdentity> children = new HashSet<AclObjectIdentity>();
	private Set<AclEntry> entries = new HashSet<AclEntry>();



	public AclObjectIdentity() {}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_acl_object_identity")
	@TableGenerator(name="gen_acl_object_identity", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="object_id_identity", nullable=false)
	public long getObjectIdentity() {
		return objectIdentity;
	}
	public void setObjectIdentity(long objectIdentity) {
		this.objectIdentity = objectIdentity;
	}

	@Column(name="entries_inheriting", nullable=false)
	public boolean isEntriesInheriting() {
		return entriesInheriting;
	}
	public void setEntriesInheriting(boolean entriesInheriting) {
		this.entriesInheriting = entriesInheriting;
	}

	@ManyToOne
	@JoinColumn(name="object_id_class")
	@ForeignKey(name="hel_aclclass_aclobjid_fk")
	public AclClass getObjectClass() {
		return objectClass;
	}
	public void setObjectClass(AclClass objectClass) {
		this.objectClass = objectClass;
	}

	@ManyToOne
	@JoinColumn(name="owner_sid")
	@ForeignKey(name="hel_aclsid_aclobjid_fk")
	public AclSid getOwnerSid() {
		return ownerSid;
	}
	public void setOwnerSid(AclSid ownerSid) {
		this.ownerSid = ownerSid;
	}

	@ManyToOne
	@JoinColumn(name="parent_object")
	@ForeignKey(name="hel_aclobjid_aclobjid_fk")
	public AclObjectIdentity getParentObject() {
		return parentObject;
	}
	public void setParentObject(AclObjectIdentity parentObject) {
		this.parentObject = parentObject;
	}

	@OneToMany(mappedBy = "parentObject", cascade=CascadeType.REMOVE)
	public Set<AclObjectIdentity> getChildren() {
		return this.children;
	}
	public void setChildren(Set<AclObjectIdentity> children) {
		this.children = children;
	}
	public void addChild(AclObjectIdentity child) {
		getChildren().add(child);
	}
	public void removeChild(AclObjectIdentity child) {
		getChildren().remove(child);
	}

	@OneToMany(mappedBy = "identity", cascade=CascadeType.REMOVE)
	public Set<AclEntry> getEntries() {
		return this.entries;
	}
	public void setEntries(Set<AclEntry> entries) {
		this.entries = entries;
	}
	public void addEntry(AclEntry entry) {
		getEntries().add(entry);
	}
	public void removeEntry(AclEntry entry) {
		getEntries().remove(entry);
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((objectClass == null) ? 0 : objectClass.hashCode());
		result = prime * result + (int) (objectIdentity ^ (objectIdentity >>> 32));
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
		AclObjectIdentity other = (AclObjectIdentity) obj;
		if (objectClass == null) {
			if (other.objectClass != null)
				return false;
		} else if (!objectClass.equals(other.objectClass))
			return false;
		if (objectIdentity != other.objectIdentity)
			return false;
		return true;
	}





	private static final long serialVersionUID = 1L;

}
