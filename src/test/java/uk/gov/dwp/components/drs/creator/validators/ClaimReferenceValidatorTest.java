package uk.gov.dwp.components.drs.creator.validators;

import uk.gov.dwp.components.drs.creator.exceptions.MetadataCreatorPermanentException;
import org.junit.Test;
import uk.gov.dwp.components.drs.creator.validators.ClaimReferenceValidator;
import uk.gov.govtalk.drs.common.metadata.DRSMetaDataDefnUD;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Validate a range of potential input for the ClaimRef field and ensure they
 * are handled appropriately and predictably.
 */
public class ClaimReferenceValidatorTest {

    /**
     * Class under test
     */
    private static final ClaimReferenceValidator cut = new ClaimReferenceValidator();

    /**
     * No claim ref specified does not throw an error
     */
    @Test
    public void testValidateNoClaimRef() throws Exception {
        // Given: DRS metadata with no Claim Reference
        DRSMetaDataDefnUD metadata = new DRSMetaDataDefnUD();
        // When: We validate it
        cut.apply(metadata);
        // Then: No exception is thrown
    }

    /**
     * A claim reference specified which is under 30 characters is allowed through
     */
    @Test
    public void testValidateClaimRefUnderThirtyCharacters() throws Exception {
        // Given: DRS metadata with a 29 character claim reference
        DRSMetaDataDefnUD metadata = new DRSMetaDataDefnUD();
        metadata.setClaimRef("xxxxxxxxx-xxxxxxxxx-xxxxxxxxx");
        // When: We validate it
        cut.apply(metadata);
        // Then: No exception is thrown
    }

    /**
     * A claim refernece specified which is exactly 30 characters is allowed through
     */
    @Test
    public void testValidateClaimRefIsThirtyCharacters() throws Exception {
        // Given: DRS metadata with a 30 character claim reference
        DRSMetaDataDefnUD metadata = new DRSMetaDataDefnUD();
        metadata.setClaimRef("xxxxxxxxx-xxxxxxxxx-xxxxxxxxx-");
        // When: We validate it
        cut.apply(metadata);
        // Then: No exception is thrown
    }

    /**
     * An overlength ClaimRef that is not a candidate UUID should cause an exception to be thrown
     */
    @Test(expected = MetadataCreatorPermanentException.class)
    public void testValidateClaimRefOverThirtyCharactersButNotUUID() throws Exception {
        // Given: DRS metadata with a 31 character claim reference which is not a candidate UUID
        DRSMetaDataDefnUD metadata = new DRSMetaDataDefnUD();
        metadata.setClaimRef("xxxxxxxxx-xxxxxxxxx-xxxxxxxxx-x");
        // When: We validate it
        cut.apply(metadata);
        // Then: An exception is thrown
        fail("No exception ");
    }

    /**
     * A UUID candidate claim reference, overlength by 1 character replaces 2 centre characters with two hyphens
     */
    @Test
    public void testValidateClaimRefThirtyOneCharacterPossibleUUID() throws Exception {
        // Given: DRS metadata with a 31 character hex claim reference
        DRSMetaDataDefnUD metadata = new DRSMetaDataDefnUD();
        metadata.setClaimRef("aaaaaaaaaa-bbbbbbbbbb-ccccccccc");
        // When: We validate it
        cut.apply(metadata);
        // Then: The claim refernece is abbreviated to fit
        assertEquals("aaaaaaaaaa-bb--bbbb-ccccccccc", metadata.getClaimRef());
        assertTrue(metadata.getClaimRef().length() <= 30);
    }

    /**
     * A UUID candidate claim reference, overlength by 2 characters, replaces 2 centre characters with two hyphens
     */
    @Test
    public void testValidateClaimRefThirtyTwoCharacterPossibleUUID() throws Exception {
        // Given: DRS metadata with a 32 character hex claim reference
        DRSMetaDataDefnUD metadata = new DRSMetaDataDefnUD();
        metadata.setClaimRef("aaaaaaaaaa-bbbbbbbbbb-cccccccccc");
        // When: We validate it
        cut.apply(metadata);
        // Then: The claim refernece is abbreviated to fit
        assertEquals("aaaaaaaaaa-bbb--bbb-cccccccccc", metadata.getClaimRef());
        assertTrue(metadata.getClaimRef().length() <= 30);
    }

    /**
     * When an actual UUID is supplied as a Claim Reference, an expected abbreviated form is applied to the metadata
     */
    @Test
    public void testValidateClaimRefActualUUID() throws Exception {
        // Given: DRS metadata with a real UUID as a claim reference
        DRSMetaDataDefnUD metadata = new DRSMetaDataDefnUD();
        metadata.setClaimRef("b4db0193-6fae-4a92-9196-6783ea8d19c8");
        // When: We validate it
        cut.apply(metadata);
        // Then: The UUID is abbreviated at its centre point
        assertEquals("b4db0193-6fae---6-6783ea8d19c8", metadata.getClaimRef());
        assertTrue(metadata.getClaimRef().length() <= 30);
    }
}
