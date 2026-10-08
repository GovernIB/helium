package net.conselldemallorca.helium.core.lucene;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;

import org.apache.lucene.index.IndexWriter;
import org.springmodules.lucene.index.factory.LuceneIndexWriter;
import org.springmodules.lucene.index.factory.SimpleLuceneIndexWriter;
import org.springmodules.lucene.index.factory.concurrent.LockIndexFactory;

import net.conselldemallorca.helium.core.util.GlobalProperties;

/** Classe pròpia que extén de SimpleIndexFactoryBean per poder fixer la classe de HeliumConcurrentMergeScheduler al IndexWriter
 * i controlar si fer o no el merge dels índexos segons l'espai que quedi buit a la unitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumLockIndexFactory extends LockIndexFactory {
	

	/** Referència al scheduler per només tenir-ne un. */
	private HeliumConcurrentMergeScheduler heliumConcurrentMergeScheduler = null;
	
	@Override
	public LuceneIndexWriter getIndexWriter() {

		// Crida al getIndexWriter de la classe LockIndexFactory.
		LuceneIndexWriter luceneIndexWriter =  super.getIndexWriter();
		// Canvia el ConcurrenMergeScheduler
		try {
			// Obté el LuceneIndexWriter deproxied
			InvocationHandler handler = java.lang.reflect.Proxy.getInvocationHandler(luceneIndexWriter);
			Field field = handler.getClass().getDeclaredField("indexWriter");
			field.setAccessible(true);
			LuceneIndexWriter original =
			        (LuceneIndexWriter) field.get(handler);
			// Accedeix a l'índex writer per reflection
			field = SimpleLuceneIndexWriter.class.getDeclaredField("indexWriter");
			field.setAccessible(true);
			IndexWriter indexWriter = (IndexWriter) field.get(original);
			// Configura l'scheduler propi d'Helium per decidir si fer el merge o no segons l'espai buit.
			if (heliumConcurrentMergeScheduler == null) {
				File indexDirectory = new File(GlobalProperties.getInstance().getProperty("app.lucene.fs.basedir"));
				heliumConcurrentMergeScheduler = new HeliumConcurrentMergeScheduler(indexDirectory);
			}
			indexWriter.setMergeScheduler(heliumConcurrentMergeScheduler);
		} catch (Exception e) {
			throw new RuntimeException("Error obtenint l'indexWriter: " + e.getMessage(), e);
		}
		return luceneIndexWriter;
	}
}
