/**
 * 
 */
package es.caib.helium.persist.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

/**
 * Entitat base amb clau primària simple i camps d'auditoria.
 * 
 * @author Limit Tecnologies
 */
@MappedSuperclass
@AttributeOverrides({
	@AttributeOverride(name = "createdDate", column = @Column(name = "createddate")),
	@AttributeOverride(name = "lastModifiedDate", column = @Column(name = "lastmodifieddate")),
	@AttributeOverride(name = "createdBy", column = @Column(name = "createdby_codi")),
	@AttributeOverride(name = "lastModifiedBy", column = @Column(name = "lastmodifiedby_codi"))
})
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditableEntity<PK extends Serializable> extends AbstractPersistable<PK> implements Auditable<String, PK, LocalDateTime> {

	public static final String TABLE_PREFIX = "hel";

	@Column(name = "createdby_codi")
	private @Nullable String createdBy;
	@Column(name = "createddate")
	@Temporal(TemporalType.TIMESTAMP) //
	private @Nullable Date createdDate;
	@Column(name = "lastmodifiedby_codi")
	private @Nullable String lastModifiedBy;
	@Column(name = "lastmodifieddate")
	@Temporal(TemporalType.TIMESTAMP) //
	private @Nullable Date lastModifiedDate;

	@Override
	public Optional<String> getCreatedBy() {
		return Optional.ofNullable(createdBy);
	}

	@Override
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Override
	public Optional<LocalDateTime> getCreatedDate() {
		return null == createdDate ? Optional.empty() : Optional.of(LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault()));
	}

	@Override
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = Date.from(createdDate.atZone(ZoneId.systemDefault()).toInstant());
	}

	@Override
	public Optional<String> getLastModifiedBy() {
		return Optional.ofNullable(lastModifiedBy);
	}

	@Override
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	@Override
	public Optional<LocalDateTime> getLastModifiedDate() {
		return null == lastModifiedDate ? Optional.empty() : Optional.of(LocalDateTime.ofInstant(lastModifiedDate.toInstant(), ZoneId.systemDefault()));
	}

	@Override
	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = Date.from(lastModifiedDate.atZone(ZoneId.systemDefault()).toInstant());
	}

}
