/**
 * 
 */
package net.conselldemallorca.helium.model.dao;

import java.util.Date;
import java.util.List;

import net.conselldemallorca.helium.model.hibernate.DocumentStore;
import net.conselldemallorca.helium.model.hibernate.DocumentStore.DocumentFont;

import org.springframework.stereotype.Repository;

/**
 * Dao pels objectes de tipus document
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
@Repository
public class DocumentStoreDao extends HibernateGenericDao<DocumentStore, Long> {

	public DocumentStoreDao() {
		super(DocumentStore.class);
	}

	public Long create(
			String processInstanceId,
			String jbpmVariable,
			Date dataDocument,
			DocumentFont documentFont,
			String arxiuNom,
			byte[] arxiuContingut,
			boolean adjunt,
			String adjuntTitol) {
		DocumentStore ds = new DocumentStore(
				documentFont,
				processInstanceId,
				jbpmVariable,
				new Date(),
				dataDocument,
				arxiuNom);
		ds.setAdjunt(adjunt);
		if (adjunt)
			ds.setAdjuntTitol(adjuntTitol);
		if (arxiuContingut != null)
			ds.setArxiuContingut(arxiuContingut);
		getHibernateTemplate().saveOrUpdate(ds);
		return ds.getId();
	}

	public void update(
			Long id,
			Date dataDocument,
			String arxiuNom,
			byte[] arxiuContingut,
			String adjuntTitol) {
		DocumentStore ds = this.getById(id, false);
		ds.setDataDocument(dataDocument);
		ds.setDataModificacio(new Date());
		if (ds.isAdjunt())
			ds.setAdjuntTitol(adjuntTitol);
		ds.setArxiuNom(arxiuNom);
		if (arxiuContingut != null) {
			ds.setArxiuContingut(arxiuContingut);
		}
	}

	public void updateReferenciaFont(
			Long id,
			String referenciaFont) {
		DocumentStore ds = this.getById(id, false);
		ds.setReferenciaFont(referenciaFont);
	}

	public void updateRegistreEntrada(
			Long id,
			Date data,
			String numero,
			String organCodi,
			String oficinaCodi,
			String oficinaNom) {
		DocumentStore ds = this.getById(id, false);
		ds.setRegistreData(data);
		ds.setRegistreNumero(numero);
		ds.setRegistreOficinaCodi(oficinaCodi);
		ds.setRegistreOficinaNom(oficinaNom);
		ds.setRegistreEntrada(true);
	}
	public void updateRegistreSortida(
			Long id,
			Date data,
			String numero,
			String organCodi,
			String oficinaCodi,
			String oficinaNom) {
		DocumentStore ds = this.getById(id, false);
		ds.setRegistreData(data);
		ds.setRegistreNumero(numero);
		ds.setRegistreOficinaCodi(oficinaCodi);
		ds.setRegistreOficinaNom(oficinaNom);
		ds.setRegistreEntrada(false);
	}

	@SuppressWarnings("unchecked")
	public List<DocumentStore> findAmbProcessInstanceId(String processInstanceId) {
		return getHibernateTemplate().find(
				"from " +
				"    DocumentStore ds " +
				"where " +
				"    ds.processInstanceId=? " +
				"order by " +
				"    ds.dataDocument",
				processInstanceId);
	}


/*
	private String alfrescoSave(
			String[] path,
			String nom,
			String nomArxiu,
			String etiquetaArxiu,
			Date dataArxiu,
			byte[] contingut) throws AlfrescoException {
		startAlfrescoSession();
		try {
			Reference ref = createAlfrescoDocumentPath(path);
			ParentReference parent = getReferenceToParent(ref);
			parent.setChildName(Constants.createQNameString(
					Constants.NAMESPACE_CONTENT_MODEL,
					normalizeNodeName(nom)));
			NamedValue[] properties = new NamedValue[]{
					Utils.createNamedValue(Constants.PROP_NAME, nom),
					Utils.createNamedValue(Constants.PROP_TITLE, etiquetaArxiu),
					Utils.createNamedValue(Constants.PROP_DESCRIPTION, "Nom arxiu: " + nomArxiu + "\nData document: " + new SimpleDateFormat("dd/MM/yyyy").format(dataArxiu))};
			CMLCreate create = new CMLCreate("1", parent, null, null, null, Constants.TYPE_CONTENT, properties);
			CML cml = new CML();
			cml.setCreate(new CMLCreate[]{create});
			UpdateResult[] result = getAlfrescoRepositoryService().update(cml);
			Reference newContentNode = result[0].getDestination();
			Content content = getAlfrescoContentService().write(
					newContentNode,
					Constants.PROP_CONTENT,
					contingut,
					new ContentFormat(new MimetypesFileTypeMap().getContentType(nom), null));
			return content.getNode().getUuid();
		} catch (Exception ex) {
			logger.error("No s'ha pogut guardar l'arxiu a dins el servidor Alfresco", ex);
			throw new AlfrescoException("No s'ha pogut guardar l'arxiu", ex);
		} finally {
			endAlfrescoSession();
		}
	}

	private void alfrescoDelete(String uuid, boolean esborrarPareSiBuit) throws AlfrescoException {
		startAlfrescoSession();
		try {
			Reference ref = new Reference(ALFRESCO_STORE, uuid, null);
			CMLDelete delete = new CMLDelete(new Predicate(new Reference[]{ref}, ALFRESCO_STORE, null));
			CML cml = new CML();
			cml.setDelete(new CMLDelete[]{delete});
			try {
				getAlfrescoRepositoryService().update(cml);
			} catch (Exception ex) {
				logger.error("No s'ha pogut esborrar l'arxiu del servidor Alfresco", ex);
				throw new AlfrescoException("No s'ha pogut esborrar l'arxiu", ex);
			}
		} finally {
			endAlfrescoSession();
		}
	}

	private byte[] alfrescoRetrieve(String uuid) throws AlfrescoException {
		startAlfrescoSession();
		try {
			Reference ref = new Reference(ALFRESCO_STORE, uuid, null);
			try {
				Content[] readResult = getAlfrescoContentService().read(
	                    new Predicate(new Reference[]{ref}, ALFRESCO_STORE, null), 
	                    Constants.PROP_CONTENT);
				InputStream is = ContentUtils.getContentAsInputStream(readResult[0]);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int nread = 0;
				byte[] buffer = new byte[4096];
				while ((nread = is.read(buffer)) > 0)
					baos.write(buffer, 0, nread);
				return baos.toByteArray();
			} catch (Exception ex) {
				logger.error("No s'ha pogut llegir l'arxiu del servidor Alfresco", ex);
				throw new AlfrescoException("No s'ha pogut llegir l'arxiu", ex);
			}
		} finally {
			endAlfrescoSession();
		}
	}

	private Reference createAlfrescoDocumentPath(String[] path) throws Exception {
		String baseDir = GlobalProperties.getInstance().getProperty("app.docstore.alfresco.basedir");
		String[] baseDirParts;
		if (baseDir.startsWith("/"))
			baseDirParts = baseDir.substring(1).split("/");
		else
			baseDirParts = baseDir.split("/");
		String[] totalPath = new String[path.length + baseDirParts.length];
		for (int i = 0; i < baseDirParts.length; i++)
			totalPath[i] = replaceForbiddenChars(baseDirParts[i]);
		for (int i = 0; i < path.length; i++)
			totalPath[baseDirParts.length + i] = replaceForbiddenChars(path[i]);
		Reference ref = retrieveOrCreateSpace(totalPath[0]);
		for (int i = 1; i < totalPath.length; i++)
			ref = retrieveOrCreateSpace(ref, totalPath[i]);
		return ref;
	}
	private ParentReference getCompanyHome() {	
		ParentReference companyHomeParent = new ParentReference(ALFRESCO_STORE, null, "/app:company_home", Constants.ASSOC_CONTAINS, null);
		return companyHomeParent;
	}
	private Reference retrieveOrCreateSpace(String spaceName) throws Exception {
		return retrieveOrCreateSpace(null, spaceName);
	}
	private Reference retrieveOrCreateSpace(Reference parentref, String spaceName) throws Exception {
		Reference space = null;
		ParentReference parent;
		if (parentref != null)
			parent = getReferenceToParent(parentref);
		else
			parent = getCompanyHome();
		// Create the space if it is not already existent
		try {
			// Therefore a reference to the maybe not existent space is required
			space = new Reference(ALFRESCO_STORE, null, parent.getPath() + "/cm:" + normalizeNodeName(spaceName));
			getAlfrescoRepositoryService().get(new Predicate(new Reference[]{space}, ALFRESCO_STORE, null));
		} catch (Exception notFound) {
			// Set parent as the parent space
			parent.setChildName(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, spaceName));
			//Set the space's property name
			NamedValue[] properties = new NamedValue[]{Utils.createNamedValue(Constants.PROP_NAME, spaceName)};
			// Create the space using CML (Content Manipulation Language)
			CMLCreate create = new CMLCreate("1", parent, null, null, null, Constants.TYPE_FOLDER, properties);
			CML cml = new CML();
			cml.setCreate(new CMLCreate[]{create});
			// Execute the CML create statement
			try {
				getAlfrescoRepositoryService().update(cml);
			} catch (Exception ex) {
				logger.error("No s'ha pogut crear l'space " + spaceName, ex);
				throw ex;
			}
		}
		return space;
	}
	private ParentReference getReferenceToParent(Reference spaceref) {
		ParentReference parent = new ParentReference();
		parent.setStore(ALFRESCO_STORE);
		parent.setPath(spaceref.getPath());
		parent.setUuid(spaceref.getUuid());
		parent.setAssociationType(Constants.ASSOC_CONTAINS);
		return parent;
	}
	private String normalizeNodeName(String src) {
		// Conversió a ISO9075
		Pattern pattern = Pattern.compile("^\\d");
		Matcher matcher = pattern.matcher(src);
		String resultat = src;
		if (matcher.find())
			resultat = "_x003" + src.substring(0, 1) + "_" + src.substring(1);
		return resultat.replace(" ", "_x0020_");
	}
	private String replaceForbiddenChars(String src) {
		String s = src;
		for (char c: forbidden)
			s = s.replace(c, '_');
		return s;
	}



	private RepositoryServiceSoapBindingStub getAlfrescoRepositoryService() {
		configureAlfrescoRepository();
		return WebServiceFactory.getRepositoryService();
	}
	private ContentServiceSoapBindingStub getAlfrescoContentService() {
		configureAlfrescoRepository();
		return WebServiceFactory.getContentService();
	}
	private boolean repoConfigured = false;
	private void configureAlfrescoRepository() {
		String alfrescoApiurl = GlobalProperties.getInstance().getProperty("app.docstore.alfresco.apiurl");
		if (!repoConfigured) {
			WebServiceFactory.setEndpointAddress(alfrescoApiurl);
			repoConfigured = true;
		}
	}
	private void startAlfrescoSession() throws AlfrescoException {
		try {
			configureAlfrescoRepository();
			String alfrescoUser = GlobalProperties.getInstance().getProperty("app.docstore.alfresco.user");
			String alfrescoPass = GlobalProperties.getInstance().getProperty("app.docstore.alfresco.pass");
			AuthenticationUtils.startSession(alfrescoUser, alfrescoPass);
		} catch (Exception ex) {
			logger.error("No s'ha pogut iniciar la sessió amb Alfresco", ex);
			throw new AlfrescoException("No s'ha pogut iniciar la sessió amb Alfresco", ex);
		}
	}
	private void endAlfrescoSession() {
		configureAlfrescoRepository();
		AuthenticationUtils.endSession();
	}

	private boolean isAlfrescoActive() {
		String alfrescoActiu = GlobalProperties.getInstance().getProperty("app.docstore.alfresco.actiu");
		return "true".equalsIgnoreCase(alfrescoActiu);
	}



	private static char[] forbidden = {
		'/', ':', '*', '?',
		'\"', '<', '>', '|',
		';', '&', '%', '+',
		'\\'};
	private static final Store ALFRESCO_STORE = new Store(Constants.WORKSPACE_STORE, "SpacesStore");
	private static final Log logger = LogFactory.getLog(DocumentStoreDao.class);
*/
}
