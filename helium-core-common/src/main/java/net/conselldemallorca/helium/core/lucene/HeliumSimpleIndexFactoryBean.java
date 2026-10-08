package net.conselldemallorca.helium.core.lucene;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.store.Directory;
import org.springframework.core.io.UrlResource;
import org.springmodules.lucene.index.support.SimpleIndexFactoryBean;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.IndexInfoDto;

/** Classe pròpia que extén de SimpleIndexFactoryBean per poder fixer la classe de HeliumConcurrentMergeScheduler al IndexWriter
 * i controlar si fer o no el merge dels índexos segons l'espai que quedi buit a la unitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumSimpleIndexFactoryBean extends SimpleIndexFactoryBean {
	
		
	/** Referència estàtica per tonrar la instància de forma estàtica. */
	private static HeliumSimpleIndexFactoryBean instance = null;
	/** Mètode estàtic per tornar la instància. */
	public static HeliumSimpleIndexFactoryBean getInstance() {
		return instance;
	}

	/** Referència al directory per calcular la grandària de l'índex.
	 */
	private Directory directory;
	/** Ruta del directory índex. */
	private File directoryFile;

	@Override
	public void afterPropertiesSet() throws Exception {
		// Guarda la seva pròpia referència.
		instance = this;
		super.afterPropertiesSet();
		this.directory = this.getDirectory();
		// Configura l'scheduler propi d'Helium per decidir si fer el merge o no segons l'espai buit.
		File indexFile = new File(GlobalProperties.getInstance().getProperty("app.lucene.fs.basedir"));
		UrlResource resource = new UrlResource(new URL(indexFile.getPath()));
		directoryFile = resource.getFile();	
	}
	
	/** Consulta l'espai de disc i grandària del directori dels índexos. */
	public IndexInfoDto comprovaIndex() throws Exception {
		SegmentInfos segmentInfos = new SegmentInfos();
		segmentInfos.read(directory);
		return HeliumLuceneUtils.comprovarIndex(directoryFile, segmentInfos);
	}
}
