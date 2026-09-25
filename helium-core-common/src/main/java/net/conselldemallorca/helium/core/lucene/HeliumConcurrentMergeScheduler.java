package net.conselldemallorca.helium.core.lucene;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.MergePolicy.OneMerge;
import org.apache.lucene.index.SegmentInfos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.conselldemallorca.helium.v3.core.api.dto.IndexInfoDto;

/** Extensió de ConcurrentMergeScheduler injectada a l'IndexWriter de Lucene per decidir si fer el merge
 * o no segons l'espai de disc disponible al directori on estan els arxius de Lucene.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HeliumConcurrentMergeScheduler extends ConcurrentMergeScheduler {
	
	private File indexDirectory;
	/**
     * Marge de seguretat 10 GB.
     */
    public static final long MARGE_SEGURETAT_BYTES = 10L * 1024L * 1024L * 1024L;

	public HeliumConcurrentMergeScheduler(File indexDirectory) {
		super();
		this.indexDirectory = indexDirectory;
	}

	/** Sobreescriu el mètode que fa el merge per decidir o no fer-lo en funció de si hi ha prou espai en el disc.
	 * 
	 */
	@Override 
	protected void doMerge(MergePolicy.OneMerge merge) throws IOException { 
		boolean hiHaEspai = false;
		try {
			hiHaEspai = hiHaProuEspaiDisc(merge);
		} catch(Exception e) {
			logger.error("Error calculant si hi ha prou espai a disc per fer el merge: " + e.getMessage());
		}
		if (!hiHaEspai)
			return;
		super.doMerge(merge); 
	}

	/** Mètdode per comprovar si hi ha prou espai al disc per realitzar el merge dels índexos de Lucene. 
	 * @throws Exception 
	 * @throws  */
	private boolean hiHaProuEspaiDisc(OneMerge merge) throws Exception {
		
		Field field = MergePolicy.OneMerge.class.getDeclaredField("segments");
		field.setAccessible(true);
		SegmentInfos segmentInfos = (SegmentInfos) field.get(this);

		IndexInfoDto indexInfo = HeliumLuceneUtils.comprovarIndex(indexDirectory, segmentInfos);
		return indexInfo.isCorrecte();
	} 
	
	private static final Logger logger = LoggerFactory.getLogger(HeliumConcurrentMergeScheduler.class);
}
