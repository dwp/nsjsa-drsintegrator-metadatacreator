package uk.gov.dwp.components.drs.creator;

import uk.gov.dwp.components.drs.creator.SoapMessageBuilder;
import uk.gov.dwp.components.drs.creator.exceptions.MetadataCreatorPermanentException;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.unitils.reflectionassert.ReflectionAssert;
import uk.gov.govtalk.drs.common.metadata.DRSMetaDataDefnUD;
import uk.gov.govtalk.drs.common.metadata.NINO;
import uk.gov.govtalk.drs.common.request.DRSIDAuditDefn;
import uk.gov.govtalk.drs.common.request.DRSRequestHeader;
import uk.gov.govtalk.drs.documentupload.documentupload_request.Request;
import uk.gov.govtalk.drs.documentupload.documentupload_request.Request.Body;

import javax.xml.bind.JAXBContext;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;

/**
 * Validates the SOAP message builder
 */
public class SoapMessageBuilderTest {

    /**
     * Class under test
     */
    private SoapMessageBuilder cut = new SoapMessageBuilder();

    /**
     * Expected exception details for failure tests
     */
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * A successfully generated SOAP request can be parsed and the same underlying object details extracted
     */
    @Test
    public void testValidRequestParsesAndUnmarshalsCorrectly() throws Exception {
        // Given: A populated DRS Request
        Request request = new Request();
        populateHeader(request);
        populateBody(request, true);

        // When: We generate a SOAP request to submit to DRS
        String soapRequest = cut.buildSoapMessage(request);

        // Then: A valid SOAP request is generated
        SOAPMessage parsedSoapRequest = MessageFactory.newInstance().createMessage(null,
                IOUtils.toInputStream(soapRequest));
        // and: the payload is a valid request object
        Request parsedRequest =
                (Request) JAXBContext.newInstance(Request.class).createUnmarshaller().unmarshal(
                        parsedSoapRequest.getSOAPBody().getFirstChild());
        // and: its content matches the original request
        ReflectionAssert.assertReflectionEquals(request, parsedRequest);
    }

    /**
     * A request which does not match the schema rules yields an exception with appropriate details
     */
    @Test
    public void testInvalidRequestThrowsMeaningfulError() throws Exception {
        // Given: A populated DRS Request with non-schema-compliant properties
        Request request = new Request();
        populateHeader(request);
        populateBody(request, false);

        // When: We generate a SOAP request to submit to DRS an exception is thrown with the correct details
        expectedException.expect(MetadataCreatorPermanentException.class);
        expectedException.expectMessage("cvc-pattern-valid: Value '123' is not facet-valid with respect to pattern '\\d{1,2}' for type '#AnonType_BusinessUnitIDDRS_MetaData_Defn_Core_Int_UD'.");
        cut.buildSoapMessage(request);
    }


    /**
     * A successfully generated SOAP request can be parsed and the same underlying object details extracted
     */
    @Test
    public void testValidRequestParsesAndUnmarshalsCorrectlyWithNullMobile() throws Exception {
        // Given: A populated DRS Request
        Request request = new Request();
        populateHeader(request);
        populateBody(request, true);
        populateMobileNumber(request, null);

        // When: We generate a SOAP request to submit to DRS
        String soapRequest = cut.buildSoapMessage(request);

        // Then: A valid SOAP request is generated
        SOAPMessage parsedSoapRequest = MessageFactory.newInstance().createMessage(null,
                IOUtils.toInputStream(soapRequest));
        // and: the payload is a valid request object
        Request parsedRequest =
                (Request) JAXBContext.newInstance(Request.class).createUnmarshaller().unmarshal(
                        parsedSoapRequest.getSOAPBody().getFirstChild());
        // and: its content matches the original request
        ReflectionAssert.assertReflectionEquals(request, parsedRequest);
    }


    /**
     * A successfully generated SOAP request can be parsed and the same underlying object details extracted
     */
    @Test
    public void testValidRequestParsesAndUnmarshalsCorrectlywithValidMobile() throws Exception {
        // Given: A populated DRS Request
        Request request = new Request();
        populateHeader(request);
        populateBody(request, true);
        populateMobileNumber(request, "07898765432");

        // When: We generate a SOAP request to submit to DRS
        String soapRequest = cut.buildSoapMessage(request);

        // Then: A valid SOAP request is generated
        SOAPMessage parsedSoapRequest = MessageFactory.newInstance().createMessage(null,
                IOUtils.toInputStream(soapRequest));
        // and: the payload is a valid request object
        Request parsedRequest =
                (Request) JAXBContext.newInstance(Request.class).createUnmarshaller().unmarshal(
                        parsedSoapRequest.getSOAPBody().getFirstChild());
        // and: its content matches the original request
        ReflectionAssert.assertReflectionEquals(request, parsedRequest);
    }


