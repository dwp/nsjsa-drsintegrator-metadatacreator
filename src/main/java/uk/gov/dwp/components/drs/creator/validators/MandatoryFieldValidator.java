package uk.gov.dwp.components.drs.creator.validators;

import uk.gov.dwp.components.drs.creator.exceptions.MetadataCreatorPermanentException;
import uk.gov.govtalk.drs.common.metadata.DRSMetaDataDefnUD;

/**
 * Validate mandatory properties are present in the metadata
 */
public class MandatoryFieldValidator implements DrsMetadataValidator {

    /**
     * Checks mandatory properties have been supplied
     */
    @Override
    public void apply(DRSMetaDataDefnUD metadata) throws MetadataCreatorPermanentException {
        if (metadata.getBusinessUnitID() == null) {
            throw new MetadataCreatorPermanentException("businessUnitID not provided in request");
        }
        if (metadata.getClassification() == null) {
            throw new MetadataCreatorPermanentException("classification not provided in request");
        }
        if (metadata.getDocumentType() == null) {
            throw new MetadataCreatorPermanentException("documentType not provided in request");
        }
        if (metadata.getDocumentSource() == null) {
            throw new MetadataCreatorPermanentException("documentSource not provided in request");
        }
    }
}
