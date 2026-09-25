package net.conselldemallorca.helium.core.lucene;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.SegmentInfos;
import org.springframework.core.io.UrlResource;
import org.springmodules.lucene.index.factory.SimpleIndexFactory;
import org.springmodules.lucene.index.factory.SimpleLuceneIndexWriter;
import org.springmodules.lucene.index.support.SimpleIndexFactoryBean;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.v3.core.api.dto.IndexInfoDto;

/** Classe pròpia que extén de SimpleIndexFactoryBean per poder fixer la classe de HeliumConcurrentMergeScheduler al IndexWriter
 * i controlar si fer o no el merge dels índexos segons l'espai que quedi buit a la unitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumSimpleIndexFactoryBean extends SimpleIndexFactoryBean {
	
		
	/** Referència a indexWriter. */
	private IndexWriter indexWriter;
	/** Referència al directori de Lucene. */
	private File indexDirectory;
	/** Referència estàtica per tonrar la instància de forma estàtica. */
	private static HeliumSimpleIndexFactoryBean singleton = null;
	/** Mètode estàtic per tornar la instància. */
	public static HeliumSimpleIndexFactoryBean getInstance() {
		return singleton;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// Guarda la seva pròpia referència.
		singleton = this;
		// Configura normalment
		super.afterPropertiesSet();
		// Per reflection arriba a -> SimpleIndexFactory -> IndexWriter
		Field field = SimpleIndexFactoryBean.class.getDeclaredField("factory");
		field.setAccessible(true);
		SimpleIndexFactory factory = (SimpleIndexFactory) field.get(this);
		SimpleLuceneIndexWriter sliw = (SimpleLuceneIndexWriter) factory.getIndexWriter();
		field = SimpleLuceneIndexWriter.class.getDeclaredField("indexWriter");
		field.setAccessible(true);
		indexWriter = (IndexWriter) field.get(sliw);
		// Configura l'scheduler propi d'Helium per decidir si fer el merge o no segons l'espai buit.
		indexDirectory = new File(GlobalProperties.getInstance().getProperty("app.lucene.fs.basedir"));
		indexWriter.setMergeScheduler(new HeliumConcurrentMergeScheduler(indexDirectory));
		UrlResource resource = new UrlResource(new URL(indexDirectory.getPath()));
		indexDirectory = resource.getFile();		
	}
	
	/** Consulta l'espai de disc i grandària del directori dels índexos. */
	public IndexInfoDto comprovaIndex() throws Exception {
		Field field = IndexWriter.class.getDeclaredField("segmentInfos");
		field.setAccessible(true);
		SegmentInfos segmentInfos = (SegmentInfos) field.get(indexWriter);
		return HeliumLuceneUtils.comprovarIndex(indexDirectory, segmentInfos);
	}
}
