package uk.gov.dwp.components.drs.creator.exceptions;

/**
 * Concrete exception class for transient exceptions
 */
public class MetadataCreatorTransientException extends MetadataCreatorException {

    /**
     * For serialisation
     */
    private static final long serialVersionUID = 1L;

    /**
     * Message only constructor
     *
     * @param message associated error message
     */
    public MetadataCreatorTransientException(String message) {
        super(true, message);
    }

    /**
     * Message and underlying cause constructor
     *
     * @param message associated error message
     * @param cause   underlying throwable
     */
    public MetadataCreatorTransientException(String message, Throwable cause) {
        super(false, message, cause);
    }
}
