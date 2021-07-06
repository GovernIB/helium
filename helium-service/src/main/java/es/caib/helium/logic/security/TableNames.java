/**
 * 
 */
package es.caib.helium.logic.security;

/**
 * Noms de les taules del mòdul ACL de spring-security.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TableNames {

	public static final String TABLE_PREFIX = "hel_";
	//public static final String TABLE_PREFIX = "";

	public static final String TABLE_OBJECT_IDENTITY = TABLE_PREFIX + "acl_object_identity";
	public static final String TABLE_SID = TABLE_PREFIX + "acl_sid";
	public static final String TABLE_CLASS = TABLE_PREFIX + "acl_class";
	public static final String TABLE_ENTRY = TABLE_PREFIX + "acl_entry";

	public static final String SEQUENCE_OBJECT_IDENTITY = TABLE_PREFIX + "acl_object_identity_seq";
	public static final String SEQUENCE_SID = TABLE_PREFIX + "acl_sid_seq";
	public static final String SEQUENCE_CLASS = TABLE_PREFIX + "acl_class_seq";
	public static final String SEQUENCE_ENTRY = TABLE_PREFIX + "acl_entry_seq";

}
