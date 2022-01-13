package uk.gov.dwp.components.drs.creator.validators;

import uk.gov.dwp.components.drs.creator.exceptions.MetadataCreatorPermanentException;
import uk.gov.govtalk.drs.common.metadata.DRSMetaDataDefnUD;

/**
 * Interface implemented by specific DRS Metadata Validator classes.
 */
public interface DrsMetadataValidator {

    /**
     * Apply a validation rule to the metadata.
     * <p>
     * Note that in our current model, validators are allowed to modify data to
     * correct predictable issues in submitted data rather than throw an error back. Callers
     * need to be aware that metadata is cosnidered mutable by implementations of this interface
     *
     * @param metadata the metadata to validate
     * @throws MetadataCreatorPermanentException if validation fails and an error must be
     *                                           reported back to the caller (or logged for asynchronous cases)
     */
    void apply(DRSMetaDataDefnUD metadata) throws MetadataCreatorPermanentException;
}
