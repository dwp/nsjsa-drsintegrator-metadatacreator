package uk.gov.dwp.components.drs.creator.api;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.*;
import java.util.*;

public class MetadataCreatorTest {
    private final static String drsrequest_username = "1234";
    private static String signkey_prv;
    private static String signkey_pub;
    private final static String metadata = "{\n" +
            "\t\t\"businessUnitID\": 36,\n" +
            "\t\t\"classification\": 0,\n" +
            "\t\t\"claimRef\": \"55091b49\",\n" +
            "\t\t\"documentType\": 9877,\n" +
            "\t\t\"documentSource\": 4,\n" +
            "\t\t\"dateOfBirth\": 20000202,\n" +
            "\t\t\"surname\": \"Pethapuria\",\n" +
            "\t\t\"forename\": \"\",\n" +
            "\t\t\"postCode\": \"TE15 5ST\",\n" +
            "\t\t\"officePostcode\": \"AB11 2XD\",\n" +
            "\t\t\"nino\": {\n" +
            "\t\t\t\"ninoBody\": \"AB123456\",\n" +
            "\t\t\t\"ninoSuffix\": \"D\"\n" +
            "\t\t},\n" +
            "\t\t\"benefitType\": 4 \n" +
            "\t}";

    @BeforeClass
    public static void beforeClass() throws Exception {
        Provider bcProvider = new BouncyCastleProvider();
        Security.addProvider(bcProvider);

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PemWriter pemWriter = new PemWriter(new OutputStreamWriter(output));
        pemWriter.writeObject(new PemObject("RSA PRIVATE KEY", keyPair.getPrivate().getEncoded()));
        pemWriter.flush();

        signkey_prv = output.toString();
        output.reset();


        long now = System.currentTimeMillis();
        Date startDate = new Date(now);

        X500Name dnName = new X500Name("CN=testkey.com");
//        BigInteger certSerialNumber = new BigInteger(Long.toString(now)); // <-- Using the current timestamp as the certificate serial number

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1); // <-- 1 Yr validity

        Date endDate = calendar.getTime();

        String signatureAlgorithm = "SHA256WithRSA"; // <-- Use appropriate signature algorithm based on your keyPair algorithm.

        ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).build(keyPair.getPrivate());

        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(dnName, BigInteger.valueOf(now),
                startDate, endDate, dnName, keyPair.getPublic());

        // Extensions --------------------------

        // Basic Constraints
        BasicConstraints basicConstraints = new BasicConstraints(true); // <-- true for CA, false for EndEntity

        certBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, basicConstraints); // Basic Constraints is usually marked as critical.

        // -------------------------------------

        pemWriter.writeObject(new PemObject("CERTIFICATE",
                new JcaX509CertificateConverter().setProvider(bcProvider).getCertificate(certBuilder.build(contentSigner)).getEncoded()));
        pemWriter.flush();
        signkey_pub = output.toString();


    }


    private void runTest(MetadataCreator metadataCreator) throws ParserConfigurationException, SAXException, IOException {
        String doc = metadataCreator.getDocument(metadata, "XXXX");

        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        ByteArrayInputStream is = new ByteArrayInputStream(doc.getBytes());
        Map<String,String> docmap = new HashMap<>();
        parser.parse(is, new DefaultHandler() {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                docmap.put(qName, new String(stream.toByteArray()));
                stream.reset();
            }

            @Override
            public void characters(char ch[], int start, int length) throws SAXException {
                try {
                    stream.write(new String(Arrays.copyOfRange(ch, start, start+length)).getBytes());
                } catch (IOException e) {
                    // silence!
                }
            }
        });

//        Assert.assertEquals(signkey_pub.replaceAll("-----(BEGIN|END) CERTIFICATE-----(\n)?", "").replaceAll("\n", ""),
//                docmap.get("X509Certificate").replaceAll("\n", ""));
        Assert.assertEquals("XXXX", docmap.get("ns3:Document"));
        Assert.assertEquals("4", docmap.get("ns4:BenefitType"));
        Assert.assertEquals("36", docmap.get("ns4:BusinessUnitID"));
        Assert.assertEquals("2", docmap.get("ns3:AttachmentType"));
        Assert.assertEquals("application/pdf", docmap.get("ns3:MimeType"));
        Assert.assertEquals("4", docmap.get("ns4:DocumentSource"));
        Assert.assertEquals("9877", docmap.get("ns4:DocumentType"));
        Assert.assertEquals("1.1", docmap.get("ns2:Version"));
        Assert.assertEquals("AB11 2XD", docmap.get("ns4:OfficePostcode"));
        Assert.assertEquals("Pethapuria", docmap.get("ns4:Surname"));
        Assert.assertEquals("20000202", docmap.get("ns4:DateOfBirth"));
        Assert.assertEquals("55091b49", docmap.get("ns4:ClaimRef"));
        Assert.assertEquals("0", docmap.get("ns4:Classification"));
        Assert.assertEquals("TE15 5ST", docmap.get("ns4:PostCode"));
        Assert.assertEquals("1", docmap.get("ns3:Store"));
        Assert.assertEquals(drsrequest_username, docmap.get("ns2:UserName"));
        Assert.assertEquals("AB123456", docmap.get("ns4:NINoBody"));
        Assert.assertEquals("D", docmap.get("ns4:NINoSuffix"));
    }


    @Test
    public void canCreateDocumentFromAPI() throws ParserConfigurationException, SAXException, IOException {
        runTest(new MetadataCreator(signkey_prv, signkey_pub, drsrequest_username));
    }

    @Test
    public void canCreateDocumentWithCRsFromCertRemoved() throws IOException, SAXException, ParserConfigurationException {
        String signkey_prv_local = signkey_prv.replaceAll("\\n", "");
        String signkey_pub_local = signkey_pub.replaceAll("\\n", "");
        runTest(new MetadataCreator(signkey_prv_local, signkey_pub_local, drsrequest_username));
    }
}
