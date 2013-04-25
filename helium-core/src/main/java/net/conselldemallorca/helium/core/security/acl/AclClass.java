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

/**
 * Taula acl_class
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name="hel_acl_class")
public class AclClass implements Serializable {

	private Long id;
	private String javaType;

	private Set<AclObjectIdentity> identities = new HashSet<AclObjectIdentity>();



	public AclClass() {}
	public AclClass(String javaType) {
		this.javaType = javaType;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator="gen_acl_class")
	@TableGenerator(name="gen_acl_class", table="hel_idgen", pkColumnName="taula", valueColumnName="valor")
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="class", nullable=false, unique=true)
	public String getJavaType() {
		return javaType;
	}
	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	@OneToMany(mappedBy = "objectClass")
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
		result = prime * result
				+ ((javaType == null) ? 0 : javaType.hashCode());
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
		AclClass other = (AclClass) obj;
		if (javaType == null) {
			if (other.javaType != null)
				return false;
		} else if (!javaType.equals(other.javaType))
			return false;
		return true;
	}



	private static final long serialVersionUID = 1L;

}
