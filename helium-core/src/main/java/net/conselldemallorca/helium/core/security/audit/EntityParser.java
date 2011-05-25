/**
 * 
 */
package net.conselldemallorca.helium.core.security.audit;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.property.Getter;
import org.hibernate.type.OneToOneType;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

/**
 * 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntityParser {

	private LocalSessionFactoryBean sessionFactoryBean;
	private PersistentClass pc = null;
	private Object entity;
	private String[] properties;

	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String DATE_TIME_FORMAT = "dd/MM/yyyy hh:mm:ss a";



	public EntityParser(
			Object entity,
			String[] properties,
			LocalSessionFactoryBean sessionFactoryBean) {
		this.sessionFactoryBean = sessionFactoryBean;
		this.entity = entity;
		this.properties = properties;
		this.pc = extractPersistentClass(entity.getClass().getName());
	}

	public String extractPrimaryKeyValue(){
		//Get pk column and value
		String pkValue = null;
		try{
			pkValue = pc.getIdentifierProperty().getGetter(entity.getClass()).get(entity).toString();
		} catch(PropertyNotFoundException pnfe) {
			log.info("PropertyNotFoundException " + pnfe.getMessage());
		} catch(MappingException me) {
			log.info("MappingException " + me.getMessage());
		} catch(HibernateException he) {
			log.info("HibernateException " + he.getMessage());
		} catch(NullPointerException npe) {
			log.info("NullPointerException " + npe.getMessage());
		}
		return pkValue;
	}

	public String extractPrimaryKeyColumn(){
		String pkColumn = null;
		try {
			pkColumn = ((Column)pc.getIdentifierProperty().getColumnIterator().next()).getName();
		} catch(NullPointerException npe) {
			log.info("NullPointerException " + npe.getMessage());
		}
		return pkColumn;
	}

	public String extractPrimaryKeyColumn(PersistentClass localPC){
		String pkColumn = null;
		try {
			pkColumn = ((Column)localPC.getIdentifierProperty().getColumnIterator().next()).getName();
		} catch(NullPointerException npe) {
			log.info("NullPointerException " + npe.getMessage());
		}
		return pkColumn;
	}

	public String extractPrimaryKeyValue(PersistentClass localPC, Object localEntity){
		String pkValue = null;
		try {
			pkValue = localPC.getIdentifierProperty().getGetter(localEntity.getClass()).get(localEntity).toString();
		} catch(PropertyNotFoundException pnfe){
			log.error("PropertyNotFoundException " + pnfe.getMessage(),pnfe);
		} catch(MappingException me ){
			log.error("MappingException " + me.getMessage(),me);
		} catch(HibernateException he) {
			log.error("HibernateException " + he.getMessage(),he);
		} catch(NullPointerException npe) {
			log.error("NullPointerException " + npe.getMessage(),npe);
		}
		return pkValue;
	}

	@SuppressWarnings("unchecked")
	public String extractColumnValues(){
		StringBuffer sb = new StringBuffer();
		//add the pk column and value
		sb.append(extractPrimaryKeyColumn() + " = " + extractPrimaryKeyValue() + " | ");
		//loop through all the other properties and get what you need
		for (String p: properties){
			Property pr = pc.getProperty(p);
		
			//make sure that this is not a collection and not a one to one as these values are not part of the table
			if (!pr.getType().isCollectionType() && !(pr.getType() instanceof OneToOneType)) {
				//make sure that the values are persistent values and not a forumla value
				if (pr.isInsertable() || pr.isUpdateable()) {
					int scale = 2;
					//get the getter for the entity
					Getter getter = pr.getGetter(entity.getClass());
					//get column value
					Object columnValue = getter.get(entity);
					//get column name
					for (Iterator it3 = pr.getColumnIterator(); it3.hasNext();) {
						Column column = (Column)it3.next();
						sb.append(column.getName());
						scale = column.getScale();
					}
					sb.append(" = ");
					//check what kind of type of value this is, it if it an association then get the forign key value from the associated entity
					if (columnValue != null) {
						if (!pr.getType().isAssociationType()) {
							//if bigD set Scale
							if (columnValue instanceof BigDecimal) {
								columnValue = ((BigDecimal)columnValue).setScale(scale,BigDecimal.ROUND_HALF_DOWN);
							} else if (columnValue instanceof java.util.Date) {
								SimpleDateFormat sdf = null;
								if(columnValue instanceof java.sql.Timestamp){
									sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
								}else{
									sdf = new SimpleDateFormat(DATE_FORMAT);
								}
								columnValue = sdf.format(columnValue);
							} else if (pr.getType().getName().equalsIgnoreCase("org.springframework.orm.hibernate3.support.ClobStringType")){
								columnValue = "Clob Value";
							}
							sb.append(columnValue);
						} else {
							//since it's an association we know that column value is an object
							String associatedEntityName = pr.getType().getName();
							//associatedEntityName = ((EntityType)pr.getType()).getAssociatedEntityName ();
							PersistentClass localPC = extractPersistentClass(associatedEntityName);
							String fkValue = extractPrimaryKeyValue(localPC,columnValue);
							sb.append(fkValue);
						}
					}
					sb.append(" | ");
				}
			}
		}
		return sb.toString();
	}

	public String extractTableName() {
		try {
			return pc.getTable().getName();
		}catch(NullPointerException npe){
			log.error("NullPointerException - table is null " + npe.getMessage(),npe);
			return "";
		}
	}



	private PersistentClass extractPersistentClass(
			String entityName) {
		PersistentClass pc = null;
		try {
			pc = sessionFactoryBean.getConfiguration().getClassMapping(entityName);
		} catch(NullPointerException npe) {
			log.error("NullPointerException - make sure you get the right session factory " + npe.getMessage(),npe);
		}
		return pc;
	}

	private static final Log log = LogFactory.getLog(EntityParser.class);

}
