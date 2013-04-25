/**
 * 
 */
package net.conselldemallorca.helium.core.security.acl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.security.acls.sid.Sid;

/**
 * Taula acl_sid
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="hel_acl_sid")
public class AclSid implements Sid, Serializable {

	private Long id;
	private String sid;
	private boolean principal;

	private Set<AclObjectIdentity> identities = new HashSet<AclObjectIdentity>();
	private Set<AclEntry> entries = new HashSet<AclEntry>();



	public AclSid() {}
	public AclSid(String sid, boolean principal) {
		this.sid = sid;
		this.principal = principal;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_acl_sid")
	@TableGenerator(name="gen_acl_sid", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="sid", length=100, nullable=false)
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}

	@Column(name="principal")
	public boolean isPrincipal() {
		return principal;
	}
	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}

	@OneToMany(mappedBy = "sid")
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

	@OneToMany(mappedBy = "ownerSid")
	public Set<AclObjectIdentity> getIdentities() {
		return this.identities;
	}
	public void setIdentities(Set<AclObjectIdentity> identities) {
		this.identities = identities;
	}
	public void addIdentity(AclObjectIdentity identity) {
		getIdentities().add(identity);
	}
	public void removeIdentity(AclObjectIdentity identity) {
		getIdentities().remove(identity);
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (principal ? 1231 : 1237);
		result = prime * result + ((sid == null) ? 0 : sid.hashCode());
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
		AclSid other = (AclSid) obj;
		if (principal != other.principal)
			return false;
		if (sid == null) {
			if (other.sid != null)
				return false;
		} else if (!sid.equals(other.sid))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;

}
