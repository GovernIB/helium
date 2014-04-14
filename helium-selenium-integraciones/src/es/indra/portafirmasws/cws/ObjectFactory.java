
package es.indra.portafirmasws.cws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.indra.portafirmasws.cws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ListResponseDocumentArchiveOptions_QNAME = new QName("", "archive-options");
    private final static QName _ListServerSignersResponseServerSigners_QNAME = new QName("", "server-signers");
    private final static QName _VisualFileExternalIds_QNAME = new QName("", "external-ids");
    private final static QName _ArchiveLocatorSignatureCustody_QNAME = new QName("", "signature-custody");
    private final static QName _ArchiveLocatorVisualFilePathName_QNAME = new QName("", "visual-file-path-name");
    private final static QName _ArchiveLocatorFilePathName_QNAME = new QName("", "file-path-name");
    private final static QName _ArchiveLocatorSignatureFilesPathPattern_QNAME = new QName("", "signature-files-path-pattern");
    private final static QName _ArchiveLocatorFolderName_QNAME = new QName("", "folder-name");
    private final static QName _ArchiveLocatorRetentionPolicy_QNAME = new QName("", "retention-policy");
    private final static QName _ArchiveLocatorArchiveUri_QNAME = new QName("", "archive-uri");
    private final static QName _ArchiveLocatorArchiveVersion_QNAME = new QName("", "archive-version");
    private final static QName _ArchiveLocatorRepositoryBase_QNAME = new QName("", "repository-base");
    private final static QName _SearchRequestSearchCriterias_QNAME = new QName("", "search-criterias");
    private final static QName _StepsSignMode_QNAME = new QName("", "sign-mode");
    private final static QName _ArchiveOptionsSourceLocators_QNAME = new QName("", "source-locators");
    private final static QName _ArchiveOptionsArchiveMetadatas_QNAME = new QName("", "archive-metadatas");
    private final static QName _ArchiveOptionsDestinationLocators_QNAME = new QName("", "destination-locators");
    private final static QName _ExternalIDsVerificationCode_QNAME = new QName("", "verification-code");
    private final static QName _ExternalIDsLogicalId_QNAME = new QName("", "logical-id");
    private final static QName _DownloadResponseDocumentVisualFiles_QNAME = new QName("", "visual-files");
    private final static QName _DownloadResponseDocumentSignatureFiles_QNAME = new QName("", "signature-files");
    private final static QName _ListStepSignersNone_QNAME = new QName("", "signers-none");
    private final static QName _ListStepSignersReject_QNAME = new QName("", "signers-reject");
    private final static QName _ListStepSignersAction_QNAME = new QName("", "signers-action");
    private final static QName _ListStepMinimalSigners_QNAME = new QName("", "minimal-signers");
    private final static QName _VerificationCodePdfPosition_QNAME = new QName("", "pdf-position");
    private final static QName _DownloadOptionsDownloadType_QNAME = new QName("", "download-type");
    private final static QName _AnnexTypeSign_QNAME = new QName("", "type-sign");
    private final static QName _AnnexIsFileSign_QNAME = new QName("", "is-file-sign");
    private final static QName _AnnexSignAnnex_QNAME = new QName("", "sign-annex");
    private final static QName _DownloadRequestArchiveInfo_QNAME = new QName("", "archive-info");
    private final static QName _DownloadRequestAdditionalInfo_QNAME = new QName("", "additional-info");
    private final static QName _DownloadRequestDownloadDocuments_QNAME = new QName("", "download-documents");
    private final static QName _SignerCheckCert_QNAME = new QName("", "check-cert");
    private final static QName _SignerPdfAppearance_QNAME = new QName("", "pdf-appearance");
    private final static QName _SignerIdUpdate_QNAME = new QName("", "id-update");
    private final static QName _PdfAppearanceSignatureImage_QNAME = new QName("", "signature-image");
    private final static QName _SignatureImageMimeType_QNAME = new QName("", "mime-type");
    private final static QName _PositionSignatureImageDimensions_QNAME = new QName("", "signature-image-dimensions");
    private final static QName _FileNumberSignatures_QNAME = new QName("", "number-signatures");
    private final static QName _FileBase64Data_QNAME = new QName("", "base64-data");
    private final static QName _FileSignersId_QNAME = new QName("", "signers-id");
    private final static QName _DocumentAttributesDateNotice_QNAME = new QName("", "date-notice");
    private final static QName _DocumentAttributesDateEntry_QNAME = new QName("", "date-entry");
    private final static QName _DocumentAttributesNumberAnnexes_QNAME = new QName("", "number-annexes");
    private final static QName _DocumentAttributesExternalData_QNAME = new QName("", "external-data");
    private final static QName _DocumentAttributesDateLimit_QNAME = new QName("", "date-limit");
    private final static QName _DocumentAttributesSignAnnexes_QNAME = new QName("", "sign-annexes");
    private final static QName _DocumentAttributesGenerateVisuals_QNAME = new QName("", "generate-visuals");
    private final static QName _DocumentAttributesDateLastUpdate_QNAME = new QName("", "date-last-update");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.indra.portafirmasws.cws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ListRequest }
     * 
     */
    public ListRequest createListRequest() {
        return new ListRequest();
    }

    /**
     * Create an instance of {@link Application }
     * 
     */
    public Application createApplication() {
        return new Application();
    }

    /**
     * Create an instance of {@link ListRequestDocuments }
     * 
     */
    public ListRequestDocuments createListRequestDocuments() {
        return new ListRequestDocuments();
    }

    /**
     * Create an instance of {@link SearchCriterias }
     * 
     */
    public SearchCriterias createSearchCriterias() {
        return new SearchCriterias();
    }

    /**
     * Create an instance of {@link UpdateRequest }
     * 
     */
    public UpdateRequest createUpdateRequest() {
        return new UpdateRequest();
    }

    /**
     * Create an instance of {@link UpdateRequestDocument }
     * 
     */
    public UpdateRequestDocument createUpdateRequestDocument() {
        return new UpdateRequestDocument();
    }

    /**
     * Create an instance of {@link ListTypeResponse }
     * 
     */
    public ListTypeResponse createListTypeResponse() {
        return new ListTypeResponse();
    }

    /**
     * Create an instance of {@link Result }
     * 
     */
    public Result createResult() {
        return new Result();
    }

    /**
     * Create an instance of {@link TypeDocuments }
     * 
     */
    public TypeDocuments createTypeDocuments() {
        return new TypeDocuments();
    }

    /**
     * Create an instance of {@link UpdateResponse }
     * 
     */
    public UpdateResponse createUpdateResponse() {
        return new UpdateResponse();
    }

    /**
     * Create an instance of {@link UpdateResponseDocument }
     * 
     */
    public UpdateResponseDocument createUpdateResponseDocument() {
        return new UpdateResponseDocument();
    }

    /**
     * Create an instance of {@link SearchResponse }
     * 
     */
    public SearchResponse createSearchResponse() {
        return new SearchResponse();
    }

    /**
     * Create an instance of {@link ListTypeRequest }
     * 
     */
    public ListTypeRequest createListTypeRequest() {
        return new ListTypeRequest();
    }

    /**
     * Create an instance of {@link DeleteRequest }
     * 
     */
    public DeleteRequest createDeleteRequest() {
        return new DeleteRequest();
    }

    /**
     * Create an instance of {@link DeleteRequestDocuments }
     * 
     */
    public DeleteRequestDocuments createDeleteRequestDocuments() {
        return new DeleteRequestDocuments();
    }

    /**
     * Create an instance of {@link SearchRequest }
     * 
     */
    public SearchRequest createSearchRequest() {
        return new SearchRequest();
    }

    /**
     * Create an instance of {@link DownloadFileRequest }
     * 
     */
    public DownloadFileRequest createDownloadFileRequest() {
        return new DownloadFileRequest();
    }

    /**
     * Create an instance of {@link DownloadFileRequestDocument }
     * 
     */
    public DownloadFileRequestDocument createDownloadFileRequestDocument() {
        return new DownloadFileRequestDocument();
    }

    /**
     * Create an instance of {@link DownloadFileResponse }
     * 
     */
    public DownloadFileResponse createDownloadFileResponse() {
        return new DownloadFileResponse();
    }

    /**
     * Create an instance of {@link DownloadFileResponseDocument }
     * 
     */
    public DownloadFileResponseDocument createDownloadFileResponseDocument() {
        return new DownloadFileResponseDocument();
    }

    /**
     * Create an instance of {@link DownloadRequest }
     * 
     */
    public DownloadRequest createDownloadRequest() {
        return new DownloadRequest();
    }

    /**
     * Create an instance of {@link DownloadRequestDocument }
     * 
     */
    public DownloadRequestDocument createDownloadRequestDocument() {
        return new DownloadRequestDocument();
    }

    /**
     * Create an instance of {@link ListResponse }
     * 
     */
    public ListResponse createListResponse() {
        return new ListResponse();
    }

    /**
     * Create an instance of {@link ListResponseDocuments }
     * 
     */
    public ListResponseDocuments createListResponseDocuments() {
        return new ListResponseDocuments();
    }

    /**
     * Create an instance of {@link UploadRequest }
     * 
     */
    public UploadRequest createUploadRequest() {
        return new UploadRequest();
    }

    /**
     * Create an instance of {@link UploadRequestDocument }
     * 
     */
    public UploadRequestDocument createUploadRequestDocument() {
        return new UploadRequestDocument();
    }

    /**
     * Create an instance of {@link UploadResponse }
     * 
     */
    public UploadResponse createUploadResponse() {
        return new UploadResponse();
    }

    /**
     * Create an instance of {@link UploadResponseDocument }
     * 
     */
    public UploadResponseDocument createUploadResponseDocument() {
        return new UploadResponseDocument();
    }

    /**
     * Create an instance of {@link DownloadResponse }
     * 
     */
    public DownloadResponse createDownloadResponse() {
        return new DownloadResponse();
    }

    /**
     * Create an instance of {@link DownloadResponseDocument }
     * 
     */
    public DownloadResponseDocument createDownloadResponseDocument() {
        return new DownloadResponseDocument();
    }

    /**
     * Create an instance of {@link DeleteResponse }
     * 
     */
    public DeleteResponse createDeleteResponse() {
        return new DeleteResponse();
    }

    /**
     * Create an instance of {@link DeleteResponseDocuments }
     * 
     */
    public DeleteResponseDocuments createDeleteResponseDocuments() {
        return new DeleteResponseDocuments();
    }

    /**
     * Create an instance of {@link ListServerSignersResponse }
     * 
     */
    public ListServerSignersResponse createListServerSignersResponse() {
        return new ListServerSignersResponse();
    }

    /**
     * Create an instance of {@link ServerSigners }
     * 
     */
    public ServerSigners createServerSigners() {
        return new ServerSigners();
    }

    /**
     * Create an instance of {@link ListServerSignersRequest }
     * 
     */
    public ListServerSignersRequest createListServerSignersRequest() {
        return new ListServerSignersRequest();
    }

    /**
     * Create an instance of {@link PdfAppearance }
     * 
     */
    public PdfAppearance createPdfAppearance() {
        return new PdfAppearance();
    }

    /**
     * Create an instance of {@link Format }
     * 
     */
    public Format createFormat() {
        return new Format();
    }

    /**
     * Create an instance of {@link DownloadOptions }
     * 
     */
    public DownloadOptions createDownloadOptions() {
        return new DownloadOptions();
    }

    /**
     * Create an instance of {@link DownloadStep }
     * 
     */
    public DownloadStep createDownloadStep() {
        return new DownloadStep();
    }

    /**
     * Create an instance of {@link VisualFile }
     * 
     */
    public VisualFile createVisualFile() {
        return new VisualFile();
    }

    /**
     * Create an instance of {@link ArchiveOptions }
     * 
     */
    public ArchiveOptions createArchiveOptions() {
        return new ArchiveOptions();
    }

    /**
     * Create an instance of {@link SignerSignatureFiles }
     * 
     */
    public SignerSignatureFiles createSignerSignatureFiles() {
        return new SignerSignatureFiles();
    }

    /**
     * Create an instance of {@link DocumentAttributes }
     * 
     */
    public DocumentAttributes createDocumentAttributes() {
        return new DocumentAttributes();
    }

    /**
     * Create an instance of {@link Files }
     * 
     */
    public Files createFiles() {
        return new Files();
    }

    /**
     * Create an instance of {@link ArchiveLocator }
     * 
     */
    public ArchiveLocator createArchiveLocator() {
        return new ArchiveLocator();
    }

    /**
     * Create an instance of {@link DeleteRequestDocument }
     * 
     */
    public DeleteRequestDocument createDeleteRequestDocument() {
        return new DeleteRequestDocument();
    }

    /**
     * Create an instance of {@link DestinationLocators }
     * 
     */
    public DestinationLocators createDestinationLocators() {
        return new DestinationLocators();
    }

    /**
     * Create an instance of {@link Steps }
     * 
     */
    public Steps createSteps() {
        return new Steps();
    }

    /**
     * Create an instance of {@link SignatureFiles }
     * 
     */
    public SignatureFiles createSignatureFiles() {
        return new SignatureFiles();
    }

    /**
     * Create an instance of {@link SignatureFile }
     * 
     */
    public SignatureFile createSignatureFile() {
        return new SignatureFile();
    }

    /**
     * Create an instance of {@link ServerSigner }
     * 
     */
    public ServerSigner createServerSigner() {
        return new ServerSigner();
    }

    /**
     * Create an instance of {@link TypeDocument }
     * 
     */
    public TypeDocument createTypeDocument() {
        return new TypeDocument();
    }

    /**
     * Create an instance of {@link PdfPosition }
     * 
     */
    public PdfPosition createPdfPosition() {
        return new PdfPosition();
    }

    /**
     * Create an instance of {@link Annex }
     * 
     */
    public Annex createAnnex() {
        return new Annex();
    }

    /**
     * Create an instance of {@link ArchiveMetadata }
     * 
     */
    public ArchiveMetadata createArchiveMetadata() {
        return new ArchiveMetadata();
    }

    /**
     * Create an instance of {@link VisualFiles }
     * 
     */
    public VisualFiles createVisualFiles() {
        return new VisualFiles();
    }

    /**
     * Create an instance of {@link ListRequestDocument }
     * 
     */
    public ListRequestDocument createListRequestDocument() {
        return new ListRequestDocument();
    }

    /**
     * Create an instance of {@link PendingResult }
     * 
     */
    public PendingResult createPendingResult() {
        return new PendingResult();
    }

    /**
     * Create an instance of {@link SourceLocators }
     * 
     */
    public SourceLocators createSourceLocators() {
        return new SourceLocators();
    }

    /**
     * Create an instance of {@link UpdateStep }
     * 
     */
    public UpdateStep createUpdateStep() {
        return new UpdateStep();
    }

    /**
     * Create an instance of {@link Annexes }
     * 
     */
    public Annexes createAnnexes() {
        return new Annexes();
    }

    /**
     * Create an instance of {@link Certificate }
     * 
     */
    public Certificate createCertificate() {
        return new Certificate();
    }

    /**
     * Create an instance of {@link Signer }
     * 
     */
    public Signer createSigner() {
        return new Signer();
    }

    /**
     * Create an instance of {@link Rejection }
     * 
     */
    public Rejection createRejection() {
        return new Rejection();
    }

    /**
     * Create an instance of {@link Field }
     * 
     */
    public Field createField() {
        return new Field();
    }

    /**
     * Create an instance of {@link Substitute }
     * 
     */
    public Substitute createSubstitute() {
        return new Substitute();
    }

    /**
     * Create an instance of {@link Delegates }
     * 
     */
    public Delegates createDelegates() {
        return new Delegates();
    }

    /**
     * Create an instance of {@link UploadStep }
     * 
     */
    public UploadStep createUploadStep() {
        return new UploadStep();
    }

    /**
     * Create an instance of {@link DeleteResponseDocument }
     * 
     */
    public DeleteResponseDocument createDeleteResponseDocument() {
        return new DeleteResponseDocument();
    }

    /**
     * Create an instance of {@link ListResponseDocument }
     * 
     */
    public ListResponseDocument createListResponseDocument() {
        return new ListResponseDocument();
    }

    /**
     * Create an instance of {@link SignerID }
     * 
     */
    public SignerID createSignerID() {
        return new SignerID();
    }

    /**
     * Create an instance of {@link Sender }
     * 
     */
    public Sender createSender() {
        return new Sender();
    }

    /**
     * Create an instance of {@link VerificationCode }
     * 
     */
    public VerificationCode createVerificationCode() {
        return new VerificationCode();
    }

    /**
     * Create an instance of {@link SignatureImageDimensions }
     * 
     */
    public SignatureImageDimensions createSignatureImageDimensions() {
        return new SignatureImageDimensions();
    }

    /**
     * Create an instance of {@link SignatureImage }
     * 
     */
    public SignatureImage createSignatureImage() {
        return new SignatureImage();
    }

    /**
     * Create an instance of {@link ListStep }
     * 
     */
    public ListStep createListStep() {
        return new ListStep();
    }

    /**
     * Create an instance of {@link File }
     * 
     */
    public File createFile() {
        return new File();
    }

    /**
     * Create an instance of {@link Substitutes }
     * 
     */
    public Substitutes createSubstitutes() {
        return new Substitutes();
    }

    /**
     * Create an instance of {@link Delegate }
     * 
     */
    public Delegate createDelegate() {
        return new Delegate();
    }

    /**
     * Create an instance of {@link SignersID }
     * 
     */
    public SignersID createSignersID() {
        return new SignersID();
    }

    /**
     * Create an instance of {@link PendingDocuments }
     * 
     */
    public PendingDocuments createPendingDocuments() {
        return new PendingDocuments();
    }

    /**
     * Create an instance of {@link ArchiveMetadatas }
     * 
     */
    public ArchiveMetadatas createArchiveMetadatas() {
        return new ArchiveMetadatas();
    }

    /**
     * Create an instance of {@link ExternalIDs }
     * 
     */
    public ExternalIDs createExternalIDs() {
        return new ExternalIDs();
    }

    /**
     * Create an instance of {@link Job }
     * 
     */
    public Job createJob() {
        return new Job();
    }

    /**
     * Create an instance of {@link Position }
     * 
     */
    public Position createPosition() {
        return new Position();
    }

    /**
     * Create an instance of {@link Signers }
     * 
     */
    public Signers createSigners() {
        return new Signers();
    }

    /**
     * Create an instance of {@link Positions }
     * 
     */
    public Positions createPositions() {
        return new Positions();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArchiveOptions }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "archive-options", scope = ListResponseDocument.class)
    public JAXBElement<ArchiveOptions> createListResponseDocumentArchiveOptions(ArchiveOptions value) {
        return new JAXBElement<ArchiveOptions>(_ListResponseDocumentArchiveOptions_QNAME, ArchiveOptions.class, ListResponseDocument.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServerSigners }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "server-signers", scope = ListServerSignersResponse.class)
    public JAXBElement<ServerSigners> createListServerSignersResponseServerSigners(ServerSigners value) {
        return new JAXBElement<ServerSigners>(_ListServerSignersResponseServerSigners_QNAME, ServerSigners.class, ListServerSignersResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArchiveOptions }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "archive-options", scope = VisualFile.class)
    public JAXBElement<ArchiveOptions> createVisualFileArchiveOptions(ArchiveOptions value) {
        return new JAXBElement<ArchiveOptions>(_ListResponseDocumentArchiveOptions_QNAME, ArchiveOptions.class, VisualFile.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExternalIDs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "external-ids", scope = VisualFile.class)
    public JAXBElement<ExternalIDs> createVisualFileExternalIds(ExternalIDs value) {
        return new JAXBElement<ExternalIDs>(_VisualFileExternalIds_QNAME, ExternalIDs.class, VisualFile.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "signature-custody", scope = ArchiveLocator.class)
    public JAXBElement<Boolean> createArchiveLocatorSignatureCustody(Boolean value) {
        return new JAXBElement<Boolean>(_ArchiveLocatorSignatureCustody_QNAME, Boolean.class, ArchiveLocator.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "visual-file-path-name", scope = ArchiveLocator.class)
    public JAXBElement<String> createArchiveLocatorVisualFilePathName(String value) {
        return new JAXBElement<String>(_ArchiveLocatorVisualFilePathName_QNAME, String.class, ArchiveLocator.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "file-path-name", scope = ArchiveLocator.class)
    public JAXBElement<String> createArchiveLocatorFilePathName(String value) {
        return new JAXBElement<String>(_ArchiveLocatorFilePathName_QNAME, String.class, ArchiveLocator.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "signature-files-path-pattern", scope = ArchiveLocator.class)
    public JAXBElement<String> createArchiveLocatorSignatureFilesPathPattern(String value) {
        return new JAXBElement<String>(_ArchiveLocatorSignatureFilesPathPattern_QNAME, String.class, ArchiveLocator.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "folder-name", scope = ArchiveLocator.class)
    public JAXBElement<String> createArchiveLocatorFolderName(String value) {
        return new JAXBElement<String>(_ArchiveLocatorFolderName_QNAME, String.class, ArchiveLocator.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "retention-policy", scope = ArchiveLocator.class)
    public JAXBElement<String> createArchiveLocatorRetentionPolicy(String value) {
        return new JAXBElement<String>(_ArchiveLocatorRetentionPolicy_QNAME, String.class, ArchiveLocator.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "archive-uri", scope = ArchiveLocator.class)
    public JAXBElement<String> createArchiveLocatorArchiveUri(String value) {
        return new JAXBElement<String>(_ArchiveLocatorArchiveUri_QNAME, String.class, ArchiveLocator.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "archive-version", scope = ArchiveLocator.class)
    public JAXBElement<String> createArchiveLocatorArchiveVersion(String value) {
        return new JAXBElement<String>(_ArchiveLocatorArchiveVersion_QNAME, String.class, ArchiveLocator.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "repository-base", scope = ArchiveLocator.class)
    public JAXBElement<String> createArchiveLocatorRepositoryBase(String value) {
        return new JAXBElement<String>(_ArchiveLocatorRepositoryBase_QNAME, String.class, ArchiveLocator.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchCriterias }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "search-criterias", scope = SearchRequest.class)
    public JAXBElement<SearchCriterias> createSearchRequestSearchCriterias(SearchCriterias value) {
        return new JAXBElement<SearchCriterias>(_SearchRequestSearchCriterias_QNAME, SearchCriterias.class, SearchRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignModeEnum }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "sign-mode", scope = Steps.class)
    public JAXBElement<SignModeEnum> createStepsSignMode(SignModeEnum value) {
        return new JAXBElement<SignModeEnum>(_StepsSignMode_QNAME, SignModeEnum.class, Steps.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchCriterias }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "search-criterias", scope = ListRequest.class)
    public JAXBElement<SearchCriterias> createListRequestSearchCriterias(SearchCriterias value) {
        return new JAXBElement<SearchCriterias>(_SearchRequestSearchCriterias_QNAME, SearchCriterias.class, ListRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SourceLocators }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "source-locators", scope = ArchiveOptions.class)
    public JAXBElement<SourceLocators> createArchiveOptionsSourceLocators(SourceLocators value) {
        return new JAXBElement<SourceLocators>(_ArchiveOptionsSourceLocators_QNAME, SourceLocators.class, ArchiveOptions.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArchiveMetadatas }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "archive-metadatas", scope = ArchiveOptions.class)
    public JAXBElement<ArchiveMetadatas> createArchiveOptionsArchiveMetadatas(ArchiveMetadatas value) {
        return new JAXBElement<ArchiveMetadatas>(_ArchiveOptionsArchiveMetadatas_QNAME, ArchiveMetadatas.class, ArchiveOptions.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DestinationLocators }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "destination-locators", scope = ArchiveOptions.class)
    public JAXBElement<DestinationLocators> createArchiveOptionsDestinationLocators(DestinationLocators value) {
        return new JAXBElement<DestinationLocators>(_ArchiveOptionsDestinationLocators_QNAME, DestinationLocators.class, ArchiveOptions.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VerificationCode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "verification-code", scope = ExternalIDs.class)
    public JAXBElement<VerificationCode> createExternalIDsVerificationCode(VerificationCode value) {
        return new JAXBElement<VerificationCode>(_ExternalIDsVerificationCode_QNAME, VerificationCode.class, ExternalIDs.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "logical-id", scope = ExternalIDs.class)
    public JAXBElement<String> createExternalIDsLogicalId(String value) {
        return new JAXBElement<String>(_ExternalIDsLogicalId_QNAME, String.class, ExternalIDs.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArchiveOptions }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "archive-options", scope = DownloadResponseDocument.class)
    public JAXBElement<ArchiveOptions> createDownloadResponseDocumentArchiveOptions(ArchiveOptions value) {
        return new JAXBElement<ArchiveOptions>(_ListResponseDocumentArchiveOptions_QNAME, ArchiveOptions.class, DownloadResponseDocument.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VisualFiles }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "visual-files", scope = DownloadResponseDocument.class)
    public JAXBElement<VisualFiles> createDownloadResponseDocumentVisualFiles(VisualFiles value) {
        return new JAXBElement<VisualFiles>(_DownloadResponseDocumentVisualFiles_QNAME, VisualFiles.class, DownloadResponseDocument.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignatureFiles }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "signature-files", scope = DownloadResponseDocument.class)
    public JAXBElement<SignatureFiles> createDownloadResponseDocumentSignatureFiles(SignatureFiles value) {
        return new JAXBElement<SignatureFiles>(_DownloadResponseDocumentSignatureFiles_QNAME, SignatureFiles.class, DownloadResponseDocument.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArchiveOptions }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "archive-options", scope = UploadRequestDocument.class)
    public JAXBElement<ArchiveOptions> createUploadRequestDocumentArchiveOptions(ArchiveOptions value) {
        return new JAXBElement<ArchiveOptions>(_ListResponseDocumentArchiveOptions_QNAME, ArchiveOptions.class, UploadRequestDocument.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Signers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "signers-none", scope = ListStep.class)
    public JAXBElement<Signers> createListStepSignersNone(Signers value) {
        return new JAXBElement<Signers>(_ListStepSignersNone_QNAME, Signers.class, ListStep.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Signers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "signers-reject", scope = ListStep.class)
    public JAXBElement<Signers> createListStepSignersReject(Signers value) {
        return new JAXBElement<Signers>(_ListStepSignersReject_QNAME, Signers.class, ListStep.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Signers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "signers-action", scope = ListStep.class)
    public JAXBElement<Signers> createListStepSignersAction(Signers value) {
        return new JAXBElement<Signers>(_ListStepSignersAction_QNAME, Signers.class, ListStep.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "minimal-signers", scope = ListStep.class)
    public JAXBElement<Integer> createListStepMinimalSigners(Integer value) {
        return new JAXBElement<Integer>(_ListStepMinimalSigners_QNAME, Integer.class, ListStep.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PdfPosition }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "pdf-position", scope = VerificationCode.class)
    public JAXBElement<PdfPosition> createVerificationCodePdfPosition(PdfPosition value) {
        return new JAXBElement<PdfPosition>(_VerificationCodePdfPosition_QNAME, PdfPosition.class, VerificationCode.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModeTypeEnum }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "download-type", scope = DownloadOptions.class)
    public JAXBElement<ModeTypeEnum> createDownloadOptionsDownloadType(ModeTypeEnum value) {
        return new JAXBElement<ModeTypeEnum>(_DownloadOptionsDownloadType_QNAME, ModeTypeEnum.class, DownloadOptions.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "minimal-signers", scope = UploadStep.class)
    public JAXBElement<Integer> createUploadStepMinimalSigners(Integer value) {
        return new JAXBElement<Integer>(_ListStepMinimalSigners_QNAME, Integer.class, UploadStep.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Signers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "signers-none", scope = DownloadStep.class)
    public JAXBElement<Signers> createDownloadStepSignersNone(Signers value) {
        return new JAXBElement<Signers>(_ListStepSignersNone_QNAME, Signers.class, DownloadStep.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Signers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "signers-reject", scope = DownloadStep.class)
    public JAXBElement<Signers> createDownloadStepSignersReject(Signers value) {
        return new JAXBElement<Signers>(_ListStepSignersReject_QNAME, Signers.class, DownloadStep.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Signers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "signers-action", scope = DownloadStep.class)
    public JAXBElement<Signers> createDownloadStepSignersAction(Signers value) {
        return new JAXBElement<Signers>(_ListStepSignersAction_QNAME, Signers.class, DownloadStep.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "minimal-signers", scope = DownloadStep.class)
    public JAXBElement<Integer> createDownloadStepMinimalSigners(Integer value) {
        return new JAXBElement<Integer>(_ListStepMinimalSigners_QNAME, Integer.class, DownloadStep.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArchiveOptions }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "archive-options", scope = Annex.class)
    public JAXBElement<ArchiveOptions> createAnnexArchiveOptions(ArchiveOptions value) {
        return new JAXBElement<ArchiveOptions>(_ListResponseDocumentArchiveOptions_QNAME, ArchiveOptions.class, Annex.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExternalIDs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "external-ids", scope = Annex.class)
    public JAXBElement<ExternalIDs> createAnnexExternalIds(ExternalIDs value) {
        return new JAXBElement<ExternalIDs>(_VisualFileExternalIds_QNAME, ExternalIDs.class, Annex.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "type-sign", scope = Annex.class)
    public JAXBElement<Integer> createAnnexTypeSign(Integer value) {
        return new JAXBElement<Integer>(_AnnexTypeSign_QNAME, Integer.class, Annex.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "is-file-sign", scope = Annex.class)
    public JAXBElement<Boolean> createAnnexIsFileSign(Boolean value) {
        return new JAXBElement<Boolean>(_AnnexIsFileSign_QNAME, Boolean.class, Annex.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "sign-annex", scope = Annex.class)
    public JAXBElement<Boolean> createAnnexSignAnnex(Boolean value) {
        return new JAXBElement<Boolean>(_AnnexSignAnnex_QNAME, Boolean.class, Annex.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "archive-info", scope = DownloadRequest.class)
    public JAXBElement<Boolean> createDownloadRequestArchiveInfo(Boolean value) {
        return new JAXBElement<Boolean>(_DownloadRequestArchiveInfo_QNAME, Boolean.class, DownloadRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "additional-info", scope = DownloadRequest.class)
    public JAXBElement<Boolean> createDownloadRequestAdditionalInfo(Boolean value) {
        return new JAXBElement<Boolean>(_DownloadRequestAdditionalInfo_QNAME, Boolean.class, DownloadRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "download-documents", scope = DownloadRequest.class)
    public JAXBElement<Boolean> createDownloadRequestDownloadDocuments(Boolean value) {
        return new JAXBElement<Boolean>(_DownloadRequestDownloadDocuments_QNAME, Boolean.class, DownloadRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "check-cert", scope = Signer.class)
    public JAXBElement<Boolean> createSignerCheckCert(Boolean value) {
        return new JAXBElement<Boolean>(_SignerCheckCert_QNAME, Boolean.class, Signer.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignerSignatureFiles }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "signature-files", scope = Signer.class)
    public JAXBElement<SignerSignatureFiles> createSignerSignatureFiles(SignerSignatureFiles value) {
        return new JAXBElement<SignerSignatureFiles>(_DownloadResponseDocumentSignatureFiles_QNAME, SignerSignatureFiles.class, Signer.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PdfAppearance }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "pdf-appearance", scope = Signer.class)
    public JAXBElement<PdfAppearance> createSignerPdfAppearance(PdfAppearance value) {
        return new JAXBElement<PdfAppearance>(_SignerPdfAppearance_QNAME, PdfAppearance.class, Signer.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "id-update", scope = Signer.class)
    public JAXBElement<String> createSignerIdUpdate(String value) {
        return new JAXBElement<String>(_SignerIdUpdate_QNAME, String.class, Signer.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArchiveOptions }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "archive-options", scope = UpdateRequestDocument.class)
    public JAXBElement<ArchiveOptions> createUpdateRequestDocumentArchiveOptions(ArchiveOptions value) {
        return new JAXBElement<ArchiveOptions>(_ListResponseDocumentArchiveOptions_QNAME, ArchiveOptions.class, UpdateRequestDocument.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignatureImage }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "signature-image", scope = PdfAppearance.class)
    public JAXBElement<SignatureImage> createPdfAppearanceSignatureImage(SignatureImage value) {
        return new JAXBElement<SignatureImage>(_PdfAppearanceSignatureImage_QNAME, SignatureImage.class, PdfAppearance.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArchiveOptions }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "archive-options", scope = SignatureFile.class)
    public JAXBElement<ArchiveOptions> createSignatureFileArchiveOptions(ArchiveOptions value) {
        return new JAXBElement<ArchiveOptions>(_ListResponseDocumentArchiveOptions_QNAME, ArchiveOptions.class, SignatureFile.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExternalIDs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "external-ids", scope = SignatureFile.class)
    public JAXBElement<ExternalIDs> createSignatureFileExternalIds(ExternalIDs value) {
        return new JAXBElement<ExternalIDs>(_VisualFileExternalIds_QNAME, ExternalIDs.class, SignatureFile.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "mime-type", scope = SignatureImage.class)
    public JAXBElement<String> createSignatureImageMimeType(String value) {
        return new JAXBElement<String>(_SignatureImageMimeType_QNAME, String.class, SignatureImage.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SearchCriterias }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "search-criterias", scope = DeleteRequest.class)
    public JAXBElement<SearchCriterias> createDeleteRequestSearchCriterias(SearchCriterias value) {
        return new JAXBElement<SearchCriterias>(_SearchRequestSearchCriterias_QNAME, SearchCriterias.class, DeleteRequest.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignatureImageDimensions }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "signature-image-dimensions", scope = Position.class)
    public JAXBElement<SignatureImageDimensions> createPositionSignatureImageDimensions(SignatureImageDimensions value) {
        return new JAXBElement<SignatureImageDimensions>(_PositionSignatureImageDimensions_QNAME, SignatureImageDimensions.class, Position.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PdfPosition }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "pdf-position", scope = Position.class)
    public JAXBElement<PdfPosition> createPositionPdfPosition(PdfPosition value) {
        return new JAXBElement<PdfPosition>(_VerificationCodePdfPosition_QNAME, PdfPosition.class, Position.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "number-signatures", scope = File.class)
    public JAXBElement<String> createFileNumberSignatures(String value) {
        return new JAXBElement<String>(_FileNumberSignatures_QNAME, String.class, File.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "base64-data", scope = File.class)
    public JAXBElement<String> createFileBase64Data(String value) {
        return new JAXBElement<String>(_FileBase64Data_QNAME, String.class, File.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignersID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "signers-id", scope = File.class)
    public JAXBElement<SignersID> createFileSignersId(SignersID value) {
        return new JAXBElement<SignersID>(_FileSignersId_QNAME, SignersID.class, File.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "mime-type", scope = File.class)
    public JAXBElement<String> createFileMimeType(String value) {
        return new JAXBElement<String>(_SignatureImageMimeType_QNAME, String.class, File.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExternalIDs }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "external-ids", scope = DocumentAttributes.class)
    public JAXBElement<ExternalIDs> createDocumentAttributesExternalIds(ExternalIDs value) {
        return new JAXBElement<ExternalIDs>(_VisualFileExternalIds_QNAME, ExternalIDs.class, DocumentAttributes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "date-notice", scope = DocumentAttributes.class)
    public JAXBElement<XMLGregorianCalendar> createDocumentAttributesDateNotice(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentAttributesDateNotice_QNAME, XMLGregorianCalendar.class, DocumentAttributes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "date-entry", scope = DocumentAttributes.class)
    public JAXBElement<XMLGregorianCalendar> createDocumentAttributesDateEntry(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentAttributesDateEntry_QNAME, XMLGregorianCalendar.class, DocumentAttributes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "number-annexes", scope = DocumentAttributes.class)
    public JAXBElement<Integer> createDocumentAttributesNumberAnnexes(Integer value) {
        return new JAXBElement<Integer>(_DocumentAttributesNumberAnnexes_QNAME, Integer.class, DocumentAttributes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "type-sign", scope = DocumentAttributes.class)
    public JAXBElement<Integer> createDocumentAttributesTypeSign(Integer value) {
        return new JAXBElement<Integer>(_AnnexTypeSign_QNAME, Integer.class, DocumentAttributes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "is-file-sign", scope = DocumentAttributes.class)
    public JAXBElement<Boolean> createDocumentAttributesIsFileSign(Boolean value) {
        return new JAXBElement<Boolean>(_AnnexIsFileSign_QNAME, Boolean.class, DocumentAttributes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "external-data", scope = DocumentAttributes.class)
    public JAXBElement<String> createDocumentAttributesExternalData(String value) {
        return new JAXBElement<String>(_DocumentAttributesExternalData_QNAME, String.class, DocumentAttributes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "date-limit", scope = DocumentAttributes.class)
    public JAXBElement<XMLGregorianCalendar> createDocumentAttributesDateLimit(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentAttributesDateLimit_QNAME, XMLGregorianCalendar.class, DocumentAttributes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "sign-annexes", scope = DocumentAttributes.class)
    public JAXBElement<Boolean> createDocumentAttributesSignAnnexes(Boolean value) {
        return new JAXBElement<Boolean>(_DocumentAttributesSignAnnexes_QNAME, Boolean.class, DocumentAttributes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "generate-visuals", scope = DocumentAttributes.class)
    public JAXBElement<Boolean> createDocumentAttributesGenerateVisuals(Boolean value) {
        return new JAXBElement<Boolean>(_DocumentAttributesGenerateVisuals_QNAME, Boolean.class, DocumentAttributes.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "date-last-update", scope = DocumentAttributes.class)
    public JAXBElement<XMLGregorianCalendar> createDocumentAttributesDateLastUpdate(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DocumentAttributesDateLastUpdate_QNAME, XMLGregorianCalendar.class, DocumentAttributes.class, value);
    }

}
