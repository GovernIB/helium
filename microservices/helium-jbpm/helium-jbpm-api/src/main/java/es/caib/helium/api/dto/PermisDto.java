/**
 * 
 */
package es.caib.helium.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Informació d'un permís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@ToString
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

	private static final long serialVersionUID = -139254994389509932L;
}
