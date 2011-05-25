/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.gesdoc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.conselldemallorca.helium.core.util.GlobalProperties;
import net.conselldemallorca.helium.integracio.plugins.util.AlfrescoUtils;

import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementació del plugin de gestió documental amb Alfresco
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GestioDocumentalPluginAlfresco implements GestioDocumentalPlugin {

	private static final String CONTENT_DATA_NAME = "data";

	private AlfrescoUtils alfrescoUtils;



	public GestioDocumentalPluginAlfresco() {
		alfrescoUtils = new AlfrescoUtils(
				GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.apiurl"),
				GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.user"),
				GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.pass"));
	}

	public String createDocument(
			String expedientNumero,
			String expedientTipus,
			String documentCodi,
			String documentDescripcio,
			Date documentData,
			String documentArxiuNom,
			byte[] documentArxiuContingut) throws GestioDocumentalPluginException {
		try {
			alfrescoUtils.startAlfrescoSession();
			Reference ref = alfrescoUtils.getAlfrescoDocumentPath(
					GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.basedir"),
					getPathDocument(
							expedientNumero,
							expedientTipus,
							documentCodi,
							documentData),
					true);
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(Constants.PROP_DESCRIPTION, documentDescripcio);
			alfrescoUtils.updateNodeProperties(ref, properties);
			return alfrescoUtils.createContent(
					ref,
					CONTENT_DATA_NAME,
					documentArxiuNom,
					"Data: " + new SimpleDateFormat("dd/MM/yyyy").format(documentData),
					documentArxiuContingut);
		} catch (Exception ex) {
			logger.error("Error al guardar l'arxiu dins el gestor documental", ex);
			throw new GestioDocumentalPluginException("Error al guardar l'arxiu dins el gestor documental", ex);
		} finally {
			alfrescoUtils.endAlfrescoSession();
		}
	}

	public byte[] retrieveDocument(String documentId) throws GestioDocumentalPluginException {
		try {
			alfrescoUtils.startAlfrescoSession();
			return alfrescoUtils.retrieveContent(
					alfrescoUtils.getReferenceForUuid(documentId));
		} catch (Exception ex) {
			logger.error("Error al llegir l'arxiu del gestor documental", ex);
			throw new GestioDocumentalPluginException("Error al llegir l'arxiu del gestor documental", ex);
		} finally {
			alfrescoUtils.endAlfrescoSession();
		}
	}

	public void deleteDocument(String documentId) throws GestioDocumentalPluginException {
		try {
			alfrescoUtils.startAlfrescoSession();
			alfrescoUtils.deleteContent(
					alfrescoUtils.getParent(
							alfrescoUtils.getReferenceForUuid(documentId)));
		} catch (Exception ex) {
			logger.error("Error al esborrar l'arxiu del gestor documental", ex);
			throw new GestioDocumentalPluginException("Error al esborrar l'arxiu del gestor documental", ex);
		} finally {
			alfrescoUtils.endAlfrescoSession();
		}
	}

	/*public void setDocumentView(
			String documentId,
			byte[] view) throws GestioDocumentalPluginException {
		try {
			alfrescoUtils.startAlfrescoSession();
			alfrescoUtils.createContent(
					alfrescoUtils.getParent(
							alfrescoUtils.getReferenceForUuid(documentId)),
					VIEW_DATA_NAME,
					null,
					null,
					view);
		} catch (Exception ex) {
			logger.error("Error al guardar la vista dins el gestor documental", ex);
			throw new AlfrescoException("Error al guardar la vista dins el gestor documental", ex);
		} finally {
			alfrescoUtils.endAlfrescoSession();
		}
	}

	public byte[] getDocumentView(
			String documentId) throws GestioDocumentalPluginException {
		try {
			alfrescoUtils.startAlfrescoSession();
			return alfrescoUtils.retrieveContentFromParent(
					alfrescoUtils.getParent(
							alfrescoUtils.getReferenceForUuid(documentId)),
					VIEW_DATA_NAME);
		} catch (Exception ex) {
			logger.error("Error al llegir la vista del gestor documental", ex);
			throw new AlfrescoException("Error al llegir la vista del gestor documental", ex);
		} finally {
			alfrescoUtils.endAlfrescoSession();
		}
	}

	public void addSignatureToDocument(
			String documentId,
			byte[] signature) throws GestioDocumentalPluginException {
		try {
			alfrescoUtils.startAlfrescoSession();
			Reference parent = alfrescoUtils.getParent(
					alfrescoUtils.getReferenceForUuid(documentId));
			int index = 0;
			boolean found;
			do {
				index++;
				found = false;
				try {
					alfrescoUtils.retrieveContentFromParent(parent, SIGNATURE_DATA_NAME + index);
					found = true;
				} catch (IOException ex) {
					// Si no existeix es llança aquesta excepció
				}
			} while (found);
			alfrescoUtils.createContent(
					parent,
					SIGNATURE_DATA_NAME + index,
					null,
					null,
					signature);
		} catch (Exception ex) {
			logger.error("Error al afegir la signatura dins el gestor documental", ex);
			throw new AlfrescoException("Error al afegir la signatura dins el gestor documental", ex);
		} finally {
			alfrescoUtils.endAlfrescoSession();
		}
	}

	public List<byte[]> getSignaturesFromDocument(
			String documentId) throws GestioDocumentalPluginException {
		try {
			alfrescoUtils.startAlfrescoSession();
			Reference parent = alfrescoUtils.getParent(
					alfrescoUtils.getReferenceForUuid(documentId));
			List<byte[]> resposta = new ArrayList<byte[]>();
			int index = 0;
			boolean found;
			do {
				index++;
				found = false;
				try {
					byte[] sc = alfrescoUtils.retrieveContentFromParent(parent, SIGNATURE_DATA_NAME + index);
					resposta.add(sc);
					found = true;
				} catch (IOException ex) {
					// Si no existeix es llança aquesta excepció
				}
			} while (found);
			return resposta;
		} catch (Exception ex) {
			logger.error("Error al llegir la signatura del gestor documental", ex);
			throw new AlfrescoException("Error al llegir la signatura del gestor documental", ex);
		} finally {
			alfrescoUtils.endAlfrescoSession();
		}
	}

	public void deleteSignatureFromDocument(String documentId, int index)
			throws GestioDocumentalPluginException {
		try {
			alfrescoUtils.startAlfrescoSession();
			Reference parent = alfrescoUtils.getParent(
					alfrescoUtils.getReferenceForUuid(documentId));
			alfrescoUtils.deleteContentFromParent(parent, SIGNATURE_DATA_NAME + index);
		} catch (Exception ex) {
			logger.error("Error al esborrar la signatura del gestor documental", ex);
			throw new AlfrescoException("Error al esborrar la signatura del gestor documental", ex);
		} finally {
			alfrescoUtils.endAlfrescoSession();
		}
	}*/



	private String[] getPathDocument(
			String expedientNumero,
			String expedientTipus,
			String documentCodi,
			Date documentData) {
		// tipus/any/numero/codi
		String[] parts = expedientTipus.split("#");
		String entornCodi = parts[0];
		String expedientTipusCodi = parts[1];
		return new String[] {
				entornCodi,
				expedientTipusCodi,
				new SimpleDateFormat("yyyy").format(documentData),
				expedientNumero,
				documentCodi};
	}



	/*private Reference getAlfrescoDocumentPath(String[] path, boolean create) throws RepositoryFault, RemoteException {
		String baseDir = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.basedir");
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
		Reference ref = retrieveOrCreateSpace(totalPath[0], create);
		for (int i = 1; i < totalPath.length; i++)
			ref = retrieveOrCreateSpace(ref, totalPath[i], create);
		return ref;
	}
	private String createContent(
			Reference ref,
			String name,
			String title,
			String description,
			byte[] data) throws RemoteException, ContentFault {
		ParentReference parent = getReferenceToParent(ref);
		parent.setChildName(Constants.createQNameString(
				Constants.NAMESPACE_CONTENT_MODEL,
				normalizeNodeName(name)));
		NamedValue[] properties = new NamedValue[]{
				Utils.createNamedValue(Constants.PROP_NAME, name),
				Utils.createNamedValue(Constants.PROP_TITLE, title),
				Utils.createNamedValue(Constants.PROP_DESCRIPTION, description)};
		CMLCreate create = new CMLCreate("1", parent, null, null, null, Constants.TYPE_CONTENT, properties);
		CML cml = new CML();
		cml.setCreate(new CMLCreate[]{create});
		UpdateResult[] result = getAlfrescoRepositoryService().update(cml);
		Reference newContentNode = result[0].getDestination();
		Content content = getAlfrescoContentService().write(
				newContentNode,
				Constants.PROP_CONTENT,
				data,
				new ContentFormat(new MimetypesFileTypeMap().getContentType(name), null));
		return content.getNode().getUuid();
	}
	private void updateNodeProperties(Reference ref, Map<String, String> properties) throws RepositoryFault, RemoteException {
		NamedValue[] props = new NamedValue[properties.size()];
		int index = 0;
		for (String p: properties.keySet())
			props[index++] = Utils.createNamedValue(p, properties.get(p));
        CMLUpdate update = new CMLUpdate(
        		props,
        		new Predicate(new Reference[] {ref}, ALFRESCO_STORE, null),
        		null);       
        CML cml = new CML();
        cml.setUpdate(new CMLUpdate[]{update});
        getAlfrescoRepositoryService().update(cml);
	}
	private byte[] retrieveContent(Reference ref) throws IOException {
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
	}
	private byte[] retrieveContentFromParent(Reference parent, String contentName) throws IOException {
		Content[] readResult = getAlfrescoContentService().read(
                new Predicate(new Reference[]{parent}, ALFRESCO_STORE, null), 
                Constants.PROP_CONTENT);
		if (readResult.length > 0) {
			Reference ref = new Reference(
					ALFRESCO_STORE,
					null,
					readResult[0].getNode().getPath() + "/cm:" + normalizeNodeName(contentName));
			return retrieveContent(ref);
		}
		return null;
	}
	private void deleteContent(Reference ref) throws RepositoryFault, RemoteException {
		CMLDelete delete = new CMLDelete(new Predicate(new Reference[]{ref}, ALFRESCO_STORE, null));
		CML cml = new CML();
		cml.setDelete(new CMLDelete[]{delete});
		getAlfrescoRepositoryService().update(cml);
	}
	private void deleteContentFromParent(Reference parent, String contentName) throws IOException {
		Content[] readResult = getAlfrescoContentService().read(
                new Predicate(new Reference[]{parent}, ALFRESCO_STORE, null), 
                Constants.PROP_CONTENT);
		if (readResult.length > 0) {
			Reference ref = new Reference(
					ALFRESCO_STORE,
					null,
					readResult[0].getNode().getPath() + "/cm:" + normalizeNodeName(contentName));
			deleteContent(ref);
		}
	}
	private Reference getParent(Reference space) throws RepositoryFault, RemoteException {
		QueryResult result = getAlfrescoRepositoryService().queryParents(space);
		ResultSet resultset = result.getResultSet();
		String first_parent_id = resultset.getRows(0).getNode().getId();
		Reference parent = new Reference(ALFRESCO_STORE, first_parent_id, null);
		return parent;
	}

	private ParentReference getCompanyHome() {	
		ParentReference companyHomeParent = new ParentReference(ALFRESCO_STORE, null, "/app:company_home", Constants.ASSOC_CONTAINS, null);
		return companyHomeParent;
	}
	private Reference retrieveOrCreateSpace(String spaceName, boolean create) throws RepositoryFault, RemoteException {
		return retrieveOrCreateSpace(null, spaceName, create);
	}
	private Reference retrieveOrCreateSpace(Reference parentref, String spaceName, boolean create) throws RepositoryFault, RemoteException {
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
			if (create) {
				// Set parent as the parent space
				parent.setChildName(Constants.createQNameString(Constants.NAMESPACE_CONTENT_MODEL, spaceName));
				//Set the space's property name
				NamedValue[] properties = new NamedValue[]{Utils.createNamedValue(Constants.PROP_NAME, spaceName)};
				// Create the space using CML (Content Manipulation Language)
				CMLCreate cmlcreate = new CMLCreate("1", parent, null, null, null, Constants.TYPE_FOLDER, properties);
				CML cml = new CML();
				cml.setCreate(new CMLCreate[]{cmlcreate});
				// Execute the CML create statement
				getAlfrescoRepositoryService().update(cml);
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
		return resultat.replace(" ", "_x0020_").replace(",", "_x002c_");
	}
	private String replaceForbiddenChars(String src) {
		String s = src;
		for (char c: forbidden)
			s = s.replace(c, '_');
		return s;
	}

	private String[] getPathDocument(
			String expedientNumero,
			String expedientTipus,
			String documentCodi,
			Date documentData) {
		// tipus/any/numero/codi
		String[] parts = expedientTipus.split("#");
		String entornCodi = parts[0];
		String expedientTipusCodi = parts[1];
		return new String[] {
				entornCodi,
				expedientTipusCodi,
				new SimpleDateFormat("yyyy").format(documentData),
				expedientNumero,
				documentCodi};
	}

	private RepositoryServiceSoapBindingStub getAlfrescoRepositoryService() {
		configureAlfrescoRepository();
		return WebServiceFactory.getRepositoryService();
	}
	private ContentServiceSoapBindingStub getAlfrescoContentService() {
		configureAlfrescoRepository();
		return WebServiceFactory.getContentService();
	}

	private String startAlfrescoSession() throws AuthenticationFault {
		configureAlfrescoRepository();
		String alfrescoUser = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.user");
		String alfrescoPass = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.pass");
		AuthenticationUtils.startSession(alfrescoUser, alfrescoPass);
		return AuthenticationUtils.getAuthenticationDetails().getTicket();
	}
	private void endAlfrescoSession() {
		configureAlfrescoRepository();
		AuthenticationUtils.endSession();
	}
	private boolean repoConfigured = false;
	private void configureAlfrescoRepository() {
		String alfrescoApiurl = GlobalProperties.getInstance().getProperty("app.gesdoc.plugin.apiurl");
		if (!repoConfigured) {
			WebServiceFactory.setEndpointAddress(alfrescoApiurl);
			repoConfigured = true;
		}
	}



	private static char[] forbidden = {
		'/', ':', '*', '?',
		'\"', '<', '>', '|',
		';', '&', '%', '+',
		'\\'};
	private static final Store ALFRESCO_STORE = new Store(Constants.WORKSPACE_STORE, "SpacesStore");*/
	private static final Log logger = LogFactory.getLog(GestioDocumentalPluginAlfresco.class);

}
