package uk.gov.dwp.components.drs.creator.definitions;

/**
 * General constants for this micro-service
 */
public class Constants {

    /**
     * Component name
     */
    public static final String COMPONENT_NAME = "DRS-MetadataCreator";

    /**
     * Signing certificate and key-pair alias
     */
    public static final String SIGNING_CERTIFICATE_NAME = "drstest";

    /**
     * Signing algorithm - no standard constant for SHA-256
     */
    public static final String SIGNING_ALGORITHM = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";

    /**
     * System property that specifies the XML signature provider implementation
     */
    public static final String SIGNING_PROVIDER_PROPERTY = "jsr105Provider";

    /**
     * Default value to use for the XML Signing provider implementation
     */
    public static final String DEFAULT_SIGNING_PROVIDER = "org.jcp.xml.dsig.internal.dom.XMLDSigRI";

    /**
     * DRS request property - attachment type
     */
    public static final int DRS_ATTACHMENT_TYPE = 2;

    /**
     * DRS request property - store
     */
    public static final int DRS_STORE = 1;

    /**
     * DRS header property - version
     */
    public static final float DRS_REQUEST_VERSION = 1.1f;

    /**
     * Private constructor to prevent instances of this class
     */
    private Constants() {
    }
}
