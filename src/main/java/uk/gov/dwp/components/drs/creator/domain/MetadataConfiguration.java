package uk.gov.dwp.components.drs.creator.domain;

import io.dropwizard.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.crypto.SecureStrings;

import javax.crypto.SealedObject;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.List;

public class MetadataConfiguration extends Configuration {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataConfiguration.class);
    private String keystoreLocation;
    private SealedObject keyStorePassword;
    private String drsRequestUserName;

    private String pdfRetrieverKeyStorePath = null;
    private SealedObject pdfRetrieverKeyStorePassword = null;
    private String pdfRetrieverTrustStorePath = null;
    private SealedObject pdfRetrieverTrustStorePassword = null;
    private boolean pdfRetrieverCheckHostname = true;
    private List<String> trustedUrlList;


    private SecureStrings secureStrings = new SecureStrings();

    public MetadataConfiguration() {
        // Default, empty constructor
    }

    public MetadataConfiguration(String keystoreLocation, String keystorePassword, String drsRequestUserName) {
        setKeystoreLocation(keystoreLocation);
        setKeyStorePassword(keystorePassword);
        setDrsRequestUserName(drsRequestUserName);
    }

    public MetadataConfiguration(String pdfRetrieverKeyStorePath, String pdfRetrieverKeyStorePassword,
                                 String pdfRetrieverTrustStorePath, String pdfRetrieverTrustStorePassword, boolean pdfRetrieverCheckHostname) {
        setPdfRetrieverKeyStorePath(pdfRetrieverKeyStorePath);
        setPdfRetrieverKeyStorePassword(pdfRetrieverKeyStorePassword);
        setPdfRetrieverTrustStorePath(pdfRetrieverTrustStorePath);
        setPdfRetrieverTrustStorePassword(pdfRetrieverTrustStorePassword);
        setPdfRetrieverCheckHostname(pdfRetrieverCheckHostname);
    }

    public String getKeystoreLocation() {
        return keystoreLocation;
    }

    public void setKeystoreLocation(String keystoreLocation) {
        this.keystoreLocation = keystoreLocation;
    }

    public String getKeyStorePassword() {
        return secureStrings.revealString(keyStorePassword);
    }

    public void setKeyStorePassword(String input) {
        keyStorePassword = secureStrings.sealString(input);
    }

    public String getDrsRequestUserName() {
        return drsRequestUserName;
    }

    public void setDrsRequestUserName(String drsRequestUserName) {
        this.drsRequestUserName = drsRequestUserName;
    }

    public String getPdfRetrieverKeyStorePath() {
        return pdfRetrieverKeyStorePath;
    }

    public void setPdfRetrieverKeyStorePath(String pdfRetrieverKeyStorePath) {
        this.pdfRetrieverKeyStorePath = pdfRetrieverKeyStorePath;
    }

    public String getPdfRetrieverKeyStorePassword() {
        return secureStrings.revealString(pdfRetrieverKeyStorePassword);
    }

    public void setPdfRetrieverKeyStorePassword(String input) {
        pdfRetrieverKeyStorePassword = secureStrings.sealString(input);
    }

    public String getPdfRetrieverTrustStorePath() {
        return pdfRetrieverTrustStorePath;
    }

    public void setPdfRetrieverTrustStorePath(String pdfRetrieverTrustStorePath) {
        this.pdfRetrieverTrustStorePath = pdfRetrieverTrustStorePath;
    }

    public String getPdfRetrieverTrustStorePassword() {
        return secureStrings.revealString(pdfRetrieverTrustStorePassword);
    }

    public void setPdfRetrieverTrustStorePassword(String input) {
        pdfRetrieverTrustStorePassword = secureStrings.sealString(input);
    }

    public boolean isPdfRetrieverCheckHostname() {
        return pdfRetrieverCheckHostname;
    }

    public void setPdfRetrieverCheckHostname(boolean pdfRetrieverCheckHostname) {
        this.pdfRetrieverCheckHostname = pdfRetrieverCheckHostname;
    }

    public List<String> getTrustedUrlList() {
        return trustedUrlList;
    }

    public void setTrustedUrlList(List<String> trustedUrlList) {
        this.trustedUrlList = trustedUrlList;
    }

    public KeyStore loadKeyStore() {
        return loadGenericKeyStore(getPdfRetrieverKeyStorePassword(), getPdfRetrieverKeyStorePath());
    }

    public KeyStore loadTrustStore() {
        return loadGenericKeyStore(getPdfRetrieverTrustStorePassword(), getPdfRetrieverTrustStorePath());
    }

    private KeyStore loadGenericKeyStore(String pw, String path) {
        KeyStore keyStore = null;
        if (!StringUtils.isEmpty(pw) && !StringUtils.isEmpty(path)) {
            try (FileInputStream trustStoreStream = new FileInputStream(path)) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Loading keystore from {}", path);
                }

                keyStore = KeyStore.getInstance("JKS");
                keyStore.load(trustStoreStream, pw.toCharArray());

            } catch (Exception e) {
                LOGGER.error("Failed to load key store : {}", e.getMessage());
                LOGGER.debug("Failed to load key store {}", path, e);
            }
        }
        return keyStore;
    }
}
