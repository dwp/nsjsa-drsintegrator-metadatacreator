package uk.gov.dwp.components.drs.creator;

import uk.gov.dwp.components.drs.creator.RequestBuilder;
import uk.gov.dwp.components.drs.creator.domain.MetadataConfiguration;
import uk.gov.dwp.components.drs.creator.exceptions.MetadataCreatorPermanentException;
import uk.gov.dwp.components.drs.creator.validators.DrsMetadataValidator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.govtalk.drs.common.metadata.DRSMetaDataDefnUD;
import uk.gov.govtalk.drs.documentupload.documentupload_request.Request;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RequestBuilderTest {

    /**
     * Sample valid JSON request
     */
    private static final String JSON_REQUEST = "{\n" +
            "\t\"businessUnitID\" : 20 ,\n" +
            "\t\"classification\" : 1,\n" +
            "\t\"documentType\" : 1242,\n" +
            "\t\"nino\" : {\n" +
            "\t\t\"ninoBody\" : \"AB123456\",\n" +
            "\t\t\"ninoSuffix\" : \"X\"\n" +
            "\t},\n" +
            "\t\"dateOfBirth\" : 20160101,\n" +
            "\t\"surname\" : \"Mawson\",\n" +
            "\t\"forename\" : \"Phil\",\n" +
            "\t\"postCode\" : \"LS11 8LP\",\n" +
            "\t\"lobcaseID\" : \"lob\",\n" +
            "\t\"officePostcode\" : \"WF16RJ\",\t\n" +
            "\t\"linkData\" : \"\",\n" +
            "\t\"customerReferenceNumber\" : \"\",\n" +
            "\t\"harmfulIndicatorFlag\" : 0,\n" +
            "\t\"benefitType\" : 7,\n" +
            "\t\"claimRef\" : \"123456\",\n" +
            "\t\"documentSource\" : 4,\n" +
            "\t\"issueDate\" : 20161002\n" +
            "}\n";

    /**
     * Class under test
     */
    private RequestBuilder builder = new RequestBuilder(new MetadataConfiguration(null, null, "1234"));

    /**
     * Expected exception for failure tests
     */
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void confirmBuilderBuildsRequestWithBase64EncodedDocument() throws Exception {
        byte[] pdfInByteForm = "A".getBytes();
        Request build = builder.build(pdfInByteForm, builder.buildAndValidateMetadata(JSON_REQUEST));
        assertThat(build, is(notNullValue()));
        assertThat(build.getBody().getDocument(), is(pdfInByteForm));
    }

    @Test
    public void confirmBuilderBuildsRequestWithAllFieldsSetCorrectly() throws Exception {
        int businessUnitId = 20;
        int classification = 1;
        int documentType = 1242;
        String ninoBody = "AB123456";
        String ninoSuffix = "X";
        String dob = "20160101";
        String surname = "Mawson";
        String forename = "Phil";
        String postcode = "LS11 8LP";
        String linkData = "linkdata";
        String custRef = "999888";
        int harmfulIndicator = 0;
        int benefitType = 7;
        String claimeRef = "123456";
        int documentSource = 4;
        String issueDate = "20161002";
        String lobCaseId = "lob";
        String officePostCode = "WF16RJ";
        String mobile = "07654321098";
        String sampleJson = "{\n" +
                "\t\"businessUnitID\" : " + businessUnitId + ",\n" +
                "\t\"classification\" : " + classification + ",\n" +
                "\t\"documentType\" : " + documentType + ",\n" +
                "\t\"nino\" : {\n" +
                "\t\t\"ninoBody\" : \"" + ninoBody + "\",\n" +
                "\t\t\"ninoSuffix\" : \"" + ninoSuffix + "\"\n" +
                "\t},\n" +
                "\t\"dateOfBirth\" : " + dob + ",\n" +
                "\t\"surname\" : \"" + surname + "\",\n" +
                "\t\"forename\" : \"" + forename + "\",\n" +
                "\t\"postCode\" : \"" + postcode + "\",\n" +
                "\t\"lobcaseID\" : \"" + lobCaseId + "\",\n" +
                "\t\"officePostcode\" : \"" + officePostCode + "\",\t\n" +
                "\t\"linkData\" : \"" + linkData + "\",\n" +
                "\t\"customerReferenceNumber\" : \"" + custRef + "\",\n" +
                "\t\"harmfulIndicatorFlag\" : " + harmfulIndicator + ",\n" +
                "\t\"benefitType\" : " + benefitType + ",\n" +
                "\t\"claimRef\" : \"" + claimeRef + "\",\n" +
                "\t\"documentSource\" : " + documentSource + ",\n" +
                "\t\"issueDate\" : " + issueDate + ",\n" +
                "\t\"customerMobileNumber\" : \"" + mobile + "\"\n" +
                "}\n";

        byte[] pdfInByteForm = "A".getBytes();

        Request build = builder.build(pdfInByteForm, builder.buildAndValidateMetadata(sampleJson));
        assertThat(build, is(notNullValue()));
        assertThat(build.getBody().getMetadata().getBusinessUnitID(), is(businessUnitId));
        assertThat(build.getBody().getMetadata().getClassification(), is(classification));
        assertThat(build.getBody().getMetadata().getDocumentType(), is(documentType));
        assertThat(build.getBody().getMetadata().getNINO().getNINoBody(), is(ninoBody));
        assertThat(build.getBody().getMetadata().getNINO().getNINoSuffix(), is(ninoSuffix));
        assertThat(build.getBody().getMetadata().getDateOfBirth(), is(dob));
        assertThat(build.getBody().getMetadata().getSurname(), is(surname));
        assertThat(build.getBody().getMetadata().getForename(), is(forename));
        assertThat(build.getBody().getMetadata().getPostCode(), is(postcode));
        assertThat(build.getBody().getMetadata().getLOBCaseID(), is(lobCaseId));
        assertThat(build.getBody().getMetadata().getOfficePostcode(), is(officePostCode));
        assertThat(build.getBody().getMetadata().getLinkData(), is(linkData));
        assertThat(build.getBody().getMetadata().getCustomerReferenceNumber(), is(custRef));
        assertThat(build.getBody().getMetadata().getHarmfulIndicatorFlag(), is(harmfulIndicator));
        assertThat(build.getBody().getMetadata().getBenefitType(), is(benefitType));
        assertThat(build.getBody().getMetadata().getClaimRef(), is(claimeRef));
        assertThat(build.getBody().getMetadata().getDocumentSource(), is(documentSource));
        assertThat(build.getBody().getMetadata().getIssueDate(), is(issueDate));
        assertThat(build.getBody().getMetadata().getCustomerMobileNumber(), is(mobile));
    }

    @Test
    public void confirmInvalidJsonIsRejected() {
        try {
            String sampleJson = "{\"apple\" : 1}";
            builder.buildAndValidateMetadata(sampleJson);
            fail("exception should have been thrown for invalid JSON");
        } catch (MetadataCreatorPermanentException e) {
            assertThat(e.getMessage(), is("Unable to extract DRS Metadata definition from JSON"));
        }
    }

    /**
     * The validator chain is called when building meta data and no exception thrown when validation passes
     */
    @Test
    public void testValidatorsCalledWithNoErrors() throws Exception {
        // Given: A validator is present which will accept the request data
        DrsMetadataValidator validator = mock(DrsMetadataValidator.class);
        builder.setMetadataValidators(Arrays.asList(validator));

        // When: We parse and validate the metadata
        builder.buildAndValidateMetadata(JSON_REQUEST);

        // Then: The validator is invoked
        verify(validator).apply(any(DRSMetaDataDefnUD.class));
        // And: no exception is thrown
    }

    /**
     * The validator chain is called when building meta data and an exception propagated
     */
    @Test
    public void testValidatorsCalledWithErrors() throws Exception {
        // Given: A validator is present which will not accept the request data
        DrsMetadataValidator validator = mock(DrsMetadataValidator.class);
        doThrow(new MetadataCreatorPermanentException("invalid data")).when(validator).apply(any(DRSMetaDataDefnUD.class));
        builder.setMetadataValidators(Arrays.asList(validator));

        expectedException.expect(MetadataCreatorPermanentException.class);
        expectedException.expectMessage("invalid data");

        // When: We parse and validate the metadata
        builder.buildAndValidateMetadata(JSON_REQUEST);

        // Then: An exception is thrown
        fail("No exception propagated when validator rejects the submitted data");
    }
}
