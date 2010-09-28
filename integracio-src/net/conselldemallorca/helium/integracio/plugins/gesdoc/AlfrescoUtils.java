/**
 * 
 */
package net.conselldemallorca.helium.integracio.plugins.gesdoc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import org.alfresco.webservice.authentication.AuthenticationFault;
import org.alfresco.webservice.content.Content;
import org.alfresco.webservice.content.ContentFault;
import org.alfresco.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.webservice.repository.QueryResult;
import org.alfresco.webservice.repository.RepositoryFault;
import org.alfresco.webservice.repository.RepositoryServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.CMLDelete;
import org.alfresco.webservice.types.CMLUpdate;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.ResultSet;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ContentUtils;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;

/**
 * Funcions per a interactuar amb l'API de Alfresco
 * 
 * @author Josep Gayà <josepg@limit.es>
 */
public class AlfrescoUtils {

	private String apiUrl;
	private String userName;
	private String password;

	public AlfrescoUtils(String apiUrl, String userName, String password) {
		this.apiUrl = apiUrl;
		this.userName = userName;
		this.password = password;
	}

	public Reference getAlfrescoDocumentPath(String baseDir, String[] path, boolean create) throws RepositoryFault, RemoteException {
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
	public String createContent(
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
	public void updateNodeProperties(Reference ref, Map<String, String> properties) throws RepositoryFault, RemoteException {
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
	public byte[] retrieveContent(Reference ref) throws IOException {
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
	public byte[] retrieveContentFromParent(Reference parent, String contentName) throws IOException {
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
	public void deleteContent(Reference ref) throws RepositoryFault, RemoteException {
		CMLDelete delete = new CMLDelete(new Predicate(new Reference[]{ref}, ALFRESCO_STORE, null));
		CML cml = new CML();
		cml.setDelete(new CMLDelete[]{delete});
		getAlfrescoRepositoryService().update(cml);
	}
	public void deleteContentFromParent(Reference parent, String contentName) throws IOException {
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
	public Reference getParent(Reference space) throws RepositoryFault, RemoteException {
		QueryResult result = getAlfrescoRepositoryService().queryParents(space);
		ResultSet resultset = result.getResultSet();
		String first_parent_id = resultset.getRows(0).getNode().getId();
		Reference parent = new Reference(ALFRESCO_STORE, first_parent_id, null);
		return parent;
	}
	public Reference getReferenceForUuid(String uuid) {
		return new Reference(ALFRESCO_STORE, uuid, null);
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

	private RepositoryServiceSoapBindingStub getAlfrescoRepositoryService() {
		configureAlfrescoRepository();
		return WebServiceFactory.getRepositoryService();
	}
	private ContentServiceSoapBindingStub getAlfrescoContentService() {
		configureAlfrescoRepository();
		return WebServiceFactory.getContentService();
	}

	public String startAlfrescoSession() throws AuthenticationFault {
		configureAlfrescoRepository();
		AuthenticationUtils.startSession(userName, password);
		return AuthenticationUtils.getAuthenticationDetails().getTicket();
	}
	public void endAlfrescoSession() {
		configureAlfrescoRepository();
		AuthenticationUtils.endSession();
	}
	private boolean repoConfigured = false;
	private void configureAlfrescoRepository() {
		if (!repoConfigured) {
			WebServiceFactory.setEndpointAddress(apiUrl);
			repoConfigured = true;
		}
	}



	private static char[] forbidden = {
		'/', ':', '*', '?',
		'\"', '<', '>', '|',
		';', '&', '%', '+',
		'\\'};
	private static final Store ALFRESCO_STORE = new Store(Constants.WORKSPACE_STORE, "SpacesStore");

}
