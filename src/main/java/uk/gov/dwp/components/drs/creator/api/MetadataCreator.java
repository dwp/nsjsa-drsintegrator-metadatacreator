package uk.gov.dwp.components.drs.creator.api;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import uk.gov.dwp.components.drs.creator.DocumentSigner;
import uk.gov.dwp.components.drs.creator.RequestBuilder;
import uk.gov.dwp.components.drs.creator.RequestCreator;
import uk.gov.dwp.components.drs.creator.SoapMessageBuilder;
import uk.gov.dwp.components.drs.creator.domain.MetadataConfiguration;
import uk.gov.govtalk.drs.common.metadata.DRSMetaDataDefnUD;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

public class MetadataCreator {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MetadataCreator.class);

    private final String signkeyPrivate;
    private final String signkeyPublic;
    private final String drsRequestUserName;

    public MetadataCreator(String signkeyPrivate, String signkeyPublic, String drsRequestUserName) {
        this.signkeyPrivate = signkeyPrivate;
        this.signkeyPublic = signkeyPublic;
        this.drsRequestUserName = drsRequestUserName;

        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
    }

    public String getDocument(String metadata, String pdfIn) {
        String xml = "";
        LOGGER.info("========================== MetadataCreator ==================================");

        MetadataConfiguration metadataConfiguration = new MetadataConfiguration();
        metadataConfiguration.setDrsRequestUserName(drsRequestUserName);
        DocumentSigner documentSigner = new DocumentSigner(metadataConfiguration);
        try {
            documentSigner.setSignKeys(signkeyPrivate, signkeyPublic);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | CertificateException e) {
            LOGGER.info("Cannot find keys", e);
            return null;
        }

        SoapMessageBuilder soapMessageBuilder = new SoapMessageBuilder();
        RequestBuilder requestBuilder = new RequestBuilder(metadataConfiguration);
        RequestCreator requestCreator = new RequestCreator(documentSigner, soapMessageBuilder, requestBuilder);

        // Extract and validate the JSON DRS metadata
        DRSMetaDataDefnUD drsMetadata = null;
        try {
            drsMetadata = requestCreator.getDRSMetadataFromRequest(metadata);
        } catch (Exception e) {
            LOGGER.info("Extract and verify of DRS metadata failed : " + e.getMessage());
            return null;
        }

        // Retrieve the PDF document
        byte[] pdf = Base64.decodeBase64(pdfIn);

        // Create the signed DRS request
        try {
            Document doc = requestCreator.createRequest(drsMetadata, pdf);
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            xml = writer.getBuffer().toString();
        } catch (Exception e) {
            LOGGER.info("Unable to create request : " + e.getMessage());
            return null;
        }

        return xml;
    }
}
