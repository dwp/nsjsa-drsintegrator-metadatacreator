package uk.gov.dwp.components.drs.creator;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.components.drs.creator.definitions.Constants;
import uk.gov.dwp.components.drs.creator.domain.MetadataConfiguration;
import uk.gov.dwp.components.drs.creator.exceptions.MetadataCreatorPermanentException;
import org.w3c.dom.Document;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collections;

import static java.util.Collections.singletonList;

/**
 * Sign an XML document provided in DOM form.
 */
public class DocumentSigner {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentSigner.class);

    /**
     * Cached service configuration details
     */
    private MetadataConfiguration configuration = null;

    /**
     * Cached signer info to save us loading the keystore information on every run
     */
    private SignerInfo signerInfo = null;

    /**
     * Fully populating constructor
     *
     * @param configuration service configuration
     */
    public DocumentSigner(MetadataConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Sign the provided document with the configured private key and associate public
     * key information for verification
     *
     * @param document the unsigned document to process
     * @return reference to the (now signed) document
     * @throws MetadataCreatorPermanentException wrapping anything that fails in this process
     */
    public Document sign(Document document) throws MetadataCreatorPermanentException {
        try {
            // Create an appropriate XMLSignatureFactory
            String signingProviderName = System.getProperty(Constants.SIGNING_PROVIDER_PROPERTY, Constants.DEFAULT_SIGNING_PROVIDER);
            XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM", (Provider) Class.forName(signingProviderName).newInstance());

            // Configure a SignedInfo object with the signature settings
            DigestMethod digestMethod = signatureFactory.newDigestMethod(DigestMethod.SHA1, null);
            Transform transform = signatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
            Reference reference = signatureFactory.newReference("", digestMethod, singletonList(transform), null, null);
            SignatureMethod signatureMethod = signatureFactory.newSignatureMethod(Constants.SIGNING_ALGORITHM, null);
            CanonicalizationMethod canonicalizationMethod = signatureFactory.newCanonicalizationMethod(
                    CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS, (C14NMethodParameterSpec) null);
            SignedInfo signedInfo = signatureFactory.newSignedInfo(canonicalizationMethod, signatureMethod, singletonList(reference));

            // Prepare the published key information provided with the signature
            KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
            X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(getSignerInfo().getCertificate()));
            KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));

            // Sign the document with our private key and public information to include
            DOMSignContext domSigningContext = new DOMSignContext(getSignerInfo().getPrivateKey(), document.getDocumentElement());
            XMLSignature signature = signatureFactory.newXMLSignature(signedInfo, keyInfo);
            signature.sign(domSigningContext);

            // Return a reference to the now signed document
            return document;
        } catch (Exception e) {
            LOGGER.warn("Digital signing of XML document failed", e);
            throw new MetadataCreatorPermanentException("Unable to sign document", e);
        }
    }

    /**
     * Loads the certificate and private key from the configured key-store. Returns a cached copy if
     * we've already successfully loaded it.
     * <p>
     * This implementation presumes that the private key is secured with the same password
     * as the keystore itself.
     *
     * @return value object comprising the signer's certificate and associated private key
     */
    private SignerInfo getSignerInfo() throws GeneralSecurityException, IOException {
        if (signerInfo == null) {
            // Multiple loads are possible on startup with this approach but once the first has loaded, one
            // cached copy will always be returned.
            synchronized (this) {
                try (FileInputStream keyStoreStream = new FileInputStream(configuration.getKeystoreLocation())) {
                    KeyStore keyStore = KeyStore.getInstance("JKS");
                    keyStore.load(keyStoreStream, configuration.getKeyStorePassword().toCharArray());
                    KeyStore.PrivateKeyEntry keyEntry =
                            (KeyStore.PrivateKeyEntry) keyStore.getEntry
                                    (Constants.SIGNING_CERTIFICATE_NAME,
                                            new KeyStore.PasswordProtection(configuration.getKeyStorePassword().toCharArray()));
                    this.signerInfo = new SignerInfo(keyEntry.getPrivateKey(), (X509Certificate) keyEntry.getCertificate());
                }
            }
        }
        return signerInfo;
    }

    /**
     * Provides home cooked signer info
     */
    public void setSignKeys(String privateKey, String publicKey) throws InvalidKeySpecException, NoSuchAlgorithmException, CertificateException {
        String privKeyPEM = privateKey.replaceAll("-----(BEGIN|END)( RSA)? PRIVATE KEY-----", "");
        byte[] decoded = Base64.decode(privKeyPEM);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");


        // One funny idiosyncrasy of this interface is that the BEGIN CERTIFICATE requires a LF at the end of the begin
        // mark.  Not mentioned in the spec, but it's required to insert one if it doesn't exist.
        String publicKey1 = publicKey.replaceFirst("-----BEGIN CERTIFICATE-----[\n\r]*", "-----BEGIN CERTIFICATE-----\r\n");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(publicKey1.getBytes());
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(bis);

        this.signerInfo = new SignerInfo(kf.generatePrivate(spec), (X509Certificate) cert);
    }

    /**
     * Value object to hold the information extracted from the keystore required for digital signing.
     */
    private class SignerInfo {

        /**
         * The private key used for the signature
         */
        private PrivateKey privateKey = null;

        /**
         * The Certificate, containing the public key, which the client can use to validate it
         */
        private X509Certificate certificate = null;

        /**
         * Fully describing constructor
         */
        public SignerInfo(PrivateKey privateKey, X509Certificate certificate) {
            this.privateKey = privateKey;
            this.certificate = certificate;
        }

        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        public X509Certificate getCertificate() {
            return certificate;
        }
    }
}
