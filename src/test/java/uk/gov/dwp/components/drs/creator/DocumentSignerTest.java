package uk.gov.dwp.components.drs.creator;

import uk.gov.dwp.components.drs.creator.DocumentSigner;
import uk.gov.dwp.components.drs.creator.domain.MetadataConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * Verifies the behaviour of the DocumentSigner.
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentSignerTest {

    /**
     * Mock microservice configuration
     */
    @Mock
    private MetadataConfiguration configuration;

    /**
     * Class under test
     */
    @InjectMocks
    private DocumentSigner cut;

    /**
     * DOM Document builder factory
     */
    private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    /**
     * Valid XML document to sign (would be better to laod this from a file)
     */
    private static final String XML_PAYLOAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><s11:Envelope xmlns:s11='http://schemas.xmlsoap.org/soap/envelope/'>\n" +
            "  <s11:Body>\n" +
            "    <drs-documentupload:request xmlns:drs-documentupload='http://www.govtalk.gov.uk/drs/DocumentUpload/DocumentUpload-Request'>\n" +
            "      <drs-documentupload:header>\n" +
            "        <drs-request:Version xmlns:drs-request='http://www.govtalk.gov.uk/drs/common/request'>1.1</drs-request:Version>\n" +
            "        <drs-request:UserIdentifier xmlns:drs-request='http://www.govtalk.gov.uk/drs/common/request'>1408CLAIM01</drs-request:UserIdentifier>\n" +
            "        <drs-request:UserName xmlns:drs-request='http://www.govtalk.gov.uk/drs/common/request'>1234</drs-request:UserName>\n" +
            "        <drs-request:AuditHeader xmlns:drs-request='http://www.govtalk.gov.uk/drs/common/request'>\n" +
            "          <drs-request:LocationName></drs-request:LocationName>\n" +
            "          <drs-request:LocationAddress></drs-request:LocationAddress>\n" +
            "          <drs-request:SourceSystem></drs-request:SourceSystem>\n" +
            "          <drs-request:SourceSubsystem></drs-request:SourceSubsystem>\n" +
            "        </drs-request:AuditHeader>\n" +
            "      </drs-documentupload:header>\n" +
            "      <drs-documentupload:body>\n" +
            "        <drs-documentupload:Metadata>\n" +
            "          <drs-metadata:BusinessUnitID xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'>33</drs-metadata:BusinessUnitID>\n" +
            "          <drs-metadata:Classification xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'>0</drs-metadata:Classification>\n" +
            "          <drs-metadata:DocumentType xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'>9086</drs-metadata:DocumentType>\n" +
            "          <drs-metadata:NINO xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'>\n" +
            "            <drs-metadata:NINoBody>JB486248</drs-metadata:NINoBody>\n" +
            "            <drs-metadata:NINoSuffix>C</drs-metadata:NINoSuffix>\n" +
            "          </drs-metadata:NINO>\n" +
            "          <drs-metadata:DateOfBirth xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'></drs-metadata:DateOfBirth>\n" +
            "          <drs-metadata:Surname xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'></drs-metadata:Surname>\n" +
            "          <drs-metadata:Forename xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'></drs-metadata:Forename>\n" +
            "          <drs-metadata:PostCode xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'></drs-metadata:PostCode>\n" +
            "          <drs-metadata:LOBCaseID xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'></drs-metadata:LOBCaseID>\n" +
            "          <drs-metadata:OfficePostcode xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'></drs-metadata:OfficePostcode>\n" +
            "          <drs-metadata:LinkData xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'></drs-metadata:LinkData>\n" +
            "          <drs-metadata:CustomerReferenceNumber xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'></drs-metadata:CustomerReferenceNumber>\n" +
            "          <drs-metadata:HarmfulIndicatorFlag xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'></drs-metadata:HarmfulIndicatorFlag>\n" +
            "          <drs-metadata:BenefitType xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'>35</drs-metadata:BenefitType>\n" +
            "          <drs-metadata:ClaimRef xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'></drs-metadata:ClaimRef>\n" +
            "          <drs-metadata:DocumentSource xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'>4</drs-metadata:DocumentSource>\n" +
            "          <drs-metadata:IssueDate xmlns:drs-metadata='http://www.govtalk.gov.uk/drs/common/metadata'></drs-metadata:IssueDate>\n" +
            "        </drs-documentupload:Metadata>\n" +
            "        <drs-documentupload:MimeType>application/pdf</drs-documentupload:MimeType>\n" +
            "        <drs-documentupload:Store></drs-documentupload:Store>\n" +
            "        <drs-documentupload:AttachmentType></drs-documentupload:AttachmentType>\n" +
            "        <drs-documentupload:Document>JYK</drs-documentupload:Document>\n" +
            "      </drs-documentupload:body>\n" +
            "    </drs-documentupload:request>\n" +
            "  </s11:Body>\n" +
            "</s11:Envelope>";

    /**
     * Happy path test - a valid document with a valid signing key pair yields a valid signature
     */
    @SuppressWarnings("rawtypes")
    @Test
    public void confirmAValidDocumentIsCorrectlySigned() throws Exception {
        // Given: a public/private key pair is configured for signing
        when(configuration.getKeystoreLocation()).thenReturn(this.getClass().getResource("/drstest.jks").getPath());
        when(configuration.getKeyStorePassword()).thenReturn("password");
        //   and: we have a parsed unsigned XML document we wish to sign
        documentBuilderFactory.setNamespaceAware(true);
        Document sourceDocument = documentBuilderFactory.newDocumentBuilder().parse(
                new InputSource(new StringReader(XML_PAYLOAD)));

        // When: We sign the XML document
        Document signedDocument = cut.sign(sourceDocument);

        // Then: the signed document has a digital signature present
        NodeList signatureNodes = signedDocument.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        assertNotNull(signatureNodes);
        assertEquals(1, signatureNodes.getLength());
        //  and: the signature is valid
        DOMValidateContext validationContext =
                new DOMValidateContext(new DRSX509KeySelector(), signatureNodes.item(0));
        XMLSignature signature = XMLSignatureFactory.getInstance("DOM").unmarshalXMLSignature(validationContext);
        if (!signature.validate(validationContext)) {
            if (signature.getSignatureValue().validate(validationContext)) {
                Iterator signatureReferencesIterator = signature.getSignedInfo().getReferences().iterator();
                while (signatureReferencesIterator.hasNext()) {
                    Reference signatureReference = ((Reference) signatureReferencesIterator.next());
                    if (!signatureReference.validate(validationContext)) {
                        fail("Signature reference " + signatureReference.getURI() + " is not valid");
                    }
                }
            } else {
                fail("Signature is invalid - either the validation key does not match or the content has changed");
            }
            fail("Signature validation failed (no further information available)");
        }
    }

    /**
     * Returns the public key of an X509 certificate based on the signature node in a signed XML document
     */
    private class DRSX509KeySelector extends KeySelector {
        @SuppressWarnings("rawtypes")
        @Override
        public KeySelectorResult select(KeyInfo keyInfo, Purpose purpose, AlgorithmMethod method,
                                        XMLCryptoContext context) throws KeySelectorException {
            Iterator ki = keyInfo.getContent().iterator();
            while (ki.hasNext()) {
                XMLStructure info = (XMLStructure) ki.next();
                if (!(info instanceof X509Data)) {
                    continue;
                }
                X509Data x509Data = (X509Data) info;
                Iterator xi = x509Data.getContent().iterator();
                while (xi.hasNext()) {
                    Object o = xi.next();
                    if (!(o instanceof X509Certificate)) {
                        continue;
                    }
                    final PublicKey key = ((X509Certificate) o).getPublicKey();
                    if (algEquals(method.getAlgorithm(), key.getAlgorithm())) {
                        return new KeySelectorResult() {
                            public Key getKey() {
                                return key;
                            }
                        };
                    }
                }
            }
            throw new KeySelectorException("No key found!");
        }

        private boolean algEquals(String algURI, String algName) {
            return ((algName.equalsIgnoreCase("DSA") &&
                    algURI.toLowerCase().startsWith("http://www.w3.org/2000/09/xmldsig#dsa"))) ||
                    (algName.equalsIgnoreCase("RSA") &&
                            (algURI.toLowerCase().startsWith("http://www.w3.org/2000/09/xmldsig#rsa") ||
                                    algURI.toLowerCase().startsWith("http://www.w3.org/2001/04/xmldsig-more#rsa")));
        }
    }
}
