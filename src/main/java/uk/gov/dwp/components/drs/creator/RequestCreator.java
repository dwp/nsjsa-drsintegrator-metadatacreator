package uk.gov.dwp.components.drs.creator;

import uk.gov.dwp.components.drs.creator.exceptions.MetadataCreatorPermanentException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.gov.govtalk.drs.common.metadata.DRSMetaDataDefnUD;
import uk.gov.govtalk.drs.documentupload.documentupload_request.Request;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;


public class RequestCreator {

    private DocumentSigner signer;
    private SoapMessageBuilder soapMessageBuilder;
    private RequestBuilder requestBuilder;

    public RequestCreator(DocumentSigner signer, SoapMessageBuilder soapMessageBuilder, RequestBuilder requestBuilder) {
        this.signer = signer;
        this.soapMessageBuilder = soapMessageBuilder;
        this.requestBuilder = requestBuilder;
    }

    public Document createRequest(DRSMetaDataDefnUD drsMetadata, byte[] pdfInByteForm) throws MetadataCreatorPermanentException, IOException, SAXException, ParserConfigurationException {
        Request request = requestBuilder.build(pdfInByteForm, drsMetadata);
        String soapMessage = soapMessageBuilder.buildSoapMessage(request);
        return signer.sign(createDocumentFromString(soapMessage));
    }

    public DRSMetaDataDefnUD getDRSMetadataFromRequest(String jsonMetadata) throws MetadataCreatorPermanentException {
        return requestBuilder.buildAndValidateMetadata(jsonMetadata);
    }

    private Document createDocumentFromString(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }
}
