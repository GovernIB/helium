/**
 * 
 */
package net.conselldemallorca.helium.model.lucene;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.jdbc.JdbcDirectory;
import org.apache.lucene.store.jdbc.dialect.Dialect;
import org.springframework.beans.factory.BeanInitializationException;
import org.springmodules.lucene.index.support.AbstractDirectoryFactoryBean;



/**
 * Factory per crear un Directory Jdbc per Lucene
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class JdbcDirectoryFactoryBean extends AbstractDirectoryFactoryBean {
	
	private DataSource dataSource;
	private Dialect dialect;
	private String tableName;
	private boolean createIfNotExists = false;

	@Override
	protected void checkFactoryBeanConfiguration() {
		if (dataSource == null) {
            throw new BeanInitializationException(
                    "Must specify a dataSource property");
        }
		if (dialect == null) {
            throw new BeanInitializationException(
                    "Must specify a dialect property");
        }
		if (tableName == null) {
            throw new BeanInitializationException(
                    "Must specify a tableName property");
        }
	}

	@Override
	protected Directory initializeDirectory() throws IOException {
		JdbcDirectory directory = new JdbcDirectory(
				dataSource,
				dialect,
				tableName);
		if (createIfNotExists) {
			if (!tableExists()) {
				try {
					directory.create();
				} catch (IOException ioex) {
					throw new BeanInitializationException("Couldn't create database table for JdbcDirectory", ioex);
				}
			}
		}
		return directory;
	}

	@SuppressWarnings("unchecked")
	public Class getObjectType() {
		return JdbcDirectory.class;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public void setCreateIfNotExists(boolean createIfNotExists) {
		this.createIfNotExists = createIfNotExists;
	}



	private boolean tableExists() {
		boolean retval = false;
		Connection con = null;
		try {
			con = dataSource.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT * FROM " + tableName);
			rset.next();
			retval = true;
		} catch (Exception e) {
			retval = false;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return retval;
	}

}
