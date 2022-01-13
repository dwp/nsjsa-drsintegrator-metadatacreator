package uk.gov.dwp.components.drs.creator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.components.drs.creator.definitions.Constants;
import uk.gov.dwp.components.drs.creator.domain.MetadataConfiguration;
import uk.gov.dwp.components.drs.creator.exceptions.MetadataCreatorPermanentException;
import uk.gov.dwp.components.drs.creator.validators.ClaimReferenceValidator;
import uk.gov.dwp.components.drs.creator.validators.DrsMetadataValidator;
import uk.gov.dwp.components.drs.creator.validators.MandatoryFieldValidator;
import uk.gov.govtalk.drs.common.metadata.DRSMetaDataDefnUD;
import uk.gov.govtalk.drs.common.request.DRSIDAuditDefn;
import uk.gov.govtalk.drs.common.request.DRSRequestHeader;
import uk.gov.govtalk.drs.documentupload.documentupload_request.Request;

import java.util.Arrays;
import java.util.List;

/**
 * Builds the DRS request model objects based on the supplied JSON metadata and the retrieved
 * PDF.
 */
public class RequestBuilder {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestBuilder.class);

    /**
     * Microservice configuration details - required for boilerplate request properties
     */
    private MetadataConfiguration configuration = null;

    /**
     * List of configured validators to apply
     */
    private List<DrsMetadataValidator> metadataValidators =
            Arrays.<DrsMetadataValidator>asList(
                    new ClaimReferenceValidator(),
                    new MandatoryFieldValidator());

    /**
     * Fully populating constructor
     *
     * @param configuration service configuration
     */
    public RequestBuilder(MetadataConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Build a DRS request from the supplied details
     *
     * @param pdfBytes    PDF data as a byte array
     * @param drsMetadata parsed and validated DRS metadata
     * @return assembled request
     * @throws MetadataCreatorPermanentException if the request cannot be assembled because the
     *                                           provided data is invalid
     */
    public Request build(byte[] pdfBytes, DRSMetaDataDefnUD drsMetadata) {
        Request request = new Request();

        // Populate the request header
        request.setHeader(createDrsRequestHeader());

        // Populate the request body
        Request.Body body = new Request.Body();
        body.setMimeType(MediaType.PDF.toString());
        body.setAttachmentType(Constants.DRS_ATTACHMENT_TYPE);
        body.setStore(Constants.DRS_STORE);
        body.setMetadata(drsMetadata);
        body.setDocument(pdfBytes);
        request.setBody(body);

        return request;
    }

    /**
     * Create a DRS request header based on our service configuration
     *
     * @return populated DRS request header
     */
    private DRSRequestHeader createDrsRequestHeader() {
        DRSRequestHeader drsRequestHeader = new DRSRequestHeader();

        DRSIDAuditDefn auditHeader = new DRSIDAuditDefn();
        drsRequestHeader.setAuditHeader(auditHeader);

        drsRequestHeader.setUserName(configuration.getDrsRequestUserName());
        drsRequestHeader.setVersion(Constants.DRS_REQUEST_VERSION);

        return drsRequestHeader;
    }

    /**
     * Convert the JSON metadata element into a model object and validate it is sufficient
     *
     * @param jsonMetadata metadata supplied in JSON form in the request
     * @return model object represented by the request data
     * @throws MetadataCreatorPermanentException if the JSON cannot be parsed into the model object or basic
     *                                           validation checks fail.
     */
    public DRSMetaDataDefnUD buildAndValidateMetadata(String jsonMetadata) throws MetadataCreatorPermanentException {
        ObjectMapper jsonMapper = new ObjectMapper();
        DRSMetaDataDefnUD metadata = null;
        try {
            metadata = jsonMapper.readValue(jsonMetadata, DRSMetaDataDefnUD.class);
        } catch (Exception e) {
            LOGGER.error("Metadata from request could not be marshalled to model object : {}", e.getMessage());
            LOGGER.debug("Metadata from request [{}] could not be marshalled to model object", jsonMetadata, e);
            throw new MetadataCreatorPermanentException("Unable to extract DRS Metadata definition from JSON", e);
        }
        validateMetadata(metadata);
        return metadata;
    }

    /**
     * Performs checks on the DRS metadata extracted from the JSON request to ensure it is sufficient
     * by applying all configured validators
     *
     * @param metadata metadata model object generated from the JSON request
     * @throws MetadataCreatorPermanentException if validation fails
     */
    private void validateMetadata(DRSMetaDataDefnUD metadata) throws MetadataCreatorPermanentException {
        for (DrsMetadataValidator metadataValidator : metadataValidators) {
            metadataValidator.apply(metadata);
        }
    }

    /**
     * Retrieve the list of configured metadata validators
     *
     * @return current metadata validators
     */
    public List<DrsMetadataValidator> getMetadataValidators() {
        return metadataValidators;
    }

    /**
     * Assign a set of metadata validators
     *
     * @param metadataValidators validators to use
     */
    public void setMetadataValidators(List<DrsMetadataValidator> metadataValidators) {
        this.metadataValidators = metadataValidators;
    }
}
