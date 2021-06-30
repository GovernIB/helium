/**
 * 
 */
package es.caib.helium.persist.dialect;

import org.hibernate.dialect.Oracle10gDialect;

import es.caib.helium.persist.entity.AbstractAuditableEntity;

/**
 * Dialecte de Hibernate per a la base de dades Oracle per a permetre
 * adaptar el nom de la seqüència HIBERNATE_SEQUENCE als estàndards de
 * nomenclatura de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OracleCaibDialect extends Oracle10gDialect {

	@Override
	public String getSelectSequenceNextValString(String sequenceName) {
		if (sequenceName.equalsIgnoreCase("hibernate_sequence")) {
			return AbstractAuditableEntity.TABLE_PREFIX + "_" + sequenceName + ".nextval";
		} else {
			return sequenceName + ".nextval";
		}
	}

	@Override
	public String getCreateSequenceString(String sequenceName) {
		//starts with 1, implicitly
		if (sequenceName.equalsIgnoreCase("hibernate_sequence")) {
			return "create sequence " + AbstractAuditableEntity.TABLE_PREFIX + "_" + sequenceName;
		} else {
			return "create sequence " + sequenceName;
		}
	}

	@Override
	public String getDropSequenceString(String sequenceName) {
		if (sequenceName.equalsIgnoreCase("hibernate_sequence")) {
			return "drop sequence " + AbstractAuditableEntity.TABLE_PREFIX + "_" + sequenceName;
		} else {
			return "drop sequence " + sequenceName;
		}
	}

}
