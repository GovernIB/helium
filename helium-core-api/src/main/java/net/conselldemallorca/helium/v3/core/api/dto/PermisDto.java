/**
 * 
 */
package net.conselldemallorca.helium.v3.core.api.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un permís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class PermisDto implements Serializable {

	private Long id;
	private String principalNom;
	private PrincipalTipusEnumDto principalTipus;

	private boolean read;
	private boolean write;
	private boolean create;
	private boolean delete;
	private boolean administration;

	private boolean cancel;
	private boolean stop;
	private boolean relate;
	private boolean dataManagement;
	private boolean docManagement;
	private boolean termManagement;
	private boolean taskManagement;
	private boolean taskSupervision;
	private boolean taskAssign;
	private boolean logRead;
	private boolean logManage;
	private boolean tokenRead;
	private boolean tokenManage;
	private boolean designAdmin;
	private boolean designDeleg;
	private boolean scriptExe;
	private boolean undoEnd;
	private boolean defprocUpdate;

	/* Permisos antics */
	private boolean design;
	private boolean organization;
	private boolean supervision;
	private boolean manage;
	private boolean reassignment;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
