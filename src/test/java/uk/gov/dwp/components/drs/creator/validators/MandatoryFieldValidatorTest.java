package uk.gov.dwp.components.drs.creator.validators;

import uk.gov.dwp.components.drs.creator.exceptions.MetadataCreatorPermanentException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.dwp.components.drs.creator.validators.MandatoryFieldValidator;
import uk.gov.govtalk.drs.common.metadata.DRSMetaDataDefnUD;
import uk.gov.govtalk.drs.common.metadata.NINO;

/**
 * Checks the behaviour of the mandatory property validator
 */
public class MandatoryFieldValidatorTest {

    /**
     * Class under test
     */
    private MandatoryFieldValidator cut = new MandatoryFieldValidator();

    /**
     * Expected exception for failure tests
     */
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * A fully populated metadata object passes validation
     */
    @Test
    public void testNoMissingValues() throws Exception {
        // Given: a metadata object
        DRSMetaDataDefnUD metadata = getFullyPopulatedMetadata();
        // When: we validate the metadata
        cut.apply(metadata);
        // Then: No exception is thrown
    }

    /**
     * Missing business unit identifier throws an appropriate exception
     */
    @Test
    public void testMissingBusinessUnitID() throws Exception {
        // Given: a metadata object
        DRSMetaDataDefnUD metadata = getFullyPopulatedMetadata();
        // And: the businessUnitID has not been specified
        metadata.setBusinessUnitID(null);
        expectedException.expect(MetadataCreatorPermanentException.class);
        expectedException.expectMessage("businessUnitID not provided in request");
        // When: we validate the metadata
        cut.apply(metadata);
        // Then: the expected exception is thrown
    }

    /**
     * Missing classification throws an appropriate exception
     */
    @Test
    public void testMissingClassification() throws Exception {
        // Given: a metadata object
        DRSMetaDataDefnUD metadata = getFullyPopulatedMetadata();
        // And: the classification has not been specified
        metadata.setClassification(null);
        expectedException.expect(MetadataCreatorPermanentException.class);
        expectedException.expectMessage("classification not provided in request");
        // When: we validate the metadata
        cut.apply(metadata);
        // Then: the expected exception is thrown
    }

    /**
     * Missing document type throws an appropriate exception
     */
    @Test
    public void testMissingDocumentType() throws Exception {
        // Given: a metadata object
        DRSMetaDataDefnUD metadata = getFullyPopulatedMetadata();
        // And: the document type has not been specified
        metadata.setDocumentType(null);
        expectedException.expect(MetadataCreatorPermanentException.class);
        expectedException.expectMessage("documentType not provided in request");
        // When: we validate the metadata
        cut.apply(metadata);
        // Then: the expected exception is thrown
    }

    /**
     * Missing document source throws an appropriate exception
     */
    @Test
    public void testMissingDocumentSource() throws Exception {
        // Given: a metadata object
        DRSMetaDataDefnUD metadata = getFullyPopulatedMetadata();
        // And: the document source has not been specified
        metadata.setDocumentSource(null);
        expectedException.expect(MetadataCreatorPermanentException.class);
        expectedException.expectMessage("documentSource not provided in request");
        // When: we validate the metadata
        cut.apply(metadata);
        // Then: the expected exception is thrown
    }

    /**
     * Create a fully populated metadata object to test.
     * Not a content test so we just assign random values here.
     */
    private DRSMetaDataDefnUD getFullyPopulatedMetadata() {
        DRSMetaDataDefnUD metadata = new DRSMetaDataDefnUD();

        metadata.setBenefitType(1);
        metadata.setBusinessUnitID(2);
        metadata.setClaimRef("3");
        metadata.setClassification(4);
        metadata.setCustomerReferenceNumber("5");
        metadata.setDateOfBirth("6");
        metadata.setDocumentSource(7);
        metadata.setDocumentType(8);
        metadata.setForename("9");
        metadata.setHarmfulIndicatorFlag(10);
        metadata.setIssueDate("11");
        metadata.setLinkData("12");
        metadata.setLOBCaseID("13");
        metadata.setOfficePostcode("14");
        metadata.setPostCode("15");
        metadata.setSurname("16");

        NINO nino = new NINO();
        nino.setNINoBody("17");
        nino.setNINoSuffix("18");
        metadata.setNINO(nino);

        return metadata;
    }
}
