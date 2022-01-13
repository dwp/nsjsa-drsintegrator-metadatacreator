package uk.gov.dwp.components.drs.creator;

import org.junit.Test;
import org.w3c.dom.Document;
import uk.gov.dwp.components.drs.creator.DocumentSigner;
import uk.gov.dwp.components.drs.creator.RequestBuilder;
import uk.gov.dwp.components.drs.creator.RequestCreator;
import uk.gov.dwp.components.drs.creator.SoapMessageBuilder;
import uk.gov.govtalk.drs.common.metadata.DRSMetaDataDefnUD;
import uk.gov.govtalk.drs.documentupload.documentupload_request.Request;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RequestCreatorTest {

    private DocumentSigner signer = mock(DocumentSigner.class);
    private SoapMessageBuilder soapMessageBuilder = mock(SoapMessageBuilder.class);
    private RequestBuilder requestBuilder = mock(RequestBuilder.class);
    private RequestCreator creator = new RequestCreator(signer, soapMessageBuilder, requestBuilder);

    @Test
    public void confirmRequestCreated() throws Exception {
        DRSMetaDataDefnUD metadata = new DRSMetaDataDefnUD();
        String generatedSoapMessage = "<Request/>";
        byte[] bytes = "dummy-pdf".getBytes();
        Request generatedRequest = mock(Request.class);

        when(requestBuilder.build(bytes, metadata)).thenReturn(generatedRequest);
        when(soapMessageBuilder.buildSoapMessage(generatedRequest)).thenReturn(generatedSoapMessage);

        creator.createRequest(metadata, bytes);

        verify(signer).sign(any(Document.class));
    }

    @Test
    public void confirmBuildAndValidateCalledSuccessfully() throws Exception {
        String sampleJson = "{\n" +
                "\t\"classification\" : 1,\n" +
                "\t\"documentType\" : 1242,\n" +
                "\t\"documentSource\" : 4\n" +
                "}\n";
        DRSMetaDataDefnUD metadata = new DRSMetaDataDefnUD();

        when(requestBuilder.buildAndValidateMetadata(sampleJson)).thenReturn(metadata);
        assertEquals(metadata, creator.getDRSMetadataFromRequest(sampleJson));
    }
}