    /**
     * A successfully generated SOAP request can be parsed and the same underlying object details extracted
     */
    @Test
    public void testValidRequestParsesAndUnmarshalsCorrectlyWithEmptyMobile() throws Exception {
        // Given: A populated DRS Request
        Request request = new Request();
        populateHeader(request);
        populateBody(request, true);
        populateMobileNumber(request, "");

        // When: We generate a SOAP request to submit to DRS
        String soapRequest = cut.buildSoapMessage(request);

        // Then: A valid SOAP request is generated
        SOAPMessage parsedSoapRequest = MessageFactory.newInstance().createMessage(null,
                IOUtils.toInputStream(soapRequest));
        // and: the payload is a valid request object
        Request parsedRequest =
                (Request) JAXBContext.newInstance(Request.class).createUnmarshaller().unmarshal(
                        parsedSoapRequest.getSOAPBody().getFirstChild());
        // and: its content matches the original request
        ReflectionAssert.assertReflectionEquals(request, parsedRequest);
    }


    /**
     * A successfully generated SOAP request can be parsed and the same underlying object details extracted
     */
    @Test
    public void testValidRequestParsesAndUnmarshalsCorrectlyWithPaddedMobile() throws Exception {
        // Given: A populated DRS Request
        Request request = new Request();
        populateHeader(request);
        populateBody(request, true);
        populateMobileNumber(request, "   ");

        // When: We generate a SOAP request to submit to DRS
        String soapRequest = cut.buildSoapMessage(request);

        // Then: A valid SOAP request is generated
        SOAPMessage parsedSoapRequest = MessageFactory.newInstance().createMessage(null,
                IOUtils.toInputStream(soapRequest));
        // and: the payload is a valid request object
        Request parsedRequest =
                (Request) JAXBContext.newInstance(Request.class).createUnmarshaller().unmarshal(
                        parsedSoapRequest.getSOAPBody().getFirstChild());
        // and: its content matches the original request
        ReflectionAssert.assertReflectionEquals(request, parsedRequest);
    }


    /**
     * Populate a request header with dummy values
     */
    private void populateHeader(Request request) {
        DRSRequestHeader header = new DRSRequestHeader();
        header.setAuditHeader(new DRSIDAuditDefn());

        header.setUserIdentifier("123456");
        header.setUserName("userName");
        header.setVersion(1.1f);
        header.getAuditHeader().setLocationAddress("locationAddress");
        header.getAuditHeader().setSourceSystem("sourceSystem");

        request.setHeader(header);
    }

    /**
     * Populates a request body with dummy values
     */
    private void populateBody(Request request, boolean valid) {
        Body body = new Body();
        body.setMetadata(new DRSMetaDataDefnUD());
        body.getMetadata().setNINO(new NINO());

        body.setAttachmentType(1);
        body.setDocument("ABCDEFG".getBytes());
        body.setMimeType("application/pdf");
        body.setStore(1);
        body.getMetadata().setBusinessUnitID(valid ? 12 : 123);
        body.getMetadata().setBenefitType(1);
        body.getMetadata().setClassification(1);
        body.getMetadata().setCustomerReferenceNumber("12345678");
        body.getMetadata().setDateOfBirth("19700101");
        body.getMetadata().setDocumentSource(1);
        body.getMetadata().setDocumentType(2);
        body.getMetadata().setForename("John");
        body.getMetadata().setHarmfulIndicatorFlag(0);
        body.getMetadata().setIssueDate("20150101");
        body.getMetadata().setLinkData("xxx");
        body.getMetadata().setLOBCaseID("987654321");
        body.getMetadata().getNINO().setNINoBody("AB123321");
        body.getMetadata().getNINO().setNINoSuffix("D");
        body.getMetadata().setOfficePostcode("AB1 1BA");
        body.getMetadata().setPostCode("XX1 1XX");
        body.getMetadata().setSurname("Doe");

        request.setBody(body);
    }

    private void populateMobileNumber(Request request, String mobile) {
        Body body = request.getBody();
        body.getMetadata().setCustomerMobileNumber(mobile);
        request.setBody(body);
    }
}
