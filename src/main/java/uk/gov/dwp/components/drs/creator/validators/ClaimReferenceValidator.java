package uk.gov.dwp.components.drs.creator.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.components.drs.creator.exceptions.MetadataCreatorPermanentException;
import uk.gov.govtalk.drs.common.metadata.DRSMetaDataDefnUD;

import java.util.regex.Pattern;

/**
 * Validates a claim reference if present in the request.
 * <p>
 * Claim references are limited to 30 characters, however we allow for the case
 * where a UUID is supplied and we will predictably truncate it to fit.
 * <p>
 * The objective here is not to replicate validation that will be performed by the
 * schema validation later; hence we don't check the content of a <30 character value
 * for legality.
 */
public class ClaimReferenceValidator implements DrsMetadataValidator {

    /**
     * For logging
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ClaimReferenceValidator.class);

    /**
     * Maximum length of a claim reference
     */
    private static final int MAX_CLAIM_REF_LENGTH = 30;

    /**
     * Abbreviation string
     */
    private static final String ABBREVIATOR = "--";

    /**
     * UUID matcher; We apply fairly loose rules here so as not to presume a standardised form from the caller
     */
    private static final Pattern UUID_PATTERN = Pattern.compile("^([0-9a-fA-F\\-])*$");

    /**
     * If a claim reference is supplied and exceeds 30 characters, if it is potentially
     * a UUID shorten it to fit.
     */
    @Override
    public void apply(DRSMetaDataDefnUD metadata) throws MetadataCreatorPermanentException {
        if (metadata.getClaimRef() != null && metadata.getClaimRef().length() > MAX_CLAIM_REF_LENGTH) {
            if (UUID_PATTERN.matcher(metadata.getClaimRef()).matches()) {

                int trimLength = metadata.getClaimRef().length() % 2 == 0 ?
                        (metadata.getClaimRef().length() - MAX_CLAIM_REF_LENGTH) + ABBREVIATOR.length() :
                        (metadata.getClaimRef().length() - MAX_CLAIM_REF_LENGTH) + ABBREVIATOR.length() + 1;
                int trimStart = (metadata.getClaimRef().length() / 2) - (trimLength / 2);

                String trimmedClaimRef = new StringBuilder(metadata.getClaimRef()).replace(trimStart, trimStart + trimLength, ABBREVIATOR).toString();
                LOGGER.info("Replacing submitted claim reference {} with {}", metadata.getClaimRef(), trimmedClaimRef);

                metadata.setClaimRef(trimmedClaimRef);

            } else {
                throw new MetadataCreatorPermanentException("ClaimReference (if specified) may not exceed " + MAX_CLAIM_REF_LENGTH + " characters.");
            }
        }
    }
}
